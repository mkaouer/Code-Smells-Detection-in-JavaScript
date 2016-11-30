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
public enum Symmetry {
    NO_SYMMETRY {
        @Override
        public String toString() {
            return "None";
        }
        @Override
        public void drawPoint(AbstractBrushTool tool, int x, int y) {
            tool.drawPoint(x, y);
        }
        @Override
        public void drawLine(AbstractBrushTool tool, int startX, int startY, int endX, int endY) {
            tool.drawLine(startX, startY, endX, endY);
        }
    }, VERTICAL_MIRROR {
        @Override
        public String toString() {
            return "Vertical";
        }
        @Override
        public void drawPoint(AbstractBrushTool tool, int x, int y) {
            tool.drawPoint(x, y);
            tool.drawPoint(compositionWidth - x, y);
        }
        @Override
        public void drawLine(AbstractBrushTool tool, int startX, int startY, int endX, int endY) {
            tool.drawLine(startX, startY, endX, endY);
            tool.drawLine(compositionWidth - startX, startY, compositionWidth - endX, endY);
        }
    }, HORIZONTAL_MIRROR {
        @Override
        public String toString() {
            return "Horizontal";
        }
        @Override
        public void drawPoint(AbstractBrushTool tool, int x, int y) {
            tool.drawPoint(x, y);
            tool.drawPoint(x, compositionHeight - y);
        }
        @Override
        public void drawLine(AbstractBrushTool tool, int startX, int startY, int endX, int endY) {
            tool.drawLine(startX, startY, endX, endY);
            tool.drawLine(startX, compositionHeight - startY, endX, compositionHeight - endY);
        }
    }, TWO_MIRRORS {
        @Override
        public String toString() {
            return "Two Mirrors";
        }
        @Override
        public void drawPoint(AbstractBrushTool tool, int x, int y) {
            tool.drawPoint(x, y);
            tool.drawPoint(compositionWidth - x, y);
            tool.drawPoint(x, compositionHeight - y);
            tool.drawPoint(compositionWidth - x, compositionHeight - y);
        }
        @Override
        public void drawLine(AbstractBrushTool tool, int startX, int startY, int endX, int endY) {
            tool.drawLine(startX, startY, endX, endY);
            tool.drawLine(startX, compositionHeight - startY, endX, compositionHeight - endY);
            tool.drawLine(compositionWidth - startX, startY, compositionWidth - endX, endY);
            tool.drawLine(compositionWidth - startX, compositionHeight - startY, compositionWidth - endX, compositionHeight - endY);
        }
    }, CENTRAL_SYMMETRY {
        @Override
        public String toString() {
            return "Central Symmetry";
        }
        @Override
        public void drawPoint(AbstractBrushTool tool, int x, int y) {
            tool.drawPoint(x, y);
            tool.drawPoint(compositionWidth - x, compositionHeight - y);
        }
        @Override
        public void drawLine(AbstractBrushTool tool, int startX, int startY, int endX, int endY) {
            tool.drawLine(startX, startY, endX, endY);
            tool.drawLine(compositionWidth - startX, compositionHeight - startY, compositionWidth - endX, compositionHeight - endY);
        }
    };

    private static int compositionWidth;
    private static int compositionHeight;

    public static void setCompositionSize(int w, int h) {
        compositionWidth = w;
        compositionHeight = h;
    }

    public abstract void drawPoint(AbstractBrushTool tool, int x, int y);

    public abstract void drawLine(AbstractBrushTool tool, int startX, int startY, int endX, int endY);
}
