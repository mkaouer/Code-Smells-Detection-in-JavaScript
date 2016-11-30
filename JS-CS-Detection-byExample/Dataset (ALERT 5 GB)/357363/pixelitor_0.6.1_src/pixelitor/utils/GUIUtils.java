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


import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import pixelitor.PixelitorWindow;
import pixelitor.menus.LookAndFeelMenu;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.logging.Level;

public final class GUIUtils {

    // this is a utility class with static methods, it should not be instantiated
    private GUIUtils() {
    }

    public static void showExceptionDialog(Exception e) {
        showExceptionDialog(null, e);
    }

    public static void showExceptionDialog(Component parent, Exception e) {
        e.printStackTrace();

        String basicErrorMessage = "An exception occurred: " + e.getMessage();
        ErrorInfo ii = new ErrorInfo("Program error", basicErrorMessage, null, null, e, Level.SEVERE, null);
        JXErrorPane.showDialog(parent, ii);
    }

    public static void showInfoDialog(String title, String msg) {
        JOptionPane.showMessageDialog(PixelitorWindow.getInstance(), msg, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showErrorDialog(String title, String msg) {
        JOptionPane.showMessageDialog(PixelitorWindow.getInstance(), msg, title, JOptionPane.ERROR_MESSAGE);
    }


    public static void testJDialog(JDialog d) {
        JComponent contentPane = (JComponent) d.getContentPane();
        testJComponent(contentPane);
    }

    public static void testJComponent(JComponent p) {
        JFrame frame = new JFrame("Test");
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        menuBar.add(new LookAndFeelMenu("Look and Feel", frame));

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.setLayout(new BorderLayout());

        frame.add(p, BorderLayout.CENTER);

        frame.pack();
        centerOnScreen(frame);
        frame.setVisible(true);
    }

    public static void centerOnScreen(Component component) {
        Rectangle bounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();

        int maxHeight = bounds.height;
        int maxWidth = bounds.width;

        Dimension frameSize = component.getSize();

        if (frameSize.height > maxHeight) {
            frameSize.height = maxHeight;
        }

        if (frameSize.width > maxWidth) {
            frameSize.width = maxWidth;
        }

        component.setLocation((maxWidth - frameSize.width) / 2,
                (maxHeight - frameSize.height) / 2);
        component.setSize(frameSize); // maximize to the available screen space!
    }

    public static BufferedImage crop(BufferedImage input, int x, int y, int width, int height) {
        BufferedImage output = new BufferedImage(width
                , height
                , input.getType());
        Graphics2D g = output.createGraphics();
        AffineTransform t = AffineTransform.getTranslateInstance(-x, -y);
        g.transform(t);
        g.drawImage(input, null, 0, 0);

        return output;
    }

    public static void registerGlobalShortcut(JComponent c, KeyStroke ks, String key, Action action) {
        c.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ks, key);
        c.getActionMap().put(key, action);
    }
}
