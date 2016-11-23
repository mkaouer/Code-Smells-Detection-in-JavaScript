package cgeo.geocaching;

import butterknife.ButterKnife;
import butterknife.InjectView;

import cgeo.geocaching.connector.gc.GCLogin;
import cgeo.geocaching.connector.gc.GCParser;
import cgeo.geocaching.enumerations.LogType;
import cgeo.geocaching.enumerations.StatusCode;
import cgeo.geocaching.network.Network;
import cgeo.geocaching.network.Parameters;
import cgeo.geocaching.settings.Settings;
import cgeo.geocaching.twitter.Twitter;
import cgeo.geocaching.ui.Formatter;
import cgeo.geocaching.ui.dialog.DateDialog;
import cgeo.geocaching.ui.dialog.Dialogs;
import cgeo.geocaching.utils.Log;
import cgeo.geocaching.utils.LogTemplateProvider.LogContext;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class LogTrackableActivity extends AbstractLoggingActivity implements DateDialog.DateDialogParent {

    @InjectView(R.id.post) protected Button buttonPost;
    @InjectView(R.id.type) protected Button typeButton;
    @InjectView(R.id.date) protected Button dateButton;
    @InjectView(R.id.tracking) protected EditText trackingEditText;
    @InjectView(R.id.tweet) protected CheckBox tweetCheck;
    @InjectView(R.id.tweet_box) protected LinearLayout tweetBox;

    private List<LogType> possibleLogTypes = new ArrayList<LogType>();
    private ProgressDialog waitDialog = null;
    private String guid = null;
    private String geocode = null;
    private String[] viewstates = null;
    private boolean gettingViewstate = true;
    private Calendar date = Calendar.getInstance();
    private LogType typeSelected = LogType.getById(Settings.getTrackableAction());
    private int attempts = 0;
    private Trackable trackable;

    private Handler showProgressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            showProgress(true);
        }
    };

    private Handler loadDataHandler = new Handler() {

        @Override
        public void handleMessage(final Message msg) {
            if (!possibleLogTypes.contains(typeSelected)) {
                setType(possibleLogTypes.get(0));

                showToast(res.getString(R.string.info_log_type_changed));
            }

            if (GCLogin.isEmpty(viewstates)) {
                if (attempts < 2) {
                    showToast(res.getString(R.string.err_log_load_data_again));
                    new LoadDataThread().start();
                } else {
                    showToast(res.getString(R.string.err_log_load_data));
                    showProgress(false);
                }
                return;
            }

            gettingViewstate = false; // we're done, user can post log

            buttonPost.setEnabled(true);
            buttonPost.setOnClickListener(new PostListener());

            showProgress(false);
        }
    };

    private final Handler postLogHandler = new Handler() {

        @Override
        public void handleMessage(final Message msg) {
            if (waitDialog != null) {
                waitDialog.dismiss();
            }

            final StatusCode error = (StatusCode) msg.obj;
            if (error == StatusCode.NO_ERROR) {
                showToast(res.getString(R.string.info_log_posted));
                finish();
            } else {
                showToast(error.getErrorString(res));
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.logtrackable_activity);
        ButterKnife.inject(this);

        // get parameters
        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            geocode = extras.getString(Intents.EXTRA_GEOCODE);
            guid = extras.getString(Intents.EXTRA_GUID);

            if (StringUtils.isNotBlank(extras.getString(Intents.EXTRA_TRACKING_CODE))) {
                trackingEditText.setText(extras.getString(Intents.EXTRA_TRACKING_CODE));
                Dialogs.moveCursorToEnd(trackingEditText);
            }
        }

        trackable = DataStore.loadTrackable(geocode);

        if (StringUtils.isNotBlank(trackable.getName())) {
            setTitle(res.getString(R.string.trackable_touch) + ": " + trackable.getName());
        } else {
            setTitle(res.getString(R.string.trackable_touch) + ": " + trackable.getGeocode());
        }

        if (guid == null) {
            showToast(res.getString(R.string.err_tb_forgot_saw));

            finish();
            return;
        }

        init();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        init();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo info) {
        super.onCreateContextMenu(menu, view, info);
        final int viewId = view.getId();

        if (viewId == R.id.type) {
            for (final LogType typeOne : possibleLogTypes) {
                menu.add(viewId, typeOne.id, 0, typeOne.getL10n());
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final int group = item.getGroupId();
        final int id = item.getItemId();

        if (group == R.id.type) {
            setType(LogType.getById(id));

            return true;
        }

        return false;
    }

    public void init() {
        registerForContextMenu(typeButton);
        typeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openContextMenu(view);
            }
        });

        setType(typeSelected);
        dateButton.setOnClickListener(new DateListener());
        setDate(date);

        initTwitter();

        if (CollectionUtils.isEmpty(possibleLogTypes)) {
            possibleLogTypes = Trackable.getPossibleLogTypes();
        }

        if (GCLogin.isEmpty(viewstates)) {
            buttonPost.setEnabled(false);
            buttonPost.setOnTouchListener(null);
            buttonPost.setOnClickListener(null);

            new LoadDataThread().start();
        } else {
            buttonPost.setEnabled(true);
            buttonPost.setOnClickListener(new PostListener());
        }
        disableSuggestions(trackingEditText);
    }

    @Override
    public void setDate(Calendar dateIn) {
        date = dateIn;

        dateButton.setText(Formatter.formatShortDateVerbally(date.getTime().getTime()));
    }

    public void setType(LogType type) {
        typeSelected = type;
        typeButton.setText(typeSelected.getL10n());
    }

    private void initTwitter() {
        tweetCheck.setChecked(true);
        if (Settings.isUseTwitter() && Settings.isTwitterLoginValid()) {
            tweetBox.setVisibility(View.VISIBLE);
        } else {
            tweetBox.setVisibility(View.GONE);
        }
    }

    private class DateListener implements View.OnClickListener {

        @Override
        public void onClick(View arg0) {
            final Dialog dateDialog = new DateDialog(LogTrackableActivity.this, LogTrackableActivity.this, date);
            dateDialog.setCancelable(true);
            dateDialog.show();
        }
    }

    private class PostListener implements View.OnClickListener {

        protected EditText logEditText = (EditText) findViewById(R.id.log);

        @Override
        public void onClick(View arg0) {
            if (!gettingViewstate) {
                waitDialog = ProgressDialog.show(LogTrackableActivity.this, null, res.getString(R.string.log_saving), true);
                waitDialog.setCancelable(true);

                Settings.setTrackableAction(typeSelected.id);

                final String tracking = trackingEditText.getText().toString();
                final String log = logEditText.getText().toString();
                new PostLogThread(postLogHandler, tracking, log).start();
            } else {
                showToast(res.getString(R.string.err_log_load_data_still));
            }
        }
    }

    private class LoadDataThread extends Thread {

        public LoadDataThread() {
            super("Load data for logging trackable");
            if (guid == null) {
                showToast(res.getString(R.string.err_tb_forgot_saw));

                finish();
            }
        }

        @Override
        public void run() {
            final Parameters params = new Parameters();

            showProgressHandler.sendEmptyMessage(0);
            gettingViewstate = true;
            attempts++;

            try {
                if (StringUtils.isNotBlank(guid)) {
                    params.put("wid", guid);
                } else {
                    loadDataHandler.sendEmptyMessage(0);
                    return;
                }

                final String page = Network.getResponseData(Network.getRequest("http://www.geocaching.com/track/log.aspx", params));

                viewstates = GCLogin.getViewstates(page);

                final List<LogType> typesPre = GCParser.parseTypes(page);
                if (CollectionUtils.isNotEmpty(typesPre)) {
                    possibleLogTypes.clear();
                    possibleLogTypes.addAll(typesPre);
                }
            } catch (Exception e) {
                Log.e("LogTrackableActivity.LoadDataThread.run", e);
            }

            loadDataHandler.sendEmptyMessage(0);
        }
    }

    private class PostLogThread extends Thread {
        final private Handler handler;
        final private String tracking;
        final private String log;

        public PostLogThread(final Handler handlerIn, final String trackingIn, final String logIn) {
            super("Post trackable log");
            handler = handlerIn;
            tracking = trackingIn;
            log = logIn;
        }

        @Override
        public void run() {
            final StatusCode status = postLogFn(tracking, log);
            handler.sendMessage(handler.obtainMessage(0, status));
        }
    }

    public StatusCode postLogFn(String tracking, String log) {
        try {
            final StatusCode status = GCParser.postLogTrackable(guid, tracking, viewstates, typeSelected, date.get(Calendar.YEAR), (date.get(Calendar.MONTH) + 1), date.get(Calendar.DATE), log);

            if (status == StatusCode.NO_ERROR && Settings.isUseTwitter() &&
                    Settings.isTwitterLoginValid() &&
                    tweetCheck.isChecked() && tweetBox.getVisibility() == View.VISIBLE) {
                Twitter.postTweetTrackable(geocode, new LogEntry(0, typeSelected, log));
            }

            return status;
        } catch (Exception e) {
            Log.e("LogTrackableActivity.postLogFn", e);
        }

        return StatusCode.LOG_POST_ERROR;
    }

    public static void startActivity(final Context context, final Trackable trackable) {
        final Intent logTouchIntent = new Intent(context, LogTrackableActivity.class);
        logTouchIntent.putExtra(Intents.EXTRA_GEOCODE, trackable.getGeocode());
        logTouchIntent.putExtra(Intents.EXTRA_GUID, trackable.getGuid());
        logTouchIntent.putExtra(Intents.EXTRA_TRACKING_CODE, trackable.getTrackingcode());
        context.startActivity(logTouchIntent);
    }

    @Override
    protected LogContext getLogContext() {
        return new LogContext(trackable, null);
    }
}
