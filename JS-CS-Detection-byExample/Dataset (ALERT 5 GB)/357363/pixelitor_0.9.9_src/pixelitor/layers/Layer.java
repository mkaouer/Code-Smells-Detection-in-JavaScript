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
import pixelitor.history.History;
import pixelitor.history.LayerBlendingEdit;
import pixelitor.history.LayerOpacityEdit;
import pixelitor.operations.comp.Flip;
import pixelitor.utils.HistogramsPanel;
import pixelitor.utils.ImageUtils;

import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 *
 */
public abstract class Layer implements Serializable {
    private static final long serialVersionUID = 2L;

    protected String name;
    protected boolean visible = true;
    protected Composition comp;

    protected transient LayerButton layerButton;

    protected float opacity = 1.0f;
    protected BlendingMode blendingMode = BlendingMode.NORMAL;

    protected Layer(Composition comp, String name) {
        this.comp = comp;
        this.name = name;
        this.opacity = 1.0f;

        layerButton = new LayerButton(this);
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        comp.imageChanged(true, true);
        comp.setDirty(true);
    }


    /**
     * A layer can choose to draw on the Graphics2D or change the given BufferedImage.
     * If the BufferedImage is changed, the method returns the new image, and null otherwise.
     */
    public abstract BufferedImage paintLayer(Graphics2D g, boolean firstVisibleLayer, BufferedImage imageSoFar);

    public LayerButton getLayerButton() {
        return layerButton;
    }

    public abstract Layer duplicate();

//    public abstract BufferedImage getBufferedImage(); // TODO - remove - adjustment layers can have no image

    public void startPreviewing() {
        // TODO - remove
    }

    public void startNewPreviewFromDialog() {
        // TODO - remove
    }


    public void cancelPreviewing() {
        // TODO - remove
    }

    public Composite calculateComposite() {
        return ImageUtils.calculateComposite(blendingMode, opacity);
    }

    public abstract boolean notTranslated();

    public void mergeTmpDrawingImageDown() {
        // TODO remove
    }

    public BufferedImage getVisibleOnCanvasImage() {
        // TODO remove
        return null;
    }

    public float getOpacity() {
        return opacity;
    }

    public BlendingMode getBlendingMode() {
        return blendingMode;
    }

    private void updateAfterBMorOpacityChange() {
        comp.imageChanged(true, true);

        HistogramsPanel hp = HistogramsPanel.INSTANCE;
        if (hp.areHistogramsShown()) {
            hp.updateFromCompIfShown(comp);
        }
    }


    public void setOpacity(float newOpacity, boolean updateGUI, boolean isUndoRedo) {
        if (!isUndoRedo) {
            LayerOpacityEdit edit = new LayerOpacityEdit(this, opacity);
            History.addEdit(edit);
        }

        if (newOpacity > 1.0f) {
            throw new IllegalArgumentException("newOpacity = " + newOpacity);
        } else if (newOpacity < 0.0f) {
            throw new IllegalArgumentException("newOpacity = " + newOpacity);
        } else {
            this.opacity = newOpacity;
        }
        if (updateGUI) {
            LayerBlendingModePanel.INSTANCE.setOpacity(newOpacity);
        }
        updateAfterBMorOpacityChange();
    }

    public void setBlendingMode(BlendingMode mode, boolean updateGUI, boolean isUndoRedo) {
        if (!isUndoRedo) {
            LayerBlendingEdit edit = new LayerBlendingEdit(this, blendingMode);
            History.addEdit(edit);
        }

        this.blendingMode = mode;
        if (updateGUI) {
            LayerBlendingModePanel.INSTANCE.setBlendingModeNotUI(mode);
        }

        updateAfterBMorOpacityChange();

    }

    public void setName(String name) {
        this.name = name;
        layerButton.setName(name);
    }

    public String getName() {
        return name;
    }

    public Composition getComposition() {
        return comp;
    }

    public abstract void mergeDownOn(Layer bellow);

    public void makeActive() {
        comp.setActiveLayer(this);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        layerButton = new LayerButton(this);
    }

    public boolean isActiveLayer() {
        return comp.isActiveLayer(this);
    }
}
