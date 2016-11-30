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
package pixelitor.utils;

import pixelitor.operations.gui.GUIParam;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A button that resets its GUIParam, and displays and arrow when the
 * GUIParam is not set to the default value
 */
public class DefaultButton extends JButton {
    private static ImageIcon defaultIcon = ImageUtils.loadIcon("default_icon.gif");
    private GUIParam guiParam;

    public DefaultButton(final GUIParam guiParam) {
        this.guiParam = guiParam;
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guiParam.reset(true);
            }
        });
        setToolTipText("Reset the Default Setting");
    }

    public void updateState() {
        setDefault(guiParam.isSetToDefault());
    }

    private void setDefault(boolean b) {
        if (b) {
            setIcon(null);
        } else {
            setIcon(defaultIcon);
        }
    }
}
