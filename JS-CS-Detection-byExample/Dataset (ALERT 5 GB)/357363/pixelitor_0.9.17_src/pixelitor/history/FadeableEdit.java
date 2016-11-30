/*
 * Copyright 2010 L�szl� Bal�zs-Cs�ki
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
package pixelitor.history;

import pixelitor.Composition;

import java.awt.image.BufferedImage;

/**
 *
 */
public abstract class FadeableEdit extends PixelitorEdit {
    private boolean died = false;  // alive in superclass is private...

    FadeableEdit(Composition comp, String name) {
        super(comp, name);
    }

    public abstract BufferedImage getBackupImage();

    @Override
    public void die() {
        super.die();

        died = true;
    }

    public boolean isAlive() {
        return !died;
    }

}
