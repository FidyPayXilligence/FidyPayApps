package com.fidypay.utils.ex;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateAndTime {

	public static String formatDate1(String inDate) throws Exception {

		SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
		SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = inputFormat.parse(inDate);
		String output = outputFormat.format(date);
		return output;
		}
	
	public static String getCurrentTimeInIST() throws ParseException {
		DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		utcFormat.setTimeZone(TimeZone.getTimeZone("IST"));
		Date timestamp = new Date();
		String istTime = utcFormat.format(timestamp);
		return istTime;
	}

	
	public static String dateFormatForPartner(String date) throws ParseException {
		SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		date = myFormat.format(fromUser.parse(date));
		return date;
	}
	
	
	public static String dateFormatForPartner2(String date) throws ParseException {
		SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		date = myFormat.format(fromUser.parse(date));
		return date;
	}
	
	
	
	
	public static String dateFormatForPartner3(String date) throws ParseException {
		SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
		SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		date = myFormat.format(fromUser.parse(date));
		return date;
	}


	public static String dateFormatForPartner4(String date) throws ParseException {
		SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.a");
		SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		date = myFormat.format(fromUser.parse(date));
		return date;
	}

	
	public static boolean isValidDateFormat(String value) {
		Date date = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			date = sdf.parse(value);
			if (!value.equals(sdf.format(date))) {
				date = null;
			}
		} catch (ParseException ex) {
			return false;
		}
		return date != null;
	}

	public static String formatDate(String inDate) throws Exception {

		SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = inputFormat.parse(inDate);
		String output = outputFormat.format(date);
		return output;
	}

	public static long compareTwoTimeStamps(Timestamp currentTime, Timestamp oldTime) {
		long milliseconds1 = oldTime.getTime();
		long milliseconds2 = currentTime.getTime();
		long diff = milliseconds2 - milliseconds1;
		long diffSeconds = diff / 1000;
		// long diffMinutes = diff / (60 * 1000);
		return diffSeconds;
	}

}
