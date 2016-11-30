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
package pixelitor.tools;

import pixelitor.FgBgColorSelector;
import pixelitor.ImageComponent;
import pixelitor.layers.Layer;
import pixelitor.utils.BlendingModePanel;

import javax.swing.*;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.Paint;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 *
 */
public class GradientTool extends Tool {
    private boolean thereWasDragging = false;

    private static final String NO_CYCLE_AS_STRING = "No Cycle";
    private static final String REFLECT_AS_STRING = "Reflect";
    private static final String REPEAT_AS_STRING = "Repeat";

    private JComboBox colorTypeSelector;
    private JComboBox typeSelector;
    private JComboBox cycleMethodSelector;
    //    private RangeParam opacityParam;
    private JCheckBox invertCheckBox;
    private BlendingModePanel blendingModePanel;

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
                    return FgBgColorSelector.getBG();
                }
                return FgBgColorSelector.getFG();
            }
            @Override
            public Color getEndColor() {
                if (isInvert()) {
                    return FgBgColorSelector.getFG();
                }
                return FgBgColorSelector.getBG();
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


    GradientTool() {
    }

    @Override
    public void initSettingsPanel(JPanel p) {
        p.add(new JLabel("Gradient Type: "));
        typeSelector = new JComboBox(new Object[]{
                Type.LINEAR,
                Type.RADIAL});
        p.add(typeSelector);

        p.add(new JLabel("Color Type: "));
        colorTypeSelector = new JComboBox(new Object[]{
                ColorType.FG_TO_BG,
                ColorType.BLACK_TO_WHITE});
        p.add(colorTypeSelector);

        p.add(new JLabel("Cycling: "));
        // cycle methods cannot be put directly in the JComboBox, because they would be all uppercase
        cycleMethodSelector = new JComboBox(new String[]{
                NO_CYCLE_AS_STRING,
                REFLECT_AS_STRING,
                REPEAT_AS_STRING});
        p.add(cycleMethodSelector);

//        opacityParam = new RangeParam("opacity", 0, 100, 100);
//        SliderSpinner opacitySlider = new SliderSpinner(opacityParam, null, true, SliderSpinner.TextPosition.WEST);
//        p.add(opacitySlider);

        p.add(new JLabel("Invert: "));
        invertCheckBox = new JCheckBox();
        p.add(invertCheckBox);

        blendingModePanel = new BlendingModePanel(true);
        p.add(blendingModePanel);
    }

    private MultipleGradientPaint.CycleMethod getCycleMethodFromString(String s) {
        if (s.equals(NO_CYCLE_AS_STRING)) {
            return MultipleGradientPaint.CycleMethod.NO_CYCLE;
        } else if (s.equals(REFLECT_AS_STRING)) {
            return MultipleGradientPaint.CycleMethod.REFLECT;
        } else if (s.equals(REPEAT_AS_STRING)) {
            return MultipleGradientPaint.CycleMethod.REPEAT;
        }
        throw new IllegalStateException("should not get here");
    }

    @Override
    public void mouseReleased(MouseEvent e, ImageComponent ic) {
        super.mouseReleased(e, ic);
        if (thereWasDragging) {
            saveImageForUndo();
            drawGradient(ic,
                    (Type) typeSelector.getSelectedItem(),
                    (ColorType) colorTypeSelector.getSelectedItem(),
                    getCycleMethodFromString((String) cycleMethodSelector.getSelectedItem()),
                    blendingModePanel.getComposite(),
                    start,
                    end,
                    invertCheckBox.isSelected()
            );

            thereWasDragging = false;
            ic.imageChanged();
        }
    }

    public static void drawGradient(ImageComponent ic, Type type, ColorType colorType, MultipleGradientPaint.CycleMethod cycleMethod, Composite composite, Point start, Point end, boolean invert) {
        Layer layer = ic.getActiveLayer();

        BufferedImage drawingImage = layer.createTmpDrawingLayer(composite);
        Graphics2D g = drawingImage.createGraphics();

        Paint gradient;
        float[] fractions = {0.0f, 1.0f};
        colorType.setInvert(invert);
        Color[] colors = {colorType.getStartColor(), colorType.getEndColor()};

        if (type == Type.LINEAR) {
            gradient = new LinearGradientPaint(start.x, start.y, end.x, end.y, fractions, colors, cycleMethod);
        } else if (type == Type.RADIAL) {
            float distance = (float) Math.sqrt((start.x - end.x) * (start.x - end.x) + (start.y - end.y) * (start.y - end.y));
            gradient = new RadialGradientPaint(start.x, start.y, distance, fractions, colors, cycleMethod);
        } else {
            throw new IllegalStateException("should not get here");
        }
        g.setPaint(gradient);

        int width = drawingImage.getWidth();
        int height = drawingImage.getHeight();
        g.fillRect(0, 0, width, height);
        g.dispose();
        layer.mergeTmpDrawingLayerDown();

    }

    @Override
    public void mouseClicked(MouseEvent e, ImageComponent ic) {
        super.mouseClicked(e, ic);
        thereWasDragging = false;
    }

    @Override
    public void mouseDragged(MouseEvent e, ImageComponent ic) {
        super.mouseDragged(e, ic);
        thereWasDragging = true;
    }

    @Override
    public String getName() {
        return "Gradient";
    }

    @Override
    public String getIconFileName() {
        return "gradient_tool_icon.png";
    }

    @Override
    public KeyStroke getActivationKeyStroke() {
        return KeyStroke.getKeyStroke('g');
    }
}
