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
package pixelitor.operations.painters;

import pixelitor.operations.gui.AdjustPanel;
import pixelitor.operations.gui.RangeParam;
import pixelitor.operations.gui.ParamAdjustingListener;
import pixelitor.utils.GridBagHelper;
import pixelitor.utils.SliderSpinner;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 */
public class CenteredTextAdjustments extends AdjustPanel implements ChangeListener, ParamAdjustingListener {
    private JTextField textTextField;
    private JComboBox fontFamilyChooserCB;
    private SliderSpinner fontSizeSlider;
    private JCheckBox boldCB;
    private JCheckBox italicCB;
    private EffectsPanel effectsPanel;

    public CenteredTextAdjustments(CenteredText centeredText) {
        super(centeredText);

        Box verticalBox = Box.createVerticalBox();
        JPanel fontPanel = new JPanel();
        initFontPanel(fontPanel);
        verticalBox.add(fontPanel);

        effectsPanel = new EffectsPanel(this);
        verticalBox.add(effectsPanel);

        add(verticalBox);
    }

    private void initFontPanel(JPanel fontPanel) {
        fontPanel.setBorder(BorderFactory.createTitledBorder("Font"));

        fontPanel.setLayout(new GridBagLayout());

        GridBagHelper.addLabel(fontPanel, "Text:", 0, 0);
        textTextField = new JTextField(20);
        textTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                stateChanged(null);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                stateChanged(null);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                stateChanged(null);
            }
        });
        GridBagHelper.addLastControl(fontPanel, textTextField);

        GridBagHelper.addLabel(fontPanel, "Font Size:", 0, 1);
        RangeParam fontSizeParam = new RangeParam("", 1, 200, 50);
        fontSizeSlider = new SliderSpinner(fontSizeParam, false, SliderSpinner.TextPosition.NONE);
        fontSizeParam.setAdjustingListener(this);
//        add(fontSixeSlider, fieldConstraint);
        GridBagHelper.addLastControl(fontPanel, fontSizeSlider);

        GridBagHelper.addLabel(fontPanel, "Font Type:", 0, 2);
        GraphicsEnvironment localGE = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] availableFonts = localGE.getAvailableFontFamilyNames();
        fontFamilyChooserCB = new JComboBox(availableFonts);
        fontFamilyChooserCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stateChanged(null);
            }
        });
        GridBagHelper.addLastControl(fontPanel, fontFamilyChooserCB);

        GridBagHelper.addLabel(fontPanel, "Bold:", 0, 3);
        boldCB = new JCheckBox();
        boldCB.addChangeListener(this);
        GridBagHelper.addControl(fontPanel, boldCB);

        GridBagHelper.addLabel(fontPanel, "Italic:", 2, 3);
        italicCB = new JCheckBox();
        italicCB.addChangeListener(this);
        GridBagHelper.addControl(fontPanel, italicCB);
    }


    private Font getSelectedFont() {
        String fontFamily = (String) fontFamilyChooserCB.getSelectedItem();
        int style = Font.PLAIN;
        if (boldCB.isSelected()) {
            style |= Font.BOLD;
        }
        if (italicCB.isSelected()) {
            style |= Font.ITALIC;
        }
        int size = fontSizeSlider.getCurrentValue();
        return new Font(fontFamily, style, size);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        paramAdjusted();
    }


    @Override
    public void paramAdjusted() {
        CenteredText centeredText = (CenteredText) op;
        centeredText.setText(textTextField.getText());
        centeredText.setFont(getSelectedFont());
        centeredText.setAreaEffects(effectsPanel.getEffects());
        super.executeFilterPreview();
    }
}
