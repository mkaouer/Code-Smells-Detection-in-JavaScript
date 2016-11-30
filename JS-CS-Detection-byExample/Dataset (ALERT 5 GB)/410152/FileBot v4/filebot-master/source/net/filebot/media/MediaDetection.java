package net.filebot.media;

import static java.util.Arrays.*;
import static java.util.Collections.*;
import static java.util.regex.Pattern.*;
import static java.util.stream.Collectors.*;
import static net.filebot.Logging.*;
import static net.filebot.MediaTypes.*;
import static net.filebot.media.XattrMetaInfo.*;
import static net.filebot.similarity.CommonSequenceMatcher.*;
import static net.filebot.similarity.Normalization.*;
import static net.filebot.util.FileUtilities.*;
import static net.filebot.util.RegularExpressions.*;
import static net.filebot.util.StringUtilities.*;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.text.CollationKey;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.filebot.Resource;
import net.filebot.WebServices;
import net.filebot.archive.Archive;
import net.filebot.similarity.CommonSequenceMatcher;
import net.filebot.similarity.DateMatcher;
import net.filebot.similarity.EpisodeMetrics;
import net.filebot.similarity.MetricAvg;
import net.filebot.similarity.NameSimilarityMetric;
import net.filebot.similarity.NumericSimilarityMetric;
import net.filebot.similarity.SeasonEpisodeMatcher;
import net.filebot.similarity.SeasonEpisodeMatcher.SeasonEpisodePattern;
import net.filebot.similarity.SeasonEpisodeMatcher.SxE;
import net.filebot.similarity.SequenceMatchSimilarity;
import net.filebot.similarity.SeriesNameMatcher;
import net.filebot.similarity.SimilarityComparator;
import net.filebot.similarity.SimilarityMetric;
import net.filebot.similarity.StringEqualsMetric;
import net.filebot.vfs.FileInfo;
import net.filebot.web.Episode;
import net.filebot.web.Movie;
import net.filebot.web.MovieIdentificationService;
import net.filebot.web.SearchResult;
import net.filebot.web.SeriesInfo;
import net.filebot.web.SimpleDate;

public class MediaDetection {

	public static final ReleaseInfo releaseInfo = new ReleaseInfo();

	public static FileFilter getSystemFilesFilter() {
		return releaseInfo.getSystemFilesFilter();
	}

	public static FileFilter getDiskFolderFilter() {
		return releaseInfo.getDiskFolderFilter();
	}

	public static FileFilter getClutterFileFilter() {
		try {
			return releaseInfo.getClutterFileFilter();
		} catch (Exception e) {
			debug.log(Level.SEVERE, "Failed to load clutter file filter: " + e.getMessage(), e);
		}
		return f -> false;
	}

	public static boolean isDiskFolder(File folder) {
		return getDiskFolderFilter().accept(folder);
	}

	public static boolean isClutterFile(File file) throws IOException {
		return getClutterFileFilter().accept(file);
	}

	public static boolean isVideoDiskFile(File file) throws Exception {
		FileFilter diskFolderEntryFilter = releaseInfo.getDiskFolderEntryFilter();
		Archive iso = Archive.open(file);
		try {
			for (FileInfo it : iso.listFiles()) {
				for (File entry : listPath(it.toFile())) {
					if (diskFolderEntryFilter.accept(entry)) {
						return true;
					}
				}
			}
			return false;
		} finally {
			iso.close();
		}
	}

	public static Locale guessLanguageFromSuffix(File file) {
		return releaseInfo.getSubtitleLanguageTag(getName(file));
	}

	public static boolean isEpisode(File file, boolean strict) {
		Object metaInfo = xattr.getMetaInfo(file);
		if (metaInfo instanceof Episode) {
			return true;
		}

		// require a good S00E00 match
		return MediaDetection.isEpisode(String.join("/", file.getParent(), file.getName()), strict);
	}

	public static boolean isMovie(File file, boolean strict) {
		Object metaInfo = xattr.getMetaInfo(file);
		if (metaInfo != null) {
			return metaInfo instanceof Movie;
		}

		if (isEpisode(file, strict)) {
			return false;
		}

		if (matchMovieName(asList(file.getName(), file.getParent()), strict, 0).size() > 0) {
			return true;
		}

		// check for valid imdb id patterns
		return grepImdbId(file.getPath()).stream().map(Movie::new).filter(m -> {
			try {
				return strict ? WebServices.TheMovieDB.getMovieDescriptor(m, Locale.ENGLISH).getId() > 0 : true;
			} catch (Exception e) {
				return false;
			}
		}).filter(Objects::nonNull).findFirst().isPresent();
	}

	private static final SeasonEpisodeMatcher seasonEpisodeMatcherStrict = new SmartSeasonEpisodeMatcher(SeasonEpisodeMatcher.DEFAULT_SANITY, true);
	private static final SeasonEpisodeMatcher seasonEpisodeMatcherNonStrict = new SmartSeasonEpisodeMatcher(SeasonEpisodeMatcher.DEFAULT_SANITY, false);
	private static final DateMatcher dateMatcher = new DateMatcher(DateMatcher.DEFAULT_SANITY, Locale.ENGLISH, Locale.getDefault());

	public static SeasonEpisodeMatcher getSeasonEpisodeMatcher(boolean strict) {
		return strict ? seasonEpisodeMatcherStrict : seasonEpisodeMatcherNonStrict;
	}

	public static DateMatcher getDateMatcher() {
		return dateMatcher;
	}

	public static SeriesNameMatcher getSeriesNameMatcher(boolean strict) {
		return new SeriesNameMatcher(strict ? seasonEpisodeMatcherStrict : seasonEpisodeMatcherNonStrict, dateMatcher);
	}

	public static boolean isEpisode(String name, boolean strict) {
		return parseEpisodeNumber(name, strict) != null || parseDate(name) != null;
	}

	public static List<SxE> parseEpisodeNumber(String string, boolean strict) {
		return getSeasonEpisodeMatcher(strict).match(string);
	}

	public static List<SxE> parseEpisodeNumber(File file, boolean strict) {
		return getSeasonEpisodeMatcher(strict).match(file);
	}

	public static SimpleDate parseDate(Object object) {
		if (object instanceof File) {
			return getDateMatcher().match((File) object);
		}
		return getDateMatcher().match(object.toString());
	}

