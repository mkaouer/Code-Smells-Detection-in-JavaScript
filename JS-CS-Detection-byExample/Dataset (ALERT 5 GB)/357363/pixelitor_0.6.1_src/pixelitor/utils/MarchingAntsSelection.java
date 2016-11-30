/*
 * Copyright 2009-2010 László Balázs-Csíki
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

package pixelitor.utils;

import javax.swing.*;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MarchingAntsSelection {

    public enum SelectionType {
        REPLACE, ADD, SUBTRACT
    }

    private float dashPhase;
    private Component c;
    private Timer tmr;
    private Shape selectionShape;

    public MarchingAntsSelection(Component c, Point start, Point end) {
        super();
        this.c = c;

        updateSelection(start, end);
    }

    public final void updateSelection(Point start, Point end) {
        int x = Math.min(end.x, start.x);
        int y = Math.min(end.y, start.y);
        int endX = Math.max(end.x, start.x);
        int endY = Math.max(end.y, start.y);

        int width = endX - x;
        int height = endY - y;

        selectionShape = new Rectangle(x, y, width, height);
    }

    public void startMarching() {
        tmr = new Timer(100, null);
        tmr.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                dashPhase++;

// TODO some optimisations are possible here, but the following is too simplistic
//				c.repaint(x, y, width + 1, height + 1);

                c.repaint();
            }
        });
        tmr.start();
    }

    public void stopMarching() {
        tmr.stop();
        c = null;
    }

    public void paintTheAnts(Graphics2D g2) {
        g2.setPaint(Color.WHITE);
        Stroke stroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_BEVEL, 0.0f, new float[]{4, 2, 6, 2},
                dashPhase);
        g2.setStroke(stroke);
        g2.draw(selectionShape);
    }

    public Shape getSelectionShape() {
        return selectionShape;
    }
}
