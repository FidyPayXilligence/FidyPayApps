package com.fidypay.ServiceProvider.NSDL;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

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
import com.fidypay.ServiceProvider.Signzy.MerchantServiceChargeService;
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
public class NSDLServiceImpl {

	private static final Logger logger = LoggerFactory.getLogger(NSDLServiceImpl.class);

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

	/*
	 * private static final String URL_PENNLESS =
	 * "https://nsdluat.transxt.in/imps-ws/transaction/sync/pl/2/acverify"; //UAT
	 * 
	 * 
	 * private static final String PENNYLESS_MOBILEWARESCRECET_KEY =
	 * "32e90259334cf6d36880df8d2ef9c6188e0fe598fac306be8dcf4642bb57417b"; // UAT
	 */
	
	// Production Pennyless
	private static final String URL_PENNLESS = "https://nsdl-imps.transxt.in/imps-ws/transaction/sync/pl/8/acverify"; 
	private static final String PENNYLESS_MOBILEWARESCRECET_KEY = "ff2a5a84ed15885e53b582c58da43e0aa7be85508648e5d096b4d971e06c8b2e";

	/*
	 * private static final String MOBILEWARESCRECET_KEY =
	 * "swBGKwEc2Y3PESTjAyXvocv0VLJbR3VtcKb6Vrx5wR9wr9cz7VBRSkwMPLf8KCT3DORi1d12ZazJwwn8d3VFPonUFnaGN7YpcZ8TnIoZvfTcsdab1fjwRDS3ppgosmmTtaQ8NouUnLZrdURfcbcTb8hpZz5QqtSBc5nArUk5Yq7NsdXBf67HGl5fFkWAcdQuFo47Y1V1YTFZ1M8O6IV8kX71v3ob7dFBJJahJzGD3HGbyp9uSZRbDqGNJUlzkdeT";
	 * private static final String URL_PENNYDROP =
	 * "https://jiffyuat.nsdlbank.co.in/jarvisdmt/impsaccntverify"; private static
	 * final String CHANNEL_ID = "f11203dfa32e4bc19dd39d1d92a90578"; private static
	 * final String PARTNER_ID = "7f27ef3f0d61118dd02394d21c70375a"; private static
	 * final String SERVICE_TYPE = "BENEVALIMPSP2A"; private static final String
	 * APP_ID = "com.mobile.nsdlpb";
	 */ // UAT

	//production Pennydrop
	private static final String MOBILEWARESCRECET_KEY = "4Wr6leR7C33tcrK6jeymgXIwCdu0PIkS37dQ6m1kHMqS9f4gjYLD6PLLicBAMc4WAQ4hHexdAcKtSoYkY49iGDkuWX9EIklhGPLRQuskURdUMy6ExA7ibJHfg9TwJEhI";
	private static final String URL_PENNYDROP = "https://apigwy.nsdlbank.in/partnerbank/impsaccntverify";
	private static final String CHANNEL_ID = "0b7c5373581e2f012cbb7c294b6a9077";
	private static final String PARTNER_ID = "dd677de0ec27df8516eaf411275d3c93";
	private static final String SERVICE_TYPE = "BENEVALIMPSP2A";
	private static final String APP_ID = "com.protean.nsdl";

