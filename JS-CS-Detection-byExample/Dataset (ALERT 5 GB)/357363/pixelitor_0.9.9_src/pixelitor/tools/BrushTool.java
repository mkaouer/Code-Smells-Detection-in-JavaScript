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

import pixelitor.Composition;
import pixelitor.ImageComponent;
import pixelitor.layers.ImageLayer;
import pixelitor.utils.BlendingModePanel;

import javax.swing.*;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 *
 */
public class BrushTool extends AbstractBrushTool {
    private BlendingModePanel blendingModePanel;

    private JComboBox typeSelector = new JComboBox();

    public BrushTool() {
        super('b', "Brush", "brush_tool_icon.gif", "click and drag to draw with the current brush, Shift-click to draw lines, right-click to draw with the background color");
    }

    @Override
    public void initSettingsPanel(JPanel p) {
        p.add(new JLabel("Brush Type:"));
        typeSelector = new JComboBox(BrushType.values());
        p.add(typeSelector);
        typeSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BrushType brushType = (BrushType) typeSelector.getSelectedItem();
                brush = brushType.getBrush();
            }
        });

        super.initSettingsPanel(p);
        blendingModePanel = new BlendingModePanel(true);
        p.add(blendingModePanel);
    }

    @Override
    protected void initDrawingGraphics(ImageLayer layer) {
        BufferedImage image = layer.createTmpDrawingImage(blendingModePanel.getComposite());
        g = image.createGraphics();
    }

    public void setupGraphics(Graphics2D g, Paint p) {
        if(p != null) {
            g.setPaint(p);
        } else {  // can happen, if multiple mouse buttons are pressed, and there is a mouse up event during dragging
            g.setColor(FgBgColorSelector.getFG());
        }
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }

    @Override
    public void mouseReleased(MouseEvent e, ImageComponent ic) {
        if(g != null) {
            ic.mergeTmpDrawingLayerDown();
        }
        super.mouseReleased(e, ic);
    }

    @Override
    public void drawBrushStrokeProgrammatically(Composition comp, Point startingPoint, Point endPoint) {
        super.drawBrushStrokeProgrammatically(comp, startingPoint, endPoint);
        comp.getActiveLayer().mergeTmpDrawingImageDown();
    }
}