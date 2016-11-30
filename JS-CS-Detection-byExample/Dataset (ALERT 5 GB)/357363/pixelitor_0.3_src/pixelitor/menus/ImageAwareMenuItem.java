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

package pixelitor.menus;

import javax.swing.Action;
import javax.swing.JMenuItem;

import pixelitor.AppLogic;
import pixelitor.ImageComponent;
import pixelitor.utils.ImageChangeListener;
import pixelitor.utils.ImageChangedEvent;

public class ImageAwareMenuItem extends JMenuItem implements ImageChangeListener {

	public ImageAwareMenuItem(Action a) {
		super(a);
		setEnabled(false);
		AppLogic.addImageChangeListener(this);
	}

	@Override
	public void newImageOpened() {
		setEnabled(true);
	}

    @Override
    public void activeImageHasChanged(ImageComponent imageComponent) {

    }

    @Override
    public void imageContentChanged(ImageChangedEvent e) {
    }

    @Override
	public void noOpenImageAnymore() {
		setEnabled(false);
	}
}
