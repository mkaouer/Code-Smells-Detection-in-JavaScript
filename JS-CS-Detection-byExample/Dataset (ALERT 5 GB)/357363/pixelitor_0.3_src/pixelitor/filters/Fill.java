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
package pixelitor.filters;

import java.awt.Color;
import java.awt.image.BufferedImage;

import pixelitor.FgBgColorSelector;
import pixelitor.ImageChangeReason;

/**
 *
 */
public class Fill extends AbstractOperation {
    private Method method;

    public enum Method {
        FG {
            @Override
            public String getName() {
                return "Fill with Foreground";
            }
            @Override
        public Color getColor() {
            return FgBgColorSelector.getFgColor();
        }},
        BG {
            @Override
            public String getName() {
                return "Fill with Background";
            }
            @Override
        public Color getColor() {
            return FgBgColorSelector.getBgColor();
        }},
        BLACK {
            @Override
            public String getName() {
                return "Fill with Black";
            }
            @Override
        public Color getColor() {
            return Color.black;
        }},
        WHITE {
            @Override
            public String getName() {
                return "Fill with White";
            }
            @Override
        public Color getColor() {
            return Color.white;
        }},
        GRAY {
            @Override
            public String getName() {
                return "Fill with Gray";
            }
            @Override
        public Color getColor() {
            return Color.GRAY;
        }};

        public abstract Color getColor();
        public abstract String getName();
    }

    public Fill(Method method) {
        super(method.getName());
        this.method = method;
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest, ImageChangeReason changeReason) {
        Color c = method.getColor();
        final int red = c.getRed();
        final int green = c.getGreen();
        final int blue = c.getBlue();
        RGBPixelOp pixelOp = new RGBPixelOp() {
            @Override
            public int changeRGB(int a, int r, int g, int b) {
                return ( (red << 16) | (green << 8) | blue);
            }
        };

        return Operations.runRGBPixelOp(pixelOp, src, dest);
    }
}
