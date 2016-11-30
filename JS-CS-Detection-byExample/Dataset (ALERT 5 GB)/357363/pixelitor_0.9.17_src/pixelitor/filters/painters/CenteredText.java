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
package pixelitor.filters.painters;

import org.jdesktop.swingx.painter.TextPainter;
import org.jdesktop.swingx.painter.effects.AreaEffect;
import pixelitor.filters.gui.AdjustPanel;
import pixelitor.filters.gui.OperationWithGUI;
import pixelitor.tools.FgBgColorSelector;
import pixelitor.utils.ImageUtils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 *
 */
public class CenteredText extends OperationWithGUI {
    private String text = "Pixelitor";
    private Font font = new Font(Font.SANS_SERIF, Font.BOLD, 50);
    private AreaEffect[] areaEffects;

    public static final CenteredText INSTANCE = new CenteredText();
    private boolean watermark;

    private CenteredText() {
        super("Centered Text");
        copySrcToDstBeforeRunning = true;
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        int width = dest.getWidth();
        int height = dest.getHeight();

        TextPainter textPainter = new TextPainter();
        textPainter.setAntialiasing(true);
        textPainter.setText(text);
        textPainter.setFont(font);
        textPainter.setAreaEffects(areaEffects);

        if (watermark) {
            textPainter.setFillPaint(Color.WHITE);

            BufferedImage bumpImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = bumpImage.createGraphics();
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, width, height);
            textPainter.paint(g, this, width, height);
            g.dispose();

//            LightFilter lightFilter = new LightFilter();
//            lightFilter.setBumpSource(LightFilter.BUMPS_FROM_MAP);
//            lightFilter.setBumpFunction(new ImageFunction2D(bumpImage, false));
//            lightFilter.setBumpSoftness(0);
//
//            dest = lightFilter.filter(src, dest);

            dest = ImageUtils.bumpMap(src, bumpImage);
        } else {
            textPainter.setFillPaint(FgBgColorSelector.getFG());

            Graphics2D g = dest.createGraphics();
            textPainter.paint(g, this, width, height);
            g.dispose();
        }


        return dest;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public void setText(String s) {
        text = s;
    }

    public void setAreaEffects(AreaEffect[] areaEffects) {
        this.areaEffects = areaEffects;
    }

    @Override
    public AdjustPanel getAdjustPanel() {
        return new CenteredTextAdjustments(this);
    }

    public void setWatermark(boolean watermark) {
        this.watermark = watermark;
    }
}