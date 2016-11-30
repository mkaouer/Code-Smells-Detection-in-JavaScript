package cgeo.geocaching;

import cgeo.geocaching.compatibility.Compatibility;
import cgeo.geocaching.concurrent.BlockingThreadPool;
import cgeo.geocaching.files.LocalStorage;
import cgeo.geocaching.geopoint.GeopointFormatter.Format;
import cgeo.geocaching.network.Network;
import cgeo.geocaching.network.Parameters;
import cgeo.geocaching.settings.Settings;
import cgeo.geocaching.utils.FileUtils;
import cgeo.geocaching.utils.Log;

import ch.boye.httpclientandroidlib.HttpResponse;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.NonNull;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

import java.io.File;
import java.util.concurrent.TimeUnit;

public final class StaticMapsProvider {
    static final int MAPS_LEVEL_MAX = 5;
    private static final String PREFIX_PREVIEW = "preview";
    private static final String GOOGLE_STATICMAP_URL = "http://maps.google.com/maps/api/staticmap";
    private static final String SATELLITE = "satellite";
    private static final String ROADMAP = "roadmap";
    private static final String WAYPOINT_PREFIX = "wp";
    private static final String MAP_FILENAME_PREFIX = "map_";
    private static final String MARKERS_URL = "http://status.cgeo.org/assets/markers/";

    /** We assume there is no real usable image with less than 1k. */
    private static final int MIN_MAP_IMAGE_BYTES = 1000;

    /** ThreadPool restricting this to 1 Thread. **/
    private static final BlockingThreadPool POOL = new BlockingThreadPool(1, Thread.MIN_PRIORITY);

    /**
     * max size in free API version: https://developers.google.com/maps/documentation/staticmaps/#Imagesizes
     */
    private static final int GOOGLE_MAPS_MAX_SIZE = 640;

    private StaticMapsProvider() {
        // utility class
    }

    private static File getMapFile(final String geocode, final String prefix, final boolean createDirs) {
        return LocalStorage.getStorageFile(geocode, MAP_FILENAME_PREFIX + prefix, false, createDirs);
    }

    private static void downloadDifferentZooms(final String geocode, final String markerUrl, final String prefix, final String latlonMap, final int edge, final Parameters waypoints) {
        downloadMap(geocode, 20, SATELLITE, markerUrl, prefix + '1', "", latlonMap, edge, edge, waypoints);
        downloadMap(geocode, 18, SATELLITE, markerUrl, prefix + '2', "", latlonMap, edge, edge, waypoints);
        downloadMap(geocode, 16, ROADMAP, markerUrl, prefix + '3', "", latlonMap, edge, edge, waypoints);
        downloadMap(geocode, 14, ROADMAP, markerUrl, prefix + '4', "", latlonMap, edge, edge, waypoints);
        downloadMap(geocode, 11, ROADMAP, markerUrl, prefix + '5', "", latlonMap, edge, edge, waypoints);
    }

    private static void downloadMap(final String geocode, final int zoom, final String mapType, final String markerUrl, final String prefix, final String shadow, final String latlonMap, final int width, final int height, final Parameters waypoints) {
        final Parameters params = new Parameters(
                "center", latlonMap,
                "zoom", String.valueOf(zoom),
                "size", String.valueOf(limitSize(width)) + 'x' + String.valueOf(limitSize(height)),
                "maptype", mapType,
                "markers", "icon:" + markerUrl + '|' + shadow + latlonMap,
                "sensor", "false");
        if (waypoints != null) {
            params.addAll(waypoints);
        }
        final HttpResponse httpResponse = Network.getRequest(GOOGLE_STATICMAP_URL, params);

        if (httpResponse == null) {
            Log.e("StaticMapsProvider.downloadMap: httpResponse is null");
            return;
        }
        if (httpResponse.getStatusLine().getStatusCode() != 200) {
            Log.d("StaticMapsProvider.downloadMap: httpResponseCode = " + httpResponse.getStatusLine().getStatusCode());
            return;
        }
        final File file = getMapFile(geocode, prefix, true);
        if (LocalStorage.saveEntityToFile(httpResponse, file)) {
            // Delete image if it has no contents
            final long fileSize = file.length();
            if (fileSize < MIN_MAP_IMAGE_BYTES) {
                FileUtils.deleteIgnoringFailure(file);
            }
        }
    }

    private static int limitSize(final int imageSize) {
        return Math.min(imageSize, GOOGLE_MAPS_MAX_SIZE);
    }

