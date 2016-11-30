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

import org.jdesktop.swingx.combobox.EnumComboBoxModel;
import pixelitor.AppLogic;
import pixelitor.Composition;
import pixelitor.ImageComponent;
import pixelitor.filters.gui.RangeParam;
import pixelitor.layers.ImageLayer;
import pixelitor.tools.brushes.Brush;
import pixelitor.utils.ImageSwitchListener;
import pixelitor.utils.ImageUtils;
import pixelitor.utils.SliderSpinner;

import javax.swing.*;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;

/**
 *
 */
public abstract class AbstractBrushTool extends Tool implements ImageSwitchListener {
    boolean respectSelection = true;

    private JComboBox typeSelector;

    private int minX = 0;
    private int minY = 0;
    private int maxX = 0;
    private int maxY = 0;

    Graphics2D g;
    private RangeParam brushRadiusParam = new RangeParam("Radius", 1, 100, 10);

    private Composition comp;
    private int diameter;

    private EnumComboBoxModel<Symmetry> symmetryModel = new EnumComboBoxModel<Symmetry>(Symmetry.class);

    private Brush brush = BrushType.IDEAL.getBrush();

    private int previousMouseX = 0;
    private int previousMouseY = 0;

    private boolean firstMouseDown = true; // for the first click don't draw lines even if it is a shift-click

