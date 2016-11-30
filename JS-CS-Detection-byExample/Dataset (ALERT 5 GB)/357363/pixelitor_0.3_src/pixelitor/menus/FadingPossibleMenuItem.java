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

package pixelitor.menus;

import javax.swing.Action;
import javax.swing.JMenuItem;

import pixelitor.AppLogic;
import pixelitor.History;
import pixelitor.ImageChangeReason;
import pixelitor.ImageComponent;
import pixelitor.utils.ImageChangeListener;
import pixelitor.utils.ImageChangedEvent;

/**
 * A menuitem that becomes acive only if there is a change in the active image (like fade, undo)
 */
public class FadingPossibleMenuItem extends JMenuItem implements ImageChangeListener {
    public FadingPossibleMenuItem(Action action) {
        super(action);
        setEnabled(false);
        AppLogic.addImageChangeListener(this);
    }

    @Override
    public void imageContentChanged(ImageChangedEvent e) {
        ImageChangeReason changeReason = e.getChangeReason();

        if (changeReason.sizeChanged()) {
            setEnabled(false);
        } else {
            setEnabled(true);
        }
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
    public void activeImageHasChanged(ImageComponent imageComponent) {
        ImageComponent activeImageComp = AppLogic.getActiveImageComponent();
        if (History.isUndoAvailable(activeImageComp)) {
            if (History.getPreviousReason().sizeChanged()) {
                setEnabled(false);
            } else {
                setEnabled(true);
            }
        } else {
            setEnabled(false);
        }
    }
}