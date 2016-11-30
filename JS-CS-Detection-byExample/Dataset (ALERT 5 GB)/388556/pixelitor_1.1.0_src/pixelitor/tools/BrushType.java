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
package pixelitor.tools;

import pixelitor.tools.brushes.Brush;
import pixelitor.tools.brushes.CalligraphyBrush;
import pixelitor.tools.brushes.IdealBrush;
import pixelitor.tools.brushes.ImageBrush;
import pixelitor.tools.brushes.ImageBrushType;
import pixelitor.tools.brushes.OutlineBrush;
import pixelitor.tools.brushes.WobbleBrush;

/**
 *
 */
enum BrushType {
    IDEAL {
        private IdealBrush idealBrush;

        @Override
        public String toString() {
            return "Hard";
        }
        @Override
        public Brush getBrush() {
            if (idealBrush == null) {
                idealBrush = new IdealBrush();
            }
            return idealBrush;
        }
    }, SOFT {
        private ImageBrush imageBrush;

        @Override
        public String toString() {
            return "Soft";
        }
        @Override
        public Brush getBrush() {
            if (imageBrush == null) {
                imageBrush = new ImageBrush(ImageBrushType.SOFT);
            }
            return imageBrush;
        }
    }, REAL {
        private ImageBrush imageBrush;

        @Override
        public String toString() {
            return "Real";
        }
        @Override
        public Brush getBrush() {
            if (imageBrush == null) {
                imageBrush = new ImageBrush(ImageBrushType.REAL);
            }
            return imageBrush;
        }
    }, HAIR {
        private ImageBrush imageBrush;

        @Override
        public String toString() {
            return "Hair";
        }
        @Override
        public Brush getBrush() {
            if (imageBrush == null) {
                imageBrush = new ImageBrush(ImageBrushType.HAIR);
            }
            return imageBrush;
        }
    }, WOBBLE {
        private WobbleBrush wobbleBrush;

        @Override
        public String toString() {
            return "Wobble";
        }
        @Override
        public Brush getBrush() {
            if (wobbleBrush == null) {
                wobbleBrush = new WobbleBrush();
            }
            return wobbleBrush;
        }
    }, CALLIGRAPHY {
        private CalligraphyBrush strokeBrush;

        @Override
        public String toString() {
            return "Calligraphy";
        }
        @Override
        public Brush getBrush() {
            if (strokeBrush == null) {
                strokeBrush = new CalligraphyBrush();
            }
            return strokeBrush;
        }
    }, OUTLINE {
        private OutlineBrush outlineBrush;

        @Override
        public String toString() {
            return "Outline";
        }
        @Override
        public Brush getBrush() {
            if (outlineBrush == null) {
                outlineBrush = new OutlineBrush();
            }
            return outlineBrush;
        }
    };

    public abstract Brush getBrush();

}
