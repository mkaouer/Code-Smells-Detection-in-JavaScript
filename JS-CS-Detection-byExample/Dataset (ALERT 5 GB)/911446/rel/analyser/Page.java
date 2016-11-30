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

package nz.org.venice.analyser;

import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * 
 * A base class for Analysis windows and their inner Pages extending JPanel
 * adding showErrorMessage functionality using invokeLater to avoid threading 
 * issues when displaying errors.
 * 
 * TODO: Find a better name for this class
 * 
 * @author Guillermo Bonvehi (gbonvehi)
 */

abstract public class Page extends JPanel {
	/**
	 * Contains the reference to desktop
	 */
	protected JDesktopPane desktop;
	
	/**
	 * Internal class to be used by invokeLater to display
	 * an error message using JOptionPane.showInternalMessageDialog 
	 */
    class RunnableErrorMessage implements Runnable {
    	private String content;
    	private String title;
    	
    	public RunnableErrorMessage(String content, String title) {
    		this.content = content;
    		this.title = title;
    	}
    	public void run() {
    		JOptionPane.showInternalMessageDialog(desktop, 
                    this.content, 
                    this.title,
                    JOptionPane.ERROR_MESSAGE);	
    	}
    }

    /**
     * Display an error using invokeLater to avoid threading issues
     * 
     * @param content
     * @param title
     */
    protected void showErrorMessage(String content, String title) {
    	// Invokes on dispatch thread
    	SwingUtilities.invokeLater(new RunnableErrorMessage(content,title));
    }
}