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

import pixelitor.Composition;
import pixelitor.history.History;
import pixelitor.ImageChangeReason;
import pixelitor.history.ImageEdit;
import pixelitor.operations.comp.Flip;
import pixelitor.tools.Tool;
import pixelitor.utils.GUIUtils;
import pixelitor.utils.ImageUtils;
import pixelitor.utils.debug.ImageLayerNode;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Represents an image layer.
 */
public class ImageLayer extends ContentLayer {
    private static final long serialVersionUID = 2L;

    // the value of this field must be set by setBufferedImage (as opposed to setting the field directly)
    // even for internal methods so that is is never null, and it can be debugged
    private transient BufferedImage bufferedImage = null;

    // this image acts as a temporary layer
    private transient BufferedImage tmpDrawingImage = null;
    private transient Composite tmpDrawingComposite = null;

    // for dialog previews
    private transient BufferedImage backupForPreviewBufferedImage = null;

    /**
     * Creates a new layer with the given image and opacity
     */
    public ImageLayer(Composition comp, BufferedImage bufferedImage, String name) {
        super(comp, name == null ? comp.generateNewLayerName(): name);
        if (bufferedImage == null) {
            throw new IllegalArgumentException("bufferedImage is null");
        }

        setBufferedImage(bufferedImage);
    }

