package cgeo.geocaching.connector.gc;

import org.eclipse.jdt.annotation.NonNull;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * These patterns have been optimized for speed. Improve them only if you can prove
 * that *YOUR* pattern is faster. Use RegExRealPerformanceTest to show.
 *
 * For further information about patterns have a look at
 * http://download.oracle.com/javase/1.4.2/docs/api/java/util/regex/Pattern.html
 */
public final class GCConstants {

    static final String GC_URL = "http://www.geocaching.com/";
    static final String GC_TILE_URL = "http://tiles.geocaching.com/";
    /** Live Map */
    public final static @NonNull String URL_LIVE_MAP = GC_URL + "map/default.aspx";
    /** Live Map pop-up */
    public final static @NonNull String URL_LIVE_MAP_DETAILS = GC_TILE_URL + "map.details";
    /** Caches in a tile */
    public final static @NonNull String URL_MAP_INFO = GC_TILE_URL + "map.info";
    /** Tile itself */
    public final static @NonNull String URL_MAP_TILE = GC_TILE_URL + "map.png";

    /**
     * Patterns for parsing the result of a (detailed) search
     */
    public final static Pattern PATTERN_HINT = Pattern.compile("<div id=\"div_hint\"[^>]*>(.*?)</div>", Pattern.DOTALL);
    public final static Pattern PATTERN_DESC = Pattern.compile("<span id=\"ctl00_ContentBody_LongDescription\">(.*?)</span>\\s*</div>\\s*<p>\\s*</p>\\s*<p id=\"ctl00_ContentBody_hints\">", Pattern.DOTALL);
    public final static Pattern PATTERN_SHORTDESC = Pattern.compile("<span id=\"ctl00_ContentBody_ShortDescription\">(.*?)</span>\\s*</div>", Pattern.DOTALL);
    public final static Pattern PATTERN_GEOCODE = Pattern.compile("class=\"CoordInfoCode\">(GC[0-9A-Z]+)</span>");
    public final static Pattern PATTERN_CACHEID = Pattern.compile("/seek/log\\.aspx\\?ID=(\\d+)");
    public final static Pattern PATTERN_GUID = Pattern.compile(Pattern.quote("&wid=") + "([0-9a-z\\-]+)" + Pattern.quote("&"));
    public final static Pattern PATTERN_SIZE = Pattern.compile("<div id=\"ctl00_ContentBody_size\" class=\"CacheSize[^\"]*\">[^<]*<p[^>]*>[^S]*Size[^:]*:[^<]*<span[^>]*>[^<]*<img src=\"[^\"]*/icons/container/[a-z_]+\\.gif\" alt=\"\\w+: ([^\"]+)\"[^>]*>[^<]*<small>[^<]*</small>[^<]*</span>[^<]*</p>");
    public final static Pattern PATTERN_LATLON = Pattern.compile("<span id=\"uxLatLon\"[^>]*>(.*?)</span>");
    public final static Pattern PATTERN_LATLON_ORIG = Pattern.compile("\\{\"isUserDefined\":true[^}]+?\"oldLatLngDisplay\":\"([^\"]+)\"\\}");
    public final static Pattern PATTERN_LOCATION = Pattern.compile(Pattern.quote("<span id=\"ctl00_ContentBody_Location\">In ") + "(?:<a href=[^>]*>)?(.*?)<");
    public final static Pattern PATTERN_PERSONALNOTE = Pattern.compile("<span id=\"cache_note\"[^>]*>(.*?)</span>", Pattern.DOTALL);
    public final static Pattern PATTERN_NAME = Pattern.compile("<span id=\"ctl00_ContentBody_CacheName\">(.*?)</span>");
    public final static Pattern PATTERN_DIFFICULTY = Pattern.compile("<span id=\"ctl00_ContentBody_uxLegendScale\"[^>]*>[^<]*<img src=\"[^\"]*/images/stars/stars([0-9_]+)\\.gif\"");
    public final static Pattern PATTERN_TERRAIN = Pattern.compile("<span id=\"ctl00_ContentBody_Localize[\\d]+\"[^>]*>[^<]*<img src=\"[^\"]*/images/stars/stars([0-9_]+)\\.gif\"");
    public final static Pattern PATTERN_OWNER_USERID = Pattern.compile("other caches <a href=\"/seek/nearest\\.aspx\\?u=(.*?)\">hidden</a> or");
    public final static Pattern PATTERN_FOUND = Pattern.compile("ctl00_ContentBody_GeoNav_logText\">(Found It|Attended)");
    public final static Pattern PATTERN_FOUND_ALTERNATIVE = Pattern.compile("<div class=\"StatusInformationWidget FavoriteWidget\"");
    public final static Pattern PATTERN_FOUND_DATE = Pattern.compile(">Logged on: ([^<]+?)<");
    public final static Pattern PATTERN_OWNER_DISPLAYNAME = Pattern.compile("<div id=\"ctl00_ContentBody_mcd1\">[^<]+<a href=\"[^\"]+\">([^<]+)</a>");
    public final static Pattern PATTERN_TYPE = Pattern.compile("<a href=\"/seek/nearest.aspx\\?tx=([0-9a-f-]+)");
    public final static Pattern PATTERN_HIDDEN = Pattern.compile("<div id=\"ctl00_ContentBody_mcd2\">\\W*Hidden[\\s:]*([^<]+?)</div>");
    public final static Pattern PATTERN_HIDDENEVENT = Pattern.compile("Event\\s*Date\\s*:\\s*([^<]+)<div id=\"calLinks\">", Pattern.DOTALL);
    public final static Pattern PATTERN_FAVORITE = Pattern.compile("<div id=\"pnlFavoriteCache\">"); // without 'class="hideMe"' inside the tag !
    public final static Pattern PATTERN_FAVORITECOUNT = Pattern.compile("<span class=\"favorite-value\">\\D*([0-9]+?)\\D*</span>");
    public final static Pattern PATTERN_COUNTLOGS = Pattern.compile("<span id=\"ctl00_ContentBody_lblFindCounts\"><p(.+?)</p></span>");
    public final static Pattern PATTERN_LOGBOOK = Pattern.compile("initalLogs = (\\{.+\\});"); // The "inital" typo really comes from gc.com site
    /** Two groups ! */
    public final static Pattern PATTERN_COUNTLOG = Pattern.compile("<img src=\"/images/logtypes/([0-9]+)\\.png\"[^>]+> (\\d*[,.]?\\d+)");
    public static final Pattern PATTERN_PREMIUMMEMBERS = Pattern.compile("<p class=\"Warning NoBottomSpacing\">This is a Premium Member Only cache.</p>");
    public final static Pattern PATTERN_ATTRIBUTES = Pattern.compile("Attributes\\s*</h3>[^<]*<div class=\"WidgetBody\">((?:[^<]*<img src=\"[^\"]+\" alt=\"[^\"]+\"[^>]*>)+?)[^<]*<p");
    /** Two groups ! */
    public final static Pattern PATTERN_ATTRIBUTESINSIDE = Pattern.compile("[^<]*<img src=\"([^\"]+)\" alt=\"([^\"]+?)\"");
    public final static Pattern PATTERN_SPOILER_IMAGE = Pattern.compile("<a href=\"(http://imgcdn\\.geocaching\\.com[^.]+\\.(jpg|jpeg|png|gif))\"[^>]+>([^<]+)</a>(?:<br />([^<]+)<br /><br />)?");
    public final static Pattern PATTERN_INVENTORY = Pattern.compile("<span id=\"ctl00_ContentBody_uxTravelBugList_uxInventoryLabel\">\\W*Inventory[^<]*</span>[^<]*</h3>[^<]*<div class=\"WidgetBody\">([^<]*<ul>(([^<]*<li>[^<]*<a href=\"[^\"]+\"[^>]*>[^<]*<img src=\"[^\"]+\"[^>]*>[^<]*<span>[^<]+<\\/span>[^<]*<\\/a>[^<]*<\\/li>)+)[^<]*<\\/ul>)?");
    public final static Pattern PATTERN_INVENTORYINSIDE = Pattern.compile("[^<]*<li>[^<]*<a href=\"[a-z0-9\\-\\_\\.\\?\\/\\:\\@]*\\/track\\/details\\.aspx\\?guid=([0-9a-z\\-]+)[^\"]*\"[^>]*>[^<]*<img src=\"[^\"]+\"[^>]*>[^<]*<span>([^<]+)<\\/span>[^<]*<\\/a>[^<]*<\\/li>");
    public final static Pattern PATTERN_WATCHLIST = Pattern.compile(Pattern.quote("watchlist.aspx") + ".{1,50}" + Pattern.quote("action=rem"));
    public final static Pattern PATTERN_RELATED_WEB_PAGE = Pattern.compile("id=\"ctl00_ContentBody_uxCacheUrl\" title=\"Related Web Page\" href=\"(.*?)\">");

