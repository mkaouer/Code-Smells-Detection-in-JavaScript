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

package pixelitor;

import org.jdesktop.swingx.painter.CheckerboardPainter;
import org.jdesktop.swingx.painter.Painter;
import pixelitor.layers.BlendingMode;
import pixelitor.layers.Layer;
import pixelitor.layers.LayerButton;
import pixelitor.layers.LayersPanel;
import pixelitor.operations.Operation;
import pixelitor.tools.Tool;
import pixelitor.utils.GUIUtils;
import pixelitor.utils.ImageUtils;
import pixelitor.utils.MarchingAntsSelection;
import pixelitor.utils.debug.ImageComponentNode;

import javax.swing.*;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;
import java.util.ArrayList;


public class ImageComponent extends JComponent implements MouseListener, MouseMotionListener {
    private int newLayerCount = 1;

    private boolean dirty = false;
    private String name;
    private InternalImageFrame internalFrame = null;

    private static Color BG_GRAY = new Color(200, 200, 200);
    private static Painter checkerBoardPainter = new CheckerboardPainter(BG_GRAY, Color.WHITE);

    private java.util.List<Layer> layerList = new ArrayList<Layer>();
    private Layer activeLayer;
    private LayersPanel layersPanel;

    private int canvasWidth;
    private int canvasHeight;

    private boolean compositeImageUpToDate = false;
    private BufferedImage compositeImage = null;

    public ImageComponent(String name, BufferedImage baseLayerImage) {
        this.name = name;
        addMouseListener(this);
        addMouseMotionListener(this);

        layersPanel = new LayersPanel();
        addImageToNewLayer(baseLayerImage, ImageChangeReason.FIRST_TIME_INIT);
        canvasWidth = baseLayerImage.getWidth();
        canvasHeight = baseLayerImage.getHeight();
    }

    // layer manipulation methods

    public void addNewEmptyLayer() {
        Layer newLayer = new Layer(this, canvasWidth, canvasHeight);
        addLayer(newLayer, ImageChangeReason.NEW_EMPTY_LAYER);
    }

    public void setActiveLayer(Layer newActiveLayer) {
        if (activeLayer != newActiveLayer) {
            this.activeLayer = newActiveLayer;
            newActiveLayer.getLayerButton().setSelected(true);
            AppLogic.activeLayerChanged(newActiveLayer);
        }
    }

    public int getNrLayers() {
        return layerList.size();
    }

    private void addLayer(Layer newLayer, ImageChangeReason changeReason) {
        int activeLayerIndex = layerList.indexOf(activeLayer);

        int newLayerIndex = activeLayerIndex + 1;
        layerList.add(newLayerIndex, newLayer);

        LayerButton layerButton = new LayerButton(newLayer);
        newLayer.setLayerButton(layerButton);

        layersPanel.addLayerButton(layerButton, newLayerIndex);
        setActiveLayer(newLayer);

        // notify the listeners
        AppLogic.imageContentChanged(this, changeReason);
        AppLogic.layerCountChanged(layerList.size());
    }

    private void deleteLayer(int layerIndex) {
        Layer layerToBeDeleted = layerList.get(layerIndex);

        LayerButton button = layerToBeDeleted.getLayerButton();
        layerList.remove(layerToBeDeleted);

        if (layerToBeDeleted == activeLayer) {
            if (layerIndex > 0) {
                setActiveLayer(layerList.get(layerIndex - 1));
            } else {  // deleted the fist layer, set the new first layer as active
                setActiveLayer(layerList.get(0));
            }
        }
        layersPanel.deleteLayerButton(button);

        AppLogic.layerCountChanged(layerList.size());
        imageChanged();
    }

    public void deleteActiveLayer() {
        int indexOfActiveLayer = layerList.indexOf(activeLayer);
        deleteLayer(indexOfActiveLayer);
    }

    public void duplicateLayer() {
        Layer duplicate = activeLayer.duplicate();
        addLayer(duplicate, ImageChangeReason.NEW_EMPTY_LAYER); // TODO real change reason
    }

