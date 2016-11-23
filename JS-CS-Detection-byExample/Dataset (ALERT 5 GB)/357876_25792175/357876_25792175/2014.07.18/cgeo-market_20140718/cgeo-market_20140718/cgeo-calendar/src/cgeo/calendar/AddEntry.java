package cgeo.calendar;

import android.content.ContentValues;
import android.net.Uri;
import android.text.Html;

import java.util.Date;

class AddEntry extends AbstractAddEntry {

    private int calendarId;

    /**
     * @param entry
     * @param calendarId
     *            The selected calendar
     */
    public AddEntry(CalendarEntry entry, CalendarActivity activity, int calendarId) {
        super(entry, activity);
        this.calendarId = calendarId;
    }

    @Override
    protected void addEntryToCalendarInternal() {
        final Uri calendarProvider = Compatibility.getCalendarEventsProviderURI();

        final Date eventDate = entry.parseDate();
        final String description = entry.parseDescription();

        // values
        final ContentValues event = new ContentValues();
        event.put("calendar_id", calendarId);
        final long eventTime = eventDate.getTime();
        final int entryStartTimeMinutes = entry.getStartTimeMinutes();
        if (entryStartTimeMinutes >= 0) {
            event.put("dtstart", eventTime + entryStartTimeMinutes * 60000L);
        } else {
            event.put("dtstart", eventTime); // midnight
            event.put("dtend", eventTime + 86400000); // + one day
            event.put("allDay", 1);
        }
        event.put("eventTimezone", "UTC");
        event.put("title", Html.fromHtml(entry.getName()).toString());
        event.put("description", description);

        if (entry.getCoords().length() > 0) {
            event.put("eventLocation", entry.getCoords());
        }
        event.put("hasAlarm", 0);

        activity.getContentResolver().insert(calendarProvider, event);
    }

}
