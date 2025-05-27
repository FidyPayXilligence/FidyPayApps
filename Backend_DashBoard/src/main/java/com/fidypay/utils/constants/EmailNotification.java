package com.fidypay.utils.constants;

import java.io.IOException;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class EmailNotification {

	public String sendEmailNotificationHelpAndSupport(String businessName, String email, String reason, String message,
			String merchantEmail, String merchantMobile) throws IOException {
		String responseBody = "NA";
		try {
			String requestBody = "{\r\n" + " \"accountManagerName\": \"string\",\r\n" + " \"businessName\": \""
					+ businessName + "\",\r\n" + " \"email\": \"" + email + "\",\r\n" + " \"link\": \"" + reason
					+ "\",\r\n" + " \"optional1\": \"" + message + "\",\r\n" + " \"optional2\": \"" + merchantEmail
					+ "\",\r\n" + " \"optional3\": \"" + merchantMobile + "\",\r\n" + " \"otp\": \"string\"\r\n" + "}";

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
			HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
			String url = "https://api.fidypay.com/notification/emailNotification/sendEmailNotificationHelpAndSupport";
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
			responseBody = responseEntity.getBody();
		} catch (Exception e) {
			responseBody = e.getMessage();
		}
		return responseBody;
	}

	public String sendEmailNotificationServiceSubscribe(String businessName, String merchantEmail,
			String merchantMobile, String productName) throws IOException {
		String responseBody = "NA";
		try {
			String email = "helpdesk@fidypay.com";
			String requestBody = "{\n" + "    \"businessName\": \"" + businessName + "\",\n" + "    \"email\": \""
					+ email + "\",\n" + "    \"optional1\": \"" + merchantMobile + "\",\n" + "    \"optional2\": \""
					+ productName + "\",\n" + "    \"optional3\": \"" + merchantEmail + "\"\n" + "}";

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
			HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
			String url = "https://api.fidypay.com/notification/emailNotification/sendEmailNotificationForServiceSubscribe";
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
			responseBody = responseEntity.getBody();
		} catch (Exception e) {
			responseBody = e.getMessage();
		}
		return responseBody;
	}

	public static String sendEmailForForgetPassword(String email, String urlForgot) throws IOException {
		String responseBody = "NA";
		try {
			String requestBody = "{\n" + " \"email\": \"" + email + "\",\n" + " \"url\": \"" + urlForgot + "\"\n" + "}";
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
			HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
			String url = "https://api.fidypay.com/notification/emailNotification/sendEmailForForgetPassword";
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
			responseBody = responseEntity.getBody();
		} catch (Exception e) {
			responseBody = e.getMessage();
		}
		return responseBody;
	}

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

	public String sendEmailNotificationLoginAttempt(String name, String email, String date, String attempt,
			String unSuccessfulLogin) throws IOException {
		String responseBody = "NA";
		try {
			String requestBody = "{\r\n" + "    \"attempt\": \"" + attempt + "\",\r\n" + "    \"date\": \"" + date
					+ "\",\r\n" + "    \"email\": \"" + email + "\",\r\n" + "    \"name\": \"" + name + "\",\r\n"
					+ "    \"unSuccessfulLogin\": \"" + unSuccessfulLogin + "\"\r\n" + "}";

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
			HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
			String url = "https://api.fidypay.com/notification/emailNotification/sendEmailForFailedAttemptLogin";
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
			responseBody = responseEntity.getBody();
		} catch (Exception e) {
			responseBody = e.getMessage();
		}
		return responseBody;
	}

	public String sendEmailNotificationLoginAttemptBlocked(String name, String email, String date) {
		String responseBody = "NA";
		try {
			String requestBody = "{\r\n" + "    \"date\": \"" + date + "\",\r\n" + "    \"email\": \"" + email
					+ "\",\r\n" + "    \"name\": \"" + name + "\"\r\n" + "}";

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
			HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
			String url = "https://api.fidypay.com/notification/emailNotification/sendEmailForFailedAttemptToBlocked";
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
			responseBody = responseEntity.getBody();
		} catch (Exception e) {
			responseBody = e.getMessage();
		}
		return responseBody;
	}

	// UPI Onboarding
	public static String sendEmailForUPIOnboardingKyc(String email, String shortUrl, String merchantBusinessName,
			String subMerchantName, String logo) throws IOException {
		String responseBody = "NA";
		try {
			String requestBody = "{\"businessName\": \"" + merchantBusinessName + "\", \"email\": \"" + email
					+ "\", \"link\": \"" + shortUrl + "\", \"optional1\": \"" + subMerchantName
					+ "\", \"optional2\": \"" + logo + "\"}";

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
			HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
			String url = "https://api.fidypay.com/notification/emailNotification/sendEmailForUpiOnboarding";
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
			responseBody = responseEntity.getBody();
		} catch (Exception e) {
			responseBody = e.getMessage();
		}
		return responseBody;
	}

	public static String sendEmailForQRCodeGeneration(String email, String merchantBusinessName, String subMerchantName,
			String logo, String subMerchantBusinessName, String upiId, String varticles, String merchantEmailCC)
			throws IOException {
		String responseBody = "NA";
		try {

			String requestBody = "{\r\n" + "  \"customerBusinessName\": \"" + subMerchantBusinessName + "\",\r\n"
					+ "  \"customerName\": \"" + subMerchantName + "\",\r\n" + "  \"email\": \"" + email + "\",\r\n"
					+ "  \"logo\": \"" + logo + "\",\r\n" + "  \"merchantBusinessName\": \"" + merchantBusinessName
					+ "\",\r\n" + "  \"merchantEmailCC\": \"" + merchantEmailCC + "\",\r\n" + "  \"upiId\": \"" + upiId
					+ "\",\r\n" + "  \"varticles\": \"" + varticles + "\"\r\n" + "}";

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
			HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
			String url = "https://api.fidypay.com/notification/emailNotification/sendEmailForQRCodeGeneration";
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
			responseBody = responseEntity.getBody();
		} catch (Exception e) {
			responseBody = e.getMessage();
		}
		return responseBody;
	}

	public static String sendEmailForDocumentRejectCase(String adminDashboardLink, String customerEmail,
			String customerMobile, String subMerchantName, String subMerchantBusinessName, String failedReason,
			String typeofDocumentSubmitted) throws IOException {
		String responseBody = "NA";
		try {
			String requestBody = "{ \"adminDashboardLink\": \"" + adminDashboardLink
					+ "\", \"customerBusinessName\": \"" + subMerchantBusinessName + "\", \"customerEmail\": \""
					+ customerEmail + "\", \"customerMobile\": \"" + customerMobile + "\", \"customerName\": \""
					+ subMerchantName + "\", \"failedReason\": \"" + failedReason
					+ "\", \"typeofDocumentSubmitted\": \"" + typeofDocumentSubmitted + "\"}";

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
			HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
			String url = "https://api.fidypay.com/notification/emailNotification/sendEmailForUpiOnboardingKycRejectCase";
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
			responseBody = responseEntity.getBody();
		} catch (Exception e) {
			responseBody = e.getMessage();
		}
		return responseBody;
	}

	public static String sendEmailNotificationForPreChagres(String content) {
		String responseBody = "NA";
		String email = "arpan.sethiya@fidypay.com";
		content = "Product Name : Dashboard  " + content;
		System.out.println("content " + content);
		try {
			String requestBody = "{\r\n" + "  \"email\": \"arpan.sethiya@fidypay.com\",\r\n"
					+ "  \"optional1\": \"fsdfjdsfsd fsdfsf sdffs  f\"\r\n" + "  }";
			System.out.println("requestBody " + requestBody);
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
			HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
			String url = "https://api.fidypay.com/notification/emailNotification/sendEmailException";
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
			responseBody = responseEntity.getBody();
		} catch (Exception e) {
			responseBody = e.getMessage();
		}
		return responseBody;
	}

	public static String sendEmailNotificationForDashboardOTP(String merchantName, String otp, String email,
			String mobileNo) {
		String responseBody = "NA";
		try {
			String requestBody = "{\r\n" + "  \"accountManagerName\": \"" + merchantName + "\",\r\n" + "  \"email\": \""
					+ email + "\",\r\n" + "  \"optional1\": \"" + mobileNo + "\",\r\n" + "  \"otp\": \"" + otp
					+ "\"\r\n" + "}";
			System.out.println("requestBody " + requestBody);
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
			HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
			String url = "https://api.fidypay.com/notification/emailNotification/sendEmailNotificationLoginDashboard";
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
			responseBody = responseEntity.getBody();
		} catch (Exception e) {
			responseBody = e.getMessage();
		}
		return responseBody;
	}

	public static String sendEmailNotificationForPasswordExpire(String merchantName, String link, String email,
			String days) {
		String responseBody = "NA";
		try {
			String requestBody = "{\r\n" + "  \"accountManagerName\": \"" + merchantName + "\",\r\n" + "  \"email\": \""
					+ email + "\",\r\n" + "  \"link\": \"" + link + "\",\r\n" + "  \"optional1\": \"" + days + "\"\r\n"
					+ "}";

			System.out.println("requestBody " + requestBody);
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
			HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
			String url = "https://api.fidypay.com/notification/emailNotification/sendEmailNotificationPasswordExpire";
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
			responseBody = responseEntity.getBody();
		} catch (Exception e) {
			responseBody = e.getMessage();
		}
		return responseBody;
	}
}
