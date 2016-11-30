package cgeo.geocaching.maps.interfaces;

import cgeo.geocaching.ICoordinates;

/**
 * Defines the common functions of the provider-specific
 * GeoPoint implementations
 */
public interface GeoPointImpl extends ICoordinates {

    int getLatitudeE6();

    int getLongitudeE6();

}
