package net.sourceforge.pmd.util.viewer.model;


import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import net.sourceforge.pmd.ast.SimpleNode;


/**
 * Model for the AST Panel Tree component
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id: ASTModel.java 4980 2007-01-27 18:11:14Z allancaplan $
 */

public class ASTModel implements TreeModel {
	
    private SimpleNode root;
    private List<TreeModelListener> listeners = new ArrayList<TreeModelListener>(1);

    /**
     * creates the tree model
     *
     * @param root tree's root
     */
    public ASTModel(SimpleNode root) {
        this.root = root;
    }

    /**
     * @see javax.swing.tree.TreeModel
     */
    public Object getChild(Object parent, int index) {
        return ((SimpleNode) parent).jjtGetChild(index);
    }

    /**
     * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
     */
    public int getChildCount(Object parent) {
        return ((SimpleNode) parent).jjtGetNumChildren();
    }

    /**
     * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object,java.lang.Object)
     */
    public int getIndexOfChild(Object parent, Object child) {
        SimpleNode node = ((SimpleNode) parent);
        for (int i = 0; i < node.jjtGetNumChildren(); i++)
            if (node.jjtGetChild(i).equals(child)) {
                return i;
            }
        return -1;
    }

    /**
     * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
     */
    public boolean isLeaf(Object node) {
        return ((SimpleNode) node).jjtGetNumChildren() == 0;
    }

    /**
     * @see javax.swing.tree.TreeModel#getRoot()
     */
    public Object getRoot() {
        return root;
    }

    /**
     * @see javax.swing.tree.TreeModel#valueForPathChanged(javax.swing.tree.TreePath,java.lang.Object)
     */
    public void valueForPathChanged(TreePath path, Object newValue) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.TreeModelListener)
     */
    public void addTreeModelListener(TreeModelListener l) {
        listeners.add(l);
    }


    /**
     * @see javax.swing.tree.TreeModel#removeTreeModelListener(javax.swing.event.TreeModelListener)
     */
    public void removeTreeModelListener(TreeModelListener l) {
        listeners.remove(l);
    }


    protected void fireTreeModelEvent(TreeModelEvent e) {
        for (TreeModelListener listener : listeners) {
            listener.treeNodesChanged(e);
        }
    }

}

