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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 */
public class BooleanParam implements GUIParam {
    private ParamAdjustingListener adjustingListener;
    private String name;
    private boolean defaultValue;
    private boolean currentValue;
    private ParamGUI paramGUI;
    private boolean resetting = false;

    public BooleanParam(String name, boolean defaultValue) {
        this.name = name + ":";
        this.defaultValue = defaultValue;
        currentValue = defaultValue;
    }

    @Override
    public String getName() {
        return name;
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
        if(!triggerAction) {
            resetting = true;
        }
        setValue(defaultValue, true);
        resetting = false;
    }


    @Override
    public void setAdjustingListener(ParamAdjustingListener listener) {
        this.adjustingListener = listener;
    }

    @Override
    public int getNrOfGUIWidgets() {
        return 2;
    }

    @Override
    public void randomize() {
        resetting = true;
        setValue(Math.random() > 0.5, true);
        resetting = false;
    }

    public boolean getValue() {
        return currentValue;
    }

    public void setValue(boolean newValue, boolean updateGUI) {
        if(currentValue != newValue) {
            currentValue = newValue;
            if(!resetting) {
                adjustingListener.paramAdjusted();
            }
        }
        if(updateGUI && (paramGUI != null)) {
            paramGUI.updateGUI();
        }
    }
}
