package com.fidypay.ServiceProvider.Karza;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fidypay.utils.constants.ResponseMessage;

@Service
public class ESignServices {

	private static final Logger log = LoggerFactory.getLogger(ESignServices.class);

	private static final String WORKFLOW_ID = "djUCUHX";
	private static final String ESIGN_URL = "https://testapi.karza.in/v3/esign-session";
	private static final String X_KARZA_KEY = "NmXy370lZytA27VA";

	private static final String E_SIGN_DOCUMENT_URL = "https://testapi.karza.in/v3/esign-document";

	private static final String E_SIGN_DELETE_DOCUMENT_URL = "https://testapi.karza.in/v3/esign-delete";

	private static final String GST_SEARCH_BASIS_PAN_URL = "https://api.karza.in/gst/uat/v2/search";

	private static final String DOCUMENT_ID_REGEX = "^[a-zA-Z0-9]+$";

	public static Map<String, Object> castToObjectmapper(String response) throws JsonProcessingException {
		TypeReference<Map<String, Object>> typeReferance = new TypeReference<Map<String, Object>>() {
		};
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(response, typeReferance);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> castToMap(Object obj) {
		return (Map<String, Object>) obj;
	}

	public static Map<String, Object> setResponse(String code, String status, String description) {
		Map<String, Object> map = new HashMap<>();
		map.put(ResponseMessage.CODE, code);
		map.put(ResponseMessage.STATUS, status);
		map.put(ResponseMessage.DESCRIPTION, description);

		return map;
	}

	public static Map<String, Object> validate(String documentId) {

		Map<String, Object> map = new HashMap<>();

		if (!documentId.matches(DOCUMENT_ID_REGEX)) {

			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DOCUMENT_ID_NOT_VALID);
			return map;
		}

		return map;
	}

	public Map<String, Object> getAuthenticateForESign(String name, String email, String document,
			String merchantTrxnRefId) {
		Map<String, Object> map = new HashMap<>();
		try {
			String apiRequest = "{\"name\":\"" + name + "\",\"email\":\"" + email + "\",\"workflowId\":\"" + WORKFLOW_ID
					+ "\",\"document\":\"" + document + "\"}";
			log.info("apiRequest: {}", apiRequest);

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("x-karza-key", X_KARZA_KEY);

			HttpEntity<String> entity = new HttpEntity<>(apiRequest, headers);
			ResponseEntity<String> response = restTemplate.exchange(ESIGN_URL, HttpMethod.POST, entity, String.class);
			String apiResponse = response.getBody();
			log.info("apiResponse: {}", apiResponse);
			Map<String, Object> objectMapper = ESignServices.castToObjectmapper(apiResponse);
			if ((int) objectMapper.get("statusCode") == 101) {
				Map<String, Object> result = ESignServices.castToMap(objectMapper.get("result"));
				map = ESignServices.setResponse(ResponseMessage.SUCCESS, ResponseMessage.API_STATUS_SUCCESS,
						ResponseMessage.DATA_SUCCESS);
				map.put("merchantTxnRefId", merchantTrxnRefId);
				map.put("documentId", result.get("documentId"));
				map.put("signUrl", result.get("signUrl"));
				map.put("expiryDate", result.get("expiryDate"));
				return map;
			}

			map = ESignServices.setResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED,
					"Failed to validate the document.");
			map.put("merchantTxnRefId", merchantTrxnRefId);
		} catch (Exception e) {
			log.error("Exception: {}", e);
			map = ESignServices.setResponse(ResponseMessage.SOMETHING_WENT_WRONG, ResponseMessage.API_STATUS_FAILED,
					ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		}
		return map;
	}

