/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.junit;

import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;

import java.util.Iterator;
import java.util.List;

public class TestClassWithoutTestCases extends AbstractJUnitRule {
    
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isAbstract() || node.isInterface() || node.isNested()) {
            return data;
        }

        String className = node.getImage();
        if (className.endsWith("Test")) {
            List<ASTMethodDeclarator> m = node.findChildrenOfType(ASTMethodDeclarator.class);
            boolean testsFound = false;
            if (m != null) {
                for (Iterator<ASTMethodDeclarator> it = m.iterator(); it.hasNext() && !testsFound;) {
                    ASTMethodDeclarator md = it.next();
                    if (!isInInnerClassOrInterface(md)
                            && md.getImage().startsWith("test")) {
                        testsFound = true;
                    }
                }
            }

            if (!testsFound) {
                addViolation(data, node);
            }

        }
        return data;
    }

    private boolean isInInnerClassOrInterface(ASTMethodDeclarator md) {
        ASTClassOrInterfaceDeclaration p = md.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class);
        return p != null && p.isNested();
    }
}
