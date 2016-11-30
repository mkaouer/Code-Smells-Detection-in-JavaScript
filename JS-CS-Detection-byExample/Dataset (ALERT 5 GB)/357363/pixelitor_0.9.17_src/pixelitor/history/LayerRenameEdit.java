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

import pixelitor.layers.Layer;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 * A PixelitorEdit that represents the changes made to the opacity of a layer
 */
public class LayerRenameEdit extends PixelitorEdit {
    private Layer layer;
    private String nameBefore;
    private String nameAfter;

    public LayerRenameEdit(Layer layer, String nameBefore, String nameAfter) {
        super(layer.getComposition(), "Rename Layer to " + nameAfter);
        this.layer = layer;
        this.nameBefore = nameBefore;
        this.nameAfter = nameAfter;

        layer.getComposition().setDirty(true);
    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();

        layer.setName(nameBefore, false);

        History.postEdit(this);
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();

        layer.setName(nameAfter, false);

        History.postEdit(this);
    }


    @Override
    public void die() {
        super.die();

        layer = null;
    }

    @Override
    public boolean canRepeat() {
        return false;
    }
}