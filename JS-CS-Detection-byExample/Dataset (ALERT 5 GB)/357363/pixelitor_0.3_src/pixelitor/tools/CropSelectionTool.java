/*
 * Copyright 2009 László Balázs-Csíki
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

import java.awt.Graphics2D;
import java.awt.Component;
import java.awt.event.MouseEvent;

import pixelitor.AppLogic;
import pixelitor.menus.CropMenuItem;
import pixelitor.utils.MarchingAntsSelection;

import javax.swing.*;

/**
 *
 */
public class CropSelectionTool extends Tool {
    private MarchingAntsSelection selection = null;

    CropSelectionTool() {
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

    public void deselect() {
        if (selection != null) {
            selection.stopMarching();
            selection = null;
        }
        CropMenuItem.INSTANCE.setEnabled(false);
    }

    public MarchingAntsSelection getSelection() {
        return selection;
    }

    public boolean hasSelection() {
        return (selection != null);
    }

    @Override
    public void mousePressed(MouseEvent e, JComponent c) {
        super.mousePressed(e, c);
        deselect();
    }

    @Override
    public void mouseReleased(MouseEvent e, JComponent c) {
        super.mouseReleased(e, c);
        startOrUpdateSelection();
    }

    @Override
    public void mouseDragged(MouseEvent e, JComponent c) {
        super.mouseDragged(e, c);
        startOrUpdateSelection();
    }

    @Override
    public void mouseClicked(MouseEvent e, JComponent c) {
        super.mouseClicked(e, c);
        deselect();
    }

    @Override
    public String getName() {
        return "crop selection";
    }

    @Override
    void initSettingsPanel(JPanel p) {

    }

}
