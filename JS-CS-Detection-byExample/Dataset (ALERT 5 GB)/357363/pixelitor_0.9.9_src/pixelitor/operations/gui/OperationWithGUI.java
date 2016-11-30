/*
 * Copyright 2009-2010 László Balázs-Csíki
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
package pixelitor.operations.gui;

import pixelitor.AppLogic;
import pixelitor.Composition;
import pixelitor.operations.Operation;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * An operation that has a GUI for customization
 */
public abstract class OperationWithGUI extends Operation {
//    protected AdjustPanel adjustPanel = null;
    private String name;

    protected OperationWithGUI(String name) {
        this(name, null);
    }

    protected OperationWithGUI(String name, Icon icon) {
        super(name + "...", icon);
        this.name = name;
    }

    public abstract AdjustPanel getAdjustPanel();

    /**
     * Returns the menu name, but without the "..." at the end
     */
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Composition comp = AppLogic.getActiveComp();
        if (comp != null) {
            comp.startPreviewing();

            AdjustPanel p = getAdjustPanel();
            AdjustDialog.showDialog(p, this);
        }
    }
}
