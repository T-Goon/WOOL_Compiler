package wool.type;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;

import org.antlr.v4.gui.TreeViewer;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import wool.symbol.AbstractBinding;
import wool.symbol.SymbolTableBuilder;
import wool.symbol.SymbolTableChecker;
import wool.symbol.TableManager;
import wool.utility.WoolFactory;
import wool.utility.WoolRunnerImpl;

class TypeCheckerTest {

	private SymbolTableBuilder stb;
	private SymbolTableChecker stc;
	private TypeChecker tc;

	private static String posFilesLoc = "test-files/typeCheckerTestFiles/posTestFiles";
	private static String negFilesLoc = "test-files/typeCheckerTestFiles/negTestFiles";
	
	@BeforeEach
	void setUp() {
		TableManager.reset();
		stc = new SymbolTableChecker();
		stb = new SymbolTableBuilder();
	}
	
	// Works if there is no error
	@ParameterizedTest
	@CsvSource({"/math.wl" // Types of math exprs done correctly
		})
	void posTests(String file) throws IOException {
		ParseTree tree;
		
		WoolRunnerImpl imp = WoolFactory.makeParserRunner(
				CharStreams.fromFileName(posFilesLoc+file));
		tree = imp.parse();
		
		tree.accept(stb);
		ParseTreeProperty<AbstractBinding> bindings = stc.visit(tree);
		tc = new TypeChecker(bindings);
		tc.visit(tree);
		
		assertTrue(true);
	}
	
	@ParameterizedTest
	@CsvSource({
		"/addStrings.wl", // Error on adding int and bool
		"/addBools.wl" // Error on adding int and string
	})
	void negTests(String file) throws IOException {
		ParseTree tree;
		WoolRunnerImpl imp = WoolFactory.makeParserRunner(
				CharStreams.fromFileName(negFilesLoc+file));
		tree = imp.parse();
		
		tree.accept(stb);
		ParseTreeProperty<AbstractBinding> bindings = stc.visit(tree);
		tc = new TypeChecker(bindings);
		
		
		Executable e = () -> {
			tc.visit(tree);
        };
		
        System.out.println(assertThrows(Exception.class, e).getMessage());
	}
	
//	@Test
	public void showTree() throws IOException
    {
		
		ParseTree tree;
		
		WoolRunnerImpl imp = WoolFactory.makeParserRunner(
				CharStreams.fromFileName(posFilesLoc+"/math.wl"));
		tree = imp.parse();
		
        List<String> ruleNames = Arrays.asList(imp.getParser().getRuleNames());
        TreeViewer tv = new TreeViewer(ruleNames, tree);
        JFrame frame = new JFrame("Parse Tree");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(tv);
        
        //Display the window.
        frame.pack();
        frame.setVisible(true);
        BufferedReader br = 
                new BufferedReader(new InputStreamReader(System.in));
        try {
            br.readLine();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        assertTrue(true);
    }

}
