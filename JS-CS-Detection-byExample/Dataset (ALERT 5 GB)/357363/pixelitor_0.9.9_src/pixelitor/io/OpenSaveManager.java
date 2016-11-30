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

package pixelitor.io;

import pixelitor.AppLogic;
import pixelitor.Composition;
import pixelitor.ImageComponent;
import pixelitor.PixelitorWindow;
import pixelitor.automate.SingleDirChooserPanel;
import pixelitor.layers.ImageLayer;
import pixelitor.layers.Layer;
import pixelitor.menus.RecentFilesMenu;
import pixelitor.utils.AppPreferences;
import pixelitor.utils.CustomFileChooser;
import pixelitor.utils.GUIUtils;
import pixelitor.utils.ImagePreviewPanel;
import pixelitor.utils.ImageUtils;
import pixelitor.utils.Utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class OpenSaveManager {
    private static JFileChooser openFileChooser;
    private static CustomFileChooser saveFileChooser;

    private static File lastOpenDir = AppPreferences.loadLastOpenDir();
    private static File lastSaveDir = AppPreferences.loadLastSaveDir();

    private OpenSaveManager() {
    } // should not be instantiated


    public static void setNewLF() {
        // cannot be cached anymore
        openFileChooser = null;
        saveFileChooser = null;
    }

    public static synchronized void initOpenFileChooser() {
        if (openFileChooser == null) {
            openFileChooser = new JFileChooser(lastOpenDir);

            FileExtensionUtils.addOpenFilters(openFileChooser);

            ImagePreviewPanel preview = new ImagePreviewPanel();
            openFileChooser.setAccessory(preview);
            openFileChooser.addPropertyChangeListener(preview);
        }
    }

    public static synchronized void initSaveFileChooser() {
        if (saveFileChooser == null) {
            saveFileChooser = new CustomFileChooser(lastSaveDir);

            FileExtensionUtils.addSaveFilters(saveFileChooser);
        }
    }

    public static void open() {
        initOpenFileChooser();

        int status = openFileChooser.showOpenDialog(PixelitorWindow.getInstance());

        if (status == JFileChooser.APPROVE_OPTION) {
            File selectedFile = openFileChooser.getSelectedFile();
            String fileName = selectedFile.getName();

            lastOpenDir = selectedFile.getParentFile();

            if (FileExtensionUtils.isSupportedExtension(fileName, FileExtensionUtils.SUPPORTED_INPUT_FORMATS)) {
                openFile(selectedFile);
            } else { // unsupported extension
                handleUnsupportedExtensionLoading(fileName);
            }

        } else if (status == JFileChooser.CANCEL_OPTION) {
            // cancelled
        }
    }

    private static void handleUnsupportedExtensionLoading(String fileName) {
        String extension = FileExtensionUtils.getFileExtension(fileName);
        String msg = "Could not load " + fileName + ", because ";
        if (extension == null) {
            msg += "it has no extension.";
        } else {
            msg += "files of type " + extension + " are not supported.";
        }
        JOptionPane.showMessageDialog(PixelitorWindow.getInstance(), msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void openFile(final File selectedFile) {
        String ext = FileExtensionUtils.getFileExtension(selectedFile.getName());
        if ("pxc".equals(ext)) {
            openComposition(selectedFile);
        } else {
            openOneLayeredFile(selectedFile);
        }
        RecentFilesMenu.getInstance().addFile(selectedFile);
    }

    private static void openOneLayeredFile(final File selectedFile) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                BufferedImage img = null;
                try {
                    img = ImageIO.read(selectedFile);
                } catch (IOException ex) {
                    GUIUtils.showExceptionDialog(ex);
                }
                if (img == null) {
                    JOptionPane.showMessageDialog(PixelitorWindow.getInstance(), "Could not load \"" + selectedFile.getName() + "\" as an image file", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                img = ImageUtils.transformToCompatibleImage(img);
                PixelitorWindow.getInstance().addNewImage(img, selectedFile, null);
            }
        };
        Utils.executeWithBusyCursor(r, false);
    }


    public static void save(boolean saveAs) {
        try {
            Composition comp = AppLogic.getActiveComp();
            save(comp, saveAs);
        } catch (Exception e) {
            GUIUtils.showExceptionDialog(e);
        }
    }

    /**
     * Returns true if the file was saved, false if the user cancels the saving
     */
    private static boolean save(Composition comp, boolean saveAs) {
        boolean needsFileChooser = saveAs || (comp.getFile() == null);
        if (needsFileChooser) {
            initSaveFileChooser();

            String defaultFileName = FileExtensionUtils.getFileNameWOExtension(comp.getName());
            saveFileChooser.setSelectedFile(new File(defaultFileName));
            String defaultExtension = FileExtensionUtils.getFileExtension(comp.getName());
            saveFileChooser.setFileFilter(FileExtensionUtils.getFileFilterForExtension(defaultExtension));

            int status = saveFileChooser.showSaveDialog(PixelitorWindow.getInstance());

            if (status == JFileChooser.APPROVE_OPTION) {
                File selectedFile = saveFileChooser.getSelectedFile();
                lastSaveDir = selectedFile.getParentFile();
                OutputFormat outputFormat = saveFileChooser.getOutputFormat();
                outputFormat.saveComposition(comp, selectedFile);
                return true;
            } else if (status == JFileChooser.CANCEL_OPTION) {
                // save cancelled
                return false;
            }
        } else {
            File file = comp.getFile();
            OutputFormat outputFormat = OutputFormat.valueFromFile(file);
            outputFormat.saveComposition(comp, file);
            return true;
        }
        throw new IllegalStateException("should not get here");
    }

    public static void saveImageToFile(final File selectedFile, final BufferedImage bufferedImage, final String format) {
        if (selectedFile == null) {
            throw new IllegalArgumentException("selectedFile is null");
        }
        if (bufferedImage == null) {
            throw new IllegalArgumentException("bufferedImage is null");
        }
        if (format == null) {
            throw new IllegalArgumentException("format is null");
        }
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    ImageIO.write(bufferedImage, format, selectedFile);
                } catch (IOException e) {
                    GUIUtils.showExceptionDialog(e);
                }
            }
        };
        Utils.executeWithBusyCursor(r, false);
    }

    public static void warnAndCloseImage(ImageComponent imageComponent) {
        try {
            Composition comp = imageComponent.getComp();
            if (comp.isDirty()) {
                Object[] options = {"Save",
                        "Don't save",
                        "Cancel"};
                Object message = new JLabel("<html><b>Do you want to save the changes made to " + comp.getName() + "?</b><br>Your changes will be lost if you don't save them.</html>");
                int answer = JOptionPane.showOptionDialog(PixelitorWindow.getInstance(), message,
                        "Unsaved changes", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
                if (answer == JOptionPane.YES_OPTION) { // save
                    boolean fileSaved = OpenSaveManager.save(comp, false);
                    if (fileSaved) {
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
        } catch (Exception ex) {
            GUIUtils.showExceptionDialog(ex);
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


    public static File getLastOpenDir() {
        return lastOpenDir;
    }

    public static File getLastSaveDir() {
        return lastSaveDir;
    }

    public static void serializeComposition(Composition comp, File f) {
        ObjectOutputStream oos = null;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);

            fos.write(new byte[]{(byte) 0xAB, (byte) 0xC4, 0x02});

            GZIPOutputStream gz = new GZIPOutputStream(fos);
            oos = new ObjectOutputStream(gz);
            oos.writeObject(comp);
            oos.flush();
        } catch (IOException e) {
            GUIUtils.showExceptionDialog(e);
        } finally {
            try {
                oos.flush();
                oos.close();
                fos.close();
            } catch (IOException e) {
                GUIUtils.showExceptionDialog(e);
            }
        }
    }

    private static Composition deserializeComposition(File f) throws NotPxcFormatException {
        Composition comp = null;
        try {
            FileInputStream fis = new FileInputStream(f);

            int firstByte = fis.read();
            int secondByte = fis.read();
            if (firstByte == 0xAB && secondByte == 0xC4) {
                // identification bytes OK
            } else {
                throw new NotPxcFormatException(f.getName() + " is not in the pxc format.");
            }
            int versionByte = fis.read();
            if(versionByte == 0) {
                throw new NotPxcFormatException(f.getName() + " is in an obsolete pxc format, it can only be opened in the old beta Pixelitor versions 0.9.2-0.9.7");
            }
            if(versionByte == 1) {
                throw new NotPxcFormatException(f.getName() + " is in an obsolete pxc format, it can only be opened in the old beta Pixelitor version 0.9.8");
            }
            if (versionByte > 2) {
                throw new NotPxcFormatException(f.getName() + " has unknown version byte " + versionByte);
            }

            GZIPInputStream gs = new GZIPInputStream(fis);
            ObjectInputStream ois = new ObjectInputStream(gs);
            comp = (Composition) ois.readObject();
            ois.close();
            fis.close();
        } catch (IOException e) {
            GUIUtils.showExceptionDialog(e);
        } catch (ClassNotFoundException e) {
            GUIUtils.showExceptionDialog(e);
        }
        return comp;
    }

    private static void openComposition(final File selectedFile) {
        Runnable r = new Runnable() {
            public void run() {
                try {
                    Composition comp = deserializeComposition(selectedFile);
                    PixelitorWindow.getInstance().addDeserializedComposition(comp, selectedFile);
                } catch (NotPxcFormatException e) {
                    GUIUtils.showExceptionDialog(e);
                }
            }
        };
        Utils.executeWithBusyCursor(r, false);
    }

    public static void setLastOpenDir(File lastOpenDir) {
        OpenSaveManager.lastOpenDir = lastOpenDir;
    }

    public static void setLastSaveDir(File lastSaveDir) {
        OpenSaveManager.lastSaveDir = lastSaveDir;
    }

    public static void openAllImagesInDir(File dir) {
        File[] files = FileExtensionUtils.getAllSupportedFilesInDir(dir);
        if (files != null) {
            for (File file : files) {
                openFile(file);
            }
        }
    }

    public static void exportLayersToPNG() {
        boolean okPressed = SingleDirChooserPanel.selectOutputDir(false);
        if (!okPressed) {
            return;
        }

        Composition comp = AppLogic.getActiveComp();
        int nrLayers = comp.getNrLayers();
        for (int i = 0; i < nrLayers; i++) {
            Layer layer = comp.getLayer(i);
            if(layer instanceof ImageLayer) {
                ImageLayer imageLayer = (ImageLayer) layer;
                BufferedImage image = imageLayer.getBufferedImage();

                File outputDir = OpenSaveManager.getLastSaveDir();

                String fileName = String.format("%03d_%s.%s", i, Utils.toFileName(layer.getName()), "png");

                File file = new File(outputDir, fileName);
                saveImageToFile(file, image, "png");
            }
        }
    }

    public static void saveCurrentImageInAllFormats() {
        Composition comp = AppLogic.getActiveComp();

        boolean cancelled = !SingleDirChooserPanel.selectOutputDir(false);
        if (cancelled) {
            return;
        }
        final File saveDir = getLastSaveDir();
        if (saveDir != null) {
            OutputFormat[] outputFormats = OutputFormat.values();
            for (OutputFormat outputFormat : outputFormats) {
                File f = new File(saveDir, "all_formats." + outputFormat.toString());
                outputFormat.saveComposition(comp, f);
            }
        }
    }

    public static void saveAllImagesToDir() {
        boolean cancelled = !SingleDirChooserPanel.selectOutputDir(true);
        if (cancelled) {
            return;
        }

        final OutputFormat outputFormat = OutputFormat.getLastOutputFormat();
        final File saveDir = getLastSaveDir();
        final List<ImageComponent> imageComponents = AppLogic.getImageComponents();

        final ProgressMonitor progressMonitor = new ProgressMonitor(PixelitorWindow.getInstance(),
                "Saving All Images to Directory",
                "", 0, 100);

        SwingWorker worker = new SwingWorker<Void, Void>() {
            public Void doInBackground() {
                for (int i = 0; i < imageComponents.size(); i++) {
                    progressMonitor.setProgress((int) ((float) i * 100 / imageComponents.size()));
                    if (progressMonitor.isCanceled()) {
                        break;
                    }

                    ImageComponent ic = imageComponents.get(i);
                    Composition comp = ic.getComp();
                    String fileName = String.format("%04d_%s.%s", i, Utils.toFileName(comp.getName()), outputFormat.toString());
                    File f = new File(saveDir, fileName);
                    progressMonitor.setNote("Saving " + fileName);
                    outputFormat.saveComposition(comp, f);
                }
                progressMonitor.close();
                return null;
            } // end of doInBackground()
        };
        worker.execute();
    }
}

