/*
 * Copyright 2009-2010 L�szl� Bal�zs-Cs�ki
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

import pixelitor.AppLogic;
import pixelitor.FgBgColorSelector;
import pixelitor.ImageComponent;

import javax.swing.*;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 *
 */
public class ColorPickerTool extends Tool {
    public ColorPickerTool() {
        super('i', "Color Picker", "color_picker_tool_icon.gif");
    }

    @Override
    public void mouseClicked(MouseEvent e, ImageComponent ic) {
        sampleColor(e, ic);
    }

    private void sampleColor(MouseEvent e, ImageComponent ic) {
        BufferedImage img = ic.getCompositeImage();
        int imgWidth = img.getWidth();
        int imgHeight = img.getHeight();

        double scale = ic.getViewScale();
        int x = (int) (e.getX() / scale);
        int y = (int) (e.getY() / scale);

        if (x < imgWidth && y < imgHeight && x >= 0 && y >= 0) {
            int rgb = img.getRGB(x, y);

            showColorInStatusBar(x, y, rgb);

            Color sampledColor = new Color(rgb);
            if (e.isAltDown()) {
                FgBgColorSelector.INSTANCE.setBgColor(sampledColor);
            } else {
                FgBgColorSelector.INSTANCE.setFgColor(sampledColor);
            }
        }
    }

    private void showColorInStatusBar(int x, int y, int rgb) {
        int a = (rgb >>> 24) & 0xFF;
        int r = (rgb >>> 16) & 0xFF;
        int g = (rgb >>> 8) & 0xFF;
        int b = (rgb) & 0xFF;

        StringBuilder sb = new StringBuilder().append("x = ").append(x).append(", y = ").append(y).append(", alpha = ").append(a).append(", red = ").append(r).append(", green = ").append(g).append(", blue = ").append(b);
        String msg = sb.toString();
        AppLogic.setStatusMessage(msg);
    }

    @Override
    public void mouseDragged(MouseEvent e, ImageComponent ic) {
        sampleColor(e, ic);
    }

    @Override
    void initSettingsPanel(JPanel p) {

    }

    @Override
    public char getActivationKeyChar() {
        return 'i';
    }

    @Override
    protected void toolStarted() {
        ImageComponent ic = AppLogic.getActiveImageComponent();
        if (ic != null) {
            ic.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        }
    }

    @Override
    protected void toolEnded() {
        ImageComponent ic = AppLogic.getActiveImageComponent();
        if (ic != null) {
            ic.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }
}
