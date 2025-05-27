package com.fidypay.utils.ex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RandomNumberGenrator {

	private static final Logger LOGGER = LoggerFactory.getLogger(RandomNumberGenrator.class);

	public static String generateWalletPin() {

		String walletPin = null;
		walletPin = "" + (long) Math.floor(Math.random() * 900000L + 100000);
		LOGGER.info("Wait 5 second random number Generated" + walletPin);

		return walletPin;
	}
	
	
	public static String generateToken(Long merchantId) {
		String token=null;
		int n =15;

		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"+"0123456789";

		StringBuilder sb = new StringBuilder(n);

		for (int i = 0; i < n; i++) {

		int index
		= (int)(AlphaNumericString.length()
		* Math.random());

		sb.append(AlphaNumericString
		.charAt(index));
		}


		token=merchantId+"RESPASS"+sb.toString();

		return token;
		}
}
