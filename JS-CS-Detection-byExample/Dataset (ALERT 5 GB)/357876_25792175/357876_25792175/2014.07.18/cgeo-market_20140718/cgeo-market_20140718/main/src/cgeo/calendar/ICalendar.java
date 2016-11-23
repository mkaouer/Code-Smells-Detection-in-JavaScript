package cgeo.calendar;

public interface ICalendar {
    static final String CALENDAR_ADDON_URI = "market://details?id=cgeo.calendar";

    static final String INTENT = "cgeo.calendar.RESERVE";

    static final String URI_SCHEME = "add";
    static final String URI_HOST = "cgeo.org";

    static final String PARAM_SHORT_DESC = "shortDesc"; // cache short description
    static final String PARAM_HIDDEN_DATE = "hiddenDate"; // cache hidden date in milliseconds
    static final String PARAM_URL = "url"; // cache URL
    static final String PARAM_NOTE = "note"; // personal note
    static final String PARAM_NAME = "name"; // cache name
    static final String PARAM_LOCATION = "location"; // cache location, or empty string
    static final String PARAM_COORDS = "coords"; // cache coordinates, or empty string
    static final String PARAM_START_TIME_MINUTES = "time"; // time of start
}
