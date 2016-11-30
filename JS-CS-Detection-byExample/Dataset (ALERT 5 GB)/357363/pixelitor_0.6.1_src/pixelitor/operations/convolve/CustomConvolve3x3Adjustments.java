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

package pixelitor.operations.convolve;

import org.jdesktop.swingx.combobox.EnumComboBoxModel;
import pixelitor.operations.gui.AdjustPanel;

import javax.swing.*;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CustomConvolve3x3Adjustments extends AdjustPanel implements
        ActionListener {
    private static final int TEXTFIELDS_PANEL_PREFERRED_WIDTH = 200;

    private JTextField[] textFields;

    private JPanel textFieldsPanel = new JPanel();
    private JButton normalizeButton;

    public CustomConvolve3x3Adjustments(Convolve3x3 filter) {
        super(filter);
        setLayout(new FlowLayout());

        textFields = new JTextField[9];
        for (int i = 0; i < textFields.length; i++) {
            textFields[i] = new JTextField();
        }

        normalizeButton = new JButton("normalize (preserve brightness)");
        normalizeButton.addActionListener(this);

        textFieldsPanel.setLayout(new GridLayout(3, 3));

        for (JTextField textField : textFields) {
            setupTextField(textField);
        }

        reset();

        Box leftVerticalBox = Box.createVerticalBox();
        leftVerticalBox.add(textFieldsPanel);

        leftVerticalBox.add(normalizeButton);
        add(leftVerticalBox);
        textFieldsPanel.setBorder(BorderFactory.createTitledBorder("Kernel"));
        Dimension minimumSize = textFieldsPanel.getMinimumSize();
        textFieldsPanel.setPreferredSize(new Dimension(TEXTFIELDS_PANEL_PREFERRED_WIDTH, minimumSize.height));

        leftVerticalBox.add(Box.createVerticalStrut(40));

        JLabel cmLabel = new JLabel("Convolution method:", JLabel.LEFT);
        cmLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
//        cmLabel.setBorder(BorderFactory.createLineBorder(Color.RED));
        leftVerticalBox.add(cmLabel);
        EnumComboBoxModel<Convolve3x3.ConvolveMethod> convolveMethodModel = filter.getConvolveMethodModel();
        JComboBox convolveMethodCB = new JComboBox(convolveMethodModel);
        leftVerticalBox.add(convolveMethodCB);
        convolveMethodCB.addActionListener(this);

        initPresets();
    }

    private void initPresets() {
        Box box = Box.createVerticalBox();
        box.setBorder(BorderFactory.createTitledBorder("Examples"));

        JButton blurButton = new JButton("Corner Blur");
        blurButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setValues(new float[]{
                        0.25f, 0f, 0.25f,
                        0f, 0f, 0f,
                        0.25f, 0f, 0.25f});
                CustomConvolve3x3Adjustments.this.actionPerformed(e);
            }
        });
        box.add(blurButton);

        JButton blur2Button = new JButton("\"Gaussian\" Blur");
        blur2Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setValues(new float[]{
                        1 / 16f, 2 / 16f, 1 / 16f,
                        2 / 16f, 4 / 16f, 2 / 16f,
                        1 / 16f, 2 / 16f, 1 / 16f});

                CustomConvolve3x3Adjustments.this.actionPerformed(e);
            }
        });
        box.add(blur2Button);

        JButton blur3Button = new JButton("Average Blur");
        blur3Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setValues(new float[]{
                        0.1115f, 0.1115f, 0.1115f,
                        0.1115f, 0.1115f, 0.1115f,
                        0.1115f, 0.1115f, 0.1115f,});

                CustomConvolve3x3Adjustments.this.actionPerformed(e);
            }
        });
        box.add(blur3Button);

        JButton sharpenButton = new JButton("Sharpen");
        sharpenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setValues(new float[]{
                        0f, -1f, 0f,
                        -1f, 5f, -1,
                        0f, -1f, 0f});
                CustomConvolve3x3Adjustments.this.actionPerformed(e);
            }
        });
        box.add(sharpenButton);

        JButton edgeDetectionButton = new JButton("Edge Detection");
        edgeDetectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setValues(new float[]{
                        0f, -1f, 0f,
                        -1f, 4f, -1,
                        0f, -1f, 0f});
                CustomConvolve3x3Adjustments.this.actionPerformed(e);
            }
        });
        box.add(edgeDetectionButton);

        JButton embossButton = new JButton("Emboss");
        embossButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setValues(new float[]{
                        -2, -2, 0,
                        -2, 6, 0,
                        0, 0, 0});
                CustomConvolve3x3Adjustments.this.actionPerformed(e);
            }
        });
        box.add(embossButton);

        JButton emboss2Button = new JButton("Emboss 2");
        emboss2Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setValues(new float[]{
                        -2, 0, 0,
                        0, 0, 0,
                        0, 0, 2});
                CustomConvolve3x3Adjustments.this.actionPerformed(e);
            }
        });
        box.add(emboss2Button);

        JButton emboss3Button = new JButton("Color Emboss");
        emboss3Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setValues(new float[]{
                        -1, -1, 0,
                        -1, 1, 1,
                        0, 1, 1});
                CustomConvolve3x3Adjustments.this.actionPerformed(e);
            }
        });
        box.add(emboss3Button);


        JButton doNothingButton = new JButton("Do Nothing");
        doNothingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reset();
                CustomConvolve3x3Adjustments.this.actionPerformed(e);
            }
        });
        box.add(doNothingButton);
        add(box);
    }

    private void reset() {
        setValues(new float[]{
                0, 0, 0,
                0, 1, 0,
                0, 0, 0});
    }

    private void setupTextField(JTextField textField) {
        textFieldsPanel.add(textField);
        textField.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        float sum = 0;
        float[] values = new float[9];
        for (int i = 0; i < values.length; i++) {
            values[i] = string2float(textFields[i].getText());
            sum += values[i];
        }
        normalizeButton.setEnabled(sum > 1.0);

        if (e.getSource() == normalizeButton) {
            if (sum > 1.0f) {
                for (int i = 0; i < values.length; i++) {
                    values[i] /= sum;
                }

                setValues(values);
            }
        }
        Convolve3x3 kernelFilter = (Convolve3x3) op;
        kernelFilter.setKernelMatrix(values);
        super.executeFilterPreview();
    }

    private void setValues(float[] values) {
        assert values.length == 9;

        float sum = 0;
        for (int i = 0; i < textFields.length; i++) {
            JTextField textField = textFields[i];
            textField.setText(float2String(values[i]));
            sum += values[i];
        }

        normalizeButton.setEnabled(sum > 1.0);
    }

    private String float2String(float f) {
        if (f == 0f) {
            return "";
        }
        return String.valueOf(f);
    }

    private float string2float(String s) {
        if (s.length() == 0) {
            return 0f;
        }
        return Float.parseFloat(s);
    }
}
