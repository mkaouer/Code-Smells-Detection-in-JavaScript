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

package pixelitor;

import pixelitor.filters.comp.CompositionUtils;
import pixelitor.filters.gui.ResizePanel;
import pixelitor.history.History;
import pixelitor.layers.Layer;
import pixelitor.layers.LayerChangeListener;
import pixelitor.menus.SelectionActions;
import pixelitor.menus.ZoomMenu;
import pixelitor.selection.Selection;
import pixelitor.tools.Symmetry;
import pixelitor.tools.Tool;
import pixelitor.tools.Tools;
import pixelitor.utils.AppPreferences;
import pixelitor.utils.ImageSwitchListener;
import pixelitor.utils.ImageUtils;
import pixelitor.utils.OKCancelDialog;
import pixelitor.utils.Utils;
import pixelitor.utils.debug.AppNode;

import javax.swing.*;
import java.awt.Cursor;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class AppLogic {
    private static Collection<ImageSwitchListener> imageSwitchListeners = new ArrayList<ImageSwitchListener>();
    private static Collection<LayerChangeListener> layerChangeListeners = new ArrayList<LayerChangeListener>();
    private static int numFramesOpen = 0;
    private static List<ImageComponent> imageComponents = new ArrayList<ImageComponent>();
    public static ImageComponent activeImageComponent;

    /**
     * Utility class with static methods
     */
    private AppLogic() {
    }

    public static void addImageComponent(ImageComponent imageComponent) {
        imageComponents.add(imageComponent);
    }

    public static void addImageChangeListener(ImageSwitchListener listener) {
        imageSwitchListeners.add(listener);
    }

    private static boolean thereAreUnsavedChanges() {
        for (ImageComponent p : imageComponents) {
            if (p.getComp().isDirty()) {
                return true;
            }
        }
        return false;
    }

    private static void allImagesAreClosed() {
        setActiveImageComponent(null, false);
        for (ImageSwitchListener listener : imageSwitchListeners) {
            listener.noOpenImageAnymore();
        }
        History.allImagesAreClosed();
        SelectionActions.setEnabled(false);

        PixelitorWindow.getInstance().setTitle(Build.getPixelitorWindowFixTitle());
    }

    public static void activeImageHasChanged(ImageComponent ic) {
        setActiveImageComponent(ic, false);
        Composition comp = ic.getComp();
        for (ImageSwitchListener listener : imageSwitchListeners) {
            listener.activeCompositionHasChanged(comp);
        }

        Layer layer = comp.getActiveLayer();
        activeLayerChanged(layer);

        SelectionActions.setEnabled(comp.hasSelection());
        ZoomMenu.INSTANCE.zoomChanged(ic.getZoomLevel());

        activeCompositionDimensionsChanged(comp);
        PixelitorWindow.getInstance().setTitle(ic.getComp().getName() + " - " + Build.getPixelitorWindowFixTitle());
    }

    public static void activeCompositionDimensionsChanged(Composition comp) {
        Symmetry.setCompositionSize(comp.getCanvasWidth(), comp.getCanvasHeight());
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
        for (ImageSwitchListener listener : imageSwitchListeners) {
            listener.newImageOpened();
        }
    }

    public static List<ImageComponent> getImageComponents() {
        return imageComponents;
    }

    private static void setNewImageAsActiveIfNecessary() {
        if (!imageComponents.isEmpty()) {
            boolean activeFound = false;

            for (ImageComponent component : imageComponents) {
                if (component == activeImageComponent) {
                    activeFound = true;
                    break;
                }
            }
            if (!activeFound) {
                setActiveImageComponent(imageComponents.get(0), true);
            }
        }
    }

    public static ImageComponent getActiveImageComponent() {
        return activeImageComponent;
    }

    public static Composition getActiveComp() {
        if (activeImageComponent != null) {
            return activeImageComponent.getComp();
        }
        return null;
    }

    public static Layer getActiveLayer() {
        return getActiveComp().getActiveLayer();
    }

    public static void setStatusMessage(String msg) {
        PixelitorWindow.getInstance().setStatusBarMessage(msg);
    }

    public static void setCurrentTool(Tool currentTool) {
        Tools.setCurrentTool(currentTool);
        setStatusMessage(currentTool.getName() + " Tool: " + currentTool.getToolMessage());
    }

    public static int getNrOfOpenImages() {
        return imageComponents.size();
    }

    public static BufferedImage getActiveCompositeImage() {
        Composition comp = getActiveComp();
        if (comp != null) {
            return comp.getCompositeImage();
        }

        return null;
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

    public static void cropActiveImage() {
        try {
            Composition comp = getActiveComp();
            Selection selection = comp.getSelection();
            if (selection != null) {
                CompositionUtils.cropImage(comp, selection);
            }
        } catch (Exception ex) {
            ExceptionHandler.showExceptionDialog(ex);
        }
    }

    public static void resizeActiveImage() {
        try {
            Composition comp = getActiveComp();
            ResizePanel.showInDialog(comp);
        } catch (Exception ex) {
            ExceptionHandler.showExceptionDialog(ex);
        }
    }

    public static void showDebugAppDialog() {
        final AppNode node = new AppNode();
        String title = "Pixelitor Debug";

        JTree tree = new JTree(node);

        OKCancelDialog d = new OKCancelDialog(tree, PixelitorWindow.getInstance(), title,
                "Copy as Text to the Clipboard", "Close") {
            @Override
            protected void dialogAccepted() {   // "Copy as Text to Clipboard"
                super.dialogAccepted();

                String text = node.toDetailedString();
                Utils.copyStringToClipboard(text);
            }

            @Override
            protected void dialogCancelled() {   // "Close"
                super.dialogCancelled();
                dispose();
            }
        };
        d.setVisible(true);
    }

    public static void exitApp() {
        if (thereAreUnsavedChanges()) {
            int answer = JOptionPane.showConfirmDialog(null, "There are unsaved changes. Are you sure you want to exit?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (answer == JOptionPane.YES_OPTION) {
                savePrefsAndExit();
            }
        } else {
            savePrefsAndExit();
        }
    }

    private static void savePrefsAndExit() {
        AppPreferences.savePreferencesBeforeExit();
        System.exit(0);
    }

    /**
     * When a new tool is activated, the cursor has to be changed for each image
     */
    public static void setToolCursor(Cursor cursor) {
        for (ImageComponent ic : imageComponents) {
            ic.setCursor(cursor);
        }
    }

    @SuppressWarnings({"WeakerAccess"})
    public static void debugImage(BufferedImage img) {
        debugImage(img, "Debug");
    }

    @SuppressWarnings({"WeakerAccess"})
    public static void debugImage(BufferedImage img, String description) {
        BufferedImage copy = ImageUtils.copyImage(img);
        PixelitorWindow.getInstance().addNewImage(copy, null, description);
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

    public static void setActiveImageComponent(ImageComponent newActiveComponent, boolean activate) {
        activeImageComponent = newActiveComponent;
        if (activate) {
            if (newActiveComponent == null) {
                throw new IllegalStateException("imageComponent is null");
            }
            InternalImageFrame internalFrame = activeImageComponent.getInternalFrame();
            PixelitorWindow.getInstance().activateInternalImageFrame(internalFrame);
            newActiveComponent.onActivation();
        }
    }
}

