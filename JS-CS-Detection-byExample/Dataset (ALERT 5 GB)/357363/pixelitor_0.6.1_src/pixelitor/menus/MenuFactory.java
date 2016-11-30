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

package pixelitor.menus;

import javax.swing.*;

public final class MenuFactory {
    enum DisableCondition {
        IF_NO_IMAGE_IS_OPEN, IF_NO_IMAGE_IS_CHANGED, IF_FADING_IS_NOT_POSSIBLE, NEVER
    }

    // this is a utility class with static methods, it should not be instantiated
    private MenuFactory() {
    }

    public static void createMenuItem(Action a, KeyStroke keyStroke, JMenu parent, DisableCondition whenToDisable) {
        JMenuItem menuItem;
        if (whenToDisable == DisableCondition.IF_NO_IMAGE_IS_OPEN) {
            menuItem = new ImageAwareMenuItem(a);
        } else if (whenToDisable == DisableCondition.IF_NO_IMAGE_IS_CHANGED) {
            menuItem = new ChangedImageAwareMenuItem(a);
        } else if (whenToDisable == DisableCondition.IF_FADING_IS_NOT_POSSIBLE) {
            menuItem = new FadingPossibleMenuItem(a);
        } else {
            menuItem = new JMenuItem(a);
        }
        parent.add(menuItem);
        if (keyStroke != null) {
            menuItem.setAccelerator(keyStroke);
        }
    }

    public static void createMenuItem(Action action, KeyStroke keyStroke, JMenu parent) {
        createMenuItem(action, keyStroke, parent, DisableCondition.IF_NO_IMAGE_IS_OPEN);
    }

}
