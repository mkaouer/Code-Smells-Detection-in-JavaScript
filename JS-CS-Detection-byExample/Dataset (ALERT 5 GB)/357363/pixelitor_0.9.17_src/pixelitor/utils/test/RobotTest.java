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
import pixelitor.Build;
import pixelitor.Composition;
import pixelitor.ExceptionHandler;
import pixelitor.ImageChangeReason;
import pixelitor.ImageComponent;
import pixelitor.PixelitorWindow;
import pixelitor.filters.Fade;
import pixelitor.filters.Operation;
import pixelitor.filters.Operations;
import pixelitor.filters.comp.Flip;
import pixelitor.filters.comp.Rotate;
import pixelitor.history.History;
import pixelitor.layers.AddNewLayerAction;
import pixelitor.layers.BlendingMode;
import pixelitor.layers.DeleteActiveLayerAction;
import pixelitor.layers.Layer;
import pixelitor.menus.CopyAction;
import pixelitor.menus.CopyType;
import pixelitor.menus.PasteAction;
import pixelitor.menus.SelectionActions;
import pixelitor.menus.ShowHideAllAction;
import pixelitor.menus.ShowHideHistogramsAction;
import pixelitor.menus.ShowHideLayersAction;
import pixelitor.menus.ShowHideStatusBarAction;
import pixelitor.menus.ShowHideToolsAction;
import pixelitor.menus.ZoomLevel;
import pixelitor.tools.FgBgColorSelector;
import pixelitor.tools.Tool;
import pixelitor.tools.ToolSettingsPanelContainer;
import pixelitor.utils.GUIUtils;

import javax.swing.*;
import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * An automatic test using java.awt.Robot.
 * Can be dangerous because of the random native mouse events that can control other apps as well if they escape.
 * Don't use it unless you understand the risks.
 */
public class RobotTest {
    final static Random rand = new Random();

    private static boolean singleImageTest = false;

    private static List<String> lastEvents;

    /**
     * Utility class with static methods, must not be instantiated
     */
    private RobotTest() {
    }

