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

package pixelitor.utils;

import pixelitor.Build;
import pixelitor.Composition;
import pixelitor.ExceptionHandler;
import pixelitor.PixelitorWindow;
import pixelitor.layers.ImageLayer;
import pixelitor.layers.Layer;

import javax.swing.*;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Random;
import java.util.TimerTask;

public final class Utils {
    /**
     * Utility class with static methods, do not instantiate
     */
    private Utils() {
    }

    private static final Cursor BUSY_CURSOR = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
    private static final Cursor DEFAULT_CURSOR = Cursor.getDefaultCursor();
    private static final int WAIT_CURSOR_DELAY = 300; // in milliseconds

    public static void executeWithBusyCursor(Runnable task, boolean newThread) {
        executeWithBusyCursor(PixelitorWindow.getInstance(), task, newThread);
    }

    public static void executeWithBusyCursor(final Component parent, Runnable task, boolean newThread) {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                parent.setCursor(BUSY_CURSOR);
            }
        };
        java.util.Timer timer = new java.util.Timer();

        try {
            timer.schedule(timerTask, WAIT_CURSOR_DELAY);
            if (newThread) {
                Thread t = new Thread(task);
                t.setPriority(Thread.NORM_PRIORITY); // lower priority than the event thread
                t.start();
            } else {
                task.run(); // on the current thread
            }
        } finally {
            timer.cancel();
            parent.setCursor(DEFAULT_CURSOR);
        }
    }

    public static void openURI(URI uri) {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(uri);
            } catch (IOException e) {
                ExceptionHandler.showExceptionDialog(e);
            }
        } else {
        }
    }

    /**
     * Replaces all the special characters in s string with an underscore
     */
    public static String toFileName(String s) {
        return s.replaceAll("[^A-Za-z0-9_]", "_");
    }

    public static void debugThread() {
        Thread thread = Thread.currentThread();
        String name = thread.getName();
        System.out.println("Utils.debugThread name = \"" + name + '\"');
        boolean eventDispatchThread = SwingUtilities.isEventDispatchThread();
        System.out.println("Utils.debugThread eventDispatchThread = " + eventDispatchThread);
    }

    /**
     * Returns the active image layer or shows an error message and returns null if the  active layer is not an image layer
     */
    public static ImageLayer checkActiveLayerIsImage(Composition comp) {
        Layer activeLayer = comp.getActiveLayer();
        if (activeLayer instanceof ImageLayer) {
            return (ImageLayer) activeLayer;
        } else {
            ExceptionHandler.showErrorDialog("Error", "The active layer is not an image layer");
            return null;
        }
    }

    public static void randomizeGUIWidgetsOn(JPanel panel) {
        int count = panel.getComponentCount();
        Random rand = new Random();

        for (int i = 0; i < count; i++) {
            Component child = panel.getComponent(i);
            if (child instanceof JComboBox) {
                JComboBox box = (JComboBox) child;
                int itemCount = box.getItemCount();
                box.setSelectedIndex(rand.nextInt(itemCount));
            } else if (child instanceof JCheckBox) {
                JCheckBox box = (JCheckBox) child;
                box.setSelected(rand.nextInt(2) == 1);
            } else if (child instanceof SliderSpinner) {
                SliderSpinner spinner = (SliderSpinner) child;
                spinner.getParam().randomize();
            } else if (child instanceof BlendingModePanel) {
                BlendingModePanel bmp = (BlendingModePanel) child;
                bmp.randomize();
            }
        }
    }

    public static String float2String(float f) {
        if (f == 0.0f) {
            return "";
        }
        return String.format("%.3f", f);
    }

    public static float string2float(String s) throws NotANumberException {
        String trimmed = s.trim();
        if (trimmed.length() == 0) {
            return 0.0f;
        }

        NumberFormat nf = NumberFormat.getInstance();
        Number number;
        try {
            number = nf.parse(trimmed);
        } catch (ParseException e) {
            NumberFormat englishFormat = NumberFormat.getInstance(Locale.ENGLISH);
            try {
                number = englishFormat.parse(trimmed);
            } catch (ParseException e1) {
                throw new NotANumberException(s);
            }
        }
        return number.floatValue();
    }

    public static void throwTestException() {
        if(Build.CURRENT != Build.FINAL) {
            throw new IllegalStateException("Test");
        }
    }
}
