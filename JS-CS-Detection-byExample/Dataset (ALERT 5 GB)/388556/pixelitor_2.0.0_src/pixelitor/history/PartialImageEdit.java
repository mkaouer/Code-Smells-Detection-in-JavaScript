/*
 * Copyright 2010 Laszlo Balazs-Csiki
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
import pixelitor.layers.ImageLayer;
import pixelitor.selection.Selection;
import pixelitor.utils.ImageUtils;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;

/**
 * Represents the changes made to a part of an image (for example brush strokes).
 * Only the affected pixels are saved in order to reduce overall memory usage
 */
public class PartialImageEdit extends FadeableEdit {
    private final Rectangle saveRectangle;
    private final boolean canRepeat;
    private Raster backupRaster;

    private final ImageLayer layer;

    public PartialImageEdit(String name, Composition comp, BufferedImage image, Rectangle saveRectangleParam, boolean canRepeat) {
        super(comp, name);

        this.canRepeat = canRepeat;
        comp.setDirty(true);
        this.layer = (ImageLayer) comp.getActiveLayer();

        saveRectangle = saveRectangleParam;
        backupRaster = image.getData(saveRectangle);
    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();

        swapRasters();
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();

        swapRasters();
    }

    private void swapRasters() {
        BufferedImage image = layer.getBufferedImage();

        Raster tmpRaster;
        try {
            tmpRaster = image.getData(saveRectangle);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("PartialImageEdit.swapRasters saveRectangle = " + saveRectangle);
            int width = image.getWidth();
            int height = image.getHeight();
            System.out.println("PartialImageEdit.swapRasters width = " + width + ", height = " + height);

            throw e;
        }

        image.setData(backupRaster);
        backupRaster = tmpRaster;

        comp.imageChanged(true, true);

        History.postEdit(this);
    }

    @Override
    public void die() {
        super.die();

        backupRaster = null;
    }

    @Override
    public boolean canRepeat() {
        return canRepeat;
    }

    @Override
    public BufferedImage getBackupImage() {
        // recreate the full image as if it was backed up entirely
        // because Fade expects to fade images of equal size
        // TODO this is not the optimal solution  - Fade should fade only the changed area
        BufferedImage fullImage = layer.getBufferedImage();
        BufferedImage previousImage = ImageUtils.copyImage(fullImage);
        previousImage.setData(backupRaster);

        Selection selection = layer.getComposition().getSelection();
        if (selection != null) {
            // backupRaster is relative to the full image, but we need to return a selection-sized image
            // TODO this is another ugly hack
            previousImage = layer.getSelectionSizedPartFrom(previousImage, selection, true);
        }

        return previousImage;
    }
}
