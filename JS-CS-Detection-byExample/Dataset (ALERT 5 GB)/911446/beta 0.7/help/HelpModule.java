/* Merchant of Venice - technical analysis software for the stock market.
   Copyright (C) 2002 Andrew Leppard (aleppard@picknowl.com.au)

   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2 of the License, or
   (at your option) any later version.
   
   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.
   
   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA 
*/

package org.mov.help;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import org.mov.main.*;
import org.mov.util.Locale;

/**
 * This module provides a help browser for Venice. It allows traveresal of a tree
 * like document in a extremely cut down HTML type browser. The tree is made from
 * {@link HelpPage} pages.
 *
 * @see HelpPage
 */
public class HelpModule extends JPanel implements Module {
    
    // ToolBar Images - these are from jlfgr-1.0.jar
    private String backImage = "toolbarButtonGraphics/navigation/Back24.gif";
    private String forwardImage = "toolbarButtonGraphics/navigation/Forward24.gif";
    private String homeImage = "toolbarButtonGraphics/navigation/Home24.gif";

    // ToolBar buttons
    JButton backButton = null;
    JButton forwardButton = null;
    JButton upButton = null;

    // Menu 
    private JMenuBar menuBar;
    private JMenuItem backMenuItem;
    private JMenuItem forwardMenuItem;

    private JTree indexTree;
    private JEditorPane editorPane;

    private JDesktopPane desktop;
    private PropertyChangeSupport propertySupport;

    // Top level page
    private HelpPage root;

    // Page we are displaying in the tree
    private HelpPage currentPage;

    // Set to true when we change the tree selection - when we change it we 
    // update the page ourselves and don't need the tree calling update page
    private boolean ignoreTreeSelectionEvent = false;
    
    // Stack of pages we've visited
    private Stack visitedPages;
    private int positionInStack;
    
    /**
     * Create a new help browser loaded at the root page.
     *
     * @param	desktop	the parent desktop.
     */
    public HelpModule(JDesktopPane desktop) {
        this.desktop = desktop;

        propertySupport = new PropertyChangeSupport(this);       

	setLayout(new BorderLayout());

	addFunctionToolBar();
        addMenuBar();
        root = HelpPage.loadIndex();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(2.0F/7.0F);

        indexTree = createIndexTree();
        editorPane = createEditorPane();

        splitPane.setLeftComponent(new JScrollPane(indexTree));
        splitPane.setRightComponent(new JScrollPane(editorPane));

        add(splitPane, BorderLayout.CENTER);        

        // Update visisted page stack
        visitedPages = new Stack();
        positionInStack = 0;
        visitedPages.push(root);
        displayPage((HelpPage)root);
    }

