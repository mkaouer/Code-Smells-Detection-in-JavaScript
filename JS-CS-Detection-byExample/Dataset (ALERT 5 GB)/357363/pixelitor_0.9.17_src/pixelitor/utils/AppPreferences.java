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

import pixelitor.NewImage;
import pixelitor.PixelitorWindow;
import pixelitor.TipsOfTheDay;
import pixelitor.history.History;
import pixelitor.io.OpenSaveManager;
import pixelitor.menus.RecentFileInfo;
import pixelitor.menus.RecentFilesMenu;
import pixelitor.tools.FgBgColorSelector;
import pixelitor.utils.debug.SystemNode;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * This class manages the saving and loading of application preferences
 */
public final class AppPreferences {
    /**
     * Persistent preference storage
     */
    private static final String FRAME_X_KEY = "window_x";
    private static final String FRAME_Y_KEY = "window_y";
    private static final String FRAME_WIDTH_KEY = "window_width";
    private static final String FRAME_HEIGHT_KEY = "window_height";

    private static final String NEW_IMAGE_WIDTH = "new_image_width";
    private static final String NEW_IMAGE_HEIGHT = "new_image_height";
    private static Dimension newImageSize = null;

    private static final String RECENT_FILE_PREFS_KEY = "recent_file_";

    private static Preferences openSaveUserNode = Preferences.userNodeForPackage(OpenSaveManager.class);
    private static Preferences windowUserNode = Preferences.userNodeForPackage(PixelitorWindow.class);
    private static Preferences recentFilesUserNode = Preferences.userNodeForPackage(RecentFilesMenu.class);
    private static Preferences fgBgUserNode = Preferences.userNodeForPackage(FgBgColorSelector.class);

    private static final String FG_COLOR_KEY = "fg_color";
    private static final String BG_COLOR_KEY = "bg_color";

    private static final String LAST_OPEN_DIR_KEY = "last_open_dir";
    private static final String LAST_SAVE_DIR_KEY = "last_save_dir";
//    private static final String LOOK_AND_FEEL_KEY = "look_and_feel_class";
    private static final String HISTOGRAMS_SHOWN_KEY = "histograms_shown";
    private static final String LAYERS_SHOWN_KEY = "layers_shown";
    private static final String TOOLS_SHOWN_KEY = "tools_shown";
    private static final String STATUS_BAR_SHOWN_KEY = "status_bar_shown";

    private static final String UNDO_LEVELS_KEY = "undo_levels";
//    private static final String SHOW_DEVEl_MENU_KEY = "show_develop_menu";

    /**
     * Utility class with static methods, do not instantiate
     */
    private AppPreferences() {
    }

    private static void saveFramePosition(Window window) {
        int x = window.getX();
        int y = window.getY();
        int width = window.getWidth();
        int height = window.getHeight();

        windowUserNode.putInt(FRAME_X_KEY, x);
        windowUserNode.putInt(FRAME_Y_KEY, y);
        windowUserNode.putInt(FRAME_WIDTH_KEY, width);
        windowUserNode.putInt(FRAME_HEIGHT_KEY, height);
    }

    public static Dimension loadNewImageSize() {
        if (newImageSize == null) {
            int width = windowUserNode.getInt(NEW_IMAGE_WIDTH, 600);
            int height = windowUserNode.getInt(NEW_IMAGE_HEIGHT, 400);
            //noinspection NonThreadSafeLazyInitialization
            newImageSize = new Dimension(width, height);
        }
        return newImageSize;
    }

    private static void saveNewImageSize() {
        Dimension lastNew = NewImage.getLastNew();
        windowUserNode.putInt(NEW_IMAGE_WIDTH, lastNew.width);
        windowUserNode.putInt(NEW_IMAGE_HEIGHT, lastNew.height);
    }

    public static void loadFramePosition(Window window) {
        int x = windowUserNode.getInt(FRAME_X_KEY, 0);
        int y = windowUserNode.getInt(FRAME_Y_KEY, 0);
        int width = windowUserNode.getInt(FRAME_WIDTH_KEY, 0);
        int height = windowUserNode.getInt(FRAME_HEIGHT_KEY, 0);

        Rectangle bounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();

        if ((width <= 0) || (height <= 0)) {
            width = bounds.width;
            height = bounds.height;
        }
        // too large window
        if (width > bounds.width) {
            width = bounds.width;
        }
        if (height > bounds.height) {
            height = bounds.height;
        }

        if ((x < 0) || (y < 0)) {
            x = 0;
            y = 0;
        }

        window.setLocation(new Point(x, y));
        window.setSize(width, height);
    }

    public static List<RecentFileInfo> loadRecentFiles(int maxNum) {
        List<RecentFileInfo> recentFileNames = new ArrayList<RecentFileInfo>();
        for (int i = 0; i < maxNum; i++) {
            String key = RECENT_FILE_PREFS_KEY + i;
            String value = recentFilesUserNode.get(key, null);
            if (value == null) {
                break;
            }
            File file = new File(value);
            if (file.exists()) {
                RecentFileInfo fileInfo = new RecentFileInfo(file);
                if (!recentFileNames.contains(fileInfo)) {
                    recentFileNames.add(fileInfo);
                }
            }
        }
        return recentFileNames;
    }

