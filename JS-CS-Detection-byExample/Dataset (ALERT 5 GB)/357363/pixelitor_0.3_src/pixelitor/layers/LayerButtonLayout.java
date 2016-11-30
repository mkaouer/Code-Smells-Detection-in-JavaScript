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
package pixelitor.layers;

import java.awt.LayoutManager;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;

/**
 * A layout maganer for a layout button
 */
public class LayerButtonLayout implements LayoutManager {
    int hgap;
    private int vgap;
    Component visibilityButton;
    Component nameEditor;

    public static final String VISIBILITY_BUTTON = "VISIBILITY_BUTTON";
    public static final String NAME_EDITOR = "NAME_EDITOR";

    public LayerButtonLayout(int hgap, int vgap) {
        this.hgap = hgap;
        this.vgap = vgap;
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {
         synchronized (comp.getTreeLock()) {
            if(VISIBILITY_BUTTON.equals(name)) {
                visibilityButton = comp;
            } else if(NAME_EDITOR.equals(name)) {
                nameEditor = comp;
            }
         }
    }

    @Override
    public void removeLayoutComponent(Component comp) {
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        synchronized (parent.getTreeLock()) {
            int preferredHeight = Math.max((int) visibilityButton.getPreferredSize().getHeight(), (int) nameEditor.getPreferredSize().getHeight());
            preferredHeight += 2*vgap;
            Dimension d = new Dimension(100, preferredHeight);
            return d;
        }
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return preferredLayoutSize(parent);
    }

    @Override
    public void layoutContainer(Container parent) {
        synchronized (parent.getTreeLock()) {
            int buttonWidth = (int) visibilityButton.getPreferredSize().getWidth();
            int buttonHeight = (int) visibilityButton.getPreferredSize().getHeight();
            visibilityButton.setBounds(hgap, vgap, buttonWidth, buttonHeight);
            nameEditor.setBounds(hgap*2 + buttonWidth, vgap, parent.getWidth() - buttonWidth - 3*hgap, (int) nameEditor.getPreferredSize().getHeight());
        }
    }
}
