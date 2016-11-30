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

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;

/**
 *
 */
public class OutlineBrush implements Brush {
    private int lastThickness = -1;
    private Stroke lastStroke;
    private static final float INNER_WIDTH = 0.5f;

    @Override
    public void drawPoint(Graphics2D g, int x, int y, int diameter) {
        g.drawOval(x, y, diameter, diameter); // TODO the value of INNER_WIDTH is not respected
    }

    @Override
    public void drawLine(Graphics2D g, int startX, int startY, int endX, int endY, int thickness) {
        if(thickness != lastThickness) {
            lastStroke = new CompositeStroke(
                    new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND),
                    new BasicStroke(INNER_WIDTH) ) ;
        }
        lastThickness = thickness;

        g.setStroke(lastStroke);

        g.drawLine(startX, startY, endX, endY);
    }
}