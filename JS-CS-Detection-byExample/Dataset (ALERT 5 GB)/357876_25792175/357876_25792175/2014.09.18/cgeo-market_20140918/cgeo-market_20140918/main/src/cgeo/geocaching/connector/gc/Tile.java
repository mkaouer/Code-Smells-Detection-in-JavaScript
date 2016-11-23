package cgeo.geocaching.connector.gc;

import cgeo.geocaching.ICoordinates;
import cgeo.geocaching.geopoint.Geopoint;
import cgeo.geocaching.geopoint.Viewport;
import cgeo.geocaching.network.Network;
import cgeo.geocaching.network.Parameters;
import cgeo.geocaching.utils.LeastRecentlyUsedSet;
import cgeo.geocaching.utils.Log;
import cgeo.geocaching.utils.RxUtils;

import ch.boye.httpclientandroidlib.HttpResponse;

import org.eclipse.jdt.annotation.NonNull;

import rx.Observable;
import rx.functions.Func0;
import rx.util.async.Async;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * All about tiles.
 *
 * @see <a href="http://msdn.microsoft.com/en-us/library/bb259689.aspx">MSDN</a>
 * @see <a
 *      href="http://svn.openstreetmap.org/applications/viewer/jmapviewer/src/org/openstreetmap/gui/jmapviewer/OsmMercator.java">OSM</a>
 */
public class Tile {

    public static final int TILE_SIZE = 256;
    public static final int ZOOMLEVEL_MAX = 18;
    public static final int ZOOMLEVEL_MIN = 0;
    public static final int ZOOMLEVEL_MIN_PERSONALIZED = 12;

    static final int[] NUMBER_OF_TILES = new int[ZOOMLEVEL_MAX - ZOOMLEVEL_MIN + 1];
    static final int[] NUMBER_OF_PIXELS = new int[ZOOMLEVEL_MAX - ZOOMLEVEL_MIN + 1];
    static {
        for (int z = ZOOMLEVEL_MIN; z <= ZOOMLEVEL_MAX; z++) {
            NUMBER_OF_TILES[z] = 1 << z;
            NUMBER_OF_PIXELS[z] = TILE_SIZE * 1 << z;
        }
    }

    public final static TileCache cache = new TileCache();

    private final int tileX;
    private final int tileY;
    private final int zoomLevel;
    private final Viewport viewPort;

    public Tile(Geopoint origin, int zoomlevel) {
        this(calcX(origin, clippedZoomlevel(zoomlevel)), calcY(origin, clippedZoomlevel(zoomlevel)), clippedZoomlevel(zoomlevel));
    }

    private Tile(int tileX, int tileY, int zoomlevel) {

        this.zoomLevel = clippedZoomlevel(zoomlevel);

        this.tileX = tileX;
        this.tileY = tileY;

        viewPort = new Viewport(getCoord(new UTFGridPosition(0, 0)), getCoord(new UTFGridPosition(63, 63)));
    }

    public int getZoomLevel() {
        return zoomLevel;
    }

    private static int clippedZoomlevel(int zoomlevel) {
        return Math.max(Math.min(zoomlevel, ZOOMLEVEL_MAX), ZOOMLEVEL_MIN);
    }

    /**
     * Calculate the tile for a Geopoint based on the Spherical Mercator.
     *
     * @see <a
     *      href="http://developers.cloudmade.com/projects/tiles/examples/convert-coordinates-to-tile-numbers">Cloudmade</a>
     */
    private static int calcX(final Geopoint origin, final int zoomlevel) {
        // The cut of the fractional part instead of rounding to the nearest integer is intentional and part of the algorithm
        return (int) ((origin.getLongitude() + 180.0) / 360.0 * NUMBER_OF_TILES[zoomlevel]);
    }

