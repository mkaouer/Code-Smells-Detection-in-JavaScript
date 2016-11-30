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

import pixelitor.PixelitorWindow;
import pixelitor.utils.GUIUtils;

import javax.swing.*;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Pastes an image from the system clipboard to a new image
 */
public class PasteAction extends AbstractAction {
    private static int pastedCount = 1;

    public PasteAction() {
        super("Paste");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            executePaste();
        } catch (Exception ex) {
            GUIUtils.showExceptionDialog(ex);
        }
    }

    private static void executePaste() {
        BufferedImage pastedImage;
        Transferable clipboardContents = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        if (clipboardContents == null) {
            GUIUtils.showInfoDialog("Paste", "There is nothing to paste.");
            return;
        }
        if (clipboardContents.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            try {
                pastedImage = (BufferedImage) clipboardContents.getTransferData(DataFlavor.imageFlavor);
                String title = "Pasted Image " + pastedCount;
                PixelitorWindow.getInstance().addNewImage(pastedImage, null, title);
                pastedCount++;
            } catch (UnsupportedFlavorException ex) {
                GUIUtils.showExceptionDialog(ex);
            } catch (IOException ex) {
                GUIUtils.showExceptionDialog(ex);
            }
        } else {
            GUIUtils.showInfoDialog("Paste", "The clipboard content is not an image.");
        }
    }
}
