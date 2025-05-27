package com.fidypay.ServiceProvider.NSDL;

import java.math.BigInteger;
import java.security.MessageDigest;

public class HashKeyGenrator {

	public static String generateHaskKey(String key) {
		StringBuilder lHashtext = new StringBuilder();
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] messageDigest = md.digest(key.getBytes());
			BigInteger number = new BigInteger(1, messageDigest);
			String lHashtext1 = number.toString(16);
			lHashtext = lHashtext.append(lHashtext1);
			while (lHashtext.length() < 32) {
				lHashtext = lHashtext.insert(0, "0");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return lHashtext.toString();
	}

}
