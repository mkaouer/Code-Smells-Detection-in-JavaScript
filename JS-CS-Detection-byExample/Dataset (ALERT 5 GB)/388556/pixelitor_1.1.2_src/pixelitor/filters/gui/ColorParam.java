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

import pixelitor.utils.ImageUtils;

import javax.swing.*;
import java.awt.Color;

/**
 * A GUIParam for selecting a color
 */
public class ColorParam extends AbstractGUIParam {
    private Color defaultColor;
    private Color color;

    private ParamGUI paramGUI;
    private boolean allowOpacity = false;
    private boolean allowOpacityAtRandomize;

    public ColorParam(String name, Color defaultColor, boolean allowOpacity, boolean allowOpacityAtRandomize) {
        super(name);

        if (allowOpacityAtRandomize && !allowOpacity) {
            throw new IllegalArgumentException();
        }

        this.defaultColor = defaultColor;
        this.color = defaultColor;
        this.allowOpacity = allowOpacity;
        this.allowOpacityAtRandomize = allowOpacityAtRandomize;
    }

    @Override
    public boolean isSetToDefault() {
        return color.equals(defaultColor);
    }

    @Override
    public JComponent createGUI() {
        ColorSelector gui = new ColorSelector(this);
        paramGUI = gui;
        return gui;
    }

    @Override
    public void reset(boolean triggerAction) {
        if (!triggerAction) {
            dontTrigger = true;
        }
        setColor(defaultColor);
        dontTrigger = false;
    }

    @Override
    public int getNrOfGridBagCols() {
        return 2;
    }

    @Override
    public void randomize() {
        Color c = ImageUtils.getRandomColor(allowOpacityAtRandomize);
        dontTrigger = true;
        setColor(c);
        dontTrigger = false;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color newColor) {
        if (newColor == null) {
            throw new IllegalArgumentException("newColor is null");
        }
        if (!color.equals(newColor)) {
            this.color = newColor;
            if (paramGUI != null) {
                paramGUI.updateGUI();
            }

            if (!dontTrigger) {
                if (adjustmentListener != null) {  // when called from randomize, this is null
                    adjustmentListener.paramAdjusted();
                }
            }
        }
    }

    public boolean allowOpacity() {
        return allowOpacity;
    }
}
