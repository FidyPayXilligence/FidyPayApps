package com.fidypay.ServiceProvider.Signzy;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fidypay.request.TANRequest;
import com.fidypay.utils.constants.ResponseMessage;

@Service
public class TANVerificationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(TANVerificationService.class);

	@Autowired
	private SignzyService signzyService;

	public Map<String, Object> tanVerification(TANRequest tanRequest) throws JsonProcessingException {

		Map<String, Object> map = new HashMap<>();

		TypeReference<Map<String, Object>> typeReference = new TypeReference<Map<String, Object>>() {
		};
		ObjectMapper objectMapper = new ObjectMapper();
		LOGGER.info("tanRequest: {}", LOGGER);
		try {
			String login = signzyService.login(SignzyService.USERNAME, SignzyService.PASSWORD);
			LOGGER.info("login: {}", login);

			Map<String, Object> readValue = objectMapper.readValue(login, typeReference);
			if (!readValue.containsKey("id")) {
				return setResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED,
						ResponseMessage.TRY_AFTER_SOMETIME);
			}

			LOGGER.info("Authorization: {}", readValue.get("id"));
			LOGGER.info("patronid: {}", readValue.get("userId"));

			String url = SignzyService.BASE_URL + (String) readValue.get("userId") + "/tans";
			LOGGER.info("url: {}", url);

			String apiRequest = "{\"essentials\":{\"companyName\":\"" + tanRequest.getCompanyName() + "\",\"tan\":\""
					+ tanRequest.getTanNumber() + "\"}}";
			LOGGER.info("apiRequest: {}", apiRequest);

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("Authorization", (String) readValue.get("id"));
			HttpEntity<String> entity = new HttpEntity<>(apiRequest, headers);
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
			String apiResponse = response.getBody();
			LOGGER.info("ApiResponse: {}", apiResponse);
			Map<String, Object> readApiValue = objectMapper.readValue(apiResponse, typeReference);
			Map<String, Object> result = castMap(readApiValue.get("result"));
			boolean verified = (boolean) result.get("verified");
			if (!verified) {

				map = setResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED,
						(String) result.get("message"));
				map.put("verified", verified);
				return map;
			}

			map = setResponse(ResponseMessage.SUCCESS, ResponseMessage.API_STATUS_SUCCESS,
					(String) result.get("message"));
			map.put("verified", verified);

		} catch (HttpClientErrorException e) {
			LOGGER.error("HttpClientErrorException: {}", e);
			String responseBody = e.getResponseBodyAsString();
			Map<String, Object> readValue = objectMapper.readValue(responseBody, typeReference);
			Map<String, Object> error = castMap(readValue.get("error"));
			LOGGER.error("apiresponse: {}", readValue);
			int errorcode = (int) error.get("status");
			if (errorcode == 409) {
				map = setResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED, "Invalid tanNumber");
			} else {
				map = setResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED,
						(String) error.get("message"));
			}
		} catch (Exception e) {
			LOGGER.error("Exception: {}", e);
			map = setResponse(ResponseMessage.SOMETHING_WENT_WRONG, ResponseMessage.API_STATUS_FAILED,
					ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		}
		return map;

	}

	public static Map<String, Object> setResponse(String code, String status, String description) {
		Map<String, Object> map = new HashMap<>();
		map.put(ResponseMessage.CODE, code);
		map.put(ResponseMessage.STATUS, status);
		map.put(ResponseMessage.DESCRIPTION, description);

		return map;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> castMap(Object obj) {
		return (Map<String, Object>) obj;
	}

}
