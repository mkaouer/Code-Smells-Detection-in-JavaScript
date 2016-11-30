/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.ast;

import static org.junit.Assert.assertEquals;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;

import org.junit.Test;

import test.net.sourceforge.pmd.testframework.ParserTst;

import java.util.Iterator;
import java.util.Set;

public class ClassDeclTest extends ParserTst {

    @Test
    public void testPublic() throws Throwable {
        String access[] = {"public"};
        ASTClassOrInterfaceDeclaration acd = getClassDecl(access);
        verifyFlags(acd, true, false, false, false);
    }

    @Test
    public void testAbstract() throws Throwable {
        String access[] = {"abstract"};
        ASTClassOrInterfaceDeclaration acd = getClassDecl(access);
        verifyFlags(acd, false, true, false, false);
    }

    @Test
    public void testFinal() throws Throwable {
        String access[] = {"final"};
        ASTClassOrInterfaceDeclaration acd = getClassDecl(access);
        verifyFlags(acd, false, false, true, false);
    }

    @Test
    public void testStrict() throws Throwable {
        String access[] = {"strictfp"};
        ASTClassOrInterfaceDeclaration acd = getClassDecl(access);
        verifyFlags(acd, false, false, false, true);
    }

    @Test
    public void testPublicFinal() throws Throwable {
        String access[] = {"public", "final"};
        ASTClassOrInterfaceDeclaration acd = getClassDecl(access);
        verifyFlags(acd, true, false, true, false);
    }

    public void verifyFlags(ASTClassOrInterfaceDeclaration acd, boolean bPublic, boolean bAbstract, boolean bFinal, boolean bStrict) {
        assertEquals("Public: ", bPublic, acd.isPublic());
        assertEquals("Abstract: ", bAbstract, acd.isAbstract());
        assertEquals("Final: ", bFinal, acd.isFinal());
        assertEquals("Strict: ", bStrict, acd.isStrictfp());
    }

    public ASTClassOrInterfaceDeclaration getClassDecl(String access[]) throws Throwable {
        String javaCode = "";

        for (int i = 0; i < access.length; i++) {
            javaCode += access[i] + " ";
        }

        javaCode += " class Test { } ";

        Set classes = getNodes(ASTClassOrInterfaceDeclaration.class, javaCode);

        assertEquals("Wrong number of classes", 1, classes.size());
        Iterator i = classes.iterator();
        return (ASTClassOrInterfaceDeclaration) i.next();
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(ClassDeclTest.class);
    }
}
