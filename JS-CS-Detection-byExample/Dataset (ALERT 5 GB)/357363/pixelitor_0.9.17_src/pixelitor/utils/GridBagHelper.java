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
package pixelitor.utils;

import javax.swing.*;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.Insets;

/**
 * Static and not thread-safe methods to help with GridBagLayout
 */
public final class GridBagHelper {
    private static final Insets insets = new Insets(2, 2, 2, 2);
    private static GridBagConstraints labelConstraint = new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0, GridBagConstraints.EAST, GridBagConstraints.NONE, insets, 0, 0);
    private static GridBagConstraints nextControlConstraint = new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0);
    private static GridBagConstraints nextLastControlConstraint = new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, 1, 0.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0);

    private GridBagHelper() {
    } // should not be instantiated

    public static void addLabel(Container container, String labelText, int gridX, int gridY) {
        JLabel label = new JLabel(labelText, SwingConstants.RIGHT);
        addLabel(container, label, gridX, gridY);
    }

    public static void addLabel(Container container, JLabel label, int gridX, int gridY) {
        labelConstraint.gridx = gridX;
        labelConstraint.gridy = gridY;
        container.add(label, labelConstraint);
    }

    /**
     * Adds the specified control to the right of the last label
     */
    public static void addControl(Container container, Component component) {
        nextControlConstraint.gridx = labelConstraint.gridx + 1;
        nextControlConstraint.gridy = labelConstraint.gridy;
        container.add(component, nextControlConstraint);
    }

    /**
     * Adds the specified control to the right of the last label without stretching
     */
    public static void addControlNoFill(Container container, Component component) {
        nextControlConstraint.gridx = labelConstraint.gridx + 1;
        nextControlConstraint.gridy = labelConstraint.gridy;
        nextControlConstraint.fill = GridBagConstraints.NONE;
        container.add(component, nextControlConstraint);

        nextControlConstraint.fill = GridBagConstraints.HORIZONTAL; // reset
    }


    /**
     * Adds the specified control to the right of the last control
     */
    public static void addNextControl(Container container, Component component) {
        nextControlConstraint.gridx++;
        container.add(component, nextControlConstraint);
    }

    public static void addLastControl(Container container, Component component) {
        nextLastControlConstraint.gridx = labelConstraint.gridx + 1;
        nextLastControlConstraint.gridy = labelConstraint.gridy;
        container.add(component, nextLastControlConstraint);
    }

    public static void addOnlyControlToRow(Container container, Component component, int row) {
        nextLastControlConstraint.gridx = 0;
        nextLastControlConstraint.gridy = row;
        container.add(component, nextLastControlConstraint);
    }
}
