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
import pixelitor.menus.CropMenuItem;
import pixelitor.utils.MarchingAntsSelection;

import javax.swing.*;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

/**
 *
 */
public class CropSelectionTool extends Tool {
    private MarchingAntsSelection selection = null;

    CropSelectionTool() {
        super('m', "Selection for Crop", "crop_selection_tool_icon.gif");
    }

    @Override
    public void paintOnImage(Graphics2D g) {
        if (selection != null) {
            selection.paintTheAnts(g);
        }
    }

    private void startOrUpdateSelection() {
        if (selection == null) {
            selection = new MarchingAntsSelection(AppLogic.getActiveImageComponent(), start, end);
            selection.startMarching();
            CropMenuItem.INSTANCE.setEnabled(true);
        } else {
            selection.updateSelection(start, end);
        }
    }

    public void deselect(ImageComponent ic) {
        if (selection != null) {
            selection.stopMarching();

            Rectangle selBounds = selection.getSelectionShape().getBounds();

            selection = null;
            ic.repaint(selBounds.x, selBounds.y, selBounds.width + 1, selBounds.height + 1);
            CropMenuItem.INSTANCE.setEnabled(false);
        }
    }

    public MarchingAntsSelection getSelection() {
        return selection;
    }

    public boolean hasSelection() {
        return (selection != null);
    }

    @Override
    public void mousePressed(MouseEvent e, ImageComponent ic) {
        super.mousePressed(e, ic);
        deselect(ic);
    }

    @Override
    public void mouseReleased(MouseEvent e, ImageComponent ic) {
        super.mouseReleased(e, ic);
        startOrUpdateSelection();
    }

    @Override
    public void mouseDragged(MouseEvent e, ImageComponent ic) {
        super.mouseDragged(e, ic);
        startOrUpdateSelection();
    }

    @Override
    public void mouseClicked(MouseEvent e, ImageComponent ic) {
        super.mouseClicked(e, ic);
        deselect(ic);
    }

    @Override
    void initSettingsPanel(JPanel p) {

    }


}
