package cgeo.geocaching.connector.oc;

import cgeo.geocaching.CgeoApplication;
import cgeo.geocaching.DataStore;
import cgeo.geocaching.Geocache;
import cgeo.geocaching.Image;
import cgeo.geocaching.LogEntry;
import cgeo.geocaching.R;
import cgeo.geocaching.Waypoint;
import cgeo.geocaching.connector.ConnectorFactory;
import cgeo.geocaching.connector.IConnector;
import cgeo.geocaching.connector.LogResult;
import cgeo.geocaching.connector.gc.GCConnector;
import cgeo.geocaching.connector.oc.OCApiConnector.ApiSupport;
import cgeo.geocaching.connector.oc.OCApiConnector.OAuthLevel;
import cgeo.geocaching.connector.oc.UserInfo.UserInfoStatus;
import cgeo.geocaching.enumerations.CacheAttribute;
import cgeo.geocaching.enumerations.CacheSize;
import cgeo.geocaching.enumerations.CacheType;
import cgeo.geocaching.enumerations.LoadFlags.SaveFlag;
import cgeo.geocaching.enumerations.LogType;
import cgeo.geocaching.enumerations.StatusCode;
import cgeo.geocaching.enumerations.WaypointType;
import cgeo.geocaching.geopoint.Geopoint;
import cgeo.geocaching.geopoint.GeopointFormatter;
import cgeo.geocaching.geopoint.Viewport;
import cgeo.geocaching.network.Network;
import cgeo.geocaching.network.OAuth;
import cgeo.geocaching.network.Parameters;
import cgeo.geocaching.settings.Settings;
import cgeo.geocaching.utils.Log;
import cgeo.geocaching.utils.SynchronizedDateFormat;

import ch.boye.httpclientandroidlib.HttpResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

final class OkapiClient {

