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
package pixelitor.filters.gui;

import pixelitor.utils.DefaultButton;

import javax.swing.*;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Displays a JComboBox as the GUI for an IntChoiceParam
 */
public class IntChoiceSelector extends JPanel implements ActionListener {
    private final IntChoiceParam model;
    private JComboBox<IntChoiceParam.Value> comboBox;
    private final DefaultButton defaultButton;

    public IntChoiceSelector(final IntChoiceParam model) {
        this.model = model;
        setLayout(new FlowLayout(FlowLayout.LEFT));

        comboBox = new JComboBox<>(model);
        comboBox.addActionListener(this);
        // workaround for nimbus bug
        Dimension comboPreferredSize = comboBox.getPreferredSize();
        comboBox.setPreferredSize(new Dimension(comboPreferredSize.width + 3, comboPreferredSize.height));

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