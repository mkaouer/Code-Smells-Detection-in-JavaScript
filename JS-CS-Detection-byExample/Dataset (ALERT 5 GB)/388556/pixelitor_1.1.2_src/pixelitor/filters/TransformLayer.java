/*
 * Copyright 2010 L�szl� Bal�zs-Cs�ki
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
import pixelitor.filters.gui.CoupledRangeParam;
import pixelitor.filters.gui.ImagePositionParam;
import pixelitor.filters.gui.ParamSet;
import pixelitor.layers.ImageLayer;
import pixelitor.utils.Utils;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * Arbitrary Rotate
 */
public class TransformLayer extends FilterWithParametrizedGUI {
    private ImagePositionParam centerParam = new ImagePositionParam("Center");
    private AngleParam angleParam = new AngleParam("Rotate Angle", 0);
    private ColorParam bgColorParam = new ColorParam("Background Color:", Utils.TRANSPARENT_COLOR, true, false);
    private CoupledRangeParam scaleParam = new CoupledRangeParam("Scale (%)", 1, 500, 100);
    private CoupledRangeParam shearParam = new CoupledRangeParam("Shear", -500, 500, 0);

    public TransformLayer() {
        super("Transform Layer");
        paramSet = new ParamSet(
                centerParam,
                angleParam,
                scaleParam,
                shearParam,
                bgColorParam
        );
        shearParam.setCoupled(false);
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

        float relativeX = centerParam.getRelativeX();
        float relativeY = centerParam.getRelativeY();

        double centerShiftX = (-layer.getTranslationX() + src.getWidth()) * relativeX;
        double centerShiftY = (-layer.getTranslationY() + src.getHeight()) * relativeY;

        AffineTransform transform = AffineTransform.getRotateInstance(theta, centerShiftX, centerShiftY);

        int scaleX = scaleParam.getFirstValue();
        int scaleY = scaleParam.getSecondValue();
        if((scaleX != 100) || (scaleY != 100)) {
            transform.translate(centerShiftX, centerShiftY);
            transform.scale(scaleX / 100.0, scaleY / 100.0);
            transform.translate(-centerShiftX, -centerShiftY);
        }

        int shearX = shearParam.getFirstValue();
        int shearY = shearParam.getSecondValue();
        if((shearX != 0) || (shearY != 0)) {
            transform.translate(centerShiftX, centerShiftY);
            transform.shear(shearX / 100.0, shearY / 100.0);
            transform.translate(-centerShiftX, -centerShiftY);
        }

        g.drawImage(src, transform, null);

        g.dispose();

        return dest;
    }
}