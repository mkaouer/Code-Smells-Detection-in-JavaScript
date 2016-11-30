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
package pixelitor.utils.test;

import pixelitor.AppLogic;
import pixelitor.ImageComponent;
import pixelitor.NewImage;
import pixelitor.tools.AbstractBrushTool;
import pixelitor.tools.BrushTool;
import pixelitor.tools.EraseTool;
import pixelitor.tools.GradientTool;
import pixelitor.tools.MoveTool;
import pixelitor.tools.ShapeType;
import pixelitor.tools.ShapesTool;
import pixelitor.tools.Tool;

import java.awt.AlphaComposite;
import java.awt.MultipleGradientPaint;
import java.awt.Point;

/**
 *
 */
public class ToolTests {
    public static void testTools() {
        NewImage.addNewImage(NewImage.BgFill.WHITE, 400, 400, "Tool Tests");

        ImageComponent ic = AppLogic.getActiveImageComponent();

        addRadialBWGradientToActiveLayer(ic);

        int xDistanceFormEdge = 20;
        int yDistanceFormEdge = 20;

        // erase diagonally
        paintDiagonals(Tool.ERASE, ic, xDistanceFormEdge, yDistanceFormEdge);

        // paint a frame
        paintImageFrame(Tool.BRUSH, ic, xDistanceFormEdge, yDistanceFormEdge);

        paintHeartShape(ic);

        MoveTool moveTool =  Tool.MOVE;
        moveTool.move(ic, 40, 40);

        ic.repaint();
    }

    public static void paintHeartShape(ImageComponent ic) {
        ShapesTool shapesTool = Tool.SHAPES;
        int canvasWidth = ic.getCanvasWidth();
        int canvasHeight = ic.getCanvasHeight();
        Point shapeStart = new Point((int)(canvasWidth * 0.25), (int) (canvasHeight * 0.25));
        Point shapeEnd = new Point((int)(canvasWidth * 0.75), (int) (canvasHeight * 0.75));
        shapesTool.setShapeType(ShapeType.HEART);
        shapesTool.paintShapeOnIC(ic, shapeStart, shapeEnd);
    }

    public static void paintDiagonals(AbstractBrushTool eraseTool, ImageComponent ic, int xDistanceFormEdge, int yDistanceFormEdge) {
        int canvasWidth = ic.getCanvasWidth();
        int canvasHeight = ic.getCanvasHeight();
        Point topLeft = new Point(xDistanceFormEdge, yDistanceFormEdge);
        Point topRight = new Point(canvasWidth - xDistanceFormEdge, yDistanceFormEdge);
        Point bottomRight = new Point(canvasWidth - xDistanceFormEdge, canvasHeight - yDistanceFormEdge);
        Point bottomLeft = new Point(xDistanceFormEdge, canvasHeight - yDistanceFormEdge);
        eraseTool.drawBrushStroke(ic, topLeft, bottomRight);
        eraseTool.drawBrushStroke(ic, topRight, bottomLeft);
    }

    public static void paintImageFrame(AbstractBrushTool brushTool, ImageComponent ic, int xDistanceFormEdge, int yDistanceFormEdge) {
        int canvasWidth = ic.getCanvasWidth();
        int canvasHeight = ic.getCanvasHeight();
        Point topLeft = new Point(xDistanceFormEdge, yDistanceFormEdge);
        Point topRight = new Point(canvasWidth - xDistanceFormEdge, yDistanceFormEdge);
        Point bottomRight = new Point(canvasWidth - xDistanceFormEdge, canvasHeight - yDistanceFormEdge);
        Point bottomLeft = new Point(xDistanceFormEdge, canvasHeight - yDistanceFormEdge);
        brushTool.drawBrushStroke(ic, topLeft, topRight);
        brushTool.drawBrushStroke(ic, topRight, bottomRight);
        brushTool.drawBrushStroke(ic, bottomRight, bottomLeft);
        brushTool.drawBrushStroke(ic, bottomLeft, topLeft);
    }

    public static void addRadialBWGradientToActiveLayer(ImageComponent ic) {
        int canvasWidth = ic.getCanvasWidth();
        int canvasHeight = ic.getCanvasHeight();


//        gradient.setType(Gradient.Type.RADIAL);
//        gradient.setCycleMethod(MultipleGradientPaint.CycleMethod.REFLECT);
//        gradient.setColorType(Gradient.ColorType.BLACK_TO_WHITE);
        int startX = canvasWidth / 2;
        int startY = canvasHeight / 2;

        int endX = 0;
        int endY = 0;
        if (canvasWidth > canvasHeight) {
            endX = startX;
        } else {
            endY = startY;
        }

        GradientTool.drawGradient(ic.getActiveLayer(),
                GradientTool.Type.RADIAL,
                GradientTool.ColorType.BLACK_TO_WHITE,
                MultipleGradientPaint.CycleMethod.REFLECT,
                AlphaComposite.SrcOver,
                new Point(startX, startY),
                new Point(endX, endY),
                false);

//        gradient.setPoints(startX, startY, endX, endY);
//        gradient.actionPerformed(null);
    }
}
