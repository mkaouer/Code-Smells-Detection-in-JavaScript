package net.filebot.ui.analyze;

import static java.util.Collections.*;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import net.filebot.ResourceManager;
import net.filebot.UserFiles;
import net.filebot.util.FilterIterator;
import net.filebot.util.TreeIterator;

public class FileTree extends JTree {

	public FileTree() {
		super(new DefaultTreeModel(new FolderNode()));
		getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		setCellRenderer(new FileTreeCellRenderer());
		setShowsRootHandles(true);
		setRootVisible(false);

		setRowHeight(22);
		setLargeModel(true);

		addMouseListener(new ExpandCollapsePopupListener());
	}

	@Override
	public DefaultTreeModel getModel() {
		return (DefaultTreeModel) super.getModel();
	}

	public FolderNode getRoot() {
		return (FolderNode) getModel().getRoot();
	}

	public void clear() {
		getModel().setRoot(new FolderNode());
		getModel().reload();
	}

	public void expandAll() {
		for (int row = 0; row < getRowCount(); row++) {
			expandRow(row);
		}
	}

	public void collapseAll() {
		for (int row = 0; row < getRowCount(); row++) {
			collapseRow(row);
		}
	}

	private class OpenExpandCollapsePopup extends JPopupMenu {

		public OpenExpandCollapsePopup() {
			TreePath[] selectionPaths = getSelectionPaths();
			Set<File> selectionFiles = null;

			if (selectionPaths != null) {
				selectionFiles = new LinkedHashSet<File>(selectionPaths.length);

				for (TreePath treePath : selectionPaths) {
					Object node = treePath.getLastPathComponent();

					if (node instanceof FileNode) {
						selectionFiles.add(((FileNode) node).getFile());
					}
				}
			}

			if (selectionFiles != null && !selectionFiles.isEmpty()) {
				JMenuItem openItem = new JMenuItem(new OpenAction("Open", selectionFiles));
				openItem.setFont(openItem.getFont().deriveFont(Font.BOLD));
				add(openItem);

				Set<File> selectionParentFolders = new LinkedHashSet<File>(selectionFiles.size());
				for (File file : selectionFiles) {
					selectionParentFolders.add(file.getParentFile());
				}

				add(new OpenAction("Open Folder", selectionParentFolders));
				addSeparator();
			}

			add(expandAction);
			add(collapseAction);
		}

		private class OpenAction extends AbstractAction {

			public OpenAction(String text, Collection<File> files) {
				super(text);
				putValue("files", files);
			}

			@Override
			public void actionPerformed(ActionEvent event) {
				UserFiles.revealFiles((Collection<File>) getValue("files"));
			}
		}

		private final Action expandAction = new AbstractAction("Expand all", ResourceManager.getIcon("tree.expand")) {

			@Override
			public void actionPerformed(ActionEvent e) {
				expandAll();
			}

		};

		private final Action collapseAction = new AbstractAction("Collapse all", ResourceManager.getIcon("tree.collapse")) {

			@Override
			public void actionPerformed(ActionEvent e) {
				collapseAll();
			}

		};

	}

	private class ExpandCollapsePopupListener extends MouseAdapter {

		@Override
		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}

		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				TreePath path = getPathForLocation(e.getX(), e.getY());

				if (!getSelectionModel().isPathSelected(path)) {
					// if clicked node is not selected, set selection to this node (and deselect all other currently selected nodes)
					setSelectionPath(path);
				}

				OpenExpandCollapsePopup popup = new OpenExpandCollapsePopup();
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	public static class AbstractTreeNode implements TreeNode {

		private TreeNode parent;

		@Override
		public TreeNode getParent() {
			return parent;
		}

		public void setParent(TreeNode parent) {
			this.parent = parent;
		}

		@Override
		public Enumeration<? extends TreeNode> children() {
			return null;
		}

		@Override
		public boolean getAllowsChildren() {
			return false;
		}

		@Override
		public TreeNode getChildAt(int childIndex) {
			return null;
		}

		@Override
		public int getChildCount() {
			return 0;
		}

		@Override
		public int getIndex(TreeNode node) {
			return -1;
		}

		@Override
		public boolean isLeaf() {
			// if we have no children, tell the UI we are a leaf,
			// so that it won't display any good-for-nothing expand buttons
			return getChildCount() == 0;
		}

	}

	public static class FileNode extends AbstractTreeNode {

		private final File file;

		public FileNode(File file) {
			this.file = file;
		}

		public File getFile() {
			return file;
		}

		@Override
		public String toString() {
			return file.getName();
		}
	}

	public static class FolderNode extends AbstractTreeNode {

		private final File file;

		private final String title;
		private final List<TreeNode> children;

		/**
		 * Creates a root node (no parent, no title, empty list of children)
		 */
		public FolderNode() {
			this(null, "", new ArrayList<TreeNode>(0));
		}

		public FolderNode(String title, List<TreeNode> children) {
			this(null, title, children);
		}

		public FolderNode(File file, String title, List<TreeNode> children) {
			this.file = file;
			this.title = title;
			this.children = children;
		}

		public File getFile() {
			return file;
		}

		@Override
		public String toString() {
			return title;
		}

		public List<TreeNode> getChildren() {
			return children;
		}

		@Override
		public Enumeration<? extends TreeNode> children() {
			return enumeration(children);
		}

		@Override
		public boolean getAllowsChildren() {
			return true;
		}

		@Override
		public TreeNode getChildAt(int childIndex) {
			return children.get(childIndex);
		}

		@Override
		public int getChildCount() {
			return children.size();
		}

		@Override
		public int getIndex(TreeNode node) {
			return children.indexOf(node);
		}

		public Iterator<TreeNode> treeIterator() {
			return new TreeIterator<TreeNode>(this) {

				@Override
				protected Iterator<TreeNode> children(TreeNode node) {
					if (node instanceof FolderNode)
						return ((FolderNode) node).getChildren().iterator();

					// can't step into non-folder nodes
					return null;
				}

			};
		}

		public Iterator<File> fileIterator() {
			return new FilterIterator<TreeNode, File>(treeIterator()) {

				@Override
				protected File filter(TreeNode node) {
					if (node instanceof FileNode)
						return ((FileNode) node).getFile();

					// filter out non-file nodes
					return null;
				}
			};
		}
	}

}
