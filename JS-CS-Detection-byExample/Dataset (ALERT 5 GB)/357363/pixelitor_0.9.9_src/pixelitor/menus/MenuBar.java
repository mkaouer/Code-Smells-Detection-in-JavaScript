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

import pixelitor.AppLogic;
import pixelitor.LayerChangeReason;
import pixelitor.TipsOfTheDay;
import pixelitor.layers.AdjustmentLayer;
import pixelitor.layers.ContentLayer;
import pixelitor.layers.Layer;
import pixelitor.layers.TextLayer;
import pixelitor.operations.ChannelInvert;
import pixelitor.operations.FastEmboss;
import pixelitor.operations.Solarize;
import pixelitor.operations.TileProxy;
import pixelitor.operations.XORTexture;
import pixelitor.operations.comp.Flip;
import pixelitor.operations.comp.Rotate;
import pixelitor.Composition;
import pixelitor.NewImage;
import pixelitor.PixelitorWindow;
import pixelitor.automate.Automate;
import pixelitor.history.History;
import pixelitor.io.OpenSaveManager;
import pixelitor.layers.AddNewLayerAction;
import pixelitor.layers.DeleteActiveLayerAction;
import pixelitor.operations.AddNoise;
import pixelitor.operations.Brightness;
import pixelitor.operations.ChannelMixer;
import pixelitor.operations.CircleToSquare;
import pixelitor.operations.Fade;
import pixelitor.operations.FastBlur;
import pixelitor.operations.Fill;
import pixelitor.operations.FillWithColorWheel;
import pixelitor.operations.HueSat;
import pixelitor.operations.Invert;
import pixelitor.operations.LastOp;
import pixelitor.operations.Magnify;
import pixelitor.operations.Mirror;
import pixelitor.operations.NoDialogPixelOpFactory;
import pixelitor.operations.Posterize;
import pixelitor.operations.RandomLines;
import pixelitor.operations.RandomSpheres;
import pixelitor.operations.StackBlur;
import pixelitor.operations.Threshold;
import pixelitor.operations.ValueNoise;
import pixelitor.operations.convolve.Convolve3x3;
import pixelitor.operations.jhlabsproxies.JHBoxBlur;
import pixelitor.operations.jhlabsproxies.JHCaustics;
import pixelitor.operations.jhlabsproxies.JHCrystallize;
import pixelitor.operations.jhlabsproxies.JHDropShadow;
import pixelitor.operations.jhlabsproxies.JHDryBrush;
import pixelitor.operations.jhlabsproxies.JHEmboss;
import pixelitor.operations.jhlabsproxies.JHFishEye;
import pixelitor.operations.jhlabsproxies.JHFrostedGlass;
import pixelitor.operations.jhlabsproxies.JHGaussianBlur;
import pixelitor.operations.jhlabsproxies.JHGlint;
import pixelitor.operations.jhlabsproxies.JHGlow;
import pixelitor.operations.jhlabsproxies.JHKaleidoscope;
import pixelitor.operations.jhlabsproxies.JHLensBlur;
import pixelitor.operations.jhlabsproxies.JHMedian;
import pixelitor.operations.jhlabsproxies.JHMotionBlur;
import pixelitor.operations.jhlabsproxies.JHOffset;
import pixelitor.operations.jhlabsproxies.JHPinch;
import pixelitor.operations.jhlabsproxies.JHPointillize;
import pixelitor.operations.jhlabsproxies.JHPolarCoordinates;
import pixelitor.operations.jhlabsproxies.JHQuantize;
import pixelitor.operations.jhlabsproxies.JHRays;
import pixelitor.operations.jhlabsproxies.JHReduceNoise;
import pixelitor.operations.jhlabsproxies.JHWaves;
import pixelitor.operations.jhlabsproxies.JHSmartBlur;
import pixelitor.operations.jhlabsproxies.JHSmear;
import pixelitor.operations.jhlabsproxies.JHSparkle;
import pixelitor.operations.jhlabsproxies.JHStamp;
import pixelitor.operations.jhlabsproxies.JHTurbulentDistortion;
import pixelitor.operations.jhlabsproxies.JHUnsharpMask;
import pixelitor.operations.jhlabsproxies.JHVideoFeedback;
import pixelitor.operations.jhlabsproxies.JHWaterRipple;
import pixelitor.operations.jhlabsproxies.JHWrapAroundArc;
import pixelitor.operations.lookup.ColorBalance;
import pixelitor.operations.lookup.Levels;
import pixelitor.operations.lookup.Luminosity;
import pixelitor.operations.lookup.StaticLookupOp;
import pixelitor.operations.lookup.StaticLookupType;
import pixelitor.operations.painters.CenteredText;
import pixelitor.operations.painters.Pinstripes;
import pixelitor.operations.painters.Star;
import pixelitor.utils.AppPreferences;
import pixelitor.utils.FilterCreator;
import pixelitor.utils.GUIUtils;
import pixelitor.utils.HistogramsPanel;
import pixelitor.utils.PerformanceTestingDialog;
import pixelitor.utils.test.ImageTests;
import pixelitor.utils.test.OpTests;
import pixelitor.utils.test.RobotTest;
import pixelitor.utils.test.ToolTests;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * The menu bar of the app
 */
