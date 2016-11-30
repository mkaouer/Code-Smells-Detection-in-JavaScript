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
import pixelitor.ExceptionHandler;
import pixelitor.ImageChangeReason;
import pixelitor.filters.comp.Flip;
import pixelitor.history.History;
import pixelitor.history.ImageEdit;
import pixelitor.history.TranslateEdit;
import pixelitor.selection.Selection;
import pixelitor.tools.Tool;
import pixelitor.utils.ImageUtils;
import pixelitor.utils.debug.ImageLayerNode;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Represents an image layer.
 */
public class ImageLayer extends ContentLayer {
    private static final long serialVersionUID = 2L;

    private transient BufferedImage bufferedImage = null;

    private transient TmpDrawingLayer tmpDrawingLayer;

    // for dialog previews
    private transient BufferedImage backupForPreviewBufferedImage = null;

    /**
     * Creates a new layer with the given image
     */
    public ImageLayer(Composition comp, BufferedImage bufferedImage, String name) {
        super(comp, name == null ? comp.generateNewLayerName() : name);
        if (bufferedImage == null) {
            throw new IllegalArgumentException("bufferedImage is null");
        }

        setBufferedImage(bufferedImage, true);
    }

    /**
     * Creates a new layer with the given image and size. Used when an image is pasted into a layer
     */
    public ImageLayer(Composition comp, BufferedImage pastedImage, String name, int width, int height) {
        super(comp, name);
        if (pastedImage == null) {
            throw new IllegalArgumentException("bufferedImage is null");
        }

        int pastedWidth = pastedImage.getWidth();
        int pastedHeight = pastedImage.getHeight();

        boolean createNewImage = pastedWidth < width || pastedHeight < height;
        boolean addXTranslation = pastedWidth > width;
        boolean addYTranslation = pastedHeight > height;

        BufferedImage newImage = pastedImage;
        if (createNewImage) { // if the pasted image is too small, a new image is created
            int newWidth = Math.max(width, pastedWidth);
            int newHeight = Math.max(height, pastedHeight);
            newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB_PRE);
            Graphics2D g = newImage.createGraphics();

            int drawX = Math.max((width - pastedWidth) / 2, 0);
            int drawY = Math.max((height - pastedHeight) / 2, 0);

            g.drawImage(pastedImage, drawX, drawY, null);
            g.dispose();
        }

        // TODO called with the ignoreSelection=true, because otherwise setBufferedImage assumes that there is already
        // a bufferedImage for this layer
        // the right thing to do would be respecting the selection
        setBufferedImage(newImage, true);

