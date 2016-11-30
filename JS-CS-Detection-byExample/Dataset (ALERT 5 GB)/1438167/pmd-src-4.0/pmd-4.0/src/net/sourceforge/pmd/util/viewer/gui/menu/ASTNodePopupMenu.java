package net.sourceforge.pmd.util.viewer.gui.menu;

import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.util.viewer.model.ViewerModel;

import javax.swing.*;

/**
 * context sensetive menu for the AST Panel
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id: ASTNodePopupMenu.java 4217 2006-02-10 14:15:31Z tomcopeland $
 */
public class ASTNodePopupMenu extends JPopupMenu {
    private ViewerModel model;
    private SimpleNode node;

    public ASTNodePopupMenu(ViewerModel model, SimpleNode node) {
        this.model = model;
        this.node = node;
        init();
    }

    private void init() {
        add(new SimpleNodeSubMenu(model, node));
        addSeparator();
        add(new AttributesSubMenu(model, node));
    }
}
