package com.fidypay.ServiceProvider.OneBharat;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fidypay.request.OneBharatRequest;
import com.fidypay.utils.constants.ResponseMessage;

@Service
public class OneBharatServices {

	private static final Logger log = LoggerFactory.getLogger(OneBharatServices.class);

	private static final String CLIENT_ID = "fidypay";

	// private static final String CLIENT_KEY =
	// "jHPrPc0fRpSlUxU0OvyoAZlFcBx5ZZr0QJtw1oeq/cM="; // UAT

	private static final String CLIENT_KEY = "loCu++mI0NKUWJmxUEoeLfew1+uBCvbX/VRBY3I9acI="; // Prod

	// private static final String BASE_URL = "https://api.staging.onebharat.tech";
	// //UAT

	private static final String BASE_URL = "https://api.onebharat.tech"; // Prod

	public static Map<String, Object> setResponse(String code, String status, String description) {
		Map<String, Object> map = new HashMap<>();
		map.put(ResponseMessage.CODE, code);
		map.put(ResponseMessage.STATUS, status);
		map.put(ResponseMessage.DESCRIPTION, description);

		return map;
	}

	private static Map<String, Object> castToObjectmapper(String response) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {
		});
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> castToMap(Object obj) {
		return (Map<String, Object>) obj;
	}

	public Map<String, Object> initiateConsent(OneBharatRequest oneBharatRequest, long merchantId, String merchantTrxnRefId) {

		Map<String, Object> map = new HashMap<>();
		try {

			Map<String, Object> genrateAuth = genrateAuth();
			String accessToken = (String) genrateAuth.get("accessToken");
			if (accessToken == null) {
				map = setResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED,
						(String) genrateAuth.get(ResponseMessage.DESCRIPTION));
				return map;
			}

			String apiRequest = "{\"clientId\":\"" + CLIENT_ID + "\",\"customerMobile\":\""
					+ oneBharatRequest.getCustomerMobile() + "\",\"customerName\":\""
					+ oneBharatRequest.getCustomerName() + "\",\"initiatorRef\":\""
					+ merchantTrxnRefId + "\",\"initiatorName\":\""
					+ oneBharatRequest.getInitiatorName() + "\",\"initiatorMobile\":\""
					+ oneBharatRequest.getInitiatorMobile() + "\"}";

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", accessToken);
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<String> entity = new HttpEntity<>(apiRequest, headers);
			ResponseEntity<String> response = restTemplate.exchange(BASE_URL + "/api/v1/consent/initiate",
					HttpMethod.POST, entity, String.class);

			String apiResponse = response.getBody();



			Map<String, Object> castToObjectmapper = castToObjectmapper(apiResponse);
			boolean status = (boolean) castToObjectmapper.get("success");
			if (status) {
				Map<String, Object> castToMap = castToMap(castToObjectmapper.get("data"));
				map = setResponse(ResponseMessage.SUCCESS, ResponseMessage.API_STATUS_SUCCESS,
						ResponseMessage.API_STATUS_SUCCESS);
				map.put("consentRef", castToMap.get("consentRef"));
				map.put("customerRef", castToMap.get("customerRef"));
				map.put("sessionKey", castToMap.get("sessionKey"));
				map.put("primeUrl", castToMap.get("primeUrl"));
			}
		} catch (HttpClientErrorException | HttpServerErrorException e) {

			String exceptionName = e.getClass().getSimpleName();
			String response = e.getResponseBodyAsString();

			map = setErrorResponse(response, exceptionName);
			log.error("Exception: {}", e);
		} catch (Exception e) {
			log.error("Exception: {}", e);
			map = setResponse(ResponseMessage.SOMETHING_WENT_WRONG, ResponseMessage.FAILED,
					ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		}
		return map;
	}

	public Map<String, Object> reportsByConsentId(String consentRefId) {

		Map<String, Object> map = new HashMap<>();

		try {

			if (!consentRefId.matches("^[a-zA-Z0-9-]{6,40}$")) {
				return setResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED,
						"please pass valid alphanumeric with one - special char consentRefId and size should be 6-40");
			}

			Map<String, Object> genrateAuth = genrateAuth();
			String accessToken = (String) genrateAuth.get("accessToken");
			if (accessToken == null) {
				map = setResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED,
						(String) genrateAuth.get(ResponseMessage.DESCRIPTION));
				return map;
			}

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", accessToken);
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<String> entity = new HttpEntity<>(headers);
			ResponseEntity<String> response = restTemplate.exchange(
					BASE_URL + "/api/v1/reports/byConsentId?consentId=" + consentRefId, HttpMethod.GET, entity,
					String.class);

			String apiResponse = response.getBody();
			
			log.info("apiResponse---------{}" , apiResponse);

			Map<String, Object> castToObjectmapper = castToObjectmapper(apiResponse);
			boolean status = (boolean) castToObjectmapper.get("success");
			if (status) {
				Map<String, Object> castToMap = castToMap(castToObjectmapper.get("data"));
				Map<String, Object> meta = castToMap(castToMap.get("meta"));
				if(meta == null) {
					map = setResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED,
							ResponseMessage.DATA_NOT_FOUND);
					return map;
				}
				map = setResponse(ResponseMessage.SUCCESS, ResponseMessage.API_STATUS_SUCCESS,
						ResponseMessage.API_STATUS_SUCCESS);
				map.put("aa_consent_id", meta.get("aa_consent_id"));
				map.put("report_ts", meta.get("report_ts"));
				map.put("txn_id", meta.get("txn_id"));
				map.put("aa_handle", meta.get("aa_handle"));
				Map<String, Object> bankDetail = castToMap(castToMap.get("bank_account"));
				map.put("bank_account", bankDetail);
				Map<String, Object> balances = castToMap(castToMap.get("balances"));
				map.put("balances", balances);
			}
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			String exceptionName = e.getClass().getSimpleName();
			String response = e.getResponseBodyAsString();
			log.error("Exception: {}", e);
			map = setErrorResponse(response, exceptionName);

		} catch (Exception e) {
			log.error("Exception: {}", e);
			map = setResponse(ResponseMessage.SOMETHING_WENT_WRONG, ResponseMessage.API_STATUS_FAILED,
					ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		}
		return map;
	}

