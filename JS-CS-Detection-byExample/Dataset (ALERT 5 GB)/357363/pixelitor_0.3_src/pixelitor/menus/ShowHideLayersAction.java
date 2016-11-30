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
package pixelitor.menus;

import pixelitor.PixelitorWindow;
import pixelitor.utils.AppPreferences;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 *
 */
public class ShowHideLayersAction extends AbstractAction {
    public ShowHideLayersAction() {
        if (AppPreferences.VisibilityInfo.getLayersVisibility()) {
            this.putValue(AbstractAction.NAME, "Hide Layers");
        } else {
            this.putValue(AbstractAction.NAME, "Show Layers");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PixelitorWindow pixelitorWindow = PixelitorWindow.getInstance();
        if(pixelitorWindow.areLayersShown()) {
            pixelitorWindow.hideLayers(true);
            this.putValue(AbstractAction.NAME, "Show Layers");
        } else {
            pixelitorWindow.showLayers(true);
            this.putValue(AbstractAction.NAME, "Hide Layers");
        }
    }
}