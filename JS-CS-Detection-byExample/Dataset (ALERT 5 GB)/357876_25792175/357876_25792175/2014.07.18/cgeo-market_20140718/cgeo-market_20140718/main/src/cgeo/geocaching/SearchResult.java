package cgeo.geocaching;

import cgeo.geocaching.connector.IConnector;
import cgeo.geocaching.connector.gc.GCLogin;
import cgeo.geocaching.enumerations.CacheType;
import cgeo.geocaching.enumerations.LoadFlags;
import cgeo.geocaching.enumerations.LoadFlags.LoadFlag;
import cgeo.geocaching.enumerations.LoadFlags.SaveFlag;
import cgeo.geocaching.enumerations.StatusCode;
import cgeo.geocaching.gcvote.GCVote;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.Nullable;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class SearchResult implements Parcelable {

    final private Set<String> geocodes;
    final private Set<String> filteredGeocodes;
    private StatusCode error = null;
    private String url = "";
    public String[] viewstates = null;
    /**
     * Overall number of search results matching our search on geocaching.com. If this number is higher than 20, we have
     * to fetch multiple pages to get all caches.
     */
    private int totalCountGC = 0;

    final public static Parcelable.Creator<SearchResult> CREATOR = new Parcelable.Creator<SearchResult>() {
        @Override
        public SearchResult createFromParcel(Parcel in) {
            return new SearchResult(in);
        }

        @Override
        public SearchResult[] newArray(int size) {
            return new SearchResult[size];
        }
    };

    /**
     * Build a new empty search result.
     */
    public SearchResult() {
        this(new HashSet<String>());
    }

    /**
     * Copy a search result, for example to apply different filters on it.
     *
     * @param searchResult the original search result, which cannot be null
     */
    public SearchResult(final SearchResult searchResult) {
        geocodes = new HashSet<String>(searchResult.geocodes);
        filteredGeocodes = new HashSet<String>(searchResult.filteredGeocodes);
        error = searchResult.error;
        url = searchResult.url;
        viewstates = searchResult.viewstates;
        setTotalCountGC(searchResult.getTotalCountGC());
    }

    /**
     * Build a search result from an existing collection of geocodes.
     *
     * @param geocodes
     *            a non-null collection of geocodes
     * @param totalCountGC
     *            the total number of caches matching that search on geocaching.com (as we always get only the next 20
     *            from a web page)
     */
    public SearchResult(final Collection<String> geocodes, final int totalCountGC) {
        this.geocodes = new HashSet<String>(geocodes.size());
        this.geocodes.addAll(geocodes);
        this.filteredGeocodes = new HashSet<String>();
        this.setTotalCountGC(totalCountGC);
    }

    /**
     * Build a search result from an existing collection of geocodes.
     *
     * @param geocodes a non-null set of geocodes
     */
    public SearchResult(final Set<String> geocodes) {
        this(geocodes, geocodes.size());
    }

    public SearchResult(final Parcel in) {
        final ArrayList<String> list = new ArrayList<String>();
        in.readStringList(list);
        geocodes = new HashSet<String>(list);
        final ArrayList<String> filteredList = new ArrayList<String>();
        in.readStringList(filteredList);
        filteredGeocodes = new HashSet<String>(filteredList);
        error = (StatusCode) in.readSerializable();
        url = in.readString();
        final int length = in.readInt();
        if (length >= 0) {
            viewstates = new String[length];
            in.readStringArray(viewstates);
        }
        setTotalCountGC(in.readInt());
    }

    /**
     * Build a search result designating a single cache.
     *
     * @param cache the cache to include
     */

    public SearchResult(final Geocache cache) {
        this(Collections.singletonList(cache));
    }

    /**
     * Build a search result from a collection of caches.
     *
     * @param caches the non-null collection of caches to include
     */
    public SearchResult(final Collection<Geocache> caches) {
        this();
        addAndPutInCache(caches);
    }

    @Override
    public void writeToParcel(final Parcel out, final int flags) {
        out.writeStringArray(geocodes.toArray(new String[geocodes.size()]));
        out.writeStringArray(filteredGeocodes.toArray(new String[filteredGeocodes.size()]));
        out.writeSerializable(error);
        out.writeString(url);
        if (viewstates == null) {
            out.writeInt(-1);
        } else {
            out.writeInt(viewstates.length);
            out.writeStringArray(viewstates);
        }
        out.writeInt(getTotalCountGC());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Set<String> getGeocodes() {
        return Collections.unmodifiableSet(geocodes);
    }

    public int getCount() {
        return geocodes.size();
    }

    public StatusCode getError() {
        return error;
    }

    public void setError(final StatusCode error) {
        this.error = error;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String[] getViewstates() {
        return viewstates;
    }

    public void setViewstates(String[] viewstates) {
        if (GCLogin.isEmpty(viewstates)) {
            return;
        }
        // lazy initialization of viewstates
        if (this.viewstates == null) {
            this.viewstates = new String[viewstates.length];
        }

        System.arraycopy(viewstates, 0, this.viewstates, 0, viewstates.length);
    }

    public int getTotalCountGC() {
        return totalCountGC;
    }

    public void setTotalCountGC(int totalCountGC) {
        this.totalCountGC = totalCountGC;
    }

    /**
     * @param excludeDisabled
     * @param excludeMine
     * @param cacheType
     * @return
     */
    public SearchResult filterSearchResults(final boolean excludeDisabled, final boolean excludeMine, final CacheType cacheType) {

        SearchResult result = new SearchResult(this);
        result.geocodes.clear();
        final ArrayList<Geocache> includedCaches = new ArrayList<Geocache>();
        final Set<Geocache> caches = DataStore.loadCaches(geocodes, LoadFlags.LOAD_CACHE_OR_DB);
        int excluded = 0;
        for (Geocache cache : caches) {
            // Is there any reason to exclude the cache from the list?
            final boolean excludeCache = (excludeDisabled && cache.isDisabled()) ||
                    (excludeMine && (cache.isOwner() || cache.isFound())) ||
                    (!cacheType.contains(cache));
            if (excludeCache) {
                excluded++;
            } else {
                includedCaches.add(cache);
            }
        }
        result.addAndPutInCache(includedCaches);
        // decrease maximum number of caches by filtered ones
        result.setTotalCountGC(result.getTotalCountGC() - excluded);
        GCVote.loadRatings(includedCaches);
        return result;
    }

    @Nullable
    public Geocache getFirstCacheFromResult(final EnumSet<LoadFlag> loadFlags) {
        return CollectionUtils.isNotEmpty(geocodes) ? DataStore.loadCache(geocodes.iterator().next(), loadFlags) : null;
    }

    public Set<Geocache> getCachesFromSearchResult(final EnumSet<LoadFlag> loadFlags) {
        return DataStore.loadCaches(geocodes, loadFlags);
    }

    /** Add the geocode to the search. No cache is loaded into the CacheCache */
    public boolean addGeocode(final String geocode) {
        if (StringUtils.isBlank(geocode)) {
            throw new IllegalArgumentException("geocode must not be blank");
        }
        return geocodes.add(geocode);
    }

    /** Add the geocodes to the search. No caches are loaded into the CacheCache */
    public boolean addGeocodes(Set<String> geocodes) {
        return this.geocodes.addAll(geocodes);
    }

    /** Add the cache geocode to the search and store the cache in the CacheCache */
    public void addAndPutInCache(final Collection<Geocache> caches) {
        for (Geocache geocache : caches) {
            addGeocode(geocache.getGeocode());
        }
        DataStore.saveCaches(caches, EnumSet.of(SaveFlag.SAVE_CACHE));
    }

    public boolean isEmpty() {
        return geocodes.isEmpty();
    }

    public boolean hasUnsavedCaches() {
        for (final String geocode : getGeocodes()) {
            if (!DataStore.isOffline(geocode, null)) {
                return true;
            }
        }
        return false;
    }

    public void addFilteredGeocodes(Set<String> cachedMissingFromSearch) {
        filteredGeocodes.addAll(cachedMissingFromSearch);
    }

    public Set<String> getFilteredGeocodes() {
        return Collections.unmodifiableSet(filteredGeocodes);
    }

    public void addSearchResult(SearchResult other) {
        if (other == null) {
            return;
        }
        addGeocodes(other.geocodes);
        addFilteredGeocodes(other.filteredGeocodes);
        if (StringUtils.isBlank(url)) {
            url = other.url;
        }
        // copy the GC total search results number to be able to use "More caches" button
        if (getTotalCountGC() == 0 && other.getTotalCountGC() != 0) {
            setViewstates(other.getViewstates());
            setTotalCountGC(other.getTotalCountGC());
        }
    }

    public static <C extends IConnector> SearchResult parallelCombineActive(final Collection<C> connectors,
                                                                            final Func1<C, SearchResult> func) {
        return Observable.from(connectors).parallel(new Func1<Observable<C>, Observable<SearchResult>>() {
            @Override
            public Observable<SearchResult> call(final Observable<C> cObservable) {
                return cObservable.flatMap(new Func1<C, Observable<? extends SearchResult>>() {
                    @Override
                    public Observable<? extends SearchResult> call(final C c) {
                        return c.isActive() ? Observable.from(func.call(c)) : Observable.<SearchResult>empty();
                    }
                });
            }
        }, Schedulers.io()).reduce(new SearchResult(), new Func2<SearchResult, SearchResult, SearchResult>() {
            @Override
            public SearchResult call(final SearchResult searchResult, final SearchResult searchResult2) {
                searchResult.addSearchResult(searchResult2);
                return searchResult;
            }
        }).toBlockingObservable().first();
    }

}
