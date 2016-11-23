package cgeo.geocaching.settings;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

/**
 * Password preference. It will only show a row of asterisks as summary instead of the password.
 * <p>
 * Use it exactly as an EditTextPreference
 * 
 */
public class EditPasswordPreference extends EditTextPreference {

    public EditPasswordPreference(Context context) {
        super(context);
    }

    public EditPasswordPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditPasswordPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setSummary(CharSequence summary) {
        if (StringUtils.isBlank(summary)) {
            super.setSummary(StringUtils.EMPTY);
        } else {
            super.setSummary(StringUtils.repeat("\u2022 ", 10));
        }
    }

}
