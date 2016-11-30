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

import java.util.*;

/**
 * This class controls progress dialog creation/deletion in venice. It controls
 * progress dialogs by allowing each thread in venice to have only a single
 * progress dialog running at anyone time. If, in a thread, a request for another
 * progress dialog is called, it will use the current progress dialog being displayed
 * for that thread. By using a proxy progress dialog class ({@link SecondaryProgressDialog}), 
 * we can control how much the second comer can change the current progress, without
 * the caller even knowing.
 * <p>
 * <b>How to Display Progress</b>
 * <pre>
 * // Get the current thread - we'll need this to see 
 * // if the user has cancelled the dialog
 * Thread thread = Thread.currentThread();
 *
 * // Create the progress dialog, configure it and show it.
 * ProgressDialog progress = ProgressDialogManager.getProgressDialog();
 * progress.setMaximum(3);
 * progress.setProgress(0);
 * progress.show("Performing Task");
 *
* for(int i = 0; i < 3; i++) {
 *    Task(i);
 *
 *    // If the progress dialog is cancelled. The thread will be interrupted.
 *    // Has the user cancelled the dialog? If the user has, we should stop 
 *    // what we are doing.
 *    if(thread.isInterrupted())
 *       break;
 *
 *    progress.increment();
 * }
 *
 * ProgressDialogManager.closeProgressDialog(progress);
 * </pre>
 *
 * <b>Master Dialogs</b>
 * <p>
 * Sometimes when you are displaying progress you might be performing several tasks,
 * where some of the tasks display their own progress. In this situation it might
 * be undesirable for the contained tasks to show their progress. You can stop them
 * by making your progress dialog a master dialog.
 * <pre>
 * progress.setMaster(true);
 * </pre>
 * This will stop their progress being displayed. The only thing they will
 * have control over is the note field. 
 * <p>
 * On the other side, if you set a contained task to be a master, it will show
 * its progress, <i>even if its container task is set to be a master</i>. This
 * is useful for initialisation tasks that can be very slow and should 
 * always be shown to the user.
 * <p>
 *
 * @see ProgressDialog
 * @see PrimaryProgressDialog
 * @see SecondaryProgressDialog
 */
public class ProgressDialogManager {

    /* List of all the currently defined Progress Dialogs available */
    private static Hashtable cachedPrimaryProgressDialogs = new Hashtable();
    private static Hashtable cachedSecondaryProgressDialogs = new Hashtable();

    /* Prevent instantiation of ProgressDialogManager */
    private ProgressDialogManager() {
        // nothing to do
    }

    /**
     * Create a new {@link PrimaryProgressDialog} if one is not already being displayed for 
     * the current thread. If one is displayed, return a proxy to the current dialog, i.e.
     * a {@link SecondaryProgressDialog}.
     * The secondary progress dialog may or may not have full control over the real dialog. 
     *
     * @return progress dialog
     * @see PrimaryProgressDialog
     * @see SecondaryProgressDialog
     */
    public static ProgressDialog getProgressDialog() {
        return getProgressDialog(true);
    }

    /**
     * Create a new {@link PrimaryProgressDialog} if one is not already being displayed for 
     * the current thread. If one is displayed, return a proxy to the current dialog, i.e.
     * a {@link SecondaryProgressDialog}.
     * The secondary progress dialog may or may not have full control over the real dialog. 
     *
     * @param isCancelButtonToBePainted true if we need to paint a cancel button
     *
     * @return progress dialog
     * @see PrimaryProgressDialog
     * @see SecondaryProgressDialog
     */
    public static ProgressDialog getProgressDialog(
            boolean isCancelButtonToBePainted) {
        PrimaryProgressDialog primaryProgressDialog = 
            (PrimaryProgressDialog)cachedPrimaryProgressDialogs.get(Thread.currentThread());

        // If there isn't a primary progress window up, then create one
        if (primaryProgressDialog == null) {
            primaryProgressDialog = new PrimaryProgressDialog(DesktopManager.getDesktop(),
                    isCancelButtonToBePainted);
            cachedPrimaryProgressDialogs.put(Thread.currentThread(), primaryProgressDialog);
            return primaryProgressDialog;
        }            

        // Otherwise create a secondary progress dialog which proxies to the primary. 
        // This allows us to set which dialog (primary or secondary) has control.
        else {

            // If a primary dialog and a secondary dialog are up, we can't open another
            // secondary dialog until the first one closses. Otherwise we'd have 3 level
            // nested dialogs which isn't supported.
            assert cachedSecondaryProgressDialogs.get(Thread.currentThread()) == null;

            ProgressDialog secondaryProgressDialog = 
                new SecondaryProgressDialog(primaryProgressDialog);
            cachedSecondaryProgressDialogs.put(Thread.currentThread(), secondaryProgressDialog);
            return secondaryProgressDialog;
        }
    }

    /**
     * Returns whether a progress dialog is currently on screen.
     *
     * @return <code>TRUE</code> if a progress dialog is being displayed.
     */
    public static boolean isProgressDialogUp() {
        return cachedPrimaryProgressDialogs.containsKey(Thread.currentThread());
    }
    
    /** 
     * Closes and removes the progress dialog associated with the current thread.
     */
    public static void closeProgressDialog(ProgressDialog progressDialog) {

        // Remove dialog from appropriate list
        if(progressDialog instanceof PrimaryProgressDialog) {
            ProgressDialog removedProgressDialog = 
                (ProgressDialog)cachedPrimaryProgressDialogs.remove(Thread.currentThread());
            assert removedProgressDialog == progressDialog;

            // If we are closing a primary - there shouldn't be any secondary open
            assert cachedSecondaryProgressDialogs.get(Thread.currentThread()) == null;
        }
        else {
            assert progressDialog instanceof SecondaryProgressDialog;
            ProgressDialog removedProgressDialog = 
                (ProgressDialog)cachedSecondaryProgressDialogs.remove(Thread.currentThread());
            assert removedProgressDialog == progressDialog;            

            // If we are closing a secondary - there should be a primary open
            assert cachedPrimaryProgressDialogs.get(Thread.currentThread()) != null;
        }

        progressDialog.hide();
    }
}
