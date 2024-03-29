package wool.utility;

import java.io.FileOutputStream;
import java.util.*;
import java.util.function.Supplier;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import wool.code.CodeGenerator;
import wool.lexparse.*;
import wool.symbol.AbstractBinding;
//import wool.symbol.SymbolTableBuilder;
import wool.symbol.SymbolTableBuilder;
import wool.symbol.SymbolTableChecker;
import wool.type.TypeChecker;

public class WoolRunnerImpl implements WoolRunner
{
    private WoolLexer lexer;
    private WoolParser parser;
    private ParserRuleContext parseTree;
    
    private Supplier<Token> nextToken;
    
    /**
     * Default constructor. Everything is uninitialized.
     */
    public WoolRunnerImpl()
    {
        nextToken = () -> { throw new WoolException("Lexer has not been initialized"); } ;
    }

    /************************************************************************** 
     * Compiler Actions 
     * These methods will usually be called by external clients. These are the
     * methods called by the WoolRunner interface
     */
    
    /*
     * @see cool.utility.CoolRunner#nextToken()
     */
    @Override
    public Token nextToken()
    {
        return nextToken.get();
    }
    
    /*
     * @see cool.utility.CoolRunner#parse()
     */
    @Override
    public ParserRuleContext parse()
    {
        parseTree = parser.program();
        return parseTree;
    }
    
    @Override
    public ParserRuleContext semantic()
    {
        parseTree = parse();
        SymbolTableBuilder stb = new SymbolTableBuilder();
        
        parseTree.accept(stb);
        SymbolTableChecker stc = new SymbolTableChecker();
        
		ParseTreeProperty<AbstractBinding> bindings = stc.visit(parseTree);
		TypeChecker tc = new TypeChecker(bindings);
		
        return parseTree;
    }
//    
//    @Override
//    public ASTNode createAST()
//    {
//        parseTree = parse();
//        ASTCreator creator = new ASTCreator();
//        ast = parseTree.accept(creator);
//        return ast;
//    }
    
//    @Override
//    public ASTNode typecheck()
//    {
//        createAST();
//        ast.accept(new SymbolTableChecker());
//        ast.accept(new TypeChecker());
//        return ast;
//    }
//    
//    @Override
//    public LinkedList<IRinstruction> makeIR()
//    {
//        typecheck();
//        IRCreator irc = new IRCreator();
//        ast.accept(irc);
//        ir = irc.ir;
//        return ir;
//    }
//    
    @Override
    public Map<String, byte[]> compile()
    {
    	parseTree = parse();
        SymbolTableBuilder stb = new SymbolTableBuilder();
        
        parseTree.accept(stb);
        SymbolTableChecker stc = new SymbolTableChecker();
        
		ParseTreeProperty<AbstractBinding> bindings = stc.visit(parseTree);
		TypeChecker tc = new TypeChecker(bindings);
		bindings = tc.visit(parseTree);
 
        CodeGenerator cg = new CodeGenerator(bindings);
		
        return cg.visit(parseTree);
    }

    /************************************************************************** 
     * Initializers
     * These methods are called by the factory in order to set up and 
     * initialize the compiler components.
     */
    
    /**
     * Set the lexer and change the nextToken variable
     * @param lexer the lexer to set
     */
    public void setLexer(WoolLexer lexer)
    {
        this.lexer = lexer;
        nextToken = () -> lexer.nextToken();
    }

    public void setParser(WoolParser parser)
    {
        this.parser = parser;
    }

    /**
     * @return the lexer
     */
    public WoolLexer getLexer()
    {
        return lexer;
    }

    /**
     * @return the parser
     */
    public WoolParser getParser()
    {
        return parser;
    }
    
    public ParserRuleContext getParseTree()
    {
        return parseTree;
    }

//    /**
//     * @return the ast
//     */
//    public ASTNode getAst()
//    {
//        return ast;
//    }
//    
//    /**
//     * get the IR code
//     */
//    public LinkedList<IRinstruction> getIR()
//    {
//        return ir;
//    }
}
