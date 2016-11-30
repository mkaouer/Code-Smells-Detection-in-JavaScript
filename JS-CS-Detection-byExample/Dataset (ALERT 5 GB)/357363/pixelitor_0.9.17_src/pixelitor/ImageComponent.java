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
import pixelitor.history.History;
import pixelitor.history.ZoomChangeEdit;
import pixelitor.layers.ImageLayer;
import pixelitor.layers.LayerButton;
import pixelitor.layers.LayersContainer;
import pixelitor.layers.LayersPanel;
import pixelitor.menus.ZoomLevel;
import pixelitor.tools.Tool;
import pixelitor.utils.ImageUtils;
import pixelitor.utils.debug.ImageComponentNode;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;
import java.io.File;

/**
 * The GUI component that shows a composition
 */
public class ImageComponent extends JComponent implements MouseListener, MouseMotionListener {
    private double viewScale = 1.0f;

    private InternalImageFrame internalFrame = null;

    private static final Color BG_GRAY = new Color(200, 200, 200);
    private static CheckerboardPainter checkerBoardPainter = new CheckerboardPainter(BG_GRAY, Color.WHITE);

    private LayersPanel layersPanel;

    private int zoomedCanvasWidth;
    private int zoomedCanvasHeight;

    private ZoomLevel zoomLevel = ZoomLevel.Z100;
    private Composition comp;

    /**
     * Called when a regular file (jpeg, png, etc.) is opened or when a new composition is created or something is pasted
     * If the file argument is not null, then the name argument is ignored
     */
    public ImageComponent(File file, String name, BufferedImage baseLayerImage) {
        if (baseLayerImage == null) {
            throw new IllegalArgumentException("baseLayerImage is null");
        }

        this.comp = new Composition(this, file, name);
        init(baseLayerImage.getWidth(), baseLayerImage.getHeight());

        comp.updateCanvasSize(baseLayerImage.getWidth(), baseLayerImage.getHeight());
        ImageLayer newLayer = new ImageLayer(comp, baseLayerImage, null);
        comp.addLayer(newLayer, LayerChangeReason.COMPOSITION_INIT, false);
    }

    /**
     * Called when a Composition is deserialized
     */
    public ImageComponent(File file, Composition comp) {
        this.comp = comp;
        comp.setImageComponent(this);

        // file is transient in Composition because the pxc file can be renamed
        comp.setFile(file);

        init(comp.getCanvasWidth(), comp.getCanvasHeight());
    }

    private void init(int cWidth, int cHeight) {
        addMouseListener(this);
        addMouseMotionListener(this);

        layersPanel = new LayersPanel();

        setCanvasWidth(cWidth);
        setCanvasHeight(cHeight);
    }

    @Override
    public Dimension getPreferredSize() {
        if (comp.isEmpty()) {
            return super.getPreferredSize();
        } else {
            return new Dimension(zoomedCanvasWidth, zoomedCanvasHeight);
        }
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        Tool.getCurrentTool().mouseClicked(e, this);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
//        mouseEntered is never used in the tools
//        Tool.getCurrentTool().mouseEntered(e, this);
    }

