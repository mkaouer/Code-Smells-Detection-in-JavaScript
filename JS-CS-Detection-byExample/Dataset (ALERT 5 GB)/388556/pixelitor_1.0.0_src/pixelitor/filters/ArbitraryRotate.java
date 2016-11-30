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
package pixelitor.filters;

import pixelitor.AppLogic;
import pixelitor.filters.gui.AngleParam;
import pixelitor.filters.gui.ColorParam;
import pixelitor.filters.gui.ParamSet;
import pixelitor.layers.ImageLayer;
import pixelitor.utils.Utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * Arbitrary Rotate
 */
public class ArbitraryRotate extends FilterWithParametrizedGUI {
    private AngleParam angleParam = new AngleParam("Angle", 0);
    private ColorParam bgColorParam = new ColorParam("Background Color:", Utils.TRANSPARENT_COLOR, true, false);

    public ArbitraryRotate() {
        super("Rotate Layer", true);
        paramSet = new ParamSet(
                angleParam,
                bgColorParam
        );
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        // fill with the background color
        Graphics2D g = dest.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setColor(bgColorParam.getColor());
        g.fillRect(0, 0, dest.getWidth(), dest.getHeight());

        double theta = angleParam.getValueInRadians();

        ImageLayer layer = AppLogic.getActiveComp().getActiveImageLayer();
        double centerShiftX =  (- layer.getTranslationX() + src.getWidth()) / 2.0;
        double centerShiftY = (- layer.getTranslationY() + src.getHeight()) / 2.0;

        g.drawImage(src, AffineTransform.getRotateInstance(theta, centerShiftX, centerShiftY), null);

        g.dispose();

        return dest;
    }
}