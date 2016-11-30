/*
 * Copyright 2010 Laszlo Balazs-Csiki
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
package pixelitor.filters.gui;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * A GUI component that can be used to select an angle with the mouse
 */
public class AngleSelectorComponent extends AbstractAngleSelectorComponent {

    public AngleSelectorComponent(AngleParam angleParam) {
        super(angleParam);

        cx = SIZE / 2;
        cy = SIZE / 2;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        Ellipse2D.Float ellipse = new Ellipse2D.Float(0, 0, SIZE, SIZE);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.draw(ellipse);

        double angle = model.getValueInRadians();

        int radius = SIZE / 2;
        float endX = (float) (cx + (radius * Math.cos(angle)));
        float endY = (float) (cy + (radius * Math.sin(angle)));

        drawArrow(g2, angle, cx, cy, endX, endY);
    }


}
