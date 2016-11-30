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
package pixelitor;

import pixelitor.utils.AppPreferences;
import pixelitor.utils.ImageUtils;

import javax.swing.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 */
public class FgBgColorSelector extends JLayeredPane implements ActionListener {
    private JButton fgButton = new JButton();
    private JButton bgButton = new JButton();
    private Color fgColor = Color.black;
    private Color bgColor = Color.white;

    private JButton defaultsButton = new JButton();
    private JButton swapButton = new JButton();
    private JButton randomizeButton = new JButton();

    private static final int BIG_BUTTON_SIZE = 32;
    private static final int SMALL_BUTTON_SIZE = 16;
    private static final int SMALL_BUTTON_VERTICAL_SPACE = 20;

    private JColorChooser colorChooser;

    public static FgBgColorSelector INSTANCE = new FgBgColorSelector();

    private FgBgColorSelector() {
        setLayout(null);
        initButton(fgButton, "Set Foreground Color", BIG_BUTTON_SIZE, 2);
        fgButton.addActionListener(this);
        initButton(bgButton, "Set Background Color", BIG_BUTTON_SIZE, 1);
        bgButton.addActionListener(this);
        initButton(defaultsButton, "Reset Default Colors (D)", SMALL_BUTTON_SIZE, 1);
        initButton(swapButton, "Swap Colors (X)", SMALL_BUTTON_SIZE, 1);
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

        Action resetToDefaultAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setFgColor(Color.black);
                setBgColor(Color.white);
            }
        };
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('d'), "reset");
        getActionMap().put("reset", resetToDefaultAction);
        defaultsButton.addActionListener(resetToDefaultAction);

        Action switchColorsAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color tmpFgColor = fgColor;

                setFgColor(bgColor);
                setBgColor(tmpFgColor);
            }
        };
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('x'), "switch");
        getActionMap().put("switch", switchColorsAction);
        swapButton.addActionListener(switchColorsAction);

        Action randomizeColorsAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setFgColor(ImageUtils.getRandomColor());
                setBgColor(ImageUtils.getRandomColor());
            }
        };
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('r'), "randomize");
        getActionMap().put("randomize", randomizeColorsAction);
        randomizeButton.addActionListener(randomizeColorsAction);


        initColorChooser();
    }

    private void initButton(JButton button, String toolTipText, int size, int addLayer) {
        button.setToolTipText(toolTipText);
        button.setSize(size, size);
        add(button, new Integer(addLayer));
    }


    private void initColorChooser() {
        colorChooser = new JColorChooser();
        AbstractColorChooserPanel[] chooserPanels = colorChooser.getChooserPanels();
        AbstractColorChooserPanel swatch = chooserPanels[0];
        AbstractColorChooserPanel hsb = chooserPanels[1];
        AbstractColorChooserPanel rgb = chooserPanels[2];
        chooserPanels[0] = hsb;
        chooserPanels[1] = rgb;
        chooserPanels[2] = swatch;
        AbstractColorChooserPanel[] newChooserPanels = new AbstractColorChooserPanel[]{hsb, rgb, swatch};
        colorChooser.setChooserPanels(newChooserPanels);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == fgButton) {
            Color c = getColorFromDialog("Set foreground color", fgColor);
            if (c != null) {
                setFgColor(c);
            }
        } else if (e.getSource() == bgButton) {
            Color c = getColorFromDialog("Set background color", bgColor);
            if (c != null) {
                setBgColor(c);
            }
        }
    }

    private Color getColorFromDialog(String title, Color defaultColor) {
        colorChooser.setColor(defaultColor);

        JDialog dialog = JColorChooser.createDialog(this, title, true, colorChooser, null, null);
        dialog.setVisible(true);

        Color c = colorChooser.getColor();
        return c;
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


    public void setFgColor(Color c) {
        Color old = fgColor;
        fgColor = c;
        fgButton.setBackground(fgColor);
        if(old != null) {
            firePropertyChange("FG", old, fgColor);
        }
    }

    public void setBgColor(Color c) {
        Color old = bgColor;
        bgColor = c;
        bgButton.setBackground(bgColor);
        if(old != null) {
            firePropertyChange("BG", old, bgColor);
        }
    }
}
