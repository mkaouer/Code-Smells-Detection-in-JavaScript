package test.net.sourceforge.pmd.dfa;

import org.junit.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.dfa.pathfinder.CurrentPath;
import net.sourceforge.pmd.dfa.pathfinder.DAAPathFinder;
import net.sourceforge.pmd.dfa.pathfinder.Executable;
import test.net.sourceforge.pmd.testframework.ParserTst;

public class DAAPathFinderTest extends ParserTst implements Executable {

    @Test
    public void testTwoUpdateDefs() throws Throwable {
        ASTMethodDeclarator meth = getOrderedNodes(ASTMethodDeclarator.class, TWO_UPDATE_DEFS).get(0);
        DAAPathFinder a = new DAAPathFinder(meth.getDataFlowNode().getFlow().get(0), this);
//        a.run();
    }

    public void execute(CurrentPath path) {
    }


    private static final String TWO_UPDATE_DEFS =
            "class Foo {" + PMD.EOL +
            " void bar(int x) {" + PMD.EOL +
            "  for (int i=0; i<10; i++, j--) {}" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(DAAPathFinderTest.class);
    }
}
