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
import pixelitor.menus.AboutDialog;
import pixelitor.menus.MenuBar;
import pixelitor.tools.FgBgColorSelector;
import pixelitor.tools.Tool;
import pixelitor.tools.ToolSettingsPanel;
import pixelitor.tools.ToolsPanel;
import pixelitor.utils.AppPreferences;
import pixelitor.utils.GUIUtils;
import pixelitor.utils.HistogramsPanel;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.dnd.DropTarget;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;
import java.io.File;
import java.util.List;
import java.util.Locale;

/**
 * The main window.
 */
public final class PixelitorWindow extends JFrame {
    private static volatile PixelitorWindow singleInstance;

    private ImageComponent activeImageComponent;

    private JDesktopPane desktopPane;
    private JLabel statusBar;
    private HistogramsPanel histogramsPanel;
    private Box verticalBoxEast;
    private Box verticalBoxWest;
    private ToolsPanel toolsPanel;
    private static final int CASCADE_HORIZONTAL_SHIFT = 15;
    private static final int CASCADE_VERTICAL_SHIFT = 25;

    private PixelitorWindow(boolean testing) throws HeadlessException {
        super("Pixelitor " + AboutDialog.VERSION_NUMBER);

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent we) {
                        AppLogic.exitApp();
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
        toolsPanel = new ToolsPanel(desktopPane);

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

        GlobalKeyboardWatch.init();

        if (testing) {
            GUIUtils.testJComponent((JComponent) getContentPane());
        } else {
            pack();
            AppPreferences.loadFramePosition(this);
            setVisible(true);
        }
    }

    public ImageComponent getActiveImageComponent() {
        return activeImageComponent;
    }

    public void setActiveImageComponent(ImageComponent imageComponent, boolean activate) {
        this.activeImageComponent = imageComponent;
        if (activate) {
            if (imageComponent == null) {
                throw new IllegalStateException("imageComponent is null");
            }
            InternalImageFrame internalFrame = activeImageComponent.getInternalFrame();
            activateInternalImageFrame(internalFrame);
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

    private void activateInternalImageFrame(InternalImageFrame frame) {
        if (frame == null) {
            throw new IllegalArgumentException("frame is null");
        }
        desktopPane.getDesktopManager().activateFrame(frame);
    }

    public void addDeserializedComposition(Composition comp, File file) {
        activeImageComponent = new ImageComponent(file, comp);
        comp.restoreAfterDeserialization();

        try {
            addNewImageComponent();
        } catch (Exception e) {
            GUIUtils.showExceptionDialog(e);
        }
    }

    /**
     * If the file argument is not null, then the name argument is ignored
     */
    public void addNewImage(BufferedImage img, File file, String name) {
        activeImageComponent = new ImageComponent(file, name, img);
        activeImageComponent.setCursor(Tool.getCurrentTool().getCursor());

        try {
            addNewImageComponent();
        } catch (Exception e) {
            GUIUtils.showExceptionDialog(e);
        }
    }

    public static void cascadeWindows() {
        List<ImageComponent> imageComponents = AppLogic.getImageComponents();
        for (int i = 0; i < imageComponents.size(); i++) {
            ImageComponent ic = imageComponents.get(i);
            int locationX = CASCADE_HORIZONTAL_SHIFT * i;
            int locationY = CASCADE_VERTICAL_SHIFT * i;
            InternalImageFrame internalFrame = ic.getInternalFrame();
            internalFrame.setLocation(locationX, locationY);
        }
    }

    private void addNewImageComponent() {
        int nrOfOpenImages = AppLogic.getNrOfOpenImages();

        AppLogic.addImageComponent(activeImageComponent);

        int locationX = CASCADE_HORIZONTAL_SHIFT * nrOfOpenImages;
        int locationY = CASCADE_VERTICAL_SHIFT * nrOfOpenImages;
        InternalImageFrame internalFrame = null;
        try {
            internalFrame = new InternalImageFrame(activeImageComponent, locationX, locationY);
        } catch (ClassCastException e) {
            // some nimbus bug com.sun.java.swing.plaf.nimbus.DerivedColor$UIResource cannot be cast to java.awt.Font
            e.printStackTrace();
        }
        internalFrame.setLocation(locationX, locationY);

        activeImageComponent.setInternalFrame(internalFrame);

        desktopPane.add(internalFrame);
        try {
            internalFrame.setSelected(true);
        } catch (PropertyVetoException e) {
            // not important - it occurs sometimes during testing
            // "com.sun.java.swing.plaf.nimbus.InternalFramePainter cannot be cast to java.awt.Color"
//            GUIUtils.showExceptionDialog(e);
            e.printStackTrace();
        }
        desktopPane.getDesktopManager().activateFrame(internalFrame);

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

            Composition comp = AppLogic.getActiveComp();
            if (comp != null) {
                histogramsPanel.updateFromCompIfShown(comp);
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

    public Dimension getDesktopSize() {
        return desktopPane.getSize();
    }

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    createAndShowGUI(args);
                } catch (Exception e) {
                    // it is important to use null parent, because the PixelitorWindow object might not be finished
                    GUIUtils.showExceptionDialog(null, e);
                }
            }
        });
    }


    /**
     * This is called on the EDT
     */
    private static void createAndShowGUI(String[] args) {
        AppLogic.setLF(AppPreferences.loadLFClassName(), null, false);
        PixelitorWindow pw = getInstance();

        // open the files given on the command line
        for (String fileName : args) {
            File f = new File(fileName);
            if (f.exists()) {
                OpenSaveManager.openFile(f);
            } else {
                GUIUtils.showErrorDialog("File not found", "The file \"" + f.getAbsolutePath() + "\" does not exist");
            }
        }

        TipsOfTheDay.showTips(pw, false);

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


        afterStartTestActions(pw);
    }

    /**
     * A possibility for automatic debugging or testing
     */
    private static void afterStartTestActions(PixelitorWindow pw) {
//        pw.dispatchEvent(new KeyEvent(pw, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), KeyEvent.CTRL_MASK, KeyEvent.VK_T, 'T'));
//        NewImage.addNewImage(NewImage.BgFill.WHITE, 600, 400, "Test");

    }



}

