/*
 * Copyright 2010 Laszlo Balazs-Csiki
 *
 * This file is part of Pixelitor. Pixelitor is free software: you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License, version 3 as published by the Free
 * Software Foundation.
 *
 * Pixelitor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Pixelitor.  If not, see <http://www.gnu.org/licenses/>.
 */
package pixelitor.filters.comp;

import pixelitor.AppLogic;
import pixelitor.Composition;
import pixelitor.ImageComponent;
import pixelitor.history.OneLayerUndoableEdit;
import pixelitor.layers.Layer;

import java.awt.Dimension;
import java.awt.Rectangle;

/**
 *
 */
public final class CompositionUtils {

    /**
     * Utility class with static methods
     */
    private CompositionUtils() {
    }

    public static void cropImage(Composition comp, Rectangle selectionBounds, boolean selection) {

        if (selectionBounds.width == 0 || selectionBounds.height == 0) {
            // TODO maybe a warning to the user?
            return;
        }

        OneLayerUndoableEdit.createAndAddToHistory(comp, "Crop", false, true);
        if(selection) {
            comp.deselect(false);
        }

        int nrLayers = comp.getNrLayers();

        for (int i = 0; i < nrLayers; i++) {
            Layer layer = comp.getLayer(i);
            layer.crop(selectionBounds);
        }

        comp.getCanvas().updateSize(selectionBounds.width, selectionBounds.height);
        comp.setDirty(true);
        comp.imageChanged(true, true);

        ImageComponent ic = comp.getIC();

        ic.setPreferredSize(new Dimension(selectionBounds.width, selectionBounds.height));
        ic.revalidate();
        ic.makeSureItIsVisible();

        AppLogic.activeCompositionDimensionsChanged(comp);
    }

    /**
     * Resizes the composition
     *
     * @param comp
     * @param targetWidth
     * @param targetHeight
     * @param resizeInBox  if true, resizes an image so that the proportions are kept and the result fits into the given dimensions
     */
    public static void resize(Composition comp, int targetWidth, int targetHeight, boolean resizeInBox) {
        int actualWidth = comp.getCanvasWidth();
        int actualHeight = comp.getCanvasHeight();

        if ((actualWidth == targetWidth) && (actualHeight == targetHeight)) {
            return;
        }

        if (resizeInBox) {
            int maxWidth = targetWidth;
            int maxHeight = targetHeight;

            double heightScale = maxHeight / (double) actualHeight;
            double widthScale = maxWidth / (double) actualWidth;
            double scale = Math.min(heightScale, widthScale);

            targetWidth = (int) (scale * (double) actualWidth);
            targetHeight = (int) (scale * (double) actualHeight);
        }

        boolean progressiveBilinear = false;
        if ((targetWidth < (actualWidth / 2)) || (targetHeight < (actualHeight / 2))) {
            progressiveBilinear = true;
        }

        OneLayerUndoableEdit.createAndAddToHistory(comp, "Resize", false, true);
        comp.deselect(false);

        int nrLayers = comp.getNrLayers();
        for (int i = 0; i < nrLayers; i++) {
            Layer layer = comp.getLayer(i);
            layer.resize(targetWidth, targetHeight, progressiveBilinear);
        }

        comp.getCanvas().updateSize(targetWidth, targetHeight);
        comp.imageChanged(false, false);

        AppLogic.activeCompositionDimensionsChanged(comp);
    }

}