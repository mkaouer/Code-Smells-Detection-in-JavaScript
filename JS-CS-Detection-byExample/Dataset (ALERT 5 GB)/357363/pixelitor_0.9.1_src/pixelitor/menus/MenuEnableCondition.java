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
 * When to disable a menu or a menu item
 */
enum MenuEnableCondition {
    IF_THERE_IS_OPEN_IMAGE {
        @Override
        public boolean enableAtStartUp() {
            return false;
        }
        @Override
        public boolean isImageChangeListener() {
            return true;
        }
        @Override
        public boolean isUndoRedoListener() {
            return false;
        }
    }, IF_CAN_REPEAT_OPERATION {
        @Override
        public boolean enableAtStartUp() {
            return false;
        }
        @Override
        public boolean isImageChangeListener() {
            return true;
        }
        @Override
        public boolean isUndoRedoListener() {
            return false;
        }
    }, IF_FADING_POSSIBLE {
        @Override
        public boolean enableAtStartUp() {
            return false;
        }
        @Override
        public boolean isImageChangeListener() {
            return false;
        }
        @Override
        public boolean isUndoRedoListener() {
            return true;
        }
    }, IF_UNDO_POSSIBLE {
        @Override
        public boolean enableAtStartUp() {
            return false;
        }
        @Override
        public boolean isImageChangeListener() {
            return false;
        }
        @Override
        public boolean isUndoRedoListener() {
            return true;
        }
    }, IF_REDO_POSSIBLE {
        @Override
        public boolean enableAtStartUp() {
            return false;
        }
        @Override
        public boolean isImageChangeListener() {
            return false;
        }
        @Override
        public boolean isUndoRedoListener() {
            return true;
        }
    }, ALWAYS {
        @Override
        public boolean enableAtStartUp() {
            return true;
        }
        @Override
        public boolean isImageChangeListener() {
            return false;
        }
        @Override
        public boolean isUndoRedoListener() {
            return false;
        }
    };

    public abstract boolean enableAtStartUp();

    public abstract boolean isImageChangeListener();

    public abstract boolean isUndoRedoListener();
}
