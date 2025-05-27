package com.fidypay.ServiceProvider.YesBank.SellerAggregator;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
@Service
public class CallBackImpl {

	private static final Logger LOGGER = LoggerFactory.getLogger(CallBackImpl.class);

	public String decryptionProcess(String callBackResponse, String key, String hash, String iv) throws Exception {
		//String response = null;
		LOGGER.info("Call Back : " + callBackResponse);

		LOGGER.info("Call Back Key : " + key);

		LOGGER.info("Call Back Hash : " + hash);
		// String iv = "1111222233334444";
		LOGGER.info("Call Back iv : " + iv);

		byte[] aesKey = rsaDecrypt(Base64.decodeBase64(key), getPrivateKey());
		System.out.println("AES Key -- " + aesKey.length);

		String decryptResp = decrypt(callBackResponse, iv.getBytes(), aesKey);
		System.out.println(" Decrypt Response -- " + decryptResp);

		return decryptResp;
	}


	public static PrivateKey getPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
		PrivateKey privateKey = null;
		String priKey = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCzVP8jc0+pG5g6\r\n"
				+ "l0GYN99Kvln9uWHyEDEAwmHiNSRW9HKgvC0x8y/JLt8Tz8cn3QPdWC76oNNRNUGb\r\n"
				+ "m87u8BgxqIp+66LKqqCMrCNhVD9lJcUNmXO0CESb4f0eiRruxTTbG8SC+5ylbyj4\r\n"
				+ "qd/wr89Q2dKbZwzObHlHrwqSy2bz9Ewa7KA9wAiWx08VIyPhx2/gYlxF3pDyPnnD\r\n"
				+ "45RDjEtsCywCjU0+7iWS3gLVq4n+L3RFxVBzhCj20/wqPjAUyiJX6KkoN/81HRNa\r\n"
				+ "dmbFsOl9uiFjqyoAcKcIxU8h9P1DgnTHqpoIfpwD+35iJJkVZcCRaFmudJWfJWzh\r\n"
				+ "CMSsBIQdAgMBAAECggEAKAN8Fk3hZWs50UD0quE0mnnUnI9jsl44gOHec1nGLPzI\r\n"
				+ "IuM59l47uFkT/1iqKFPhS98kRFnkLMeERxNB3gCGdXYUyPMM1Mmznw+9qTbiGlCO\r\n"
				+ "IlWuAmqh3GJVVx3ie4pXG9ibRVqc/jDKZImK5SplSLYBjxk0O07q1LrS0iQ+aYom\r\n"
				+ "CODAiDbEwx+SD8h3ZeVhLVoF668wLVe8HTJNHDXLfeEKzIQasxFpxhmw8+WwObrF\r\n"
				+ "yo9tC8F4QSSyU1hCn/ZK+H57VEFE9j5uR9PqtrYpVK3PUNa75SZ7a95MtAy5PPam\r\n"
				+ "dWHQYaalQMhcyMJBjZSQAyAf3B4ou9AvZt9kN2KW5QKBgQDpBHRmpEmsJZ96wNr9\r\n"
				+ "tlLzNWz5TRvydXKY3Vt89vYnXgEJ/bpdZxdiY+FUU36rTsXwNM7X6OEFtoKvpscc\r\n"
				+ "IdGXJRTOpJzjIHCjkf7prf5ZAImyx3+GeEb13kYQBlXrwE7+sgK+27iQnnVFqFuT\r\n"
				+ "D4tl8dvFgXer4aOVar0fodau9wKBgQDFBQTrPX6bbtJFZgq3DG/mIfch7AxpgMWe\r\n"
				+ "4lKeyrTIYM1AFeUI9lDowukFUFKTu1oMYoXFE9hyAMTsFgnIygG36g/aHbTkqcVf\r\n"
				+ "3W+wTlsDSHTmluHwvQJGSBsQOAtyeEvbsVZ0TC4bF7JGpDLl/RAE4YwAWF5/cLT1\r\n"
				+ "FRWnDpOciwKBgGtwvGMfWUDg0Bn/lnnpeXHernJ81WgEM2S8nBSQoosgUwZSUX4D\r\n"
				+ "rMqXFyyUmxFN7wKEtuLi4+6IatWm5qPYDBXO6Tsmt5gaOxWQmaMRsPdEwjkGt4w/\r\n"
				+ "JSj05gU5hqB/OW1CkvWBxiYYiiYmLKMwRawpypXUzmMYVR7t6moNazmdAoGAKJJ4\r\n"
				+ "CNmJEQkpVBFHc5qkEIg+FEY/6BoUmDuTOBAWWo1UtzXLDKVs1AIPaoC4AKQ8TwCa\r\n"
				+ "+5KyqAdwhg2jxi2TXQReb1RexBgSBUPclDoOlAZ/zFyV+rVxmneO2zAva0tKk9tZ\r\n"
				+ "KmnltL6Uf+egc6xBeD/aTNfl3eif2ziUVOqzizECgYBMJXGY6EJKUvLQ3M7FwgO+\r\n"
				+ "Om9oavMTqmkaiaBR/Lf27moP5ePBqOaZliMi334iy51lR3XKgf2HTN80kUh+n+6D\r\n"
				+ "yN+PunBcP6T45MNBAHPlBAT01NFqECXGJ4sj/XpSFc5nyidoo0Cla5wfgeg8oFpb\r\n"
				+ "WsPQ7NAQivugm42j8HkWXA==";

		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(priKey.getBytes()));
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		privateKey = keyFactory.generatePrivate(keySpec);
		return privateKey;
	}


	private static byte[] rsaDecrypt(byte[] data, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException,
			InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

		System.out.println("  Rsa Decrpt : ");
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
		cipher.init(Cipher.DECRYPT_MODE, key);
		return cipher.doFinal(data);
	}

	public static String decrypt(String encryptedData, byte[] initVector, byte[] key) throws Exception {
		IvParameterSpec iv = new IvParameterSpec(initVector);
		SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
		cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
		return new String(cipher.doFinal(java.util.Base64.getMimeDecoder().decode(encryptedData.getBytes())));
	}

}
