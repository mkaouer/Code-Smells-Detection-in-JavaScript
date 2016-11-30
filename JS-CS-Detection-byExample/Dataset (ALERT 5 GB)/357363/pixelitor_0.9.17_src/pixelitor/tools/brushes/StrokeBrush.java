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
package pixelitor.tools.brushes;

import pixelitor.tools.StrokeType;

import java.awt.Graphics2D;
import java.awt.Stroke;

/**
 *
 */
public class StrokeBrush implements Brush {
    private StrokeType strokeType;

    float lastThickness = -1;
    Stroke lastStroke;

    public StrokeBrush(StrokeType strokeType) {
        this.strokeType = strokeType;
    }

    @Override
    public void drawPoint(Graphics2D g, int x, int y, float diameter) {

    }

    @Override
    public void drawLine(Graphics2D g, int startX, int startY, int endX, int endY, float thickness) {
        if (thickness != lastThickness) {
            lastStroke = strokeType.getStroke(thickness);
            lastThickness = thickness;
        }

        g.setStroke(lastStroke);

        g.drawLine(startX, startY, endX, endY);
    }
}
