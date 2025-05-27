package com.fidypay.utils.ex;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RandomNumberGenrator {

	private static final Logger LOGGER = LoggerFactory.getLogger(RandomNumberGenrator.class);

	public static String generateWalletPin() {

		String walletPin = null;
		walletPin = "" + (long) Math.floor(Math.random() * 900000L + 100000);
		LOGGER.info("Wait 5 second random number Generated" + walletPin);

		return walletPin;
	}
	
	
	
	public static String generateRandomStringRefId() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssSSS");
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String timestampDate = sdf.format(timestamp);
		String refId = "FP" + timestampDate;
		// LOGGER.info("refId : " + refId + " length " + refId.length());
		return refId;
	}

	public static String randomNumberGenerate(int n) {
		// int n = 16;
		// chose a Character random from this String
		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "abcdefghijklmnopqrstuvxyz";

		// create StringBuffer size of AlphaNumericString
		StringBuilder sb = new StringBuilder(n);

		for (int i = 0; i < n; i++) {

			// generate a random number between
			// 0 to AlphaNumericString variable length
			int index = (int) (AlphaNumericString.length() * Math.random());

			// add Character one by one in end of sb
			sb.append(AlphaNumericString.charAt(index));
		}

		return sb.toString();

	}
	
}
