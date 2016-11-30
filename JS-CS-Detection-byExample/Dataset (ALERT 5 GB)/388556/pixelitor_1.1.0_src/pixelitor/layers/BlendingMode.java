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
package pixelitor.layers;

import com.jhlabs.composite.ColorBurnComposite;
import com.jhlabs.composite.ColorComposite;
import com.jhlabs.composite.DarkenComposite;
import com.jhlabs.composite.HardLightComposite;
import com.jhlabs.composite.HueComposite;
import com.jhlabs.composite.MultiplyComposite;
import com.jhlabs.composite.OverlayComposite;
import com.jhlabs.composite.SaturationComposite;
import com.jhlabs.composite.ScreenComposite;
import com.jhlabs.composite.SoftLightComposite;
import com.jhlabs.composite.ValueComposite;
import org.jdesktop.swingx.graphics.BlendComposite;

import java.awt.AlphaComposite;
import java.awt.Composite;

/**
 *
 */
public enum BlendingMode {
    NORMAL {
        @Override
        public Composite getComposite(float opacity) {
            return AlphaComposite.SrcOver.derive(opacity);
        }
        @Override
        public String toString() {
            return "Normal";
        }
//    }, TEST {
//        @Override
//        public Composite getComposite(float opacity) {
//            FastMultiplyComposite composite = new FastMultiplyComposite();
//            if (opacity == 1.0f) {
//                return composite;
//            }
//            return new FadingComposite(composite, opacity);
//        }
//        @Override
//        public String toString() {
//            return "Test";
//        }
    }, DARKEN {
        @Override
        public Composite getComposite(float opacity) {
//            return BlendComposite.Darken;
            return new DarkenComposite(opacity);
        }
        @Override
        public String toString() {
            return "Darken";
        }},
    MULTIPLY {
        @Override
        public Composite getComposite(float opacity) {
//            return BlendComposite.Multiply;
            return new MultiplyComposite(opacity);
        }
        @Override
        public String toString() {
            return "Multiply";
        }
    }, COLOR_BURN {
        @Override
        public Composite getComposite(float opacity) {
//            return BlendComposite.ColorBurn;
            return new ColorBurnComposite(opacity);
        }
        @Override
        public String toString() {
            return "Color Burn";
        }
    }, LIGHTEN {
        @Override
        public Composite getComposite(float opacity) {
            return BlendComposite.Lighten;
        }
        @Override
        public String toString() {
            return "Lighten";
        }
    }, SCREEN {
        @Override
        public Composite getComposite(float opacity) {
//            return BlendComposite.Screen;
            return new ScreenComposite(opacity);
        }
        @Override
        public String toString() {
            return "Screen";
        }
    }, COLOR_DODGE {
        @Override
        public Composite getComposite(float opacity) {
            return BlendComposite.ColorDodge;
        }
        @Override
        public String toString() {
            return "Color Dodge";
        }
    }, LINEAR_DODGE {
        @Override
        public Composite getComposite(float opacity) {
            return BlendComposite.Add;
        }
        @Override
        public String toString() {
            return "Linear Dodge (Add)";
        }
    }, OVERLAY {
        @Override
        public Composite getComposite(float opacity) {
//            return BlendComposite.Overlay;
            return new OverlayComposite(opacity);
        }
        @Override
        public String toString() {
            return "Overlay";
        }
    }, SOFT_LIGHT {
        @Override
        public Composite getComposite(float opacity) {
//            return BlendComposite.SoftLight;
            return new SoftLightComposite(opacity);
        }
        @Override
        public String toString() {
            return "Soft Light";
        }
    }, HARD_LIGHT {
        @Override
        public Composite getComposite(float opacity) {
//            return BlendComposite.HardLight;
            return new HardLightComposite(opacity);
        }
        @Override
        public String toString() {
            return "Hard Light";
        }
    }, DIFFERENCE {
        @Override
        public Composite getComposite(float opacity) {
            return BlendComposite.Difference;
        }
        @Override
        public String toString() {
            return "Difference";
        }
    }, EXCLUSION {
        @Override
        public Composite getComposite(float opacity) {
            return BlendComposite.Exclusion;
        }
        @Override
        public String toString() {
            return "Exclusion";
        }
    }, HUE {
        @Override
        public Composite getComposite(float opacity) {
//            return BlendComposite.Hue;
            return new HueComposite(opacity);
        }
        @Override
        public String toString() {
            return "Hue";
        }
    }, SATURATION {
        @Override
        public Composite getComposite(float opacity) {
//            return BlendComposite.Saturation;
            return new SaturationComposite(opacity);
        }
        @Override
        public String toString() {
            return "Saturation";
        }
    }, COLOR {
        @Override
        public Composite getComposite(float opacity) {
//            return BlendComposite.Color;
            return new ColorComposite(opacity);
        }
        @Override
        public String toString() {
            return "Color";
        }
    }, LUMINOSITY {
        @Override
        public Composite getComposite(float opacity) {
//            return BlendComposite.Luminosity;
            return new ValueComposite(opacity);
        }
        @Override
        public String toString() {
//            return "Luminosity";
            return "Value";
        }
    };

    public abstract Composite getComposite(float opacity);
}


//        blendingModes.put("Average", BlendComposite.Average);
//        blendingModes.put("Red", BlendComposite.Red);
//        blendingModes.put("Green", BlendComposite.Green);
//        blendingModes.put("Blue", BlendComposite.Blue);
//        blendingModes.put("Hard Light", BlendComposite.HardLight);
//        blendingModes.put("Heat", BlendComposite.Heat);
//        blendingModes.put("Freeze", BlendComposite.Freeze);
//        blendingModes.put("Glow", BlendComposite.Glow);
//        blendingModes.put("Inverse Color Burn", BlendComposite.InverseColorBurn);
//        blendingModes.put("Inverse Color Dodge", BlendComposite.InverseColorDodge);
//        blendingModes.put("Negation", BlendComposite.Negation);
//        blendingModes.put("Reflect", BlendComposite.Reflect);
//        blendingModes.put("Soft Burn", BlendComposite.SoftBurn);
//        blendingModes.put("Soft Dodge", BlendComposite.SoftDodge);
//        blendingModes.put("Stamp", BlendComposite.Stamp);
