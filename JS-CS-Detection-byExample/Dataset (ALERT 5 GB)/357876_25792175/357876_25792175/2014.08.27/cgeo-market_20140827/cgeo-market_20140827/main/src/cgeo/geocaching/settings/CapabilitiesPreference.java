package cgeo.geocaching.settings;

import cgeo.geocaching.CgeoApplication;
import cgeo.geocaching.R;
import cgeo.geocaching.connector.ConnectorFactory;
import cgeo.geocaching.connector.IConnector;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

/**
 * Preference for displaying the supported capabilities of an {@link IConnector} implementation.
 */
public class CapabilitiesPreference extends AbstractAttributeBasedPrefence {

    private String connectorCode;

    public CapabilitiesPreference(Context context) {
        super(context);
    }

    public CapabilitiesPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CapabilitiesPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public View getView(View convertView, ViewGroup parent) {
        setOnPreferenceClickListener(new ClickListener());
        return super.getView(convertView, parent);
    }

    private final class ClickListener implements OnPreferenceClickListener {
        @Override
        public boolean onPreferenceClick(final Preference preference) {
            WebView htmlView = new WebView(preference.getContext());
            htmlView.loadDataWithBaseURL(null, createCapabilitiesMessage(), "text/html", "utf-8", null);
            AlertDialog.Builder builder = new AlertDialog.Builder(preference.getContext());
            builder.setView(htmlView)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle(R.string.settings_features)
                    .setPositiveButton(R.string.err_none, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            builder.create().show();
            return false;
        }
    }

    public String createCapabilitiesMessage() {
        // TODO: this needs a better key for the connectors
        IConnector connector = ConnectorFactory.getConnector(connectorCode + "1234");
        StringBuilder builder = new StringBuilder("<p>"
                + TextUtils.htmlEncode(CgeoApplication.getInstance().getString(R.string.feature_description)) + "</p><ul>");

        for (String capability : connector.getCapabilities()) {
            builder.append("<li>").append(TextUtils.htmlEncode(capability)).append("</li>");
        }

        builder.append("</ul>");
        return builder.toString();
    }

    @Override
    protected void processAttributeValues(TypedArray values) {
        connectorCode = values.getString(0);
    }

    @Override
    protected int[] getAttributeNames() {
        return new int[] { R.attr.connector };
    }
}
