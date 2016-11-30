/*
 * Copyright 2010 Laszlo Balazs-Csiki
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

import pixelitor.ImageComponents;
import pixelitor.filters.gui.*;
import pixelitor.layers.ImageLayer;
import pixelitor.utils.Utils;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * Arbitrary Rotate
 */
public class TransformLayer extends FilterWithParametrizedGUI {
    private final ImagePositionParam centerParam = new ImagePositionParam("Center");
    private final AngleParam angleParam = new AngleParam("Rotate Angle", 0);
    private final ColorParam bgColorParam = new ColorParam("Background Color:", Utils.TRANSPARENT_COLOR, true, false);
    private final CoupledRangeParam scaleParam = new CoupledRangeParam("Scale (%)", 1, 500, 100);
    private final CoupledRangeParam shearParam = new CoupledRangeParam("Shear", -500, 500, 0);

    public TransformLayer() {
        super("Transform Layer", true, false);
        setParamSet(new ParamSet(
                centerParam,
                angleParam,
                scaleParam,
                shearParam,
                bgColorParam
        ));
        shearParam.setCoupled(false);
    }

    @Override
    public BufferedImage doTransform(BufferedImage src, BufferedImage dest) {
        // fill with the background color
        Graphics2D g = dest.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setColor(bgColorParam.getColor());
        g.fillRect(0, 0, dest.getWidth(), dest.getHeight());

        double theta = angleParam.getValueInRadians();

        ImageLayer layer = ImageComponents.getActiveComp().getActiveImageLayer();

        float relativeX = centerParam.getRelativeX();
        float relativeY = centerParam.getRelativeY();

        double centerShiftX = (-layer.getTranslationX() + src.getWidth()) * relativeX;
        double centerShiftY = (-layer.getTranslationY() + src.getHeight()) * relativeY;

        AffineTransform transform = AffineTransform.getRotateInstance(theta, centerShiftX, centerShiftY);

        int scaleX = scaleParam.getFirstValue();
        int scaleY = scaleParam.getSecondValue();
        if ((scaleX != 100) || (scaleY != 100)) {
            transform.translate(centerShiftX, centerShiftY);
            transform.scale(scaleX / 100.0, scaleY / 100.0);
            transform.translate(-centerShiftX, -centerShiftY);
        }

        int shearX = shearParam.getFirstValue();
        int shearY = shearParam.getSecondValue();
        if ((shearX != 0) || (shearY != 0)) {
            transform.translate(centerShiftX, centerShiftY);
            transform.shear(shearX / 100.0, shearY / 100.0);
            transform.translate(-centerShiftX, -centerShiftY);
        }

        g.drawImage(src, transform, null);

        g.dispose();

        return dest;
    }
}