	public static Map<Set<File>, Set<String>> mapSeriesNamesByFiles(Collection<File> files, Locale locale, boolean anime) throws Exception {
		// map series names by folder
		Map<File, Set<String>> seriesNamesByFolder = new HashMap<File, Set<String>>();
		Map<File, List<File>> filesByFolder = mapByFolder(files);

		for (Entry<File, List<File>> it : filesByFolder.entrySet()) {
			Set<String> namesForFolder = new TreeSet<String>(getLenientCollator(locale));
			namesForFolder.addAll(detectSeriesNames(it.getValue(), anime, locale));

			seriesNamesByFolder.put(it.getKey(), namesForFolder);
		}

		// reverse map folders by series name
		Map<String, Set<File>> foldersBySeriesName = new HashMap<String, Set<File>>();

		for (Set<String> nameSet : seriesNamesByFolder.values()) {
			for (String name : nameSet) {
				Set<File> foldersForSeries = new HashSet<File>();
				for (Entry<File, Set<String>> it : seriesNamesByFolder.entrySet()) {
					if (it.getValue().contains(name)) {
						foldersForSeries.add(it.getKey());
					}
				}
				foldersBySeriesName.put(name, foldersForSeries);
			}
		}

		// join both sets
		Map<Set<File>, Set<String>> batchSets = new HashMap<Set<File>, Set<String>>();

		while (seriesNamesByFolder.size() > 0) {
			Set<String> combinedNameSet = new TreeSet<String>(getLenientCollator(locale));
			Set<File> combinedFolderSet = new HashSet<File>();

			// build combined match set
			combinedFolderSet.add(seriesNamesByFolder.keySet().iterator().next());

			boolean resolveFurther = true;
			while (resolveFurther) {
				boolean modified = false;
				for (File folder : combinedFolderSet) {
					modified |= combinedNameSet.addAll(seriesNamesByFolder.get(folder));
				}
				for (String name : combinedNameSet) {
					modified |= combinedFolderSet.addAll(foldersBySeriesName.get(name));
				}
				resolveFurther &= modified;
			}

			// build result entry
			Set<File> combinedFileSet = new TreeSet<File>();
			for (File folder : combinedFolderSet) {
				combinedFileSet.addAll(filesByFolder.get(folder));
			}

			if (combinedFileSet.size() > 0) {
				// divide file set per complete series set
				Map<Object, List<File>> filesByEpisode = new LinkedHashMap<Object, List<File>>();
				for (File file : combinedFileSet) {
					Object eid = getEpisodeIdentifier(file.getName(), true);

					// SPECIAL CASE: 101, 201, 202, etc 3-digit SxE pattern
					if (eid == null) {
						List<SxE> d3sxe = new SeasonEpisodePattern(null, "(?<!\\p{Alnum})(\\d)(\\d{2})(?!\\p{Alnum})").match(file.getName());
						if (d3sxe != null && d3sxe.size() > 0) {
							eid = d3sxe;
						}
					}

					// merge specials into first SxE group
					if (eid == null) {
						eid = file; // open new SxE group for each unrecognized file
					}

					List<File> episodeFiles = filesByEpisode.get(eid);
					if (episodeFiles == null) {
						episodeFiles = new ArrayList<File>();
						filesByEpisode.put(eid, episodeFiles);
					}
					episodeFiles.add(file);
				}

				for (int i = 0; true; i++) {
					Set<File> series = new LinkedHashSet<File>();
					for (List<File> episode : filesByEpisode.values()) {
						if (i < episode.size()) {
							series.add(episode.get(i));
						}
					}

					if (series.isEmpty()) {
						break;
					}

					combinedFileSet.removeAll(series);
					batchSets.put(series, combinedNameSet);
				}

				if (combinedFileSet.size() > 0) {
					batchSets.put(combinedFileSet, combinedNameSet);
				}
			}

			// set folders as accounted for
			seriesNamesByFolder.keySet().removeAll(combinedFolderSet);
		}

		// handle files that have not been matched to a batch set yet
		Set<File> remainingFiles = new HashSet<File>(files);
		for (Set<File> batch : batchSets.keySet()) {
			remainingFiles.removeAll(batch);
		}
		if (remainingFiles.size() > 0) {
			batchSets.put(remainingFiles, null);
		}

		return batchSets;
	}

	public static Object getEpisodeIdentifier(CharSequence name, boolean strict) {
		// check SxE first
		Object match = getSeasonEpisodeMatcher(true).match(name);

		// then Date pattern
		if (match == null) {
			match = getDateMatcher().match(name);
		}

		// check SxE non-strict
		if (match == null && !strict) {
			match = getSeasonEpisodeMatcher(false).match(name);
		}

		return match;
	}

	public static List<String> detectSeriesNames(Collection<File> files, boolean anime, Locale locale) throws Exception {
		return detectSeriesNames(files, anime ? getAnimeIndex() : getSeriesIndex(), locale);
	}

