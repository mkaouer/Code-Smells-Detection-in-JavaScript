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
package pixelitor.operations.gui;

import javax.swing.*;

/**
 *
 */
public class BooleanParam extends AbstractGUIParam {
    private ParamAdjustingListener adjustingListener;
    private boolean defaultValue;
    private boolean currentValue;
    private ParamGUI paramGUI;
    private boolean dontTrigger = false;

    public BooleanParam(String name, boolean defaultValue) {
        super(name + ":");
        this.defaultValue = defaultValue;
        currentValue = defaultValue;
    }

    @Override
    public boolean isSetToDefault() {
        return (defaultValue == currentValue);
    }

    @Override
    public JComponent createControl() {
        BooleanSelector selector = new BooleanSelector(this);
        paramGUI = selector;

        return selector;
    }

    public void reset(boolean triggerAction) {
        if (!triggerAction) {
            dontTrigger = true;
        }
        setValue(defaultValue, true);
        dontTrigger = false;
    }


    @Override
    public void setAdjustingListener(ParamAdjustingListener listener) {
        this.adjustingListener = listener;
    }

    @Override
    public int getNrOfGridBagCols() {
        return 2;
    }

    @Override
    public void randomize() {
        dontTrigger = true;
        setValue(Math.random() > 0.5, true);
        dontTrigger = false;
    }

    public boolean getValue() {
        return currentValue;
    }

    public void setValue(boolean newValue, boolean updateGUI) {
        if (currentValue != newValue) {
            currentValue = newValue;
            if (!dontTrigger) {
                adjustingListener.paramAdjusted();
            }
        }
        if (updateGUI && (paramGUI != null)) {
            paramGUI.updateGUI();
        }
    }

    @Override
    public void setDontTrigger(boolean b) {
        dontTrigger = b;
    }

}
