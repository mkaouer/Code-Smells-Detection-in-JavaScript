/*
 * Created on Jan 17, 2005 
 *
 * $Id: ArrayIsStoredDirectly.java,v 1.17 2006/10/25 19:40:45 xlv Exp $
 */
package net.sourceforge.pmd.rules.sunsecure;

import net.sourceforge.pmd.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.ast.ASTBlockStatement;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.ast.ASTEqualityExpression;
import net.sourceforge.pmd.ast.ASTExpression;
import net.sourceforge.pmd.ast.ASTFormalParameter;
import net.sourceforge.pmd.ast.ASTFormalParameters;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.ast.ASTStatementExpression;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.SimpleNode;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * @author mgriffa
 */
public class ArrayIsStoredDirectly extends AbstractSunSecureRule {

    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface()) {
            return data;
        }
        return super.visit(node, data);
    }

    public Object visit(ASTConstructorDeclaration node, Object data) {
        ASTFormalParameter[] arrs = getArrays(node.getParameters());
        if (arrs != null) {
            //TODO check if one of these arrays is stored in a non local variable
            List bs = node.findChildrenOfType(ASTBlockStatement.class);
            checkAll(data, arrs, bs);
        }
        return data;
    }

    public Object visit(ASTMethodDeclaration node, Object data) {
        final ASTFormalParameters params = (ASTFormalParameters) node.getFirstChildOfType(ASTFormalParameters.class);
        ASTFormalParameter[] arrs = getArrays(params);
        if (arrs != null) {
            checkAll(data, arrs, node.findChildrenOfType(ASTBlockStatement.class));
        }
        return data;
    }

    private void checkAll(Object context, ASTFormalParameter[] arrs, List bs) {
        for (int i = 0; i < arrs.length; i++) {
            checkForDirectAssignment(context, arrs[i], bs);
        }
    }

    /**
     * Checks if the variable designed in parameter is written to a field (not local variable) in the statements.
     */
    private boolean checkForDirectAssignment(Object ctx, final ASTFormalParameter parameter, final List bs) {
        final ASTVariableDeclaratorId vid = (ASTVariableDeclaratorId) parameter.getFirstChildOfType(ASTVariableDeclaratorId.class);
        final String varName = vid.getImage();
        for (Iterator it = bs.iterator(); it.hasNext();) {
            final ASTBlockStatement b = (ASTBlockStatement) it.next();
            if (b.containsChildOfType(ASTAssignmentOperator.class)) {
                final ASTStatementExpression se = (ASTStatementExpression) b.getFirstChildOfType(ASTStatementExpression.class);
                if (se == null || !(se.jjtGetChild(0) instanceof ASTPrimaryExpression)) {
                    continue;
                }
                ASTPrimaryExpression pe = (ASTPrimaryExpression) se.jjtGetChild(0);
                String assignedVar = getFirstNameImage(pe);
                if (assignedVar == null) {
                    ASTPrimarySuffix suffix = (ASTPrimarySuffix) se.getFirstChildOfType(ASTPrimarySuffix.class);
                    if (suffix == null) {
                        continue;
                    }
                    assignedVar = suffix.getImage();
                }

                SimpleNode n = (ASTMethodDeclaration) pe.getFirstParentOfType(ASTMethodDeclaration.class);
                if (n == null) {
					n = (ASTConstructorDeclaration) pe.getFirstParentOfType(ASTConstructorDeclaration.class);
					if (n == null) {
						continue;
					}
				}
                if (!isLocalVariable(assignedVar, n)) {
                    // TODO could this be more clumsy?  We really
                    // need to build out the PMD internal framework more
                    // to support simply queries like "isAssignedTo()" or something
                    if (se.jjtGetNumChildren() < 3) {
                        continue;
                    }
                    ASTExpression e = (ASTExpression) se.jjtGetChild(2);
                    if (e.findChildrenOfType(ASTEqualityExpression.class).size() > 0) {
                        continue;
                    }
                    String val = getFirstNameImage(e);
                    if (val == null) {
                        ASTPrimarySuffix foo = (ASTPrimarySuffix) se.getFirstChildOfType(ASTPrimarySuffix.class);
                        if (foo == null) {
                            continue;
                        }
                        val = foo.getImage();
                    }
                    if (val == null) {
                        continue;
                    }
                    ASTPrimarySuffix foo = (ASTPrimarySuffix) se.getFirstChildOfType(ASTPrimarySuffix.class);
                    if (foo != null && foo.isArrayDereference()) {
                        continue;
                    }

                    if (val.equals(varName)) {
                        SimpleNode md = (ASTMethodDeclaration) parameter.getFirstParentOfType(ASTMethodDeclaration.class);
                        if (md == null) {
                        	md = (ASTConstructorDeclaration) pe.getFirstParentOfType(ASTConstructorDeclaration.class);
        				}
                        if (!isLocalVariable(varName, md)) {
                            addViolation(ctx, parameter, varName);
                        }
                    }
                }
            }
        }
        return false;
    }

    private final ASTFormalParameter[] getArrays(ASTFormalParameters params) {
        final List l = params.findChildrenOfType(ASTFormalParameter.class);
        if (l != null && !l.isEmpty()) {
            Vector v = new Vector();
            for (Iterator it = l.iterator(); it.hasNext();) {
                ASTFormalParameter fp = (ASTFormalParameter) it.next();
                if (fp.isArray())
                    v.add(fp);
            }
            return (ASTFormalParameter[]) v.toArray(new ASTFormalParameter[v.size()]);
        }
        return null;
    }

}
