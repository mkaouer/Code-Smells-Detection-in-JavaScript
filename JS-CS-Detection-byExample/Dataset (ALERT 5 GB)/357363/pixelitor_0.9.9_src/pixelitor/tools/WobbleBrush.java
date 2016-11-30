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

import com.jhlabs.awt.CompositeStroke;
import com.jhlabs.awt.WobbleStroke;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;

/**
 *
 */
public class WobbleBrush implements Brush {
    private int lastThickness = -1;
    private Stroke lastStroke;

    @Override
    public void drawPoint(Graphics2D g, int x, int y, int thickness) {
        float smallThickness = thickness / 5.0f;

        if(thickness != lastThickness) {
            lastStroke = new WobbleStroke(0.5f, smallThickness);
        }
        g.setStroke(lastStroke);

        Ellipse2D.Float circle = new Ellipse2D.Float(x, y, smallThickness, smallThickness);

        g.draw(circle);
    }

    @Override
    public void drawLine(Graphics2D g, int startX, int startY, int endX, int endY, int thickness) {
        if(thickness != lastThickness) {
            lastStroke = new WobbleStroke(0.5f, thickness/5.0f);
        }
        g.setStroke(lastStroke);

        int halfThickness = thickness / 2; // for some reason (WobbleStroke?), this needs to be subtracted
        g.drawLine(startX - halfThickness, startY - halfThickness, endX - halfThickness, endY - halfThickness);
    }
}
