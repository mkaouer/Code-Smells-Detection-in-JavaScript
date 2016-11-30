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

package pixelitor.operations.convolve;

import com.jhlabs.image.ConvolveFilter;
import org.jdesktop.swingx.combobox.EnumComboBoxModel;
import pixelitor.operations.gui.AdjustPanel;
import pixelitor.operations.gui.OperationWithGUI;
import pixelitor.utils.GUIUtils;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class Convolve3x3 extends OperationWithGUI {
    enum ConvolveMethod {
        JHLabs {
            @Override
            BufferedImageOp getConvolveOp(Kernel kernel) {
                ConvolveFilter filter = new ConvolveFilter(kernel);
                filter.setEdgeAction(ConvolveFilter.CLAMP_EDGES);
                filter.setPremultiplyAlpha(false);
                filter.setUseAlpha(false);
                return filter;
            }
            @Override
            public String toString() {
                return "JHLabs ConvolveFilter (Better)";
            }
        }, AWT {
            @Override
            BufferedImageOp getConvolveOp(Kernel kernel) {
                ConvolveOp op = new ConvolveOp(kernel);
                return op;
            }
            @Override
            public String toString
                    () {
                return "AWT ConvolveOp (Faster)";
            }
        };

        abstract BufferedImageOp getConvolveOp(Kernel kernel);
    }

    private EnumComboBoxModel<ConvolveMethod> convolveMethodModel = new EnumComboBoxModel<ConvolveMethod>(ConvolveMethod.class);

    private float[] kernelMatrix;

    public Convolve3x3() {
        super("Custom 3x3 Convolution");
    }

    public void setKernelMatrix(float[] kernelMatrix) {
        this.kernelMatrix = kernelMatrix;
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        Kernel kernel = new Kernel(3, 3, kernelMatrix);
        BufferedImageOp convolveOp = convolveMethodModel.getSelectedItem().getConvolveOp(kernel);
        try {
            convolveOp.filter(src, dest);
        } catch (java.awt.image.ImagingOpException e) {
            GUIUtils.showExceptionDialog(null, e);
        }

        return dest;
    }

    @Override
    public AdjustPanel getAdjustPanel() {
        return new CustomConvolve3x3Adjustments(this);
    }

    public EnumComboBoxModel<ConvolveMethod> getConvolveMethodModel() {
        return convolveMethodModel;
    }

    @Override
    public void randomizeSettings() {
        kernelMatrix = new float[]{
                0, 0, 0,
                0, 1, 0,
                0, 0, 0
        };
    }
}
