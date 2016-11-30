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
package pixelitor.io;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

/**
 *
 */
public class FileExtensionUtils {
    public static final String[] SUPPORTED_INPUT_FORMATS = {"jpg", "jpeg", "png", "gif", "bmp", "pxc"};
    public static final String[] SUPPORTED_OUTPUT_FORMATS = SUPPORTED_INPUT_FORMATS;

    /**
     * Utility class with static methods
     */
    private FileExtensionUtils() {
    }

    private static final FileFilter jpegFilter = new FileNameExtensionFilter("JPEG files", "jpg", "jpeg");
    private static final FileFilter pngFilter = new FileNameExtensionFilter("PNG files", "png");
    private static final FileFilter bmpFilter = new FileNameExtensionFilter("BMP files", "bmp");
    private static final FileFilter gifFilter = new FileNameExtensionFilter("GIF files", "gif");
    private static final FileFilter pxcFilter = new FileNameExtensionFilter("PXC files", "pxc");

    public static void addOpenFilters(JFileChooser chooser) {
        chooser.addChoosableFileFilter(pngFilter);
        chooser.addChoosableFileFilter(bmpFilter);
        chooser.addChoosableFileFilter(gifFilter);
        chooser.addChoosableFileFilter(pxcFilter);
        chooser.addChoosableFileFilter(jpegFilter);
    }

    public static void addSaveFilters(JFileChooser chooser) {
        addOpenFilters(chooser);
    }

    public static void setupJPegSaveFilter(JFileChooser chooser) {
        chooser.removeChoosableFileFilter(pngFilter);
        chooser.removeChoosableFileFilter(bmpFilter);
        chooser.removeChoosableFileFilter(gifFilter);
        chooser.removeChoosableFileFilter(pxcFilter);

        chooser.setFileFilter(jpegFilter);
    }

    public static FileFilter getFileFilterForExtension(String ext) {
        if(ext == null) {
            return jpegFilter; // default
        } else {
            ext = ext.toLowerCase();
        }
        switch (ext) {
            case "jpg":
                return jpegFilter;
            case "jpeg":
                return jpegFilter;
            case "png":
                return pngFilter;
            case "bmp":
                return bmpFilter;
            case "gif":
                return gifFilter;
            case "pxc":
                return pxcFilter;
        }
        return jpegFilter; // default
    }

    public static String getFileExtension(String fileName) {
        int lastIndex = fileName.lastIndexOf('.');
        if (lastIndex == -1) {
            return null;
        }
        return fileName.substring(lastIndex + 1, fileName.length());
    }

    public static String getFileNameWOExtension(String fileName) {
        int lastIndex = fileName.lastIndexOf('.');
        if (lastIndex == -1) {
            return fileName;
        }
        return fileName.substring(0, lastIndex);
    }

    public static boolean isSupportedExtension(String fileName, String[] supportedExtensions) {
        String extension = getFileExtension(fileName);
        if (extension == null) {
            return false;
        }
        extension = extension.toLowerCase();
        for (String supportedExtension : supportedExtensions) {
            if (extension.equals(supportedExtension)) {
                return true;
            }
        }
        return false;
    }

    public static File[] getAllSupportedFilesInDir(File dir) {
        java.io.FileFilter imageFilter = new java.io.FileFilter() {
            @Override
            public boolean accept(File f) {
                return isSupportedExtension(f.getName(), SUPPORTED_INPUT_FORMATS);
            }
        };
        File[] files = dir.listFiles(imageFilter);
        return files;
    }

    public static String replaceExtension(String inputFileName, String newExtension) {
        String inputExtension = getFileExtension(inputFileName);
        if (inputExtension == null) {
            return inputFileName + '.' + newExtension;
        }
        String woExtension = getFileNameWOExtension(inputFileName);
        return woExtension + '.' + newExtension;
    }
}