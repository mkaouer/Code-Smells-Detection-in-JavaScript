/*
 * Copyright 2009 László Balázs-Csíki
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

import java.awt.event.ActionEvent;
import java.awt.image.BandCombineOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import javax.swing.AbstractAction;
import javax.swing.Action;

import pixelitor.ImageChangeReason;
import pixelitor.utils.Utils;

public class ChannelMixer extends AbstractOperationWithDialog {
    private static final int MIN_PERCENT = -100;
    private static final int MAX_PERCENT = 100;


    private LinearIntParam lipRedFromRed = new LinearIntParam("<html><b><font color=red>Red</font></b> from <font color=red>red</font>:</html>", MIN_PERCENT, MAX_PERCENT, 100);
    private LinearIntParam lipRedFromGreen = new LinearIntParam("<html><b><font color=red>Red</font></b> from <font color=green>green</font>:</html>", MIN_PERCENT, MAX_PERCENT, 0);
    private LinearIntParam lipRedFromBlue = new LinearIntParam("<html><b><font color=red>Red</font></b> from <font color=blue>blue</font>:</html>", MIN_PERCENT, MAX_PERCENT, 0);

    private LinearIntParam lipGreenFromRed = new LinearIntParam("<html><b><font color=green>Green</font></b> from <font color=red>red</font>:</html>", MIN_PERCENT, MAX_PERCENT, 0);
    private LinearIntParam lipGreenFromGreen = new LinearIntParam("<html><b><font color=green>Green</font></b> from <font color=green>green</font>:</html>", MIN_PERCENT, MAX_PERCENT, 100);
    private LinearIntParam lipGreenFromBlue = new LinearIntParam("<html><b><font color=green>Green</font></b> from <font color=blue>blue</font>:</html>", MIN_PERCENT, MAX_PERCENT, 0);

    private LinearIntParam lipBlueFromRed = new LinearIntParam("<html><b><font color=blue>Blue</font></b> from <font color=red>red</font>:</html>", MIN_PERCENT, MAX_PERCENT, 0);
    private LinearIntParam lipBlueFromGreen = new LinearIntParam("<html><b><font color=blue>Blue</font></b> from <font color=green>green</font>:</html>", MIN_PERCENT, MAX_PERCENT, 0);
    private LinearIntParam lipBlueFromBlue = new LinearIntParam("<html><b><font color=blue>Blue</font></b> from <font color=blue>blue</font>:</html>", MIN_PERCENT, MAX_PERCENT, 100);

    private LinearIntParam[] params = new LinearIntParam[]{
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
    private ParamSet paramSet = new ParamSet(params);


    private Action resetAction = new AbstractAction("reset") {
        @Override
        public void actionPerformed(ActionEvent e) {
            startPresetAdjusting();

            lipRedFromRed.setValue(100);
            lipRedFromGreen.setValue(0);
            lipRedFromBlue.setValue(0);

            lipGreenFromRed.setValue(0);
            lipGreenFromGreen.setValue(100);
            lipGreenFromBlue.setValue(0);

            lipBlueFromRed.setValue(0);
            lipBlueFromGreen.setValue(0);
            lipBlueFromBlue.setValue(100);

            endPresetAdjusting();
        }
    };

    private Action switchRedGreen = new AbstractAction("switch red-green") {
        @Override
        public void actionPerformed(ActionEvent e) {
            startPresetAdjusting();

            lipRedFromRed.setValue(0);
            lipRedFromGreen.setValue(100);
            lipRedFromBlue.setValue(0);

            lipGreenFromRed.setValue(100);
            lipGreenFromGreen.setValue(0);
            lipGreenFromBlue.setValue(0);

            lipBlueFromRed.setValue(0);
            lipBlueFromGreen.setValue(0);
            lipBlueFromBlue.setValue(100);

            endPresetAdjusting();
        }
    };

    private Action switchRedBlue = new AbstractAction("switch red-blue") {
        @Override
        public void actionPerformed(ActionEvent e) {
            startPresetAdjusting();

            lipRedFromRed.setValue(0);
            lipRedFromGreen.setValue(0);
            lipRedFromBlue.setValue(100);

            lipGreenFromRed.setValue(0);
            lipGreenFromGreen.setValue(100);
            lipGreenFromBlue.setValue(0);

            lipBlueFromRed.setValue(100);
            lipBlueFromGreen.setValue(0);
            lipBlueFromBlue.setValue(0);

            endPresetAdjusting();
        }
    };

    private Action switchGreenBlue = new AbstractAction("switch green-blue") {
        @Override
        public void actionPerformed(ActionEvent e) {
            startPresetAdjusting();

            lipRedFromRed.setValue(100);
            lipRedFromGreen.setValue(0);
            lipRedFromBlue.setValue(0);

            lipGreenFromRed.setValue(0);
            lipGreenFromGreen.setValue(0);
            lipGreenFromBlue.setValue(100);

            lipBlueFromRed.setValue(0);
            lipBlueFromGreen.setValue(100);
            lipBlueFromBlue.setValue(0);

            endPresetAdjusting();
        }
    };

    private Action averageBW = new AbstractAction("average BW") {
        @Override
        public void actionPerformed(ActionEvent e) {
            startPresetAdjusting();

            lipRedFromRed.setValue(33);
            lipRedFromGreen.setValue(33);
            lipRedFromBlue.setValue(33);

            lipGreenFromRed.setValue(33);
            lipGreenFromGreen.setValue(33);
            lipGreenFromBlue.setValue(33);

            lipBlueFromRed.setValue(33);
            lipBlueFromGreen.setValue(33);
            lipBlueFromBlue.setValue(33);

            endPresetAdjusting();
        }
    };


    private Action[] actions = new Action[]{ resetAction, switchRedGreen, switchRedBlue, switchGreenBlue, averageBW};

    public ChannelMixer() {
        super("Channel Mixer");
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest, ImageChangeReason changeReason) {
        System.out.println("ChannelMixer.transform CALLED");
//        Thread.dumpStack();
        boolean packedInt = Utils.hasPackedIntArray(src);

        if (packedInt) {
            // this way is 7 times fater than with BandCombineOp

            DataBufferInt srcDataBuffer = (DataBufferInt) src.getRaster().getDataBuffer();
            int[] srcData = srcDataBuffer.getData();

            DataBufferInt destDataBuffer = (DataBufferInt) dest.getRaster().getDataBuffer();
            int[] destData = destDataBuffer.getData();

            int length = srcData.length;
            if (length != destData.length) {
                throw new IllegalArgumentException("src and dest are not the same size");
            }


            float redFromRed = lipRedFromRed.getValueAsFloat();
            float redFromGreen = lipRedFromGreen.getValueAsFloat();
            float redFromBlue = lipRedFromBlue.getValueAsFloat();

            float greenFromRed = lipGreenFromRed.getValueAsFloat();
            float greenFromGreen = lipGreenFromGreen.getValueAsFloat();
            float greenFromBlue = lipGreenFromBlue.getValueAsFloat();

            float blueFromRed = lipBlueFromRed.getValueAsFloat();
            float blueFromGreen = lipBlueFromGreen.getValueAsFloat();
            float blueFromBlue = lipBlueFromBlue.getValueAsFloat();

            for (int i = 0; i < length; i++) {
                int rgb = srcData[i];
                int a = (rgb >>> 24) & 0xFF;
                int r = (rgb >>> 16) & 0xFF;
                int g = (rgb >>> 8) & 0xFF;
                int b = (rgb) & 0xFF;

                int newRed = (int) (redFromRed * r + redFromGreen * g + redFromBlue * b);
                int newGreen = (int) (greenFromRed * r + greenFromGreen * g + greenFromBlue * b);
                int newBlue = (int) (blueFromRed * r + blueFromGreen * g + blueFromBlue * b);

//                rgb = (a << 24) | (r << 16) | (g << 8) | b;
                rgb = (a << 24) | (newRed << 16) | (newGreen << 8) | newBlue;
                destData[i] = rgb;
            }
        } else {
            BandCombineOp bandCombineOp = new BandCombineOp(new float[][]{
                    {lipRedFromRed.getValueAsFloat(), lipRedFromGreen.getValueAsFloat(), lipRedFromBlue.getValueAsFloat()},
                    {lipGreenFromRed.getValueAsFloat(), lipGreenFromGreen.getValueAsFloat(), lipGreenFromBlue.getValueAsFloat()},
                    {lipBlueFromRed.getValueAsFloat(), lipBlueFromGreen.getValueAsFloat(),lipBlueFromBlue.getValueAsFloat()}
            }, null);
            Raster srcRaster = src.getRaster();
            Raster destRaster = dest.getRaster();
            bandCombineOp.filter(srcRaster, (WritableRaster) destRaster);
        }

        return dest;
    }

    @Override
    public AdjustPanel getAdjustPanel() {
        if(adjustPanel == null) {
            adjustPanel =  new ChannelMixerAdjustments(this, actions);
        } else {
            adjustPanel.setRunFiltersIfStateChanged(false);
            paramSet.reset();
            adjustPanel.setRunFiltersIfStateChanged(true);
        }

        return adjustPanel;
    }

    @Override
    public ParamSet getParams() {
        return paramSet;
    }
}