	@SuppressWarnings("unchecked")
	private Map<String, Object> accounVerify(String beneficiaryAccount, String beneficiaryIFSC,
			String merchantTrxnRefId) {

		Map<String, Object> map = new HashMap<>();

		String apiResponse = "NA";

		String hash = NSDLEncrypt.generateHaskKey(merchantTrxnRefId + beneficiaryAccount + beneficiaryIFSC);
		logger.info("hash: {}", hash);

		try {

			String apiRequest = "{\"clientRefId\":\"" + merchantTrxnRefId + "\",\"toAccountNo\":\"" + beneficiaryAccount
					+ "\",\"toIFSC\":\"" + beneficiaryIFSC + "\",\"narration\":\"csc\",\"hashKey\":\"" + hash + "\"}";
			logger.info("apiRequest: {}", apiRequest);
			String encryptedRequest = Encrypt.encryptValidationRequest(apiRequest, PENNYLESS_MOBILEWARESCRECET_KEY);
			logger.info("encryptedRequest: {}", encryptedRequest);

			String eReq = "{\"eReq\":\"" + encryptedRequest + "\"}";

			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, eReq);
			Request request = new Request.Builder().url(URL_PENNLESS).method("POST", body)
					.addHeader("Content-Type", "application/json").build();
			Response response = client.newCall(request).execute();
			apiResponse = response.body().string();
			logger.info("apiResponse: {}", apiResponse);
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> mapResponse = objectMapper.readValue(apiResponse, Map.class);
			apiResponse = Encrypt.decryptValidationRequest((String) mapResponse.get("eResp"),
					PENNYLESS_MOBILEWARESCRECET_KEY);
			logger.info("apiResponse: {}", apiResponse);

			ObjectMapper objectMapper1 = new ObjectMapper();
			Map<String, Object> mapObject = objectMapper1.readValue(apiResponse, Map.class);

			map.put("transDateTime", mapObject.get("transDateTime"));
			map.put("beneficiaryName", mapObject.get("beneficiaryName"));
			map.put("rrn", mapObject.get("rrn"));
			map.put("clientRefId", mapObject.get("clientRefId"));
			if (((String) mapObject.get("status")).equals("SUCCESS")) {
				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, mapObject.get("responseMsg"));
				return map;

			}

			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, mapObject.get("responseMsg"));

		} catch (Exception e) {
			logger.error("Exception: {}", e);
		}
		return map;
	}

	public Map<String, Object> bankAccountVerificationRequestForPennyLess(String beneficiaryAccount,
			String beneficiaryIFSC, String merchantTrxnRefId, long merchantId, long serviceId, double amount,
			String serviceName, String trxnRefId, long ekycRequestId)
			throws ParseException, JsonMappingException, JsonProcessingException {
		Map<String, Object> map = new HashMap<>();

		Map<String, Object> verificationResponse = accounVerify(beneficiaryAccount, beneficiaryIFSC, merchantTrxnRefId);

		String remarks = (String) verificationResponse.get("clientRefId");

		String active = verificationResponse.get("status").equals("SUCCESS") ? "yes" : "no";
		String reason = (String) verificationResponse.get("description");

		// Charges & Commission

		MerchantService merchantsService = merchantServiceRepository.findByMerchantIdAndServiceId(merchantId,
				serviceId);
		Long merchantServiceId = merchantsService.getMerchantServiceId();
		logger.info("merchantServiceId: {}", merchantServiceId);
		String serviceType = merchantsService.getServiceType();
		logger.info("serviceType: " + serviceType);
		double charges = 0.0;
		double commission = 0.0;

		switch (serviceType) {

		case "Charge":
			charges = chargeService.getMerchantServiceChargesV2(merchantServiceId, amount);
			logger.info("charges: " + charges);
			break;

		case "Commission":
			commission = chargeService.getMerchantServiceCommissionV2(merchantServiceId, amount);
			logger.info("commission: " + commission);
			break;

		default:
			break;

		}

		Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

		if (active.equals("yes")) {

			String beneName = (String) verificationResponse.get("beneficiaryName");
			String beneIFSC = beneficiaryIFSC;
			String bankRRN = (String) verificationResponse.get("rrn");

			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, "Account verified successfully");
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			map.put("beneficiaryAccNo", beneficiaryAccount);
			map.put("merchantTrxnRefId", remarks);
			map.put("active", active);
			map.put("beneficiaryName", beneName);
			map.put("beneficiaryIfscCode", beneIFSC);
			map.put("utr", bankRRN);

			String serviceProvider = "NSDL";
			ServiceProviders spInfo = serviceprovidersrepository.findBySpName(Encryption.encString(serviceProvider));
			Long serviceProviderId = spInfo.getServiceProviderId();
			
			EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
					.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
							ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
							serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
							serviceProviderId, '0'));
			logger.info("ekycTransactionDetails: {} ", ekycTransactionDetails);
			
//			EkycTransactionDetails elk = ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
//			logger.info("ELASTICSEARCH DATA INSERTION FOR PENNY LESS ACTIVE YES EKYC TRXN DETAILS : {}", elk);

		}
		if (active.equals("no")) {

			String serviceProvider = "NSDL";
			ServiceProviders spInfo = serviceprovidersrepository.findBySpName(Encryption.encString(serviceProvider));
			Long serviceProviderId = spInfo.getServiceProviderId();
			
			EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
					.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
							ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
							serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 2L,
							serviceProviderId, '0'));
			logger.info("ekycTransactionDetails: {} ", ekycTransactionDetails);
			
