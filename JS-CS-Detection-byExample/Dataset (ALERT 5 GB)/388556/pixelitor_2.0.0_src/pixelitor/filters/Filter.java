/*
 * Copyright 2009-2014 Laszlo Balazs-Csiki
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

import pixelitor.*;
import pixelitor.layers.Layers;
import pixelitor.utils.ImageUtils;
import pixelitor.utils.Utils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

public abstract class Filter extends AbstractAction implements Comparable<Filter> {
    protected boolean copySrcToDstBeforeRunning = false;

    protected Filter(String name) {
        this(name, null);
    }

    protected Filter(String name, Icon icon) {
        if (name == null) {
            throw new IllegalArgumentException("name is null");
        }

        putValue(Action.SMALL_ICON, icon);
        putValue(Action.NAME, name);

        FilterUtils.addFilter(this);
    }


    public String getMenuName() {
        return (String) getValue(Action.NAME);
    }

    public void setMenuName(String s) {
        putValue(Action.NAME, s);
    }

    public String getName() {
        return getMenuName();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!Layers.activeIsImageLayer()) {
            ExceptionHandler.showNotImageLayerDialog();
            return;
        }

        execute(ChangeReason.OP_WITHOUT_DIALOG);
    }

    @Override
    public String toString() {
        return getMenuName();
    }

    /**
     * Should the contents of the source BufferedImage be copied into the destination before running the op
     */
    @SuppressWarnings("WeakerAccess")
    protected boolean copyContents() {
        // TODO - not overwritten - should be removed?
        return copySrcToDstBeforeRunning;
    }

    /**
     * Should a default destination buffer be created before running the op or null can be passed and the
     * op will take care of that
     */
    @SuppressWarnings("WeakerAccess")
    protected boolean createDefaultDestBuffer() {
        return true;
    }

    /**
     * This code is executed with busy cursor
     */
    public void runit(Composition comp, ChangeReason changeReason) {
        BufferedImage src = comp.getImageOrSubImageIfSelectedForActiveLayer(false, true);
        BufferedImage dest = executeForOneLayer(src);
//                    AppLogic.debugImage(dest);
        assert dest != null;

        if (changeReason == ChangeReason.OP_PREVIEW) {
            comp.changePreviewImage(dest);
        } else if (changeReason == ChangeReason.OP_WITHOUT_DIALOG) {
            comp.changeImageSimpleFilterFinished(dest, changeReason, getName());
        } else if (changeReason == ChangeReason.PERFORMANCE_TEST) {
            comp.changeImageSimpleFilterFinished(dest, changeReason, getName());
        } else {
            throw new IllegalStateException(changeReason.toString());
        }
//
//                    comp.changeActiveLayerImage(dest, changeReason, getName());
    }

    public void execute(final ChangeReason changeReason) {
        Utils.executeFilterWithBusyCursor(this, changeReason, PixelitorWindow.getInstance());
    }

    public BufferedImage executeForOneLayer(BufferedImage src) {
//        assert !EventQueue.isDispatchThread();

        BufferedImage dest = null;
        if (createDefaultDestBuffer()) {
            if (copyContents()) {
                dest = ImageUtils.copyImage(src);
            } else {
                dest = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());
            }
        }

        dest = transform(src, dest);

        assert dest != null;

        return dest;
    }

    protected abstract BufferedImage transform(BufferedImage src, BufferedImage dest);

    @Override
    public int compareTo(Filter o) {
        String name = getMenuName();
        String otherName = o.getMenuName();
        return name.compareTo(otherName);
    }

    public abstract void randomizeSettings();
}
