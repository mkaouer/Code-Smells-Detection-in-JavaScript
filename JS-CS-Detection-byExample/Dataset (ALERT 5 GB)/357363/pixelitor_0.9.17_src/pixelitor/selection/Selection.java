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

package pixelitor.selection;

import pixelitor.Build;
import pixelitor.Composition;
import pixelitor.ExceptionHandler;
import pixelitor.tools.UserDrag;

import javax.swing.*;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Path2D;

public class Selection {
    private float dashPhase;
    private Component component;
    private Timer timer;

    private Shape currentSelectionShape;
    private Shape lastSelectionShape;

    private SelectionType selectionType;
    private SelectionInteraction selectionInteraction;

    private static final float[] MARCHING_ANTS_DASH = new float[]{4, 4};


    enum State {
        NO_SHAPE_YET, HAS_SHAPE, DIED
    }

    private State state;

    /**
     * Called when a new selection is created with the marquee selection tool
     */
    public Selection(Component c, SelectionType selectionType, SelectionInteraction selectionInteraction) {
        this.component = c;
        this.selectionType = selectionType;
        this.selectionInteraction = selectionInteraction;

        state = State.NO_SHAPE_YET;
    }

//    /**
//     * Called when a new selection is created with the lasso selection tool
//     */
//    public Selection(Component c, LassoSelectionType lassoSelectionType, SelectionInteraction selectionInteraction) {
//        this.component = c;
//
//        this.selectionInteraction = selectionInteraction;
//
//        state = State.NO_SHAPE_YET;
//    }


    /**
     * Called when a deselect is undone (and when a  new selection is undone and then redone)
     */
    public Selection(Shape shape, Component c) {
        this.currentSelectionShape = shape;
        this.component = c;

        // selectionType is not set, but this shouldn't be a problem

        startMarching();
        state = State.HAS_SHAPE;
    }

    public final void updateSelection(UserDrag userDrag) {
        if (selectionType == null) { // TODO should not happen but happened during robot tests
            return;
        }

        currentSelectionShape = selectionType.updateShape(userDrag, currentSelectionShape);

        if (state == State.NO_SHAPE_YET) {
            startMarching();
            state = State.HAS_SHAPE;
        }
    }

    public void startNewShape(SelectionType selectionType, SelectionInteraction selectionInteraction) {
        if (state != State.HAS_SHAPE) {
            throw new IllegalStateException("state = " + state);
        }

        this.selectionType = selectionType;
        this.selectionInteraction = selectionInteraction;

        if (selectionInteraction != SelectionInteraction.REPLACE) {
            lastSelectionShape = currentSelectionShape;
        }
    }

    /**
     * @return true if something is still selected
     */
    public boolean combineShapes() {
        boolean somethingSelected = true;
        if (lastSelectionShape != null) {
            currentSelectionShape = selectionInteraction.combine(lastSelectionShape, currentSelectionShape);

            Rectangle newBounds = currentSelectionShape.getBounds();
            if (newBounds.isEmpty()) {
                currentSelectionShape = null;
                updateComponent();
                if (Build.CURRENT != Build.ROBOT_TEST) {
                    ExceptionHandler.showInfoDialog("Nothing selected", "As a result of the "
                            + selectionInteraction.toString().toLowerCase() + " operation, nothing is selected now.");
                }
                somethingSelected = false;
            }

            lastSelectionShape = null;
        } else {
            // there is the current selection only but it also can be empty if it has width or height = 0
            if (currentSelectionShape.getBounds().isEmpty()) {
                somethingSelected = false;
            }
        }
        return somethingSelected;
    }

