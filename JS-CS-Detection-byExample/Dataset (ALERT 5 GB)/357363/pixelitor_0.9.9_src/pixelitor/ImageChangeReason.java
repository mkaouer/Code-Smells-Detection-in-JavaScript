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

public enum ImageChangeReason {
    OP_WITHOUT_DIALOG           (SizeChanged.NO,  UpdateHistogram.YES, NewImage.YES, MakeUndoBackup.YES) {
    }, OP_PREVIEW               (SizeChanged.NO,  UpdateHistogram.YES, NewImage.YES, MakeUndoBackup.NO) {
    }, OP_WITH_PREVIEW_FINISHED (SizeChanged.NO,  UpdateHistogram.NO,  NewImage.NO,  MakeUndoBackup.YES) {
    }, COMPOSITION_INIT         (SizeChanged.YES, UpdateHistogram.YES, NewImage.YES, MakeUndoBackup.NO) {
    }, UNDO_REDO                (SizeChanged.NO,  UpdateHistogram.YES, NewImage.YES, MakeUndoBackup.NO) {
    }, PERFORMANCE_TEST         (SizeChanged.NO,  UpdateHistogram.NO,  NewImage.YES, MakeUndoBackup.NO) {
    };

    private SizeChanged sizeChangedStr;
    private UpdateHistogram updateHistogramStr;
    private NewImage newImageStr;
    private MakeUndoBackup makeUndoBackupStr;

    public boolean makeUndoBackup() {
        return makeUndoBackupStr.makeUndoBackup();
    }

    public boolean updateHistogram() {
        return updateHistogramStr.updateHistogram();
    }

    public boolean sizeChanged() {
        return sizeChangedStr.sizeChanged();
    }

    /**
     * Returns true if a new image is set, indicating that the BufferedImage object should be replaced and
     * repaint should be called
     */
    public boolean setNewImage() {
        return newImageStr.setNewImage();
    }

//    public void reset() {
//        // can be overridden if necessary
//    }

    private ImageChangeReason(SizeChanged sizeChangedStr, UpdateHistogram updateHistogramStr, NewImage newImageStr, MakeUndoBackup makeUndoBackupStr) {
        this.sizeChangedStr = sizeChangedStr;
        this.updateHistogramStr = updateHistogramStr;
        this.newImageStr = newImageStr;
        this.makeUndoBackupStr = makeUndoBackupStr;
    }

    private static enum SizeChanged {
        YES {
            @Override
            public boolean sizeChanged() {
                return true;
            }
        }, NO {
            @Override
            public boolean sizeChanged() {
                return false;
            }
        };

        public abstract boolean sizeChanged();
    }

    private static enum UpdateHistogram {
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


