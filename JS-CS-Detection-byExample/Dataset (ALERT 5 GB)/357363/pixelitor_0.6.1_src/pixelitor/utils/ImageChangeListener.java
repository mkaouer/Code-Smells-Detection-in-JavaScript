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

package pixelitor.utils;

import pixelitor.ImageComponent;

public interface ImageChangeListener {
    /**
     * Called when the user has changed an image
     */
    void imageContentChanged(ImageChangedEvent e);

    /**
     * Called when the user has closed all the images
     */
    void noOpenImageAnymore();

    /**
     * Called when the user has opened a new image
     */
    void newImageOpened();

    /**
     * Called when the used switches to another image
     */
    void activeImageHasChanged(ImageComponent imageComponent);
}