    /**
     * Calculate the tile for a Geopoint based on the Spherical Mercator.
     *
     */
    private static int calcY(final Geopoint origin, final int zoomlevel) {
        // Optimization from Bing
        double sinLatRad = Math.sin(Math.toRadians(origin.getLatitude()));
        // The cut of the fractional part instead of rounding to the nearest integer is intentional and part of the algorithm
        return (int) ((0.5 - Math.log((1 + sinLatRad) / (1 - sinLatRad)) / (4 * Math.PI)) * NUMBER_OF_TILES[zoomlevel]);
    }

    public int getX() {
        return tileX;
    }

    public int getY() {
        return tileY;
    }

    /**
     * Calculate latitude/longitude for a given x/y position in this tile.
     *
     * @see <a
     *      href="http://developers.cloudmade.com/projects/tiles/examples/convert-coordinates-to-tile-numbers">Cloudmade</a>
     */
    @NonNull
    public Geopoint getCoord(UTFGridPosition pos) {

        double pixX = tileX * TILE_SIZE + pos.x * 4;
        double pixY = tileY * TILE_SIZE + pos.y * 4;

        double lonDeg = ((360.0 * pixX) / NUMBER_OF_PIXELS[this.zoomLevel]) - 180.0;
        double latRad = Math.atan(Math.sinh(Math.PI * (1 - 2 * pixY / NUMBER_OF_PIXELS[this.zoomLevel])));
        return new Geopoint(Math.toDegrees(latRad), lonDeg);
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "(%d/%d), zoom=%d", tileX, tileY, zoomLevel);
    }

    /**
     * Calculates the maximum possible zoom level where the supplied points
     * are covered by at least by the supplied number of
     * adjacent tiles on the east/west axis.
     * This criterion can be exactly met for even numbers of tiles
     * while it may result in one more tile as requested for odd numbers
     * of tiles.
     *
     * The order of the points (left/right) is irrelevant.
     *
     * @param left
     *            First point
     * @param right
     *            Second point
     * @return
     */
    public static int calcZoomLon(final Geopoint left, final Geopoint right, final int numberOfTiles) {

        int zoom = (int) Math.floor(
                Math.log(360.0 * numberOfTiles / (2.0 * Math.abs(left.getLongitude() - right.getLongitude())))
                        / Math.log(2)
        );

        Tile tileLeft = new Tile(left, zoom);
        Tile tileRight = new Tile(right, zoom);

        if (Math.abs(tileLeft.tileX - tileRight.tileX) < (numberOfTiles - 1)) {
            zoom += 1;
        }

        return Math.min(zoom, ZOOMLEVEL_MAX);
    }

    /**
     * Calculates the maximum possible zoom level where the supplied points
     * are covered by at least by the supplied number of
     * adjacent tiles on the north/south axis.
     * This criterion can be exactly met for even numbers of tiles
     * while it may result in one more tile as requested for odd numbers
     * of tiles.
     *
     * The order of the points (bottom/top) is irrelevant.
     *
     * @param bottom
     *            First point
     * @param top
     *            Second point
     * @return
     */
    public static int calcZoomLat(final Geopoint bottom, final Geopoint top, final int numberOfTiles) {

        int zoom = (int) Math.ceil(
                Math.log(2.0 * Math.PI * numberOfTiles / (
                        Math.abs(
                                asinh(tanGrad(bottom.getLatitude()))
                                        - asinh(tanGrad(top.getLatitude()))
                                ) * 2.0)
                        ) / Math.log(2)
                );

        Tile tileBottom = new Tile(bottom, zoom);
        Tile tileTop = new Tile(top, zoom);

        if (Math.abs(tileBottom.tileY - tileTop.tileY) > (numberOfTiles - 1)) {
            zoom -= 1;
        }

        return Math.min(zoom, ZOOMLEVEL_MAX);
    }

    private static double tanGrad(double angleGrad) {
        return Math.tan(angleGrad / 180.0 * Math.PI);
    }

    /**
     * Calculates the inverted hyperbolic sine
     * (after Bronstein, Semendjajew: Taschenbuch der Mathematik)
     *
     * @param x
     * @return
     */
    private static double asinh(double x) {
        return Math.log(x + Math.sqrt(x * x + 1.0));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tile)) {
            return false;
        }
        return (this.tileX == ((Tile) o).tileX)
                && (this.tileY == ((Tile) o).tileY)
                && (this.zoomLevel == ((Tile) o).zoomLevel);
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    /** Request JSON informations for a tile. Return as soon as the request has been made, before the answer has been
     * read.
     *
     * @return An observable with one element, which may be <tt>null</tt>.
     */
    public static Observable<String> requestMapInfo(final String url, final Parameters params, final String referer) {
        final HttpResponse response = Network.getRequest(url, params, new Parameters("Referer", referer));
        return Async.start(new Func0<String>() {
            @Override
            public String call() {
                return Network.getResponseData(response);
            }
        }, RxUtils.networkScheduler);
    }

    /** Request .png image for a tile. Return as soon as the request has been made, before the answer has been
     * read and processed.
     *
     * @return An observable with one element, which may be <tt>null</tt>.
     */
    public static Observable<Bitmap> requestMapTile(final Parameters params) {
        final HttpResponse response = Network.getRequest(GCConstants.URL_MAP_TILE, params, new Parameters("Referer", GCConstants.URL_LIVE_MAP));
        return Async.start(new Func0<Bitmap>() {
            @Override
            public Bitmap call() {
                try {
                    return response != null ? BitmapFactory.decodeStream(response.getEntity().getContent()) : null;
                } catch (IOException e) {
                    Log.e("Tile.requestMapTile() ", e);
                    return null;
                }
            }
        }, RxUtils.computationScheduler);
    }

    public boolean containsPoint(final @NonNull ICoordinates point) {
        return viewPort.contains(point);
    }

    public Viewport getViewport() {
        return viewPort;
    }

    /**
     * Calculate needed tiles for the given viewport to cover it with
     * max 2x2 tiles
     *
     * @param viewport
     * @return
     */
    protected static Set<Tile> getTilesForViewport(final Viewport viewport) {
        return getTilesForViewport(viewport, 2, Tile.ZOOMLEVEL_MIN);
    }

    /**
     * Calculate needed tiles for the given viewport.
     * You can define the minimum number of tiles on the longer axis
     * and/or the minimum zoom level.
     *
     * @param viewport
     * @param tilesOnAxis
     * @param minZoom
     * @return
     */
    protected static Set<Tile> getTilesForViewport(final Viewport viewport, final int tilesOnAxis, final int minZoom) {
        Set<Tile> tiles = new HashSet<>();
        int zoom = Math.max(
                Math.min(Tile.calcZoomLon(viewport.bottomLeft, viewport.topRight, tilesOnAxis),
                        Tile.calcZoomLat(viewport.bottomLeft, viewport.topRight, tilesOnAxis)),
                minZoom);

        Tile tileBottomLeft = new Tile(viewport.bottomLeft, zoom);
        Tile tileTopRight = new Tile(viewport.topRight, zoom);

        int xLow = Math.min(tileBottomLeft.getX(), tileTopRight.getX());
        int xHigh = Math.max(tileBottomLeft.getX(), tileTopRight.getX());

        int yLow = Math.min(tileBottomLeft.getY(), tileTopRight.getY());
        int yHigh = Math.max(tileBottomLeft.getY(), tileTopRight.getY());

        for (int xNum = xLow; xNum <= xHigh; xNum++) {
            for (int yNum = yLow; yNum <= yHigh; yNum++) {
                tiles.add(new Tile(xNum, yNum, zoom));
            }
        }

        return tiles;
    }

    public static class TileCache extends LeastRecentlyUsedSet<Tile> {

        private static final long serialVersionUID = -1942301031192719547L;

        public TileCache() {
            super(64);
        }

        public void removeFromTileCache(@NonNull final ICoordinates point) {
            for (final Tile tile : new ArrayList<>(this)) {
                if (tile.containsPoint(point)) {
                    remove(tile);
                }
            }
        }
    }
}
