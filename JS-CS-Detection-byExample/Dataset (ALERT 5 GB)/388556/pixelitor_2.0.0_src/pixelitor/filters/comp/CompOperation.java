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
package pixelitor.filters.comp;

import pixelitor.Composition;
import pixelitor.ImageComponents;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * An operation that acts on a Composition
 */
public abstract class CompOperation extends AbstractAction {

    CompOperation(String name) {
        this(name, null);
    }

    private CompOperation(String name, Icon icon) {
        if (name == null) {
            throw new IllegalArgumentException("name is null");
        }

        putValue(Action.SMALL_ICON, icon);
        putValue(Action.NAME, name);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Composition comp = ImageComponents.getActiveComp();
        transform(comp);
    }

    protected abstract void transform(Composition comp);
}
