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

import javax.swing.*;

/**
 * A menu item for the recent file entries
 */
public class RecentFilesMenuItem extends JMenuItem {
    private RecentFileInfo fileInfo;

    public RecentFilesMenuItem(RecentFileInfo fileInfo) {
        super(fileInfo.getMenuName());
        this.fileInfo = fileInfo;
    }

    public RecentFileInfo getFileInfo() {
        return fileInfo;
    }
}
