package cgeo.geocaching.activity;

import cgeo.geocaching.CgeoApplication;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;

public abstract class AbstractListActivity extends ActionBarListActivity implements
        IAbstractActivity {

    private boolean keepScreenOn = false;

    protected CgeoApplication app = null;
    protected Resources res = null;

    protected AbstractListActivity() {
        this(false);
    }

    protected AbstractListActivity(final boolean keepScreenOn) {
        this.keepScreenOn = keepScreenOn;
    }

    final public void showProgress(final boolean show) {
        ActivityMixin.showProgress(this, show);
    }

    final public void setTheme() {
        ActivityMixin.setTheme(this);
    }

    @Override
    public final void showToast(final String text) {
        ActivityMixin.showToast(this, text);
    }

    @Override
    public final void showShortToast(final String text) {
        ActivityMixin.showShortToast(this, text);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        initializeCommonFields();
        initUpAction();
    }

    protected void initUpAction() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId()== android.R.id.home) {
            return ActivityMixin.navigateUp(this);
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeCommonFields() {
        // init
        res = this.getResources();
        app = (CgeoApplication) this.getApplication();

        ActivityMixin.keepScreenOn(this, keepScreenOn);
    }

    final protected void setTitle(final String title) {
        ActivityMixin.setTitle(this, title);
    }

    @Override
    public void invalidateOptionsMenuCompatible() {
        ActivityMixin.invalidateOptionsMenu(this);
    }

    public void onCreate(final Bundle savedInstanceState, final int resourceLayoutID) {
        super.onCreate(savedInstanceState);
        initializeCommonFields();

        setTheme();
        setContentView(resourceLayoutID);
    }

    @Override
    public void setContentView(final int layoutResID) {
        super.setContentView(layoutResID);

        // initialize action bar title with activity title
        ActivityMixin.setTitle(this, getTitle());
    }

    @Override
    public final void presentShowcase() {
        ActivityMixin.presentShowcase(this);
    }

    @Override
    public ShowcaseViewBuilder getShowcase() {
        // do nothing by default
        return null;
    }

}
