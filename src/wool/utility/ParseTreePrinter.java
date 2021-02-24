/*******************************************************************************
 * This files was developed for CS4533: Techniques of Programming Language Translation
 * and/or CS544: Compiler Construction
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Copyright ©2020-21 Gary F. Pollice
 *******************************************************************************/

package wool.utility;

import java.util.*;
import wool.lexparse.WoolBaseVisitor;
import wool.lexparse.WoolParser.*;

/**
 * This is a pretty printer using the parse tree.
 */
public class ParseTreePrinter extends WoolBaseVisitor<String>
{
	private int indentLevel;
	private StringBuilder printedTree;
	private String indentString;
	
	/**
	 * Default constructor
	 */
	public ParseTreePrinter()
	{
		indentLevel = 0;
		indentString = "  ";
	}

	@Override
	public String visitProgram(ProgramContext ctx)
	{
		printedTree = new StringBuilder(">>> Program Start <<<\n");
		super.visitProgram(ctx);
		return printedTree.toString();
	}

	@Override
	public String visitCls(ClsContext ctx) {
		indentLevel++;
		indent();
		addText("class ", ctx.className.getText());
		if (ctx.inhrt() != null) {
			addText(" inherits ", ctx.inhrt().getText());
		}
		indent();
		addText("{");
//		super.visitCls(ctx);
		indentLevel++;
		for (VardefContext v : ctx.classVars) {
			v.accept(this);
		}
		for (MethodContext m : ctx.classMeth) {
			m.accept(this);
		}
		indentLevel--;
		indent();
		addText("}\n");
		indentLevel--;
		
		return null;
	}

	@Override
	public String visitVardef(VardefContext ctx) {
		indent();
		addText(ctx.ID().getText(), " : ", ctx.varType.getText());
		if (ctx.varInit != null) {
			addText(" <- ", ctx.varInit.accept(this));
		}
		addText(";");
		return null;
	}

	@Override
	public String visitMethod(MethodContext ctx)
	{
		addText("\n");
		indent();
		addText(ctx.ID().getText(), "(");
		String comma = "";
		for (FormalContext f : ctx.methForms) {
			addText(comma, f.ID().getText(), " : ", f.formType.getText());
			comma = ", ";
		}
		addText(") : ", ctx.methType.getText());
		indent();
		addText("{");
		indentLevel++;
		for (VardefContext v : ctx.methVars) {
			v.accept(this);
		}
		ctx.expr().accept(this);
		indentLevel--;
		indent();
		addText("}");
		return null;
	}




	/* *************************************************
	 * Expressions return the string.
	 ***************************************************/
	
	@Override
	public String visitObjMeth(ObjMethContext ctx) {
		String object = ctx.obj.accept(this);
		String methodName = ctx.meth.getText();
		List<String> arguments = new LinkedList<String>();
		for (ExprContext e : ctx.params().expr()) {
			arguments.add(e.accept(this));
		}
		StringBuilder sb = new StringBuilder(object + "." + methodName + "(");
		if (!arguments.isEmpty()) {
			sb.append(arguments.remove(0));
			for (String s : arguments) {
				sb.append(", ");
				sb.append(s);
			}
		}
		sb.append(")");
		return sb.toString();
	}
	
	@Override
	public String visitIdExpr(IdExprContext ctx) {
//		return ctx.getText().toString();
		addText("\n"+ctx.getText().toString());
		return "\nHello\n";
	}

	@Override
	public String visitNewObj(NewObjContext ctx) {
		// TODO Auto-generated method stub
		return "new " + ctx.obj.getText();
	}

	@Override
	public String visitNullCheck(NullCheckContext ctx) {
		// TODO Auto-generated method stub
		return "isnull " + ctx.expr().accept(this);
	}

	/* ********************************** helpers ********************************** */
	private void indent()
	{
		printedTree.append('\n');
		for (int i = 0; i < indentLevel; i++) {
			printedTree.append(indentString);
		}
	}
	
	private void addText(String ...strings)
	{
		for (String s : strings) {
			printedTree.append(s);
		}
	}
}
