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

import pixelitor.Build;
import pixelitor.filters.gui.*;
import pixelitor.layers.BlendingMode;
import pixelitor.utils.ImageUtils;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * A test operation with all GUIParam objects
 */
public class ParamTest extends FilterWithParametrizedGUI {
    private final float[] defaultThumbPositions = {0.0f, 0.5f, 1.0f};
    private final Color[] defaultValues = {Color.BLACK, Color.BLUE, Color.WHITE};

    private final GradientParam gradientParam = new GradientParam("Colors", defaultThumbPositions, defaultValues);


    private final RangeParam rangeParam = new RangeParam("RangeParam", 0, 100, 50);
    private final RangeWithColorsParam rangeWithColorsParam = new RangeWithColorsParam(Color.RED, Color.BLUE, "RangeWithColorsParam", 0, 100, 50);
    private final ImagePositionParam centerParam = new ImagePositionParam("ImagePositionParam");
    private final IntChoiceParam edgeActionParam = new IntChoiceParam("IntChoiceParam", new IntChoiceParam.Value[]{
            new IntChoiceParam.Value("value 1", 1),
            new IntChoiceParam.Value("value 2", 2),
    });
    private final ColorParam colorParam = new ColorParam("ColorParam:", Color.WHITE, true, true);
    private final ActionParam actionParam = new ActionParam("ActionParam", new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    });
    private final AngleParam angleParam = new AngleParam("AngleParam", 0);
    private final ElevationAngleParam elevationAngleParam = new ElevationAngleParam("ElevationAngleParam", 0);
    private final BlendingModeParam blendingModeParam = new BlendingModeParam(BlendingMode.values());
    private final BooleanParam booleanParam = new BooleanParam("BooleanParam", false);
    private final TextParam textParam = new TextParam("TextParam", "default value");

    public ParamTest() {
        super("ParamTest", true, false);
        setParamSet(new ParamSet(
                gradientParam,
                rangeParam,
                rangeWithColorsParam,
                centerParam,
                edgeActionParam,
                colorParam,
                actionParam,
                angleParam,
                elevationAngleParam,
                blendingModeParam,
                booleanParam,
                textParam
        ));
    }

    @Override
    public BufferedImage doTransform(BufferedImage src, BufferedImage dest) {
        if ((Build.CURRENT == Build.DEVELOPMENT) && (!Build.CURRENT.isRobotTest())) {
            System.out.println("ParamTest.transform CALLED");
        }

        dest = ImageUtils.copyImage(src);
        return dest;
    }
}