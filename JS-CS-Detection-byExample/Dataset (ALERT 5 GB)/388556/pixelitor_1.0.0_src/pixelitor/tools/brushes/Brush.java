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

import java.awt.Graphics2D;

/**
 *
 */
public interface Brush {
    /**
     * @param g
     * @param x        the x of the mouse event translated with the radius
     * @param y        the y of the mouse event translated with the radius
     * @param diameter 2*radius
     */
    void drawPoint(Graphics2D g, int x, int y, float diameter);

    /**
     * @param g
     * @param startX   the x of the first mouse drag event
     * @param startY   the y of the first mouse drag event
     * @param endX     the x of the second mouse drag event
     * @param endY     the y of the second mouse drag event
     * @param diameter 2*radius
     */
    void drawLine(Graphics2D g, int startX, int startY, int endX, int endY, float diameter);
}
