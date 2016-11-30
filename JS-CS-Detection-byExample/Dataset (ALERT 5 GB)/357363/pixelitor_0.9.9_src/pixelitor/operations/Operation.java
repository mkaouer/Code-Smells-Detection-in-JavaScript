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

package pixelitor.operations;

import pixelitor.AppLogic;
import pixelitor.Composition;
import pixelitor.ImageChangeReason;
import pixelitor.PixelitorWindow;
import pixelitor.utils.GUIUtils;
import pixelitor.utils.ImageUtils;
import pixelitor.utils.Utils;

import javax.swing.*;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

public abstract class Operation extends AbstractAction implements Comparable<Operation> {
    protected boolean copySrcToDstBeforeRunning = false;

    protected Operation(String name) {
        this(name, null);
    }

    protected Operation(String name, Icon icon) {
        if (name == null) {
            throw new IllegalArgumentException("name is null");
        }

        putValue(Action.SMALL_ICON, icon);
        putValue(Action.NAME, name);

        Operations.addOperation(this);
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
        execute(ImageChangeReason.OP_WITHOUT_DIALOG);
    }

    @Override
    public String toString() {
        return getMenuName();
    }

    /**
     * Should the contents of the source BufferedImage be copied into the destination before running the op
     */
    protected boolean copyContents() {
        // TODO - not overwritten - should be removed?
        return copySrcToDstBeforeRunning;
    }

    /**
     * Should a default destination buffer be created before running the op or null can be passed and the
     * op will take care of that
     */
    protected boolean createDefaultDestBuffer() {
        // TODO - not overwritten - should be removed?
        return true;
    }

    public void execute(final ImageChangeReason changeReason, Component busyCursorParent) {
        try {
            final Composition comp = AppLogic.getActiveComp();
            if (comp == null) {
                GUIUtils.showErrorDialog("Error", "No active composition found while executing " + getName());
                return;
            }

            if (changeReason == ImageChangeReason.OP_PREVIEW) {
                comp.startNewPreviewFromDialog();
            } else {
                Operations.setLastExecutedOperation(this);
            }

            long startTime = System.nanoTime();


            Runnable task = new Runnable() {
                public void run() {
                    BufferedImage src = comp.getImageForActiveLayer();
                    BufferedImage dest = executeForOneLayer(src);
                    comp.changeActiveLayerImage(dest, changeReason, getName());
                }
            };
            Utils.executeWithBusyCursor(busyCursorParent, task, false);

            afterFilterActions(comp);

            long totalTime = (System.nanoTime() - startTime) / 1000000;
            String performanceMessage = getMenuName() + " took " + totalTime + " ms";
//            System.out.println("Operation.execute: \"" + performanceMessage + "\"");
            AppLogic.setStatusMessage(performanceMessage);
        } catch (OutOfMemoryError e) {
            JOptionPane.showMessageDialog(PixelitorWindow.getInstance(), "Not enough memory. Try increasing the maximal memory available to this application with -Xmx", "Out of memory error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            GUIUtils.showExceptionDialog(PixelitorWindow.getInstance(), e);
        }
        LastOp.INSTANCE.setMenuName("Repeat " + getMenuName());
    }

    public void execute(final ImageChangeReason changeReason) {
        execute(changeReason, PixelitorWindow.getInstance());
    }

    public BufferedImage executeForOneLayer(BufferedImage src) {
        BufferedImage dest = null;
        if (createDefaultDestBuffer()) {
            if (copyContents()) {
                dest = ImageUtils.copyImage(src);
            } else {
                dest = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());
            }
        }

        dest = transform(src, dest);

        return dest;
    }

    protected abstract BufferedImage transform(BufferedImage src, BufferedImage dest);

    @Override
    public int compareTo(Operation o) {
        String name = getMenuName();
        String otherName = o.getMenuName();
        return name.compareTo(otherName);
    }

    public void randomizeSettings() {

    }

    /**
     * Things to do with the Composition after the transform has been run
     */
    public void afterFilterActions(Composition ic) {
        // TODO - not overwritten - should be removed?
    }
}
