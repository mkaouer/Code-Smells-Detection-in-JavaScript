package cgeo.geocaching.backup;

import cgeo.geocaching.utils.ApplicationSettings;

import android.annotation.TargetApi;
import android.app.backup.BackupAgentHelper;
import android.app.backup.SharedPreferencesBackupHelper;

@TargetApi(8)
public class CentralBackupAgent extends BackupAgentHelper {

    private static final String PREFS_BACKUP_KEY = "prefs";

    @Override
    public void onCreate() {
        final SharedPreferencesBackupHelper helper = new SharedPreferencesBackupHelper(this, ApplicationSettings.getPreferencesName());
        addHelper(PREFS_BACKUP_KEY, helper);
    }

}
