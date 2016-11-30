package cgeo.geocaching.files;

import cgeo.geocaching.DataStore;
import cgeo.geocaching.Geocache;
import cgeo.geocaching.R;
import cgeo.geocaching.SearchResult;
import cgeo.geocaching.StaticMapsProvider;
import cgeo.geocaching.activity.ActivityMixin;
import cgeo.geocaching.activity.Progress;
import cgeo.geocaching.enumerations.LoadFlags;
import cgeo.geocaching.settings.Settings;
import cgeo.geocaching.ui.dialog.Dialogs;
import cgeo.geocaching.utils.CancellableHandler;
import cgeo.geocaching.utils.Log;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.Nullable;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class GPXImporter {
    static final int IMPORT_STEP_START = 0;
    static final int IMPORT_STEP_READ_FILE = 1;
    static final int IMPORT_STEP_READ_WPT_FILE = 2;
    static final int IMPORT_STEP_STORE_STATIC_MAPS = 4;
    static final int IMPORT_STEP_FINISHED = 5;
    static final int IMPORT_STEP_FINISHED_WITH_ERROR = 6;
    static final int IMPORT_STEP_CANCEL = 7;
    static final int IMPORT_STEP_CANCELED = 8;
    static final int IMPORT_STEP_STATIC_MAPS_SKIPPED = 9;

    public static final String GPX_FILE_EXTENSION = ".gpx";
    public static final String LOC_FILE_EXTENSION = ".loc";
    public static final String ZIP_FILE_EXTENSION = ".zip";
    public static final String WAYPOINTS_FILE_SUFFIX = "-wpts";
    public static final String WAYPOINTS_FILE_SUFFIX_AND_EXTENSION = WAYPOINTS_FILE_SUFFIX + GPX_FILE_EXTENSION;

    private static final List<String> GPX_MIME_TYPES = Arrays.asList("text/xml", "application/xml");
    private static final List<String> ZIP_MIME_TYPES = Arrays.asList("application/zip", "application/x-compressed", "application/x-zip-compressed", "application/x-zip", "application/octet-stream");

    private final Progress progress = new Progress(true);

    private final Resources res;
    private final int listId;
    private final Activity fromActivity;
    private final Handler importFinishedHandler;

    public GPXImporter(final Activity fromActivity, final int listId, final Handler importFinishedHandler) {
        this.listId = listId;
        this.fromActivity = fromActivity;
        res = fromActivity.getResources();
        this.importFinishedHandler = importFinishedHandler;
    }

    /**
     * Import GPX file. Currently supports *.gpx, *.zip (containing gpx files, e.g. PQ queries) or *.loc files.
     *
     * @param file
     *            the file to import
     */
    public void importGPX(final File file) {
        if (StringUtils.endsWithIgnoreCase(file.getName(), GPX_FILE_EXTENSION)) {
            new ImportGpxFileThread(file, listId, importStepHandler, progressHandler).start();
        } else if (StringUtils.endsWithIgnoreCase(file.getName(), ZIP_FILE_EXTENSION)) {
            new ImportGpxZipFileThread(file, listId, importStepHandler, progressHandler).start();
        } else {
            new ImportLocFileThread(file, listId, importStepHandler, progressHandler).start();
        }
    }

    /**
     * Import GPX file from URI.
     *
     * @param uri
     *            URI of the file to import
     * @param knownMimeType
     * @param knownPathName
     */
    public void importGPX(final Uri uri, final @Nullable String knownMimeType, final @Nullable String knownPathName) {
        final ContentResolver contentResolver = fromActivity.getContentResolver();
        String mimeType = knownMimeType;
        final String pathName = knownPathName != null ? knownPathName : uri.getPath();

        // if mimetype can't be determined (e.g. for emulators email app), derive it from uri file extension
        // contentResolver.getType(uri) doesn't help but throws exception for emulators email app
        //   Permission Denial: reading com.android.email.provider.EmailProvider uri
        // Google search says: there is no solution for this problem
        // Gmail doesn't work at all, see #967
        if (mimeType == null) {
            if (StringUtils.endsWithIgnoreCase(pathName, GPX_FILE_EXTENSION) || StringUtils.endsWithIgnoreCase(pathName, LOC_FILE_EXTENSION)) {
                mimeType = "application/xml";
            } else {
                // if we can't determine a better type, default to zip import
                // emulator email sends e.g. content://com.android.email.attachmentprovider/1/1/RAW, mimetype=null
                mimeType = "application/zip";
            }
        }

        Log.i("importGPX: " + uri + ", mimetype=" + mimeType);
        if (GPX_MIME_TYPES.contains(mimeType)) {
            if (StringUtils.endsWithIgnoreCase(pathName, LOC_FILE_EXTENSION)) {
                new ImportLocAttachmentThread(uri, contentResolver, listId, importStepHandler, progressHandler).start();
            } else {
                new ImportGpxAttachmentThread(uri, contentResolver, listId, importStepHandler, progressHandler).start();
            }
        } else if (ZIP_MIME_TYPES.contains(mimeType)) {
            new ImportGpxZipAttachmentThread(uri, contentResolver, listId, importStepHandler, progressHandler).start();
        } else {
            importFinished();
        }
    }

    /**
     * Import GPX provided via intent of activity that instantiated this GPXImporter.
     */
    public void importGPX() {
        final Intent intent = fromActivity.getIntent();
        final Uri uri = intent.getData();
        final String mimeType = intent.getType();
        importGPX(uri, mimeType, null);
    }

    static abstract class ImportThread extends Thread {
        final int listId;
        final Handler importStepHandler;
        final CancellableHandler progressHandler;

        protected ImportThread(int listId, Handler importStepHandler, CancellableHandler progressHandler) {
            this.listId = listId;
            this.importStepHandler = importStepHandler;
            this.progressHandler = progressHandler;
        }

        @Override
        public void run() {
            try {
                importStepHandler.sendMessage(importStepHandler.obtainMessage(IMPORT_STEP_START));
                final Collection<Geocache> caches = doImport();
                Log.i("Imported successfully " + caches.size() + " caches.");

                final SearchResult search = new SearchResult(caches);
                // Do not put imported caches into the cachecache. That would consume lots of memory for no benefit.

                if (Settings.isStoreOfflineMaps() || Settings.isStoreOfflineWpMaps()) {
                    importStepHandler.sendMessage(importStepHandler.obtainMessage(IMPORT_STEP_STORE_STATIC_MAPS, R.string.gpx_import_store_static_maps, search.getCount()));
                    final boolean finishedWithoutCancel = importStaticMaps(search);
                    // Skip last message if static maps where canceled
                    if (!finishedWithoutCancel) {
                        return;
                    }
                }

                importStepHandler.sendMessage(importStepHandler.obtainMessage(IMPORT_STEP_FINISHED, search.getCount(), 0, search));
            } catch (final IOException e) {
                Log.i("Importing caches failed - error reading data: ", e);
                importStepHandler.sendMessage(importStepHandler.obtainMessage(IMPORT_STEP_FINISHED_WITH_ERROR, R.string.gpx_import_error_io, 0, e.getLocalizedMessage()));
            } catch (final ParserException e) {
                Log.i("Importing caches failed - data format error", e);
                importStepHandler.sendMessage(importStepHandler.obtainMessage(IMPORT_STEP_FINISHED_WITH_ERROR, R.string.gpx_import_error_parser, 0, e.getLocalizedMessage()));
            } catch (final CancellationException e) {
                Log.i("Importing caches canceled");
                importStepHandler.sendMessage(importStepHandler.obtainMessage(IMPORT_STEP_CANCELED));
            } catch (final Exception e) {
                Log.e("Importing caches failed - unknown error: ", e);
                importStepHandler.sendMessage(importStepHandler.obtainMessage(IMPORT_STEP_FINISHED_WITH_ERROR, R.string.gpx_import_error_unexpected, 0, e.getLocalizedMessage()));
            }
        }

        protected abstract Collection<Geocache> doImport() throws IOException, ParserException;

        private boolean importStaticMaps(final SearchResult importedCaches) {
            int storedCacheMaps = 0;
            for (final String geocode : importedCaches.getGeocodes()) {
                final Geocache cache = DataStore.loadCache(geocode, LoadFlags.LOAD_WAYPOINTS);
                if (cache != null) {
                    Log.d("GPXImporter.ImportThread.importStaticMaps start downloadMaps for cache " + geocode);
                    StaticMapsProvider.downloadMaps(cache);
                } else {
                    Log.d("GPXImporter.ImportThread.importStaticMaps: no data found for " + geocode);
                }
                storedCacheMaps++;
                if (progressHandler.isCancelled()) {
                    return false;
                }
                progressHandler.sendMessage(progressHandler.obtainMessage(0, storedCacheMaps, 0));
            }
            return true;
        }
    }

    static class ImportLocFileThread extends ImportThread {
        private final File file;

        public ImportLocFileThread(final File file, int listId, Handler importStepHandler, CancellableHandler progressHandler) {
            super(listId, importStepHandler, progressHandler);
            this.file = file;
        }

        @Override
        protected Collection<Geocache> doImport() throws IOException, ParserException {
            Log.i("Import LOC file: " + file.getAbsolutePath());
            importStepHandler.sendMessage(importStepHandler.obtainMessage(IMPORT_STEP_READ_FILE, R.string.gpx_import_loading_caches, (int) file.length()));
            final LocParser parser = new LocParser(listId);
            return parser.parse(file, progressHandler);
        }
    }

    static class ImportLocAttachmentThread extends ImportThread {
        private final Uri uri;
        private final ContentResolver contentResolver;

        public ImportLocAttachmentThread(Uri uri, ContentResolver contentResolver, int listId, Handler importStepHandler, CancellableHandler progressHandler) {
            super(listId, importStepHandler, progressHandler);
            this.uri = uri;
            this.contentResolver = contentResolver;
        }

        @Override
        protected Collection<Geocache> doImport() throws IOException, ParserException {
            Log.i("Import LOC from uri: " + uri);
            importStepHandler.sendMessage(importStepHandler.obtainMessage(IMPORT_STEP_READ_FILE, R.string.gpx_import_loading_caches, -1));
            final InputStream is = contentResolver.openInputStream(uri);
            final LocParser parser = new LocParser(listId);
            try {
                return parser.parse(is, progressHandler);
            } finally {
                is.close();
            }
        }
    }

    static abstract class ImportGpxThread extends ImportThread {

        protected ImportGpxThread(int listId, Handler importStepHandler, CancellableHandler progressHandler) {
            super(listId, importStepHandler, progressHandler);
        }

        @Override
        protected Collection<Geocache> doImport() throws IOException, ParserException {
            try {
                // try to parse cache file as GPX 10
                return doImport(new GPX10Parser(listId));
            } catch (final ParserException pe) {
                // didn't work -> lets try GPX11
                return doImport(new GPX11Parser(listId));
            }
        }

        protected abstract Collection<Geocache> doImport(GPXParser parser) throws IOException, ParserException;
    }

    static class ImportGpxFileThread extends ImportGpxThread {
        private final File cacheFile;

        public ImportGpxFileThread(final File file, int listId, Handler importStepHandler, CancellableHandler progressHandler) {
            super(listId, importStepHandler, progressHandler);
            this.cacheFile = file;
        }

        @Override
        protected Collection<Geocache> doImport(GPXParser parser) throws IOException, ParserException {
            Log.i("Import GPX file: " + cacheFile.getAbsolutePath());
            importStepHandler.sendMessage(importStepHandler.obtainMessage(IMPORT_STEP_READ_FILE, R.string.gpx_import_loading_caches, (int) cacheFile.length()));
            Collection<Geocache> caches = parser.parse(cacheFile, progressHandler);

            final String wptsFilename = getWaypointsFileNameForGpxFile(cacheFile);
            if (wptsFilename != null) {
                final File wptsFile = new File(cacheFile.getParentFile(), wptsFilename);
                if (wptsFile.canRead()) {
                    Log.i("Import GPX waypoint file: " + wptsFile.getAbsolutePath());
                    importStepHandler.sendMessage(importStepHandler.obtainMessage(IMPORT_STEP_READ_WPT_FILE, R.string.gpx_import_loading_waypoints, (int) wptsFile.length()));
                    caches = parser.parse(wptsFile, progressHandler);
                }
            }
            return caches;
        }
    }

    public static class ImportGpxAttachmentThread extends ImportGpxThread {
        private final Uri uri;
        private final ContentResolver contentResolver;

        public ImportGpxAttachmentThread(Uri uri, ContentResolver contentResolver, int listId, Handler importStepHandler, CancellableHandler progressHandler) {
            super(listId, importStepHandler, progressHandler);
            this.uri = uri;
            this.contentResolver = contentResolver;
        }

        @Override
        protected Collection<Geocache> doImport(GPXParser parser) throws IOException, ParserException {
            Log.i("Import GPX from uri: " + uri);
            importStepHandler.sendMessage(importStepHandler.obtainMessage(IMPORT_STEP_READ_FILE, R.string.gpx_import_loading_caches, -1));
            final InputStream is = contentResolver.openInputStream(uri);
            try {
                return parser.parse(is, progressHandler);
            } finally {
                is.close();
            }
        }
    }

    static abstract class ImportGpxZipThread extends ImportGpxThread {

        protected ImportGpxZipThread(int listId, Handler importStepHandler, CancellableHandler progressHandler) {
            super(listId, importStepHandler, progressHandler);
        }

        @Override
        protected Collection<Geocache> doImport(GPXParser parser) throws IOException, ParserException {
            Collection<Geocache> caches = Collections.emptySet();
            // can't assume that GPX file comes before waypoint file in zip -> so we need two passes
            // 1. parse GPX files
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(getInputStream()));
            try {
                for (ZipEntry zipEntry = zis.getNextEntry(); zipEntry != null; zipEntry = zis.getNextEntry()) {
                    if (StringUtils.endsWithIgnoreCase(zipEntry.getName(), GPX_FILE_EXTENSION)) {
                        if (!StringUtils.endsWithIgnoreCase(zipEntry.getName(), WAYPOINTS_FILE_SUFFIX_AND_EXTENSION)) {
                            importStepHandler.sendMessage(importStepHandler.obtainMessage(IMPORT_STEP_READ_FILE, R.string.gpx_import_loading_caches, (int) zipEntry.getSize()));
                            caches = parser.parse(new NoCloseInputStream(zis), progressHandler);
                        }
                    } else {
                        throw new ParserException("Imported zip is not a GPX zip file.");
                    }
                    zis.closeEntry();
                }
            } finally {
                zis.close();
            }

            // 2. parse waypoint files
            zis = new ZipInputStream(new BufferedInputStream(getInputStream()));
            try {
                for (ZipEntry zipEntry = zis.getNextEntry(); zipEntry != null; zipEntry = zis.getNextEntry()) {
                    if (StringUtils.endsWithIgnoreCase(zipEntry.getName(), WAYPOINTS_FILE_SUFFIX_AND_EXTENSION)) {
                        importStepHandler.sendMessage(importStepHandler.obtainMessage(IMPORT_STEP_READ_WPT_FILE, R.string.gpx_import_loading_waypoints, (int) zipEntry.getSize()));
                        caches = parser.parse(new NoCloseInputStream(zis), progressHandler);
                    }
                    zis.closeEntry();
                }
            } finally {
                zis.close();
            }

            return caches;
        }

        protected abstract InputStream getInputStream() throws IOException;
    }

    static class ImportGpxZipFileThread extends ImportGpxZipThread {
        private final File cacheFile;

        public ImportGpxZipFileThread(final File file, int listId, Handler importStepHandler, CancellableHandler progressHandler) {
            super(listId, importStepHandler, progressHandler);
            this.cacheFile = file;
            Log.i("Import zipped GPX: " + file);
        }

        @Override
        protected InputStream getInputStream() throws IOException {
            return new FileInputStream(cacheFile);
        }
    }

    static class ImportGpxZipAttachmentThread extends ImportGpxZipThread {
        private final Uri uri;
        private final ContentResolver contentResolver;

        public ImportGpxZipAttachmentThread(Uri uri, ContentResolver contentResolver, int listId, Handler importStepHandler, CancellableHandler progressHandler) {
            super(listId, importStepHandler, progressHandler);
            this.uri = uri;
            this.contentResolver = contentResolver;
            Log.i("Import zipped GPX from uri: " + uri);
        }

        @Override
        protected InputStream getInputStream() throws IOException {
            return contentResolver.openInputStream(uri);
        }
    }

    final private CancellableHandler progressHandler = new CancellableHandler() {
        @Override
        public void handleRegularMessage(Message msg) {
            progress.setProgress(msg.arg1);
        }
    };

    final private Handler importStepHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case IMPORT_STEP_START:
                    final Message cancelMessage = importStepHandler.obtainMessage(IMPORT_STEP_CANCEL);
                    progress.show(fromActivity, res.getString(R.string.gpx_import_title_reading_file), res.getString(R.string.gpx_import_loading_caches), ProgressDialog.STYLE_HORIZONTAL, cancelMessage);
                    break;

                case IMPORT_STEP_READ_FILE:
                case IMPORT_STEP_READ_WPT_FILE:
                    progress.setMessage(res.getString(msg.arg1));
                    progress.setMaxProgressAndReset(msg.arg2);
                    break;

                case IMPORT_STEP_STORE_STATIC_MAPS:
                    progress.dismiss();
                    final Message skipMessage = importStepHandler.obtainMessage(IMPORT_STEP_STATIC_MAPS_SKIPPED, msg.arg2, 0);
                    progress.show(fromActivity, res.getString(R.string.gpx_import_title_static_maps), res.getString(R.string.gpx_import_store_static_maps), ProgressDialog.STYLE_HORIZONTAL, skipMessage);
                    progress.setMaxProgressAndReset(msg.arg2);
                    break;

                case IMPORT_STEP_STATIC_MAPS_SKIPPED:
                    progress.dismiss();
                    progressHandler.cancel();
                    final StringBuilder bufferSkipped = new StringBuilder(20);
                    bufferSkipped.append(res.getString(R.string.gpx_import_static_maps_skipped)).append(", ").append(msg.arg1).append(' ').append(res.getString(R.string.gpx_import_caches_imported));
                    Dialogs.message(fromActivity, R.string.gpx_import_title_caches_imported, bufferSkipped.toString());
                    importFinished();
                    break;

                case IMPORT_STEP_FINISHED:
                    progress.dismiss();
                    Dialogs.message(fromActivity, R.string.gpx_import_title_caches_imported, msg.arg1 + " " + res.getString(R.string.gpx_import_caches_imported));
                    importFinished();
                    break;

                case IMPORT_STEP_FINISHED_WITH_ERROR:
                    progress.dismiss();
                    Dialogs.message(fromActivity, R.string.gpx_import_title_caches_import_failed, res.getString(msg.arg1) + "\n\n" + msg.obj);
                    importFinished();
                    break;

                case IMPORT_STEP_CANCEL:
                    progress.dismiss();
                    progressHandler.cancel();
                    break;

                case IMPORT_STEP_CANCELED:
                    final StringBuilder bufferCanceled = new StringBuilder(20);
                    bufferCanceled.append(res.getString(R.string.gpx_import_canceled));
                    ActivityMixin.showShortToast(fromActivity, bufferCanceled.toString());
                    importFinished();
                    break;

                default:
                    break;
            }
        }
    };

    /**
     * @param gpxfile
     *            the gpx file
     * @return the expected file name of the waypoints file
     */
    static String getWaypointsFileNameForGpxFile(final File gpxfile) {
        if (gpxfile == null || !gpxfile.canRead()) {
            return null;
        }
        final String gpxFileName = gpxfile.getName();
        final File dir = gpxfile.getParentFile();
        final String[] filenameList = dir.list();
        for (final String filename : filenameList) {
            if (!StringUtils.containsIgnoreCase(filename, WAYPOINTS_FILE_SUFFIX)) {
                continue;
            }
            final String expectedGpxFileName = StringUtils.substringBeforeLast(filename, WAYPOINTS_FILE_SUFFIX)
                    + StringUtils.substringAfterLast(filename, WAYPOINTS_FILE_SUFFIX);
            if (gpxFileName.equals(expectedGpxFileName)) {
                return filename;
            }
        }
        return null;
    }

    protected void importFinished() {
        if (importFinishedHandler != null) {
            importFinishedHandler.sendEmptyMessage(0);
        }
    }
}
