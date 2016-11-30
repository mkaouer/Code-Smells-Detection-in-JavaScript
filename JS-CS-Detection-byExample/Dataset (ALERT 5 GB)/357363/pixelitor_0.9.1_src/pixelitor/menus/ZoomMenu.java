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
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 *
 */
public class ZoomMenu extends JMenu {
    private ZoomLevel currentZoom;
    private static ButtonGroup radioGroup = new ButtonGroup();

    public static ZoomMenu INSTANCE = new ZoomMenu();

    private ZoomMenu() {
        super("Zoom");
        this.currentZoom = ZoomLevel.Z100;

        Action increaseAction = new AbstractAction("Zoom In") {
            @Override
            public void actionPerformed(ActionEvent e) {
                increaseZoom();
            }
        };
        MenuFactory.createMenuItem(increaseAction, KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, InputEvent.CTRL_MASK), this);

        Action decreaseAction = new AbstractAction("Zom Out") {
            @Override
            public void actionPerformed(ActionEvent e) {
                decreaseZoom();
            }
        };
        MenuFactory.createMenuItem(decreaseAction, KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, InputEvent.CTRL_MASK), this);
        addSeparator();

        // add other key bindings - see http://forums.sun.com/thread.jspa?threadID=5378257
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();
        String actionMapKeyIncrease = "increase";
        String actionMapKeyDecrease = "decrease";
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS  , InputEvent.CTRL_DOWN_MASK + InputEvent.SHIFT_MASK), actionMapKeyIncrease);  // + key in English keyboards
//        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS    , InputEvent.CTRL_DOWN_MASK), actionMapKeyPlus);  // + key in non-English keyboards
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ADD     , InputEvent.CTRL_DOWN_MASK), actionMapKeyIncrease);  // + key on the numpad
//        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS   , InputEvent.CTRL_DOWN_MASK), actionMapKeyMinus); // - key
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, InputEvent.CTRL_DOWN_MASK), actionMapKeyDecrease); // - key on the numpad
        actionMap.put(actionMapKeyIncrease, increaseAction);
        actionMap.put(actionMapKeyDecrease, decreaseAction);

        ZoomLevel[] zoomLevels = ZoomLevel.values();
        for (int i = 0; i < zoomLevels.length; i++) {
            ZoomLevel level = zoomLevels[i];
            ZoomMenuItem menuItem = level.getMenuItem();
            if (level == currentZoom) {
                menuItem.setSelected(true);
            }
            add(menuItem);
            radioGroup.add(menuItem);
        }
    }

    private void increaseZoom() {
        ZoomLevel newZoomLevel = currentZoom.increase();
        setNewZoomLevel(newZoomLevel);
    }

    private void decreaseZoom() {
        ZoomLevel newZoomLevel = currentZoom.decrease();
        setNewZoomLevel(newZoomLevel);
    }

    private void setNewZoomLevel(ZoomLevel newZoomLevel) {
        if (newZoomLevel != currentZoom) {
            currentZoom = newZoomLevel;
            ZoomMenuItem menuItem = currentZoom.getMenuItem();
            menuItem.doClick();
        }
    }

    /**
     * Called when the active image has changed
     */
    public void zoomChanged(ZoomLevel zoomLevel) {
        currentZoom = zoomLevel;
        zoomLevel.getMenuItem().setSelected(true);
    }
}
