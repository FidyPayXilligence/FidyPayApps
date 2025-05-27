package com.fidypay.utils.ex;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class SMSAPIImpl {

	private static final Logger LOGGER = LoggerFactory.getLogger(SMSAPIImpl.class);

	public String registrationOTP(String mobiles, String merchantName, String otp) throws IOException {
		String responseBody = "NA";
		try {
			String requestBody = "{ \"mobile\": \"" + mobiles + "\", \"otp\": \"" + otp + "\", \"p1\": \""
					+ merchantName + "\", \"p2\": \"0\", \"p3\": \"0\"}";

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
			headers.set("accept", "*/*");
			headers.set("smsToken", "3708302484886346196");
			HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
			String url = "https://api.fidypay.com/notification/sendNotification";
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
//			HttpStatus statusCode = responseEntity.getStatusCode();
			responseBody = responseEntity.getBody();
		} catch (Exception e) {
			responseBody = e.getMessage();
		}
		LOGGER.info("responseBody " + responseBody);
		return responseBody;
	}

	public String workFlowLink(String mobiles, String businessName, String link) throws IOException {
		String responseBody = "NA";
		try {
			String requestBody = "{\r\n" + "    \"mobile\": \"" + mobiles + "\",\r\n" + "    \"otp\": \"string\",\r\n"
					+ "    \"p1\": \"" + businessName + "\",\r\n" + "    \"p2\": \"" + link + "\",\r\n"
					+ "    \"p3\": \"string\"\r\n" + "}";

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
			headers.set("accept", "*/*");
			headers.set("smsToken", "0122161208934346164");
			HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
			String url = "https://api.fidypay.com/notification/sendNotification";
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
//			HttpStatus statusCode = responseEntity.getStatusCode();
			responseBody = responseEntity.getBody();
		} catch (Exception e) {
			responseBody = e.getMessage();
		}
		LOGGER.info("responseBody " + responseBody);
		return responseBody;
	}

	public String upiKyc(String mobiles, String businessName, String shortUrl) throws IOException {
		String responseBody = "NA";
		try {
			
			String requestBody = "{ \"mobileNo\": \"" + mobiles + "\", \"p1\": \"" + businessName + "\", \"p2\": \""
					+ shortUrl + "\"}";

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
			headers.set("accept", "*/*");
			headers.set("smsToken", "3208870629174622513");
			HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
			String url = "https://api.fidypay.com/notification/sendSMSNotificationForUPIkycVerification";
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
			responseBody = responseEntity.getBody();
		} catch (Exception e) {
			responseBody = e.getMessage();
		}
		LOGGER.info("responseBody " + responseBody);
		return responseBody;
	}

}
