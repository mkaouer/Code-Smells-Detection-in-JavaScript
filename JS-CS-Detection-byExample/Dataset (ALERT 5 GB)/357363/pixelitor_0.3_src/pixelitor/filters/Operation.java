package pixelitor.filters;

import java.awt.image.BufferedImage;

import pixelitor.ImageChangeReason;

/**
 *
 */
public interface Operation extends Comparable<Operation> {
    BufferedImage transform(BufferedImage src, BufferedImage dest, ImageChangeReason changeReason);

    String getName();

    void execute(ImageChangeReason changeReason);
    BufferedImage executeForOneLayer(ImageChangeReason changeReason, BufferedImage src);
}
