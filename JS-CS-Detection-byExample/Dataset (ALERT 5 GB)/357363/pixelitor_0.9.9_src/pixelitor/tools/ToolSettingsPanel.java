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

import javax.swing.*;
import java.awt.CardLayout;
import java.awt.FlowLayout;

/**
 *
 */
public final class ToolSettingsPanel extends JPanel {
    public static final ToolSettingsPanel INSTANCE = new ToolSettingsPanel();

    private ToolSettingsPanel() {
        setLayout(new CardLayout());

        Tool[] tools = Tool.values();
        for (Tool tool : tools) {
            JPanel p = new JPanel();
            p.setLayout(new FlowLayout(FlowLayout.LEFT));
            tool.initSettingsPanel(p);
            add(p, tool.getName());
        }
    }

    public void showSettingsFor(Tool tool) {
        CardLayout cl = (CardLayout) (getLayout());
        cl.show(this, tool.getName());
    }
}
