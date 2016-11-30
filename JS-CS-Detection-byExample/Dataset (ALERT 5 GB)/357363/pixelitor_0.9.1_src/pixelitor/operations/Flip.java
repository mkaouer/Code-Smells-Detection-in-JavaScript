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

package pixelitor.operations;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public final class Flip extends Operation {
    private Direction direction;

    public enum Direction {
        HORIZONTAL {
            @Override
            String getName() {
                return "Flip Horizontal";
            }},
        VERTICAL {
            @Override
            String getName() {
                return "Flip Vertical";
            }};

        abstract String getName();
    }

    private static Flip horizontalFlip = new Flip(Direction.HORIZONTAL);
    private static Flip verticalFlip = new Flip(Direction.VERTICAL);

    public static Flip createFlipOp(Direction dir) {
        if (dir == Direction.HORIZONTAL) {
            return horizontalFlip;
        } else if (dir == Direction.VERTICAL) {
            return verticalFlip;
        }
        throw new IllegalStateException("should not get here");
    }

    private Flip(Direction dir) {
        super(dir.getName());
        direction = dir;
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        Graphics2D g2 = dest.createGraphics();
        int width = dest.getWidth();
        int height = dest.getHeight();

        if (direction == Direction.HORIZONTAL) {
            g2.translate(width, 0);
            g2.scale(-1, 1);
        } else {
            g2.translate(0, height);
            g2.scale(1, -1);
        }

        g2.drawImage(src, 0, 0, width, height, null);
        g2.dispose();
        return dest;
    }
}
