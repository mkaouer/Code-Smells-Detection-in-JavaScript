package cgeo.geocaching.apps.cachelist;

import cgeo.geocaching.Geocache;
import cgeo.geocaching.R;
import cgeo.geocaching.SearchResult;
import cgeo.geocaching.apps.AbstractLocusApp;

import org.apache.commons.collections4.CollectionUtils;

import android.app.Activity;
import android.content.Intent;

import java.util.List;

abstract class AbstractLocusCacheListApp extends AbstractLocusApp implements CacheListApp {

    private boolean export;

    public AbstractLocusCacheListApp(final int id, boolean export) {
        super(getString(export ? R.string.caches_map_locus_export : R.string.caches_map_locus), id, Intent.ACTION_VIEW);
        this.export = export;
    }

    /**
     * show caches in Locus
     *
     * @see AbstractLocusApp#showInLocus
     */
    @Override
    public boolean invoke(List<Geocache> cacheList, Activity activity, final SearchResult search) {
        if (CollectionUtils.isEmpty(cacheList)) {
            return false;
        }

        showInLocus(cacheList, false, export, activity);

        return true;
    }

}
