package wool.symbol;

import org.antlr.v4.runtime.tree.ParseTreeProperty;

import wool.lexparse.WoolBaseVisitor;
import wool.lexparse.WoolParser.AssignContext;
import wool.lexparse.WoolParser.AssignExprContext;
import wool.lexparse.WoolParser.ClsContext;
import wool.lexparse.WoolParser.FormalContext;
import wool.lexparse.WoolParser.IdExprContext;
import wool.lexparse.WoolParser.LocMethContext;
import wool.lexparse.WoolParser.MethodContext;
import wool.lexparse.WoolParser.NewObjContext;
import wool.lexparse.WoolParser.ObjMethContext;
import wool.lexparse.WoolParser.ProgramContext;
import wool.lexparse.WoolParser.VardefContext;
import wool.symbol.BindingFactory.ClassBinding;
import wool.symbol.BindingFactory.MethodBinding;
import wool.symbol.BindingFactory.ObjectBinding;
import wool.utility.WoolException;

/**
 * Class to do the second pass through the parse tree.
 * Checks for inheritance redefinition errors and resolves 
 * identifier usages.
 * Returns parse tree annotations to be used by the type checker later.
 */
public class SymbolTableChecker extends WoolBaseVisitor<ParseTreeProperty<AbstractBinding>> {
	private TableManager tm;
	private ParseTreeProperty<AbstractBinding> bindings = new ParseTreeProperty<AbstractBinding>();
	private ClsContext currentClass;
	private MethodContext currentMethod;
	private int scopeLevel = 0;
	
	public SymbolTableChecker() {
		tm = TableManager.getInstance();
	}

	@Override
	public ParseTreeProperty<AbstractBinding> visitProgram(ProgramContext ctx) {
		super.visitProgram(ctx);
		return bindings;
	}

	@Override
	public ParseTreeProperty<AbstractBinding> visitCls(ClsContext ctx) {
		scopeLevel++;
		
		// Check that an inherited class exists
		String className = ctx.className.getText().toString();
		ClassDescriptor cd = tm.lookupClass(className).getClassDescriptor();
		String parentName = cd.inherits;
		
		ClassBinding cb = tm.lookupClass(parentName);
		
		// Make sure a parent class exists
		if(cb == null) {
			throw new WoolException(
					"Line "+ctx.getStart().getLine()+
					", Class "+ctx.className.getText().toString()+
					", Parent class does not exist: " + parentName);
		}
		
		// Make sure class is not Int, Bool, or Str
		if(cb.symbol.equals("Int") || cb.symbol.equals("Bool") || cb.symbol.equals("Str")) {
			throw new WoolException(
					"Line "+ctx.getStart().getLine()+
					", Class "+ctx.className.getText().toString()+
					", Cannot inherit from: " + cb.symbol);
		} 
		
		bindings.put(ctx.inhrt(), cb);
		
		// Make sure there are no cycles in inheritance graph
		while(cb != null) {
			if(cb.equals(tm.lookupClass(className))) {
				throw new WoolException(
						"Line "+ctx.getStart().getLine()+
						", Class "+ctx.className.getText().toString()+
						", Cycle in inheritance hierarchy: " + parentName);
			}

			cb = tm.lookupClass(cb.getClassDescriptor().inherits);	
		}
		
		currentClass = ctx;
		
		// Visit all class varDefs
		for(VardefContext v : ctx.classVars) {
			v.accept(this);
		}
		
		// Visit all class methods
		for(MethodContext m : ctx.classMeth) {
			m.accept(this);
		}
		
		
		scopeLevel--;
		return null;
	}