    // Info box top-right
    public static final Pattern PATTERN_LOGIN_NAME = Pattern.compile("\"SignedInProfileLink\">(.*?)</a>");
    public static final Pattern PATTERN_MEMBER_STATUS = Pattern.compile("<span id=\"ctl00_litPMLevel\">([^<]+)</span>");
    public static final String MEMBER_STATUS_RENEW = "<a id=\"ctl00_hlRenew";
    public static final String MEMBER_STATUS_PM = "Premium Member";
    /** Use replaceAll("[,.]","") on the resulting string before converting to an int */
    public static final Pattern PATTERN_CACHES_FOUND = Pattern.compile("<strong[^>]*>.*?([\\d,.]+) Caches? Found", Pattern.DOTALL);
    public static final Pattern PATTERN_AVATAR_IMAGE_PROFILE_PAGE = Pattern.compile("src=\"(https?://(imgcdn\\.geocaching\\.com|[^>\"]+\\.cloudfront\\.net)/avatar/[0-9a-f-]+\\.jpg)\"[^>]*alt=\"");
    public static final Pattern PATTERN_LOGIN_NAME_LOGIN_PAGE = Pattern.compile("ctl00_ContentBody_lbUsername\">.*<strong>(.*)</strong>");
    public static final Pattern PATTERN_CUSTOMDATE = Pattern.compile("<option selected=\"selected\" value=\"([ /Mdy-]+)\">");
    public static final Pattern PATTERN_MAP_LOGGED_IN = Pattern.compile("<a href=\"https?://www.geocaching.com/my/\" class=\"CommonUsername\"");

