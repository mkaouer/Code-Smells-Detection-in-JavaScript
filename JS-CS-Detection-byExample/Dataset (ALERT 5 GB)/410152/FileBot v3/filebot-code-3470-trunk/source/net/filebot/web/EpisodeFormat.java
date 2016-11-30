package net.filebot.web;

import static net.filebot.similarity.Normalization.*;
import static net.filebot.util.StringUtilities.*;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class EpisodeFormat extends Format {

	public static final EpisodeFormat SeasonEpisode = new EpisodeFormat();

	@Override
	public StringBuffer format(Object obj, StringBuffer sb, FieldPosition pos) {
		if (obj instanceof MultiEpisode) {
			return sb.append(formatMultiEpisode(((MultiEpisode) obj).getEpisodes()));
		}

		// format episode object, e.g. Dark Angel - 3x01 - Labyrinth [2009-06-01]
		Episode episode = (Episode) obj;

		// episode number is most likely a number but could also be some kind of special identifier (e.g. Special)
		String episodeNumber = episode.getEpisode() != null ? String.format("%02d", episode.getEpisode()) : null;

		// series name should not be empty or null
		sb.append(episode.getSeriesName());

		if (episode.getSeason() != null) {
			// season and episode
			sb.append(" - ").append(episode.getSeason()).append('x');

			if (episode.getEpisode() != null) {
				sb.append(String.format("%02d", episode.getEpisode()));
			} else if (episode.getSpecial() != null) {
				sb.append("Special " + episode.getSpecial());
			}
		} else {
			// episode, but no season
			if (episode.getEpisode() != null) {
				sb.append(" - ").append(episodeNumber);
			} else if (episode.getSpecial() != null) {
				sb.append(" - ").append("Special " + episode.getSpecial());
			}
		}
		sb.append(" - ").append(episode.getTitle());
		return sb;
	}

	public String formatMultiEpisode(List<Episode> episodes) {
		Set<String> name = new LinkedHashSet<String>(2);
		Set<String> sxe = new LinkedHashSet<String>(2);
		Set<String> title = new LinkedHashSet<String>(2);
		for (Episode it : episodes) {
			name.add(it.getSeriesName());
			sxe.add(formatSxE(it));
			title.add(removeTrailingBrackets(it.getTitle()));
		}
		return String.format("%s - %s - %s", join(name, " & "), join(" & ", sxe), join(" & ", title));
	}

	public String formatSxE(Episode episode) {
		if (episode instanceof MultiEpisode) {
			return formatMultiRangeSxE(((MultiEpisode) episode).getEpisodes());
		}

		StringBuilder sb = new StringBuilder();
		if (episode.getSeason() != null || episode.getSpecial() != null) {
			sb.append(episode.getSpecial() == null ? episode.getSeason() : 0).append('x');
		}
		if (episode.getEpisode() != null || episode.getSpecial() != null) {
			sb.append(String.format("%02d", episode.getSpecial() == null ? episode.getEpisode() : episode.getSpecial()));
		}
		return sb.toString();
	}

	public String formatS00E00(Episode episode) {
		if (episode instanceof MultiEpisode) {
			return formatMultiRangeS00E00(((MultiEpisode) episode).getEpisodes());
		}

		StringBuilder sb = new StringBuilder();
		if (episode.getSeason() != null || episode.getSpecial() != null) {
			sb.append(String.format("S%02d", episode.getSpecial() == null ? episode.getSeason() : 0));
		}
		if (episode.getEpisode() != null || episode.getSpecial() != null) {
			sb.append(String.format("E%02d", episode.getSpecial() == null ? episode.getEpisode() : episode.getSpecial()));
		}
		return sb.toString();
	}

	public String formatMultiTitle(List<Episode> episodes) {
		return episodes.stream().map(e -> removeTrailingBrackets(e.getTitle())).distinct().collect(Collectors.joining(" & "));
	}

	public String formatMultiRangeSxE(List<Episode> episodes) {
		return getSeasonEpisodeNumbers(episodes).entrySet().stream().map(it -> {
			if (it.getKey() >= 0) {
				// season episode format
				return String.format("%01dx%02d-%02d", it.getKey(), it.getValue().first(), it.getValue().last());
			} else {
				// absolute episode format
				return String.format("%02d-%02d", it.getValue().first(), it.getValue().last());
			}
		}).collect(Collectors.joining("-"));
	}

	public String formatMultiRangeS00E00(List<Episode> episodes) {
		return getSeasonEpisodeNumbers(episodes).entrySet().stream().map(it -> {
			if (it.getKey() >= 0) {
				// season episode format
				return String.format("S%02dE%02d-E%02d", it.getKey(), it.getValue().first(), it.getValue().last());
			} else {
				// absolute episode format
				return String.format("E%02d-E%02d", it.getValue().first(), it.getValue().last());
			}
		}).collect(Collectors.joining("-"));
	}

	private SortedMap<Integer, SortedSet<Integer>> getSeasonEpisodeNumbers(Iterable<Episode> episodes) {
		SortedMap<Integer, SortedSet<Integer>> n = new TreeMap<Integer, SortedSet<Integer>>();
		for (Episode it : episodes) {
			Integer s = it.getSeason() == null || it.getSpecial() != null ? it.getSpecial() == null ? -1 : 0 : it.getSeason();
			Integer e = it.getEpisode() == null ? it.getSpecial() == null ? -1 : it.getSpecial() : it.getEpisode();

			n.computeIfAbsent(s, key -> new TreeSet<Integer>()).add(e);
		}
		return n;
	}

	private final Pattern sxePattern = Pattern.compile("- (?:(\\d{1,2})x)?(Special )?(\\d{1,3}) -");
	private final Pattern airdatePattern = Pattern.compile("\\[(\\d{4}-\\d{1,2}-\\d{1,2})\\]");

	@Override
	public Episode parseObject(String s, ParsePosition pos) {
		StringBuilder source = new StringBuilder(s);

		Integer season = null;
		Integer episode = null;
		Integer special = null;
		SimpleDate airdate = null;

		Matcher m;

		if ((m = airdatePattern.matcher(source)).find()) {
			airdate = SimpleDate.parse(m.group(1), "yyyy-MM-dd");
			source.replace(m.start(), m.end(), ""); // remove matched part from text
		}

		if ((m = sxePattern.matcher(source)).find()) {
			season = (m.group(1) == null) ? null : new Integer(m.group(1));
			if (m.group(2) == null)
				episode = new Integer(m.group(3));
			else
				special = new Integer(m.group(3));

			source.replace(m.start(), m.end(), ""); // remove matched part from text

			// assume that all the remaining text is series name and title
			String name = source.substring(0, m.start()).trim();
			String title = source.substring(m.start()).trim();

			// did parse input
			pos.setIndex(source.length());
			return new Episode(name, season, episode, title, season == null ? episode : null, special, airdate, null);
		}

		// failed to parse input
		pos.setErrorIndex(0);
		return null;
	}

	@Override
	public Episode parseObject(String source) throws ParseException {
		return (Episode) super.parseObject(source);
	}

}
