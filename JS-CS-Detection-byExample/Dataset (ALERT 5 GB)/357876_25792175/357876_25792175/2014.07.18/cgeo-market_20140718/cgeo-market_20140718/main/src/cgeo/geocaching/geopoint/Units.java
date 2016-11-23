package cgeo.geocaching.geopoint;

import cgeo.geocaching.settings.Settings;

import org.apache.commons.lang3.tuple.ImmutablePair;

public class Units {

    public static ImmutablePair<Double, String> scaleDistance(final double distanceKilometers) {
        double distance;
        String units;
        if (Settings.isUseImperialUnits()) {
            distance = distanceKilometers / IConversion.MILES_TO_KILOMETER;
            if (distance >= 0.1) {
                units = "mi";
            } else {
                distance *= 5280;
                units = "ft";
            }
        } else {
            if (distanceKilometers >= 1) {
                distance = distanceKilometers;
                units = "km";
            } else {
                distance = distanceKilometers * 1000;
                units = "m";
            }
        }
        return new ImmutablePair<Double, String>(distance, units);
    }

    public static String getDistanceFromKilometers(final Float distanceKilometers) {
        if (distanceKilometers == null) {
            return "?";
        }

        final ImmutablePair<Double, String> scaled = scaleDistance(distanceKilometers);
        String formatString;
        if (scaled.left >= 100) {
            formatString = "%.0f";
        } else if (scaled.left >= 10) {
            formatString = "%.1f";
        } else {
            formatString = "%.2f";
        }

        return String.format(formatString + " %s", scaled.left, scaled.right);
    }

    public static String getDistanceFromMeters(float meters) {
        return getDistanceFromKilometers(meters / 1000f);
    }

    public static String getSpeed(float kilometersPerHour) {
        final String speed = getDistanceFromKilometers(kilometersPerHour);
        if (speed.endsWith("mi")) {
            return speed.substring(0, speed.length() - 2) + "mph";
        }
        return speed + (!Settings.isUseImperialUnits() ? "/h" : "ph");
    }
}
