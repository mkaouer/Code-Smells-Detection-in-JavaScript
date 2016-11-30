package cgeo.geocaching;

import cgeo.geocaching.activity.AbstractListActivity;
import cgeo.geocaching.ui.AddressListAdapter;
import cgeo.geocaching.utils.Log;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import android.app.ProgressDialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;

import java.util.List;
import java.util.Locale;

public class AddressListActivity extends AbstractListActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.addresslist_activity);

        // get parameters
        final String keyword = getIntent().getStringExtra(Intents.EXTRA_KEYWORD);

        if (keyword == null) {
            showToast(res.getString(R.string.err_search_address_forgot));
            finish();
            return;
        }

        final AddressListAdapter adapter = new AddressListAdapter(this);
        setListAdapter(adapter);

        final ProgressDialog waitDialog =
                ProgressDialog.show(this, res.getString(R.string.search_address_started), keyword, true);
        waitDialog.setCancelable(true);

        new AsyncTask<Void, Void, List<Address>>() {

            @Override
            protected List<Address> doInBackground(Void... params) {
                final Geocoder geocoder = new Geocoder(AddressListActivity.this, Locale.getDefault());
                try {
                    return geocoder.getFromLocationName(keyword, 20);
                } catch (Exception e) {
                    // non Google devices come without the geocoder
                    if (StringUtils.containsIgnoreCase(e.getMessage(), "Service not Available")) {
                        Log.i("No geocoder available");
                    }
                    else {
                        Log.e("AddressListActivity.doInBackground", e);
                    }
                    return null;
                }
            }

            @Override
            protected void onPostExecute(final List<Address> addresses) {
                waitDialog.dismiss();
                if (CollectionUtils.isNotEmpty(addresses)) {
                    for (final Address address : addresses) {
                        adapter.add(address); // don't use addAll, it's only available with API >= 11
                    }
                } else {
                    finish();
                    CacheListActivity.startActivityAddress(AddressListActivity.this, null, keyword);
                }
            }

        }.execute();
    }
}