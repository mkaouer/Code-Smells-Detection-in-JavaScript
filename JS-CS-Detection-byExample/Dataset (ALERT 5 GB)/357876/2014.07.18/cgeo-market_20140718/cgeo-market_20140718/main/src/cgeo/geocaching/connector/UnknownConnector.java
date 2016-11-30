package cgeo.geocaching.connector;

import cgeo.geocaching.Geocache;
import cgeo.geocaching.ICache;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.NonNull;

public class UnknownConnector extends AbstractConnector {

    @Override
    public String getName() {
        return "Unknown caches";
    }

    @Override
    public String getCacheUrl(@NonNull Geocache cache) {
        return null; // we have no url for these caches
    }

    @Override
    public String getHost() {
        return null; // we have no host for these caches
    }

    @Override
    public boolean isOwner(final ICache cache) {
        return false;
    }

    @Override
    public boolean canHandle(final @NonNull String geocode) {
        return StringUtils.isNotBlank(geocode);
    }

    @Override
    protected String getCacheUrlPrefix() {
        return null;
    }

}
