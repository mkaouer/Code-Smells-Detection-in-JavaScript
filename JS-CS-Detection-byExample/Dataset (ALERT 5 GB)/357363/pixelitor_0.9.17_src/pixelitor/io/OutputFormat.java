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
package pixelitor.io;

import pixelitor.Composition;
import pixelitor.menus.RecentFilesMenu;
import pixelitor.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 *
 */
public enum OutputFormat {
    JPG {
        @Override
        public void doSaveComposition(Composition comp, File file) {
            BufferedImage img = comp.getCompositeImage();
            BufferedImage finalImage = ImageUtils.convertToRGB(img, false); // no alpha support, convert first to RGB
            OpenSaveManager.saveImageToFile(file, finalImage, toString());
        }
    }, PNG {
        @Override
        public void doSaveComposition(Composition comp, File file) {
            BufferedImage finalImage = comp.getCompositeImage(); // the format supports alpha, no need to convert ARGB to RGB
            OpenSaveManager.saveImageToFile(file, finalImage, toString());
        }
    }, GIF {
        @Override
        public void doSaveComposition(Composition comp, File file) {
            BufferedImage finalImage = comp.getCompositeImage(); // the format supports alpha, no need to convert ARGB to RGB
            OpenSaveManager.saveImageToFile(file, finalImage, toString());
        }
    }, BMP {
        @Override
        public void doSaveComposition(Composition comp, File file) {
            BufferedImage compositeImage = comp.getCompositeImage();
            BufferedImage finalImage = ImageUtils.convertToRGB(compositeImage, false); // no alpha support, convert first to RGB
            OpenSaveManager.saveImageToFile(file, finalImage, toString());
        }
    }, PXC {
        @Override
        public void doSaveComposition(Composition comp, File file) {
            OpenSaveManager.serializeComposition(comp, file);
        }
    };

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    public static OutputFormat valueFromFile(File file) {
        String fileName = file.getName();
        String extension = FileExtensionUtils.getFileExtension(fileName);
        return valueFromExtension(extension);
    }

    public static OutputFormat valueFromExtension(String extension) {
        String lce = extension.toLowerCase();
        if (lce.equals("jpg") || lce.equals("jpeg")) {
            return JPG;
        } else if (lce.equals("png")) {
            return PNG;
        } else if (lce.equals("bmp")) {
            return BMP;
        } else if (lce.equals("gif")) {
            return GIF;
        } else if (lce.equals("pxc")) {
            return PXC;
        } else {
            throw new IllegalArgumentException("extension = " + extension);
        }
    }

    public void saveComposition(Composition comp, File file) {
        doSaveComposition(comp, file);

        comp.setDirty(false);
        comp.setFile(file);
        RecentFilesMenu.getInstance().addFile(file);
    }

    abstract void doSaveComposition(Composition comp, File file);

    private static OutputFormat lastOutputFormat = JPG;

    public static OutputFormat getLastOutputFormat() {
        return lastOutputFormat;
    }

    public static void setLastOutputFormat(OutputFormat lastOutputFormat) {
        OutputFormat.lastOutputFormat = lastOutputFormat;
    }
}