	public static List<String> detectSeriesNames(Collection<File> files, List<IndexEntry<SearchResult>> index, Locale locale) throws Exception {
		// known series names
		List<String> unids = new ArrayList<String>();

		// try xattr metadata if enabled
		for (File it : files) {
			Object metaObject = xattr.getMetaInfo(it);
			if (metaObject instanceof Episode) {
				unids.add(((Episode) metaObject).getSeriesName());
			}
		}

		// completely trust xattr metadata if all files are tagged
		if (unids.size() == files.size()) {
			return getUniqueQuerySet(unids);
		}

		// try to detect series name via nfo files
		try {
			for (SearchResult it : lookupSeriesNameByInfoFile(files, locale)) {
				unids.add(it.getName());
			}
		} catch (Exception e) {
			debug.warning("Failed to lookup info by id: " + e);
		}

		// try to detect series name via known patterns
		unids.addAll(matchSeriesMappings(files));

		// strict series name matcher for recognizing 1x01 patterns
		SeriesNameMatcher strictSeriesNameMatcher = getSeriesNameMatcher(true);

		// cross-reference known series names against file structure
		try {
			Set<String> folders = new LinkedHashSet<String>();
			Set<String> filenames = new LinkedHashSet<String>();
			for (File f : files) {
				for (int i = 0; i < 3 && f != null && !isStructureRoot(f); i++, f = f.getParentFile()) {
					String fn = getName(f);

					// try to minimize noise
					String sn = strictSeriesNameMatcher.matchByEpisodeIdentifier(fn);
					if (sn != null) {
						fn = sn;
					}

					(i == 0 ? filenames : folders).add(fn); // keep series name unique with year
				}
			}

			// check foldernames first
			List<String> matches = matchSeriesByName(folders, 0, index);

			// check all filenames if necessary
			if (matches.isEmpty()) {
				matches.addAll(matchSeriesByName(filenames, 0, index));
				matches.addAll(matchSeriesByName(stripReleaseInfo(filenames, false), 0, index));
			}

			// use lenient sub sequence matching only as fallback and try name without spacing logic that may mess up any lookup
			if (matches.isEmpty()) {
				// try to narrow down file to series name as best as possible
				List<String> sns = new ArrayList<String>();
				sns.addAll(folders);
				sns.addAll(filenames);
				for (int i = 0; i < sns.size(); i++) {
					String sn = strictSeriesNameMatcher.matchByEpisodeIdentifier(sns.get(i));
					if (sn != null) {
						sns.set(i, sn);
					}
				}
				for (SearchResult it : matchSeriesFromStringWithoutSpacing(stripReleaseInfo(sns, false), true, index)) {
					matches.add(it.getName());
				}

				matches.addAll(matchSeriesByName(folders, 2, index));
				matches.addAll(matchSeriesByName(filenames, 2, index));

				// pass along only valid terms
				unids.addAll(stripBlacklistedTerms(matches));
			} else {
				// trust terms matched by 0-stance
				unids.addAll(matches);
			}
		} catch (Exception e) {
			debug.warning("Failed to match folder structure: " + e);
		}

		// match common word sequence and clean detected word sequence from unwanted elements
		Set<String> queries = new LinkedHashSet<String>();

		// check for known pattern matches
		for (boolean strict : new boolean[] { true, false }) {
			if (queries.isEmpty()) {
				// check CWS matches
				SeriesNameMatcher seriesNameMatcher = getSeriesNameMatcher(strict);
				queries.addAll(strictSeriesNameMatcher.matchAll(files.toArray(new File[files.size()])));

				// try before SxE pattern
				if (queries.isEmpty()) {
					for (File f : files) {
						for (File path : listPathTail(f, 2, true)) {
							String fn = getName(path);
							// ignore non-strict series name parsing if there are movie year patterns
							if (!strict && parseMovieYear(fn).size() > 0) {
								break;
							}
							String sn = seriesNameMatcher.matchByEpisodeIdentifier(fn);
							if (sn != null && sn.length() > 0) {
								// try simplification by separator (for name - title naming style)
								if (!strict) {
									String sn2 = seriesNameMatcher.matchBySeparator(fn);
									if (sn2 != null && sn2.length() > 0) {
										if (sn2.length() < sn.length()) {
											sn = sn2;
										}
									}
								}
								queries.add(sn);
								break;
							}
						}
					}
				}
			}
		}

		debug.finest(format("Match Series Name => %s %s", unids, queries));

		List<String> querySet = getUniqueQuerySet(unids, queries);
		debug.finest(format("Query Series => %s", querySet));
		return querySet;
	}

	public static List<String> matchSeriesMappings(Collection<File> files) {
		try {
			Map<Pattern, String> patterns = releaseInfo.getSeriesMappings();
			List<String> matches = new ArrayList<String>();
			for (File file : files) {
				patterns.forEach((pattern, seriesName) -> {
					if (pattern.matcher(getName(file)).find()) {
						matches.add(seriesName);
					}
				});
			}
			return matches;
		} catch (Exception e) {
			debug.log(Level.SEVERE, "Failed to load series mappings: " + e.getMessage(), e);
		}
		return emptyList();
	}

	private static final ArrayList<IndexEntry<SearchResult>> seriesIndex = new ArrayList<IndexEntry<SearchResult>>();

	public static List<IndexEntry<SearchResult>> getSeriesIndex() throws IOException {
		return getIndex(() -> {
			try {
				return releaseInfo.getTheTVDBIndex();
			} catch (Exception e) {
				debug.severe("Failed to load series index: " + e.getMessage());
				return new SearchResult[0];
			}
		}, HighPerformanceMatcher::prepare, seriesIndex);
	}

	private static final ArrayList<IndexEntry<SearchResult>> animeIndex = new ArrayList<IndexEntry<SearchResult>>();

	public static List<IndexEntry<SearchResult>> getAnimeIndex() {
		return getIndex(() -> {
			try {
				return releaseInfo.getAnidbIndex();
			} catch (Exception e) {
				debug.severe("Failed to load anime index: " + e.getMessage());
				return new SearchResult[0];
			}
		}, HighPerformanceMatcher::prepare, animeIndex);
	}

	public static List<String> matchSeriesByName(Collection<String> files, int maxStartIndex, List<IndexEntry<SearchResult>> index) throws Exception {
		HighPerformanceMatcher nameMatcher = new HighPerformanceMatcher(maxStartIndex);
		List<String> matches = new ArrayList<String>();

		for (CollationKey[] name : HighPerformanceMatcher.prepare(files)) {
			IndexEntry<SearchResult> bestMatch = null;
			for (IndexEntry<SearchResult> it : index) {
				CollationKey[] commonName = nameMatcher.matchFirstCommonSequence(new CollationKey[][] { name, it.getLenientKey() });
				if (commonName != null && commonName.length >= it.getLenientKey().length && (bestMatch == null || commonName.length > bestMatch.getLenientKey().length)) {
					bestMatch = it;
				}
			}
			if (bestMatch != null) {
				matches.add(bestMatch.getLenientName());
			}
		}

		// sort by length of name match (descending)
		return matches.stream().sorted((a, b) -> {
			return Integer.compare(b.length(), a.length());
		}).collect(toList());
	}

	public static List<SearchResult> matchSeriesFromStringWithoutSpacing(Collection<String> names, boolean strict, List<IndexEntry<SearchResult>> index) throws IOException {
		// clear name of punctuation, spacing, and leading 'The' or 'A' that are common causes for word-lookup to fail
		Pattern spacing = Pattern.compile("(^(?i)(The|A)\\b)|[\\p{Punct}\\p{Space}]+");

		List<String> terms = new ArrayList<String>(names.size());
		for (String it : names) {
			String term = spacing.matcher(it).replaceAll("").toLowerCase();
			if (term.length() >= 3) {
				terms.add(term); // only consider words, not just random letters
			}
		}

		// similarity threshold based on strict/non-strict
		SimilarityMetric metric = new NameSimilarityMetric();
		float similarityThreshold = strict ? 0.75f : 0.5f;

		List<SearchResult> seriesList = new ArrayList<SearchResult>();
		for (IndexEntry<SearchResult> it : index) {
			String name = spacing.matcher(it.getLenientName()).replaceAll("").toLowerCase();
			for (String term : terms) {
				if (term.contains(name)) {
					if (metric.getSimilarity(term, name) >= similarityThreshold) {
						seriesList.add(it.object);
					}
					break;
				}
			}
		}
		return seriesList;
	}

