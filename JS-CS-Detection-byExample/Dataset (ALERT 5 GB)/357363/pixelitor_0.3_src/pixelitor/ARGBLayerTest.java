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

import pixelitor.utils.GUIUtils;
import pixelitor.filters.lookup.StaticLookupOp;
import pixelitor.filters.lookup.StaticLookupType;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.Kernel;
import java.awt.image.ConvolveOp;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 *
 */
public class ARGBLayerTest extends JPanel {
    private BufferedImage bi;
    public static final float[] BLUR3x3 = {
            0.1f, 0.1f, 0.1f,
            0.1f, 0.2f, 0.1f,
            0.1f, 0.1f, 0.1f};


    public ARGBLayerTest() {
        bi = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
        g.setColor(Color.RED);
        g.fillOval(200, 200, 20, 20);
        g.dispose();

        JButton b = new JButton("filter");
        setLayout(new BorderLayout());
        add(b, BorderLayout.SOUTH);
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // blur();

                StaticLookupOp op = new StaticLookupOp(StaticLookupType.INVERT);
                bi = op.transform(bi, bi, null);

                repaint();
            }
        });
    }

    private void blur() {
        Kernel kernel = new Kernel(3, 3, BLUR3x3);
        ConvolveOp cop = new ConvolveOp(kernel,
                ConvolveOp.EDGE_NO_OP,
                null);
        bi = cop.filter(bi, null);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(bi.getWidth(), bi.getHeight());
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.drawImage(bi, 0, 0, getWidth(), getHeight(), this);
    }

    public static void main(String[] args) {
        GUIUtils.testJComponent(new ARGBLayerTest());
    }

}
