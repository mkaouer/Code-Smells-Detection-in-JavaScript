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
package pixelitor.layers;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * A GUI element representing a layer in an image
 */
public class LayerButton extends JToggleButton {
    private final Layer layer;

    private static final String uiClassID = "LayerButtonUI";

    @Override
    public String getUIClassID() {
        return uiClassID;
    }

    @Override
    public void updateUI() {
        setUI(new LayerButtonUI());
    }

    public void setUI(LayerButtonUI ui) {
        super.setUI(ui);
    }

    public LayerButton(final Layer layer) {
        this.layer = layer;
        setLayout(new LayerButtonLayout(5, 5));
        final JToggleButton visibilityButton = new JCheckBox();
        visibilityButton.setSelected(true);
        visibilityButton.setToolTipText("Layer visibility");
        add(visibilityButton, LayerButtonLayout.VISIBILITY_BUTTON);

        final JTextField nameEditor = new LayerNameEditor(layer);
        add(nameEditor, LayerButtonLayout.NAME_EDITOR);

        addPropertyChangeListener("name", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                nameEditor.setText(getName());
            }
        });

        addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (isSelected()) {
                    layer.makeActive();
                }
            }
        });

        visibilityButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                layer.setVisible(visibilityButton.isSelected());
            }
        });
    }



    @Override
    public String toString() {
        return "LayerButton{" +
                "name='" + layer.getName() + '\'' +
                '}';
    }
}
