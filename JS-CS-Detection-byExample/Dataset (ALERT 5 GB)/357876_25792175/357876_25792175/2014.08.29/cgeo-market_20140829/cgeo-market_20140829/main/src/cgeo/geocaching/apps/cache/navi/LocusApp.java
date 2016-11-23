package cgeo.geocaching.apps.cache.navi;


import cgeo.geocaching.Geocache;
import cgeo.geocaching.R;
import cgeo.geocaching.Waypoint;
import cgeo.geocaching.apps.AbstractLocusApp;

import android.app.Activity;
import android.content.Intent;

import java.util.Collections;

class LocusApp extends AbstractLocusApp implements CacheNavigationApp, WaypointNavigationApp {

    private static final String INTENT = Intent.ACTION_VIEW;

    protected LocusApp() {
        super(getString(R.string.caches_map_locus), R.id.cache_app_locus, INTENT);
    }

    @Override
    public boolean isEnabled(Waypoint waypoint) {
        return waypoint.getCoords() != null;
    }

    @Override
    public boolean isEnabled(Geocache cache) {
        return cache.getCoords() != null;
    }

    /**
     * Show a single cache with waypoints or a single waypoint in Locus.
     * This method constructs a list of cache and waypoints only.
     *
     */
    @Override
    public void navigate(Activity activity, Waypoint waypoint) {
        showInLocus(Collections.singletonList(waypoint), true, false, activity);
    }

    @Override
    public void navigate(Activity activity, Geocache cache) {
        showInLocus(Collections.singletonList(cache), true, false, activity);
    }
}
