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
package pixelitor.automate;

import pixelitor.io.OutputFormat;

import javax.swing.*;

/**
 * The GUI elements of an output format selector are separated into this
 * non-component class so that they can be reused with different layout managers
 */
public class OutputFormatSelector {
    private final JComboBox formatCombo;

    public OutputFormatSelector() {
        formatCombo = new JComboBox(OutputFormat.values());
        formatCombo.setSelectedItem(OutputFormat.getLastOutputFormat());
    }

    public JComboBox getFormatCombo() {
        return formatCombo;
    }

    public OutputFormat getSelectedFormat() {
        return (OutputFormat) formatCombo.getSelectedItem();
    }

    public String getLabelText() {
        return "Output Format:";
    }
}
