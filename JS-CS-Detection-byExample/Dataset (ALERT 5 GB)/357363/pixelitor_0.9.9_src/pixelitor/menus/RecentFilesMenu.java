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

import pixelitor.io.OpenSaveManager;
import pixelitor.utils.AppPreferences;
import pixelitor.utils.GUIUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

public final class RecentFilesMenu extends JMenu {
    private static final int DEFAULT_MAX_RECENT_FILES = 10;

    private static RecentFilesMenu singleInstance;

    private final int maxRecentFiles;
    private JMenuItem clearMenuItem;

    private List<RecentFileInfo> recentFileNames;

    private final ActionListener fileOpener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                RecentFilesMenuItem mi = (RecentFilesMenuItem) e.getSource();
                File f = mi.getFileInfo().getFile();
                if (f.exists()) {
                    OpenSaveManager.openFile(f);
                } else {
                    JOptionPane.showMessageDialog(null, "The file " + f + " does not exist.", "Problem", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                GUIUtils.showExceptionDialog(ex);
            }
        }
    };

    private RecentFilesMenu() {
        super("Recent Files");
        maxRecentFiles = DEFAULT_MAX_RECENT_FILES;
        clearMenuItem = new JMenuItem("Clear Recent Files");
        ActionListener clearer = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    clear();
                } catch (Exception ex) {
                    GUIUtils.showExceptionDialog(ex);
                }
            }
        };
        clearMenuItem.addActionListener(clearer);
        load();
        rebuildGUI();

    }

    private void clear() {
        AppPreferences.removeRecentFiles(maxRecentFiles);
        recentFileNames.clear();
        clearGUI();
    }

    public static synchronized RecentFilesMenu getInstance() {
        if (singleInstance == null) {
            singleInstance = new RecentFilesMenu();
        }
        return singleInstance;
    }

    public void addFile(File f) {
        if (f.exists()) {
            RecentFileInfo fileInfo = new RecentFileInfo(f);
            if (recentFileNames.contains(fileInfo)) {
                recentFileNames.remove(fileInfo);
            }
            recentFileNames.add(0, fileInfo); // add to the front

            if(recentFileNames.size() > maxRecentFiles) { // it is now too large
                recentFileNames.remove(maxRecentFiles);
            }

            rebuildGUI();
        }
    }

    private void load() {
        recentFileNames = AppPreferences.loadRecentFiles(maxRecentFiles);
    }

    private void clearGUI() {
        removeAll();
    }

    public List<RecentFileInfo> getRecentFileNamesForSaving() {
        if(recentFileNames.size() > maxRecentFiles) {
            return recentFileNames.subList(0, maxRecentFiles);
        }

        return recentFileNames;
    }

    private void rebuildGUI() {
        clearGUI();
        for (int i = 0, recentFileNamesSize = recentFileNames.size(); i < recentFileNamesSize; i++) {
            RecentFileInfo fileInfo = recentFileNames.get(i);
            fileInfo.setNr(i + 1);
            RecentFilesMenuItem item = new RecentFilesMenuItem(fileInfo);
            add(item);
            item.addActionListener(fileOpener);
        }
        if (!recentFileNames.isEmpty()) {
            addSeparator();
            add(clearMenuItem);
        }
    }
}

