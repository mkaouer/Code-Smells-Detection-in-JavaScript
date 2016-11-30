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
    DEVELOPMENT {
        private boolean robotTest = false;

        @Override
        public boolean isRobotTest() {
            return robotTest;
        }
        @Override
        public void setRobotTest(boolean robotTest) {
            this.robotTest = robotTest;
            fixTitle = null;
        }
    }, FINAL {
        @Override
        public boolean isRobotTest() {
            return false;
        }
        @Override
        public void setRobotTest(boolean robotTest) {
            // do nothing - no way
        }
    };


    public static Build CURRENT = FINAL;

    public static final String VERSION_NUMBER = "1.1.2";

    private static String fixTitle = null;

    public static String getPixelitorWindowFixTitle() {
        if (fixTitle == null) {
            fixTitle = "Pixelitor " + Build.VERSION_NUMBER;
            if (CURRENT != FINAL) {
                fixTitle += " DEVELOPMENT";
            }
            if (CURRENT.isRobotTest()) {
                fixTitle += " - ROBOT TEST";
            }
        }

        return fixTitle;
    }

    public abstract boolean isRobotTest();

    public abstract void setRobotTest(boolean robotTest);

}
