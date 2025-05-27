package com.fidypay.ServiceProvider.Signzy;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fidypay.ServiceProvider.Karza.ESignServices;
import com.fidypay.encryption.Encryption;
import com.fidypay.entity.EkycTransactionDetails;
import com.fidypay.entity.MerchantService;
import com.fidypay.entity.ServiceProviders;
import com.fidypay.repo.EkycTransactionDetailsRepository;
import com.fidypay.repo.MerchantServiceRepository;
import com.fidypay.repo.ServiceProvidersRepository;
import com.fidypay.request.NSDLRequest;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.DateAndTime;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class AccountVerification {

	private static final Logger LOGGER = LoggerFactory.getLogger(AccountVerification.class);

	@Autowired
	private MerchantServiceRepository merchantServiceRepository;

	@Autowired
	private MerchantServiceChargeService chargeService;

	@Autowired
	private EkycTransactionDetailsRepository ekycTransactionDetailsRepository;

	@Autowired
	private ServiceProvidersRepository serviceprovidersrepository;
	
//	@Autowired
//	private EkycTransactionDetailsSearchRepository ekycTransactionDetailsSearchRepository;

	// public static final String BASE_URL =
	// "https://preproduction.signzy.tech/api/v2/patrons/";

	// UAT
//	public static final String USERNAME = "Fidypay_test";
//	public static final String PASSWORD = "juSff9ET#vzc@Rc*";
//	public static final String BASE_URL = "https://preproduction.signzy.tech/api/v2/patrons/";

	// LIVE
	public static final String USERNAME = "fidypay_prod";
	public static final String PASSWORD = "u4wwVbDFy2xYMrbRU8xs";
	public static final String BASE_URL = "https://signzy.tech/api/v2/patrons/";
	public static final String ACCOUNT_VERIFICSTION_URL = "https://auroapi.transxt.in/api/1.1/pennydrop";

	public Map<String, Object> bankAccountVerification(String beneficiaryAccount, String beneficiaryIFSC,
			String merchantTrxnRefId) {

		Map<String, Object> map = new HashMap<>();
		try {

			String responseLogin = new AccountVerification().login(USERNAME, PASSWORD);
			LOGGER.info("Login Response " + responseLogin);
			JSONObject jsonObject = new JSONObject(responseLogin);
			String id = jsonObject.getString("id");
			String userId = jsonObject.getString("userId");

			String requstStr = "{\r\n" + "	\"task\": \"bankTransfer\",\r\n" + "	\"essentials\": {\r\n"
					+ "		\"beneficiaryAccount\": \"" + beneficiaryAccount + "\",\r\n"
					+ "		\"beneficiaryIFSC\": \"" + beneficiaryIFSC + "\",\r\n" + "		\"remarks\": \""
					+ merchantTrxnRefId + "\",\r\n" + "		\"nameMatchScore\": \"0\",\r\n"
					+ "		\"nameFuzzy\": \"true\"\r\n" + "	}\r\n" + "}";

			OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(120, TimeUnit.SECONDS)
					.readTimeout(120, TimeUnit.SECONDS).writeTimeout(120, TimeUnit.SECONDS).build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, requstStr);
			Request request = new Request.Builder().url(BASE_URL + userId + "/bankaccountverifications")
					.method("POST", body).addHeader("Authorization", id).addHeader("Content-Type", "application/json")
					.build();
			Response response = client.newCall(request).execute();

			String results = response.body().string();
			LOGGER.info("Response: " + results);
			map.put("results", results);

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}

		return map;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> bankAccountVerificationRequest(String beneficiaryAccount, String beneficiaryIFSC,
			String merchantTrxnRefId, long merchantId, long serviceId, double amount, String serviceName,
			String trxnRefId, long ekycRequestId, Long merchantServiceId, double charges) throws ParseException, JsonMappingException, JsonProcessingException {

		Map<String, Object> map = new HashMap<>();

		Map<String, Object> verificationResponse = new AccountVerification().bankAccountVerification(beneficiaryAccount,
				beneficiaryIFSC, merchantTrxnRefId);
		String resposne = (String) verificationResponse.get("results");
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, Object> mapResponse = objectMapper.readValue(resposne, Map.class);

		Map<String, Object> essentials = (Map<String, Object>) mapResponse.get("essentials");
		String beneficiaryAccountRes = (String) essentials.get("beneficiaryAccount");
		String remarks = (String) essentials.get("remarks");

		Map<String, Object> result = (Map<String, Object>) mapResponse.get("result");

		String active = (String) result.get("active");
		String reason = (String) result.get("reason");

		// Charges & Commission

//		MerchantService merchantsService = merchantServiceRepository.findByMerchantIdAndServiceId(merchantId,
//				serviceId);
//		Long merchantServiceId = merchantsService.getMerchantServiceId();
//		String serviceType = merchantsService.getServiceType();
//		LOGGER.info("serviceType: " + serviceType);
//		double charges = 0.0;
		double commission = 0.0;
//
//		switch (serviceType) {
//
//		case "Charge":
//			charges = chargeService.getMerchantServiceChargesV2(merchantServiceId, amount);
//			LOGGER.info("charges: " + charges);
//			break;
//
//		case "Commission":
//			commission = chargeService.getMerchantServiceCommissionV2(merchantServiceId, amount);
//			LOGGER.info("commission: " + commission);
//			break;
//
//		default:
//			break;
//
//		}

		Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

		if (active.equals("yes")) {

			Map<String, Object> bankTransfer = (Map<String, Object>) result.get("bankTransfer");
			String beneName = (String) bankTransfer.get("beneName");
			String beneIFSC = (String) bankTransfer.get("beneIFSC");
			String bankRRN = (String) bankTransfer.get("bankRRN");

			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, "Account verified successfully");
			map.put("beneficiaryAccNo", beneficiaryAccountRes);
			map.put("merchantTrxnRefId", remarks);
			map.put("active", active);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			map.put("beneficiaryName", beneName);
			map.put("beneficiaryIfscCode", beneIFSC);
			map.put("utr", bankRRN);

			String serviceProvider = ResponseMessage.SIGNZY;
			ServiceProviders spInfo = serviceprovidersrepository.findBySpName(Encryption.encString(serviceProvider));
			Long serviceProviderId = spInfo.getServiceProviderId();
			
			EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
					.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
							ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
							serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
							serviceProviderId, '0'));
			LOGGER.info("ekycTransactionDetails: {} ", ekycTransactionDetails);
			