	@Override
	public ParseTreeProperty<AbstractBinding> visitVardef(VardefContext ctx) {
		// Check if variable definition is in class or method
		
		// ID "this" does not need to be defined
		if(ctx.ID().getText().toString().equals("this")) {
			throw new WoolException(
					"Line "+ctx.getStart().getLine()+
					", Class "+currentClass.className.getText().toString()+
					", Cannot define variable named \"this\"");
		}
		
		if(scopeLevel == 1) {
			// In class, check up hierarchy for duplicates
			ClassBinding cb = tm.lookupClass(currentClass.className.getText().toString());
			
			ObjectBinding ob = tm.lookupIDInClass(ctx.ID().getText().toString(), cb.getClassDescriptor().inherits);
			
			// Check parent classes for definitions of variable
			if(ob != null) {
				// Redefined, error
				throw new WoolException(
						"Line "+ctx.getStart().getLine()+
						", Class "+currentClass.className.getText().toString()+
						", Redefined parent variable: " + ctx.ID().getText().toString());
			}
		}
		
		// if in method, all set
		
		// Make sure type of class exists
		ClassBinding cb = tm.lookupClass(ctx.varType.getText().toString());
		
		if(ctx.varType.getText().toString().equals("int")) {
			cb = tm.lookupClass("Int");
		} else if(ctx.varType.getText().toString().equals("boolean")) {
			cb = tm.lookupClass("Bool");
		}
		
		// Type does not exist
		if(cb == null) {
			throw new WoolException(
					"Line "+ctx.getStart().getLine()+
					", Class "+currentClass.className.getText().toString()+
					", Type does not exist: " + ctx.TYPE().getText().toString());
		}
		
		bindings.put(ctx.ID(), cb);
		
		// Check variable init
		if(ctx.varInit != null) {
			ctx.varInit.accept(this);
		}
		
		return null;
	}

	@Override
	public ParseTreeProperty<AbstractBinding> visitMethod(MethodContext ctx) {
		// Make sure all duplicated method definitions are exactly the same
		scopeLevel ++;
		
		ClassBinding cb = tm.lookupClass(currentClass.className.getText().toString());
		String parent = cb.getClassDescriptor().inherits;
		
		// Current method descriptor
		MethodDescriptor mdThis = tm.lookupMethodInClass(
				ctx.ID().getText().toString(), currentClass.className.getText().toString())
				.getMethodDescriptor();
		
		// Look for any methods with the same name in parent classes
		MethodBinding mbOther = tm.lookupMethodInClass(ctx.ID().getText().toString(), parent);
		
		if(mbOther != null) {
			MethodDescriptor mdOther = mbOther.getMethodDescriptor();
			
			// Found a method make sure it is equal
			if(!mdThis.equals(mdOther)){
				throw new WoolException(
						"Line "+ctx.getStart().getLine()+
						", Class "+currentClass.className.getText().toString()+
						", Bad method redefinition: " + ctx.ID().getText().toString());
			}
		}
		
		currentMethod = ctx;
		
		for(FormalContext f : ctx.methForms) {
			f.accept(this);
		}
		
		// Check all variable and method refs in the vardefs
		for(VardefContext v : ctx.methVars) {
			v.accept(this);
		}
		
		// Check the variable and method refs in expr
		ctx.expr().accept(this);
		
		scopeLevel --;
		return null;
	}
	
	

	@Override
	public ParseTreeProperty<AbstractBinding> visitFormal(FormalContext ctx) {
		
		// ID "this" does not need to be defined
		if(ctx.ID().getText().toString().equals("this")) {
			throw new WoolException(
					"Line "+ctx.getStart().getLine()+
					", Class "+currentClass.className.getText().toString()+
					", Cannot define variable named \"this\"");
		}
		
		// Make sure type of class exists
		String type = ctx.formType.getText().toString();
		ClassBinding cb = tm.lookupClass(type);
		
		if(type.equals("int")) {
			cb = tm.lookupClass("Int");
		} else if(type.equals("boolean")) {
			cb = tm.lookupClass("Bool");
		}
		
		// Type does not exist
		if(cb == null) {
			throw new WoolException(
					"Line "+ctx.getStart().getLine()+
					", Class "+currentClass.className.getText().toString()+
					", Type does not exist: " + type);
		}
		
		bindings.put(ctx.ID(), cb);
		
		return null;
	}

