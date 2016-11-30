/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTWhileStatement;
import net.sourceforge.pmd.ast.SimpleNode;

import java.util.ArrayList;
import java.util.List;

public class PositionalIteratorRule extends AbstractRule {

    public Object visit(ASTWhileStatement node, Object data) {
        if (hasNameAsChild((SimpleNode) node.jjtGetChild(0))) {
            String exprName = getName((SimpleNode) node.jjtGetChild(0));
            if (exprName.indexOf(".hasNext") != -1 && node.jjtGetNumChildren() > 1) {

                SimpleNode loopBody = (SimpleNode) node.jjtGetChild(1);
                List<String> names = new ArrayList<String>();
                collectNames(getVariableName(exprName), names, loopBody);
                int nextCount = 0;
                for (String name: names) {
                    if (name.indexOf(".next") != -1) {
                        nextCount++;
                    }
                }

                if (nextCount > 1) {
                    addViolation(data, node);
                }

            }
        }
        return null;
    }

    private String getVariableName(String exprName) {
        return exprName.substring(0, exprName.indexOf('.'));
    }

    private void collectNames(String target, List<String> names, SimpleNode node) {
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            SimpleNode child = (SimpleNode) node.jjtGetChild(i);
            if (child.jjtGetNumChildren() > 0) {
                collectNames(target, names, child);
            } else {
                if (child instanceof ASTName && isQualifiedName(child) && target.equals(getVariableName(child.getImage()))) {
                    names.add(child.getImage());
                }
            }
        }
    }

    private boolean hasNameAsChild(SimpleNode node) {
        while (node.jjtGetNumChildren() > 0) {
            if (node.jjtGetChild(0) instanceof ASTName) {
                return true;
            }
            return hasNameAsChild((SimpleNode) node.jjtGetChild(0));
        }
        return false;
    }

    private String getName(SimpleNode node) {
        while (node.jjtGetNumChildren() > 0) {
            if (node.jjtGetChild(0) instanceof ASTName) {
                return ((ASTName) node.jjtGetChild(0)).getImage();
            }
            return getName((SimpleNode) node.jjtGetChild(0));
        }
        throw new IllegalArgumentException("Check with hasNameAsChild() first!");
    }
}
