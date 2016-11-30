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

package pixelitor.menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.*;

import pixelitor.*;
import pixelitor.layers.DeleteActiveLayerAction;
import pixelitor.layers.AddEmptyLayerAction;
import pixelitor.filters.*;
import pixelitor.filters.painters.Gloss;
import pixelitor.filters.painters.Pinstripe;
import pixelitor.filters.painters.Text;
import pixelitor.filters.convolve.Blur;
import pixelitor.filters.convolve.CustomConvolveFilter3x3;
import pixelitor.filters.convolve.Sharpen;
import pixelitor.filters.lookup.ColorBalance;
import pixelitor.filters.lookup.Levels;
import pixelitor.filters.lookup.StaticLookupOp;
import pixelitor.filters.lookup.StaticLookupType;
import pixelitor.utils.GUIUtils;
import pixelitor.utils.HistogramsPanel;

public class MenuBar extends JMenuBar {

    public MenuBar(JFrame parent) {
        super();

        initFileMenu();
        initEditMenu();
        initAdjustmentsMenu();
        initFilterMenu();
        initLayerMenu();
        initViewMenu(parent);
        initDebugMenu();
        initHelpMenu();
    }

    private void initFileMenu() {
        JMenu fileMenu = new JMenu("File");

        // new image
        MenuFactory.createMenuItem(NewImagePanel.getAction(), KeyStroke.getKeyStroke('N', InputEvent.CTRL_MASK), fileMenu, MenuFactory.DisableCondition.NEVER);

        // open
        Action openAction = new AbstractAction("Open...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                OpenSaveManager.open();
            }
        };
        MenuFactory.createMenuItem(openAction, KeyStroke.getKeyStroke('O', InputEvent.CTRL_MASK), fileMenu, MenuFactory.DisableCondition.NEVER);

        // recent files
        JMenu recentFiles = RecentFilesMenu.getInstance();
        fileMenu.add(recentFiles);

