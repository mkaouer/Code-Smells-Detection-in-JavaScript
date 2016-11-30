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
package pixelitor.tools;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Paint;

/**
 * The erase tool.
 */
public class EraseTool extends AbstractBrushTool {

    public EraseTool() {
        super('e', "Erase", "erase_tool_icon.gif", "click and drag to erase pixels");
//        useFillOval = true;
    }

    public void setupGraphics(Graphics2D g, Paint p) {
        // the color does not matter as long as AlphaComposite.CLEAR is used
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 1.0f));
    }
}