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
package pixelitor.operations.painters;

import org.jdesktop.swingx.painter.PinstripePainter;
import pixelitor.operations.OperationWithParametrizedGUI;
import pixelitor.operations.gui.AngleParam;
import pixelitor.operations.gui.ColorParam;
import pixelitor.operations.gui.ParamSet;
import pixelitor.operations.gui.RangeParam;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 *
 */
public class Pinstripes extends OperationWithParametrizedGUI {
    private AngleParam angle = new AngleParam("Angle", 0);
    private RangeParam spacing = new RangeParam("Spacing", 1, 100, 5);
    private RangeParam stripeWidth = new RangeParam("Stripe Width", 1, 100, 1);
    private ColorParam color = new ColorParam("Color:", Color.WHITE);

    public Pinstripes() {
        super("Pinstripes", true);
        paramSet = new ParamSet(
                color,
                angle,
                spacing,
                stripeWidth
        );
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        Graphics2D g = dest.createGraphics();

        PinstripePainter psp = new PinstripePainter();

        int degrees = angle.getValueInNonIntuitiveDegrees();

        psp.setAngle(degrees);
        psp.setPaint(color.getColor());
        psp.setSpacing(spacing.getValue());
        psp.setStripeWidth(stripeWidth.getValue());


        psp.paint(g, this, dest.getWidth(), dest.getHeight());

        g.dispose();
        return dest;
    }
}
