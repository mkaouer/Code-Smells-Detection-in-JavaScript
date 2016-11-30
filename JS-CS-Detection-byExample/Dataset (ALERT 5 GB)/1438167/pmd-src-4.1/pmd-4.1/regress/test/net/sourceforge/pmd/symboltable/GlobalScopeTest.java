package test.net.sourceforge.pmd.symboltable;

import static org.junit.Assert.assertEquals;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.symboltable.ClassNameDeclaration;
import net.sourceforge.pmd.symboltable.Scope;

import org.junit.Test;

import java.util.Map;

public class GlobalScopeTest extends STBBaseTst {

    @Test
    public void testClassDeclAppears() {
        parseCode(TEST1);
        ASTCompilationUnit decl = acu.findChildrenOfType(ASTCompilationUnit.class).get(0);
        Scope scope = decl.getScope();
        Map m = scope.getClassDeclarations();
        ClassNameDeclaration classNameDeclaration = (ClassNameDeclaration) m.keySet().iterator().next();
        assertEquals(classNameDeclaration.getImage(), "Foo");
    }

    @Test
    public void testEnums() {
        parseCode15(TEST2);
    }

    private static final String TEST1 =
            "public class Foo {}" + PMD.EOL;

    private static final String TEST2 =
            "public enum Bar {" + PMD.EOL +
            "  FOO1 {          " + PMD.EOL +
            "    private static final String FIELD_NAME = \"\";" + PMD.EOL +
            "  }," + PMD.EOL +
            "  FOO2 {          " + PMD.EOL +
            "    private static final String FIELD_NAME = \"\";" + PMD.EOL +
            "  }" + PMD.EOL +
            "}" + PMD.EOL;

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(GlobalScopeTest.class);
    }
}