    /**
     * Patterns for parsing trackables
     */
    public final static Pattern PATTERN_TRACKABLE_GUID = Pattern.compile("<a id=\"ctl00_ContentBody_lnkPrint\" title=\"[^\"]*\" href=\".*sheet\\.aspx\\?guid=([a-z0-9\\-]+)\"[^>]*>[^<]*</a>");
    public final static Pattern PATTERN_TRACKABLE_GEOCODE = Pattern.compile("<strong>(TB[0-9A-Z]+)[^<]*</strong> to reference this item.");

    // multiple error codes, depending on the search term for the trackable code
    public final static String ERROR_TB_DOES_NOT_EXIST = "does not exist in the system";
    public final static String ERROR_TB_ELEMENT_EXCEPTION = "ElementNotFound Exception";
    public final static String ERROR_TB_ARITHMETIC_OVERFLOW = "operation resulted in an overflow";
    public final static String ERROR_TB_NOT_ACTIVATED = "hasn't been activated";
    /**
     * some parts of the webpage don't correctly encode the name, therefore this pattern must be checked with a
     * trackable name that needs HTML encoding
     */
    public final static Pattern PATTERN_TRACKABLE_NAME = Pattern.compile("<span id=\"ctl00_ContentBody_lbHeading\">(.*?)</span>");
    /** Two groups ! */
    public final static Pattern PATTERN_TRACKABLE_OWNER = Pattern.compile("<a id=\"ctl00_ContentBody_BugDetails_BugOwner\"[^>]+href=\"https?://www.geocaching.com/profile/\\?guid=(.*?)\">(.*?)</a>");
    public final static Pattern PATTERN_TRACKABLE_RELEASES = Pattern.compile("<dt>\\W*Released:[^<]*</dt>[^<]*<dd>[^<]*<span id=\"ctl00_ContentBody_BugDetails_BugReleaseDate\">([^<]+)<\\/span>[^<]*</dd>");
    public final static Pattern PATTERN_TRACKABLE_ORIGIN = Pattern.compile("<dt>\\W*Origin:[^<]*</dt>[^<]*<dd>[^<]*<span id=\"ctl00_ContentBody_BugDetails_BugOrigin\">([^<]+)<\\/span>[^<]*</dd>");
    /** Two groups ! */
    public final static Pattern PATTERN_TRACKABLE_SPOTTEDCACHE = Pattern.compile("<dt>\\W*Recently Spotted:[^<]*</dt>[^<]*<dd>[^<]*<a id=\"ctl00_ContentBody_BugDetails_BugLocation\" title=\"[^\"]*\" href=\"[^\"]*/seek/cache_details.aspx\\?guid=([a-z0-9\\-]+)\">In ([^<]+)</a>[^<]*</dd>");
    /** Two groups ! */
    public final static Pattern PATTERN_TRACKABLE_SPOTTEDUSER = Pattern.compile("<dt>\\W*Recently Spotted:[^<]*</dt>[^<]*<dd>[^<]*<a id=\"ctl00_ContentBody_BugDetails_BugLocation\" href=\"[^\"]*/profile/\\?guid=([a-z0-9\\-]+)\">In the hands of ([^<]+).</a>[^<]*</dd>");
    public final static Pattern PATTERN_TRACKABLE_SPOTTEDUNKNOWN = Pattern.compile("<dt>\\W*Recently Spotted:[^<]*</dt>[^<]*<dd>[^<]*<a id=\"ctl00_ContentBody_BugDetails_BugLocation\">Unknown Location[^<]*</a>[^<]*</dd>");
    public final static Pattern PATTERN_TRACKABLE_SPOTTEDOWNER = Pattern.compile("<dt>\\W*Recently Spotted:[^<]*</dt>[^<]*<dd>[^<]*<a id=\"ctl00_ContentBody_BugDetails_BugLocation\">In the hands of the owner[^<]*</a>[^<]*</dd>");
    public final static Pattern PATTERN_TRACKABLE_GOAL = Pattern.compile("<div id=\"TrackableGoal\">[^<]*<p>(.*?)</p>[^<]*</div>", Pattern.DOTALL);
    /** Four groups */
    public final static Pattern PATTERN_TRACKABLE_DETAILSIMAGE = Pattern.compile("<h3>\\W*About This Item[^<]*</h3>[^<]*<div id=\"TrackableDetails\">([^<]*<p>([^<]*<img id=\"ctl00_ContentBody_BugDetails_BugImage\" class=\"[^\"]+\" src=\"([^\"]+)\"[^>]*>)?[^<]*</p>)?[^<]*<p[^>]*>(.*)</p>[^<]*</div> <div id=\"ctl00_ContentBody_BugDetails_uxAbuseReport\">");
    public final static Pattern PATTERN_TRACKABLE_ICON = Pattern.compile("<img id=\"ctl00_ContentBody_BugTypeImage\" class=\"TravelBugHeaderIcon\" src=\"([^\"]+)\"[^>]*>");
    public final static Pattern PATTERN_TRACKABLE_TYPE = Pattern.compile("<img id=\"ctl00_ContentBody_BugTypeImage\" class=\"TravelBugHeaderIcon\" src=\"[^\"]+\" alt=\"([^\"]+)\"[^>]*>");
    public final static Pattern PATTERN_TRACKABLE_DISTANCE = Pattern.compile("<h4[^>]*\\W*Tracking History \\(([0-9.,]+(km|mi))[^\\)]*\\)");
    public final static Pattern PATTERN_TRACKABLE_LOG = Pattern.compile("<tr class=\"Data BorderTop .+?/images/logtypes/([^.]+)\\.png[^>]+>&nbsp;([^<]+)</td>.+?guid.+?>([^<]+)</a>.+?(?:guid=([^\"]+)\">(<span[^>]+>)?([^<]+)</.+?)?<td colspan=\"4\">\\s*<div>(.*?)</div>\\s*(?:<ul.+?ul>)?\\s*</td>\\s*</tr>");
    public final static Pattern PATTERN_TRACKABLE_LOG_IMAGES = Pattern.compile("<li><a href=\"([^\"]+)\".+?LogImgTitle.+?>([^<]+)</");

