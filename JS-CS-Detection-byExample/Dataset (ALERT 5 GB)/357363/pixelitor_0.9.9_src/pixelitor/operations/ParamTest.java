/*
 * Copyright 2010 László Balázs-Csíki
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

import pixelitor.operations.gui.ActionParam;
import pixelitor.operations.gui.AngleParam;
import pixelitor.operations.gui.BooleanParam;
import pixelitor.operations.gui.ColorParam;
import pixelitor.operations.gui.ImagePositionParam;
import pixelitor.operations.gui.IntChoiceParam;
import pixelitor.operations.gui.ParamSet;
import pixelitor.operations.gui.RangeParam;
import pixelitor.operations.gui.RangeWithColorsParam;
import pixelitor.operations.gui.TextParam;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

/**
 * ParamTest - just for testing various GUIParam implementations
 */
public class ParamTest extends OperationWithParametrizedGUI {
    private RangeParam range1 = new RangeParam("Range 1", 0, 100, 0);
    private RangeWithColorsParam range2 = new RangeWithColorsParam(Color.RED, Color.BLUE, "Range 2", 0, 100, 0);
    private ImagePositionParam center1 = new ImagePositionParam("Center 1");
    private ImagePositionParam center2 = new ImagePositionParam("Center 2");
    private IntChoiceParam edgeAction = IntChoiceParam.getEdgeActionChoices();
    private IntChoiceParam interpolation = IntChoiceParam.getInterpolationChoices();
    private ColorParam color1 = new ColorParam("Color 1", Color.WHITE);
    private ColorParam color2 = new ColorParam("Color 2", Color.WHITE);

    private ActionParam actionParam = new ActionParam("test action", new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("ParamTest.actionPerformed CALLED");
        }
    });
    private AngleParam angleParam = new AngleParam("Test Angle", 0.26f);
    private BooleanParam booleanParam = new BooleanParam("Test Boolean", true);
    private TextParam textParam = new TextParam("Test Text", "Pixelitor");

    public ParamTest() {
        super("ParamTest", true);
        paramSet = new ParamSet(
                range1,
                range2,
                center1,
                center2,
                edgeAction,
                interpolation,
                color1,
                color2,
                actionParam,
                angleParam,
                booleanParam,
                textParam
        );
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {

//        int inDegrees = angleParam.getValueInDegrees();
//        System.out.println("ParamTest.transform inDegrees = " + inDegrees);

//        boolean booleanValue = booleanParam.getValue();
//        System.out.println("ParamTest.transform booleanValue = " + booleanValue);

//        Thread.dumpStack();

        int startX = (int) (center1.getRelativeX() * src.getWidth());
        int startY = (int) (center1.getRelativeY() * src.getHeight());
        int endX = (int) (center2.getRelativeX() * src.getWidth());
        int endY = (int) (center2.getRelativeY() * src.getHeight());
        if ((startX == endX) && (startY == endY)) {
            return Operations.getDefaultBufferedImage(src);
        }

        float[] fractions = {0.0f, 1.0f};
        Color[] colors = {color1.getColor(), color2.getColor()};
        Paint gradient = new LinearGradientPaint(startX, startY, endX, endY, fractions, colors, MultipleGradientPaint.CycleMethod.NO_CYCLE);

        Graphics2D g2 = dest.createGraphics();
        g2.setPaint(gradient);
        g2.fillRect(0, 0, dest.getWidth(), dest.getHeight());

        drawCircle(startX, startY, g2);
        drawCircle(endX, endY, g2);

        g2.dispose();
        return dest;
    }

    private static void drawCircle(int x, int y, Graphics2D g) {
        g.setColor(Color.WHITE);
        int radius = 10;
        Ellipse2D.Float ellipse = new Ellipse2D.Float(x - radius, y - radius, 2 * radius, 2 * radius);
        g.fill(ellipse);
        g.setColor(Color.BLACK);
        g.draw(ellipse);
    }
}