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

import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JOptionPane;

import pixelitor.AppLogic;
import pixelitor.ImageChangeReason;
import pixelitor.ImageComponent;
import pixelitor.PixelitorWindow;
import pixelitor.utils.GUIUtils;
import pixelitor.utils.Utils;

public abstract class AbstractOperation extends AbstractAction implements Operation {
//    private final boolean hasDialog;
    protected boolean copySrcToDstBeforeRunning = false;

    protected AbstractOperation(String name) {
        this(name, null);
    }

    protected AbstractOperation(String name, Icon icon) {
        if (name == null) {
            throw new IllegalArgumentException("name is null");
        }

        putValue(Action.SMALL_ICON, icon);
        putValue(Action.NAME, name);

        Operations.allOps.add(this);
    }


    @Override
    public String getName() {
        return (String) getValue(Action.NAME);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        execute(ImageChangeReason.OP_WITHOUT_DIALOG);
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * Should the contents of the source BufferedImage be copied into the destionation before running the op
     */
    protected boolean copyContents() {
        return copySrcToDstBeforeRunning;
    }

    /**
     * Should a default destionation buffer be created before funning the op or null can be passed and the
     * op will take care of that
     */
    protected boolean createDefaultDestBuffer() {
        return true;
    }

    @Override
    public void execute(ImageChangeReason changeReason) {
        try {
            ImageComponent ip = AppLogic.getActiveImageComponent();

            if (changeReason == ImageChangeReason.OP_PREVIEW) {
                ip.startNewPreviewFromDialog();
            } else {
                Operations.lastExecutedOperation = this;
            }

            long startTime = System.nanoTime();

            if(this instanceof Resize) {
                ip.runOpForAllLayers(this, changeReason);
            } else {
                BufferedImage src = ip.getImageForActiveLayer();
                BufferedImage dest = executeForOneLayer(changeReason, src);
                ip.changeActiveLayerImage(dest, changeReason);
            }

            long totalTime = (System.nanoTime() - startTime) / 1000000;
            AppLogic.setStatusMessage(getName() + " took " + totalTime + " ms");
        } catch (OutOfMemoryError e) {
            JOptionPane.showMessageDialog(PixelitorWindow.getInstance(), "Not enough memory. Try increasing the maximal memory available to this application with -Xmx", "Out of memory error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            GUIUtils.showExceptionDialog(PixelitorWindow.getInstance(), e);
        }
    }

    public BufferedImage executeForOneLayer(ImageChangeReason changeReason, BufferedImage src) {
        BufferedImage dest = null;
        if (createDefaultDestBuffer()) {
            if (copyContents()) {
                dest = Utils.copyImage(src);
            } else {
                dest = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());
            }
        }
        changeReason.reset();
        dest = transform(src, dest, changeReason);
        return dest;
    }

    @Override
    public int compareTo(Operation o) {
        String name = getName();
        String otherName = o.getName();
        return name.compareTo(otherName);
    }

}
