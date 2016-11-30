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

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import java.awt.image.BufferedImage;

/**
 * A PixelitorEdit that represents the changes made to an image.
 */
public class ImageEdit extends FadeableEdit {
    private BufferedImage backupImage;
    private ImageLayer layer;

    private boolean canRepeat;


    public ImageEdit(String name, Composition comp, BufferedImage backupImage, boolean canRepeat) {
        super(comp, name);

        this.backupImage = backupImage;
        this.canRepeat = canRepeat;
        layer = (ImageLayer) comp.getActiveLayer();

        comp.setDirty(true);
//        AppLogic.debugImage(backupImage);

        sanityCheck();
    }

    private void sanityCheck() {
        // post condition: the backup should never be identical to the active image
        if (layer.getBufferedImage() == backupImage) {
            throw new IllegalStateException("backup BufferedImage is identical to the active one");
        }
    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();

        swapImages("ImageEdit UNDO");
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();

        swapImages("ImageEdit REDO");
    }

    private void swapImages(String opName) {
        BufferedImage tmp = layer.getImageOrSubImageIfSelected(false, true);

        comp.changeLayerImage(layer, backupImage, ImageChangeReason.UNDO_REDO, opName);
        backupImage = tmp;
        History.postEdit(this);

        sanityCheck();
    }

    @Override
    public void die() {
        super.die();

        backupImage.flush();
        backupImage = null;

        layer = null;
    }

    @Override
    public BufferedImage getBackupImage() {
        return backupImage;
    }

    @Override
    public boolean canRepeat() {
        return canRepeat;
    }
}
