/*
 * Copyright 2009 L�szl� Bal�zs-Cs�ki
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
package pixelitor.layers;

import javax.swing.plaf.basic.BasicToggleButtonUI;
import javax.swing.*;
import java.awt.Graphics;
import java.awt.Color;

/**
 *
 */
public class LayerButtonUI extends BasicToggleButtonUI {
    static Color selectedColor = new Color(48, 76, 111);

    @Override
    protected void paintButtonPressed(Graphics g, AbstractButton b) {
        Color oldColor = g.getColor();
//        String pp = getPropertyPrefix();
//        Color c = UIManager.getColor("Panel.background").darker();
//        Color bg = b.getBackground();
//        Color c = Color.YELLOW;

        g.setColor(selectedColor);
        g.fillRoundRect(0, 0, b.getWidth(), b.getHeight(), 5, 5);
        g.setColor(oldColor);
    }


}
