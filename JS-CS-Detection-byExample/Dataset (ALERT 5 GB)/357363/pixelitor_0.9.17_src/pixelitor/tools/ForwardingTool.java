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

import pixelitor.AppLogic;
import pixelitor.GlobalKeyboardWatch;
import pixelitor.ImageComponent;

import java.awt.Cursor;
import java.awt.event.MouseEvent;

/**
 * A tool that forwards the work to the Hand Tool if space is pressed
 */
public abstract class ForwardingTool extends Tool {
    private boolean handToolForwarding = false;
    private boolean normalToolUsage = false;
    private boolean spaceDown = false;

    protected ForwardingTool(char activationKeyChar, String name, String iconFileName, String toolMessage, Cursor cursor) {
        super(activationKeyChar, name, iconFileName, toolMessage, cursor);
    }

    @Override
    public boolean mousePressed(MouseEvent e, ImageComponent ic) {
        if (GlobalKeyboardWatch.isSpaceDown()) {
            HAND.mousePressed(e, ic);
            handToolForwarding = true;
            return true;
        }
        normalToolUsage = true;
        handToolForwarding = false;

        return super.mousePressed(e, ic);
    }

    @Override
    public boolean mouseDragged(MouseEvent e, ImageComponent ic) {
        if (handToolForwarding) {
            HAND.mouseDragged(e, ic);
            return true;
        }

        normalToolUsage = true;

        return super.mouseDragged(e, ic);
    }

    @Override
    public boolean mouseReleased(MouseEvent e, ImageComponent ic) {
        normalToolUsage = false;

        if (handToolForwarding) {
            HAND.mouseReleased(e, ic);
            handToolForwarding = false;

            AppLogic.setToolCursor(cursor);
            return true;
        }

        return super.mouseReleased(e, ic);
    }

    @Override
    public void spacePressed() {
        if (!spaceDown) { // this is called all the time while the space is held down, but we are interested only in ist first call
            if (!normalToolUsage) {
                AppLogic.setToolCursor(HAND.getCursor());
            }
        }
        spaceDown = true;
    }

    @Override
    public void spaceReleased() {
        spaceDown = false;
        if (!handToolForwarding) {
            AppLogic.setToolCursor(cursor);
        }
    }

}
