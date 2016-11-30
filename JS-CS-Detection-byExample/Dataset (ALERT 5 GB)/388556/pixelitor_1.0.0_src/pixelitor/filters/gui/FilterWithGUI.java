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
package pixelitor.filters.gui;

import pixelitor.AppLogic;
import pixelitor.Composition;
import pixelitor.ExceptionHandler;
import pixelitor.filters.Filter;
import pixelitor.layers.Layers;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * An operation that has a GUI for customization
 */
public abstract class FilterWithGUI extends Filter {
//    protected AdjustPanel adjustPanel = null;
    private String name;

    protected FilterWithGUI(String name) {
        this(name, null);
    }

    protected FilterWithGUI(String name, Icon icon) {
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
        if (!Layers.activeIsImageLayer()) {
            ExceptionHandler.showNotImageLayerDialog();
            return;
        }

        Composition comp = AppLogic.getActiveComp();
        if (comp != null) {
            comp.getActiveImageLayer().startPreviewing();

            AdjustPanel p = getAdjustPanel();
            startDialogSession();
            AdjustDialog.showDialog(p, this);
            endDialogSession();
        }
    }

    public void startDialogSession() {

    }

    public void endDialogSession() {

    }

}
