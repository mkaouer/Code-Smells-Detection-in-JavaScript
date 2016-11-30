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
package pixelitor.utils;

import javax.swing.*;
import java.awt.Frame;

/**
 * An OKCancelDialog that uses a ValidatedForm as the form panel
 */
public class ValidatedDialog extends OKCancelDialog {
    // for some reason it only works if this is declared static
    // otherwise isOkPressed returns false even if it was previously set to true
    // (must be some thread stuff related to modal dialogs and dispose)
    private static boolean okPressed = false;

    public ValidatedDialog(ValidatedForm formPanel, Frame owner, String title, boolean visible) {
        super(formPanel, owner, title, visible);
    }

    public ValidatedDialog(ValidatedForm formPanel, Frame owner, String title, String okText, String cancelText, boolean visible) {
        super(formPanel, owner, title, okText, cancelText, visible);
    }

    @Override
    protected void dialogAccepted() {
        super.dialogAccepted();

        ValidatedForm validatedForm = (ValidatedForm) formPanel;
        if (validatedForm.validateData()) {
            setOkPressed(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, validatedForm.getErrorMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    protected void dialogCancelled() {
        super.dialogCancelled();

        setOkPressed(false);
        dispose();
    }

    public boolean isOkPressed() {
        return okPressed;
    }

    private synchronized void setOkPressed(boolean okPressed) {
        ValidatedDialog.okPressed = okPressed;
    }
}
