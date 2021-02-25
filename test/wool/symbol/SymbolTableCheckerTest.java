package wool.symbol;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

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
	
	private static ParseTree iTree;
	private static ParseTree ibTree;
	private static ParseTree ibcTree;
	private static ParseTree ibc2Tree;
	private static ParseTree ivTree;
	private static ParseTree ivbTree;
	private static ParseTree cviTree;
	private static ParseTree cvibTree;
	private static ParseTree aerTree;
	private static ParseTree aerbTree;
	private static ParseTree orTree;
	private static ParseTree vmnTree;
	private static ParseTree mrdTree;
	private static ParseTree mrdbTree;
	
	private static String posFilesLoc = "test-files/symbolTableTestFiles/posTestFiles";
	private static String negFilesLoc = "test-files/symbolTableTestFiles/negTestFiles";
	
	@BeforeAll
	static void makeTrees() throws IOException {
		// Parse Tree for inhertTest
		WoolRunnerImpl imp = WoolFactory.makeParserRunner(
				CharStreams.fromFileName(posFilesLoc+"/inhrt.wl"));
		iTree = imp.parse();
		
		// Parse Tree for inhertBadTest
		imp = WoolFactory.makeParserRunner(
				CharStreams.fromFileName(negFilesLoc+"/inhrtBad.wl"));
		ibTree = imp.parse();
		
		// Parse Tree for inhertBadTest2
		imp = WoolFactory.makeParserRunner(
				CharStreams.fromFileName(negFilesLoc+"/inhrtBad2.wl"));
		ibcTree = imp.parse();
		
		// Parse Tree for inhertBadTest3
		imp = WoolFactory.makeParserRunner(
				CharStreams.fromFileName(negFilesLoc+"/inhrtBad3.wl"));
		ibc2Tree = imp.parse();
		
		// Parse Tree for inhertVarTest
		imp = WoolFactory.makeParserRunner(
				CharStreams.fromFileName(posFilesLoc+"/inhrtVar.wl"));
		ivTree = imp.parse();
		
		// Parse Tree for inhertVarBadTest
		imp = WoolFactory.makeParserRunner(
				CharStreams.fromFileName(negFilesLoc+"/inhrtVarBad.wl"));
		ivbTree = imp.parse();
		
		// Parse Tree for classVarInitTest
		imp = WoolFactory.makeParserRunner(
				CharStreams.fromFileName(posFilesLoc+"/classVarInit.wl"));
		cviTree = imp.parse();
		
		// Parse Tree for classVarInitBadTest
		imp = WoolFactory.makeParserRunner(
				CharStreams.fromFileName(negFilesLoc+"/classVarInitBad.wl"));
		cvibTree = imp.parse();
		
		// Parse Tree for classVarInitBadTest
		imp = WoolFactory.makeParserRunner(
				CharStreams.fromFileName(negFilesLoc+"/outsideRef.wl"));
		orTree = imp.parse();
		
		// Parse Tree for assignExprRefTest
		imp = WoolFactory.makeParserRunner(
				CharStreams.fromFileName(posFilesLoc+"/assignExprRef.wl"));
		aerTree = imp.parse();
		
		// Parse Tree for assignExprRefBadTest
		imp = WoolFactory.makeParserRunner(
				CharStreams.fromFileName(negFilesLoc+"/assignExprRefBad.wl"));
		aerbTree = imp.parse();
		
		// Parse Tree for varMethNameTest
		imp = WoolFactory.makeParserRunner(
				CharStreams.fromFileName(posFilesLoc+"/vnm.wl"));
		vmnTree = imp.parse();
		
		// Parse Tree for methRedefTest
		imp = WoolFactory.makeParserRunner(
				CharStreams.fromFileName(posFilesLoc+"/methRedef.wl"));
		mrdTree = imp.parse();
				
		// Parse Tree for methRedefBadTest
		imp = WoolFactory.makeParserRunner(
				CharStreams.fromFileName(negFilesLoc+"/methRedefBad.wl"));
		mrdbTree = imp.parse();
	}

	@BeforeEach
	void setUp() {
		TableManager.reset();
		stc = new SymbolTableChecker();
		stb = new SymbolTableBuilder();
	}
	
	// Test inheritance
	// Works if there is no error
	@Test
	void inhertTest() {
		
		iTree.accept(stb);
		iTree.accept(stc);
		
		assertTrue(true);
	}
	
	// Test inherited class does not exist
	@Test
	void inhertBadTest() {
		System.out.print("inhertBadTest: ");
		ibTree.accept(stb);
		
		Executable e = () -> {
			ibTree.accept(stc);
        };
		
        System.out.println(assertThrows(Exception.class, e).getMessage());
	}

	// Test inherited classes have a cycle
	@Test
	void inhertBadTest2() {
		System.out.print("inhertBadTest2: ");
		ibcTree.accept(stb);
		
		Executable e = () -> {
			ibcTree.accept(stc);
        };
		
        System.out.println(assertThrows(Exception.class, e).getMessage());
	}
	
	// Test inherited classes have a cycle
	@Test
	void inhertBadTest3() {
		System.out.print("inhertBadTest3: ");
		ibc2Tree.accept(stb);
		
		Executable e = () -> {
			ibc2Tree.accept(stc);
        };
		
		System.out.println(assertThrows(Exception.class, e).getMessage());
	}
	
	// Test inheritance of variables
	// Works if there is no error
	@Test
	void inhertVarTest() {
		
		ivTree.accept(stb);
		ivTree.accept(stc);
		
		assertTrue(true);
	}
	
	// Test error on redefined inherited variables
	@Test
	void inhertVarBadTest() {
		System.out.print("inhertVarBadTest: ");
		ivbTree.accept(stb);
		
		Executable e = () -> {
			ivbTree.accept(stc);
        };
		
		System.out.println(assertThrows(Exception.class, e).getMessage());
	}
	
	// Test referencing class variables.
	@Test
	void clasVarInitTest() {
		
		cviTree.accept(stb);
		cviTree.accept(stc);

		
		assertTrue(true);
	}
	
	// Test error on class variable use before definition
	@Test
	void classVarInitBadTest() {
		System.out.print("classVarInitBadTest: ");
		cvibTree.accept(stb);
		
		Executable e = () -> {
			cvibTree.accept(stc);
        };
		
		System.out.println(assertThrows(Exception.class, e).getMessage());
	}
	
	// Test error on class variable use outside class
	@Test
	void outsideRefTest() {
		System.out.print("outsideRefTest: ");
		orTree.accept(stb);
		
		Executable e = () -> {
			orTree.accept(stc);
        };
		
		System.out.println(assertThrows(Exception.class, e).getMessage());
	}
	
	// Test referencing class variables from assign expression.
	@Test
	void assignExprRefTest() {
		
		aerTree.accept(stb);
		aerTree.accept(stc);

		
		assertTrue(true);
	}
	
	// Test error on class variable use before definition from assign expression.
	@Test
	void assignExprRefBadTest() {
		System.out.print("assignExprRefBadTest: ");
		aerbTree.accept(stb);
		
		Executable e = () -> {
			aerbTree.accept(stc);
        };
		
		System.out.println(assertThrows(Exception.class, e).getMessage());
	}
	
	// Test method and class variable having the same name
	@Test
	void varMethNameTest() {
		
		vmnTree.accept(stb);
		vmnTree.accept(stc);

		
		assertTrue(true);
	}
	
	// Test inheritance method redefinition
	@Test
	void methRedefTest() {
		
		mrdTree.accept(stb);
		mrdTree.accept(stc);

		
		assertTrue(true);
	}
	
	// Test error on bad inherited method redefinition
	@Test
	void methRedefBadTest() {
		System.out.print("methRedefBadTest: ");
		mrdbTree.accept(stb);
		
		Executable e = () -> {
			mrdbTree.accept(stc);
        };
		
		System.out.println(assertThrows(Exception.class, e).getMessage());
	}
}
