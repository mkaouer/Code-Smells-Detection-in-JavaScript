package cgeo.geocaching;

import static org.assertj.core.api.Assertions.assertThat;

import cgeo.geocaching.connector.gc.GCConstants;
import cgeo.geocaching.list.StoredList;

import org.apache.commons.lang3.StringUtils;

import junit.framework.TestCase;

public class PersonalNoteTest extends TestCase {

    public static void testParse() {
        final String testString = "Simple cgeo note\n--\nSimple provider note";
        Geocache cache = new Geocache();
        cache.setPersonalNote(testString);
        PersonalNote parsedNote = new PersonalNote(cache);
        assertThat(parsedNote.toString()).isEqualTo(testString);
        assertPersonalNote(parsedNote, "Simple cgeo note", "Simple provider note");

    }

    public static void testParseProviderOnly() {
        final String testString = "Simple provider note";
        Geocache cache = new Geocache();
        cache.setPersonalNote(testString);
        PersonalNote parsedNote = new PersonalNote(cache);
        assertThat(parsedNote.toString()).isEqualTo(testString);
        assertPersonalNote(parsedNote, null, "Simple provider note");
    }

    public static void testLocalNoteExceedsLimit() {
        String testString = StringUtils.repeat("x", GCConstants.PERSONAL_NOTE_MAX_CHARS + 1);
        Geocache truncCache = new Geocache();
        truncCache.setPersonalNote(testString.substring(0, GCConstants.PERSONAL_NOTE_MAX_CHARS));
        PersonalNote parsedNote = new PersonalNote(truncCache);

        Geocache exceedingCache = new Geocache();
        exceedingCache.setListId(StoredList.STANDARD_LIST_ID); // stored
        exceedingCache.setPersonalNote(testString);
        PersonalNote otherNote = new PersonalNote(exceedingCache);
        PersonalNote result = parsedNote.mergeWith(otherNote);
        assertPersonalNote(result, null, testString);
    }

    public static void testParseCgeoOnly() {
        final String testString = "Simple cgeo note";
        Geocache cache = new Geocache();
        cache.setPersonalNote(testString);
        PersonalNote parsedNote = new PersonalNote(cache);
        assertThat(parsedNote.toString()).isEqualTo("Simple cgeo note");
        assertPersonalNote(parsedNote, null, "Simple cgeo note");
    }

    public static void testSimpleMerge() {
        Geocache cache1 = new Geocache(); // not stored
        cache1.setPersonalNote("Simple cgeo note\n--\nSimple provider note");
        PersonalNote myNote = new PersonalNote(cache1);
        Geocache cache2 = new Geocache();
        cache2.setListId(StoredList.STANDARD_LIST_ID); // stored
        cache2.setPersonalNote("cgeo note\n--\nProvider note");
        PersonalNote otherNote = new PersonalNote(cache2);
        PersonalNote result = myNote.mergeWith(otherNote);
        assertThat(result.toString()).isEqualTo("cgeo note\n--\nSimple provider note");
        assertPersonalNote(result, "cgeo note", "Simple provider note");
    }

    public static void testMixedMerge() {
        Geocache cache1 = new Geocache(); // not stored
        cache1.setPersonalNote("Simple cgeo note\n--\nSimple provider note");
        PersonalNote myNote = new PersonalNote(cache1);
        Geocache cache2 = new Geocache();
        cache2.setListId(StoredList.STANDARD_LIST_ID); // stored
        cache2.setPersonalNote("Provider note");
        PersonalNote otherNote = new PersonalNote(cache2);
        PersonalNote result = myNote.mergeWith(otherNote);
        assertThat(result.toString()).isEqualTo("Simple cgeo note\n--\nSimple provider note");
        assertPersonalNote(result, "Simple cgeo note", "Simple provider note");
        result = otherNote.mergeWith(myNote);
        assertThat(result.toString()).isEqualTo("Simple cgeo note\n--\nProvider note");
        assertPersonalNote(result, "Simple cgeo note", "Provider note");
    }

    private static void assertPersonalNote(final PersonalNote note, final String cgeoNote, final String providerNote) {
        assertThat(note.getCgeoNote()).isEqualTo(cgeoNote);
        assertThat(note.getProviderNote()).isEqualTo(providerNote);
    }
}
