package com.fidypay.utils.ex;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

@Service
public class Validations {

	public static boolean isValidEmail(String str) {
		Pattern ptrn = Pattern.compile(
				"^(?=.{1,64}@)[a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*@[^-][a-zA-Z0-9]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{2,})$");
		Matcher match = ptrn.matcher(str);
		return (match.find() && match.group().equals(str));
	}

	public static boolean isValidPanCardNo(String panCardNo) {

		String regex = "[A-Z]{5}[0-9]{4}[A-Z]{1}";

		Pattern p = Pattern.compile(regex);

		if (panCardNo == null) {
			return false;
		}
		if (panCardNo.length() != 10) {
			return false;
		}

		Matcher m = p.matcher(panCardNo);

		return m.matches();
	}

	public static boolean isValidGSTNo(String str) {
		// Regex to check valid
		// GST (Goods and Services Tax) number
		String regex = "^[0-9]{2}[A-Z]{5}[0-9]{4}" + "[A-Z]{1}[1-9A-Z]{1}" + "Z[0-9A-Z]{1}$";

		Pattern p = Pattern.compile(regex);

		if (str.length() != 15) {
			return false;
		}

		Matcher m = p.matcher(str);

		return m.matches();
	}

	public static boolean isValidGSTNo2(String str) {
		// Regex to check valid
		// GST (Goods and Services Tax) number
		String regex = "^[a-zA-Z\\s]{1,60}+$";

		Pattern p = Pattern.compile(regex);

		if (str.length() >= 60) {
			return false;
		}

		Matcher m = p.matcher(str);

		return m.matches();
	}

	public static boolean isValidEPICNumber(String str) {
		// Regex to check valid EPIC Number
		String regex = "^[a-zA-Z]{3}[0-9]{7}$";

		// Compile the ReGex
		Pattern p = Pattern.compile(regex);

		// If the str is empty return false
		if (str == null) {
			return false;
		}
		Matcher m = p.matcher(str);

		// Return if the str
		// matched the ReGex
		return m.matches();
	}

	public static boolean isValidAdharNumber(String str) {

		if (str == null) {
			return false;
		}

		if (str.length() != 12) {
			return false;
		}
		return true;
	}

	public static boolean isValidDateFormat(String value) {
		Date date = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			date = sdf.parse(value);
			if (!value.equals(sdf.format(date))) {
				date = null;
			}
		} catch (ParseException ex) {
			return false;
		}
		return date != null;
	}

	public static boolean validateJavaDate(String strDate) {
		/* Check if date is 'null' */
		if (strDate.trim().equals("")) {
			return true;
		}
		/* Date is not 'null' */
		else {
			/*
			 * Set preferred date format, For example MM-dd-yyyy, MM.dd.yyyy,dd.MM.yyyy etc.
			 */
			SimpleDateFormat sdfrmt = new SimpleDateFormat("dd/MM/yyyy");
			sdfrmt.setLenient(false);
			/*
			 * Create Date object parse the string into date
			 */
			try {
				Date javaDate = sdfrmt.parse(strDate);
				System.out.println(strDate + " is valid date format");
			}
			/* Date format is invalid */
			catch (ParseException e) {
				System.out.println(strDate + " is Invalid Date format");
				return false;
			}
			/* Return true if date format is valid */
			return true;
		}
	}
}
