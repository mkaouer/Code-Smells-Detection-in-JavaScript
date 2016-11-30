package net.sourceforge.pmd.util.viewer.gui.menu;

import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.jaxen.Attribute;
import net.sourceforge.pmd.jaxen.AttributeAxisIterator;
import net.sourceforge.pmd.util.viewer.model.AttributeToolkit;
import net.sourceforge.pmd.util.viewer.model.ViewerModel;
import net.sourceforge.pmd.util.viewer.util.NLS;

import javax.swing.*;
import java.text.MessageFormat;


/**
 * contains menu items for the predicate creation
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id: AttributesSubMenu.java,v 1.10 2006/02/10 14:15:31 tomcopeland Exp $
 */
public class AttributesSubMenu
        extends JMenu {
    private ViewerModel model;
    private SimpleNode node;

    public AttributesSubMenu(ViewerModel model, SimpleNode node) {
        super(MessageFormat.format(NLS.nls("AST.MENU.ATTRIBUTES"), new Object[]{node.toString()}));
        this.model = model;
        this.node = node;
        init();
    }

    private void init() {
        AttributeAxisIterator i = new AttributeAxisIterator(node);
        while (i.hasNext()) {
            Attribute attribute = (Attribute) i.next();
            add(new XPathFragmentAddingItem(attribute.getName() + " = " + attribute.getValue(), model,
                    AttributeToolkit.constructPredicate(attribute)));
        }
    }
}
