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
import pixelitor.history.History;
import pixelitor.ImageChangeReason;
import pixelitor.ImageComponent;
import pixelitor.PixelitorWindow;
import pixelitor.menus.CropMenuItem;
import pixelitor.menus.ZoomLevel;
import pixelitor.operations.Fade;
import pixelitor.operations.Operation;
import pixelitor.operations.Operations;
import pixelitor.tools.FgBgColorSelector;
import pixelitor.utils.GUIUtils;

import javax.swing.*;
import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Random;

/**
 *
 */
public class RobotTest {
    final static Random rand = new Random();

    public static void runRobot() throws AWTException {
        final Robot r = new Robot();

        final ProgressMonitor progressMonitor = new ProgressMonitor(PixelitorWindow.getInstance(),
                "Robot Test Progress",
                "", 0, 100);

        SwingWorker worker = new SwingWorker<Void, Void>() {
            public Void doInBackground() {
                PixelitorWindow pw = PixelitorWindow.getInstance();
                Dimension winDim = pw.getSize();
                int minX = 20;
                int minY = 40;
                int maxX = winDim.width - 2 * minX;
                int maxY = winDim.height - 2 * minY;

                int maxTests = 2000;
                for (int i = 0; i < maxTests; i++) {
                    progressMonitor.setProgress((int) ((float) i * 100 / maxTests));
                    if (progressMonitor.isCanceled()) {
                        break;
                    }
                    if (!GUIUtils.appIsActive()) {
                        System.out.println("RobotTest app focus lost");
                        break;
                    }

                    int randomX = minX + rand.nextInt(maxX);
                    int randomY = minY + rand.nextInt(maxY);

                    r.delay(200);

                    try {
                        executeRandomOp(randomX, randomY, progressMonitor, r);
                    } catch (Exception e) {
                        e.printStackTrace();
                        progressMonitor.close();
                        return null;
                    }
//                    OpTests.randomResize();

                }
                progressMonitor.close();
                return null;
            } // end of doInBackground()
        };
        worker.execute();


    }

    private static void executeRandomOp(int randomX, int randomY, ProgressMonitor progressMonitor, Robot r) {
        int op = rand.nextInt(19);

        switch (op) {
            case 0:
            case 1:
            case 2:
                log("random move to (" + randomX + ", " + randomY + ")", progressMonitor);
                randomMove(r, randomX, randomY);
                break;
            case 3:
            case 4:
            case 5:
                log("random drag to (" + randomX + ", " + randomY + ")", progressMonitor);
                randomDrag(r, randomX, randomY);
                break;
            case 6:
            case 7:
            case 8:
                log("random click to (" + randomX + ", " + randomY + ")", progressMonitor);
                randomClick(r);
                break;
            case 9:
                log("random colors", progressMonitor);
                randomColors();
                break;
            case 10:
                randomOperation(progressMonitor);
                break;
            case 11:
                log("random key", progressMonitor);
                randomKey(r);
                break;
            case 12:
                log("random zoom", progressMonitor);
                randomZoom();
                break;
            case 13:
                log("random resize", progressMonitor);
                OpTests.randomResize();
                break;
            case 14:
                log("random repeat", progressMonitor);
                randomRepeat();
                break;
            case 15:
                log("random undo", progressMonitor);
                randomUndo();
                break;
            case 16:
                log("random redo", progressMonitor);
                randomRedo();
                break;
            case 17:
                log("random crop", progressMonitor);
                randomCrop();
                break;
            case 18:
                log("random fade", progressMonitor);
                randomFade();
                break;
        }
    }

    private static void log(String msg, ProgressMonitor progressMonitor) {
//        System.out.println(msg);
        progressMonitor.setNote(msg);
    }


    private static void randomMove(Robot r, int x, int y) {
        r.mouseMove(x, y);
    }

    private static void randomDrag(Robot r, int x, int y) {
        r.mousePress(InputEvent.BUTTON1_MASK);

        r.mouseMove(x, y);
        r.mouseRelease(InputEvent.BUTTON1_MASK);
    }

    private static void randomClick(Robot r) {
        r.mousePress(InputEvent.BUTTON1_MASK);
        r.delay(50);
        r.mouseRelease(InputEvent.BUTTON1_MASK);
    }

    private static void randomColors() {
        FgBgColorSelector.setRandomColors();
    }

    private static void randomOperation(ProgressMonitor progressMonitor) {
        double r = Math.random();
        if (r < 0.8) {
            return;
        }

        Operation op = Operations.getRandomOperation();
        if(op instanceof Fade) {
            return;
        }

        log("random operation: " + op.getName(), progressMonitor);

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
        r.keyPress(keyEvent);
        r.delay(50);
        r.keyRelease(keyEvent);
    }

    private static void randomZoom() {
        ImageComponent ic = AppLogic.getActiveImageComponent();
        if (ic != null) {
            ic.setZoom(ZoomLevel.getRandomZoomLevel());
        }
    }

    private static void randomRepeat() {
        if (Math.random() > 0.9) {
            PixelitorWindow pw = PixelitorWindow.getInstance();
            pw.dispatchEvent(new KeyEvent(pw, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), KeyEvent.CTRL_MASK, KeyEvent.VK_F, 'F'));
        }
    }

    private static void randomUndo() {
        if (Math.random() > 0.8) {
            if (History.canUndo()) {
                History.undo();
            }
        }
    }

    private static void randomRedo() {
        if (History.canRedo()) {
            History.redo();
        }
    }

    private static void randomCrop() {
        CropMenuItem menuItem = CropMenuItem.INSTANCE;
        boolean enabled = menuItem.isEnabled();
        if(enabled) {
            menuItem.doClick();
        }
    }

    private static void randomFade() {
        boolean b = History.canFade();
        if (!b) {
            return;
        }

        Fade fade = new Fade();
        fade.setOpacity(50);
        fade.execute(ImageChangeReason.OP_WITHOUT_DIALOG);
    }
}
