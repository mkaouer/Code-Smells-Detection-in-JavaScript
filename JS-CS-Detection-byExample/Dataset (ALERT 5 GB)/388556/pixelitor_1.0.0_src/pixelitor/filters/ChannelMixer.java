/*
 * Copyright 2009-2010 L�szl� Bal�zs-Cs�ki
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

import pixelitor.filters.gui.ActionParam;
import pixelitor.filters.gui.AdjustPanel;
import pixelitor.filters.gui.ChannelMixerAdjustments;
import pixelitor.filters.gui.GUIParam;
import pixelitor.filters.gui.ParamSet;
import pixelitor.filters.gui.RangeParam;
import pixelitor.utils.ImageUtils;
import pixelitor.utils.SliderSpinner;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BandCombineOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class ChannelMixer extends FilterWithParametrizedGUI {
    private static final int MIN_PERCENT = -200;
    private static final int MAX_PERCENT = 200;

    private RangeParam rpRedFromRed = new RangeParam("<html><b><font color=red>Red</font></b> from <font color=red>red</font> (%):</html>", MIN_PERCENT, MAX_PERCENT, 100, true, SliderSpinner.TextPosition.NONE);
    private RangeParam rpRedFromGreen = new RangeParam("<html><b><font color=red>Red</font></b> from <font color=green>green</font> (%):</html>", MIN_PERCENT, MAX_PERCENT, 0, true, SliderSpinner.TextPosition.NONE);
    private RangeParam rpRedFromBlue = new RangeParam("<html><b><font color=red>Red</font></b> from <font color=blue>blue</font> (%):</html>", MIN_PERCENT, MAX_PERCENT, 0, true, SliderSpinner.TextPosition.NONE);

    private RangeParam rpGreenFromRed = new RangeParam("<html><b><font color=green>Green</font></b> from <font color=red>red</font> (%):</html>", MIN_PERCENT, MAX_PERCENT, 0, true, SliderSpinner.TextPosition.NONE);
    private RangeParam rpGreenFromGreen = new RangeParam("<html><b><font color=green>Green</font></b> from <font color=green>green</font> (%):</html>", MIN_PERCENT, MAX_PERCENT, 100, true, SliderSpinner.TextPosition.NONE);
    private RangeParam rpGreenFromBlue = new RangeParam("<html><b><font color=green>Green</font></b> from <font color=blue>blue</font> (%):</html>", MIN_PERCENT, MAX_PERCENT, 0, true, SliderSpinner.TextPosition.NONE);

    private RangeParam rpBlueFromRed = new RangeParam("<html><b><font color=blue>Blue</font></b> from <font color=red>red</font> (%):</html>", MIN_PERCENT, MAX_PERCENT, 0, true, SliderSpinner.TextPosition.NONE);
    private RangeParam rpBlueFromGreen = new RangeParam("<html><b><font color=blue>Blue</font></b> from <font color=green>green</font> (%):</html>", MIN_PERCENT, MAX_PERCENT, 0, true, SliderSpinner.TextPosition.NONE);
    private RangeParam rpBlueFromBlue = new RangeParam("<html><b><font color=blue>Blue</font></b> from <font color=blue>blue</font> (%):</html>", MIN_PERCENT, MAX_PERCENT, 100, true, SliderSpinner.TextPosition.NONE);

    @SuppressWarnings({"FieldCanBeLocal"})
    private ActionListener normalizeAction = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            paramSet.startPresetAdjusting();

            normalizeChannel(rpRedFromRed, rpRedFromGreen, rpRedFromBlue);
            normalizeChannel(rpGreenFromRed, rpGreenFromGreen, rpGreenFromBlue);
            normalizeChannel(rpBlueFromRed, rpBlueFromGreen, rpBlueFromBlue);

            paramSet.endPresetAdjusting(false);
        }
    };

    private Action switchRedGreen;{
        switchRedGreen = new AbstractAction("switch red-green") {
            @Override
            public void actionPerformed(ActionEvent e) {
                paramSet.startPresetAdjusting();

                rpRedFromRed.setValue(0);
                rpRedFromGreen.setValue(100);
                rpRedFromBlue.setValue(0);

                rpGreenFromRed.setValue(100);
                rpGreenFromGreen.setValue(0);
                rpGreenFromBlue.setValue(0);

                rpBlueFromRed.setValue(0);
                rpBlueFromGreen.setValue(0);
                rpBlueFromBlue.setValue(100);

                paramSet.endPresetAdjusting(true);
            }
        };
    }

    private Action switchRedBlue = new AbstractAction("switch red-blue") {
        @Override
        public void actionPerformed(ActionEvent e) {
            paramSet.startPresetAdjusting();

            rpRedFromRed.setValue(0);
            rpRedFromGreen.setValue(0);
            rpRedFromBlue.setValue(100);

            rpGreenFromRed.setValue(0);
            rpGreenFromGreen.setValue(100);
            rpGreenFromBlue.setValue(0);

            rpBlueFromRed.setValue(100);
            rpBlueFromGreen.setValue(0);
            rpBlueFromBlue.setValue(0);

            paramSet.endPresetAdjusting(true);
        }
    };

    private Action switchGreenBlue = new AbstractAction("switch green-blue") {
        @Override
        public void actionPerformed(ActionEvent e) {
            paramSet.startPresetAdjusting();

            rpRedFromRed.setValue(100);
            rpRedFromGreen.setValue(0);
            rpRedFromBlue.setValue(0);

            rpGreenFromRed.setValue(0);
            rpGreenFromGreen.setValue(0);
            rpGreenFromBlue.setValue(100);

            rpBlueFromRed.setValue(0);
            rpBlueFromGreen.setValue(100);
            rpBlueFromBlue.setValue(0);

            paramSet.endPresetAdjusting(true);
        }
    };

    private Action shiftRGBR = new AbstractAction("R -> G -> B -> R") {
        @Override
        public void actionPerformed(ActionEvent e) {
            paramSet.startPresetAdjusting();

            rpRedFromRed.setValue(0);
            rpRedFromGreen.setValue(0);
            rpRedFromBlue.setValue(100);

            rpGreenFromRed.setValue(100);
            rpGreenFromGreen.setValue(0);
            rpGreenFromBlue.setValue(0);

            rpBlueFromRed.setValue(0);
            rpBlueFromGreen.setValue(100);
            rpBlueFromBlue.setValue(0);

            paramSet.endPresetAdjusting(true);
        }
    };

    private Action shiftRBGR = new AbstractAction("R -> B -> G -> R") {
        @Override
        public void actionPerformed(ActionEvent e) {
            paramSet.startPresetAdjusting();

            rpRedFromRed.setValue(0);
            rpRedFromGreen.setValue(100);
            rpRedFromBlue.setValue(0);

            rpGreenFromRed.setValue(0);
            rpGreenFromGreen.setValue(0);
            rpGreenFromBlue.setValue(100);

            rpBlueFromRed.setValue(100);
            rpBlueFromGreen.setValue(0);
            rpBlueFromBlue.setValue(0);

            paramSet.endPresetAdjusting(true);
        }
    };


    private Action averageBW = new AbstractAction("average BW") {
        @Override
        public void actionPerformed(ActionEvent e) {
            paramSet.startPresetAdjusting();

            rpRedFromRed.setValue(33);
            rpRedFromGreen.setValue(33);
            rpRedFromBlue.setValue(33);

            rpGreenFromRed.setValue(33);
            rpGreenFromGreen.setValue(33);
            rpGreenFromBlue.setValue(33);

            rpBlueFromRed.setValue(33);
            rpBlueFromGreen.setValue(33);
            rpBlueFromBlue.setValue(33);

            paramSet.endPresetAdjusting(true);
        }
    };

    private Action luminosityBW = new AbstractAction("luminosity BW") {
        @Override
        public void actionPerformed(ActionEvent e) {
            paramSet.startPresetAdjusting();

            rpRedFromRed.setValue(22);
            rpRedFromGreen.setValue(71);
            rpRedFromBlue.setValue(7);

            rpGreenFromRed.setValue(22);
            rpGreenFromGreen.setValue(71);
            rpGreenFromBlue.setValue(7);

            rpBlueFromRed.setValue(22);
            rpBlueFromGreen.setValue(71);
            rpBlueFromBlue.setValue(7);

            paramSet.endPresetAdjusting(true);
        }
    };

    private Action sepia = new AbstractAction("sepia") {
        @Override
        public void actionPerformed(ActionEvent e) {
            paramSet.startPresetAdjusting();

            rpRedFromRed.setValue(39);
            rpRedFromGreen.setValue(77);
            rpRedFromBlue.setValue(19);

            rpGreenFromRed.setValue(35);
            rpGreenFromGreen.setValue(69);
            rpGreenFromBlue.setValue(17);

            rpBlueFromRed.setValue(27);
            rpBlueFromGreen.setValue(53);
            rpBlueFromBlue.setValue(13);

            paramSet.endPresetAdjusting(true);
        }
    };

    private Action[] actions = new Action[]{switchRedGreen, switchRedBlue, switchGreenBlue, shiftRGBR, shiftRBGR, averageBW, luminosityBW, sepia};


    public ChannelMixer() {
        super("Channel Mixer", false);
        ActionParam normalize = new ActionParam("Normalize", normalizeAction);
        GUIParam[] params = new GUIParam[]{
                rpRedFromRed,
                rpRedFromGreen,
                rpRedFromBlue,

                rpGreenFromRed,
                rpGreenFromGreen,
                rpGreenFromBlue,

                rpBlueFromRed,
                rpBlueFromGreen,
                rpBlueFromBlue,

                normalize
        };
        paramSet = new ParamSet(params);
    }

    private static void normalizeChannel(RangeParam fromRed, RangeParam fromGreen, RangeParam fromBlue) {
        int red = fromRed.getValue();
        int green = fromGreen.getValue();
        int blue = fromBlue.getValue();
        int extra = red + green + blue - 100;
        if (extra != 0) {
            fromRed.setValue(red - extra / 3);
            fromGreen.setValue(green - extra / 3);
            fromBlue.setValue(blue - extra / 3);
        }
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        boolean packedInt = ImageUtils.hasPackedIntArray(src);

        if (packedInt) {
            DataBufferInt srcDataBuffer = (DataBufferInt) src.getRaster().getDataBuffer();
            int[] srcData = srcDataBuffer.getData();

            DataBufferInt destDataBuffer = (DataBufferInt) dest.getRaster().getDataBuffer();
            int[] destData = destDataBuffer.getData();

            int length = srcData.length;
            if (length != destData.length) {
                throw new IllegalArgumentException("src and dest are not the same size");
            }


            float redFromRed = rpRedFromRed.getValueAsPercentage();
            float redFromGreen = rpRedFromGreen.getValueAsPercentage();
            float redFromBlue = rpRedFromBlue.getValueAsPercentage();

            float greenFromRed = rpGreenFromRed.getValueAsPercentage();
            float greenFromGreen = rpGreenFromGreen.getValueAsPercentage();
            float greenFromBlue = rpGreenFromBlue.getValueAsPercentage();

            float blueFromRed = rpBlueFromRed.getValueAsPercentage();
            float blueFromGreen = rpBlueFromGreen.getValueAsPercentage();
            float blueFromBlue = rpBlueFromBlue.getValueAsPercentage();

            for (int i = 0; i < length; i++) {
                int rgb = srcData[i];
                int a = rgb & 0xFF000000;
                int r = (rgb >>> 16) & 0xFF;
                int g = (rgb >>> 8) & 0xFF;
                int b = (rgb) & 0xFF;

                int newRed = (int) (redFromRed * r + redFromGreen * g + redFromBlue * b);
                int newGreen = (int) (greenFromRed * r + greenFromGreen * g + greenFromBlue * b);
                int newBlue = (int) (blueFromRed * r + blueFromGreen * g + blueFromBlue * b);

                newRed = ImageUtils.limitTo8Bits(newRed);
                newGreen = ImageUtils.limitTo8Bits(newGreen);
                newBlue = ImageUtils.limitTo8Bits(newBlue);

                destData[i] = a | (newRed << 16) | (newGreen << 8) | newBlue;
            }
        } else {
            BandCombineOp bandCombineOp = new BandCombineOp(new float[][]{
                    {rpRedFromRed.getValueAsPercentage(), rpRedFromGreen.getValueAsPercentage(), rpRedFromBlue.getValueAsPercentage()},
                    {rpGreenFromRed.getValueAsPercentage(), rpGreenFromGreen.getValueAsPercentage(), rpGreenFromBlue.getValueAsPercentage()},
                    {rpBlueFromRed.getValueAsPercentage(), rpBlueFromGreen.getValueAsPercentage(), rpBlueFromBlue.getValueAsPercentage()}
            }, null);
            Raster srcRaster = src.getRaster();
            Raster destRaster = dest.getRaster();
            bandCombineOp.filter(srcRaster, (WritableRaster) destRaster);
        }

        return dest;
    }

    @Override
    public AdjustPanel getAdjustPanel() {
        return new ChannelMixerAdjustments(this, actions);
    }

}