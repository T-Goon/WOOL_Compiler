package wool.code;

import wool.lexparse.WoolBaseVisitor;
import wool.lexparse.WoolParser.AddSubExprContext;
import wool.lexparse.WoolParser.AssignExprContext;
import wool.lexparse.WoolParser.BlockExprContext;
import wool.lexparse.WoolParser.ClsContext;
import wool.lexparse.WoolParser.Comp_ExprContext;
import wool.lexparse.WoolParser.EQ_ExprContext;
import wool.lexparse.WoolParser.EqOpsContext;
import wool.lexparse.WoolParser.ExprContext;
import wool.lexparse.WoolParser.FalseExprContext;
import wool.lexparse.WoolParser.FormalContext;
import wool.lexparse.WoolParser.IdExprContext;
import wool.lexparse.WoolParser.IfExprContext;
import wool.lexparse.WoolParser.IntExprContext;
import wool.lexparse.WoolParser.LocMethContext;
import wool.lexparse.WoolParser.MethodContext;
import wool.lexparse.WoolParser.MulDivExprContext;
import wool.lexparse.WoolParser.NegNumExprContext;
import wool.lexparse.WoolParser.Neg_Log_ExprContext;
import wool.lexparse.WoolParser.NewObjContext;
import wool.lexparse.WoolParser.NullCheckContext;
import wool.lexparse.WoolParser.NullExprContext;
import wool.lexparse.WoolParser.ObjMethContext;
import wool.lexparse.WoolParser.ParenExprContext;
import wool.lexparse.WoolParser.ProgramContext;
import wool.lexparse.WoolParser.SelectExprContext;
import wool.lexparse.WoolParser.SelectPartContext;
import wool.lexparse.WoolParser.StrExprContext;
import wool.lexparse.WoolParser.TrueExprContext;
import wool.lexparse.WoolParser.VardefContext;
import wool.lexparse.WoolParser.WhileExprContext;
import wool.symbol.AbstractBinding;
import wool.symbol.ClassDescriptor;
import wool.symbol.MethodDescriptor;
import wool.symbol.TableManager;
import wool.utility.WoolException;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.RecordComponentVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;

public class CodeGenerator extends WoolBaseVisitor<HashMap<String, byte[]>> implements Opcodes{

	private ParseTreeProperty<AbstractBinding> bindings;
	private ParseTreeProperty<Object> values;
	private String WOOL = "wool/";
	
	private HashMap<String, byte[]> code = null;
	private HashMap<String, HashMap<String, Integer>> locVars = null;
	private HashMap<String, Integer> curMethMap;
	
	private FieldVisitor fieldVisitor;
	private MethodVisitor methodVisitor;
	private ClassWriter classWriter;
	
	private String intType = "I";
	private String boolType = "Z";
	
	private String currentClass;
	private String currentMethod;
	private boolean isStatic;
	
	private static int scope_lvl = 0;
	private static int loc_addr = 0;
	
	public CodeGenerator(ParseTreeProperty<AbstractBinding> bindings) {
		this.bindings = bindings;
		
		code = new HashMap<String, byte[]>();
		values = new ParseTreeProperty<Object>();
		locVars = new HashMap<String, HashMap<String, Integer>>();
	}
	
	

	@Override
	public HashMap<String, byte[]> visitProgram(ProgramContext ctx) {
		
		super.visitProgram(ctx);
		
		return code;
	}



