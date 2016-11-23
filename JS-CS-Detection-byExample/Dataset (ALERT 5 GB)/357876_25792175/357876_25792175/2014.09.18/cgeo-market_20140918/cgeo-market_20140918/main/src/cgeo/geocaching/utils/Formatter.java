package cgeo.geocaching.utils;

import cgeo.geocaching.CgeoApplication;
import cgeo.geocaching.Geocache;
import cgeo.geocaching.R;
import cgeo.geocaching.Waypoint;
import cgeo.geocaching.enumerations.CacheListType;
import cgeo.geocaching.enumerations.CacheSize;
import cgeo.geocaching.enumerations.WaypointType;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.text.format.DateUtils;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class Formatter {

    /** Text separator used for formatting texts */
    public static final String SEPARATOR = " · ";

    private static final Context context = CgeoApplication.getInstance().getBaseContext();

    /**
     * Generate a time string according to system-wide settings (locale, 12/24 hour)
     * such as "13:24".
     *
     * @param date
     *            milliseconds since the epoch
     * @return the formatted string
     */
    public static String formatTime(long date) {
        return DateUtils.formatDateTime(context, date, DateUtils.FORMAT_SHOW_TIME);
    }

    /**
     * Generate a date string according to system-wide settings (locale, date format)
     * such as "20 December" or "20 December 2010". The year will only be included when necessary.
     *
     * @param date
     *            milliseconds since the epoch
     * @return the formatted string
     */
    public static String formatDate(long date) {
        return DateUtils.formatDateTime(context, date, DateUtils.FORMAT_SHOW_DATE);
    }

    /**
     * Generate a date string according to system-wide settings (locale, date format)
     * such as "20 December 2010". The year will always be included, making it suitable
     * to generate long-lived log entries.
     *
     * @param date
     *            milliseconds since the epoch
     * @return the formatted string
     */
    public static String formatFullDate(long date) {
        return DateUtils.formatDateTime(context, date, DateUtils.FORMAT_SHOW_DATE
                | DateUtils.FORMAT_SHOW_YEAR);
    }

    /**
     * Generate a numeric date string according to system-wide settings (locale, date format)
     * such as "10/20/2010".
     *
     * @param date
     *            milliseconds since the epoch
     * @return the formatted string
     */
    public static String formatShortDate(long date) {
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);
        return dateFormat.format(date);
    }

    /**
     * Generate a numeric date string according to system-wide settings (locale, date format)
     * such as "10/20/2010". Today and yesterday will be presented as strings "today" and "yesterday".
     *
     * @param date
     *            milliseconds since the epoch
     * @return the formatted string
     */
    public static String formatShortDateVerbally(long date) {
        int diff = cgeo.geocaching.utils.DateUtils.daysSince(date);
        switch (diff) {
            case 0:
                return CgeoApplication.getInstance().getString(R.string.log_today);
            case 1:
                return CgeoApplication.getInstance().getString(R.string.log_yesterday);
            default:
                return formatShortDate(date);
        }
    }

    /**
     * Generate a numeric date and time string according to system-wide settings (locale,
     * date format) such as "7 sept. at 12:35".
     *
     * @param date
     *            milliseconds since the epoch
     * @return the formatted string
     */
    public static String formatShortDateTime(long date) {
        return DateUtils.formatDateTime(context, date, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_ABBREV_ALL);
    }

    /**
     * Generate a numeric date and time string according to system-wide settings (locale,
     * date format) such as "7 september at 12:35".
     *
     * @param date
     *            milliseconds since the epoch
     * @return the formatted string
     */
    public static String formatDateTime(long date) {
        return DateUtils.formatDateTime(context, date, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME);
    }

    public static String formatCacheInfoLong(Geocache cache, CacheListType cacheListType) {
        final ArrayList<String> infos = new ArrayList<>();
        if (StringUtils.isNotBlank(cache.getGeocode())) {
            infos.add(cache.getGeocode());
        }

        addShortInfos(cache, infos);

        if (cache.isPremiumMembersOnly()) {
            infos.add(CgeoApplication.getInstance().getString(R.string.cache_premium));
        }
        if (cacheListType != CacheListType.OFFLINE && cacheListType != CacheListType.HISTORY && cache.getListId() > 0) {
            infos.add(CgeoApplication.getInstance().getString(R.string.cache_offline));
        }
        return StringUtils.join(infos, Formatter.SEPARATOR);
    }

    public static String formatCacheInfoShort(Geocache cache) {
        final ArrayList<String> infos = new ArrayList<>();
        addShortInfos(cache, infos);
        return StringUtils.join(infos, Formatter.SEPARATOR);
    }

    private static void addShortInfos(Geocache cache, final ArrayList<String> infos) {
        if (cache.hasDifficulty()) {
            infos.add("D " + String.format("%.1f", cache.getDifficulty()));
        }
        if (cache.hasTerrain()) {
            infos.add("T " + String.format("%.1f", cache.getTerrain()));
        }

        // don't show "not chosen" for events and virtuals, that should be the normal case
        if (cache.getSize() != CacheSize.UNKNOWN && cache.showSize()) {
            infos.add(cache.getSize().getL10n());
        } else if (cache.isEventCache()) {
            final Date hiddenDate = cache.getHiddenDate();
            if (hiddenDate != null) {
                infos.add(Formatter.formatShortDate(hiddenDate.getTime()));
            }
        }
    }

    public static String formatCacheInfoHistory(Geocache cache) {
        final ArrayList<String> infos = new ArrayList<>(3);
        infos.add(StringUtils.upperCase(cache.getGeocode()));
        infos.add(Formatter.formatDate(cache.getVisitedDate()));
        infos.add(Formatter.formatTime(cache.getVisitedDate()));
        return StringUtils.join(infos, Formatter.SEPARATOR);
    }

    public static String formatWaypointInfo(Waypoint waypoint) {
        final List<String> infos = new ArrayList<>(3);
        WaypointType waypointType = waypoint.getWaypointType();
        if (waypointType != WaypointType.OWN && waypointType != null) {
            infos.add(waypointType.getL10n());
        }
        if (Waypoint.PREFIX_OWN.equalsIgnoreCase(waypoint.getPrefix())) {
            infos.add(CgeoApplication.getInstance().getString(R.string.waypoint_custom));
        } else {
            if (StringUtils.isNotBlank(waypoint.getPrefix())) {
                infos.add(waypoint.getPrefix());
            }
            if (StringUtils.isNotBlank(waypoint.getLookup())) {
                infos.add(waypoint.getLookup());
            }
        }
        return StringUtils.join(infos, Formatter.SEPARATOR);
    }
}
