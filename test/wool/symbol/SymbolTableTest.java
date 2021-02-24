package wool.symbol;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import wool.symbol.BindingFactory.ClassBinding;
import wool.symbol.BindingFactory.ObjectBinding;
import wool.utility.WoolFactory;
import wool.utility.WoolRunnerImpl;

class SymbolTableTest {
	
	private TableManager tm = TableManager.getInstance();
	private SymbolTableBuilder stb = new SymbolTableBuilder();
	
	@BeforeEach
	void setUp() {
		TableManager.reset();
	}

	// Test adding classes to the symbol table
	@ParameterizedTest
	@ValueSource(strings= {
			"Simple",
			"Simple2",
			"Simple3"
	})
	void classTest(String cls) throws IOException{
		WoolRunnerImpl imp = WoolFactory.makeParserRunner(
				CharStreams.fromFileName("test-files/symbolTableTestFiles/class.wl"));
		ParseTree tree = imp.parse();
		
		tree.accept(stb);
		
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
		WoolRunnerImpl imp = WoolFactory.makeParserRunner(
				CharStreams.fromFileName("test-files/symbolTableTestFiles/classVar.wl"));
		ParseTree tree = imp.parse();
		
		tree.accept(stb);
		
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
		WoolRunnerImpl imp = WoolFactory.makeParserRunner(
				CharStreams.fromFileName("test-files/symbolTableTestFiles/classMeth.wl"));
		ParseTree tree = imp.parse();
		
		tree.accept(stb);
		
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
		WoolRunnerImpl imp = WoolFactory.makeParserRunner(
				CharStreams.fromFileName("test-files/symbolTableTestFiles/methVars.wl"));
		ParseTree tree = imp.parse();
		
		tree.accept(stb);
		
		// Symbol table for scope of method
		SymbolTable t = tm.getTables().get(table);
		
		assertNotNull(t.lookup(var));
	}
	
//	@Test
	void test() throws IOException{
		WoolRunnerImpl imp = WoolFactory.makeParserRunner(
				CharStreams.fromFileName("test-files/symbolTableTestFiles/classVar.wl"));
		ParseTree tree = imp.parse();
		
		tree.accept(stb);
		
		ClassBinding d = tm.lookupClass("Simple");
		
		ClassDescriptor cd = d.getClassDescriptor();
		
		ObjectBinding var = cd.getVariable("var1");
		
		System.out.println(var.toString());
		
		assertTrue(true);
	}

}
