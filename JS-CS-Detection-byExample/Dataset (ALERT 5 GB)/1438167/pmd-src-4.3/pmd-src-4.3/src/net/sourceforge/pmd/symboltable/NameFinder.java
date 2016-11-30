/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.ASTArguments;
import net.sourceforge.pmd.ast.ASTMemberSelector;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.ast.SimpleNode;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class NameFinder {

    private LinkedList<NameOccurrence> names = new LinkedList<NameOccurrence>();

    public NameFinder(ASTPrimaryExpression node) {
        ASTPrimaryPrefix prefix = (ASTPrimaryPrefix) node.jjtGetChild(0);
        if (prefix.usesSuperModifier()) {
            add(new NameOccurrence(prefix, "super"));
        } else if (prefix.usesThisModifier()) {
            add(new NameOccurrence(prefix, "this"));
        }
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            checkForNameChild((SimpleNode) node.jjtGetChild(i));
        }
    }

    public List<NameOccurrence> getNames() {
        return names;
    }

    private void checkForNameChild(SimpleNode node) {
        if (node.getImage() != null) {
            add(new NameOccurrence(node, node.getImage()));
        }
        if (node.jjtGetNumChildren() > 0 && node.jjtGetChild(0) instanceof ASTName) {
            ASTName grandchild = (ASTName) node.jjtGetChild(0);
            for (StringTokenizer st = new StringTokenizer(grandchild.getImage(), "."); st.hasMoreTokens();) {
                add(new NameOccurrence(grandchild, st.nextToken()));
            }
        }
        if (node instanceof ASTPrimarySuffix) {
            ASTPrimarySuffix suffix = (ASTPrimarySuffix) node;                                
            if (suffix.isArguments()) {                              
                NameOccurrence occurrence = names.getLast();
                occurrence.setIsMethodOrConstructorInvocation();
                ASTArguments args = (ASTArguments) ((ASTPrimarySuffix) node).jjtGetChild(0);
                occurrence.setArgumentCount(args.getArgumentCount());
            } else if (suffix.jjtGetNumChildren() == 1 && suffix.jjtGetChild(0) instanceof ASTMemberSelector) {
                add(new NameOccurrence((SimpleNode)suffix.jjtGetChild(0), ((SimpleNode)suffix.jjtGetChild(0)).getImage()));    
            }
        }
    }

    private void add(NameOccurrence name) {
        names.add(name);
        if (names.size() > 1) {
            NameOccurrence qualifiedName = names.get(names.size() - 2);
            qualifiedName.setNameWhichThisQualifies(name);
        }
    }


    public String toString() {
        StringBuffer result = new StringBuffer();
        for (NameOccurrence occ: names) {
            result.append(occ.getImage());
        }
        return result.toString();
    }
}
