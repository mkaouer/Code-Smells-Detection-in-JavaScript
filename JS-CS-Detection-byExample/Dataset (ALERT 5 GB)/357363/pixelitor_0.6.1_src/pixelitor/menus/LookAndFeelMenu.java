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
package pixelitor.menus;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * A menu that lets the user select the swing look and feel of the application.
 */
public class LookAndFeelMenu extends JMenu {
    public LookAndFeelMenu(String s, final JFrame parent) {
        super(s);

        String currentClassName = UIManager.getLookAndFeel().getClass().getName();
        ButtonGroup lfRadioGroup = new ButtonGroup();
        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            final String name = info.getName();
            final String className = info.getClassName();
            AbstractAction a = new AbstractAction(name) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
//                        LookAndFeelMenu.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                        UIManager.setLookAndFeel(className);
                    } catch (ClassNotFoundException e1) {
                        e1.printStackTrace();
                    } catch (InstantiationException e1) {
                        e1.printStackTrace();
                    } catch (IllegalAccessException e1) {
                        e1.printStackTrace();
                    } catch (UnsupportedLookAndFeelException e1) {
                        e1.printStackTrace();
//                    } finally {
//                        LookAndFeelMenu.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    }
                    SwingUtilities.updateComponentTreeUI(parent);
                }
            };
            JRadioButtonMenuItem lfMenuItem = new JRadioButtonMenuItem(a);
            if (className.equals(currentClassName)) {
                lfMenuItem.setSelected(true);
            }
            this.add(lfMenuItem);
            lfRadioGroup.add(lfMenuItem);
        }
    }
}
