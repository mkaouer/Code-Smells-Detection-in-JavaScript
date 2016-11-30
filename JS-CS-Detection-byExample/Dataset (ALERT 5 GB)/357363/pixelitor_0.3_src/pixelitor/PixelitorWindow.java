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

package pixelitor;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;
import java.io.File;

import javax.swing.*;

import pixelitor.menus.MenuBar;
import pixelitor.menus.ShowHideAllAction;
import pixelitor.tools.ToolSettingsPanel;
import pixelitor.tools.ToolsPanel;
import pixelitor.utils.AppPreferences;
import pixelitor.utils.GUIUtils;
import pixelitor.utils.HistogramsPanel;
import pixelitor.layers.LayersContainer;
import pixelitor.layers.LayersPanel;

public final class PixelitorWindow extends JFrame {
    private static volatile PixelitorWindow singleInstance;


    private ImageComponent activeImageComponent;

    private JDesktopPane desktopPane;
    private JLabel statusBar;
    private LayersContainer layersContainer;
    private HistogramsPanel histogramsPanel;
    private Box verticalBoxEast;
    private Box verticalBoxWest;
    private ToolsPanel toolsPanel;
    private FgBgColorSelector fgBgColorSelector;

    private PixelitorWindow(boolean testing) throws HeadlessException {
        super("Pixelitor");

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent we) {
                        if (AppLogic.thereAreUnsavedChanges()) {
                            int answer = JOptionPane.showConfirmDialog(null, "There are unsaved changes. Are you sure you want to exit?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                            if (answer == JOptionPane.YES_OPTION) {
                                savePrefsAndExit();
                            }
                        } else {
                            savePrefsAndExit();
                        }
                    }
                }
        );

        JMenuBar menuBar = new MenuBar(this);
        setJMenuBar(menuBar);

        desktopPane = new JDesktopPane();
        desktopPane.setBackground(Color.GRAY);
        add(desktopPane, BorderLayout.CENTER);

        verticalBoxEast = Box.createVerticalBox();
        histogramsPanel = HistogramsPanel.INSTANCE;
        AppLogic.addImageChangeListener(histogramsPanel);
        if(AppPreferences.VisibilityInfo.getHistoVisibility()) {
            verticalBoxEast.add(histogramsPanel);
        }

        layersContainer = new LayersContainer();
        if(AppPreferences.VisibilityInfo.getLayersVisibility()) {
            verticalBoxEast.add(layersContainer);
        }

        add(verticalBoxEast, BorderLayout.EAST);

        verticalBoxWest = Box.createVerticalBox();
        toolsPanel = new ToolsPanel();
        fgBgColorSelector = new FgBgColorSelector();

        if(AppPreferences.VisibilityInfo.getToolsVisibility()) {
            verticalBoxWest.add(toolsPanel);
            verticalBoxWest.add(fgBgColorSelector);
            add(ToolSettingsPanel.INSTANCE, BorderLayout.NORTH);
        }

        add(verticalBoxWest, BorderLayout.WEST);

        statusBar = new JLabel("Pixelitor started");
        statusBar.setBorder(BorderFactory.createEtchedBorder());

        if(AppPreferences.VisibilityInfo.getStatusBarVisibility()) {
            add(statusBar, BorderLayout.SOUTH);
        }


        java.net.URL imgURL = getClass().getResource("/images/pixelitor_icon.png");

        if (imgURL != null) {
            setIconImage(new ImageIcon(imgURL).getImage());
        } else {
            JOptionPane.showMessageDialog(this, "icon imgURL is null", "Error", JOptionPane.ERROR_MESSAGE);
        }

        // tab is the focus traversal key, it must be handled before it gets consumed
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    if (e.getKeyCode() == KeyEvent.VK_TAB) {
                        ShowHideAllAction.INSTANCE.actionPerformed(null);
                    }
                }
                return false;
            }
        });


        if (testing) {
            GUIUtils.testJComponent((JComponent) getContentPane());
        } else {
            pack();
            AppPreferences.loadFramePosition(this);
            setVisible(true);
            InternalImageFrame.setMaxSize(desktopPane.getSize());
        }
    }

    private void savePrefsAndExit() {
        AppPreferences.savePreferencesBeforeExit();
        System.exit(0);
    }

    public ImageComponent getActiveImageComponent() {
        return activeImageComponent;
    }

    public void setActiveImageComponent(ImageComponent imageComponent) {
        this.activeImageComponent = imageComponent;
    }

    // called on the EDT
    private static void createAndShowGUI(String[] args) {
        try {
            UIManager.setLookAndFeel(AppPreferences.loadLFClassName());
        } catch (Exception e) {
            GUIUtils.showExceptionDialog(PixelitorWindow.getInstance(), e);
        }

        getInstance();

        // open the files given on the command line
        for (String fileName : args) {
            File f = new File(fileName);
            if (f.exists()) {
                OpenSaveManager.openFile(f);
            }
        }

        Runnable loadFileChoosersTask = new Runnable() {
            @Override
            public void run() {
//                long startTime = System.nanoTime();

                OpenSaveManager.initOpenFileChooser();
                OpenSaveManager.initSaveFileChooser();

//                long totalTime = (System.nanoTime() - startTime) / 1000000;
//                System.out.println("PixelitorWindow loading file choosers in background: it took " + totalTime + " ms");
            }
        };
        Thread t = new Thread(loadFileChoosersTask);
        t.setPriority(Thread.NORM_PRIORITY);
        t.start();
    }

    public static PixelitorWindow getInstance() {
        if (singleInstance == null) {
            synchronized (PixelitorWindow.class) {
                if (singleInstance == null) {
                    singleInstance = new PixelitorWindow(false);
                }
            }
        }
        return singleInstance;
    }

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    createAndShowGUI(args);
                } catch (Exception e) {
                    GUIUtils.showExceptionDialog(e);
                }
            }
        });
    }


    public void activateThis(InternalImageFrame a) {
        desktopPane.getDesktopManager().activateFrame(a);
    }

    public void addImage(BufferedImage img, String imageTitle) {
        int nrOfOpenImages = AppLogic.getNrOfOpenImages();

        activeImageComponent = new ImageComponent(imageTitle);
        AppLogic.addImageComponent(activeImageComponent);

        InternalImageFrame internalFrame = new InternalImageFrame(activeImageComponent);
        internalFrame.setLocation(15*nrOfOpenImages, 25*nrOfOpenImages);

        activeImageComponent.setInternalFrame(internalFrame);

        desktopPane.add(internalFrame);
        try {
            internalFrame.setSelected(true);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        desktopPane.getDesktopManager().activateFrame(internalFrame);


        internalFrame.setSize(400, 200);
        activeImageComponent.addImageToNewLayer(img, ImageChangeReason.FIRST_TIME_INIT);

        AppLogic.newImageOpened();
    }

    public void setStatusBarMessage(String msg) {
        statusBar.setText(msg);
    }

    public boolean isStatusBarShown() {
        return (statusBar.getParent() != null);
    }

    public void hideStatusBar(boolean revalidate) {
        remove(statusBar);
        if (revalidate) {
            ((JComponent) getContentPane()).revalidate();
        }
    }

    public void showStatusBar(boolean revalidate) {
        add(statusBar, BorderLayout.SOUTH);
        if (revalidate) {
            ((JComponent) getContentPane()).revalidate();
        }
    }

    public void hideHistograms(boolean revalidate) {
        verticalBoxEast.remove(histogramsPanel);
        if (revalidate) {
            verticalBoxEast.revalidate();
        }
    }

    public void showHistograms(boolean revalidate) {
        verticalBoxEast.add(histogramsPanel);
        BufferedImage bi = AppLogic.getActiveImageComponent().getCompositeImage();
        histogramsPanel.updateWithImage(bi);
        if (revalidate) {
            verticalBoxEast.revalidate();
        }
    }

    public boolean areHistogramsShown() {
        return (histogramsPanel.getParent() != null);
    }

    public void hideLayers(boolean revalidate) {
        verticalBoxEast.remove(layersContainer);
        if (revalidate) {
            verticalBoxEast.revalidate();
        }
    }

    public void showLayers(boolean revalidate) {
        verticalBoxEast.add(layersContainer);
        if (revalidate) {
            verticalBoxEast.revalidate();
        }
    }

    public boolean areLayersShown() {
        return (layersContainer.getParent() != null);
    }

    public void hideTools(boolean revalidate) {
        verticalBoxWest.remove(toolsPanel);
        verticalBoxWest.remove(fgBgColorSelector);
        remove(ToolSettingsPanel.INSTANCE);

        if (revalidate) {
            ((JComponent) getContentPane()).revalidate();
        }
    }

    public void showTools(boolean revalidate) {
        verticalBoxWest.add(toolsPanel);
        verticalBoxWest.add(fgBgColorSelector);
        add(ToolSettingsPanel.INSTANCE, BorderLayout.NORTH);

        if (revalidate) {
            ((JComponent) getContentPane()).revalidate();
        }
    }

    public boolean areToolsShown() {
        return (toolsPanel.getParent() != null);
    }

    /**
     * Each image has its own LayersPanel object, and when a new imege is activated, this
     * method is called
     */
    public void showLayersPanel(LayersPanel p) {
        layersContainer.setLayersPanel(p);
    }
}
