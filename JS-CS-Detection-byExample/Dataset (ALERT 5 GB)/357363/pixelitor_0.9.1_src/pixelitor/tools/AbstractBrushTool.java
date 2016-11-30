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

import pixelitor.ImageComponent;
import pixelitor.layers.Layer;
import pixelitor.operations.gui.RangeParam;
import pixelitor.utils.GUIUtils;
import pixelitor.utils.ImageUtils;
import pixelitor.utils.SliderSpinner;

import javax.swing.*;
import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 *
 */
public abstract class AbstractBrushTool extends Tool {
    protected Graphics2D g;
    private RangeParam brushRadiusParam = new RangeParam("Brush Radius", 1, 100, 10);
    private JCheckBox mirrorCB = new JCheckBox();

    private int translateX = 0;
    private int translateY = 0;
    private int imageWidth = 0;

    protected Brush brush = new Brush(brushRadiusParam);
    protected boolean useFillOval = true;

    private int previousMouseX = 0;
    private int previousMouseY = 0;

    protected AbstractBrushTool(char activationKeyChar, String name, String iconFileName) {
        super(activationKeyChar, name, iconFileName);
    }

    @Override
    public void mousePressed(MouseEvent e, ImageComponent ic) {
        double scale = ic.getViewScale();
        int x = (int) (e.getX() / scale);
        int y = (int) (e.getY() / scale);
        drawTo(x, y, ic);
    }

    @Override
    public void mouseDragged(MouseEvent e, ImageComponent ic) {
        double scale = ic.getViewScale();
        int x = (int) (e.getX() / scale);
        int y = (int) (e.getY() / scale);
        drawTo(x, y, ic);
    }

    @Override
    public void mouseReleased(MouseEvent e, ImageComponent ic) {
        finishBrushStroke();
    }

    private void finishBrushStroke() {
        if(g != null) {
            g.dispose();
        }
        g = null;
        // for the histogram update:
//        AppLogic.getActiveImageComponent().setImage(null, ImageChangeReason.PAINT_TOOL);
    }

    /**
     * Draws a brush stroke programmatically.
     */
    public void drawBrushStroke(ImageComponent ic, Point startingPoint, Point endPoint) {
//        g = null;

        int startX = startingPoint.x;
        int startY = startingPoint.y;
        int endX = endPoint.x;
        int endY = endPoint.y;

        drawTo(startX, startY, ic);
        drawTo(endX, endY, ic);
        finishBrushStroke();
    }

    protected void initDrawingGraphics(Layer layer) {
        BufferedImage image = layer.getBufferedImage();
        g = image.createGraphics();
    }

    private void drawTo(int x, int y, ImageComponent ic) {
        int brushRadius = brushRadiusParam.getValue();
//        int drawX = x - brushRadius;
//        int drawY = y - brushRadius;
        int diameter = 2 * brushRadius;

        if (g == null) {
            Layer layer = ic.getActiveLayer();
            BufferedImage image = layer.getBufferedImage();
            initDrawingGraphics(layer);
            setupGraphics(g);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            translateX = layer.getTranslationX();
            translateY = layer.getTranslationY();
            imageWidth = image.getWidth();
            saveImageForUndo(ic);

            drawPoint(ic, x, y, diameter);
            if(mirrorCB.isSelected()) {
                drawPoint(ic, imageWidth - x, y, diameter);
            }
        } else {
            drawLine(ic, previousMouseX, previousMouseY, x, y, diameter);
            if(mirrorCB.isSelected()) {
                drawLine(ic, imageWidth - previousMouseX, previousMouseY, imageWidth - x, y, diameter);
            }
        }

        previousMouseX = x;
        previousMouseY = y;

    }

    private void drawPoint(ImageComponent ic, int x, int y, int diameter) {
        int radius = diameter / 2;
        int drawX = x - translateX - radius;
        int drawY = y - translateY - radius;
        if(useFillOval) {
            g.fillOval(drawX, drawY, diameter, diameter);
        } else {
            brush.drawTo(g, drawX, drawY);
        }
//        ic.paintImmediately(drawX, drawY, diameter, diameter);
        ic.updateRegion(drawX, drawY, drawX + diameter, drawY + diameter, 0);
    }

    private void drawLine(ImageComponent ic, int startX, int startY, int endX, int endY, int thickness) {
        int drawStartX = startX - translateX;
        int drawStartY = startY - translateY;
        int drawEndX = endX - translateX;
        int drawEndY = endY - translateY;
        if(useFillOval) {
            g.setStroke(new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(drawStartX, drawStartY, drawEndX, drawEndY);
        } else {
            brush.drawLine(g, drawStartX, drawStartY, drawEndX, drawEndY);
        }
        ic.updateRegion(startX, startY, endX, endY, thickness);
    }

    protected abstract void setupGraphics(Graphics2D g);

    private void initKeyboardShortcuts(JComponent c) {
        Action increaseBrushSizeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                brushRadiusParam.increaseValue();
            }
        };
        GUIUtils.registerGlobalShortcut(c, KeyStroke.getKeyStroke(']'), "increment", increaseBrushSizeAction);

        Action decreaseBrushSizeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                brushRadiusParam.decreaseValue();
            }
        };
        GUIUtils.registerGlobalShortcut(c, KeyStroke.getKeyStroke('['), "decrement", decreaseBrushSizeAction);
    }

    @Override
    public void initSettingsPanel(JPanel p) {
        initKeyboardShortcuts(p);

        SliderSpinner brushSizeSpinner = new SliderSpinner(brushRadiusParam, false, SliderSpinner.TextPosition.WEST);
        p.add(brushSizeSpinner);

        p.add(new JLabel("Mirrored Paint:"));
        p.add(mirrorCB);
    }

//    // TODO - this not used because the black icons do not look nice on dark images - better icons are needed
//    private Cursor calculateDrawingCursor() {
//        ImageIcon icon = ImageUtils.loadIcon(getIconFileName());
//        Image img = icon.getImage();
//        Cursor customCursor = Toolkit.getDefaultToolkit().createCustomCursor(img, new Point(0, 0), "draw");
//        return customCursor;
//    }
//
//    @Override
//    protected void toolStarted() {
////        AppLogic.getActiveImageComponent().setCursor(calculateDrawingCursor());
//    }
//
//    @Override
//    protected void toolEnded() {
////         AppLogic.getActiveImageComponent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
//    }
}
