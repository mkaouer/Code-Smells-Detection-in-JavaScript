package test.net.sourceforge.pmd.ast;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.ast.ASTSwitchLabel;

import org.junit.Test;

import test.net.sourceforge.pmd.testframework.ParserTst;

import java.util.Set;

public class ASTSwitchLabelTest extends ParserTst {

    @Test
    public void testDefaultOff() throws Throwable {
        Set ops = getNodes(ASTSwitchLabel.class, TEST1);
        assertFalse(((ASTSwitchLabel) (ops.iterator().next())).isDefault());
    }

    @Test
    public void testDefaultSet() throws Throwable {
        Set ops = getNodes(ASTSwitchLabel.class, TEST2);
        assertTrue(((ASTSwitchLabel) (ops.iterator().next())).isDefault());
    }


    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " void bar() {" + PMD.EOL +
            "  switch (x) {" + PMD.EOL +
            "   case 1: y = 2;" + PMD.EOL +
            "  }" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            " void bar() {" + PMD.EOL +
            "  switch (x) {" + PMD.EOL +
            "   default: y = 2;" + PMD.EOL +
            "  }" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(ASTSwitchLabelTest.class);
    }
}
