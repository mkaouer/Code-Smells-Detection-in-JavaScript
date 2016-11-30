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
import pixelitor.ImageComponent;
import pixelitor.history.History;
import pixelitor.history.ImageEdit;
import pixelitor.history.PartialImageEdit;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * An enum-like abstract class for the tools. For each tool there is a subclass.
 */
public abstract class Tool {
    public static final SelectionTool SELECTION = new SelectionTool();
//    public static final LassoTool LASSO = new LassoTool();
    public static final MoveTool MOVE = new MoveTool();
    public static final GradientTool GRADIENT = new GradientTool();
    public static final BrushTool BRUSH = new BrushTool();
    public static final EraseTool ERASER = new EraseTool();
    public static final ColorPickerTool COLOR_PICKER = new ColorPickerTool();
    public static final ShapesTool SHAPES = new ShapesTool();
    public static final HandTool HAND = new HandTool();
    public static final PaintBucketTool PAINT_BUCKET = new PaintBucketTool();

    private boolean mouseDown = false;
    private ToolButton toolButton;

    static Tool currentTool = BRUSH;

    /**
     * All the subclass tools in an array.
     */
    private static Tool[] allTools = new Tool[]{MOVE, SELECTION, BRUSH, ERASER, GRADIENT, PAINT_BUCKET, COLOR_PICKER, SHAPES, HAND};
    private final String name;
    private final String iconFileName;
    private final String toolMessage;
    protected Cursor cursor;

    public static Tool[] values() {
        return allTools;
    }

    UserDrag userDrag = new UserDrag();

    private final char activationKeyChar;

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
        ToolSettingsPanelContainer.INSTANCE.showSettingsFor(currentTool);
    }

    /**
     * @return true if all the work is done, and the subclass can return
     */
    public boolean mouseClicked(MouseEvent e, ImageComponent ic) {
        // empty for the convenience of subclasses
        return false;
    }


    public boolean mousePressed(MouseEvent e, ImageComponent ic) {
        if (mouseDown) {
            // can happen if the tool is changed while drawing, and then changed back
            MouseEvent fake = new MouseEvent((Component) e.getSource(), e.getID(), e.getWhen(), e.getModifiers(),
                    userDrag.getEndX(), userDrag.getEndY(), 1, false);
            mouseReleased(fake, ic); // try to clean-up
        }
        mouseDown = true;


        userDrag.setStartFromMouseEvent(e, ic);
        return false;
    }

    public boolean mouseReleased(MouseEvent e, ImageComponent ic) {
        if (!mouseDown) { // can happen if the tool is changed while drawing
            mousePressed(e, ic); // try to initialize
        }
        mouseDown = false;

        userDrag.setEndFromMouseEvent(e, ic);
        return false;
    }

    public boolean mouseDragged(MouseEvent e, ImageComponent ic) {
        if (!mouseDown) { // can happen if the tool is changed while drawing
            mousePressed(e, ic); // try to initialize
        }
        mouseDown = true;

        userDrag.setEndFromMouseEvent(e, ic);
        return false;
    }


    void setButton(ToolButton toolButton) {
        this.toolButton = toolButton;
    }

    public ToolButton getButton() {
        return toolButton;
    }

    abstract void initSettingsPanel(ToolSettingsPanel p);

    public String getName() {
        return name;
    }

    protected String getIconFileName() {
        return iconFileName;
    }

    public char getActivationKeyChar() {
        return activationKeyChar;
    }

    /**
     * This saving method is used only by the Gradient Tool.
     * It saves the full image or the selected area only if there is a selection
     */
    void saveImageForUndo(Composition comp) {
        BufferedImage copy = comp.getImageOrSubImageIfSelectedForActiveLayer(true, true);

        ImageEdit edit = new ImageEdit(getName(), comp, copy, false);
        History.addEdit(edit);
    }

    /**
     * This saving method is used by the brush tools, by the shapes and by the paint bucket.
     * It saves the intersection of the selection (if there is one) with the maximal affected area.
     * TODO currently it does not take the selection into account
     *
     * @param comp
     * @param fullUntouchedImage
     * @param rectangleAffectedByTool - given relative to the canvas
     */
    public void saveSubImageForUndo(BufferedImage fullUntouchedImage, AffectedArea affectedArea) {
        assert (fullUntouchedImage != null);
        Rectangle rectangleAffectedByTool = affectedArea.getRectangle();
        if (rectangleAffectedByTool.isEmpty()) {
            return;
        }

        Composition comp = affectedArea.getComp();

        Rectangle fullImageBounds = new Rectangle(0, 0, fullUntouchedImage.getWidth(), fullUntouchedImage.getHeight());
        Rectangle saveRectangle = rectangleAffectedByTool.intersection(fullImageBounds);

        if (!saveRectangle.isEmpty()) {
            PartialImageEdit edit = new PartialImageEdit(getName(), comp, fullUntouchedImage, saveRectangle, false);
            History.addEdit(edit);
        }
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
        // empty for the convenience of subclasses
    }

    public Cursor getCursor() {
        return cursor;
    }

    public void paintOverLayer(Graphics2D g) {
        // empty for the convenience of subclasses
    }

    public static boolean isShapesDrawing() {
        if (currentTool != SHAPES) {
            return false;
        }
        return SHAPES.isDrawing();
    }

    /**
     * A possibility to paint temporarily something (like marching ants) on the ImageComponent
     * after all the layers have been painted.
     */
    public void paintOverImage(Graphics2D g2) {
        // empty for the convenience of subclasses
    }

    public void mouseMoved(MouseEvent e, ImageComponent ic) {
        // empty for the convenience of subclasses
    }

    public static void increaseActiveBrushSize() {
        if(currentTool instanceof AbstractBrushTool) {
            AbstractBrushTool brushTool = (AbstractBrushTool) currentTool;
            brushTool.increaseBrushSize();
        }
    }


    public static void decreaseActiveBrushSize() {
        if(currentTool instanceof AbstractBrushTool) {
            AbstractBrushTool brushTool = (AbstractBrushTool) currentTool;
            brushTool.decreaseBrushSize();
        }
    }

}