    private static final char SEPARATOR = '|';
    private static final String SEPARATOR_STRING = Character.toString(SEPARATOR);
    private static final SynchronizedDateFormat LOG_DATE_FORMAT = new SynchronizedDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ", TimeZone.getTimeZone("UTC"), Locale.US);
    private static final SynchronizedDateFormat ISO8601DATEFORMAT = new SynchronizedDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());

    private static final String CACHE_ATTRNAMES = "attrnames";
    private static final String CACHE_ATTR_ACODES = "attr_acodes";
    private static final String WPT_LOCATION = "location";
    private static final String WPT_DESCRIPTION = "description";
    private static final String WPT_TYPE = "type";
    private static final String WPT_NAME = "name";
    private static final String CACHE_IS_WATCHED = "is_watched";
    private static final String CACHE_WPTS = "alt_wpts";
    private static final String CACHE_STATUS_ARCHIVED = "Archived";
    private static final String CACHE_STATUS_DISABLED = "Temporarily unavailable";
    private static final String CACHE_IS_FOUND = "is_found";
    private static final String CACHE_SIZE_DEPRECATED = "size";
    private static final String CACHE_SIZE2 = "size2";
    private static final String CACHE_VOTES = "rating_votes";
    private static final String CACHE_NOTFOUNDS = "notfounds";
    private static final String CACHE_FOUNDS = "founds";
    private static final String CACHE_HIDDEN = "date_hidden";
    private static final String CACHE_LATEST_LOGS = "latest_logs";
    private static final String CACHE_IMAGE_URL = "url";
    private static final String CACHE_IMAGE_CAPTION = "caption";
    private static final String CACHE_IMAGES = "images";
    private static final String CACHE_HINT = "hint";
    private static final String CACHE_DESCRIPTION = "description";
    private static final String CACHE_RECOMMENDATIONS = "recommendations";
    private static final String CACHE_RATING = "rating";
    private static final String CACHE_TERRAIN = "terrain";
    private static final String CACHE_DIFFICULTY = "difficulty";
    private static final String CACHE_OWNER = "owner";
    private static final String CACHE_STATUS = "status";
    private static final String CACHE_TYPE = "type";
    private static final String CACHE_LOCATION = "location";
    private static final String CACHE_NAME = "name";
    private static final String CACHE_CODE = "code";
    private static final String CACHE_REQ_PASSWORD = "req_passwd";
    private static final String CACHE_MY_NOTES = "my_notes";

    private static final String LOG_TYPE = "type";
    private static final String LOG_COMMENT = "comment";
    private static final String LOG_DATE = "date";
    private static final String LOG_USER = "user";

    private static final String USER_UUID = "uuid";
    private static final String USER_USERNAME = "username";
    private static final String USER_CACHES_FOUND = "caches_found";
    private static final String USER_INFO_FIELDS = "username|caches_found";

    // the several realms of possible fields for cache retrieval:
    // Core: for livemap requests (L3 - only with level 3 auth)
    // Additional: additional fields for full cache (L3 - only for level 3 auth, current - only for connectors with current api)
    private static final String SERVICE_CACHE_CORE_FIELDS = "code|name|location|type|status|difficulty|terrain|size|size2|date_hidden";
    private static final String SERVICE_CACHE_CORE_L3_FIELDS = "is_found";
    private static final String SERVICE_CACHE_ADDITIONAL_FIELDS = "owner|founds|notfounds|rating|rating_votes|recommendations|description|hint|images|latest_logs|alt_wpts|attrnames|req_passwd";
    private static final String SERVICE_CACHE_ADDITIONAL_CURRENT_FIELDS = "gc_code|attribution_note|attr_acodes";
    private static final String SERVICE_CACHE_ADDITIONAL_L3_FIELDS = "is_watched|my_notes";

    private static final String METHOD_SEARCH_ALL = "services/caches/search/all";
    private static final String METHOD_SEARCH_BBOX = "services/caches/search/bbox";
    private static final String METHOD_SEARCH_NEAREST = "services/caches/search/nearest";
    private static final String METHOD_RETRIEVE_CACHES = "services/caches/geocaches";

    public static Geocache getCache(final String geoCode) {
        final Parameters params = new Parameters("cache_code", geoCode);
        final IConnector connector = ConnectorFactory.getConnector(geoCode);
        if (!(connector instanceof OCApiConnector)) {
            return null;
        }

        final OCApiConnector ocapiConn = (OCApiConnector) connector;

        params.add("fields", getFullFields(ocapiConn));
        params.add("attribution_append", "none");

        final JSONResult result = request(ocapiConn, OkapiService.SERVICE_CACHE, params);

        return result.isSuccess ? parseCache(result.data) : null;
    }

    public static List<Geocache> getCachesAround(final Geopoint center, final OCApiConnector connector) {
        final String centerString = GeopointFormatter.format(GeopointFormatter.Format.LAT_DECDEGREE_RAW, center) + SEPARATOR + GeopointFormatter.format(GeopointFormatter.Format.LON_DECDEGREE_RAW, center);
        final Parameters params = new Parameters("search_method", METHOD_SEARCH_NEAREST);
        final Map<String, String> valueMap = new LinkedHashMap<String, String>();
        valueMap.put("center", centerString);
        valueMap.put("limit", "20");
        valueMap.put("radius", "200");

        return requestCaches(connector, params, valueMap, false);
    }

    public static List<Geocache> getCachesByOwner(final String username, final OCApiConnector connector) {
        return getCachesByUser(username, connector, "owner_uuid");
    }

    public static List<Geocache> getCachesByFinder(final String username, final OCApiConnector connector) {
        return getCachesByUser(username, connector, "found_by");
    }

    private static List<Geocache> getCachesByUser(final String username, final OCApiConnector connector, final String userRequestParam) {
        final Parameters params = new Parameters("search_method", METHOD_SEARCH_ALL);
        final Map<String, String> valueMap = new LinkedHashMap<String, String>();
        final @Nullable
        String uuid = getUserUUID(connector, username);
        if (StringUtils.isEmpty(uuid)) {
            return Collections.emptyList();
        }
        valueMap.put(userRequestParam, uuid);

        return requestCaches(connector, params, valueMap, connector.isSearchForMyCaches(username));
    }

    public static List<Geocache> getCachesNamed(final Geopoint center, final String namePart, final OCApiConnector connector) {
        final Map<String, String> valueMap = new LinkedHashMap<String, String>();
        final Parameters params;

        // search around current position, if there is a position
        if (center != null) {
            final String centerString = GeopointFormatter.format(GeopointFormatter.Format.LAT_DECDEGREE_RAW, center) + SEPARATOR + GeopointFormatter.format(GeopointFormatter.Format.LON_DECDEGREE_RAW, center);
            params = new Parameters("search_method", METHOD_SEARCH_NEAREST);
            valueMap.put("center", centerString);
            valueMap.put("limit", "20");
        }
        else {
            params = new Parameters("search_method", METHOD_SEARCH_ALL);
            valueMap.put("limit", "20");
        }

        // full wildcard search, maybe we need to change this after some testing and evaluation
        valueMap.put("name", "*" + namePart + "*");
        return requestCaches(connector, params, valueMap, false);
    }

    private static List<Geocache> requestCaches(final OCApiConnector connector, final Parameters params, final Map<String, String> valueMap, final boolean my) {
        // if a global type filter is set, and OKAPI does not know that type, then return an empty list instead of all caches
        if (Settings.getCacheType() != CacheType.ALL && StringUtils.isBlank(getFilterFromType())) {
            return Collections.emptyList();
        }

        addFilterParams(valueMap, connector, my);
        params.add("search_params", new JSONObject(valueMap).toString());
        addRetrieveParams(params, connector);

        final JSONObject data = request(connector, OkapiService.SERVICE_SEARCH_AND_RETRIEVE, params).data;

        if (data == null) {
            return Collections.emptyList();
        }

        return parseCaches(data);
    }

    /**
     * Assumes level 3 OAuth.
     */
    public static List<Geocache> getCachesBBox(final Viewport viewport, final OCApiConnector connector) {

        if (viewport.getLatitudeSpan() == 0 || viewport.getLongitudeSpan() == 0) {
            return Collections.emptyList();
        }

        final String bboxString = GeopointFormatter.format(GeopointFormatter.Format.LAT_DECDEGREE_RAW, viewport.bottomLeft)
                + SEPARATOR + GeopointFormatter.format(GeopointFormatter.Format.LON_DECDEGREE_RAW, viewport.bottomLeft)
                + SEPARATOR + GeopointFormatter.format(GeopointFormatter.Format.LAT_DECDEGREE_RAW, viewport.topRight)
                + SEPARATOR + GeopointFormatter.format(GeopointFormatter.Format.LON_DECDEGREE_RAW, viewport.topRight);
        final Parameters params = new Parameters("search_method", METHOD_SEARCH_BBOX);
        final Map<String, String> valueMap = new LinkedHashMap<String, String>();
        valueMap.put("bbox", bboxString);

        return requestCaches(connector, params, valueMap, false);
    }

    public static boolean setWatchState(final Geocache cache, final boolean watched, final OCApiConnector connector) {
        final Parameters params = new Parameters("cache_code", cache.getGeocode());
        params.add("watched", watched ? "true" : "false");

        final JSONObject data = request(connector, OkapiService.SERVICE_MARK_CACHE, params).data;

        if (data == null) {
            return false;
        }

        cache.setOnWatchlist(watched);

        return true;
    }

    public static LogResult postLog(final Geocache cache, final LogType logType, final Calendar date, final String log, final String logPassword, final OCApiConnector connector) {
        final Parameters params = new Parameters("cache_code", cache.getGeocode());
        params.add("logtype", logType.oc_type);
        params.add("comment", log);
        params.add("comment_format", "plaintext");
        params.add("when", LOG_DATE_FORMAT.format(date.getTime()));
        if (logType.equals(LogType.NEEDS_MAINTENANCE)) {
            params.add("needs_maintenance", "true");
        }
        if (logPassword != null) {
            params.add("password", logPassword);
        }

        final JSONObject data = request(connector, OkapiService.SERVICE_SUBMIT_LOG, params).data;

        if (data == null) {
            return new LogResult(StatusCode.LOG_POST_ERROR, "");
        }

        try {
            if (data.getBoolean("success")) {
                return new LogResult(StatusCode.NO_ERROR, data.getString("log_uuid"));
            }

            return new LogResult(StatusCode.LOG_POST_ERROR, "");
        } catch (final JSONException e) {
            Log.e("OkapiClient.postLog", e);
        }
        return new LogResult(StatusCode.LOG_POST_ERROR, "");
    }

    private static List<Geocache> parseCaches(final JSONObject response) {
        try {
            // Check for empty result
            final String result = response.getString("results");
            if (StringUtils.isBlank(result) || StringUtils.equals(result, "[]")) {
                return Collections.emptyList();
            }

            // Get and iterate result list
            final JSONObject cachesResponse = response.getJSONObject("results");
            if (cachesResponse != null) {
                final List<Geocache> caches = new ArrayList<Geocache>(cachesResponse.length());
                @SuppressWarnings("unchecked")
                final
                Iterator<String> keys = cachesResponse.keys();
                while (keys.hasNext()) {
                    final String key = keys.next();
                    final Geocache cache = parseSmallCache(cachesResponse.getJSONObject(key));
                    caches.add(cache);
                }
                return caches;
            }
        } catch (final JSONException e) {
            Log.e("OkapiClient.parseCachesResult", e);
        }
        return Collections.emptyList();
    }

    private static Geocache parseSmallCache(final JSONObject response) {
        final Geocache cache = new Geocache();
        cache.setReliableLatLon(true);
        try {

            parseCoreCache(response, cache);

            DataStore.saveCache(cache, EnumSet.of(SaveFlag.SAVE_CACHE));
        } catch (final JSONException e) {
            Log.e("OkapiClient.parseSmallCache", e);
        }
        return cache;
    }

    private static Geocache parseCache(final JSONObject response) {
        final Geocache cache = new Geocache();
        cache.setReliableLatLon(true);
        try {

            parseCoreCache(response, cache);

            // not used: url
            final JSONObject owner = response.getJSONObject(CACHE_OWNER);
            cache.setOwnerDisplayName(parseUser(owner));

            cache.getLogCounts().put(LogType.FOUND_IT, response.getInt(CACHE_FOUNDS));
            cache.getLogCounts().put(LogType.DIDNT_FIND_IT, response.getInt(CACHE_NOTFOUNDS));

            if (!response.isNull(CACHE_RATING)) {
                cache.setRating((float) response.getDouble(CACHE_RATING));
            }
            cache.setVotes(response.getInt(CACHE_VOTES));

            cache.setFavoritePoints(response.getInt(CACHE_RECOMMENDATIONS));
            // not used: req_password
            // Prepend gc-link to description if available
            final StringBuilder description = new StringBuilder(500);
            if (!response.isNull("gc_code")) {
                final String gccode = response.getString("gc_code");
                description.append(CgeoApplication.getInstance().getResources()
                        .getString(R.string.cache_listed_on, GCConnector.getInstance().getName()))
                        .append(": <a href=\"http://coord.info/")
                        .append(gccode)
                        .append("\">")
                        .append(gccode)
                        .append("</a><br /><br />");
            }
            description.append(response.getString(CACHE_DESCRIPTION));
            cache.setDescription(description.toString());

            // currently the hint is delivered as HTML (contrary to OKAPI documentation), so we can store it directly
            cache.setHint(response.getString(CACHE_HINT));
            // not used: hints

            final JSONArray images = response.getJSONArray(CACHE_IMAGES);
            if (images != null) {
                for (int i = 0; i < images.length(); i++) {
                    final JSONObject imageResponse = images.getJSONObject(i);
                    final String title = imageResponse.getString(CACHE_IMAGE_CAPTION);
                    final String url = absoluteUrl(imageResponse.getString(CACHE_IMAGE_URL), cache.getGeocode());
                    // all images are added as spoiler images, although OKAPI has spoiler and non spoiler images
                    cache.addSpoiler(new Image(url, title));
                }
            }

            cache.setAttributes(parseAttributes(response.getJSONArray(CACHE_ATTRNAMES), response.optJSONArray(CACHE_ATTR_ACODES)));
            //TODO: Store license per cache
            //cache.setLicense(response.getString("attribution_note"));
            cache.setWaypoints(parseWaypoints(response.getJSONArray(CACHE_WPTS)), false);
            if (!response.isNull(CACHE_IS_WATCHED)) {
                cache.setOnWatchlist(response.getBoolean(CACHE_IS_WATCHED));
            }
            if (!response.isNull(CACHE_MY_NOTES)) {
                cache.setPersonalNote(response.getString(CACHE_MY_NOTES));
                cache.parseWaypointsFromNote();
            }
            cache.setLogPasswordRequired(response.getBoolean(CACHE_REQ_PASSWORD));

            cache.setDetailedUpdatedNow();
            // save full detailed caches
            DataStore.saveCache(cache, EnumSet.of(SaveFlag.SAVE_DB));
            DataStore.saveLogsWithoutTransaction(cache.getGeocode(), parseLogs(response.getJSONArray(CACHE_LATEST_LOGS)));
        } catch (final JSONException e) {
            Log.e("OkapiClient.parseCache", e);
        }
        return cache;
    }

    private static void parseCoreCache(final JSONObject response, final Geocache cache) throws JSONException {
        cache.setGeocode(response.getString(CACHE_CODE));
        cache.setName(response.getString(CACHE_NAME));
        // not used: names
        setLocation(cache, response.getString(CACHE_LOCATION));
        cache.setType(getCacheType(response.getString(CACHE_TYPE)));

        final String status = response.getString(CACHE_STATUS);
        cache.setDisabled(status.equalsIgnoreCase(CACHE_STATUS_DISABLED));
        cache.setArchived(status.equalsIgnoreCase(CACHE_STATUS_ARCHIVED));

        cache.setSize(getCacheSize(response));
        cache.setDifficulty((float) response.getDouble(CACHE_DIFFICULTY));
        cache.setTerrain((float) response.getDouble(CACHE_TERRAIN));

        if (!response.isNull(CACHE_IS_FOUND)) {
            cache.setFound(response.getBoolean(CACHE_IS_FOUND));
        }
        cache.setHidden(parseDate(response.getString(CACHE_HIDDEN)));
    }

    private static String absoluteUrl(final String url, final String geocode) {
        final Uri uri = Uri.parse(url);

        if (!uri.isAbsolute()) {
            final IConnector connector = ConnectorFactory.getConnector(geocode);
            final String host = connector.getHost();
            if (StringUtils.isNotBlank(host)) {
                return "http://" + host + "/" + url;
            }
        }
        return url;
    }

    private static String parseUser(final JSONObject user) throws JSONException {
        return user.getString(USER_USERNAME);
    }

    private static List<LogEntry> parseLogs(final JSONArray logsJSON) {
        List<LogEntry> result = null;
        for (int i = 0; i < logsJSON.length(); i++) {
            try {
                final JSONObject logResponse = logsJSON.getJSONObject(i);
                final LogEntry log = new LogEntry(
                        parseUser(logResponse.getJSONObject(LOG_USER)),
                        parseDate(logResponse.getString(LOG_DATE)).getTime(),
                        parseLogType(logResponse.getString(LOG_TYPE)),
                        logResponse.getString(LOG_COMMENT).trim());
                if (result == null) {
                    result = new ArrayList<LogEntry>();
                }
                result.add(log);
            } catch (final JSONException e) {
                Log.e("OkapiClient.parseLogs", e);
            }
        }
        return result;
    }

    private static List<Waypoint> parseWaypoints(final JSONArray wptsJson) {
        List<Waypoint> result = null;
        for (int i = 0; i < wptsJson.length(); i++) {
            try {
                final JSONObject wptResponse = wptsJson.getJSONObject(i);
                final Waypoint wpt = new Waypoint(wptResponse.getString(WPT_NAME),
                        parseWptType(wptResponse.getString(WPT_TYPE)),
                        false);
                wpt.setNote(wptResponse.getString(WPT_DESCRIPTION));
                final Geopoint pt = parseCoords(wptResponse.getString(WPT_LOCATION));
                if (pt != null) {
                    wpt.setCoords(pt);
                }
                if (result == null) {
                    result = new ArrayList<Waypoint>();
                }
                wpt.setPrefix(wpt.getName());
                result.add(wpt);
            } catch (final JSONException e) {
                Log.e("OkapiClient.parseWaypoints", e);
            }
        }
        return result;
    }

    private static LogType parseLogType(final String logType) {
        if ("Found it".equalsIgnoreCase(logType)) {
            return LogType.FOUND_IT;
        }
        if ("Didn't find it".equalsIgnoreCase(logType)) {
            return LogType.DIDNT_FIND_IT;
        }
        if ("Will attend".equalsIgnoreCase(logType)) {
            return LogType.WILL_ATTEND;
        }
        if ("Attended".equalsIgnoreCase(logType)) {
            return LogType.ATTENDED;
        }
        if ("Temporarily unavailable".equalsIgnoreCase(logType)) {
            return LogType.TEMP_DISABLE_LISTING;
        }
        if ("Ready to search".equalsIgnoreCase(logType)) {
            return LogType.ENABLE_LISTING;
        }
        if ("Archived".equalsIgnoreCase(logType)) {
            return LogType.ARCHIVE;
        }
        if ("Needs maintenance".equalsIgnoreCase(logType)) {
            return LogType.NEEDS_MAINTENANCE;
        }
        if ("Moved".equalsIgnoreCase(logType)) {
            return LogType.UPDATE_COORDINATES;
        }
        if ("OC Team comment".equalsIgnoreCase(logType)) {
            return LogType.POST_REVIEWER_NOTE;
        }
        return LogType.NOTE;
    }

    private static WaypointType parseWptType(final String wptType) {
        if ("parking".equalsIgnoreCase(wptType)) {
            return WaypointType.PARKING;
        }
        if ("path".equalsIgnoreCase(wptType)) {
            return WaypointType.TRAILHEAD;
        }
        if ("stage".equalsIgnoreCase(wptType)) {
            return WaypointType.STAGE;
        }
        if ("physical-stage".equalsIgnoreCase(wptType)) {
            return WaypointType.STAGE;
        }
        if ("virtual-stage".equalsIgnoreCase(wptType)) {
            return WaypointType.PUZZLE;
        }
        if ("final".equalsIgnoreCase(wptType)) {
            return WaypointType.FINAL;
        }
        if ("poi".equalsIgnoreCase(wptType)) {
            return WaypointType.TRAILHEAD;
        }
        return WaypointType.WAYPOINT;
    }

    private static Date parseDate(final String date) {
        final String strippedDate = date.replaceAll("\\+0([0-9]){1}\\:00", "+0$100");
        try {
            return ISO8601DATEFORMAT.parse(strippedDate);
        } catch (final ParseException e) {
            Log.e("OkapiClient.parseDate", e);
        }
        return null;
    }

    private static Geopoint parseCoords(final String location) {
        final String latitude = StringUtils.substringBefore(location, SEPARATOR_STRING);
        final String longitude = StringUtils.substringAfter(location, SEPARATOR_STRING);
        if (StringUtils.isNotBlank(latitude) && StringUtils.isNotBlank(longitude)) {
            return new Geopoint(latitude, longitude);
        }

        return null;
    }

    private static List<String> parseAttributes(final JSONArray nameList, final JSONArray acodeList) {

        final List<String> result = new ArrayList<String>();

        for (int i = 0; i < nameList.length(); i++) {
            try {
                final String name = nameList.getString(i);
                final int acode = acodeList != null ? Integer.parseInt(acodeList.getString(i).substring(1)) : CacheAttribute.NO_ID;
                final CacheAttribute attr = CacheAttribute.getByOcACode(acode);

                if (attr != null) {
                    result.add(attr.rawName);
                } else {
                    result.add(name);
                }
            } catch (final JSONException e) {
                Log.e("OkapiClient.parseAttributes", e);
            }
        }

        return result;
    }

    private static void setLocation(final Geocache cache, final String location) {
        final String latitude = StringUtils.substringBefore(location, SEPARATOR_STRING);
        final String longitude = StringUtils.substringAfter(location, SEPARATOR_STRING);
        cache.setCoords(new Geopoint(latitude, longitude));
    }

    private static CacheSize getCacheSize(final JSONObject response) {
        if (response.isNull(CACHE_SIZE2)) {
            return getCacheSizeDeprecated(response);
        }
        try {
            final String size = response.getString(CACHE_SIZE2);
            return CacheSize.getById(size);
        } catch (JSONException e) {
            Log.e("OkapiClient.getCacheSize", e);
            return getCacheSizeDeprecated(response);
        }
    }

    private static CacheSize getCacheSizeDeprecated(final JSONObject response) {
        if (response.isNull(CACHE_SIZE_DEPRECATED)) {
            return CacheSize.NOT_CHOSEN;
        }
        double size = 0;
        try {
            size = response.getDouble(CACHE_SIZE_DEPRECATED);
        } catch (final JSONException e) {
            Log.e("OkapiClient.getCacheSize", e);
        }
        switch ((int) Math.round(size)) {
            case 1:
                return CacheSize.MICRO;
            case 2:
                return CacheSize.SMALL;
            case 3:
                return CacheSize.REGULAR;
            case 4:
                return CacheSize.LARGE;
            case 5:
                return CacheSize.VERY_LARGE;
            default:
                break;
        }
        return CacheSize.NOT_CHOSEN;
    }

    private static CacheType getCacheType(final String cacheType) {
        if (cacheType.equalsIgnoreCase("Traditional")) {
            return CacheType.TRADITIONAL;
        }
        if (cacheType.equalsIgnoreCase("Multi")) {
            return CacheType.MULTI;
        }
        if (cacheType.equalsIgnoreCase("Quiz")) {
            return CacheType.MYSTERY;
        }
        if (cacheType.equalsIgnoreCase("Virtual")) {
            return CacheType.VIRTUAL;
        }
        if (cacheType.equalsIgnoreCase("Event")) {
            return CacheType.EVENT;
        }
        if (cacheType.equalsIgnoreCase("Webcam")) {
            return CacheType.WEBCAM;
        }
        if (cacheType.equalsIgnoreCase("Math/Physics")) {
            return CacheType.MYSTERY;
        }
        if (cacheType.equalsIgnoreCase("Drive-In")) {
            return CacheType.TRADITIONAL;
        }
        return CacheType.UNKNOWN;
    }

    private static String getCoreFields(final OCApiConnector connector) {
        if (connector == null) {
            Log.e("OkapiClient.getCoreFields called with invalid connector");
            return StringUtils.EMPTY;
        }

        if (connector.getSupportedAuthLevel() == OAuthLevel.Level3) {
            return SERVICE_CACHE_CORE_FIELDS + SEPARATOR + SERVICE_CACHE_CORE_L3_FIELDS;
        }

        return SERVICE_CACHE_CORE_FIELDS;
    }

    private static String getFullFields(final OCApiConnector connector) {
        if (connector == null) {
            Log.e("OkapiClient.getFullFields called with invalid connector");
            return StringUtils.EMPTY;
        }

        final StringBuilder res = new StringBuilder(500);

        res.append(SERVICE_CACHE_CORE_FIELDS);
        res.append(SEPARATOR).append(SERVICE_CACHE_ADDITIONAL_FIELDS);
        if (connector.getSupportedAuthLevel() == OAuthLevel.Level3) {
            res.append(SEPARATOR).append(SERVICE_CACHE_CORE_L3_FIELDS);
            res.append(SEPARATOR).append(SERVICE_CACHE_ADDITIONAL_L3_FIELDS);
        }
        if (connector.getApiSupport() == ApiSupport.current) {
            res.append(SEPARATOR).append(SERVICE_CACHE_ADDITIONAL_CURRENT_FIELDS);
        }

        return res.toString();
    }

    @NonNull
    private static JSONResult request(final OCApiConnector connector, final OkapiService service, final Parameters params) {
        if (connector == null) {
            return new JSONResult(null);
        }

        final String host = connector.getHost();
        if (StringUtils.isBlank(host)) {
            return new JSONResult(null);
        }

        params.add("langpref", getPreferredLanguage());

        if (connector.getSupportedAuthLevel() == OAuthLevel.Level3) {
            ImmutablePair<String, String> tokens = Settings.getTokenPair(connector.getTokenPublicPrefKeyId(), connector.getTokenSecretPrefKeyId());
            OAuth.signOAuth(host, service.methodName, "GET", false, params, tokens.left, tokens.right, connector.getCK(), connector.getCS());
        } else {
            connector.addAuthentication(params);
        }

        final String uri = "http://" + host + service.methodName;
        return new JSONResult(Network.getRequest(uri, params));
    }

    private static String getPreferredLanguage() {
        final String code = Locale.getDefault().getCountry();
        if (StringUtils.isNotBlank(code)) {
            return StringUtils.lowerCase(code) + "|en";
        }
        return "en";
    }

    private static void addFilterParams(final Map<String, String> valueMap, final OCApiConnector connector, final boolean my) {
        if (!Settings.isExcludeDisabledCaches()) {
            valueMap.put("status", "Available|Temporarily unavailable");
        }
        if (!my && Settings.isExcludeMyCaches() && connector.getSupportedAuthLevel() == OAuthLevel.Level3) {
            valueMap.put("exclude_my_own", "true");
            valueMap.put("found_status", "notfound_only");
        }
        if (Settings.getCacheType() != CacheType.ALL) {
            valueMap.put("type", getFilterFromType());
        }
    }

    private static void addRetrieveParams(final Parameters params, final OCApiConnector connector) {
        params.add("retr_method", METHOD_RETRIEVE_CACHES);
        params.add("retr_params", "{\"fields\": \"" + getCoreFields(connector) + "\"}");
        params.add("wrap", "true");
    }

    private static String getFilterFromType() {
        switch (Settings.getCacheType()) {
            case EVENT:
                return "Event";
            case MULTI:
                return "Multi";
            case MYSTERY:
                return "Quiz";
            case TRADITIONAL:
                return "Traditional";
            case VIRTUAL:
                return "Virtual";
            case WEBCAM:
                return "Webcam";
            default:
                return "";
        }
    }

    public static @Nullable
    String getUserUUID(final OCApiConnector connector, final String userName) {
        final Parameters params = new Parameters("fields", USER_UUID, USER_USERNAME, userName);

        final JSONResult result = request(connector, OkapiService.SERVICE_USER_BY_USERNAME, params);
        if (!result.isSuccess) {
            final OkapiError error = new OkapiError(result.data);
            Log.e("OkapiClient.getUserUUID: error getting user info: '" + error.getMessage() + "'");
            return null;
        }

        JSONObject data = result.data;
        if (!data.isNull(USER_UUID)) {
            try {
                return data.getString(USER_UUID);
            } catch (final JSONException e) {
                Log.e("OkapiClient.getUserUUID - uuid", e);
            }
        }

        return null;
    }

    public static UserInfo getUserInfo(final OCApiLiveConnector connector) {
        final Parameters params = new Parameters("fields", USER_INFO_FIELDS);

        final JSONResult result = request(connector, OkapiService.SERVICE_USER, params);

        if (!result.isSuccess) {
            final OkapiError error = new OkapiError(result.data);
            Log.e("OkapiClient.getUserInfo: error getting user info: '" + error.getMessage() + "'");
            return new UserInfo(StringUtils.EMPTY, 0, UserInfoStatus.getFromOkapiError(error.getResult()));
        }

        JSONObject data = result.data;

        String name = StringUtils.EMPTY;
        boolean successUserName = false;

        if (!data.isNull(USER_USERNAME)) {
            try {
                name = data.getString(USER_USERNAME);
                successUserName = true;
            } catch (final JSONException e) {
                Log.e("OkapiClient.getUserInfo - name", e);
            }
        }

        int finds = 0;
        boolean successFinds = false;

        if (!data.isNull(USER_CACHES_FOUND)) {
            try {
                finds = data.getInt(USER_CACHES_FOUND);
                successFinds = true;
            } catch (final JSONException e) {
                Log.e("OkapiClient.getUserInfo - finds", e);
            }
        }

        return new UserInfo(name, finds, successUserName && successFinds ? UserInfoStatus.SUCCESSFUL : UserInfoStatus.FAILED);
    }

    /**
     * Retrieves error information from an unsuccessful Okapi-response
     *
     * @param response
     *            response containing an error object
     * @return OkapiError object with detailed information
     */
    public static OkapiError decodeErrorResponse(HttpResponse response) {
        final JSONResult result = new JSONResult(response);
        if (!result.isSuccess) {
            return new OkapiError(result.data);
        }
        return new OkapiError(new JSONObject());
    }

    /**
     * Encapsulates response state and content of an HTTP-request that expects a JSON result. <code>isSuccess</code> is
     * only true, if the response state was success and <code>data</code> is not null.
     */
    private static class JSONResult {

        public final boolean isSuccess;
        public final JSONObject data;

        public JSONResult(final @Nullable HttpResponse response) {
            boolean isSuccess = Network.isSuccess(response);
            final String responseData = Network.getResponseDataAlways(response);
            JSONObject data = null;
            if (responseData != null) {
                try {
                    data = new JSONObject(responseData);
                } catch (final JSONException e) {
                    Log.w("JSONResult", e);
                }
            }
            this.data = data;
            this.isSuccess = isSuccess && data != null;
        }
    }
}
