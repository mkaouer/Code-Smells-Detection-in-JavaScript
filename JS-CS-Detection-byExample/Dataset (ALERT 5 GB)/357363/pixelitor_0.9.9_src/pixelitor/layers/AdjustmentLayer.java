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
package pixelitor.layers;

import pixelitor.Composition;
import pixelitor.operations.Operation;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * A global adjustment to all the layers bellow
 */
public class AdjustmentLayer extends Layer {
    private static final long serialVersionUID = 2L;

    private Operation operation;

    public AdjustmentLayer(Composition comp, String name, Operation operation) {
        super(comp, name);
        this.operation = operation;
    }

    @Override
    public Layer duplicate() {
        // TODO operation  should be copied so that it can be adjusted independently
        return new AdjustmentLayer(comp, name, operation);
    }

    @Override
    public boolean notTranslated() {
        return true;
    }

    @Override
    public void mergeDownOn(Layer bellow) {
        // TODO
    }

    @Override
    public BufferedImage paintLayer(Graphics2D g, boolean firstVisibleLayer, BufferedImage imageSoFar) {
        System.out.println("AdjustmentLayer.paintLayer CALLED - RUNNING AN OP");
        return operation.executeForOneLayer(imageSoFar);
    }
}
