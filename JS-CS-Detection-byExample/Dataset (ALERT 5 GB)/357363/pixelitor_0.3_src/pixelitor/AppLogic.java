/*
 * Copyright 2009 László Balázs-Csíki
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

import java.awt.DisplayMode;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

import pixelitor.menus.CropMenuItem;
import pixelitor.tools.Tool;
import pixelitor.utils.DebugUtils;
import pixelitor.utils.ImageChangeListener;
import pixelitor.utils.ImageChangedEvent;
import pixelitor.layers.DeleteActiveLayerAction;
import pixelitor.layers.LayerChangeListener;
import pixelitor.layers.Layer;

public final class AppLogic {
    private static Collection<ImageChangeListener> imageChangeListeners = new ArrayList<ImageChangeListener>();
    private static Collection<LayerChangeListener> layerChangeListeners = new ArrayList<LayerChangeListener>();
    private static int numFramesOpen = 0;
    private static List<ImageComponent> imageComponents = new ArrayList<ImageComponent>();

    // this is a utility class with static methods, it should not be instantiated
    private AppLogic() {
    }

    public static String getDebugInfo() {
        StringBuilder sb = new StringBuilder();
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        DisplayMode displayMode = device.getDisplayMode();

        int width = displayMode.getWidth();
        int height = displayMode.getHeight();
        int bitDepth = displayMode.getBitDepth();
        sb.append("display mode = ").append(width).append("x").append(height).append(", bit depth = ").append(bitDepth);

        GraphicsConfiguration configuration = device.getDefaultConfiguration();
        ColorModel defaultColorModel = configuration.getColorModel();
        sb.append("\ndefault color model = ").append(DebugUtils.getColorModelDescription(defaultColorModel, 1));

        if (imageComponents.size() == 0) {
            sb.append("\nno open images");
            return sb.toString();
        }
        sb.append("\n");
        for (ImageComponent imageComponent : imageComponents) {
            sb.append("\n");
            if(getActiveImageComponent() == imageComponent) {
                sb.append("ACTIVE ");
            }
            sb.append(imageComponent.toString());
        }
        return sb.toString();
    }

    public static String getLayerDebugInfo() {
        if(imageComponents.size() == 0) {
            return "no open images";
        }
        ImageComponent ic = getActiveImageComponent();
        return ic.getLayersDebugInfo();
    }

    public static void addImageComponent(ImageComponent imageComponent) {
        imageComponents.add(imageComponent);
    }

    public static void addImageChangeListener(ImageChangeListener listener) {
        imageChangeListeners.add(listener);
    }

    public static boolean thereAreUnsavedChanges() {
        for (ImageComponent p : imageComponents) {
            if (p.isDirty()) {
                return true;
            }
        }
        return false;
    }

    private static void allImagesAreClosed() {
        for (ImageChangeListener listener : imageChangeListeners) {
            listener.noOpenImageAnymore();
        }
    }

    public static void activeImageHasChanged(ImageComponent imageComponent) {
        PixelitorWindow.getInstance().setActiveImageComponent(imageComponent);
        for (ImageChangeListener listener : imageChangeListeners) {
            listener.activeImageHasChanged(imageComponent);
        }
        CropMenuItem.INSTANCE.setEnabled((Tool.getCurrentTool() == Tool.CROP_SELECTION) && Tool.CROP_SELECTION.hasSelection());
    }


    public static void imageContentChanged(ImageComponent ic, ImageChangeReason changeReason) {
        for (ImageChangeListener listener : imageChangeListeners) {
            listener.imageContentChanged(new ImageChangedEvent(ic, changeReason));
        }
    }

    public static void imageClosed(ImageComponent imageComponent) {
        imageComponents.remove(imageComponent);
        numFramesOpen--;
        if (numFramesOpen == 0) {
            allImagesAreClosed();
        }
        setNewImageAsActiveIfNecessary();
    }

    public static void newImageOpened() {
        numFramesOpen++;
        for (ImageChangeListener listener : AppLogic.imageChangeListeners) {
            listener.newImageOpened();
        }

    }

    public static List<ImageComponent> getImageComponents() {
        return imageComponents;
    }

    private static void setNewImageAsActiveIfNecessary() {
        if(imageComponents.size() > 0) {
            boolean activeFound = false;

            ImageComponent activeComponent = getActiveImageComponent();

            for (ImageComponent component : imageComponents) {
                if(component == activeComponent) {
                     activeFound = true;
                }
            }
            if(!activeFound) {
                setActiveImageComponent(imageComponents.get(0));
            }
        }
    }

    public static ImageComponent getActiveImageComponent() {
        return PixelitorWindow.getInstance().getActiveImageComponent();
    }

    public static void setActiveImageComponent(ImageComponent comp) {
        PixelitorWindow.getInstance().setActiveImageComponent(comp);
    }

    public static BufferedImage getActiveLayerImage() {
        return getActiveImageComponent().getImageForActiveLayer();
    }

    public static void setStatusMessage(String msg) {
        PixelitorWindow.getInstance().setStatusBarMessage(msg);
    }

    public static void setCurrentTool(Tool currentTool) {
        Tool.setCurrentTool(currentTool);
        setStatusMessage(currentTool.getName() + " tool selected");
    }

    private static int untitledCount = 1;
    // from paste or
    public static void addUntitledImage(BufferedImage img) {
        PixelitorWindow.getInstance().addImage(img, "Untitled" + untitledCount);
        untitledCount++;
    }

    public static int getNrOfOpenImages() {
        return imageComponents.size();
    }

    public static void addLayerChangeListener(LayerChangeListener listener) {
        layerChangeListeners.add(listener);
    }

    public static void layerCountChanged(int newLayerCount) {
        for (LayerChangeListener listener : layerChangeListeners) {
            listener.layerCountChanged(newLayerCount);
        }

    }

    public static void activeLayerChanged(Layer newActiveLayer) {
        for (LayerChangeListener listener : layerChangeListeners) {
            listener.activeLayerChanged(newActiveLayer);
        }
    }
}

