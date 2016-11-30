/*
 * Copyright 2010 L�szl� Bal�zs-Cs�ki
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

/**
 *
 */
public class BooleanParam extends AbstractGUIParam {
    private ParamAdjustingListener adjustingListener;
    private boolean defaultValue;
    private boolean currentValue;
    private boolean ignoreRandomize;
    private ParamGUI paramGUI;
    private boolean dontTrigger = false;


    public BooleanParam(String name, boolean defaultValue) {
        this(name, defaultValue, false);
    }

    public BooleanParam(String name, boolean defaultValue, boolean ignoreRandomize) {
        super(name);
        this.defaultValue = defaultValue;
        currentValue = defaultValue;
        this.ignoreRandomize = ignoreRandomize;
    }

    public static BooleanParam paramForHPSharpening() {
        return new BooleanParam("High-Pass Sharpening", false, true);
    }

    public static BooleanParam paramForShowOriginal() {
        return new BooleanParam("Show Original", false, true);
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

    @Override
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
        if (!ignoreRandomize) {
            dontTrigger = true;
            setValue(Math.random() > 0.5, true);
            dontTrigger = false;
        }
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