	@Override
	public HashMap<String, byte[]> visitCls(ClsContext ctx) {
		scope_lvl ++;
		
		String inhrt;
		if(ctx.inhrt() == null) {
			inhrt = "Object";
		} else {
			inhrt = ctx.inhrt().inhrtType.getText().toString();
		}
				
		// New class
		classWriter = new WoolClassWriter(COMPUTE_FRAMES + COMPUTE_MAXS);
		String name = ctx.className.getText().toString();
		
		currentClass = WOOL + name;
		
		classWriter.visit(V11, ACC_PUBLIC | ACC_SUPER, currentClass,
				null, WOOL+inhrt, null);
		
		// uninitialized class variables
		for(VardefContext v : ctx.classVars) {
			handleUninitVars(v);
		}
		
		// Class's constructor
		methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		methodVisitor.visitCode();
		methodVisitor.visitVarInsn(ALOAD, 0); // this
		
		// parent constructor
		methodVisitor.visitMethodInsn(INVOKESPECIAL, 
				WOOL + inhrt, "<init>", "()V", false);
		
		// Handle class attributes initialization
		for(VardefContext v : ctx.classVars) {
			v.accept(this);
		}
		
		methodVisitor.visitInsn(RETURN);
		
		// num local variables and size of stack
		methodVisitor.visitMaxs(1, 1);
		methodVisitor.visitEnd();
		// End classes constructor
		
		// Initialize all the methods
		for(MethodContext m : ctx.classMeth) {
			m.accept(this);
		}
		
		classWriter.visitEnd();
		
		// Pairs of class names and the byte array
		code.put(
				name+".class",
				classWriter.toByteArray());
		
		scope_lvl --;

		return null;
	}
	
	private void handleUninitVars(VardefContext ctx) {
		String type;
		String typeName = ctx.varType.getText().toLowerCase();
		
		if(typeName.equals("int")) {
			type = intType;
		} else if(typeName.equals("bool") || typeName.equals("boolean")) {
			type = boolType;
		} else if(typeName.equals("str")) {
			type = "L"+WOOL+"Str;";
		} else {
			// Some type of wool class
			type = "L"+WOOL+ctx.varType.getText().toString()+";";
		}
		
		fieldVisitor = classWriter.visitField(ACC_PROTECTED, ctx.ID().getText().toString(), type, null, null);
		fieldVisitor.visitEnd();
	}

	@Override
	public HashMap<String, byte[]> visitVardef(VardefContext ctx) {
		String typeName = ctx.varType.getText().toLowerCase();
		String varName =  ctx.ID().getText().toString();
		
		if(scope_lvl == 1) {
			// Class attributes
			if(ctx.varInit == null) {
				// The uninitialized variables
				if(typeName.equals("int")) {
					
					methodVisitor.visitVarInsn(ALOAD, 0);
					methodVisitor.visitInsn(ICONST_0);
					methodVisitor.visitFieldInsn(PUTFIELD, currentClass, varName, intType);
					
				} else if(typeName.equals("bool") || typeName.equals("boolean")) {
					
					methodVisitor.visitVarInsn(ALOAD, 0);
					methodVisitor.visitInsn(ICONST_0);
					methodVisitor.visitFieldInsn(PUTFIELD, currentClass, varName, boolType);
					
				} else if(typeName.equals("str")) {
					
					methodVisitor.visitVarInsn(ALOAD, 0);
					methodVisitor.visitTypeInsn(NEW, WOOL+"Str");
					methodVisitor.visitInsn(DUP);
					methodVisitor.visitMethodInsn(INVOKESPECIAL, WOOL+"Str", "<init>", "()V", false);
					methodVisitor.visitFieldInsn(PUTFIELD, currentClass, varName, "L"+WOOL+"Str;");
					
				} 
				// Everything else is null

			} else {
				
				methodVisitor.visitVarInsn(ALOAD, 0);
				
				// Evaluate assign expression
				ctx.varInit.accept(this);
				
				methodVisitor.visitFieldInsn(PUTFIELD, currentClass, varName, getTypeCode(ctx.varType.getText().toString()));			
			}
		} else if(scope_lvl == 2) {
			// Method local variables
			if(ctx.varInit == null) {
				// The uninitialized variables
				if(typeName.equals("int") || typeName.equals("bool") || typeName.equals("boolean")) {
					
					methodVisitor.visitInsn(ICONST_0);
					methodVisitor.visitVarInsn(ISTORE, loc_addr);
					
					curMethMap.put(varName, loc_addr);
					
					loc_addr ++;
					
				} else if(typeName.equals("str")) {
					
					methodVisitor.visitTypeInsn(NEW, WOOL+"Str");
					methodVisitor.visitInsn(DUP);
					methodVisitor.visitMethodInsn(INVOKESPECIAL, WOOL+"Str", "<init>", "()V", false);
					methodVisitor.visitVarInsn(ASTORE, loc_addr);
					
					curMethMap.put(varName, loc_addr);
					
					loc_addr ++;
				} else {
					methodVisitor.visitInsn(ACONST_NULL);
					methodVisitor.visitVarInsn(ASTORE, loc_addr);
					curMethMap.put(varName, loc_addr);
					
					loc_addr ++;
				}

			} else {
				
				// Evaluate assign expression
				ctx.varInit.accept(this);
				if(typeName.equals("int") || typeName.equals("bool") || typeName.equals("boolean")) {
					methodVisitor.visitVarInsn(ISTORE, loc_addr);
				} else {
					methodVisitor.visitVarInsn(ASTORE, loc_addr);
				}
				
				curMethMap.put(varName, loc_addr);
				loc_addr ++;			
			}
		}
		
		
		return null;
	}

