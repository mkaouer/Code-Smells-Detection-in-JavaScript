package cgeo.geocaching.activity;

import cgeo.geocaching.MainActivity;
import cgeo.geocaching.R;
import cgeo.geocaching.compatibility.Compatibility;
import cgeo.geocaching.settings.Settings;

import org.apache.commons.lang3.StringUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public final class ActivityMixin {

    public final static void goHome(final Activity fromActivity) {
        final Intent intent = new Intent(fromActivity, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        fromActivity.startActivity(intent);
        fromActivity.finish();
    }

    public static void setTitle(final Activity activity, final CharSequence text) {
        if (StringUtils.isBlank(text)) {
            return;
        }

        final TextView title = (TextView) activity.findViewById(R.id.actionbar_title);
        if (title != null) {
            title.setText(text);
        }
    }

    public static void showProgress(final Activity activity, final boolean show) {
        if (activity == null) {
            return;
        }

        final ProgressBar progress = (ProgressBar) activity.findViewById(R.id.actionbar_progress);
        if (show) {
            progress.setVisibility(View.VISIBLE);
        } else {
            progress.setVisibility(View.GONE);
        }
    }

    public static void setTheme(final Activity activity) {
        if (Settings.isLightSkin()) {
            activity.setTheme(R.style.light);
        } else {
            activity.setTheme(R.style.dark);
        }
    }

    public static int getDialogTheme() {
        // Light theme dialogs don't work on Android Api < 11
        if (Settings.isLightSkin() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return R.style.popup_light;
        }

        return R.style.popup_dark;
    }

    public static void showToast(final Activity activity, final int resId) {
        ActivityMixin.showToast(activity, activity.getString(resId));
    }

    public static void showToast(final Activity activity, final String text) {
        if (StringUtils.isNotBlank(text)) {
            Toast toast = Toast.makeText(activity, text, Toast.LENGTH_LONG);

            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 100);
            toast.show();
        }
    }

    public static void showShortToast(final Activity activity, final String text) {
        if (StringUtils.isNotBlank(text)) {
            Toast toast = Toast.makeText(activity, text, Toast.LENGTH_SHORT);

            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 100);
            toast.show();
        }
    }

    public static void keepScreenOn(final Activity abstractActivity, boolean keepScreenOn) {
        if (keepScreenOn) {
            abstractActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    public static void invalidateOptionsMenu(Activity activity) {
        Compatibility.invalidateOptionsMenu(activity);
    }

    /**
     * insert text into the EditText at the current cursor position
     *
     * @param editText
     * @param insertText
     * @param moveCursor
     *            place the cursor after the inserted text
     */
    public static void insertAtPosition(final EditText editText, final String insertText, final boolean moveCursor) {
        int selectionStart = editText.getSelectionStart();
        int selectionEnd = editText.getSelectionEnd();
        int start = Math.min(selectionStart, selectionEnd);
        int end = Math.max(selectionStart, selectionEnd);

        final String content = editText.getText().toString();
        String completeText;
        if (start > 0 && !Character.isWhitespace(content.charAt(start - 1))) {
            completeText = " " + insertText;
        } else {
            completeText = insertText;
        }

        editText.getText().replace(start, end, completeText);
        int newCursor = moveCursor ? start + completeText.length() : start;
        editText.setSelection(newCursor);
    }
}
