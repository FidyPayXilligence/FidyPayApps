package com.fidypay.ServiceProvider.NSDL;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Encrypt {

	public static String encryptstring(String value, String password) {
		try {
			SecretKey originalKey = new SecretKeySpec(password.substring(0, 32).getBytes("UTF-8"), "AES");
			IvParameterSpec iv = new IvParameterSpec(password.substring(0, 16).getBytes("UTF-8"));
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, originalKey, iv);
			byte[] encrypted = cipher.doFinal(value.getBytes());
			return new String(Base64.getEncoder().encodeToString(encrypted));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static String decryptString(String value, String password) {
		try {
			SecretKeySpec originalKey = new SecretKeySpec(password.substring(0, 32).getBytes("UTF-8"), "AES");
			IvParameterSpec iv = new IvParameterSpec(password.substring(0, 16).getBytes("UTF-8"));
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, originalKey, iv);
			byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(value));
			return new String(decrypted);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static String encryptValidationRequest(String value, String secretKey) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");

			byte[] digestOfPassword = md.digest(secretKey.getBytes("UTF-8"));
			byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
			byte[] iv = Arrays.copyOf(digestOfPassword, 16);

			SecretKey key = new SecretKeySpec(keyBytes, "AES");
			IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);

			byte[] encrypted = cipher.doFinal(value.getBytes());
			return Base64.getEncoder().encodeToString(encrypted);
		} catch (Exception ex) {
			// throw new E("Unable to encrypt", ex);
			return "";
		}

	}

	public static String decryptValidationRequest(String value, String secretKey) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");

			byte[] digestOfPassword = md.digest(secretKey.getBytes("UTF-8"));
			byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
			byte[] iv = Arrays.copyOf(digestOfPassword, 16);

			SecretKey key = new SecretKeySpec(keyBytes, "AES");
			IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);

			byte[] base64decryptedMessage = Base64.getDecoder().decode(value);
			byte[] decrypted = cipher.doFinal(base64decryptedMessage);

			return new String(decrypted);
		} catch (Exception ex) {
			// throw new BankConfigException("Unable to encrypt", ex);
			return "";
		}

	}

}