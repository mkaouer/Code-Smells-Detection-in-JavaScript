package cgeo.geocaching.ui.dialog;

import cgeo.geocaching.activity.ActivityMixin;
import cgeo.geocaching.utils.Log;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Modified progress dialog class which allows hiding the absolute numbers
 *
 */
public class CustomProgressDialog extends ProgressDialog {

    public CustomProgressDialog(Context context) {
        super(context, ActivityMixin.getDialogTheme());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Method method = TextView.class.getMethod("setVisibility", Integer.TYPE);

            Field[] fields = this.getClass().getSuperclass().getDeclaredFields();

            for (Field field : fields) {
                if (field.getName().equalsIgnoreCase("mProgressNumber")) {
                    field.setAccessible(true);
                    TextView textView = (TextView) field.get(this);
                    method.invoke(textView, View.GONE);
                }
            }
        } catch (NoSuchMethodException e) {
            Log.e("Failed to invoke the progressDialog method 'setVisibility' and set 'mProgressNumber' to GONE.", e);
        } catch (IllegalAccessException e) {
            Log.e("Failed to invoke the progressDialog method 'setVisibility' and set 'mProgressNumber' to GONE.", e);
        } catch (InvocationTargetException e) {
            Log.e("Failed to invoke the progressDialog method 'setVisibility' and set 'mProgressNumber' to GONE.", e);
        }
    }
}