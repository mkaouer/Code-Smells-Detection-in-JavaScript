/*
 * Copyright 2010 Laszlo Balazs-Csiki
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

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import pixelitor.history.History;
import pixelitor.utils.test.DebugEventQueue;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import javax.swing.*;
import java.awt.Frame;
import java.awt.Toolkit;
import java.util.logging.Level;

/**
 *
 */
public class ExceptionHandler implements Thread.UncaughtExceptionHandler {
    public static ExceptionHandler INSTANCE = new ExceptionHandler();
    private static boolean mainWindowInitialized = false;

    private ExceptionHandler() {
    }

    public void register() {
        Thread.setDefaultUncaughtExceptionHandler(this);
    }


    @Override
    public void uncaughtException(Thread t, Throwable e) {
/*        if (e instanceof ClassCastException) {
            // Nimbus has these bugs sometimes, no need for a dialog...
            e.printStackTrace();
            return;
        }
*/
        showExceptionDialog(e);
    }

    private static Frame getParentForDialogs() {
        if (mainWindowInitialized) {
            return PixelitorWindow.getInstance();
        }
        return null;
    }

    public static void showExceptionDialog(Throwable e) {
        e.printStackTrace();

        if (Build.CURRENT.isRobotTest()) {
            DebugEventQueue.dump();
            History.showHistory();
            Toolkit.getDefaultToolkit().beep();

            playWarningSound();
        }

        Frame parent = getParentForDialogs();
        String basicErrorMessage = "An exception occurred: " + e.getMessage();
        ErrorInfo ii = new ErrorInfo("Program error", basicErrorMessage, null, null, e, Level.SEVERE, null);
        JXErrorPane.showDialog(parent, ii);
    }

    public static void playWarningSound() {
        try {
            int velocity = 127;    // max volume
            int sound = 65;
            Synthesizer synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
            MidiChannel channel = synthesizer.getChannels()[9];  // drums channel.
            for (int i = 0; i < 10; i++) {
                Thread.sleep(100);
                channel.noteOn(sound + i, velocity);
                Thread.sleep(100);
                channel.noteOff(sound + i);
            }
        } catch (MidiUnavailableException | InterruptedException e1) {
            e1.printStackTrace();
        }
    }

    public static void showOutOfMemoryDialog() {
        String message = "<html><b>Out of memory error.</b> You can try <ul>" +
                "<li>decreasing the undo levels" +
                "<li>decreasing the number of layers" +
                "<li>working with smaller images";
        String title = "Out of memory error.";
        JOptionPane.showMessageDialog(getParentForDialogs(), message, title, JOptionPane.ERROR_MESSAGE);
    }


    public static void showInfoDialog(String title, String msg) {
        JOptionPane.showMessageDialog(getParentForDialogs(), msg, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showErrorDialog(String title, String msg) {
        JOptionPane.showMessageDialog(getParentForDialogs(), msg, title, JOptionPane.ERROR_MESSAGE);
    }

    public static void setMainWindowInitialized(boolean mainWindowInitialized) {
        ExceptionHandler.mainWindowInitialized = mainWindowInitialized;
    }

    public static boolean isMainWindowInitialized() {
        return mainWindowInitialized;
    }

    public static void showNotImageLayerDialog() {
        if (!Build.CURRENT.isRobotTest()) {
            showErrorDialog("Error", "The active layer is not an image layer");
        }
    }
}
