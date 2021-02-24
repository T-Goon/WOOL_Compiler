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
import wool.symbol.BindingFactory.ObjectBinding;

public class SymbolTableBuilder extends WoolBaseVisitor<Void> {
	private TableManager tm;
//	ParseTreeProperty<Integer> values = new ParseTreeProperty<Integer>();

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
		String name = ctx.ID().getText().toString();
		String type = ctx.varType.getText().toString();
		Token t = ctx.getStart();
		
		ObjectBinding var = tm.newVariable(name, type, t);
		
		// If the variable def is in a class, add the binding to the descriptor.
		if(tm.currentScopeLevel() == 1) {
			ClassBinding cb = tm.currentClassBinding;
			ClassDescriptor cd = cb.getClassDescriptor();
			cd.addVariable(var);
		}
		
		return null;
	}

	@Override
	public Void visitMethod(MethodContext ctx) {
		String name = ctx.ID().getText().toString();
		String type = ctx.methType.toString().toString();
		
		MethodDescriptor md = new MethodDescriptor(name, type);
		
		// Add the types to the method descriptor
		if(ctx.methForms != null) {
			for(FormalContext f : ctx.methForms) {
				md.addArgumentType(f.formType.getText().toString());
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
		
		// TODO The expr
		
		// Exit method body scope
		tm.exitScope();
		
		return null;
	}

	@Override
	public Void visitFormal(FormalContext ctx) {
		String name = ctx.ID().getText().toString();
		String type = ctx.formType.getText().toString();
		
		tm.newVariable(name, type, ctx.getStart());
		
		return null;
	}
	
	
	
}