	public static List<Movie> detectMovie(File movieFile, MovieIdentificationService service, Locale locale, boolean strict) throws Exception {
		List<Movie> options = new ArrayList<Movie>();

		// try xattr metadata if enabled
		Object metaObject = xattr.getMetaInfo(movieFile);
		if (metaObject instanceof Movie) {
			options.add((Movie) metaObject);
		}

		// lookup by id from nfo file
		if (service != null) {
			for (int imdbid : grepImdbId(movieFile.getPath())) {
				Movie movie = service.getMovieDescriptor(new Movie(imdbid), locale);
				if (movie != null) {
					options.add(movie);
				}
			}

			// try to grep imdb id from nfo files
			for (int imdbid : grepImdbIdFor(movieFile)) {
				Movie movie = service.getMovieDescriptor(new Movie(imdbid), locale);
				if (movie != null) {
					options.add(movie);
				}
			}
		}

		// search by file name or folder name (NOTE: can't initialize with known options because misleading NFO files may lead to bad matches)
		List<String> names = new ArrayList<String>(2);

		// 1. term: try to match movie pattern 'name (year)' or use filename as is
		names.add(getName(movieFile));

		// 2. term: first meaningful parent folder
		File movieFolder = guessMovieFolder(movieFile);
		if (movieFolder != null) {
			names.add(getName(movieFolder));
		}

		// reduce movie names
		Set<String> terms = reduceMovieNamePermutations(names);
		List<Movie> movieNameMatches = matchMovieName(terms, true, 0);

		// skip further queries if collected matches are already sufficient
		if (movieNameMatches.size() > 0) {
			options.addAll(movieNameMatches);
			return sortMoviesBySimilarity(options, terms);
		}

		if (movieNameMatches.isEmpty()) {
			movieNameMatches = matchMovieName(terms, strict, 2);
		}

		// skip further queries if collected matches are already sufficient
		if (options.size() > 0 && movieNameMatches.size() > 0) {
			options.addAll(movieNameMatches);
			return sortMoviesBySimilarity(options, terms);
		}

		// if matching name+year failed, try matching only by name (in non-strict mode we would have checked these cases already by now)
		if (movieNameMatches.isEmpty() && strict) {
			movieNameMatches = matchMovieName(terms, false, 0);
			if (movieNameMatches.isEmpty()) {
				movieNameMatches = matchMovieName(terms, false, 2);
			}
		}

		// assume name without spacing will mess up any lookup
		if (movieNameMatches.isEmpty()) {
			movieNameMatches = matchMovieFromStringWithoutSpacing(terms, strict);

			if (movieNameMatches.isEmpty() && !terms.equals(stripReleaseInfo(terms, true))) {
				movieNameMatches = matchMovieFromStringWithoutSpacing(stripReleaseInfo(terms, true), strict);
			}
		}

		// query by file / folder name
		if (service != null) {
			List<Movie> results = queryMovieByFileName(terms, service, locale);

			// try query without year as it sometimes messes up results if years don't match properly (movie release years vs dvd release year, etc)
			if (results.isEmpty() && !strict) {
				List<String> lastResortQueryList = new ArrayList<String>();
				Pattern yearPattern = Pattern.compile("(?:19|20)\\d{2}");
				Pattern akaPattern = Pattern.compile("\\bAKA\\b", Pattern.CASE_INSENSITIVE);
				for (String term : terms) {
					if (yearPattern.matcher(term).find() || akaPattern.matcher(term).find()) {
						// try to separate AKA titles as well into separate searches
						for (String mn : akaPattern.split(yearPattern.matcher(term).replaceAll(""))) {
							lastResortQueryList.add(mn.trim());
						}
					}
				}
				if (lastResortQueryList.size() > 0) {
					results = queryMovieByFileName(lastResortQueryList, service, locale);
				}
			}

			// online results have better ranking so add them first
			options.addAll(results);
		}

		// consider potential local index matches second
		options.addAll(movieNameMatches);

		// sort by relevance
		return sortMoviesBySimilarity(options, terms);
	}

	public static SimilarityMetric getMovieMatchMetric() {
		return new MetricAvg(new SequenceMatchSimilarity(), new NameSimilarityMetric(), new SequenceMatchSimilarity(0, true), new StringEqualsMetric() {

			@Override
			protected String normalize(Object object) {
				return super.normalize(removeTrailingBrackets(object.toString()));
			}
		}, new NumericSimilarityMetric() {

			private Pattern year = Pattern.compile("\\b\\d{4}\\b");

			@Override
			protected String normalize(Object object) {
				Matcher ym = year.matcher(object.toString());
				StringBuilder sb = new StringBuilder();
				while (ym.find()) {
					sb.append(ym.group()).append(' ');
				}
				return sb.toString().trim();
			}

			@Override
			public float getSimilarity(Object o1, Object o2) {
				return super.getSimilarity(o1, o2) * 2; // DOUBLE WEIGHT FOR YEAR MATCH
			}
		});
	}

	public static SimilarityMetric getSeriesMatchMetric() {
		return new MetricAvg(new SequenceMatchSimilarity(), new NameSimilarityMetric(), new SequenceMatchSimilarity(0, true));
	}

	public static <T extends SearchResult> List<T> sortBySimilarity(Collection<T> options, Collection<String> terms, SimilarityMetric metric) {
		return sortBySimilarity(options, terms, metric, SearchResult::getEffectiveNames);
	}

	public static <T extends SearchResult> List<T> sortBySimilarity(Collection<T> options, Collection<String> terms, SimilarityMetric metric, Function<SearchResult, Collection<String>> mapper) {
		// similarity comparator with multi-value support
		SimilarityComparator<SearchResult, String> comparator = new SimilarityComparator<SearchResult, String>(metric, terms, mapper);

		// sort by ranking and remove duplicate entries
		List<T> ranking = options.stream().sorted(comparator).distinct().collect(toList());

		// DEBUG
		debug.finest(format("Rank %s => %s", terms, ranking));

		// sort by ranking and remove duplicate entries
		return ranking;
	}

