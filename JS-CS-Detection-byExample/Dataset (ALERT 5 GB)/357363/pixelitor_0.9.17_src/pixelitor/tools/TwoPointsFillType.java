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

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**
 * A enum whose values return Paint objects based on the two points of a UserDrag
 */
enum TwoPointsFillType {
    LINEAR_GRADIENT {
        @Override
        public Paint getPaint(UserDrag ud) {
            Color fgColor = FgBgColorSelector.getFG();
            Color bgColor = FgBgColorSelector.getBG();

            return new GradientPaint(ud.getStartXFromCenter(), ud.getStartYFromCenter(), fgColor, ud.getEndX(), ud.getEndY(), bgColor);
        }

        @Override
        public String toString() {
            return "Linear Gradient";
        }
    }, RADIAL_GRADIENT {
        private final float[] FRACTIONS = new float[]{0.0f, 1.0f};
        private final AffineTransform gradientTransform = new AffineTransform();

        @Override
        public Paint getPaint(UserDrag userDrag) {
            Color fgColor = FgBgColorSelector.getFG();
            Color bgColor = FgBgColorSelector.getBG();

            Point2D center = userDrag.getCenterPoint();
            float distance = userDrag.getDistance();

            return new RadialGradientPaint(center, distance / 2, center, FRACTIONS, new Color[]{fgColor, bgColor},
                    MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, gradientTransform);
        }

        @Override
        public String toString() {
            return "Radial Gradient";
        }
    }, FOREGROUND {
        @Override
        public Paint getPaint(UserDrag userDrag) {
            Color fgColor = FgBgColorSelector.getFG();
            return fgColor;
        }

        @Override
        public String toString() {
            return "Foreground";
        }
    }, BACKGROUND {
        @Override
        public Paint getPaint(UserDrag userDrag) {
            Color bgColor = FgBgColorSelector.getBG();
            return bgColor;
        }

        @Override
        public String toString() {
            return "Background";
        }
    };

    public abstract Paint getPaint(UserDrag userDrag);
}
