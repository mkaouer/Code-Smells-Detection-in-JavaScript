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
package pixelitor.operations.jhlabsproxies;

import com.jhlabs.image.OffsetFilter;
import pixelitor.operations.OperationWithParametrizedGUI;
import pixelitor.operations.gui.ImagePositionParam;
import pixelitor.operations.gui.ParamSet;

import java.awt.image.BufferedImage;

/**
 * Offset based on the JHLabs OffsetFilter
 */
public class JHOffset extends OperationWithParametrizedGUI {
    private ImagePositionParam center = new ImagePositionParam("Translate Top Left Point To");

    OffsetFilter filter;

    public JHOffset() {
        super("Offset", true);
        paramSet = new ParamSet(center);
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {
        if (filter == null) {
            filter = new OffsetFilter();
        }

        filter.setRelativeX(center.getRelativeX());
        filter.setRelativeY(center.getRelativeY());
        filter.setUseRelative(true);

        dest = filter.filter(src, dest);
        return dest;
    }
}