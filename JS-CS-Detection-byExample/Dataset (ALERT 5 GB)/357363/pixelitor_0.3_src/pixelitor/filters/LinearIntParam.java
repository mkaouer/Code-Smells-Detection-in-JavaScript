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
package pixelitor.filters;

import javax.swing.DefaultBoundedRangeModel;

/**
 *
 */
public class LinearIntParam extends DefaultBoundedRangeModel  {
    private String name;
    private int defaultValue;

    public LinearIntParam(String name, int minValue, int maxValue, int defaultValue) {
        super(defaultValue, 0, minValue, maxValue);

        this.name = name;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public boolean isSetToDefault() {
        return (getValue() == defaultValue);
    }

    public int getDefaultValue() {
        return defaultValue;
    }

    public void reset() {
        setValue(defaultValue);
    }

    /**
     * this class can be used to manage non-integer values by multiplying them with 100
     */
    public float getValueAsFloat() {
        return ((float)getValue())/100f;
    }

    /**
     * int values measured in grades are transformed to radians
     */
    public float getValueInRadians() {
        return (float) Math.toRadians (getValue());
    }
}
