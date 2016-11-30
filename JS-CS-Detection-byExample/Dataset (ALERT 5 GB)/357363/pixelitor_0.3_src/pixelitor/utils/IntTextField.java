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
package pixelitor.utils;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;
import javax.swing.text.Document;

/**
 * A JTextfield that allows only input consisting of numbers and nothing else.
 */
public class IntTextField extends JTextField implements KeyListener {
    private int maxValue;
    private int minValue;
    private boolean limitRange;

    public IntTextField() {
        init();
    }

    public IntTextField(int columns) {
        super(columns);
        init();
    }

    public IntTextField(Document doc, String text, int columns) {
        super(doc, text, columns);
        init();
    }

    public IntTextField(String text) {
        super(text);
        init();
    }

    public IntTextField(String text, int columns) {
        super(text, columns);
        init();
    }

    private void init() {
        addKeyListener(this);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();
        if (!((Character.isDigit(c) ||
          (c == KeyEvent.VK_BACK_SPACE) ||
          (c == KeyEvent.VK_ENTER) ||
          (c == KeyEvent.VK_DELETE)))) {
            getToolkit().beep();
            e.consume();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public int getIntValue() {
        String s = getText();
        return Integer.parseInt(s);
    }

    public void setRange(int min, int max) {
        this.minValue = min;
        this.maxValue = max;
        this.limitRange = true;
    }
}
