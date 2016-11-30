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

package org.mov.main;

import java.awt.Dimension;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;
import org.mov.ui.DesktopManager;

/**
 * An internal frame designed specifically for holding Module objects.  Every visible Module should run within
 * an ModuleFrame, and is supplied to it upon construction.
 */
public class ModuleFrame extends JInternalFrame
    implements PropertyChangeListener, InternalFrameListener
{
    // Property indicating window should be closed
    public static final String WINDOW_CLOSE_PROPERTY = "window close";

    // Property indicating title bar has changed
    public static final String TITLEBAR_CHANGED_PROPERTY = "titlebar changed";

    private final static int DEFAULT_LAYER = 2;

    // Each module frame contains a single module
    private Module module;

    private DesktopManager desktopManager;

    // Preferred width and height of frame
    static private int DEFAULT_FRAME_WIDTH = 535;
    static private int DEFAULT_FRAME_HEIGHT = 475;

    /**
     * Construct a new frame around the given module and display.
     *
     * @param	module The module to feed to the frame
     * @param	centre	Should the frame be centred?
     * @param	honourSize	Should we respect the frame's preferred
     *				size or should we override it with the 
     *				default?
     */
    public ModuleFrame(DesktopManager desktopManager, 
		       Module module, boolean centre, boolean honourSize) {

	super(module.getTitle(), 
	      honourSize? false : true, // resizable
	      true,   // closable
	      honourSize? false : true, // maximisable
	      true);  // iconifiable

	this.module = module;
	this.desktopManager = desktopManager;

	JDesktopPane desktop = DesktopManager.getDesktop();

	// Module can be enclosed in scroll pane if it desires to be
	if(module.encloseInScrollPane()) 
	    getContentPane().add(new JScrollPane(module.getComponent()));
	else
	    getContentPane().add(module.getComponent());

	setSizeAndLocation(this, desktop, centre, honourSize);

	if(module.getJMenuBar() != null)
	    setJMenuBar(module.getJMenuBar());

	// Listen to events from module
	module.addModuleChangeListener(this);

	super.setFrameIcon(module.getFrameIcon());

	// We want to notify module when it is closing so it can save data
	addInternalFrameListener(this);
	show();	
    }

    /**
     * Set the size and location of any frame, taking care of out of
     * bounds frames. This method has been made static to help any
     * internal frames size themselves as if they were a proper
     * Venice ModuleFrame.
     *
     * @param frame the frame to size
     * @param desktop the desktop
     * @param centre whether the frame should be centred?
     * @param honourSize whether we should honour the frame's preferred size 
     */
    public static void setSizeAndLocation(JInternalFrame frame,
                                          JDesktopPane desktop, 
                                          boolean centre, boolean honourSize) {
	int x, y, width, height;
	Dimension preferred = frame.getPreferredSize();	    

	// Should we respect the window's preferred size or override?
	if(honourSize) {
	    width = preferred.width;
	    height = preferred.height;
	}
	else {
	    width = DEFAULT_FRAME_WIDTH;
	    height = DEFAULT_FRAME_HEIGHT;
	}

	// Should we centre the window or place in (0,0) ?
	if(centre) {
	    x = (desktop.getWidth() - preferred.width) / 2;
	    y = (desktop.getHeight() - preferred.height) / 2;
	}
	else {
	    x = 0;
	    y = 0;
	}

	// Make sure new frame is within window bounds
	if(x > desktop.getWidth())
	    x = desktop.getWidth() - width;
	if(y > desktop.getHeight())
	    y = desktop.getWidth() - height;
	if(x < 0) 
	    x = 0;
	if(y < 0)
	    y = 0;
	if(width > x + desktop.getWidth())
	    width = desktop.getWidth() - x;
	if(height > y + desktop.getHeight())
	    height = desktop.getHeight() - y;

	// Set size and location
	frame.setBounds(x, y, width, height);
    }

    /**
     * Gives a reference to the module running inside the ModuleFrame
     *
     * @return The module running in the frame
     */
    public Module getModule() {
	return module;
    }

    /** 
     * Standard property change handler that listens for a WINDOW_CLOSE event 
     */
    public void propertyChange(PropertyChangeEvent event) {
	String property = event.getPropertyName();

	// Window closed? Close window!
	if(property.equals(WINDOW_CLOSE_PROPERTY)) {
	    dispose();
	}

	// Title changed? Change title!
	else if(property.equals(TITLEBAR_CHANGED_PROPERTY)) {
	    setTitle(module.getTitle());

	    // Update menu containing list of windows
	    desktopManager.fireModuleRenamed(module);
	}
    }

    /* Make sure the internal modules saves its information before destroying it
     */
    public void internalFrameClosed(InternalFrameEvent e) { 
	module.save();

	// Update menu containing list of windows
	desktopManager.fireModuleRemoved(module);
    }
    /**
     * Standard InternalFrame functions
     */
    public void internalFrameActivated(InternalFrameEvent e) { }
    public void internalFrameClosing(InternalFrameEvent e) { }
    public void internalFrameDeactivated(InternalFrameEvent e) { }
    public void internalFrameDeiconified(InternalFrameEvent e) { }
    public void internalFrameIconified(InternalFrameEvent e) { }
    public void internalFrameOpened(InternalFrameEvent e) { }


    
}


