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

import com.jhlabs.composite.DifferenceComposite;
import com.jhlabs.composite.ScreenComposite;
import com.jhlabs.image.PolarFilter;
import pixelitor.AppLogic;
import pixelitor.Composition;
import pixelitor.ImageComponents;
import pixelitor.filters.gui.*;
import pixelitor.filters.lookup.FastLookupOp;
import pixelitor.utils.ImageUtils;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ShortLookupTable;

/**
 * Lightning
 */
public class Lightning extends FilterWithParametrizedGUI {
    private final RangeParam numberOfBoltsParam = new RangeParam("Number of Bolts", 1, 20, 8);
    private final ImagePositionParam centerParam = new ImagePositionParam("Center");
    private final ColorParam colorParam = new ColorParam("Color:", Color.WHITE, false, false);
    private final RangeParam boltExpansionParam = new RangeParam("Bolt Expansion", 1, 255, 70);

    public static final int MATH_ATAN = 0;
    public static final int FAST_ATAN = 1;

    @SuppressWarnings("FieldCanBeLocal")
    private final ActionParam reseedAction = new ActionParam("Reseed", new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            Clouds.reseed();
        }
    });

    public Lightning() {
        super("Lightning", true, false);
        setParamSet(new ParamSet(
                centerParam,
                numberOfBoltsParam,
                boltExpansionParam,
//                colorParam,        TODO does not work right
                reseedAction
        ));
    }

    @Override
    public BufferedImage doTransform(BufferedImage src, BufferedImage dest) {
        boolean debug = false;

        int numberOfBolts = numberOfBoltsParam.getValue();
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();


        int lightningImageSize = 2 * Math.max(srcWidth, srcHeight);
        int gradientDistance = (lightningImageSize / (numberOfBolts ));

        // create image with vertical bars
        BufferedImage lightningImage = new BufferedImage(lightningImageSize, lightningImageSize, BufferedImage.TYPE_INT_ARGB);
        Paint gradient = new LinearGradientPaint(0, 0, gradientDistance, 0, ImageUtils.FRACTIONS_2_COLOR_UNIFORM, new Color[]{Color.BLACK, Color.WHITE}, MultipleGradientPaint.CycleMethod.REFLECT);
        Graphics2D g = lightningImage.createGraphics();
        g.setPaint(gradient);
        g.fillRect(0, 0, lightningImage.getWidth(), lightningImage.getHeight());
        g.dispose();

        Composition composition = null;
        if (debug) {
            composition = ImageComponents.getActiveComp();
            AppLogic.debugImage(lightningImage, "lightningImage before polar");
        }

        // apply rectangular to polar transform
        PolarFilter polarFilter = new PolarFilter(PolarFilter.RECT_TO_POLAR);
        polarFilter.setEdgeAction(PolarFilter.ZERO);
        polarFilter.setInterpolation(PolarFilter.NEAREST_NEIGHBOUR);
        lightningImage = polarFilter.filter(lightningImage, lightningImage);

        if (debug) {
            AppLogic.debugImage(lightningImage, "lightningImage after polar");
        }

        // crop the image to the target size
        int xSizeDiff = lightningImage.getWidth() - srcWidth;
        int ySizeDiff = lightningImage.getHeight() - srcHeight;
        int xTrans = -xSizeDiff / 2 + (int) (srcWidth * (centerParam.getRelativeX() - 0.5));
        int yTrans = -ySizeDiff / 2 + (int) (srcHeight * (centerParam.getRelativeY() - 0.5));

        BufferedImage croppedLightningImage = new BufferedImage(srcWidth, srcHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gCroppedLightning = croppedLightningImage.createGraphics();
        gCroppedLightning.drawImage(lightningImage, xTrans, yTrans, null);

        // apply difference clouds
        BufferedImage cloudsImage = new BufferedImage(srcWidth, srcHeight, BufferedImage.TYPE_INT_ARGB);
        Clouds.renderClouds(cloudsImage, 100, 0.5f, Color.BLACK, Color.WHITE);
        gCroppedLightning.setComposite(new DifferenceComposite(1.0f));
        gCroppedLightning.drawImage(cloudsImage, 0, 0, null);

        gCroppedLightning.dispose();

//        if(2 > 1) {
//            return croppedLightningImage;
//        }

        if (debug) {
//            AppLogic.debugImage(croppedLightningImage, "croppedLightningImage after crop");
        }

        int levelsThreshold = boltExpansionParam.getValue();
        short[] invertAndDarkenLUT = new short[256];
        for (short i = 0; i < invertAndDarkenLUT.length; i++) {
            if (i >= levelsThreshold) {
                invertAndDarkenLUT[i] = 0;
            } else {
                invertAndDarkenLUT[i] = (short) (255 * (levelsThreshold - i) / levelsThreshold);
            }
            // TODO it would be better to use a formula corresponding to the midtones movement of the levels slider
        }

        short[][] lookupData = new short[3][256];
        lookupData[0] = invertAndDarkenLUT;
        lookupData[1] = invertAndDarkenLUT;
        lookupData[2] = invertAndDarkenLUT;

        Color color = colorParam.getColor();
        if (!color.equals(Color.WHITE)) {
            int red = color.getRed();
            int blue = color.getBlue();
            int green = color.getGreen();
            // colorize the white-gray-black lightning image so that white stays white, black stays black

            for (int i = 0; i < 256; i++) {
                int luminance = lookupData[0][i]; // all are the same
                float grayness = (luminance - 255) / 128.0f;
                if (grayness < 0) {
                    grayness = -grayness;
                }
                lookupData[0][i] = (short) ((short) (red * grayness) + lookupData[0][i]);
                lookupData[1][i] = (short) ((short) (green * grayness) + lookupData[0][i]);
                lookupData[2][i] = (short) ((short) (blue * grayness) + lookupData[0][i]);
            }
        }


        BufferedImage invertedImage = new BufferedImage(srcWidth, srcHeight, BufferedImage.TYPE_INT_ARGB);
        invertedImage = new FastLookupOp(new ShortLookupTable(0, lookupData)).filter(croppedLightningImage, invertedImage);

        if (debug) {
//            AppLogic.debugImage(lightningImage, "after polar");
            ImageComponents.setActiveImageComponent(composition.getIC(), true);
        }

        dest = ImageUtils.copyImage(src);
        Graphics2D gDest = dest.createGraphics();
        gDest.setComposite(new ScreenComposite(1.0f));
        gDest.drawImage(invertedImage, 0, 0, null);
        gDest.dispose();

        return dest;
    }
}