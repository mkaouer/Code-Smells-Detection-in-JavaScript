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
package pixelitor.menus;

import pixelitor.AppLogic;
import pixelitor.utils.GUIUtils;

import javax.swing.*;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Copies the the active image to the system clipboard
 */
public class CopyAction extends AbstractAction {
    private Type type;

    public enum Type {
        COPY_LAYER {
            @Override
            BufferedImage getCopySource() {
                return AppLogic.getActiveComp().getImageForActiveLayer();
            }
            @Override
            public String toString() {
                return "Copy Layer";
            }
        },
        COPY_COMPOSITE {
            @Override
            BufferedImage getCopySource() {
                return AppLogic.getActiveCompositeImage();
            }
            @Override
            public String toString() {
                return "Copy Composite";
            }
        };

        abstract BufferedImage getCopySource();
    }

    public CopyAction(Type type) {
        super(type.toString());
        this.type = type;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            BufferedImage activeImage = type.getCopySource();
            Transferable imageTransferable = new ImageTransferable(activeImage);
            clipboard.setContents(imageTransferable, null);
        } catch (Exception ex) {
            GUIUtils.showExceptionDialog(ex);
        }
    }
}

class ImageTransferable implements Transferable {
    private BufferedImage image;

    ImageTransferable(BufferedImage image) {
        this.image = image;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{DataFlavor.imageFlavor};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(DataFlavor.imageFlavor);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        return image;
    }
}