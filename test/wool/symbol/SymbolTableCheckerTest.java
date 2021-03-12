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

import wool.utility.WoolFactory;
import wool.utility.WoolRunnerImpl;

/**
 * Tests the second pass of the parse tree to resolve
 * bindings.
 * Assumes that the SymbolTableBuilder works and passes all
 * tests.
 *
 */
class SymbolTableCheckerTest {
	
	private SymbolTableBuilder stb;
	private SymbolTableChecker stc;

	private static String posFilesLoc = "test-files/symbolTableTestFiles/posTestFiles";
	private static String negFilesLoc = "test-files/symbolTableTestFiles/negTestFiles";
	
	@BeforeEach
	void setUp() {
		TableManager.reset();
		stc = new SymbolTableChecker();
		stb = new SymbolTableBuilder();
	}
	
	// Works if there is no error
	@ParameterizedTest
	@CsvSource({"/inhrt.wl", // Detects correct inheritance
		"/inhrtVar.wl", // Detects correct inheritance of variables
		"/classVarInit.wl", // Test referencing class variables.
		"/assignExprRef.wl", // Test referencing class variables from assign expression.
		"/vnm.wl", // Test method and class variable having the same name
		"/methRedef.wl", // Test inheritance method redefinition
		"/methVarRef.wl", // Test referencing variables in a method
		"/objMethExpr.wl", // Test referencing methods with . operator
		"/locMethRef.wl", // Test that using local method references is correct
		"/objInstance.wl",// Create instance of object correctly
		"/copy.wl", // Check the overriding of SELF_TYPE methods
		"/ref.wl",
		"/methClassVarRedef.wl", // check referenced class vars in methods before they are redefed in method
		"/forwardAttributeReference1.wl"
		})
	void posTests(String file) throws IOException {
		ParseTree tree;
		
		WoolRunnerImpl imp = WoolFactory.makeParserRunner(
				CharStreams.fromFileName(posFilesLoc+file));
		tree = imp.parse();
		
		tree.accept(stb);
		tree.accept(stc);
		
		assertTrue(true);
	}
	
	@ParameterizedTest
	@CsvSource({
		"/inhrtBad.wl", // Test inherited class does not exist
		"/inhrtBad2.wl", // Test inherited classes have a cycle
		"/inhrtBad3.wl", // Test inherited classes have a cycle
		"/inhrtBad4.wl", // Test inherited classes have a cycle
		"/inhrtVarBad.wl", // Test error on redefined inherited variables
		"/outsideRef.wl", // Test error on class variable use outside class
		"/methRedefBad.wl", // Test error on bad inherited method redefinition
		"/methVarRefBad.wl", // Test error on bad var ref in method
		"/objInstanceBad.wl", //Create instance of class that doesn't exist
		"/inhertInt.wl", // Error on inherit from int
		"/inhrtBool.wl", // Error on inherit from boolean
		"/inhrtStr.wl", // Error on inherit from Str
		"/intInstance.wl", // Error on instance of Int object
		"/boolInstance.wl", // Error on instance of Bool object
		"/varDefBadType.wl", // Variable def with non-existant type
		"/this2.wl", // Error on assign to this
		"/selfBad.wl",
		"/ref.wl",
		"/badForm.wl"
	})
	void negTests(String file) throws IOException {
		ParseTree tree;
		WoolRunnerImpl imp = WoolFactory.makeParserRunner(
				CharStreams.fromFileName(negFilesLoc+file));
		tree = imp.parse();
		
		tree.accept(stb);
		
		Executable e = () -> {
			tree.accept(stc);
        };
		
        System.out.print(file+" ");
        System.out.println(assertThrows(Exception.class, e).getMessage());
	}
}