    /**
     * Patterns for parsing the result of a search (next)
     */
    public final static Pattern PATTERN_SEARCH_TYPE = Pattern.compile("<td class=\"Merge\">.*?<img src=\"[^\"]*/images/wpttypes/[^.]+\\.gif\" alt=\"([^\"]+)\" title=\"[^\"]+\"[^>]*>[^<]*</a>");
    public final static Pattern PATTERN_SEARCH_GUIDANDDISABLED = Pattern.compile("SearchResultsWptType.*?<a href=\"[^\"]*\" class=\"lnk ([^\"]*)\"><span>([^<]*)</span>[^|]*[|][^|]*[|]([^<]*)<");
    /** Two groups **/
    public final static Pattern PATTERN_SEARCH_TRACKABLES = Pattern.compile("<a id=\"ctl00_ContentBody_dlResults_ctl[0-9]+_uxTravelBugList\" class=\"tblist\" data-tbcount=\"([0-9]+)\" data-id=\"[^\"]*\"[^>]*>(.*)</a>");
    /** Second group used */
    public final static Pattern PATTERN_SEARCH_TRACKABLESINSIDE = Pattern.compile("(<img src=\"[^\"]+\" alt=\"([^\"]+)\" title=\"[^\"]*\" />[^<]*)");
    public final static Pattern PATTERN_SEARCH_DIRECTION_DISTANCE = Pattern.compile("<img src=\"/images/icons/compass/([^\\.]+)\\.gif\"[^>]*>[^<]*<br />([^<]+)</span>");
    public final static Pattern PATTERN_SEARCH_DIFFICULTY_TERRAIN = Pattern.compile("<span class=\"small\">([0-5]([\\.,]5)?)/([0-5]([\\.,]5)?)</span><br />");
    public final static Pattern PATTERN_SEARCH_CONTAINER = Pattern.compile("<img src=\"/images/icons/container/([^\\.]+)\\.gif\"");
    public final static Pattern PATTERN_SEARCH_GEOCODE = Pattern.compile("\\|\\W*(GC[0-9A-Z]+)[^\\|]*\\|");
    public final static Pattern PATTERN_SEARCH_ID = Pattern.compile("name=\"CID\"[^v]*value=\"(\\d+)\"");
    public final static Pattern PATTERN_SEARCH_FAVORITE = Pattern.compile("favorite-rank\">([0-9,.]+)</span>");
    public final static Pattern PATTERN_SEARCH_TOTALCOUNT = Pattern.compile("<span>Total Records\\D*(\\d+)<");
    public final static Pattern PATTERN_SEARCH_RECAPTCHA = Pattern.compile("<script[^>]*src=\"[^\"]*/recaptcha/api/challenge\\?k=([^\"]+)\"[^>]*>");
    public final static Pattern PATTERN_SEARCH_RECAPTCHACHALLENGE = Pattern.compile("challenge : '([^']+)'");
    public final static Pattern PATTERN_SEARCH_HIDDEN_DATE = Pattern.compile("<td valign=\"top\"[^<]+<span class=\"small\">([^<]+)</span>");

