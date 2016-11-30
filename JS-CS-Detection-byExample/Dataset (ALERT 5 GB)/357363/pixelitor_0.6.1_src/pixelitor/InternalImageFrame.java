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

import javax.swing.*;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import java.awt.Dimension;

public class InternalImageFrame extends JInternalFrame implements InternalFrameListener {
    private static int maxWidth = 400;
    private static int maxHeight = 400;

    private final ImageComponent imageComponent;

    public InternalImageFrame(ImageComponent p) {
        super(p.getName(), true, true, true, true);
        addInternalFrameListener(this);
        setFrameIcon(null);
        this.imageComponent = p;
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        JScrollPane scrollPane = new JScrollPane(imageComponent);
        this.add(scrollPane);

        setImageSizeHasChanged(p.getCanvasWidth(), p.getCanvasHeight());
        this.setVisible(true);
    }

    @Override
    public void internalFrameActivated(InternalFrameEvent e) {
        AppLogic.activeImageHasChanged(imageComponent);
        imageComponent.onActivation();
    }

    @Override
    public void internalFrameClosed(InternalFrameEvent e) {
        AppLogic.imageClosed(imageComponent);
    }

    @Override
    public void internalFrameClosing(InternalFrameEvent e) {
        OpenSaveManager.warnAndCloseImage(imageComponent);
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

    public void setImageSizeHasChanged(int width, int height) {
        if (width > maxWidth) {
            width = maxWidth;
        }
        if (height > maxHeight) {
            height = maxHeight;
        }

        // TODO why must these values be added  - do they work in all LFs?
//        setSize(width + 10, height + 30);
        // nimbus needs more:
        setSize(width + 18, height + 38);
    }

    public static void setMaxSize(Dimension dim) {
        InternalImageFrame.maxWidth = dim.width - 40;
        InternalImageFrame.maxHeight = dim.height - 40;
    }
}
