package pixelitor.filters;

/**
 *  An Operation with a dialog
 */
public interface OperationWithDialog extends Operation {
    ParamSet getParams();

    AdjustPanel getAdjustPanel();
}
