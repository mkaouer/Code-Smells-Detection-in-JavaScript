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
import pixelitor.History;
import pixelitor.ImageComponent;
import pixelitor.utils.ImageChangeListener;
import pixelitor.utils.ImageChangedEvent;

import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;

/**
 * A JMenuItem that is enabled or disabled by events
 */
public class IntelligentMenuItem extends JMenuItem implements ImageChangeListener, UndoableEditListener {
    private MenuEnableCondition enableCondition;

    public IntelligentMenuItem(Action a, MenuEnableCondition enableCondition) {
        super(a);
        this.enableCondition = enableCondition;
        init();
    }

    private void init() {
        setEnabled(enableCondition.enableAtStartUp());
        if (enableCondition.isImageChangeListener()) {
            AppLogic.addImageChangeListener(this);
        } else if (enableCondition.isUndoRedoListener()) {
            History.addUndoableEditListener(this);
        }
    }

    @Override
    public void imageContentChanged(ImageChangedEvent e) {
//        if (enableCondition == MenuEnableCondition.IF_FADING_POSSIBLE) {
//            ImageChangeReason changeReason = e.getChangeReason();
//            if (changeReason.sizeChanged()) {
//                setEnabled(false);
//            } else {
//                setEnabled(true);
//            }
//        }
    }

    @Override
    public void noOpenImageAnymore() {
        if (enableCondition != MenuEnableCondition.ALWAYS) {
            setEnabled(false);
        }
    }

    @Override
    public void newImageOpened() {
        if (enableCondition == MenuEnableCondition.IF_THERE_IS_OPEN_IMAGE) {
            setEnabled(true);
        } else {
            setEnabled(false);
        }
    }

    @Override
    public void activeImageHasChanged(ImageComponent ic) {
        if (enableCondition == MenuEnableCondition.IF_CAN_REPEAT_OPERATION) {
            if (History.canRepeatOperation(ic)) {
                setEnabled(true);
            } else {
                setEnabled(false);
            }
        }
    }

    @Override
    public void undoableEditHappened(UndoableEditEvent e) {
//        System.out.println("IntelligentMenuItem.undoableEditHappened CALLED");
//        Thread.dumpStack();

//        UndoableEdit edit = e.getEdit();
        if (enableCondition == MenuEnableCondition.IF_UNDO_POSSIBLE) {
            setEnabled(History.canUndo());
            getAction().putValue(Action.NAME, History.getUndoPresentationName());
        } else if (enableCondition == MenuEnableCondition.IF_REDO_POSSIBLE) {
            setEnabled(History.canRedo());
            getAction().putValue(Action.NAME, History.getRedoPresentationName());
        } else if (enableCondition == MenuEnableCondition.IF_FADING_POSSIBLE) {
            boolean b = History.canFade();
//            System.out.println("IntelligentMenuItem.undoableEditHappened b = " + b);
            setEnabled(b);
            getAction().putValue(Action.NAME, History.getFadePresentationName());
        }
    }
}
