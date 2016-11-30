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

package pixelitor.menus;

import pixelitor.AppLogic;
import pixelitor.Composition;
import pixelitor.ImageComponent;
import pixelitor.utils.ImageSwitchListener;

import javax.swing.*;

/**
 * A JRadioButtonMenuItem that becomes active only if there is an open image
 */
public class ImageAwareRadioButtonMenuItem extends JRadioButtonMenuItem implements ImageSwitchListener {
    ImageAwareRadioButtonMenuItem(String name) {
        super(name);
        init();
    }

    public ImageAwareRadioButtonMenuItem(Action a) {
        super(a);
        init();
    }

    private void init() {
        setEnabled(false);
        AppLogic.addImageChangeListener(this);
    }

    @Override
    public void newImageOpened() {
        setEnabled(true);
    }

    @Override
    public void activeImageHasChanged(Composition comp) {

    }

    @Override
    public void noOpenImageAnymore() {
        setEnabled(false);
    }
}