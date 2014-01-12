package com.censoredsoftware.censoredlib.util;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

public class Times
{
	/**
	 * Return the seconds since the provided time.
	 * 
	 * @param time The provided time.
	 * @return Seconds.
	 */
	public static double getSeconds(long time)
	{
		return (double) TimeUnit.MILLISECONDS.toSeconds(time - System.currentTimeMillis());
	}

	/**
	 * Return the minutes since the provided time.
	 * 
	 * @param time The provided time.
	 * @return Minutes.
	 */
	public static double getMinutes(long time)
	{
		return (double) TimeUnit.MILLISECONDS.toMinutes(time - System.currentTimeMillis());
	}

	/**
	 * Return the hours since the provided time.
	 * 
	 * @param time The provided time.
	 * @return Hours.
	 */
	public static double getHours(long time)
	{
		return (double) TimeUnit.MILLISECONDS.toHours(time - System.currentTimeMillis());
	}

	/**
	 * Return a readable String of the amount of time since a provided time.
	 * 
	 * @param time The provided time.
	 * @param round Round to an int.
	 * @return The readable String.
	 */
	public static String getTimeTagged(long time, boolean round)
	{

		DecimalFormat format = round ? new DecimalFormat("#") : new DecimalFormat("#.##");
		if(getHours(time) >= 1) return format.format(getHours(time)) + "h";
		else if(Double.valueOf(format.format(getMinutes(time))) >= 1) return format.format(getMinutes(time)) + "m";
		else return format.format(getSeconds(time)) + "s";
	}
}
