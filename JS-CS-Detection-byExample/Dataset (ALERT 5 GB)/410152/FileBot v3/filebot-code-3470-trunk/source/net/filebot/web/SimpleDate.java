package net.filebot.web;

import static java.util.Calendar.*;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class SimpleDate implements Serializable, Comparable<Object> {

	private int year;
	private int month;
	private int day;

	protected SimpleDate() {
		// used by serializer
	}

	public SimpleDate(int year, int month, int day) {
		this.year = year;
		this.month = month;
		this.day = day;
	}

	public SimpleDate(long t) {
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(new Date(t));
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH) + 1;
		day = c.get(Calendar.DAY_OF_MONTH);
	}

	public int getYear() {
		return year;
	}

	public int getMonth() {
		return month;
	}

	public int getDay() {
		return day;
	}

	public long getTimeStamp() {
		return new GregorianCalendar(year, month - 1, day).getTimeInMillis(); // Month value is 0-based, e.g. 0 for January
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SimpleDate) {
			SimpleDate other = (SimpleDate) obj;
			return year == other.year && month == other.month && day == other.day;
		} else if (obj instanceof CharSequence) {
			return this.toString().equals(obj.toString());
		}

		return super.equals(obj);
	}

	@Override
	public int compareTo(Object other) {
		if (other instanceof SimpleDate) {
			return compareTo((SimpleDate) other);
		} else if (other instanceof CharSequence) {
			SimpleDate otherDate = parse(other.toString());
			if (otherDate != null) {
				return compareTo(otherDate);
			}
		}

		throw new IllegalArgumentException(String.valueOf(other));
	}

	public int compareTo(SimpleDate other) {
		return Long.compare(this.getTimeStamp(), other.getTimeStamp());
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(new Object[] { year, month, day });
	}

	@Override
	public SimpleDate clone() {
		return new SimpleDate(year, month, day);
	}

	@Override
	public String toString() {
		return String.format("%04d-%02d-%02d", year, month, day);
	}

	public String format(String pattern) {
		return format(pattern, Locale.ROOT);
	}

	public String format(String pattern, Locale locale) {
		return new SimpleDateFormat(pattern, locale).format(new GregorianCalendar(year, month - 1, day).getTime()); // Calendar months start at 0
	}

	public static SimpleDate parse(String string) {
		return parse(string, "yyyy-MM-dd");
	}

	public static SimpleDate parse(String string, String pattern) {
		if (string == null || string.isEmpty())
			return null;

		SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.ROOT);
		formatter.setLenient(false); // enable strict mode (e.g. fail on invalid dates like 0000-00-00)

		try {
			Calendar date = new GregorianCalendar(Locale.ROOT);
			date.setTime(formatter.parse(string));
			return new SimpleDate(date.get(YEAR), date.get(MONTH) + 1, date.get(DAY_OF_MONTH)); // Calendar months start at 0
		} catch (ParseException e) {
			// no result if date is invalid
			// Logger.getLogger(Date.class.getName()).log(Level.WARNING, e.getMessage());
			return null;
		}
	}

}
