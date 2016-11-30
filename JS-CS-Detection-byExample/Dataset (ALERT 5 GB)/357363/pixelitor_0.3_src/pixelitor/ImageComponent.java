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

package pixelitor;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;
import java.util.*;

import javax.swing.JComponent;

import pixelitor.tools.Tool;
import pixelitor.utils.MarchingAntsSelection;
import pixelitor.utils.GUIUtils;
import pixelitor.utils.Utils;
import pixelitor.layers.LayersPanel;
import pixelitor.layers.Layer;
import pixelitor.layers.LayerButton;
import pixelitor.filters.Operation;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.painter.CheckerboardPainter;

public class ImageComponent extends JComponent implements MouseListener, MouseMotionListener {
    private boolean dirty = false;
    private String name;
    private InternalImageFrame internalFrame = null;

    private static Color BG_GRAY = new Color(200, 200, 200);
    private Painter checkerBoardPainter = new CheckerboardPainter(BG_GRAY, Color.WHITE);

    private java.util.List<Layer> layerList = new ArrayList<Layer>();
    Layer activeLayer;
    private LayersPanel layersPanel;
    private int newLayerCount = 1;

    public ImageComponent(String name) {
        this.name = name;
        addMouseListener(this);
        addMouseMotionListener(this);

        layersPanel = new LayersPanel();
    }

    public void changeActiveLayerImage(BufferedImage img, ImageChangeReason changeReason) {
        if (changeReason.makeBackup()) {
            History.setBackup(activeLayer.getBufferedImage(), this, changeReason);
        }

        if (changeReason.setNewImage()) {
            if (img == null) {
                throw new IllegalArgumentException("trying to update with null image");
            }
            activeLayer.setBufferedImage(img);
        } else {
            // this is a special case, the image is already set,
            // the img parameter is null in this case
        }

        // notify the listeners
        AppLogic.imageContentChanged(this, changeReason);
        updateState(img, changeReason);
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
        int x = selection.getX();
        int y = selection.getY();
        int width = selection.getWidth();
        int height = selection.getHeight();

        int nrLayers = getNrLayers();
        for (int i = 0; i < nrLayers; i++) {
            Layer layer = layerList.get(i);
            BufferedImage img = layer.getBufferedImage();
            BufferedImage src = img;
            BufferedImage dest = GUIUtils.crop(src, x, y, width, height);
            layer.setBufferedImage(dest);
        }
        updateState(layerList.get(0).getBufferedImage(), ImageChangeReason.CROP);
        Tool.CROP_SELECTION.deselect();
        setPreferredSize(new Dimension(width, height));
        revalidate();
    }

    public void addImageToNewLayer(BufferedImage img, ImageChangeReason changeReason) {
        updateState(img, changeReason);
        addLayer(img, changeReason);
    }

    private void updateState(BufferedImage img, ImageChangeReason changeReason) {
        if (changeReason.sizeChanged()) {
            revalidate();
            if (internalFrame != null) {
                internalFrame.setImageSizeHasChanged(img.getWidth(), img.getHeight());
            }
        }

        if (changeReason.makeBackup()) {
            setDirty(true);
        }
        if (changeReason.setNewImage()) {
            repaint();
        }
    }


    public void addNewEmptyLayer() {
        addLayer(null, ImageChangeReason.NEW_EMPTY_LAYER);
    }


    private void addLayer(BufferedImage img, ImageChangeReason changeReason) {
        String name = "layer " + newLayerCount;
        Layer newLayer = new Layer(this, img, name, getWidth(), getHeight());
        layerList.add(newLayer);
        newLayerCount++;

        LayerButton layerButton = new LayerButton(newLayer);
        newLayer.setLayerButton(layerButton);

        layersPanel.addLayerButton(layerButton);
        setActiveLayer(newLayer);

        // notify the listeners
        AppLogic.imageContentChanged(this, changeReason);
        AppLogic.layerCountChanged(layerList.size());
    }

    public void setActiveLayer(Layer newActiveLayer) {
        if (activeLayer != newActiveLayer) {
            this.activeLayer = newActiveLayer;
            newActiveLayer.getLayerButton().setSelected(true);
            AppLogic.activeLayerChanged(newActiveLayer);
        }
    }


    public BufferedImage getImageForActiveLayer() {
        BufferedImage img = activeLayer.getBufferedImage();
        return img;
    }


    public BufferedImage getCompositeImage() {
        if (noLayers()) {
            return null;
        }

        // find the first bisible layer
        int nrLayers = layerList.size();
        int index = 0;
        BufferedImage baseImage = null;
        for (; index < nrLayers; index++) {
            Layer next = layerList.get(index);
            if (next.isVisible()) {
                baseImage = next.getBufferedImage();
                break;
            }
        }
        if (baseImage == null) { // all visible layers were empty
            return null;
        }

//        System.out.println("Layers.getCompositeImage base image at index = " + index);

        // composite the rest of the images into the base image
        BufferedImage result = null;
        Graphics2D g = null;
        index++;
        for (; index < nrLayers; index++) {
            Layer next = layerList.get(index);
//            System.out.println("Layers.getCompositeImage now processing index = " + index + ", layer = " + next);
            if (next.isVisible()) {
                BufferedImage img = next.getBufferedImage();
                if (img != null) {
                    // have to make a copy because we cannot draw into the base layer
                    if (result == null) {
                        result = Utils.copyImage(baseImage);
                        g = result.createGraphics();
                    }

                    g.setComposite(next.getComposite());
                    g.drawImage(img, 0, 0, null);
                }
            }
        }
        if (g != null) {
            g.dispose();
        }

        if (result != null) {
            return result;
        } else { // copy was not necessary
            return baseImage;
        }
    }

