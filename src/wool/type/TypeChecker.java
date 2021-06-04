package wool.type;

import org.antlr.v4.runtime.tree.ParseTreeProperty;

import wool.lexparse.WoolBaseVisitor;
import wool.lexparse.WoolParser.AddSubExprContext;
import wool.lexparse.WoolParser.AssignContext;
import wool.lexparse.WoolParser.AssignExprContext;
import wool.lexparse.WoolParser.BlockExprContext;
import wool.lexparse.WoolParser.ClsContext;
import wool.lexparse.WoolParser.Comp_ExprContext;
import wool.lexparse.WoolParser.EQ_ExprContext;
import wool.lexparse.WoolParser.ExprContext;
import wool.lexparse.WoolParser.FalseExprContext;
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
import wool.lexparse.WoolParser.ParamsContext;
import wool.lexparse.WoolParser.ParenExprContext;
import wool.lexparse.WoolParser.ProgramContext;
import wool.lexparse.WoolParser.SelectExprContext;
import wool.lexparse.WoolParser.SelectPartContext;
import wool.lexparse.WoolParser.StrExprContext;
import wool.lexparse.WoolParser.TrueExprContext;
import wool.lexparse.WoolParser.VardefContext;
import wool.lexparse.WoolParser.WhileExprContext;
import wool.symbol.AbstractBinding;
import wool.symbol.BindingFactory.ClassBinding;
import wool.symbol.BindingFactory.MethodBinding;
import wool.symbol.BindingFactory.ObjectBinding;
import wool.symbol.MethodDescriptor;
import wool.symbol.TableManager;
import wool.utility.WoolException;

public class TypeChecker extends WoolBaseVisitor<ParseTreeProperty<AbstractBinding>> {
	
	private TableManager tm;
	private ParseTreeProperty<AbstractBinding> bindings;
	private ObjectBinding boolBinding;
	private ObjectBinding intBinding;
	private ObjectBinding strBinding;
	private ObjectBinding nullBinding;
	private ClassBinding objBinding;
	private ClsContext currentClass;

	public TypeChecker(ParseTreeProperty<AbstractBinding> bindings) {
		this.bindings = bindings;
		tm = TableManager.getInstance();
		
		// Binding for boolean constants
		boolBinding = new ObjectBinding("boolean", "Bool", null);
		// Binding for int constants
		intBinding = new ObjectBinding("int", "Int", null);
		// Binding for Str constants
		strBinding = new ObjectBinding("Str", "Str", null);
		// Binding for object class
		objBinding = tm.lookupClass("Object");
		// Binding for null
		nullBinding = new ObjectBinding("null", "Object", null);
	}

	@Override
	public ParseTreeProperty<AbstractBinding> visitProgram(ProgramContext ctx) {
		super.visitProgram(ctx);
		return bindings;
	}

	@Override
	public ParseTreeProperty<AbstractBinding> visitCls(ClsContext ctx) {
		currentClass = ctx;
		
		// Visit the vardefs
		for(VardefContext v : ctx.classVars) {
			v.accept(this);
		}
		
		// Visit the methods
		for(MethodContext m : ctx.classMeth) {
			m.accept(this);
		}
		
		return null;
	}
	
	@Override
	public ParseTreeProperty<AbstractBinding> visitVardef(VardefContext ctx) {
		String originalAssignType;
		
		// Get declared type
		AbstractBinding abType = bindings.get(ctx.ID());
		
		// Check initialization expression type
		if(ctx.varInit != null) {
			ctx.varInit.accept(this);
			
			// Type of assignment
			AbstractBinding abExpr = bindings.get(ctx.varInit);
			originalAssignType = abExpr.symbolType;
			
			// Check expression type conforms to declared type
			AbstractBinding con = conforms(abType, abExpr);
			
			if(con == null) {
				throw new WoolException(
						"Line "+ctx.getStart().getLine()+
						", Class "+currentClass.className.getText().toString()+
						", Assignment type " + originalAssignType+
						" does not conform to type "+abType.symbolType);
			}
		}
		
		return null;
	}
	
	@Override
	public ParseTreeProperty<AbstractBinding> visitMethod(MethodContext ctx) {
		if(ctx.methVars != null) {
			for(VardefContext v : ctx.methVars) {
				v.accept(this);
			}
		}
		
		ctx.expr().accept(this);
		
		AbstractBinding ab = bindings.get(ctx.expr());
		
		MethodDescriptor md = tm.lookupMethodInClass(ctx.ID().getText().toString(),
				currentClass.className.getText().toString())
				.getMethodDescriptor();
		
		AbstractBinding res = conforms(new ObjectBinding(null, md.returnType, null), ab);
		
		// Check return type
		if(res == null) {
			throw new WoolException(
					"Line "+ctx.getStart().getLine()+
					", Class "+currentClass.className.getText().toString()+
					", Expression type " + ab.symbolType+
					" does not conform to type "+md.returnType);
		}
		
		return null;
	}

