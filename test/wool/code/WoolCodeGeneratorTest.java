package wool.code;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import wool.code.testClasses.*;
import wool.symbol.AbstractBinding;
import wool.symbol.SymbolTableBuilder;
import wool.symbol.SymbolTableChecker;
import wool.symbol.TableManager;
import wool.type.TypeChecker;
import wool.utility.WoolFactory;
import wool.utility.WoolRunnerImpl;

class WoolCodeGeneratorTest {
	
	private static SymbolTableBuilder stb;
	private static SymbolTableChecker stc;
	private static TypeChecker tc;
	
	private static String testFilesLoc = "test-files/codeGenTestFiles";
	private static String outputLoc = "./woolcode/wool";
	
	@BeforeEach
	public void setUp() {
		
	}

	@ParameterizedTest
	@CsvSource({
		"/emptyClass.wl", // Single empty class
		"/manyEmptyCls.wl", // many empty classes
		"/SimpleInhrt.wl", // Empty classes with inheritance
		"/uninitClassVars.wl", // Uninitialized class variables
		"/simpleInitClassVarsTest.wl", // Class variables initialized with constants
		"/nonMethExprTest.wl", // Expressions done at class scope, no method calls
		"/methodDef.wl", // Method definitions and usage
		"/methodDispatch.wl", // Method dispatch
		"/CondsAndWhile.wl", // Conditional statements and loops
		"/WoolLibTest.wl", // Main method and wool library classes test
<<<<<<< HEAD
<<<<<<< HEAD
		"/harnessTest.wl"
=======
		"/harnessTest.wl",
		"/subStr.wl"
>>>>>>> e87302e7fba3adc12df649c369eeedb6f4c7ea7c
=======
		"/harnessTest.wl",
		"/subStr.wl"
>>>>>>> e87302e7fba3adc12df649c369eeedb6f4c7ea7c
	})
	public void genClasses(String file) throws IOException {
		TableManager.reset();
		stc = new SymbolTableChecker();
		stb = new SymbolTableBuilder();
		
		ParseTree tree;
		
		WoolRunnerImpl imp = WoolFactory.makeParserRunner(
				CharStreams.fromFileName(testFilesLoc + file));
		tree = imp.parse();
		
		tree.accept(stb);
		ParseTreeProperty<AbstractBinding> bindings = stc.visit(tree);
		tc = new TypeChecker(bindings);
		bindings = tc.visit(tree);
		
		CodeGenerator cg = new CodeGenerator(bindings);
		HashMap<String, byte[]> b = cg.visit(tree);
		
		// Create all class files
		for (String s : b.keySet()) {
            String classFilePath =  outputLoc + "/" + s;
            FileOutputStream fos = new FileOutputStream(classFilePath);
            fos.write(b.get(s));
            fos.close();
        }
		
        assertTrue(true);
        
	}
	
	/**
	 * Tests if the generated classes are correct.
	 * Must run the genClasses test alone first and also refresh 
	 * eclipse to get rid of errors before running this test.
	 */
	@Test
	public void runTests() {

		// A single empty class
		new SingleEmptyClsTest();

		// Many classes in a single file
		new ManyEmptyClsTest();
		new ManyEmptyClsTest2();
		
		// Inheritance off of an empty class
		new SimpleInhrtTest();

		// Test uninitialized variables of all types
		new UninitClassVarsTest();
		System.out.println();
		
		// Test initializing variables with constants
		new SimpleInitClassVarsTest();
		System.out.println();
		
		// Expressions at class scope level, no methods
		new NonMethExprTest();
		System.out.println();
		
		// Method definitions
		new MethodDefTest();
		System.out.println();
		
		new MethodDispatchTest();
		System.out.println();
		
		new CondsAndWhileTest();
		System.out.println();
		
		new WoolLibTest();
		System.out.println();
		
		new HarnessTest();
<<<<<<< HEAD
<<<<<<< HEAD
=======
		System.out.println();
		
		new SubStrTest();
>>>>>>> e87302e7fba3adc12df649c369eeedb6f4c7ea7c
=======
		System.out.println();
		
		new SubStrTest();
>>>>>>> e87302e7fba3adc12df649c369eeedb6f4c7ea7c

		assertTrue(true);

	}

}
