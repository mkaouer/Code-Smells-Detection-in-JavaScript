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
package pixelitor.tools;

import pixelitor.AppLogic;
import pixelitor.Composition;
import pixelitor.ImageComponent;
import pixelitor.layers.ImageLayer;
import pixelitor.utils.ImageUtils;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * The erase tool.
 */
public class EraseTool extends AbstractBrushTool {
    private BufferedImage copyBeforeStart;

    public EraseTool() {
        super('e', "Erase", "erase_tool_icon.gif", "click and drag to erase pixels");
//        useFillOval = true;
    }

    @Override
    public boolean mouseReleased(MouseEvent e, ImageComponent ic) {
        if (super.mouseReleased(e, ic)) {
            return true;
        }

        copyBeforeStart.flush();
        copyBeforeStart = null;
        return false;
    }

    @Override
    void initDrawingGraphics(ImageLayer layer) {
        // uses the graphics of the buffered image contained in the layer
        BufferedImage drawImage = layer.createCompositionSizedSubImage();
        g = drawImage.createGraphics();
        if (respectSelection) {
            layer.getComposition().setSelectionClipping(g, null);
        }
    }

    @Override
    public void setupGraphics(Graphics2D g, Paint p) {
        // the color does not matter as long as AlphaComposite.CLEAR is used
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OUT, 1.0f));

        BufferedImage image = AppLogic.getActiveComp().getActiveImageLayer().getBufferedImage();
        copyBeforeStart = ImageUtils.copyImage(image);
    }

    @Override
    BufferedImage getFullUntouchedImage(Composition comp) {
        if (copyBeforeStart == null) {
            throw new IllegalStateException();
        }

        return copyBeforeStart;
    }

    @Override
    void mergeTmpLayer(Composition comp) {
        // do nothing - this tool draws directly into the image
    }
}