	@Override
	public HashMap<String, byte[]> visitMethod(MethodContext ctx) {
		scope_lvl ++;
		
		MethodDescriptor md = TableManager.getInstance().lookupMethodInClass(
				ctx.ID().getText().toString(), currentClass.substring(5)).getMethodDescriptor();
		currentMethod = md.methodName;
		curMethMap = new HashMap<String, Integer>();
		locVars.put(currentMethod, curMethMap);
		
		// Main or instance method
		if(md.methodName.equals("main")) {
			if(md.argumentTypes.size() != 0) {
				throw new WoolException("main method cannot have arguments");
			}
			
			methodVisitor = classWriter.visitMethod(ACC_PUBLIC | ACC_STATIC, "main",
					"([Ljava/lang/String;)V", null, null);
			
			isStatic = true;
		} else {
			methodVisitor = classWriter.visitMethod(ACC_PUBLIC, md.methodName, 
					"("+genArgCode(md)+")"+getTypeCode(md.returnType), null, null);
			isStatic = false;
		}
		
		methodVisitor.visitCode();
		
		loc_addr ++;
		// Handle arguments
		for(FormalContext f : ctx.methForms) {
			f.accept(this);
		}
		
		// Handle local variables
		for(VardefContext v : ctx.methVars) {
			v.accept(this);
		}
		
		// The expression
		ctx.expr().accept(this);
		
		// End method
		if(!isStatic) {
			if(md.returnType.toLowerCase().equals("int") ||
					md.returnType.toLowerCase().equals("bool")) {
				methodVisitor.visitInsn(IRETURN);
			} else {
				methodVisitor.visitInsn(ARETURN);
			}
				
		}else {
			methodVisitor.visitInsn(RETURN);
		}
			
		methodVisitor.visitMaxs(1, 1);
		methodVisitor.visitEnd();
		
		loc_addr = 0;
		scope_lvl --;
		
		return null;
	}
	
	@Override
	public HashMap<String, byte[]> visitFormal(FormalContext ctx) {
		curMethMap.put(ctx.ID().getText().toString(), loc_addr);
		loc_addr ++;
		
		return null;
	}

