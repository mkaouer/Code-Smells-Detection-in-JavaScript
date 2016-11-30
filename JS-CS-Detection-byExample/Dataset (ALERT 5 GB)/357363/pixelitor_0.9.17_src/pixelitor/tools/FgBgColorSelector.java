/*
 * Copyright 2009-2010 L�szl� Bal�zs-Cs�ki
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
package pixelitor.tools;

import com.bric.swing.ColorPicker;
import pixelitor.GlobalKeyboardWatch;
import pixelitor.PixelitorWindow;
import pixelitor.utils.AppPreferences;
import pixelitor.utils.ImageUtils;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 */
public class FgBgColorSelector extends JLayeredPane implements ActionListener {
    private JButton fgButton = new JButton();
    private JButton bgButton = new JButton();
    private Color fgColor = Color.black;
    private Color bgColor = Color.white;

    private static final int BIG_BUTTON_SIZE = 32;
    private static final int SMALL_BUTTON_SIZE = 16;
    private static final int SMALL_BUTTON_VERTICAL_SPACE = 20;

//    private JColorChooser colorChooser;


    public static final FgBgColorSelector INSTANCE = new FgBgColorSelector();
    private Action randomizeColorsAction;
    private boolean xpLF = false;
    private Action resetToDefaultAction;
    private Action switchColorsAction;

    private FgBgColorSelector() {
        setLayout(null);
        initButton(fgButton, "Set Foreground Color", BIG_BUTTON_SIZE, 2);
        fgButton.addActionListener(this);


        initButton(bgButton, "Set Background Color", BIG_BUTTON_SIZE, 1);
        bgButton.addActionListener(this);


        JButton defaultsButton = new JButton();
        initButton(defaultsButton, "Reset Default Colors (D)", SMALL_BUTTON_SIZE, 1);
        JButton swapButton = new JButton();
        initButton(swapButton, "Swap Colors (X)", SMALL_BUTTON_SIZE, 1);
        JButton randomizeButton = new JButton();
        initButton(randomizeButton, "Randomize Colors (R)", SMALL_BUTTON_SIZE, 1);

        defaultsButton.setLocation(0, 0);
        swapButton.setLocation(SMALL_BUTTON_SIZE, 0);
        randomizeButton.setLocation(2 * SMALL_BUTTON_SIZE, 0);

        fgButton.setLocation(0, SMALL_BUTTON_VERTICAL_SPACE);
        bgButton.setLocation(BIG_BUTTON_SIZE / 2, SMALL_BUTTON_VERTICAL_SPACE + BIG_BUTTON_SIZE / 2);

        int preferredHorizontalSize = (int) (BIG_BUTTON_SIZE * 1.5);
        int preferredVerticalSize = preferredHorizontalSize + SMALL_BUTTON_VERTICAL_SPACE;
        Dimension preferredDim = new Dimension(preferredHorizontalSize, preferredVerticalSize);
        setPreferredSize(preferredDim);
        setMinimumSize(preferredDim);
        setMaximumSize(preferredDim);

        setFgColor(AppPreferences.loadFgColor());
        setBgColor(AppPreferences.loadBgColor());

        resetToDefaultAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setFgColor(Color.black);
                setBgColor(Color.white);
            }
        };
        defaultsButton.addActionListener(resetToDefaultAction);

        switchColorsAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color tmpFgColor = fgColor;

                setFgColor(bgColor);
                setBgColor(tmpFgColor);
            }
        };
        swapButton.addActionListener(switchColorsAction);

        randomizeColorsAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setFgColor(ImageUtils.getRandomColor(false));
                setBgColor(ImageUtils.getRandomColor(false));
            }
        };
        randomizeButton.addActionListener(randomizeColorsAction);


        setupKeyboardShortcuts();
//        initColorChooser();
    }


//    public void setLF(String shortName) {
//        setXPLookAndFeel(shortName.equals("Windows"));
//    }

    /**
     * The XP look and feel sucks for colorized buttons, this is a workaround
     * See http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4880747
     * The workaround is activated only for the LF, because other LFs look better without it
     */
//    private void setXPLookAndFeel(boolean xp) {
//        if (xp && !xpLF) { // previously not activated, but now switch on
//            fgButton.setContentAreaFilled(false);
//            fgButton.setOpaque(true);
//            bgButton.setContentAreaFilled(false);
//            bgButton.setOpaque(true);
//        }
//        if (!xp && xpLF) { // previously activated, but now switch off
//            fgButton.setContentAreaFilled(true);
//            bgButton.setContentAreaFilled(true);
//        }
//
//        xpLF = xp;
//
//    }
    private void initButton(JButton button, String toolTipText, int size, int addLayer) {
        button.setToolTipText(toolTipText);
        button.setSize(size, size);
        add(button, Integer.valueOf(addLayer));
    }


//    private void initColorChooser() {
//        if (colorChooser != null) {
//            return;
//        }
//
//        colorChooser = new JColorChooser();
//        AbstractColorChooserPanel[] chooserPanels = colorChooser.getChooserPanels();
//        AbstractColorChooserPanel swatch = chooserPanels[0];
//        AbstractColorChooserPanel hsb = chooserPanels[1];
//        AbstractColorChooserPanel rgb = chooserPanels[2];
//        chooserPanels[0] = hsb;
//        chooserPanels[1] = rgb;
//        chooserPanels[2] = swatch;
//        AbstractColorChooserPanel[] newChooserPanels = new AbstractColorChooserPanel[]{hsb, rgb, swatch};
//        colorChooser.setChooserPanels(newChooserPanels);
//    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == fgButton) {
            Color c = ColorPicker.showDialog(PixelitorWindow.getInstance(), "Set foreground color", fgColor, false);
            if (c != null) {
                setFgColor(c);
            }
        } else if (e.getSource() == bgButton) {
            Color c = ColorPicker.showDialog(PixelitorWindow.getInstance(), "Set background color", bgColor, false);
            if (c != null) {
                setBgColor(c);
            }
        }
    }

    private Color getBgColor() {
        return bgColor;
    }

    private Color getFgColor() {
        return fgColor;
    }

    public static Color getFG() {
        return INSTANCE.getFgColor();
    }

    public static Color getBG() {
        return INSTANCE.getBgColor();
    }

    public static void setFG(Color c) {
        INSTANCE.setFgColor(c);
    }

    public static void setBG(Color c) {
        INSTANCE.setBgColor(c);
    }

    public static void setRandomColors() {
        INSTANCE.randomizeColorsAction.actionPerformed(null);
    }


    public void setFgColor(Color c) {
        Color old = fgColor;
        fgColor = c;
        fgButton.setBackground(fgColor);
        if (old != null) {
            firePropertyChange("FG", old, fgColor);
        }
    }

    public void setBgColor(Color c) {
        Color old = bgColor;
        bgColor = c;
        bgButton.setBackground(bgColor);
        if (old != null) {
            firePropertyChange("BG", old, bgColor);
        }
    }

    private void setupKeyboardShortcuts() {
        GlobalKeyboardWatch.addKeyboardShortCut('d', true, "reset", resetToDefaultAction);
        GlobalKeyboardWatch.addKeyboardShortCut('x', true, "switch", switchColorsAction);
        GlobalKeyboardWatch.addKeyboardShortCut('r', true, "randomize", randomizeColorsAction);
    }

}
