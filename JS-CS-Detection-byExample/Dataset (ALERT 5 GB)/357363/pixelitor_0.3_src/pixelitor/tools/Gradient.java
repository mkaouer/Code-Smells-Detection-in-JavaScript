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

package pixelitor.tools;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.image.BufferedImage;

import pixelitor.FgBgColorSelector;
import pixelitor.ImageChangeReason;
import pixelitor.filters.AbstractOperation;

/**
 * An operation that puts a gradient on the image.
 */
public final class Gradient extends AbstractOperation {
    public static final Gradient INSTANCE = new Gradient();

    private Type type;
    private ColorType colorType;
    private MultipleGradientPaint.CycleMethod cycleMethod;
    private Composite composite;

    private float startX = 0;
    private float startY = 0;
    private float endX = 100;
    private float endY = 100;

    public enum Type {
        LINEAR {
            @Override
            public String toString() {
                return "linear";
            }},
        RADIAL {
            @Override
            public String toString() {
                return "radial";
            }
        }
    }

    public enum ColorType {
        FG_TO_BG {
            @Override
            public String toString() {
                return "foreground to background";
            }
            @Override
            public Color getStartColor() {
                if (isInvert()) {
                    return FgBgColorSelector.getBgColor();
                }
                return FgBgColorSelector.getFgColor();
            }
            @Override
            public Color getEndColor() {
                if(isInvert()) {
                    return FgBgColorSelector.getFgColor();
                }
                return FgBgColorSelector.getBgColor();
            }
        },
        BLACK_TO_WHITE {
            @Override
            public String toString() {
                return "black to white";
            }
            @Override
            public Color getStartColor() {
                if (isInvert()) {
                    return Color.WHITE;
                }
                return Color.BLACK;
            }
            @Override
            public Color getEndColor() {
                if (isInvert()) {
                    return Color.BLACK;
                }
                return Color.WHITE;
            }
        };

        public abstract Color getStartColor();

        public abstract Color getEndColor();


        public boolean isInvert() {
            return invert;
        }

        public void setInvert(boolean invert) {
            this.invert = invert;
        }

        private boolean invert;
    }



    private Gradient() {
        super("Gradient");
    }

    public void setColorType(ColorType colorType) {
        this.colorType = colorType;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setCycleMethod(MultipleGradientPaint.CycleMethod cycleMethod) {
        this.cycleMethod = cycleMethod;
    }

    public void setPoints(float startX, float startY, float endX, float endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    public void setComposite(Composite composite) {
        this.composite = composite;
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest, ImageChangeReason changeReason) {
        Graphics2D g = dest.createGraphics();
        int width = dest.getWidth();
        int height = dest.getHeight();


        Paint gradient = null;

        float[] fractions = {0.0f, 1.0f};
        Color[] colors = {colorType.getStartColor(), colorType.getEndColor()};

        if (type == Type.LINEAR) {
            gradient = new LinearGradientPaint(startX, startY, endX, endY, fractions, colors, cycleMethod);
        } else if (type == Type.RADIAL) {
            float distance = (float) Math.sqrt((startX - endX) * (startX - endX) + (startY - endY) * (startY - endY));
            gradient = new RadialGradientPaint(startX, startY, distance, fractions, colors, cycleMethod);
        } else {
            throw new IllegalStateException("should not get here");
        }

        if(composite != null) {
            g.setComposite(composite);
        }
        g.setPaint(gradient);
        g.fillRect(0, 0, width, height);

        g.dispose();
        return dest;
    }
}
