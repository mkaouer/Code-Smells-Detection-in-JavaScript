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
package pixelitor.automate;

import pixelitor.AppLogic;
import pixelitor.Composition;
import pixelitor.ExceptionHandler;
import pixelitor.PixelitorWindow;
import pixelitor.io.FileExtensionUtils;
import pixelitor.io.OpenSaveManager;
import pixelitor.io.OutputFormat;
import pixelitor.utils.CompositionAction;
import pixelitor.utils.Utils;
import pixelitor.utils.ValidatedDialog;

import javax.swing.*;
import java.awt.EventQueue;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

/**
 *
 */
public class Automate {
    private static final String OVERWRITE_YES = "Yes";
    private static final String OVERWRITE_YES_ALL = "Yes, All";
    private static final String OVERWRITE_NO = "No (Skip)";
    private static final String OVERWRITE_CANCEL = "Cancel Processing";

    /**
     * Utility class with static methods
     */
    private Automate() {
    }

    /**
     * Processes each file in the input directory with the given CompositionAction
     */
    public static void processEachFile(final CompositionAction action, final boolean closeAfterDone, String progressMonitorTitle) {
        File lastOpenDir = OpenSaveManager.getLastOpenDir();
        if (lastOpenDir == null) {
            throw new IllegalStateException("lastOpenDir is null");
        }
        if (!lastOpenDir.exists()) {
            throw new IllegalStateException("Last open dir " + lastOpenDir.getAbsolutePath() + " does not exist");
        }

        final File lastSaveDir = OpenSaveManager.getLastSaveDir();
        if (lastSaveDir == null) {
            throw new IllegalStateException("lastSaveDir is null");
        }
        if (!lastSaveDir.exists()) {
            throw new IllegalStateException("Last save dir " + lastSaveDir.getAbsolutePath() + " does not exist");
        }

        final File[] children = FileExtensionUtils.getAllSupportedFilesInDir(lastOpenDir);
        if (children.length == 0) {
            ExceptionHandler.showInfoDialog("No files", "There are no supported files in " + lastOpenDir.getAbsolutePath());
            return;
        }

        final ProgressMonitor progressMonitor = Utils.createProgressMonitor(progressMonitorTitle);
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            public Void doInBackground() {

                boolean overwriteAll = false;

                for (int i = 0, nrOfFiles = children.length; i < nrOfFiles; i++) {
                    File file = children[i];
                    progressMonitor.setProgress((int) ((float) i * 100 / nrOfFiles));
                    progressMonitor.setNote("Processing " + file.getName());

                    System.out.println("Processing " + file.getName());

                    if (progressMonitor.isCanceled()) {
                        break;
                    }

                    OpenSaveManager.openFile(file);
                    final Composition comp = AppLogic.getActiveComp();

                    Runnable guiTask = new Runnable() {
                        @Override
                        public void run() {
                            action.process(comp);
                        }
                    };
                    try {
                        EventQueue.invokeAndWait(guiTask);
                    } catch (InterruptedException e) {
                        ExceptionHandler.showExceptionDialog(e);
                    } catch (InvocationTargetException e) {
                        ExceptionHandler.showExceptionDialog(e);
                    }

                    OutputFormat outputFormat = OutputFormat.getLastOutputFormat();

                    String inputFileName = file.getName();
                    String outFileName = FileExtensionUtils.replaceExtension(inputFileName, outputFormat.toString());

                    File outputFile = new File(lastSaveDir, outFileName);

                    if (outputFile.exists() && (!overwriteAll)) {

                        JOptionPane pane = new JOptionPane("File " + outputFile + " already exists. Overwrite?", JOptionPane.WARNING_MESSAGE);
                        pane.setOptions(new String[]{OVERWRITE_YES, OVERWRITE_YES_ALL, OVERWRITE_NO, OVERWRITE_CANCEL});
                        pane.setInitialValue(OVERWRITE_NO);

                        JDialog dialog = pane.createDialog(PixelitorWindow.getInstance(), "Warning");
                        dialog.setVisible(true);
                        String value = (String) pane.getValue();
                        String answer;

                        if (value == null) { // cancelled
                            answer = OVERWRITE_CANCEL;
                        } else {
                            answer = value;
                        }

                        if (answer.equals(OVERWRITE_YES)) {
                            outputFormat.saveComposition(comp, outputFile);
                        } else if (answer.equals(OVERWRITE_YES_ALL)) {
                            outputFormat.saveComposition(comp, outputFile);
                            overwriteAll = true;
                        } else if (answer.equals(OVERWRITE_NO)) {
                            // do nothing
                        } else if (answer.equals(OVERWRITE_CANCEL)) {
                            if (closeAfterDone) {
                                OpenSaveManager.warnAndCloseImage(comp.getIC());
                            }
                            break;
                        }
                    } else { // the file does not exist or overwrite all was pressed previously
                        outputFormat.saveComposition(comp, outputFile);
                    }
                    if (closeAfterDone) {
                        OpenSaveManager.warnAndCloseImage(comp.getIC());
                    }
                } // end of for loop
                progressMonitor.close();
                return null;
            } // end of doInBackground
        };
        worker.execute();
    }

    /**
     * Lets the user select the input and output directory properties of the application.
     *
     * @param allowToBeTheSame
     * @param dialogTitle
     * @return true if a selection was made, false if the operation was cancelled
     */
    public static boolean selectInputAndOutputDir(boolean allowToBeTheSame, String dialogTitle) {
        OpenSaveDirsPanel p = new OpenSaveDirsPanel(allowToBeTheSame);
        ValidatedDialog chooser = new ValidatedDialog(p, PixelitorWindow.getInstance(), dialogTitle);
        chooser.setVisible(true);
        if (!chooser.isOkPressed()) {
            return false;
        }
        p.saveValues();

        return true;
    }
}