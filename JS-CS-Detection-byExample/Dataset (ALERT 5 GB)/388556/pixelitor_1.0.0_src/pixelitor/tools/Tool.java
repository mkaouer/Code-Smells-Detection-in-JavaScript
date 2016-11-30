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
import pixelitor.Composition;
import pixelitor.GlobalKeyboardWatch;
import pixelitor.ImageComponent;
import pixelitor.history.History;
import pixelitor.history.ImageEdit;
import pixelitor.history.PartialImageEdit;
import pixelitor.tools.toolhandlers.ColorPickerToolEventHandler;
import pixelitor.tools.toolhandlers.HandToolEventHandler;
import pixelitor.tools.toolhandlers.ImageLayerCheckHandler;
import pixelitor.tools.toolhandlers.ToolEventHandler;
import pixelitor.tools.toolhandlers.ToolHandler;
import pixelitor.utils.Utils;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * A tool
 */
public abstract class Tool {

    private boolean mouseDown = false;
    private ToolButton toolButton;


    private final String name;
    private final String iconFileName;
    private final String toolMessage;
    protected Cursor cursor;
    private boolean constrainIfShiftDown;

    private boolean endPointInitialized = false;
    protected boolean spaceDragBehavior = false;

    UserDrag userDrag = new UserDrag();

    private final char activationKeyChar;
    ToolEventHandler handlerChainStart;
    private HandToolEventHandler handToolEventHandler;

    protected ToolSettingsPanel toolSettingsPanel;

    protected Tool(char activationKeyChar, String name, String iconFileName, String toolMessage, Cursor cursor, boolean allowOnlyImageLayers, boolean handToolForwarding, boolean constrainIfShiftDown) {
        this.activationKeyChar = activationKeyChar;
        this.name = name;
        this.iconFileName = iconFileName;
        this.toolMessage = toolMessage;
        this.cursor = cursor;
        this.constrainIfShiftDown = constrainIfShiftDown;

        initHandlerChain(cursor, allowOnlyImageLayers, handToolForwarding);
    }

    private void initHandlerChain(Cursor cursor, boolean allowOnlyImageLayers, boolean handToolForwarding) {
        ToolEventHandler lastHandler = null;
        if (allowOnlyImageLayers) {
            lastHandler = addHandlerToChain(new ImageLayerCheckHandler(), lastHandler);
        }
        if (handToolForwarding) {
            handToolEventHandler = new HandToolEventHandler(cursor);
            lastHandler = addHandlerToChain(handToolEventHandler, lastHandler);
        }
        if (this instanceof AbstractBrushTool) { // TODO
            ColorPickerToolEventHandler colorPickerEventHandler = new ColorPickerToolEventHandler();
            lastHandler = addHandlerToChain(colorPickerEventHandler, lastHandler);
        }
        lastHandler = addHandlerToChain(new ToolHandler(this), lastHandler);
    }

    /**
     * Adds the new handler to the end of the chain and returns the new end of the chain
     */
    private ToolEventHandler addHandlerToChain(ToolEventHandler newHandler, ToolEventHandler lastOne) {
        if (lastOne == null) {
            handlerChainStart = newHandler;
            return handlerChainStart;
        } else {
            lastOne.setSuccessor(newHandler);
            return newHandler;
        }
    }

    public String getToolMessage() {
        return toolMessage;
    }

    public boolean mouseClicked(MouseEvent e, ImageComponent ic) {
        // empty for the convenience of subclasses
        return false;
    }

    public void mousePressed(MouseEvent e, ImageComponent ic) {
        if (mouseDown) {
            // can happen if the tool is changed while drawing, and then changed back
            MouseEvent fake = new MouseEvent((Component) e.getSource(), e.getID(), e.getWhen(), e.getModifiers(),
                    userDrag.getEndX(), userDrag.getEndY(), 1, false);
            mouseReleased(fake, ic); // try to clean-up
        }
        mouseDown = true;

        userDrag.setStartFromMouseEvent(e, ic);

        handlerChainStart.handleMousePressed(e, ic);

        endPointInitialized = false;
    }

    public void mouseReleased(MouseEvent e, ImageComponent ic) {
        if (!mouseDown) { // can happen if the tool is changed while drawing
            mousePressed(e, ic); // try to initialize
        }
        mouseDown = false;

        userDrag.setEndFromMouseEvent(e, ic);

        handlerChainStart.handleMouseReleased(e, ic);

        endPointInitialized = false;
    }


    public void mouseDragged(MouseEvent e, ImageComponent ic) {
        if (!mouseDown) { // can happen if the tool is changed while drawing
            mousePressed(e, ic); // try to initialize
        }
        mouseDown = true;

        if (spaceDragBehavior) {
            userDrag.saveEndValues();
        }
        if (constrainIfShiftDown) {
            userDrag.setConstrainPoints(e.isShiftDown());
        }

        userDrag.setEndFromMouseEvent(e, ic);

        if (spaceDragBehavior) {
            if (endPointInitialized && GlobalKeyboardWatch.isSpaceDown()) {
                userDrag.adjustStartForSpaceDownMove();
            }

            endPointInitialized = true; // can be set to true after the first super.mouseDragged(e, ic);
        }

        handlerChainStart.handleMouseDragged(e, ic);
    }


    void setButton(ToolButton toolButton) {
        this.toolButton = toolButton;
    }

    public ToolButton getButton() {
        return toolButton;
    }

    abstract void initSettingsPanel();

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
    public void saveSubImageForUndo(BufferedImage fullUntouchedImage, ToolAffectedArea affectedArea) {
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
        if (handToolEventHandler != null) { // there is hand tool forwarding
            handToolEventHandler.spacePressed();
        }
    }

    public void spaceReleased() {
        if (handToolEventHandler != null) { // there is hand tool forwarding
            handToolEventHandler.spaceReleased();
        }
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


    public abstract void toolMousePressed(MouseEvent e, ImageComponent ic);

    public abstract void toolMouseDragged(MouseEvent e, ImageComponent ic);

    public abstract void toolMouseReleased(MouseEvent e, ImageComponent ic);

    public void setToolSettingsPanel(ToolSettingsPanel toolSettingsPanel) {
        this.toolSettingsPanel = toolSettingsPanel;
    }

    public void randomize() {
        Utils.randomizeGUIWidgetsOn(toolSettingsPanel);
    }
}
