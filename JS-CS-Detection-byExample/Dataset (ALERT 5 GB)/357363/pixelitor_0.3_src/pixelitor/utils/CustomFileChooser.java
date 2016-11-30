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

package pixelitor.utils;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import pixelitor.OpenSaveManager;

public class CustomFileChooser extends JFileChooser {

    public CustomFileChooser(String currentDirectoryPath) {
        super(currentDirectoryPath);
        setAcceptAllFileFilterUsed(false);
    }

    @Override
    public void approveSelection() {
        File f = getSelectedFile();
        if (f.exists()) {
            int userResponse = JOptionPane.showConfirmDialog(
                    this, f.getName() + " exists already. Overwrite?",
                    "Confirmation", JOptionPane.YES_NO_OPTION);
            if (userResponse != JOptionPane.OK_OPTION) {
                return;
            }
        }
        super.approveSelection();
    }

    @Override
    public File getSelectedFile() {
        File f = super.getSelectedFile();
        if (f == null) {
            return f;
        }
        String extension = Utils.getFileExtension(f.getName());

        if (extension == null) {
            // the user entered no extension
            // determine it from the active ChoosableFileFilter
            extension = getExtensionFromFileFilter();
            f = new File(f.getAbsolutePath() + "." + extension);
        } else {
            boolean supportedExtension = Utils.isSupportedExtension(f.getName(), OpenSaveManager.SUPPORTED_OUTOUT_FORMATS);
            if(!supportedExtension) {
                String newExtension = getExtensionFromFileFilter();
                f = new File(f.getAbsolutePath() + "." + newExtension);
            }
        }

        return f;
    }

    private String getExtensionFromFileFilter() {
        FileFilter currentFilter = getFileFilter();
        return ((FileNameExtensionFilter) currentFilter).getExtensions()[0];
    }
}
