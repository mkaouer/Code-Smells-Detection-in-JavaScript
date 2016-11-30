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

package pixelitor.filters.convolve;

import org.jdesktop.swingx.combobox.EnumComboBoxModel;
import pixelitor.ExceptionHandler;
import pixelitor.filters.gui.AdjustPanel;
import pixelitor.filters.gui.OperationWithGUI;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.Kernel;
import java.util.Random;

public class Convolve extends OperationWithGUI {

    private EnumComboBoxModel<ConvolveMethod> convolveMethodModel = new EnumComboBoxModel<ConvolveMethod>(ConvolveMethod.class);

    private float[] kernelMatrix;
    private int size;

    public Convolve(int size) {
        super(new StringBuilder().append("Custom ").append(size).append('x').append(size).append(" Convolution").toString());
        this.size = size;
    }

    public void setKernelMatrix(float[] kernelMatrix) {
        if (kernelMatrix.length != (size * size)) {
            throw new IllegalArgumentException("kernelMatrix.length = " + kernelMatrix.length + ", size = " + size);
        }
        this.kernelMatrix = kernelMatrix;
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        Kernel kernel = new Kernel(size, size, kernelMatrix);
        BufferedImageOp convolveOp = convolveMethodModel.getSelectedItem().getConvolveOp(kernel);
        try {
            convolveOp.filter(src, dest);
        } catch (java.awt.image.ImagingOpException e) {
            ExceptionHandler.showExceptionDialog(e);
        }

        return dest;
    }

    @Override
    public AdjustPanel getAdjustPanel() {
        return new CustomConvolveAdjustments(this);
    }

    public EnumComboBoxModel<ConvolveMethod> getConvolveMethodModel() {
        return convolveMethodModel;
    }

    @Override
    public void randomizeSettings() {
        kernelMatrix = getRandomKernelMatrix(size);
    }

    /**
     * Returns a randomized array that is on average close to being normalized
     */
    public static float[] getRandomKernelMatrix(int size) {
        Random rand = new Random();
        float[] retVal = new float[size * size];
        for (int i = 0; i < retVal.length; i++) {
            int randomInt = rand.nextInt(10000);
            retVal[i] = (4 * randomInt / (10000.0f * retVal.length)) - (1.0f / retVal.length);
        }

        return retVal;
    }

    public int getSize() {
        return size;
    }
}
