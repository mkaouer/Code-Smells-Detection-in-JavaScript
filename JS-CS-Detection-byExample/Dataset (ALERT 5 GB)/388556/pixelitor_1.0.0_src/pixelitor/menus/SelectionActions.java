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

package pixelitor.menus;

import pixelitor.AppLogic;
import pixelitor.Build;
import pixelitor.Composition;
import pixelitor.ExceptionHandler;
import pixelitor.layers.Layers;
import pixelitor.selection.Selection;
import pixelitor.tools.AbstractBrushTool;
import pixelitor.tools.Tools;

import javax.swing.*;
import java.awt.Shape;
import java.awt.event.ActionEvent;

public final class SelectionActions {

    private static AbstractAction cropAction = new AbstractAction("Crop") {
        @Override
        public void actionPerformed(ActionEvent e) {
            AppLogic.cropActiveImage();
        }
    };

    private static AbstractAction deselectAction = new AbstractAction("Deselect") {
        @Override
        public void actionPerformed(ActionEvent e) {
            AppLogic.getActiveComp().deselect(true);
        }
    };

    private static AbstractAction invertSelectionAction = new AbstractAction("Invert Selection") {
        @Override
        public void actionPerformed(ActionEvent e) {
            AppLogic.getActiveComp().invertSelection();
        }
    };

    private static AbstractAction traceWithBrush = new TraceAction("Stroke with Current Brush", Tools.BRUSH);
    private static AbstractAction traceWithEraser = new TraceAction("Stroke with Current Eraser", Tools.ERASER);

    static {
        setEnabled(false);
    }

    private SelectionActions() {
    }

    public static void setEnabled(boolean b) {

        if (Build.CURRENT.isRobotTest()) {
            Composition comp = AppLogic.getActiveComp();
            if (comp != null) {
                boolean hasSelection = comp.hasSelection();
                if (hasSelection != b) {
                    throw new IllegalStateException();
                }
            }
        }

        cropAction.setEnabled(b);
        traceWithBrush.setEnabled(b);
        traceWithEraser.setEnabled(b);
        deselectAction.setEnabled(b);
        invertSelectionAction.setEnabled(b);
    }

    public static boolean areEnabled() {
        return cropAction.isEnabled();
    }

    public static AbstractAction getCropAction() {
        return cropAction;
    }

    public static AbstractAction getTraceWithBrush() {
        return traceWithBrush;
    }

    public static AbstractAction getTraceWithEraser() {
        return traceWithEraser;
    }

    public static AbstractAction getDeselectAction() {
        return deselectAction;
    }

    public static AbstractAction getInvertSelectionAction() {
        return invertSelectionAction;
    }

    private static class TraceAction extends AbstractAction {
        private AbstractBrushTool brushTool;

        private TraceAction(String name, AbstractBrushTool brushTool) {
            super(name);
            this.brushTool = brushTool;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!Layers.activeIsImageLayer()) {
                ExceptionHandler.showNotImageLayerDialog();
                return;
            }

            Composition comp = AppLogic.getActiveComp();
            Selection selection = comp.getSelection();
            if (selection != null) {
                Shape shape = selection.getShape();
                if (shape != null) {
                    brushTool.trace(comp, shape);
                }
            }
        }
    }

}
