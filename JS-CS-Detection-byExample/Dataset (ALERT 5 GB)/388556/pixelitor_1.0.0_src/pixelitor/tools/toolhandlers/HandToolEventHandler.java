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
package pixelitor.tools.toolhandlers;

import pixelitor.AppLogic;
import pixelitor.GlobalKeyboardWatch;
import pixelitor.ImageComponent;
import pixelitor.tools.Tools;

import java.awt.Cursor;
import java.awt.event.MouseEvent;

/**
 * Checks whether the current tool should behave as a hand tool (space is down at the start of a mouse drag),
 * and forwards the request to the next ToolEventHandler if not.
 */
public class HandToolEventHandler extends ToolEventHandler {
    private boolean handToolForwarding = false;
    private boolean normalToolUsage = false;
    private boolean spaceDown = false;

    private Cursor cursor;

    public HandToolEventHandler(Cursor cursor) {
        this.cursor = cursor;
    }

    @Override
    boolean mousePressed(MouseEvent e, ImageComponent ic) {
        if (GlobalKeyboardWatch.isSpaceDown()) {
            Tools.HAND.mousePressed(e, ic);
            handToolForwarding = true;
            return true;
        }
        normalToolUsage = true;
        handToolForwarding = false;

        return false;
    }

    @Override
    boolean mouseDragged(MouseEvent e, ImageComponent ic) {
        if (handToolForwarding) {
            Tools.HAND.mouseDragged(e, ic);
            return true;
        }

        normalToolUsage = true;

        return false;
    }

    @Override
    boolean mouseReleased(MouseEvent e, ImageComponent ic) {
        normalToolUsage = false;

        if (handToolForwarding) {
            Tools.HAND.toolMouseReleased(e, ic);
            handToolForwarding = false;

            AppLogic.setToolCursor(cursor);
            return true;
        }

        return false;
    }

    public void spacePressed() {
        if (!spaceDown) { // this is called all the time while the space is held down, but we are interested only in ist first call
            if (!normalToolUsage) {
                AppLogic.setToolCursor(Tools.HAND.getCursor());
            }
        }
        spaceDown = true;
    }

    public void spaceReleased() {
        spaceDown = false;
        if (!handToolForwarding) {
            AppLogic.setToolCursor(cursor);
        }
    }

}
