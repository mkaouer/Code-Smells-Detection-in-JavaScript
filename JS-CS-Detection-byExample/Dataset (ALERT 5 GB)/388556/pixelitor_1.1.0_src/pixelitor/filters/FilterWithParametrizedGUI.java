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
package pixelitor.filters;

import pixelitor.filters.gui.AdjustPanel;
import pixelitor.filters.gui.FilterWithGUI;
import pixelitor.filters.gui.ParamSet;
import pixelitor.filters.gui.ParametrizedAdjustPanel;

import javax.swing.*;

/**
 *
 */
public abstract class FilterWithParametrizedGUI extends FilterWithGUI {
    protected ParamSet paramSet; // initialized by subclasses

    protected FilterWithParametrizedGUI(String name) {
        this(name, null);
    }

    protected FilterWithParametrizedGUI(String name, Icon icon) {
        super(name, icon);
    }

    public ParamSet getParams() {
        return paramSet;
    }

    @Override
    public void randomizeSettings() {
        paramSet.randomize();
    }

    @Override
    public AdjustPanel getAdjustPanel() {
        return new ParametrizedAdjustPanel(this);
    }

}
