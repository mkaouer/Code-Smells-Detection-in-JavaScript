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
package pixelitor.tools;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.*;

/**
 * An enum-like abstract class for the tools. For each tool there is a subclass.
 */
public abstract class Tool {
    public static final CropSelectionTool CROP_SELECTION = new CropSelectionTool();
    public static final Tool GRADIENT = new GradientTool();
    public static final Tool DRAW = new DrawTool();

    private static Tool currentTool = CROP_SELECTION;


    private static final boolean MOUSEDEBUG = false;

    /**
     * All the subclass tools in an array.
     */
    private static Tool[] allTools = new Tool[]{ CROP_SELECTION, GRADIENT, DRAW };

    public static Tool[] values() {
        return allTools;
    }


    protected Point start = new Point(0, 0);
    protected Point end = new Point(0, 0);

    protected Tool() {
    }

    public static Tool getCurrentTool() {
        return currentTool;
    }

    public static void setCurrentTool(Tool currentTool) {
        Tool.currentTool.toolEnded();
        Tool.currentTool = currentTool;
        Tool.currentTool.toolStarted();
        ToolSettingsPanel.INSTANCE.showSettingsFor(currentTool);
    }

    public abstract String getName();

    public void paintOnImage(Graphics2D g) {
    }

    public void mouseClicked(MouseEvent e, JComponent c) {
        if(MOUSEDEBUG) {
            System.out.println("Tool.mouseClicked CALLED");
        }
    }

    public void mouseEntered(MouseEvent e, JComponent c) {
        if(MOUSEDEBUG) {
            System.out.println("Tool.mouseEntered CALLED");
        }
    }

    public void mouseExited(MouseEvent e, JComponent c) {
        if(MOUSEDEBUG) {
            System.out.println("Tool.mouseExited CALLED");
        }
    }

    public void mousePressed(MouseEvent e, JComponent c) {
        if(MOUSEDEBUG) {
            System.out.println("Tool.mousePressed CALLED");
        }
        start = new Point(e.getX(), e.getY());
    }

    public void mouseReleased(MouseEvent e, JComponent c) {
        if(MOUSEDEBUG) {
            System.out.println("Tool.mouseReleased CALLED");
        }
        end = new Point(e.getX(), e.getY());
    }

    public void mouseDragged(MouseEvent e, JComponent c) {
        if(MOUSEDEBUG) {
            System.out.println("Tool.mouseDragged CALLED");
        }
        end = new Point(e.getX(), e.getY());
    }

    public void mouseMoved(MouseEvent e, JComponent c) {
        if(MOUSEDEBUG) {
//            System.out.println("Tool.mouseMoved CALLED");
        }
    }

    public void toolStarted() {
//        System.out.println("Tool.toolStarted CALLED c = " + getClass().getName());
    }

    public void toolEnded() {
//        System.out.println("Tool.toolEnded CALLED c = " + getClass().getName());
    }

    abstract void initSettingsPanel(JPanel p);
}
