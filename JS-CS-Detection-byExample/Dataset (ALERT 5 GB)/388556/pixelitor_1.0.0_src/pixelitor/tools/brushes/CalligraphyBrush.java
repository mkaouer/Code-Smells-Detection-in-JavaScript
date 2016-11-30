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

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;

/**
 *
 */
public class CalligraphyBrush extends StrokeBrush {
    private static Stroke pointStroke = new BasicStroke(1.2f);

    public CalligraphyBrush() {
        super(StrokeType.CALLIGRAPHY);
    }

    @Override
    public void drawPoint(Graphics2D g, int x, int y, float diameter) {
        float projectedShift = diameter / 1.4142f;
        float projectedStart = (diameter - projectedShift) / 2.0f;
        float projectedEnd = projectedStart + projectedShift;

        int startX = (int) (x + projectedStart);
        int startY = (int) (y + projectedEnd);
        int endX = (int) (x + projectedEnd);
        int endY = (int) (y + projectedStart);

        g.setStroke(pointStroke);

        // for some reasons (rounding errors previously?) these ones have to be added and subtracted
        g.drawLine(startX + 1, startY - 1, endX - 1, endY + 1);

        if (lastStroke != null) {
            g.setStroke(lastStroke);
        }
    }

    @Override
    public void drawLine(Graphics2D g, int startX, int startY, int endX, int endY, float thickness) {
        super.drawLine(g, startX, startY, endX, endY, thickness);

        // for some reason this must be called, otherwise gaps remain
        drawPoint(g, (int) (startX - thickness / 2), (int) (startY - thickness / 2), thickness);
    }
}
