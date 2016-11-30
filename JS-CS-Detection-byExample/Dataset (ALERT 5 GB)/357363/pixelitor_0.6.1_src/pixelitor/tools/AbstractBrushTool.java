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
    private RangeParam brushRadiusParam = new RangeParam("Brush Radius", 1, 100, 5);
    private JCheckBox mirrorCB = new JCheckBox();

    private int translateX = 0;
    private int translateY = 0;
    private int imageWidth = 0;

    protected Brush brush = new Brush(brushRadiusParam);

    private int previousMouseX = 0;
    private int previousMouseY = 0;

    protected AbstractBrushTool() {

    }

    @Override
    public void mousePressed(MouseEvent e, ImageComponent ic) {
        drawTo(e.getX(), e.getY(), ic);
    }

    @Override
    public void mouseDragged(MouseEvent e, ImageComponent ic) {
        drawTo(e.getX(), e.getY(), ic);
    }

    @Override
    public void mouseReleased(MouseEvent e, ImageComponent ic) {
//        AppLogic.getActiveImageComponent().flattenDrawingTmpLayer(AlphaComposite.getInstance(AlphaComposite.SRC_OUT, opacity));
        g.dispose();
        g = null;
        // for the histogram update:
//        AppLogic.getActiveImageComponent().setImage(null, ImageChangeReason.PAINT_TOOL);


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
            saveImageForUndo();

            drawPoint(ic, x, y, diameter);
        } else {
            drawLine(ic, previousMouseX, previousMouseY, x, y, diameter);
        }
        previousMouseX = x;
        previousMouseY = y;

    }

    private void drawPoint(ImageComponent ic, int drawX, int drawY, int diameter) {
        brush.drawTo(g, drawX - translateX, drawY - translateY);
//        g.fillOval(drawX - translateX, drawY - translateY, diameter, diameter);
        ic.paintImmediately(drawX, drawY, diameter, diameter);
        if (mirrorCB.isSelected()) {
            drawX = imageWidth - drawX;
//            g.fillOval(drawX - translateX, drawY - translateY, diameter, diameter);

            brush.drawTo(g, drawX - translateX, drawY - translateY);
            ic.paintImmediately(drawX, drawY, diameter, diameter);
        }
    }

    private void drawLine(ImageComponent ic, int startX, int startY, int endX, int endY, int thickness) {
//        g.setStroke(new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        //g.drawLine(startX, startY, endX, endY);
        brush.drawLine(g, startX, startY, endX, endY);
        ic.updateRegion(startX, startY, endX, endY, thickness);

        if (mirrorCB.isSelected()) {
            startX = imageWidth - startX;
            endX = imageWidth - endX;

            //g.drawLine(startX, startY, endX, endY);
            brush.drawLine(g, startX, startY, endX, endY);
            ic.updateRegion(startX, startY, endX, endY, thickness);
        }
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

    // TODO - this not used because the black icons do not look nice on dark images - better icons are needed
    private Cursor calculateDrawingCursor() {
        ImageIcon icon = ImageUtils.loadIcon(getIconFileName());
        Image img = icon.getImage();
        Cursor customCursor = Toolkit.getDefaultToolkit().createCustomCursor(img, new Point(0, 0), "draw");
        return customCursor;
    }

    @Override
    protected void toolStarted() {
//        AppLogic.getActiveImageComponent().setCursor(calculateDrawingCursor());
    }

    @Override
    protected void toolEnded() {
//         AppLogic.getActiveImageComponent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
}
