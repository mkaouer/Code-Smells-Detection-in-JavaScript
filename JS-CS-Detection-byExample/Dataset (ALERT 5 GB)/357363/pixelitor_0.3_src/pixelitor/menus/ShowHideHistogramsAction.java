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
public class ShowHideHistogramsAction extends AbstractAction {
    public ShowHideHistogramsAction() {
        if (AppPreferences.VisibilityInfo.getHistoVisibility()) {
            this.putValue(AbstractAction.NAME, "Hide Histograms");
        } else {
            this.putValue(AbstractAction.NAME, "Show Histograms");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PixelitorWindow pixelitorWindow = PixelitorWindow.getInstance();
        if (pixelitorWindow.areHistogramsShown()) {
            pixelitorWindow.hideHistograms(true);
            this.putValue(AbstractAction.NAME, "Show Histograms");
        } else {
            pixelitorWindow.showHistograms(true);
            this.putValue(AbstractAction.NAME, "Hide Histograms");
        }
    }
}