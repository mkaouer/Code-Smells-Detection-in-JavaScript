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

package pixelitor.operations.gui;

import pixelitor.AppLogic;
import pixelitor.Composition;
import pixelitor.ImageChangeReason;
import pixelitor.PixelitorWindow;
import pixelitor.operations.Operation;
import pixelitor.operations.Operations;
import pixelitor.utils.OKCancelDialog;

/**
 * A dialog for the operation adjustments
 */
public final class AdjustDialog extends OKCancelDialog {
    private Operation activeOperation;

    private AdjustDialog(AdjustPanel adjustPanel, Operation activeOperation) {
        super(adjustPanel, PixelitorWindow.getInstance(), activeOperation.getMenuName(), false);
        this.activeOperation = activeOperation;
    }

    public static void showDialog(AdjustPanel adjustPanel, Operation activeOperation) {
        AdjustDialog dialog = new AdjustDialog(adjustPanel, activeOperation);
        dialog.setVisible(true);
    }

    @Override
    public void dialogAccepted() {
//        if (activeOperation instanceof Resize) {
//            activeOperation.execute(ImageChangeReason.RESIZE, this);
//        } else {
        Composition comp = AppLogic.getActiveComp();
        comp.changeActiveLayerImage(null, ImageChangeReason.OP_WITH_PREVIEW_FINISHED, activeOperation.getName());
//        }

        Operations.setLastExecutedOperation(activeOperation);
        dispose();
    }

    @Override
    public void dialogCancelled() {
        Composition comp = AppLogic.getActiveComp();
        comp.cancelPreviewing();
        dispose();
    }
}