	public static List<Movie> sortMoviesBySimilarity(Collection<Movie> options, Collection<String> terms) throws Exception {
		Set<String> paragon = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		paragon.addAll(stripReleaseInfo(terms, true));
		paragon.addAll(stripReleaseInfo(terms, false));

		return sortBySimilarity(options, paragon, getMovieMatchMetric());
	}

	public static boolean isEpisodeNumberMatch(File f, Episode e) {
		float similarity = EpisodeMetrics.EpisodeIdentifier.getSimilarity(f, e);
		if (similarity >= 1) {
			return true;
		} else if (similarity >= 0.5 && e.getSeason() == null && e.getEpisode() != null && e.getSpecial() == null) {
			for (SxE it : parseEpisodeNumber(f, false)) {
				if (it.season < 0 && it.episode == e.getEpisode()) {
					return true;
				}
			}
		}
		return false;
	}

	public static List<Integer> parseMovieYear(String name) {
		return matchIntegers(name).stream().filter(DateMatcher.DEFAULT_SANITY::acceptYear).collect(toList());
	}

	public static String reduceMovieName(String name, boolean strict) throws IOException {
		Matcher matcher = compile(strict ? "^(.+)[\\[\\(]((?:19|20)\\d{2})[\\]\\)]" : "^(.+?)((?:19|20)\\d{2})").matcher(name);
		if (matcher.find() && parseMovieYear(matcher.group(2)).size() > 0) {
			return String.format("%s %s", trimTrailingPunctuation(matcher.group(1)), matcher.group(2));
		}
		return null;
	}

	public static Set<String> reduceMovieNamePermutations(Collection<String> terms) throws IOException {
		LinkedList<String> names = new LinkedList<String>();

		for (String it : terms) {
			String rn = reduceMovieName(it, true);
			if (rn != null) {
				names.addFirst(rn);
			} else {
				names.addLast(it); // unsure, keep original term just in case, but also try non-strict reduce
				rn = reduceMovieName(it, false);
				if (rn != null) {
					names.addLast(rn);
				}
			}
		}

		return new LinkedHashSet<String>(names);
	}

	public static File guessMovieFolder(File movieFile) throws Exception {
		// special case for folder mode
		if (movieFile.isDirectory()) {
			File f = movieFile;

			// check for double nested structures
			if (!isStructureRoot(f.getParentFile()) && checkMovie(f.getParentFile(), false) != null && checkMovie(f, false) == null) {
				return f.getParentFile();
			} else {
				return isStructureRoot(f) ? null : f;
			}
		}

		// first parent folder that matches a movie (max 3 levels deep)
		for (boolean strictness : new boolean[] { true, false }) {
			File f = movieFile.getParentFile();
			for (int i = 0; f != null && i < 3 && !isStructureRoot(f); f = f.getParentFile(), i++) {
				String term = stripReleaseInfo(f.getName());
				if (term.length() > 0 && checkMovie(f, strictness) != null) {
					return f;
				}
			}
		}

		// otherwise try the first potentially meaningful parent folder (max 2 levels deep)
		File f = movieFile.getParentFile();
		for (int i = 0; f != null && i < 2 && !isStructureRoot(f); f = f.getParentFile(), i++) {
			String term = stripReleaseInfo(f.getName());
			if (term.length() > 0) {
				// check for double nested structures
				if (checkMovie(f.getParentFile(), false) != null && checkMovie(f, false) == null) {
					return f.getParentFile();
				} else {
					return f;
				}
			}
		}

		if (movieFile.getParentFile() != null && !isStructureRoot(f.getParentFile()) && stripReleaseInfo(movieFile.getParentFile().getName()).length() > 0) {
			return movieFile.getParentFile();
		}
		return null;
	}

	public static Movie checkMovie(File file, boolean strict) {
		List<Movie> matches = file == null ? null : matchMovieName(singleton(file.getName()), strict, 4);
		return matches == null || matches.isEmpty() ? null : matches.get(0);
	}

	private static final ArrayList<IndexEntry<Movie>> movieIndex = new ArrayList<IndexEntry<Movie>>();

	private static <T extends SearchResult> List<IndexEntry<T>> getIndex(Supplier<T[]> function, Function<T, List<IndexEntry<T>>> mapper, ArrayList<IndexEntry<T>> sink) {
		synchronized (sink) {
			if (sink.isEmpty()) {
				T[] index = function.get();
				sink.ensureCapacity(index.length * 4); // alias names
				stream(index).map(mapper).forEach(sink::addAll);
			}
			return sink;
		}
	}

	public static List<IndexEntry<Movie>> getMovieIndex() {
		return getIndex(() -> {
			try {
				return releaseInfo.getMovieList();
			} catch (Exception e) {
				debug.severe("Failed to load movie index: " + e.getMessage());
				return new Movie[0];
			}
		}, HighPerformanceMatcher::prepare, movieIndex);
	}

	public static List<Movie> matchMovieName(Collection<String> files, boolean strict, int maxStartIndex) {
		// cross-reference file / folder name with movie list
		final HighPerformanceMatcher nameMatcher = new HighPerformanceMatcher(maxStartIndex);
		final Map<Movie, String> matchMap = new HashMap<Movie, String>();

		List<CollationKey[]> names = HighPerformanceMatcher.prepare(files);

		for (IndexEntry<Movie> movie : getMovieIndex()) {
			for (CollationKey[] name : names) {
				CollationKey[] commonName = nameMatcher.matchFirstCommonSequence(new CollationKey[][] { name, movie.getLenientKey() });
				if (commonName != null && commonName.length >= movie.getLenientKey().length) {
					CollationKey[] strictCommonName = nameMatcher.matchFirstCommonSequence(new CollationKey[][] { name, movie.getStrictKey() });
					if (strictCommonName != null && strictCommonName.length >= movie.getStrictKey().length) {
						// prefer strict match
						matchMap.put(movie.getObject(), movie.getStrictName());
					} else if (!strict) {
						// make sure the common identifier is not just the year
						matchMap.put(movie.getObject(), movie.getLenientName());
					}
				}
			}
		}

		// sort by length of name match (descending)
		return matchMap.keySet().stream().sorted((a, b) -> {
			return Integer.compare(matchMap.get(b).length(), matchMap.get(a).length());
		}).collect(toList());
	}

