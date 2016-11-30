/*
 * Copyright 2010 L�szl� Bal�zs-Cs�ki
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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 */
public class BooleanSelector extends JPanel implements ParamGUI {
    private BooleanParam model;
    private JCheckBox checkBox;

    public BooleanSelector(final BooleanParam model) {
        this.model = model;
        setLayout(new FlowLayout(FlowLayout.LEFT));
        checkBox = new JCheckBox();
        checkBox.setSelected(model.getValue());
        add(checkBox);

        checkBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.setValue(checkBox.isSelected(), false);
            }
        });

    }

    @Override
    public void updateGUI() {
        checkBox.setSelected(model.getValue());
    }
}
