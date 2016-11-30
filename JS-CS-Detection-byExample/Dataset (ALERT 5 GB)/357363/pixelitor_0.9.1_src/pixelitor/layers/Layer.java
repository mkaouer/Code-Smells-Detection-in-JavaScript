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
package pixelitor.layers;

import pixelitor.AppLogic;
import pixelitor.History;
import pixelitor.ImageChangeReason;
import pixelitor.ImageComponent;
import pixelitor.operations.ImageEdit;
import pixelitor.utils.ImageUtils;
import pixelitor.utils.debug.LayerNode;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 * Represents an image layer.
 */
public class Layer {

    private ImageComponent ic;
    private BufferedImage bufferedImage = null;

    private BufferedImage tmpDrawingImage = null;
    private Composite tmpDrawingComposite = null;

    private int temporaryTranslationX = 0;
    private int temporaryTranslationY = 0;
    private int translationX = 0;
    private int translationY = 0;

    private float opacity = 1.0f;
    //    private Composite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);
    private BlendingMode blendingMode = BlendingMode.NORMAL;

    private String name;
    private boolean visible = true;
    private LayerButton layerButton;

    // for dialog previews
    private BufferedImage backupForPreviewBufferedImage = null;

    /**
     * Creates a new layer with the given image and opacity
     */
    public Layer(ImageComponent ic, BufferedImage bufferedImage, String name) {
        if (bufferedImage == null) {
            throw new IllegalArgumentException("bufferedImage is null");
        }

        this.ic = ic;
        this.bufferedImage = bufferedImage;
        this.opacity = 1.0f;
        if (name == null) {
            this.name = ic.generateNewLayerName();
        } else {
            this.name = name;
        }

        layerButton = new LayerButton(this);
    }

