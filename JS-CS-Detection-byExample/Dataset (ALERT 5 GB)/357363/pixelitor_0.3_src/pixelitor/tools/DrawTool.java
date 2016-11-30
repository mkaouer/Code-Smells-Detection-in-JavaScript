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

import java.awt.Graphics2D;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.*;

import pixelitor.AppLogic;
import pixelitor.FgBgColorSelector;
import pixelitor.ImageChangeReason;
import pixelitor.filters.LinearIntParam;
import pixelitor.utils.SliderSpinner;

/**
 *
 */
public class DrawTool extends Tool {
    private Graphics2D g;
    private LinearIntParam brushSizeParam = new LinearIntParam("Brush Size", 1, 100, 10);
    private SliderSpinner brushSizeSpinner = new SliderSpinner(brushSizeParam, null, false, SliderSpinner.TextPosition.LABEL);

//    private float opacity = 0.5f;

    DrawTool() {
    }

    @Override
    public void mousePressed(MouseEvent e, JComponent c) {
        drawTo(e.getX(), e.getY(), c);
    }

    @Override
    public void mouseDragged(MouseEvent e, JComponent c) {
        drawTo(e.getX(), e.getY(), c);
    }

    @Override
    public void mouseReleased(MouseEvent e, JComponent c) {
//        AppLogic.getActiveImageComponent().flattenDrawingTmpLayer(AlphaComposite.getInstance(AlphaComposite.SRC_OUT, opacity));
        g.dispose();
        g = null;
        // for the histogram update:
//        AppLogic.getActiveImageComponent().setImage(null, ImageChangeReason.PAINT_TOOL);
    }

    private void drawTo(int x, int y, JComponent c) {
        if (g == null) {
            BufferedImage image = AppLogic.getActiveLayerImage();
//            ImageComponent imageComp = AppLogic.getActiveImageComponent();
//            BufferedImage image = imageComp.createDrawingTmpLayer();
            g = image.createGraphics();
            g.setColor(FgBgColorSelector.getFgColor());
//            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        }
        int brushSize = brushSizeParam.getValue();
        int halfBrushSize = brushSize / 2;
        int drawX = x - halfBrushSize;
        int drawY = y - halfBrushSize;
        g.fillOval(drawX, drawY, brushSize, brushSize);
//        c.repaint();
//        c.repaint(drawX, drawY, brushSize, brushSize);
        c.paintImmediately(drawX, drawY, brushSize, brushSize);
    }


    @Override
    public String getName() {
        return "draw";
    }

    @Override
    public void initSettingsPanel(JPanel p) {
       p.add(brushSizeSpinner);
    }

//    public void setOpacity(float opacity) {
//        if(opacity < 0f || opacity > 1.0) {
//            throw new IllegalArgumentException("opacity = " + opacity);
//        }
//        this.opacity = opacity;
//    }

}