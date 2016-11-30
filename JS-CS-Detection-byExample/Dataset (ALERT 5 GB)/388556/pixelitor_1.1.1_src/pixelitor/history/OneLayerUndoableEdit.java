/*
 * Copyright 2010 László Balázs-Csíki
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
package pixelitor.history;

import pixelitor.ChangeReason;
import pixelitor.Composition;
import pixelitor.layers.ImageLayer;
import pixelitor.layers.Layer;
import pixelitor.selection.Selection;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import java.awt.Shape;
import java.awt.image.BufferedImage;

/**
 * A PixelitorEdit that represents an operation that can affect multiple layers,
 * such as resize, a crop, flip, or image rotation.
 * These are undoable only if the image consists of a single layer
 */
public class OneLayerUndoableEdit extends PixelitorEdit {
    private BufferedImage backupImage;
    private boolean saveSelection;
    private int backupTranslationX;
    private int backupTranslationY;

    private int backupCanvasWidth;
    private int backupCanvasHeight;

    private Shape backupShape;

    private OneLayerUndoableEdit(String presentationName, BufferedImage backupImage, Composition comp, boolean saveSelection) {
        super(comp, presentationName);
        this.backupImage = backupImage;
        this.saveSelection = saveSelection;

        if (saveSelection) {
            Selection selection = comp.getSelection();
            if (selection != null) {
                backupShape = selection.getShape();
            }
        }

        int nrLayers = comp.getNrLayers();
        if (backupImage != null) {
            if (nrLayers != 1) { // make backups only if there is only one layer
                throw new IllegalArgumentException("(backupImage != null, nrLayers = " + nrLayers);
            }
            ImageLayer layer = (ImageLayer) comp.getActiveLayer();
            backupTranslationX = layer.getTranslationX();
            backupTranslationY = layer.getTranslationY();

            backupCanvasWidth = comp.getCanvasWidth();
            backupCanvasHeight = comp.getCanvasHeight();
        }
    }

    @Override
    public boolean canUndo() {
        if (backupImage == null) {
            return false;
        }
        return super.canUndo();
    }

    @Override
    public boolean canRedo() {
        if (backupImage == null) {
            return false;
        }
        return super.canRedo();
    }

    @Override
    public boolean canRepeat() {
        return false;
    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();

//        System.out.println("OneLayerUndoableEdit.undo CALLED");
//        AppLogic.debugImage(backupImage, "backup before undo");

        swapImages(getPresentationName() + " UNDO");

        if (saveSelection && (backupShape != null)) {
            comp.createSelectionFromShape(backupShape);
        }

    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();

        if (saveSelection) {
            comp.deselect(false);
        }
        swapImages(getPresentationName() + " REDO");
    }

    private void swapImages(String opName) {
        if (comp.getNrLayers() != 1) {
            throw new IllegalStateException("nr of layers = " + comp.getNrLayers());
        }

        ImageLayer layer = (ImageLayer) comp.getActiveLayer();
        BufferedImage tmp = layer.getImageOrSubImageIfSelected(false, true);

        int tmpTranslationX = layer.getTranslationX();
        int tmpTranslationY = layer.getTranslationY();

        int tmpCanvasWidth = comp.getCanvasWidth();
        int tmpCanvasHeight = comp.getCanvasHeight();

//        comp.changeActiveLayerImage(backupImage, ChangeReason.UNDO_REDO, opName);
        comp.getActiveImageLayer().changeImageUndoRedo(backupImage);

        if (!comp.hasSelection()) {
            layer.setTranslationX(backupTranslationX);
            layer.setTranslationY(backupTranslationY);
            comp.updateCanvasSize(backupCanvasWidth, backupCanvasHeight);
        }

        backupImage = tmp;
        backupTranslationX = tmpTranslationX;
        backupTranslationY = tmpTranslationY;
        backupCanvasWidth = tmpCanvasWidth;
        backupCanvasHeight = tmpCanvasHeight;

        History.postEdit(this);
    }

    public static void createAndAddToHistory(Composition comp, String presentationName, boolean saveSubImageOnly, boolean saveSelection) {
        int nrLayers = comp.getNrLayers();
        if (nrLayers > 1) {
            History.addEdit(new OneLayerUndoableEdit(presentationName, null, comp, saveSelection));
        } else {
            BufferedImage backup = null;
            Layer layer = comp.getLayer(0);
            if (layer instanceof ImageLayer) {
                ImageLayer imageLayer = (ImageLayer) layer;
                if (saveSubImageOnly) {
                    backup = imageLayer.getImageOrSubImageIfSelected(false, true);
                } else {
                    backup = imageLayer.getBufferedImage(); // for crop/resize  we save the whole image
                }
            }

            History.addEdit(new OneLayerUndoableEdit(presentationName, backup, comp, saveSelection));
        }
    }
}