public class MenuBar extends JMenuBar {
    private boolean showDevelopMenu;
    private JMenu helpMenu;
    private JMenu developMenu;

    public MenuBar(JFrame parent) {
        super();

        showDevelopMenu = AppPreferences.loadShowDevelopMenu();

        initFileMenu();
        initEditMenu();
        initColorsMenu();
        initLayerMenu();
        initFilterMenu();
        initViewMenu(parent);
        initDevelopMenu();
        initHelpMenu();
    }

    private void initFileMenu() {
        JMenu fileMenu = new JMenu("File");

        // new image
        MenuFactory.createMenuItem(NewImage.getAction(), KeyStroke.getKeyStroke('N', InputEvent.CTRL_MASK), fileMenu, MenuEnableCondition.ALWAYS);

        // open
        Action openAction = new AbstractAction("Open...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    OpenSaveManager.open();
                } catch (Exception ex) {
                    GUIUtils.showExceptionDialog(ex);
                }
            }
        };
        MenuFactory.createMenuItem(openAction, KeyStroke.getKeyStroke('O', InputEvent.CTRL_MASK), fileMenu, MenuEnableCondition.ALWAYS);

        // recent files
        JMenu recentFiles = RecentFilesMenu.getInstance();
        fileMenu.add(recentFiles);

        // save
        Action saveAction = new AbstractAction("Save") {
            @Override
            public void actionPerformed(ActionEvent e) {
                OpenSaveManager.save(false);
            }
        };
        MenuFactory.createMenuItem(saveAction, KeyStroke.getKeyStroke('S', InputEvent.CTRL_MASK), fileMenu, MenuEnableCondition.IF_THERE_IS_OPEN_IMAGE);

        // save as
        Action saveAsAction = new AbstractAction("Save As...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                OpenSaveManager.save(true);
            }
        };
        MenuFactory.createMenuItem(saveAsAction, KeyStroke.getKeyStroke('S', InputEvent.CTRL_MASK + InputEvent.SHIFT_MASK), fileMenu, MenuEnableCondition.IF_THERE_IS_OPEN_IMAGE);

        // close
        Action closeAction = new AbstractAction("Close") {
            @Override
            public void actionPerformed(ActionEvent e) {
                OpenSaveManager.warnAndCloseImage(AppLogic.getActiveImageComponent());
            }
        };
        MenuFactory.createMenuItem(closeAction, KeyStroke.getKeyStroke('W', InputEvent.CTRL_MASK), fileMenu, MenuEnableCondition.IF_THERE_IS_OPEN_IMAGE);

        // close all
        Action closeAllAction = new AbstractAction("Close All") {
            @Override
            public void actionPerformed(ActionEvent e) {
                OpenSaveManager.warnAndCloseAllImages();
            }
        };
        MenuFactory.createMenuItem(closeAllAction, KeyStroke.getKeyStroke('W', InputEvent.CTRL_MASK | InputEvent.ALT_MASK), fileMenu, MenuEnableCondition.IF_THERE_IS_OPEN_IMAGE);


        initAutomateSubmenu(fileMenu);

        fileMenu.addSeparator();


        // exit
        Action exitAction = new AbstractAction("Exit") {
            @Override
            public void actionPerformed(ActionEvent e) {
                AppLogic.exitApp();
            }
        };
        MenuFactory.createMenuItem(exitAction, null, fileMenu, MenuEnableCondition.ALWAYS);

        this.add(fileMenu);
    }

    private static void initAutomateSubmenu(JMenu fileMenu) {
        JMenu batchSubMenu = new JMenu("Automate");
        fileMenu.add(batchSubMenu);

        Action batchResizeAction = new AbstractAction("Batch Resize...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Automate.batchResize();
                } catch (Exception ex) {
                    GUIUtils.showExceptionDialog(ex);
                }
            }
        };
        MenuFactory.createMenuItem(batchResizeAction, null, batchSubMenu, MenuEnableCondition.ALWAYS);

        Action exportLayersAction = new AbstractAction("Export Layers to PNG...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    OpenSaveManager.exportLayersToPNG();
                } catch (Exception ex) {
                    GUIUtils.showExceptionDialog(ex);
                }
            }
        };
        MenuFactory.createMenuItem(exportLayersAction, null, batchSubMenu, MenuEnableCondition.IF_THERE_IS_OPEN_IMAGE);
    }

    private void initEditMenu() {
        JMenu editMenu = new JMenu("Edit");

        // last op
        MenuFactory.createMenuItem(LastOp.INSTANCE, KeyStroke.getKeyStroke('F', InputEvent.CTRL_MASK), editMenu, MenuEnableCondition.IF_CAN_REPEAT_OPERATION);
        editMenu.addSeparator();

        // undo
        Action undoAction = new AbstractAction("Undo") {
            @Override
            public void actionPerformed(ActionEvent e) {
                History.undo();
            }
        };
        MenuFactory.createMenuItem(undoAction, KeyStroke.getKeyStroke('Z', InputEvent.CTRL_MASK), editMenu, MenuEnableCondition.IF_UNDO_POSSIBLE);

        // undo
        Action redoAction = new AbstractAction("Redo") {
            @Override
            public void actionPerformed(ActionEvent e) {
                History.redo();
            }
        };
        MenuFactory.createMenuItem(redoAction, KeyStroke.getKeyStroke('Z', InputEvent.SHIFT_MASK + InputEvent.CTRL_MASK), editMenu, MenuEnableCondition.IF_REDO_POSSIBLE);

        // fade
        MenuFactory.createMenuItem(new Fade(), KeyStroke.getKeyStroke('F', InputEvent.CTRL_MASK + InputEvent.SHIFT_MASK), editMenu, MenuEnableCondition.IF_FADING_POSSIBLE);

        // copy
        MenuFactory.createMenuItem(new CopyAction(CopyAction.Type.COPY_LAYER), KeyStroke.getKeyStroke('C', InputEvent.CTRL_MASK), editMenu, MenuEnableCondition.IF_THERE_IS_OPEN_IMAGE);
        MenuFactory.createMenuItem(new CopyAction(CopyAction.Type.COPY_COMPOSITE), KeyStroke.getKeyStroke('C', InputEvent.CTRL_MASK + InputEvent.SHIFT_MASK), editMenu, MenuEnableCondition.IF_THERE_IS_OPEN_IMAGE);
        // paste
        MenuFactory.createMenuItem(new PasteAction(), KeyStroke.getKeyStroke('V', InputEvent.CTRL_MASK), editMenu, MenuEnableCondition.ALWAYS);

        // crop
        JMenuItem crop = CropMenuItem.INSTANCE;
        editMenu.add(crop);

        // resize
        Action resizeAction = new AbstractAction("Resize...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                AppLogic.resizeActiveImage();
            }
        };
        MenuFactory.createMenuItem(resizeAction, KeyStroke.getKeyStroke('I', InputEvent.CTRL_MASK | InputEvent.ALT_MASK), editMenu, MenuEnableCondition.IF_THERE_IS_OPEN_IMAGE);

        JMenu rotateSubmenu = new JMenu("Rotate");
        editMenu.add(rotateSubmenu);
        // rotate
        MenuFactory.createMenuItem(new Rotate(90, "Rotate 90\u00B0 CW"), null, rotateSubmenu, MenuEnableCondition.IF_THERE_IS_OPEN_IMAGE);
        MenuFactory.createMenuItem(new Rotate(180, "Rotate 180\u00B0"), null, rotateSubmenu, MenuEnableCondition.IF_THERE_IS_OPEN_IMAGE);
        MenuFactory.createMenuItem(new Rotate(270, "Rotate 90\u00B0 CCW"), null, rotateSubmenu, MenuEnableCondition.IF_THERE_IS_OPEN_IMAGE);
        rotateSubmenu.addSeparator();
        // flip
        MenuFactory.createMenuItem(Flip.createFlipOp(Flip.Direction.HORIZONTAL), null, rotateSubmenu, MenuEnableCondition.IF_THERE_IS_OPEN_IMAGE);
        MenuFactory.createMenuItem(Flip.createFlipOp(Flip.Direction.VERTICAL), null, rotateSubmenu, MenuEnableCondition.IF_THERE_IS_OPEN_IMAGE);

        editMenu.addSeparator();
        // preferences
        Action preferencesAction = new AbstractAction("Preferences...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                AppPreferences.Panel.showInDialog();
            }
        };
        editMenu.add(preferencesAction);

        this.add(editMenu);
    }

    private void initColorsMenu() {
        JMenu colorsMenu = new JMenu("Colors");

        MenuFactory.createMenuItem(new ColorBalance(), KeyStroke.getKeyStroke('B', InputEvent.CTRL_MASK), colorsMenu);
        MenuFactory.createMenuItem(new HueSat(), KeyStroke.getKeyStroke('U', InputEvent.CTRL_MASK), colorsMenu);
        MenuFactory.createMenuItem(new Levels(), KeyStroke.getKeyStroke('L', InputEvent.CTRL_MASK), colorsMenu);
        MenuFactory.createMenuItem(new Brightness(), null, colorsMenu);
        MenuFactory.createMenuItem(new Solarize(), null, colorsMenu);
        MenuFactory.createMenuItem(new Invert(), KeyStroke.getKeyStroke('I', InputEvent.CTRL_MASK), colorsMenu);
        MenuFactory.createMenuItem(new ChannelInvert(), null, colorsMenu);
        MenuFactory.createMenuItem(new ChannelMixer(), null, colorsMenu);

        initExtractChannelsSubMenu(colorsMenu);

        initReduceColorsSubMenu(colorsMenu);

        initFillSubMenu(colorsMenu);

        this.add(colorsMenu);
    }

    private static void initFillSubMenu(JMenu colorsMenu) {
        JMenu fillSubmenu = new JMenu("Fill");
        MenuFactory.createMenuItem(new Fill(Fill.Method.FG), KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, InputEvent.ALT_MASK), fillSubmenu);
        MenuFactory.createMenuItem(new Fill(Fill.Method.BG), KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, InputEvent.CTRL_MASK), fillSubmenu);
        MenuFactory.createMenuItem(new Fill(Fill.Method.BLACK), null, fillSubmenu);
        MenuFactory.createMenuItem(new Fill(Fill.Method.WHITE), null, fillSubmenu);
        MenuFactory.createMenuItem(new Fill(Fill.Method.GRAY), null, fillSubmenu);
        MenuFactory.createMenuItem(new FillWithColorWheel(), null, fillSubmenu);

        colorsMenu.add(fillSubmenu);
    }

    private static void initExtractChannelsSubMenu(JMenu colorsMenu) {
        JMenu channelsSubmenu = new JMenu("Extract Channels");
        colorsMenu.add(channelsSubmenu);

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
        MenuFactory.createMenuItem(new Luminosity(), null, channelsSubmenu);
        MenuFactory.createMenuItem(NoDialogPixelOpFactory.getValueChannelOp(), null, channelsSubmenu);
        MenuFactory.createMenuItem(NoDialogPixelOpFactory.getDesaturateChannelOp(), null, channelsSubmenu);
        channelsSubmenu.addSeparator();
        MenuFactory.createMenuItem(NoDialogPixelOpFactory.getHueChannelOp(), null, channelsSubmenu);
        MenuFactory.createMenuItem(NoDialogPixelOpFactory.getHueInColorsChannelOp(), null, channelsSubmenu);
        MenuFactory.createMenuItem(NoDialogPixelOpFactory.getSaturationChannelOp(), null, channelsSubmenu);
    }

    private static void initReduceColorsSubMenu(JMenu colorsMenu) {
        JMenu reduceColorsSubMenu = new JMenu("Reduce Colors");
        colorsMenu.add(reduceColorsSubMenu);

        MenuFactory.createMenuItem(new JHQuantize(), null, reduceColorsSubMenu);
        MenuFactory.createMenuItem(new Posterize(), null, reduceColorsSubMenu);
        MenuFactory.createMenuItem(new Threshold(), null, reduceColorsSubMenu);
    }

    private void initFilterMenu() {
        JMenu filterMenu = new JMenu("Filter");

        initBlurSubMenu(filterMenu);
        initSharpenSubMenu(filterMenu);
        initDistortSubMenu(filterMenu);
        initLightSubMenu(filterMenu);
        initFunSubMenu(filterMenu);
        initNoiseSubMenu(filterMenu);
        initArtisticSubMenu(filterMenu);
        initOtherSubMenu(filterMenu);

        MenuFactory.createMenuItem(CenteredText.INSTANCE, KeyStroke.getKeyStroke('T'), filterMenu);

    }

    private void initOtherSubMenu(JMenu filterMenu) {
        JMenu otherFiltersSubMenu = new JMenu("Other");
        filterMenu.add(otherFiltersSubMenu);
        MenuFactory.createMenuItem(new Convolve3x3(), null, otherFiltersSubMenu);
        MenuFactory.createMenuItem(new JHOffset(), null, otherFiltersSubMenu);
        MenuFactory.createMenuItem(new JHDropShadow(), null, otherFiltersSubMenu);
        this.add(filterMenu);
    }

    private static void initArtisticSubMenu(JMenu filterMenu) {
        JMenu artisticFiltersSubMenu = new JMenu("Artistic");
        filterMenu.add(artisticFiltersSubMenu);
        MenuFactory.createMenuItem(new JHStamp(), null, artisticFiltersSubMenu);
        MenuFactory.createMenuItem(new JHDryBrush(), null, artisticFiltersSubMenu);
    }

    private static void initBlurSubMenu(JMenu filterMenu) {
        JMenu blurSubMenu = new JMenu("Blur");
        filterMenu.add(blurSubMenu);
//        MenuFactory.createMenuItem(new GaussianBlur(), null, blurSubMenu);
        MenuFactory.createMenuItem(new JHGaussianBlur(), null, blurSubMenu);
        MenuFactory.createMenuItem(new JHSmartBlur(), null, blurSubMenu);
        MenuFactory.createMenuItem(new JHBoxBlur(), null, blurSubMenu);
        MenuFactory.createMenuItem(new FastBlur(), null, blurSubMenu);
        MenuFactory.createMenuItem(new StackBlur(), null, blurSubMenu);
        MenuFactory.createMenuItem(new JHLensBlur(), null, blurSubMenu);
        MenuFactory.createMenuItem(new JHMotionBlur(JHMotionBlur.Mode.MOTION_BLUR), null, blurSubMenu);
        MenuFactory.createMenuItem(new JHMotionBlur(JHMotionBlur.Mode.SPIN_ZOOM_BLUR), null, blurSubMenu);
    }

    private static void initSharpenSubMenu(JMenu filterMenu) {
        JMenu sharpenSubMenu = new JMenu("Sharpen");
        MenuFactory.createMenuItem(new JHUnsharpMask(), null, sharpenSubMenu);
        filterMenu.add(sharpenSubMenu);
    }

    private static void initNoiseSubMenu(JMenu filterMenu) {
        JMenu noiseSubMenu = new JMenu("Noise");
        MenuFactory.createMenuItem(new JHReduceNoise(), null, noiseSubMenu);
        MenuFactory.createMenuItem(new JHMedian(), null, noiseSubMenu);

        noiseSubMenu.addSeparator();
        MenuFactory.createMenuItem(new AddNoise(), null, noiseSubMenu);
        MenuFactory.createMenuItem(new ValueNoise(), null, noiseSubMenu);

        filterMenu.add(noiseSubMenu);
    }

    private static void initLightSubMenu(JMenu filterMenu) {
        JMenu lightSubMenu = new JMenu("Light");
        filterMenu.add(lightSubMenu);
        MenuFactory.createMenuItem(new JHGlow(), null, lightSubMenu);
        MenuFactory.createMenuItem(new JHSparkle(), null, lightSubMenu);
        MenuFactory.createMenuItem(new JHRays(), null, lightSubMenu);
        MenuFactory.createMenuItem(new JHGlint(), null, lightSubMenu);
        MenuFactory.createMenuItem(new JHCaustics(), null, lightSubMenu);
    }

    private static void initDistortSubMenu(JMenu filterMenu) {
        JMenu distortMenu = new JMenu("Distort");
        filterMenu.add(distortMenu);
        MenuFactory.createMenuItem(new JHTurbulentDistortion(), null, distortMenu);
        MenuFactory.createMenuItem(new JHPinch(), null, distortMenu);
        MenuFactory.createMenuItem(new JHFishEye(), null, distortMenu);
        MenuFactory.createMenuItem(new JHWaterRipple(), null, distortMenu);
        MenuFactory.createMenuItem(new JHWaves(), null, distortMenu);
        MenuFactory.createMenuItem(new JHPolarCoordinates(), null, distortMenu);
        MenuFactory.createMenuItem(new TileProxy(), null, distortMenu);
        MenuFactory.createMenuItem(new JHFrostedGlass(), null, distortMenu);
        MenuFactory.createMenuItem(new CircleToSquare(), null, distortMenu);
        MenuFactory.createMenuItem(new JHWrapAroundArc(), null, distortMenu);
    }

    private static void initFunSubMenu(JMenu filterMenu) {
        JMenu funSubMenu = new JMenu("Fun");
        filterMenu.add(funSubMenu);
        MenuFactory.createMenuItem(new JHKaleidoscope(), null, funSubMenu);
        MenuFactory.createMenuItem(new JHCrystallize(), null, funSubMenu);
        MenuFactory.createMenuItem(new JHPointillize(), null, funSubMenu);
        MenuFactory.createMenuItem(new JHVideoFeedback(), null, funSubMenu);
        MenuFactory.createMenuItem(new JHSmear(), null, funSubMenu);
        funSubMenu.addSeparator();
        MenuFactory.createMenuItem(new FastEmboss(), null, funSubMenu);
        MenuFactory.createMenuItem(new JHEmboss(), null, funSubMenu);
    }


    private void initLayerMenu() {
        JMenu layersMenu = new JMenu("Layer");

        layersMenu.add(AddNewLayerAction.INSTANCE);
        layersMenu.add(DeleteActiveLayerAction.INSTANCE);

        AbstractAction flattenImageAction = new AbstractAction("Flatten Image") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Composition comp = AppLogic.getActiveComp();
                comp.flattenImage();
            }
        };
        MenuFactory.createMenuItem(flattenImageAction, null, layersMenu);

        AbstractAction mergeDownAction = new AbstractAction("Merge Down") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Composition comp = AppLogic.getActiveComp();
                comp.mergeDown();
            }
        };
        MenuFactory.createMenuItem(mergeDownAction, KeyStroke.getKeyStroke('E', InputEvent.CTRL_MASK), layersMenu);


        AbstractAction duplicateLayerAction = new AbstractAction("Duplicate Layer") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Composition comp = AppLogic.getActiveComp();
                comp.duplicateLayer();
            }
        };
        MenuFactory.createMenuItem(duplicateLayerAction, KeyStroke.getKeyStroke('J', InputEvent.CTRL_MASK), layersMenu);

        AbstractAction newLayerFromCompositeAction = new AbstractAction("New Layer from Composite") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Composition comp = AppLogic.getActiveComp();
                comp.addNewLayerFromComposite("Composite");
            }
        };
        MenuFactory.createMenuItem(newLayerFromCompositeAction, KeyStroke.getKeyStroke('E', InputEvent.CTRL_MASK + InputEvent.ALT_MASK + InputEvent.SHIFT_MASK), layersMenu);


        initLayerStackSubMenu(layersMenu);
        this.add(layersMenu);
    }

    private static void initLayerStackSubMenu(JMenu layersMenu) {
        JMenu layerStackSubMenu = new JMenu("Layer Stack");

        AbstractAction moveUpAction = new AbstractAction("Raise Layer") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Composition comp = AppLogic.getActiveComp();
                comp.moveActiveLayerUp();
            }
        };
        MenuFactory.createMenuItem(moveUpAction, KeyStroke.getKeyStroke(']', InputEvent.CTRL_MASK), layerStackSubMenu);

        AbstractAction moveDownAction = new AbstractAction("Lower Layer") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Composition comp = AppLogic.getActiveComp();
                comp.moveActiveLayerDown();
            }
        };
        MenuFactory.createMenuItem(moveDownAction, KeyStroke.getKeyStroke('[', InputEvent.CTRL_MASK), layerStackSubMenu);

        AbstractAction moveToLast = new AbstractAction("Layer to Top") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Composition comp = AppLogic.getActiveComp();
                comp.moveActiveLayerToTop();
            }
        };
        MenuFactory.createMenuItem(moveToLast, KeyStroke.getKeyStroke(']', InputEvent.CTRL_MASK + InputEvent.SHIFT_MASK), layerStackSubMenu);

        AbstractAction moveToFirstAction = new AbstractAction("Layer to Bottom") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Composition comp = AppLogic.getActiveComp();
                comp.moveActiveLayerToBottom();
            }
        };
        MenuFactory.createMenuItem(moveToFirstAction, KeyStroke.getKeyStroke('[', InputEvent.CTRL_MASK + InputEvent.SHIFT_MASK), layerStackSubMenu);

        layerStackSubMenu.addSeparator();

        AbstractAction moveSelectionUpAction = new AbstractAction("Raise Layer Selection") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Composition comp = AppLogic.getActiveComp();
                comp.moveLayerSelectionUp();
            }
        };
        MenuFactory.createMenuItem(moveSelectionUpAction, KeyStroke.getKeyStroke(']', InputEvent.ALT_MASK), layerStackSubMenu);

        AbstractAction moveDownSelectionAction = new AbstractAction("Lower Layer Selection") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Composition comp = AppLogic.getActiveComp();
                comp.moveLayerSelectionDown();
            }
        };
        MenuFactory.createMenuItem(moveDownSelectionAction, KeyStroke.getKeyStroke('[', InputEvent.ALT_MASK), layerStackSubMenu);


        layersMenu.add(layerStackSubMenu);
    }


    private void initViewMenu(JFrame parent) {
        JMenu viewMenu = new JMenu("View");
        JMenu lfSubMenu = new LookAndFeelMenu("Look and Feel", parent);
        viewMenu.add(lfSubMenu);

        viewMenu.add(ZoomMenu.INSTANCE); // adds the zoom menu as a submenu

        viewMenu.addSeparator();

        viewMenu.add(new ShowHideStatusBarAction());
        MenuFactory.createMenuItem(new ShowHideHistogramsAction(), KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0), viewMenu, MenuEnableCondition.ALWAYS);
        MenuFactory.createMenuItem(new ShowHideLayersAction(), KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0), viewMenu, MenuEnableCondition.ALWAYS);
        viewMenu.add(new ShowHideToolsAction());
        viewMenu.addSeparator();
        MenuFactory.createMenuItem(ShowHideAllAction.INSTANCE, KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), viewMenu, MenuEnableCondition.ALWAYS);
        viewMenu.addSeparator();
        AbstractAction cascadeWindowsAction = new AbstractAction("Arrange Windows") {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    PixelitorWindow.getInstance().cascadeWindows();
                } catch (Exception ex) {
                    GUIUtils.showExceptionDialog(ex);
                }
            }
        };
        MenuFactory.createMenuItem(cascadeWindowsAction, null, viewMenu);

        this.add(viewMenu);
    }

    private void initDevelopMenu() {
        developMenu = new JMenu("Develop");

        initDebugSubMenu(developMenu);
        initTestSubMenu(developMenu);
        initExperimentalSubMenu(developMenu);

        AbstractAction newTextLayer = new AbstractAction("New Text Layer...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = JOptionPane.showInputDialog(PixelitorWindow.getInstance(), "Text:", "Text Layer Text",  JOptionPane.QUESTION_MESSAGE);
                Composition comp = AppLogic.getActiveComp();
                TextLayer textLayer = new TextLayer(comp, "text layer", s);
                comp.addLayer(textLayer, LayerChangeReason.NEW_LAYER_WITH_CONTENT);
            }
        };
        MenuFactory.createMenuItem(newTextLayer, null, developMenu);

        AbstractAction newAdjustmentLayer = new AbstractAction("New Global Adjustment Layer...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Composition comp = AppLogic.getActiveComp();
                AdjustmentLayer adjustmentLayer = new AdjustmentLayer(comp, "invert adjustment", new Invert());
                comp.addLayer(adjustmentLayer, LayerChangeReason.NEW_LAYER_WITH_CONTENT);
            }
        };
        MenuFactory.createMenuItem(newAdjustmentLayer, null, developMenu);


        AbstractAction filterCreatorAction = new AbstractAction("Filter Creator...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                FilterCreator.showInDialog(PixelitorWindow.getInstance());
            }
        };
        MenuFactory.createMenuItem(filterCreatorAction, null, developMenu, MenuEnableCondition.ALWAYS);

        if (showDevelopMenu) {
            this.add(developMenu);
        }
    }

    private static void initExperimentalSubMenu(JMenu developMenu) {
        JMenu experimentalSubmenu = new JMenu("Experimental");
        developMenu.add(experimentalSubmenu);

        MenuFactory.createMenuItem(new RandomLines(), null, experimentalSubmenu);
        MenuFactory.createMenuItem(new RandomSpheres(), null, experimentalSubmenu);
        MenuFactory.createMenuItem(new Pinstripes(), null, experimentalSubmenu);
//        MenuFactory.createMenuItem(new Gloss(), null, experimentalSubmenu);
        MenuFactory.createMenuItem(new Star(), null, experimentalSubmenu);
        MenuFactory.createMenuItem(new Magnify(), null, experimentalSubmenu);
        MenuFactory.createMenuItem(new Mirror(), null, experimentalSubmenu);
        MenuFactory.createMenuItem(new XORTexture(), null, experimentalSubmenu);
    }

    private static void initTestSubMenu(JMenu developMenu) {
        JMenu testSubMenu = new JMenu("Test");

//        MenuFactory.createMenuItem(new ParamTest(),  KeyStroke.getKeyStroke('T', InputEvent.CTRL_MASK), testSubMenu);

        AbstractAction randomResizeAction = new AbstractAction("Random Resize") {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    OpTests.randomResize();
                } catch (Exception ex) {
                    GUIUtils.showExceptionDialog(ex);
                }
            }
        };
        MenuFactory.createMenuItem(randomResizeAction, null, testSubMenu, MenuEnableCondition.ALWAYS);

        AbstractAction randomBrushAction = new AbstractAction("1001 Random Brush Strokes") {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ToolTests.randomBrushStrokes();
                } catch (Exception ex) {
                    GUIUtils.showExceptionDialog(ex);
                }
            }
        };
        MenuFactory.createMenuItem(randomBrushAction, null, testSubMenu, MenuEnableCondition.ALWAYS);


        AbstractAction robotTestAction = new AbstractAction("Robot Test...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    RobotTest.runRobot();
                } catch (Exception ex) {
                    GUIUtils.showExceptionDialog(ex);
                }
            }
        };
