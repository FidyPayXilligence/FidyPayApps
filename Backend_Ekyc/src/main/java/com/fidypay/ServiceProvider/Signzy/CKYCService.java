package com.fidypay.ServiceProvider.Signzy;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fidypay.utils.constants.ResponseMessage;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class CKYCService {

	private static final Logger LOGGER = LoggerFactory.getLogger(CKYCService.class);

	public static final String CKYC_SEARCH = "http://regtechapi.in/api/search";

	@SuppressWarnings("unchecked")
	public Map<String, Object> cKycAPI(@Valid String idNumber) {
		Map<String, Object> map = new HashMap<>();

		try {

			String requestStr = "{\r\n" + "  \"document_type\": \"PAN\",\r\n" + "    \"id_number\": \"" + idNumber
					+ "\",\r\n" + "    \"consent\": true,\r\n"
					+ "    \"consent_purpose\": \"Fidyasddfhbhjbjnknkknjbjgn\"\r\n" + "}";

			OkHttpClient client = new OkHttpClient();

			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, requestStr);
			Request request = new Request.Builder().url(ResponseMessage.CKYC_SEARCH_URL).post(body)
					.addHeader("Accept", "application/json").addHeader("Authorization", ResponseMessage.CKYC_TOKEN)
					.build();

			Response response = client.newCall(request).execute();
			String finalResponse = response.body().string();

			LOGGER.info("json request: " + requestStr);
			LOGGER.info("request: " + request);
			LOGGER.info("finalResponse: " + finalResponse);

			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> objectMap = objectMapper.readValue(finalResponse, Map.class);

			String kycStatus = (String) objectMap.get("kycStatus");

			String message = (String) objectMap.get("message");

			if (kycStatus != null && kycStatus.equals("SUCCESS")) {

				Map<String, Object> kycDetails = (Map<String, Object>) objectMap.get("kycDetails");

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, message);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put("kycDetails", kycDetails);
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, message);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			}

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> cKycDownloadAPI(@Valid String cKycId, String authFactor) {

		Map<String, Object> map = new HashMap<>();

		try {
			String requestStr = "{\r\n" + "    \"ckyc_id\": \"" + cKycId + "\",\r\n"
					+ "    \"auth_factor_type\": 1,\r\n" + "    \"consent\": true,\r\n"
					+ "    \"consent_purpose\": \"Fidyasddfhbhjbjn7knkknjbj7n\",\r\n" + "    \"auth_factor\": \""
					+ authFactor + "\"\r\n" + "}";

			LOGGER.info("requestStr: " + requestStr);

			OkHttpClient client = new OkHttpClient();

			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, requestStr);
			Request request = new Request.Builder().url(ResponseMessage.CKYC_DOWNLOAD_URL).post(body)
					.addHeader("Accept", "application/json").addHeader("Authorization", ResponseMessage.CKYC_TOKEN)
					.build();

			LOGGER.info("request: " + request);

			Response response = client.newCall(request).execute();
			String finalResponse = response.body().string();
			LOGGER.info("finalResponse: " + finalResponse);

			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> mapObject = objectMapper.readValue(finalResponse, Map.class);

			if (mapObject.containsKey("kycStatus")) {

				String kycStatus = (String) mapObject.get("kycStatus");
				String message = (String) mapObject.get("message");

				if (kycStatus.equals("SUCCESS")) {
					Map<String, Object> kycDetails = (Map<String, Object>) mapObject.get("kycDetails");

					map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
					map.put(ResponseMessage.DESCRIPTION, message);
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
					map.put("kycDetails", kycDetails);
				} else {
					LOGGER.error("message: {}", message);
					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.DESCRIPTION, message);
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				}
			} else {

				Map<String, Object> error = (Map<String, Object>) mapObject.get("error");
				LOGGER.error("error: {}", error);
				String errorMessage = (String) error.get("message");

				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, errorMessage);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			}

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;
	}

	public Map<String, Object> cKycAPIV2(@Valid String idNumber) {
		
		LOGGER.info("Inside cKycAPIV2 ------------->: ");
		Map<String, Object> map = new HashMap<>();

		try {

			String requestStr = "{\n\"pano\":\"" + idNumber
					+ "\",\n\"client_ref_num\":7856566,\n\"dob\":\"1400-02-12\",\n\"identifier_type\":\"AADH\"\n}";

			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, requestStr);
			Request request = new Request.Builder().url(CKYC_SEARCH).method("POST", body)
					.addHeader("AccessToken", "106c96e6f6567e6c99f8faddee91ad485b2")
					.addHeader("Content-Type", "application/json").build();
			Response response = client.newCall(request).execute();
			String finalResponse = response.body().string();

			LOGGER.info("json request: " + requestStr);
			LOGGER.info("request: " + request);
			LOGGER.info("finalResponse: " + finalResponse);

//			ObjectMapper objectMapper = new ObjectMapper();
//			Map<String, Object> objectMap = objectMapper.readValue(finalResponse, Map.class);

			JSONObject responseJson = new JSONObject(finalResponse);

			String statusCode = String.valueOf(responseJson.getLong("statusCode"));
			
			LOGGER.info("statusCode: " + statusCode);

//			String message = (String) objectMap.get("message");

			if (statusCode != null && statusCode.equals("200")) {

				JSONObject respJson = responseJson.getJSONObject("response");

				String message = respJson.getString("message");

				JSONObject kycDetails = respJson.getJSONObject("kycDetails");

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, message);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put("kycDetails", kycDetails.toMap());

			} else if (statusCode != null && statusCode.equals("102")) {

				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, "PAN Number is invalid! Please enter a valid PAN Number");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, "PAN Number is invalid");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			}

		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;
	}

}
