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

import com.bric.awt.CalligraphyStroke;
import com.jhlabs.awt.CompositeStroke;
import com.jhlabs.awt.ShapeStroke;
import com.jhlabs.awt.WobbleStroke;
import com.jhlabs.awt.ZigzagStroke;
import pixelitor.tools.shapes.Kiwi;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.Stroke;

/**
 *
 */
public enum StrokeType {
    BASIC {
        @Override
        public Stroke getStroke(float width, int cap, int join, float[] dashFloats) {
            Stroke stroke = new BasicStroke(width, cap, join, 1.5f,
                    dashFloats,
                    0.0f);
            return stroke;
        }

        @Override
        public String toString() {
            return "Basic";
        }
    }, WOBBLE {
        private float lastWidth = 0.0f;
        private WobbleStroke wobbleStroke;

        @Override
        public Stroke getStroke(float width, int cap, int join, float[] dashFloats) {
            if (wobbleStroke == null) {
                wobbleStroke = new WobbleStroke(0.5f, width, 10);
                lastWidth = width;
                return wobbleStroke;
            }

            if (width == lastWidth) {
                // avoids calling new WobbleStroke objects, the main benefit is that
                // the seed is not changed when the mouse is released
                return wobbleStroke;
            } else {
                wobbleStroke = new WobbleStroke(0.5f, width, 10);
                lastWidth = width;
                return wobbleStroke;
            }
        }

        @Override
        public Stroke getInnerStroke() {
            return null; // TODO this should have one
        }

        @Override
        public String toString() {
            return "Wobble";
        }
    }, ZIGZAG {
        private Stroke tmp;

        @Override
        public Stroke getStroke(float width, int cap, int join, float[] dashFloats) {
            tmp = BASIC.getStroke(width, cap, join, dashFloats);
            Stroke stroke = new ZigzagStroke(tmp, width, width);
            return stroke;
        }

        @Override
        public Stroke getInnerStroke() {
            return tmp;
        }
        @Override
        public String toString() {
            return "Zigzag";
        }
    }, OUTLINE {
        private static final float INNER_WIDTH = 0.5f;
        public BasicStroke innerStroke = new BasicStroke(INNER_WIDTH);

        @Override
        public Stroke getStroke(float width, int cap, int join, float[] dashFloats) {
            Stroke stroke = new CompositeStroke(
                    new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND),
                    innerStroke);
            return stroke;
        }

        @Override
        public Stroke getInnerStroke() {
            return innerStroke;
        }

        @Override
        public String toString() {
            return "Outline";
        }
    }, CALLIGRAPHY {
        @Override
        public Stroke getStroke(float width, int cap, int join, float[] dashFloats) {
            return new CalligraphyStroke(width);
        }

        @Override
        public String toString() {
            return "Calligraphy";
        }
    }, KIWI {
        @Override
        public Stroke getStroke(float width, int cap, int join, float[] dashFloats) {
            int size = (int) width;
            Shape oneKiwi = new Kiwi(0, 0, size, size);
            float advance = width * 1.2f;
            if (dashFloats != null) {
                advance *= 2.0f; // simulate dashes
            }
            return new ShapeStroke(oneKiwi, advance);
        }

        @Override
        public String toString() {
            return "Kiwi";
        }
    };

    /**
     * Some strokes have an inner stoke. They can return here a non-null value
     *
     * @return
     */
    public Stroke getInnerStroke() {
        return null;
    }

    public abstract Stroke getStroke(float width, int cap, int join, float[] dashFloats);

    /**
     * A simple getter for the brushes
     */
    public Stroke getStroke(float width) {
        return getStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, null);
    }
}

