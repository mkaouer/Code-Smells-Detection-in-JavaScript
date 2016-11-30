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

/**
 *
 */
public class AngleParam implements GUIParam {
    private String name;
    double angleInRadians; // as returned form Math.atan2, this is between -PI and PI
    double defaultInRadians;
    private ParamAdjustingListener adjustingListener;

    protected transient ChangeEvent changeEvent = null;
    protected EventListenerList listenerList = new EventListenerList();

    public AngleParam(String name, double defaultValue) {
        this.name = name;
        angleInRadians = defaultValue;
        defaultInRadians = defaultValue;
    }

    public void setValueInDegrees(int d, boolean trigger) {
        int degrees = d;
        if (degrees < 0) {
            degrees = -degrees;
        } else {
            degrees = 360 - degrees;
        }
        setValueInRadians(Math.toRadians(degrees), trigger);
    }

    public void setValueInRadians(double r, boolean trigger) {
        if (angleInRadians != r) {
            angleInRadians = r;
            fireStateChanged();
        }
        if (trigger) {
            // trigger even if this angle was already set, because it was dragging, and now it is mouse up
            adjustingListener.paramAdjusted();
        }
    }

    public int getValueInDegrees() {
        int degrees = (int) Math.toDegrees(angleInRadians);
        if (degrees <= 0) {
            degrees = -degrees;
        } else {
            degrees = 360 - degrees;
        }
        return degrees;
    }

    /**
     * Returns the "Math.atan2" radians: the value between -PI and PI
     */
    public double getValueInRadians() {
        return angleInRadians;
    }

    /**
     * Returns the value in the range of 0 and 2*PI, and in the intuitive direction
     */
    public double getValueInIntuitiveRadians() {
        if (angleInRadians <= 0) {
            return -angleInRadians;
        } else {
            return Math.PI * 2 - angleInRadians;
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isSetToDefault() {
        return (angleInRadians == defaultInRadians);
    }

    @Override
    public JComponent createControl() {
        return new AngleSelector(this);
    }

    @Override
    public void reset(boolean triggerAction) {
        setValueInRadians(defaultInRadians, triggerAction);
    }

    @Override
    public void setAdjustingListener(ParamAdjustingListener listener) {
        adjustingListener = listener;
    }

    @Override
    public int getNrOfGUIWidgets() {
        return 1;
    }

    @Override
    public void randomize() {
        double r = Math.random();
        setValueInRadians((r * 2 * Math.PI - Math.PI), false);
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

    public void addChangeListener(ChangeListener x) {
        listenerList.add(ChangeListener.class, x);
    }

    public void removeChangeListener(ChangeListener x) {
        listenerList.remove(ChangeListener.class, x);
    }
}