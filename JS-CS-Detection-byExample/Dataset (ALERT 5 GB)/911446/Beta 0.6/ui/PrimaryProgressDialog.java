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

import org.mov.util.Locale;

/**
 * This class is the only instance of a progress dialog in venice. When a progress
 * dialog is created, this class creates the dialog. The other class that implements
 * {@link ProgressDialog} merely proxies to this class.
 * <p>
 * See {@link ProgressDialogManager} for details about using progress dialogs in
 * venice.
 *
 * @author Andrew Leppard
 */
public class PrimaryProgressDialog implements ProgressDialog {

    // Current progress 
    private int progress = 0;

    // Current percent displayed
    private int percent = -1;

    // Title of window (without the percent progress)
    private String title;

    // Is this dialog a master dialog?
    private boolean master;

    // GUI components
    private JProgressBar progressBar;
    private JLabel noteLabel;
    private JLabel progressLabel;
    private JButton cancelButton;
    private JDialog dialog;
        
    /** 
     * Create a new Progress Dialog. The dialog will not be displayed until {@link #show}
     * is called. The Primary Progress Dialog is the actual dialog being displayed, it
     * is returned by the first call to {@link ProgressDialogManager#getProgressDialog}.
     *
     * @param parent the parent desktop pane
     */
    public PrimaryProgressDialog(JDesktopPane parent) {
        newDialog(parent);
        setIndeterminate(true);
    }

    // Creates a new instance of the contained dialog
    private void newDialog(JDesktopPane parent) {
        final Thread thread = Thread.currentThread();

        // If the cancel button is hit, close the dialog and send an
        // interrupt to the current thread
	cancelButton = new JButton(Locale.getString("CANCEL"));
        cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    hide();
                    thread.interrupt();
                }
            });

	progressBar = new JProgressBar();

        // Most the Java implementations have very poor representations of
        // indeterminate progress, the exception being the Mac implementation.
        // So unless the user is running under Mac. Use our custom graphic.
        if(!isMacOSX())
            progressBar.setUI(new ProgressBarUI());

	noteLabel = new JLabel(Locale.getString("LOADING"));
	progressLabel = new JLabel(Locale.getString("PLEASE_WAIT"));

	JPanel panel = new JPanel();
	BorderLayout layout = new BorderLayout();
        layout.setHgap(50);
	layout.setVgap(5);

	panel.setLayout(layout);
	panel.add(noteLabel, BorderLayout.NORTH);
        panel.add(progressBar, BorderLayout.CENTER);
        panel.add(progressLabel, BorderLayout.SOUTH);

	Object options[] = {cancelButton};
	JOptionPane optionPane = 
            new JOptionPane(panel,
                            JOptionPane.INFORMATION_MESSAGE,
                            JOptionPane.OK_CANCEL_OPTION,
                            null, options, null);

	dialog = optionPane.createDialog(parent, Locale.getString("PROGRESS"));
        dialog.setModal(false);

	optionPane.getRootPane().setDefaultButton(cancelButton);
    }

    /**
     * Close the dialog window.
     */
    public void hide() {
        dialog.setVisible(false);
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
        setTitle(title);
        dialog.setVisible(true);
        setProgress(getProgress());
    }

    /** 
     * Get the note or rather the action we are curently waiting for. E.g. the task might be
     * "Graph CBA", but the action we are waiting for would be "Load CBA Quotes".
     *
     * @return the action we are waiting for
     */
    public String getNote() {
        return noteLabel.getText();
    }

    /**
     * Set the note to be the action we are curently waiting for. E.g. the task might be
     * "Graph CBA", but the action we are waiting for would be "Load CBA Quotes".
     *
     * @param note the action we are waiting for
     */
    public void setNote(String note) {
        if(!noteLabel.getText().equals(note))       
            noteLabel.setText(note);
    }
    
    // Set the text to display in the dialog title. This should be set
    // to the end task that we are performing. It should be set only
    // once per dialog creation.
    private void setTitle(String title) {
        this.title = title;
        updateTitle();
    }

    // Updates the text displayed in the title. 
    private void updateTitle() {
        if(isIndeterminate()) {
            dialog.setTitle(title);
        }
        else {
            dialog.setTitle(getPercent() + "% " + title);
        }
    }

    /** 
     * Get the minimum progress value.
     *
     * @return the minimum progress value
     */
    public int getMinimum() {
        return progressBar.getMinimum();
    }
    
    /** 
     * Set the minimum progress value.
     *
     * @param minimum the minimum progress value
     */
    public void setMinimum(int minimum) {
        setIndeterminate(false);
        progressBar.setMinimum(minimum);
    }
    
    /** 
     * Get the maximum progress value.
     *
     * @return the maximum progress value
     */
    public int getMaximum() {
        return progressBar.getMaximum();
    }
    
    /** 
     * Get the maximum progress value.
     *
     * @param maximum the maximum progress value
     */
    public void setMaximum(int maximum) {
        setIndeterminate(false);
        progressBar.setMaximum(maximum);
    }
    
    /**
     * Get current progress value.
     *
     * @return Value of property progress
     */
    public int getProgress() {
        return progress;
    }
    
    /** 
     * Set current progress value.
     *
     * @param progress New value of property progress
     */
    public void setProgress(int progress) {
        this.progress = progress;

        assert progress >= getMinimum() && progress <= getMaximum();

        if(isIndeterminate()) {
            progressLabel.setText(Locale.getString("PLEASE_WAIT"));
        }
        else { 
            int newPercent = getPercent();

            // Only bother updating if the percent has changed
            if(newPercent != percent) {
                percent = newPercent;
                progressLabel.setText(Locale.getString("PROGRESS_PERCENT",
						       Integer.toString(getProgress()),
						       Integer.toString(getMaximum()),
						       Integer.toString(newPercent)));
                progressBar.setValue(getProgress());
                progressBar.repaint();
                updateTitle();
            }
        }
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
        return progressBar.isIndeterminate();
    }
    
    /**
     * Set whether the dialog is indeterminate. An indeterminate 
     * progress does not given the user any indication of when the
     * task might be completed.
     *
     * @param indeterminate <code>true</code> if no progress can be displayed
     */
    public void setIndeterminate(boolean indeterminate) {
        progressBar.setIndeterminate(indeterminate);
        progressBar.repaint();
    }

    // Return the current progress as a percent
    private int getPercent() {
        if(getMaximum() == 0) {
            return 0;
        }
        else
            return (int)(getProgress()*100/getMaximum());
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

    /**
     * Return whether the current operating system is Mac OS X.
     *
     * @return <code>true</code> if the OS is Mac OS X; <code>false</code> otherwise
     */
    private boolean isMacOSX() {
        String OS = System.getProperty("os.name");
        return OS.equals("Mac OS X");
    }
}
