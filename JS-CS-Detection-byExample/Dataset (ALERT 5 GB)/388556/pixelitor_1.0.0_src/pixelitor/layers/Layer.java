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
package pixelitor.layers;

import pixelitor.Composition;
import pixelitor.history.History;
import pixelitor.history.LayerBlendingEdit;
import pixelitor.history.LayerOpacityEdit;
import pixelitor.history.LayerRenameEdit;
import pixelitor.history.LayerVisibilityChangeEdit;
import pixelitor.utils.HistogramsPanel;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * The abstract superclass of all layer classes
 */
public abstract class Layer implements Serializable {
    private static final long serialVersionUID = 2L;

    String name;
    private boolean visible = true;
    Composition comp;
    LayerMask layerMask;

    private transient LayerButton layerButton;

    float opacity = 1.0f;
    BlendingMode blendingMode = BlendingMode.NORMAL;

    Layer(Composition comp, String name) {
        this.comp = comp;
        this.name = name;
        this.opacity = 1.0f;

        layerButton = new LayerButton(this);
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean newVisibility, boolean addToHistory) {
        if (this.visible == newVisibility) {
            return;
        }

        this.visible = newVisibility;
        comp.imageChanged(true, true);
        comp.setDirty(true);
        layerButton.setOpenEye(newVisibility);

        if (addToHistory) {
            LayerVisibilityChangeEdit edit = new LayerVisibilityChangeEdit(comp, this, newVisibility);
            History.addEdit(edit);
        }
    }


    public LayerButton getLayerButton() {
        return layerButton;
    }

    String getDuplicateLayerName() {
        String copyString = "copy"; // could be longer or shorter in other languages
        int copyStringLength = copyString.length();

        int index = name.lastIndexOf(copyString);
        if (index == -1) {
            return name + ' ' + copyString;
        }
        if (index == name.length() - copyStringLength) {
            // it ends with the copyString - this was the first copy
            return name + " 2";
        }
        String afterCopyString = name.substring(index + copyStringLength);

        int copyNr;
        try {
            copyNr = Integer.parseInt(afterCopyString.trim());
        } catch (NumberFormatException e) {
            // the part after copy was not a number...
            return name + ' ' + copyString;
        }

        copyNr++;

        return name.substring(0, index + copyStringLength) + ' ' + copyNr;
    }

    public abstract Layer duplicate();

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

    public void setOpacity(float newOpacity, boolean updateGUI, boolean addToHistory) {
        if (addToHistory) {
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

    public void setBlendingMode(BlendingMode mode, boolean updateGUI, boolean addToHistory) {
        if (addToHistory) {
            LayerBlendingEdit edit = new LayerBlendingEdit(this, blendingMode);
            History.addEdit(edit);
        }

        this.blendingMode = mode;
        if (updateGUI) {
            LayerBlendingModePanel.INSTANCE.setBlendingModeNotUI(mode);
        }

        updateAfterBMorOpacityChange();

    }

    public void setName(String newName, boolean addToHistory) {
        String previousName = name;
        this.name = newName;

        if (name.equals(previousName)) { // important because this might be called twice for a single rename
            return;
        }

        layerButton.setName(newName);

        if (addToHistory) {
            LayerRenameEdit edit = new LayerRenameEdit(this, previousName, name);
            History.addEdit(edit);
        }
    }

    public String getName() {
        return name;
    }

    public Composition getComposition() {
        return comp;
    }

    public abstract void mergeDownOn(ImageLayer bellow);

    public void makeActive(boolean addToHistory) {
        comp.setActiveLayer(this, addToHistory);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        layerButton = new LayerButton(this);
    }

    boolean isActiveLayer() {
        return comp.isActiveLayer(this);
    }

    public boolean hasLayerMask() {
        return layerMask != null;
    }

    public void addTestLayerMask() {
        int canvasWidth = comp.getCanvasWidth();
        int canvasHeight = comp.getCanvasHeight();

        BufferedImage bwLayerMask = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = bwLayerMask.createGraphics();

//        Color showColor = new Color(255, 255, 255, 255);
//        Color hideColor = new Color(255, 255, 255, 0);
        Color showColor = Color.WHITE;
        Color hideColor = Color.BLACK;
        GradientPaint mask = new GradientPaint(0, 0,
                showColor,
                0, canvasHeight, hideColor);
        g.setPaint(mask);
        g.fillRect(0, 0, canvasWidth, canvasHeight);
        g.dispose();

        layerMask = new LayerMask(bwLayerMask);

        comp.imageChanged(true, true);
    }

    /**
     * A layer can choose to draw on the Graphics2D or change the given BufferedImage.
     * If the BufferedImage is changed, the method returns the new image and null otherwise.
     * The reason is that adjustment layers change a BufferedImage, while
     */
    public abstract BufferedImage paintLayer(Graphics2D g, boolean firstVisibleLayer, BufferedImage imageSoFar);

    public abstract void resize(int targetWidth, int targetHeight, boolean progressiveBilinear);

    public abstract void crop(Rectangle selectionBounds);

    public LayerMask getLayerMask() {
        return layerMask;
    }
}
