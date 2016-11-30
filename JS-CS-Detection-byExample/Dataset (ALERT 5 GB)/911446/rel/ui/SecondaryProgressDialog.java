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

package nz.org.venice.ui;

/**
 * This class is a proxy to the {@link PrimaryProgressDialog} class. 
 * <p>
 * See {@link ProgressDialogManager} for details about using progress dialogs in
 * venice.
 */
public class SecondaryProgressDialog implements ProgressDialog {

    // Actual dialog we are proxying
    private PrimaryProgressDialog primaryProgressDialog;

    private boolean master;

    /* Cache the primary values incase we change them */
    private int minimum;
    private int maximum;
    private int progress;
    private boolean indeterminate;

    /**
     * Create a new Secondary Progress Dialog. This will actually create a proxy
     * to the {@link PrimaryProgressDialog}. The purpose of this proxy is that it
     * contains the logic to test whether or not it has the authority to change
     * the real progress dialog. The Secondary Progress Dialog is returned when
     * the programme calls {@link ProgressDialogManager#getProgressDialog} and
     * there is already a progress dialog up.
     * <p>
     * If this is the case, it might not be able to have full control over the
     * progress dialog.
     *
     * @param primaryProgressDialog the progress dialog we are proxying
     */
    public SecondaryProgressDialog(PrimaryProgressDialog primaryProgressDialog) {
        this.primaryProgressDialog = primaryProgressDialog;
    }

    /**
     * Close the dialog window.
     */
    public void hide() {
        // only the primary progress dialog can close it

        // If the primary is set to master and we are set to master we will
        // override the primary's values. In this case we need to restore them
        // as we really hijacked it.
        if(isMaster() && primaryProgressDialog.isMaster()) {
            primaryProgressDialog.setMinimum(minimum);
            primaryProgressDialog.setMaximum(maximum);
            primaryProgressDialog.setProgress(progress);
            primaryProgressDialog.setIndeterminate(indeterminate);
        }
    }

    /**
     * Show the dialog window.
     *
     * @param title the title should be set to the task that we are performing,
     *              which is not necessarily the action we are waiting for.
     *              e.g. the task might be "Graph CBA", but the action we are
     *              waiting for would be "Load CBA Quotes".
     */
    public void show(String title) {
        // only the primary progress dialog can show it

        // Cache dialog settings incase we change them and need to restore them
        minimum = primaryProgressDialog.getMinimum();
        maximum = primaryProgressDialog.getMaximum();
        progress = primaryProgressDialog.getProgress();
        indeterminate = primaryProgressDialog.isIndeterminate();
    }

    /** 
     * Get the note or rather the action we are curently waiting for. E.g. the task might be
     * "Graph CBA", but the action we are waiting for would be "Load CBA Quotes".
     *
     * @return the action we are waiting for
     */
    public String getNote() {
        return primaryProgressDialog.getNote();
    }

    /**
     * Set the note or rather the action we are curently waiting for. E.g. the task might be
     * "Graph CBA", but the action we are waiting for would be "Load CBA Quotes".
     *
     * @param note the action we are waiting for
     */
    public void setNote(String note) {
        primaryProgressDialog.setNote(note);
    }
    
    /** 
     * Get the minimum progress value.
     *
     * @return the minimum progress value
     */
    public int getMinimum() {
        return primaryProgressDialog.getMinimum();
    }

    /** 
     * Set the minimum progress value.
     *
     * @param minimum the minimum progress value
     */
    public void setMinimum(int minimum) {
        if(hasControl())
            primaryProgressDialog.setMinimum(minimum);
    }

    /** 
     * Get the maximum progress value.
     *
     * @return the maximum progress value
     */
    public int getMaximum() {
        return primaryProgressDialog.getMaximum();
    }

    /** 
     * Get the maximum progress value.
     *
     * @param maximum the maximum progress value
     */
    public void setMaximum(int maximum) {
        if(hasControl())
            primaryProgressDialog.setMaximum(maximum);
    }

    /**
     * Get current progress value.
     *
     * @return Value of property progress
     */
    public int getProgress() {
        return primaryProgressDialog.getProgress();
    }

    /** 
     * Set current progress value.
     *
     * @param progress New value of property progress
     */
    public void setProgress(int progress) {
        if(hasControl())
            primaryProgressDialog.setProgress(progress);
    }

    /** 
     * Increment current progress by one unit.
     */
    public void increment() {
        setProgress(getProgress() + 1);
    }

    /** 
     * Decrement current progress by one unit.
     */
    public void decrement() {
        setProgress(getProgress() - 1);
    }

    /**
     * Return whether the dialog is indeterminate. An indeterminate 
     * progress does not given the user any indication of when the
     * task might be completed.
     *
     * @return <code>true</code> if no progress is displayed
     */
    public boolean isIndeterminate() {
        return primaryProgressDialog.isIndeterminate();
    }

    /**
     * Set whether the dialog is indeterminate. An indeterminate 
     * progress does not given the user any indication of when the
     * task might be completed.
     *
     * @param indeterminate <code>true</code> if no progress can be displayed
     */
    public void setIndeterminate(boolean indeterminate) {
        if(hasControl())
            primaryProgressDialog.setIndeterminate(indeterminate);
    }

    /**
     * Set whether dialog is a master dialog. 
     *
     * @param master whether the dialog is a master dialog
     * @see ProgressDialogManager
     */
    public void setMaster(boolean master) {
        this.master = master;
    }

    /**
     * Query if the dialog is a master dialog.
     * 
     * @return whether the dialog is a master dialog
     * @see ProgressDialogManager
     */
    public boolean isMaster() {
        return master;
    }

    // Do we have full control of the progress dialog? I.e. are we allowed
    // to modify the progress, minimum, maximum & indeterminate fields?
    // We can if we are a master dialog or if neither dialogs are master
    // dialogs (which is the default).
    private boolean hasControl() {
        return (!primaryProgressDialog.isMaster() ||
                isMaster());
            
    }
}
