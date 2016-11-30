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

package pixelitor.filters;

import javax.swing.JOptionPane;

import pixelitor.AppLogic;
import pixelitor.ImageChangeReason;
import pixelitor.ImageComponent;
import pixelitor.PixelitorWindow;
import pixelitor.utils.OKCancelDialog;

public final class AdjustDialog extends OKCancelDialog {
    private AdjustPanel adjustPanel;
    private Operation activeOperation;

	private AdjustDialog(AdjustPanel adjustPanel, Operation activeOperation) {
		super(adjustPanel, PixelitorWindow.getInstance(), activeOperation.getName(), false);
        this.adjustPanel = adjustPanel;
        this.activeOperation = activeOperation;
    }

    public static void showDialog(AdjustPanel adjustPanel, Operation activeOperation) {
        AdjustDialog dialog = new AdjustDialog(adjustPanel, activeOperation);
        dialog.setVisible(true);
    }

    @Override
    public void dialogAccepted() {
        if(!adjustPanel.validData()) {
            JOptionPane.showMessageDialog(this, adjustPanel.getErrorMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(activeOperation instanceof Resize) {
            activeOperation.execute(ImageChangeReason.RESIZE);
        } else {
            ImageComponent ic = AppLogic.getActiveImageComponent();
            ic.changeActiveLayerImage(null, ImageChangeReason.OP_WITH_PREVIEW_FINISHED);
        }

        Operations.setLastExecutedOperation(activeOperation);
        dispose();
    }

    @Override
    public void dialogCancelled() {
        ImageComponent ic = AppLogic.getActiveImageComponent();
        ic.cancelPreviewing();
        dispose();
    }
}
