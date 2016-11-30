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
package pixelitor.operations.painters;

import org.jdesktop.swingx.painter.TextPainter;
import org.jdesktop.swingx.painter.effects.AreaEffect;
import pixelitor.FgBgColorSelector;
import pixelitor.ImageChangeReason;
import pixelitor.operations.gui.AdjustPanel;
import pixelitor.operations.gui.OperationWithGUI;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 *
 */
public class CenteredText extends OperationWithGUI {
    private String text = "Pixelitor";
    private Font font = new Font(Font.SANS_SERIF, Font.BOLD, 50);
    private AreaEffect[] areaEffects = new AreaEffect[0];

    public static CenteredText INSTANCE = new CenteredText();

    private CenteredText() {
        super("Centered Text");
        copySrcToDstBeforeRunning = true;
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest, ImageChangeReason changeReason) {
        Graphics2D g = dest.createGraphics();
        TextPainter p = new TextPainter();
        p.setAntialiasing(true);
        p.setText(text);
        p.setFont(font);
        p.setFillPaint(FgBgColorSelector.getFG());
        p.setAreaEffects(areaEffects);

        p.paint(g, this, dest.getWidth(), dest.getHeight());
        g.dispose();
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
}