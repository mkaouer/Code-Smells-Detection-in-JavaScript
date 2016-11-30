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
package pixelitor.tools;

import pixelitor.AppLogic;
import pixelitor.History;
import pixelitor.ImageChangeReason;
import pixelitor.ImageComponent;
import pixelitor.utils.ImageUtils;

import javax.swing.*;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * An enum-like abstract class for the tools. For each tool there is a subclass.
 */
public abstract class Tool {
    public static final CropSelectionTool CROP_SELECTION = new CropSelectionTool();
    public static final Tool MOVE = new MoveTool();
    public static final Tool GRADIENT = new GradientTool();
    public static final Tool BRUSH = new BrushTool();
    public static final Tool ERASE = new EraseTool();
    public static final Tool COLOR_PICKER = new ColorPickerTool();
    public static final Tool SHAPES = new ShapesTool();
    private ToolButton toolButton;

    private static Tool currentTool = CROP_SELECTION;

    /**
     * All the subclass tools in an array.
     */
//    private static Tool[] allTools = new Tool[]{CROP_SELECTION, MOVE, GRADIENT, DRAW, ERASE, COLOR_PICKER};
    private static Tool[] allTools = new Tool[]{CROP_SELECTION, MOVE, GRADIENT, BRUSH, ERASE, COLOR_PICKER, SHAPES};

    public static Tool[] values() {
        return allTools;
    }


    protected Point start = new Point(0, 0);
    protected Point end = new Point(0, 0);

    protected Tool() {
    }

    public static Tool getCurrentTool() {
        return currentTool;
    }

    public static void setCurrentTool(Tool currentTool) {
        Tool.currentTool.toolEnded();
        Tool.currentTool = currentTool;
        Tool.currentTool.toolStarted();
        ToolSettingsPanel.INSTANCE.showSettingsFor(currentTool);
    }

    protected void toolStarted() {
    }

    protected void toolEnded() {
    }

    /**
     * A possibility to paint something (like marching ants) on the ImageComponent
     * after all the layers have been painted.
     */
    public void paintOnImage(Graphics2D g) {
    }

    public void mouseClicked(MouseEvent e, ImageComponent ic) {
    }

    public void mouseEntered(MouseEvent e, ImageComponent ic) {
    }

    public void mouseExited(MouseEvent e, ImageComponent ic) {
    }

    public void mousePressed(MouseEvent e, ImageComponent ic) {
        start = new Point(e.getX(), e.getY());
    }

    public void mouseReleased(MouseEvent e, ImageComponent ic) {
        end = new Point(e.getX(), e.getY());
    }

    public void mouseDragged(MouseEvent e, ImageComponent ic) {
        end = new Point(e.getX(), e.getY());
    }

    public void mouseMoved(MouseEvent e, ImageComponent ic) {
    }

    public void setButton(ToolButton toolButton) {
        this.toolButton = toolButton;
    }

    public ToolButton getButton() {
        return toolButton;
    }

    abstract void initSettingsPanel(JPanel p);

    public abstract String getName();

    protected abstract String getIconFileName();

    public abstract KeyStroke getActivationKeyStroke();

    public void saveImageForUndo() {
        ImageComponent ic = AppLogic.getActiveImageComponent();
        BufferedImage bi = ic.getActiveLayer().getBufferedImage();
        BufferedImage copy = ImageUtils.copyImage(bi);
        History.setBackup(copy, ic, ImageChangeReason.TOOL);
        AppLogic.activeImageHasChanged(ic);
    }

    public static class ToolButton extends JToggleButton implements ActionListener {
        private Tool tool;

        public ToolButton(Tool tool) {
            this.tool = tool;
            tool.setButton(this);

            Icon icon = ImageUtils.loadIcon(tool.getIconFileName());
            setIcon(icon);

            char c = tool.getActivationKeyStroke().getKeyChar();
            String s = new String(new char[]{c}).toUpperCase();
            setToolTipText(tool.getName() + " Tool (" + s + ")");

            setMargin(new Insets(0, 0, 0, 0));
            setBorderPainted(true);
            setRolloverEnabled(false);
            addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            AppLogic.setCurrentTool(tool);
        }
    }
}
