/*
 * Copyright 2009 László Balázs-Csíki
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
import pixelitor.utils.GUIUtils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;

/**
 *
 */
public class FgBgColorSelector extends JLayeredPane implements ActionListener {
    private JButton fgButton;
    private JButton bgButton;
    private static Color fgColor = Color.black;
    private static Color bgColor = Color.white;

    private static final int BUTTON_SIZE = 32;
    JColorChooser colorChooser;

    public FgBgColorSelector() {
        fgColor = AppPreferences.loadFgColor();
        bgColor = AppPreferences.loadBgColor();

        fgButton = new JButton();
        bgButton = new JButton();
        fgButton.addActionListener(this);
        bgButton.addActionListener(this);

//        Box verticalBox = Box.createVerticalBox();
//        verticalBox.add(fgButton);
//        verticalBox.add(bgButton);
//        add(verticalBox);

        setLayout(null);
        add(fgButton, new Integer(2));
        add(bgButton, new Integer(1));
        fgButton.setSize(BUTTON_SIZE, BUTTON_SIZE);
        bgButton.setSize(BUTTON_SIZE, BUTTON_SIZE);
        fgButton.setLocation(0, 0);
        bgButton.setLocation(BUTTON_SIZE/2, BUTTON_SIZE/2);

        int preferredSize = (int) (BUTTON_SIZE * 1.5);
        Dimension preferredDim = new Dimension(preferredSize, preferredSize);
        setPreferredSize(preferredDim);
        setMinimumSize(preferredDim);
        setMaximumSize(preferredDim);

        fgButton.setBackground(fgColor);
        bgButton.setBackground(bgColor);

        Action resetToDefaultAction = new AbstractAction("reset") {
            @Override
            public void actionPerformed(ActionEvent e) {
                fgColor = Color.black;
                fgButton.setBackground(Color.black);

                bgColor = Color.white;
                bgButton.setBackground(Color.white);
            }
        };
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('d'), "reset");
        getActionMap().put("reset", resetToDefaultAction);

        Action switchColorsAction = new AbstractAction("switch") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color tmpFgColor = fgColor;

                fgColor = bgColor;
                fgButton.setBackground(bgColor);

                bgColor = tmpFgColor;
                bgButton.setBackground(tmpFgColor);
            }
        };
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('x'), "switch");
        getActionMap().put("switch", switchColorsAction);

        initColorChooser();
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
        AbstractColorChooserPanel[] newChooserPanels = new AbstractColorChooserPanel[] {hsb, rgb, swatch};
        colorChooser.setChooserPanels(newChooserPanels);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == fgButton) {

            fgColor = getColorFromDialog("Select Foreground Color", fgColor);
            if(fgColor != null) {
                fgButton.setBackground(fgColor);
            }
        } else if (e.getSource() == bgButton) {
            bgColor = getColorFromDialog("Select Background Color", bgColor);
            if(bgColor != null) {
                bgButton.setBackground(bgColor);
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

    public static Color getBgColor() {
        return bgColor;
    }

    public static Color getFgColor() {
        return fgColor;
    }

    public static void main(String[] args) {
        GUIUtils.testJComponent(new FgBgColorSelector());
    }

}
