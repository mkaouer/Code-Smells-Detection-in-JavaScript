package cgeo.geocaching.ui;

import cgeo.geocaching.Geocache;
import cgeo.geocaching.Trackable;
import cgeo.geocaching.connector.ConnectorFactory;

import android.view.View;
import android.widget.TextView;

/**
 * Listener for clicks on user name
 */
public class UserActionsClickListener extends AbstractUserClickListener {

    public UserActionsClickListener(Geocache cache) {
        super(ConnectorFactory.getConnector(cache).getUserActions());
    }

    public UserActionsClickListener(Trackable trackable) {
        super(ConnectorFactory.getConnector(trackable).getUserActions());
    }

    @Override
    protected String getUserName(View view) {
        return ((TextView) view).getText().toString();
    }
}

