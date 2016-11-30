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
package pixelitor.menus;

import java.util.Random;

/**
 *
 */
public enum ZoomLevel {
    Z25 {
        @Override
        public int getValue() {
            return 25;
        }
        @Override
        public ZoomLevel increase() {
            return Z50;
        }
        @Override
        public ZoomLevel decrease() {
            return Z25;
        }
        @Override
        public String toString() {
            return "25 %";
        }
    }, Z50 {
        @Override
        public int getValue() {
            return 50;
        }
        @Override
        public ZoomLevel increase() {
            return Z100;
        }
        @Override
        public ZoomLevel decrease() {
            return Z25;
        }
        @Override
        public String toString() {
            return "50 %";
        }
    }, Z100 {
        @Override
        public int getValue() {
            return 100;
        }
        @Override
        public ZoomLevel increase() {
            return Z200;
        }
        @Override
        public ZoomLevel decrease() {
            return Z50;
        }
        @Override
        public String toString() {
            return "100 %";
        }
    }, Z200 {
        @Override
        public int getValue() {
            return 200;
        }
        @Override
        public ZoomLevel increase() {
            return Z400;
        }
        @Override
        public ZoomLevel decrease() {
            return Z100;
        }
        @Override
        public String toString() {
            return "200 %";
        }
    }, Z400 {
        @Override
        public int getValue() {
            return 400;
        }
        @Override
        public ZoomLevel increase() {
            return Z800;
        }
        @Override
        public ZoomLevel decrease() {
            return Z200;
        }
        @Override
        public String toString() {
            return "400 %";
        }
    }, Z800 {
        @Override
        public int getValue() {
            return 800;
        }
        @Override
        public ZoomLevel increase() {
            return Z1600;
        }
        @Override
        public ZoomLevel decrease() {
            return Z400;
        }
        @Override
        public String toString() {
            return "800 %";
        }
    }, Z1600 {
        @Override
        public int getValue() {
            return 1600;
        }
        @Override
        public ZoomLevel increase() {
            return Z3200;
        }
        @Override
        public ZoomLevel decrease() {
            return Z800;
        }
        @Override
        public String toString() {
            return "1600 %";
        }
    }, Z3200 {
        @Override
        public int getValue() {
            return 3200;
        }
        @Override
        public ZoomLevel increase() {
            return Z6400;
        }
        @Override
        public ZoomLevel decrease() {
            return Z1600;
        }
        @Override
        public String toString() {
            return "3200 %";
        }
    }, Z6400 {
        @Override
        public int getValue() {
            return 6400;
        }
        @Override
        public ZoomLevel increase() {
            return Z6400;
        }
        @Override
        public ZoomLevel decrease() {
            return Z3200;
        }
        @Override
        public String toString() {
            return "6400 %";
        }
    };

    public abstract int getValue();

    public abstract ZoomLevel increase();

    public abstract ZoomLevel decrease();

    private ZoomMenuItem menuItem = new ZoomMenuItem(this);

    public ZoomMenuItem getMenuItem() {
        return menuItem;
    }

    public static ZoomLevel getRandomZoomLevel(Random rand) {
        int index = 1 + rand.nextInt(3);
        return values()[index];
    }
}
