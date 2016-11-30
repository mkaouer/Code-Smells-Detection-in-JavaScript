/*
 * Copyright 2009-2010 L�szl� Bal�zs-Cs�ki
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

package pixelitor;

import pixelitor.io.OpenSaveManager;

import javax.swing.*;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import java.awt.Dimension;
import java.awt.Rectangle;

/**
 * A JInternalFrame for displaying the compositions
 */
public class InternalImageFrame extends JInternalFrame implements InternalFrameListener {
    private static final int NIMBUS_HORIZONTAL_ADJUSTMENT = 18;
    private static final int NIMBUS_VERTICAL_ADJUSTMENT = 38;

    private final ImageComponent ic;

    public InternalImageFrame(ImageComponent ic, int locationX, int locationY) {
        super(ic.createFrameTitle(), true, true, true, true);
        addInternalFrameListener(this);
        setFrameIcon(null);
        this.ic = ic;
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        JScrollPane scrollPane = new JScrollPane(this.ic);
        this.add(scrollPane);

        Dimension preferredSize = ic.getPreferredSize();
        setNewSize((int) preferredSize.getWidth(), (int) preferredSize.getHeight(), locationX, locationY);
        this.setVisible(true);
    }

    @Override
    public void internalFrameActivated(InternalFrameEvent e) {
        AppLogic.activeImageHasChanged(ic);
        ic.onActivation();
    }

    @Override
    public void internalFrameClosed(InternalFrameEvent e) {
        AppLogic.imageClosed(ic);
    }

    @Override
    public void internalFrameClosing(InternalFrameEvent e) {
        if (Build.CURRENT != Build.ROBOT_TEST) {
            OpenSaveManager.warnAndCloseImage(ic);
        }
    }

    @Override
    public void internalFrameDeactivated(InternalFrameEvent e) {
    }

    @Override
    public void internalFrameDeiconified(InternalFrameEvent e) {
    }

    @Override
    public void internalFrameIconified(InternalFrameEvent e) {
    }

    @Override
    public void internalFrameOpened(InternalFrameEvent e) {
    }

    public void setToNaturalSize(int locationX, int locationY) {
        int canvasWidth = ic.getZoomedCanvasWidth();
        int canvasHeight = ic.getZoomedCanvasHeight();
        setNewSize(canvasWidth, canvasHeight, locationX, locationY);
    }

    public void setNewSize(int width, int height, int locationX, int locationY) {
        if (locationX == -1) {
            locationX = getLocation().x;
        }
        if (locationY == -1) {
            locationY = getLocation().y;
        }

        Dimension desktopSize = PixelitorWindow.getInstance().getDesktopSize();
        int maxWidth = Math.max(0, desktopSize.width - 20 - locationX);
        int maxHeight = Math.max(0, desktopSize.height - 40 - locationY);


        if (width > maxWidth) {
            width = maxWidth;
        }
        if (height > maxHeight) {
            height = maxHeight;
        }

        setSize(width + NIMBUS_HORIZONTAL_ADJUSTMENT, height + NIMBUS_VERTICAL_ADJUSTMENT);
    }

    public void updateTitle() {
        setTitle(ic.createFrameTitle());
    }

    public void makeSureItIsVisible() {
        Rectangle bounds = getBounds();
        if (bounds.x < 0 || bounds.y < 0) {
            int newX = bounds.x < 0 ? 0 : bounds.x;
            int newY = bounds.y < 0 ? 0 : bounds.y;
            setBounds(newX, newY, bounds.width, bounds.height);
        }
    }
}

