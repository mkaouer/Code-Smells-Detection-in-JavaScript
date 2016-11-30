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

import com.bric.util.JVM;
import pixelitor.filters.Starburst;
import pixelitor.io.OpenSaveManager;
import pixelitor.layers.Layers;
import pixelitor.tools.FgBgColorSelector;
import pixelitor.utils.AppPreferences;
import pixelitor.utils.MacScreenMenu;

import javax.swing.*;
import java.awt.EventQueue;
import java.io.File;

/**
 * The main class
 */
public class Pixelitor {
    /**
     * Utility class with static methods
     */
    private Pixelitor() {
    }

    public static void main(final String[] args) {
        // this works
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Pixelitor");

        // it is respected only by the native Aqua look-and-feel
        System.setProperty("apple.laf.useScreenMenuBar", "true");

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
//        if(JVM.isMac) {
//            MacScreenMenu.saveTrickyUISettings();
//        }

        try {
            UIManager.setLookAndFeel(AppPreferences.getDefaultLookAndFeelClass());
        } catch (Exception e) {
            ExceptionHandler.showExceptionDialog(e);
        }

//        if(JVM.isMac) {
//            MacScreenMenu.restoreTrickyUISettings();
//        }

        Layers.init();

        PixelitorWindow pw = PixelitorWindow.getInstance();
        ExceptionHandler.setMainWindowInitialized(true);


        if (args.length > 0) {
            // open the files given on the command line
            for (String fileName : args) {
                File f = new File(fileName);
                if (f.exists()) {
                    OpenSaveManager.openFile(f);
                } else {
                    ExceptionHandler.showErrorDialog("File not found", "The file \"" + f.getAbsolutePath() + "\" does not exist");
                }
            }
        } else {
            // ensure that the focus is not grabbed by a textfield so that the keyboard shortcuts work properly
            FgBgColorSelector.INSTANCE.requestFocus();
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
//        Composition comp = AppLogic.getActiveComp();
//        if(comp != null) {
//            String layerName = "text layer";
//            String layerText = "text layer text";
//            TextLayer textLayer = new TextLayer(comp, layerName, layerText);
//            comp.addLayer(textLayer, true, true, false);
//        }

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

//        ImageTests.createSplashImage();
//        AppLogic.getActiveComp().moveLayerSelectionDown();

//        Starburst starburst = new Starburst();
//        starburst.actionPerformed(null);

    }
}
