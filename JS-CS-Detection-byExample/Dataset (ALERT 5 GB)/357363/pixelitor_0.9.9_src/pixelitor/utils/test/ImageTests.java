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
package pixelitor.utils.test;

import org.jdesktop.swingx.painter.effects.AreaEffect;
import org.jdesktop.swingx.painter.effects.ShadowPathEffect;
import pixelitor.AppLogic;
import pixelitor.Composition;
import pixelitor.ImageChangeReason;
import pixelitor.NewImage;
import pixelitor.PixelitorWindow;
import pixelitor.automate.Automate;
import pixelitor.automate.SingleDirChooserPanel;
import pixelitor.io.OpenSaveManager;
import pixelitor.io.OutputFormat;
import pixelitor.layers.AddNewLayerAction;
import pixelitor.layers.BlendingMode;
import pixelitor.layers.DeleteActiveLayerAction;
import pixelitor.layers.ImageLayer;
import pixelitor.menus.AboutDialog;
import pixelitor.operations.FillWithColorWheel;
import pixelitor.operations.ValueNoise;
import pixelitor.operations.jhlabsproxies.JHDropShadow;
import pixelitor.operations.jhlabsproxies.JHGaussianBlur;
import pixelitor.operations.painters.CenteredText;
import pixelitor.tools.FgBgColorSelector;
import pixelitor.utils.CompositionAction;
import pixelitor.utils.GUIUtils;

import javax.swing.*;
import java.awt.Color;
import java.awt.Font;
import java.io.File;

/**
 *
 */
public class ImageTests {

