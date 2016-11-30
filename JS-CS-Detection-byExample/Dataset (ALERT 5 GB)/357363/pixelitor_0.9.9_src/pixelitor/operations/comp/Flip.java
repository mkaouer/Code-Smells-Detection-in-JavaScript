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
package pixelitor.operations.comp;

import pixelitor.Composition;
import pixelitor.history.OneLayerUndoable;
import pixelitor.layers.ContentLayer;
import pixelitor.layers.Layer;

/**
 *
 */
public class Flip extends CompOperation {
    private Flip.Direction direction;

    private static Flip horizontalFlip = new Flip(Direction.HORIZONTAL);
    private static Flip verticalFlip = new Flip(Direction.VERTICAL);

    public static Flip createFlipOp(Direction dir) {
        if (dir == Direction.HORIZONTAL) {
            return horizontalFlip;
        } else if (dir == Direction.VERTICAL) {
            return verticalFlip;
        }
        throw new IllegalStateException("should not get here");
    }

    private Flip(Direction dir) {
        super(dir.getName());
        direction = dir;
    }

    @Override
    public void transform(Composition comp) {

        OneLayerUndoable.backup(comp, "Flip");
        int nrLayers = comp.getNrLayers();

        for (int i = 0; i < nrLayers; i++) {
            Layer layer = comp.getLayer(i);
            if(layer instanceof ContentLayer) {
                ContentLayer contentLayer = (ContentLayer) layer;

                contentLayer.flip(direction);
            }
        }

        comp.setDirty(true);
        comp.imageChanged(true, false);
    }


    public enum Direction {
        HORIZONTAL {
            @Override
            public String getName() {
                return "Flip Horizontal";
            }},
        VERTICAL {
            @Override
            public String getName() {
                return "Flip Vertical";
            }};

        public abstract String getName();
    }
}
