/*
 * Copyright 2009-2010 László Balázs-Csíki
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
package pixelitor.utils.debug;

import pixelitor.ImageComponent;
import pixelitor.InternalImageFrame;
import pixelitor.layers.Layer;

import java.awt.image.BufferedImage;

/**
 * A debugging node for an ImageComponent
 */
public class ImageComponentNode extends DebugNode {
    public ImageComponentNode(String name, ImageComponent ic) {
        super(name, ic);

        addStringChildWithQuotes("name", ic.getName());
        boolean dirty = ic.isDirty();
        addBooleanChild("dirty", dirty);

        int canvasWidth = ic.getCanvasWidth();
        addIntChild("canvasWidth", canvasWidth);
        int canvasHeight = ic.getCanvasHeight();
        addIntChild("canvasHeight", canvasHeight);

        int width = ic.getWidth();
        addIntChild("width", width);
        int height = ic.getHeight();
        addIntChild("height", height);

        InternalImageFrame internalFrame = ic.getInternalFrame();
        int internalFrameWidth = internalFrame.getWidth();
        addIntChild("internalFrameWidth", internalFrameWidth);
        int internalFrameHeight = internalFrame.getHeight();
        addIntChild("internalFrameHeight", internalFrameHeight);


        int nrLayers = ic.getNrLayers();
        addIntChild("nrLayers", nrLayers);

        Layer activeLayer = ic.getActiveLayer();
        for (int i = 0; i < nrLayers; i++) {
            Layer layer = ic.getLayer(i);
            LayerNode node;
            if (layer == activeLayer) {
                node = new LayerNode("ACTIVE Layer - " + layer.getName(), layer);
            } else {
                node = new LayerNode("Layer - " + layer.getName(), layer);
            }

            add(node);
        }

        BufferedImage compositeImage = ic.getCompositeImage();
        BufferedImageNode imageNode = new BufferedImageNode("Composite Image", compositeImage);
        add(imageNode);
    }
}