    public static void downloadMaps(final Geocache cache) {
        if ((!Settings.isStoreOfflineMaps() && !Settings.isStoreOfflineWpMaps()) || StringUtils.isBlank(cache.getGeocode())) {
            return;
        }
        int edge = guessMaxDisplaySide();

        if (Settings.isStoreOfflineMaps() && cache.getCoords() != null) {
            storeCachePreviewMap(cache);
            storeCacheStaticMap(cache, edge, false);
        }

        // clean old and download static maps for waypoints if one is missing
        if (Settings.isStoreOfflineWpMaps()) {
            for (final Waypoint waypoint : cache.getWaypoints()) {
                if (!hasAllStaticMapsForWaypoint(cache.getGeocode(), waypoint)) {
                    refreshAllWpStaticMaps(cache, edge);
                }
            }

        }
    }

    /**
     * Deletes and download all Waypoints static maps.
     *
     * @param cache
     *            The cache instance
     * @param edge
     *            The boundings
     */
    private static void refreshAllWpStaticMaps(final Geocache cache, final int edge) {
        LocalStorage.deleteFilesWithPrefix(cache.getGeocode(), MAP_FILENAME_PREFIX + WAYPOINT_PREFIX);
        for (Waypoint waypoint : cache.getWaypoints()) {
            storeWaypointStaticMap(cache.getGeocode(), edge, waypoint, false);
        }
    }

    public static void storeWaypointStaticMap(final Geocache cache, final Waypoint waypoint, final boolean waitForResult) {
        int edge = StaticMapsProvider.guessMaxDisplaySide();
        storeWaypointStaticMap(cache.getGeocode(), edge, waypoint, waitForResult);
    }

    private static void storeWaypointStaticMap(final String geocode, final int edge, final Waypoint waypoint, final boolean waitForResult) {
        if (geocode == null) {
            Log.e("storeWaypointStaticMap - missing input parameter geocode");
            return;
        }
        if (waypoint == null) {
            Log.e("storeWaypointStaticMap - missing input parameter waypoint");
            return;
        }
        if (waypoint.getCoords() == null) {
            return;
        }
        String wpLatlonMap = waypoint.getCoords().format(Format.LAT_LON_DECDEGREE_COMMA);
        String wpMarkerUrl = getWpMarkerUrl(waypoint);
        if (!hasAllStaticMapsForWaypoint(geocode, waypoint)) {
            // download map images in separate background thread for higher performance
            downloadMaps(geocode, wpMarkerUrl, WAYPOINT_PREFIX + waypoint.getId() + '_' + waypoint.getStaticMapsHashcode() + "_", wpLatlonMap, edge, null, waitForResult);
        }
    }

    public static void storeCacheStaticMap(final Geocache cache, final boolean waitForResult) {
        int edge = guessMaxDisplaySide();
        storeCacheStaticMap(cache, edge, waitForResult);
    }

    private static void storeCacheStaticMap(final Geocache cache, final int edge, final boolean waitForResult) {
        final String latlonMap = cache.getCoords().format(Format.LAT_LON_DECDEGREE_COMMA);
        final Parameters waypoints = new Parameters();
        for (final Waypoint waypoint : cache.getWaypoints()) {
            if (waypoint.getCoords() == null) {
                continue;
            }
            final String wpMarkerUrl = getWpMarkerUrl(waypoint);
            waypoints.put("markers", "icon:" + wpMarkerUrl + '|' + waypoint.getCoords().format(Format.LAT_LON_DECDEGREE_COMMA));
        }
        // download map images in separate background thread for higher performance
        final String cacheMarkerUrl = getCacheMarkerUrl(cache);
        downloadMaps(cache.getGeocode(), cacheMarkerUrl, "", latlonMap, edge, waypoints, waitForResult);
    }

    public static void storeCachePreviewMap(final Geocache cache) {
        final String latlonMap = cache.getCoords().format(Format.LAT_LON_DECDEGREE_COMMA);
        final Point displaySize = Compatibility.getDisplaySize();
        final int minSize = Math.min(displaySize.x, displaySize.y);
        final String markerUrl = MARKERS_URL + "my_location_mdpi.png";
        downloadMap(cache.getGeocode(), 15, ROADMAP, markerUrl, PREFIX_PREVIEW, "shadow:false|", latlonMap, minSize, minSize, null);
    }

    private static int guessMaxDisplaySide() {
        Point displaySize = Compatibility.getDisplaySize();
        return Math.max(displaySize.x, displaySize.y) - 25;
    }

