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
package pixelitor.operations.comp;

import pixelitor.Composition;
import pixelitor.history.OneLayerUndoable;
import pixelitor.layers.ContentLayer;
import pixelitor.layers.Layer;

/**
 *
 */
public class Rotate extends CompOperation {
    private int angleDegree;

//    private int newWidth;
//    private int newHeight;
    private int newCanvasWidth;
    private int newCanvasHeight;
//    private int newTranslationXAbs;
//    private int newTranslationYAbs;

    public Rotate(int angleDegree, String name) {
        super(name);
        this.angleDegree = angleDegree;
    }

    @Override
    public void transform(Composition comp) {

        OneLayerUndoable.backup(comp, "Rotate");
        int nrLayers = comp.getNrLayers();

        int canvasWidth = comp.getCanvasWidth();
        int canvasHeight = comp.getCanvasHeight();
        rotateCanvas(canvasWidth, canvasHeight);

        for (int i = 0; i < nrLayers; i++) {
            Layer layer = comp.getLayer(i);
            if(layer instanceof ContentLayer) {
                ContentLayer contentLayer = (ContentLayer) layer;
                contentLayer.rotateLayer(angleDegree);
            }
        }

        comp.updateCanvasSize(newCanvasWidth, newCanvasHeight);
        comp.setDirty(true);
        comp.imageChanged(true, false);
    }

    private void rotateCanvas(int canvasWidth, int canvasHeight) {
        if (angleDegree == 90 || angleDegree == 270) {
            newCanvasWidth = canvasHeight;
            newCanvasHeight = canvasWidth;
        } else {
            newCanvasWidth = canvasWidth;
            newCanvasHeight = canvasHeight;
        }
    }
}