    private static void saveRecentFiles(List<RecentFileInfo> fileNames) {
        for (int i = 0; i < fileNames.size(); i++) {
            String key = RECENT_FILE_PREFS_KEY + i;
            String value = fileNames.get(i).getSavedName();
            recentFilesUserNode.put(key, value);
        }
    }

    public static void removeRecentFiles(int maxNum) {
        for (int i = 0; i < maxNum; i++) {
            recentFilesUserNode.remove(RECENT_FILE_PREFS_KEY + i);
        }
    }


    public static File loadLastOpenDir() {
        return loadDir(LAST_OPEN_DIR_KEY);
    }

    public static File loadLastSaveDir() {
        return loadDir(LAST_SAVE_DIR_KEY);
    }

    private static File loadDir(String key) {
        String s = openSaveUserNode.get(key, null);

        if (s == null) {
            return getDocumentsDir();
        }
        File dir = new File(s);
        if (!dir.exists()) {
            return getDocumentsDir();
        }
        if (!dir.isDirectory()) {
            return getDocumentsDir();
        }
        return dir;
    }

    private static File getDocumentsDir() {
        return FileSystemView.getFileSystemView().getDefaultDirectory();
    }


    private static void saveLastOpenDir() {
        saveDir(OpenSaveManager.getLastOpenDir(), LAST_OPEN_DIR_KEY);
    }

    private static void saveLastSaveDir() {
        saveDir(OpenSaveManager.getLastSaveDir(), LAST_SAVE_DIR_KEY);
    }

    private static void saveDir(File f, String key) {
        if (f != null) {
            openSaveUserNode.put(key, f.getAbsolutePath());
        } else {
            openSaveUserNode.put(key, null);
        }
    }

    public static int loadUndoLevels() {
        int retVal = windowUserNode.getInt(UNDO_LEVELS_KEY, -1);
        if (retVal == -1) {
            return getDefaultUndoLevels();
        }
        return retVal;
    }

//    public static boolean loadShowDevelopMenu() {
//        boolean b = windowUserNode.getBoolean(SHOW_DEVEl_MENU_KEY, false);
//        return b;
//    }

    private static void saveUndoLevels() {
        windowUserNode.putInt(UNDO_LEVELS_KEY, History.getUndoLevels());
    }

//    private static void saveShowDevelopMenu() {
//        MenuBar menuBar = (MenuBar) PixelitorWindow.getInstance().getJMenuBar();
//        boolean b = menuBar.isDevelopMenuShown();
//        windowUserNode.putBoolean(SHOW_DEVEl_MENU_KEY, b);
//    }


    public static void savePreferencesBeforeExit() {
        saveRecentFiles(RecentFilesMenu.getInstance().getRecentFileNamesForSaving());
        saveFramePosition(PixelitorWindow.getInstance());
        saveLastOpenDir();
        saveLastSaveDir();
//        saveLFClassName();
        saveFgBgColors();
        WorkSpace.saveVisibility();
        saveUndoLevels();
//        saveShowDevelopMenu();
        TipsOfTheDay.saveNextTipNr();
        saveNewImageSize();
    }

    public static Color loadFgColor() {
        int fgInt = fgBgUserNode.getInt(FG_COLOR_KEY, 0xFF000000);
        return new Color(fgInt);
    }

    public static Color loadBgColor() {
        int bgInt = fgBgUserNode.getInt(BG_COLOR_KEY, 0xFFFFFFFF);
        return new Color(bgInt);
    }


    private static void saveFgBgColors() {
        Color fgColor = FgBgColorSelector.getFG();
        if (fgColor != null) {
            fgBgUserNode.putInt(FG_COLOR_KEY, fgColor.getRGB());
        }

        Color bgColor = FgBgColorSelector.getBG();
        if (bgColor != null) {
            fgBgUserNode.putInt(BG_COLOR_KEY, bgColor.getRGB());
        }
    }

//    private static void saveLFClassName() {
//        String className = UIManager.getLookAndFeel().getClass().getName();
//        windowUserNode.put(LOOK_AND_FEEL_KEY, className);
//    }
//
//    public static String loadLFClassName() {
//        String retVal = windowUserNode.get(LOOK_AND_FEEL_KEY, "");
//        if (retVal.isEmpty()) {
//            retVal = getDefaultLookAndFeelClass();
//        }
//        return retVal;
//    }

    public static String getDefaultLookAndFeelClass() {
        UIManager.LookAndFeelInfo[] lookAndFeels = UIManager.getInstalledLookAndFeels();
        for (UIManager.LookAndFeelInfo lookAndFeel : lookAndFeels) {
            if (lookAndFeel.getName().equals("Nimbus")) {
                return lookAndFeel.getClassName();
            }
        }
        return UIManager.getSystemLookAndFeelClassName();
    }

