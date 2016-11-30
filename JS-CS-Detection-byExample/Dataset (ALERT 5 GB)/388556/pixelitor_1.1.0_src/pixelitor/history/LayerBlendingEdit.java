/*
 * Copyright 2010 L�szl� Bal�zs-Cs�ki
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

import pixelitor.layers.BlendingMode;
import pixelitor.layers.Layer;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 * A PixelitorEdit that represents the changes made to the blending mode of a layer
 */
public class LayerBlendingEdit extends PixelitorEdit {
    private Layer layer;
    private BlendingMode backupBlendingMode;

    public LayerBlendingEdit(Layer layer, BlendingMode backupBlendingMode) {
        super(layer.getComposition(), "Blending Mode Change");
        this.layer = layer;
        this.backupBlendingMode = backupBlendingMode;

        layer.getComposition().setDirty(true);
    }

    @Override
    public void die() {
        super.die();

        layer = null;
        backupBlendingMode = null;
    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();

        swapBlendingModes();
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();

        swapBlendingModes();
    }

    private void swapBlendingModes() {
        BlendingMode tmp = layer.getBlendingMode();
        layer.setBlendingMode(backupBlendingMode, true, false);
        backupBlendingMode = tmp;
        History.postEdit(this);
    }

    @Override
    public boolean canRepeat() {
        return false;
    }
}
