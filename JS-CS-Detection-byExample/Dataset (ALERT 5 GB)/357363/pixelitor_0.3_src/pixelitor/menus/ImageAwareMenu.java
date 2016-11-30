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

import javax.swing.JMenu;

import pixelitor.AppLogic;
import pixelitor.ImageComponent;
import pixelitor.utils.ImageChangeListener;
import pixelitor.utils.ImageChangedEvent;

public class ImageAwareMenu extends JMenu implements ImageChangeListener {

	public ImageAwareMenu(String s) {
		super(s);
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
	public void noOpenImageAnymore() {
		setEnabled(false);
	}

    @Override
    public void imageContentChanged(ImageChangedEvent e) {
    }
}
