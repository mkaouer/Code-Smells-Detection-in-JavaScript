package cgeo.geocaching.maps.mapsforge.v024;

import cgeo.geocaching.maps.CachesOverlay;
import cgeo.geocaching.maps.interfaces.ItemizedOverlayImpl;
import cgeo.geocaching.maps.interfaces.MapProjectionImpl;
import cgeo.geocaching.maps.interfaces.MapViewImpl;

import org.mapsforge.android.mapsold.ItemizedOverlay;
import org.mapsforge.android.mapsold.Projection;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MapsforgeCacheOverlay extends ItemizedOverlay<MapsforgeCacheOverlayItem> implements ItemizedOverlayImpl {

    private CachesOverlay base;
    private Lock lock = new ReentrantLock();

    public MapsforgeCacheOverlay(Context contextIn, Drawable markerIn) {
        super(boundCenterBottom(markerIn));
        base = new CachesOverlay(this, contextIn);
    }

    @Override
    public CachesOverlay getBase() {
        return base;
    }

    @Override
    protected MapsforgeCacheOverlayItem createItem(int i) {
        if (base == null) {
            return null;
        }

        return (MapsforgeCacheOverlayItem) base.createItem(i);
    }

    @Override
    public int size() {
        if (base == null) {
            return 0;
        }

        return base.size();
    }

    @Override
    protected boolean onTap(int arg0) {
        if (base == null) {
            return false;
        }

        return base.onTap(arg0);
    }

    @Override
    protected void drawOverlayBitmap(Canvas canvas, Point drawPosition,
            Projection projection, byte drawZoomLevel) {
        base.drawOverlayBitmap(canvas, drawPosition, new MapsforgeMapProjection(projection), drawZoomLevel);
    }

    @Override
    public void superPopulate() {
        populate();
    }

    @Override
    public Drawable superBoundCenterBottom(Drawable marker) {
        return ItemizedOverlay.boundCenterBottom(marker);
    }

    @Override
    public void superSetLastFocusedItemIndex(int i) {
        // nothing to do
    }

    @Override
    public boolean superOnTap(int index) {
        return super.onTap(index);
    }

    @Override
    public void superDraw(Canvas canvas, MapViewImpl mapView, boolean shadow) {
        // nothing to do here...
    }

    @Override
    public void superDrawOverlayBitmap(Canvas canvas, Point drawPosition,
            MapProjectionImpl projection, byte drawZoomLevel) {
        super.drawOverlayBitmap(canvas, drawPosition, (Projection) projection.getImpl(), drawZoomLevel);
    }

    @Override
    public void lock() {
        lock.lock();
    }

    @Override
    public void unlock() {
        lock.unlock();
    }

    @Override
    public MapViewImpl getMapViewImpl() {
        return (MapViewImpl) internalMapView;
    }

}
