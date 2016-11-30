/*
 * Copyright 2010 Laszlo Balazs-Csiki
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
package pixelitor.filters.gui;

import pixelitor.layers.BlendingMode;

/**
 *
 */
public class BlendingModeParam extends IntChoiceParam {
    private final BlendingMode[] blendingModes;

    public BlendingModeParam(BlendingMode... blendingModes) {
        this(blendingModes, false);
    }

    public BlendingModeParam(BlendingMode[] blendingModes, boolean ignoreRandomize) {
        super("Blending Mode", transformToValues(blendingModes), ignoreRandomize);
        this.blendingModes = blendingModes;
    }

    public BlendingMode getSelectedBlendingMode() {
        return blendingModes[super.getValue()];
    }

    private static Value[] transformToValues(BlendingMode... blendingModes) {
        int size = blendingModes.length;
        Value[] retVal = new Value[size];
        for (int i = 0; i < size; i++) {
            retVal[i] = new Value(blendingModes[i].toString(), i);
        }
        return retVal;
    }
}