        // save
        Action saveAction = new AbstractAction("Save As...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                OpenSaveManager.save();
            }
        };
        MenuFactory.createMenuItem(saveAction, KeyStroke.getKeyStroke('S', InputEvent.CTRL_MASK), fileMenu, MenuFactory.DisableCondition.IF_NO_IMAGE_IS_OPEN);

        // close
        Action closeAction = new AbstractAction("Close") {
            @Override
            public void actionPerformed(ActionEvent e) {
                OpenSaveManager.warnAndCloseImage(AppLogic.getActiveImageComponent());
            }
        };
        MenuFactory.createMenuItem(closeAction, KeyStroke.getKeyStroke('W', InputEvent.CTRL_MASK), fileMenu, MenuFactory.DisableCondition.IF_NO_IMAGE_IS_OPEN);

        // close all
        Action closeAllAction = new AbstractAction("Close All") {
            @Override
            public void actionPerformed(ActionEvent e) {
                OpenSaveManager.warnAndCloseAllImages();
            }
        };
        MenuFactory.createMenuItem(closeAllAction, KeyStroke.getKeyStroke('W', InputEvent.CTRL_MASK | InputEvent.ALT_MASK), fileMenu, MenuFactory.DisableCondition.IF_NO_IMAGE_IS_OPEN);

        fileMenu.addSeparator();

        // exit
        Action exitAction = new AbstractAction("Exit") {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        };
        MenuFactory.createMenuItem(exitAction, null, fileMenu, MenuFactory.DisableCondition.NEVER);

        this.add(fileMenu);
    }

    private void initEditMenu() {
        JMenu editMenu = new JMenu("Edit");

        // last op
        MenuFactory.createMenuItem(new LastOp(), KeyStroke.getKeyStroke('F', InputEvent.CTRL_MASK), editMenu, MenuFactory.DisableCondition.IF_NO_IMAGE_IS_CHANGED);
        editMenu.addSeparator();

        // undo
        Action undoAction = new AbstractAction("Undo") {
            @Override
            public void actionPerformed(ActionEvent e) {
                History.undo();
            }
        };
        MenuFactory.createMenuItem(undoAction, KeyStroke.getKeyStroke('Z', InputEvent.CTRL_MASK), editMenu, MenuFactory.DisableCondition.IF_NO_IMAGE_IS_CHANGED);

        // copy
        MenuFactory.createMenuItem(new CopyAction(CopyAction.Type.COPY_LAYER), KeyStroke.getKeyStroke('C', InputEvent.CTRL_MASK), editMenu, MenuFactory.DisableCondition.IF_NO_IMAGE_IS_OPEN);
        MenuFactory.createMenuItem(new CopyAction(CopyAction.Type.COPY_MERGED), KeyStroke.getKeyStroke('C', InputEvent.CTRL_MASK + InputEvent.SHIFT_MASK), editMenu, MenuFactory.DisableCondition.IF_NO_IMAGE_IS_OPEN);
        // paste
        MenuFactory.createMenuItem(new PasteAction(), KeyStroke.getKeyStroke('V', InputEvent.CTRL_MASK), editMenu, MenuFactory.DisableCondition.NEVER);

        // fade
        MenuFactory.createMenuItem(new Fade(), null, editMenu, MenuFactory.DisableCondition.IF_FADING_IS_NOT_POSSIBLE);

        // crop
        JMenuItem crop = CropMenuItem.INSTANCE;
        editMenu.add(crop);

        // resize
        MenuFactory.createMenuItem(new Resize(), KeyStroke.getKeyStroke('I', InputEvent.CTRL_MASK | InputEvent.ALT_MASK), editMenu, MenuFactory.DisableCondition.IF_NO_IMAGE_IS_OPEN);

        JMenu rotateSubmenu = new ImageAwareMenu("Rotate");
        editMenu.add(rotateSubmenu);
        // rotate
        MenuFactory.createMenuItem(new Rotate(90, "Rotate 90\u00B0 CW"), null, rotateSubmenu, MenuFactory.DisableCondition.IF_NO_IMAGE_IS_OPEN);
        MenuFactory.createMenuItem(new Rotate(180, "Rotate 180\u00B0"), null, rotateSubmenu, MenuFactory.DisableCondition.IF_NO_IMAGE_IS_OPEN);
        MenuFactory.createMenuItem(new Rotate(270, "Rotate 90\u00B0 CCW"), null, rotateSubmenu, MenuFactory.DisableCondition.IF_NO_IMAGE_IS_OPEN);
        rotateSubmenu.addSeparator();
        // flip
        MenuFactory.createMenuItem(Flip.createFlipOp(Flip.Direction.HORIZONTAL), null, rotateSubmenu, MenuFactory.DisableCondition.IF_NO_IMAGE_IS_OPEN);
        MenuFactory.createMenuItem(Flip.createFlipOp(Flip.Direction.VERTICAL), null, rotateSubmenu, MenuFactory.DisableCondition.IF_NO_IMAGE_IS_OPEN);

        this.add(editMenu);
    }

    private void initAdjustmentsMenu() {
        JMenu adjustmentsMenu = new JMenu("Adjustments");

        // invert
        MenuFactory.createMenuItem(new StaticLookupOp(StaticLookupType.INVERT), KeyStroke.getKeyStroke('I', InputEvent.CTRL_MASK), adjustmentsMenu);
        // color balance
        MenuFactory.createMenuItem(new ColorBalance(), KeyStroke.getKeyStroke('B', InputEvent.CTRL_MASK), adjustmentsMenu);
        // levels
        MenuFactory.createMenuItem(new Levels(), KeyStroke.getKeyStroke('L', InputEvent.CTRL_MASK), adjustmentsMenu);
        // threshold
        MenuFactory.createMenuItem(new Threshold(), null, adjustmentsMenu);
        // brightness
        MenuFactory.createMenuItem(new Brightness(), null, adjustmentsMenu);

        MenuFactory.createMenuItem(new HueSat(), null, adjustmentsMenu);
        MenuFactory.createMenuItem(new ChannelMixer(), null, adjustmentsMenu);
        JMenu channelsSubmenu = new ImageAwareMenu("Extract Channels");
        adjustmentsMenu.add(channelsSubmenu);

        // red and remove red
        MenuFactory.createMenuItem(new StaticLookupOp(StaticLookupType.RED), null, channelsSubmenu);
        MenuFactory.createMenuItem(NoDialogPixelOpFactory.getRedChannelOp(), null, channelsSubmenu);
        MenuFactory.createMenuItem(new StaticLookupOp(StaticLookupType.REMOVE_RED), null, channelsSubmenu);
        channelsSubmenu.addSeparator();

        // green and remove green
        MenuFactory.createMenuItem(new StaticLookupOp(StaticLookupType.GREEN), null, channelsSubmenu);
        MenuFactory.createMenuItem(NoDialogPixelOpFactory.getGreenChannelOp(), null, channelsSubmenu);
        MenuFactory.createMenuItem(new StaticLookupOp(StaticLookupType.REMOVE_GREEN), null, channelsSubmenu);
        channelsSubmenu.addSeparator();

        // blue and remove blue
        MenuFactory.createMenuItem(new StaticLookupOp(StaticLookupType.BLUE), null, channelsSubmenu);
        MenuFactory.createMenuItem(NoDialogPixelOpFactory.getBlueChannelOp(), null, channelsSubmenu);
        MenuFactory.createMenuItem(new StaticLookupOp(StaticLookupType.REMOVE_BLUE), null, channelsSubmenu);
        channelsSubmenu.addSeparator();
        MenuFactory.createMenuItem(NoDialogPixelOpFactory.getLuminanceChannelOp(), null, channelsSubmenu);
        MenuFactory.createMenuItem(NoDialogPixelOpFactory.getLuminance2ChannelOp(), null, channelsSubmenu);
        MenuFactory.createMenuItem(NoDialogPixelOpFactory.getLuminance3ChannelOp(), null, channelsSubmenu);
        channelsSubmenu.addSeparator();
        MenuFactory.createMenuItem(NoDialogPixelOpFactory.getHueChannelOp(), null, channelsSubmenu);
        MenuFactory.createMenuItem(NoDialogPixelOpFactory.getHueInColorsChannelOp(), null, channelsSubmenu);
        MenuFactory.createMenuItem(NoDialogPixelOpFactory.getSaturationChannelOp(), null, channelsSubmenu);
        MenuFactory.createMenuItem(NoDialogPixelOpFactory.getValueChannelOp(), null, channelsSubmenu);
        MenuFactory.createMenuItem(NoDialogPixelOpFactory.getBrightnessChannelOp(), null, channelsSubmenu);

        JMenu fillSubmenu = new ImageAwareMenu("Fill");
        MenuFactory.createMenuItem(new FillWithColorWheel(), null, fillSubmenu);
        MenuFactory.createMenuItem(new Fill(Fill.Method.FG), KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, InputEvent.ALT_MASK), fillSubmenu);
        MenuFactory.createMenuItem(new Fill(Fill.Method.BG), KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, InputEvent.CTRL_MASK), fillSubmenu);
        MenuFactory.createMenuItem(new Fill(Fill.Method.BLACK), null, fillSubmenu);
        MenuFactory.createMenuItem(new Fill(Fill.Method.WHITE), null, fillSubmenu);
        MenuFactory.createMenuItem(new Fill(Fill.Method.GRAY), null, fillSubmenu);


        adjustmentsMenu.add(fillSubmenu);

        this.add(adjustmentsMenu);
    }

    private void initFilterMenu() {
        JMenu filterMenu = new JMenu("Filter");


        MenuFactory.createMenuItem(new Emboss(), null, filterMenu);
        MenuFactory.createMenuItem(new Sharpen(), null, filterMenu);
        MenuFactory.createMenuItem(new Blur(), null, filterMenu);

        JMenu advancedFiltersSubMenu = new ImageAwareMenu("Advanced");
        filterMenu.add(advancedFiltersSubMenu);
        MenuFactory.createMenuItem(new CustomConvolveFilter3x3(), null, advancedFiltersSubMenu);
        this.add(filterMenu);
    }

    private void initLayerMenu() {
        JMenu layersMenu = new JMenu("Layer");

        layersMenu.add(AddEmptyLayerAction.INSTANCE);
        layersMenu.add(DeleteActiveLayerAction.INSTANCE);

        AbstractAction flattenImageAction = new AbstractAction("Flatten Image") {
            @Override
            public void actionPerformed(ActionEvent e) {
                ImageComponent ic = AppLogic.getActiveImageComponent();
                ic.flattenImage();
            }
        };
        MenuFactory.createMenuItem(flattenImageAction, null, layersMenu);

        AbstractAction duplicateLayerAction = new AbstractAction("Duplicate Layer") {
            @Override
            public void actionPerformed(ActionEvent e) {
                ImageComponent ic = AppLogic.getActiveImageComponent();
                ic.duplicateLayer();
            }
        };
        MenuFactory.createMenuItem(duplicateLayerAction, null, layersMenu);


        this.add(layersMenu);
    }


    private void initViewMenu(JFrame parent) {
        JMenu viewMenu = new JMenu("View");
        JMenu lfSubMenu = new LookAndFeelMenu("Look and Feel", parent);
        viewMenu.add(lfSubMenu);
        viewMenu.addSeparator();

        viewMenu.add(new ShowHideStatusBarAction());
        viewMenu.add(new ShowHideHistogramsAction());
        viewMenu.add(new ShowHideLayersAction());
        viewMenu.add(new ShowHideToolsAction());
        viewMenu.addSeparator();
        MenuFactory.createMenuItem(ShowHideAllAction.INSTANCE, KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), viewMenu, MenuFactory.DisableCondition.NEVER);

        this.add(viewMenu);
    }

    private void initDebugMenu() {
        JMenu develMenu = new JMenu("Development");
        JMenuItem debugMenuItem = new JMenuItem("Debug Images");
        debugMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = AppLogic.getDebugInfo();
                GUIUtils.showLongMessageDialog(s);

            }
        });
        develMenu.add(debugMenuItem);

        JMenuItem layerDebugMenuItem = new JMenuItem("Debug Layers");
        layerDebugMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = AppLogic.getLayerDebugInfo();
                GUIUtils.showLongMessageDialog(s);

            }
        });
        develMenu.add(layerDebugMenuItem);


