package cgeo.geocaching.activity;

import cgeo.geocaching.ui.dialog.CustomProgressDialog;
import cgeo.geocaching.utils.Log;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Message;
import android.view.WindowManager;

/**
 * progress dialog wrapper for easier management of resources
 */
public class Progress {

    private ProgressDialog dialog;
    private int progress = 0;
    private int progressDivider = 1;
    final private boolean hideAbsolute;

    public Progress(boolean hideAbsolute) {
        this.hideAbsolute = hideAbsolute;
    }

    public Progress() {
        this(false);
    }

    public synchronized void dismiss() {
        if (isShowing()) {
            try {
                dialog.dismiss();
            } catch (final Exception e) {
                Log.e("Progress.dismiss", e);
            }
        }
        dialog = null;
    }

    public synchronized void show(final Context context, final String title, final String message, final boolean indeterminate, final Message cancelMessage) {
        if (!isShowing()) {
            createProgressDialog(context, title, message, cancelMessage);
            dialog.setIndeterminate(indeterminate);
            dialog.show();
        }
    }

    public synchronized void show(final Context context, final String title, final String message, final int style, final Message cancelMessage) {
        if (!isShowing()) {
            createProgressDialog(context, title, message, cancelMessage);
            dialog.setProgressStyle(style);
            dialog.show();
        }
    }

    private void createProgressDialog(final Context context, final String title, final String message, final Message cancelMessage) {
        dialog = hideAbsolute ? new CustomProgressDialog(context) : new ProgressDialog(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        if (cancelMessage != null) {
            dialog.setCancelable(true);
            dialog.setCancelMessage(cancelMessage);
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getResources().getString(android.R.string.cancel), cancelMessage);
        } else {
            dialog.setCancelable(false);
        }
        dialog.setProgress(0);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.progress = 0;
    }

    public synchronized void setMessage(final String message) {
        if (dialog != null && dialog.isShowing()) {
            dialog.setMessage(message);
        }
    }

    public synchronized boolean isShowing() {
        return dialog != null && dialog.isShowing();
    }

    public synchronized void setMaxProgressAndReset(final int max) {
        if (isShowing()) {
            final int modMax = max / this.progressDivider;
            dialog.setMax(modMax);
            dialog.setProgress(0);
        }
        this.progress = 0;
    }

    public synchronized void setProgress(final int progress) {
        final int modProgress = progress / this.progressDivider;
        if (isShowing()) {
            dialog.setProgress(modProgress);
        }
        this.progress = modProgress;
    }

    public synchronized int getProgress() {
        if (dialog != null) {
            dialog.getProgress();
        }
        return this.progress;
    }

    public synchronized void setProgressDivider(final int progressDivider) {
        this.progressDivider = progressDivider;
    }
}