	@Override
	public ParseTreeProperty<AbstractBinding> visitAssignExpr(AssignExprContext ctx) {
		ctx.assign().accept(this);
		
		// Type of the identifier
		AbstractBinding abType = bindings.get(ctx.ID());
		// Type of the assign expression
		AbstractBinding abExpr = bindings.get(ctx.assign());
		
		String originType = abExpr.symbolType;
		
		// Check expression type conforms to declared type
		AbstractBinding con = conforms(abType, abExpr);
		
		if (con == null && !abExpr.symbol.equals(nullBinding.symbol)) {
			// Assign type does not conform to declared type
			throw new WoolException(
					"Line "+ctx.getStart().getLine()+
					", Class "+currentClass.className.getText().toString()+
					", Assignment type " + originType+
					" does not conform to type "+abType.symbolType);
		}
		
		bindings.put(ctx, abExpr);
		
		return null;
	}

	/**
	 * Checks is the second type conforms to the first type.
	 * @param abType AbstractBinding that contains the first type
	 * @param abExpr AbstractBinding that contains the second type
	 * @return abType if abExpr does conform and null otherwise
	 */
	private AbstractBinding conforms(AbstractBinding abType, AbstractBinding abExpr) {
		
		ClassBinding cb = tm.lookupClass(abExpr.symbolType);
		ClassBinding parent;
		
		// Check expression type conforms to declared type
		do {
			parent = tm.lookupClass(cb.getClassDescriptor().inherits);
			
			if(abType.symbolType.equals(abExpr.symbolType)) {
				return abType;
			}
			
			cb = parent;
			abExpr = cb;
		}while(abExpr != null);
		
		return null;
	}
	
	@Override
	public ParseTreeProperty<AbstractBinding> visitAssign(AssignContext ctx) {
		ctx.expr().accept(this);
		
		AbstractBinding ab = bindings.get(ctx.expr());
		
		bindings.put(ctx, ab);
		
		return null;
	}
	
	@Override
	public ParseTreeProperty<AbstractBinding> visitParenExpr(ParenExprContext ctx) {
		ctx.expr().accept(this);
		
		// Type of paren expr is type of enclosed expr
		AbstractBinding ab = bindings.get(ctx.expr());
		
		bindings.put(ctx, ab);
		
		return null;
	}
	
	@Override
	public ParseTreeProperty<AbstractBinding> visitNewObj(NewObjContext ctx) {
		AbstractBinding ab = bindings.get(ctx.TYPE());
		
		bindings.put(ctx, ab);

		return null;
	}
	
	
	@Override
	public ParseTreeProperty<AbstractBinding> visitBlockExpr(BlockExprContext ctx) {
		// Visit all expressions
		for(ExprContext e : ctx.exprs) {
			e.accept(this);
		}
	
		bindings.put(ctx, 
				bindings.get( // Type of the last expression
						ctx.exprs.get(
								ctx.exprs.size()-1)));
		
		return null;
	}
	
	// Method dispatch
	@Override
	public ParseTreeProperty<AbstractBinding> visitObjMeth(ObjMethContext ctx) {
		if(ctx.params() != null) {
			ctx.params().accept(this);
		}
		
		ctx.obj.accept(this);
		AbstractBinding obj = bindings.get(ctx.obj);
		
		String methName = ctx.meth.getText().toString();
		MethodBinding mb = tm.lookupMethodInClass(methName, obj.symbolType);
		
		int paramNum = ctx.params() == null ? 0 : ctx.params().methForms.size();
		ObjectBinding ob = checkMethod(obj, methName, mb, ctx.getStart().getLine(), paramNum,
				ctx.params());
		
		
		// Set type to return type
		bindings.put(ctx, ob);
		
		return null;
	}

	@Override
	public ParseTreeProperty<AbstractBinding> visitLocMeth(LocMethContext ctx) {
		if(ctx.params() != null) {
			ctx.params().accept(this);
		}
		
		String type = tm.lookupClass(currentClass.className.getText().toString()).getClassDescriptor().className;
		AbstractBinding obj = new ObjectBinding(null, type, null);
		
		String methName = ctx.meth.getText().toString();
		MethodBinding mb = tm.lookupMethodInClass(methName, obj.symbolType);
		
		int paramNum = ctx.params() == null ? 0 : ctx.params().methForms.size();
		ObjectBinding ob = checkMethod(obj, methName, mb, ctx.getStart().getLine(), paramNum,
				ctx.params());
		
		
		// Set type to return type
		bindings.put(ctx, ob);
		
		return null;
	}
	
