/*
 * Copyright 2010 L�szl� Bal�zs-Cs�ki
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
package pixelitor.filters.jhlabsproxies;

import com.jhlabs.image.BlockFilter;
import pixelitor.filters.FilterWithParametrizedGUI;
import pixelitor.filters.gui.IntChoiceParam;
import pixelitor.filters.gui.ParamSet;
import pixelitor.filters.gui.RangeParam;
import pixelitor.filters.impl.BrickBlockFilter;
import pixelitor.utils.ImageUtils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Pixelate based on the JHLabs BlockFilter
 */
public class JHPixelate extends FilterWithParametrizedGUI {
    private static final int STYLE_FLAT = 0;
    private static final int STYLE_3D = 1;
    private static final int STYLE_EMBEDDED = 2;
    private static final int STYLE_GRID_ONLY = 3;

    private static final int TYPE_SQUARE = 0;
    private static final int TYPE_BRICK = 1;

    private IntChoiceParam typeParam = new IntChoiceParam("Type", new IntChoiceParam.Value[]{
            new IntChoiceParam.Value("Squares", TYPE_SQUARE),
            new IntChoiceParam.Value("Brick Wall", TYPE_BRICK),
    });

    private IntChoiceParam styleParam = new IntChoiceParam("Style", new IntChoiceParam.Value[]{
            new IntChoiceParam.Value("Flat", STYLE_FLAT),
            new IntChoiceParam.Value("3D", STYLE_3D),
            new IntChoiceParam.Value("Embedded", STYLE_EMBEDDED),
            new IntChoiceParam.Value("Grid", STYLE_GRID_ONLY)
    });

    private RangeParam cellSizeParam = new RangeParam("Cell Size", 3, 200, 20);

    private BlockFilter blockFilter;
    private BrickBlockFilter brickBlockFilter;

    public JHPixelate() {
        super("Pixelate");
        paramSet = new ParamSet(
                cellSizeParam,
                styleParam,
                typeParam
        );
    }

    @Override
    public BufferedImage transform(BufferedImage src, BufferedImage dest) {

        int style = styleParam.getValue();
        int type = typeParam.getValue();

        int cellSize = cellSizeParam.getValue();

        if (style == STYLE_FLAT || style == STYLE_3D || style == STYLE_EMBEDDED) {
            if (type == TYPE_SQUARE) {
                if (blockFilter == null) {
                    blockFilter = new BlockFilter();
                }
                blockFilter.setBlockSize(cellSize);
                dest = blockFilter.filter(src, dest);
            } else if (type == TYPE_BRICK) {
                if (brickBlockFilter == null) {
                    brickBlockFilter = new BrickBlockFilter();
                }
                brickBlockFilter.setHorizontalBlockSize(cellSize * 2);
                brickBlockFilter.setVerticalBlockSize(cellSize);
                dest = brickBlockFilter.filter(src, dest);
            }
        }

        if ((style == STYLE_3D) || (style == STYLE_GRID_ONLY || (style == STYLE_EMBEDDED))) {
            int width = dest.getWidth();
            int height = dest.getHeight();
            int srcType = src.getType();

            BufferedImage bumpSource;
            if (style == STYLE_EMBEDDED) {
                bumpSource = dest;
            } else {
                bumpSource = createBumpSource(type, cellSize, width, height, srcType);
            }

            if (style == STYLE_3D || style == STYLE_EMBEDDED) {
                dest = ImageUtils.bumpMap(dest, bumpSource);
            } else if (style == STYLE_GRID_ONLY) {
                dest = ImageUtils.bumpMap(src, bumpSource);
            } else {
                throw new IllegalStateException();
            }
        }


        return dest;
    }

    private static BufferedImage createBumpSource(int type, int cellSize, int width, int height, int srcType) {
        BufferedImage bumpSource = new BufferedImage(width, height, srcType);

        int gapWidth;
        if (cellSize < 15) {
            gapWidth = 1;
        } else {
            gapWidth = 2;
        }

        Graphics2D g = bumpSource.createGraphics();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.GRAY);

        if (type == TYPE_SQUARE) {
            ImageUtils.drawGrid(g, width, height, gapWidth, cellSize, gapWidth, cellSize);
        } else if (type == TYPE_BRICK) {
            ImageUtils.drawBrickGrid(g, cellSize, width, height);
        }

        g.dispose();
        return bumpSource;
    }
}