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
package pixelitor.utils;

import pixelitor.Composition;
import pixelitor.selection.Selection;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 *
 */
public final class Assertions {
    /**
     * Utility class with static methods, do not instantiate
     */
    private Assertions() {
    }


    public static boolean checkRectangleIsInImage(Rectangle bounds, BufferedImage src) {
        Rectangle imageBounds = new Rectangle(0, 0, src.getWidth(), src.getHeight());

        boolean isOK = imageBounds.contains(bounds);

        System.out.println("Assertions.checkRectangleIsInImage imageBounds = " + imageBounds);
        System.out.println("Assertions.checkRectangleIsInImage bounds = " + bounds);

        return isOK;
    }

    public static boolean checkRectangleIsInComposition(Rectangle bounds, Composition comp) {
        Rectangle canvasBounds = comp.getCanvasBounds();
        boolean isOK = canvasBounds.contains(bounds);

        System.out.println("Assertions.checkRectangleIsInComposition canvasBounds = " + canvasBounds);
        System.out.println("Assertions.checkRectangleIsInComposition bounds = " + bounds);

        return isOK;
    }

    public static boolean checkSelectionBoundsAreInsideComposition(Composition comp) {
        System.out.println("Assertions.checkSelectionBoundsAreInsideComposition CALLED");

        Selection selection = comp.getSelection();
        if (selection == null) {
            return true;
        }
        return checkRectangleIsInComposition(selection.getShapeBounds(), comp);
    }
}