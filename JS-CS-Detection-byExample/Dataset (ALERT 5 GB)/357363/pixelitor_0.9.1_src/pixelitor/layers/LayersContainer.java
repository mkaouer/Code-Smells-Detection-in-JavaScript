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

import pixelitor.AppLogic;
import pixelitor.ImageComponent;
import pixelitor.utils.ImageChangeListener;
import pixelitor.utils.ImageChangedEvent;

import javax.swing.*;
import java.awt.BorderLayout;

/**
 * The part of the GUI that manages the layers of an image.
 */
public class LayersContainer extends JPanel implements ImageChangeListener {
    private LayersPanel layersPanel;
    private JScrollPane scrollPane;

    public static LayersContainer INSTANCE = new LayersContainer();

    private LayersContainer() {
        setLayout(new BorderLayout());

        scrollPane = new JScrollPane();

        add(scrollPane, BorderLayout.CENTER);


        add(LayerBlendingModePanel.INSTANCE, BorderLayout.NORTH);


        JPanel southPanel = new JPanel();

        JButton addButton = new JButton(AddNewLayerAction.INSTANCE);
        JButton deleteButton = new JButton(DeleteActiveLayerAction.INSTANCE);

        southPanel.add(addButton);
        southPanel.add(deleteButton);
        add(southPanel, BorderLayout.SOUTH);

        AppLogic.addImageChangeListener(this);
        setBorder(BorderFactory.createTitledBorder("Layers"));

//        setBorder(BorderFactory.createLineBorder(Color.RED));
    }

    public void setLayersPanel(LayersPanel newLayersPanel) {
        if (layersPanel != null) {
            scrollPane.remove(layersPanel);
        }
        layersPanel = newLayersPanel;
        scrollPane.setViewportView(newLayersPanel);
    }

    @Override
    public void imageContentChanged(ImageChangedEvent e) {

    }

    @Override
    public void noOpenImageAnymore() {
        scrollPane.setViewportView(null);
    }

    @Override
    public void newImageOpened() {
    }

    @Override
    public void activeImageHasChanged(ImageComponent imageComponent) {
        // the layers pane of the imageComponent is set in
        // ImageComponent.onActivation()
    }
}