//        JMenuItem transformMenuItem = new JMenuItem("conform images");
//        transformMenuItem.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                AppLogic.conformImages();
//            }
//        });
//        debugMenu.add(transformMenuItem);


        AbstractAction performanceTestAction = new AbstractAction("Performance Test...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                new PerformanceTestingDialog(PixelitorWindow.getInstance());
            }
        };
        MenuFactory.createMenuItem(performanceTestAction, null, develMenu);

        JMenu experimentalSubmenu = new ImageAwareMenu("Experimental");
        develMenu.add(experimentalSubmenu);

        MenuFactory.createMenuItem(new RandomLines(), null, experimentalSubmenu);
        MenuFactory.createMenuItem(new RandomCircles(), null, experimentalSubmenu);
        MenuFactory.createMenuItem(new Noise(), null, experimentalSubmenu);
        MenuFactory.createMenuItem(new Pinstripe(), null, experimentalSubmenu);
        MenuFactory.createMenuItem(new Gloss(), null, experimentalSubmenu);
        MenuFactory.createMenuItem(new Text(), null, experimentalSubmenu);


        develMenu.addSeparator();

        AbstractAction repaintActive = new AbstractAction("repaint() the active image") {
            @Override
            public void actionPerformed(ActionEvent e) {
                AppLogic.getActiveImageComponent().repaint();
            }
        };
        MenuFactory.createMenuItem(repaintActive, null, develMenu);

        AbstractAction revalidateActive = new AbstractAction("revalidate() the main window") {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((JComponent) PixelitorWindow.getInstance().getContentPane()).revalidate();
            }
        };
        develMenu.add(revalidateActive);

        AbstractAction updateHistogram = new AbstractAction("Update Histograms") {
            @Override
            public void actionPerformed(ActionEvent e) {
                ImageComponent ic = AppLogic.getActiveImageComponent();
                HistogramsPanel.INSTANCE.updateWithImage(ic.getCompositeImage());
            }
        };
        MenuFactory.createMenuItem(updateHistogram, null, develMenu);

        AbstractAction runAllOps = new AbstractAction("Run All Operations") {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<Operation> allOps = Operations.allOps;
                for (Operation op : allOps) {
                    System.out.println("Running " + op.getName());
                    ((AbstractOperation) op).actionPerformed(null);
                }
            }
        };
        MenuFactory.createMenuItem(runAllOps, null, develMenu);


        this.add(develMenu);
    }

    private void initHelpMenu() {
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutMenuItem = new JMenuItem(new AbstractAction("About") {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AboutDialog(PixelitorWindow.getInstance());
            }
        });
        helpMenu.add(aboutMenuItem);
        this.add(helpMenu);
    }
}
