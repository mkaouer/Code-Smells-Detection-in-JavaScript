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

import pixelitor.filters.gui.AngleParam;
import pixelitor.filters.gui.BlendingModeParam;
import pixelitor.filters.gui.IntChoiceParam;
import pixelitor.filters.gui.ParamSet;
import pixelitor.filters.gui.RangeParam;
import pixelitor.layers.BlendingMode;
import pixelitor.utils.ImageUtils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Brick
 */
public class Brick extends OperationWithParametrizedGUI {
    private static final int TYPE_3D = 0;
    private static final int TYPE_EMBEDDED = 1;
    private static final int TYPE_FLAT = 2;

    private RangeParam sizeParam = new RangeParam("Size", 5, 100, 10);
    private BlendingModeParam blendingModeParam = new BlendingModeParam(BlendingMode.values());

    // TODO not used
    private IntChoiceParam typeParam = new IntChoiceParam("Type", new IntChoiceParam.Value[]{
            new IntChoiceParam.Value("3D", TYPE_3D),
            new IntChoiceParam.Value("Embedded", TYPE_EMBEDDED),
            new IntChoiceParam.Value("Flat", TYPE_FLAT),
    });

    private AngleParam lightAngleParam = new AngleParam("Light Direction (Azimuth) - Degrees", ImageUtils.DEG_315_IN_RADIANS);

    public Brick() {
        super("Brick", true);
        paramSet = new ParamSet(
                sizeParam,
                typeParam,
                lightAngleParam,
                blendingModeParam
        );
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        int size = sizeParam.getValue();

        int width = src.getWidth();
        int height = src.getHeight();

        BufferedImage bumpMap = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D bmg = bumpMap.createGraphics();
        bmg.setColor(Color.GRAY);
        ImageUtils.drawBrickGrid(bmg, size, width, height);
        bmg.dispose();

        float azimuth = (float) lightAngleParam.getValueInIntuitiveRadians();
        dest = ImageUtils.bumpMap(src, bumpMap, blendingModeParam.getSelectedBlendingMode().getComposite(1.0f), azimuth, 0.5235988f, 1.0f);

        return dest;
    }
}