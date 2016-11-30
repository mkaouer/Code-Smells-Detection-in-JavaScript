/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.ast.ASTBlock;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTReferenceType;
import net.sourceforge.pmd.ast.ASTTryStatement;
import net.sourceforge.pmd.ast.ASTType;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.properties.StringProperty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Makes sure you close your database connections. It does this by
 * looking for code patterned like this:
 * <pre>
 *  Connection c = X;
 *  try {
 *   // do stuff, and maybe catch something
 *  } finally {
 *   c.close();
 *  }
 *
 *  @author original author unknown
 *  @author Contribution from Pierre Mathien
 * </pre>
 */
public class CloseResource extends AbstractRule {

    private Set<String> types = new HashSet<String>();

    private Set<String> closeTargets = new HashSet<String>();
    private static final PropertyDescriptor closeTargetsDescriptor = new StringProperty("closeTargets",
            "Methods which may close this resource", "", 1.0f);

    private static final PropertyDescriptor typesDescriptor = new StringProperty("types",
            "Types that are affected by this rule", "", 2.0f);

    private static final Map<String, PropertyDescriptor> propertyDescriptorsByName = asFixedMap(new PropertyDescriptor[] { typesDescriptor, closeTargetsDescriptor });

    protected Map<String, PropertyDescriptor> propertiesByName() {
        return propertyDescriptorsByName;
    }

    public Object visit(ASTCompilationUnit node, Object data) {
        if (closeTargets.isEmpty() && getStringProperty(closeTargetsDescriptor) != null) {
            for (StringTokenizer st = new StringTokenizer(getStringProperty(closeTargetsDescriptor), ","); st.hasMoreTokens();) {
                closeTargets.add(st.nextToken());
            }
        }
        if (types.isEmpty() && getStringProperty(typesDescriptor) != null) {
            for (StringTokenizer st = new StringTokenizer(getStringProperty(typesDescriptor), ","); st.hasMoreTokens();) {
                types.add(st.nextToken());
            }
        }
        return super.visit(node, data);
    }

    public Object visit(ASTMethodDeclaration node, Object data) {
        List<ASTLocalVariableDeclaration> vars = node.findChildrenOfType(ASTLocalVariableDeclaration.class);
        List<ASTVariableDeclaratorId> ids = new ArrayList<ASTVariableDeclaratorId>();

        // find all variable references to Connection objects
        for (ASTLocalVariableDeclaration var: vars) {
            ASTType type = var.getTypeNode();

            if (type.jjtGetChild(0) instanceof ASTReferenceType) {
                ASTReferenceType ref = (ASTReferenceType) type.jjtGetChild(0);
                if (ref.jjtGetChild(0) instanceof ASTClassOrInterfaceType) {
                    ASTClassOrInterfaceType clazz = (ASTClassOrInterfaceType) ref.jjtGetChild(0);
                    if (types.contains(clazz.getImage())) {
                        ASTVariableDeclaratorId id = (ASTVariableDeclaratorId) var.jjtGetChild(1).jjtGetChild(0);
                        ids.add(id);
                    }
                }
            }
        }

        // if there are connections, ensure each is closed.
        for (ASTVariableDeclaratorId x : ids) {
            ensureClosed((ASTLocalVariableDeclaration) x.jjtGetParent().jjtGetParent(), x, data);
        }
        return data;
    }

    private void ensureClosed(ASTLocalVariableDeclaration var,
                              ASTVariableDeclaratorId id, Object data) {
        // What are the chances of a Connection being instantiated in a
        // for-loop init block? Anyway, I'm lazy!
        String target = id.getImage() + ".close";
        Node n = var;

        while (!(n instanceof ASTBlock)) {
            n = n.jjtGetParent();
        }

        ASTBlock top = (ASTBlock) n;

        List<ASTTryStatement> tryblocks = new ArrayList<ASTTryStatement>();
        top.findChildrenOfType(ASTTryStatement.class, tryblocks, true);

        boolean closed = false;

        // look for try blocks below the line the variable was
        // introduced and make sure there is a .close call in a finally
        // block.
        for (ASTTryStatement t : tryblocks) {
            if ((t.getBeginLine() > id.getBeginLine()) && (t.hasFinally())) {
                ASTBlock f = (ASTBlock) t.getFinally().jjtGetChild(0);
                List<ASTName> names = new ArrayList<ASTName>();
                f.findChildrenOfType(ASTName.class, names, true);
                for (ASTName oName : names) {
                    String name = oName.getImage();
                    if (name.equals(target) || closeTargets.contains(name)) {
                        closed = true;
                    }
                }
            }
        }

        // if all is not well, complain
        if (!closed) {
            ASTType type = (ASTType) var.jjtGetChild(0);
            ASTReferenceType ref = (ASTReferenceType) type.jjtGetChild(0);
            ASTClassOrInterfaceType clazz = (ASTClassOrInterfaceType) ref.jjtGetChild(0);
            addViolation(data, id, clazz.getImage());
        }
    }
}
