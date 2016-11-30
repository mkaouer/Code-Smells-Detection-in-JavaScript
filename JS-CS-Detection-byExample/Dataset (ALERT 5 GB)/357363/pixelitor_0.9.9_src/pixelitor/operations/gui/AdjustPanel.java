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

import pixelitor.ImageChangeReason;
import pixelitor.operations.Operation;

import javax.swing.*;

/**
 * The superclass of all operation adjustment panels
 */
public abstract class AdjustPanel extends JPanel {
    protected Operation op;

    protected AdjustPanel(Operation operation) {
        super();
        this.op = operation;
    }


    protected void executeFilterPreview() {
        op.execute(ImageChangeReason.OP_PREVIEW, this);
    }

}