	public static List<Movie> matchMovieFromStringWithoutSpacing(Collection<String> names, boolean strict) {
		// clear name of punctuation, spacing, and leading 'The' or 'A' that are common causes for word-lookup to fail
		Pattern spacing = Pattern.compile("(^(?i)(The|A)\\b)|[\\p{Punct}\\p{Space}]+");

		List<String> terms = new ArrayList<String>(names.size());
		for (String it : names) {
			String term = spacing.matcher(it).replaceAll("").toLowerCase();
			if (term.length() >= 3) {
				terms.add(term); // only consider words, not just random letters
			}
		}

		// similarity threshold based on strict/non-strict
		SimilarityMetric metric = new NameSimilarityMetric();
		float similarityThreshold = strict ? 0.9f : 0.5f;

		LinkedList<Movie> movies = new LinkedList<Movie>();
		for (IndexEntry<Movie> it : getMovieIndex()) {
			String name = spacing.matcher(it.getLenientName()).replaceAll("").toLowerCase();
			for (String term : terms) {
				if (term.contains(name)) {
					String year = String.valueOf(it.getObject().getYear());
					if (term.contains(year) && metric.getSimilarity(term, name + year) > similarityThreshold) {
						movies.addFirst(it.getObject());
					} else if (metric.getSimilarity(term, name) > similarityThreshold) {
						movies.addLast(it.getObject());
					}
					break;
				}
			}
		}
		return new ArrayList<Movie>(movies);
	}

	private static List<Movie> queryMovieByFileName(Collection<String> files, MovieIdentificationService queryLookupService, Locale locale) throws Exception {
		// remove blacklisted terms and remove duplicates
		List<String> querySet = getUniqueQuerySet(emptySet(), files);

		// DEBUG
		debug.finest(format("Query Movie => %s", querySet));

		final Map<Movie, Float> probabilityMap = new LinkedHashMap<Movie, Float>();
		final SimilarityMetric metric = getMovieMatchMetric();
		for (String query : querySet) {
			for (Movie movie : queryLookupService.searchMovie(query.toLowerCase(), locale)) {
				probabilityMap.put(movie, metric.getSimilarity(query, movie));
			}
		}

		// sort by similarity to original query (descending)
		List<Movie> results = new ArrayList<Movie>(probabilityMap.keySet());
		results.sort((a, b) -> {
			return probabilityMap.get(b).compareTo(probabilityMap.get(a));
		});

		return results;
	}

	private static List<String> getUniqueQuerySet(Collection<String> exactMatches, Collection<String>... guessMatches) {
		Map<String, String> unique = new LinkedHashMap<String, String>();

		// unique key function (case-insensitive ignore-punctuation)
		Function<String, String> normalize = (s) -> normalizePunctuation(s).toLowerCase();
		addUniqueQuerySet(exactMatches, normalize, Function.identity(), unique);

		// remove blacklisted terms and remove duplicates
		List<String> extra = stream(guessMatches).flatMap(Collection::stream).filter(t -> {
			return !unique.containsKey(normalize.apply(t));
		}).collect(toList());

		Set<String> terms = new LinkedHashSet<String>();
		terms.addAll(stripReleaseInfo(extra, true));
		terms.addAll(stripReleaseInfo(extra, false));
		addUniqueQuerySet(stripBlacklistedTerms(terms), normalize, normalize, unique);

		return new ArrayList<String>(unique.values());
	}

	private static void addUniqueQuerySet(Collection<String> terms, Function<String, String> keyFunction, Function<String, String> valueFunction, Map<String, String> uniqueMap) {
		for (String it : terms) {
			String key = keyFunction.apply(it);
			if (key.length() > 0 && !uniqueMap.containsKey(key)) {
				uniqueMap.put(key, valueFunction.apply(it));
			}
		}
	}

	public static List<Movie> matchMovieByWordSequence(String name, Collection<Movie> options, int maxStartIndex) {
		List<Movie> movies = new ArrayList<Movie>();

		HighPerformanceMatcher nameMatcher = new HighPerformanceMatcher(maxStartIndex);
		CollationKey[] nameSeq = HighPerformanceMatcher.prepare(normalizePunctuation(name));

		for (Movie movie : options) {
			for (String alias : movie.getEffectiveNames()) {
				CollationKey[] movieSeq = HighPerformanceMatcher.prepare(normalizePunctuation(alias));
				CollationKey[] commonSeq = nameMatcher.matchFirstCommonSequence(new CollationKey[][] { nameSeq, movieSeq });

				if (commonSeq != null && commonSeq.length >= movieSeq.length) {
					movies.add(movie);
					break;
				}
			}
		}

		return movies;
	}

	private static Pattern formatInfoPattern = releaseInfo.getVideoFormatPattern(true);

	public static String stripFormatInfo(CharSequence name) {
		return formatInfoPattern.matcher(name).replaceAll("");
	}

	public static boolean isVolumeRoot(File folder) {
		return folder == null || folder.getName() == null || folder.getName().isEmpty() || releaseInfo.getVolumeRoots().contains(folder);
	}

	public static boolean isStructureRoot(File folder) throws Exception {
		return isVolumeRoot(folder) || releaseInfo.getStructureRootPattern().matcher(folder.getName()).matches();
	}

	public static File getStructureRoot(File file) throws Exception {
		boolean structureRoot = false;
		for (File it : listPathTail(file, Integer.MAX_VALUE, true)) {
			if (structureRoot || isStructureRoot(it)) {
				if (it.isDirectory()) {
					return it;
				}
				structureRoot = true; // find first existing folder at or after the structure root folder (which may not exist yet)
			}
		}
		return null;
	}

	public static List<String> listStructurePathTail(File file) throws Exception {
		LinkedList<String> relativePath = new LinkedList<String>();
		for (File it : listPathTail(file, FILE_WALK_MAX_DEPTH, true)) {
			if (isStructureRoot(it))
				break;

			// iterate path in reverse
			relativePath.addFirst(it.getName());
		}
		return relativePath;
	}

	public static File getStructurePathTail(File file) throws Exception {
		List<String> relativePath = listStructurePathTail(file);
		return relativePath.isEmpty() ? null : new File(String.join(File.separator, relativePath));
	}

