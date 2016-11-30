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
import pixelitor.ImageComponent;

import javax.swing.*;
import java.awt.Cursor;
import java.awt.event.MouseEvent;

/**
 *
 */
public class MoveTool extends Tool {

    @Override
    public void mousePressed(MouseEvent e, ImageComponent ic) {
        super.mousePressed(e, ic);
        ic.startTranslation();
    }

    @Override
    public void mouseDragged(MouseEvent e, ImageComponent ic) {
        super.mouseDragged(e, ic);
        int relativeX = (int) (end.getX() - start.getX());
        int relativeY = (int) (end.getY() - start.getY());

        ic.moveImageRelative(relativeX, relativeY);
        ic.imageChanged();
    }

    @Override
    public void mouseReleased(MouseEvent e, ImageComponent ic) {
        ic.endTranslation();
    }

    @Override
    public String getName() {
        return "Move";
    }

    @Override
    public String getIconFileName() {
        return "move_tool_icon.gif";
    }

    @Override
    void initSettingsPanel(JPanel p) {

    }

    @Override
    public KeyStroke getActivationKeyStroke() {
        return KeyStroke.getKeyStroke('v');
    }

    @Override
    protected void toolStarted() {
        ImageComponent ic = AppLogic.getActiveImageComponent();
        if (ic != null) {
            ic.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        }
    }

    @Override
    protected void toolEnded() {
        ImageComponent ic = AppLogic.getActiveImageComponent();
        if (ic != null) {
            ic.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

}