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
package pixelitor.filters.gui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.FlowLayout;
import java.util.Date;

/**
 *
 */
public class TextParam extends AbstractGUIParam {
    private String defaultValue;
    private ParamAdjustingListener adjustingListener;
    private JTextField tf;
    private boolean dontTrigger = false;

    public TextParam(String name, String defaultValue) {
        super(name);
        this.defaultValue = defaultValue;

        tf = new JTextField(defaultValue);
    }

    @Override
    public boolean isSetToDefault() {
        return defaultValue.equals(getValue());
    }

    @Override
    public JComponent createControl() {
        JPanel p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.LEFT));
        p.add(new JLabel(getName() + ": "));
        if (adjustingListener != null) {
            tf.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
//                    adjustingListener.paramAdjusted();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
//                    adjustingListener.paramAdjusted();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    if (!dontTrigger) {
                        adjustingListener.paramAdjusted();
                    }
                }
            });
        }
        p.add(tf);
        return p;
    }

    @Override
    public void reset(boolean triggerAction) {
        if (!triggerAction) {
            dontTrigger = true;
        }
        setValue(defaultValue);
        dontTrigger = false;
    }

    @Override
    public void setAdjustingListener(ParamAdjustingListener listener) {
        this.adjustingListener = listener;
    }

    @Override
    public int getNrOfGridBagCols() {
        return 2;
    }

    @Override
    public void randomize() {
        dontTrigger = true;
        setValue(new Date().toString()); // TODO
        dontTrigger = false;
    }

    public String getValue() {
        return tf.getText();
    }

    public void setValue(String s) {
        tf.setText(s);
    }

    @Override
    public void setDontTrigger(boolean b) {
        dontTrigger = b;
    }
}
