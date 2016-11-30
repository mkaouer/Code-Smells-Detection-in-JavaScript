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

import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

/**
 *
 */
public class PixelitorUndoManager extends UndoManager {
    public PixelitorUndoManager() {
    }

    /**
     * This method is necessary mostly because lastEdit() in CompoundEdit is protected
     */
    public PixelitorEdit getLastEdit() {
        UndoableEdit edit = super.lastEdit();
        if(edit != null) {
            return (PixelitorEdit) edit;
        }
        return null;
    }
}
