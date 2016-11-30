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
import pixelitor.automate.SingleDirChooserPanel;
import pixelitor.operations.comp.CompOperations;
import pixelitor.Composition;
import pixelitor.history.History;
import pixelitor.ImageChangeReason;
import pixelitor.ImageComponent;
import pixelitor.PixelitorWindow;
import pixelitor.io.OpenSaveManager;
import pixelitor.io.OutputFormat;
import pixelitor.operations.Operation;
import pixelitor.operations.Operations;
import pixelitor.operations.gui.ParametrizedAdjustPanel;
import pixelitor.utils.Utils;

import javax.swing.*;
import java.io.File;

/**
 *
 */
public class OpTests {
    /**
     * Utility class with static methods, do not instantiate
     */
    private OpTests() {
    }

    public static void saveTheResultOfEachOp() {

        boolean cancelled = !SingleDirChooserPanel.selectOutputDir(true);
        if (cancelled) {
            return;
        }
        final File selectedDir = OpenSaveManager.getLastSaveDir();
        final OutputFormat outputFormat = OutputFormat.getLastOutputFormat();

        final ProgressMonitor progressMonitor = new ProgressMonitor(PixelitorWindow.getInstance(),
                "Saving the Results of Each Operation",
                "", 0, 100);
        ParametrizedAdjustPanel.setResetParams(false);

        SwingWorker worker = new SwingWorker<Void, Void>() {
            public Void doInBackground() {

//                ImageComponent ic = AppLogic.getActiveImageComponent();

                if (selectedDir != null) {
                    Operation[] operations = Operations.getAllOperationsShuffled();
                    progressMonitor.setProgress(0);

                    // TODO resize, crop should be also tested
                    for (int i = 0; i < operations.length; i++) {
                        progressMonitor.setProgress((int) ((float) i * 100 / operations.length));

                        Operation op = operations[i];
                        if (op.isEnabled()) {
                            progressMonitor.setNote("Running " + op.getMenuName());
                            if (progressMonitor.isCanceled()) {
                                break;
                            }

                            op.randomizeSettings();
                            op.execute(ImageChangeReason.OP_WITHOUT_DIALOG); // a  reason that makes backup
                            Composition comp = AppLogic.getActiveComp();
                            String fileName = "test_" + Utils.toFileName(op.getMenuName()) + "." + outputFormat.toString();
                            File f = new File(selectedDir, fileName);
                            outputFormat.saveComposition(comp, f);

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

    public static void runAllOpsOnCurrentLayer() {
        ParametrizedAdjustPanel.setResetParams(false);
        try {
            final ProgressMonitor progressMonitor = new ProgressMonitor(PixelitorWindow.getInstance(),
                    "Run All Operations on Current Layer",
                    "", 0, 100);

            progressMonitor.setProgress(0);

            // It is best to run this on the current EDT thread, using SwingWorker leads to strange things here

            Operation[] allOps = Operations.getAllOperationsShuffled();
            for (int i = 0, allOpsLength = allOps.length; i < allOpsLength; i++) {
                progressMonitor.setProgress((int) ((float) i * 100 / allOpsLength));
                Operation op = allOps[i];

                progressMonitor.setNote("Running " + op.getMenuName());
                if (progressMonitor.isCanceled()) {
                    break;
                }

                op.randomizeSettings();
                op.actionPerformed(null);
            }
            progressMonitor.close();
        } finally {
            ParametrizedAdjustPanel.setResetParams(true);
        }
    }

    public static void getCompositeImagePerformanceTest() {
        final Composition comp = AppLogic.getActiveComp();

        Runnable task = new Runnable() {
            public void run() {
                final long startTime = System.nanoTime();
                int times = 100;
                for (int i = 0; i < times; i++) {
                    comp.getCompositeImage("Performance Test");
                }

                long totalTime = (System.nanoTime() - startTime) / 1000000;
                String msg = "Executing getCompositeImage() " + times + " times took " + totalTime + " ms, average time = " + totalTime / times + " ms";
                JOptionPane.showMessageDialog(PixelitorWindow.getInstance(), msg, "Test Result", JOptionPane.INFORMATION_MESSAGE);
            }
        };
        Utils.executeWithBusyCursor(task, false);
    }

    public static void randomResize() {
        Composition comp = AppLogic.getActiveComp();
        if (comp != null) {
            int targetWidth = 100 + RobotTest.rand.nextInt(600);
            int targetHeight = 100 + RobotTest.rand.nextInt(400);
            CompOperations.resize(comp, targetWidth, targetHeight, false);
        }

    }
}