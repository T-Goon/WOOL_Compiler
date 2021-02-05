package wool.lexparse;

import static org.antlr.v4.runtime.Recognizer.EOF;
import static org.junit.jupiter.api.Assertions.*;
import static wool.lexparse.WoolLexer.*;
import java.util.stream.Stream;
import org.antlr.v4.runtime.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import junit.textui.TestRunner;
import wool.utility.*;

/**
 * Acceptance tests for Assignment 1, Cool-W Lexer.
 * 
 * @version Mar 23, 2018
 */
public class WoolLexerTest extends TestRunner
{
    @ParameterizedTest
    @MethodSource("textTypeProvider")
    void recognizeSingleToken(String text, int type)
    {
        WoolRunnerImpl runner = newLexer(toStream(text));
        Token t = runner.nextToken();
        assertEquals(type, t.getType());
        assertEquals(EOF, runner.nextToken().getType());
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
        "hello \"",
        "\"\"\""
    })
    void badSequenceOfLexemes(String text)
    {
        WoolRunner runner = newLexer(toStream(text));
        Token t = runner.nextToken();
        Executable e = () -> {
            Token x = t;
            while (x.getType() != EOF) {
                x = runner.nextToken();
            }
        };
        assertThrows(Exception.class, e);
    }
    
    // Helper methods
    /**
     * Turn the string into an ANTLRInputStream
     * 
     * @param text
     *            the original text
     * @return the stream created from the text
     */
    private CharStream toStream(String text)
    {
        return CharStreams.fromString(text);
    }
    
    /**
     * Create the lexer for the current test
     * 
     * @param input
     *            the ANTLRInputStream to be scanned
     * @return the lexer
     */
    private WoolRunnerImpl newLexer(CharStream input)
    {
        return WoolFactory.makeLexerRunner(input);
    }
    
    /**
     * Data for single lexeme tests. These tests take a string that should only return a
     * single token and then be at EOF. Each instance of the Arguments.of() method
     * provides the two parameters for these tests, a String and an int representing the
     * token type.
     * 
     * @return the stream of arguments
     */
    private static Stream<Arguments> textTypeProvider()
    {
        return Stream.of(
            // ID tests
    		Arguments.of("a", ID),
    		Arguments.of("world", ID),
            Arguments.of("hello", ID),
            Arguments.of("he089745099845____dfkjeigJJJJ33", ID),
            // TYPE tests
        	Arguments.of("H", TYPE),
        	Arguments.of("HELLO", TYPE),
        	Arguments.of("Hello", TYPE),
        	Arguments.of("Y30984872034", TYPE),
        	Arguments.of("P__________", TYPE),
        	Arguments.of("R9875hfg982gh74hfg287gg_uf832_gr_", TYPE),
        	// Keyword tests
        	Arguments.of("boolean", BOOL),
        	Arguments.of("class", CLASS),
        	Arguments.of("else", ELSE),
        	Arguments.of("end", END),
        	Arguments.of("false", FALSE),
        	Arguments.of("fi", FI),
        	Arguments.of("if", IF),
        	Arguments.of("in", IN),
        	Arguments.of("int", INT),
        	Arguments.of("inherits", INHERITS),
        	Arguments.of("isnull", ISNULL),
        	Arguments.of("loop", LOOP),
        	Arguments.of("new", NEW),
        	Arguments.of("null", NULL),
        	Arguments.of("pool", POOL),
        	Arguments.of("select", SELECT),
        	Arguments.of("then", THEN),
        	Arguments.of("true", TRUE),
        	Arguments.of("while", WHILE),
        	// String tests
        	Arguments.of("\"hello\"", STRING),
        	Arguments.of("\"4750934750world\"", STRING),
        	Arguments.of("\"However, I don't like to eat speakers.\"", STRING),
        	Arguments.of("\"However, I don't like \\\\n to eat speakers.\"", STRING),
        	Arguments.of("\"However, I d\\t\\ton't like \\\\n to eat    speakers.\"", STRING),
        	Arguments.of("\"\"", STRING),
        	Arguments.of("\"zz\"", STRING),
        	Arguments.of("\"][][[[[][][\"", STRING),
        	// Ignore comments tests
            Arguments.of("# \"comment\"\nworld", ID),
            Arguments.of("(* comment *) hello", ID)
            );
    }
}
