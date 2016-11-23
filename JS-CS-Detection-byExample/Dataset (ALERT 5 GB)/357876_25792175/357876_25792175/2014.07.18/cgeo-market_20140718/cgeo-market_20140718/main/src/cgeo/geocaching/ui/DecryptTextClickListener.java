package cgeo.geocaching.ui;

import cgeo.geocaching.utils.CryptUtils;

import org.eclipse.jdt.annotation.NonNull;

import android.text.Spannable;
import android.view.View;
import android.widget.TextView;

public class DecryptTextClickListener implements View.OnClickListener {

    @NonNull private final TextView targetView;

    public DecryptTextClickListener(@NonNull final TextView targetView) {
        this.targetView = targetView;
    }

    @Override
    public final void onClick(final View view) {
        try {
            // do not run the click listener if a link was clicked
            if (targetView.getSelectionStart() != -1 || targetView.getSelectionEnd() != -1) {
                return;
            }

            CharSequence text = targetView.getText();
            if (text instanceof Spannable) {
                Spannable span = (Spannable) text;
                targetView.setText(CryptUtils.rot13(span));
            }
            else {
                String string = (String) text;
                targetView.setText(CryptUtils.rot13(string));
            }
        } catch (RuntimeException e) {
            // nothing
        }
    }
}
