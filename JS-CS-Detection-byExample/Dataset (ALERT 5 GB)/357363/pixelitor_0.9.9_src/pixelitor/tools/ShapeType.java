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

import org.jdesktop.swingx.geom.Star2D;
import pixelitor.tools.shapes.Bat;
import pixelitor.tools.shapes.Cat;
import pixelitor.tools.shapes.Heart;
import pixelitor.tools.shapes.Kiwi;
import pixelitor.tools.shapes.Rabbit;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

/**
 *
 */
public enum ShapeType {
    RECTANGLE {
        @Override
        public Shape getShape(Point start, Point end) {
            updateCoordinatesPositive(start, end);
            return new    Rectangle(x, y, width, height);
        }
        @Override
        public String toString() {
            return "Rectangle";
        }
    }, ELLIPSE {
        @Override
        public Shape getShape(Point start, Point end) {
            updateCoordinatesPositive(start, end);
            return new Ellipse2D.Float(x, y, width, height);
        }
        @Override
        public String toString() {
            return "Ellipse";
        }
    }, LINE {
        @Override
        public Shape getShape(Point start, Point end) {
//            updateCoordinatesPositive(start, end);
            return new Line2D.Float(start.x, start.y, end.x, end.y);
        }
        @Override
        public String toString() {
            return "Line";
        }
    }, HEART {
        @Override
        public Shape getShape(Point start, Point end) {
            updateCoordinates(start, end);
            return new Heart(x, y, width, height);
        }
        @Override
        public String toString() {
            return "Heart";
        }
    }, STAR {
        @Override
        public Shape getShape(Point start, Point end) {
            updateCoordinates(start, end);
            double halfWidth = ((double) width) / 2;
            double halfHeight = ((double) height) / 2;
            double cx = x + halfWidth;
            double cy = y + halfHeight;
            double innerRadius;
            double outerRadius;
            if (width > height) {
                innerRadius = halfHeight;
                outerRadius = halfWidth;
            } else if (height > width) {
                innerRadius = halfWidth;
                outerRadius = halfHeight;
            } else { // TODO
                innerRadius = halfWidth;
                outerRadius = innerRadius + 0.01;
            }

            return new Star2D(cx, cy, innerRadius, outerRadius, 7);
        }
        @Override
        public String toString() {
            return "Star";
        }
    }, CAT {
        @Override
        public Shape getShape(Point start, Point end) {
            updateCoordinates(start, end);
            return new Cat(x, y, width, height);
        }
        @Override
        public String toString() {
            return "Cat";
        }
    }, KIWI {
        @Override
        public Shape getShape(Point start, Point end) {
            updateCoordinates(start, end);
            return new Kiwi(x, y, width, height);
        }
        @Override
        public String toString() {
            return "Kiwi";
        }
    }, BAT {
        @Override
        public Shape getShape(Point start, Point end) {
            updateCoordinates(start, end);
            return new Bat(x, y, width, height);
        }
        @Override
        public String toString() {
            return "Bat";
        }
    }, RABBIT {
        @Override
        public Shape getShape(Point start, Point end) {
            updateCoordinates(start, end);
            return new Rabbit(x, y, width, height);
        }
        @Override
        public String toString() {
            return "Rabbit";
        }

    };

    /**
     * Update the x, y, width, height coordinates so that width and height are positive
     */
    protected void updateCoordinatesPositive(Point start, Point end) {
        x = Math.min(end.x, start.x);
        y = Math.min(end.y, start.y);
        int endX = Math.max(end.x, start.x);
        int endY = Math.max(end.y, start.y);

        width = endX - x;
        height = endY - y;
    }

    /**
     * Update the x, y, width, height coordinates
     */
    protected void updateCoordinates(Point start, Point end) {
        x = start.x;
        y = start.y;
        width = end.x - start.x;
        height = end.y - start.y;
    }

    protected int x, y, width, height;

    public abstract Shape getShape(Point start, Point end);

}