//			EkycTransactionDetails elk = ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
//			logger.info("ELASTICSEARCH DATA INSERTION FOR PENNY LESS ACTIVE NO EKYC TRXN DETAILS : {}", elk);

			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, reason.equals("")? ResponseMessage.API_STATUS_FAILED: reason);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put("beneficiaryAccNo", beneficiaryAccount);
			map.put("merchantTrxnRefId", remarks);
			map.put("active", active);

		}
		return map;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> bankAccountVerificationRequestForPennyDrop(String beneficiaryAccount,
			String beneficiaryIFSC, String merchantTrxnRefId, long merchantId, Long serviceId, double amount,
			String serviceName, String trxnRefId, long ekycRequestId, NSDLRequest nsdlRequest) throws ParseException {
		Map<String, Object> map = new HashMap<>();

		Map<String, Object> verificationResponse = verifyAccount(nsdlRequest);
		Map<String, Object> responsedata = (Map<String, Object>) verificationResponse.get("responsedata");

		String remarks = (String) verificationResponse.get("requestId");
		String active = verificationResponse.get("response").equals("Success") ? "yes" : "no";
		String reason = (String) responsedata.get("nwrespmessg");

		// Charges & Commission

		MerchantService merchantsService = merchantServiceRepository.findByMerchantIdAndServiceId(merchantId,
				serviceId);
		Long merchantServiceId = merchantsService.getMerchantServiceId();
		String serviceType = merchantsService.getServiceType();
		logger.info("serviceType: " + serviceType);
		double charges = 0.0;
		double commission = 0.0;

		switch (serviceType) {

		case "Charge":
			charges = chargeService.getMerchantServiceChargesV2(merchantServiceId, amount);
			logger.info("charges: " + charges);
			break;

		case "Commission":
			commission = chargeService.getMerchantServiceCommissionV2(merchantServiceId, amount);
			logger.info("commission: " + commission);
			break;

		default:
			break;

		}

		Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

		if (active.equals("yes")) {

			String beneName = (String) responsedata.get("c_name");
			String beneIFSC = beneficiaryIFSC;
			String bankRRN = (String) responsedata.get("nwtxnrefid");

			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, "Account verified successfully");
			map.put("beneficiaryAccNo", beneficiaryAccount);
			map.put("merchantTrxnRefId", remarks);
			map.put("active", active);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			map.put("beneficiaryName", beneName);
			map.put("beneficiaryIfscCode", beneIFSC);
			map.put("utr", bankRRN);

			String serviceProvider = "NSDL";
			ServiceProviders spInfo = serviceprovidersrepository.findBySpName(Encryption.encString(serviceProvider));
			Long serviceProviderId = spInfo.getServiceProviderId();
			
			EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
					.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
							ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
							serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
							serviceProviderId, '0'));
			logger.info("ekycTransactionDetails: {} ", ekycTransactionDetails);
			
//			EkycTransactionDetails elk = ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
//			logger.info("ELASTICSEARCH DATA INSERTION FOR PENNY DROP ACTIVE YES EKYC TRXN DETAILS : {}", elk);


		}
		if (active.equals("no")) {

			String serviceProvider = "NSDL";
			ServiceProviders spInfo = serviceprovidersrepository.findBySpName(Encryption.encString(serviceProvider));
			Long serviceProviderId = spInfo.getServiceProviderId();
			
			EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
					.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
							ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
							serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 2L,
							serviceProviderId, '0'));
			logger.info("ekycTransactionDetails: {} ", ekycTransactionDetails);
			
