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
package pixelitor.filters.gui;

import javax.swing.*;

/**
 * A GUIParam for selecting an image coordinate (relative to the image size)
 */
public class ImagePositionParam extends AbstractGUIParam {
    private float relativeX = 0.5f;
    private float relativeY = 0.5f;

    private ParamGUI paramGUI;


    public ImagePositionParam(String name) {
        super(name);
    }

    @Override
    public boolean isSetToDefault() {
        return false;
    }

    @Override
    public JComponent createGUI() {
        ImagePositionPanel selector = new ImagePositionPanel(this);
        paramGUI = selector;
        paramGUI.updateGUI();
        return selector;
    }

    @Override
    public void reset(boolean triggerAction) {
        if (!triggerAction) {
            dontTrigger = true;
        }
        setRelativeValues(0.5f, 0.5f, true);
        dontTrigger = false;
    }

    @Override
    public int getNrOfGridBagCols() {
        return 1;
    }

    @Override
    public void randomize() {
        setRelativeValues((float) Math.random(), (float) Math.random(), true);
    }

    public ParamAdjustmentListener getAdjustingListener() {
        return adjustmentListener;
    }

    public float getRelativeX() {
        return relativeX;
    }

    public float getRelativeY() {
        return relativeY;
    }

    private void setRelativeValues(float relativeX, float relativeY, boolean updateGUI) {
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
}
