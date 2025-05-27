package com.fidypay.ServiceProvider.YesBank.SellerAggregator;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EncryptionAndDecryptionProduction {

	// Live URL
	private static final String URL = "https://api.yespayhub.in/services/seller/ps";

	private static final Logger LOGGER = LoggerFactory.getLogger(EncryptionAndDecryptionProduction.class);

	public String getEncDec(String reqString, String partnerKey)
			throws Exception, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException,
			IllegalBlockSizeException, InvalidKeySpecException, Exception {

		System.out.println("Started");
		SecretKey key = getKey();
		LOGGER.info("Secret key -- " + Base64.encodeBase64String(key.getEncoded()));

		Date date = new Date();
		Timestamp trxnDate = new Timestamp(date.getTime());
		System.out.println("  Date : " + trxnDate);

//		String reqString = "{\r\n" + " \"partnerReferenceNo\": \"PRN012\",\r\n"
//				+ " \"actionName\": \"ADD_PARTNER_SELLER\",\r\n" + " \"partnerKey\": \"dG5qN0RPRW\",\r\n"
//				+ " \"p1\": \"BUSI007\",\r\n" + " \"p2\": \"E RAJESH\",\r\n" + " \"p3\": \"SI00000\",\r\n"
//				+ " \"p4\": \"9820548300\",\r\n" + " \"p5\": \"kunal00@gmail.com\",\r\n" + " \"p6\": \"1520\",\r\n"
//				+ " \"p7\": \"SMALL\",\r\n" + " \"p8\": \"ONLINE\",\r\n" + " \"p9\": \"OTHERS\",\r\n"
//				+ " \"p10\": \"PUNE\",\r\n" + " \"p11\": \"PUNE\",\r\n" + " \"p12\": \"36\",\r\n"
//				+ " \"p13\": \"411031\",\r\n" + " \"p14\": \"AAQPE1394K12345\",\r\n" + " \"p15\": \"23AABCU3513E2ZW\",\r\n"
//				+ " \"p16\": \"AAQPE1394K\",\r\n" + " \"p17\": \"YESB0000005\"\r\n" + "}";
		String iv = "1111222233334444";
		String hash = signData(reqString, getPrivateKey());

		System.out.println("ReqString : " + reqString);
		System.out.println("hash -- " + hash);
		reqString = "{\"body\": \"" + encrypt(reqString, key.getEncoded(), iv.getBytes()) + "\"}";

		System.out.println("Encrypted Request -- " + reqString);
		HashMap hashMap = postHttpData(URL, reqString, rsaEncrypt(key.getEncoded(), getPublicKey()), iv, hash,
				rsaEncrypt(partnerKey.getBytes(), getPublicKey()));

		String respBody = (String) hashMap.get("body");
		System.out.println("response is -- " + respBody);

		JSONParser parser = new JSONParser();
		JSONObject json = (JSONObject) parser.parse(respBody);
		System.out.println(json);

		String aesKeyEnc = (String) hashMap.get("key");
		System.out.println("aesKeyEnc -- " + aesKeyEnc);

		byte[] aesKey = rsaDecrypt(Base64.decodeBase64(aesKeyEnc), getPrivateKey());
		System.out.println("AES Key -- " + aesKey.length);

		String encResponseBody = json.get("body").toString();
		System.out.println("  encResponseBody  : " + encResponseBody);

		String decryptResp = decrypt(encResponseBody, iv.getBytes(), aesKey);
		return decryptResp;

	}

	public static SecretKey getKey() throws NoSuchAlgorithmException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(128);
		SecretKey key = keyGenerator.generateKey();
		return key;
	}

	public static String signData(String data, PrivateKey key)
			throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
		Signature signature = Signature.getInstance("SHA1withRSA");
		signature.initSign(key);
		signature.update(data.getBytes());
		return Base64.encodeBase64String(signature.sign());
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
				+ "yN+PunBcP6T45MNBAHPlBAT01NFqECXGJ4sj/XpSFc5nyidoo0Cla5wfgeg8oFpb\r\n" + "WsPQ7NAQivugm42j8HkWXA==";

		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(priKey.getBytes()));
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		privateKey = keyFactory.generatePrivate(keySpec);
		return privateKey;
	}

	public static String encrypt(String data, byte[] key, byte[] initVector) throws Exception {
		IvParameterSpec iv = new IvParameterSpec(initVector);
		SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
		return Base64.encodeBase64String(cipher.doFinal(data.getBytes()));
	}

	private static String rsaEncrypt(byte[] data, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException,
			InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return Base64.encodeBase64String(cipher.doFinal(data));
	}

	public static PublicKey getPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
		String pubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCUocEmm0jQbFTboi7TdKD5IRfP\r\n"
				+ "fkVNNFgkxvsvGbo79mVibjmn5/J4JrS6MuWWZuY8OHbVcZyrBS9pVjydYxPK4/S6\r\n"
				+ "GvLm8gRn09XN9KThfKqPTZpIY8/MW8HL8LpETiKU1I4vb05fvaDwzd1mNPRKJ62U\r\n" + "tZBew98HEF8wSy+ecwIDAQAB";
		X509EncodedKeySpec spec = new X509EncodedKeySpec(Base64.decodeBase64(pubKey.getBytes()));
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePublic(spec);
	}

	public static HashMap<String, String> postHttpData(String url, String data, String key, String iv, String token,
			String partnerKey) throws Exception {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		StringEntity entity = new StringEntity(data);
		httpPost.setEntity(entity);
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-type", "application/json");
		httpPost.setHeader("token", token);
		httpPost.setHeader("key", key);
		httpPost.setHeader("partner", partnerKey);
		httpPost.setHeader("iv", iv);

		System.out.println(" token  " + token);
		System.out.println("  key  " + key);
		System.out.println("  partnerKey   " + partnerKey);
		System.out.println("  iv " + iv);

		CloseableHttpResponse response = client.execute(httpPost);
		HashMap hashMap = new HashMap<String, String>();
		if (response.getStatusLine().getStatusCode() == 200) {
			Arrays.stream(response.getAllHeaders()).forEach(a -> hashMap.put(a.getName(), a.getValue()));
			hashMap.put("body", EntityUtils.toString(response.getEntity()));
			System.out.println("got success response");
		} else {
			System.out.println("Got failure" + EntityUtils.toString(response.getEntity()));
		}
		client.close();
		return hashMap;
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

	public static boolean verifyData(String token, PublicKey key, String data)
			throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, UnsupportedEncodingException {
		Signature signature = Signature.getInstance("SHA1withRSA");
		signature.initVerify(key);
		signature.update(data.getBytes("UTF-8"));
		return signature.verify(Base64.decodeBase64(token));
	}

}
