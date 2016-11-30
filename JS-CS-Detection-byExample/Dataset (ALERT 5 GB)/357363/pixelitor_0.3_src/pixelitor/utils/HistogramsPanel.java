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

package pixelitor.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;

import javax.swing.*;

import pixelitor.ImageComponent;

public class HistogramsPanel extends JPanel implements ImageChangeListener {
    public static final HistogramsPanel INSTANCE = new HistogramsPanel();

    private HistogramPainter red;
    private HistogramPainter green;
    private HistogramPainter blue;

    private HistogramsPanel() {
        red = new HistogramPainter(Color.red);
        green = new HistogramPainter(Color.green);
        blue = new HistogramPainter(Color.blue);
        Box box = Box.createVerticalBox();
        add(box);

        box.add(red);
        box.add(green);
        box.add(blue);
        setBorder(BorderFactory.createTitledBorder("Histograms"));
    }

    public void updateWithImage(BufferedImage input) {
        if(getParent() == null) {
            return;
        }
        if (input == null) {
            throw new IllegalArgumentException("trying to update with null image");
        }

        int[] redValues = new int[256];
        int[] blueValues = new int[256];
        int[] greenValues = new int[256];

        int[] data = Utils.getPixelsAsArray(input);
        for (int rgb : data) {
            //                int a = (rgb >>> 24) & 0xFF;
            int r = (rgb >>> 16) & 0xFF;
            int g = (rgb >>> 8) & 0xFF;
            int b = (rgb) & 0xFF;

            redValues[r]++;
            greenValues[g]++;
            blueValues[b]++;
        }

        red.updateData(redValues);
        green.updateData(greenValues);
        blue.updateData(blueValues);
        repaint();
    }

    @Override
    public void noOpenImageAnymore() {
        red.updateWithNothing();
        green.updateWithNothing();
        blue.updateWithNothing();
        repaint();
    }

    @Override
    public void newImageOpened() {
    }

    @Override
    public void imageContentChanged(ImageChangedEvent e) {
        if (e.getChangeReason().updateHistogram()) {
            updateWithImage(e.getImageComponent().getCompositeImage());
        }
    }

    @Override
    public void activeImageHasChanged(ImageComponent imageComponent) {
        BufferedImage image = imageComponent.getCompositeImage();
        if (image != null) {
            updateWithImage(image);
        }
    }
}
