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
import pixelitor.operations.comp.CompOperations;
import pixelitor.Composition;
import pixelitor.PixelitorWindow;
import pixelitor.io.FileExtensionUtils;
import pixelitor.io.OpenSaveManager;
import pixelitor.io.OutputFormat;
import pixelitor.utils.CompositionAction;
import pixelitor.utils.GUIUtils;
import pixelitor.utils.ValidatedDialog;

import javax.swing.*;
import java.io.File;

/**
 *
 */
public class Automate {
    private static final String OVERWRITE_YES = "Yes";
    private static final String OVERWRITE_YES_ALL = "Yes, All";
    private static final String OVERWRITE_NO = "No (Skip)";
    private static final String OVERWRITE_CANCEL = "Cancel Processing";

    /**
     * Utility class with static methods, do not instantiate
     */
    private Automate() {
    }


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
            GUIUtils.showInfoDialog("No files", "There are no supported files in " + lastOpenDir.getAbsolutePath());
            return;
        }

        final ProgressMonitor progressMonitor = new ProgressMonitor(PixelitorWindow.getInstance(),
                progressMonitorTitle,
                "", 0, 100);

        SwingWorker worker = new SwingWorker<Void, Void>() {
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

                    OpenSaveManager.openFile(file); // TODO openFile should return a composition, or null on failure
                    Composition comp = AppLogic.getActiveComp();
                    action.process(comp);

                    OutputFormat outputFormat = OutputFormat.getLastOutputFormat();

                    String inputFileName = file.getName();
                    String outFileName = FileExtensionUtils.replaceExtension(inputFileName, outputFormat.toString());                     

                    File outputFile = new File(lastSaveDir, outFileName);

                    if (outputFile.exists() && (!overwriteAll)) {

                        JOptionPane pane = new JOptionPane("File " + outputFile + " exists already. Overwrite?", JOptionPane.WARNING_MESSAGE);
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
                                comp.close();
                            }
                            break;
                        }
                    } else { // the file does not exist or overwrite all was pressed previously
                        outputFormat.saveComposition(comp, outputFile);
                    }
                    if (closeAfterDone) {
                        comp.close();
                    }
                } // end of for loop
                progressMonitor.close();
                return null;
            } // end of doInBackground
        };
        worker.execute();
    }

    public static void batchResize() {
        BatchResizePanel p = new BatchResizePanel();
        ValidatedDialog chooser = new ValidatedDialog(p, PixelitorWindow.getInstance(), "Batch Resize", true);
        if (!chooser.isOkPressed()) {
            return;
        }
        p.saveValues();

        // execute the resizing

        final int maxWidth = p.getWidthValue();
        final int maxHeight = p.getHeightValue();

        CompositionAction resizeAction = new CompositionAction() {
            @Override
            public void process(Composition comp) {
                CompOperations.resize(comp, maxWidth, maxHeight, true);
            }
        };
        processEachFile(resizeAction, true, "Batch Resize...");
    }

    /**
     * Lets the user select the input and output directory properties of the application.
     *
     * @param allowToBeTheSame
     * @param dialogTitle
     * @return true if a selection was made, false if the operation was cancelled
     */
    public static boolean selectInputAndOutputDir(boolean allowToBeTheSame, String dialogTitle) {
        IODirsSelectorPanel p = new IODirsSelectorPanel(allowToBeTheSame);
        ValidatedDialog chooser = new ValidatedDialog(p, PixelitorWindow.getInstance(), dialogTitle, true);
        if (!chooser.isOkPressed()) {
            return false;
        }
        p.saveValues();

        return true;
    }

}