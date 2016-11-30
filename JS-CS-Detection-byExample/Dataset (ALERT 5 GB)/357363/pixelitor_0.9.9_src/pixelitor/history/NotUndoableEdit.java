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

import pixelitor.Composition;

/**
 * Actions that are not undoable
 */
public class NotUndoableEdit extends PixelitorEdit {
    private String presentationName;

    public NotUndoableEdit(Composition comp, String presentationName) {
        super(comp);
        this.presentationName = presentationName;
    }

    @Override
    public boolean canUndo() {
        return false;
    }

    @Override
    public boolean canRedo() {
        return false;
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
}