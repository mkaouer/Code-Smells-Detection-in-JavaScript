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

import pixelitor.FgBgColorSelector;
import pixelitor.ImageComponent;
import pixelitor.layers.Layer;
import pixelitor.utils.BlendingModePanel;

import javax.swing.*;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
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

    enum Type {
        IDEAL {
            @Override
            public String toString() {
                return "Ideal";
            }
            @Override
            public boolean useFillOval() {
                return true;
            }
        }, REAL {
            @Override
            public String toString() {
                return "Real";
            }
            @Override
            public boolean useFillOval() {
                return false;
            }
        };

        public abstract boolean useFillOval();
    }

    private JComboBox typeSelector = new JComboBox();

//    private float opacity = 0.5f;


    public BrushTool() {
        super('b', "Brush", "brush_tool_icon.gif");
    }

    @Override
    public void initSettingsPanel(JPanel p) {
        p.add(new JLabel("Brush Type:"));
        typeSelector = new JComboBox(Type.values());
        p.add(typeSelector);
        typeSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Type type = (Type) typeSelector.getSelectedItem();
                useFillOval = type.useFillOval();
            }
        });

        super.initSettingsPanel(p);
        blendingModePanel = new BlendingModePanel(true);
        p.add(blendingModePanel);
    }

    @Override
    protected void initDrawingGraphics(Layer layer) {
        BufferedImage image = layer.createTmpDrawingLayer(blendingModePanel.getComposite());
        g = image.createGraphics();
    }

    public void setupGraphics(Graphics2D g) {
        g.setColor(FgBgColorSelector.getFG());
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }

    @Override
    public void mouseReleased(MouseEvent e, ImageComponent ic) {
        super.mouseReleased(e, ic);
        ic.getActiveLayer().mergeTmpDrawingLayerDown();
    }

    @Override
    public void drawBrushStroke(ImageComponent ic, Point startingPoint, Point endPoint) {
        super.drawBrushStroke(ic, startingPoint, endPoint);
        ic.getActiveLayer().mergeTmpDrawingLayerDown();
    }

    @Override
    public void mouseDragged(MouseEvent e, ImageComponent ic) {
        super.mouseDragged(e, ic);
        ic.imageChanged(false);
    }
}