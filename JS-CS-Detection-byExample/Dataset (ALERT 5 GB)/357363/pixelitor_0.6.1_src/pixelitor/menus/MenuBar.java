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
import pixelitor.History;
import pixelitor.ImageComponent;
import pixelitor.NewImage;
import pixelitor.PerformanceTestingDialog;
import pixelitor.PixelitorWindow;
import pixelitor.io.OpenSaveManager;
import pixelitor.layers.AddNewLayerAction;
import pixelitor.layers.DeleteActiveLayerAction;
import pixelitor.layers.Layer;
import pixelitor.operations.AddNoise;
import pixelitor.operations.Brightness;
import pixelitor.operations.ChannelMixer;
import pixelitor.operations.Emboss;
import pixelitor.operations.Magnify;
import pixelitor.operations.Fade;
import pixelitor.operations.FastBlur;
import pixelitor.operations.Fill;
import pixelitor.operations.FillWithColorWheel;
import pixelitor.operations.Flip;
import pixelitor.operations.HueSat;
import pixelitor.operations.LastOp;
import pixelitor.operations.NoDialogPixelOpFactory;
import pixelitor.operations.Posterize;
import pixelitor.operations.RandomLines;
import pixelitor.operations.RandomSpheres;
import pixelitor.operations.Resize;
import pixelitor.operations.Rotate;
import pixelitor.operations.StackBlur;
import pixelitor.operations.ParamTest;
import pixelitor.operations.Threshold;
import pixelitor.operations.ValueNoise;
import pixelitor.operations.Invert;
import pixelitor.operations.convolve.Convolve3x3;
import pixelitor.operations.jhlabsproxies.JHBoxBlur;
import pixelitor.operations.jhlabsproxies.JHCrystallize;
import pixelitor.operations.jhlabsproxies.JHDryBrush;
import pixelitor.operations.jhlabsproxies.JHEmboss;
import pixelitor.operations.jhlabsproxies.JHFrostedGlass;
import pixelitor.operations.jhlabsproxies.JHGaussianBlur;
import pixelitor.operations.jhlabsproxies.JHGlint;
import pixelitor.operations.jhlabsproxies.JHGlow;
import pixelitor.operations.jhlabsproxies.JHKaleidoscope;
import pixelitor.operations.jhlabsproxies.JHLensBlur;
import pixelitor.operations.jhlabsproxies.JHMotionBlur;
import pixelitor.operations.jhlabsproxies.JHOffset;
import pixelitor.operations.jhlabsproxies.JHPinch;
import pixelitor.operations.jhlabsproxies.JHPointillize;
import pixelitor.operations.jhlabsproxies.JHPolarCoordinates;
import pixelitor.operations.jhlabsproxies.JHRays;
import pixelitor.operations.jhlabsproxies.JHReduceNoise;
import pixelitor.operations.jhlabsproxies.JHRipple;
import pixelitor.operations.jhlabsproxies.JHSmartBlur;
import pixelitor.operations.jhlabsproxies.JHSmear;
import pixelitor.operations.jhlabsproxies.JHSparkle;
import pixelitor.operations.jhlabsproxies.JHSphere;
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
import pixelitor.operations.painters.Gloss;
import pixelitor.operations.painters.Pinstripe;
import pixelitor.operations.painters.Star;
import pixelitor.utils.FilterCreator;
import pixelitor.utils.HistogramsPanel;
import pixelitor.utils.test.ImageTests;
import pixelitor.utils.test.OpTests;
import pixelitor.utils.OutputDirChooser;
import pixelitor.utils.debug.AppNode;

import javax.swing.*;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * The menu bar of the app
 */
public class MenuBar extends JMenuBar {

    public MenuBar(JFrame parent) {
        super();

        initFileMenu();
        initEditMenu();
        initAdjustmentsMenu();
        initLayerMenu();
        initFilterMenu();
        initViewMenu(parent);
        initDevelopMenu();
        initHelpMenu();
    }

