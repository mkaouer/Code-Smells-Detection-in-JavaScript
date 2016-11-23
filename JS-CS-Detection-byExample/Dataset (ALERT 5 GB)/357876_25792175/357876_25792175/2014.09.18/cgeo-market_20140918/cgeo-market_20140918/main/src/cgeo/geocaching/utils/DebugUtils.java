package cgeo.geocaching.utils;

import cgeo.geocaching.R;

import org.eclipse.jdt.annotation.NonNull;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DebugUtils {

    private DebugUtils() {
        // utility class
    }

    public static void createMemoryDump(final @NonNull Context context) {
        try {
            final Date now = new Date();
            final SimpleDateFormat fileNameDateFormat = new SimpleDateFormat("yyyy-MM-dd_hh-mm", Locale.US);
            File file = FileUtils.getUniqueNamedFile(Environment.getExternalStorageDirectory().getPath()
                    + File.separatorChar + "cgeo_dump_" + fileNameDateFormat.format(now) + ".hprof");
            android.os.Debug.dumpHprofData(file.getPath());
            Toast.makeText(context, context.getString(R.string.init_memory_dumped, file.getAbsolutePath()),
                    Toast.LENGTH_LONG).show();
            ShareUtils.share(context, file, R.string.init_memory_dump);
        } catch (IOException e) {
            Log.e("createMemoryDump", e);
        }
    }
}
