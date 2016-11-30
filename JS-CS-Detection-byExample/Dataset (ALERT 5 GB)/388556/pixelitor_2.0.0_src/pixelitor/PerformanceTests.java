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
package pixelitor;

import com.jhlabs.image.CausticsFilter;
import com.jhlabs.image.TransformFilter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.File;
import java.io.IOException;

public class PerformanceTests {
    private static FilterWrapper[] filters = {
            new FilterWrapper(new CausticsFilter()),
//            new FilterWrapper(new BrushedMetalFilter()),
//            new FilterWrapper(new CircleFilter()),
//            new FilterWrapper(new CircleToSquareFilter()),
//            new FilterWrapper(new DiffuseFilter()),
//            new FilterWrapper(new KaleidoscopeFilter()),
//            new FilterWrapper(new MagnifyFilter()),
//            new FilterWrapper(new MarbleFilter()),
//            new FilterWrapper(new MirrorFilter()),
//            new FilterWrapper(new OffsetFilter()),
//            new FilterWrapper(new PerspectiveFilter()),
//            new FilterWrapper(new PolarFilter()),
//            new FilterWrapper(new RippleFilter()),
//            new FilterWrapper(new SphereFilter()),
//            new FilterWrapper(new SwirlFilter()),
//            new FilterWrapper(new TileFilter()),
//            new FilterWrapper(new WaterFilter())
    };

    public static void main(String[] args) throws IOException {
        BufferedImage image = ImageIO.read(new File("C:\\Users\\Laci\\Desktop\\FOTÓK\\test.JPG"));
        for (FilterWrapper filterWrapper : filters) {
            String description = filterWrapper.getDescription();
            System.out.println(description + ':');
            if(filterWrapper.isTransformFilter()) {
                testTransformFilter(image, filterWrapper);
            } else {
                double timeNormal = testFilter("NORMAL", filterWrapper, image);
                System.out.println();
            }
        }
        System.exit(0); // exit because of the threads
    }

    private static void testTransformFilter(BufferedImage image, FilterWrapper filterWrapper) {
        filterWrapper.setInterpolation(TransformFilter.NEAREST_NEIGHBOUR);
        double timeNN = testFilter("NEAREST_NEIGHBOUR", filterWrapper, image);
        System.out.println();

        filterWrapper.setInterpolation(TransformFilter.NEAREST_NEIGHBOUR_OLD);
        double timeNNOld = testFilter("NEAREST_NEIGHBOUR_OLD", filterWrapper, image);
        double percentOfOld = timeNN * 100.0/ timeNNOld;
        System.out.println(String.format("(%.2f %%)", percentOfOld));

        filterWrapper.setInterpolation(TransformFilter.BILINEAR);
        double timeBL = testFilter("BILINEAR", filterWrapper, image);
        System.out.println();

        filterWrapper.setInterpolation(TransformFilter.BILINEAR_OLD);
        double timeBLOLD = testFilter("BILINEAR_OLD", filterWrapper, image);
        percentOfOld = timeBL * 100.0/ timeBLOLD;
        System.out.println(String.format("(%.2f %%)", percentOfOld));
    }

    private static double testFilter(String what, FilterWrapper filterWrapper, BufferedImage src) {
        System.out.print("    " + what + ": ");
        BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());

        BufferedImageOp filter = filterWrapper.getFilter();

        for (int i = 0; i < 2; i++) {
            filter.filter(src, dest);
        }

        long startTime = System.nanoTime();

        for (int i = 0; i < 10; i++) {
            filter.filter(src, dest);
        }

        double estimatedSeconds = (System.nanoTime() - startTime) / 100000000.0;
        System.out.print(String.format("%.2f", estimatedSeconds));

        return estimatedSeconds;
    }
}

class FilterWrapper {
    private BufferedImageOp filter;
    private String description;


    FilterWrapper(BufferedImageOp filter) {
        this(filter, filter.getClass().getSimpleName());
    }

    FilterWrapper(BufferedImageOp filter, String description) {
        this.filter = filter;
        this.description = description;
        if(filter instanceof TransformFilter) {
            ((TransformFilter)filter).setEdgeAction(TransformFilter.WRAP);
        }
    }

    public void setInterpolation(int interpolation) {
        ((TransformFilter)filter).setInterpolation(interpolation);
    }

    BufferedImageOp getFilter() {
        return filter;
    }

    String getDescription() {
        return description;
    }

    boolean isTransformFilter() {
        return filter instanceof TransformFilter;
    }
}