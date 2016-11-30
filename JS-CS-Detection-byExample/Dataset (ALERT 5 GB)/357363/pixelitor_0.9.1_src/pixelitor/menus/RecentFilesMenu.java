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
    private static final int DEFAULT_MAX_RECENT_FILES = 5;

    private static RecentFilesMenu singleInstance;

    private final int maxRecentFiles;
    private JMenuItem clearMenuItem;

    private List<String> recentFileNames;

    private transient ActionListener fileOpener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JMenuItem mi = (JMenuItem) e.getSource();
            File f = new File(mi.getText());
            if (f.exists()) {
                OpenSaveManager.openFile(f);
            } else {
                JOptionPane.showMessageDialog(null, "The file " + f + " does not exist.", "Problem", JOptionPane.ERROR_MESSAGE);
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
                clear();
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

    public void fileOpened(File f) {
        if (f.exists()) {
            String name = "";
            try {
                name = f.getCanonicalPath();
            } catch (IOException e) {
                GUIUtils.showExceptionDialog(null, e);
            }
            if (recentFileNames.contains(name)) {
                recentFileNames.remove(name);
            }
            recentFileNames.add(0, name);
            rebuildGUI();
        }
    }

    private void load() {
        recentFileNames = AppPreferences.loadRecentFiles(maxRecentFiles);
    }

    private void clearGUI() {
        removeAll();
//		setEnabled(false);
    }

    // for saving
    public List<String> getRecentFileNames() {
        return recentFileNames;
    }

    private void rebuildGUI() {
        clearGUI();
        for (String name : recentFileNames) {
            JMenuItem item = new JMenuItem(name);
            add(item);
            item.addActionListener(fileOpener);
        }
        if (recentFileNames.size() > 0) {
//			setEnabled(true);
            addSeparator();
            add(clearMenuItem);
        }
    }
}