//        MenuFactory.createMenuItem(robotTestAction, KeyStroke.getKeyStroke('R', InputEvent.CTRL_MASK), testSubMenu, MenuEnableCondition.ALWAYS);
        MenuFactory.createMenuItem(robotTestAction, null, testSubMenu, MenuEnableCondition.ALWAYS);


        AbstractAction opPerformanceTestAction = new AbstractAction("Operation Performance Test...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                new PerformanceTestingDialog(PixelitorWindow.getInstance());
            }
        };
        MenuFactory.createMenuItem(opPerformanceTestAction, null, testSubMenu);

        AbstractAction ciPerformanceTestAction = new AbstractAction("getCompositeImage() Performance Test...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                OpTests.getCompositeImagePerformanceTest();
            }
        };
        MenuFactory.createMenuItem(ciPerformanceTestAction, null, testSubMenu);

        testSubMenu.addSeparator();

        AbstractAction runAllOps = new AbstractAction("Run All Operations on Current Layer") {
            @Override
            public void actionPerformed(ActionEvent e) {
                OpTests.runAllOpsOnCurrentLayer();
            }
        };
        MenuFactory.createMenuItem(runAllOps, null, testSubMenu);

        AbstractAction saveAllOps = new AbstractAction("Save the Result of Each Operation...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                OpTests.saveTheResultOfEachOp();
            }
        };
        MenuFactory.createMenuItem(saveAllOps, null, testSubMenu);

        AbstractAction saveInAllFormats = new AbstractAction("Save Current Image in All Formats...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                OpenSaveManager.saveCurrentImageInAllFormats();
            }
        };
        MenuFactory.createMenuItem(saveInAllFormats, null, testSubMenu);


        testSubMenu.addSeparator();

        AbstractAction splashScreenAction = new AbstractAction("Create Pixelitor Splash Image") {
            @Override
            public void actionPerformed(ActionEvent e) {
                ImageTests.createSplashImage(0);
            }
        };
        MenuFactory.createMenuItem(splashScreenAction, null, testSubMenu, MenuEnableCondition.ALWAYS);

        AbstractAction manySplashScreensAction = new AbstractAction("Save Many Splash Images...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                ImageTests.saveManySplashImages();
            }
        };
        MenuFactory.createMenuItem(manySplashScreensAction, null, testSubMenu, MenuEnableCondition.ALWAYS);

        testSubMenu.addSeparator();

        AbstractAction testAllOnNewImg = new AbstractAction("Test Layer Operations") {
            @Override
            public void actionPerformed(ActionEvent e) {
                ImageTests.testLayers();
            }
        };
        MenuFactory.createMenuItem(testAllOnNewImg, null, testSubMenu, MenuEnableCondition.ALWAYS);

        AbstractAction testTools = new AbstractAction("Test Tools") {
            @Override
            public void actionPerformed(ActionEvent e) {
                ToolTests.testTools();
            }
        };
        MenuFactory.createMenuItem(testTools, null, testSubMenu, MenuEnableCondition.ALWAYS);

        AbstractAction testIOOverlayBlur = new AbstractAction("IO Overlay Blur...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                ImageTests.ioOverlayBlur();
            }
        };
        MenuFactory.createMenuItem(testIOOverlayBlur, null, testSubMenu, MenuEnableCondition.ALWAYS);


        developMenu.add(testSubMenu);
    }

    private static void initDebugSubMenu(JMenu develMenu) {
        JMenu debugSubMenu = new JMenu("Debug");

        JMenuItem debugMenuItem = new JMenuItem("Debug App...");
        debugMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AppLogic.showDebugAppDialog();

            }
        });
        debugSubMenu.add(debugMenuItem);

