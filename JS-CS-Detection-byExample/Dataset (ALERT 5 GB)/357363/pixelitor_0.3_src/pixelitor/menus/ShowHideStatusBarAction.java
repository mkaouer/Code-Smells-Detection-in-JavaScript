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
public class ShowHideStatusBarAction extends AbstractAction {
    public ShowHideStatusBarAction() {
        if (AppPreferences.VisibilityInfo.getStatusBarVisibility()) {
            this.putValue(AbstractAction.NAME, "Hide Statusbar");
        } else {
            this.putValue(AbstractAction.NAME, "Show Statusbar");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PixelitorWindow pixelitorWindow = PixelitorWindow.getInstance();
        if(pixelitorWindow.isStatusBarShown()) {
            pixelitorWindow.hideStatusBar(true);
            this.putValue(AbstractAction.NAME, "Show Statusbar");
        } else {
            pixelitorWindow.showStatusBar(true);
            this.putValue(AbstractAction.NAME, "Hide Statusbar");
        }
    }
}
