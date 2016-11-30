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

/**
 *
 */
public enum ZoomLevel {
    Z50 {
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
            return Z50;
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
    };

    public abstract int getValue();

    public abstract ZoomLevel increase();

    public abstract ZoomLevel decrease();

    private ZoomMenuItem menuItem = new ZoomMenuItem(this);

    public ZoomMenuItem getMenuItem() {
        return menuItem;
    }
}
