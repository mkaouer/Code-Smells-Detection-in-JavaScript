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
package pixelitor.operations.gui;

import pixelitor.utils.SliderSpinner;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import java.util.Random;

/**
 * Represents an integer value with a minimum, a maximum and a default
 */
public class RangeParam extends AbstractGUIParam implements BoundedRangeModel {
    private int minValue;
    private int maxValue;
    private int defaultValue;
    private int value;
    private boolean adjusting;
    private boolean addDefaultButtons;
    private SliderSpinner.TextPosition textPosition;
    private ParamAdjustingListener adjustingListener;

    private transient ChangeEvent changeEvent = null;
    private EventListenerList listenerList = new EventListenerList();

    private boolean dontTrigger = false;

    public RangeParam(String name, int minValue, int maxValue, int defaultValue) {
        this(name, minValue, maxValue, defaultValue, true, SliderSpinner.TextPosition.BORDER);
    }

    public RangeParam(String name, int minValue, int maxValue, int defaultValue, boolean addDefaultButtons, SliderSpinner.TextPosition position) {
        super(name);
        if (minValue > maxValue) {
            throw new IllegalArgumentException("minValue > maxValue");
        }
        if (defaultValue < minValue) {
            throw new IllegalArgumentException("defaultValue < minValue");
        }
        if (defaultValue > maxValue) {
            throw new IllegalArgumentException("defaultValue > maxValue");
        }

        this.minValue = minValue;
        this.maxValue = maxValue;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.addDefaultButtons = addDefaultButtons;
        this.textPosition = position;
    }

    @Override
    public boolean isSetToDefault() {
        return (getValue() == defaultValue);
    }

    public int getDefaultValue() {
        return defaultValue;
    }

    @Override
    public void reset(boolean triggerAction) {
        if (!triggerAction) {
            dontTrigger = true;
        }
        setValue(defaultValue);
        dontTrigger = false;
    }

    /**
     * This class can be used to manage non-integer values by multiplying them with 100
     */
    public float getValueAsPercentage() {
        return ((float) getValue()) / 100.0f;
    }

    /**
     * Int values measured in grades are transformed to radians
     */
    public float getValueInRadians() {
        return (float) Math.toRadians(getValue());
    }

    @Override
    public void setAdjustingListener(ParamAdjustingListener listener) {
        adjustingListener = listener;
    }

    @Override
    public int getNrOfGridBagCols() {
        if (textPosition == SliderSpinner.TextPosition.NONE) {
            return 2;
        }
        return 1;
    }

    @Override
    public void randomize() {
        int range = maxValue - minValue;
        Random rnd = new Random();
        int newValue = minValue + rnd.nextInt(range);
        dontTrigger = true;
        setValue(newValue);
        dontTrigger = false;
    }

    @Override
    public JComponent createControl() {
        SliderSpinner sliderSpinner = new SliderSpinner(this, addDefaultButtons, textPosition);
        return sliderSpinner;
    }

    public void increaseValue() {
        setValue(getValue() + 1);
    }

    public void decreaseValue() {
        setValue(getValue() - 1);
    }

    @Override
    public int getMinimum() {
        return minValue;
    }

    @Override
    public void setMinimum(int newMinimum) {
        minValue = newMinimum;
    }

    @Override
    public int getMaximum() {
        return maxValue;
    }

    @Override
    public void setMaximum(int newMaximum) {
        maxValue = newMaximum;
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public void setValue(int n) {
        if (value != n) {
            value = n;
            fireStateChanged();
            if (!adjusting) {
                if (!dontTrigger) {
                    if (adjustingListener != null) {
                        adjustingListener.paramAdjusted();
                    }
                }
            }
        }
    }

    @Override
    public void setValueIsAdjusting(boolean b) {
        if (!b) {
            if (adjusting) {
                if (adjustingListener != null) {
                    adjustingListener.paramAdjusted();
                }
            }
        }
        if (adjusting != b) {
            adjusting = b;
            fireStateChanged();
        }
    }

    @Override
    public boolean getValueIsAdjusting() {
        return adjusting;
    }

    @Override
    public int getExtent() {
        return 0;
    }

    @Override
    public void setExtent(int newExtent) {
        // not used
    }

    @Override
    public void setRangeProperties(int value, int extent, int min, int max, boolean adjusting) {
        // not used
    }

    @Override
    public void addChangeListener(ChangeListener x) {
        listenerList.add(ChangeListener.class, x);
    }

    @Override
    public void removeChangeListener(ChangeListener x) {
        listenerList.remove(ChangeListener.class, x);
    }

    private void fireStateChanged() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ChangeListener.class) {
                if (changeEvent == null) {
                    changeEvent = new ChangeEvent(this);
                }
                ((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
            }
        }
    }

    @Override
    public void setDontTrigger(boolean b) {
        dontTrigger = b;
    }

}
