/*
 * Copyright 2009-2010 L�szl� Bal�zs-Cs�ki
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
import pixelitor.Composition;
import pixelitor.utils.BlendingModePanel;
import pixelitor.utils.ImageSwitchListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The GUI selector for the opacity and blending mode of the layers
 */
public class LayerBlendingModePanel extends BlendingModePanel implements ImageSwitchListener, LayerChangeListener {
    private boolean userInteractionChange = true;

    public static final LayerBlendingModePanel INSTANCE = new LayerBlendingModePanel();

    private LayerBlendingModePanel() {
        super(false);

        AppLogic.addImageChangeListener(this);
        AppLogic.addLayerChangeListener(this);

        opacityDDSlider.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (userInteractionChange) {
                    opacityChanged();
                }
            }
        });

        blendingModeCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (userInteractionChange) {
                    blendingModeChanged();
                }
            }
        });

        setEnabled(false);
    }


    private void opacityChanged() {
        Composition comp = AppLogic.getActiveComp();

        if (comp != null) {
            Layer activeLayer = comp.getActiveLayer();

            if (activeLayer != null) {
                float floatValue = getOpacity();
                activeLayer.setOpacity(floatValue, false, true);
            }
        }
    }

    private void blendingModeChanged() {
        Composition comp = AppLogic.getActiveComp();

        if (comp != null) {
            Layer activeLayer = comp.getActiveLayer();

            if (activeLayer != null) {
                BlendingMode blendingMode = getBlendingMode();
                activeLayer.setBlendingMode(blendingMode, false, true);
            }
        }
    }

    @Override
    public void noOpenImageAnymore() {
        setEnabled(false);
    }

    @Override
    public void newImageOpened() {
        setEnabled(true);
    }

    @Override
    public void activeCompositionHasChanged(Composition comp) {
        setEnabled(true);

//        Layer layer = comp.getActiveLayer();
//        activeLayerChanged(layer);
    }

    @Override
    public void activeCompLayerCountChanged(Composition comp, int newLayerCount) {
    }

    @Override
    public void layerOrderChanged(Composition comp) {

    }

    @Override
    public void activeLayerChanged(Layer newActiveLayer) {
        float floatOpacity = newActiveLayer.getOpacity();
        int intOpacity = (int) (floatOpacity * 100);

        BlendingMode bm = newActiveLayer.getBlendingMode();
        try {
            userInteractionChange = false;
            opacityDDSlider.setValue(intOpacity);
            blendingModeCombo.setSelectedItem(bm);
        } finally {
            userInteractionChange = true;
        }
    }

    public void setBlendingModeNotUI(BlendingMode bm) {
        try {
            userInteractionChange = false;
            blendingModeCombo.setSelectedItem(bm);
        } finally {
            userInteractionChange = true;
        }
    }
}
