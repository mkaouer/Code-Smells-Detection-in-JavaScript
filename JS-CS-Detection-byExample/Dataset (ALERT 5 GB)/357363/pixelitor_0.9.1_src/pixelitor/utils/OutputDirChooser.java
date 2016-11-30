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

import pixelitor.PixelitorWindow;

import javax.swing.*;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 *
 */
public class OutputDirChooser extends OKCancelDialog {
    private static boolean okPressed;

    private OutputDirChooser(JPanel formPanel, Frame owner, String title, boolean visible) {
        super(formPanel, owner, title, visible);
    }

    @Override
    protected void dialogAccepted() {
        okPressed = true;
        dispose();
    }

    @Override
    protected void dialogCancelled() {
        okPressed = false;
        dispose();
    }

    public static File getSelectedDir() {
        ChooserPanel chooserPanel = new ChooserPanel();
        new OutputDirChooser(chooserPanel, PixelitorWindow.getInstance(), "Select Output Folder", true);
        if(!okPressed) {
            return null;
        }
        return chooserPanel.getSelectedDir();
    }
}

class ChooserPanel extends JPanel {
    private JTextField dirTF;

    public ChooserPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        add(new JLabel("Output directory:"));
        dirTF = new JTextField(20);
        dirTF.setText(AppPreferences.loadLastSaveDir());
        add(dirTF);
        JButton button = new JButton("Browse...");
        add(button);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser(dirTF.getText());
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.showOpenDialog(null);
                File selectedFile = chooser.getSelectedFile();
                if (selectedFile != null) {
                    String fileName = selectedFile.toString();
                    dirTF.setText(fileName);
                }
            }
        });
    }

    public File getSelectedDir() {
        String s = dirTF.getText();
        File f = new File(s);

        if (f.exists() && f.isDirectory()) {
            return f;
        }

        return null;
    }
}