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

import javax.swing.JPanel;

import pixelitor.ImageChangeReason;
import pixelitor.utils.Utils;

public abstract class AdjustPanel extends JPanel {
    protected Operation op;
    private boolean runFiltersIfStateChanged = true;

    protected AdjustPanel(Operation operation) {
        super();
        this.op = operation;
    }

    public void setRunFiltersIfStateChanged(boolean runFiltersIfStateChanged) {
        this.runFiltersIfStateChanged = runFiltersIfStateChanged;
    }

    protected void executeFilterPreview() {
        if(!runFiltersIfStateChanged) {
            return;
        }
        Runnable task = new Runnable(){
            @Override
            public void run() {
                op.execute(ImageChangeReason.OP_PREVIEW);
            }
        };
        Utils.executeWithBusyCursor(this, task);
    }

    public boolean validData() {
        return true;
    }

    // called only then validData() returns false
    public String getErrorMessage() {
        return null;
    }

}
