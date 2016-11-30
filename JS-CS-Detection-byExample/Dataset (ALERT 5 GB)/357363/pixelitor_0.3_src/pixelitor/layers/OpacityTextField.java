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

import pixelitor.AppLogic;
import pixelitor.ImageComponent;
import pixelitor.utils.ImageChangeListener;
import pixelitor.utils.ImageChangedEvent;
import pixelitor.utils.IntTextField;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 *
 */
public class OpacityTextField extends IntTextField implements ImageChangeListener, ActionListener, LayerChangeListener {
    public OpacityTextField() {
        super("100");
        AppLogic.addImageChangeListener(this);
        AppLogic.addLayerChangeListener(this);
        setEnabled(false);
        addActionListener(this);
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
    public void activeImageHasChanged(ImageComponent imageComponent) {
        setEnabled(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int intValue = getIntValue();
        if(intValue > 100) {
            setText("100");
            intValue = 100;
        } else if(intValue < 0) {
            setText("0");
            intValue = 0;
        }

        float floatValue = intValue / 100f;
        ImageComponent ic = AppLogic.getActiveImageComponent();
        ic.getActiveLayer().setOpacity(floatValue);
        ic.repaint();
    }

    @Override
    public void layerCountChanged(int newLayerCount) {

    }

    @Override
    public void activeLayerChanged(Layer newActiveLayer) {
        float floatOpacity = newActiveLayer.getOpacity();
        int intOpacity = (int) (floatOpacity * 100);
        setText(String.valueOf(intOpacity));
    }
}
