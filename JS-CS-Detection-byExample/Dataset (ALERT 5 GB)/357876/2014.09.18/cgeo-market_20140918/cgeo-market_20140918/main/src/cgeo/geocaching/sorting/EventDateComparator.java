package cgeo.geocaching.sorting;

/**
 * Compares caches by date. Used only for event caches.
 */
public class EventDateComparator extends DateComparator {

    final static public EventDateComparator singleton = new EventDateComparator();

}
