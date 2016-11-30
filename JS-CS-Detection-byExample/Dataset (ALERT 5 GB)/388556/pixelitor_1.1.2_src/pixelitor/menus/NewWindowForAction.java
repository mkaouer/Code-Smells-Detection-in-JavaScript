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
package pixelitor.menus;

import pixelitor.AppLogic;
import pixelitor.Composition;
import pixelitor.utils.ImageSwitchListener;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 *
 */
public class NewWindowForAction extends AbstractAction implements ImageSwitchListener {
    private static final String defaultName = "New Window for Current Image";

    public NewWindowForAction() {
        super(defaultName);
        setEnabled(false);
        AppLogic.addImageChangeListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void noOpenImageAnymore() {
        putValue(Action.NAME, defaultName);
        setEnabled(false);
    }

    @Override
    public void newImageOpened() {
        // handled in activeCompositionHasChanged
    }

    @Override
    public void activeCompositionHasChanged(Composition comp) {
        setEnabled(true);
        putValue(Action.NAME, "New Window for " + comp.getName());
    }
}
