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
        "\" hello",
        "\"\"\"",
        "\"THis is not \n O.K.\"",
        "!!!!!!",
        "someid ! Sometype"
    })
    void badSequenceOfLexemes(String text)
    {
        WoolRunner runner = newLexer(toStream(text));
        
        Executable e = () -> {
            Token x = runner.nextToken();
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
            Arguments.of("\the089745099845____dfkjeigJJJJ33\t", ID),
            // TYPE tests
        	Arguments.of("H", TYPE),
        	Arguments.of("HELLO", TYPE),
        	Arguments.of("Hello", TYPE),
        	Arguments.of("Y30984872034", TYPE),
        	Arguments.of("P__________\n", TYPE),
        	Arguments.of("R9875hfg982gh74hfg287gg_uf832_gr_", TYPE),
        	Arguments.of("int", TYPE),
        	Arguments.of("boolean", TYPE),
        	// Keyword tests
        	Arguments.of("class", CLASS), Arguments.of("true", TRUE),
        	Arguments.of("else", ELSE), Arguments.of("end", END),
        	Arguments.of("false", FALSE), Arguments.of("fi", FI),
        	Arguments.of("if", IF), Arguments.of("in", IN),
        	Arguments.of("inherits", INHERITS), Arguments.of("while", WHILE),
        	Arguments.of("isnull", ISNULL), Arguments.of("loop", LOOP),
        	Arguments.of("new", NEW), Arguments.of("null", NULL),
        	Arguments.of("pool", POOL), Arguments.of("select", SELECT),
        	Arguments.of("then", THEN),
        	// Operators and special characters test
        	Arguments.of("<-", ASSIGN), Arguments.of("+", PLUS),
        	Arguments.of("-", MINUS), Arguments.of("*", TIMES),
        	Arguments.of("/", DIV), Arguments.of("<", LT),
        	Arguments.of("<=", LTE), Arguments.of("=", EQ),
        	Arguments.of("~=", AE), Arguments.of(">=", GTE),
        	Arguments.of(">", GT), Arguments.of("(", LP),
        	Arguments.of(")", RP), Arguments.of(".", DOT),
        	Arguments.of(":", TS), Arguments.of(";", EL),
        	Arguments.of(",",PS),  Arguments.of("~", NEG),
        	// String tests
        	Arguments.of("\"hello\"", STRING),
        	Arguments.of("     \"hello\"    ", STRING),
        	Arguments.of("\"4750934750world\"", STRING),
        	Arguments.of("\"However, I don't like to eat speakers.\"", STRING),
        	Arguments.of("\"However, I don't like \\\\n to eat speakers.\"", STRING),
        	Arguments.of("\"However, I d\\t\\ton't like \\\\n to eat    speakers.\"", STRING),
        	Arguments.of("\"\"", STRING),
        	Arguments.of("\"zz\"", STRING),
        	Arguments.of("\"][][[[[][][\"", STRING),
        	Arguments.of("\"''\"", STRING),
        	Arguments.of("\"\'\"", STRING),
        	Arguments.of("\"\\\"\"", STRING),
        	Arguments.of("\"\\\\n\"", STRING),
        	Arguments.of("\"THis is \\\n O.K.\"", STRING),
        	Arguments.of("\" \\\"This is a Quote$$$\\\"\"", STRING),
        	Arguments.of("\";V!l_*rpo%faD\\f:q\\bp)#e\\\"\\r\"", STRING),
        	// Ignore comments tests
            Arguments.of("# \"comment\"\nworld", ID),
            Arguments.of("(* comment *) hello", ID),
            Arguments.of("class (* comment *) ", CLASS),
            Arguments.of("class (* comment(* comment *)(* comment *) *) ", CLASS),
            Arguments.of("(* comment(* comment *)(* comment *) *)class  ", CLASS),
            Arguments.of("\"string \" (* comment *) ", STRING),
            Arguments.of("\"string \" #agt93ubsn9e8y tb265sd4fg98s4g   EOF", STRING)
            );
    }
   
}