	@Override
	public ParseTreeProperty<AbstractBinding> visitAssign(AssignContext ctx) {
	
		ctx.expr().accept(this);
		return null;
	}
	
	@Override
	public ParseTreeProperty<AbstractBinding> visitIdExpr(IdExprContext ctx) {
		// Make sure the id has been defined
		int line = ctx.getStart().getLine();
		int pos = ctx.getStart().getCharPositionInLine();
		ObjectBinding ob = null;
		
		// this is type current class
		if(ctx.ID().getText().toString().equals("this")) {
			
			bindings.put(ctx, new ObjectBinding(null, currentClass.className.getText().toString(), null));
			return null;
		}
		
		if(scopeLevel == 1) {
			// Check if identifier has been defined properly in the class scope
			ob = checkID(line, pos, ctx.ID().getText().toString());
		} else {
			// Check if a identifier has been defined properly in method scope
			ob = checkIDMeth(line, pos, ctx.ID().getText().toString());	
		}
		
		bindings.put(ctx, ob);
		
		return null;
	}
	
	@Override
	public ParseTreeProperty<AbstractBinding> visitAssignExpr(AssignExprContext ctx) {
		// Check the variable assigned to for usage errors
		int line = ctx.getStart().getLine();
		int pos = ctx.getStart().getCharPositionInLine();
		ObjectBinding ob = null;
		
		// Cannot assign to this
		if(ctx.ID().getText().toString().equals("this")) {
			throw new WoolException(
					"Line "+ctx.getStart().getLine()+
					", Class "+currentClass.className.getText().toString()+
					", Cannot assign to \"this\"");
		}
		
		if(scopeLevel == 1) {
			// Check if identifier has been defined properly in the class scope
			ob = checkID(line, pos, ctx.ID().getText().toString());
		} else {
			// Check if a identifier has been defined properly in method scope
			ob = checkIDMeth(line, pos, ctx.ID().getText().toString());
		}
		
		bindings.put(ctx.ID(), ob);
		
		// Check the assignment statement
		ctx.assign().accept(this);

		return null;
	}
	
	/**
	 * Check if an ID is valid in a method
	 * @param start line id is in
	 * @param pos position of id in line
	 * @param name name of the id
	 * @return ObjectBinding to be used to determine type of the ID
	 */
	private ObjectBinding checkIDMeth(int start, int pos,String name) {
		int line = start;
		int def = 0;
		int defCol = 0;
		ObjectBinding var = null;
		
		// Look for definition in method
		MethodBinding mb = tm.lookupMethodInClass(currentMethod.ID().getText().toString(),
				currentClass.className.getText().toString());
		
		MethodDescriptor md = mb.getMethodDescriptor();
		
		String className = currentClass.className.getText().toString();
		// Variable ref not defined in method
		if(!md.variables.containsKey(name)) {
			
			ObjectBinding varInClass = checkClass(name, className, line);
			
			var = varInClass;
		} else {
			// Check the line numbers
			var = md.variables.get(name);
			def = var.getToken().getLine();
			defCol = var.getToken().getCharPositionInLine();
			
			if(def > line || 
					(def == line && defCol > pos)) {
				// if line number is bad check the class for a definition first
				ObjectBinding varInClass = checkClass(name, className, line);
				
				if(varInClass != null) {
					return varInClass;
				}
				
				throw new WoolException(
						"Line "+line+
						", Class "+currentClass.className.getText().toString()+
						", Variable referenced before declared: " + name);
			}
		}
		
		
		return var;
	}
	
	/**
	 * Checks to see if a variable is defined in class hierarchy of a method.
	 * @param name name of the variable
	 * @param className name of the class the method is defined in
	 * @param line line the variable reference is on
	 * @return ObjectBinding with the type of the variable or null if it does not exist
	 */
	private ObjectBinding checkClass(String name, String className, int line) {
		ObjectBinding varInClass = tm.lookupIDInClass(name, className);
		
		// Variable not defined in class hierarchy
		if(varInClass == null) {
			throw new WoolException(
					"Line "+line+
					", Class "+className+
					", Method "+currentMethod.ID().getText().toString()+
					", Variable reference never defined: " + name);
		}
		
		return varInClass;
	}
	
