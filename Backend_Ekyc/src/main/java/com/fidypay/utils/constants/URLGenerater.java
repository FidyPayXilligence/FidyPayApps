package com.fidypay.utils.constants;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class URLGenerater {

	private static final Logger logger = LoggerFactory.getLogger(URLGenerater.class);


	public static String generateShortUrl(String inputUrl) {
		logger.info("*************generateShortUrl************");
		String shortUrl = null;

		/**
		 * If testing is regarding shortUrl then only make use of this class from
		 * localhost
		 */

		// for live
		logger.info("*************generateShortUrl************" + inputUrl);

		String apiUrl = "http://fdypy.in/yourls-api.php"; // http://192.168.117.152:82/url/yourls-api.php

		// for localhost
		// String apiUrl = "http://45.249.108.8:82/url/yourls-api.php";
		try {

			URL conUrl = new URL(apiUrl);
			HttpURLConnection conn = (HttpURLConnection) conUrl.openConnection();

			conn.setRequestMethod("POST");
			conn.setDoOutput(true);

			String parameters = "username=url&password=url@umoja1234&action=shorturl&title=ULB&format=json&url="
					+ inputUrl;

			DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
			wr.writeBytes(parameters);
			wr.flush();
			wr.close();

			int responseCode = conn.getResponseCode();
			logger.info("responseCode=" + responseCode);
			logger.info("response contenet length = " + conn.getContentLength());
			if (responseCode == 200) {
				BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line;
				StringBuffer responseBuffer = new StringBuffer();

				while ((line = in.readLine()) != null) {
					responseBuffer.append(line);
				}

				in.close();

				logger.info("response in string =" + responseBuffer.toString());
				JSONParser parser = new JSONParser();
				JSONObject jsonData = (JSONObject) parser.parse(responseBuffer.toString());
				logger.info("json string=" + jsonData.toJSONString());
				shortUrl = (String) jsonData.get("shorturl");
				logger.info("shortUrl=" + shortUrl);
				
				shortUrl	=  shortUrl.substring(4);
				logger.info("shortUrl 1 =" + shortUrl);

				shortUrl =  "https"+shortUrl;


			} else {
				logger.info("responseCode : " + responseCode);
			}

		} catch (Exception e) {
			
		}

		return shortUrl;
	}

	

	

	}