    public static void saveManySplashImages() {
        boolean okPressed = SingleDirChooserPanel.selectOutputDir(true);
        if (!okPressed) {
            return;
        }

        final ProgressMonitor progressMonitor = new ProgressMonitor(PixelitorWindow.getInstance(),
                "Save Many Splash Images",
                "", 0, 100);

        SwingWorker worker = new SwingWorker<Void, Void>() {
            public Void doInBackground() {
                int nurOfSplashImages = 32;

                for (int i = 0; i < nurOfSplashImages; i++) {
                    String fileName = String.format("splash%04d.bmp", i);

                    progressMonitor.setProgress((int) ((float) i * 100 / nurOfSplashImages));
                    progressMonitor.setNote("Creating " + fileName);
                    if (progressMonitor.isCanceled()) {
                        break;
                    }

                    createSplashImage(i);

                    File lastSaveDir = OpenSaveManager.getLastSaveDir();
                    File f = new File(lastSaveDir, fileName);

                    Composition comp = AppLogic.getActiveComp();

                    OutputFormat.getLastOutputFormat().saveComposition(comp, f);

                    AppLogic.getActiveImageComponent().closeContainer();
                    ValueNoise.reseed();

                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        GUIUtils.showExceptionDialog(e);
                    }
                } // end of for loop
                progressMonitor.close();
                return null;
            } // end of doInBackground()
        };
        worker.execute();

    }

    public static void createSplashImage(int adjust) {
        NewImage.addNewImage(NewImage.BgFill.WHITE, 400, 247, "Splash");
        ImageLayer layer;
        Font font;
        Composition ic = AppLogic.getActiveComp();
        ic.getActiveLayer().setName("Color Wheel");
        new FillWithColorWheel().execute(ImageChangeReason.OP_WITHOUT_DIALOG);

        addNewLayer("Value Noise");
        ValueNoise valueNoise = new ValueNoise();
        valueNoise.setDetails(7);
        valueNoise.execute(ImageChangeReason.OP_WITHOUT_DIALOG);
        layer = (ImageLayer) ic.getActiveLayer();
        layer.setOpacity(0.3f, true, false);
        layer.setBlendingMode(BlendingMode.SCREEN, true, false);

        addNewLayer("Gradient");
        ToolTests.addRadialBWGradientToActiveLayer(ic);
        layer = (ImageLayer) ic.getActiveLayer();
        layer.setOpacity(0.4f, true, false);
        layer.setBlendingMode(BlendingMode.LUMINOSITY, true, false);

        FgBgColorSelector.INSTANCE.setFgColor(Color.WHITE);
        font = new Font("Comic Sans MS", Font.BOLD, 42);
        addTextLayer(ic, "Pixelitor", font, -17, BlendingMode.NORMAL, 0.9f, false);
        addDropShadow();

        font = new Font("Comic Sans MS", Font.BOLD, 24);
        addTextLayer(ic, "Loading...", font, -70, BlendingMode.NORMAL, 0.9f, false);
        addDropShadow();

        font = new Font(Font.SANS_SERIF, Font.PLAIN, 20);
        addTextLayer(ic, "version " + AboutDialog.VERSION_NUMBER, font, 50, BlendingMode.NORMAL, 0.9f, false);
        addDropShadow();

//        font = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
//        addTextLayer(ic, new Date().toString(), font, 0.8f, 100, false);

    }

    public static void ioOverlayBlur() {
        boolean selected = Automate.selectInputAndOutputDir(false, "Overlay Blur - select the input and output folders");
        if (!selected) {
            return;
        }

        CompositionAction ca = new CompositionAction() {
            @Override
            public void process(Composition comp) {
                comp.addNewLayerFromComposite("Overlay Blur");
                comp.getActiveLayer().setBlendingMode(BlendingMode.OVERLAY, true, false);
                JHGaussianBlur blur = new JHGaussianBlur();
                blur.setRadius(5);
                blur.execute(ImageChangeReason.OP_WITHOUT_DIALOG);
            }
        };
        Automate.processEachFile(ca, false, "Overlay Blur Progress");
    }

    private static void addDropShadow() {
        JHDropShadow dropShadow = new JHDropShadow();
        dropShadow.setDistance(5);
        dropShadow.setSoftness(5);
        dropShadow.setOpacity(0.8f);
        dropShadow.execute(ImageChangeReason.OP_WITHOUT_DIALOG);
    }

    private static void addNewLayer(String name) {
        AddNewLayerAction.INSTANCE.actionPerformed(null);
        AppLogic.getActiveLayer().setName(name);
    }

    public static void testLayers() {
        FgBgColorSelector.setBG(Color.WHITE);
        FgBgColorSelector.setFG(Color.BLACK);
        NewImage.addNewImage(NewImage.BgFill.TRANSPARENT, 400, 400, "Layer Test");
        Composition comp = AppLogic.getActiveComp();

        addTextLayer(comp, "this should be deleted", 0);

        addTextLayer(comp, "this should at the bottom", 100);
        comp.moveActiveLayerToBottom();

        comp.moveLayerSelectionUp();
        comp.moveLayerSelectionUp();
        DeleteActiveLayerAction.INSTANCE.actionPerformed(null);

        addTextLayer(comp, "this should at the top", -100);
        addTextLayer(comp, "this should be selected", 50);
        comp.moveActiveLayerDown();


//        ic.moveActiveLayerDown();
//        ic.flattenImage();


        // merge down
        // HueSat
        // ColorBalance
        // Channel mixer

        comp.imageChanged(true, true);
    }

    private static void addTextLayer(Composition ic, String text, int translationY) {
        Font font = new Font(Font.SANS_SERIF, Font.BOLD, 20);
        addTextLayer(ic, text, font, translationY, BlendingMode.NORMAL, 1.0f, false);
    }

    private static void addTextLayer(Composition ic, String text, Font font, int translationY, BlendingMode blendingMode, float opacity, boolean dropShadow) {
        CenteredText centeredText;
        ImageLayer layer;
        addNewLayer(text);
        centeredText = CenteredText.INSTANCE;
        centeredText.setText(text);
        centeredText.setFont(font);
        if (dropShadow) {
            centeredText.setAreaEffects(new AreaEffect[]{new ShadowPathEffect()});
        }
        centeredText.execute(ImageChangeReason.OP_WITHOUT_DIALOG);
        layer = (ImageLayer) ic.getActiveLayer();
        layer.setTranslationY(translationY);
        layer.setOpacity(opacity, true, false);
        layer.setBlendingMode(blendingMode, true, false);
    }
}