	/**
	 * Checks if an id reference in a class is valid.
	 * @param start line number of id
	 * @param pos the position of the id in the line
	 * @param name name of id
	 * @return ObjectBinding that can be used to determine the type of the ID
	 */
	private ObjectBinding checkID(int start, int pos, String name) {
		int line = start;
		int def = 0;
		int defCol = 0;
		
		// Look for reference in parents
		ClassBinding cb = tm.lookupClass(currentClass.className.getText().toString());
		ObjectBinding ob = tm.lookupIDInClass(name,
				cb.getClassDescriptor().inherits);
		
		// Not in parents look in current class
		if(ob == null) {
			 ob = tm.lookupIDInClass(name,
						cb.getClassDescriptor().className);
			 
			 // id never defined
			 if(ob == null) {
				 throw new WoolException(
							"Line "+line+
							", Class "+currentClass.className.getText().toString()+
							", Variable reference never defined: " + name);
			 }
			 
			 def = ob.getToken().getLine();
			 defCol = ob.getToken().getCharPositionInLine();
		}
		
		// Class variable reference before declared
//		if(def > line || defCol > pos) {
//			throw new WoolException(
//					"Line "+line+
//					", Class "+currentClass.className.getText().toString()+
//					", Variable referenced before declared: " + name);
//		}
		
		return ob;
	}

	@Override
	public ParseTreeProperty<AbstractBinding> visitObjMeth(ObjMethContext ctx) {		
		// Check that any ids in expr have been defined
		ctx.obj.accept(this);

		
		// Check that any ids in the args have been defined
		if(ctx.params() != null) {
			ctx.params().accept(this);
		}
		
		return null;
	}

	@Override
	public ParseTreeProperty<AbstractBinding> visitLocMeth(LocMethContext ctx) {
		// Check that any ids in the args have been defined
		if(ctx.params() != null) {
			ctx.params().accept(this);
		}
	
		return null;
	}

	private MethodBinding checkMeth(int line, int numParams,String method, String className) {
		// Check that the method has been defined.
		MethodBinding mb = tm.lookupMethodInClass(method, className);
		
		// No method of that name exists in the class of the object.
		if(mb == null) {
			throw new WoolException(
					"Line "+line+
					", Class "+currentClass.className.getText().toString()+
					", Method does not exist: " + method);
		}
		
		MethodDescriptor md = mb.getMethodDescriptor();
		// Number of passed arguments don't match
		if(md.argumentTypes.size() != numParams) {
			throw new WoolException(
					"Line "+line+
					", Class "+currentClass.className.getText().toString()+
					", Method has wrong number of arguments: " + 
					"Expected "+md.argumentTypes.size()+
					" but found "+numParams);
		}
		
		return mb;
	}

	@Override
	public ParseTreeProperty<AbstractBinding> visitNewObj(NewObjContext ctx) {
		// Check that object instance exists
		String cls = ctx.obj.getText().toString();
		ClassBinding cb = tm.lookupClass(cls);
		
		if(cb == null) {
			throw new WoolException(
					"Line "+ctx.obj.getLine()+
					", Class "+currentClass.className.getText().toString()+
					", Class: " + cls +
					" does not exist");
		}
		
		// Make sure class is not Int or Bool
		if(cb.symbol.equals("Int") || cb.symbol.equals("Bool")) {
			throw new WoolException(
					"Line "+ctx.getStart().getLine()+
					", Class "+currentClass.className.getText().toString()+
					", Cannot create instance of primitive: " + cb.symbol);
		} 
		
		bindings.put(ctx.TYPE(), cb);
		
		return null;
	}
	
	

}
