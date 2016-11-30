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

import pixelitor.utils.ImageUtils;

import javax.swing.*;
import java.awt.Color;

/**
 *
 */
public class ColorParam implements GUIParam {
    private ParamAdjustingListener adjustingListener;
    private String name;
    private Color defaultColor;
    private Color color;
    private boolean dontTrigger = false;
    private ParamGUI paramGUI;

    public ColorParam(String name, Color defaultColor) {
        this.name = name + ":";
        this.defaultColor = defaultColor;
        this.color = defaultColor;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isSetToDefault() {
        return color.equals(defaultColor);
    }

    @Override
    public JComponent createControl() {
        ColorSelector gui = new ColorSelector(this);
        paramGUI = gui;
        return gui;
    }

    public void reset(boolean triggerAction) {
        if (!triggerAction) {
            dontTrigger = true;
        }
        setColor(defaultColor);
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
        Color c = ImageUtils.getRandomColor();
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
                if (adjustingListener != null) {  // when called from randomize, this is null
                    adjustingListener.paramAdjusted();
                }
            }
        }
    }

    @Override
    public void setDontTrigger(boolean b) {
        dontTrigger = b;
    }
}
