/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;

import org.junit.Test;

import test.net.sourceforge.pmd.testframework.ParserTst;

import java.util.Iterator;
import java.util.Set;

public class FieldDeclTest extends ParserTst {

    public String makeAccessJavaCode(String access[]) {
        String result = "public class Test { ";
        for (int i = 0; i < access.length; i++) {
            result += access[i] + " ";
        }
        return result + " int j;  }";
    }

    public ASTFieldDeclaration getFieldDecl(String access[]) throws Throwable {
        Set fields = getNodes(ASTFieldDeclaration.class, makeAccessJavaCode(access));

        assertEquals("Wrong number of fields", 1, fields.size());
        Iterator i = fields.iterator();
        return (ASTFieldDeclaration) i.next();
    }

    @Test
    public void testPublic() throws Throwable {
        String access[] = {"public"};
        ASTFieldDeclaration afd = getFieldDecl(access);
        assertTrue("Expecting field to be public.", afd.isPublic());
    }

    @Test
    public void testProtected() throws Throwable {
        String access[] = {"protected"};
        ASTFieldDeclaration afd = getFieldDecl(access);
        assertTrue("Expecting field to be protected.", afd.isProtected());
    }

    @Test
    public void testPrivate() throws Throwable {
        String access[] = {"private"};
        ASTFieldDeclaration afd = getFieldDecl(access);
        assertTrue("Expecting field to be private.", afd.isPrivate());
    }

    @Test
    public void testStatic() throws Throwable {
        String access[] = {"private", "static"};
        ASTFieldDeclaration afd = getFieldDecl(access);
        assertTrue("Expecting field to be static.", afd.isStatic());
        assertTrue("Expecting field to be private.", afd.isPrivate());
    }

    @Test
    public void testFinal() throws Throwable {
        String access[] = {"public", "final"};
        ASTFieldDeclaration afd = getFieldDecl(access);
        assertTrue("Expecting field to be final.", afd.isFinal());
        assertTrue("Expecting field to be public.", afd.isPublic());
    }

    @Test
    public void testTransient() throws Throwable {
        String access[] = {"private", "transient"};
        ASTFieldDeclaration afd = getFieldDecl(access);
        assertTrue("Expecting field to be private.", afd.isPrivate());
        assertTrue("Expecting field to be transient.", afd.isTransient());
    }

    @Test
    public void testVolatile() throws Throwable {
        String access[] = {"private", "volatile"};
        ASTFieldDeclaration afd = getFieldDecl(access);
        assertTrue("Expecting field to be volatile.", afd.isVolatile());
        assertTrue("Expecting field to be private.", afd.isPrivate());
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(FieldDeclTest.class);
    }
}
