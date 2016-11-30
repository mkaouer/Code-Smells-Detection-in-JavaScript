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
package pixelitor.layers;

import javax.swing.*;
import java.awt.event.*;
import java.awt.Container;

/**
 * A textfiled for layes names that becomes editable if double-clicked
 */
public class LayerNameEditor extends JTextField {
    public LayerNameEditor(String text) {
        super(text);
        disableEditing();

        // enable if double-clicked
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    enableEditing();
                } else {
                    requestFocus();
                    Container container = getParent();
                    if(container != null) {
                        LayerButton b = (LayerButton) container;
                        b.setSelected(true);
                    }
                }
            }
        });

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                disableEditing();
            }
        });

        // disable if enter pressed
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                disableEditing();
            }
        });

    }

    private void enableEditing() {
        setEnabled(true);
        setEditable(true);
        requestFocus();
        selectAll();
    }

    private void disableEditing() {
        setEnabled(false);
        setEditable(false);
    }
}
