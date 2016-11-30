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
package pixelitor.menus;

import pixelitor.filters.Filter;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 *
 */
public class LazyImageMenuItem extends OpenImageAwareMenuItem {
    private Filter filter;
    private String opClassName;

    public LazyImageMenuItem() {
        super(null);
        Action action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent x) {
                if (filter == null) {
                    try {
                        Class<?> c = Class.forName(opClassName);
                        filter = (Filter) c.newInstance();
                    } catch (ClassNotFoundException e1) {
                        e1.printStackTrace();
                        return;
                    } catch (InstantiationException e1) {
                        e1.printStackTrace();
                        return;
                    } catch (IllegalAccessException e1) {
                        e1.printStackTrace();
                        return;
                    }
                }
                filter.actionPerformed(x);
            }
        };
        setAction(action);

    }

}
