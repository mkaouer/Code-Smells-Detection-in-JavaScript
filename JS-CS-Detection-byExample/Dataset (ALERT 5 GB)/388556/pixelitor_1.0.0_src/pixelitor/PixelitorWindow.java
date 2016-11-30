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
import pixelitor.layers.LayersContainer;
import pixelitor.menus.MenuBar;
import pixelitor.tools.FgBgColorSelector;
import pixelitor.tools.ToolSettingsPanelContainer;
import pixelitor.tools.Tools;
import pixelitor.tools.ToolsPanel;
import pixelitor.utils.AppPreferences;
import pixelitor.utils.HistogramsPanel;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.dnd.DropTarget;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;
import java.io.File;
import java.util.List;

/**
 * The main window.
 */
public final class PixelitorWindow extends JFrame {
    private static volatile PixelitorWindow singleInstance;

    private JDesktopPane desktopPane;
    private JLabel statusBar;
    private HistogramsPanel histogramsPanel;
    private Box verticalBoxEast;
    private Box verticalBoxWest;
    private ToolsPanel toolsPanel;
    private static final int CASCADE_HORIZONTAL_SHIFT = 15;
    private static final int CASCADE_VERTICAL_SHIFT = 25;

    private PixelitorWindow() throws HeadlessException {
        super(Build.getPixelitorWindowFixTitle());

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
        GlobalKeyboardWatch.setAlwaysVisibleComponent(desktopPane);
        GlobalKeyboardWatch.registerBrushSizeActions();

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
            add(ToolSettingsPanelContainer.INSTANCE, BorderLayout.NORTH);
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

//        pack();
        AppPreferences.loadFramePosition(this);
        setVisible(true);
    }

    public void activateInternalImageFrame(InternalImageFrame frame) {
        if (frame == null) {
            throw new IllegalArgumentException("frame is null");
        }
        desktopPane.getDesktopManager().activateFrame(frame);
    }

    public static PixelitorWindow getInstance() {
        if (singleInstance == null) {
            synchronized (PixelitorWindow.class) {
                if (singleInstance == null) {
                    singleInstance = new PixelitorWindow();
                }
            }
        }
        return singleInstance;
    }

    public void addDeserializedComposition(Composition comp, File file) {
        AppLogic.activeImageComponent = new ImageComponent(file, comp);
        comp.restoreAfterDeserialization();

        try {
            addNewImageComponent();
        } catch (Exception e) {
            ExceptionHandler.showExceptionDialog(e);
        }
    }

    /**
     * If the file argument is not null, then the name argument is ignored
     */
    public void addNewImage(BufferedImage img, File file, String name) {
        AppLogic.activeImageComponent = new ImageComponent(file, name, img);
        AppLogic.activeImageComponent.addBaseLayer(img);
        AppLogic.activeImageComponent.setCursor(Tools.getCurrentTool().getCursor());

        try {
            addNewImageComponent();
        } catch (Exception e) {
            ExceptionHandler.showExceptionDialog(e);
        }
    }

    public void cascadeWindows() {
        List<ImageComponent> imageComponents = AppLogic.getImageComponents();
        int locationX = 0;
        int locationY = 0;
        for (ImageComponent ic : imageComponents) {
            InternalImageFrame internalFrame = ic.getInternalFrame();
            internalFrame.setLocation(locationX, locationY);
            internalFrame.setToNaturalSize(locationX, locationY);
            try {
                internalFrame.setIcon(false);
                internalFrame.setMaximum(false);
            } catch (PropertyVetoException e) {
                e.printStackTrace();
            }

            locationX += CASCADE_HORIZONTAL_SHIFT;
            locationY += CASCADE_VERTICAL_SHIFT;

            // wrap
            int maxWidth = desktopPane.getWidth() - CASCADE_HORIZONTAL_SHIFT;
            int maxHeight = desktopPane.getHeight() - CASCADE_VERTICAL_SHIFT;

            if (locationX > maxWidth) {
                locationX = 0;
            }
            if (locationY > maxHeight) {
                locationY = 0;
            }
        }
    }

    public void tileWindows() {
        List<ImageComponent> imageComponents = AppLogic.getImageComponents();
        int numComponents = imageComponents.size();

        int rows = (int) Math.sqrt(numComponents);
        int cols = numComponents / rows;
        int extra = numComponents % rows;

        int width = desktopPane.getWidth() / cols;
        int height = desktopPane.getHeight() / rows;
        int currentRow = 0;
        int currentColumn = 0;

        for (ImageComponent ic : imageComponents) {
            InternalImageFrame frame = ic.getInternalFrame();
            try {
                frame.setIcon(false);
                frame.setMaximum(false);
            } catch (PropertyVetoException e) {
                e.printStackTrace();
            }
            frame.reshape(currentColumn * width, currentRow * height, width, height);
            currentRow++;
            if (currentRow == rows) {
                currentRow = 0;
                currentColumn++;
                if (currentColumn == cols - extra) {
                    rows++;
                    height = desktopPane.getHeight() / rows;
                }
            }
        }
    }

    private void addNewImageComponent() {
        int nrOfOpenImages = AppLogic.getNrOfOpenImages();

        AppLogic.addImageComponent(AppLogic.activeImageComponent);

        int locationX = CASCADE_HORIZONTAL_SHIFT * nrOfOpenImages;
        int locationY = CASCADE_VERTICAL_SHIFT * nrOfOpenImages;

        int maxWidth = desktopPane.getWidth() - CASCADE_HORIZONTAL_SHIFT;
        locationX %= maxWidth;

        int maxHeight = desktopPane.getHeight() - CASCADE_VERTICAL_SHIFT;
        locationY %= maxHeight;

        InternalImageFrame internalFrame = new InternalImageFrame(AppLogic.activeImageComponent, locationX, locationY);
        internalFrame.setLocation(locationX, locationY);

        AppLogic.activeImageComponent.setInternalFrame(internalFrame);

        desktopPane.add(internalFrame);
        try {
            internalFrame.setSelected(true);
            desktopPane.getDesktopManager().activateFrame(internalFrame);
            AppLogic.newImageOpened();
        } catch (PropertyVetoException e) {
            ExceptionHandler.showExceptionDialog(e);
        }
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
            add(ToolSettingsPanelContainer.INSTANCE, BorderLayout.NORTH);

        } else {
            verticalBoxWest.remove(toolsPanel);
            verticalBoxWest.remove(FgBgColorSelector.INSTANCE);
            remove(ToolSettingsPanelContainer.INSTANCE);
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

    /**
     * This method iconifies a frame; the maximized bits are not affected.
     */
    public void iconify() {
        int state = getExtendedState();

        // Set the iconified bit
        state |= Frame.ICONIFIED;

        // Iconify the frame
        setExtendedState(state);
    }

    /**
     * This method deiconifies a frame; the maximized bits are not affected.
     */
    public void deiconify() {
        int state = getExtendedState();

        // Clear the iconified bit
        state &= ~Frame.ICONIFIED;

        // Deiconify the frame
        setExtendedState(state);
    }
}

