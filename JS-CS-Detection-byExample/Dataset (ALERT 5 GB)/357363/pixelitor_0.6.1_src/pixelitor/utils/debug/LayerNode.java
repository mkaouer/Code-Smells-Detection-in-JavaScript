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
package pixelitor.utils.debug;

import pixelitor.layers.Layer;

import java.awt.image.BufferedImage;

/**
 * A debugging node for a Layer
 */
public class LayerNode extends DebugNode {
    public LayerNode(Layer layer) {
        this("Layer", layer);
    }

    public LayerNode(String name, Layer layer) {
        super(name, layer);

        addFloatChild("opacity", layer.getOpacity());
        addStringChildWithQuotes("BlendingMode", layer.getBlendingMode().toString());
        addStringChildWithQuotes("name", layer.getName());
        addIntChild("translationX", layer.getTranslationX());
        addIntChild("translationY", layer.getTranslationY());

        BufferedImage image = layer.getBufferedImage();
        add(new BufferedImageNode(image));
    }

}