	/**
	 * Check if method dispatch is valid.
	 * @param obj AbstractBinding containing the type of the calling object
	 * @param methName name of the method
	 * @param mb MethodBinding for the method
	 * @param line line that the method call starts on
	 * @param numArgs number of args passed to the method
	 * @param forms ParamsContext object for the parsed method call
	 * @return ObjectBinding containing the return type of the called method
	 */
	private ObjectBinding checkMethod(AbstractBinding obj, String methName, 
			MethodBinding mb, int line, int numArgs, ParamsContext forms) {
		
		if(obj.symbol != null && obj.symbol.equals("null")) {
			throw new WoolException(
					"Line "+line+
					", Class "+currentClass.className.getText().toString()+
					", null type has no methods");
		}
		
		// Check method exists
		if(mb == null) {
			throw new WoolException(
					"Line "+line+
					", Class "+currentClass.className.getText().toString()+
					", Method: " + methName+
					", does not exist for type: "+obj.symbolType);
		}
		
		MethodDescriptor md = mb.getMethodDescriptor();
		
		// Method exists, check params
		if(md.getArgumentTypes().size() != // Found method num args
				numArgs) { // Given num args
			throw new WoolException(
					"Line "+line+
					", Class "+currentClass.className.getText().toString()+
					", Method: " + methName+
					", expected : "+md.getArgumentTypes().size()+
					" arguments but given "+numArgs);
		} 
		
		// Check arg types
		for(int i=0; i<numArgs; i++) {
			String expectedType = md.argumentTypes.get(i);
			String foundType = bindings.get(forms.methForms.get(i)).symbolType;
			
			AbstractBinding con = conforms(new ObjectBinding(null, expectedType, null),
					new ObjectBinding(null, foundType, null));
			
			if(con == null) {
				throw new WoolException(
						"Line "+line+
						", Class "+currentClass.className.getText().toString()+
						", Method: " + methName+
						", given parameter number: "+(i+1)+
						" of type "+foundType+
						" does not conform to "+expectedType);
			}
		}
		
		
		String retType = mb.getMethodDescriptor().returnType;
		if(retType.equals("SELF_TYPE")) {
			return new ObjectBinding(null, obj.symbolType, null);
		}
		
		return new ObjectBinding(null, retType, null);
	}

	// Conditional Statements
	@Override
	public ParseTreeProperty<AbstractBinding> visitIfExpr(IfExprContext ctx) {
		ctx.cond.accept(this);
		ctx.then.accept(this);
		ctx.els.accept(this);
		
		// Make sure conditional is a bool
		AbstractBinding cond = bindings.get(ctx.cond);
		condType(cond.symbolType, ctx.cond.getStart().getLine());
		
		AbstractBinding then = bindings.get(ctx.then);
		AbstractBinding els = bindings.get(ctx.els);
		
		// Type of at least one statement is int or bool		
		if(then.symbolType.equals(boolBinding.symbolType)||
				then.symbolType.equals(intBinding.symbolType)||
				els.symbolType.equals(boolBinding.symbolType)||
				els.symbolType.equals(intBinding.symbolType)) {
			
			// Both not type int or bool
			if(!then.symbolType.equals(els.symbolType)) {
				throw new WoolException(
						"Line "+ctx.getStart().getLine()+
						", Class "+currentClass.className.getText().toString()+
						", Primitive types in if expression must match");
			}
			
		}
		
		// Do join operation and make sure it is not null
		AbstractBinding join;
		ClassBinding t = tm.lookupClass(then.symbolType);
		// Look at all parents of then and check to see if els ever conforms to one
		while(t != null) {
			join = conforms(t, els);
			
			if(join != null) {
				
				bindings.put(ctx, join);
				
				return null;
			}

			t = tm.lookupClass(t.getClassDescriptor().inherits);
		}
		
		// Probably never happens because everything is type object
		throw new WoolException(
				"Line "+ctx.getStart().getLine()+
				", Class "+currentClass.className.getText().toString()+
				", "+then.symbol+
				" join "+els.symbol+
				" returns null");
	}
	
