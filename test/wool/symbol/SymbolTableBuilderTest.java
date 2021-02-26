package wool.symbol;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import wool.utility.WoolFactory;
import wool.utility.WoolRunnerImpl;

/**
 * Tests the first pass of the parse tree to build the symbol table.
 *
 */
class SymbolTableBuilderTest {
	
	private TableManager tm;
	private SymbolTableBuilder stb;
	
	private static ParseTree cTree;
	private static ParseTree cvTree;
	private static ParseTree cmTree;
	private static ParseTree mvTree;
	private static ParseTree crdTree;
	private static ParseTree cvrdTree;
	private static ParseTree cmrdTree;
	private static ParseTree mvrdTree;
	
	@BeforeAll
	static void makeTrees() throws IOException {
		// Parse Tree for classTest
		WoolRunnerImpl imp = WoolFactory.makeParserRunner(
				CharStreams.fromFileName("test-files/symbolTableTestFiles/posTestFiles/class.wl"));
		cTree = imp.parse();
		
		// Parse Tree for classVarTest
		imp = WoolFactory.makeParserRunner(
				CharStreams.fromFileName("test-files/symbolTableTestFiles/posTestFiles/classVar.wl"));
		cvTree = imp.parse();
		
		// Parse Tree for classMethTest
		imp = WoolFactory.makeParserRunner(
				CharStreams.fromFileName("test-files/symbolTableTestFiles/posTestFiles/classMeth.wl"));
		cmTree = imp.parse();
		
		// Parse Tree for methVarTest
		imp = WoolFactory.makeParserRunner(
				CharStreams.fromFileName("test-files/symbolTableTestFiles/posTestFiles/methVars.wl"));
		mvTree = imp.parse();
		
		// Parse Tree for classRedefTest
		imp = WoolFactory.makeParserRunner(
				CharStreams.fromFileName("test-files/symbolTableTestFiles/negTestFiles/classRedef.wl"));
		crdTree = imp.parse();
		
		// Parse Tree for classVarRedefTest
		imp = WoolFactory.makeParserRunner(
				CharStreams.fromFileName("test-files/symbolTableTestFiles/negTestFiles/classVarRedef.wl"));
		cvrdTree = imp.parse();
		
		// Parse Tree for classMethRedefTest
		imp = WoolFactory.makeParserRunner(
				CharStreams.fromFileName("test-files/symbolTableTestFiles/negTestFiles/classMethRedef.wl"));
		cmrdTree = imp.parse();
		
		// Parse Tree for methVarRedefTest
		imp = WoolFactory.makeParserRunner(
				CharStreams.fromFileName("test-files/symbolTableTestFiles/negTestFiles/methVarRedef.wl"));
		mvrdTree = imp.parse();
	}
	
	@BeforeEach
	void setUp() {
		TableManager.reset();
		tm = TableManager.getInstance();
		stb = new SymbolTableBuilder();
	}

	// Test adding classes to the symbol table
	@ParameterizedTest
	@CsvSource({
			"SomeClass",
			"Simple2",
			"Simple3",
			"B"
	})
	void classTest(String cls) throws IOException{
		
		cTree.accept(stb);
		
		assertNotNull(tm.lookupClass(cls));
	}
	
	// Test declaring variables in classes.
	@ParameterizedTest
	@CsvSource({
			"var1, Simple",
			"var2, Simple",
			"var3, Simple2",
			"var4, Simple2",
			"var5, Simple3",
			"var6, Simple3",
	})
	void classVarTest(String var, String cls) throws IOException{

		cvTree.accept(stb);
		
		assertNotNull(tm.lookupIDInClass(var, cls));
	}
	
	// Test defining methods in a class
	@ParameterizedTest
	@CsvSource({
			"method1, Simple",
			"method2, Simple",
			"method3, Simple2",
			"method4, Simple2",
			"method5, Simple3",
			"method6, Simple3",
	})
	void classMethTest(String meth, String cls) throws IOException{

		cmTree.accept(stb);
		
		assertNotNull(tm.lookupMethodInClass(meth, cls));
	}
	
	// Test defining variables and formals in methods
	@ParameterizedTest
	@CsvSource({
			"form1, 1",
			"form2, 1",
			"var1, 1",
			"var2, 1",
			"form3, 2",
			"var3, 2",
			"var4, 2"
	})
	void methVarTest(String var, int table) throws IOException{
		
		mvTree.accept(stb);
		
		// Symbol table for scope of method
		SymbolTable t = tm.getTables().get(table);
		
		assertNotNull(t.lookup(var));
	}
	
	// Test that redefining a class results in error
	@Test
	void classRedefTest() {
		
		Executable e = () -> {
			crdTree.accept(stb);
        };
        assertThrows(Exception.class, e);
	}
	
	// Test that redefining a class variable results in error
	@Test
	void classVarRedefTest() {
		
		Executable e = () -> {
			cvrdTree.accept(stb);
        };
        assertThrows(Exception.class, e);
	}
	
	// Test that redefining a class method results in error
	@Test
	void classMethRedefTest() {
		
		Executable e = () -> {
			cmrdTree.accept(stb);
        };
        assertThrows(Exception.class, e);
	}
	
	// Test that redefining a method variable results in error
	@Test
	void methVarRedefTest() {
		
		Executable e = () -> {
			mvrdTree.accept(stb);
        };
        assertThrows(Exception.class, e);
	}
	
//	@Test
//	void test() throws IOException{
//		WoolRunnerImpl imp = WoolFactory.makeParserRunner(
//				CharStreams.fromFileName("test-files/symbolTableTestFiles/classVar.wl"));
//		ParseTree tree = imp.parse();
//		
//		tree.accept(stb);
//		
//		ClassBinding d = tm.lookupClass("Simple");
//		
//		ClassDescriptor cd = d.getClassDescriptor();
//		
//		ObjectBinding var = cd.getVariable("var1");
//		
//		System.out.println(var.toString());
//		
//		assertTrue(true);
//	}

}
