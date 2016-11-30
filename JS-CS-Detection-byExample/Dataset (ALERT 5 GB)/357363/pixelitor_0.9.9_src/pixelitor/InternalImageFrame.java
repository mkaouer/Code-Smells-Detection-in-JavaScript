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

package pixelitor;

import pixelitor.io.OpenSaveManager;
import pixelitor.menus.ZoomLevel;

import javax.swing.*;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import java.awt.Dimension;

/**
 * A JInternalFrame for displaying the compositions
 */
public class InternalImageFrame extends JInternalFrame implements InternalFrameListener {
//    private static int maxWidth = 400;
//    private static int maxHeight = 400;

    private final ImageComponent ic;
    private static final int NIMBUS_HORIZONTAL_ADJUSTMENT = 18;
    private static final int NIMBUS_VERTICAL_ADJUSTMENT = 38;

    private static final int METAL_HORIZONTAL_ADJUSTMENT = 13;
    private static final int METAL_VERTICAL_ADJUSTMENT = 36;

    private static final int MOTIF_HORIZONTAL_ADJUSTMENT = 12;
    private static final int MOTIF_VERTICAL_ADJUSTMENT = 31;

    private static final int WINDOWS_HORIZONTAL_ADJUSTMENT = 12;
    private static final int WINDOWS_VERTICAL_ADJUSTMENT = 37;

    private static final int WINDOWS_CLASSIC_HORIZONTAL_ADJUSTMENT = 10;
    private static final int WINDOWS_CLASSIC_VERTICAL_ADJUSTMENT = 37;


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
        OpenSaveManager.warnAndCloseImage(ic);
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

        setSize(width + sizeHorizontalAdjustment, height + sizeVerticalAdjustment);
    }

    private static int sizeHorizontalAdjustment = NIMBUS_HORIZONTAL_ADJUSTMENT;
    private static int sizeVerticalAdjustment = NIMBUS_VERTICAL_ADJUSTMENT;

    public static void setLF(String lfName) {
        if(lfName.equals("Nimbus")) {
            sizeHorizontalAdjustment = NIMBUS_HORIZONTAL_ADJUSTMENT;
            sizeVerticalAdjustment = NIMBUS_VERTICAL_ADJUSTMENT;
        } else if(lfName.equals("Metal")) {
            sizeHorizontalAdjustment = METAL_HORIZONTAL_ADJUSTMENT;
            sizeVerticalAdjustment = METAL_VERTICAL_ADJUSTMENT;
        } else if(lfName.equals("Motif")) {
            sizeHorizontalAdjustment = MOTIF_HORIZONTAL_ADJUSTMENT;
            sizeVerticalAdjustment = MOTIF_VERTICAL_ADJUSTMENT;
        } else if(lfName.equals("Windows")) {
            sizeHorizontalAdjustment = WINDOWS_HORIZONTAL_ADJUSTMENT;
            sizeVerticalAdjustment = WINDOWS_VERTICAL_ADJUSTMENT;
        } else if(lfName.equals("WindowsClassic")) {
            sizeHorizontalAdjustment = WINDOWS_CLASSIC_HORIZONTAL_ADJUSTMENT;
            sizeVerticalAdjustment = WINDOWS_CLASSIC_VERTICAL_ADJUSTMENT;
        } else {
            // Nimbus is quite big, so it will be a good default
            sizeHorizontalAdjustment = NIMBUS_HORIZONTAL_ADJUSTMENT;
            sizeVerticalAdjustment = NIMBUS_VERTICAL_ADJUSTMENT;
        }
    }


    public void updateTitle() {
        setTitle(ic.createFrameTitle());
    }
}

