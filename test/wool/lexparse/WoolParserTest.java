/*******************************************************************************
 * This files was developed for CS4233: Object-Oriented Analysis & Design.
 * The course was taken at Worcester Polytechnic Institute.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Copyright ©2020 Gary F. Pollice
 *******************************************************************************/

package wool.lexparse;

import static org.junit.jupiter.api.Assertions.*;
import java.io.*;
import java.util.*;
import java.util.stream.Stream;
import javax.swing.JFrame;
import org.antlr.v4.gui.TreeViewer;
import org.antlr.v4.runtime.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import wool.testutil.WoolTestRunner;
import wool.utility.WoolRunnerImpl;
import org.junit.jupiter.api.function.Executable;

/**
 * Description
 */
class WoolParserTest extends WoolTestRunner
{
    /**
     * Clear the state for the new test
     * @throws java.lang.Exception
     */
    @BeforeEach
    void setUp() throws Exception
    {
        parser = null;
        tree = null;
        runner = testRunner = new WoolRunnerImpl();
    }
    
    @ParameterizedTest
    @ValueSource( strings = {
        "emptyClass.wl",
        "classWithVariables.wl",
        "multipleClasses.wl", // program parser rule good
        "inheritance.wl", // class parser rule is good
        "vardefTests.wl", // vardef parser rule is good
        "methodTests.wl", // method parser rule good
        "formalsTest.wl", // formals parser rule good
        "exprTest1.wl", // expr parser rule good up to select
        "exprTest2.wl", // expr parser rule good up to - expr rule
        "exprTest3.wl", // expr parser rule good up to ~ expr rule
        "exprTest4.wl", // expr parser rule is good
        "exprTest.wl", // Borrowed test case for exprs
        "commentsTest.wl", // Comments are all set
        "commentTest.wl", // Borrowed test case for comments
        "stringTest.wl", // Borrowed test case for strings
        "methodArgExample.wl", // Borrowed test case for method args
        "stringArgTests.wl", // Borrowed test case for strings as args
        "vardefTests.wl" // Borrowed test case for vardefs
    })
    void useTestFiles(String f) throws IOException
    {
        doParse(CharStreams.fromFileName("test-files/posTestFiles/" + f));
        assertNotNull(tree);
    }
    
    @ParameterizedTest
    @ValueSource( strings = {
            "empty.wl",
            "randomText.wl",
            "tooManyInherts.wl",
            "vardefBad.wl",
            "vardefBad2.wl",
            "vardefBad3.wl",
            "vardefBad4.wl",
            "nestedClasses.wl",
            "badMethod.wl",
            "badMethod2.wl",
            "badMethod3.wl",
            "badFormal.wl"})
    void negTests(String f) {
    	Executable e = () ->{
    		doParse(CharStreams.fromFileName("test-files/negTestFiles/" + f));
    	};
    	
    	assertThrows(Exception.class, e);
    }
}