    // Adds the tool bar
    private void addFunctionToolBar() {
        // Create images on toolbar
        URL backURL = ClassLoader.getSystemResource(backImage);
        URL forwardURL = ClassLoader.getSystemResource(forwardImage);
        URL homeURL = ClassLoader.getSystemResource(homeImage);

        // If not all of the images could be found then do not create 
        // the toolbar
        if(backURL != null && forwardURL != null && homeURL != null) {
            JToolBar toolBar = new JToolBar(SwingConstants.HORIZONTAL);
            
            backButton = new JButton(new ImageIcon(backURL));
            backButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        back();
                    }
                });
            toolBar.add(backButton);

            forwardButton = new JButton(new ImageIcon(forwardURL));            
            forwardButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        forward();
                    }
                });
            toolBar.add(forwardButton);

            JButton homeButton = new JButton(new ImageIcon(homeURL));            
            homeButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        home();
                    }
                });
            toolBar.add(homeButton);

            add(toolBar, BorderLayout.NORTH);
        }
    }

    // Adds the menu bar
    private void addMenuBar() {
        menuBar = new JMenuBar();

        JMenu helpMenu = new JMenu(Locale.getString("HELP"));

        JMenuItem closeMenuItem = new JMenuItem(Locale.getString("CLOSE"));
        closeMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    propertySupport.firePropertyChange(ModuleFrame.WINDOW_CLOSE_PROPERTY, 0, 1);
                }
            });
        helpMenu.add(closeMenuItem);

        JMenu goMenu = new JMenu(Locale.getString("GO"));
        backMenuItem = new JMenuItem(Locale.getString("BACK"));
        backMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    back();
                }
            });
        goMenu.add(backMenuItem);

        forwardMenuItem = new JMenuItem(Locale.getString("FORWARD"));
        forwardMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    forward();
                }
            });
        goMenu.add(forwardMenuItem);

        JMenuItem homeMenuItem = new JMenuItem(Locale.getString("HOME"));
        homeMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    home();
                }
            });
        goMenu.add(homeMenuItem);

        menuBar.add(helpMenu);
        menuBar.add(goMenu);
    }

    // This function creates the index tree widget on the left hand side. It
    // does this by feeding in the root document page into the tree. This
    // function is also responsible for listening to tree events. If the
    // user clicks on an item in the tree it will display that page.
    private JTree createIndexTree() {
        JTree indexTree = new JTree(root);
        indexTree.addTreeSelectionListener(new TreeSelectionListener() {
                public void valueChanged(TreeSelectionEvent e) {
                    if(!ignoreTreeSelectionEvent) {
                        TreePath treePath = e.getPath();
                        
                        // I'm not sure if it's possible for it to be 0, but handle
                        // it incase
                        if(treePath.getPathCount() > 0) {
                            HelpPage selectedPage =
                                (HelpPage)treePath.getLastPathComponent();
                            jump(selectedPage);
                        }
                    }
                }
            });
        
        return indexTree;
    }

    // This function creates the editor pane (actually it's non-editable) that displays
    // the current HTML page
    private JEditorPane createEditorPane() {
        JEditorPane editorPane = new JEditorPane();
        editorPane.setEditable(false);
        editorPane.setContentType("text/html");

        // This code is executed when the user clicks on a hyper text link
        editorPane.addHyperlinkListener(new HyperlinkListener() {
                public void hyperlinkUpdate(HyperlinkEvent e) {
                    if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                        String link = e.getDescription();

                        // If the chapter has a space in it, the link will
                        // have "%20" so replace this with a space.
                        Pattern pattern = Pattern.compile("%20");
                        Matcher matcher = pattern.matcher(link);
                        link = matcher.replaceAll(" ");

                        HelpPage page = root.findPageWithLink(link);

                        // There should be a page with the link description!
                        if(page != null)
                            jump(page);
                        else
                            assert false;
                    }
                }
            });

        return editorPane;
    }

    // Display the given page. Make sure you update the stack!!! You can use the
    // function jump() to display a page and handle the stack. The stack is used
    // to handle the user pressing the back/forward buttons.
    private void displayPage(HelpPage page) {
        // Display page
        editorPane.setText(page.getText());

        // By default it'll be viewing the bottom of the page - so
        // make it view the top of the page.
        editorPane.setCaretPosition(0);

        // This page is now the current page
        currentPage = page;

        // Make sure the arrows/menus are disabled for traversals that are
        // impossible, e.g. go back on first page etc.
        checkDisabledStatus();

        // Make sure it's selected in the tree
        ignoreTreeSelectionEvent = true;
        indexTree.setSelectionPath(new TreePath(currentPage.getPath()));
        ignoreTreeSelectionEvent = false;       
    }

    // Enable/disable the back/forward buttons depending on whether we can
    // go back/forward
    private void checkDisabledStatus() {
        backMenuItem.setEnabled(positionInStack > 0);

        if(backButton != null)
            backButton.setEnabled(positionInStack > 0);

        forwardMenuItem.setEnabled(positionInStack < (visitedPages.size() - 1));

        if(forwardButton != null)
            forwardButton.setEnabled(positionInStack < (visitedPages.size() - 1));
    }

    // Go to the last page visited
    private void back() {
        assert positionInStack > 0;
        
        HelpPage previousPage = 
            (HelpPage)visitedPages.elementAt(--positionInStack);
        displayPage(previousPage);
    }

    // Go to the next page visited
    private void forward() {       
        assert positionInStack < (visitedPages.size() - 1);

        HelpPage nextPage =
            (HelpPage)visitedPages.elementAt(++positionInStack);
        displayPage(nextPage);
    }

    // Jump to the following page
    private void jump(HelpPage page) {
        // If there are "forward" pages in the stack they are deleted if the
        // user jumps to another page
        while(positionInStack != (visitedPages.size() - 1)) {
            visitedPages.pop();
        }

        // If the page is the one we are on then ignore
        if(!visitedPages.peek().equals(page)) {
            visitedPages.push(page);
            positionInStack++;
            displayPage(page);
        }
        else
            checkDisabledStatus();
    }

    // Jump to the home page
    private void home() {
        jump(root);
    }

    /**
     * Return the window title.
     *
     * @return	the window title
     */
    public String getTitle() {
	return Locale.getString("HELP");
    }

    /**
     * Add a property change listener for module change events.
     *
     * @param	listener	listener
     */
    public void addModuleChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove a property change listener for module change events.
     *
     * @param	listener	listener
     */
    public void removeModuleChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }

    /**
     * Return displayed component for this module.
     *
     * @return the component to display.
     */
    public JComponent getComponent() {
	return this;
    }

    /**
     * Return menu bar for chart module.
     *
     * @return	the menu bar.
     */
    public JMenuBar getJMenuBar() {
	return menuBar;
    }

    /**
     * Return frame icon for chart module.
     *
     * @return	the frame icon
     */
    public ImageIcon getFrameIcon() {
        return null;
    }

    /**
     * Return whether the module should be enclosed in a scroll pane.
     *
     * @return	enclose module in scroll bar
     */
    public boolean encloseInScrollPane() {
	return false;
    }

    /**
     * Tell module to save any current state data / preferences data because
     * the window is being closed.
     */
    public void save() { 
        // nothing to do
    }

}
