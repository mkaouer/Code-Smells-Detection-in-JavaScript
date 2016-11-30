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
package pixelitor;

/**
 * The type of the build.
 */
public enum Build {
    ROBOT_TEST {
    }, TEST {
    }, FINAL {
    };

    public static final Build CURRENT = FINAL;

    public static final String VERSION_NUMBER = "0.9.17";

    public static String getPixelitorWindowTitle() {
        String title = "Pixelitor " + Build.VERSION_NUMBER;
        if (CURRENT != FINAL) {
            title += (" " + CURRENT);
        }
        return title;
    }
}
