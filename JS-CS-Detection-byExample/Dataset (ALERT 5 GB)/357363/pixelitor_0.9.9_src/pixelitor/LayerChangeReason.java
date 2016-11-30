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


public enum LayerChangeReason {
    COMPOSITION_INIT           (UpdateHistogram.YES) {
    }, NEW_EMPTY_LAYER         (UpdateHistogram.NO) {
    }, NEW_LAYER_WITH_CONTENT  (UpdateHistogram.YES) {
    }, UNDO_REDO               (UpdateHistogram.YES) {
    };

    private UpdateHistogram updateHistogramStr;

    public boolean updateHistogram() {
        return updateHistogramStr.updateHistogram();
    }


    private LayerChangeReason(UpdateHistogram updateHistogramStr) {
        this.updateHistogramStr = updateHistogramStr;
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
}