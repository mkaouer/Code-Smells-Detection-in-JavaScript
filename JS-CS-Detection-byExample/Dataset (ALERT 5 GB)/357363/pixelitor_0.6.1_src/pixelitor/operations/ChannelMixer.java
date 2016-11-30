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

package pixelitor.operations;

import pixelitor.ImageChangeReason;
import pixelitor.operations.gui.AdjustPanel;
import pixelitor.operations.gui.ChannelMixerAdjustments;
import pixelitor.operations.gui.RangeParam;
import pixelitor.operations.gui.ParamSet;
import pixelitor.utils.ImageUtils;
import pixelitor.utils.SliderSpinner;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.image.BandCombineOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class ChannelMixer extends OperationWithParametrizedGUI {
    private static final int MIN_PERCENT = 0;
    private static final int MAX_PERCENT = 100;


    private RangeParam lipRedFromRed = new RangeParam("<html><b><font color=red>Red</font></b> from <font color=red>red</font>:</html>", MIN_PERCENT, MAX_PERCENT, 100, true, SliderSpinner.TextPosition.WEST);
    private RangeParam lipRedFromGreen = new RangeParam("<html><b><font color=red>Red</font></b> from <font color=green>green</font>:</html>", MIN_PERCENT, MAX_PERCENT, 0, true, SliderSpinner.TextPosition.WEST);
    private RangeParam lipRedFromBlue = new RangeParam("<html><b><font color=red>Red</font></b> from <font color=blue>blue</font>:</html>", MIN_PERCENT, MAX_PERCENT, 0, true, SliderSpinner.TextPosition.WEST);

    private RangeParam lipGreenFromRed = new RangeParam("<html><b><font color=green>Green</font></b> from <font color=red>red</font>:</html>", MIN_PERCENT, MAX_PERCENT, 0, true, SliderSpinner.TextPosition.WEST);
    private RangeParam lipGreenFromGreen = new RangeParam("<html><b><font color=green>Green</font></b> from <font color=green>green</font>:</html>", MIN_PERCENT, MAX_PERCENT, 100, true, SliderSpinner.TextPosition.WEST);
    private RangeParam lipGreenFromBlue = new RangeParam("<html><b><font color=green>Green</font></b> from <font color=blue>blue</font>:</html>", MIN_PERCENT, MAX_PERCENT, 0, true, SliderSpinner.TextPosition.WEST);

    private RangeParam lipBlueFromRed = new RangeParam("<html><b><font color=blue>Blue</font></b> from <font color=red>red</font>:</html>", MIN_PERCENT, MAX_PERCENT, 0, true, SliderSpinner.TextPosition.WEST);
    private RangeParam lipBlueFromGreen = new RangeParam("<html><b><font color=blue>Blue</font></b> from <font color=green>green</font>:</html>", MIN_PERCENT, MAX_PERCENT, 0, true, SliderSpinner.TextPosition.WEST);
    private RangeParam lipBlueFromBlue = new RangeParam("<html><b><font color=blue>Blue</font></b> from <font color=blue>blue</font>:</html>", MIN_PERCENT, MAX_PERCENT, 100, true, SliderSpinner.TextPosition.WEST);

    private RangeParam[] params = new RangeParam[]{
            lipRedFromRed,
            lipRedFromGreen,
            lipRedFromBlue,

            lipGreenFromRed,
            lipGreenFromGreen,
            lipGreenFromBlue,

            lipBlueFromRed,
            lipBlueFromGreen,
            lipBlueFromBlue,
    };

    private Action resetAction = new AbstractAction("reset") {
        @Override
        public void actionPerformed(ActionEvent e) {
//            startPresetAdjusting();

            lipRedFromRed.setValue(100);
            lipRedFromGreen.setValue(0);
            lipRedFromBlue.setValue(0);

            lipGreenFromRed.setValue(0);
            lipGreenFromGreen.setValue(100);
            lipGreenFromBlue.setValue(0);

            lipBlueFromRed.setValue(0);
            lipBlueFromGreen.setValue(0);
            lipBlueFromBlue.setValue(100);

//            endPresetAdjusting();
        }
    };

    private Action switchRedGreen = new AbstractAction("switch red-green") {
        @Override
        public void actionPerformed(ActionEvent e) {
//            startPresetAdjusting();

            lipRedFromRed.setValue(0);
            lipRedFromGreen.setValue(100);
            lipRedFromBlue.setValue(0);

            lipGreenFromRed.setValue(100);
            lipGreenFromGreen.setValue(0);
            lipGreenFromBlue.setValue(0);

            lipBlueFromRed.setValue(0);
            lipBlueFromGreen.setValue(0);
            lipBlueFromBlue.setValue(100);

//            endPresetAdjusting();
        }
    };

    private Action switchRedBlue = new AbstractAction("switch red-blue") {
        @Override
        public void actionPerformed(ActionEvent e) {
//            startPresetAdjusting();

            lipRedFromRed.setValue(0);
            lipRedFromGreen.setValue(0);
            lipRedFromBlue.setValue(100);

            lipGreenFromRed.setValue(0);
            lipGreenFromGreen.setValue(100);
            lipGreenFromBlue.setValue(0);

            lipBlueFromRed.setValue(100);
            lipBlueFromGreen.setValue(0);
            lipBlueFromBlue.setValue(0);

//            endPresetAdjusting();
        }
    };

    private Action switchGreenBlue = new AbstractAction("switch green-blue") {
        @Override
        public void actionPerformed(ActionEvent e) {
//            startPresetAdjusting();

            lipRedFromRed.setValue(100);
            lipRedFromGreen.setValue(0);
            lipRedFromBlue.setValue(0);

            lipGreenFromRed.setValue(0);
            lipGreenFromGreen.setValue(0);
            lipGreenFromBlue.setValue(100);

            lipBlueFromRed.setValue(0);
            lipBlueFromGreen.setValue(100);
            lipBlueFromBlue.setValue(0);

//            endPresetAdjusting();
        }
    };

    private Action averageBW = new AbstractAction("average BW") {
        @Override
        public void actionPerformed(ActionEvent e) {
//            startPresetAdjusting();

            lipRedFromRed.setValue(33);
            lipRedFromGreen.setValue(33);
            lipRedFromBlue.setValue(33);

            lipGreenFromRed.setValue(33);
            lipGreenFromGreen.setValue(33);
            lipGreenFromBlue.setValue(33);

            lipBlueFromRed.setValue(33);
            lipBlueFromGreen.setValue(33);
            lipBlueFromBlue.setValue(33);

//            endPresetAdjusting();
        }
    };


    private Action[] actions = new Action[]{resetAction, switchRedGreen, switchRedBlue, switchGreenBlue, averageBW};

    public ChannelMixer() {
        super("Channel Mixer", false);
        paramSet = new ParamSet(params);
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest, ImageChangeReason changeReason) {
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


            float redFromRed = lipRedFromRed.getValueAsPercentage();
            float redFromGreen = lipRedFromGreen.getValueAsPercentage();
            float redFromBlue = lipRedFromBlue.getValueAsPercentage();

            float greenFromRed = lipGreenFromRed.getValueAsPercentage();
            float greenFromGreen = lipGreenFromGreen.getValueAsPercentage();
            float greenFromBlue = lipGreenFromBlue.getValueAsPercentage();

            float blueFromRed = lipBlueFromRed.getValueAsPercentage();
            float blueFromGreen = lipBlueFromGreen.getValueAsPercentage();
            float blueFromBlue = lipBlueFromBlue.getValueAsPercentage();

            for (int i = 0; i < length; i++) {
                int rgb = srcData[i];
                int a = rgb & 0xFF000000;
                int r = (rgb >>> 16) & 0xFF;
                int g = (rgb >>> 8) & 0xFF;
                int b = (rgb) & 0xFF;

                int newRed = (int) (redFromRed * r + redFromGreen * g + redFromBlue * b);
                int newGreen = (int) (greenFromRed * r + greenFromGreen * g + greenFromBlue * b);
                int newBlue = (int) (blueFromRed * r + blueFromGreen * g + blueFromBlue * b);

                rgb = a | (newRed << 16) | (newGreen << 8) | newBlue;
                destData[i] = rgb;
            }
        } else {
            BandCombineOp bandCombineOp = new BandCombineOp(new float[][]{
                    {lipRedFromRed.getValueAsPercentage(), lipRedFromGreen.getValueAsPercentage(), lipRedFromBlue.getValueAsPercentage()},
                    {lipGreenFromRed.getValueAsPercentage(), lipGreenFromGreen.getValueAsPercentage(), lipGreenFromBlue.getValueAsPercentage()},
                    {lipBlueFromRed.getValueAsPercentage(), lipBlueFromGreen.getValueAsPercentage(), lipBlueFromBlue.getValueAsPercentage()}
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
//        if (adjustPanel == null) {
//            adjustPanel = new ChannelMixerAdjustments(this, actions);
//        } else {
//            adjustPanel.setRunFiltersIfStateChanged(false);
//            paramSet.reset();
//            adjustPanel.setRunFiltersIfStateChanged(true);
//        }
//
//        return adjustPanel;
    }

}