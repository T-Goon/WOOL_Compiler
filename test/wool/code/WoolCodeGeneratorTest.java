package wool.code;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import wool.symbol.AbstractBinding;
import wool.symbol.SymbolTableBuilder;
import wool.symbol.SymbolTableChecker;
import wool.symbol.TableManager;
import wool.type.TypeChecker;
import wool.utility.WoolFactory;
import wool.utility.WoolRunnerImpl;

class WoolCodeGeneratorTest {
	
	private SymbolTableBuilder stb;
	private SymbolTableChecker stc;
	private TypeChecker tc;
	
	@BeforeEach
	void setUp() {
		TableManager.reset();
		stc = new SymbolTableChecker();
		stb = new SymbolTableBuilder();
	}

	@Test
	void test() throws IOException {
		
		ParseTree tree;
		
		WoolRunnerImpl imp = WoolFactory.makeParserRunner(
				CharStreams.fromFileName("test-files/codeGenTestFiles/emptyClass.wl"));
		tree = imp.parse();
		
		tree.accept(stb);
		ParseTreeProperty<AbstractBinding> bindings = stc.visit(tree);
		tc = new TypeChecker(bindings);
		tc.visit(tree);
		
		CodeGenerator cg = new CodeGenerator(bindings);
		byte[] b = cg.visit(tree);
		
		
		FileOutputStream fos = new FileOutputStream("./woolcode/wool/Empty.class");
        byte[] code = b;
        fos.write(code);
        fos.close();
        
        assertTrue(true);
	}

}
