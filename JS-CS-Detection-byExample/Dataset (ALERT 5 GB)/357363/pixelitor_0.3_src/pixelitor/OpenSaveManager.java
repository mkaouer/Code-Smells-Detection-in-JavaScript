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

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import pixelitor.menus.RecentFilesMenu;
import pixelitor.utils.AppPreferences;
import pixelitor.utils.CustomFileChooser;
import pixelitor.utils.GUIUtils;
import pixelitor.utils.ImagePreviewPanel;
import pixelitor.utils.Utils;

public class OpenSaveManager {
    private static JFileChooser openFileChooser;
    private static JFileChooser saveFileChooser;
    public static final String[] SUPPORTED_INPUT_FORMATS = new String[]{"jpg", "jpeg", "png", "gif", "bmp"};
    public static final String[] SUPPORTED_OUTOUT_FORMATS = SUPPORTED_INPUT_FORMATS;

    private static FileFilter jpegFilter = new FileNameExtensionFilter("JPEG files", "jpg", "jpeg");
    private static FileFilter pngFilter = new FileNameExtensionFilter("PNG files", "png");
    private static FileFilter bmpFilter = new FileNameExtensionFilter("BMP files", "bmp");
    private static FileFilter gifFilter = new FileNameExtensionFilter("GIF files", "gif");

    private static String lastOpenDir = null;
    private static String lastSaveDir = null;

    private OpenSaveManager() {} // should not be instantiated

    public static synchronized void initOpenFileChooser() {
        if (openFileChooser == null) {
            lastOpenDir = AppPreferences.loadLastOpenDir();
            openFileChooser = new JFileChooser(lastOpenDir);
            openFileChooser.addChoosableFileFilter(pngFilter);
            openFileChooser.addChoosableFileFilter(bmpFilter);
            openFileChooser.addChoosableFileFilter(gifFilter);
            openFileChooser.addChoosableFileFilter(jpegFilter);

            ImagePreviewPanel preview = new ImagePreviewPanel();
            openFileChooser.setAccessory(preview);
            openFileChooser.addPropertyChangeListener(preview);
        }
    }

    public static synchronized void initSaveFileChooser() {
        if (saveFileChooser == null) {
            lastSaveDir  = AppPreferences.loadLastSaveDir();
            saveFileChooser = new CustomFileChooser(lastSaveDir);

            saveFileChooser.addChoosableFileFilter(pngFilter);
            saveFileChooser.addChoosableFileFilter(bmpFilter);
            saveFileChooser.addChoosableFileFilter(gifFilter);
            saveFileChooser.addChoosableFileFilter(jpegFilter);
        }
    }

    public static void open() {
        initOpenFileChooser();

        int status = openFileChooser.showOpenDialog(PixelitorWindow.getInstance());

        if (status == JFileChooser.APPROVE_OPTION) {
            File selectedFile = openFileChooser.getSelectedFile();
            String fileName = selectedFile.getName();
            lastOpenDir = selectedFile.getParent();
            if(Utils.isSupportedExtension(fileName, SUPPORTED_INPUT_FORMATS)) {
                openFile(selectedFile);
            } else { // unsupported extension
                handleUnsupporedExtensionLoading(fileName);
            }

        } else if (status == JFileChooser.CANCEL_OPTION) {
            // cancelled
        }
    }

    private static void handleUnsupporedExtensionLoading(String fileName) {
        String extension = Utils.getFileExtension(fileName);
        String msg = "Could not load " + fileName + ", because ";
        if(extension == null) {
            msg += "it has no extension.";
        } else {
            msg += "files of type " + extension + " are not supported.";
        }
        JOptionPane.showMessageDialog(PixelitorWindow.getInstance(), msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void openFile(final File selectedFile) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                BufferedImage img = null;
                try {
                    img = ImageIO.read(selectedFile);
                } catch (IOException e1) {
                    GUIUtils.showExceptionDialog(e1);
                }
                if(img == null) {
                    JOptionPane.showMessageDialog(PixelitorWindow.getInstance(), "Could not load " + selectedFile.getName() + " as an image file", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                img = Utils.transformToCompatibleImage(img);
                PixelitorWindow.getInstance().addImage(img, selectedFile.getName());
                RecentFilesMenu.getInstance().fileOpened(selectedFile);
            }
        };
        Utils.executeWithBusyCursor(PixelitorWindow.getInstance(), r);
    }

    public static void save() {
        ImageComponent activeImageComponent = PixelitorWindow.getInstance().getActiveImageComponent();
        save(activeImageComponent);
    }

    public static int save(ImageComponent activeImageComponent) {
        initSaveFileChooser();
        int status = saveFileChooser.showSaveDialog(PixelitorWindow.getInstance());

        if (status == JFileChooser.APPROVE_OPTION) {
            File selectedFile = saveFileChooser.getSelectedFile();
            lastSaveDir = selectedFile.getParent();
            saveFile(selectedFile, activeImageComponent.getCompositeImage());
            activeImageComponent.setDirty(false);
        } else if (status == JFileChooser.CANCEL_OPTION) {
            // save cancelled
        }
        return status;
    }

    public static void saveFile(final File selectedFile, final RenderedImage renderedImage) {
        String extension = Utils.getFileExtension( selectedFile.getName());
        final String extensionLC = extension.toLowerCase();


        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    ImageIO.write(renderedImage, extensionLC, selectedFile);
                } catch (IOException e) {
                    GUIUtils.showExceptionDialog(PixelitorWindow.getInstance(), e);
                }
            }
        };
        Utils.executeWithBusyCursor(PixelitorWindow.getInstance(), r);
    }

    public static void warnAndCloseImage(ImageComponent imageComponent) {
        if (imageComponent.isDirty()) {
            Object[] options = {"Save...",
                    "Don't save",
                    "Cancel"};
            Object message = new JLabel("<html><b>Do you want to save the changes made to " + imageComponent.getName() + "?</b><br>Your changes will be lost if you don't save them.</html>");
            int answer = JOptionPane.showOptionDialog(PixelitorWindow.getInstance(), message,
                    "Unsaved changes", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
            if (answer == JOptionPane.YES_OPTION) { // save
                int status = OpenSaveManager.save(imageComponent);
                if (status == JFileChooser.APPROVE_OPTION) { // file was saved
                    imageComponent.closeContainer();
                }
            } else if (answer == JOptionPane.NO_OPTION) { // don't save
                imageComponent.closeContainer();
            } else if (answer == JOptionPane.CANCEL_OPTION) { // cancel
                // do nothing
            } else {
                // do nothing
            }
        } else {
            imageComponent.closeContainer();
        }

    }

    public static void warnAndCloseAllImages() {
        List<ImageComponent> imageComponents = AppLogic.getImageComponents();
        // make a copy because items will be removed from the original while iterating
        Iterable<ImageComponent> tmpCopy = new ArrayList<ImageComponent>(imageComponents);
        for (ImageComponent component : tmpCopy) {
            warnAndCloseImage(component);
        }
    }


    public static String getLastOpenDir() {
        return lastOpenDir;
    }

    public static String getLastSaveDir() {
        return lastSaveDir;
    }
}