    private static void downloadMaps(final String geocode, final String markerUrl, final String prefix, final String latlonMap, final int edge,
            final Parameters waypoints, final boolean waitForResult) {
        if (waitForResult) {
            downloadDifferentZooms(geocode, markerUrl, prefix, latlonMap, edge, waypoints);
        }
        else {
            final Runnable currentTask = new Runnable() {
                @Override
                public void run() {
                    downloadDifferentZooms(geocode, markerUrl, prefix, latlonMap, edge, waypoints);
                }
            };
            try {
                POOL.add(currentTask, 20, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Log.e("StaticMapsProvider.downloadMaps error adding task", e);
            }
        }
    }

    private static String getCacheMarkerUrl(final Geocache cache) {
        StringBuilder url = new StringBuilder(MARKERS_URL);
        url.append("marker_cache_").append(cache.getType().id);
        if (cache.isFound()) {
            url.append("_found");
        } else if (cache.isDisabled()) {
            url.append("_disabled");
        }
        url.append(".png");
        return url.toString();
    }

    private static String getWpMarkerUrl(final Waypoint waypoint) {
        String type = waypoint.getWaypointType() != null ? waypoint.getWaypointType().id : null;
        return MARKERS_URL + "marker_waypoint_" + type + ".png";
    }

    public static void removeWpStaticMaps(final Waypoint waypoint, final String geocode) {
        if (waypoint == null) {
            return;
        }
        int waypointId = waypoint.getId();
        int waypointMapHash = waypoint.getStaticMapsHashcode();
        for (int level = 1; level <= MAPS_LEVEL_MAX; level++) {
            final File mapFile = StaticMapsProvider.getMapFile(geocode, WAYPOINT_PREFIX + waypointId + "_" + waypointMapHash + '_' + level, false);
            if (!FileUtils.delete(mapFile)) {
                Log.e("StaticMapsProvider.removeWpStaticMaps failed for " + mapFile.getAbsolutePath());
            }
        }
    }

    /**
     * Check if at least one map file exists for the given cache.
     *
     * @param cache
     * @return <code>true</code> if at least one map file exists; <code>false</code> otherwise
     */
    public static boolean hasStaticMap(@NonNull final Geocache cache) {
       final String geocode = cache.getGeocode();
        if (StringUtils.isBlank(geocode)) {
            return false;
        }
        for (int level = 1; level <= MAPS_LEVEL_MAX; level++) {
            File mapFile = StaticMapsProvider.getMapFile(geocode, String.valueOf(level), false);
            if (mapFile.exists()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if at least one map file exists for the given geocode and waypoint ID.
     *
     * @param geocode
     * @param waypoint
     * @return <code>true</code> if at least one map file exists; <code>false</code> otherwise
     */
    public static boolean hasStaticMapForWaypoint(final String geocode, final Waypoint waypoint) {
        int waypointId = waypoint.getId();
        int waypointMapHash = waypoint.getStaticMapsHashcode();
        for (int level = 1; level <= MAPS_LEVEL_MAX; level++) {
            File mapFile = StaticMapsProvider.getMapFile(geocode, WAYPOINT_PREFIX + waypointId + "_" + waypointMapHash + "_" + level, false);
            if (mapFile.exists()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if all map files exist for the given geocode and waypoint ID.
     *
     * @param geocode
     * @param waypoint
     * @return <code>true</code> if all map files exist; <code>false</code> otherwise
     */
    public static boolean hasAllStaticMapsForWaypoint(final String geocode, final Waypoint waypoint) {
        int waypointId = waypoint.getId();
        int waypointMapHash = waypoint.getStaticMapsHashcode();
        for (int level = 1; level <= MAPS_LEVEL_MAX; level++) {
            File mapFile = StaticMapsProvider.getMapFile(geocode, WAYPOINT_PREFIX + waypointId + "_" + waypointMapHash + "_" + level, false);
            boolean mapExists = mapFile.exists();
            if (!mapExists) {
                return false;
            }
        }
        return true;
    }

    public static Bitmap getPreviewMap(final Geocache cache) {
        return decodeFile(StaticMapsProvider.getMapFile(cache.getGeocode(), PREFIX_PREVIEW, false));
    }

    public static Bitmap getWaypointMap(final String geocode, final Waypoint waypoint, final int level) {
        int waypointId = waypoint.getId();
        int waypointMapHash = waypoint.getStaticMapsHashcode();
        return decodeFile(StaticMapsProvider.getMapFile(geocode, WAYPOINT_PREFIX + waypointId + "_" + waypointMapHash + "_" + level, false));
    }

    public static Bitmap getCacheMap(final String geocode, final int level) {
        return decodeFile(StaticMapsProvider.getMapFile(geocode, String.valueOf(level), false));
    }

    private static Bitmap decodeFile(final File mapFile) {
        // avoid exception in system log, if we got nothing back from Google.
        if (mapFile.exists()) {
            return BitmapFactory.decodeFile(mapFile.getPath());
        }
        return null;
    }
}
