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

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import java.awt.*;

/**
 * Represents the creation of a new selection.
 */
public class NewSelectionEdit extends PixelitorEdit {
    private final Shape newShape;

    public NewSelectionEdit(Composition comp, Shape shape) {
        super(comp, "Create Selection");
        newShape = shape;
    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();

        comp.deselect(false);

        History.postEdit(this);
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();

        comp.createSelectionFromShape(newShape);

        History.postEdit(this);
    }

    @Override
    public boolean canRepeat() {
        return false;
    }
}
