package wool.sem;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import wool.lexparse.WoolLexer;
import wool.lexparse.WoolParser;
import wool.lexparse.WoolParser.ProgramContext;
import wool.utility.ParseTreePrinter;
import wool.utility.WoolException;
import wool.utility.WoolFactory;

class RandomTester {

	@ParameterizedTest
    @ValueSource( strings = {"test.wl"})
	void test(String f) throws IOException{
		ParseTreePrinter printer = new ParseTreePrinter();

		final WoolLexer lexer = new WoolLexer(CharStreams.fromFileName("test-files/semTestFiles/"+f));

        lexer.addErrorListener(
                new BaseErrorListener() {
                    @Override
                    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                            int line, int charPositionInLine, String msg,
                            RecognitionException e)
                    {
                        throw new WoolException(msg, e);
                    }
                }
        );
        
        final CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        final WoolParser parser = new WoolParser(tokenStream);

        parser.addErrorListener(
                new BaseErrorListener() {
                    @Override
                    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                            int line, int charPositionInLine, String msg,
                            RecognitionException e)
                    {
                        throw new WoolException(
                            e == null ? "Recoverable parser error" : e.getMessage(), e);
                    }
                }
        );
        
        ProgramContext tree = parser.program();
        
        String str = printer.visit(tree);
        
        System.out.print(str);
        
        assertTrue(true);
	}

}