	public Map<String, Object> getEsignDocument(String documentId, String merchantTrxnRefId) {

		Map<String, Object> map = new HashMap<>();

		try {
			String apiRequest = "{\"documentId\":\"" + documentId + "\",\"verificationDetailsRequired\":\"Y\"}";

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("x-karza-key", X_KARZA_KEY);

			HttpEntity<String> entity = new HttpEntity<>(apiRequest, headers);
			ResponseEntity<String> response = restTemplate.exchange(E_SIGN_DOCUMENT_URL, HttpMethod.POST, entity,
					String.class);
			String apiResponse = response.getBody();

			JSONObject resultJsonObject = new JSONObject(apiResponse);

			log.info("apiResponse: {}", apiResponse);

			if ((int) resultJsonObject.getInt("statusCode") == 101) {

				map = ESignServices.setResponse(ResponseMessage.SUCCESS, ResponseMessage.API_STATUS_SUCCESS,
						ResponseMessage.DATA_SUCCESS);
				map.put("merchantTxnRefId", merchantTrxnRefId);
				JSONObject resultJson = resultJsonObject.getJSONObject("result");

				map.put("verificationResult", resultJson.getJSONArray("verificationDetails").toList());

				return map;

			}

			map = ESignServices.setResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED,
					ResponseMessage.DATA_NOT_FOUND);
			map.put("merchantTxnRefId", merchantTrxnRefId);

		} catch (Exception e) {
			log.error("Exception: {}", e);
			map = ESignServices.setResponse(ResponseMessage.SOMETHING_WENT_WRONG, ResponseMessage.API_STATUS_FAILED,
					ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		}
		return map;
	}

	public Map<String, Object> deleteEsignDocument(String documentId, String merchantTrxnRefId) {

		Map<String, Object> map = new HashMap<>();

		try {
			String apiRequest = "{\r\n    \"documentId\": \"" + documentId + "\"\r\n}";

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("x-karza-key", X_KARZA_KEY);

			HttpEntity<String> entity = new HttpEntity<>(apiRequest, headers);
			ResponseEntity<String> response = restTemplate.exchange(E_SIGN_DELETE_DOCUMENT_URL, HttpMethod.POST, entity,
					String.class);
			String apiResponse = response.getBody();

			JSONObject resultJsonObject = new JSONObject(apiResponse);

			log.info("apiResponse: {}", apiResponse);

			if ((int) resultJsonObject.getInt("statusCode") == 101) {
				JSONObject result = resultJsonObject.getJSONObject("result");
				map = ESignServices.setResponse(ResponseMessage.SUCCESS, ResponseMessage.API_STATUS_SUCCESS,
						result.getString("status"));
				map.put("merchantTxnRefId", merchantTrxnRefId);
				return map;

			}

			map = ESignServices.setResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED,
					ResponseMessage.DATA_NOT_FOUND);
			map.put("merchantTxnRefId", merchantTrxnRefId);

		} catch (Exception e) {
			log.error("Exception: {}", e);
			map = ESignServices.setResponse(ResponseMessage.SOMETHING_WENT_WRONG, ResponseMessage.API_STATUS_FAILED,
					ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		}
		return map;
	}

	public Map<String, Object> GSTSearchBasisPAN(String panNo, String merchantTrxnRefId) {

		Map<String, Object> map = new HashMap<>();

		try {
			String apiRequest = "{\r\n  \"consent\": \"Y\",\r\n  \"pan\": \"" + panNo
					+ "\",\r\n  \"clientData\": {\r\n    \"caseId\": \"123456\"\r\n  }\r\n}";

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("x-karza-key", X_KARZA_KEY);

			HttpEntity<String> entity = new HttpEntity<>(apiRequest, headers);
			ResponseEntity<String> response = restTemplate.exchange(GST_SEARCH_BASIS_PAN_URL, HttpMethod.POST, entity,
					String.class);
			String apiResponse = response.getBody();

			JSONObject resultJsonObject = new JSONObject(apiResponse);

			log.info("apiResponse: {}", apiResponse);

			if ((int) resultJsonObject.getInt("statusCode") == 101) {
				JSONArray result = resultJsonObject.getJSONArray("result");
				map = ESignServices.setResponse(ResponseMessage.SUCCESS, ResponseMessage.API_STATUS_SUCCESS,
						ResponseMessage.DATA_SUCCESS);
				map.put("merchantTxnRefId", merchantTrxnRefId);
				map.put("Data", result.toList());
				return map;

			}

			map = ESignServices.setResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED,
					ResponseMessage.API_STATUS_FAILED);
			map.put("merchantTxnRefId", merchantTrxnRefId);

		} catch (Exception e) {
			log.error("Exception: {}", e);
			map = ESignServices.setResponse(ResponseMessage.SOMETHING_WENT_WRONG, ResponseMessage.API_STATUS_FAILED,
					ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		}
		return map;
	}

}
