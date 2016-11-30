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

public enum ChangeReason {
    OP_WITHOUT_DIALOG(VisualChange.YES, NewImage.YES, MakeUndoBackup.YES) {
    }, OP_PREVIEW(VisualChange.YES, NewImage.YES, MakeUndoBackup.NO) {
    }, OP_WITH_PREVIEW_FINISHED(VisualChange.NO, NewImage.NO, MakeUndoBackup.YES) {
    }, UNDO_REDO(VisualChange.YES, NewImage.YES, MakeUndoBackup.NO) {
    }, PERFORMANCE_TEST(VisualChange.YES, NewImage.YES, MakeUndoBackup.NO) {
    };

    private VisualChange visualChangeStr;
    private NewImage newImageStr;
    private MakeUndoBackup makeUndoBackupStr;

    public boolean makeUndoBackup() {
        return makeUndoBackupStr.makeUndoBackup();
    }

    public boolean repaintAndUpdateHistogram() {
        return visualChangeStr.updateHistogram();
    }

    /**
     * Returns true if a new image is set, indicating that the BufferedImage object should be replaced and
     * repaint should be called
     */
    public boolean setNewImage() {
        return newImageStr.setNewImage();
    }

    private ChangeReason(VisualChange visualChangeStr, NewImage newImageStr, MakeUndoBackup makeUndoBackupStr) {
        this.visualChangeStr = visualChangeStr;
        this.newImageStr = newImageStr;
        this.makeUndoBackupStr = makeUndoBackupStr;
    }

    private static enum VisualChange {
        YES {
            @Override
            public boolean updateHistogram() {
                return true;
            }
        }, NO {
            @Override
            public boolean updateHistogram() {
                return false;
            }
        };

        public abstract boolean updateHistogram();
    }

    private static enum NewImage {
        YES {
            @Override
            public boolean setNewImage() {
                return true;
            }},
        NO {
            @Override
            public boolean setNewImage() {
                return false;
            }};

        public abstract boolean setNewImage();
    }

    private static enum MakeUndoBackup {
        YES {
            @Override
            public boolean makeUndoBackup() {
                return true;
            }},
        NO {
            @Override
            public boolean makeUndoBackup() {
                return false;
            }};

        public abstract boolean makeUndoBackup();
    }

}


