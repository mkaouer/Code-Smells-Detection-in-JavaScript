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

package pixelitor.history;

import pixelitor.AppLogic;
import pixelitor.Composition;
import pixelitor.utils.AppPreferences;
import pixelitor.utils.GUIUtils;

import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;
import javax.swing.undo.UndoableEditSupport;
import java.awt.image.BufferedImage;

/**
 * Manages history and undo/redo for all open images
 */
public final class History {
    private static PixelitorUndoManager undoManager = new PixelitorUndoManager();
    private static UndoableEditSupport undoableEditSupport = new UndoableEditSupport();
    private static int undoDepth = 0; // how deep we are back in time

    // TODO it would be better if PixelitorUndoManager supported canFade, canRepeat, getPreviousImageForFade - undoDepth is not elegant
    private static ImageEdit lastImageEdit;
//    private static PixelitorEdit lastEdit;

    static {
        setUndoLevels(AppPreferences.loadUndoLevels());
    }

    /**
     * Utility class with static methods, do not instantiate
     */
    private History() {
    }

    /**
     * This is used to notify the menu items
     */
    public static void postEdit(UndoableEdit e) {
        undoableEditSupport.postEdit(e);
    }

    public static void addEdit(PixelitorEdit e) {

        if (e == null) {
            throw new IllegalArgumentException("e is null");
        }

        if (e.canUndo()) {
            undoManager.addEdit(e);
        } else {
            undoManager.discardAllEdits();
        }

        if (e instanceof ImageEdit) {
            lastImageEdit = (ImageEdit) e;
        }

        undoableEditSupport.postEdit(e);
        undoDepth = 0;
    }

    public static String getUndoPresentationName() {
        return undoManager.getUndoPresentationName();
    }

    public static String getRedoPresentationName() {
        return undoManager.getRedoPresentationName();
    }

    public static void redo() {
        try {
            undoManager.redo();
        } catch (Exception ex) {
            GUIUtils.showExceptionDialog(ex);
        }
        undoDepth--;
    }

    public static void undo() {
        try {
            undoManager.undo();
        } catch (Exception ex) {
            GUIUtils.showExceptionDialog(ex);
        }
        undoDepth++;
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

    public static boolean canRepeatOperation() {
        if(undoDepth > 0) {
            return false;
        }

        PixelitorEdit lastEdit = undoManager.getLastEdit();
        if (lastEdit != null) {
            return lastEdit.canRepeat();
        }
        return false;
    }

    public static BufferedImage getPreviousImageForFade() {
        if (lastImageEdit != null && lastImageEdit.isAlive()) {
            return lastImageEdit.getBackupImage();
        }

        return null;
    }

    /**
     * Used for the name of the fade/repeat menu items
     */
    public static String getLastPresentationName() {
        PixelitorEdit lastEdit = undoManager.getLastEdit();
        if(lastEdit != null) {
            return lastEdit.getPresentationName();
        }
        return "";
    }

    public static boolean canFade() {
        if(undoDepth > 0) {
            return false;
        }
        PixelitorEdit lastEdit = undoManager.getLastEdit();
        if (lastEdit != null) {
            Composition lastComp = lastEdit.getComp();
            Composition comp = AppLogic.getActiveComp();
            if(comp != lastComp) {
                return false;
            }
            return lastEdit.canFade();
        }
        return false;
    }

    public static void allImagesAreClosed() {
        undoDepth = 0;
        lastImageEdit = null;

        undoManager.discardAllEdits();
        undoableEditSupport.postEdit(null);
    }
}