    public Layer getActiveLayer() {
        return activeLayer;
    }

    // TODO the param is always false

    public void setCompositeImageUpToDate(boolean compositeImageUpToDate) {
        this.compositeImageUpToDate = compositeImageUpToDate;
    }

    public void changeActiveLayerImage(BufferedImage img, ImageChangeReason changeReason) {
        activeLayer.changeImage(img, changeReason);

        // notify the listeners
        AppLogic.imageContentChanged(this, changeReason);
        updateState(img, changeReason);
//
        // post condition: the backup should never be identical to the active image
        if (activeLayer.getBufferedImage() == History.getBackupBufferedImage()) {
            throw new IllegalStateException("backup BufferedImage is identical to the active one");
        }
    }

    public void runOpForAllLayers(Operation op, ImageChangeReason changeReason) {
        int nrLayers = getNrLayers();
        for (int i = 0; i < nrLayers; i++) {
            Layer layer = layerList.get(i);
            BufferedImage img = layer.getBufferedImage();
            BufferedImage src = img;
            BufferedImage dest = op.executeForOneLayer(changeReason, src);
            layer.setBufferedImage(dest);
        }
        updateState(layerList.get(0).getBufferedImage(), changeReason);
    }

    public void cropImage(MarchingAntsSelection selection) {
        Rectangle selectionBounds = selection.getSelectionShape().getBounds();

        int nrLayers = getNrLayers();
        for (int i = 0; i < nrLayers; i++) {
            Layer layer = layerList.get(i);
            BufferedImage img = layer.getBufferedImage();
            BufferedImage src = img;
            int cropX = selectionBounds.x - layer.getTranslationX();
            int cropY = selectionBounds.y - layer.getTranslationY();
            int cropWidth = selectionBounds.width;
            int cropHeight = selectionBounds.height;
            BufferedImage dest = GUIUtils.crop(src, cropX, cropY, cropWidth, cropHeight);
            layer.setBufferedImage(dest);
            layer.setTranslationX(0);
            layer.setTranslationY(0);
        }
        updateState(layerList.get(0).getBufferedImage(), ImageChangeReason.CROP);
        Tool.CROP_SELECTION.deselect(this);
        setPreferredSize(new Dimension(selectionBounds.width, selectionBounds.height));
        revalidate();
    }

    private void addImageToNewLayer(BufferedImage img, ImageChangeReason changeReason) {
        updateState(img, changeReason);

        Layer newLayer = new Layer(this, img, null);
        addLayer(newLayer, changeReason);
    }

    public BufferedImage getImageForActiveLayer() {
        BufferedImage img = activeLayer.getBufferedImage();
        return img;
    }

    @Override
    public Dimension getPreferredSize() {
        if (layerList.isEmpty()) {
            return super.getPreferredSize();
        } else {
            return new Dimension(canvasWidth, canvasHeight);
        }
    }

    public int getCanvasWidth() {
        return canvasWidth;
    }

    public int getCanvasHeight() {
        return canvasHeight;
    }

    // called when a new dialog appears

    public void startPreviewing() {
        activeLayer.startPreviewing();
    }

    // called when a new adjustment is made in the dialog, before running the op

    public void startNewPreviewFromDialog() {
        activeLayer.startNewPreviewFromDialog();
    }

    // cancel was pressed in the preview dialog

    public void cancelPreviewing() {
        activeLayer.cancelPreviewing();
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Tool.getCurrentTool().mouseClicked(e, this);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        Tool.getCurrentTool().mouseEntered(e, this);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        Tool.getCurrentTool().mouseExited(e, this);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Tool.getCurrentTool().mousePressed(e, this);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Tool.getCurrentTool().mouseReleased(e, this);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Tool.getCurrentTool().mouseDragged(e, this);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
//        mouseMoved is never used in the tools
//        Tool.getCurrentTool().mouseMoved(e, this);
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        ImageComponentNode node = new ImageComponentNode("ImageComponent", this);
        return node.toDetailedString();
    }