    /**
     * Patterns for waypoints
     */
    public final static Pattern PATTERN_WPTYPE = Pattern.compile("\\/wpttypes\\/sm\\/(.+)\\.jpg");
    public final static Pattern PATTERN_WPPREFIXORLOOKUPORLATLON = Pattern.compile(">([^<]*<[^>]+>)?([^<]+)(<[^>]+>[^<]*)?<\\/td>");
    public final static Pattern PATTERN_WPNAME = Pattern.compile(">[^<]*<a[^>]+>([^<]*)<\\/a>");
    public final static Pattern PATTERN_WPNOTE = Pattern.compile("colspan=\"6\">(.*)" + Pattern.quote("</td>"), Pattern.DOTALL);

    /**
     * Patterns for different purposes
     */
    /** replace line break and paragraph tags */
    public final static Pattern PATTERN_LINEBREAK = Pattern.compile("<(br|p)[^>]*>");
    public final static Pattern PATTERN_TYPEBOX = Pattern.compile("<select name=\"ctl00\\$ContentBody\\$LogBookPanel1\\$ddLogType\" id=\"ctl00_ContentBody_LogBookPanel1_ddLogType\"[^>]*>"
            + "(([^<]*<option[^>]*>[^<]+</option>)+)[^<]*</select>", Pattern.CASE_INSENSITIVE);
    public final static Pattern PATTERN_TYPE2 = Pattern.compile("<option( selected=\"selected\")? value=\"(\\d+)\">[^<]+</option>", Pattern.CASE_INSENSITIVE);
    // FIXME: pattern is over specified
    public final static Pattern PATTERN_TRACKABLE = Pattern.compile("<tr id=\"ctl00_ContentBody_LogBookPanel1_uxTrackables_repTravelBugs_ctl[0-9]+_row\"[^>]*>"
            + "[^<]*<td>[^<]*<a href=\"[^\"]+\">([A-Z0-9]+)</a>[^<]*</td>[^<]*<td>([^<]+)</td>[^<]*<td>"
            + "[^<]*<select name=\"ctl00\\$ContentBody\\$LogBookPanel1\\$uxTrackables\\$repTravelBugs\\$ctl([0-9]+)\\$ddlAction\"[^>]*>"
            + "([^<]*<option value=\"([0-9]+)(_[a-z]+)?\">[^<]+</option>)+"
            + "[^<]*</select>[^<]*</td>[^<]*</tr>", Pattern.CASE_INSENSITIVE);
    public final static Pattern PATTERN_MAINTENANCE = Pattern.compile("<span id=\"ctl00_ContentBody_LogBookPanel1_lbConfirm\"[^>]*>([^<]*<font[^>]*>)?([^<]+)(</font>[^<]*)?</span>", Pattern.CASE_INSENSITIVE);
    public final static Pattern PATTERN_OK1 = Pattern.compile("<h2[^>]*>[^<]*<span id=\"ctl00_ContentBody_lbHeading\"[^>]*>[^<]*</span>[^<]*</h2>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
    public final static Pattern PATTERN_OK2 = Pattern.compile("<div id=[\"|']ctl00_ContentBody_LogBookPanel1_ViewLogPanel[\"|']>", Pattern.CASE_INSENSITIVE);
    public final static Pattern PATTERN_IMAGE_UPLOAD_URL = Pattern.compile("title=\"Click for Larger Image\"\\s*src=\"(.*?)\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
    public final static Pattern PATTERN_VIEWSTATEFIELDCOUNT = Pattern.compile("id=\"__VIEWSTATEFIELDCOUNT\"[^(value)]+value=\"(\\d+)\"[^>]+>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
    public final static Pattern PATTERN_VIEWSTATES = Pattern.compile("id=\"__VIEWSTATE(\\d*)\"[^(value)]+value=\"([^\"]+)\"[^>]+>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
    public final static Pattern PATTERN_USERTOKEN = Pattern.compile("userToken\\s*=\\s*'([^']+)'");
    public final static Pattern PATTERN_LIST_PQ = Pattern.compile(Pattern.quote("(") + "(\\d+)" + Pattern.quote(")") + ".+?guid=(.+?)\".*?title=\"(.+?)\"");

    /** Live Map since 14.02.2012 */
    public final static Pattern PATTERN_USERSESSION = Pattern.compile("UserSession\\('([^']+)'");
    public final static Pattern PATTERN_SESSIONTOKEN = Pattern.compile("sessionToken:'([^']+)'");

    public final static Pattern PATTERN_LOG_IMAGE_UPLOAD = Pattern.compile("/seek/upload\\.aspx\\?LID=(\\d+)", Pattern.CASE_INSENSITIVE);

    public final static String STRING_PREMIUMONLY_2 = "Sorry, the owner of this listing has made it viewable to Premium Members only.";
    public final static String STRING_PREMIUMONLY_1 = "has chosen to make this cache listing visible to Premium Members only.";
    public final static String STRING_UNPUBLISHED_OTHER = "you cannot view this cache listing until it has been published";
    public final static String STRING_UNPUBLISHED_FROM_SEARCH = "class=\"UnpublishedCacheSearchWidge\"";
    public final static String STRING_UNKNOWN_ERROR = "An Error Has Occurred";
    public final static String STRING_DISABLED = "<li>This cache is temporarily unavailable.";
    public final static String STRING_ARCHIVED = "<li>This cache has been archived,";
    public final static String STRING_CACHEDETAILS = "id=\"cacheDetails\"";

    /** Number of logs to retrieve from GC.com */
    public final static int NUMBER_OF_LOGS = 35;
    /** Maximum number of chars for personal note. **/
    public final static int PERSONAL_NOTE_MAX_CHARS = 500;

    private final static String SEQUENCE_GCID = "0123456789ABCDEFGHJKMNPQRTVWXYZ";
    private final static long GC_BASE31 = 31;
    private final static long GC_BASE16 = 16;

    /**
     * Convert GCCode (geocode) to (old) GCIds
     *
     * Based on http://www.geoclub.de/viewtopic.php?f=111&t=54859&start=40
     * see http://support.groundspeak.com/index.php?pg=kb.printer.friendly&id=1#p221
     */
    public static long gccodeToGCId(final String gccode) {
        long base = GC_BASE31;
        final String geocodeWO = gccode.substring(2).toUpperCase(Locale.US);

        if ((geocodeWO.length() < 4) || (geocodeWO.length() == 4 && SEQUENCE_GCID.indexOf(geocodeWO.charAt(0)) < 16)) {
            base = GC_BASE16;
        }

        long gcid = 0;
        for (int p = 0; p < geocodeWO.length(); p++) {
            gcid = base * gcid + SEQUENCE_GCID.indexOf(geocodeWO.charAt(p));
        }

        if (base == GC_BASE31) {
            gcid += Math.pow(16, 4) - 16 * Math.pow(31, 3);
        }
        return gcid;
    }

    private GCConstants() {
        // this class shall not have instances
    }

}