    public boolean noLayers() {
        return layerList.isEmpty();
    }


    @Override
    public Dimension getPreferredSize() {
        if (noLayers()) {
            return super.getPreferredSize();
        } else {
            int w = layerList.get(0).getBufferedImage().getWidth();
            int h = layerList.get(0).getBufferedImage().getHeight();

            return new Dimension(w, h);
        }
    }

    public int getImageWidth() {
        return layerList.get(0).getBufferedImage().getWidth();
    }

    public int getImageHeight() {
        return layerList.get(0).getBufferedImage().getHeight();
    }

    // called when a new dialog apperas
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
//        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Tool.getCurrentTool().mouseReleased(e, this);
//        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Tool.getCurrentTool().mouseDragged(e, this);
//        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Tool.getCurrentTool().mouseMoved(e, this);
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
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\n    name = ").append(name);
        sb.append("\n    dirty = ").append(dirty);
        sb.append("\n    index of active layer = ").append(layerList.indexOf(activeLayer));

        for (int i = 0; i < layerList.size(); i++) {
            Layer layer = layerList.get(i);
            sb.append("\n    layer ").append(i).append(": ").append(layer.toString());
        }

        sb.append("\n}");

        return sb.toString();
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

    public void paint(Graphics g) {
//        System.out.println(g.getClipBounds());
//        super.paint(g);
        paintComponent(g);
    }

    @Override
    public void paintComponent(Graphics g) {
        checkerBoardPainter.paint((Graphics2D) g, this, getImageWidth(), getImageHeight());

        if (!layerList.isEmpty()) {
            BufferedImage compositeImage = getCompositeImage();
            Rectangle clipBounds = g.getClipBounds();

            // g.drawImage(compositeImage, 0, 0, this);

            // without this optimalization the drawing on large images would be very slow
            int clipX = (int) clipBounds.getX();
            int clipY = (int) clipBounds.getY();
            int clipWidth = (int) clipBounds.getWidth();
            int clipHeight = (int) clipBounds.getHeight();
            int clipX2 = clipX + clipWidth;
            int clipY2 = clipY + clipHeight;
            g.drawImage(compositeImage, clipX, clipY, clipX2, clipY2, clipX, clipY, clipX2, clipY2, this);
        }

        Tool.getCurrentTool().paintOnImage((Graphics2D) g);
    }

    public void onActivation() {
        try {
            getInternalFrame().setSelected(true);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        PixelitorWindow.getInstance().showLayersPanel(layersPanel);
    }


    public int getNrLayers() {
        return layerList.size();
    }

    //    public BufferedImage createDrawingTmpLayer() {
//        if(drawingTmpLayer == null) {
//            drawingTmpLayer = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getType());
//        } else {
//            throw new IllegalStateException("tmp layer already created");
//        }
//        return drawingTmpLayer;
//    }
//
//    public void flattenDrawingTmpLayer(AlphaComposite composite) {
//        if(drawingTmpLayer == null) {
//            throw new IllegalStateException("drawingTmpLayer == null");
//        }
//        Graphics2D g = bufferedImage.createGraphics();
//
//        g.setComposite(composite);
//        g.drawImage(drawingTmpLayer, 0, 0, this);
//    }

    public void flattenImage() {
        BufferedImage bi = getCompositeImage();
        Layer firstLayer = layerList.get(0);
        firstLayer.setBufferedImage(bi);
        setActiveLayer(firstLayer);

        int nrLayers = getNrLayers();
        for (int i = nrLayers - 1; i > 0; i--) {
            deleteLayer(i);
        }
    }

    private void deleteLayer(int layerIndex) {
        Layer layerToBeDeleted = layerList.get(layerIndex);

        LayerButton button = layerToBeDeleted.getLayerButton();
        layerList.remove(layerToBeDeleted);

        if (layerToBeDeleted == activeLayer) {
            if (layerIndex > 0) {
                setActiveLayer(layerList.get(layerIndex - 1));
            }
        }
        layersPanel.deleteLayerButton(button);
        repaint();
    }

    public void deleteActiveLayer() {
        int indexOfActiveLayer = layerList.indexOf(activeLayer);
        deleteLayer(indexOfActiveLayer);
    }

    public void duplicateLayer() {
        BufferedImage bi = activeLayer.getBufferedImage();

//        addImageToNewLayer(bi, ImageChangeReason.NEW_EMPTY_LAYER);
        // TODO real change reason
        // TODO don't add  at the top
        addImageToNewLayer(bi, ImageChangeReason.NEW_EMPTY_LAYER);
    }


    public Layer getActiveLayer() {
        return activeLayer;
    }

    public String getLayersDebugInfo() {
        if(layerList.size() == 0) {
            return "no layers";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("image name: ").append(name);
        sb.append("\ndirty: ").append(dirty);
        sb.append("\nnr of layers: ").append(layerList.size());
        sb.append("\nindex of active layer: ").append(layerList.indexOf(activeLayer));

        for (int i = 0; i < layerList.size(); i++) {
            Layer layer = layerList.get(i);
            sb.append("\nlayer ").append(i).append(":").append(layer.getLayerDebugInfo());
        }



        return sb.toString();
    }
}
