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
package pixelitor;

import pixelitor.io.OpenSaveManager;
import pixelitor.utils.AppPreferences;

import javax.swing.*;
import java.awt.EventQueue;
import java.io.File;

/**
 * The main class
 */
public class Pixelitor {
    /**
     * Must not be instantiated
     */
    private Pixelitor() {
    }

    public static void main(final String[] args) {
        ExceptionHandler.INSTANCE.register();
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    createAndShowGUI(args);
                } catch (Exception e) {
                    ExceptionHandler.showExceptionDialog(e);
                }
            }
        });
    }

    /**
     * This is called on the EDT
     */
    private static void createAndShowGUI(String[] args) {
        try {
            UIManager.setLookAndFeel(AppPreferences.getDefaultLookAndFeelClass());
        } catch (Exception e) {
            ExceptionHandler.showExceptionDialog(e);
        }

        PixelitorWindow pw = PixelitorWindow.getInstance();
        ExceptionHandler.setMainWindowInitialized(true);

        // open the files given on the command line
        for (String fileName : args) {
            File f = new File(fileName);
            if (f.exists()) {
                OpenSaveManager.openFile(f);
            } else {
                ExceptionHandler.showErrorDialog("File not found", "The file \"" + f.getAbsolutePath() + "\" does not exist");
            }
        }

        TipsOfTheDay.showTips(pw, false);

        Runnable loadFileChoosersTask = new Runnable() {
            @Override
            public void run() {
                OpenSaveManager.initOpenFileChooser();
                OpenSaveManager.initSaveFileChooser();
            }
        };
        Thread t = new Thread(loadFileChoosersTask);
        t.setPriority(Thread.NORM_PRIORITY);
        t.start();

        afterStartTestActions(pw);
    }

    /**
     * A possibility for automatic debugging or testing
     */
    private static void afterStartTestActions(PixelitorWindow pw) {
//        pw.dispatchEvent(new KeyEvent(pw, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), KeyEvent.CTRL_MASK, KeyEvent.VK_T, 'T'));
//        NewImage.addNewImage(NewImage.BgFill.WHITE, 10, 10, "Test");
//        NewImage.addNewImage(NewImage.BgFill.WHITE, 600, 400, "Test 2");

//        History.showHistory();
//        Tool.SELECTION.getButton().doClick();

//          Tool.GRADIENT.getButton().doClick();

//        Tool.PAINT_BUCKET.getButton().doClick();
//        AppLogic.getActiveImageComponent().setZoom(ZoomLevel.Z6400);

//        JHEmboss op = new JHEmboss();
//        RandomSpheres op = new RandomSpheres();
//        JHCells op = new JHCells();
//        op.actionPerformed(null);

//        Tool.SHAPES.getButton().doClick();
//        Tool.SHAPES.setAction(ShapesAction.STROKE);
//        Tool.SHAPES.setStrokeType(StrokeType.WOBBLE);

    }
}
