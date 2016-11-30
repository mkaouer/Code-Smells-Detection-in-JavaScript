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

package org.mov.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

import org.mov.main.Main;

/**
 * This interface defines the functions that a progress dialog class should have.
 * It's purpose is to allow us to create a {@link PrimaryProgressDialog} and a
 * {@link SecondaryProgressDialog} where the secondary progress dialog proxies to
 * the first.
 * <p>
 * See {@link ProgressDialogManager} for details about using progress dialogs in
 * venice.
 */
public interface ProgressDialog {

    /**
     * Close the dialog window.
     */
    public void hide();

    /**
     * Show the dialog window.
     *
     * @param title the title should be set to the task that we are performing,
     *              which is not necessarily the action we are waiting for.
     *              e.g. the task might be "Graph CBA", but the action we are
     *              waiting for would be "Load CBA Quotes".
     */
    public void show(String title);

    /** 
     * Get the note or rather the action we are curently waiting for. E.g. the task might be
     * "Graph CBA", but the action we are waiting for would be "Load CBA Quotes".
     *
     * @return the action we are waiting for
     */
    public String getNote();

    /**
     * Set the note or rather the action we are curently waiting for. E.g. the task might be
     * "Graph CBA", but the action we are waiting for would be "Load CBA Quotes".
     *
     * @param note the action we are waiting for
     */
    public void setNote(String note);

    /** 
     * Get the minimum progress value.
     *
     * @return the minimum progress value
     */
    public int getMinimum();
    
    /** 
     * Set the minimum progress value.
     *
     * @param minimum the minimum progress value
     */
    public void setMinimum(int minimum);
    
    /** 
     * Get the maximum progress value.
     *
     * @return the maximum progress value
     */
    public int getMaximum();

    /** 
     * Get the maximum progress value.
     *
     * @param maximum the maximum progress value
     */
    public void setMaximum(int maximum);
    
    /**
     * Get current progress value.
     *
     * @return Value of property progress
     */
    public int getProgress();
    
    /** 
     * Set current progress value.
     *
     * @param progress New value of property progress
     */
    public void setProgress(int progress);
    
    /** 
     * Increment current progress by one unit.
     */
    public void increment();
    
    /** 
     * Decrement current progress by one unit.
     */
    public void decrement();

    /**
     * Return whether the dialog is indeterminate. An indeterminate 
     * progress does not given the user any indication of when the
     * task might be completed.
     *
     * @return <code>true</code> if no progress is displayed
     */
    public boolean isIndeterminate();
    
    /**
     * Set whether the dialog is indeterminate. An indeterminate 
     * progress does not given the user any indication of when the
     * task might be completed.
     *
     * @param indeterminate <code>true</code> if no progress can be displayed
     */
    public void setIndeterminate(boolean indeterminate);

    /**
     * Set whether dialog is a master dialog. 
     *
     * @param master whether the dialog is a master dialog
     * @see ProgressDialogManager
     */
    public void setMaster(boolean master);

    /**
     * Query if the dialog is a master dialog.
     * 
     * @return whether the dialog is a master dialog
     * @see ProgressDialogManager
     */
    public boolean isMaster();
}