	@Override
	public ParseTreeProperty<AbstractBinding> visitSelectExpr(SelectExprContext ctx) {
		// Visit all select alternatives
		for(SelectPartContext s : ctx.selectPart()) {
			s.accept(this);
		}
		
		// Check if any alternative is a primitive
		for(SelectPartContext s : ctx.selectPart()) {
			String type = bindings.get(s).symbolType;
			
			// Found one that is a primitive
			if(type.equals(intBinding.symbolType) || 
					type.equals(boolBinding.symbolType)) {
				
				// Make sure all alternative types is that primitive
				for(SelectPartContext s2 : ctx.selectPart()) {
					String type2 = bindings.get(s2).symbolType;
					
					if(!type.equals(type2)) {
						throw new WoolException(
								"Line "+ctx.getStart().getLine()+
								", Class "+currentClass.className.getText().toString()+
								", select expression alternatives with primitive types must have all types "+
								"match a sinlge primitive");
					}
				}
				
				// All alternatives are of the same primitive type
				bindings.put(ctx, bindings.get(s));
				
				return null;
			}
		}
		
		AbstractBinding join = bindings.get(ctx.selectPart().get(0));
		
		// Perform join on all alternative types
		for(int i=1; i<ctx.selectPart().size(); i++) {
			
			ClassBinding t = tm.lookupClass(join.symbol);
			// Join the current join with each type of the alternatives
			while(t != null) {
				AbstractBinding con = conforms(t, bindings.get(ctx.selectPart().get(i)));
				
				if(con != null) {
					
					join = con;
					break;
				}

				t = tm.lookupClass(t.getClassDescriptor().inherits);
			}
			
		}
		
		bindings.put(ctx, join);
		
		return null;
	}

	@Override
	public ParseTreeProperty<AbstractBinding> visitSelectPart(SelectPartContext ctx) {
		ctx.first.accept(this);
		ctx.second.accept(this);
		
		// Make sure cond is type bool
		AbstractBinding first = bindings.get(ctx.first);
		condType(first.symbolType, ctx.first.getStart().getLine());
		
		AbstractBinding second = bindings.get(ctx.second);
		bindings.put(ctx, second);
		
		return null;
	}

	@Override
	public ParseTreeProperty<AbstractBinding> visitWhileExpr(WhileExprContext ctx) {
		ctx.cond.accept(this);
		ctx.body.accept(this);
		
		// Make sure cond is type bool
		AbstractBinding cond = bindings.get(ctx.cond);
		condType(cond.symbolType, ctx.cond.getStart().getLine());
		
		bindings.put(ctx, objBinding);
		
		return null;
	}
	
	/**
	 * Makes sure the type is a bool.
	 * @param type type of a statement
	 * @param line line the statement is on
	 */
	private void condType(String type, int line) {
		if(!type.equals(boolBinding.symbolType)) {
			throw new WoolException(
					"Line "+line+
					", Class "+currentClass.className.getText().toString()+
					", Conditional must be of type boolean");
		}
	}

	// Equality operators
	@Override
	public ParseTreeProperty<AbstractBinding> visitEQ_Expr(EQ_ExprContext ctx) {
		ctx.right.accept(this);
		ctx.left.accept(this);
		
		AbstractBinding left = bindings.get(ctx.left);
		AbstractBinding right = bindings.get(ctx.right);
		
		// Type of at least one side is int or bool		
		if(left.symbolType.equals(boolBinding.symbolType)||
				left.symbolType.equals(intBinding.symbolType)||
				right.symbolType.equals(boolBinding.symbolType)||
				right.symbolType.equals(intBinding.symbolType)) {
			
			// Both not type int or bool
			if(!left.symbolType.equals(right.symbolType)) {
				throw new WoolException(
						"Line "+ctx.getStart().getLine()+
						", Class "+currentClass.className.getText().toString()+
						", Types in equality operator must match");
			}
		}
		
		bindings.put(ctx, boolBinding);
		
		return null;
	}

	@Override
	public ParseTreeProperty<AbstractBinding> visitNeg_Log_Expr(Neg_Log_ExprContext ctx) {
		ctx.expr().accept(this);
		
		AbstractBinding ab = bindings.get(ctx.expr());
		
		// Make sure expr is a bool
		if(!ab.symbolType.equals(boolBinding.symbolType)){
			throw new WoolException(
					"Line "+ctx.getStart().getLine()+
					", Class "+currentClass.className.getText().toString()+
					", Expression: "+ ctx.expr().getText().toString()+
					" must be of type boolean");
		}
		
		bindings.put(ctx, boolBinding);
		
		return null;
	}

	@Override
	public ParseTreeProperty<AbstractBinding> visitNullCheck(NullCheckContext ctx) {
		ctx.expr().accept(this);
		
		bindings.put(ctx, boolBinding);
		
		return null;
	}

