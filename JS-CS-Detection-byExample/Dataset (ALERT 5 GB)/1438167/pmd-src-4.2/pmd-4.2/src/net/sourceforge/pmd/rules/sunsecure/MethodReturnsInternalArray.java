/*
 * Created on Jan 17, 2005 
 *
 * $Id: MethodReturnsInternalArray.java 5017 2007-01-31 00:53:53Z xlv $
 */
package net.sourceforge.pmd.rules.sunsecure;

import net.sourceforge.pmd.ast.ASTAllocationExpression;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.ast.ASTReturnStatement;
import net.sourceforge.pmd.ast.ASTTypeDeclaration;

import java.util.List;

/**
 * Implementation note: this rule currently ignores return types of y.x.z,
 * currently it handles only local type fields.
 *
 * @author mgriffa
 */
public class MethodReturnsInternalArray extends AbstractSunSecureRule {

    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface()) {
            return data;
        }
        return super.visit(node, data);
    }

    public Object visit(ASTMethodDeclaration method, Object data) {
        if (!method.getResultType().returnsArray()) {
            return data;
        }
        List<ASTReturnStatement> returns = method.findChildrenOfType(ASTReturnStatement.class);
        ASTTypeDeclaration td = method.getFirstParentOfType(ASTTypeDeclaration.class);
        for (ASTReturnStatement ret: returns) {
            final String vn = getReturnedVariableName(ret);
            if (!isField(vn, td)) {
                continue;
            }
            if (ret.findChildrenOfType(ASTPrimarySuffix.class).size() > 2) {
                continue;
            }
            if (!ret.findChildrenOfType(ASTAllocationExpression.class).isEmpty()) {
                continue;
            }
            if (!isLocalVariable(vn, method)) {
                addViolation(data, ret, vn);
            } else {
                // This is to handle field hiding
                final ASTPrimaryPrefix pp = ret.getFirstChildOfType(ASTPrimaryPrefix.class);
                if (pp != null && pp.usesThisModifier()) {
                    final ASTPrimarySuffix ps = ret.getFirstChildOfType(ASTPrimarySuffix.class);
                    if (ps.hasImageEqualTo(vn)) {
                        addViolation(data, ret, vn);
                    }
                }
            }
        }
        return data;
    }


}