    public void setInternalFrame(InternalImageFrame internalFrame) {
        this.internalFrame = internalFrame;
    }

    public InternalImageFrame getInternalFrame() {
        return internalFrame;
    }

    public void closeContainer() {
        if (internalFrame != null) {
            internalFrame.dispose();
        }
    }

    public void onActivation() {
        try {
            getInternalFrame().setSelected(true);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        PixelitorWindow.getInstance().showLayersPanel(layersPanel);
    }

    public void moveImageRelative(int x, int y) {
        activeLayer.moveImageRelative(x, y);
    }

    public void startTranslation() {
        activeLayer.startTranslation();
    }

    public void endTranslation() {
        activeLayer.endTranslation();
    }

    public Layer getLayer(int i) {
        return layerList.get(i);
    }

    public String generateNewLayerName() {
        String retVal = "layer " + newLayerCount;
        newLayerCount++;
        return retVal;
    }


    // without this the drawing on large images would be very slow

    private void drawImageWithClipping(Graphics g, BufferedImage img) {
        Rectangle clipBounds = g.getClipBounds();
        int clipX = (int) clipBounds.getX();
        int clipY = (int) clipBounds.getY();
        int clipWidth = (int) clipBounds.getWidth();
        int clipHeight = (int) clipBounds.getHeight();
        int clipX2 = clipX + clipWidth;
        int clipY2 = clipY + clipHeight;
        g.drawImage(img, clipX, clipY, clipX2, clipY2, clipX, clipY, clipX2, clipY2, this);
    }

    /**
     * Paints all the layers into the given Graphics2D object
     */
    private void paintLayers(Graphics2D g) {
        boolean firstVisibleLayer = true;
        for (Layer layer : layerList) {
            if (layer.isVisible()) {
                layer.drawLayerOnGraphics(g, firstVisibleLayer);
                firstVisibleLayer = false;
            }
        }
    }

    public void flattenImage() {
        BufferedImage bi = getCompositeImage();
        Layer firstLayer = layerList.get(0);
        firstLayer.setBufferedImage(bi);
        setActiveLayer(firstLayer);

        int nrLayers = getNrLayers();
        for (int i = nrLayers - 1; i > 0; i--) {
            deleteLayer(i);
        }
        AppLogic.layerCountChanged(1);
    }


    public void mergeDown() {
        int activeIndex = layerList.indexOf(activeLayer);
        if (activeIndex > 0) {
            if (activeLayer.isVisible()) {
                Layer bellow = layerList.get(activeIndex - 1);
                if (bellow.isVisible()) {
                    int aX = activeLayer.getTranslationX();
                    int aY = activeLayer.getTranslationY();
                    BufferedImage bellowImage = bellow.getBufferedImage();
                    int bX = bellow.getTranslationX();
                    int bY = bellow.getTranslationY();
                    BufferedImage activeImage = activeLayer.getBufferedImage();
                    Graphics2D g = bellowImage.createGraphics();
                    int x = aX - bX;
                    int y = aY - bY;
                    Composite comp = activeLayer.calculateComposite();
                    System.out.println("ImageComponent.mergeDown comp = " + comp.toString());
                    g.setComposite(comp);
                    g.drawImage(activeImage, x, y, null);
                    g.dispose();
                    deleteActiveLayer();
                }
            }
        }
    }


    private void updateState(BufferedImage img, ImageChangeReason changeReason) {
        setCompositeImageUpToDate(false);
        if (changeReason.sizeChanged()) {
            // TODO: is this always true?
            canvasWidth = img.getWidth();
            canvasHeight = img.getHeight();

            revalidate();
            if (internalFrame != null) {
                internalFrame.setImageSizeHasChanged(img.getWidth(), img.getHeight());
            }
        }

        if (changeReason.makeBackup()) {
            setDirty(true);
        }
        if (changeReason.setNewImage()) {
            imageChanged();
        }
    }

    public void moveActiveLayerUp() {
        int oldIndex = layerList.indexOf(activeLayer);
        swapLayers(oldIndex, oldIndex + 1);
    }

    public void moveActiveLayerDown() {
        int oldIndex = layerList.indexOf(activeLayer);
        swapLayers(oldIndex, oldIndex - 1);
    }

    public void moveActiveLayerToTop() {
        int oldIndex = layerList.indexOf(activeLayer);
        swapLayers(oldIndex, layerList.size() - 1);
    }


    public void moveActiveLayerToBottom() {
        int oldIndex = layerList.indexOf(activeLayer);
        swapLayers(oldIndex, 0);
    }

    private void swapLayers(int oldIndex, int newIndex) {
        if (newIndex < 0) {
            return;
        }
        if (newIndex >= layerList.size()) {
            return;
        }
        if (oldIndex == newIndex) {
            return;
        }
        Layer layer = layerList.get(oldIndex);
        layerList.remove(layer);
        layerList.add(newIndex, layer);
        layersPanel.changeLayerIndex(oldIndex, newIndex);
    }


    public void moveLayerSelectionUp() {
        int oldIndex = layerList.indexOf(activeLayer);
        if (oldIndex + 1 >= layerList.size()) {
            return;
        }
        setActiveLayer(layerList.get(oldIndex + 1));
    }


    public void moveLayerSelectionDown() {
        int oldIndex = layerList.indexOf(activeLayer);
        if (oldIndex - 1 < 0) {
            return;
        }

        setActiveLayer(layerList.get(oldIndex - 1));
    }

    public void updateRegion(int startX, int startY, int endX, int endY, int thickness) {
        if (endX < startX) {
            int tmp = startX;
            startX = endX;
            endX = tmp;
        }
        if (endY < startY) {
            int tmp = startY;
            startY = endY;
            endY = tmp;
        }
        startX -= thickness;
        endX += thickness;
        startY -= thickness;
        endY += thickness;

        // repaint is also good
        paintImmediately(startX, startY, endX - startX, endY - startY);
    }

    public BufferedImage calculateCompositeImage() {
        BufferedImage baseImage = layerList.get(0).getBufferedImage();
        if (layerList.size() == 1) {
            return baseImage;
        }

        BufferedImage img = new BufferedImage(canvasWidth, canvasHeight, baseImage.getType());
        Graphics2D g = img.createGraphics();
        paintLayers(g);
        g.dispose();

        return img;
    }

    @Override
    public void paint(Graphics g) {
        paintComponent(g);
    }

    private boolean hasNonNormalBlending() {
        for (Layer layer : layerList) {
            if (layer.hasNonNormalBlending()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        checkerBoardPainter.paint(g2, this, canvasWidth, canvasHeight);

        boolean workaround = hasNonNormalBlending();
        // TODO: why is it is not possible to paint with non-normal blending modes on the Graphics object received by paintComponent?

        if (workaround) {
            getGraphicsConfiguration();
            BufferedImage tmp = ImageUtils.createCompatibleImage(canvasWidth, canvasHeight);
            Graphics2D tmpG2D = tmp.createGraphics();
            tmpG2D.setClip(g.getClipBounds());
            paintLayers(tmpG2D);
            tmpG2D.dispose();

            // g2.drawImage(tmp, 0, 0, null);
            drawImageWithClipping(g2, tmp);
        } else {
            paintLayers(g2);
        }

        Tool.getCurrentTool().paintOnImage(g2);
    }

    public void imageChanged() {
        compositeImageUpToDate = false;
        repaint(0, 0, canvasWidth, canvasHeight);        
    }

    @Override
    public void repaint() {
        repaint(0, 0, canvasWidth, canvasHeight);
    }

    public BufferedImage getCompositeImage() {

        if (compositeImageUpToDate) {
            return compositeImage;
        } else {
            compositeImage = calculateCompositeImage();
            compositeImageUpToDate = true;
        }
        return compositeImage;
    }
}
