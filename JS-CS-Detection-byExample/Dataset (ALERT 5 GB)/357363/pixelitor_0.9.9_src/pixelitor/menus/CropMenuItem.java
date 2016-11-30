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

import javax.swing.*;
import java.awt.event.ActionEvent;

public final class CropMenuItem extends JMenuItem {
    public static final CropMenuItem INSTANCE = new CropMenuItem();

    private CropMenuItem() {
        super(new AbstractAction("Crop") {
            @Override
            public void actionPerformed(ActionEvent e) {
                AppLogic.cropActiveImage();
            }
        });
        setEnabled(false);
    }

}
