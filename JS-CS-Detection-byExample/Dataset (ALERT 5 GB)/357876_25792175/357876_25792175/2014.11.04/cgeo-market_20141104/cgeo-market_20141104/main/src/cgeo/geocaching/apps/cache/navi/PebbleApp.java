package cgeo.geocaching.apps.cache.navi;

import cgeo.geocaching.R;
import cgeo.geocaching.geopoint.Geopoint;

import android.content.Intent;

/**
 * Application for communication with the Pebble watch.
 *
 */
class PebbleApp extends AbstractRadarApp {

    private static final String INTENT = "com.webmajstr.pebble_gc.NAVIGATE_TO";
    private static final String PACKAGE_NAME = "com.webmajstr.pebble_gc";

    PebbleApp() {
        super(getString(R.string.cache_menu_pebble), R.id.cache_app_pebble, INTENT, PACKAGE_NAME);
    }

    @Override
    protected void addCoordinates(final Intent intent, final Geopoint coords) {
        intent.putExtra("latitude", coords.getLatitude());
        intent.putExtra("longitude", coords.getLongitude());
    }
}