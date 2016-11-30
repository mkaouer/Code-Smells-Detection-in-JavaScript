package cgeo.geocaching.test;

import cgeo.geocaching.utils.Log;

import android.test.AndroidTestCase;

import java.util.List;

/**
 * Test class to compare the performance of two regular expressions on given data.
 * Can be used to improve the time needed to parse the cache data.
 */
public class RegExRealPerformanceTest extends AndroidTestCase {

    public static void testRegEx() {

        final List<String> output = RegExPerformanceTest.doTheTests(10);

        for (String s : output) {
            Log.d(s);
        }

    }
}
