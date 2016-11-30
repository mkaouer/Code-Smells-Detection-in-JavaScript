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

/**
*
*/
enum BrushType {
    IDEAL {
        private IdealBrush idealBrush;

        @Override
        public String toString() {
            return "Ideal";
        }
        @Override
        public Brush getBrush() {
            if(idealBrush == null) {
                idealBrush = new IdealBrush();
            }
            return idealBrush;
        }
    }, REAL {
        private RealBrush realBrush;

        @Override
        public String toString() {
            return "Real";
        }
        @Override
        public Brush getBrush() {
            if(realBrush == null) {
                realBrush = new RealBrush();
            }
            return realBrush;
        }
    }, WOBBLE {
        private WobbleBrush wobbleBrush;

        @Override
        public String toString() {
            return "Wobble";
        }
        @Override
        public Brush getBrush() {
            if(wobbleBrush == null) {
                wobbleBrush = new WobbleBrush();
            }
            return wobbleBrush;
        }
    }, OUTLINE {
        private OutlineBrush outlineBrush;

        @Override
        public String toString() {
            return "Outline";
        }
        @Override
        public Brush getBrush() {
            if(outlineBrush == null) {
                outlineBrush = new OutlineBrush();
            }
            return outlineBrush;
        }
    };


    public abstract Brush getBrush();


}
