package com.fidypay.utils.ex;

import java.io.IOException;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EmailAPIImpl {

	public static String sendEmailForMerchantOnboarding(String email, String name, String onboardingType,
			String password, String urlOnboarding, String username, String environment) throws IOException {

		String responseBody = "NA";

		try {
			String requestBody = "{\r\n" + "	\"email\": \"" + email + "\",\r\n" + "	\"environment\": \""
					+ environment + "\",\r\n" + "	\"name\": \"" + name + "\",\r\n" + "	\"onboardingType\": \""
					+ onboardingType + "\",\r\n" + "	\"password\": \"" + password + "\",\r\n" + "	\"url\": \""
					+ urlOnboarding + "\",\r\n" + "	\"username\": \"" + username + "\"\r\n" + "}";

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
			HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
			String url = "https://api.fidypay.com/notification/emailNotification/sendEmailNotificationForProductionOnboarding";
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
//HttpStatus statusCode = responseEntity.getStatusCode();
			responseBody = responseEntity.getBody();
		} catch (Exception e) {
			responseBody = e.getMessage();
		}
		return responseBody;
	}

}
