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

package pixelitor;

import pixelitor.io.DropListener;
import pixelitor.io.OpenSaveManager;
import pixelitor.layers.LayersContainer;
import pixelitor.layers.LayersPanel;
import pixelitor.menus.MenuBar;
import pixelitor.menus.ShowHideAllAction;
import pixelitor.menus.ZoomLevel;
import pixelitor.tools.Tool;
import pixelitor.tools.ToolSettingsPanel;
import pixelitor.tools.ToolsPanel;
import pixelitor.utils.AppPreferences;
import pixelitor.utils.GUIUtils;
import pixelitor.utils.HistogramsPanel;
import pixelitor.utils.test.ImageTests;
import pixelitor.utils.test.ToolTests;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.dnd.DropTarget;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;
import java.io.File;
import java.util.List;

public final class PixelitorWindow extends JFrame {
    private static volatile PixelitorWindow singleInstance;

    private ImageComponent activeImageComponent;

    private JDesktopPane desktopPane;
    private JLabel statusBar;
    private HistogramsPanel histogramsPanel;
    private Box verticalBoxEast;
    private Box verticalBoxWest;
    private ToolsPanel toolsPanel;

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

        MenuBar menuBar = new MenuBar(this);
        setJMenuBar(menuBar);

        desktopPane = new JDesktopPane();
//        desktopPane.setTransferHandler(new DropHandler(null));

        new DropTarget(desktopPane, new DropListener());

        desktopPane.setBackground(Color.GRAY);
        add(desktopPane, BorderLayout.CENTER);

        verticalBoxEast = Box.createVerticalBox();
        histogramsPanel = HistogramsPanel.INSTANCE;
        AppLogic.addImageChangeListener(histogramsPanel);
        if (AppPreferences.WorkSpace.getHistogramsVisibility()) {
            verticalBoxEast.add(histogramsPanel);
        }

        if (AppPreferences.WorkSpace.getLayersVisibility()) {
            verticalBoxEast.add(LayersContainer.INSTANCE);
        }

        add(verticalBoxEast, BorderLayout.EAST);

        verticalBoxWest = Box.createVerticalBox();
        toolsPanel = new ToolsPanel();

        if (AppPreferences.WorkSpace.getToolsVisibility()) {
            verticalBoxWest.add(toolsPanel);
            verticalBoxWest.add(FgBgColorSelector.INSTANCE);
            add(ToolSettingsPanel.INSTANCE, BorderLayout.NORTH);
        }

        add(verticalBoxWest, BorderLayout.WEST);

        statusBar = new JLabel("Pixelitor started");
        statusBar.setBorder(BorderFactory.createEtchedBorder());