    private void initFileMenu() {
        JMenu fileMenu = new JMenu("File");

        // new image
        MenuFactory.createMenuItem(NewImage.getAction(), KeyStroke.getKeyStroke('N', InputEvent.CTRL_MASK), fileMenu, MenuFactory.DisableCondition.NEVER);

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
        MenuFactory.createMenuItem(LastOp.INSTANCE, KeyStroke.getKeyStroke('F', InputEvent.CTRL_MASK), editMenu, MenuFactory.DisableCondition.IF_NO_IMAGE_IS_CHANGED);
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

        MenuFactory.createMenuItem(new Invert(), KeyStroke.getKeyStroke('I', InputEvent.CTRL_MASK), adjustmentsMenu);
        MenuFactory.createMenuItem(new ColorBalance(), KeyStroke.getKeyStroke('B', InputEvent.CTRL_MASK), adjustmentsMenu);
        MenuFactory.createMenuItem(new Levels(), KeyStroke.getKeyStroke('L', InputEvent.CTRL_MASK), adjustmentsMenu);
        MenuFactory.createMenuItem(new Brightness(), null, adjustmentsMenu);
        MenuFactory.createMenuItem(new HueSat(), null, adjustmentsMenu);
        MenuFactory.createMenuItem(new ChannelMixer(), null, adjustmentsMenu);
        MenuFactory.createMenuItem(new Posterize(), null, adjustmentsMenu);
        MenuFactory.createMenuItem(new Threshold(), null, adjustmentsMenu);

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
        MenuFactory.createMenuItem(new Luminosity(), null, channelsSubmenu);
        MenuFactory.createMenuItem(NoDialogPixelOpFactory.getValueChannelOp(), null, channelsSubmenu);
        MenuFactory.createMenuItem(NoDialogPixelOpFactory.getDesaturateChannelOp(), null, channelsSubmenu);
        channelsSubmenu.addSeparator();
        MenuFactory.createMenuItem(NoDialogPixelOpFactory.getHueChannelOp(), null, channelsSubmenu);
        MenuFactory.createMenuItem(NoDialogPixelOpFactory.getHueInColorsChannelOp(), null, channelsSubmenu);
        MenuFactory.createMenuItem(NoDialogPixelOpFactory.getSaturationChannelOp(), null, channelsSubmenu);

        JMenu fillSubmenu = new ImageAwareMenu("Fill");
        MenuFactory.createMenuItem(new Fill(Fill.Method.FG), KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, InputEvent.ALT_MASK), fillSubmenu);
        MenuFactory.createMenuItem(new Fill(Fill.Method.BG), KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, InputEvent.CTRL_MASK), fillSubmenu);
        MenuFactory.createMenuItem(new Fill(Fill.Method.BLACK), null, fillSubmenu);
        MenuFactory.createMenuItem(new Fill(Fill.Method.WHITE), null, fillSubmenu);
        MenuFactory.createMenuItem(new Fill(Fill.Method.GRAY), null, fillSubmenu);
        MenuFactory.createMenuItem(new FillWithColorWheel(), null, fillSubmenu);

        adjustmentsMenu.add(fillSubmenu);

        this.add(adjustmentsMenu);
    }

    private void initFilterMenu() {
        JMenu filterMenu = new JMenu("Filter");

        initBlurSubMenu(filterMenu);
        initSharpenSubMenu(filterMenu);
        initDistortSubMenu(filterMenu);
        initLightSubMenu(filterMenu);
        initFunSubMenu(filterMenu);

        initNoiseSubMenu(filterMenu);

        MenuFactory.createMenuItem(new JHOffset(), null, filterMenu);
        MenuFactory.createMenuItem(new JHStamp(), null, filterMenu);
        MenuFactory.createMenuItem(new JHDryBrush(), null, filterMenu);

        MenuFactory.createMenuItem(CenteredText.INSTANCE, KeyStroke.getKeyStroke('T'), filterMenu);

        JMenu advancedFiltersSubMenu = new ImageAwareMenu("Advanced");
        filterMenu.add(advancedFiltersSubMenu);
        MenuFactory.createMenuItem(new Convolve3x3(), null, advancedFiltersSubMenu);
        this.add(filterMenu);
    }

    private void initBlurSubMenu(JMenu filterMenu) {
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

    private void initSharpenSubMenu(JMenu filterMenu) {
        JMenu sharpenSubMenu = new JMenu("Sharpen");
        MenuFactory.createMenuItem(new JHUnsharpMask(), null, sharpenSubMenu);
        filterMenu.add(sharpenSubMenu);
    }

    private void initNoiseSubMenu(JMenu filterMenu) {
        JMenu noiseSubMenu = new JMenu("Noise");
        MenuFactory.createMenuItem(new JHReduceNoise(), null, noiseSubMenu);

        noiseSubMenu.addSeparator();
        MenuFactory.createMenuItem(new AddNoise(), null, noiseSubMenu);
        MenuFactory.createMenuItem(new ValueNoise(), null, noiseSubMenu);

        filterMenu.add(noiseSubMenu);
    }

    private void initLightSubMenu(JMenu filterMenu) {
        JMenu lightSubMenu = new JMenu("Light");
        filterMenu.add(lightSubMenu);
        MenuFactory.createMenuItem(new JHGlow(), null, lightSubMenu);
        MenuFactory.createMenuItem(new JHSparkle(), null, lightSubMenu);
        MenuFactory.createMenuItem(new JHRays(), null, lightSubMenu);
        MenuFactory.createMenuItem(new JHGlint(), null, lightSubMenu);
    }

    private void initDistortSubMenu(JMenu filterMenu) {
        JMenu distortMenu = new JMenu("Distort");
        filterMenu.add(distortMenu);
        MenuFactory.createMenuItem(new JHTurbulentDistortion(), null, distortMenu);
        MenuFactory.createMenuItem(new JHPinch(), null, distortMenu);
        MenuFactory.createMenuItem(new JHSphere(), null, distortMenu);
        MenuFactory.createMenuItem(new JHWaterRipple(), null, distortMenu);
        MenuFactory.createMenuItem(new JHRipple(), null, distortMenu);
        MenuFactory.createMenuItem(new JHPolarCoordinates(), null, distortMenu);
        MenuFactory.createMenuItem(new JHWrapAroundArc(), null, distortMenu);
        MenuFactory.createMenuItem(new JHFrostedGlass(), null, distortMenu);
    }

    private void initFunSubMenu(JMenu filterMenu) {
        JMenu funSubMenu = new JMenu("Fun");
        filterMenu.add(funSubMenu);
        MenuFactory.createMenuItem(new JHKaleidoscope(), null, funSubMenu);
        MenuFactory.createMenuItem(new JHCrystallize(), null, funSubMenu);
        MenuFactory.createMenuItem(new JHPointillize(), null, funSubMenu);
        MenuFactory.createMenuItem(new JHVideoFeedback(), null, funSubMenu);
        MenuFactory.createMenuItem(new JHSmear(), null, funSubMenu);
        funSubMenu.addSeparator();
        MenuFactory.createMenuItem(new Emboss(), null, funSubMenu);
        MenuFactory.createMenuItem(new JHEmboss(), null, funSubMenu);
    }


    private void initLayerMenu() {
        JMenu layersMenu = new JMenu("Layer");

        layersMenu.add(AddNewLayerAction.INSTANCE);
        layersMenu.add(DeleteActiveLayerAction.INSTANCE);

        AbstractAction flattenImageAction = new AbstractAction("Flatten Image") {
            @Override
            public void actionPerformed(ActionEvent e) {
                ImageComponent ic = AppLogic.getActiveImageComponent();
                ic.flattenImage();
            }
        };
        MenuFactory.createMenuItem(flattenImageAction, null, layersMenu);

        AbstractAction mergeDownAction = new AbstractAction("Merge Down") {
            @Override
            public void actionPerformed(ActionEvent e) {
                ImageComponent ic = AppLogic.getActiveImageComponent();
                ic.mergeDown();
            }
        };
        MenuFactory.createMenuItem(mergeDownAction, KeyStroke.getKeyStroke('E', InputEvent.CTRL_MASK), layersMenu);


        AbstractAction duplicateLayerAction = new AbstractAction("Duplicate Layer") {
            @Override
            public void actionPerformed(ActionEvent e) {
                ImageComponent ic = AppLogic.getActiveImageComponent();
                ic.duplicateLayer();
            }
        };
        MenuFactory.createMenuItem(duplicateLayerAction, null, layersMenu);

        initLayerStackSubMenu(layersMenu);
        this.add(layersMenu);
    }

    private void initLayerStackSubMenu(JMenu layersMenu) {
        JMenu layerStackSubMenu = new JMenu("Layer Stack");

        AbstractAction moveUpAction = new AbstractAction("Raise Layer") {
            @Override
            public void actionPerformed(ActionEvent e) {
                ImageComponent ic = AppLogic.getActiveImageComponent();
                ic.moveActiveLayerUp();
            }
        };
        MenuFactory.createMenuItem(moveUpAction, KeyStroke.getKeyStroke(']', InputEvent.CTRL_MASK), layerStackSubMenu);

        AbstractAction moveDownAction = new AbstractAction("Lower Layer") {
            @Override
            public void actionPerformed(ActionEvent e) {
                ImageComponent ic = AppLogic.getActiveImageComponent();
                ic.moveActiveLayerDown();
            }
        };
        MenuFactory.createMenuItem(moveDownAction, KeyStroke.getKeyStroke('[', InputEvent.CTRL_MASK), layerStackSubMenu);

        AbstractAction moveToLast = new AbstractAction("Layer to Top") {
            @Override
            public void actionPerformed(ActionEvent e) {
                ImageComponent ic = AppLogic.getActiveImageComponent();
                ic.moveActiveLayerToTop();
            }
        };
        MenuFactory.createMenuItem(moveToLast, KeyStroke.getKeyStroke(']', InputEvent.CTRL_MASK + InputEvent.SHIFT_MASK), layerStackSubMenu);

        AbstractAction moveToFirstAction = new AbstractAction("Layer to Bottom") {
            @Override
            public void actionPerformed(ActionEvent e) {
                ImageComponent ic = AppLogic.getActiveImageComponent();
                ic.moveActiveLayerToBottom();
            }
        };
        MenuFactory.createMenuItem(moveToFirstAction, KeyStroke.getKeyStroke('[', InputEvent.CTRL_MASK + InputEvent.SHIFT_MASK), layerStackSubMenu);

        layerStackSubMenu.addSeparator();

        AbstractAction moveSelectionUpAction = new AbstractAction("Raise Layer Selection") {
            @Override
            public void actionPerformed(ActionEvent e) {
                ImageComponent ic = AppLogic.getActiveImageComponent();
                ic.moveLayerSelectionUp();
            }
        };
        MenuFactory.createMenuItem(moveSelectionUpAction, KeyStroke.getKeyStroke(']', InputEvent.ALT_MASK), layerStackSubMenu);

        AbstractAction moveDownSelectionAction = new AbstractAction("Lower Layer Selection") {
            @Override
            public void actionPerformed(ActionEvent e) {
                ImageComponent ic = AppLogic.getActiveImageComponent();
                ic.moveLayerSelectionDown();
            }
        };
        MenuFactory.createMenuItem(moveDownSelectionAction, KeyStroke.getKeyStroke('[', InputEvent.ALT_MASK), layerStackSubMenu);


        layersMenu.add(layerStackSubMenu);
    }


    private void initViewMenu(JFrame parent) {
        JMenu viewMenu = new JMenu("View");
        JMenu lfSubMenu = new LookAndFeelMenu("Look and Feel", parent);
        viewMenu.add(lfSubMenu);
        viewMenu.addSeparator();

        viewMenu.add(new ShowHideStatusBarAction());
        MenuFactory.createMenuItem(new ShowHideHistogramsAction(), KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0), viewMenu, MenuFactory.DisableCondition.NEVER);
        MenuFactory.createMenuItem(new ShowHideLayersAction(), KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0), viewMenu, MenuFactory.DisableCondition.NEVER);
        viewMenu.add(new ShowHideToolsAction());
        viewMenu.addSeparator();
        MenuFactory.createMenuItem(ShowHideAllAction.INSTANCE, KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), viewMenu, MenuFactory.DisableCondition.NEVER);

        this.add(viewMenu);
    }

    private void initDevelopMenu() {
        JMenu developMenu = new JMenu("Develop");

        initDebugSubMenu(developMenu);
        initTestSubMenu(developMenu);
        initExperimentalSubMenu(developMenu);

        AbstractAction filterCreatorAction = new AbstractAction("Filter Creator...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                FilterCreator.showInDialog(PixelitorWindow.getInstance());
            }
        };
        MenuFactory.createMenuItem(filterCreatorAction, null, developMenu, MenuFactory.DisableCondition.NEVER);

        this.add(developMenu);
    }

    private void initExperimentalSubMenu(JMenu developMenu) {
        JMenu experimentalSubmenu = new JMenu("Experimental");
        developMenu.add(experimentalSubmenu);

        MenuFactory.createMenuItem(new RandomLines(), null, experimentalSubmenu);
        MenuFactory.createMenuItem(new RandomSpheres(), null, experimentalSubmenu);
        MenuFactory.createMenuItem(new Pinstripe(), null, experimentalSubmenu);
//        MenuFactory.createMenuItem(new Gloss(), null, experimentalSubmenu);
        MenuFactory.createMenuItem(new Star(), null, experimentalSubmenu);
        MenuFactory.createMenuItem(new Magnify(), null, experimentalSubmenu);
    }

    private void initTestSubMenu(JMenu developMenu) {
        JMenu testSubMenu = new JMenu("Test");

//        MenuFactory.createMenuItem(new ParamTest(),  KeyStroke.getKeyStroke('T', InputEvent.CTRL_MASK), testSubMenu);

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

        AbstractAction runAllOps = new AbstractAction("Run All Operations on Current Image") {
            @Override
            public void actionPerformed(ActionEvent e) {
                OpTests.runAllOpsOnCurrentImage();
            }
        };
        MenuFactory.createMenuItem(runAllOps, null, testSubMenu);

        AbstractAction saveAllOps = new AbstractAction("Save the Result of Each Operation") {
            @Override
            public void actionPerformed(ActionEvent e) {
                OpTests.saveTheResultOfEachOp();
            }
        };
        MenuFactory.createMenuItem(saveAllOps, null, testSubMenu);

        AbstractAction saveInAllFormats = new AbstractAction("Save Current Image in All Formats") {
            @Override
            public void actionPerformed(ActionEvent e) {
                BufferedImage image = AppLogic.getActiveCompositeImage();
                File saveDir = OutputDirChooser.getSelectedDir();
                if(saveDir != null) {
                    OpenSaveManager.saveImageInAllFormats(image, saveDir);
                }
            }
        };
        MenuFactory.createMenuItem(saveInAllFormats, null, testSubMenu);


        testSubMenu.addSeparator();

        AbstractAction splashScreenAction = new AbstractAction("Create Pixelitor Splash Screen") {
            @Override
            public void actionPerformed(ActionEvent e) {
                ImageTests.createSplashImage(0);
            }
        };
        MenuFactory.createMenuItem(splashScreenAction, null, testSubMenu, MenuFactory.DisableCondition.NEVER);

        AbstractAction testAllOnNewImg = new AbstractAction("Test Layer Operations") {
            @Override
            public void actionPerformed(ActionEvent e) {
                ImageTests.testLayers();
            }
        };
        MenuFactory.createMenuItem(testAllOnNewImg, null, testSubMenu, MenuFactory.DisableCondition.NEVER);




        developMenu.add(testSubMenu);
    }

    private void initDebugSubMenu(JMenu develMenu) {
        JMenu debugSubMenu = new JMenu("Debug");

        JMenuItem debugMenuItem = new JMenuItem("Debug App");
        debugMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AppNode node = new AppNode();
                String title = "Pixelitor Debug ";
                Component parent1 = PixelitorWindow.getInstance();

                JTree tree = new JTree(node);

                JScrollPane scroll = new JScrollPane(tree);
                scroll.setPreferredSize(new Dimension(500, 500));

                JOptionPane.showMessageDialog(parent1, scroll, title, JOptionPane.INFORMATION_MESSAGE);

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


        AbstractAction repaintActive = new AbstractAction("repaint() on the active image") {
            @Override
            public void actionPerformed(ActionEvent e) {
                AppLogic.getActiveImageComponent().repaint();
            }
        };
        MenuFactory.createMenuItem(repaintActive, null, debugSubMenu);

        AbstractAction imageChangedActive = new AbstractAction("imageChanged() on the active image") {
            @Override
            public void actionPerformed(ActionEvent e) {
                AppLogic.getActiveImageComponent().imageChanged();
            }
        };
        MenuFactory.createMenuItem(imageChangedActive, null, debugSubMenu);


        AbstractAction revalidateActive = new AbstractAction("revalidate() the main window") {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((JComponent) PixelitorWindow.getInstance().getContentPane()).revalidate();
            }
        };
        debugSubMenu.add(revalidateActive);

        AbstractAction resetLayerTranslation = new AbstractAction("reset the translation of current layer") {
            @Override
            public void actionPerformed(ActionEvent e) {
                ImageComponent ic = AppLogic.getActiveImageComponent();
                Layer layer = ic.getActiveLayer();
                layer.setTranslationX(0);
                layer.setTranslationY(0);
                ic.imageChanged();
            }
        };
        debugSubMenu.add(resetLayerTranslation);


        AbstractAction updateHistogram = new AbstractAction("Update Histograms") {
            @Override
            public void actionPerformed(ActionEvent e) {
                ImageComponent ic = AppLogic.getActiveImageComponent();
                HistogramsPanel.INSTANCE.updateWithImage(ic.getCompositeImage());
            }
        };
        MenuFactory.createMenuItem(updateHistogram, null, debugSubMenu);


        develMenu.add(debugSubMenu);
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
