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
import java.awt.Component;

/**
 * The container for LayerButton objects
 */
public class LayersPanel extends JPanel {
    //    private List<LayerButton> layerButtons = new LinkedList<LayerButton>();
    private ButtonGroup buttonGroup = new ButtonGroup();

    public LayersPanel() {
        setLayout(new LayersLayout(1, 1));
//        setBorder(BorderFactory.createLineBorder(Color.BLUE));
    }

    public void addLayerButton(LayerButton button, int newLayerIndex) {
        if (button == null) {
            throw new IllegalArgumentException("button is null");
        }
        
        buttonGroup.add(button);
        add(button, newLayerIndex);
        button.setSelected(true);
        revalidate();
        repaint();
    }

    public void deleteLayerButton(LayerButton button) {
        buttonGroup.remove(button);
        remove(button);
        revalidate();
        repaint();
    }

    public void changeLayerOrder(int oldIndex, int newIndex) {
        Component c = getComponent(oldIndex);
        add(c, newIndex);
        revalidate();
    }
}