	// Check the types of the int comparison operators
	@Override
	public ParseTreeProperty<AbstractBinding> visitComp_Expr(Comp_ExprContext ctx) {
		ctx.left.accept(this);
		ctx.right.accept(this);
		
		AbstractBinding left = bindings.get(ctx.left);
		AbstractBinding right = bindings.get(ctx.right);
		String leftText = ctx.left.getText().toString();
		String rightText = ctx.right.getText().toString();
		int leftLine = ctx.left.getStart().getLine();
		int rightLine = ctx.right.getStart().getLine();
		
		// Can only compare ints.
		checkBinMathOp(left, right, leftText, rightText, leftLine, rightLine);
		
		bindings.put(ctx, boolBinding);
		
		return null;
	}

	// Check the types of the arithmetic operators
	
	@Override
	public ParseTreeProperty<AbstractBinding> visitMulDivExpr(MulDivExprContext ctx) {
		ctx.left.accept(this);
		ctx.right.accept(this);
		
		AbstractBinding left = bindings.get(ctx.left);
		AbstractBinding right = bindings.get(ctx.right);
		String leftText = ctx.left.getText().toString();
		String rightText = ctx.right.getText().toString();
		int leftLine = ctx.left.getStart().getLine();
		int rightLine = ctx.right.getStart().getLine();
		
		// Can only multiply or divide ints
		checkBinMathOp(left, right, leftText, rightText, leftLine, rightLine);
		
		bindings.put(ctx, intBinding);
		
		return null;
	}

	@Override
	public ParseTreeProperty<AbstractBinding> visitAddSubExpr(AddSubExprContext ctx) {
		ctx.left.accept(this);
		ctx.right.accept(this);
		
		AbstractBinding left = bindings.get(ctx.left);
		AbstractBinding right = bindings.get(ctx.right);
		String leftText = ctx.left.getText().toString();
		String rightText = ctx.right.getText().toString();
		int leftLine = ctx.left.getStart().getLine();
		int rightLine = ctx.right.getStart().getLine();
		
		// Can only add or subtract ints.
		checkBinMathOp(left, right, leftText, rightText, leftLine, rightLine);
		
		bindings.put(ctx, intBinding);
		
		return null;
	}

	@Override
	public ParseTreeProperty<AbstractBinding> visitNegNumExpr(NegNumExprContext ctx) {
		ctx.expr().accept(this);
		
		AbstractBinding num = bindings.get(ctx.expr());
		
		// Can only make ints negative
		if(!num.symbolType.equals(intBinding.symbolType))
		{
			throw new WoolException(
					"Line "+ctx.expr().getStart().getLine()+
					", Class "+currentClass.className.getText().toString()+
					", " + ctx.expr().getText().toString()+
					" must be of type int");
		}
		
		bindings.put(ctx, intBinding);
		
		return null;
	}

	/**
	 * Check that 2 expressions are both of type int
	 * @param left first expression
	 * @param right second expression
	 * @param leftText text of the first expression
	 * @param rightText text of the second expression
	 * @param leftLine line that the first expression is on
	 * @param rightLine line that the second expression is on
	 */
	private void checkBinMathOp(AbstractBinding left, AbstractBinding right,
			String leftText, String rightText, int leftLine, int rightLine) {

		// Make sure the left is of type int
		if(!left.symbolType.equals(intBinding.symbolType))
		{
			throw new WoolException(
					"Line "+leftLine+
					", Class "+currentClass.className.getText().toString()+
					", Subexpression " + leftText+
					" must be of type int");
		}
		
		// Make sure the right is of type int
		if(!right.symbolType.equals(intBinding.symbolType))
		{
			throw new WoolException(
					"Line "+rightLine+
					", Class "+currentClass.className.getText().toString()+
					", Subexpression " + rightText+
					" must be of type int");
		}
	}
	
	// Visiting the constant expressions
	@Override
	public ParseTreeProperty<AbstractBinding> visitNullExpr(NullExprContext ctx) {
		bindings.put(ctx, nullBinding);
		return null;
	}

	@Override
	public ParseTreeProperty<AbstractBinding> visitTrueExpr(TrueExprContext ctx) {
		bindings.put(ctx, boolBinding);
		return null;
	}

	@Override
	public ParseTreeProperty<AbstractBinding> visitFalseExpr(FalseExprContext ctx) {
		bindings.put(ctx, boolBinding);
		return null;
	}

	@Override
	public ParseTreeProperty<AbstractBinding> visitIntExpr(IntExprContext ctx) {
		bindings.put(ctx, intBinding);
		return null;
	}
	
	@Override
	public ParseTreeProperty<AbstractBinding> visitStrExpr(StrExprContext ctx) {
		bindings.put(ctx, strBinding);
		return null;
	}
}
