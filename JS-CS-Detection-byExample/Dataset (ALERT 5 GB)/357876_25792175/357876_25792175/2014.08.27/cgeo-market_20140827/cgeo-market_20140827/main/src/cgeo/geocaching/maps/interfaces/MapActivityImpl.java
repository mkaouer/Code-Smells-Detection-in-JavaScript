package cgeo.geocaching.maps.interfaces;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * Defines the common functions of the provider-specific
 * MapActivity implementations.
 */
public interface MapActivityImpl {

    Resources getResources();

    Activity getActivity();

    void superOnCreate(Bundle savedInstanceState);

    void superOnResume();

    void superOnDestroy();

    void superOnStop();

    void superOnPause();

    boolean superOnCreateOptionsMenu(Menu menu);

    boolean superOnPrepareOptionsMenu(Menu menu);

    boolean superOnOptionsItemSelected(MenuItem item);

    public abstract void navigateUp(View view);
}
