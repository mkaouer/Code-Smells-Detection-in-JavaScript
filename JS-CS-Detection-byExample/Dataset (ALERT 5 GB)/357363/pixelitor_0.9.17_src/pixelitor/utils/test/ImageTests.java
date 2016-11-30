/*
 * Copyright 2009-2010 L�szl� Bal�zs-Cs�ki
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
import pixelitor.Build;
import pixelitor.Composition;
import pixelitor.ExceptionHandler;
import pixelitor.FillType;
import pixelitor.ImageChangeReason;
import pixelitor.NewImage;
import pixelitor.PixelitorWindow;
import pixelitor.automate.Automate;
import pixelitor.automate.SingleDirChooserPanel;
import pixelitor.filters.FillWithColorWheel;
import pixelitor.filters.ValueNoise;
import pixelitor.filters.jhlabsproxies.JHDropShadow;
import pixelitor.filters.jhlabsproxies.JHGaussianBlur;
import pixelitor.filters.painters.CenteredText;
import pixelitor.io.OpenSaveManager;
import pixelitor.io.OutputFormat;
import pixelitor.layers.AddNewLayerAction;
import pixelitor.layers.BlendingMode;
import pixelitor.layers.DeleteActiveLayerAction;
import pixelitor.layers.ImageLayer;
import pixelitor.tools.FgBgColorSelector;
import pixelitor.utils.CompositionAction;

import javax.swing.*;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

/**
 *
 */
public class ImageTests {
    /**
     * Utility class with static methods, must not be instantiated
     */
    private ImageTests() {
    }

    public static void saveManySplashImages() {
        boolean okPressed = SingleDirChooserPanel.selectOutputDir(true);
        if (!okPressed) {
            return;
        }

        final ProgressMonitor progressMonitor = new ProgressMonitor(PixelitorWindow.getInstance(),
                "Save Many Splash Images",
                "", 0, 100);

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            public Void doInBackground() {
                int nurOfSplashImages = 32;

                for (int i = 0; i < nurOfSplashImages; i++) {
                    final OutputFormat outputFormat = OutputFormat.getLastOutputFormat();

                    final String fileName = String.format("splash%04d.%s", i, outputFormat.toString());

                    progressMonitor.setProgress((int) ((float) i * 100 / nurOfSplashImages));
                    progressMonitor.setNote("Creating " + fileName);
                    if (progressMonitor.isCanceled()) {
                        break;
                    }

                    Runnable guiTask = new Runnable() {
                        @Override
                        public void run() {
                            createSplashImage();

                            File lastSaveDir = OpenSaveManager.getLastSaveDir();
                            File f = new File(lastSaveDir, fileName);

                            Composition comp = AppLogic.getActiveComp();

                            outputFormat.saveComposition(comp, f);

                            AppLogic.getActiveImageComponent().close();
                            ValueNoise.reseed();
                        }
                    };
                    try {
                        EventQueue.invokeAndWait(guiTask);
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        ExceptionHandler.showExceptionDialog(e);
                    } catch (InvocationTargetException e) {
                        ExceptionHandler.showExceptionDialog(e);
                    }
                } // end of for loop
                progressMonitor.close();
                return null;
            } // end of doInBackground()
        };
        worker.execute();

    }

    public static void createSplashImage() {
        assert EventQueue.isDispatchThread();

        NewImage.addNewImage(FillType.WHITE, 400, 247, "Splash");

        Composition ic = AppLogic.getActiveComp();
        ic.getActiveLayer().setName("Color Wheel", true);
        new FillWithColorWheel().execute(ImageChangeReason.OP_WITHOUT_DIALOG);

        addNewLayer("Value Noise");
        ValueNoise valueNoise = new ValueNoise();
        valueNoise.setDetails(7);
        valueNoise.execute(ImageChangeReason.OP_WITHOUT_DIALOG);
        ImageLayer layer = (ImageLayer) ic.getActiveLayer();
        layer.setOpacity(0.3f, true, true);
        layer.setBlendingMode(BlendingMode.SCREEN, true, true);

        addNewLayer("Gradient");
        ToolTests.addRadialBWGradientToActiveLayer(ic);
        layer = (ImageLayer) ic.getActiveLayer();
        layer.setOpacity(0.4f, true, true);
        layer.setBlendingMode(BlendingMode.LUMINOSITY, true, true);

        FgBgColorSelector.INSTANCE.setFgColor(Color.WHITE);
        Font font = new Font("Comic Sans MS", Font.BOLD, 42);
        addTextLayer(ic, "Pixelitor", font, -17, BlendingMode.NORMAL, 0.9f, false);
        addDropShadow();

        font = new Font("Comic Sans MS", Font.BOLD, 24);
        addTextLayer(ic, "Loading...", font, -70, BlendingMode.NORMAL, 0.9f, false);
        addDropShadow();

        font = new Font(Font.SANS_SERIF, Font.PLAIN, 20);
        addTextLayer(ic, "version " + Build.VERSION_NUMBER, font, 50, BlendingMode.NORMAL, 0.9f, false);
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
                comp.getActiveLayer().setBlendingMode(BlendingMode.OVERLAY, true, true);
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
        AddNewLayerAction.INSTANCE.actionPerformed(new ActionEvent(PixelitorWindow.getInstance(), 0, name));
        AppLogic.getActiveLayer().setName(name, true);
    }

    public static void testLayers() {
        FgBgColorSelector.setBG(Color.WHITE);
        FgBgColorSelector.setFG(Color.BLACK);
        NewImage.addNewImage(FillType.TRANSPARENT, 400, 400, "Layer Test");
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
        addNewLayer(text);
        CenteredText centeredText = CenteredText.INSTANCE;
        centeredText.setText(text);
        centeredText.setFont(font);
        if (dropShadow) {
            centeredText.setAreaEffects(new AreaEffect[]{new ShadowPathEffect()});
        }
        centeredText.execute(ImageChangeReason.OP_WITHOUT_DIALOG);
        ImageLayer layer = (ImageLayer) ic.getActiveLayer();
        layer.setTranslationY(translationY);
        layer.setOpacity(opacity, true, true);
        layer.setBlendingMode(blendingMode, true, true);
    }
}
