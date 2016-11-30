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
import pixelitor.FgBgColorSelector;
import pixelitor.ImageChangeReason;
import pixelitor.ImageComponent;
import pixelitor.NewImage;
import pixelitor.io.OpenSaveManager;
import pixelitor.io.OutputFormat;
import pixelitor.layers.AddNewLayerAction;
import pixelitor.layers.BlendingMode;
import pixelitor.layers.DeleteActiveLayerAction;
import pixelitor.layers.Layer;
import pixelitor.menus.AboutDialog;
import pixelitor.operations.FillWithColorWheel;
import pixelitor.operations.ValueNoise;
import pixelitor.operations.jhlabsproxies.JHDropShadow;
import pixelitor.operations.painters.CenteredText;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 *
 */
public class ImageTests {

    public static void saveManySplashImages() {
        for (int i = 0; i < 32; i++) {
            createSplashImage(i);

            String fileName = String.format("splash%04d.bmp", i);

            File f = new File("C:\\Documents and Settings\\Laci\\Asztal\\test_results\\" + fileName);
            BufferedImage compositeImage = AppLogic.getActiveCompositeImage();
            System.out.println("ImageTests.saveManySplashImages: saving " + f.toString());
            OpenSaveManager.saveFile(f, compositeImage, OutputFormat.BMP);
            AppLogic.getActiveImageComponent().closeContainer();
            ValueNoise.reseed();
        }
    }

    public static void createSplashImage(int adjust) {
        NewImage.addNewImage(NewImage.BgFill.WHITE, 400, 247, "Splash");
        Layer layer;
        Font font;
        ImageComponent ic = AppLogic.getActiveImageComponent();
        ic.getActiveLayer().setName("Color Wheel");
        new FillWithColorWheel().execute(ImageChangeReason.OP_WITHOUT_DIALOG);

        addNewLayer("Value Noise");
        ValueNoise valueNoise = new ValueNoise();
        valueNoise.execute(ImageChangeReason.OP_WITHOUT_DIALOG);
        layer = ic.getActiveLayer();
        layer.setOpacity(0.3f, true);
        layer.setBlendingMode(BlendingMode.SCREEN, true);

        addNewLayer("Gradient");
        ToolTests.addRadialBWGradientToActiveLayer(ic);
        layer = ic.getActiveLayer();
        layer.setOpacity(0.4f, true);
        layer.setBlendingMode(BlendingMode.LUMINOSITY, true);

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

    private static void addDropShadow() {
        JHDropShadow dropShadow = new JHDropShadow();
        dropShadow.setDistance(5);
        dropShadow.setSoftness(5);
        dropShadow.setOpacity(0.8f);
        dropShadow.execute(ImageChangeReason.OP_WITHOUT_DIALOG);
    }

    private static void addNewLayer(String name) {
        AddNewLayerAction.INSTANCE.actionPerformed(null);
        AppLogic.getActiveImageComponent().getActiveLayer().setName(name);
    }

    public static void testLayers() {
        FgBgColorSelector.setBG(Color.WHITE);
        FgBgColorSelector.setFG(Color.BLACK);
        NewImage.addNewImage(NewImage.BgFill.TRANSPARENT, 400, 400, "Layer Test");
        ImageComponent ic = AppLogic.getActiveImageComponent();

        addTextLayer(ic, "this should be deleted", 0);

        addTextLayer(ic, "this should at the bottom", 100);
        ic.moveActiveLayerToBottom();

        ic.moveLayerSelectionUp();
        ic.moveLayerSelectionUp();
        DeleteActiveLayerAction.INSTANCE.actionPerformed(null);

        addTextLayer(ic, "this should at the top", -100);
        addTextLayer(ic, "this should be selected", 50);
        ic.moveActiveLayerDown();


//        ic.moveActiveLayerDown();
//        ic.flattenImage();


        // merge down
        // HueSat
        // ColorBalance
        // Channel mixer

        ic.imageChanged(true);
    }

    private static void addTextLayer(ImageComponent ic, String text, int translationY) {
        Font font = new Font(Font.SANS_SERIF, Font.BOLD, 20);
        addTextLayer(ic, text, font, translationY, BlendingMode.NORMAL, 1.0f, false);
    }

    private static void addTextLayer(ImageComponent ic, String text, Font font, int translationY, BlendingMode blendingMode, float opacity, boolean dropShadow) {
        CenteredText centeredText;
        Layer layer;
        addNewLayer(text);
        centeredText = CenteredText.INSTANCE;
        centeredText.setText(text);
        centeredText.setFont(font);
        if (dropShadow) {
            centeredText.setAreaEffects(new AreaEffect[]{new ShadowPathEffect()});
        }
        centeredText.execute(ImageChangeReason.OP_WITHOUT_DIALOG);
        layer = ic.getActiveLayer();
        layer.setTranslationY(translationY);
        layer.setOpacity(opacity, true);
        layer.setBlendingMode(blendingMode, true);
    }
}
