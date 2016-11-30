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
 * A layout manager for the layers. It arranges its components vertically from the bottom upwards,
 * respecting their preferred sizes.
 */
public class LayersLayout implements LayoutManager {
    private int hgap;
    private int vgap;

    public LayersLayout(int hgap, int vgap) {
        this.hgap = hgap;
        this.vgap = vgap;
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {
    }

    @Override
    public void removeLayoutComponent(Component comp) {
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        synchronized (parent.getTreeLock()) {

            Dimension dim = new Dimension(0, vgap);
            int nmembers = parent.getComponentCount();

            for (int i = 0 ; i < nmembers ; i++) {
                Component c = parent.getComponent(i);
                dim.height += c.getPreferredSize().getHeight();
                dim.height += vgap;
                dim.width = Math.max(dim.width, (int)c.getPreferredSize().getWidth());
            }
            dim.width += 2*hgap;

            return dim;
        }
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return preferredLayoutSize(parent);
    }

    @Override
    public void layoutContainer(Container parent) {
//        System.out.println("LayerButtonLayout.layoutContainer parent = " + parent.toString());
        synchronized (parent.getTreeLock()) {
            int nmembers = parent.getComponentCount();
            int parentHeight = Math.max((int) parent.getPreferredSize().getHeight(), parent.getHeight());
            int currentBaseY = parentHeight - vgap;
//            System.out.println("LayersLayout.layoutContainer currentBaseY = " + currentBaseY);
            int totalWidth = Math.max((int) parent.getPreferredSize().getWidth(), parent.getWidth());
            for (int i = 0 ; i < nmembers ; i++) {
                Component c = parent.getComponent(i);
                int compHeight = (int) c.getPreferredSize().getHeight();

                int x = hgap;
                int y = currentBaseY - compHeight;
                int width = totalWidth - 2 * hgap;
                int height = compHeight;
//                System.out.println("LayersLayout.layoutContainer x = " + x + ", y = " + y + ", width = " + width + ", height = " + height);
                c.setBounds(x, y, width, height);
                currentBaseY -= (compHeight + vgap);
            }
        }
    }
}