//        JMenuItem transformMenuItem = new JMenuItem("conform images");
//        transformMenuItem.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                AppLogic.conformImages();
//            }
//        });
//        debugSubMenu.add(transformMenuItem);


        AbstractAction imageInfo = new AbstractAction("Image Info...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Composition comp = AppLogic.getActiveComp();
                int canvasWidth = comp.getCanvasWidth();
                int canvasHeight = comp.getCanvasHeight();
                long pixels = canvasWidth * canvasHeight * 4;

                float sizeMBytes = pixels / 1048576.0f;
                String msg = String.format("Canvas Width = %d pixels\nCanvas Height = %d pixels\nSize in Memory = %.2f Mbytes/layer", canvasWidth, canvasHeight, sizeMBytes);
                GUIUtils.showInfoDialog("Image Info - " + comp.getName(), msg);
            }
        };
        MenuFactory.createMenuItem(imageInfo, null, debugSubMenu);

        AbstractAction repaintActive = new AbstractAction("repaint() on the active image") {
            @Override
            public void actionPerformed(ActionEvent e) {
                AppLogic.getActiveImageComponent().repaint();
            }
        };
        MenuFactory.createMenuItem(repaintActive, null, debugSubMenu);

        AbstractAction imageChangedActive = new AbstractAction("imageChanged(true, true) on the active image") {
            @Override
            public void actionPerformed(ActionEvent e) {
                AppLogic.getActiveComp().imageChanged(true, true);
            }
        };
        MenuFactory.createMenuItem(imageChangedActive, null, debugSubMenu);


        AbstractAction revalidateActive = new AbstractAction("revalidate() the main window") {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((JComponent) PixelitorWindow.getInstance().getContentPane()).revalidate();
            }
        };
        MenuFactory.createMenuItem(revalidateActive, null, debugSubMenu, MenuEnableCondition.ALWAYS);

        AbstractAction resetLayerTranslation = new AbstractAction("reset the translation of current layer") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Composition comp = AppLogic.getActiveComp();
                Layer layer = comp.getActiveLayer();
                if(layer instanceof ContentLayer) {
                    ContentLayer contentLayer = (ContentLayer) layer;
                    contentLayer.setTranslationX(0);
                    contentLayer.setTranslationY(0);
                    comp.imageChanged(true, true);
                }
            }
        };
        MenuFactory.createMenuItem(resetLayerTranslation, null, debugSubMenu);


        AbstractAction updateHistogram = new AbstractAction("Update Histograms") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Composition comp = AppLogic.getActiveComp();
                HistogramsPanel.INSTANCE.updateFromCompIfShown(comp);
            }
        };
        MenuFactory.createMenuItem(updateHistogram, null, debugSubMenu);

        AbstractAction saveAllImagesToDir = new AbstractAction("Save All Images to Directory...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                OpenSaveManager.saveAllImagesToDir();
            }
        };
        MenuFactory.createMenuItem(saveAllImagesToDir, null, debugSubMenu);


        develMenu.add(debugSubMenu);
    }

    private void initHelpMenu() {
        helpMenu = new JMenu("Help");

        JMenuItem tipOfTheDayMenuItem = new JMenuItem(new AbstractAction("Tip of the Day") {
            @Override
            public void actionPerformed(ActionEvent e) {
                TipsOfTheDay.showTips(PixelitorWindow.getInstance(), true);
            }
        });
        helpMenu.add(tipOfTheDayMenuItem);

        JMenuItem aboutMenuItem = new JMenuItem(new AbstractAction("About") {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AboutDialog(PixelitorWindow.getInstance());
            }
        });
        helpMenu.add(aboutMenuItem);

        this.add(helpMenu);
    }

    public boolean isDevelopMenuShown() {
        return showDevelopMenu;
    }

    public void setShowDevelopMenu(boolean newShowDevelopMenu) {
        if (newShowDevelopMenu && !showDevelopMenu) { // it was not shown, but it should be
            remove(helpMenu);
            add(developMenu);
            add(helpMenu);
            revalidate();
        } else if (!newShowDevelopMenu && showDevelopMenu) { // it was shown but it shouldn't be
            remove(developMenu);
            revalidate();
        }

        this.showDevelopMenu = newShowDevelopMenu;

    }
}
