package com.fidypay.utils.ex;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateAndTime {
	
	private static final Logger LOG = LoggerFactory.getLogger(DateAndTime.class);

	public static Timestamp currentDateAndTime() throws ParseException {

		String s = getCurrentTimeInIST();

		Timestamp date2 = Timestamp.valueOf(s);

		return date2;

	}

	/*
	 * This Function is using for API Response Current Date & Time.
	 */
	public static LocalDateTime getCurrentDateTime() {
		return LocalDateTime.now();
	}

	public static String getCurrentTimeInIST() throws ParseException {
		DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		utcFormat.setTimeZone(TimeZone.getTimeZone("IST"));
		Date timestamp = new Date();
		String istTime = utcFormat.format(timestamp);
		return istTime;
	}

	public static String dateFormatReports(String date) throws ParseException {
		SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		date = myFormat.format(fromUser.parse(date));
		return date;
	}

	public static String dateFormatReportsForFinal(String date) throws ParseException {
		SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
		date = myFormat.format(fromUser.parse(date));
		return date;
	}

	// bhartijoshi
	public static String convertDateTimeFormat(String dateTimeString) {
		LocalDateTime dateTime = LocalDateTime.parse(dateTimeString,
				DateTimeFormatter.ofPattern("yyyy-MM-dd HH.mm.ss.S"));
		return dateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

	}
	
	public static String convertDateTimeFormatSubMerchant(String inputDate) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        try {
            Date date = inputFormat.parse(inputDate);
            String outputDate = outputFormat.format(date);
            return outputDate;
        } catch (ParseException e) {
            return e.getMessage();
        }
    }

	public static long compareTwoTimeStamps(Timestamp currentTime, Timestamp oldTime) {
		long milliseconds1 = oldTime.getTime();
		long milliseconds2 = currentTime.getTime();
		long diff = milliseconds2 - milliseconds1;
		long diffSeconds = diff / 1000;
		// long diffMinutes = diff / (60 * 1000);
		return diffSeconds;
	}

	public static String getCurrentTimeInISTForDebitTrxn(String inputDate) throws ParseException {
		String istTime = "NA";
		try {
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
			Date date = inputFormat.parse(inputDate);
			istTime = outputFormat.format(date);
			LOG.info("Converted Date: {}" , istTime);
		} catch (ParseException e) {
			e.printStackTrace();
			istTime = "NA";
		}

		return istTime;
	}
}
