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
import pixelitor.menus.ZoomLevel;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 * A PixelitorEdit that represents a change in the zooming of the image
 */
public class ZoomChangeEdit extends PixelitorEdit {
    private final ZoomLevel before;
    private final ZoomLevel after;

    public ZoomChangeEdit(Composition comp, ZoomLevel before, ZoomLevel after) {
        super(comp, "Zoom to " + after.toString());
        this.before = before;
        this.after = after;
    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();

        comp.getIC().setZoom(before, false);

        History.postEdit(this);
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();

        comp.getIC().setZoom(after, false);

        History.postEdit(this);
    }

    @Override
    public boolean canRepeat() {
        return false;
    }
}