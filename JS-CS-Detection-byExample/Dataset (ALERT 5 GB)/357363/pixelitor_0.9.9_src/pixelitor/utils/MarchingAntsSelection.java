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
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class MarchingAntsSelection {

    public enum SelectionInteractionType {
        REPLACE, ADD, SUBTRACT
    }

    public enum SelectionType {
        RECTANGLE {
            @Override
            public Shape getShape(float x, float y, float width, float height) {
                return new Rectangle2D.Float(x, y, width, height);
            }
        }, ELLIPSE {
            @Override
            public Shape getShape(float x, float y, float width, float height) {
                return new Ellipse2D.Float(x, y, width, height);
            }
        };

        public abstract Shape getShape(float x, float y, float width, float height);
    }

    private float dashPhase;
    private Component c;
    private Timer tmr;
    private Shape selectionShape;

    private SelectionType selectionType = SelectionType.RECTANGLE;

    public MarchingAntsSelection(Component c, Point start, Point end) {
        super();
        this.c = c;

        updateSelection(start, end, false);
    }

    public final void updateSelection(Point start, Point end, boolean startFromCenter) {
        int x, y, width, height;

        if (startFromCenter) {
            int halfWidth = Math.abs(end.x - start.x);
            int halfHeight = Math.abs(end.y - start.y);
            x = start.x - halfWidth;
            y = start.y - halfHeight;
            width = 2 * halfWidth;
            height = 2 * halfHeight;
        } else {
            x = Math.min(end.x, start.x);
            y = Math.min(end.y, start.y);
            int endX = Math.max(end.x, start.x);
            int endY = Math.max(end.y, start.y);

            width = endX - x;
            height = endY - y;
        }

        selectionShape = selectionType.getShape(x, y, width, height);
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
                BasicStroke.JOIN_ROUND, 0.0f, new float[]{4, 4},
                dashPhase);
        g2.setStroke(stroke);
        g2.draw(selectionShape);

        g2.setPaint(Color.BLACK);
        Stroke stroke2 = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_ROUND, 0.0f, new float[]{4, 4},
                dashPhase + 4);
        g2.setStroke(stroke2);
        g2.draw(selectionShape);
    }

    public Shape getSelectionShape() {
        return selectionShape;
    }
}
