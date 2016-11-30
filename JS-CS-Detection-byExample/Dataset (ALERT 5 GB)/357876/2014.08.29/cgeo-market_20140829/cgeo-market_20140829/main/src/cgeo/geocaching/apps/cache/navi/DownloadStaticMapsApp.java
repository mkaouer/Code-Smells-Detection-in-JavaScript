package cgeo.geocaching.apps.cache.navi;

import cgeo.geocaching.Geocache;
import cgeo.geocaching.R;
import cgeo.geocaching.Waypoint;

import android.app.Activity;

class DownloadStaticMapsApp extends AbstractStaticMapsApp {

    DownloadStaticMapsApp() {
        super(getString(R.string.cache_menu_download_map_static), R.id.cache_app_download_static_maps);
    }

    @Override
    public boolean isEnabled(final Geocache cache) {
        return cache.isOffline() && !cache.hasStaticMap();
    }

    @Override
    public boolean isEnabled(final Waypoint waypoint) {
        return !hasStaticMap(waypoint);
    }

    @Override
    public void navigate(final Activity activity, final Geocache cache) {
        invokeStaticMaps(activity, cache, null, true);
    }

    @Override
    public void navigate(final Activity activity, final Waypoint waypoint) {
        invokeStaticMaps(activity, null, waypoint, true);
    }
}