	@Override
	public HashMap<String, byte[]> visitObjMeth(ObjMethContext ctx) {

		String methName = ctx.meth.getText().toString();
		
		// Type of object
		AbstractBinding ab = bindings.get(ctx.obj);
		
		// Class Method is defined in
		ClassDescriptor cd = TableManager.getInstance().lookupClassForMethod(
				ctx.ID().getText().toString(), ab.symbolType);
		
		// Method descriptor for method
		MethodDescriptor md = TableManager.getInstance().lookupMethodInClass(
				ctx.ID().getText().toString(), cd.className).getMethodDescriptor();
		
		// Evaluate calling object expression
		ctx.obj.accept(this);
		
		// Evaluate arguments
		if(ctx.params() != null) {
			ctx.params().accept(this);
		}
		
		// Return type
		String returnType = "";
		if(md.returnType.equals("SELF_TYPE")) {
			if(cd.className.equals("Object")) {
				returnType = getTypeCode("Object");
			} else if(cd.className.equals("IO")) {
				returnType = getTypeCode("IO");
			}
			
		} else {
			returnType = getTypeCode(md.returnType);
		}
		
		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, WOOL+cd.className, methName,
				"("+genArgCode(md)+")"+returnType, false);
		
		// self_type
		if(md.returnType.equals("SELF_TYPE")) {
			methodVisitor.visitTypeInsn(CHECKCAST, WOOL+ab.symbolType);
		}
		