    /**
     * Creates a new empty layer
     */
    public Layer(ImageComponent ic, String name, int width, int height) {
        this.ic = ic;
        this.opacity = 1.0f;
        if(name == null) {
            this.name = ic.generateNewLayerName();
        } else {
            this.name = name;
        }    

        layerButton = new LayerButton(this);
        this.bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE);
    }

    public Layer duplicate() {
        BufferedImage imageCopy = ImageUtils.copyImage(bufferedImage);
        Layer d = new Layer(ic, imageCopy, name + " copy");
        d.setOpacity(opacity, false);
        d.setTranslationX(translationX);
        d.setTranslationY(translationY);
        d.setBlendingMode(blendingMode, false);

        return d;
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

    // called when a new dialog appears

    public void startPreviewing() {
        this.backupForPreviewBufferedImage = this.bufferedImage;

        this.bufferedImage = ImageUtils.copyImage(bufferedImage);
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

    public void changeImage(BufferedImage img, ImageChangeReason changeReason, String opName) {
        BufferedImage backupImage = null;
        if (changeReason == ImageChangeReason.OP_WITH_PREVIEW_FINISHED) {
            if (img != backupForPreviewBufferedImage) {
                backupImage = backupForPreviewBufferedImage;
            }
        } else if (changeReason.makeBackup()) {
            if (img != bufferedImage) {
                backupImage = bufferedImage;
            }
        }

        if (changeReason.setNewImage()) {
            if (img == null) {
                throw new IllegalArgumentException("trying to update with null image");
            }
            setBufferedImage(img);
        } else {
            // this is a special case, the image is already set,
            // the img parameter is null in this case
        }

        if ((changeReason != ImageChangeReason.UNDO_REDO) && (backupImage != null)) {
            ImageEdit edit = new ImageEdit(opName, ic, backupImage, changeReason);
            History.addEdit(edit, changeReason);
        }
    }

    public LayerButton getLayerButton() {
        return layerButton;
    }

    public void setLayerButton(LayerButton layerButton) {
        this.layerButton = layerButton;
    }

    public String getName() {
        return name;
    }

    public void makeActive() {
        ic.setActiveLayer(this);
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        AppLogic.getActiveImageComponent().imageChanged(true);
    }


    public Composite calculateComposite() {
        return ImageUtils.calculateComposite(blendingMode, opacity);
    }

    public BlendingMode getBlendingMode() {
        return blendingMode;
    }

    public void setBlendingMode(BlendingMode mode, boolean updateGUI) {
        this.blendingMode = mode;
        if (updateGUI) {
            LayerBlendingModePanel.INSTANCE.setBlendingMode(mode);
        }
    }

    public void setOpacity(float newOpacity, boolean updateGUI) {
        if (newOpacity > 1.0f) {
            throw new IllegalArgumentException("newOpacity = " + newOpacity);
        } else if (newOpacity < 0f) {
            throw new IllegalArgumentException("newOpacity = " + newOpacity);
        } else {
            this.opacity = newOpacity;
        }
        if (updateGUI) {
            LayerBlendingModePanel.INSTANCE.setOpacity(newOpacity);
        }
    }

    public float getOpacity() {
        return opacity;
    }


    @Override
    public String toString() {
        LayerNode node = new LayerNode(this);
        return node.toDetailedString();
    }


    /**
     * setTranslationX and setTranslationY programmatically set the translation (for example during testing)
     */
    public void setTranslationX(int translationX) {
        this.translationX = translationX;
        checkForLayerEnlargement();
    }

    /**
     * setTranslationX and setTranslationY programmatically set the translation (for example during testing)
     */
    public void setTranslationY(int translationY) {
        this.translationY = translationY;
        checkForLayerEnlargement();
    }


    /**
     * startTranslation(), endTranslation(), and moveImageRelative(int, int) are used by the Move tool
     */
    public void startTranslation() {
        temporaryTranslationX = 0;
        temporaryTranslationY = 0;
    }

    /**
     * startTranslation(), endTranslation(), and moveImageRelative(int, int) are used by the Move tool
     */
    public void endTranslation() {
        translationX += temporaryTranslationX;
        translationY += temporaryTranslationY;
        temporaryTranslationX = 0;
        temporaryTranslationY = 0;
        checkForLayerEnlargement();
    }

    /**
     * startTranslation(), endTranslation(), and moveImageRelative(int, int) are used by the Move tool
     */
    public void moveImageRelative(int x, int y) {
        temporaryTranslationX = x;
        temporaryTranslationY = y;
    }

    public int getTranslationX() {
        return translationX + temporaryTranslationX;
    }

    public int getTranslationY() {
        return translationY + temporaryTranslationY;
    }

    public Rectangle getBounds() {
        return new Rectangle(translationX, translationY, bufferedImage.getWidth(), bufferedImage.getHeight());
    }


    private void checkForLayerEnlargement() {
        Rectangle canvasBounds = new Rectangle(0, 0, ic.getCanvasWidth(), ic.getCanvasHeight());
        Rectangle layerBounds = getBounds();
        boolean needsEnlarging = !(layerBounds.contains(canvasBounds));
        if (needsEnlarging) {
            if (translationX >= 0) {
                if (translationY >= 0) {
                    enlargeNW();
                } else {
                    enlargeSW();
                }
            } else {
                if (translationY >= 0) {
                    enlargeNE();
                } else {
                    enlargeSE();
                }
            }
        }
    }

    private void enlargeSE() {
        BufferedImage bi = new BufferedImage(
                bufferedImage.getWidth() - translationX,
                bufferedImage.getHeight() - translationY,
                bufferedImage.getType());
        Graphics2D g = bi.createGraphics();
        g.drawImage(bufferedImage, 0, 0, null);
        g.dispose();
        bufferedImage = bi;
    }

    private void enlargeNE() {
        BufferedImage bi = new BufferedImage(
                bufferedImage.getWidth() - translationX,
                bufferedImage.getHeight() + translationY,
                bufferedImage.getType());
        Graphics2D g = bi.createGraphics();
        g.drawImage(bufferedImage, 0, translationY, null);
        g.dispose();
        bufferedImage = bi;
        translationY = 0;
    }


    private void enlargeSW() {
        BufferedImage bi = new BufferedImage(
                bufferedImage.getWidth() + translationX,
                bufferedImage.getHeight() - translationY,
                bufferedImage.getType());
        Graphics2D g = bi.createGraphics();
        g.drawImage(bufferedImage, translationX, 0, null);
        g.dispose();
        bufferedImage = bi;
        translationX = 0;
    }

    private void enlargeNW() {
        BufferedImage bi = new BufferedImage(
                bufferedImage.getWidth() + translationX,
                bufferedImage.getHeight() + translationY,
                bufferedImage.getType());
        Graphics2D g = bi.createGraphics();
        g.drawImage(bufferedImage, translationX, translationY, null);
        g.dispose();
        bufferedImage = bi;
        translationX = 0;
        translationY = 0;
    }

    public void setName(String name) {
        this.name = name;
        layerButton.setName(name);
    }

    public BufferedImage createTmpDrawingLayer(Composite c) {
        // TODO use canvas sizes?
        tmpDrawingImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getType());
        tmpDrawingComposite = c;
        return tmpDrawingImage;
    }

    public void mergeTmpDrawingLayerDown() {
        Graphics2D g = bufferedImage.createGraphics();
        g.setComposite(tmpDrawingComposite);
        g.drawImage(tmpDrawingImage, 0, 0, tmpDrawingImage.getWidth(), tmpDrawingImage.getHeight(), ic);
        tmpDrawingImage = null;
        tmpDrawingComposite = null;
    }

    public void drawLayerOnGraphics(Graphics2D g, boolean isFirstVisibleLayer) {
        if (isFirstVisibleLayer) {  // the first visible layer is always painted with normal mode
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        } else {
            g.setComposite(calculateComposite());
        }

        g.drawImage(bufferedImage, getTranslationX(), getTranslationY(), ic);

        if (tmpDrawingImage != null) {
            g.setComposite(tmpDrawingComposite);
            g.drawImage(tmpDrawingImage, getTranslationX(), getTranslationY(), ic);
        }
    }

    public boolean hasNonNormalBlending() {
        if (blendingMode != BlendingMode.NORMAL) {
            return true;
        }
        if (tmpDrawingComposite != null) {
            if (!(tmpDrawingComposite instanceof AlphaComposite)) {
                return true;
            }
        }
        return false;
    }
}
