/*
 * Created on Jan 19, 2005 
 *
 * $Id: ASTThrowStatementTest.java 5043 2007-02-09 01:38:14Z allancaplan $
 */
package test.net.sourceforge.pmd.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.ast.ASTThrowStatement;

import org.junit.Test;

import test.net.sourceforge.pmd.testframework.ParserTst;

/**
 * @author mgriffa
 */
public class ASTThrowStatementTest extends ParserTst {

    @Test
    public final void testGetFirstASTNameImageNull() throws Throwable {
        ASTThrowStatement t = getNodes(ASTThrowStatement.class, NULL_NAME).iterator().next();
        assertNull(t.getFirstClassOrInterfaceTypeImage());
    }

    @Test
    public final void testGetFirstASTNameImageNew() throws Throwable {
        ASTThrowStatement t = getNodes(ASTThrowStatement.class, OK_NAME).iterator().next();
        assertEquals("FooException", t.getFirstClassOrInterfaceTypeImage());
    }

    private static final String NULL_NAME =
            "public class Test {" + PMD.EOL +
            "  void bar() {" + PMD.EOL +
            "   throw e;" + PMD.EOL +
            "  }" + PMD.EOL +
            "}";

    private static final String OK_NAME =
            "public class Test {" + PMD.EOL +
            "  void bar() {" + PMD.EOL +
            "   throw new FooException();" + PMD.EOL +
            "  }" + PMD.EOL +
            "}";

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(ASTThrowStatementTest.class);
    }
}
