package cgeo.geocaching.settings;

import org.eclipse.jdt.annotation.Nullable;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.AttributeSet;

/**
 * Base class for preferences which evaluate their XML attributes for further processing.
 *
 */
public abstract class AbstractAttributeBasedPrefence extends Preference {

    public AbstractAttributeBasedPrefence(Context context) {
        super(context);
    }

    public AbstractAttributeBasedPrefence(Context context, AttributeSet attrs) {
        super(context, attrs);
        processAttributes(context, attrs, 0);
    }

    public AbstractAttributeBasedPrefence(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        processAttributes(context, attrs, defStyle);
    }

    private void processAttributes(Context context, @Nullable AttributeSet attrs, int defStyle) {
        if (attrs == null) {
            return;
        }
        TypedArray types = context.obtainStyledAttributes(attrs, getAttributeNames(),
                defStyle, 0);

        processAttributeValues(types);

        types.recycle();
    }

    /**
     * Evaluate the attributes which where requested in {@link AbstractAttributeBasedPrefence#getAttributeNames()}.
     * 
     * @param values
     */
    protected abstract void processAttributeValues(TypedArray values);

    /**
     * @return the names of the attributes you want to read in your preference implementation
     */
    protected abstract int[] getAttributeNames();

}