    AbstractBrushTool(char activationKeyChar, String name, String iconFileName, String toolMessage) {
        super(activationKeyChar, name, iconFileName, toolMessage, Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR), true, true, false);
        AppLogic.addImageChangeListener(this);
    }

    @Override
    void initSettingsPanel() {
        toolSettingsPanel.add(new JLabel("Type:"));
        typeSelector = new JComboBox(BrushType.values());
        toolSettingsPanel.add(typeSelector);
        typeSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BrushType brushType = (BrushType) typeSelector.getSelectedItem();
                brush = brushType.getBrush();
            }
        });

        SliderSpinner brushSizeSpinner = new SliderSpinner(brushRadiusParam, false, SliderSpinner.TextPosition.WEST);
        toolSettingsPanel.add(brushSizeSpinner);

        toolSettingsPanel.add(new JLabel("Mirror:"));

        JComboBox symmetryCombo = new JComboBox(symmetryModel);

        toolSettingsPanel.add(symmetryCombo);
    }

    @Override
    public void toolMousePressed(MouseEvent e, ImageComponent ic) {
        boolean withLine = !firstMouseDown && e.isShiftDown();

        Paint p;
        int button = e.getButton();

        if (button == MouseEvent.BUTTON3) {
            p = FgBgColorSelector.getBG();
        } else if (button == MouseEvent.BUTTON2) {
            // we never get here because isAltDown is always true for middle-button events, even if Alt is not pressed
            Color fg = FgBgColorSelector.getFG();
            Color bg = FgBgColorSelector.getBG();
            if (e.isControlDown()) {
                p = ImageUtils.getHSBAverageColor(fg, bg);
            } else {
                p = ImageUtils.getRGBAverageColor(fg, bg);
            }
        } else {
            p = FgBgColorSelector.getFG();
        }

        int x = userDrag.getStartX();
        int y = userDrag.getStartY();
        drawTo(ic.getComp(), p, x, y, withLine);
        firstMouseDown = false;

        if (withLine) {
            updateMinMaxCoordinates(x, y);
        } else {
            minX = x;
            minY = y;
            maxX = x;
            maxY = y;
        }
    }

    @Override
    public void toolMouseDragged(MouseEvent e, ImageComponent ic) {
        int x = userDrag.getEndX();
        int y = userDrag.getEndY();
        drawTo(ic.getComp(), null, x, y, false);
    }

    @Override
    public void toolMouseReleased(MouseEvent e, ImageComponent ic) {
        finishBrushStroke(ic.getComp());
    }

    private void updateMinMaxCoordinates(int x, int y) {
        if (x > maxX) {
            maxX = x;
        } else if (x < minX) {
            minX = x;
        }

        if (y > maxY) {
            maxY = y;
        } else if (y < minY) {
            minY = y;
        }
    }

    abstract BufferedImage getFullUntouchedImage(Composition comp);

    abstract void mergeTmpLayer(Composition comp);

    void finishBrushStroke(Composition comp) {
        ToolAffectedArea affectedArea = new ToolAffectedArea(comp, getRectangleAffectedByBrush(), false);
        saveSubImageForUndo(getFullUntouchedImage(comp), affectedArea);
        mergeTmpLayer(comp);
        if (g != null) {
            g.dispose();
        }
        g = null;

        comp.imageChanged(false, true); // for the histogram update
    }

    public void drawBrushStrokeProgrammatically(Composition comp, Point startingPoint, Point endPoint) {
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
     * Creates the global Graphics2D object g.
     */
    abstract void initDrawingGraphics(ImageLayer layer);

    /**
     * Called from mousePressed, mouseDragged, and drawBrushStrokeProgrammatically
     */
    private void drawTo(Composition comp, Paint p, int x, int y, boolean connectClickWithLine) {
        // TODO there two variables could be initialized outside this function
        setupDrawingDiameter();
        Symmetry currentSymmetry = symmetryModel.getSelectedItem();

        if (g == null) {
            this.comp = comp;

            ImageLayer imageLayer = (ImageLayer) comp.getActiveLayer();

//            BufferedImage image = imageLayer.getBufferedImage();
            initDrawingGraphics(imageLayer);

            setupGraphics(g, p);

            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


            if (connectClickWithLine) {
                currentSymmetry.drawLine(this, previousMouseX, previousMouseY, x, y);
            } else {
                currentSymmetry.drawPoint(this, x, y);
            }
        } else {
            currentSymmetry.drawLine(this, previousMouseX, previousMouseY, x, y);
        }

        previousMouseX = x;
        previousMouseY = y;
    }

    private void setupDrawingDiameter() {
        diameter = brushRadiusParam.getValue() * 2;
    }

    public void drawPoint(int x, int y) {
        updateMinMaxCoordinates(x, y);
        int radius = diameter / 2;
        int drawX = x - radius;
        int drawY = y - radius;

        brush.drawPoint(g, drawX, drawY, diameter);

        comp.updateRegion(drawX, drawY, drawX + diameter + 1, drawY + diameter + 1, 0);
    }

    public void drawLine(int startX, int startY, int endX, int endY) {
        updateMinMaxCoordinates(endX, endY);

        brush.drawLine(g, startX, startY, endX, endY, diameter);
        comp.updateRegion(startX, startY, endX, endY, diameter);
    }


    protected abstract void setupGraphics(Graphics2D g, Paint p);

    @Override
    protected void toolStarted() {
        super.toolStarted();
        resetState();
    }

    @Override
    public void noOpenImageAnymore() {

    }

    @Override
    public void newImageOpened() {
        resetState();
    }

    @Override
    public void activeCompositionHasChanged(Composition comp) {
        resetState();

    }

    private void resetState() {
        firstMouseDown = true;
        respectSelection = true;
    }


    private Rectangle getRectangleAffectedByBrush() {
        int brushRadius = brushRadiusParam.getValue();

        // To be on the safe side, save a little more than necessary - some brushes have randomness
        int radius2 = 2 * brushRadius;
        int radius4 = 4 * brushRadius;

        int saveX = minX - radius2;
        int saveY = minY - radius2;
        int saveWidth = maxX - minX + radius4;
        int saveHeight = maxY - minY + radius4;
        Rectangle rectangleAffectedByBrush = new Rectangle(saveX, saveY, saveWidth, saveHeight);
        return rectangleAffectedByBrush;
    }

    /**
     * Traces the given shape and paint with the current brush tool
     */
    public void trace(Composition comp, Shape shape) {
        this.comp = comp; // just to be sure
        setupDrawingDiameter();
        try {
            respectSelection = false;

            ImageLayer imageLayer = (ImageLayer) comp.getActiveLayer();
            initDrawingGraphics(imageLayer);
            setupGraphics(g, FgBgColorSelector.getFG());

            int startingX = 0;
            int startingY = 0;

            FlatteningPathIterator fpi = new FlatteningPathIterator(shape.getPathIterator(null), 1.0);
            float[] coords = new float[2];
            while (!fpi.isDone()) {
                int type = fpi.currentSegment(coords);
                int x = (int) coords[0];
                int y = (int) coords[1];
                updateMinMaxCoordinates(x, y);

                if (type == PathIterator.SEG_MOVETO) {
                    startingX = x;
                    startingY = y;

                    previousMouseX = x;
                    previousMouseY = y;

                } else if (type == PathIterator.SEG_LINETO) {
                    drawLine(previousMouseX, previousMouseY, x, y);

                    previousMouseX = x;
                    previousMouseY = y;

                } else if (type == PathIterator.SEG_CLOSE) {
                    drawLine(previousMouseX, previousMouseY, startingX, startingY);
                } else {
                    throw new IllegalArgumentException("type = " + type);
                }

                fpi.next();
            }
            finishBrushStroke(comp);
        } finally {
            resetState();
        }
    }

    public void increaseBrushSize() {
        brushRadiusParam.increaseValue();
    }

    public void decreaseBrushSize() {
        brushRadiusParam.decreaseValue();
    }
}