	public static Map<File, List<File>> mapByMediaFolder(Collection<File> files) {
		Map<File, List<File>> mediaFolders = new HashMap<File, List<File>>();
		for (File f : files) {
			File folder = guessMediaFolder(f);
			List<File> value = mediaFolders.get(folder);
			if (value == null) {
				value = new ArrayList<File>();
				mediaFolders.put(folder, value);
			}
			value.add(f);
		}
		return mediaFolders;
	}

	public static Map<String, List<File>> mapByMediaExtension(Iterable<File> files) {
		Map<String, List<File>> map = new LinkedHashMap<String, List<File>>();

		for (File file : files) {
			String key = getExtension(file);

			// allow extended extensions for subtitles files, for example name.eng.srt => map by en.srt
			if (key != null && SUBTITLE_FILES.accept(file)) {
				Locale locale = releaseInfo.getSubtitleLanguageTag(getName(file));
				if (locale != null) {
					key = locale.getLanguage() + '.' + key;
				}
			}

			// normalize to lower-case
			if (key != null) {
				key = key.toLowerCase();
			}

			List<File> valueList = map.get(key);
			if (valueList == null) {
				valueList = new ArrayList<File>();
				map.put(key, valueList);
			}

			valueList.add(file);
		}

		return map;
	}

	public static Map<String, List<File>> mapBySeriesName(Collection<File> files, boolean anime, Locale locale) throws Exception {
		Map<String, List<File>> result = new TreeMap<String, List<File>>(String.CASE_INSENSITIVE_ORDER);

		for (File f : files) {
			List<String> names = detectSeriesNames(singleton(f), anime, locale);
			String key = names.isEmpty() ? "" : names.get(0);

			List<File> value = result.get(key);
			if (value == null) {
				value = new ArrayList<File>();
				result.put(key, value);
			}
			value.add(f);
		}

		return result;
	}

	public static Movie matchMovie(File file, int depth) {
		List<String> names = new ArrayList<String>(depth);
		for (File it : listPathTail(file, depth, true)) {
			names.add(it.getName());
		}

		List<Movie> matches = matchMovieName(names, true, 0);
		return matches.size() > 0 ? matches.get(0) : null;
	}

	public static File guessMediaFolder(File file) {
		List<File> tail = listPathTail(file, 3, true);

		// skip file itself (first entry)
		for (int i = 1; i < tail.size(); i++) {
			File folder = tail.get(i);
			String term = stripReleaseInfo(folder.getName());
			if (term.length() > 0) {
				return folder;
			}
		}

		// simply default to parent folder
		return file.getParentFile();
	}

	public static List<String> stripReleaseInfo(Collection<String> names, boolean strict) {
		try {
			return releaseInfo.cleanRelease(names, strict);
		} catch (Exception e) {
			debug.log(Level.SEVERE, "Failed to strip release info: " + e.getMessage(), e);
		}
		return new ArrayList<String>(names);
	}

	public static String stripReleaseInfo(String name, boolean strict) {
		Iterator<String> value = stripReleaseInfo(singleton(name), strict).iterator();
		if (value.hasNext()) {
			return value.next();
		}
		return ""; // default value in case all tokens are stripped away
	}

	public static String stripReleaseInfo(String name) {
		return stripReleaseInfo(name, true);
	}

	private static final Resource<Pattern> blacklistPattern = Resource.lazy(releaseInfo::getBlacklistPattern);

	public static List<String> stripBlacklistedTerms(Collection<String> names) {
		try {
			Pattern pattern = blacklistPattern.get();
			return names.stream().filter(s -> pattern.matcher(s).replaceAll("").trim().length() > 0).collect(toList());
		} catch (Exception e) {
			debug.log(Level.SEVERE, "Failed to strip release info: " + e.getMessage(), e);
		}
		return emptyList();
	}

	public static Set<Integer> grepImdbIdFor(File file) throws Exception {
		Set<Integer> collection = new LinkedHashSet<Integer>();
		List<File> nfoFiles = new ArrayList<File>();
		if (file.isDirectory()) {
			nfoFiles.addAll(filter(listFiles(file), NFO_FILES));
		} else if (file.getParentFile() != null && file.getParentFile().isDirectory()) {
			nfoFiles.addAll(getChildren(file.getParentFile(), NFO_FILES));
		}

		// parse ids from nfo files
		for (File nfo : nfoFiles) {
			try {
				String text = new String(readFile(nfo), "UTF-8");
				collection.addAll(grepImdbId(text));
			} catch (Exception e) {
				debug.warning("Failed to read nfo: " + e.getMessage());
			}
		}

		return collection;
	}

	public static Set<SearchResult> lookupSeriesNameByInfoFile(Collection<File> files, Locale language) throws Exception {
		Set<SearchResult> names = new LinkedHashSet<SearchResult>();

		SortedSet<File> folders = new TreeSet<File>(reverseOrder());
		for (File f : files) {
			for (int i = 0; i < 2 && f.getParentFile() != null; i++) {
				f = f.getParentFile();
				folders.add(f);
			}
		}

		// search for id in sibling nfo files
		for (File folder : folders) {
			if (!folder.exists())
				continue;

			for (File nfo : getChildren(folder, NFO_FILES)) {
				String text = new String(readFile(nfo), "UTF-8");

				for (int imdbid : grepImdbId(text)) {
					SearchResult series = WebServices.TheTVDB.lookupByIMDbID(imdbid, language);
					if (series != null) {
						names.add(series);
					}
				}

				for (int tvdbid : grepTheTvdbId(text)) {
					SearchResult series = WebServices.TheTVDB.lookupByID(tvdbid, language);
					if (series != null) {
						names.add(series);
					}
				}
			}
		}

		return names;
	}

	public static Set<Integer> grepImdbId(CharSequence text) {
		// scan for imdb id patterns like tt1234567
		Matcher imdbMatch = Pattern.compile("(?<!\\p{Alnum})tt(\\d{7})(?!\\p{Alnum})", Pattern.CASE_INSENSITIVE).matcher(text);
		Set<Integer> collection = new LinkedHashSet<Integer>();

		while (imdbMatch.find()) {
			collection.add(Integer.parseInt(imdbMatch.group(1)));
		}

		return collection;
	}