    public static void runRobot() throws AWTException {
        if (Build.CURRENT != Build.ROBOT_TEST) {
            ExceptionHandler.showErrorDialog("Error", "Build is not ROBOT_TEST");
            return;
        }

        System.out.println("RobotTest.runRobot CALLED at " + new Date());

        lastEvents = new LinkedList<String>();
        final Robot r = new Robot();

        randomCopy(); // ensure an image is on the clipboard

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            public Void doInBackground() {
                int maxTests = 8000;
                int onePercent = maxTests / 100;

                for (int i = 0; i < maxTests; i++) {
                    if ((i % onePercent) == 0) {
                        int percent = 100 * i / maxTests;
                        System.out.print(percent + "% ");
                    }

                    if (!GUIUtils.appIsActive()) {
                        System.out.println("\nRobotTest app focus lost");
                        break;
                    }

                    PixelitorWindow pw = PixelitorWindow.getInstance();
                    Dimension winDim = pw.getSize();
                    int minX = 20;
                    int minY = 40;
                    int maxX = winDim.width - 2 * minX;
                    int maxY = winDim.height - 2 * minY;

                    final int randomX = minX + rand.nextInt(maxX);
                    final int randomY = minY + rand.nextInt(maxY);

                    r.delay(100 + rand.nextInt(400));

                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                executeRandomOp(randomX, randomY, r);
                                if(AppLogic.getActiveComp() == null) {
                                    throw new IllegalStateException("no active composition");
                                }

                            } catch (Exception e) {
                                ExceptionHandler.showExceptionDialog(e);
                            }
                        } // end of run
                    };
                    try {
                        EventQueue.invokeAndWait(runnable);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("\nRobotTest.runRobot FINISHED at " + new Date());
                Toolkit.getDefaultToolkit().beep();

                return null;
            } // end of doInBackground()
        };
        worker.execute();
    }

    public static void printLastTenEvents() {
        if(lastEvents == null) {
            return; // we are in Robot test mode, but the robot test was not run yet
        }

        int lastTenEventsSize = lastEvents.size();
        System.out.println("RobotTest - the last " + lastTenEventsSize + " events:");

        for (int i = 0; i < lastTenEventsSize; i++) {
            String event = lastEvents.get(i);
            System.out.println("    Event(" + i + ") = \"" + event + '\"');
        }
    }

    private static void executeRandomOp(int randomX, int randomY, Robot r) {
        int op = rand.nextInt(42);

        switch (op) {
            case 0:
            case 1:
            case 2:
                randomMove(r, randomX, randomY);
                break;
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                randomDrag(r, randomX, randomY);
                break;
            case 10:
            case 11:
            case 12:
                randomClick(r);
                break;
            case 13:
                randomResize();
                break;
            case 14:
                repeat();
                break;
            case 15:
                undo();
                break;
            case 16:
                redo();
                break;
            case 17:
                crop();
                break;
            case 18:
                fade();
                break;
            case 19:
                randomizeToolSettings();
                break;
            case 20:
                arrangeWindows();
                break;
            case 21:
                randomColors();
                break;
            case 22:
                randomOperation();
                break;
            case 23:
                randomKey(r);
                break;
            case 24:
                randomZoom(rand);
                break;
            case 25:
                randomDeselect();
                break;
            case 26:
                layerToCanvasSize();
                break;
            case 27:
                invertSelection();
                break;
            case 28:
                traceWithCurrentBrush();
                break;
            case 29:
                traceWithCurrentEraser();
                break;
            case 30:
                randomRotateFlip();
                break;
            case 31:
                layerOrderChange();
                break;
            case 32:
                layerMerge();
                break;
            case 33:
                layerAddDelete();
                break;
            case 34:
                randomHideShow();
                break;
            case 35:
                randomCopy();
                break;
            case 36:
                randomPaste();
                break;
            case 37:
                randomChangeLayerOpacityOrBlending();
                break;
            case 38:
                createSelectionFromRandomRect();
                break;
            case 39:
                randomCloseImageWOSaving();
                break;
            case 40:
                randomLoadImage();
                break;
            case 41:
                randomSaveInAllFormats();
                break;
        }
    }

    private static void randomResize() {
        if (Math.random() > 0.7) {
            log("random resize");
            OpTests.randomResize();
        }
    }

    private static void log(String msg) {
//        System.out.println(msg);
//        progressMonitor.setNote(msg);
        Composition comp = AppLogic.getActiveComp();
        lastEvents.add(msg + " on " + comp.getName());
        if (lastEvents.size() > 12) {
            lastEvents.remove(0);
        }
    }

    private static void randomMove(Robot r, int x, int y) {
        log("random move to (" + x + ", " + y + ')');
        r.mouseMove(x, y);
    }

    private static void randomDrag(Robot r, int x, int y) {
        log("random " + Tool.getCurrentTool().getName() + " drag to (" + x + ", " + y + ')');
        r.mousePress(InputEvent.BUTTON1_MASK);
        r.mouseMove(x, y);
        r.mouseRelease(InputEvent.BUTTON1_MASK);
    }

    private static void randomClick(Robot r) {
        log("random click");

        r.mousePress(InputEvent.BUTTON1_MASK);
        r.delay(50);
        r.mouseRelease(InputEvent.BUTTON1_MASK);
    }

    private static void randomColors() {
        log("random colors");
        FgBgColorSelector.setRandomColors();
    }

    private static void randomOperation() {
        int r = rand.nextInt(10);
        if (r < 8) {
            return;
        }

        Operation op = Operations.getRandomOperation();
        if (op instanceof Fade) {
            return;
        }

        log("random operation: " + op.getName());

        op.randomizeSettings();
        op.execute(ImageChangeReason.OP_WITHOUT_DIALOG);
    }

    private static int[] keyEvents = new int[]{KeyEvent.VK_1, KeyEvent.VK_A,
            KeyEvent.VK_ENTER, KeyEvent.VK_ESCAPE, KeyEvent.VK_F1,
            KeyEvent.VK_M, KeyEvent.VK_V,
            KeyEvent.VK_G, KeyEvent.VK_B,
            KeyEvent.VK_E, KeyEvent.VK_I,
            KeyEvent.VK_S, KeyEvent.VK_Q,
    };

    private static void randomKey(Robot r) {
        int randomIndex = rand.nextInt(keyEvents.length);
        int keyEvent = keyEvents[randomIndex];

        log("random key keyEvent = " + keyEvent);

        r.keyPress(keyEvent);
        r.delay(50);
        r.keyRelease(keyEvent);
    }

    private static void randomZoom(Random rand) {
        ImageComponent ic = AppLogic.getActiveImageComponent();
        if (ic != null) {
            ZoomLevel randomZoomLevel = ZoomLevel.getRandomZoomLevel(rand);
            log("random zoom zoomLevel = " + randomZoomLevel);
            ic.setZoom(randomZoomLevel, true);
        }
    }

    private static void repeat() {
        if (rand.nextInt(10) == 1) {  // 10% chance
            log("repeat");
            PixelitorWindow pw = PixelitorWindow.getInstance();
            pw.dispatchEvent(new KeyEvent(pw, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), KeyEvent.CTRL_MASK, KeyEvent.VK_F, 'F'));
        }
    }

    private static void undo() {
        if (rand.nextInt(10) > 7) { // 20% chance
            if (History.canUndo()) {
                log("undo");
                History.undo();
            }
        }
    }

    private static void redo() {
        if (History.canRedo()) {
            log("redo");
            History.redo();
        }
    }

    private static void crop() {
        boolean enabled = SelectionActions.areEnabled();
        if (enabled) {
            log("crop");
            SelectionActions.getCropAction().actionPerformed(new ActionEvent("", 0, ""));
        }
    }

    private static void fade() {
        boolean b = History.canFade();
        if (!b) {
            return;
        }

        log("fade");

        Fade fade = new Fade();
        fade.setOpacity(50);

        fade.execute(ImageChangeReason.OP_WITHOUT_DIALOG);
    }

    private static void randomizeToolSettings() {
        log("randomize tool settings");
        ToolSettingsPanelContainer.INSTANCE.randomizeToolSettings();
    }

    private static void arrangeWindows() {
        double r = Math.random();
        if (r > 0.8) {
            log("arrange windows - tile");
            PixelitorWindow.getInstance().tileWindows();
        } else if (r > 0.7) {
            log("arrange windows - cascade");
            PixelitorWindow.getInstance().cascadeWindows();
        }
    }

    private static void randomDeselect() {
        if (SelectionActions.areEnabled()) {
            log("deselect");
            SelectionActions.getDeselectAction().actionPerformed(new ActionEvent("", 0, ""));
        }
    }

    private static void layerToCanvasSize() {
        log("layer to canvas size");
        AppLogic.getActiveComp().layerToCanvasSize();
    }

    private static void invertSelection() {
        if (SelectionActions.areEnabled()) {
            log("invert selection");
            SelectionActions.getInvertSelectionAction().actionPerformed(new ActionEvent("", 0, ""));
        }
    }

    private static void traceWithCurrentBrush() {
        if (SelectionActions.areEnabled()) {
            log("trace with current brush");
            SelectionActions.getTraceWithBrush().actionPerformed(new ActionEvent("", 0, ""));
        }
    }

    private static void traceWithCurrentEraser() {
        if (SelectionActions.areEnabled()) {
            log("trace with current easer");
            SelectionActions.getTraceWithEraser().actionPerformed(new ActionEvent("", 0, ""));
        }
    }

    private static void randomRotateFlip() {
        int r = rand.nextInt(5);
        Action action = null;

        switch (r) {
            case 0:
                action = new Rotate(90, "Rotate 90\u00B0 CW");
                break;
            case 1:
                action = new Rotate(180, "Rotate 180\u00B0");
                break;
            case 2:
                action = new Rotate(270, "Rotate 90\u00B0 CCW");
                break;
            case 3:
                action = Flip.createFlipOp(Flip.Direction.HORIZONTAL);
                break;
            case 4:
                action = Flip.createFlipOp(Flip.Direction.VERTICAL);
                break;
        }
        log("roate-flip action = " + action);

        action.actionPerformed(new ActionEvent("", 0, ""));
    }

    private static void layerOrderChange() {
        log("layer order change");
        Composition comp = AppLogic.getActiveComp();
        int r = rand.nextInt(6);
        switch (r) {
            case 0:
                comp.moveActiveLayerToTop();
                break;
            case 1:
                comp.moveActiveLayerToBottom();
                break;
            case 2:
                comp.moveLayerSelectionUp();
                break;
            case 3:
                comp.moveLayerSelectionDown();
                break;
            case 4:
                comp.moveActiveLayerUp();
                break;
            case 5:
                comp.moveActiveLayerDown();
                break;
        }
    }

    private static void layerMerge() {
        Composition comp = AppLogic.getActiveComp();
        int r = rand.nextInt(2);

        if (r == 0) {
            log("layer merge down");
            comp.mergeDown();
        } else if (r == 1) {
            log("layer flatten image");
            comp.flattenImage();
        }
    }

    private static void layerAddDelete() {
        int r = rand.nextInt(2);

        if (r == 0) {
            if (AddNewLayerAction.INSTANCE.isEnabled()) {
                log("add new layer");
                AddNewLayerAction.INSTANCE.actionPerformed(new ActionEvent("", 0, ""));
            }
        } else if (r == 1) {
            if (DeleteActiveLayerAction.INSTANCE.isEnabled()) {
                log("delete active layer");
                DeleteActiveLayerAction.INSTANCE.actionPerformed(new ActionEvent("", 0, ""));
            }
        }
    }

    private static void randomHideShow() {
        int r = rand.nextInt(5);
        if (r == 0) {
            log("random show-hide histograms");
            new ShowHideHistogramsAction().actionPerformed(new ActionEvent("", 0, ""));
        } else if (r == 1) {
            log("random show-hide layers");
            new ShowHideLayersAction().actionPerformed(new ActionEvent("", 0, ""));
        } else if (r == 2) {
            log("random show-hide tools");
            new ShowHideToolsAction().actionPerformed(new ActionEvent("", 0, ""));
        } else if (r == 4) {
            log("random show-hide statusbar");
            new ShowHideStatusBarAction().actionPerformed(new ActionEvent("", 0, ""));
        } else if (r == 5) {
            log("random show-hide all");
            ShowHideAllAction.INSTANCE.actionPerformed(new ActionEvent("", 0, ""));
        }
    }

    private static void randomCopy() {
        int r = rand.nextInt(2);
        if (r == 0) {
            log("random copy layer");
            new CopyAction(CopyType.COPY_LAYER).actionPerformed(new ActionEvent("", 0, ""));
        } else if (r == 1) {
            log("random copy composite");
            new CopyAction(CopyType.COPY_COMPOSITE).actionPerformed(new ActionEvent("", 0, ""));
        }
    }

    private static void randomPaste() {
        int r = rand.nextInt(2);
        if (r == 0) {
            if(singleImageTest) {
                return;
            }
            log("random paste as new image");
            new PasteAction(false).actionPerformed(new ActionEvent("", 0, ""));
        } else if (r == 1) {
            log("random paste as new layer");
            new PasteAction(true).actionPerformed(new ActionEvent("", 0, ""));
        }
    }


    private static void randomChangeLayerOpacityOrBlending() {
        int r = rand.nextInt(2);

        Layer layer = AppLogic.getActiveLayer();
        if (r == 1) {
            float opacity = layer.getOpacity();
            float f = rand.nextFloat();

            if (f > opacity) {
                // always increase
                log("random increase opacity");
                layer.setOpacity(f, true, true);
            } else if (rand.nextFloat() > 0.75) { // sometimes decrease
                log("random decrease opacity");
                layer.setOpacity(f, true, true);
            }
        } else if (r == 2) {
            log("random change layer blending mode");
            BlendingMode[] blendingModes = BlendingMode.values();
            BlendingMode randomBlendingMode = blendingModes[rand.nextInt(blendingModes.length)];
            layer.setBlendingMode(randomBlendingMode, true, true);
        }
    }

    private static void createSelectionFromRandomRect() {
        log("create selection from random rectangle");
        Composition comp = AppLogic.getActiveComp();

        int x1 = rand.nextInt(comp.getCanvasWidth());
        int x2 = rand.nextInt(comp.getCanvasWidth());

        int y1 = rand.nextInt(comp.getCanvasHeight());
        int y2 = rand.nextInt(comp.getCanvasHeight());

        Rectangle randomRect = new Rectangle(
                Math.min(x1, x2),
                Math.min(y1, y2),
                Math.abs(x1 - x2),
                Math.abs(y1 - y2)
        );
        comp.deselect(true);
        comp.createSelectionFromShape(randomRect);
    }

    private static void randomCloseImageWOSaving() {
        if(singleImageTest) {
            return;
        }
//        log("random close image without saving");
//        ImageComponent ic = AppLogic.getActiveImageComponent();
//        if(ic != null) {
//            ic.close();
//        }
    }

    private static void randomSaveInAllFormats() {
//        log("random save in all formats");
    }

    private static void randomLoadImage() {
        if(singleImageTest) {
            return;
        }
//        log("random load image");
    }
}


