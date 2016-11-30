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
    OP_WITHOUT_DIALOG(Backup.YES, SizeChanged.NO, UpdateHistogram.YES, NewImage.YES) {
    }, OP_PREVIEW(Backup.NO, SizeChanged.NO, UpdateHistogram.YES, NewImage.YES) {
    }, OP_WITH_PREVIEW_FINISHED(Backup.YES, SizeChanged.NO, UpdateHistogram.YES, NewImage.NO) {
    }, TOOL(Backup.NO, SizeChanged.NO, UpdateHistogram.YES, NewImage.NO) {
    }, FIRST_TIME_INIT(Backup.NO, SizeChanged.YES, UpdateHistogram.YES, NewImage.YES) {
    }, NEW_EMPTY_LAYER(Backup.NO, SizeChanged.NO, UpdateHistogram.NO, NewImage.NO) {
    }, UNDO_REDO(Backup.YES, SizeChanged.NO, UpdateHistogram.YES, NewImage.YES) {
    }, CROP(Backup.YES, SizeChanged.YES, UpdateHistogram.YES, NewImage.YES) {
    }, RESIZE(Backup.YES, SizeChanged.YES, UpdateHistogram.NO, NewImage.YES) {
    }, PERFORMANCE_TEST(Backup.NO, SizeChanged.NO, UpdateHistogram.NO, NewImage.YES) {
    };

    private Backup backupStrategy;
    private SizeChanged sizeChangedStr;
    private UpdateHistogram updateHistogramStr;
    private NewImage newImageStr;

    public boolean makeBackup() {
        return backupStrategy.makeBackup();
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

    private ImageChangeReason(Backup backupStrategy, SizeChanged sizeChangedStr, UpdateHistogram updateHistogramStr, NewImage newImageStr) {
        this.backupStrategy = backupStrategy;
        this.sizeChangedStr = sizeChangedStr;
        this.updateHistogramStr = updateHistogramStr;
        this.newImageStr = newImageStr;
    }

//    public void setLastNotUndoRedoReason(ImageChangeReason lastNotUndoRedoReason) {
//        if (lastNotUndoRedoReason != UNDO_REDO) {
//            this.lastNotUndoRedoReason = lastNotUndoRedoReason;
//        }
//    }

//    protected ImageChangeReason getLastNotUndoRedoReason() {
//        return lastNotUndoRedoReason;
//    }

//    public void setUpdateHistogram(boolean updateHistogram) {
//        this.updateHistogram = updateHistogram;
//    }
//
//    public void setSizeChanged(boolean sizeChanged) {
//        this.sizeChanged = sizeChanged;
//    }

//    private ImageChangeReason lastNotUndoRedoReason;
//    protected boolean updateHistogram;
//    protected boolean sizeChanged;

    private static enum Backup {
        YES {
            @Override
            public boolean makeBackup() {
                return true;
            }},
        NO {
            @Override
            public boolean makeBackup() {
                return false;
            }};

        public abstract boolean makeBackup();
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
//        } , MAYBE {
//            @Override
//            public boolean sizeChanged() {
//                throw new IllegalStateException("should be overwritten");
//            }
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
//        }, MAYBE {
//            @Override
//            public boolean updateHistogram() {
//                throw new IllegalStateException("should be overwritten");
//            }
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
}


