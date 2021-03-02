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
	
	private String negFilesLoc = "test-files/symbolTableTestFiles/negTestFiles";
	private String posFilesLoc = "test-files/symbolTableTestFiles/posTestFiles";
	
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
	
	@ParameterizedTest
	@CsvSource({
		"/thisMeth.wl" // No error on create method named this
		})
	public void posTest(String file) throws IOException{
		ParseTree tree;
		
		WoolRunnerImpl imp = WoolFactory.makeParserRunner(
				CharStreams.fromFileName(posFilesLoc+file));
		tree = imp.parse();
		tree.accept(stb);

        assertTrue(true);
	}
	
	@ParameterizedTest
	@CsvSource({
		"/this.wl", // Error on create variable named this
		"/methVarRedef.wl", // Error on redef of var in meth
		"/classRedef.wl", // Test that redefining a class results in error
		"/strRedef.wl", // Test that redefining a Str
		"/classVarRedef.wl", // Test that redefining a class variable results in error
		"/classMethRedef.wl", // Test that redefining a class method results in error
		"/formRedef.wl", // Formal id redef
		"/thisArg.wl"
		})
	public void negTest(String file) throws IOException{
		ParseTree tree;
		
		WoolRunnerImpl imp = WoolFactory.makeParserRunner(
				CharStreams.fromFileName(negFilesLoc+file));
		tree = imp.parse();
		
		Executable e = () -> {
			tree.accept(stb);
        };
        assertThrows(Exception.class, e);
	}

}
