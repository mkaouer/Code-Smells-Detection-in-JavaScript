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

import pixelitor.menus.ShowHideAllAction;
import pixelitor.tools.Tool;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

/**
 *
 */
public class GlobalKeyboardWatch {
    private static boolean spaceDown = false;

    public static void init() {
        // tab is the focus traversal key, it must be handled before it gets consumed
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            public boolean dispatchKeyEvent(KeyEvent e) {
                int id = e.getID();
                if (id == KeyEvent.KEY_PRESSED) {
                    int keyCode = e.getKeyCode();
                    if (keyCode == KeyEvent.VK_TAB) {
                        ShowHideAllAction.INSTANCE.actionPerformed(null);
                    } else if (keyCode == KeyEvent.VK_SPACE) {
                        Tool.getCurrentTool().spacePressed();
                        spaceDown = true;
                    }
                } else if(id == KeyEvent.KEY_RELEASED) {
                    int keyCode = e.getKeyCode();
                    if (keyCode == KeyEvent.VK_SPACE) {
                        Tool.getCurrentTool().spaceReleased();
                        spaceDown = false;
                    }
                }
                return false;
            }
        });
    }

    public static boolean isSpaceDown() {
        return spaceDown;
    }
}
