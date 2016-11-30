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

import pixelitor.operations.Operation;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 *
 */
public class LazyImageMenuItem extends OpenImageAwareMenuItem {
    private Operation operation;
    private String opClassName;

    public LazyImageMenuItem() {
        super(null);
        Action action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent x) {
                if (operation == null) {
                    try {
                        Class<?> c = Class.forName(opClassName);
                        operation = (Operation) c.newInstance();
                    } catch (ClassNotFoundException e1) {
                        e1.printStackTrace();
                    } catch (InstantiationException e1) {
                        e1.printStackTrace();
                    } catch (IllegalAccessException e1) {
                        e1.printStackTrace();
                    }
                }
                operation.actionPerformed(x);
            }
        };
        setAction(action);

    }

}
