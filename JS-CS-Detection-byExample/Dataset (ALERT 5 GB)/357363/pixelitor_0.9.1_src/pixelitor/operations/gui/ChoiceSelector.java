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

import pixelitor.utils.DefaultButton;

import javax.swing.*;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Displays a JComboBox
 */
public class ChoiceSelector extends JPanel implements ActionListener {
    private IntChoiceParam model;
    private JComboBox comboBox;
    private DefaultButton defaultButton;

    public ChoiceSelector(final IntChoiceParam model) {
        this.model = model;
        setLayout(new FlowLayout(FlowLayout.LEFT));

        comboBox = new JComboBox(model);
        comboBox.addActionListener(this);
        add(comboBox);

        defaultButton = new DefaultButton(model);
        int buttonSize = comboBox.getPreferredSize().height;
        defaultButton.setPreferredSize(new Dimension(buttonSize, buttonSize));
        add(defaultButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        model.setSelectedItem(comboBox.getSelectedItem());   // TODO is this necessary?
        defaultButton.updateState();
    }

}