//			EkycTransactionDetails elk = ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
//			logger.info("ELASTICSEARCH DATA INSERTION FOR PENNY DROP ACTIVE NO EKYC TRXN DETAILS : {}", elk);

			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, reason.equals("")? ResponseMessage.API_STATUS_FAILED: reason);
			map.put("beneficiaryAccNo", beneficiaryAccount);
			map.put("merchantTrxnRefId", remarks);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put("active", active);

		}

		return map;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> verifyAccount(NSDLRequest nsdlRequest) {
		Map<String, Object> map = new HashMap<>();
		try {
			String apiResponse = "";
			logger.info("nsdlRequest: {}", nsdlRequest);

			String requestId = nsdlRequest.getMerchantTrxnRefId();
			String bankAccountType = (nsdlRequest.getBankAccountType().toUpperCase()).equals("CURRENT") ?nsdlRequest.getBankAccountType(): "SAVING";
			logger.info("requestId: {}", requestId);

			String apiRequest = "{\"data\":{\"requestId\":\"" + requestId
					+ "\",\"paymentdtls\":{\"txntype\":\"IMPSP2A\",\"chantxnrefno\":\"" + requestId
					+ "\",\"usrtxnrefno\":\"" + requestId
					+ "\",\"payerdtls\":{\"d_usrremarks\":\"Transferfund\",\"d_txntag\":\"FUNDTRANSFER\",\"d_mobile\":\""
					+ nsdlRequest.getMobileNo() + "\",\"d_ipaddress\":\"0.0.0.0\"},\"payeedtls\":{\"c_accntid_type\":\""
					+ bankAccountType + "\",\"c_accntid\":\"" + nsdlRequest.getBankAccountNumber()
					+ "\",\"c_name\":\"" + nsdlRequest.getMerchantName() + "\",\"c_ifsc\":\""
					+ nsdlRequest.getBankIFSCCode() + "\"},\"txnamount\":\"1\",\"txncurrency\":\"INR\"}}}";

			String encryptedRequest = Encrypt.encryptstring(apiRequest, MOBILEWARESCRECET_KEY);
			logger.info("encryptedRequest : {}", encryptedRequest);

			String requestBody = "{\"channelid\": \"" + CHANNEL_ID + "\", \"partnerid\": \"" + PARTNER_ID
					+ "\", \"appid\": \"" + APP_ID + "\",\"servicetype\": \"" + SERVICE_TYPE + "\",\"encdata\": \""
					+ encryptedRequest + "\"}";

			logger.info("requestBody : {}", requestBody);

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.set("channelid", CHANNEL_ID);
			headers.set("partnerid", PARTNER_ID);
			headers.set("servicetype", SERVICE_TYPE);
			headers.set("requestId", requestId);
			headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

			HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

			ResponseEntity<String> response = restTemplate.postForEntity(URL_PENNYDROP, entity, String.class);
			apiResponse = response.getBody();
			logger.info("apiResponse : {}", apiResponse);

			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> readValue = objectMapper.readValue(apiResponse, Map.class);
			Map<String, Object> responsedata = (Map<String, Object>) readValue.get("responsedata");
			Map<String, Object> reqdtls = (Map<String, Object>) responsedata.get("reqdtls");
			map.put("responsedata", responsedata);
			map.put("reqdtls", reqdtls);
			map.put("response", readValue.get("response"));
			map.put("requestId", readValue.get("requestId"));
		} catch (Exception e) {
			logger.error("Exception: {}", e);
		}
		return map;
	}

	
	@SuppressWarnings("unchecked")
	public Map<String, Object> accounVerifyPennyLess(String beneficiaryAccount, String beneficiaryIFSC,
	String merchantTrxnRefId) {

	Map<String, Object> map = new HashMap<>();

	String apiResponse = "NA";

	String hash = NSDLEncrypt.generateHaskKey(merchantTrxnRefId + beneficiaryAccount + beneficiaryIFSC);
	logger.info("hash: {}", hash);

	try {

	String apiRequest = "{\"clientRefId\":\"" + merchantTrxnRefId + "\",\"toAccountNo\":\"" + beneficiaryAccount
	+ "\",\"toIFSC\":\"" + beneficiaryIFSC + "\",\"narration\":\"csc\",\"hashKey\":\"" + hash + "\"}";
	logger.info("apiRequest: {}", apiRequest);
	String encryptedRequest = Encrypt.encryptValidationRequest(apiRequest, PENNYLESS_MOBILEWARESCRECET_KEY);
	logger.info("encryptedRequest: {}", encryptedRequest);

	String eReq = "{\"eReq\":\"" + encryptedRequest + "\"}";

	OkHttpClient client = new OkHttpClient().newBuilder().build();
	MediaType mediaType = MediaType.parse("application/json");
	RequestBody body = RequestBody.create(mediaType, eReq);
	Request request = new Request.Builder().url(URL_PENNLESS).method("POST", body)
	.addHeader("Content-Type", "application/json").build();
	Response response = client.newCall(request).execute();
	apiResponse = response.body().string();
	logger.info("apiResponse: {}", apiResponse);
	ObjectMapper objectMapper = new ObjectMapper();
	Map<String, Object> mapResponse = objectMapper.readValue(apiResponse, Map.class);
	apiResponse = Encrypt.decryptValidationRequest((String) mapResponse.get("eResp"),
	PENNYLESS_MOBILEWARESCRECET_KEY);
	logger.info("apiResponse: {}", apiResponse);

	ObjectMapper objectMapper1 = new ObjectMapper();
	Map<String, Object> mapObject = objectMapper1.readValue(apiResponse, Map.class);

	map.put("transDateTime", mapObject.get("transDateTime"));
	map.put("beneficiaryName", mapObject.get("beneficiaryName"));
	map.put("rrn", mapObject.get("rrn"));
	map.put("clientRefId", mapObject.get("clientRefId"));
	if (((String) mapObject.get("status")).equals("SUCCESS")) {
	map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
	map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
	map.put(ResponseMessage.DESCRIPTION, mapObject.get("responseMsg"));
	return map;

	}

	map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
	map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
	map.put(ResponseMessage.DESCRIPTION, mapObject.get("responseMsg"));

	} catch (Exception e) {
	logger.error("Exception: {}", e);
	}
	return map;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> verifyAccountPennyDrop(NSDLRequest nsdlRequest) {
	Map<String, Object> map = new HashMap<>();
	try {
	String apiResponse = "";
	logger.info("nsdlRequest: {}", nsdlRequest);

	String requestId = nsdlRequest.getMerchantTrxnRefId();
	String bankAccountType = (nsdlRequest.getBankAccountType().toUpperCase()).equals("CURRENT") ?nsdlRequest.getBankAccountType(): "SAVING";
	logger.info("requestId: {}", requestId);

	String apiRequest = "{\"data\":{\"requestId\":\"" + requestId
	+ "\",\"paymentdtls\":{\"txntype\":\"IMPSP2A\",\"chantxnrefno\":\"" + requestId
	+ "\",\"usrtxnrefno\":\"" + requestId
	+ "\",\"payerdtls\":{\"d_usrremarks\":\"Transferfund\",\"d_txntag\":\"FUNDTRANSFER\",\"d_mobile\":\""
	+ nsdlRequest.getMobileNo() + "\",\"d_ipaddress\":\"0.0.0.0\"},\"payeedtls\":{\"c_accntid_type\":\""
	+ bankAccountType + "\",\"c_accntid\":\"" + nsdlRequest.getBankAccountNumber()
	+ "\",\"c_name\":\"" + nsdlRequest.getMerchantName() + "\",\"c_ifsc\":\""
	+ nsdlRequest.getBankIFSCCode() + "\"},\"txnamount\":\"1\",\"txncurrency\":\"INR\"}}}";

	String encryptedRequest = Encrypt.encryptstring(apiRequest, MOBILEWARESCRECET_KEY);
	logger.info("encryptedRequest : {}", encryptedRequest);

	String requestBody = "{\"channelid\": \"" + CHANNEL_ID + "\", \"partnerid\": \"" + PARTNER_ID
	+ "\", \"appid\": \"" + APP_ID + "\",\"servicetype\": \"" + SERVICE_TYPE + "\",\"encdata\": \""
	+ encryptedRequest + "\"}";

	logger.info("requestBody : {}", requestBody);

	RestTemplate restTemplate = new RestTemplate();
	HttpHeaders headers = new HttpHeaders();
	headers.set("channelid", CHANNEL_ID);
	headers.set("partnerid", PARTNER_ID);
	headers.set("servicetype", SERVICE_TYPE);
	headers.set("requestId", requestId);
	headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

	HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

	ResponseEntity<String> response = restTemplate.postForEntity(URL_PENNYDROP, entity, String.class);
	apiResponse = response.getBody();
	logger.info("apiResponse : {}", apiResponse);

	ObjectMapper objectMapper = new ObjectMapper();
	Map<String, Object> readValue = objectMapper.readValue(apiResponse, Map.class);
	Map<String, Object> responsedata = (Map<String, Object>) readValue.get("responsedata");
	Map<String, Object> reqdtls = (Map<String, Object>) responsedata.get("reqdtls");
	map.put("responsedata", responsedata);
	map.put("reqdtls", reqdtls);
	map.put("response", readValue.get("response"));
	map.put("requestId", readValue.get("requestId"));
	} catch (Exception e) {
	logger.error("Exception: {}", e);
	}
	return map;
	}
	
	public Map<String, Object> bankAccountVerificationRequestForPennyLessUat(String accountNumber, String ifsc,
			String merchantTrxnRefId, long merchantId, Long serviceId, double amtInDouble, String serviceName,
			String trxnRefId, long ekycRequestId) throws JsonMappingException, JsonProcessingException {

			RestTemplate restTemplate = new RestTemplate();
			String url = "https://api.fidypay.com/ekyc/accounVerifyPennyLess";

			String response = "NA";

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
			headers.set("accept", "*/*");

			String requestBody = "{ \"bankAccountNumber\": \"" + accountNumber
			+ "\", \"bankAccountType\": \"Current\", \"bankIFSCCode\": \"" + ifsc
			+ "\", \"merchantName\": \"Arpan\", \"merchantTrxnRefId\": \"" + merchantTrxnRefId
			+ "\", \"mobileNo\": \"7869920537\"}";

			HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
			String.class);

			response = responseEntity.getBody();

			logger.info("response " + response);
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> map = objectMapper.readValue(response, Map.class);
			logger.info("map " + map);
			return map;

			}
	
}
