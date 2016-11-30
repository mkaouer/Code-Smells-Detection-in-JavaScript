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

import pixelitor.utils.ImageUtils;

import java.awt.image.BufferedImage;

/**
 *
 */
public enum OutputFormat {
    JPG {
        @Override
        public boolean supportsAlpha() {
            return false;
        }
    }, PNG {
        @Override
        public boolean supportsAlpha() {
            return true;
        }
    }, GIF {
        @Override
        public boolean supportsAlpha() {
            return true;
        }
    }, BMP {
        @Override
        public boolean supportsAlpha() {
            return false;
        }
    };

    public abstract boolean supportsAlpha();

    public BufferedImage getFinalImage(BufferedImage src) {
        if (supportsAlpha()) {
            return src;
        }
        return ImageUtils.convertToRGB(src);
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
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
        } else {
            throw new IllegalArgumentException("extension = " + extension);
        }
    }
}
