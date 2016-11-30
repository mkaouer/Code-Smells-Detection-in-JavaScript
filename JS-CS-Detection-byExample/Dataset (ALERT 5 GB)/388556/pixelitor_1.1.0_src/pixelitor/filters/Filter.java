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

package pixelitor.filters;

import pixelitor.AppLogic;
import pixelitor.ChangeReason;
import pixelitor.Composition;
import pixelitor.ExceptionHandler;
import pixelitor.PixelitorWindow;
import pixelitor.layers.Layers;
import pixelitor.utils.ImageUtils;
import pixelitor.utils.Utils;

import javax.swing.*;
import java.awt.Component;
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

    void setMenuName(String s) {
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
    @SuppressWarnings({"WeakerAccess"})
    protected boolean copyContents() {
        // TODO - not overwritten - should be removed?
        return copySrcToDstBeforeRunning;
    }

    /**
     * Should a default destination buffer be created before running the op or null can be passed and the
     * op will take care of that
     */
    @SuppressWarnings({"WeakerAccess"})
    protected boolean createDefaultDestBuffer() {
        return true;
    }

    public void executeWithBusyCursor(final ChangeReason changeReason, Component busyCursorParent) {
        try {
            final Composition comp = AppLogic.getActiveComp();
            if (comp == null) {
                ExceptionHandler.showErrorDialog("Error", "No active composition found while executing " + getName());
                return;
            }

            if (changeReason == ChangeReason.OP_PREVIEW) {
                comp.getActiveImageLayer().startNewPreviewFromDialog();
            } else {
                FilterUtils.setLastExecutedFilter(this);
            }

            long startTime = System.nanoTime();

            Runnable task = new Runnable() {
                @Override
                public void run() {
                    BufferedImage src = comp.getImageOrSubImageIfSelectedForActiveLayer(false, true);
                    BufferedImage dest = executeForOneLayer(src);
//                    AppLogic.debugImage(dest);
                    assert dest != null;

                    if(changeReason == ChangeReason.OP_PREVIEW) {
                        comp.changePreviewImage(dest);
                    } else if(changeReason == ChangeReason.OP_WITHOUT_DIALOG) {
                        comp.changeImageSimpleFilterFinished(dest, changeReason, getName());
                    } else if(changeReason == ChangeReason.PERFORMANCE_TEST) {
                        comp.changeImageSimpleFilterFinished(dest, changeReason, getName());
                    } else {
                        throw new IllegalStateException(changeReason.toString());
                    }
//
//                    comp.changeActiveLayerImage(dest, changeReason, getName());
                }
            };
            Utils.executeWithBusyCursor(busyCursorParent, task, false);

            long totalTime = (System.nanoTime() - startTime) / 1000000;
            String performanceMessage;
            if (totalTime < 1000) {
                performanceMessage = getMenuName() + " took " + totalTime + " ms";
            } else {
                float seconds = totalTime / 1000.0f;
                performanceMessage = String.format("%s took %.1f s", getMenuName(), seconds);
            }
            AppLogic.setStatusMessage(performanceMessage);
        } catch (OutOfMemoryError e) {
            JOptionPane.showMessageDialog(PixelitorWindow.getInstance(), "Not enough memory. Try increasing the maximal memory available to this application with -Xmx", "Out of memory error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            ExceptionHandler.showExceptionDialog(e);
        }
        RepeatLastOp.INSTANCE.setMenuName("Repeat " + getMenuName());
    }

    public void execute(final ChangeReason changeReason) {
        executeWithBusyCursor(changeReason, PixelitorWindow.getInstance());
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

//        if (dest == src) { // src must be treated as read-only
//            throw new IllegalStateException("dest == src");
//        }

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
