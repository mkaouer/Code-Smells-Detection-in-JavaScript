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

import pixelitor.Composition;
import pixelitor.ImageComponent;

import java.awt.Cursor;
import java.awt.event.MouseEvent;

/**
 *
 */
public class MoveTool extends ForwardingTool {
    public MoveTool() {
        super('v', "Move", "move_tool_icon.gif", "drag to move the active layer, Alt-drag to move a duplicate of the active layer", Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
    }

    @Override
    void initSettingsPanel(ToolSettingsPanel p) {

    }

    @Override
    public boolean mousePressed(MouseEvent e, ImageComponent ic) {
        if (super.mousePressed(e, ic)) {
            return true;
        }

        ic.getComp().startTranslation(e.isAltDown());
        return false;
    }

    @Override
    public boolean mouseDragged(MouseEvent e, ImageComponent ic) {
        if (super.mouseDragged(e, ic)) {
            return true;
        }

        Composition c = ic.getComp();
        int relativeX = userDrag.getHorizontalDifference();
        int relativeY = userDrag.getVerticalDifference();
        c.moveActiveContentRelative(relativeX, relativeY, true);

        return false;
    }

    @Override
    public boolean mouseReleased(MouseEvent e, ImageComponent ic) {
        if (super.mouseReleased(e, ic)) {
            return true;
        }
        ic.getComp().endTranslation();
        return false;
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

}