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
package pixelitor.tools;

import java.awt.MultipleGradientPaint;
import java.awt.Component;
import java.awt.event.MouseEvent;

import javax.swing.*;

/**
 *
 */
public class GradientTool extends Tool {
    private boolean thereWasDragging = false;

    private static final String NO_CYCLE_AS_STRING = "No Cycle";
    private static final String REFLECT_AS_STRING = "Reflect";
    private static final String NEPEAT_AS_STRING = "Repeat";

    private JComboBox colorTypeSelector;
    private JComboBox typeSelector;
    private JComboBox cycleMethodSelector;
//    private LinearIntParam opacityParam;
    private JCheckBox invertCheckBox;

    GradientTool() {
    }

    @Override
    public void initSettingsPanel(JPanel p) {
        p.add(new JLabel("Gradient Type: "));
        typeSelector = new JComboBox(new Object[]{
                Gradient.Type.LINEAR,
                Gradient.Type.RADIAL});
        p.add(typeSelector);

        p.add(new JLabel("Color Type: "));
        colorTypeSelector = new JComboBox(new Object[]{
                Gradient.ColorType.FG_TO_BG,
                Gradient.ColorType.BLACK_TO_WHITE});
        p.add(colorTypeSelector);

        p.add(new JLabel("Cycling: "));
        // cycle methods cannot be put directly in the JComboBox, because they would be all uppercase
        cycleMethodSelector = new JComboBox(new String[]{
                NO_CYCLE_AS_STRING,
                REFLECT_AS_STRING,
                NEPEAT_AS_STRING});
        p.add(cycleMethodSelector);

//        opacityParam = new LinearIntParam("opacity", 0, 100, 100);
//        SliderSpinner opacitySlider = new SliderSpinner(opacityParam, null, true, SliderSpinner.TextPosition.LABEL);
//        p.add(opacitySlider);

        p.add(new JLabel("Invert: "));
        invertCheckBox = new JCheckBox();
        p.add(invertCheckBox);
    }

    private MultipleGradientPaint.CycleMethod getCycleMethodFromString(String s) {
        if(s.equals(NO_CYCLE_AS_STRING)) {
            return MultipleGradientPaint.CycleMethod.NO_CYCLE;
        } else if(s.equals(REFLECT_AS_STRING)) {
            return MultipleGradientPaint.CycleMethod.REFLECT;
        } else if(s.equals(NEPEAT_AS_STRING)) {
            return MultipleGradientPaint.CycleMethod.REPEAT;
        }
        throw new IllegalStateException("should not get here");
    }

    @Override
    public void mouseReleased(MouseEvent e, JComponent c) {
        super.mouseReleased(e, c);
        if(thereWasDragging) {
            Gradient gradient = Gradient.INSTANCE;
            gradient.setCycleMethod(getCycleMethodFromString((String) cycleMethodSelector.getSelectedItem()));
            gradient.setPoints(start.x, start.y, end.x, end.y);
            Gradient.Type type = (Gradient.Type) typeSelector.getSelectedItem();
            gradient.setType(type);
            Gradient.ColorType colorType = (Gradient.ColorType) colorTypeSelector.getSelectedItem();
            colorType.setInvert(invertCheckBox.isSelected());
            gradient.setColorType(colorType);

//            float opacity = 0.5f;
//            Composite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity);
//            gradient.setComposite(composite);

            gradient.actionPerformed(null);
            thereWasDragging = false;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e, JComponent c) {
        super.mouseClicked(e, c);
        thereWasDragging = false;
    }

    @Override
    public void mouseDragged(MouseEvent e, JComponent c) {
        super.mouseDragged(e, c);
        thereWasDragging = true;
    }

    @Override
    public String getName() {
        return "gradient";
    }
}
