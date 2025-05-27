package com.fidypay.ServiceProvider.AirtelPayments;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
@Service
public class JwtTokenGenerator {

	public  String generateJwtToken(String mid,String accessKey ) {
//		String mid = "MER0000002555782";
//		String accessKey = "a63607a4-27fd-45";
//		String vpa = "fidypay@appl";

		
//		String mid = "MER0000007174489";
//		String accessKey = "5989c99c-4dab-43";
	//	String vpa = "fidypay@appl";		
		
		String result = "";
		try {
			Date iat = new Date(System.currentTimeMillis());
			Date expiration = new Date(System.currentTimeMillis() + 5 * 60 * 1000);

			Map<String, Object> claims = new HashMap<>();
			claims.put("sub", "merchant-onboarding");
			claims.put("MID", mid);
			claims.put("apiServiceId", "externalVpaCreate");
			claims.put("iat", iat);
			claims.put("exp", expiration);

			result = doGenerateToken("merchant-onboarding", accessKey, claims);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;

	}

	@SuppressWarnings("deprecation")
	public String doGenerateToken(String userName, String accessKey, Map<String, Object> claims)
			throws NoSuchAlgorithmException {

		long jwtExpiryTime = 5 * 60L;
		long expiryTime = jwtExpiryTime * 1000;

		return Jwts.builder().setClaims(claims).setSubject(userName).setIssuedAt(convertToDate(LocalDateTime.now()))
				.setExpiration(new Date(System.currentTimeMillis() + expiryTime))
				.signWith(SignatureAlgorithm.HS256, getSHA256Key(accessKey)).compressWith(CompressionCodecs.GZIP)
				.compact();
	}

	private Date convertToDate(LocalDateTime localDateTime) {
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	private String getSHA256Key(String accessKey) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-512");
		md.update(accessKey.getBytes());
		byte[] byteData = md.digest();
		StringBuilder sb = new StringBuilder();
		for (byte element : byteData) {
			sb.append(Integer.toString((element & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}

}