//	This api is used to generate auth token which is used for access the API
	private Map<String, Object> genrateAuth() {
		Map<String, Object> map = new HashMap<>();
		try {

			String apiRequest = "{\"clientId\":\"" + CLIENT_ID + "\",\"clientKey\":\"" + CLIENT_KEY + "\"}";
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<String> entity = new HttpEntity<>(apiRequest, headers);
			ResponseEntity<String> response = restTemplate.exchange(BASE_URL + "/api/v1/auth", HttpMethod.POST, entity,
					String.class);
			String apiResponse = response.getBody();
			log.info("apiResponse: {}", apiResponse);

			Map<String, Object> castToObjectmapper = castToObjectmapper(apiResponse);
			boolean status = (boolean) castToObjectmapper.get("success");
			if (status) {
				Map<String, Object> data = castToMap(castToObjectmapper.get("data"));
				map = setResponse(ResponseMessage.SUCCESS, ResponseMessage.API_STATUS_SUCCESS,
						ResponseMessage.API_STATUS_SUCCESS);
				map.put("accessToken", data.get("accessToken"));
			}

		} catch (HttpClientErrorException | HttpServerErrorException e) {
			String exceptionName = e.getClass().getSimpleName();
			String response = e.getResponseBodyAsString();
			log.error("Exception: {}", e);
			map = setErrorResponse(response, exceptionName);

		} catch (Exception e) {
			log.error("Exception: {}", e);
			map = setResponse(ResponseMessage.SOMETHING_WENT_WRONG, ResponseMessage.API_STATUS_FAILED,
					ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		}
		return map;
	}

	private Map<String, Object> setErrorResponse(String resonse, String exceptionName) {
		try {
			if (exceptionName.equals("ServiceUnavailable")) {
				return setResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED,
						"Service Temporarily Unavailable");
			}
			Map<String, Object> error = castToObjectmapper(resonse);
			String message = (String) error.get("errorMessage");
			message = message == null ? (String) error.get("error") : message;
			return setResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED, message);
		} catch (Exception e) {
			log.error("Exception: {}", e);
		}
		return Collections.emptyMap();
	}

}
