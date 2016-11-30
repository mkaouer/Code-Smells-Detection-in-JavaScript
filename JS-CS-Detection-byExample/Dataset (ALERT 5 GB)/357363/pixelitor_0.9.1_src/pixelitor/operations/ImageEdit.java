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
package pixelitor.operations;

import pixelitor.History;
import pixelitor.ImageChangeReason;
import pixelitor.ImageComponent;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import java.awt.image.BufferedImage;

/**
 * An UndoableEdit that represents the changes made to an image.
 */
public class ImageEdit extends AbstractUndoableEdit {
    private String name;
    private ImageComponent ic;
    private BufferedImage backupImage;
    private ImageChangeReason changeReason;
    private boolean died = false;  // alive in superclass is private...

    public ImageEdit(String name, ImageComponent ic, BufferedImage backupImage, ImageChangeReason changeReason) {
        this.name = name;
        this.ic = ic;
        this.backupImage = backupImage;
        this.changeReason = changeReason;

        ic.setDirty(true);

        // post condition: the backup should never be identical to the active image
        if (ic.getActiveLayer().getBufferedImage() == backupImage) {
            throw new IllegalStateException("backup BufferedImage is identical to the active one");
        }
    }

    @Override
    public String getPresentationName() {
        return name;
    }

    @Override
    public String getUndoPresentationName() {
        return "Undo " + name;
    }

    @Override
    public String getRedoPresentationName() {
        return "Redo " + name;
    }

    @Override
    public void undo() throws CannotUndoException {
//        System.out.println("ImageEdit.undo CALLED");
        super.undo();

        BufferedImage tmp = ic.getImageForActiveLayer();
        ic.changeActiveLayerImage(backupImage, ImageChangeReason.UNDO_REDO, "ImageEdit UNDO");

//        System.out.println("ImageEdit.undo changeReason = " + changeReason.toString());

        if(changeReason.sizeChanged()) {
            ic.updateSize(backupImage.getWidth(), backupImage.getHeight());
        }
        backupImage = tmp;
        History.postEdit(this);
    }

    @Override
    public void redo() throws CannotRedoException {
//        System.out.println("ImageEdit.redo CALLED");
        super.redo();

        BufferedImage tmp = ic.getImageForActiveLayer();
        ic.changeActiveLayerImage(backupImage, ImageChangeReason.UNDO_REDO, "ImageEdit REDO");
        if(changeReason.sizeChanged()) {
            ic.updateSize(backupImage.getWidth(), backupImage.getHeight());
        }
        backupImage = tmp;
        History.postEdit(this);
    }

    @Override
    public void die() {
        super.die();

        backupImage.flush();
        backupImage = null;
        died = true;
    }

    public BufferedImage getBackupImage() {
        return backupImage;
    }

    public boolean isAlive() {
        return !died;
    }

    @Override
    public String toString() {
        return "ImageEdit{" +
                "name='" + name + '\'' +
                ", ic=" + ic.getName() +
                ", canUndo() =" + canUndo() +
                ", canRedo() =" + canRedo() +
                ", super.toString() =" + super.toString() +
                '}';
    }
}
