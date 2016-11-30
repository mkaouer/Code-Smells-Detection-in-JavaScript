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
import pixelitor.utils.BlendingModePanel;
import pixelitor.utils.ImageChangeListener;
import pixelitor.utils.ImageChangedEvent;
import pixelitor.utils.IntTextField;
import pixelitor.utils.HistogramsPanel;

import javax.swing.*;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The GUI selector for the opacity and blending mode of the layers
 */
public class LayerBlendingModePanel extends BlendingModePanel implements ImageChangeListener, LayerChangeListener {

    public static LayerBlendingModePanel INSTANCE = new LayerBlendingModePanel();

    private LayerBlendingModePanel() {
        super(false);

        AppLogic.addImageChangeListener(this);
        AppLogic.addLayerChangeListener(this);

        opacityTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                blendingModeChanged();
            }
        });

        blendingModeCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                blendingModeChanged();
            }
        });
    }

    private void blendingModeChanged() {
        ImageComponent ic = AppLogic.getActiveImageComponent();
        if (ic != null) {
            Layer activeLayer = ic.getActiveLayer();

            if (activeLayer != null) {
                float floatValue = getOpacity();
                BlendingMode blendingMode = getBlendingMode();
                activeLayer.setBlendingMode(blendingMode, false);
                activeLayer.setOpacity(floatValue, false);
                ic.imageChanged();

                HistogramsPanel hp = HistogramsPanel.INSTANCE;
                if(hp.areHistogramsShown()) {
                    hp.updateWithImage(ic.getCompositeImage());
                }
            }
        }
    }

    @Override
    public void imageContentChanged(ImageChangedEvent e) {

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
    public void activeImageHasChanged(ImageComponent ic) {
        setEnabled(true);

        Layer layer = ic.getActiveLayer();
        activeLayerChanged(layer);
    }

    @Override
    public void layerCountChanged(int newLayerCount) {
//        if (newLayerCount < 2) {
//            blendingModeCombo.setSelectedIndex(0);
//            opacityTextField.setText("100");
//            setEnabled(false);
//        } else {
//            setEnabled(true);
//        }
    }

    @Override
    public void activeLayerChanged(Layer newActiveLayer) {
        float floatOpacity = newActiveLayer.getOpacity();
        int intOpacity = (int) (floatOpacity * 100);
        opacityTextField.setText(String.valueOf(intOpacity));

        BlendingMode bm = newActiveLayer.getBlendingMode();
        blendingModeCombo.setSelectedItem(bm);
    }

    @Override
    public void setEnabled(boolean enabled) {
        opacityTextField.setEnabled(enabled);
        blendingModeCombo.setEnabled(enabled);
    }

    public void setOpacity(float f) {
        int intValue = (int) (f * 100);
        opacityTextField.setText(String.valueOf(intValue));
    }

    public void setBlendingMode(BlendingMode bm) {
        blendingModeCombo.setSelectedItem(bm);
    }
}
