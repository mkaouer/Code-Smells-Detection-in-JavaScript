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
package pixelitor.menus;

import pixelitor.AppLogic;
import pixelitor.Composition;
import pixelitor.history.History;
import pixelitor.utils.ImageSwitchListener;

import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;

/**
 *
 */
public class FadeMenuItem extends JMenuItem implements UndoableEditListener, ImageSwitchListener {
    public FadeMenuItem(Action a) {
        super(a);
        History.addUndoableEditListener(this);
        AppLogic.addImageChangeListener(this);
        setEnabled(false);
    }

    @Override
    public void undoableEditHappened(UndoableEditEvent e) {
        boolean b = History.canFade();

        setEnabled(b);
        getAction().putValue(Action.NAME, "Fade " + History.getLastPresentationName());
    }

    @Override
    public void noOpenImageAnymore() {
        setEnabled(false);
    }

    @Override
    public void newImageOpened() {
        setEnabled(false);
    }

    @Override
    public void activeCompositionHasChanged(Composition comp) {
        setEnabled(History.canFade());
    }
}