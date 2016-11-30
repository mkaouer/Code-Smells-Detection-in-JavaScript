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
package pixelitor.tools;

import pixelitor.ImageComponent;

import javax.swing.*;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;

/**
 *
 */
public class HandTool extends Tool {
    private int startX;
    private int startY;
    private int maxScrollPositionX;
    private int maxScrollPositionY;

    HandTool() {
        super('h', "Hand", "hand_tool_icon.gif", "drag to move the view (if scrollbars are present)", Cursor.getPredefinedCursor(Cursor.HAND_CURSOR), false, false, false);
    }

    @Override
    void initSettingsPanel() {
    }

    @Override
    public void toolMousePressed(MouseEvent e, ImageComponent ic) {
        startX = e.getX();
        startY = e.getY();
        JViewport viewport = (JViewport) ic.getParent();
        Dimension viewSize = viewport.getViewSize();
        Dimension extentSize = viewport.getExtentSize(); // the size of the visible part of the view in view coordinates

        maxScrollPositionX = viewSize.width - extentSize.width;
        maxScrollPositionY = viewSize.height - extentSize.height;
    }

    @Override
    public void toolMouseDragged(MouseEvent e, ImageComponent ic) {
        int dx = e.getX() - startX;
        int dy = e.getY() - startY;
        JViewport viewport = (JViewport) ic.getParent();
        Point scrollPos = viewport.getViewPosition();
        scrollPos.x -= dx;
        scrollPos.y -= dy;
        if (scrollPos.x < 0) {
            scrollPos.x = 0;
        }
        if (scrollPos.y < 0) {
            scrollPos.y = 0;
        }
        if (scrollPos.x > maxScrollPositionX) {
            scrollPos.x = maxScrollPositionX;
        }
        if (scrollPos.y > maxScrollPositionY) {
            scrollPos.y = maxScrollPositionY;
        }

        viewport.setViewPosition(scrollPos);
    }

    @Override
    public void toolMouseReleased(MouseEvent e, ImageComponent ic) {

    }

}
