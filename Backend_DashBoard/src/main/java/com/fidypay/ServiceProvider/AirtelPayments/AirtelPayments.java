package com.fidypay.ServiceProvider.AirtelPayments;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fidypay.utils.constants.ResponseMessage;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class AirtelPayments {

	private final Logger log = LoggerFactory.getLogger(AirtelPayments.class);

	@Autowired
	private JwtTokenGenerator jwtTokenGenerator;

	@Autowired
	private AESCryptoV2Utils encryptionService;

	//private static final String SUB_MERCHANT_ONBOARDING = "https://apbuat.airtelbank.com/onboarding/external/vpa/create";
	



//	Production Credentials
	private static final String VALIDATE_VPA_URL = "https://merchantupi.airtelbank.com:5055/apb/merchant-upi-service/upivaladd";
	private static final String RAISE_COLLECT_URL = "https://merchantupi.airtelbank.com:5055/apb/merchant-upi-service/upimercollect";
	private static final String TRANSACTION_ENQUIRY_URL = "https://merchantupi.airtelbank.com:5055/apb/merchant-upi-service/upitxnenquiry";
	private static final String RAISE_REFUND_URL = "https://merchantupi.airtelbank.com:5055/apb/merchant-upi-service/upitxnrefund";
	private static final String MERCHANT_SECRET_KEY = "5989c99c-4dab-43";
	private static final String MID_PROD = "MER0000007174489";
	private static final String SUB_MERCHANT_ONBOARDING ="https://merchantupi.airtelbank.com:5055/apb/merchant-onb-service/external/vpa/create";


	public Map<String, Object> subMerchantOnBoarding(String jsonData, String MID, String accessKey,
			String SUB_MERCHANT_ONBOARDING) throws IOException {

		log.info("Inside subMerchantOnBoarding: {}", jsonData);

		Map<String, Object> map = new HashMap<>();

		JSONObject json = new JSONObject();

		// String MID = "a63607a4-27fd-45";
		try {

			log.info("Service Request to merchantOnBoarding: {}", jsonData);
			log.info("MID: {}", MID);

			String token = jwtTokenGenerator.generateJwtToken(MID, accessKey);

			String encryptedData = encryptionService.encryptString(jsonData, accessKey);

			json.put("data", encryptedData);

			log.info("token: {}", token);

			log.info("Request: {}", json.toString());

			OkHttpClient client = new OkHttpClient().newBuilder().build();

			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, json.toString());

			Request request = new Request.Builder().url(SUB_MERCHANT_ONBOARDING).method("POST", body)
					.addHeader("Authorization", token).addHeader("MID", MID)
					.addHeader("Content-Type", "application/json").build();

			Response response1 = client.newCall(request).execute();

			log.info("AIRTEL PAYMENT BANK SUB MERCHANT ON-BOARDING API response1 : {}", response1);

			String apiResponse = response1.body().string();
			JSONObject jsonObject = new JSONObject(apiResponse);
			String data = jsonObject.getString("data");

			log.info(" data : {}", data);

			String rsp = encryptionService.decryptString(data, accessKey);
			log.info(" rsp : {}", rsp);

		} catch (Exception ex) {

			ex.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		}
		return map;
	}

	// @Override
	public Map<String, Object> validateCustomerVPA(String vpaRequest, String url) throws IOException {

		log.info("Inside validateCustomerVPA: {}", vpaRequest);

		Map<String, Object> map = new HashMap<>();

		JSONObject json = new JSONObject(vpaRequest);
		try {
			String ver = json.getString("ver");
			String hdnOrderID = json.getString("hdnOrderID");
			String mid = json.getString("mid");
			String merchantVpa = json.getString("merchantVpa");
			String customerVpa = json.getString("customerVpa");

			String vpaHashRequest = ver + "#" + hdnOrderID + "#" + mid + "#" + merchantVpa + "#" + customerVpa + "#"
					+ MERCHANT_SECRET_KEY;

			String generatedHash = encryptionService.generateHash(vpaHashRequest);

			log.info("generatedHash: {}", generatedHash);

			String validateVPARequest = "{\r\n" + "  \"ver\": \"" + ver + "\",\r\n" + "  \"hdnOrderID\": \""
					+ hdnOrderID + "\",\r\n" + "  \"mid\": \"" + mid + "\",\r\n" + "  \"merchantVpa\": \"" + merchantVpa
					+ "\",\r\n" + "  \"customerVpa\": \"" + customerVpa + "\",\r\n" + "  \"hash\": \"" + generatedHash
					+ "\"\r\n" + "}";

			log.info("validateVPARequest: {}", validateVPARequest);

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

			HttpEntity<String> entity = new HttpEntity<>(validateVPARequest, headers);
			ResponseEntity<String> response = restTemplate.exchange(VALIDATE_VPA_URL, HttpMethod.POST, entity,
					String.class);
			String apiResponse = response.getBody();

			log.info("apiResponse: {}", apiResponse);

			JSONObject resultJsonObject = new JSONObject(apiResponse);

			String messageText = resultJsonObject.getString("messageText");

			if (messageText.equalsIgnoreCase("SUCCESS")) {

				String name = resultJsonObject.getString("name");

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_FOUND);
				map.put("name", name);
				return map;

			} else if (messageText.equalsIgnoreCase("FAILED")) {

				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
				return map;
			}

		} catch (HttpClientErrorException e) {

			String responeString = e.getResponseBodyAsString();
			log.error("Exception: {}", responeString);

			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);

		}

		catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		}

		return map;
	}

	public Map<String, Object> raiseCollect(String getRequest, String url) throws IOException {

		log.info("Inside raiseCollect: {}", getRequest);
		Map<String, Object> map = new HashMap<>();

		JSONObject json = new JSONObject(getRequest);
		try {
			String amount = json.getString("amount");
			String feSessionId = json.getString("feSessionId");
			String hdnOrderID = json.getString("hdnOrderID");
			String mid = json.getString("mid");
			String payeeVirtualAdd = json.getString("payeeVirtualAdd");
			String payerMobNo = json.getString("payerMobNo");
			String payerVirtualAdd = json.getString("payerVirtualAdd");
			String remarks = json.getString("remarks");
			String ver = json.getString("ver");

			String raiseCollectHashRequest = amount + "#" + feSessionId + "#" + hdnOrderID + "#" + mid + "#"
					+ payeeVirtualAdd + "#" + payerMobNo + "#" + payerVirtualAdd + "#" + remarks + "#" + ver + "#"
					+ MERCHANT_SECRET_KEY;

			String generatedHash = encryptionService.generateHash(raiseCollectHashRequest);

			log.info("generatedHash: {}", generatedHash);

			String raiseCollectRequest = "{\r\n" + "    \"amount\": \"" + amount + "\",\r\n" + "    \"feSessionId\": \""
					+ feSessionId + "\",\r\n" + "    \"hdnOrderID\": \"" + hdnOrderID + "\",\r\n" + "    \"mid\": \""
					+ mid + "\",\r\n" + "    \"payeeVirtualAdd\": \"" + payeeVirtualAdd + "\",\r\n"
					+ "    \"payerMobNo\": \"" + payerMobNo + "\",\r\n" + "    \"payerVirtualAdd\": \""
					+ payerVirtualAdd + "\",\r\n" + "    \"remarks\": \"" + remarks + "\",\r\n" + "    \"ver\": \""
					+ ver + "\",\r\n" + "    \"hash\": \"" + generatedHash + "\"\r\n" + "}";

			log.info("raiseCollectRequest: {}", raiseCollectRequest);

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

			HttpEntity<String> entity = new HttpEntity<>(raiseCollectRequest, headers);
			ResponseEntity<String> response = restTemplate.exchange(RAISE_COLLECT_URL, HttpMethod.POST, entity,
					String.class);
			String apiResponse = response.getBody();

			log.info("apiResponse: {}", apiResponse);

			JSONObject resultJsonObject = new JSONObject(apiResponse);

			String messageText = resultJsonObject.getString("messageText");

			if (messageText.equalsIgnoreCase("SUCCESS")) {

				resultJsonObject.remove("code");
				resultJsonObject.remove("errorCode");

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_FOUND);
				map.put("data", resultJsonObject.toMap());
				return map;

			} else {

				JSONObject meta = resultJsonObject.getJSONObject("meta");

				resultJsonObject.remove("code");
				resultJsonObject.remove("status");

				String description = meta.getString("description");

				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, description);
				return map;
			}

		} catch (HttpClientErrorException e) {

			String responeString = e.getResponseBodyAsString();
			log.error("Exception: {}", responeString);

			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);

		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		}

		return map;
	}

	public Map<String, Object> transactionEnquiry(String trxnRequest, String url) throws IOException {

		log.info("Inside transactionEnquiry: {}", trxnRequest);
		Map<String, Object> map = new HashMap<>();

		JSONObject json = new JSONObject(trxnRequest);
		try {
			String channel = json.getString("channel");
			String feSessionId = json.getString("feSessionId");
			String hdnOrderID = json.getString("hdnOrderID");
			String merchantId = json.getString("merchantId");
			String ver = json.getString("ver");
			String rrn = json.getString("rrn");

			String transactionEnquiryHashRequest = channel + "#" + feSessionId + "#" + hdnOrderID + "#" + merchantId
					+ "#" + ver + "#" + rrn + "#" + MERCHANT_SECRET_KEY;

			String generatedHash = encryptionService.generateHash(transactionEnquiryHashRequest);

			log.info("generatedHash: {}", generatedHash);

			String transactionEnquiryRequest = "{\r\n" + "\"merchantId\": \"" + merchantId + "\",\r\n" + "\"ver\": \""
					+ ver + "\",\r\n" + "\"rrn\": \"" + rrn + "\",\r\n" + "\"hdnOrderID\": \"" + hdnOrderID + "\",\r\n"
					+ "\"channel\": \"" + channel + "\",\r\n" + "\"feSessionId\": \"" + feSessionId + "\",\r\n"
					+ "\"hash\": \"" + generatedHash + "\"\r\n" + "}";

			log.info("transactionEnquiryRequest: {}", transactionEnquiryRequest);

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

			HttpEntity<String> entity = new HttpEntity<>(transactionEnquiryRequest, headers);
			ResponseEntity<String> response = restTemplate.exchange(TRANSACTION_ENQUIRY_URL, HttpMethod.POST, entity,
					String.class);
			String apiResponse = response.getBody();

			log.info("apiResponse: {}", apiResponse);

			JSONObject resultJsonObject = new JSONObject(apiResponse);

			String messageText = resultJsonObject.getString("messageText");

			if (messageText.equalsIgnoreCase("SUCCESS")) {

				resultJsonObject.remove("code");
				resultJsonObject.remove("errorCode");

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_FOUND);
				map.put("data", resultJsonObject.toMap());
				return map;

			} else if (messageText.equalsIgnoreCase("DEEMED")) {
				resultJsonObject.remove("code");
				resultJsonObject.remove("errorCode");

				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
				map.put("data", resultJsonObject.toMap());
				return map;
			}
		} catch (HttpClientErrorException e) {

			String responeString = e.getResponseBodyAsString();
			log.error("Exception: {}", responeString);

			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);

		}

		catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		}

		return map;
	}

	public Map<String, Object> raiseRefund(String request) throws IOException {

		log.info("Inside raiseRefund: {}", request);
		Map<String, Object> map = new HashMap<>();

		JSONObject json = new JSONObject(request);
		try {
			String merchantId = json.getString("merchantId");
			String feSessionId = json.getString("feSessionId");
			String ver = json.getString("ver");
			String channel = json.getString("channel");
			String orgOrderID = json.getString("orgOrderID");
			String hdnOrderID = json.getString("hdnOrderID");
			String remark = json.getString("remark");
			String amount = json.getString("amount");
			String isMerchantInitiated = json.getString("isMerchantInitiated");

			String raiseRefundHashRequest = merchantId + "#" + feSessionId + "#" + ver + "#" + channel + "#"
					+ orgOrderID + "#" + hdnOrderID + "#" + remark + "#" + amount + "#" + isMerchantInitiated + "#"
					+ MERCHANT_SECRET_KEY;

			String generatedHash = encryptionService.generateHash(raiseRefundHashRequest);

			log.info("generatedHash: {}", generatedHash);

			String raiseRefundRequest = "{\r\n" + "    \"merchantId\": \"" + merchantId + "\",\r\n"
					+ "    \"feSessionId\": \"" + feSessionId + "\",\r\n" + "    \"ver\": \"" + ver + "\",\r\n"
					+ "    \"channel\": \"" + channel + "\",\r\n" + "    \"orgOrderID\": \"" + orgOrderID + "\",\r\n"
					+ "    \"hdnOrderID\": \"" + hdnOrderID + "\",\r\n" + "    \"remark\": \"" + remark + "\",\r\n"
					+ "    \"amount\": \"" + amount + "\",\r\n" + "    \"isMerchantInitiated\": " + isMerchantInitiated
					+ ",\r\n" + "    \"hash\": \"" + generatedHash + "\"\r\n" + "}";

			log.info("raiseRefundRequest: {}", raiseRefundRequest);

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

			HttpEntity<String> entity = new HttpEntity<>(raiseRefundRequest, headers);
			ResponseEntity<String> response = restTemplate.exchange(RAISE_REFUND_URL, HttpMethod.POST, entity,
					String.class);
			String apiResponse = response.getBody();

			log.info("apiResponse: {}", apiResponse);

			JSONObject resultJsonObject = new JSONObject(apiResponse);

			String messageText = resultJsonObject.getString("messageText");

			if (messageText.equalsIgnoreCase("SUCCESS")) {

				resultJsonObject.remove("code");
				resultJsonObject.remove("errorCode");

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_FOUND);
				map.put("data", resultJsonObject.toMap());
				return map;

			} else {

				JSONObject meta = resultJsonObject.getJSONObject("meta");

				resultJsonObject.remove("code");
				resultJsonObject.remove("status");

				String description = meta.getString("description");

				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, description);
				return map;
			}

		} catch (HttpClientErrorException e) {

			String responeString = e.getResponseBodyAsString();
			log.error("Exception: {}", responeString);

			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);

		}

		catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		}

		return map;
	}
}
