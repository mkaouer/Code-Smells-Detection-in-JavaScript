/*
 * Copyright 2010 László Balázs-Csíki
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
package pixelitor;

import pixelitor.history.FadeableEdit;
import pixelitor.history.History;
import pixelitor.layers.ImageLayer;
import pixelitor.menus.SelectionActions;

import java.awt.image.BufferedImage;

/**
 *
 */
public final class ConsistencyChecks {
    /**
     * Utility class with static methods, must not be instantiated
     */
    private ConsistencyChecks() {
    }


    public static void checkAll() {
        Composition comp = AppLogic.getActiveComp();
        if (comp != null) {
            selectionCheck(comp);
            fadeCheck(comp);
        }
    }

    private static void fadeCheck(Composition comp) {
        if (History.canFade()) {
            ImageLayer layer = comp.getActiveImageLayer();
            if (layer != null) {
                BufferedImage current = layer.getImageOrSubImageIfSelected(false, true);

                FadeableEdit edit = History.getPreviousEditForFade();
                BufferedImage previous = edit.getBackupImage();

                boolean differentWidth = current.getWidth() != previous.getWidth();
                boolean differentHeight = current.getHeight() != previous.getHeight();
                if (differentWidth || differentHeight) {
                    throw new IllegalStateException("Fade would not work now: width = " + current.getWidth() +
                            ", previous width = " + previous.getWidth() +
                            ", height = " + current.getHeight() +
                            ", previous height = " + previous.getHeight()
                    );
                }
            }
        }
    }

    private static void selectionCheck(Composition comp) {
        if (comp.hasSelection()) {
            if (!SelectionActions.areEnabled()) {
                throw new IllegalStateException(comp.getName() + " has selection, but selections are disabled");
            }
        }
    }
}