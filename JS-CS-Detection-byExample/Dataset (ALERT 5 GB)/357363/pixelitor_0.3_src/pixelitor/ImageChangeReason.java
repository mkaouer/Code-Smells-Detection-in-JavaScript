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

package pixelitor;

public enum ImageChangeReason {
    OP_WITHOUT_DIALOG(BackupStrategy.MAKE_BACKUP, SizeChangedStr.MAYBE, UpdateHistogramStr.MAYBE, NewImageStr.YES) {
        @Override
        public void reset() {
            updateHistogram = true;
            sizeChanged = false;
        }

        @Override
        public boolean updateHistogram() {
            return updateHistogram;
        }

        @Override
        public boolean sizeChanged() {
            return sizeChanged;
        }
    }, OP_PREVIEW(BackupStrategy.NO_BACKUP, SizeChangedStr.NO, UpdateHistogramStr.YES, NewImageStr.YES) {
    }, OP_WITH_PREVIEW_FINISHED(BackupStrategy.MAKE_BACKUP, SizeChangedStr.MAYBE, UpdateHistogramStr.MAYBE, NewImageStr.NO) {
        @Override
        public void reset() {
            updateHistogram = true;
            sizeChanged = false;
        }

        @Override
        public boolean updateHistogram() {
            return updateHistogram;
        }

        @Override
        public boolean sizeChanged() {
            return sizeChanged;
        }
    }, PAINT_TOOL(BackupStrategy.NO_BACKUP, SizeChangedStr.NO, UpdateHistogramStr.YES, NewImageStr.NO) {
    }, FIRST_TIME_INIT(BackupStrategy.NO_BACKUP, SizeChangedStr.YES, UpdateHistogramStr.YES, NewImageStr.YES) {
    }, NEW_EMPTY_LAYER(BackupStrategy.NO_BACKUP, SizeChangedStr.NO, UpdateHistogramStr.NO, NewImageStr.NO) {
    }, CROP(BackupStrategy.MAKE_BACKUP, SizeChangedStr.YES, UpdateHistogramStr.YES, NewImageStr.YES) {
    }, UNDO(BackupStrategy.MAKE_BACKUP, SizeChangedStr.MAYBE, UpdateHistogramStr.MAYBE, NewImageStr.YES) {
        @Override
        public boolean updateHistogram() {
            return getPreviousReason().updateHistogram();
        }

        @Override
        public boolean sizeChanged() {
            return getPreviousReason().sizeChanged();
        }
    }, RESIZE(BackupStrategy.MAKE_BACKUP, SizeChangedStr.YES, UpdateHistogramStr.NO, NewImageStr.YES) {
    }, PERFORMANCE_TEST(BackupStrategy.NO_BACKUP, SizeChangedStr.NO, UpdateHistogramStr.NO, NewImageStr.YES) {
    };
    private BackupStrategy backupStrategy;
    private SizeChangedStr sizeChangedStr;
    private UpdateHistogramStr updateHistogramStr;
    private NewImageStr newImageStr;

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

    public void reset() {
        // can be overridden if necessary
    }

    private ImageChangeReason(BackupStrategy backupStrategy, SizeChangedStr sizeChangedStr, UpdateHistogramStr updateHistogramStr, NewImageStr newImageStr) {
        this.backupStrategy = backupStrategy;
        this.sizeChangedStr = sizeChangedStr;
        this.updateHistogramStr = updateHistogramStr;
        this.newImageStr = newImageStr;
    }

    public void setPreviousReason(ImageChangeReason previousReason) {
        if (previousReason != UNDO) {
            this.previousReason = previousReason;
        }
    }

    protected ImageChangeReason getPreviousReason() {
        return previousReason;
    }

    public void setUpdateHistogram(boolean updateHistogram) {
        this.updateHistogram = updateHistogram;
    }

    public void setSizeChanged(boolean sizeChanged) {
        this.sizeChanged = sizeChanged;
    }

    private ImageChangeReason previousReason;
    protected boolean updateHistogram;
    protected boolean sizeChanged;

}

enum BackupStrategy {
    MAKE_BACKUP {
        @Override
        public boolean makeBackup() {
            return true;
        }},
    NO_BACKUP {
        @Override
        public boolean makeBackup() {
            return false;
        }};

    public abstract boolean makeBackup();
}

enum SizeChangedStr {
    YES {
        @Override
        public boolean sizeChanged() {
            return true;
        }},
    NO {
        @Override
        public boolean sizeChanged() {
            return false;
        }},
    MAYBE {
        @Override
        public boolean sizeChanged() {
            throw new IllegalStateException("should be overwritten");
        }};

    public abstract boolean sizeChanged();
}

enum UpdateHistogramStr {
    YES {
        @Override
        public boolean updateHistogram() {
            return true;
        }},
    NO {
        @Override
        public boolean updateHistogram() {
            return false;
        }},
    MAYBE {
        @Override
        public boolean updateHistogram() {
            throw new IllegalStateException("should be overwritten");
        }};

    public abstract boolean updateHistogram();
}

enum NewImageStr {
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