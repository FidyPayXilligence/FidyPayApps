package com.fidypay.ServiceProvider.AirtelPayments;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

@Component
//@Slf4j
public class AESCryptoV2Utils {

	private static final Logger log = LoggerFactory.getLogger(AESCryptoV2Utils.class);

	private static final String ENCRYPT_ALGO = "AES/GCM/NoPadding";
	private static final int TAG_LENGTH_BIT = 128;
	private static final int IV_LENGTH_BYTE = 12;
	private static final int SALT_LENGTH_BYTE = 16;
	private static final Charset UTF_8 = StandardCharsets.UTF_8;

	public String encryptString(String strToEncrypt, String key) {
		String encrytedString = null;
		if (StringUtils.isNotBlank(strToEncrypt)) {
			try {
				encrytedString = encrypt(strToEncrypt.getBytes(StandardCharsets.UTF_8), key);
				log.info("encrypted data {}", encrytedString);
				return encrytedString;
			} catch (Exception e) {
				log.error("error encryting data", e);
			}
		}
		return encrytedString;
	}

	public String decryptString(String strToDecrypt, String key) {
		String dataString = "";
		if (StringUtils.isNotEmpty(strToDecrypt)) {
			try {
				dataString = decrypt(strToDecrypt, key);
				log.info("decrypted data {}", dataString);
				return dataString;
			} catch (Exception e) {
				log.error("error decrypting data", e);
			}
		}
		return dataString;
	}

	private String encrypt(byte[] pText, String key)
			throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
		byte[] salt = getRandomNonce(SALT_LENGTH_BYTE);
		byte[] iv = getRandomNonce(IV_LENGTH_BYTE);
		SecretKey aesKeyFromPassword = getAESKeyFromPassword(key.toCharArray(), salt);
		Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
		cipher.init(Cipher.ENCRYPT_MODE, aesKeyFromPassword, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
		byte[] cipherText = cipher.doFinal(pText);
		byte[] cipherTextWithIvSalt = ByteBuffer.allocate(iv.length + salt.length + cipherText.length).put(iv).put(salt)
				.put(cipherText).array();
		return Base64.getEncoder().encodeToString(cipherTextWithIvSalt);

	}

	private String decrypt(String cText, String key)
			throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
		byte[] decode = Base64.getDecoder().decode(cText.getBytes(UTF_8));
		ByteBuffer bb = ByteBuffer.wrap(decode);
		byte[] iv = new byte[IV_LENGTH_BYTE];
		bb.get(iv);
		byte[] salt = new byte[SALT_LENGTH_BYTE];
		bb.get(salt);
		byte[] cipherText = new byte[bb.remaining()];
		bb.get(cipherText);
		SecretKey aesKeyFromPassword = getAESKeyFromPassword(key.toCharArray(), salt);
		Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
		cipher.init(Cipher.DECRYPT_MODE, aesKeyFromPassword, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
		byte[] plainText = cipher.doFinal(cipherText);
		return new String(plainText, UTF_8);
	}

	private SecretKey getAESKeyFromPassword(char[] password, byte[] salt)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		KeySpec spec = new PBEKeySpec(password, salt, 65536, 128);
		return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
	}

	private byte[] getRandomNonce(int numBytes) {
		byte[] nonce = new byte[numBytes];
		new SecureRandom().nextBytes(nonce);
		return nonce;
	}

	public static String generateHash(String hashString) throws NoSuchAlgorithmException {

		log.info("----------------------------------HASH-STRING----------------------------------\n" + hashString);
		MessageDigest md = MessageDigest.getInstance("SHA-512");
		md.update(hashString.getBytes());
		byte[] byteData = md.digest();
		StringBuilder sb = new StringBuilder();
		for (byte element : byteData) {
			sb.append(Integer.toString((element & 0xff) + 0x100, 16).substring(1));
		}
		log.info("----------------------------------HASH----------------------------------\n" + sb.toString());
		return sb.toString();
	}

}
