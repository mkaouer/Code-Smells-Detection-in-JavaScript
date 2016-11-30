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
package pixelitor.operations.gui;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 */
public class ColorSelector extends JPanel implements ActionListener, ParamGUI {
    private ColorParam model;
    private JButton button;
    private static final int BUTTON_SIZE = 30;

    public ColorSelector(ColorParam model) {
        this.model = model;
        setLayout(new FlowLayout(FlowLayout.LEFT));
//        add(new JLabel(model.getName()));

        button = new JButton();
        button.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        button.setBackground(model.getColor());
        button.addActionListener(this);
        add(button);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Color color = JColorChooser.showDialog(this, "Select Color", model.getColor());
        if (color != null) { // ok was pressed
            button.setBackground(color);
            button.paintImmediately(0, 0, BUTTON_SIZE, BUTTON_SIZE);
            model.setColor(color);
        }
    }

    @Override
    public void updateGUI() {
        button.setBackground(model.getColor());
    }
}
