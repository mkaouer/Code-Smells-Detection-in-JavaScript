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

import javax.swing.*;

/**
 * When to enable a menu or a menu item
 */
enum MenuEnableCondition {
    IF_THERE_IS_OPEN_IMAGE {
        @Override
        public JMenuItem getMenuItem(Action a) {
            return new OpenImageAwareMenuItem(a);
        }
    }, IF_CAN_REPEAT_OPERATION {
        @Override
        public JMenuItem getMenuItem(Action a) {
            return new RepeatMenuItem(a);
        }
    }, IF_FADING_POSSIBLE {
        @Override
        public JMenuItem getMenuItem(Action a) {
            return new FadeMenuItem(a);
        }
    }, IF_UNDO_POSSIBLE {
        @Override
        public JMenuItem getMenuItem(Action a) {
            return new UndoMenuItem(a);
        }
    }, IF_REDO_POSSIBLE {
        @Override
        public JMenuItem getMenuItem(Action a) {
            return new RedoMenuItem(a);
        }
    }, ACTION_MANAGED { // always enabled unless disabled by disabling the action
        @Override
        public JMenuItem getMenuItem(Action a) {
            return new JMenuItem(a);
        }
    };

    public abstract JMenuItem getMenuItem(Action a);

}
