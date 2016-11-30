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

import pixelitor.GlobalKeyboardWatch;
import pixelitor.ImageComponent;

import javax.swing.*;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;

/**
 *
 */
public class CropSelectionTool extends Tool {
//    private MarchingAntsSelection selection = null;
//    private Point lastEndBeforeSelectionMove = null;

    CropSelectionTool() {
        super('m', "Selection for Crop", "crop_selection_tool_icon.gif", "click and drag to select an area to be cropped with Edit/Crop", Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    @Override
    public void mousePressed(MouseEvent e, ImageComponent ic) {
        super.mousePressed(e, ic);
        ic.getComp().deselect();

    }

    @Override
    public void mouseReleased(MouseEvent e, ImageComponent ic) {
        super.mouseReleased(e, ic);
        ic.getComp().startOrUpdateSelection(start, end, e.isAltDown());
    }

    @Override
    public void mouseDragged(MouseEvent e, ImageComponent ic) {
        Point oldEnd = end;

        super.mouseDragged(e, ic);

        if (GlobalKeyboardWatch.isSpaceDown()) {
            int dx = end.x - oldEnd.x;
            int dy = end.y - oldEnd.y;

            start.x += dx;
            start.y += dy;
        }
        ic.getComp().startOrUpdateSelection(start, end, e.isAltDown());
    }

    @Override
    public void mouseClicked(MouseEvent e, ImageComponent ic) {
        super.mouseClicked(e, ic);
        ic.getComp().deselect();

    }

    @Override
    void initSettingsPanel(JPanel p) {

    }


}
