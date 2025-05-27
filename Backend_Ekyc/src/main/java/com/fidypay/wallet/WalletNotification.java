package com.fidypay.wallet;

import java.io.IOException;
import java.text.DecimalFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fidypay.encryption.Encryption;
import com.fidypay.utils.constants.ResponseMessage;

@Service
public class WalletNotification {

	private static final Logger LOGGER = LoggerFactory.getLogger(WalletNotification.class);

	public String checkWalletBalance(double wallet, String bussinessName, String email) {
        String res = null;
		try {

			bussinessName = Encryption.decString(bussinessName);
			email = Encryption.decString(email);

			DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");
			amount1.setMinimumIntegerDigits(1);

			String am = amount1.format(wallet);

			if (wallet <= 1000) {
                return sendEmailForLowWalletBalance(bussinessName, email, am);
			}

			res = "Failed";

		} catch (Exception e) {
			LOGGER.error("Exception{}", e);
			e.printStackTrace();
		}
		return res;
	}

	public static String sendEmailForLowWalletBalance(String bussinessName, String email, String amount)
			throws IOException {

		String responseBody = "NA";
		try {

			String requestBody = "{\"businessName\": \"" + bussinessName + "\", \"email\": \"" + email
					+ "\",\"optional1\": \"" + amount + "\", \"optional2\": \"" + ResponseMessage.MERCHANT_ENVIRONMENT
					+ "\"}";

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
			HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
			String url = "https://api.fidypay.com/notification/emailNotification/sendEmailNotificationForLowWalletBalance";
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
			// HttpStatus statusCode = responseEntity.getStatusCode();
			responseBody = responseEntity.getBody();
		} catch (Exception e) {
			LOGGER.error("Exception{}", e);
			responseBody = e.getMessage();
		}
		return responseBody;
	}

}
