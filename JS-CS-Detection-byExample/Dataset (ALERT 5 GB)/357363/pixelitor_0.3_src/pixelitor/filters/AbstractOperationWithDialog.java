/*
 * Copyright 2009 László Balázs-Csíki
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
package pixelitor.filters;

import pixelitor.ImageComponent;
import pixelitor.AppLogic;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 *
 */
public abstract class AbstractOperationWithDialog extends AbstractOperation implements OperationWithDialog {
    protected AdjustPanel adjustPanel = null;

    protected AbstractOperationWithDialog(String name) {
        this(name, null);
    }

    protected AbstractOperationWithDialog(String name, Icon icon) {
        super(name + "...", icon);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        ImageComponent ic = AppLogic.getActiveImageComponent();
        ic.startPreviewing();

        AdjustPanel p = getAdjustPanel();
        AdjustDialog.showDialog(p, this);
    }

    protected void startPresetAdjusting() {
        if (adjustPanel != null) {
            adjustPanel.setRunFiltersIfStateChanged(false);
        }
    }

    protected void endPresetAdjusting() {
        if (adjustPanel != null) {
            adjustPanel.setRunFiltersIfStateChanged(true);
            adjustPanel.executeFilterPreview();
        }
    }
}
