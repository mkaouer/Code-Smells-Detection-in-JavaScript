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
public enum ShapesAction {
    FILL(false, true, false, true, false, false) {
        @Override
        public String toString() {
            return "Fill";
        }
    }, STROKE(true, false, true, false, false, true) {
        @Override
        public String toString() {
            return "Stroke";
        }
    }, FILL_AND_STROKE(true, true, true, true, false, true) {
        @Override
        public String toString() {
            return "Fill and Stroke";
        }
    }, SELECTION(false, false, false, false, true, false) {
        @Override
        public String toString() {
            return "Create Selection";
        }
    }, SELECTION_FROM_STROKE(true, false, false, false, true, false) {
        @Override
        public String toString() {
            return "Create Stroked Selection";
        }
    };

    private final boolean enableStrokeSettings;
    private final boolean enableFillPaintSelection;
    private final boolean enableStrokePaintSelection;

    private final boolean stroke;
    private final boolean fill;
    private final boolean createSelection;

    private ShapesAction(boolean enableStrokeSettings, boolean enableFillPaintSelection, boolean stroke, boolean fill, boolean createSelection, boolean enableStrokePaintSelection) {
        this.enableStrokeSettings = enableStrokeSettings;
        this.enableFillPaintSelection = enableFillPaintSelection;
        this.stroke = stroke;
        this.fill = fill;
        this.createSelection = createSelection;
        this.enableStrokePaintSelection = enableStrokePaintSelection;

        if (createSelection) {
            if (stroke || fill) {
                throw new IllegalArgumentException();
            }
        } else {
            if (!stroke && !fill) {
                throw new IllegalArgumentException();
            }
        }
    }

    public boolean enableStrokeSettings() {
        return enableStrokeSettings;
    }

    public boolean enableFillPaintSelection() {
        return enableFillPaintSelection;
    }

    public boolean enableStrokePaintSelection() {
        return enableStrokePaintSelection;
    }


    public boolean stroke() {
        return stroke;
    }

    public boolean fill() {
        return fill;
    }

    public boolean createSelection() {
        return createSelection;
    }

}