	public static Set<Integer> grepTheTvdbId(CharSequence text) {
		// scan for thetvdb id patterns like http://www.thetvdb.com/?tab=series&id=78874&lid=14
		Set<Integer> collection = new LinkedHashSet<Integer>();
		for (String token : Pattern.compile("[\\s\"<>|]+").split(text)) {
			try {
				URL url = new URL(token);
				if (url.getHost().contains("thetvdb") && url.getQuery() != null && url.getQuery().contains("tab=series")) {
					Matcher m = Pattern.compile("\\Wid=(\\d+)").matcher(url.getQuery());
					while (m.find()) {
						collection.add(Integer.parseInt(m.group(1)));
					}
				}
			} catch (Exception e) {
				debug.finest(e::toString);
			}
		}
		return collection;
	}

	public static Movie grepMovie(File nfo, MovieIdentificationService resolver, Locale locale) throws Exception {
		String contents = new String(readFile(nfo), "UTF-8");
		int imdbid = grepImdbId(contents).iterator().next();
		return resolver.getMovieDescriptor(new Movie(imdbid), locale);
	}

	public static SeriesInfo grepSeries(File nfo, Locale locale) throws Exception {
		String contents = new String(readFile(nfo), "UTF-8");
		int thetvdbid = grepTheTvdbId(contents).iterator().next();
		return WebServices.TheTVDB.getSeriesInfo(thetvdbid, locale);
	}

	public static List<SearchResult> getProbableMatches(String query, Collection<? extends SearchResult> options, boolean alias, boolean strict) {
		if (query == null) {
			return options.stream().distinct().collect(toList());
		}

		// check all alias names, or just the primary name
		Function<SearchResult, Collection<String>> names = alias ? SearchResult::getEffectiveNames : (it) -> singleton(it.getName());

		// auto-select most probable search result
		List<SearchResult> probableMatches = new ArrayList<SearchResult>();

		// use name similarity metric
		SimilarityMetric metric = new NameSimilarityMetric();
		float threshold = strict && options.size() > 1 ? 0.8f : 0.6f;
		float sanity = strict && options.size() > 1 ? 0.5f : 0.2f;

		// remove trailing braces, e.g. Doctor Who (2005) -> doctor who
		String q = removeTrailingBrackets(query).toLowerCase();

		// find probable matches using name similarity > 0.8 (or > 0.6 in non-strict mode)
		for (SearchResult option : options) {
			float f = 0;
			for (String n : names.apply(option)) {
				n = removeTrailingBrackets(n).toLowerCase();
				f = Math.max(f, metric.getSimilarity(q, n));

				// boost matching beginnings
				if (f >= sanity && n.startsWith(q)) {
					f = 1;
					break;
				}
			}

			if (f >= threshold) {
				probableMatches.add(option);
			}
		}

		return sortBySimilarity(probableMatches, singleton(query), new NameSimilarityMetric(), names);
	}

	public static class IndexEntry<T> implements Serializable {

		private T object;
		private String lenientName;
		private String strictName;

		private transient CollationKey[] lenientKey;
		private transient CollationKey[] strictKey;

		public IndexEntry(T object, String lenientName, String strictName) {
			this.object = object;
			this.lenientName = lenientName;
			this.strictName = strictName;
		}

		public T getObject() {
			return object;
		}

		public String getLenientName() {
			return lenientName;
		}

		public String getStrictName() {
			return strictName;
		}

		public CollationKey[] getLenientKey() {
			if (lenientKey == null && lenientName != null) {
				lenientKey = HighPerformanceMatcher.prepare(lenientName);
			}
			return lenientKey;
		}

		public CollationKey[] getStrictKey() {
			if (strictKey == null && strictName != null) {
				strictKey = HighPerformanceMatcher.prepare(strictName);
			}
			return strictKey;
		}

		@Override
		public String toString() {
			return strictName != null ? strictName : lenientName;
		}
	}

	/*
	 * Heavy-duty name matcher used for matching a file to or more movies (out of a list of ~50k)
	 */
	private static class HighPerformanceMatcher extends CommonSequenceMatcher {

		private static final Collator collator = getLenientCollator(Locale.ENGLISH);

		public static CollationKey[] prepare(String sequence) {
			String[] words = SPACE.split(sequence);
			CollationKey[] keys = new CollationKey[words.length];
			for (int i = 0; i < words.length; i++) {
				keys[i] = collator.getCollationKey(words[i]);
			}
			return keys;
		}

		public static List<CollationKey[]> prepare(Collection<String> sequences) {
			return sequences.stream().filter(Objects::nonNull).map(s -> {
				return prepare(normalizePunctuation(s));
			}).collect(toList());
		}

		public static List<IndexEntry<Movie>> prepare(Movie m) {
			List<String> effectiveNamesWithoutYear = m.getEffectiveNamesWithoutYear();
			List<String> effectiveNames = m.getEffectiveNames();
			List<IndexEntry<Movie>> index = new ArrayList<IndexEntry<Movie>>(effectiveNames.size());

			for (int i = 0; i < effectiveNames.size(); i++) {
				String lenientName = normalizePunctuation(effectiveNamesWithoutYear.get(i));
				String strictName = normalizePunctuation(effectiveNames.get(i));
				index.add(new IndexEntry<Movie>(m, lenientName, strictName));
			}
			return index;
		}

		public static List<IndexEntry<SearchResult>> prepare(SearchResult r) {
			List<String> effectiveNames = r.getEffectiveNames();
			List<IndexEntry<SearchResult>> index = new ArrayList<IndexEntry<SearchResult>>(effectiveNames.size());

			for (int i = 0; i < effectiveNames.size(); i++) {
				String lenientName = normalizePunctuation(effectiveNames.get(i));
				index.add(new IndexEntry<SearchResult>(r, lenientName, null));
			}
			return index;
		}

		public HighPerformanceMatcher(int maxStartIndex) {
			super(collator, maxStartIndex, true);
		}

		@Override
		public CollationKey[] split(String sequence) {
			throw new UnsupportedOperationException("requires ahead-of-time collation");
		}
	}

	public static void warmupCachedResources() throws Exception {
		// load filter data
		MediaDetection.getClutterFileFilter();
		MediaDetection.getDiskFolderFilter();
		MediaDetection.matchSeriesMappings(emptyList());

		// load movie/series index
		MediaDetection.stripReleaseInfo(singleton(""), true);
		MediaDetection.matchSeriesByName(singleton(""), -1, MediaDetection.getSeriesIndex());
		MediaDetection.matchSeriesByName(singleton(""), -1, MediaDetection.getAnimeIndex());
		MediaDetection.matchMovieName(singleton(""), true, -1);
	}

}
