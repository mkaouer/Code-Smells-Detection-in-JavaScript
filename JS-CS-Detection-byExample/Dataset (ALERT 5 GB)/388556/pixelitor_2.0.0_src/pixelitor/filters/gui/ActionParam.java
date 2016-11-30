/*
 * Copyright 2010 Laszlo Balazs-Csiki
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
package pixelitor.filters.gui;

import javax.swing.*;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Creates a button that executes an action when pushed
 */
public class ActionParam extends AbstractGUIParam {
    private final ActionListener actionListener;
    private final Icon icon;

    public ActionParam(String name, ActionListener actionListener) {
        this(name, actionListener, null);
    }

    public ActionParam(String name, ActionListener actionListener, Icon icon) {
        super(name);
        this.actionListener = actionListener;
        this.icon = icon;
    }

    @Override
    public boolean isSetToDefault() {
        return false;
    }

    @Override
    public JComponent createGUI() {
        OrderedExecutionButton b = new OrderedExecutionButton(getName(), actionListener, adjustmentListener, icon);
        return b;
    }

    @Override
    public void reset(boolean triggerAction) {
        // do nothing
    }


    @Override
    public int getNrOfGridBagCols() {
        return 1;
    }

    @Override
    public void randomize() {
        // do nothing
    }

    /**
     * A button that executes first its ActionListener, and after then its ChangeListener
     */
    private static class OrderedExecutionButton extends JButton {

        private OrderedExecutionButton(String name, final ActionListener actionListener, final ParamAdjustmentListener adjustmentListener, Icon icon) {
            super(name);

            if (icon != null) {
                setIcon(icon);
            }

            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    actionListener.actionPerformed(e);
                    adjustmentListener.paramAdjusted();
                }
            });
        }
    }

    @Override
    public void setDontTrigger(boolean b) {
        // do nothing
    }

    @Override
    public void considerImageSize(Rectangle bounds) {
    }
}
