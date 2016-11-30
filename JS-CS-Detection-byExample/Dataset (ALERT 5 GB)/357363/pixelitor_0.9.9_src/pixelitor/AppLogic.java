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

import pixelitor.history.History;
import pixelitor.io.OpenSaveManager;
import pixelitor.layers.Layer;
import pixelitor.layers.LayerChangeListener;
import pixelitor.menus.CropMenuItem;
import pixelitor.menus.ZoomMenu;
import pixelitor.operations.comp.CompOperations;
import pixelitor.operations.gui.ResizePanel;
import pixelitor.tools.FgBgColorSelector;
import pixelitor.tools.Tool;
import pixelitor.utils.AppPreferences;
import pixelitor.utils.GUIUtils;
import pixelitor.utils.ImageSwitchListener;
import pixelitor.utils.MarchingAntsSelection;
import pixelitor.utils.OKCancelDialog;
import pixelitor.utils.debug.AppNode;

import javax.swing.*;
import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class AppLogic {
    private static Collection<ImageSwitchListener> imageSwitchListeners = new ArrayList<ImageSwitchListener>();
    private static Collection<LayerChangeListener> layerChangeListeners = new ArrayList<LayerChangeListener>();
    private static int numFramesOpen = 0;
    private static List<ImageComponent> imageComponents = new ArrayList<ImageComponent>();


    /**
     * Utility class with static methods, do not instantiate
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
        for (ImageSwitchListener listener : imageSwitchListeners) {
            listener.noOpenImageAnymore();
        }
        PixelitorWindow.getInstance().setActiveImageComponent(null, false);
        History.allImagesAreClosed();
    }

    public static void activeImageHasChanged(ImageComponent ic) {
        PixelitorWindow.getInstance().setActiveImageComponent(ic, false);
        Composition comp = ic.getComp();
        for (ImageSwitchListener listener : imageSwitchListeners) {
            listener.activeImageHasChanged(comp);
        }
        CropMenuItem.INSTANCE.setEnabled((Tool.getCurrentTool() == Tool.CROP_SELECTION) && ic.getComp().hasSelection());
        ZoomMenu.INSTANCE.zoomChanged(ic.getZoomLevel());
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

            ImageComponent activeComponent = getActiveImageComponent();

            for (ImageComponent component : imageComponents) {
                if (component == activeComponent) {
                    activeFound = true;
                }
            }
            if (!activeFound) {
                PixelitorWindow.getInstance().setActiveImageComponent(imageComponents.get(0), true);
            }
        }
    }

    public static ImageComponent getActiveImageComponent() {
        return PixelitorWindow.getInstance().getActiveImageComponent();
    }

    public static Composition getActiveComp() {
        ImageComponent ic = getActiveImageComponent();
        if (ic != null) {
            return ic.getComp();
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
        Tool.setCurrentTool(currentTool);
//        setStatusMessage(currentTool.getName() + " tool selected");
        setStatusMessage(currentTool.getName() + " Tool: " + currentTool.getToolMessage());
    }

    public static int getNrOfOpenImages() {
        return imageComponents.size();
    }

    public static BufferedImage getActiveCompositeImage() {
        Composition comp = getActiveComp();
        if (comp != null) {
            return comp.getCompositeImage("AppLogic.getActiveCompositeImage");
        }

        return null;
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


    public static void cropActiveImage() {
        try {
            Composition comp = getActiveComp();
            MarchingAntsSelection selection = comp.getSelection();
            if (selection != null) {
                CompOperations.cropImage(comp, selection);
            }
        } catch (Exception ex) {
            GUIUtils.showExceptionDialog(ex);
        }
    }

    public static void resizeActiveImage() {
        try {
            Composition comp = getActiveComp();
            ResizePanel.showInDialog(comp);
        } catch (Exception ex) {
            GUIUtils.showExceptionDialog(ex);
        }
    }

    public static void showDebugAppDialog() {
        final AppNode node = new AppNode();
        String title = "Pixelitor Debug";


        JTree tree = new JTree(node);

        new OKCancelDialog(tree, PixelitorWindow.getInstance(), title,
                "Copy as Text to the Clipboard", "Close", true) {
            @Override
            protected void dialogAccepted() {   // "Copy as Text to Clipboard"
                String text = node.toDetailedString();
                StringSelection stringSelection = new StringSelection(text);

                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, new ClipboardOwner() {
                    @Override
                    public void lostOwnership(Clipboard clipboard, Transferable contents) {
                        //do nothing
                    }
                });
            }

            @Override
            protected void dialogCancelled() {   // "Close"
                dispose();
            }
        };
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

    public static void setLF(String className, JFrame parent, boolean updateCompTree) {
        if(updateCompTree) {
            if (parent == null) {
                throw new IllegalArgumentException("parent is null, while updateCompTree is true");
            }
        }

        try {
            UIManager.setLookAndFeel(className);
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        } catch (InstantiationException e1) {
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        } catch (UnsupportedLookAndFeelException e1) {
            e1.printStackTrace();
        }
        if (updateCompTree) {
            SwingUtilities.updateComponentTreeUI(parent);
        }
        String shortName = lfNameFromClass(className);

        FgBgColorSelector.INSTANCE.setLF(shortName);
        InternalImageFrame.setLF(shortName);
        OpenSaveManager.setNewLF();
    }

    private static String lfNameFromClass(String className) {
        int lastIndex = className.lastIndexOf(".");
        if (lastIndex == -1) {
            return null;
        }
        return className.substring(lastIndex + 1, className.length() - 11);
    }

    public static void debugImage(BufferedImage img) {
        PixelitorWindow.getInstance().addNewImage(img, null, "Debug");
    }

    /**
     * When a new tool is activated, the cursor has to be changed for each image
     */
    public static void setToolCursor(Cursor cursor) {
        for (ImageComponent ic : imageComponents) {
            ic.setCursor(cursor);
        }
    }
}

