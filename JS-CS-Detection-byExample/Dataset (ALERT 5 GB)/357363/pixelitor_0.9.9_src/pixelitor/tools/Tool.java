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
import pixelitor.history.History;
import pixelitor.ImageComponent;
import pixelitor.history.ImageEdit;
import pixelitor.utils.ImageUtils;

import javax.swing.*;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * An enum-like abstract class for the tools. For each tool there is a subclass.
 */
public abstract class Tool {
    public static final CropSelectionTool CROP_SELECTION = new CropSelectionTool();
    public static final MoveTool MOVE = new MoveTool();
    public static final GradientTool GRADIENT = new GradientTool();
    public static final BrushTool BRUSH = new BrushTool();
    public static final EraseTool ERASE = new EraseTool();
    public static final ColorPickerTool COLOR_PICKER = new ColorPickerTool();
    public static final ShapesTool SHAPES = new ShapesTool();
    public static final HandTool HAND = new HandTool();

    private ToolButton toolButton;

    public static Tool currentTool = BRUSH;

    /**
     * All the subclass tools in an array.
     */
    private static Tool[] allTools = new Tool[]{CROP_SELECTION, MOVE, GRADIENT, BRUSH, ERASE, COLOR_PICKER, SHAPES, HAND};
    private String name;
    private String iconFileName;
    private String toolMessage;
    protected Cursor cursor;

    public static Tool[] values() {
        return allTools;
    }

    protected Point start = new Point(0, 0);
    protected Point end = new Point(0, 0);

    private char activationKeyChar;

    protected Tool(char activationKeyChar, String name, String iconFileName, String toolMessage, Cursor cursor) {
        this.activationKeyChar = activationKeyChar;
        this.name = name;
        this.iconFileName = iconFileName;
        this.toolMessage = toolMessage;
        this.cursor = cursor;
    }

    public String getToolMessage() {
        return toolMessage;
    }

    public static Tool getCurrentTool() {
        return currentTool;
    }

    public static void setCurrentTool(Tool currentTool) {
        Tool.currentTool.toolEnded();
        Tool.currentTool = currentTool;
        Tool.currentTool.toolStarted();
        ToolSettingsPanel.INSTANCE.showSettingsFor(currentTool);
    }

//    /**
//     * A possibility to paint temporarily something (like marching ants) on the ImageComponent
//     * after all the layers have been painted.
//     */
//    public void paintOverImage(Graphics2D g) {
//    }

    public void mouseClicked(MouseEvent e, ImageComponent ic) {
    }

    public void mousePressed(MouseEvent e, ImageComponent ic) {
        start = getPointFormMouseEvent(e, ic);
    }

    public void mouseReleased(MouseEvent e, ImageComponent ic) {
        end = getPointFormMouseEvent(e, ic);
    }

    public void mouseDragged(MouseEvent e, ImageComponent ic) {
        end = getPointFormMouseEvent(e, ic);
    }

    private static Point getPointFormMouseEvent(MouseEvent e, ImageComponent ic) {
        double scale = ic.getViewScale();
        int scaledX = (int) (e.getX() / scale);
        int scaledY = (int) (e.getY() / scale);
        return new Point(scaledX, scaledY);
    }

    void setButton(ToolButton toolButton) {
        this.toolButton = toolButton;
    }

    public ToolButton getButton() {
        return toolButton;
    }

    abstract void initSettingsPanel(JPanel p);

    public String getName() {
        return name;
    }

    protected String getIconFileName() {
        return iconFileName;
    }

    public char getActivationKeyChar() {
        return activationKeyChar;
    }

    void saveImageForUndo(Composition comp) {
        BufferedImage bi = comp.getImageForActiveLayer();
        BufferedImage copy = ImageUtils.copyImage(bi);

        ImageEdit edit = new ImageEdit(getName(), comp, copy, false);
        History.addEdit(edit);
    }

    public void spacePressed() {
        // do nothing by default
    }

    public void spaceReleased() {
        // do nothing by default
    }

    protected void toolStarted() {
        AppLogic.setToolCursor(cursor);
    }

    protected void toolEnded() {
    }

    public Cursor getCursor() {
        return cursor;
    }

    public void paintOverLayer(Graphics2D g) {

    }

    public static boolean isShapesDrawing() {
        if(currentTool != SHAPES) {
            return false;
        }
        return SHAPES.isDrawing();    
    }

}
