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

import pixelitor.AppLogic;
import pixelitor.Composition;
import pixelitor.GlobalKeyboardWatch;
import pixelitor.ImageComponent;
import pixelitor.layers.ImageLayer;
import pixelitor.layers.Layer;
import pixelitor.operations.gui.RangeParam;
import pixelitor.utils.GUIUtils;
import pixelitor.utils.ImageSwitchListener;
import pixelitor.utils.ImageUtils;
import pixelitor.utils.SliderSpinner;
import pixelitor.utils.Utils;

import javax.swing.*;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 *
 */
public abstract class AbstractBrushTool extends Tool implements ImageSwitchListener {
    private boolean painting = false;

    Graphics2D g;
    private RangeParam brushRadiusParam = new RangeParam("Brush Radius", 1, 100, 10);
    private JCheckBox mirrorCB = new JCheckBox();

    private int translateX = 0;
    private int translateY = 0;
    private int imageWidth = 0;

    Brush brush = BrushType.IDEAL.getBrush();
//    boolean useFillOval = true;

    private int previousMouseX = 0;
    private int previousMouseY = 0;

    private boolean firstMouseDown = true; // for the first one, don't draw lines even if it is a shift-click

    AbstractBrushTool(char activationKeyChar, String name, String iconFileName, String toolMessage) {
        super(activationKeyChar, name, iconFileName, toolMessage, Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        AppLogic.addImageChangeListener(this);
    }

    public void spacePressed() {
        if(!painting) {
            AppLogic.setToolCursor(HAND.getCursor());
        }
    }

    public void spaceReleased() {
        AppLogic.setToolCursor(cursor);
    }

    @Override
    public void mousePressed(MouseEvent e, ImageComponent ic) {
        if(e.isAltDown()) {
            ColorPickerTool.sampleColor(e, ic, false);
            return;
        }
        if(GlobalKeyboardWatch.isSpaceDown()) {
            HAND.mousePressed(e, ic);
            return;
        }

        painting = true;

        double scale = ic.getViewScale();
        int x = (int) (e.getX() / scale);
        int y = (int) (e.getY() / scale);
        boolean withLine = !firstMouseDown && e.isShiftDown();

        Paint p = null;
        int button = e.getButton();

        if(button == MouseEvent.BUTTON3) {
            p = FgBgColorSelector.getBG();
        } else if(button == MouseEvent.BUTTON2) {
            Color fg = FgBgColorSelector.getFG();
            Color bg = FgBgColorSelector.getBG();
            if(e.isControlDown()) { // note that isAltDown is always true for middle-button events, even if Alt is not pressed
                p = ImageUtils.getHSBAverageColor(fg, bg);
            } else {
                p = ImageUtils.getRGBAverageColor(fg, bg);
            }
        } else {
            p = FgBgColorSelector.getFG();
        }

        drawTo(ic.getComp(), p, x, y, withLine);
        firstMouseDown = false;
    }

    @Override
    public void mouseDragged(MouseEvent e, ImageComponent ic) {
        if(e.isAltDown()) {
            ColorPickerTool.sampleColor(e, ic, false);
            return;
        }
        if(GlobalKeyboardWatch.isSpaceDown() && !painting) {
            HAND.mouseDragged(e, ic);
            return;
        }

        painting = true;

        double scale = ic.getViewScale();
        int x = (int) (e.getX() / scale);
        int y = (int) (e.getY() / scale);

        drawTo(ic.getComp(), null, x, y, false);
    }

    @Override
    public void mouseReleased(MouseEvent e, ImageComponent ic) {
        finishBrushStroke(ic.getComp());
        painting = false;
    }

    void finishBrushStroke(Composition comp) {
        if (g != null) {
            g.dispose();
        }
        g = null;

        comp.imageChanged(false, true); // for the histogram update
    }

    public void drawBrushStrokeProgrammatically(Composition comp, Point startingPoint, Point endPoint) {
//        g = null;

        int startX = startingPoint.x;
        int startY = startingPoint.y;
        int endX = endPoint.x;
        int endY = endPoint.y;

        Color c = FgBgColorSelector.getFG();
        drawTo(comp, c, startX, startY, false);
        drawTo(comp, c, endX, endY, false);
        finishBrushStroke(comp);
    }

    /**
     * Creates the global Graphics2D object g. Overridden in BrushTool.
     */
    void initDrawingGraphics(ImageLayer layer) {
        // the default implementation (used in the Erase Tool) uses
        // the graphics of the buffered image contained in the layer
        BufferedImage image = layer.getBufferedImage();
        g = image.createGraphics();
    }

    /**
     * Called from mousePressed, mouseDragged, and drawBrushStrokeProgrammatically
     */
    private void drawTo(Composition comp, Paint p, int x, int y, boolean connectClickWithLine) {
        int brushRadius = brushRadiusParam.getValue();
//        int drawX = x - brushRadius;
//        int drawY = y - brushRadius;
        int diameter = 2 * brushRadius;

        if (g == null) {
            ImageLayer imageLayer = Utils.checkActiveLayerIsImage(comp);

            BufferedImage image = imageLayer.getBufferedImage();
            initDrawingGraphics(imageLayer);

            setupGraphics(g, p);

            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            translateX = imageLayer.getTranslationX();
            translateY = imageLayer.getTranslationY();
            imageWidth = image.getWidth();
            saveImageForUndo(comp);

            if (connectClickWithLine) {
                drawLine(comp, previousMouseX, previousMouseY, x, y, diameter);
            } else {
                drawPoint(comp, x, y, diameter);
            }

            if (mirrorCB.isSelected()) {
                drawPoint(comp, imageWidth - x, y, diameter);
            }
        } else {
            drawLine(comp, previousMouseX, previousMouseY, x, y, diameter);
            if (mirrorCB.isSelected()) {
                drawLine(comp, imageWidth - previousMouseX, previousMouseY, imageWidth - x, y, diameter);
            }
        }

        previousMouseX = x;
        previousMouseY = y;

    }

    private void drawPoint(Composition comp, int x, int y, int diameter) {
        int radius = diameter / 2;
        int drawX = x - translateX - radius;
        int drawY = y - translateY - radius;
//        if (useFillOval) {
//            g.fillOval(drawX, drawY, diameter, diameter);
//        } else {
//            brush.drawPoint(g, drawX, drawY);
//        }

        brush.drawPoint(g, drawX, drawY, diameter);

        comp.updateRegion(drawX, drawY, drawX + diameter, drawY + diameter, 0);
    }

    private void drawLine(Composition comp, int startX, int startY, int endX, int endY, int thickness) {
        int drawStartX = startX - translateX;
        int drawStartY = startY - translateY;
        int drawEndX = endX - translateX;
        int drawEndY = endY - translateY;
//        if (useFillOval) {
////            g.setStroke(new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
//            g.setStroke(new CompositeStroke( new BasicStroke( 10f ), new BasicStroke( 0.5f ) ) );
//
//            g.drawLine(drawStartX, drawStartY, drawEndX, drawEndY);
//        } else {
//            brush.drawLine(g, drawStartX, drawStartY, drawEndX, drawEndY);
//        }
        brush.drawLine(g, drawStartX, drawStartY, drawEndX, drawEndY, thickness);
        comp.updateRegion(startX, startY, endX, endY, thickness);
    }

    protected abstract void setupGraphics(Graphics2D g, Paint p);

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
    void initSettingsPanel(JPanel p) {
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
    @Override
    protected void toolStarted() {
        super.toolStarted();
        firstMouseDown = true;
    }

    @Override
    public void noOpenImageAnymore() {

    }

    @Override
    public void newImageOpened() {
        firstMouseDown = true;
    }

    @Override
    public void activeImageHasChanged(Composition comp) {
        firstMouseDown = true;
    }

}
