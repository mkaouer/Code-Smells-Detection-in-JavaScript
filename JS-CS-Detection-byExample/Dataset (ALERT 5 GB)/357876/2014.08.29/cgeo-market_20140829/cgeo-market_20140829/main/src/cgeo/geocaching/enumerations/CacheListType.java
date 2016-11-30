package cgeo.geocaching.enumerations;

import cgeo.geocaching.loaders.AbstractSearchLoader.CacheListLoaderType;

public enum CacheListType {
    OFFLINE(true, CacheListLoaderType.OFFLINE),
    POCKET(false, CacheListLoaderType.POCKET),
    HISTORY(true, CacheListLoaderType.HISTORY),
    NEAREST(false, CacheListLoaderType.NEAREST),
    COORDINATE(false, CacheListLoaderType.COORDINATE),
    KEYWORD(false, CacheListLoaderType.KEYWORD),
    ADDRESS(false, CacheListLoaderType.ADDRESS),
    FINDER(false, CacheListLoaderType.FINDER),
    OWNER(false, CacheListLoaderType.OWNER),
    MAP(false, CacheListLoaderType.MAP);

    /**
     * whether or not this list allows switching to another list
     */
    public final boolean canSwitch;

    public final CacheListLoaderType loaderType;

    CacheListType(final boolean canSwitch, final CacheListLoaderType loaderType) {
        this.canSwitch = canSwitch;
        this.loaderType = loaderType;
    }

    public int getLoaderId() {
        return loaderType.getLoaderId();
    }
}
