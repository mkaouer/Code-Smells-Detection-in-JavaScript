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
import pixelitor.Composition;
import pixelitor.FillType;
import pixelitor.ImageComponent;
import pixelitor.NewImage;
import pixelitor.PixelitorWindow;
import pixelitor.layers.ImageLayer;
import pixelitor.tools.AbstractBrushTool;
import pixelitor.tools.BrushTool;
import pixelitor.tools.FgBgColorSelector;
import pixelitor.tools.GradientColorType;
import pixelitor.tools.GradientTool;
import pixelitor.tools.GradientType;
import pixelitor.tools.MoveTool;
import pixelitor.tools.ShapeType;
import pixelitor.tools.ShapesTool;
import pixelitor.tools.Tool;
import pixelitor.tools.UserDrag;

import javax.swing.*;
import java.awt.AlphaComposite;
import java.awt.MultipleGradientPaint;
import java.awt.Point;
import java.util.Random;

/**
 *
 */
public class ToolTests {

    /**
     * Utility class with static methods, must not be instantiated
     */
    private ToolTests() {
    }

    public static void testTools() {
        NewImage.addNewImage(FillType.WHITE, 400, 400, "Tool Tests");

        ImageComponent ic = AppLogic.getActiveImageComponent();
        Composition comp = ic.getComp();

        addRadialBWGradientToActiveLayer(comp);

        int xDistanceFormEdge = 20;
        int yDistanceFormEdge = 20;

        // erase diagonally
        paintDiagonals(Tool.ERASER, comp, xDistanceFormEdge, yDistanceFormEdge);

        // paint a frame
        paintImageFrame(Tool.BRUSH, comp, xDistanceFormEdge, yDistanceFormEdge);

        paintHeartShape(comp);

        MoveTool.move(comp, 40, 40);

        ic.repaint();
    }

    private static void paintHeartShape(Composition comp) {
        ShapesTool shapesTool = Tool.SHAPES;
        int canvasWidth = comp.getCanvasWidth();
        int canvasHeight = comp.getCanvasHeight();

        UserDrag userDrag = new UserDrag((int) (canvasWidth * 0.25), (int) (canvasHeight * 0.25), (int) (canvasWidth * 0.75), (int) (canvasHeight * 0.75));

        shapesTool.setShapeType(ShapeType.HEART);
        shapesTool.paintShapeOnIC(comp, userDrag);
    }

    private static void paintDiagonals(AbstractBrushTool eraseTool, Composition comp, int xDistanceFormEdge, int yDistanceFormEdge) {
        int canvasWidth = comp.getCanvasWidth();
        int canvasHeight = comp.getCanvasHeight();
        Point topLeft = new Point(xDistanceFormEdge, yDistanceFormEdge);
        Point topRight = new Point(canvasWidth - xDistanceFormEdge, yDistanceFormEdge);
        Point bottomRight = new Point(canvasWidth - xDistanceFormEdge, canvasHeight - yDistanceFormEdge);
        Point bottomLeft = new Point(xDistanceFormEdge, canvasHeight - yDistanceFormEdge);
        eraseTool.drawBrushStrokeProgrammatically(comp, topLeft, bottomRight);
        eraseTool.drawBrushStrokeProgrammatically(comp, topRight, bottomLeft);
    }

    private static void paintImageFrame(AbstractBrushTool brushTool, Composition comp, int xDistanceFormEdge, int yDistanceFormEdge) {
        int canvasWidth = comp.getCanvasWidth();
        int canvasHeight = comp.getCanvasHeight();
        Point topLeft = new Point(xDistanceFormEdge, yDistanceFormEdge);
        Point topRight = new Point(canvasWidth - xDistanceFormEdge, yDistanceFormEdge);
        Point bottomRight = new Point(canvasWidth - xDistanceFormEdge, canvasHeight - yDistanceFormEdge);
        Point bottomLeft = new Point(xDistanceFormEdge, canvasHeight - yDistanceFormEdge);
        brushTool.drawBrushStrokeProgrammatically(comp, topLeft, topRight);
        brushTool.drawBrushStrokeProgrammatically(comp, topRight, bottomRight);
        brushTool.drawBrushStrokeProgrammatically(comp, bottomRight, bottomLeft);
        brushTool.drawBrushStrokeProgrammatically(comp, bottomLeft, topLeft);
    }

    public static void addRadialBWGradientToActiveLayer(Composition comp) {
        int canvasWidth = comp.getCanvasWidth();
        int canvasHeight = comp.getCanvasHeight();


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

        GradientTool.drawGradient((ImageLayer) comp.getActiveLayer(),
                GradientType.RADIAL,
                GradientColorType.BLACK_TO_WHITE,
                MultipleGradientPaint.CycleMethod.REFLECT,
                AlphaComposite.SrcOver,
                new UserDrag(startX, startY, endX, endY),
                false);

//        gradient.setPoints(startX, startY, endX, endY);
//        gradient.actionPerformed(null);
    }

    public static void randomBrushStrokes() {

        final Composition comp = AppLogic.getActiveComp();
        final Random random = new Random();

        if (comp != null) {
            final BrushTool brushTool = Tool.BRUSH;
            final int canvasWidth = comp.getCanvasWidth();
            final int canvasHeight = comp.getCanvasHeight();

            final ProgressMonitor progressMonitor = new ProgressMonitor(PixelitorWindow.getInstance(),
                    "1001 Random Brush Strokes",
                    "", 0, 100);

            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                public Void doInBackground() {


                    final int numStrokes = 1001;
                    for (int i = 0; i < numStrokes; i++) {
                        progressMonitor.setProgress((int) ((float) i * 100 / numStrokes));
//                        progressMonitor.setNote("Creating " + fileName);
                        if (progressMonitor.isCanceled()) {
                            break;
                        }
                        FgBgColorSelector.setRandomColors();
                        Point start = new Point(random.nextInt(canvasWidth), random.nextInt(canvasHeight));
                        Point end = new Point(random.nextInt(canvasWidth), random.nextInt(canvasHeight));
                        brushTool.drawBrushStrokeProgrammatically(comp, start, end);
                    }
                    progressMonitor.close();
                    return null;
                } // end of doInBackground()
            };
            worker.execute();


        }
    }
}
