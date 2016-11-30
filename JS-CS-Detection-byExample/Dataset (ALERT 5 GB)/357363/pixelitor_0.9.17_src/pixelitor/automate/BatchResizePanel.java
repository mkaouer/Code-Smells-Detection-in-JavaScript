/*
 * Copyright 2010 L�szl� Bal�zs-Cs�ki
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
package pixelitor.automate;

import pixelitor.utils.IntTextField;
import pixelitor.utils.ValidatedForm;

import javax.swing.*;

/**
 *
 */
class BatchResizePanel extends ValidatedForm {
    private String errorMessage;
    private IODirsSelectorPanel ioPanel = new IODirsSelectorPanel(false);
    private IntTextField withTextField;
    private IntTextField heightTextField;

    BatchResizePanel() {
        JPanel dimensionsPanel = new JPanel();
        dimensionsPanel.add(new JLabel("Max Width:"));
        withTextField = new IntTextField(5);
        withTextField.setText("300");
        dimensionsPanel.add(withTextField);
        dimensionsPanel.add(new JLabel("Max Height:"));
        heightTextField = new IntTextField(5);
        heightTextField.setText("300");
        dimensionsPanel.add(heightTextField);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(dimensionsPanel);
        add(ioPanel);
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public boolean validateData() {
        if (!ioPanel.validateData()) {
            errorMessage = ioPanel.getErrorMessage();
            return false;
        }
        if (withTextField.getText().trim().isEmpty()) {
            errorMessage = "The 'width' field is empty";
            return false;
        }
        if (heightTextField.getText().trim().isEmpty()) {
            errorMessage = "The 'height' field is empty";
            return false;
        }

        return true;
    }

    public void saveValues() {
        ioPanel.saveValues();
    }

    public int getWidthValue() {
        return withTextField.getIntValue();
    }

    public int getHeightValue() {
        return heightTextField.getIntValue();
    }
}
