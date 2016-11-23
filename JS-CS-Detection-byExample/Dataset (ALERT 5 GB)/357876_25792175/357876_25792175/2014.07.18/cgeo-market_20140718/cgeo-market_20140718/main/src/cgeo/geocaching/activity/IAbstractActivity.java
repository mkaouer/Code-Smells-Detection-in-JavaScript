package cgeo.geocaching.activity;

import android.view.View;

public interface IAbstractActivity {

    public void goHome(View view);

    public void showToast(String text);

    public void showShortToast(String text);

    public void invalidateOptionsMenuCompatible();
}