//			EkycTransactionDetails elk = ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
//			LOGGER.info("ELASTICSEARCH DATA INSERTION FOR ACCOUNT VERIFICATION ACTIVE YES EKYC TRXN DETAILS : {}", elk);

		}
		if (active.equals("no")) {

			String serviceProvider = ResponseMessage.SIGNZY;
			ServiceProviders spInfo = serviceprovidersrepository.findBySpName(Encryption.encString(serviceProvider));
			Long serviceProviderId = spInfo.getServiceProviderId();
			
			EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
					.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
							ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
							serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
							serviceProviderId, '0'));
			LOGGER.info("ekycTransactionDetails: {} ", ekycTransactionDetails);
			
//			EkycTransactionDetails elk = ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
//			LOGGER.info("ELASTICSEARCH DATA INSERTION FOR ACCOUNT VERIFICATION ACTIVE NO EKYC TRXN DETAILS : {}", elk);

			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, reason);
			map.put("beneficiaryAccNo", beneficiaryAccount);
			map.put("merchantTrxnRefId", remarks);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put("active", active);

		}

		return map;
	}

	public String login(String username, String password) throws IOException {
		// For Production Credentials
		// https://signzy.tech/api/v2/patrons/login

		String requestStr = "{\r\n" + "    \"username\": \"" + username + "\",\r\n" + "    \"password\": \"" + password
				+ "\"\r\n" + "  }";
		OkHttpClient client = new OkHttpClient().newBuilder().build();
		MediaType mediaType = MediaType.parse("application/json");
		RequestBody body = RequestBody.create(mediaType, requestStr);
		Request request = new Request.Builder().url(BASE_URL + "login").method("POST", body)
				.addHeader("Content-Type", "application/json").build();
		Response response = client.newCall(request).execute();
		String finalResposne = response.body().string();
		LOGGER.info("finalResponse: {}", finalResposne);
		return finalResposne;
	}

	public Map<String, Object> bankAccountVerificationPennyDrop(NSDLRequest nsdlRequest) {

		Map<String, Object> map = new HashMap<>();

		String bankAccount = nsdlRequest.getBankAccountNumber();
		String ifsc = nsdlRequest.getBankIFSCCode();
		String traceId = nsdlRequest.getMerchantTrxnRefId();

		try {

			String apiRequest = "{\r\n    \"bankAccount\": \"" + bankAccount + "\",\r\n    \"ifsc\": \"" + ifsc
					+ "\",\r\n    \"traceId\": \"" + traceId + "\"\r\n}";
//			UAT Auth
//			String authorization = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOiI0MDIiLCJzdWIiOiJhdXRoIiwiaXNzIjoiVFJBTlNYVCIsIlNFU1NJT05JRCI6IjAiLCJTRUNSRVQiOiIiLCJQUk9ETElTVCI6W10sIlVTRVJJRCI6IjAiLCJQT1JUQUwiOiIiLCJFTlYiOiJwcm9kIn0.Dk8q0GM4pK8ClP_7gFTTCiKyE7nn5dH452JK0C0_azcaYMgN0k53mDKiu50FW8zgZdGXEXbfBgGGmUalqCIAKg";

//			Prod Auth
			String authorization = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOiI0MjkiLCJzdWIiOiJhdXRoIiwiaXNzIjoiVFJBTlNYVCIsIlNFU1NJT05JRCI6IjAiLCJTRUNSRVQiOiIiLCJQUk9ETElTVCI6W10sIlVTRVJJRCI6IjAiLCJQT1JUQUwiOiIiLCJFTlYiOiJwcm9kIn0.CRIW6g1LhaxzUDX6A72uBBJzu1u1ktWFm8E8zsNtaTqIAZpU1uIJAWlNmq4HJ1kitphaET3qfqnU9oDRgz5kbg";

			LOGGER.info("apiRequest: {}", apiRequest);

//			RestTemplate restTemplate = new RestTemplate();
//			HttpHeaders headers = new HttpHeaders();
//			headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
//			headers.set("Authorization",
//					"eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOiI0MDIiLCJzdWIiOiJhdXRoIiwiaXNzIjoiVFJBTlNYVCIsIlNFU1NJT05JRCI6IjAiLCJTRUNSRVQiOiIiLCJQUk9ETElTVCI6W10sIlVTRVJJRCI6IjAiLCJQT1JUQUwiOiIiLCJFTlYiOiJwcm9kIn0.Dk8q0GM4pK8ClP_7gFTTCiKyE7nn5dH452JK0C0_azcaYMgN0k53mDKiu50FW8zgZdGXEXbfBgGGmUalqCIAKg");
//
//			HttpEntity<String> entity = new HttpEntity<>(apiRequest, headers);
//			ResponseEntity<String> response = restTemplate.exchange(ACCOUNT_VERIFICSTION_URL, HttpMethod.POST, entity,
//					String.class);
//			String apiResponse = response.getBody();

			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, apiRequest);
			Request request = new Request.Builder().url(ACCOUNT_VERIFICSTION_URL).method("POST", body)
					.addHeader("Content-Type", "application/json").addHeader("Authorization", authorization).build();
			Response response = client.newCall(request).execute();

			String apiResponse = response.body().string();

			org.json.JSONObject resultJsonObject = new org.json.JSONObject(apiResponse);

			LOGGER.info("apiResponse: {}", apiResponse);

			String status = resultJsonObject.getString("status");

			if (status.equals("SUCCESS")) {

				JSONObject result = resultJsonObject.getJSONObject("data");
				String utr = result.getString("refId");
				String nameAtBank = result.getString("nameAtBank");

				map = ESignServices.setResponse(ResponseMessage.SUCCESS, ResponseMessage.API_STATUS_SUCCESS,
						"Account verified successfully");

				map.put("merchantTxnRefId", traceId);
//				map.put("subCode", resultJsonObject.getString("subCode"));
//				map.put("traceId", resultJsonObject.getString("traceId"));
//				map.put("Data", result.toMap());
				map.put("beneficiaryAccNo", bankAccount);
				map.put("active", "yes");
				map.put("beneficiaryName", nameAtBank);
				map.put("beneficiaryIfscCode", ifsc);
				map.put("utr", utr);

				return map;

			} else {

				if (!resultJsonObject.has("data")) {

					map = ESignServices.setResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED,
							resultJsonObject.getString("message"));
					map.put("beneficiaryAccNo", bankAccount);
					map.put("merchantTrxnRefId", traceId);
					map.put("active", "No");

				} else {
					map = ESignServices.setResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED,
							resultJsonObject.getString("message"));
					map.put("beneficiaryAccNo", bankAccount);
					map.put("merchantTrxnRefId", traceId);
					map.put("active", "No");
				}
			}

		} catch (Exception e) {
			LOGGER.error("Exception: {}", e);
			map = ESignServices.setResponse(ResponseMessage.SOMETHING_WENT_WRONG, ResponseMessage.API_STATUS_FAILED,
					ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		}
		return map;
	}
	
	public Map<String, Object> bankAccountVerificationPennyDropUat(NSDLRequest nsdlRequest)
			throws JsonMappingException, JsonProcessingException {

			String bankAccount = nsdlRequest.getBankAccountNumber();
			String ifsc = nsdlRequest.getBankIFSCCode();
			String merchantTrxnRefId = nsdlRequest.getMerchantTrxnRefId();

			RestTemplate restTemplate = new RestTemplate();
			String url = "https://api.fidypay.com/ekyc/verifyAccountPennyDrop";

			String response = "NA";

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
			headers.set("accept", "*/*");

			String requestBody = "{ \"bankAccountNumber\": \"" + bankAccount
			+ "\", \"bankAccountType\": \"Current\", \"bankIFSCCode\": \"" + ifsc
			+ "\", \"merchantName\": \"Arpan\", \"merchantTrxnRefId\": \"" + merchantTrxnRefId
			+ "\", \"mobileNo\": \"7869920537\"}";

			HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
			String.class);

			response = responseEntity.getBody();
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> map = objectMapper.readValue(response, Map.class);
			return map;
			}

}
