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
public class ImagePositionParam implements GUIParam {
    private float relativeX = 0.5f;
    private float relativeY = 0.5f;

    private String name;
    private ParamAdjustingListener adjustingListener;
    private ParamGUI paramGUI;
    private boolean dontTrigger = false;

    public ImagePositionParam(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isSetToDefault() {
        return false;
    }

    @Override
    public JComponent createControl() {
        ImagePositionPanel selector = new ImagePositionPanel(this);
        paramGUI = selector;
        paramGUI.updateGUI();
        return selector;
    }

    public void reset(boolean triggerAction) {
        if (!triggerAction) {
            dontTrigger = true;
        }
        setRelativeValues(0.5f, 0.5f, true);
        dontTrigger = false;
    }

    @Override
    public void setAdjustingListener(ParamAdjustingListener listener) {
        this.adjustingListener = listener;
    }

    @Override
    public int getNrOfGridBagCols() {
        return 1;
    }

    @Override
    public void randomize() {
        setRelativeValues((float) Math.random(), (float) Math.random(), true);
    }

    public ParamAdjustingListener getAdjustingListener() {
        return adjustingListener;
    }

    public float getRelativeX() {
        return relativeX;
    }

    public float getRelativeY() {
        return relativeY;
    }

    public void setRelativeValues(float relativeX, float relativeY, boolean updateGUI) {
        this.relativeX = relativeX;
        this.relativeY = relativeY;
        if (updateGUI && (paramGUI != null)) {
            paramGUI.updateGUI();
        }
        if (!dontTrigger) {
            // TODO this should call adjustingListener.paramAdjusted();
        }
    }

    public void setRelativeX(float newRelativeX) {
        setRelativeValues(newRelativeX, relativeY, false);
    }

    public void setRelativeY(float newRelativeY) {
        setRelativeValues(relativeX, newRelativeY, false);
    }

    @Override
    public void setDontTrigger(boolean b) {
        dontTrigger = b;
    }
}
