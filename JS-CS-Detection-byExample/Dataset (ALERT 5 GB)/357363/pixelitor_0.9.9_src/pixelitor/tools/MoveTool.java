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
package pixelitor.tools;

import pixelitor.AppLogic;
import pixelitor.Composition;
import pixelitor.ImageComponent;
import pixelitor.layers.ContentLayer;
import pixelitor.layers.Layer;

import javax.swing.*;
import java.awt.Cursor;
import java.awt.event.MouseEvent;

/**
 *
 */
public class MoveTool extends Tool {
    public MoveTool() {
        super('v', "Move", "move_tool_icon.gif", "drag to move the active layer, Alt-drag to move a duplicate of the active layer", Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
    }

    @Override
    public void mousePressed(MouseEvent e, ImageComponent ic) {
        super.mousePressed(e, ic);
        ic.getComp().startTranslation(e.isAltDown());
    }

    @Override
    public void mouseDragged(MouseEvent e, ImageComponent ic) {
        super.mouseDragged(e, ic);

        Composition c = ic.getComp();
        int relativeX = (int) (end.getX() - start.getX());
        int relativeY = (int) (end.getY() - start.getY());
        c.moveActiveContentRelative(relativeX, relativeY, true);
    }

    @Override
    public void mouseReleased(MouseEvent e, ImageComponent ic) {
        ic.getComp().endTranslation();
    }

    /**
     * Moves the active layer programmatically.
     */
    public static void move(Composition comp, int relativeX, int relativeY) {
        comp.startTranslation(false);
        comp.moveActiveContentRelative(relativeX, relativeY, false);
        comp.imageChanged(true, true);
        comp.endTranslation();
    }

    @Override
    void initSettingsPanel(JPanel p) {

    }
}