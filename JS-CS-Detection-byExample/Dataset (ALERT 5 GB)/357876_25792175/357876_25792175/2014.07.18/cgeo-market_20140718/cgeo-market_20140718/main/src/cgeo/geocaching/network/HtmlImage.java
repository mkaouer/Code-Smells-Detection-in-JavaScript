package cgeo.geocaching.network;

import cgeo.geocaching.CgeoApplication;
import cgeo.geocaching.R;
import cgeo.geocaching.compatibility.Compatibility;
import cgeo.geocaching.connector.ConnectorFactory;
import cgeo.geocaching.files.LocalStorage;
import cgeo.geocaching.list.StoredList;
import cgeo.geocaching.utils.CancellableHandler;
import cgeo.geocaching.utils.FileUtils;
import cgeo.geocaching.utils.ImageUtils;
import cgeo.geocaching.utils.Log;
import cgeo.geocaching.utils.RxUtils;

import ch.boye.httpclientandroidlib.HttpResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Scheduler;
import rx.Scheduler.Inner;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.text.Html;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HtmlImage implements Html.ImageGetter {

    // This class implements an all-purpose image getter that can also be used as a ImageGetter interface
    // when displaying caches. An instance mainly has three possible use cases:
    //  - If onlySave is true, getDrawable() will return null immediately and will queue the image retrieval
    //    and saving in the loading subject. Downloads will start in parallel when the blocking
    //    waitForBackgroundLoading() method is called, and they can be cancelled through the given handler.
    //  - If onlySave is false and the instance is called through fetchDrawable(), then an observable for the
    //    given URL will be returned. This observable will emit the local copy of the image if it is present,
    //    regardless of its freshness, then if needed an updated fresher copy after retrieving it from the network.
    //  - If onlySave is false and the instance is used as an ImageGetter, only the final version of the
    //    image will be returned.

    private static final String[] BLOCKED = new String[] {
            "gccounter.de",
            "gccounter.com",
            "cachercounter/?",
            "gccounter/imgcount.php",
            "flagcounter.com",
            "compteur-blog.net",
            "counter.digits.com",
            "andyhoppe",
            "besucherzaehler-homepage.de",
            "hitwebcounter.com",
            "kostenloser-counter.eu",
            "trendcounter.com",
            "hit-counter-download.com",
            "gcwetterau.de/counter"
    };
    public static final String SHARED = "shared";

    final private String geocode;
    /**
     * on error: return large error image, if <code>true</code>, otherwise empty 1x1 image
     */
    final private boolean returnErrorImage;
    final private int listId;
    final private boolean onlySave;
    final private int maxWidth;
    final private int maxHeight;
    final private Resources resources;

    // Background loading
    final private PublishSubject<Observable<String>> loading = PublishSubject.create();
    final Observable<String> waitForEnd = Observable.merge(loading).publish().refCount();
    final CompositeSubscription subscription = new CompositeSubscription(waitForEnd.subscribe());
    final private Scheduler downloadScheduler = Schedulers.executor(new ThreadPoolExecutor(10, 10, 5, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>()));

    public HtmlImage(final String geocode, final boolean returnErrorImage, final int listId, final boolean onlySave) {
        this.geocode = geocode;
        this.returnErrorImage = returnErrorImage;
        this.listId = listId;
        this.onlySave = onlySave;

        Point displaySize = Compatibility.getDisplaySize();
        this.maxWidth = displaySize.x - 25;
        this.maxHeight = displaySize.y - 25;
        this.resources = CgeoApplication.getInstance().getResources();
    }

    @Nullable
    @Override
    public BitmapDrawable getDrawable(final String url) {
        final Observable<BitmapDrawable> drawable = fetchDrawable(url);
        if (onlySave) {
            loading.onNext(drawable.map(new Func1<BitmapDrawable, String>() {
                @Override
                public String call(final BitmapDrawable bitmapDrawable) {
                    return url;
                }
            }));
            return null;
        }
        return drawable.toBlockingObservable().lastOrDefault(null);
    }

    // Caches are loaded from disk on a computation scheduler to avoid using more threads than cores while decoding
    // the image. Downloads happen on downloadScheduler, in parallel with image decoding.
    public Observable<BitmapDrawable> fetchDrawable(final String url) {

        if (StringUtils.isBlank(url) || ImageUtils.containsPattern(url, BLOCKED)) {
            return Observable.from(getTransparent1x1Image(resources));
        }

        // Explicit local file URLs are loaded from the filesystem regardless of their age. The IO part is short
        // enough to make the whole operation on the computation scheduler.
        if (FileUtils.isFileUrl(url)) {
            return Observable.defer(new Func0<Observable<? extends BitmapDrawable>>() {
                @Override
                public Observable<? extends BitmapDrawable> call() {
                    final Bitmap bitmap = loadCachedImage(FileUtils.urlToFile(url), true).getLeft();
                    return bitmap != null ? Observable.from(ImageUtils.scaleBitmapToFitDisplay(bitmap)) : Observable.<BitmapDrawable>empty();
                }
            }).subscribeOn(RxUtils.computationScheduler);
        }

        final boolean shared = url.contains("/images/icons/icon_");
        final String pseudoGeocode = shared ? SHARED : geocode;

        return Observable.create(new OnSubscribe<BitmapDrawable>() {
            @Override
            public void call(final Subscriber<? super BitmapDrawable> subscriber) {
                subscription.add(subscriber);
                subscriber.add(RxUtils.computationScheduler.schedule(new Action1<Inner>() {
                    @Override
                    public void call(final Inner inner) {
                        final Pair<BitmapDrawable, Boolean> loaded = loadFromDisk();
                        final BitmapDrawable bitmap = loaded.getLeft();
                        if (loaded.getRight()) {
                            subscriber.onNext(bitmap);
                            subscriber.onCompleted();
                            return;
                        }
                        if (bitmap != null && !onlySave) {
                            subscriber.onNext(bitmap);
                        }
                        subscriber.add(downloadScheduler.schedule(new Action1<Inner>() {
                            @Override
                            public void call(final Inner inner) {
                                downloadAndSave(subscriber);
                            }
                        }));
                    }
                }));
            }

            private Pair<BitmapDrawable, Boolean> loadFromDisk() {
                final Pair<Bitmap, Boolean> loadResult = loadImageFromStorage(url, pseudoGeocode, shared);
                final Bitmap bitmap = loadResult.getLeft();
                return new ImmutablePair<BitmapDrawable, Boolean>(bitmap != null ?
                        ImageUtils.scaleBitmapToFitDisplay(bitmap) :
                        null,
                        loadResult.getRight()
                );
            }

            private void downloadAndSave(final Subscriber<? super BitmapDrawable> subscriber) {
                final File file = LocalStorage.getStorageFile(pseudoGeocode, url, true, true);
                if (url.startsWith("data:image/")) {
                    if (url.contains(";base64,")) {
                        ImageUtils.decodeBase64ToFile(StringUtils.substringAfter(url, ";base64,"), file);
                    } else {
                        Log.e("HtmlImage.getDrawable: unable to decode non-base64 inline image");
                        subscriber.onCompleted();
                        return;
                    }
                } else {
                    if (subscriber.isUnsubscribed() || downloadOrRefreshCopy(url, file)) {
                        // The existing copy was fresh enough or we were unsubscribed earlier.
                        subscriber.onCompleted();
                        return;
                    }
                }
                if (onlySave) {
                    subscriber.onCompleted();
                } else {
                    RxUtils.computationScheduler.schedule(new Action1<Inner>() {
                        @Override
                        public void call(final Inner inner) {
                            final Pair<BitmapDrawable, Boolean> loaded = loadFromDisk();
                            final BitmapDrawable image = loaded.getLeft();
                            if (image != null) {
                                subscriber.onNext(image);
                            } else {
                                subscriber.onNext(returnErrorImage ?
                                        new BitmapDrawable(resources, BitmapFactory.decodeResource(resources, R.drawable.image_not_loaded)) :
                                        getTransparent1x1Image(resources));
                            }
                            subscriber.onCompleted();
                        }
                    });
                }
            }
        });
    }

    public void waitForBackgroundLoading(@Nullable final CancellableHandler handler) {
        if (handler != null) {
            handler.unsubscribeIfCancelled(subscription);
        }
        loading.onCompleted();
        waitForEnd.toBlockingObservable().lastOrDefault(null);
    }

    /**
     * Download or refresh the copy of <code>url</code> in <code>file</code>.
     *
     * @param url the url of the document
     * @param file the file to save the document in
     * @return <code>true</code> if the existing file was up-to-date, <code>false</code> otherwise
     */
    private boolean downloadOrRefreshCopy(final String url, final File file) {
        final String absoluteURL = makeAbsoluteURL(url);

        if (absoluteURL != null) {
            try {
                final HttpResponse httpResponse = Network.getRequest(absoluteURL, null, file);
                if (httpResponse != null) {
                    final int statusCode = httpResponse.getStatusLine().getStatusCode();
                    if (statusCode == 200) {
                        LocalStorage.saveEntityToFile(httpResponse, file);
                    } else if (statusCode == 304) {
                        if (!file.setLastModified(System.currentTimeMillis())) {
                            makeFreshCopy(file);
                        }
                        return true;
                    }
                }
            } catch (Exception e) {
                Log.e("HtmlImage.downloadOrRefreshCopy", e);
            }
        }
        return false;
    }

    /**
     * Make a fresh copy of the file to reset its timestamp. On some storage, it is impossible
     * to modify the modified time after the fact, in which case a brand new file must be
     * created if we want to be able to use the time as validity hint.
     *
     * See Android issue 1699.
     *
     * @param file the file to refresh
     */
    private static void makeFreshCopy(final File file) {
        final File tempFile = new File(file.getParentFile(), file.getName() + "-temp");
        if (file.renameTo(tempFile)) {
            LocalStorage.copy(tempFile, file);
            FileUtils.deleteIgnoringFailure(tempFile);
        }
        else {
            Log.e("Could not reset timestamp of file " + file.getAbsolutePath());
        }
    }

    private BitmapDrawable getTransparent1x1Image(final Resources res) {
        return new BitmapDrawable(res, BitmapFactory.decodeResource(resources, R.drawable.image_no_placement));
    }

    /**
     * Load an image from primary or secondary storage.
     *
     * @param url the image URL
     * @param pseudoGeocode the geocode or the shared name
     * @param forceKeep keep the image if it is there, without checking its freshness
     * @return <code>true</code> if the image was there and is fresh enough, <code>false</code> otherwise
     */
    @NonNull
    private Pair<Bitmap, Boolean> loadImageFromStorage(final String url, final String pseudoGeocode, final boolean forceKeep) {
        try {
            final File file = LocalStorage.getStorageFile(pseudoGeocode, url, true, false);
            final Pair<Bitmap, Boolean> image = loadCachedImage(file, forceKeep);
            if (image.getRight() || image.getLeft() != null) {
                return image;
            }
            final File fileSec = LocalStorage.getStorageSecFile(pseudoGeocode, url, true);
            return loadCachedImage(fileSec, forceKeep);
        } catch (Exception e) {
            Log.w("HtmlImage.loadImageFromStorage", e);
        }
        return new ImmutablePair<Bitmap, Boolean>(null, false);
    }

    @Nullable
    private String makeAbsoluteURL(final String url) {
        // Check if uri is absolute or not, if not attach the connector hostname
        // FIXME: that should also include the scheme
        if (Uri.parse(url).isAbsolute()) {
            return url;
        }

        final String host = ConnectorFactory.getConnector(geocode).getHost();
        if (StringUtils.isNotEmpty(host)) {
            final StringBuilder builder = new StringBuilder("http://");
            builder.append(host);
            if (!StringUtils.startsWith(url, "/")) {
                // FIXME: explain why the result URL would be valid if the path does not start with
                // a '/', or signal an error.
                builder.append('/');
            }
            builder.append(url);
            return builder.toString();
        }

        return null;
    }

    /**
     * Load a previously saved image.
     *
     * @param file the file on disk
     * @param forceKeep keep the image if it is there, without checking its freshness
     * @return a pair with <code>true</code> if the image was there and is fresh enough or <code>false</code> otherwise,
     *         and the image (possibly <code>null</code> if the first component is <code>false</code> and the image
     *         could not be loaded, or if the first component is <code>true</code> and <code>onlySave</code> is also
     *         <code>true</code>)
     */
    @NonNull
    private Pair<Bitmap, Boolean> loadCachedImage(final File file, final boolean forceKeep) {
        if (file.exists()) {
            final boolean freshEnough = listId >= StoredList.STANDARD_LIST_ID || file.lastModified() > (new Date().getTime() - (24 * 60 * 60 * 1000)) || forceKeep;
            if (onlySave) {
                return new ImmutablePair<Bitmap, Boolean>(null, true);
            }
            final BitmapFactory.Options bfOptions = new BitmapFactory.Options();
            bfOptions.inTempStorage = new byte[16 * 1024];
            bfOptions.inPreferredConfig = Bitmap.Config.RGB_565;
            setSampleSize(file, bfOptions);
            final Bitmap image = BitmapFactory.decodeFile(file.getPath(), bfOptions);
            if (image == null) {
                Log.e("Cannot decode bitmap from " + file.getPath());
                return new ImmutablePair<Bitmap, Boolean>(null, false);
            }
            return new ImmutablePair<Bitmap, Boolean>(image,
                    freshEnough);
        }
        return new ImmutablePair<Bitmap, Boolean>(null, false);
    }

    private void setSampleSize(final File file, final BitmapFactory.Options bfOptions) {
        //Decode image size only
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BufferedInputStream stream = null;
        try {
            stream = new BufferedInputStream(new FileInputStream(file));
            BitmapFactory.decodeStream(stream, null, options);
        } catch (FileNotFoundException e) {
            Log.e("HtmlImage.setSampleSize", e);
        } finally {
            IOUtils.closeQuietly(stream);
        }

        int scale = 1;
        if (options.outHeight > maxHeight || options.outWidth > maxWidth) {
            scale = Math.max(options.outHeight / maxHeight, options.outWidth / maxWidth);
        }
        bfOptions.inSampleSize = scale;
    }

}
