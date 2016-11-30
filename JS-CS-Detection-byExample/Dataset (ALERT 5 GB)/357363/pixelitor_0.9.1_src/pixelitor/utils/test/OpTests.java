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
package pixelitor.utils.test;

import pixelitor.AppLogic;
import pixelitor.History;
import pixelitor.ImageChangeReason;
import pixelitor.ImageComponent;
import pixelitor.PixelitorWindow;
import pixelitor.io.OpenSaveManager;
import pixelitor.io.OutputFormat;
import pixelitor.operations.Operation;
import pixelitor.operations.Operations;
import pixelitor.operations.Resize;
import pixelitor.operations.Rotate;
import pixelitor.operations.gui.ParametrizedAdjustPanel;
import pixelitor.utils.OutputDirChooser;
import pixelitor.utils.Utils;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

/**
 *
 */
public class OpTests {
    // Utility class with static methods, do not instantiate

    private OpTests() {
    }

    public static void saveTheResultOfEachOp() {
        final File selectedDir = OutputDirChooser.getSelectedDir();
        if(selectedDir == null) { // cancel was pressed
            return;            
        }

        final ProgressMonitor progressMonitor = new ProgressMonitor(PixelitorWindow.getInstance(),
                "Saving the Results of Each Operation",
                "", 0, 100);
        ParametrizedAdjustPanel.setResetParams(false);

        SwingWorker worker = new SwingWorker<Void, Void>() {
            public Void doInBackground() {
                OutputFormat outputFormat = OutputFormat.PNG;
//                ImageComponent ic = AppLogic.getActiveImageComponent();

                if (selectedDir != null) {
                    List<Operation> allOps = Operations.allOps;
                    progressMonitor.setProgress(0);

                    for (int i = 0; i < allOps.size(); i++) {
                        progressMonitor.setProgress((int) ((float) i * 100 / allOps.size()));

                        Operation op = allOps.get(i);
                        if (op.isEnabled()) {
                            System.out.println("Running " + op.getMenuName());
                            progressMonitor.setNote("Running " + op.getMenuName());
                            if (progressMonitor.isCanceled()) {
                                break;
                            }

                            if (op instanceof Resize) {
                                continue; // TODO
                            }
                            if (op instanceof Rotate) {
                                continue; // TODO
                            }

                            op.randomizeSettings();
                            op.execute(ImageChangeReason.OP_WITHOUT_DIALOG); // a  reason that makes backup
                            BufferedImage newImage = AppLogic.getActiveLayerImage();
                            String fileName = "test_" + Utils.toFileName(op.getMenuName()) + "." + outputFormat.toString();
                            File f = new File(selectedDir, fileName);
                            OpenSaveManager.saveFile(f, newImage, outputFormat);

                            if (History.canUndo()) {
                                History.undo();
                            }
                        }
                    }
                    progressMonitor.close();
                }
                return null;
            }
        };
        try {
            worker.execute();
        } finally {
            ParametrizedAdjustPanel.setResetParams(true);
        }
    }

    public static void runAllOpsOnCurrentImage() {
        ParametrizedAdjustPanel.setResetParams(false);
        try {
            List<Operation> allOps = Operations.allOps;
            for (Operation op : allOps) {
                System.out.println("Running " + op.getMenuName());
                op.randomizeSettings();
                op.actionPerformed(null);
            }
        } finally {
            ParametrizedAdjustPanel.setResetParams(true);
        }
    }

    public static void getCompositeImagePerformanceTest() {
        final ImageComponent ic = AppLogic.getActiveImageComponent();
        Runnable task = new Runnable() {
            public void run() {
                final long startTime = System.nanoTime();
                int times = 100;
                for (int i = 0; i < times; i++) {
                    ic.getCompositeImage();
                }

                long totalTime = (System.nanoTime() - startTime) / 1000000;
                String msg = "Executing getCompositeImage() " + times + " times took " + totalTime + " ms, average time = " + totalTime / times + " ms";
                JOptionPane.showMessageDialog(PixelitorWindow.getInstance(), msg, "Test Result", JOptionPane.INFORMATION_MESSAGE);
            }
        };
        Utils.executeWithBusyCursor(task, false);
    }
}