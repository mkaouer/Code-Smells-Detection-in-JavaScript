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

package pixelitor.filters;

import pixelitor.filters.gui.BooleanParam;

import javax.swing.*;
import java.awt.image.BufferedImage;

/**
 * An OperationWithParametrizedGUI with "Show Original" functionality
 */
public abstract class FilterWithSOParametrizedGUI extends FilterWithParametrizedGUI {
    protected BooleanParam showOriginalParam = BooleanParam.createParamForShowOriginal();
    private ShowOriginalHelper showOriginalHelper = new ShowOriginalHelper();

    protected FilterWithSOParametrizedGUI(String name, boolean runFilterImmediately) {
        super(name, runFilterImmediately);
    }

    protected FilterWithSOParametrizedGUI(String name, Icon icon, boolean runFilterImmediately) {
        super(name, icon, runFilterImmediately);
    }

    @Override
    public void startDialogSession() {
    }

    @Override
    public void endDialogSession() {
        BufferedImage img = showOriginalHelper.getLastTransformed();
        if (img != null) {
            // cannot be always flushed because it might be the active layer image as well
            // TODO keep track of it
            // img.flush();
            showOriginalHelper.setLastTransformed(null);
        }
        showOriginalHelper.setPreviousShowOriginal(false);
    }

    @Override
    protected BufferedImage transform(BufferedImage src, BufferedImage dest) {
        boolean showOriginal = showOriginalParam.getValue();
        showOriginalHelper.setShowOriginal(showOriginal);
        if (showOriginal) {
            return FilterUtils.getDefaultImage(src);
        }

        if (showOriginalHelper.showCached()) {
            dest = showOriginalHelper.getLastTransformed();
            assert dest != null;
            return dest;
        }

        dest = realTransform(src, dest);

        showOriginalHelper.setLastTransformed(dest);

        return dest;
    }


    public abstract BufferedImage realTransform(BufferedImage src, BufferedImage dest);
}
