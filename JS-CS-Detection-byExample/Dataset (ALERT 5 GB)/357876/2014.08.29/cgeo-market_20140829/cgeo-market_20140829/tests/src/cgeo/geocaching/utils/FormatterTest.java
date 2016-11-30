package cgeo.geocaching.utils;

import static org.assertj.core.api.Assertions.assertThat;

import cgeo.geocaching.CgeoApplication;
import cgeo.geocaching.R;
import cgeo.geocaching.Waypoint;
import cgeo.geocaching.enumerations.WaypointType;
import cgeo.geocaching.utils.Formatter;

import android.test.AndroidTestCase;

public class FormatterTest extends AndroidTestCase {

    public static void testParkingWaypoint() {
        assertFormatting(new Waypoint("you can park here", WaypointType.PARKING, false), WaypointType.PARKING.getL10n());
    }

    public static void testOriginalWaypoint() {
        assertFormatting(new Waypoint("an original", WaypointType.ORIGINAL, false), WaypointType.ORIGINAL.getL10n());
    }

    public static void testOwnWaypoint() {
        final Waypoint own = new Waypoint("my own", WaypointType.OWN, true);
        own.setPrefix(Waypoint.PREFIX_OWN);
        assertFormatting(own, CgeoApplication.getInstance().getString(R.string.waypoint_custom));
    }

    private static void assertFormatting(Waypoint waypoint, String expected) {
        assertThat(Formatter.formatWaypointInfo(waypoint)).isEqualTo(expected);
    }

}
