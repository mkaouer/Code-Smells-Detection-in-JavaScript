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
package pixelitor.io;

import pixelitor.PixelitorWindow;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 *
 */
public class DropListener implements DropTargetListener {

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {

    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {

    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {

    }

    @Override
    public void dragExit(DropTargetEvent dte) {

    }

    @Override
    public void drop(DropTargetDropEvent dtde) {
        Transferable transferable = dtde.getTransferable();
        DataFlavor[] flavors = transferable.getTransferDataFlavors();
        for (DataFlavor flavor : flavors) {
            if (flavor.equals(DataFlavor.imageFlavor)) {
                System.out.println("DropListener.drop Image Dropped");
                return;
            }
            if (flavor.isFlavorJavaFileListType()) {
                dtde.acceptDrop(DnDConstants.ACTION_COPY);
                List list;
                try {
                    list = (List) transferable.getTransferData(flavor);
                    for (Object o : list) {
                        File file = (File) o;
                        if (file.isDirectory()) {
                            int answer = JOptionPane.showConfirmDialog(PixelitorWindow.getInstance(),
                                    "You have dropped the folder \"" + file.getName() + "\". Do you want to open all image files inside it?",
                                    "Question", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                            if (answer == JOptionPane.YES_OPTION) {
                                OpenSaveManager.openAllImagesInDir(file);
                            } else if (answer == JOptionPane.NO_OPTION) {
                                // do nothing
                            }
                        } else if (file.isFile()) {
                            OpenSaveManager.openFile(file);
                        }
                    }
                } catch (UnsupportedFlavorException e) {
                    e.printStackTrace();
                    dtde.rejectDrop();
                } catch (IOException e) {
                    e.printStackTrace();
                    dtde.rejectDrop();
                }
                dtde.dropComplete(true);
                return;
            }
        }

        // DataFlavor not recognized
        dtde.rejectDrop();
    }
}
