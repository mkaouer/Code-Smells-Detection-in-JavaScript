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

package pixelitor.utils;

import com.bric.util.JVM;
import pixelitor.ExceptionHandler;
import pixelitor.GlobalKeyboardWatch;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public abstract class OKCancelDialog extends JDialog {
    JComponent formPanel;


    protected OKCancelDialog(JComponent form, Frame owner, String title, boolean visible) {
        this(form, owner, title, "OK", "Cancel", visible);
    }

    protected OKCancelDialog(JComponent form, Frame owner, String title, String okText, String cancelText, boolean visible) {
        super(owner, title, true);
        this.formPanel = form;

        setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(form);
        add(scrollPane, BorderLayout.CENTER);

        JPanel southPanel = new JPanel();


        JButton okButton = new JButton(okText);
        JButton cancelButton = new JButton(cancelText);

        GlobalKeyboardWatch.setShowHideAllForTab(false);

        if (JVM.isMac) {
//        if(2 > 1) {
            southPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
            southPanel.add(cancelButton);
            southPanel.add(okButton);
        } else {
            southPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
            southPanel.add(okButton);
            southPanel.add(cancelButton);
        }

        add(southPanel, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(okButton);
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    dialogAccepted();
                } catch (Exception ex) {
                    ExceptionHandler.showExceptionDialog(ex);
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    dialogCancelled();
                } catch (Exception ex) {
                    ExceptionHandler.showExceptionDialog(ex);
                }
            }
        });

        // cancel when window is closed
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dialogCancelled();
            }
        });

        // cancel when ESC is pressed
        ((JComponent) getContentPane()).registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialogCancelled();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        pack();
        GUIUtils.centerOnScreen(this);
        if (visible) {
            setVisible(true);
        }
    }


    protected void dialogAccepted() {
        GlobalKeyboardWatch.setShowHideAllForTab(true);
    }

    protected void dialogCancelled() {
        GlobalKeyboardWatch.setShowHideAllForTab(true);
    }
}
