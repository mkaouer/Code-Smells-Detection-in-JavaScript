package test.net.sourceforge.pmd.ast;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import net.sourceforge.pmd.ast.ASTAllocationExpression;
import net.sourceforge.pmd.ast.ASTAssertStatement;
import net.sourceforge.pmd.ast.ASTBlockStatement;

import org.junit.Test;

import test.net.sourceforge.pmd.testframework.ParserTst;

public class ASTBlockStatementTest extends ParserTst {

    @Test
    public void testIsAllocation() {
        ASTBlockStatement bs = new ASTBlockStatement(0);
        bs.jjtAddChild(new ASTAllocationExpression(1), 0);
        assertTrue(bs.isAllocation());
    }

    @Test
    public void testIsAllocation2() {
        ASTBlockStatement bs = new ASTBlockStatement(0);
        bs.jjtAddChild(new ASTAssertStatement(1), 0);
        assertFalse(bs.isAllocation());
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(ASTBlockStatementTest.class);
    }
}
