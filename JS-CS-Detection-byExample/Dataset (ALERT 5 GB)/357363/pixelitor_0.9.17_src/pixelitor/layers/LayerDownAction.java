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
import pixelitor.Composition;
import pixelitor.utils.IconUtils;
import pixelitor.utils.ImageSwitchListener;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 *
 */
public class LayerDownAction extends AbstractAction implements ImageSwitchListener, LayerChangeListener {
    public static final LayerDownAction INSTANCE = new LayerDownAction();

    private LayerDownAction() {
        super("Lower Layer", IconUtils.getSouthArrowIcon());
        setEnabled(false);
        AppLogic.addImageChangeListener(this);
        AppLogic.addLayerChangeListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Composition comp = AppLogic.getActiveComp();
        comp.moveActiveLayerDown();
    }

    @Override
    public void noOpenImageAnymore() {
        setEnabled(false);
    }

    @Override
    public void newImageOpened() {
        Composition comp = AppLogic.getActiveComp();
        checkIndex(comp);
    }

    private void checkIndex(Composition comp) {
        if (comp != null) {
            int activeLayerIndex = comp.getActiveLayerIndex();
            if (activeLayerIndex > 0) {
                setEnabled(true);
            } else {
                setEnabled(false);
            }
        }
    }

    @Override
    public void activeCompositionHasChanged(Composition comp) {
        checkIndex(comp);
    }

    @Override
    public void layerCountChanged(Composition comp, int newLayerCount) {
        checkIndex(comp);
    }

    @Override
    public void activeLayerChanged(Layer newActiveLayer) {
        Composition comp = newActiveLayer.getComposition();
        checkIndex(comp);
    }

    @Override
    public void layerOrderChanged(Composition comp) {
        checkIndex(comp);
    }
}