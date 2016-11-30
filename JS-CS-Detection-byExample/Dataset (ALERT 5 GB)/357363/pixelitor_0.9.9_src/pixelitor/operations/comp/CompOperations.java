/*
 * Copyright 2010 László Balázs-Csíki
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
package pixelitor.operations.comp;

import pixelitor.Composition;
import pixelitor.ImageComponent;
import pixelitor.history.OneLayerUndoable;
import pixelitor.layers.ImageLayer;
import pixelitor.utils.GUIUtils;
import pixelitor.utils.ImageUtils;
import pixelitor.utils.MarchingAntsSelection;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 *
 */
public class CompOperations {

    /**
     * Utility class with static methods, do not instantiate
     */
    private CompOperations() {
    }

    public static void cropImage(Composition comp, MarchingAntsSelection selection) {
        Rectangle selectionBounds = selection.getSelectionShape().getBounds();

        OneLayerUndoable.backup(comp, "Crop");
        int nrLayers = comp.getNrLayers();

        for (int i = 0; i < nrLayers; i++) {
            ImageLayer layer = (ImageLayer) comp.getLayer(i);

            BufferedImage img = layer.getBufferedImage();
            int transX = layer.getTranslationX();
            int transY = layer.getTranslationY();

            int cropX = selectionBounds.x - transX;
            int cropY = selectionBounds.y - transY;
            int cropWidth = selectionBounds.width;
            int cropHeight = selectionBounds.height;
            BufferedImage dest = GUIUtils.crop(img, cropX, cropY, cropWidth, cropHeight);
            layer.setBufferedImage(dest);
            layer.setTranslationX(0);
            layer.setTranslationY(0);
        }

        comp.updateCanvasSize(selectionBounds.width, selectionBounds.height);
        comp.setDirty(true);
        comp.imageChanged(true, true);

        ImageComponent ic = comp.getIC();

        comp.deselect();

        ic.setPreferredSize(new Dimension(selectionBounds.width, selectionBounds.height));
        ic.revalidate();
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

        Rectangle canvasBounds = comp.getCanvasBounds();

        OneLayerUndoable.backup(comp, "Resize");

        int nrLayers = comp.getNrLayers();
        for (int i = 0; i < nrLayers; i++) {
            ImageLayer layer = (ImageLayer) comp.getLayer(i);
            BufferedImage img = layer.getBufferedImage();

            Rectangle layerBounds = layer.getBounds();
            // the layer size can be bigger than the canvas size, and it can have a negative
            // translation value
            boolean bigLayer = !canvasBounds.contains(layerBounds);
            int resizeWidth = targetWidth;
            int resizeHeight = targetHeight;

            double horizontalResizeRatio = 1.0;
            double verticalResizeRatio = 1.0;
            if (bigLayer) {
                horizontalResizeRatio = ((double) targetWidth) / comp.getCanvasWidth();
                verticalResizeRatio = ((double) targetHeight) / comp.getCanvasHeight();
                resizeWidth = (int) (img.getWidth() * horizontalResizeRatio);
                resizeHeight = (int) (img.getHeight() * verticalResizeRatio);
            }

            BufferedImage resizedImg = ImageUtils.getFasterScaledInstance(img, resizeWidth, resizeHeight, RenderingHints.VALUE_INTERPOLATION_BICUBIC, progressiveBilinear);
            layer.setBufferedImage(resizedImg);

            if (bigLayer) {
                layer.setTranslationX((int) (layer.getTranslationX() * horizontalResizeRatio));
                layer.setTranslationY((int) (layer.getTranslationY() * verticalResizeRatio));
            }
        }

        comp.updateCanvasSize(targetWidth, targetHeight);
        comp.imageChanged(false, false);

//        int width = comp.getCompositeImage().getWidth();
//        int height = comp.getCompositeImage().getHeight();
    }
}