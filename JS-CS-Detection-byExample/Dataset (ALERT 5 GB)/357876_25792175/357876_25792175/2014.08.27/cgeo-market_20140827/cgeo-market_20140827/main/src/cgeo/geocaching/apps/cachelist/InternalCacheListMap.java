package cgeo.geocaching.apps.cachelist;

import cgeo.geocaching.Geocache;
import cgeo.geocaching.R;
import cgeo.geocaching.SearchResult;
import cgeo.geocaching.apps.AbstractApp;
import cgeo.geocaching.maps.CGeoMap;

import android.app.Activity;

import java.util.List;

class InternalCacheListMap extends AbstractApp implements CacheListApp {

    InternalCacheListMap() {
        super(getString(R.string.cache_menu_map), R.id.cache_list_app_map, null);
    }

    @Override
    public boolean isInstalled() {
        return true;
    }

    @Override
    public boolean invoke(List<Geocache> caches, Activity activity, final SearchResult search) {
        CGeoMap.startActivitySearch(activity, search, null);
        return true;
    }
}
