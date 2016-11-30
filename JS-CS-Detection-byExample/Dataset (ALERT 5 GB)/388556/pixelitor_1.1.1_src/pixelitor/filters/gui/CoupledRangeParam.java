/*
 * Copyright 2010 László Balázs-Csíki
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
                              
/**
 * Two range params that can be coupled.
 */
public class CoupledRangeParam extends AbstractGUIParam {
    private RangeParam rangeParam1;
    private RangeParam rangeParam2;
    private boolean coupled = true;

    public CoupledRangeParam(String name, int minValue, int maxValue, int defaultValue) {
        this(name, "Horizontal:", "Vertical:", minValue, maxValue, defaultValue);
    }

    public CoupledRangeParam(String name, String firstRangeName, String secondRangeName, int minValue, int maxValue, int defaultValue) {
        super(name);
        rangeParam1 = new RangeParam(firstRangeName, minValue, maxValue, defaultValue);
        rangeParam2 = new RangeParam(secondRangeName, minValue, maxValue, defaultValue);

        rangeParam1.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (coupled) {
                    rangeParam2.setDontTrigger(true);
                    rangeParam2.setValue(rangeParam1.getValue());
                    rangeParam2.setDontTrigger(false);
                }
            }
        });
        rangeParam2.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (coupled) {
                    rangeParam1.setDontTrigger(true);
                    rangeParam1.setValue(rangeParam2.getValue());
                    rangeParam1.setDontTrigger(false);
                }
            }
        });
    }

    @Override
    public void setAdjustmentListener(ParamAdjustmentListener listener) {
        rangeParam1.setAdjustmentListener(listener);
        rangeParam2.setAdjustmentListener(listener);
    }

    @Override
    public JComponent createGUI() {
        return new CoupledRangeSelector(this);
    }

    public int getFirstValue() {
        return rangeParam1.getValue();
    }

    public int getSecondValue() {
        return rangeParam2.getValue();
    }

    public void setFirstValue(int newValue) {
        rangeParam1.setValue(newValue);
        // TODO not necessary
//        if(coupled) {
//            rangeParam2.setValue(newValue);
//        }
    }

    public void setSecondValue(int newValue) {
        rangeParam2.setValue(newValue);
        // TODO not necessary
//        if(coupled) {
//            rangeParam1.setValue(newValue);
//        }
    }

    public boolean isCoupled() {
        return coupled;
    }

    public void setCoupled(boolean coupled) {
        this.coupled = coupled;
    }

    @Override
    public int getNrOfGridBagCols() {
        return 1;
    }

    @Override
    public void randomize() {
        if (coupled) {
            rangeParam1.randomize();
        } else {
            rangeParam1.randomize();
            rangeParam2.randomize();
        }
    }

    @Override
    public void setDontTrigger(boolean b) {
        rangeParam1.setDontTrigger(b);
        rangeParam2.setDontTrigger(b);
    }

    @Override
    public boolean isSetToDefault() {
        return rangeParam1.isSetToDefault() && rangeParam2.isSetToDefault();
    }

    @Override
    public void reset(boolean triggerAction) {
        rangeParam1.reset(false);
        rangeParam2.reset(triggerAction);
    }

    public RangeParam getFirstRangeParam() {
        return rangeParam1;
    }

    public RangeParam getSecondRangeParam() {
        return rangeParam2;
    }
}