    public static class WorkSpace {
        /**
         * Utility class with static methods, must not be instantiated
         */
        private WorkSpace() {
        }

        static boolean loaded = false;
        private static boolean histogramsVisibility;
        private static boolean toolsVisibility;
        private static boolean layersVisibility;
        private static boolean statusBarVisibility;

        private static void load() {
            if (loaded) {
                return;
            }
            histogramsVisibility = windowUserNode.getBoolean(HISTOGRAMS_SHOWN_KEY, false);
            toolsVisibility = windowUserNode.getBoolean(TOOLS_SHOWN_KEY, true);
            layersVisibility = windowUserNode.getBoolean(LAYERS_SHOWN_KEY, true);
            statusBarVisibility = windowUserNode.getBoolean(STATUS_BAR_SHOWN_KEY, true);
            loaded = true;
        }

        public static boolean getHistogramsVisibility() {
            load();
            return histogramsVisibility;
        }

        public static boolean getLayersVisibility() {
            load();
            return layersVisibility;
        }

        public static boolean getStatusBarVisibility() {
            load();
            return statusBarVisibility;
        }

        public static boolean getToolsVisibility() {
            load();
            return toolsVisibility;
        }

        private static void saveVisibility() {
            windowUserNode.putBoolean(HISTOGRAMS_SHOWN_KEY, histogramsVisibility);
            windowUserNode.putBoolean(LAYERS_SHOWN_KEY, layersVisibility);
            windowUserNode.putBoolean(TOOLS_SHOWN_KEY, toolsVisibility);
            windowUserNode.putBoolean(STATUS_BAR_SHOWN_KEY, statusBarVisibility);
        }

        public static void setLayersVisibility(boolean v) {
            layersVisibility = v;
            PixelitorWindow pixelitorWindow = PixelitorWindow.getInstance();
            pixelitorWindow.setLayersVisibility(v, true);
        }

        public static void setHistogramsVisibility(boolean v) {
            histogramsVisibility = v;
            PixelitorWindow pixelitorWindow = PixelitorWindow.getInstance();
            pixelitorWindow.setHistogramsVisibility(v, true);
        }

        public static void setToolsVisibility(boolean v) {
            toolsVisibility = v;
            PixelitorWindow pixelitorWindow = PixelitorWindow.getInstance();
            pixelitorWindow.setToolsVisibility(v, true);
        }

        public static void setStatusBarVisibility(boolean v) {
            statusBarVisibility = v;
            PixelitorWindow pixelitorWindow = PixelitorWindow.getInstance();
            pixelitorWindow.setStatusBarVisibility(v, true);
        }
    }

    public static class Panel extends JPanel {
        private JTextField undoLevelsTF;
//        private JCheckBox showDevelopMenuCB;

        Panel() {
            setLayout(new GridBagLayout());
            GridBagHelper.addLabel(this, "Undo/Redo Levels: ", 0, 0);
            undoLevelsTF = new IntTextField(3);
            undoLevelsTF.setText(String.valueOf(History.getUndoLevels()));
            GridBagHelper.addControl(this, undoLevelsTF);

//            GridBagHelper.addLabel(this, "Show Develop Menu: ", 0, 1);
//            showDevelopMenuCB = new JCheckBox();
//            MenuBar menuBar = (MenuBar) PixelitorWindow.getInstance().getJMenuBar();
//            showDevelopMenuCB.setSelected(menuBar.isDevelopMenuShown());
//            GridBagHelper.addControl(this, showDevelopMenuCB);
        }

        private int getUndoLevels() {
            String s = undoLevelsTF.getText();
            int retVal = Integer.parseInt(s);
            return retVal;
        }

//        private boolean getShowDevelopMenuSetting() {
//            return showDevelopMenuCB.isSelected();
//        }

        public static void showInDialog() {
            final Panel p = new Panel();
            new OKCancelDialog(p, PixelitorWindow.getInstance(), "Preferences", true) {
                @Override
                protected void dialogAccepted() {
                    super.dialogAccepted();

                    int undoLevels = p.getUndoLevels();
                    History.setUndoLevels(undoLevels);

//                    boolean b = p.getShowDevelopMenuSetting();
//                    MenuBar menuBar = (MenuBar) PixelitorWindow.getInstance().getJMenuBar();
//                    menuBar.setShowDevelopMenu(b);

                    dispose();
                }

                @Override
                protected void dialogCancelled() {
                    super.dialogCancelled();
                    dispose();
                }
            };
        }
    }

    public static Preferences getWindowUserNode() {
        return windowUserNode;
    }

    private static int getDefaultUndoLevels() {
        int sizeInMegaBytes = SystemNode.getMaxHeapInMegabytes();
        int retVal = 1 + (sizeInMegaBytes / 50);

        return retVal;
    }

}