    private void startMarching() {
        timer = new Timer(100, null);
        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                dashPhase++;

                updateComponent();
            }
        });
        timer.start();
    }

    public void paintMarchingAnts(Graphics2D g2) {
        if (currentSelectionShape == null) {

//            if(lastSelectionShape != null) {
            // lastSelectionShape can be still not null if "no selection left after subtract", etc
//                throw new IllegalStateException();
//            }
            return;
        }

        paintAnts(g2, lastSelectionShape, 0);
        paintAnts(g2, currentSelectionShape, dashPhase);
    }

    private static void paintAnts(Graphics2D g2, Shape shape, float phase) {
        if (shape == null) {
            return;
        }

        g2.setPaint(Color.WHITE);
        Stroke stroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_ROUND, 0.0f, MARCHING_ANTS_DASH,
                phase);
        g2.setStroke(stroke);
        g2.draw(shape);

        g2.setPaint(Color.BLACK);
        Stroke stroke2 = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_ROUND, 0.0f, MARCHING_ANTS_DASH,
                phase + 4);
        g2.setStroke(stroke2);
        g2.draw(shape);
    }

    public void invert(Rectangle fullImage) {
        if (currentSelectionShape != null) {
            Area area = new Area(currentSelectionShape);
            Area fullArea = new Area(fullImage);
            fullArea.subtract(area);
            currentSelectionShape = fullArea;
        }
    }

    public void deselectAndDispose() {
        switch (state) {
            case NO_SHAPE_YET:
                state = State.DIED;
                break;
            case HAS_SHAPE:
                timer.stop();
                updateComponent();
                component = null;
                state = State.DIED;
                break;
            case DIED:
                throw new IllegalStateException("died twice");
        }
    }

    private void updateComponent() {
//        Rectangle selBounds = currentSelectionShape.getBounds();
//
//        if(lastSelectionShape != null) {
//            Rectangle r = lastSelectionShape.getBounds();
//            selBounds = selBounds.union(r);
//        }
//
//        component.repaint(selBounds.x, selBounds.y, selBounds.width + 1, selBounds.height + 1);

        // TODO the above optimization is not enough, the previous positions should be also considered for the
        // case when the selection is shrinking while dragging...

        component.repaint();
    }

    public void setShape(Shape selectionShape) {
        currentSelectionShape = selectionShape;
    }

    public SelectionInteraction getSelectionInteraction() {
        return selectionInteraction;
    }

    /**
     * Intersects the selection shape with the composition bounds
     *
     * @param comp
     * @return true if something is still selected
     */
    public boolean clipToCompSize(Composition comp) {
        if (currentSelectionShape != null) {
            Area compBounds = new Area(new Rectangle(0, 0, comp.getCanvasWidth(), comp.getCanvasHeight()));
            Area tmp = new Area(currentSelectionShape);
            tmp.intersect(compBounds);
            currentSelectionShape = tmp;
            updateComponent();

            return !currentSelectionShape.getBounds().isEmpty();
        }
        return false;
    }

    public Shape getShape() {
        if (currentSelectionShape == null) {
            switch (state) {
                case NO_SHAPE_YET:
                    break; // can happen for a simple click without a previous selection
                case HAS_SHAPE:
//                    throw new IllegalStateException("null shape, while in HAS_SHAPE");
                    // TODO it happens during robot tests when nothing is selected after a subtract/intersection but why?
                    break;
                case DIED:
                    throw new IllegalStateException("getShape() called, while in DIED");
            }
        }
        return currentSelectionShape;
    }

    public Shape getTransformedShape(AffineTransform at) {
        Path2D.Float pathShape = new Path2D.Float(getShape());
        pathShape.transform(at);
        return pathShape;
    }

    public Rectangle getShapeBounds() {
        if (currentSelectionShape != null) {
            return currentSelectionShape.getBounds(); // cached in Area
        }
        return null;
    }

    public void addNewPolygonalLassoPoint(UserDrag userDrag) {
        Polygon polygon = (Polygon) currentSelectionShape;
        int[] xPoints = polygon.xpoints;
        int[] yPoints = polygon.ypoints;
        int nPoints = polygon.npoints;

        int newNPoints = nPoints + 1;
        int[] newXPoints = new int[newNPoints];
        int[] newYPoints = new int[newNPoints];

        for (int i = 0; i < nPoints; i++) {
            newXPoints[i] = xPoints[i];
            newYPoints[i] = yPoints[i];
            // TODO use System.arraycopy();
        }

        newXPoints[newNPoints - 1] = userDrag.getEndX();
        newYPoints[newNPoints - 1] = userDrag.getEndY();

        currentSelectionShape = new Polygon(newXPoints, newYPoints, newNPoints);
    }


    @Override
    public String toString() {
        return "Selection{" +
                "component=" + component.getName() +
                ", currentSelectionShape=" + currentSelectionShape +
                ", currentSelectionShapeBounds=" + currentSelectionShape == null ? "null" : currentSelectionShape.getBounds()+
                ", lastSelectionShape=" + lastSelectionShape +
                ", selectionType=" + selectionType +
                ", selectionInteraction=" + selectionInteraction +
                ", state=" + state +
                '}';
    }
}
