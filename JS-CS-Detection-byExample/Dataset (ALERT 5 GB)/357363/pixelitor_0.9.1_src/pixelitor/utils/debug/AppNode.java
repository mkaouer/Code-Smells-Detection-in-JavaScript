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
package pixelitor.utils.debug;

import pixelitor.AppLogic;
import pixelitor.ImageComponent;
import pixelitor.PixelitorWindow;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.List;

/**
 * A debugging node for the whole application
 */
public class AppNode extends DebugNode {
    public AppNode() {
        super("Pixelitor", PixelitorWindow.getInstance());

        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        SystemNode systemNode = new SystemNode(device);
        add(systemNode);

        List<ImageComponent> images = AppLogic.getImageComponents();

        int nrOpenImages = images.size();
        addIntChild("number of open images", nrOpenImages);

        ImageComponent activeIC = AppLogic.getActiveImageComponent();
        for (int i = 0; i < nrOpenImages; i++) {
            ImageComponent ic = images.get(i);
            ImageComponentNode node;
            if (ic == activeIC) {
                node = new ImageComponentNode("ACTIVE Image - " + ic.getName(), ic);
            } else {
                node = new ImageComponentNode("Image - " + ic.getName(), ic);
            }
            add(node);
        }
    }
}
