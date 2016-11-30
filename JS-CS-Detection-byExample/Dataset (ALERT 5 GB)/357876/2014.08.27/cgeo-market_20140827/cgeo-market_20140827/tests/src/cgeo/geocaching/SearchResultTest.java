package cgeo.geocaching;

import static org.assertj.core.api.Assertions.assertThat;

import android.os.Parcel;
import android.test.AndroidTestCase;

import java.util.HashSet;
import java.util.Set;

public class SearchResultTest extends AndroidTestCase {
    public static void testCreateFromGeocodes() {
        final HashSet<String> geocodes = new HashSet<String>();
        geocodes.add("GC12345");
        geocodes.add("GC23456");
        final SearchResult searchResult = new SearchResult(geocodes);
        assertThat(searchResult.getCount()).isEqualTo(2);
        assertThat(searchResult.getTotalCountGC()).isEqualTo(2);
        assertThat(searchResult.getGeocodes().contains("GC12345")).isTrue();
    }

    public static void testParcel() {
        final Set<String> geocodes = new HashSet<String>();
        geocodes.add("GC12345");
        geocodes.add("GC23456");
        geocodes.add("GC34567");
        final SearchResult search = new SearchResult(geocodes);
        geocodes.clear();
        geocodes.add("GC45678");
        geocodes.add("GC56789");
        search.addFilteredGeocodes(geocodes);

        Parcel parcel = Parcel.obtain();
        search.writeToParcel(parcel, 0);
        // reset to ready for reading
        parcel.setDataPosition(0);

        final SearchResult receive = new SearchResult(parcel);

        parcel.recycle();

        assertThat(receive.getCount()).isEqualTo(3);
        assertThat(receive.getFilteredGeocodes()).hasSize(2);

        assertThat(receive.getGeocodes().contains("GC12345")).isTrue();
        assertThat(receive.getGeocodes().contains("GC45678")).isFalse();

        assertThat(receive.getFilteredGeocodes().contains("GC12345")).isFalse();
        assertThat(receive.getFilteredGeocodes().contains("GC45678")).isTrue();
    }

    public static void testAddSearchResult() {
        final Set<String> geocodes = new HashSet<String>();
        geocodes.add("GC12345");
        geocodes.add("GC23456");
        geocodes.add("GC34567");
        final SearchResult search = new SearchResult(geocodes);
        geocodes.clear();
        geocodes.add("GC45678");
        geocodes.add("GC56789");
        search.addFilteredGeocodes(geocodes);

        final SearchResult newSearch = new SearchResult();
        newSearch.addGeocode("GC01234");
        newSearch.addSearchResult(search);

        assertThat(newSearch.getCount()).isEqualTo(4);
        assertThat(newSearch.getFilteredGeocodes()).hasSize(2);

        assertThat(newSearch.getGeocodes().contains("GC12345")).isTrue();
        assertThat(newSearch.getGeocodes().contains("GC01234")).isTrue();
        assertThat(newSearch.getGeocodes().contains("GC45678")).isFalse();

        assertThat(newSearch.getFilteredGeocodes().contains("GC12345")).isFalse();
        assertThat(newSearch.getFilteredGeocodes().contains("GC45678")).isTrue();
    }
}
