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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * A fixed set of GUIParam objects
 */
public class ParamSet implements Iterable<GUIParam> {
    private List<GUIParam> paramList = new ArrayList<GUIParam>();
    private ParamAdjustingListener adjustingListener;

    public ParamSet(GUIParam... params) {
        paramList.addAll(Arrays.asList(params));
        init();
    }

    public ParamSet(GUIParam param) {
        paramList.add(param);
        init();
    }

    private void init() {
        if (paramList.size() > 1) {
            addRandomizeAction();
            addResetAllAction();
        }
    }


    private void addRandomizeAction() {
        ActionParam randomizeAction = new ActionParam("Randomize Settings", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                randomize();
            }
        });
        paramList.add(randomizeAction);
    }

    private void addResetAllAction() {
        ActionParam randomizeAction = new ActionParam("Reset All", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reset();
            }
        }, "default_icon.gif");
        paramList.add(randomizeAction);
    }

    @Override
    public Iterator<GUIParam> iterator() {
        return paramList.iterator();
    }

    /**
     * Resets all params without triggering an operation
     */
    public void reset() {
        for (GUIParam param : paramList) {
            param.reset(false);
        }
    }

    public void randomize() {
        for (GUIParam param : paramList) {
            param.randomize();
        }
    }

    public void startPresetAdjusting() {
        for (GUIParam param : paramList) {
            param.setDontTrigger(true);
        }
    }


    public void endPresetAdjusting(boolean trigger) {
        for (GUIParam param : paramList) {
            param.setDontTrigger(false);
        }
        if (trigger) {
            if (adjustingListener != null) {
                adjustingListener.paramAdjusted();
            }
        }
    }

    public void setAdjustingListener(ParamAdjustingListener listener) {
        adjustingListener = listener;
    }
}
