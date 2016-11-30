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

package pixelitor;

import pixelitor.operations.ImageEdit;
import pixelitor.utils.AppPreferences;

import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;
import javax.swing.undo.UndoableEditSupport;
import java.awt.image.BufferedImage;

/**
 * Manages history and undo/redo for all open images
 */
public final class History {
    private static UndoManager undoManager = new UndoManager();
    private static UndoableEditSupport undoableEditSupport = new UndoableEditSupport();
    private static ImageChangeReason lastReason;
    private static ImageEdit lastImageEdit;

    static {
        setUndoLevels(AppPreferences.loadUndoLevels());
    }

    // this is a utility class with static methods, it should not be instantiated
    private History() {
    }

    /**
     * This is used to notify the menu items
     */
    public static void postEdit(UndoableEdit e) {
        undoableEditSupport.postEdit(e);
    }

    public static void addEdit(UndoableEdit e, ImageChangeReason changeReason) {
        lastReason = changeReason;
        boolean canBeUndone = true;

        if(changeReason.sizeChanged()) {
            int nrLayers = AppLogic.getActiveImageComponent().getNrLayers();
            if(nrLayers > 1) {
                // operations that change the size also are applied to multiple
                // layers which means that they cannot be undone with the ImageEdit mechanism
                // if there are more than 1 layers
                canBeUndone = false;
            }
        }

        if(canBeUndone) {
            undoManager.addEdit(e);
        } else {
            undoManager.discardAllEdits();
        }

        if(e instanceof ImageEdit) {
//            System.out.println("History.addEdit CALLED");
//            Thread.dumpStack();
            lastImageEdit = (ImageEdit) e;
        }
        undoableEditSupport.postEdit(e);
    }

    public static String getUndoPresentationName() {
        return undoManager.getUndoPresentationName();
    }

    public static String getRedoPresentationName() {
        return undoManager.getRedoPresentationName();
    }


    public static void redo() {
        undoManager.redo();
    }

    public static void undo() {
        undoManager.undo();
    }

    public static boolean canUndo() {
        boolean b = undoManager.canUndo();
        return b;
    }

    public static boolean canRedo() {
        boolean b = undoManager.canRedo();
        return b;
    }

    public static void addUndoableEditListener(UndoableEditListener listener) {
        undoableEditSupport.addUndoableEditListener(listener);
    }

    public static void setUndoLevels(int undoLevels) {
        undoManager.setLimit(undoLevels);
    }

    public static int getUndoLevels() {
        return undoManager.getLimit();
    }

    public static boolean canRepeatOperation(ImageComponent ic) {
        return canUndo(); // TODO !!!
    }

    public static boolean canFade() {
        boolean b = lastImageEdit != null && lastImageEdit.isAlive();
//        Thread.dumpStack();
//        System.out.println("History.canFade b = " + b);
        if (b) {
            if (lastReason.sizeChanged()) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public static BufferedImage getPreviousImageForFade() {
        if(lastImageEdit != null && lastImageEdit.isAlive()) {
            return lastImageEdit.getBackupImage();
        }

        return null;
    }

    /**
     * Used for the name of the fade menu item
     */
    public static String getFadePresentationName() {
        String ur = undoManager.getUndoOrRedoPresentationName();

        return "Fade " + ur.substring(5); // cut off "Undo " or "Redo "
    }
}

