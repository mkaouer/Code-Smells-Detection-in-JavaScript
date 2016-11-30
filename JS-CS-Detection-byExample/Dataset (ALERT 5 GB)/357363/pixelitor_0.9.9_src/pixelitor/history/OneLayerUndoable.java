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

import pixelitor.Composition;
import pixelitor.ImageChangeReason;
import pixelitor.layers.ImageLayer;
import pixelitor.layers.Layer;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import java.awt.image.BufferedImage;

/**
 * A PixelitorEdit that represents an operation that can affect multiple layers, such as resize, a crop, flip, or image rotation.
 * These are undoable only if the image consists of a single layer
 */
public class OneLayerUndoable extends PixelitorEdit {
    private String presentationName;

    private BufferedImage backupImage;
    private int backupTranslationX;
    private int backupTranslationY;

    private int backupCanvasWidth;
    private int backupCanvasHeight;

    public OneLayerUndoable(String presentationName, BufferedImage backupImage, Composition comp) {
        super(comp);
        this.presentationName = presentationName;
        this.backupImage = backupImage;

        int nrLayers = comp.getNrLayers();
        if(backupImage != null) {
            if(nrLayers != 1) { // make backups only if there is only one layer
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
        if(backupImage == null) {
            return false;
        }
        return super.canUndo();
    }

    @Override
    public boolean canRedo() {
        if(backupImage == null) {
            return false;
        }
        return super.canRedo();
    }

    @Override
    public String getPresentationName() {
        return presentationName;
    }

    @Override
    public boolean canFade() {
        return false;
    }

    @Override
    public boolean canRepeat() {
        return false;
    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();

        swapImages(presentationName + " UNDO");
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        swapImages(presentationName + " REDO");
    }

    private void swapImages(String opName) {
        ImageLayer layer = (ImageLayer) comp.getActiveLayer();
        BufferedImage tmp = layer.getBufferedImage();

        int tmpTranslationX = layer.getTranslationX();
        int tmpTranslationY = layer.getTranslationY();

        int tmpCanvasWidth = comp.getCanvasWidth();
        int tmpCanvasHeight = comp.getCanvasHeight();

        comp.changeActiveLayerImage(backupImage, ImageChangeReason.UNDO_REDO, opName);
        comp.updateCanvasSize(backupImage.getWidth(), backupImage.getHeight());
        layer.setTranslationX(backupTranslationX);
        layer.setTranslationY(backupTranslationY);
        comp.updateCanvasSize(backupCanvasWidth, backupCanvasHeight);

        backupImage = tmp;
        backupTranslationX = tmpTranslationX;
        backupTranslationY = tmpTranslationY;
        backupCanvasWidth = tmpCanvasWidth;
        backupCanvasHeight = tmpCanvasHeight;

        History.postEdit(this);
    }

    public static void backup(Composition comp, String presentationName) {
        int nrLayers = comp.getNrLayers();
        if(nrLayers > 1 ) {
            History.addEdit(new OneLayerUndoable(presentationName, null, comp));
        } else {
            BufferedImage backup = null;
            Layer layer = comp.getLayer(0);
            if(layer instanceof ImageLayer) {
                ImageLayer imageLayer = (ImageLayer) layer;
                backup = imageLayer.getBufferedImage();
            }

            History.addEdit(new OneLayerUndoable("Crop", backup, comp));
        }
    }
}