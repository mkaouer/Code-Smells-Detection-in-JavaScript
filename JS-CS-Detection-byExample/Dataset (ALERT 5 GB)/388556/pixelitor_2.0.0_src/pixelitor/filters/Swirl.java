package pixelitor.filters;

import pixelitor.filters.gui.ImagePositionParam;
import pixelitor.filters.gui.IntChoiceParam;
import pixelitor.filters.gui.ParamSet;
import pixelitor.filters.gui.RangeParam;
import pixelitor.filters.impl.SwirlFilter;

import java.awt.image.BufferedImage;

/**
 * Swirl based on the JHLabs SwirlFilter
 */
public class Swirl extends FilterWithParametrizedGUI {
    private RangeParam amountParam = new RangeParam("Amount", -400, 400, 100);
    private RangeParam radiusParam = new RangeParam("Radius", 0, 1000, 300);
    private RangeParam divideParam = new RangeParam("Divide (%)", 50, 500, 100);

    private ImagePositionParam centerParam = new ImagePositionParam("Center");
    private IntChoiceParam edgeActionParam =  IntChoiceParam.getEdgeActionChoices();
    private IntChoiceParam interpolationParam = IntChoiceParam.getInterpolationChoices();

    private SwirlFilter filter;

    public Swirl() {
        super("Swirl", true, false);
        setParamSet(new ParamSet(
                amountParam,
                radiusParam.adjustMaxAccordingToImage(),
                divideParam,
                centerParam,
                edgeActionParam,
                interpolationParam
        ));
    }

    @Override
    public BufferedImage doTransform(BufferedImage src, BufferedImage dest) {
        float amount =  amountParam.getValueAsPercentage();
        int size =  radiusParam.getValue();
        float divide = divideParam.getValueAsPercentage();
        if(filter == null) {
            filter = new SwirlFilter();
        }

        filter.setAmount(amount);
        filter.setRadius(size);
        filter.setDivideFactor(divide);

        filter.setCenterX(centerParam.getRelativeX());
        filter.setCenterY(centerParam.getRelativeY());
        filter.setEdgeAction(edgeActionParam.getValue());
        filter.setInterpolation(interpolationParam.getValue());

        dest = filter.filter(src, dest);
        return dest;
    }
}