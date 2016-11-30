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

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 *
 */
public class ShowHideAllAction extends AbstractAction {
    public static final ShowHideAllAction INSTANCE = new ShowHideAllAction();

    private boolean histogramsWereShown = false;
    private boolean layersWereShown = false;
    private boolean statusBarWasShown = false;
    private boolean toolsWereShown = false;

    private boolean allHidden = false;

    private ShowHideAllAction() {
        this.putValue(AbstractAction.NAME, "Hide All");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PixelitorWindow pixelitorWindow = PixelitorWindow.getInstance();

        if(allHidden) { // unhide that previously was hidden
            if(histogramsWereShown) {
                pixelitorWindow.showHistograms(false);
            }
            if(statusBarWasShown) {
                pixelitorWindow.showStatusBar(false);
            }
            if(toolsWereShown) {
                pixelitorWindow.showTools(false);
            }
            if(layersWereShown) {
                pixelitorWindow.showLayers(false);
            }

            ((JComponent) pixelitorWindow.getContentPane()).revalidate();

            this.putValue(AbstractAction.NAME, "Hide All");

            allHidden = false;
        } else { // hide all
            histogramsWereShown = pixelitorWindow.areHistogramsShown();
            if(histogramsWereShown) {
                pixelitorWindow.hideHistograms(false);
            }

            layersWereShown = pixelitorWindow.areLayersShown();
            if(layersWereShown) {
                pixelitorWindow.hideLayers(false);
            }

            statusBarWasShown = pixelitorWindow.isStatusBarShown();
            if(statusBarWasShown) {
                pixelitorWindow.hideStatusBar(false);
            }

            toolsWereShown = pixelitorWindow.areToolsShown();
            if(toolsWereShown) {
                pixelitorWindow.hideTools(false);
            }

            ((JComponent) pixelitorWindow.getContentPane()).revalidate();

            this.putValue(AbstractAction.NAME, "Show Hidden");

            allHidden = true;
        }
    }
}