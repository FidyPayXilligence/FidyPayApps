package com.fidypay.utils.ex;

import java.util.regex.Pattern;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

	public static boolean isValidNumber(String input) {
		String regex = "\\d+";
		Pattern pattern = Pattern.compile(regex);
		return pattern.matcher(input).matches();
	}

	public static String getCurrentTimeInIST() throws ParseException {
		DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		utcFormat.setTimeZone(TimeZone.getTimeZone("IST"));
		Date timestamp = new Date();
		String istTime = utcFormat.format(timestamp);
		return istTime;
	}

	public static String convertDateToString(Date date) {
		if (date != null) {
			DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			String strDate = dateFormat.format(date);
			return strDate;
		} else {
			return "";
		}

	}

	public static String longToDate(Long date) {
		if (date != null) {
			DateFormat simple = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			String strDate = simple.format(date);
			return strDate;
		} else {
			return "";
		}

	}

	public static String convertDateToStringSlash(Date date) {
		if (date != null) {
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			String strDate = dateFormat.format(date);
			return strDate;
		} else {
			return "";
		}

	}

	public static String convertDateToStringYYYYMMDD(Date date) {
		if (date != null) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			String strDate = formatter.format(new Date());
			return strDate;
		} else {
			return "";
		}

	}

	public static String convertDateToStringWithTime(Date date) {
		if (date != null) {
			DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss aa");
			String strDate = dateFormat.format(date);
			return strDate;
		} else {
			return "";
		}

	}

	public static String convertDateToStringWithTimeNew(Date date) {
		if (date != null) {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss ");
			String strDate = dateFormat.format(date);
			return strDate;
		} else {
			return "";
		}

	}

	public static String convertDateToStringWithTimes(Date date) {
		if (date != null) {
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
			String strDate = dateFormat.format(date);
			return strDate;
		} else {
			return "";
		}

	}

	public static String convertDateToStringExpiry(Integer duration) {
		if (duration != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyy hh:mm:ss");
			Calendar c = Calendar.getInstance();
			c.add(Calendar.DAY_OF_MONTH, duration - 1);
			String newDate = sdf.format(c.getTime());
			return newDate;
		} else {
			return "";
		}

	}

	public static String convertTimeStampToDate(long timeStamp) {
		if (timeStamp != 0L) {
			Date currentDate = new Date(timeStamp);
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			String newDate = df.format(currentDate);
			return newDate;
		} else {
			return "";
		}

	}

	public static long getMintue(Date expriyDate) throws ParseException {
		Date date = new Date();

		long timeDiff;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String strDate = dateFormat.format(date);
		String expirydate = dateFormat.format(expriyDate);
		System.out.println("strDate=" + strDate);
		System.out.println("expriyDate=" + expirydate);
		Date startDateObj = dateFormat.parse(strDate);
		Date endDateObj = dateFormat.parse(expirydate);
		System.out.println("1:" + strDate.compareTo(expirydate));
		if (strDate.compareTo(expirydate) > 0) {
			timeDiff = startDateObj.getTime() - endDateObj.getTime();
		} else if (strDate.compareTo(expirydate) < 0) {
			timeDiff = startDateObj.getTime() - endDateObj.getTime();
		} else {
			timeDiff = endDateObj.getTime() - startDateObj.getTime();
		}

		long minDiff = timeDiff / (1000 * 60);
		return minDiff;

	}

	public static boolean isValidDateFormat(String value) {
		Date date = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			date = sdf.parse(value);
			if (!value.equals(sdf.format(date))) {
				date = null;
			}
		} catch (ParseException ex) {
			return false;
		}
		return date != null;
	}

	public static boolean isValidDateFormat(String from, String to) throws ParseException {
		boolean check = false;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date fromDate = sdf.parse(from);
		Date toDate = sdf.parse(to);
		int result = fromDate.compareTo(toDate);
		if (result > 0) {
			check = true;
		}
		return check;
	}

	public static Date convertStringToDateTime(String date) {
		try {
			if (date != null) {
				DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
				Date dateTime = dateFormat.parse(date);
				return dateTime;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public static Date StringToDate(String date) {
		try {
			if (date != null) {
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date dateTime = dateFormat.parse(date);
				return dateTime;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public static Date convertStringToDate(String date) {
		try {
			if (date != null) {
				;
				String pattern = "yyyy-MM-dd";
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
				Date praseDate = simpleDateFormat.parse(date);
				return praseDate;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public static Long convertStrigDateToLong(String date) {
		SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
		long milliseconds = 0;
		try {
			Date d = f.parse(date);
			milliseconds = d.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return milliseconds;
	}

	public static int dayBetweenTwoDate() {
		Calendar calendar = Calendar.getInstance();
		Date d1 = calendar.getTime();
		calendar.add(Calendar.MONTH, 1);
		Date d2 = calendar.getTime();
		long difference = d2.getTime() - d1.getTime();
		float daysBetween = (difference / (1000 * 60 * 60 * 24));
		int days = (int) daysBetween;
		return days;
	}

	public static String getDay(Date date) {
		if (date != null) {
			SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE"); // the day of the week abbreviated
			String days = simpleDateformat.format(date);
			return days;
		} else {
			return "";
		}

	}

	public static Date convertStringToDateDDMMYY(String date) {
		try {
			if (date != null) {
				DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
				Date dateTime = dateFormat.parse(date);
				return dateTime;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Date convertStringToDateMMDDYYYY(String date) {
		try {
			if (date != null) {
				DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
				Date dateTime = dateFormat.parse(date);
				return dateTime;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public static long countDays(String updatePasswordDate, String trxnDate) {
		try {
			DateTimeFormatter formatterWithMicroseconds = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
			DateTimeFormatter formatterWithMilliseconds = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
			LocalDateTime updatePasswordDateTime = LocalDateTime.parse(updatePasswordDate, formatterWithMicroseconds);
			LocalDateTime trxnDateTime = LocalDateTime.parse(trxnDate, formatterWithMilliseconds);
			long daysBetween = Duration.between(updatePasswordDateTime, trxnDateTime).toDays();
			System.out.println("daysBetween " + daysBetween);
			return daysBetween;
		} catch (Exception e) {
			DateTimeFormatter formatterWithMicroseconds = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
			DateTimeFormatter formatterWithMilliseconds = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
			LocalDateTime updatePasswordDateTime = LocalDateTime.parse(updatePasswordDate, formatterWithMicroseconds);
			LocalDateTime trxnDateTime = LocalDateTime.parse(trxnDate, formatterWithMilliseconds);
			long daysBetween = Duration.between(updatePasswordDateTime, trxnDateTime).toDays();
			System.out.println("daysBetween " + daysBetween);
			return daysBetween;
		}
	}

}
