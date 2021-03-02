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

/**
 * Tests the type checking phase of the semantic analysis.
 *
 */
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
	@CsvSource({
		"/math.wl", // Types of arithmetic exprs done correctly
		"/compare.wl", // Types for comparison exprs done correctly
		"/varDefType.wl", // Types for vardef and new obj exprs done correctly
		"/assignExpr.wl", // Types for assign expr done correctly
		"/eq.wl", // Equality operators check types correctly
		"/if.wl", // If expression checks types correctly
		"/select.wl", // select expression checks types correctly
		"/while.wl", // while expression checks types correctly
		"/block.wl", // block expression evaluates to the correct type
		"/objMeth.wl", // use method calls correctly
		"/thisRef.wl", // reference this correctly
		"/methType.wl", // meth ret and expr type correct
		"/selfType.wl", // self_type is computed correctly
		"/formRef.wl", // Formal is reference correctly
		"/whileUse.wl",
		"/selfType2.wl"
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
		"/addBools.wl", // Error on adding int and string
		"/compareChain.wl", // Error on chaining comparison ops
		"/compPres.wl", // Error on chaining comparison ops
		"/compareNotInt.wl", // Error on comparing non-ints
		"/newObjBad.wl", // Expr type does not conform to ID type
		"/badAssign.wl", // Error on assign string to int
		"/assignExprBad.wl", // Error on assign expr conform types
		"/eqBad.wl", // Error on bad use of equality operators
		"/ifBadCond.wl", // Error if statement cond not a bool
		"/ifIntCheck.wl", // Error primitive only in one branch
		"/selBadCond.wl", // Error on select cond not bool
		"/selPrimCheck.wl", // Error on alts not matching primitive type
		"/whileBadCond.wl", // Error on while cond not bool
		"/methNotExist.wl", // Error on method name that doesn't exist
		"/methParamNum.wl", // Error on wrong number of meth params
		"/methParamType.wl", // Error on wrong type of param
		"/locMethRefBad.wl", // Test using outside method with local usage
		"/locMethRefBad2.wl", // Test local method call with bad number of args
		"/objMethExprBad.wl", // Test ref method name that does not exist
		"/objMethExprBad2.wl", // Test using method with wrong number of args
		"/methTypeBad.wl", // meth expr and ret type don't match
		"/badObjMeth.wl", // error on calling method from primitive
		"/null.wl"
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

}
