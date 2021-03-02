package wool.symbol;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import wool.lexparse.WoolBaseVisitor;
import wool.lexparse.WoolParser.ClsContext;
import wool.lexparse.WoolParser.FormalContext;
import wool.lexparse.WoolParser.MethodContext;
import wool.lexparse.WoolParser.ProgramContext;
import wool.lexparse.WoolParser.VardefContext;
import wool.symbol.BindingFactory.ClassBinding;
import wool.symbol.BindingFactory.MethodBinding;
import wool.symbol.BindingFactory.ObjectBinding;
import wool.utility.WoolException;

/**
 * 
 * Visitor class that does the first pass through the parse tree
 * to create the symbol table.
 * Does not catch any redefinition errors caused by inheritance.
 */
public class SymbolTableBuilder extends WoolBaseVisitor<Void> {
	private TableManager tm;
	private String currentClass;

	public SymbolTableBuilder() {
		tm = TableManager.getInstance();
	}

	@Override
	public Void visitProgram(ProgramContext ctx) {
		super.visitProgram(ctx);
		return null;
	}

	@Override
	public Void visitCls(ClsContext ctx) {
		String name = ctx.className.getText().toString();
		String inhrt = null;
		currentClass = name;
		
		if(ctx.inhrt() != null) {
			inhrt = ctx.inhrt().inhrtType.getText().toString();
		}
		
		tm.StartNewClass(name, inhrt);
		
		// Visit all the variable declaration nodes
		for (VardefContext v : ctx.classVars) {
			v.accept(this);
		}
		
		// Visit all the method definitions
		for (MethodContext m : ctx.classMeth) {
			m.accept(this);
		}
		
		tm.exitScope();
		
		return null;
	}

	@Override
	public Void visitVardef(VardefContext ctx) {
		// Add variable to method or class tables
		String name = ctx.ID().getText().toString();
		String type = ctx.varType.getText().toString();
		Token t = ctx.getStart();
		
		// Change int and bool to the class name
		if(type.equals("int")) {
			type = "Int";
		}
		
		if(type.equals("boolean")) {
			type = "Bool";
		}
		
		if(name.equals("this")) {
			throw new WoolException(
					"Line "+ctx.getStart().getLine()+
					", Class "+currentClass+
					", Cannont create attribute named \"this\"");
		}
		
		ObjectBinding var = tm.newVariable(name, type, t);
		
		// If the variable def is in a class, add the binding to the descriptor.
		if(tm.currentScopeLevel() == 1) {
			ClassBinding cb = tm.currentClassBinding;
			ClassDescriptor cd = cb.getClassDescriptor();
			cd.addVariable(var);
		} else {
			// Variable not at class scope so it is in a method
			MethodBinding mb = tm.currentMethodBinding;
			MethodDescriptor md = mb.getMethodDescriptor();
			md.addVariable(var);
		}
		
		return null;
	}

	@Override
	public Void visitMethod(MethodContext ctx) {
		String name = ctx.ID().getText().toString();
		String type = ctx.methType.getText().toString();
		
		// Change int and bool to the class name
		if(type.equals("int")) {
			type = "Int";
		}
		
		if(type.equals("boolean")) {
			type = "Bool";
		}
		
		MethodDescriptor md = new MethodDescriptor(name, type);
		
		// Add the types to the method descriptor
		if(ctx.methForms != null) {
			for(FormalContext f : ctx.methForms) {
				String t = f.formType.getText().toString();
				// Change int and bool to the class name
				if(t.equals("int")) {
					t = "Int";
				}
				
				if(t.equals("boolean")) {
					t = "Bool";
				}
				md.addArgumentType(t);
			}
		}
		
		tm.newMethod(md, ctx.getStart());
		
		// Enter method body scope
		tm.enterScope();
		
		// Formals
		for(FormalContext f : ctx.methForms) {
			f.accept(this);
		}
	
		// Method variable declarations
		for(VardefContext v : ctx.methVars) {
			v.accept(this);
		}
		
		// Exit method body scope
		tm.exitScope();
		
		return null;
	}

	@Override
	public Void visitFormal(FormalContext ctx) {
		// Add formal to method table
		String name = ctx.ID().getText().toString();
		String type = ctx.formType.getText().toString();
		
		// Change int and bool to the class name
		if(type.equals("int")) {
			type = "Int";
		}
		
		if(type.equals("boolean")) {
			type = "Bool";
		}
		
		if(name.equals("this")) {
			throw new WoolException(
					"Line "+ctx.getStart().getLine()+
					", Class "+currentClass+
					", Cannont create variable named \"this\"");
		}
		
		ObjectBinding form = tm.newVariable(name, type, ctx.getStart());
		MethodDescriptor md = tm.currentMethodBinding.getMethodDescriptor();
		md.addVariable(form);
		
		return null;
	}
	
	
	
}
