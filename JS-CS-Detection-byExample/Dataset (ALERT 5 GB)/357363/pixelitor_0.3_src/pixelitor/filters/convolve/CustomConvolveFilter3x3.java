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

package pixelitor.filters.convolve;

import java.awt.image.Kernel;
import java.awt.image.BufferedImage;

import pixelitor.filters.AdjustPanel;
import pixelitor.filters.AbstractOperationWithDialog;
import pixelitor.filters.Operations;
import pixelitor.filters.ParamSet;
import pixelitor.ImageChangeReason;

public class CustomConvolveFilter3x3 extends AbstractOperationWithDialog {
	private float[] kernelMatrix;

    public CustomConvolveFilter3x3() {
        super("Custom 3x3 Convolution");
    }

    public void setKernelMatrix(float[] kernelMatrix) {
		this.kernelMatrix = kernelMatrix;
	}


    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest, ImageChangeReason changeReason) {
        return Operations.convolve(new Kernel(3, 3, kernelMatrix), src, dest);
    }

    @Override
    public ParamSet getParams() {
        // TODO this method is not used currently, because this filter has a custom panel
        return null;
    }

    @Override
	public AdjustPanel getAdjustPanel() {
		return new CustomConvolve3x3Adjustments(this);
	}
}
