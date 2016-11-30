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

import pixelitor.operations.OperationWithParametrizedGUI;
import pixelitor.utils.GridBagHelper;

import javax.swing.*;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;

public class ParametrizedAdjustPanel extends AdjustPanel implements ParamAdjustingListener {
    private static boolean resetParams = true;

    public ParametrizedAdjustPanel(OperationWithParametrizedGUI filter, boolean runFilterImmediately) {
        super(filter);
        setupControls(this, this, filter);

        if (runFilterImmediately) {
            paramAdjusted();
        }
    }

    protected static void setupControls(JPanel panel, ParamAdjustingListener adjustingListener, OperationWithParametrizedGUI filter) {
        ParamSet params = filter.getParams();
        if (resetParams) {
            params.reset(false);
        }

        panel.setLayout(new GridBagLayout());
        int row = 0;
        JPanel buttonsPanel = null;

        params.setAdjustingListener(adjustingListener);
        for (GUIParam param : params) {
            param.setAdjustingListener(adjustingListener);
            JComponent control = param.createControl();

            if (param instanceof ActionParam) { // all the buttons go in one row
                if (buttonsPanel == null) {
                    buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
//                    buttonsPanel.setBorder(BorderFactory.createTitledBorder("Playground"));
                    GridBagHelper.addOnlyControlToRow(panel, buttonsPanel, row);
                }
                buttonsPanel.add(control);
            } else {
                int nrOfGridBagCols = param.getNrOfGridBagCols();
                if (nrOfGridBagCols == 1) {
                    GridBagHelper.addOnlyControlToRow(panel, control, row);
                } else if (nrOfGridBagCols == 2) {
                    GridBagHelper.addLabel(panel, param.getName(), 0, row);
                    GridBagHelper.addLastControl(panel, control);
                }
            }

            row++;
        }
    }

    @Override
    public void paramAdjusted() {
        super.executeFilterPreview();
    }

    public static void setResetParams(boolean resetParams) {
        ParametrizedAdjustPanel.resetParams = resetParams;
    }
}