		return null;
	}

	@Override
	public HashMap<String, byte[]> visitLocMeth(LocMethContext ctx) {
		
		if(isStatic) {
			throw new WoolException("Cannot make a static reference to a non-static method");
		}
		
		methodVisitor.visitVarInsn(ALOAD, 0); // this
		
		String methName = ctx.meth.getText().toString();
		
		// Evaluate arguments
		if(ctx.params() != null) {
			ctx.params().accept(this);
		}
		
		MethodDescriptor md = TableManager.getInstance().lookupMethodInClass(
				ctx.ID().getText().toString(), currentClass.substring(5)).getMethodDescriptor();
		// Class in hierarchy that method is defined in
		ClassDescriptor cd = TableManager.getInstance().lookupClassForMethod(
				ctx.ID().getText().toString(), currentClass.substring(5));
		
		// Return type
		String returnType;
		if(md.returnType.equals("SELF_TYPE")) {
			returnType = getTypeCode("Object");
		} else {
			returnType = getTypeCode(md.returnType);
		}

		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, WOOL+cd.className, methName,
				"("+genArgCode(md)+")"+returnType, false);
		
		// self_type
		if(md.returnType.equals("SELF_TYPE")) {
			methodVisitor.visitTypeInsn(CHECKCAST, currentClass);
		}
		
		return null;
	}
	
	/**
	 * Creates the string for argument types for methods.
	 * @param md method descriptor for method
	 * @return string of type codes concatenated
	 */
	private String genArgCode(MethodDescriptor md) {
		
		// Create string for argument types
		String argTypes = "";
		for(String s : md.argumentTypes) {
			argTypes += getTypeCode(s);
		}
		
		return argTypes;
	}
	
	@Override
	public HashMap<String, byte[]> visitWhileExpr(WhileExprContext ctx) {
		Label condLabel = new Label();
		Label bodyLabel = new Label();
		
		methodVisitor.visitJumpInsn(GOTO, condLabel);
		
		methodVisitor.visitLabel(bodyLabel);
		// Put in loop body code
		ctx.body.accept(this);
		// Pop off object
		methodVisitor.visitInsn(POP);
		
		methodVisitor.visitLabel(condLabel);
		// Put in code code
		ctx.cond.accept(this);
		
		methodVisitor.visitJumpInsn(IFNE, bodyLabel);
		
		// Create new object
		methodVisitor.visitInsn(ACONST_NULL);
		
		return null;
	}

	@Override
	public HashMap<String, byte[]> visitIfExpr(IfExprContext ctx) {
		// Condition
		ctx.cond.accept(this);
		
		// GOTO false label if cond false
		Label fLabel = new Label();
		methodVisitor.visitJumpInsn(IFEQ, fLabel);
		
		// true expression
		ctx.then.accept(this);
		
		// Skip over false expression
		Label eLabel = new Label();
		methodVisitor.visitJumpInsn(GOTO, eLabel);
		
		// Placement of false label
		methodVisitor.visitLabel(fLabel);
		
		// False expression
		ctx.els.accept(this);
		
		// End of if statement code
		methodVisitor.visitLabel(eLabel);
		
		return null;
	}

	@Override
	public HashMap<String, byte[]> visitSelectExpr(SelectExprContext ctx) {
		
		Label eLabel = new Label();
		
		ArrayList<Label> list = new ArrayList<Label>();
		
		// Visit all conditionals
		for(int i=0; i<ctx.selectPart().size(); i++) {
			// Evaluate condition
			ctx.selectPart().get(i).first.accept(this);
			
			// Jump to first select alternative with condition true
			Label l = new Label();
			methodVisitor.visitJumpInsn(IFNE, l);
			
			list.add(l);
		}
		
		// Add default
		AbstractBinding ab = bindings.get(ctx);
		String type = ab.symbolType.toLowerCase();
		if(type.equals("int") || 
				type.equals("bool") || type.equals("boolean")) {
			
			methodVisitor.visitInsn(ICONST_0);
			
		} else if(type.equals("str")) {
			
			methodVisitor.visitTypeInsn(NEW, WOOL+"Str");
			methodVisitor.visitInsn(DUP);
			methodVisitor.visitMethodInsn(INVOKESPECIAL, WOOL+"Str", "<init>", "()V", false);
			
		} else {
			methodVisitor.visitInsn(ACONST_NULL);
		}
		
		// Go to end
		methodVisitor.visitJumpInsn(GOTO, eLabel);
		
		// Visit all select alternatives
		for(int i=0; i<ctx.selectPart().size(); i++){
			
			// Placement for jump
			methodVisitor.visitLabel(list.get(i));
						
			// Evaluate alternative body
			ctx.selectPart().get(i).second.accept(this);
			
			// Go to end
			methodVisitor.visitJumpInsn(GOTO, eLabel);
		}
			
		methodVisitor.visitLabel(eLabel);
				
		return null;
	}

	@Override
	public HashMap<String, byte[]> visitBlockExpr(BlockExprContext ctx) {

		for(int i=0; i<ctx.exprs.size()-1; i++) {
			// Execute expression and discard result
			ctx.exprs.get(i).accept(this);
			methodVisitor.visitInsn(POP);
		}
		
		ctx.exprs.get(ctx.exprs.size()-1).accept(this);
		
		return null;
	}

	@Override
	public HashMap<String, byte[]> visitNeg_Log_Expr(Neg_Log_ExprContext ctx) {
		ctx.expr().accept(this);
		
		methodVisitor.visitInsn(ICONST_1);
		methodVisitor.visitInsn(ISUB);
		methodVisitor.visitInsn(INEG);
		
		return null;
	}

	@Override
	public HashMap<String, byte[]> visitComp_Expr(Comp_ExprContext ctx) {
		
		String op = ctx.compOps().getText().toString();
		
		// branches
		ctx.left.accept(this);
		ctx.right.accept(this);
		
		if(op.equals("<")) {	
			
			// GOTO false label if cond false
			Label fLabel = new Label();
			methodVisitor.visitJumpInsn(IF_ICMPGE, fLabel);
			
			methodVisitor.visitInsn(ICONST_1);
			
			// Skip over false expression
			Label eLabel = new Label();
			methodVisitor.visitJumpInsn(GOTO, eLabel);
			
			// Placement of false label
			methodVisitor.visitLabel(fLabel);
			methodVisitor.visitInsn(ICONST_0);
			
			// End of if statement code
			methodVisitor.visitLabel(eLabel);
						
		} else if(op.equals(">")) {
			
			// GOTO false label if cond false
			Label fLabel = new Label();
			methodVisitor.visitJumpInsn(IF_ICMPLE, fLabel);
			
			methodVisitor.visitInsn(ICONST_1);
			
			// Skip over false expression
			Label eLabel = new Label();
			methodVisitor.visitJumpInsn(GOTO, eLabel);
			
			// Placement of false label
			methodVisitor.visitLabel(fLabel);
			methodVisitor.visitInsn(ICONST_0);
			
			// End of if statement code
			methodVisitor.visitLabel(eLabel);
			
		} else if(op.equals("<=")) {
			
			// GOTO false label if cond false
			Label fLabel = new Label();
			methodVisitor.visitJumpInsn(IF_ICMPGT, fLabel);
			
			methodVisitor.visitInsn(ICONST_1);
			
			// Skip over false expression
			Label eLabel = new Label();
			methodVisitor.visitJumpInsn(GOTO, eLabel);
			
			// Placement of false label
			methodVisitor.visitLabel(fLabel);
			methodVisitor.visitInsn(ICONST_0);
			
			// End of if statement code
			methodVisitor.visitLabel(eLabel);
		} else if(op.equals(">=")) {
			
			// GOTO false label if cond false
			Label fLabel = new Label();
			methodVisitor.visitJumpInsn(IF_ICMPLT, fLabel);
			
			methodVisitor.visitInsn(ICONST_1);
			
			// Skip over false expression
			Label eLabel = new Label();
			methodVisitor.visitJumpInsn(GOTO, eLabel);
			
			// Placement of false label
			methodVisitor.visitLabel(fLabel);
			methodVisitor.visitInsn(ICONST_0);
			
			// End of if statement code
			methodVisitor.visitLabel(eLabel);
		}
		
		return null;
	}

	@Override
	public HashMap<String, byte[]> visitEQ_Expr(EQ_ExprContext ctx) {
		
		String op = ctx.eqOps().getText().toString();
		
		AbstractBinding ab = bindings.get(ctx.left);

		// branches
		ctx.left.accept(this);
		ctx.right.accept(this);
		
		if(op.equals("=")) {	
			
			// GOTO false label if cond false
			Label fLabel = new Label();
			if(ab.symbolType.toLowerCase().equals("int") || 
					ab.symbolType.toLowerCase().equals("bool")) {
				methodVisitor.visitJumpInsn(IF_ICMPNE, fLabel);
			} else {
				methodVisitor.visitJumpInsn(IF_ACMPNE, fLabel);
			}
			
			methodVisitor.visitInsn(ICONST_1);
			
			// Skip over false expression
			Label eLabel = new Label();
			methodVisitor.visitJumpInsn(GOTO, eLabel);
			
			// Placement of false label
			methodVisitor.visitLabel(fLabel);
			methodVisitor.visitInsn(ICONST_0);
			
			// End of if statement code
			methodVisitor.visitLabel(eLabel);
						
		} else if(op.equals("~=")) {
			
			// GOTO false label if cond false
			Label fLabel = new Label();
			if(ab.symbolType.toLowerCase().equals("int") || 
					ab.symbolType.toLowerCase().equals("bool")) {
				methodVisitor.visitJumpInsn(IF_ICMPEQ, fLabel);
			} else {
				methodVisitor.visitJumpInsn(IF_ACMPEQ, fLabel);
			}
			
			methodVisitor.visitInsn(ICONST_1);
			
			// Skip over false expression
			Label eLabel = new Label();
			methodVisitor.visitJumpInsn(GOTO, eLabel);
			
			// Placement of false label
			methodVisitor.visitLabel(fLabel);
			methodVisitor.visitInsn(ICONST_0);
			
			// End of if statement code
			methodVisitor.visitLabel(eLabel);
		}
		
		return null;
	}

	@Override
	public HashMap<String, byte[]> visitAssignExpr(AssignExprContext ctx) {
		
		// Class scope
		if(scope_lvl == 1) {
			methodVisitor.visitVarInsn(ALOAD, 0);
			
			ctx.assign().expr().accept(this);
			
			methodVisitor.visitInsn(DUP_X1);
			
			AbstractBinding ab = bindings.get(ctx.ID());
			
			methodVisitor.visitFieldInsn(PUTFIELD, currentClass, ctx.ID().getText().toString(), getTypeCode(ab.symbolType));
		
		} else if(scope_lvl == 2) { // Method Scope
			
			ctx.assign().expr().accept(this);
			
			methodVisitor.visitInsn(DUP);
			
			AbstractBinding ab = bindings.get(ctx);
			
			int addr = curMethMap.get(ctx.ID().getText().toString());
			
			// Check type
			if(ab.symbolType.toLowerCase().equals("int") || 
					ab.symbolType.toLowerCase().equals("bool")) {
				methodVisitor.visitVarInsn(ISTORE, addr);
				
			} else {
				methodVisitor.visitVarInsn(ASTORE, addr);
			}
		}
		
		
		return null;
	}

	@Override
	public HashMap<String, byte[]> visitNullCheck(NullCheckContext ctx) {
		
		AbstractBinding ab = bindings.get(ctx.expr());
		
		// GOTO false label if cond false
		Label fLabel = new Label();
		if(ab.symbolType.toLowerCase().equals("int") || 
				ab.symbolType.toLowerCase().equals("bool")) {
			// Primitives always non null
			methodVisitor.visitInsn(ICONST_0);
			return null;
		} else {
			ctx.expr().accept(this);
			methodVisitor.visitJumpInsn(IFNONNULL, fLabel);
		}
		
		// is null
		methodVisitor.visitInsn(ICONST_1);
		
		// Skip over false expression
		Label eLabel = new Label();
		methodVisitor.visitJumpInsn(GOTO, eLabel);
		
		// Placement of false label
		methodVisitor.visitLabel(fLabel);
		methodVisitor.visitInsn(ICONST_0);
		
		// End of if null check
		methodVisitor.visitLabel(eLabel);
					
		return null;
	}
	
	@Override
	public HashMap<String, byte[]> visitNegNumExpr(NegNumExprContext ctx) {
		// Make int negative
		ctx.expr().accept(this);
		
		methodVisitor.visitInsn(INEG);
		return null;
	}

	@Override
	public HashMap<String, byte[]> visitMulDivExpr(MulDivExprContext ctx) {
		// Evaluate left and right expressions
		ctx.left.accept(this);
		ctx.right.accept(this);
		
		if(ctx.mulDivOps().getText().toString().equals("*")) {
			methodVisitor.visitInsn(IMUL);
		} else if(ctx.mulDivOps().getText().toString().equals("/")) {
			methodVisitor.visitInsn(IDIV);
		}
		
		return null;
	}

	@Override
	public HashMap<String, byte[]> visitAddSubExpr(AddSubExprContext ctx) {
		
		// Evaluate left and right expressions
		ctx.left.accept(this);
		ctx.right.accept(this);
		
		if(ctx.plusMinOps().getText().toString().equals("+")) {
			methodVisitor.visitInsn(IADD);
		} else if(ctx.plusMinOps().getText().toString().equals("-")) {
			methodVisitor.visitInsn(ISUB);
		}
		
		return null;
	}

	@Override
	public HashMap<String, byte[]> visitIdExpr(IdExprContext ctx) {
		
		// Class scope
		if(scope_lvl == 1) {
			loadID(bindings.get(ctx), ctx.ID().getText().toString());
			
		} else if(scope_lvl == 2) {
			// look for variable in method
			if(curMethMap.containsKey(ctx.getText().toString())) {
				
				int addr = curMethMap.get(ctx.getText().toString());
				
				// Check type
				AbstractBinding ab = bindings.get(ctx);
				if(ab.symbolType.toLowerCase().equals("int") || 
						ab.symbolType.toLowerCase().equals("bool")) {
					methodVisitor.visitVarInsn(ILOAD, addr);
				} else {
					methodVisitor.visitVarInsn(ALOAD, addr);
				}
				
			} else {
				if(isStatic) {
					throw new WoolException("Cannot make a static reference to a non-static field");
				}
				// not in method load from class
				loadID(bindings.get(ctx), ctx.ID().getText().toString());
			}
		}
		
		return null;
	}
	
	/**
	 * Load an id.
	 * @param ab Abstractbinding that contains the id's type
	 * @param name name of the id
	 */
	private void loadID(AbstractBinding ab, String name) {
		// Load an id
		methodVisitor.visitVarInsn(ALOAD, 0);
		
		if(!name.equals("this")) {
			switch(ab.symbolType.toLowerCase()) {
			case "int":
				methodVisitor.visitFieldInsn(GETFIELD, currentClass, name, intType);
				return;
			case "bool":
				methodVisitor.visitFieldInsn(GETFIELD, currentClass, name, boolType);
				return;
			default:
				methodVisitor.visitFieldInsn(GETFIELD, currentClass, name, "L"+WOOL+ab.symbolType+";");
				return;
			}
		}
	}
	
	@Override
	public HashMap<String, byte[]> visitStrExpr(StrExprContext ctx) {
		
		methodVisitor.visitTypeInsn(NEW, WOOL+"Str");
		methodVisitor.visitInsn(DUP);
		
		String s = ctx.getText().toString().substring(1, ctx.getText().toString().length()-1);
		Pattern pattern4 = Pattern.compile("\n");
		Matcher matcher4 = pattern4.matcher(s);
		s = matcher4.replaceAll("");
		Pattern pattern1 = Pattern.compile("\\\\n");
		Matcher matcher1 = pattern1.matcher(s);
		s = matcher1.replaceAll("\n");
		Pattern pattern2 = Pattern.compile("\\\\t");
		Matcher matcher2 = pattern2.matcher(s);
		s = matcher2.replaceAll("\t");
		Pattern pattern3 = Pattern.compile("\\\\\\\\");
		Matcher matcher3 = pattern3.matcher(s);
		s = matcher3.replaceAll("\\\\");
		
		methodVisitor.visitLdcInsn(s);
		methodVisitor.visitMethodInsn(INVOKESPECIAL, WOOL+"Str", "<init>", "(Ljava/lang/String;)V", false);
		return null;
	}

	@Override
	public HashMap<String, byte[]> visitIntExpr(IntExprContext ctx) {
		methodVisitor.visitIntInsn(SIPUSH, Integer.parseInt(ctx.NUM().getText().toString()));
		return null;
	}

	@Override
	public HashMap<String, byte[]> visitTrueExpr(TrueExprContext ctx) {
		methodVisitor.visitInsn(ICONST_1);
		return null;
	}

	@Override
	public HashMap<String, byte[]> visitFalseExpr(FalseExprContext ctx) {
		methodVisitor.visitInsn(ICONST_0);
		return null;
	}

	@Override
	public HashMap<String, byte[]> visitNewObj(NewObjContext ctx) {
		methodVisitor.visitTypeInsn(NEW, WOOL+ctx.obj.getText().toString());
		methodVisitor.visitInsn(DUP);
		methodVisitor.visitMethodInsn(INVOKESPECIAL, WOOL+ctx.obj.getText().toString(), "<init>", "()V", false);
		return null;
	}

	@Override
	public HashMap<String, byte[]> visitNullExpr(NullExprContext ctx) {
		methodVisitor.visitInsn(ACONST_NULL);
		return null;
	}
	
	/**
	 * Gets the code for a type.
	 * @param type String that specifies the type
	 * @return code for the type as a string.
	 */
	private String getTypeCode(String type) {
		switch(type.toLowerCase()) {
		case "int":
			return intType;
		case "bool":
			return boolType;
		case "boolean":
			return boolType;
		case "str":
			return "L"+WOOL+"Str"+";";
		default :
			return "L"+WOOL+type+";";
		}
	}
	
}
