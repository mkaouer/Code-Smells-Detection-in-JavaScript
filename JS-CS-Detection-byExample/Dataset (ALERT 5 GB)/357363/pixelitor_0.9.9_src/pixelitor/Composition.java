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
package pixelitor;

import pixelitor.history.DeleteLayerEdit;
import pixelitor.history.History;
import pixelitor.history.LayerOrderChangeEdit;
import pixelitor.history.NewLayerEdit;
import pixelitor.history.NotUndoableEdit;
import pixelitor.io.OpenSaveManager;
import pixelitor.layers.ContentLayer;
import pixelitor.layers.Layer;
import pixelitor.layers.ImageLayer;
import pixelitor.layers.LayerButton;
import pixelitor.menus.CropMenuItem;
import pixelitor.utils.HistogramsPanel;
import pixelitor.utils.MarchingAntsSelection;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * An image composition consisting of multiple layers
 */
public class Composition implements Serializable {
    private static final long serialVersionUID = 1L;

    private int newLayerCount = 1;

    private List<Layer> layerList = new ArrayList<Layer>();
    private Layer activeLayer;
    private int canvasWidth;
    private int canvasHeight;
    private String name; // the file name or something like "Untitled 1"

    private transient File file;
    private transient boolean dirty = false;
    private transient boolean compositeImageUpToDate = false;
    private transient BufferedImage cachedCompositeImage = null;
    private transient ImageComponent ic;

    private transient MarchingAntsSelection selection;

    /**
     * If the file argument is not null, then the name argument is ignored
     */
    public Composition(ImageComponent ic, File file, String name) {
        this.ic = ic;
        if (file != null) {
            setFile(file);
        } else {
            this.name = name;
        }
    }

    // layer manipulation methods

