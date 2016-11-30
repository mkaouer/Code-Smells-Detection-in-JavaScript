package net.filebot.web;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Locale;

import org.junit.Test;

public class TheTVDBClientTest {

	static TheTVDBClient db = new TheTVDBClient("BA864DEE427E384A");

	SearchResult buffy = new SearchResult(70327, "Buffy the Vampire Slayer");
	SearchResult wonderfalls = new SearchResult(78845, "Wonderfalls");
	SearchResult firefly = new SearchResult(78874, "Firefly");

	@Test
	public void search() throws Exception {
		// test default language and query escaping (blanks)
		List<SearchResult> results = db.search("babylon 5", Locale.ENGLISH);

		assertEquals(2, results.size());

		SearchResult first = results.get(0);

		assertEquals("Babylon 5", first.getName());
		assertEquals(70726, first.getId());
	}

	@Test
	public void searchGerman() throws Exception {
		List<SearchResult> results = db.search("Buffy the Vampire Slayer", Locale.GERMAN);

		assertEquals(2, results.size());

		SearchResult first = results.get(0);

		assertEquals("Buffy the Vampire Slayer", first.getName());
		assertEquals(70327, first.getId());
	}

	@Test
	public void getEpisodeListAll() throws Exception {
		List<Episode> list = db.getEpisodeList(buffy, SortOrder.Airdate, Locale.ENGLISH);

		assertEquals(145, list.size());

		// check ordinary episode
		Episode first = list.get(0);
		assertEquals("Buffy the Vampire Slayer", first.getSeriesName());
		assertEquals("1997-03-10", first.getSeriesInfo().getStartDate().toString());
		assertEquals("Welcome to the Hellmouth (1)", first.getTitle());
		assertEquals("1", first.getEpisode().toString());
		assertEquals("1", first.getSeason().toString());
		assertEquals("1", first.getAbsolute().toString());
		assertEquals("1997-03-10", first.getAirdate().toString());

		// check special episode
		Episode last = list.get(list.size() - 1);
		assertEquals("Buffy the Vampire Slayer", last.getSeriesName());
		assertEquals("Unaired Pilot", last.getTitle());
		assertEquals(null, last.getSeason());
		assertEquals(null, last.getEpisode());
		assertEquals(null, last.getAbsolute());
		assertEquals("1", last.getSpecial().toString());
		assertEquals(null, last.getAirdate());
	}

	@Test
	public void getEpisodeListSingleSeason() throws Exception {
		List<Episode> list = db.getEpisodeList(wonderfalls, SortOrder.Airdate, Locale.ENGLISH);

		Episode first = list.get(0);

		assertEquals("Wonderfalls", first.getSeriesName());
		assertEquals("2004-03-12", first.getSeriesInfo().getStartDate().toString());
		assertEquals("Wax Lion", first.getTitle());
		assertEquals("1", first.getEpisode().toString());
		assertEquals("1", first.getSeason().toString());
		assertEquals(null, first.getAbsolute()); // should be "1" but data has not yet been entered
		assertEquals("2004-03-12", first.getAirdate().toString());
	}

	@Test
	public void getEpisodeListNumbering() throws Exception {
		List<Episode> list = db.getEpisodeList(firefly, SortOrder.DVD, Locale.ENGLISH);

		Episode first = list.get(0);
		assertEquals("Firefly", first.getSeriesName());
		assertEquals("2002-09-20", first.getSeriesInfo().getStartDate().toString());
		assertEquals("Serenity", first.getTitle());
		assertEquals("1", first.getEpisode().toString());
		assertEquals("1", first.getSeason().toString());
		assertEquals("1", first.getAbsolute().toString());
		assertEquals("2002-12-20", first.getAirdate().toString());
	}

	public void getEpisodeListLink() {
		assertEquals("http://www.thetvdb.com/?tab=seasonall&id=78874", db.getEpisodeListLink(firefly).toString());
	}

	@Test
	public void lookupByID() throws Exception {
		SearchResult series = db.lookupByID(78874, Locale.ENGLISH);
		assertEquals("Firefly", series.getName());
		assertEquals(78874, series.getId());
	}

	@Test
	public void lookupByIMDbID() throws Exception {
		SearchResult series = db.lookupByIMDbID(303461, Locale.ENGLISH);
		assertEquals("Firefly", series.getName());
		assertEquals(78874, series.getId());
	}

	@Test
	public void getSeriesInfo() throws Exception {
		TheTVDBSeriesInfo it = db.getSeriesInfo(80348, Locale.ENGLISH);

		assertEquals(80348, it.getId(), 0);
		assertEquals("Action", it.getGenres().get(0));
		assertEquals("en", it.getLanguage());
		assertEquals("45", it.getRuntime().toString());
		assertEquals("Chuck", it.getName());
		assertEquals(9.0, it.getRating(), 0.5);
		assertEquals(1000, it.getRatingCount(), 100);
		assertEquals("tt0934814", it.getImdbId());
		assertEquals("Friday", it.getAirsDayOfWeek());
		assertEquals("8:00 PM", it.getAirsTime());
		assertEquals(1000, it.getOverview().length(), 100);
		assertEquals("http://thetvdb.com/banners/graphical/80348-g26.jpg", it.getBannerUrl().toString());
	}

	@Test
	public void getArtwork() throws Exception {
		Artwork i = db.getArtwork(buffy.getId(), "fanart", Locale.ENGLISH).get(0);

		assertEquals("[fanart, 1920x1080]", i.getTags().toString());
		assertEquals("http://thetvdb.com/banners/fanart/original/70327-7.jpg", i.getUrl().toString());
		assertTrue(i.matches("fanart", "1920x1080"));
		assertFalse(i.matches("fanart", "1920x1080", "1"));
		assertEquals(8.0, i.getRating(), 1.0);
	}

	@Test
	public void getLanguages() throws Exception {
		List<String> languages = db.getLanguages();
		assertEquals("[zh, en, sv, no, da, fi, nl, de, it, es, fr, pl, hu, el, tr, ru, he, ja, pt, cs, sl, hr, ko]", languages.toString());
	}

	@Test
	public void getActors() throws Exception {
		Person p = db.getActors(firefly.getId(), Locale.ENGLISH).get(0);
		assertEquals("Alan Tudyk", p.getName());
		assertEquals("Hoban 'Wash' Washburne", p.getCharacter());
		assertEquals(null, p.getJob());
		assertEquals(null, p.getDepartment());
		assertEquals("0", p.getOrder().toString());
		assertEquals("http://thetvdb.com/banners/actors/68409.jpg", p.getImage().toString());
	}

}
