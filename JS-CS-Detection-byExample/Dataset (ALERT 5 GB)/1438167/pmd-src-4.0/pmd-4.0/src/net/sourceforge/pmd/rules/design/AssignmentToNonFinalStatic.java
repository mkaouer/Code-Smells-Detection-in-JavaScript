/*
 * AssignmentToNonFinalStaticRule.java
 *
 * Created on October 24, 2004, 8:56 AM
 */

package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.symboltable.NameOccurrence;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;

import java.util.List;
import java.util.Map;


/**
 * @author Eric Olander
 */
public class AssignmentToNonFinalStatic extends AbstractRule {

    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        Map<VariableNameDeclaration, List<NameOccurrence>> vars = node.getScope().getVariableDeclarations();
        for (Map.Entry<VariableNameDeclaration, List<NameOccurrence>> entry: vars.entrySet()) {
            VariableNameDeclaration decl = entry.getKey();
            if (!decl.getAccessNodeParent().isStatic() || decl.getAccessNodeParent().isFinal()) {
                continue;
            }

            if (initializedInConstructor(entry.getValue())) {
                addViolation(data, decl.getNode(), decl.getImage());
            }
        }
        return super.visit(node, data);
    }

    private boolean initializedInConstructor(List<NameOccurrence> usages) {
        boolean initInConstructor = false;

        for (NameOccurrence occ: usages) {
            if (occ.isOnLeftHandSide()) { // specifically omitting prefix and postfix operators as there are legitimate usages of these with static fields, e.g. typesafe enum pattern.
                SimpleNode node = occ.getLocation();
                SimpleNode constructor = node.getFirstParentOfType(ASTConstructorDeclaration.class);
                if (constructor != null) {
                    initInConstructor = true;
                }
            }
        }

        return initInConstructor;
    }

}
