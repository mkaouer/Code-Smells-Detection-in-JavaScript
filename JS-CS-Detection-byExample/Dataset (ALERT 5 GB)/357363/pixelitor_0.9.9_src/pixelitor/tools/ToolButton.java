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
package pixelitor.tools;

import pixelitor.AppLogic;
import pixelitor.utils.ImageUtils;

import javax.swing.*;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
*
*/
public class ToolButton extends JToggleButton implements ActionListener {
    private Tool tool;

    public ToolButton(Tool tool) {
        this.tool = tool;
        tool.setButton(this);

        Icon icon = ImageUtils.loadIcon(tool.getIconFileName());
        setIcon(icon);

        char c = tool.getActivationKeyChar();
        String s = new String(new char[]{c}).toUpperCase();
        setToolTipText(tool.getName() + " Tool (" + s + ")");

        setMargin(new Insets(0, 0, 0, 0));
        setBorderPainted(true);
        setRolloverEnabled(false);
        addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        AppLogic.setCurrentTool(tool);
    }
}