    /**
     * Creates a new empty layer
     */
    public ImageLayer(Composition comp, String name, int width, int height) {
        super(comp, name == null ? comp.generateNewLayerName(): name);

        setBufferedImage(new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE));
    }

    public Layer duplicate() {
        BufferedImage imageCopy = ImageUtils.copyImage(bufferedImage);
        ImageLayer d = new ImageLayer(comp, imageCopy, name + " copy");
        d.setOpacity(opacity, false, false);
        d.setTranslationX(translationX);
        d.setTranslationY(translationY);
        d.setBlendingMode(blendingMode, false, false);

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
        comp.imageChanged(false, false);
    }

    /**
     * Called when a new dialog appears
     */
    public void startPreviewing() {
        this.backupForPreviewBufferedImage = this.bufferedImage;

        setBufferedImage(ImageUtils.copyImage(bufferedImage));
    }

    /**
     * Called when a new adjustment is made in the dialog, before running the op
     */
    public void startNewPreviewFromDialog() {
        restoreOriginalFromPreviewBackup();
    }

    /**
     * Called when cancel was pressed in the preview dialog
     */
    public void cancelPreviewing() {
        restoreOriginalFromPreviewBackup();
    }

    private void restoreOriginalFromPreviewBackup() {
        if (backupForPreviewBufferedImage == null) {
            throw new IllegalStateException("backupForPreviewBufferedImage is null");
        }

        // restore the original
        setBufferedImage(this.backupForPreviewBufferedImage);
    }

    public void changeImage(BufferedImage img, ImageChangeReason changeReason, String opName) {
        BufferedImage backupImage = null;
        if (changeReason == ImageChangeReason.OP_WITH_PREVIEW_FINISHED) {
            if (img != backupForPreviewBufferedImage) {
                backupImage = backupForPreviewBufferedImage;
            }
        } else if (changeReason.makeUndoBackup()) {
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

        if ((changeReason.makeUndoBackup()) && (backupImage != null)) {
            ImageEdit edit = new ImageEdit(opName, comp, backupImage, true);
            History.addEdit(edit);
        }
    }

    @Override
    public String toString() {
        ImageLayerNode node = new ImageLayerNode(this);
        return node.toDetailedString();
    }

    public Rectangle getBounds() {
        return new Rectangle(translationX, translationY, bufferedImage.getWidth(), bufferedImage.getHeight());
    }

    public boolean checkForLayerEnlargement() {
        Rectangle canvasBounds = comp.getCanvasBounds();
        Rectangle layerBounds = getBounds();
        boolean needsEnlarging = !(layerBounds.contains(canvasBounds));
        if (needsEnlarging) {
            try {
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
            } catch (OutOfMemoryError e) {
                GUIUtils.showOutOfMemoryDialog();
            }
        }
        return needsEnlarging;
    }

    private void enlargeSE() {
        BufferedImage bi = new BufferedImage(
                bufferedImage.getWidth() - translationX,
                bufferedImage.getHeight() - translationY,
                bufferedImage.getType());
        Graphics2D g = bi.createGraphics();
        g.drawImage(bufferedImage, 0, 0, null);
        g.dispose();
        setBufferedImage(bi);
    }

    private void enlargeNE() {
        BufferedImage bi = new BufferedImage(
                bufferedImage.getWidth() - translationX,
                bufferedImage.getHeight() + translationY,
                bufferedImage.getType());
        Graphics2D g = bi.createGraphics();
        g.drawImage(bufferedImage, 0, translationY, null);
        g.dispose();
        setBufferedImage(bi);
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
        setBufferedImage(bi);
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
        setBufferedImage(bi);
        translationX = 0;
        translationY = 0;
    }


    public BufferedImage createTmpDrawingImage(Composite c) {
        // TODO use canvas sizes?
        tmpDrawingImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getType());
        tmpDrawingComposite = c;
        return tmpDrawingImage;
    }

    public void mergeTmpDrawingImageDown() {
        if (tmpDrawingImage == null) {
            return;
        }
        Graphics2D g = bufferedImage.createGraphics();
        g.setComposite(tmpDrawingComposite);
        g.drawImage(tmpDrawingImage, 0, 0, tmpDrawingImage.getWidth(), tmpDrawingImage.getHeight(), null);
        g.dispose();
        tmpDrawingImage = null;
        tmpDrawingComposite = null;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        ImageUtils.serializeImage(out, bufferedImage);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        setBufferedImage(ImageUtils.deserializeImage(in));
    }

    /**
     * Should be called when this layer is not needed anymore
     */
    // TODO when to call it? at DeleteLayerEdit.die?
    public void dispose() {
        bufferedImage.flush();
        bufferedImage = null;
        if (tmpDrawingImage != null) {
            tmpDrawingImage.flush();
            tmpDrawingImage = null;
        }
    }

    /**
     * Returns the sub-image that is currently inside the canvas, therefore contributes to the final image
     */
    public BufferedImage getVisibleOnCanvasImage() {
        if(notTranslated()) {
            return bufferedImage;
        }
        return bufferedImage.getSubimage(-getTranslationX(), -getTranslationY(), comp.getCanvasWidth(), comp.getCanvasHeight());
    }

    public boolean notTranslated() {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        int canvasWidth = comp.getCanvasWidth();
        int canvasHeight = comp.getCanvasHeight();

        return (width == canvasWidth) && (height == canvasHeight);
    }

    public void flip(Flip.Direction direction) {
        int translationXAbs = - getTranslationX();
        int translationYAbs = - getTranslationY();
        int newTranslationXAbs = 0;
        int newTranslationYAbs = 0;

        BufferedImage src = getBufferedImage();
        int canvasWidth = comp.getCanvasWidth();
        int canvasHeight = comp.getCanvasHeight();
        int imageWidth = src.getWidth();
        int imageHeight = src.getHeight();

        BufferedImage dest = new BufferedImage(imageWidth, imageHeight, src.getType());

        Graphics2D g2 = dest.createGraphics();


        if (direction == Flip.Direction.HORIZONTAL) {
            g2.translate(imageWidth, 0);
            g2.scale(-1, 1);

            newTranslationXAbs = imageWidth - canvasWidth - translationXAbs;
            newTranslationYAbs = translationYAbs;
        } else {
            g2.translate(0, imageHeight);
            g2.scale(1, -1);

            newTranslationXAbs = translationXAbs;
            newTranslationYAbs = imageHeight - canvasHeight - translationYAbs;
        }

        g2.drawImage(src, 0, 0, imageWidth, imageHeight, null);
        g2.dispose();

        setTranslationX(- newTranslationXAbs);
        setTranslationY(- newTranslationYAbs);

        setBufferedImage(dest);
    }

    @Override
    public void mergeDownOn(Layer bellow) {
        if (!(bellow instanceof ImageLayer)) {
             return;
        }
        ImageLayer bellowImageLayer = (ImageLayer) bellow;

        int aX = getTranslationX();
        int aY = getTranslationY();
        BufferedImage bellowImage = bellowImageLayer.getBufferedImage();
        int bX = bellowImageLayer.getTranslationX();
        int bY = bellowImageLayer.getTranslationY();
        BufferedImage activeImage = getBufferedImage();
        Graphics2D g = bellowImage.createGraphics();
        int x = aX - bX;
        int y = aY - bY;
        Composite comp = calculateComposite();
        g.setComposite(comp);
        g.drawImage(activeImage, x, y, null);
        g.dispose();
    }

    public void rotateLayer(int angleDegree) {
        int translationXAbs = - getTranslationX();
        int translationYAbs = - getTranslationY();
        int newTranslationXAbs = 0;
        int newTranslationYAbs = 0;
        BufferedImage img = getBufferedImage();

        int imageWidth = img.getWidth();
        int imageHeight = img.getHeight();

        int canvasWidth = comp.getCanvasWidth();
        int canvasHeight = comp.getCanvasHeight();


        if(angleDegree == 90) {
            newTranslationXAbs = imageHeight - translationYAbs - canvasHeight;
            newTranslationYAbs = translationXAbs;
        } else if(angleDegree == 270) {
            newTranslationXAbs = translationYAbs;
            newTranslationYAbs = imageWidth  - translationXAbs - canvasWidth;
        } else if(angleDegree == 180) {
            newTranslationXAbs = imageWidth - canvasWidth - translationXAbs;
            newTranslationYAbs = imageHeight - canvasHeight - translationYAbs;
        }

        int newImageWidth;
        int newImageHeight;

        if (angleDegree == 90 || angleDegree == 270) {
            newImageWidth = imageHeight;
            newImageHeight = imageWidth;
        } else {
            newImageWidth = imageWidth;
            newImageHeight = imageHeight;
        }

        // TODO for arbitrary  rotation create a rectangle, then rotate it with the same AffineTransform
        // something like this: http://forums.sun.com/thread.jspa?threadID=5362614

        BufferedImage dest = new BufferedImage(newImageWidth, newImageHeight, img.getType());

        Graphics2D g2 = dest.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        if (angleDegree == 90) {
            g2.translate(imageHeight, 0);
        } else if (angleDegree == 180) {
            g2.translate(imageWidth, imageHeight);
        } else if (angleDegree == 270) {
            g2.translate(0, imageWidth);
        }


        g2.rotate(Math.toRadians(angleDegree));
        g2.drawImage(img, 0, 0, imageWidth, imageHeight, null);
        g2.dispose();

        setTranslationX(-newTranslationXAbs);
        setTranslationY(-newTranslationYAbs);

        setBufferedImage(dest);
    }

    public BufferedImage paintLayer(Graphics2D g, boolean isFirstVisibleLayer, BufferedImage imageSoFar) {
        if (isFirstVisibleLayer) {  // the first visible layer is always painted with normal mode
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        } else {
            g.setComposite(calculateComposite());
        }

        if (tmpDrawingImage == null) {
            if(Tool.isShapesDrawing() && isActiveLayer()) {
                // we need to draw inside the layer, but only temporarily
                BufferedImage tmp = ImageUtils.copyImage(bufferedImage);
                Graphics2D tmpG = tmp.createGraphics();
                tmpG.drawImage(bufferedImage, 0, 0, null);

                // brush and shapes cannot be active together, therefore if is enough to call this only here!
                Tool.SHAPES.paintOverLayer(tmpG);
                tmpG.dispose();

                g.drawImage(tmp, getTranslationX(), getTranslationY(), null);
                tmp.flush();
            } else { // the simple case
                g.drawImage(bufferedImage, getTranslationX(), getTranslationY(), null);
            }
        } else { // we are in the middle of a brush draw
            if(tmpDrawingComposite == null) {
                throw new IllegalStateException("tmpDrawingComposite == null");
            }

            if (blendingMode == BlendingMode.NORMAL && opacity > 0.99f) {  // layer in normal mode, opacity  = 100%
                g.drawImage(bufferedImage, getTranslationX(), getTranslationY(), null);
                g.setComposite(tmpDrawingComposite);
                g.drawImage(tmpDrawingImage, getTranslationX(), getTranslationY(), null);
            } else { // layer is not in normal mode
                // first create a merged layer-brush image
                BufferedImage mergedLayerBrushImg = ImageUtils.copyImage(bufferedImage);
                Graphics2D mergedLayerBrushG = mergedLayerBrushImg.createGraphics();
                mergedLayerBrushG.setComposite(tmpDrawingComposite);
                mergedLayerBrushG.drawImage(tmpDrawingImage, 0, 0, null); // draw the brush on the layer
                mergedLayerBrushG.dispose();

                // now draw the merged layer-brush on the target Graphics with the layer composite
                g.drawImage(mergedLayerBrushImg, getTranslationX(), getTranslationY(), null);

                // TODO there are still problems because drawing with multiply mode
            }
        }
        return null;
    }


}
