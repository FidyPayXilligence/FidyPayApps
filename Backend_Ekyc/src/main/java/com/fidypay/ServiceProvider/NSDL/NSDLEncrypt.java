package com.fidypay.ServiceProvider.NSDL;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NSDLEncrypt {

	private static final Logger logger = LoggerFactory.getLogger(NSDLEncrypt.class);

	private static final String pennylessmobilewarescrecetkey = "32e90259334 cf6d36880df8d2ef9c6188e0fe598fac306be8dcf4642bb57417b";
	private static final String mobilewarescrecetkey = "swBGKwEc2Y3PESTjAyXvocv0VLJbR3VtcKb6Vrx5wR9wr9cz7VBRSkwMPLf8KCT3DORi1d12ZazJwwn8d3VFPonUFnaGN7YpcZ8TnIoZvfTcsdab1fjwRDS3ppgosmmTtaQ8NouUnLZrdURfcbcTb8hpZz5QqtSBc5nArUk5Yq7NsdXBf67HGl5fFkWAcdQuFo47Y1V1YTFZ1M8O6IV8kX71v3ob7dFBJJahJzGD3HGbyp9uSZRbDqGNJUlzkdeT";
	private static final String mobilewaresyncpennylessurl = "https://nsdluat.transxt.in/imps-ws/transaction/sync/pl/2/acverify";

	private static final String UTF8 = "UTF-8";
	private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
	private static final String AES = "AES";
	private static final String SHA256 = "SHA-256";

	public static String generateHaskKey(String key) {
		StringBuilder lHashtext = new StringBuilder();
		try {
			MessageDigest md = MessageDigest.getInstance(SHA256);
			byte[] messageDigest = md.digest(key.getBytes());
			BigInteger number = new BigInteger(1, messageDigest);
			lHashtext = lHashtext.append(number.toString(16));
			while (lHashtext.length() < 32) {
				lHashtext = lHashtext.insert(0, "0");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return lHashtext.toString();
	}

	public static String encdatapayload(String message, String mobilewareServiceName) {
		logger.info("mobilewareServiceName: {}", mobilewareServiceName);
		try {
			String sercretKey = "";
			if (mobilewareServiceName.equals(mobilewaresyncpennylessurl))
				sercretKey = pennylessmobilewarescrecetkey;
			else
				sercretKey = mobilewarescrecetkey;

			MessageDigest md = MessageDigest.getInstance(SHA256);

			byte[] digestOfPassword = md.digest(sercretKey.getBytes(UTF8));
			byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
			byte[] iv = Arrays.copyOf(digestOfPassword, 16);

			SecretKey key = new SecretKeySpec(keyBytes, AES);
			IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);

			return new String(Base64.encodeBase64(cipher.doFinal(message.getBytes(UTF8))));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String decdatapayload(String message, String mobilewareServiceName) {
		try {
			String sercretKey = "";
			if (mobilewareServiceName.equals(mobilewaresyncpennylessurl))
				sercretKey = pennylessmobilewarescrecetkey;
			else
				sercretKey = mobilewarescrecetkey;

			MessageDigest md = MessageDigest.getInstance(SHA256);

			byte[] digestOfPassword = md.digest(sercretKey.getBytes(UTF8));
			byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
			byte[] iv = Arrays.copyOf(digestOfPassword, 16);

			SecretKey key = new SecretKeySpec(keyBytes, AES);
			IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);

			return new String(cipher.doFinal(Base64.decodeBase64(message)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

}
