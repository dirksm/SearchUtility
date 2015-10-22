package com.leewardassociates.search.code.util;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * The Class DateUtil.
 *
 * @author Michael R Dirks
 */
public class DateUtil {

	/**
	 * Parses text from the beginning of the given string to produce a date. The method may not use the entire text of the given string.
	 * @param dt A String whose beginning should be parsed.
	 * @param pattern a pattern string describing this date format.
	 * @return A Date parsed from the string. In case of error, returns null.
	 */
	public static Date parse(String dt, String pattern)
	{
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		Date date = null;
		try {
			date = sdf.parse(dt);
		} catch (Exception e) {
			date = null;
		}
		return date;
	}
	
	/**
	 * Formats a {@link Date} into a date/time {@link String}.
	 * @param date the time value to be formatted into a time {@link String}.
	 * @param pattern a pattern string describing this {@link Date} format.
	 * @return A {@link Date} parsed from the {@link String}.
	 */
	public static String format(Date date, String pattern)
	{
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		String dt = "";
		try {
			dt = sdf.format(date);
		} catch (Exception e) {
			dt = "";
		}
		return dt;
	}
}
