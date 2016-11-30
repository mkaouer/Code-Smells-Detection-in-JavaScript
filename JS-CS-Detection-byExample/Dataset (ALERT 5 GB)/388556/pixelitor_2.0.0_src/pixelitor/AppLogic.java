/*
 * Copyright 2009-2014 Laszlo Balazs-Csiki
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

package pixelitor;

import pixelitor.layers.Layer;
import pixelitor.layers.LayerChangeListener;
import pixelitor.tools.Symmetry;
import pixelitor.utils.GUIUtils;
import pixelitor.utils.ImageUtils;
import pixelitor.utils.debug.AppNode;

import javax.swing.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Static methods to support global application logic
 */
public final class AppLogic {
    private static final Collection<LayerChangeListener> layerChangeListeners = new ArrayList<>();
//    private static int numFramesOpen = 0;

    private AppLogic() {
    }

    public static void activeCompositionDimensionsChanged(Composition comp) {
        Symmetry.setCompositionSize(comp.getCanvasWidth(), comp.getCanvasHeight());
    }

    public static void setStatusMessage(String msg) {
        PixelitorWindow.getInstance().setStatusBarMessage(msg);
    }

    public static void addLayerChangeListener(LayerChangeListener listener) {
        layerChangeListeners.add(listener);
    }

    public static void activeCompLayerCountChanged(Composition comp, int newLayerCount) {
        for (LayerChangeListener listener : layerChangeListeners) {
            listener.activeCompLayerCountChanged(comp, newLayerCount);
        }
    }

    public static void activeLayerChanged(Layer newActiveLayer) {
        for (LayerChangeListener listener : layerChangeListeners) {
            listener.activeLayerChanged(newActiveLayer);
        }
    }

    public static void layerOrderChanged(Composition comp) {
        for (LayerChangeListener listener : layerChangeListeners) {
            listener.layerOrderChanged(comp);
        }
    }

    public static void showDebugAppDialog() {
        final AppNode node = new AppNode();
        String title = "Pixelitor Debug";

        JTree tree = new JTree(node);
        String text = node.toDetailedString();

        GUIUtils.showTextDialog(tree, title, text);
    }

    @SuppressWarnings("WeakerAccess")
    public static void debugImage(BufferedImage img) {
        debugImage(img, "Debug");
    }

    @SuppressWarnings("WeakerAccess")
    public static void debugImage(BufferedImage img, String description) {
        Composition save = ImageComponents.getActiveComp();

        BufferedImage copy = ImageUtils.copyImage(img);
        PixelitorWindow.getInstance().addNewImage(copy, null, description);

        if (save != null) {
            ImageComponents.setActiveImageComponent(save.getIC(), true);
        }
    }

    public static void debugRaster(Raster raster) {
        ColorModel colorModel = new DirectColorModel(
                ColorSpace.getInstance(ColorSpace.CS_sRGB),
                32,
                0x00ff0000,// Red
                0x0000ff00,// Green
                0x000000ff,// Blue
                0xff000000,// Alpha
                true,       // Alpha Premultiplied
                DataBuffer.TYPE_INT
        );

        Raster correctlyTranslated = raster.createChild(raster.getMinX(), raster.getMinY(), raster.getWidth(), raster.getHeight(), 0, 0, null);
        BufferedImage debugImage = new BufferedImage(colorModel, (WritableRaster) correctlyTranslated, true, null);
        AppLogic.debugImage(debugImage);
    }

    public static void debugRasterWithEmptySpace(Raster raster) {
        BufferedImage debugImage = new BufferedImage(raster.getMinX() + raster.getWidth(), raster.getMinY() + raster.getHeight(), BufferedImage.TYPE_4BYTE_ABGR_PRE);
        debugImage.setData(raster);
        AppLogic.debugImage(debugImage);
    }
}

