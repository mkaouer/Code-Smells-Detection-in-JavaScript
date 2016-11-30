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

import pixelitor.ImageComponent;
import pixelitor.AppLogic;
import pixelitor.utils.ImageChangeListener;
import pixelitor.utils.ImageChangedEvent;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 *
 */
public class AddEmptyLayerAction extends AbstractAction implements ImageChangeListener {
    public static AddEmptyLayerAction INSTANCE = new AddEmptyLayerAction();

    private AddEmptyLayerAction() {
        super("Add Empty Layer");
        setEnabled(false);
        AppLogic.addImageChangeListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ImageComponent ic = AppLogic.getActiveImageComponent();
        ic.addNewEmptyLayer();
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
    }
}