        if (AppPreferences.WorkSpace.getStatusBarVisibility()) {
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

    public void setActiveImageComponent(ImageComponent imageComponent, boolean activate) {
        this.activeImageComponent = imageComponent;
        if(activate) {
            if (imageComponent == null) {
                throw new IllegalStateException("imageComponent is null");
            }
            InternalImageFrame internalFrame = activeImageComponent.getInternalFrame();
            activateThis(internalFrame);
            imageComponent.onActivation();
        }
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


    public void activateThis(InternalImageFrame a) {
        desktopPane.getDesktopManager().activateFrame(a);
    }

    public void addNewImage(BufferedImage img, String imageTitle) {
        int nrOfOpenImages = AppLogic.getNrOfOpenImages();

        activeImageComponent = new ImageComponent(imageTitle, img);
        AppLogic.addImageComponent(activeImageComponent);

        InternalImageFrame internalFrame = new InternalImageFrame(activeImageComponent);
        internalFrame.setLocation(15 * nrOfOpenImages, 25 * nrOfOpenImages);

        activeImageComponent.setInternalFrame(internalFrame);

        desktopPane.add(internalFrame);
        try {
            internalFrame.setSelected(true);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        desktopPane.getDesktopManager().activateFrame(internalFrame);


//        internalFrame.setSize(400, 200);


        AppLogic.newImageOpened();
    }

    public void setStatusBarMessage(String msg) {
        statusBar.setText(msg);
    }

    public boolean isStatusBarShown() {
        return (statusBar.getParent() != null);
    }

    public void setStatusBarVisibility(boolean v, boolean revalidate) {
        if (v) {
            add(statusBar, BorderLayout.SOUTH);
        } else {
            remove(statusBar);
        }
        if (revalidate) {
            ((JComponent) getContentPane()).revalidate();
        }
    }

    public void setHistogramsVisibility(boolean v, boolean revalidate) {
        if (v) {
            verticalBoxEast.add(histogramsPanel);
            BufferedImage bi = AppLogic.getActiveCompositeImage();
            if (bi != null) {
                histogramsPanel.updateWithImage(bi);
            }
        } else {
            verticalBoxEast.remove(histogramsPanel);
        }
        if (revalidate) {
            verticalBoxEast.revalidate();
        }
    }

    public boolean areHistogramsShown() {
        return histogramsPanel.areHistogramsShown();
    }

    public void setLayersVisibility(boolean v, boolean revalidate) {
        if (v) {
            verticalBoxEast.add(LayersContainer.INSTANCE);
        } else {
            verticalBoxEast.remove(LayersContainer.INSTANCE);
        }
        if (revalidate) {
            verticalBoxEast.revalidate();
        }
    }


    public boolean areLayersShown() {
        return (LayersContainer.INSTANCE.getParent() != null);
    }


    public void setToolsVisibility(boolean v, boolean revalidate) {
        if (v) {
            verticalBoxWest.add(toolsPanel);
            verticalBoxWest.add(FgBgColorSelector.INSTANCE);
            add(ToolSettingsPanel.INSTANCE, BorderLayout.NORTH);

        } else {
            verticalBoxWest.remove(toolsPanel);
            verticalBoxWest.remove(FgBgColorSelector.INSTANCE);
            remove(ToolSettingsPanel.INSTANCE);
        }
        if (revalidate) {
            ((JComponent) getContentPane()).revalidate();
        }
    }


    public boolean areToolsShown() {
        return (toolsPanel.getParent() != null);
    }

    /**
     * Each image has its own LayersPanel object, and when a new image is activated, this
     * method is called
     */
    public void showLayersPanel(LayersPanel p) {
        LayersContainer.INSTANCE.setLayersPanel(p);
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

    // called on the EDT

    private static void createAndShowGUI(String[] args) {
        try {
            UIManager.setLookAndFeel(AppPreferences.loadLFClassName());
        } catch (Exception e) {
            GUIUtils.showExceptionDialog(null, e);
        }
        PixelitorWindow pw = getInstance();

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
                OpenSaveManager.initOpenFileChooser();
                OpenSaveManager.initSaveFileChooser();
            }
        };
        Thread t = new Thread(loadFileChoosersTask);
        t.setPriority(Thread.NORM_PRIORITY);
        t.start();

        // the following line must be commented out in production builds
 //       afterStartTestActions(pw);
    }

    /**
     * A possibility for automatic debugging or testing
     */
    private static void afterStartTestActions(PixelitorWindow pw) {
//        pw.dispatchEvent(new KeyEvent(pw, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), KeyEvent.CTRL_MASK, KeyEvent.VK_T, 'T'));
//        ImageTests.saveManySplashImages();


//        List<ImageComponent> imageComponents = AppLogic.getImageComponents();
//        for (int i = 0; i < imageComponents.size(); i++) {
//            ImageComponent ic =  imageComponents.get(i);
//            pw.setActiveImageComponent(ic, true);
//
//            ic.addNewEmptyLayer(ic.getName() + " heart");
//            ToolTests.paintHeartShape(ic);
//            ic.addNewEmptyLayer(ic.getName() + " diagonals");
//            ToolTests.paintDiagonals(Tool.BRUSH, ic, 20, 20);
//
//            if(i == 0) {
//                ic.setZoom(ZoomLevel.Z50);
//            } else if(i == 1) {
//                ic.setZoom(ZoomLevel.Z200);
//            }
//        }

    }


}