    @Override
    public void mouseExited(MouseEvent e) {
//        mouseExited is never used in the tools
//        Tool.getCurrentTool().mouseExited(e, this);
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
        Tool.getCurrentTool().mouseMoved(e, this);
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


    public void close() {
        if (internalFrame != null) {
            // this will also cause the calling of AppLogic.imageClosed via
            // InternalImageFrame.internalFrameClosed
            internalFrame.dispose();
        }
    }

    public void onActivation() {
        try {
            getInternalFrame().setSelected(true);
        } catch (PropertyVetoException e) {
            ExceptionHandler.showExceptionDialog(e);
        }
        LayersContainer.showLayersPanel(layersPanel);
    }

    private void setCanvasWidth(int newCanvasWidth) {
        comp.setCanvasWidth(newCanvasWidth);
        zoomedCanvasWidth = (int) (viewScale * newCanvasWidth);
    }

    private void setCanvasHeight(int newCanvasHeight) {
        comp.setCanvasHeight(newCanvasHeight);
        zoomedCanvasHeight = (int) (viewScale * newCanvasHeight);
    }

    public double getViewScale() {
        return viewScale;
    }


    public void setZoom(ZoomLevel zoomLevel, boolean addToHistory) {
        ZoomLevel previous = this.zoomLevel;
        this.zoomLevel = zoomLevel;

        if (previous == zoomLevel) {
            return;
        }

        if (addToHistory) {
            ZoomChangeEdit edit = new ZoomChangeEdit(comp, previous, this.zoomLevel);
            History.addEdit(edit);
        }

        int zoom = zoomLevel.getValue();

        this.viewScale = zoom / 100.0;
        zoomedCanvasWidth = (int) (viewScale * comp.getCanvasWidth());
        zoomedCanvasHeight = (int) (viewScale * comp.getCanvasHeight());

        if (internalFrame != null) {
            setInternalFrameTitle();
            internalFrame.setNewSize(zoomedCanvasWidth, zoomedCanvasHeight, -1, -1);
        }
        revalidate();

        super.repaint();
    }

    public void setInternalFrameTitle() {
        if (internalFrame != null) {
            String frameTitle = createFrameTitle();
            internalFrame.setTitle(frameTitle);
        }
    }

    public String createFrameTitle() {
        return comp.getName() + " - " + zoomLevel.getValue() + " %";
    }


    public ZoomLevel getZoomLevel() {
        return zoomLevel;
    }

    public void updateCanvasSize(int newWidth, int newHeight) {
        setCanvasWidth(newWidth);
        setCanvasHeight(newHeight);

        if (internalFrame != null) {
            internalFrame.setNewSize(zoomedCanvasWidth, zoomedCanvasHeight, -1, -1);
        }
        revalidate();
    }

    public void addLayerButton(LayerButton layerButton, int newLayerIndex) {
        layersPanel.addLayerButton(layerButton, newLayerIndex);
    }

    public void deleteLayerButton(LayerButton button) {
        layersPanel.deleteLayerButton(button);
    }

    public Composition getComp() {
        return comp;
    }

    public void changeLayerOrderInTheGUI(int oldIndex, int newIndex) {
        layersPanel.changeLayerOrder(oldIndex, newIndex);
    }

    public int getZoomedCanvasWidth() {
        return zoomedCanvasWidth;
    }

    public int getZoomedCanvasHeight() {
        return zoomedCanvasHeight;
    }

    @Override
    public void paint(Graphics g) {
        try {
            paintComponent(g);  // no borders, no children - but TODO consider double-buffering
        } catch (OutOfMemoryError e) {
            ExceptionHandler.showOutOfMemoryDialog();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

//        Dimension dimension = getSize();
//        int drawX = (int) ((dimension.getWidth() - comp.getCanvasWidth()) / 2);
//        int drawY = (int) ((dimension.getHeight() - comp.getCanvasHeight()) / 2);
//        g2.translate(drawX, drawY);

        boolean clipAdjusted = adjustClipBounds(g, zoomedCanvasWidth, zoomedCanvasHeight);

        checkerBoardPainter.paint(g2, this, zoomedCanvasWidth, zoomedCanvasHeight);

        g2.scale(viewScale, viewScale);

        BufferedImage compositeImage = comp.getCompositeImage();

//        g2.drawImage(compositeImage, 0, 0, null);
        ImageUtils.drawImageWithClipping(g2, compositeImage);


//        Tool.getCurrentTool().paintOverImage(g2);

        // the selection must be painted to coordinates that are increased by one at the bottom and at the right
        if (clipAdjusted) {
            // use  comp.getCanvasWidth() instead of zoomedCanvasWidth because the Graphics itself is scaled
            g.setClip(0, 0, comp.getCanvasWidth() + 1, comp.getCanvasHeight() + 1);
        }

        comp.paintSelection(g2);
        Tool.getCurrentTool().paintOverImage(g2);
    }

    private static boolean adjustClipBounds(Graphics g, int maxWidth, int maxHeight) {
        boolean hadToBeAdjusted = false;

        Rectangle clipBounds = g.getClipBounds();
        int extraWidth = clipBounds.x + clipBounds.width - maxWidth;
        if (extraWidth > 0) {
            clipBounds.width -= extraWidth;
            hadToBeAdjusted = true;
        }
        int extraHeight = clipBounds.y + clipBounds.height - maxHeight;
        if (extraHeight > 0) {
            clipBounds.height -= extraHeight;
            hadToBeAdjusted = true;
        }
        if (hadToBeAdjusted) {
            g.setClip(clipBounds);
        }
        return hadToBeAdjusted;
    }

    public void updateRegion(int startX, int startY, int endX, int endY, int thickness) {
        double diff = viewScale - 1.0f;
        if (diff > 0.0001f || diff < -0.0001f) { // not the 100% view - avoids testing for floating point equality
            startX = (int) (viewScale * startX);
            startY = (int) (viewScale * startY);
            endX = (int) (viewScale * endX);
            endY = (int) (viewScale * endY);
            thickness = (int) (viewScale * thickness);
        }

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

        int repWidth = endX - startX;
        int repHeight = endY - startY;

        repaint(startX, startY, repWidth, repHeight);
    }

    public void makeSureItIsVisible() {
        if (internalFrame != null) {
            internalFrame.makeSureItIsVisible();
        }
    }
}
