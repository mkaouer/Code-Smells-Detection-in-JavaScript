/*
 * Copyright 2009-2010 L�szl� Bal�zs-Cs�ki
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
package pixelitor.menus;

import pixelitor.PixelitorWindow;
import pixelitor.utils.AppPreferences;

/**
 *
 */
public class ShowHideLayersAction extends ShowHideAction {

    public ShowHideLayersAction() {
        super("Show Layers", "Hide Layers");
    }

    @Override
    public boolean getCurrentVisibility() {
        PixelitorWindow pixelitorWindow = PixelitorWindow.getInstance();
        return pixelitorWindow.areLayersShown();
    }

    @Override
    public boolean getVisibilityAtStartUp() {
        return AppPreferences.WorkSpace.getLayersVisibility();
    }

    @Override
    public void setVisibilityAction(boolean value) {
        AppPreferences.WorkSpace.setLayersVisibility(value);
    }
}