/*
 * Copyright 2010 L�szl� Bal�zs-Cs�ki
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

    public static final ZoomMenu INSTANCE = new ZoomMenu();
    private static final String ACTION_MAP_KEY_INCREASE = "increase";
    private static final String ACTION_MAP_KEY_DECREASE = "decrease";

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
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, InputEvent.CTRL_DOWN_MASK + InputEvent.SHIFT_MASK), ACTION_MAP_KEY_INCREASE);  // + key in English keyboards
//        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS    , InputEvent.CTRL_DOWN_MASK), actionMapKeyPlus);  // + key in non-English keyboards
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ADD, InputEvent.CTRL_DOWN_MASK), ACTION_MAP_KEY_INCREASE);  // + key on the numpad
//        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS   , InputEvent.CTRL_DOWN_MASK), actionMapKeyMinus); // - key
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, InputEvent.CTRL_DOWN_MASK), ACTION_MAP_KEY_DECREASE); // - key on the numpad
        actionMap.put(ACTION_MAP_KEY_INCREASE, increaseAction);
        actionMap.put(ACTION_MAP_KEY_DECREASE, decreaseAction);

        ZoomLevel[] zoomLevels = ZoomLevel.values();
        for (ZoomLevel level : zoomLevels) {
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

    public void setCurrentZoom(ZoomLevel currentZoom) {
        this.currentZoom = currentZoom;
    }

    /**
     * Called when the active image has changed
     */
    public void zoomChanged(ZoomLevel zoomLevel) {
        currentZoom = zoomLevel;
        zoomLevel.getMenuItem().setSelected(true);
    }
}
