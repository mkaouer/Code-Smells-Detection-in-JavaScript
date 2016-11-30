/*
 * Copyright 2010 L�szl� Bal�zs-Cs�ki
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

import javax.swing.*;
import java.awt.Dimension;
import java.awt.FlowLayout;

/**
 *
 */
public class ToolSettingsPanel extends JPanel {
    public ToolSettingsPanel() {
        super(new FlowLayout(FlowLayout.LEFT));
    }

    public void addSeparator() {
        JSeparator separator = new JSeparator(JSeparator.VERTICAL);
        separator.setPreferredSize(new Dimension(
                separator.getPreferredSize().width,
                26));
        add(separator);
    }
}
