/*
 * Copyright 2009 László Balázs-Csíki
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

import pixelitor.AppLogic;
import pixelitor.ImageComponent;
import pixelitor.utils.DebugUtils;
import pixelitor.utils.Utils;

import java.awt.image.BufferedImage;
import java.awt.Composite;
import java.awt.AlphaComposite;

/**
 * Represents an image layer.
 */
public class Layer {
    private ImageComponent imageComponent;
    private BufferedImage bufferedImage = null;
    private float opacity = 1.0f;
    private String name;
    private int width;
    private int height;
    private boolean visible = true;
    private LayerButton layerButton;

    // for dialog previews
    private BufferedImage backupForPreviewBufferedImage = null;

    /**
     * Creates a new layer with the given image and 100% opacity
     */
    public Layer(ImageComponent imageComponent, BufferedImage bufferedImage, String name, int width, int height) {
        this(imageComponent, bufferedImage, name, width, height, 1.0f);
    }

    /**
     * Creates a new layer with the given image and opacity
     */
    public Layer(ImageComponent imageComponent, BufferedImage bufferedImage, String name, int width, int height, float opacity) {
        this.imageComponent = imageComponent;
        this.bufferedImage = bufferedImage;
        this.opacity = opacity;
        this.name = name;

        this.width = width;
        this.height = height;

        layerButton = new LayerButton(this);
        if (this.bufferedImage == null) {
            this.bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public void setBufferedImage(BufferedImage newBufferedImage) {
        if (newBufferedImage == null) {
            throw new IllegalArgumentException("newBufferedImage is null");
        }
        this.bufferedImage = newBufferedImage;
    }

    public boolean isVisible() {
        return visible;
    }

    // called when a new dialog apperas
    public void startPreviewing() {
        this.backupForPreviewBufferedImage = this.bufferedImage;

        this.bufferedImage = Utils.copyImage(bufferedImage);
    }

    // called when a new adjustment is made in the dialog, before running the op
    public void startNewPreviewFromDialog() {
        // restore the original
        if (backupForPreviewBufferedImage == null) {
            throw new IllegalStateException("backupForPreviewBufferedImage is null");
        }
        this.bufferedImage = this.backupForPreviewBufferedImage;
    }

    // cancel was pressed in the preview dialog
    public void cancelPreviewing() {
        if (backupForPreviewBufferedImage == null) {
            throw new IllegalStateException("backupForPreviewBufferedImage is null");
        }
        this.bufferedImage = this.backupForPreviewBufferedImage;
    }

    public LayerButton getLayerButton() {
        return layerButton;
    }

    public void setLayerButton(LayerButton layerButton) {
        this.layerButton = layerButton;
    }

    @Override
    public String toString() {
        return "Layer{" +
                "opacity=" + opacity +
                ", name='" + name + '\'' +
                ", empty=" + (bufferedImage == null) +
                ", visible=" + visible +
                ", bufferedImage=" + DebugUtils.getBufferedImageDescription(bufferedImage) +
                '}';
    }

    public String getName() {
        return name;
    }

    public void makeActive() {
        imageComponent.setActiveLayer(this);
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        AppLogic.getActiveImageComponent().repaint();
    }

    public Composite getComposite() {
        return AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity);
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    public float getOpacity() {
        return opacity;
    }

    public String getLayerDebugInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nis the BufferedImage null: ").append(bufferedImage == null);
        if (bufferedImage != null) {
            int type = bufferedImage.getType();

            sb.append("\ntype = ").append(DebugUtils.getBufferedImageTypeDescription(type));
        }

        return sb.toString();
    }
}
