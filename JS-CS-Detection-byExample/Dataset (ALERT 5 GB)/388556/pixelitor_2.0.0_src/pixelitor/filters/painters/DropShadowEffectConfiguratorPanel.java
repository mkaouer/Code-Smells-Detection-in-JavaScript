/*
 * Copyright 2010 Laszlo Balazs-Csiki
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
package pixelitor.filters.painters;

import pixelitor.filters.gui.AngleParam;
import pixelitor.filters.gui.AngleSelectorComponent;
import pixelitor.filters.gui.ParamAdjustmentListener;
import pixelitor.filters.gui.RangeParam;
import pixelitor.utils.GridBagHelper;
import pixelitor.utils.SliderSpinner;
import pixelitor.utils.Utils;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 *
 */
public class DropShadowEffectConfiguratorPanel extends EffectConfiguratorPanel {
    private final AngleParam angleParam;
    private final RangeParam distanceParam;
    private final RangeParam spreadParam;

    DropShadowEffectConfiguratorPanel(boolean defaultSelected, final Color defaultColor) {
        super("Drop Shadow", defaultSelected, defaultColor);

        distanceParam = new RangeParam("Distance:", 1, 100, 10);
        SliderSpinner distanceSlider = new SliderSpinner(distanceParam, false, SliderSpinner.TextPosition.NONE);
        GridBagHelper.addLabel(this, "Distance:", 0, 3);
        GridBagHelper.addControl(this, distanceSlider);

        angleParam = new AngleParam("Angle", 0.7);
        AngleSelectorComponent angleSelectorComponent = new AngleSelectorComponent(angleParam);
        GridBagHelper.addLabel(this, "Angle:", 0, 4);
        GridBagHelper.addControl(this, angleSelectorComponent);

        spreadParam = new RangeParam("Spread:", 1, 100, 10);
        SliderSpinner spreadSlider = new SliderSpinner(spreadParam, false, SliderSpinner.TextPosition.NONE);
        GridBagHelper.addLabel(this, "Spread:", 0, 5);
        GridBagHelper.addControl(this, spreadSlider);
    }

    @Override
    public void setAdjustmentListener(ParamAdjustmentListener adjustmentListener) {
        super.setAdjustmentListener(adjustmentListener);
        angleParam.setAdjustmentListener(adjustmentListener);
        distanceParam.setAdjustmentListener(adjustmentListener);
        spreadParam.setAdjustmentListener(adjustmentListener);
    }

    public Point2D getOffset() {
        int distance = distanceParam.getValue();
        double angle = angleParam.getValueInRadians();

        return Utils.calculateOffset(distance, angle);
    }


    @Override
    public int getBrushWidth() {
        return spreadParam.getValue();
    }

}