    public void addNewEmptyLayer(String name) {
        ImageLayer newLayer = new ImageLayer(this, name, canvasWidth, canvasHeight);
        addLayer(newLayer, LayerChangeReason.NEW_EMPTY_LAYER);
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

    /**
     * Adds the specified layer above the active layer
     */
    public void addLayer(Layer newLayer, LayerChangeReason changeReason) {
        int activeLayerIndex = layerList.indexOf(activeLayer);
        int newLayerIndex = activeLayerIndex + 1;
        addLayer(newLayer, changeReason, newLayerIndex);
    }

    /**
     * Adds the specified layer at the specified layer position
     */
    public void addLayer(Layer newLayer, LayerChangeReason changeReason, int newLayerIndex) {
        layerList.add(newLayerIndex, newLayer);
        addLayerToGUI(newLayer, newLayerIndex);

        if (changeReason != LayerChangeReason.UNDO_REDO && changeReason != LayerChangeReason.COMPOSITION_INIT) {
            NewLayerEdit newLayerEdit = new NewLayerEdit(this, newLayer);
            History.addEdit(newLayerEdit);
        }
        if(changeReason.updateHistogram()) {
            imageChanged(true, true);
        } else {
            imageChanged(false, false);
        }
    }

    private void addLayerToGUI(Layer newLayer, int newLayerIndex) {
        LayerButton layerButton = newLayer.getLayerButton();
        ic.addLayerButton(layerButton, newLayerIndex);
        setActiveLayer(newLayer);

        // notify the listeners
        AppLogic.layerCountChanged(layerList.size());
    }


    public void duplicateLayer() {
        Layer duplicate = activeLayer.duplicate();
        addLayer(duplicate, LayerChangeReason.NEW_LAYER_WITH_CONTENT);
    }

    public Layer getActiveLayer() {
        return activeLayer;
    }

    // TODO the param is always false

    public void changeActiveLayerImage(BufferedImage img, ImageChangeReason changeReason, String opName) {
        changeLayerImage((ImageLayer) activeLayer, img, changeReason, opName);
    }

    public void changeLayerImage(ImageLayer layer, BufferedImage img, ImageChangeReason changeReason, String opName) {
        layer.changeImage(img, changeReason, opName);

        if (changeReason.sizeChanged()) {     // TODO necessary?
            updateCanvasSize(img.getWidth(), img.getHeight());
        }
        if (changeReason.makeUndoBackup()) {
            setDirty(true);
        }
        if (changeReason.updateHistogram()) {
            imageChanged(true, true);
        }  else {
            imageChanged(false, false);
        }
    }

    public BufferedImage getImageForActiveLayer() {
        if(activeLayer instanceof ImageLayer) {
            ImageLayer imageLayer = (ImageLayer) activeLayer;
            return imageLayer.getBufferedImage();
        }

        return null;
    }

    public int getCanvasWidth() {
        return canvasWidth;
    }

    public int getCanvasHeight() {
        return canvasHeight;
    }

    public boolean isEmpty() {
        return layerList.isEmpty();
    }


    /**
     * This method is called when a new dialog appears
     */
    public void startPreviewing() {
        activeLayer.startPreviewing();
    }

    /**
     * This method is called when a new adjustment is made in the dialog, before running the op
     */
    public void startNewPreviewFromDialog() {
        activeLayer.startNewPreviewFromDialog();
    }

    /**
     * This method is called when cancel was pressed in the preview dialog
     */
    public void cancelPreviewing() {
        activeLayer.cancelPreviewing();
        ic.repaint();
    }

    public String getName() {
        return name;
    }

    public void startTranslation(boolean makeDuplicateLayer) {
        if(!(activeLayer instanceof ContentLayer)) {
            return;
        }

        if(makeDuplicateLayer) {
            duplicateLayer();
        }

        ((ContentLayer)activeLayer).startTranslation();
    }

    public void endTranslation() {
        if(!(activeLayer instanceof ContentLayer)) {
            return;
        }

        ((ContentLayer)activeLayer).endTranslation();
        imageChanged(true, true);
    }

    public Layer getLayer(int i) {
        return layerList.get(i);
    }

    /**
     * Paints all the layers into the given Graphics2D object
     */
    BufferedImage paintLayers(Graphics2D g, BufferedImage imageSoFar) {
        boolean firstVisibleLayer = true;
        for (Layer layer : layerList) {
            if (layer.isVisible()) {
                BufferedImage result = layer.paintLayer(g, firstVisibleLayer, imageSoFar);
                if(result != null) {
                    imageSoFar = result;
                }
                firstVisibleLayer = false;
            }
        }
        return imageSoFar;
    }

    public void flattenImage() {
        if (layerList.size() < 2) {
            return;
        }

        int nrLayers = getNrLayers();
        BufferedImage bi = getCompositeImage("flattenImage");

        Layer flattenedLayer = new ImageLayer(this, bi, "flattened");
        addLayer(flattenedLayer, LayerChangeReason.NEW_LAYER_WITH_CONTENT, nrLayers); // add to the top

        for (int i = nrLayers - 1; i >= 0; i--) { // remove the rest
            removeLayer(i);
        }
        AppLogic.layerCountChanged(1);
        History.addEdit(new NotUndoableEdit(this, "Flatten Image"));
    }


    public void mergeDown() {
        int activeIndex = layerList.indexOf(activeLayer);
        if (activeIndex > 0) {
            if (activeLayer.isVisible()) {
                Layer bellow = layerList.get(activeIndex - 1);
                if (bellow.isVisible()) {
                    activeLayer.mergeDownOn(bellow);
                    removeActiveLayer();

                    History.addEdit(new NotUndoableEdit(this, "Merge Down"));
                }
            }
        }
    }


    public void moveActiveLayerUp() {
        int oldIndex = layerList.indexOf(activeLayer);
        swapLayers(oldIndex, oldIndex + 1, false);
    }

    public void moveActiveLayerDown() {
        int oldIndex = layerList.indexOf(activeLayer);
        swapLayers(oldIndex, oldIndex - 1, false);
    }

    public void moveActiveLayerToTop() {
        int oldIndex = layerList.indexOf(activeLayer);
        swapLayers(oldIndex, layerList.size() - 1, false);
    }


    public void moveActiveLayerToBottom() {
        int oldIndex = layerList.indexOf(activeLayer);
        swapLayers(oldIndex, 0, false);
    }

    public void swapLayers(int oldIndex, int newIndex, boolean isUndoRedo) {
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
        ic.changeLayerOrderInTheGUI(oldIndex, newIndex);

        imageChanged(true, true);

        if(!isUndoRedo) {
              LayerOrderChangeEdit edit = new LayerOrderChangeEdit(this, oldIndex, newIndex);
              History.addEdit(edit);
        }

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


    private BufferedImage calculateCompositeImage() {

//        BufferedImage tmp = ImageUtils.createCompatibleImage(getCanvasWidth(), getCanvasHeight());
        BufferedImage img = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_ARGB_PRE);
        Graphics2D g = img.createGraphics();
        img = paintLayers(g, img);
        g.dispose();

        return img;
    }

    void setCanvasWidth(int newCanvasWidth) {
        this.canvasWidth = newCanvasWidth;
    }

    void setCanvasHeight(int newCanvasHeight) {
        this.canvasHeight = newCanvasHeight;
    }

    public String generateNewLayerName() {
        String retVal = "layer " + newLayerCount;
        newLayerCount++;
        return retVal;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void updateCanvasSize(int newWidth, int newHeight) {
        ic.updateCanvasSize(newWidth, newHeight);  // TODO not very elegant, because it will call back to this object
    }

    public void updateRegion(int startX, int startY, int endX, int endY, int thickness) {
        compositeImageUpToDate = false;
        ic.updateRegion(startX, startY, endX, endY, thickness);
    }

    /**
     * Called when deserialized
     */
    public void setImageComponent(ImageComponent ic) {
        this.ic = ic;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

    }

    /**
     * This is called then the deserialization is complete in order to provide additional
     * initialization
     */
    public void restoreAfterDeserialization() {
        Layer activeLayerRef = activeLayer;

        for (Layer layer : layerList) {
            restoreLayerAfterDeserialization(layer);
        }

        setActiveLayer(activeLayerRef);
    }

    private void restoreLayerAfterDeserialization(Layer layer) {
        if (layerList == null) {
            throw new IllegalStateException("layerList is null");
        }
        int layerIndex = layerList.indexOf(layer);
        addLayerToGUI(layer, layerIndex);
    }


    public Rectangle getCanvasBounds() {
        return new Rectangle(0, 0, getCanvasWidth(), getCanvasHeight());
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
        setName(file.getName());
    }

    private void setName(String name) {
        this.name = name;
        if (ic != null) {
            ic.setInternalFrameTitle();
        }
    }

    private void removeLayer(int layerIndex) {
        Layer layer = layerList.get(layerIndex);
        removeLayer(layer, false);
    }

    public void removeActiveLayer() {
        removeLayer(activeLayer, false);
    }


    public void removeLayer(Layer layerToBeRemoved, boolean isUndoRedo) {
        int layerIndex = layerList.indexOf(layerToBeRemoved);

        if (!isUndoRedo) {
            DeleteLayerEdit newLayerEdit = new DeleteLayerEdit(this, layerToBeRemoved, layerIndex);
            History.addEdit(newLayerEdit);
        }

        LayerButton button = layerToBeRemoved.getLayerButton();

        layerList.remove(layerToBeRemoved);

        if (layerToBeRemoved == activeLayer) {
            if (layerIndex > 0) {
                setActiveLayer(layerList.get(layerIndex - 1));
            } else {  // removed the fist layer, set the new first layer as active
                setActiveLayer(layerList.get(0));
            }
        }

        ic.deleteLayerButton(button);

        AppLogic.layerCountChanged(layerList.size());
        imageChanged(true, true);

    }

    public void close() {
        OpenSaveManager.warnAndCloseImage(ic);
    }

    public void addNewLayerFromComposite(String newLayerName) {
        ImageLayer newLayer = new ImageLayer(this, getCompositeImage("addNewLayerFromComposite"), newLayerName);
        addLayer(newLayer, LayerChangeReason.NEW_LAYER_WITH_CONTENT);
    }

    public ImageComponent getIC() {
        return ic;
    }

    public void paintSelection(Graphics2D g) {
        if (selection != null) {
            selection.paintTheAnts(g);
        }
    }

    public void startOrUpdateSelection(Point start, Point end, boolean startFromCenter) {
        if (selection == null) {
            selection = new MarchingAntsSelection(AppLogic.getActiveImageComponent(), start, end);
            selection.startMarching();
            CropMenuItem.INSTANCE.setEnabled(true);
        } else {
            selection.updateSelection(start, end, startFromCenter);
        }
    }

    public void deselect() {
        if (selection != null) {
            selection.stopMarching();

            Rectangle selBounds = selection.getSelectionShape().getBounds();

            selection = null;
            ic.repaint(selBounds.x, selBounds.y, selBounds.width + 1, selBounds.height + 1);
            CropMenuItem.INSTANCE.setEnabled(false);
        }
    }

    public MarchingAntsSelection getSelection() {
        return selection;
    }

    public boolean hasSelection() {
        return (selection != null);
    }

    public BufferedImage getCompositeImage(String fromWhere) {
//        System.out.println("Composition.getCompositeImage fromWhere = \"" + fromWhere + "\"");

        if (compositeImageUpToDate) {
//            System.out.println("Composition.getCompositeImage CALLED - compositeImageUpToDate was useful!");
            return cachedCompositeImage; // this caching is useful for example when using the Color Picker Tool
        }

//        long startTime = System.nanoTime();

        cachedCompositeImage = calculateCompositeImage();

//        long totalTime = (System.nanoTime() - startTime) / 1000000;
//        System.out.println("Composition.getCompositeImage CALLED - calculating composite image... it took " + totalTime + " ms");

        compositeImageUpToDate = true;
        return cachedCompositeImage;
    }

    /**
     *
     */
    public void imageChanged(boolean repaint, boolean updateHistogram) {
        compositeImageUpToDate = false;

        if(repaint) {
            if (ic != null) {
                ic.repaint();
            }
        }

        if (updateHistogram) {
            HistogramsPanel.INSTANCE.updateFromCompIfShown(this);
        }
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public void moveActiveContentRelative(int relativeX, int relativeY, boolean repaint) {
        if(activeLayer instanceof ContentLayer) {
            ContentLayer contentLayer = (ContentLayer) activeLayer;
            contentLayer.moveLayerRelative(relativeX, relativeY);
            imageChanged(repaint, false);
        }
    }

    public boolean isActiveLayer(Layer layer) {
        return layer == activeLayer;
    }
}