        if (addXTranslation) {
            int newXTrans = -(pastedWidth - width) / 2;
            setTranslationX(newXTrans);
        }
        if (addYTranslation) {
            int newYTrans = -(pastedHeight - height) / 2;
            setTranslationY(newYTrans);
        }
    }


    /**
     * Creates a new empty layer
     */
    public ImageLayer(Composition comp, String name, int width, int height) {
        super(comp, name == null ? comp.generateNewLayerName() : name);

        setBufferedImage(new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE), true);
    }

    @Override
    public Layer duplicate() {
        BufferedImage imageCopy = ImageUtils.copyImage(bufferedImage);
        ImageLayer d = new ImageLayer(comp, imageCopy, getDuplicateLayerName());
        d.setOpacity(opacity, false, true);
        d.setTranslationX(translationX);
        d.setTranslationY(translationY);
        d.setBlendingMode(blendingMode, false, true);

        return d;
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public void setBufferedImage(BufferedImage newImage, boolean ignoreSelection) {
        if (newImage == null) {
            throw new IllegalArgumentException("newImage is null");
        }

        Selection selection = comp.getSelection();
        if (ignoreSelection || (selection == null)) {
            bufferedImage = newImage;
        } else {
            Shape selectionShape = selection.getShape();
            if (selectionShape != null) {
                Graphics2D g = bufferedImage.createGraphics();
                g.translate(-getTranslationX(), -getTranslationY());
                g.setComposite(AlphaComposite.Src);
                g.setClip(selectionShape);
//                if(drawIntoSelection) {
                Rectangle bounds = selectionShape.getBounds();
                g.drawImage(newImage, bounds.x, bounds.y, null);
//                } else {
//                    g.drawImage(newImage, 0, 0, null);
//                }
                g.dispose();
            } else {
                bufferedImage = newImage;
            }
        }

        comp.imageChanged(false, false);
    }

    /**
     * This method is called when a new dialog appears
     */
    public void startPreviewing() {
        this.backupForPreviewBufferedImage = getImageOrSubImageIfSelected(true, true);
    }

    /**
     * This method is called when a new adjustment is made in the dialog, before running the op
     */
    public void startNewPreviewFromDialog() {
        restoreOriginalFromPreviewBackup();
    }

    /**
     * This method is called when cancel was pressed in the preview dialog
     */
    public void cancelPreviewing() {
        restoreOriginalFromPreviewBackup();

        getComposition().getIC().repaint(); // TODO necessary?
    }

    private void restoreOriginalFromPreviewBackup() {
        if (backupForPreviewBufferedImage == null) {
            throw new IllegalStateException("backupForPreviewBufferedImage is null");
        }

        // restore the original
        setBufferedImage(this.backupForPreviewBufferedImage, false);
    }

    /**
     * Called then a filter or a filter-related undo changes the image
     */
    public void changeImage(BufferedImage img, ImageChangeReason changeReason, String opName) {
        BufferedImage imageForUndo = null;
        if (changeReason == ImageChangeReason.OP_WITH_PREVIEW_FINISHED) {
            if (img != backupForPreviewBufferedImage) {
                imageForUndo = backupForPreviewBufferedImage;
            }
        } else if (changeReason.makeUndoBackup()) {
            if (img != bufferedImage) {
                // if there is a selection it is important to make a copy because in this case
                // setBufferedImage does not change the bufferedImage reference, and the new image
                // would be saved for undo

                imageForUndo = getImageOrSubImageIfSelected(false, true);
            }
        }

        if (changeReason.setNewImage()) {
            if (img == null) {
                throw new IllegalArgumentException("trying to update with null image");
            }
            setBufferedImage(img, false);
        } else {
            // this is a special case, the image is already set,
            // the img parameter is null in this case
        }

        if (imageForUndo == bufferedImage) {
            throw new IllegalStateException("imageForUndo == bufferedImage");
        }

        if ((changeReason.makeUndoBackup()) && (imageForUndo != null)) {
            ImageEdit edit = new ImageEdit(opName, comp, imageForUndo, true);
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
        return needsEnlarging;
    }

    void enlargeLayer() {
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
            ExceptionHandler.showOutOfMemoryDialog();
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

        setBufferedImage(bi, true);
    }

    private void enlargeNE() {
        BufferedImage bi = new BufferedImage(
                bufferedImage.getWidth() - translationX,
                bufferedImage.getHeight() + translationY,
                bufferedImage.getType());
        Graphics2D g = bi.createGraphics();
        g.drawImage(bufferedImage, 0, translationY, null);
        g.dispose();
        setBufferedImage(bi, true);
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
        setBufferedImage(bi, true);
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
        setBufferedImage(bi, true);
        translationX = 0;
        translationY = 0;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        ImageUtils.serializeImage(out, bufferedImage);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        setBufferedImage(ImageUtils.deserializeImage(in), true);
    }

    /**
     * Should be called when this layer is not needed anymore
     */
    // TODO when to call it? at DeleteLayerEdit.die?
    public void dispose() {
        bufferedImage.flush();
        bufferedImage = null;
        if (tmpDrawingLayer != null) {
            tmpDrawingLayer.dispose();
        }
    }

    /**
     * Returns the image shown in the image selector in filter dialogs
     */
    public BufferedImage getImageForFilterDialogs() {
        Selection selection = comp.getSelection();
        if (selection == null) {
            return bufferedImage;
        }

        Rectangle selectionBounds = selection.getShapeBounds();
        return bufferedImage.getSubimage(selectionBounds.x, selectionBounds.y, selectionBounds.width, selectionBounds.height);
    }

    @Override
    public boolean notTranslated() {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        int canvasWidth = comp.getCanvasWidth();
        int canvasHeight = comp.getCanvasHeight();

        return (width == canvasWidth) && (height == canvasHeight);
    }

    @Override
    public void flip(Flip.Direction direction) {
        int translationXAbs = -getTranslationX();
        int translationYAbs = -getTranslationY();
        int newTranslationXAbs;
        int newTranslationYAbs;

        BufferedImage src = getImageOrSubImageIfSelected(false, false);
        boolean selected = comp.hasSelection();

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

        if (!selected) {
            setTranslationX(-newTranslationXAbs);
            setTranslationY(-newTranslationYAbs);
        }

        setBufferedImage(dest, false);
    }

    @Override
    public void rotate(int angleDegree) {
        int translationXAbs = -getTranslationX();
        int translationYAbs = -getTranslationY();
        int newTranslationXAbs = 0;
        int newTranslationYAbs = 0;

        BufferedImage img = getImageOrSubImageIfSelected(false, false);
        boolean selected = comp.hasSelection();

        int imageWidth = img.getWidth();
        int imageHeight = img.getHeight();

        int canvasWidth = comp.getCanvasWidth();
        int canvasHeight = comp.getCanvasHeight();


        if (angleDegree == 90) {
            newTranslationXAbs = imageHeight - translationYAbs - canvasHeight;
            newTranslationYAbs = translationXAbs;
        } else if (angleDegree == 270) {
            newTranslationXAbs = translationYAbs;
            newTranslationYAbs = imageWidth - translationXAbs - canvasWidth;
        } else if (angleDegree == 180) {
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

        if (!selected) {
            setTranslationX(-newTranslationXAbs);
            setTranslationY(-newTranslationYAbs);
        }
        setBufferedImage(dest, false);
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
        Composite composite = blendingMode.getComposite(opacity);
        g.setComposite(composite);
        g.drawImage(activeImage, x, y, null);
        g.dispose();
    }

    @Override
    public BufferedImage paintLayer(Graphics2D g, boolean isFirstVisibleLayer, BufferedImage imageSoFar) {
        if (isFirstVisibleLayer) {  // the first visible layer is always painted with normal mode
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        } else {
            Composite composite = blendingMode.getComposite(opacity);
            g.setComposite(composite);
        }

        if (tmpDrawingLayer == null) {
            if (Tool.isShapesDrawing() && isActiveLayer()) {
                // we need to draw inside the layer, but only temporarily
                BufferedImage tmp = createCompositionSizedTmpImage();
                Graphics2D tmpG = tmp.createGraphics();
                tmpG.drawImage(bufferedImage, getTranslationX(), getTranslationY(), null);

                // brush and shapes cannot be active together, therefore if is enough to call this only here!
                comp.setSelectionClipping(tmpG, null);
                Tool.SHAPES.paintOverLayer(tmpG);
                tmpG.dispose();

                g.drawImage(tmp, 0, 0, null);
                tmp.flush();
            } else { // the simple case
                g.drawImage(bufferedImage, getTranslationX(), getTranslationY(), null);
            }
        } else { // we are in the middle of a brush draw

            if (blendingMode == BlendingMode.NORMAL && opacity > 0.999f) {  // layer in normal mode, opacity  = 100%
                g.drawImage(bufferedImage, getTranslationX(), getTranslationY(), null);
                tmpDrawingLayer.paintLayer(g, 0, 0);
            } else { // layer is not in normal mode
                // first create a merged layer-brush image
                BufferedImage mergedLayerBrushImg = ImageUtils.copyImage(bufferedImage); // TODO a canvas-sized image is enough and then less translating is necessary
                Graphics2D mergedLayerBrushG = mergedLayerBrushImg.createGraphics();

                tmpDrawingLayer.paintLayer(mergedLayerBrushG, -getTranslationX(), -getTranslationY()); // draw the brush on the layer
                mergedLayerBrushG.dispose();

                // now draw the merged layer-brush on the target Graphics with the layer composite
                g.drawImage(mergedLayerBrushImg, getTranslationX(), getTranslationY(), null);
            }
        }
        return null;
    }


    public TmpDrawingLayer createTmpDrawingLayer(Composite c, boolean respectSelection) {
        tmpDrawingLayer = new TmpDrawingLayer(this, c, respectSelection);
        return tmpDrawingLayer;
    }

    public void mergeTmpDrawingImageDown() {
        if (tmpDrawingLayer == null) {
            return;
        }
        Graphics2D g = bufferedImage.createGraphics();

        tmpDrawingLayer.paintLayer(g, -getTranslationX(), -getTranslationY());
        g.dispose();

        tmpDrawingLayer.dispose();
        tmpDrawingLayer = null;
    }

    public BufferedImage createCompositionSizedTmpImage() {
        return new BufferedImage(comp.getCanvasWidth(), comp.getCanvasHeight(), bufferedImage.getType());
    }

    public BufferedImage createCompositionSizedSubImage() {
        return bufferedImage.getSubimage(-getTranslationX(), -getTranslationY(), comp.getCanvasWidth(), comp.getCanvasHeight());
    }

    public BufferedImage getImageOrSubImageIfSelected(boolean copyIfFull, boolean copyAndTranslateIfSelected) {
        Selection selection = comp.getSelection();
        if (selection == null) {
            if (copyIfFull) {
                return ImageUtils.copyImage(bufferedImage);
            }
            return bufferedImage;
        }

        return getSelectionSizedPartFrom(bufferedImage, selection, copyAndTranslateIfSelected);
    }

    public BufferedImage getSelectionSizedPartFrom(BufferedImage src, Selection selection, boolean copyAndTranslateIfSelected) {
        Rectangle bounds = selection.getShapeBounds(); // relative to the composition

        if (bounds == null) { // TODO happened during robot tests
            if (copyAndTranslateIfSelected) {
                return ImageUtils.copyImage(src);
            } else {
                return src;
            }
        }

        bounds.translate(-getTranslationX(), -getTranslationY()); // relative to the image
        Rectangle imageBounds = new Rectangle(0, 0, src.getWidth(), src.getHeight());
        bounds = bounds.intersection(imageBounds);

        if (bounds.isEmpty()) {
            if (copyAndTranslateIfSelected) {
                return ImageUtils.copyImage(src);
            } else {
                return src;
            }
        }

        if (copyAndTranslateIfSelected) {
            return ImageUtils.copyAndTranslateSubimage(src, bounds);
        } else {
            BufferedImage retVal = src.getSubimage(bounds.x, bounds.y, bounds.width, bounds.height);
            return retVal;
        }
    }

    public void cropToCanvasSize() {
        int imageWidth = bufferedImage.getWidth();
        int imageHeight = bufferedImage.getHeight();
        int canvasWidth = comp.getCanvasWidth();
        int canvasHeight = comp.getCanvasHeight();

        if ((imageWidth > canvasWidth) || (imageHeight > canvasHeight)) {
            BufferedImage newImage = ImageUtils.crop(bufferedImage, -getTranslationX(), -getTranslationY(), canvasWidth, canvasHeight);

            BufferedImage tmp = bufferedImage;
            setBufferedImage(newImage, true);
            tmp.flush();

            setTranslationX(0);
            setTranslationY(0);
        }
    }

    @Override
    public void enlargeCanvas(int north, int east, int south, int west) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        int canvasWidth = comp.getCanvasWidth();
        int canvasHeight = comp.getCanvasHeight();

        // TODO the layer can already be larger than the canvas

        int newImageWidth = width + east + west;
        int newImageHeight = height + north + south;
        int newImagePaintX = west;
        int newImagePaintY = north;
        int newTranslationX = getTranslationX();
        int newTranslationY = getTranslationY();

        BufferedImage newImage = new BufferedImage(newImageWidth, newImageHeight, bufferedImage.getType());
        Graphics2D g = newImage.createGraphics();
        g.drawImage(bufferedImage, newImagePaintX, newImagePaintY, null);
        g.dispose();

        bufferedImage.flush();
        bufferedImage = newImage;

        setTranslationX(newTranslationX);
        setTranslationY(newTranslationY);
    }

    @Override
    TranslateEdit createTranslateEdit(int oldTranslationX, int oldTranslationY) {
        TranslateEdit edit = null;
        boolean needsEnlarging = checkForLayerEnlargement();
        if (needsEnlarging) {
            edit = new TranslateEdit(this, getBufferedImage(), oldTranslationX, oldTranslationY);
        } else {
            edit = new TranslateEdit(this, null, oldTranslationX, oldTranslationY);
        }

        if (needsEnlarging) {
            enlargeLayer();
        }

        return edit;
    }
}
