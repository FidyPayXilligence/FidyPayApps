package com.fidypay.ServiceProvider.Decentro;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.json.JSONArray;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fidypay.ServiceProvider.Karza.ESignServices;
import com.fidypay.ServiceProvider.Signzy.EKYCService;
import com.fidypay.ServiceProvider.Signzy.MerchantServiceChargeService;
import com.fidypay.encryption.Encryption;
import com.fidypay.entity.EkycRequest;
import com.fidypay.entity.EkycTransactionDetails;
import com.fidypay.entity.MerchantService;
import com.fidypay.entity.ServiceInfo;
import com.fidypay.entity.ServiceProviders;
import com.fidypay.repo.EkycRequestRepository;
import com.fidypay.repo.EkycTransactionDetailsRepository;
import com.fidypay.repo.MerchantServiceRepository;
import com.fidypay.repo.MerchantWalletTransactionsRepository;
import com.fidypay.repo.MerchantsRepository;
import com.fidypay.repo.ServiceInfoRepository;
import com.fidypay.repo.ServiceProvidersRepository;
import com.fidypay.request.AdvancedCKycDownloadRequest;
import com.fidypay.request.DigilockerGetAccessToken;
import com.fidypay.request.DigilockerGetIssuedFiles;
import com.fidypay.request.DigilockerInitiateSession;
import com.fidypay.request.PanToMobileRequest;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.DateAndTime;
import com.fidypay.utils.ex.GenerateTrxnRefId;
import com.fidypay.utils.ex.RandomNumberGenrator;
import com.fidypay.utils.ex.ValidateUtils;
import com.fidypay.wallet.WalletNotification;
import com.fidypay.wallet.WalletRequest;
import com.fidypay.wallet.WalletService;
import com.google.gson.Gson;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.lang3.*;

@Service
public class DecentroServiceImpl {

	@Autowired
	private WalletNotification walletNotification;

	@Autowired
	private ServiceInfoRepository serviceInfoRepository;

	@Autowired
	private MerchantServiceRepository merchantServiceRepository;

	@Autowired
	private MerchantServiceChargeService chargeService;

	@Autowired
	private EkycRequestRepository ekycRequestRepository;

	@Autowired
	private MerchantsRepository merchantsRepository;

	@Autowired
	private EkycTransactionDetailsRepository ekycTransactionDetailsRepository;

	@Autowired
	private MerchantWalletTransactionsRepository merchantWalletTransactionsRepository;

	@Autowired
	private ServiceProvidersRepository serviceprovidersrepository;

	@Autowired
	private EKYCService ekycService;

	@Autowired
	private WalletService walletService;

//	@Autowired
//	private EkycTransactionDetailsSearchRepository ekycTransactionDetailsSearchRepository;

	private static final Logger LOGGER = LoggerFactory.getLogger(DecentroServiceImpl.class);

//	private static final String VALIDATE_UPI_HANDLE_URL = "https://in.decentro.tech/v2/payments/vpa/validate";

	private static final String SEARCH_CKYC_URL = "https://in.staging.decentro.tech/v2/kyc/ckyc/search";

	private static final String HYPERSTREAM_SYNCHRONOUS_URL = "https://in.staging.decentro.tech/synchronous/hyperstream-executor#advanced-ckyc-download";

	private static final String PHONE_TO_MOBILE_URL = "https://in.decentro.tech/synchronous/hyperstream-executor";

	private static final String HYPERSTREAM_ASYNCHRONOUS_URL = "https://in.staging.decentro.tech/asynchronous/hyperstream-executor#advanced-ckyc-download";

	private static final String CLIENT_ID = "fidypay_staging";
	private static final String CLIENT_SECRET = "dRr5IqBh5L4wQ2GYmst5iAxYLcWCLXEN";
	private static final String KYC_AND_ONBOARDING_SECRET = "OLD006mfTYAF4Zgco4rZkL0SkIPc6vtN";
//	private static final String MODULE_SECRET = "OLD006mfTYAF4Zgco4rZkL0SkIPc6vtN";
	private static final String VALIDATE_UPI_MODULE_SECRET = "yXHOdZWfW87UZ6yEXTosMlnJmwH6NKRQ";
	private static final String VPA_REGEX = "^[a-zA-Z0-9._\\-]{1,252}@[a-zA-Z0-9]{1,252}$";

	private static final String DOB = "(0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[1,2])-(19|20)\\d{2}";

	private final Semaphore semaphore = new Semaphore(1);
	private final Lock lock = new ReentrantLock();

	@SuppressWarnings("unchecked")
	public Map<String, Object> initiateSession(DigilockerInitiateSession digilockerinitiatesession) {

		Map<String, Object> map = new HashMap<>();

		String responseBody = "";
		try {

			String apiRequest = "{\r\n" + "  \"consent\": true,\r\n" + "  \"redirect_to_signup\": false,\r\n"
					+ "  \"abstract_access_token\": true,\r\n"
					+ "  \"reference_id\": \"ABCDjhgfdew3456789098uytre434erEF12345\",\r\n"
					+ "  \"consent_purpose\": \"for banking purpose only\",\r\n"
					+ "  \"redirect_url\": \"https://www.fidypay.com\"\r\n" + "}";

			LOGGER.info("ApiRequest: {}", apiRequest);

			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, apiRequest);

			Request request = new Request.Builder()
					.url(DecentroUtils.DECENTRO_API_BASE_URL + "v2/kyc/digilocker/initiate_session")
					.method("POST", body).addHeader("client_id", DecentroUtils.DECENTRO_CLIENT_ID)
					.addHeader("client_secret", DecentroUtils.DECENTRO_CLIENT_SECRET)
					.addHeader("module_secret", DecentroUtils.DECENTRO_MODULE_SECRET)
					.addHeader("Content-Type", "application/json").build();

			Response response = client.newCall(request).execute();
			responseBody = response.body().string();

			LOGGER.info("ApiResponse: {}", responseBody);

			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> mapObject = objectMapper.readValue(responseBody, Map.class);

			String result = (String) mapObject.get("status");
			String message = (String) mapObject.get("message");
			String merchantTxnId = (String) mapObject.get("decentroTxnId");

			if (result != null && result.contains("SUCCESS")) {
				Map<String, Object> data = (Map<String, Object>) mapObject.get("data");
				map.put("code", "0x200");
				map.put("merchantTxnId", merchantTxnId);
				map.put("description", message);
				map.put("merchantData", data);

			} else if (message.equals("Your IP address is not allowed")) {
				map.put("message", message);
			} else {

				map.put("code", "0x202");
				map.put("description", message);
				map.put("merchantTxnId", merchantTxnId);

			}
		} catch (IOException e) {
			LOGGER.error("IOException: {}", e);
		} catch (Exception e) {
			LOGGER.error("Exception: {}", e);
		}

		return map;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getAccessToken(DigilockerGetAccessToken digilockergetaccesstoken) {

		Map<String, Object> map = new HashMap<>();

		String responseBody = "";
		try {

			LOGGER.info("ApiRequest: {}", digilockergetaccesstoken);

			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, new Gson().toJson(digilockergetaccesstoken));

			Request request = new Request.Builder()
					.url(DecentroUtils.DECENTRO_API_BASE_URL + "v2/kyc/digilocker/access_token/code")
					.method("POST", body).addHeader("client_id", DecentroUtils.DECENTRO_CLIENT_ID)
					.addHeader("client_secret", DecentroUtils.DECENTRO_CLIENT_SECRET)
					.addHeader("module_secret", DecentroUtils.DECENTRO_MODULE_SECRET)
					.addHeader("Content-Type", "application/json").build();

			Response response = client.newCall(request).execute();
			responseBody = response.body().string();
			LOGGER.info("ApiResponse: {}", responseBody);

			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> mapObject = objectMapper.readValue(responseBody, Map.class);

			String result = (String) mapObject.get("status");
			String message = (String) mapObject.get("message");

			if (result != null && result.contains("SUCCESS")) {
				map.put("code", "0x200");
				Map<String, Object> data = (Map<String, Object>) mapObject.get("data");
				map.put("description", message);
				map.put("merchantData", data);

			} else {
				String merchantTxnId = (String) mapObject.get("decentroTxnId");
				map.put("code", "0x202");
				map.put("description", message);
				map.put("merchantTxnId", merchantTxnId);

			}
		} catch (IOException e) {
			LOGGER.error("IOException: {}", e);
		} catch (Exception e) {
			LOGGER.error("Exception: {}", e);
		}

		return map;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getIssuedFiles(DigilockerGetIssuedFiles digilockergetissuedfiles) {

		Map<String, Object> map = new HashMap<>();
		String responseBody = "";
		try {

			LOGGER.info("ApiRequest: {}", digilockergetissuedfiles);

			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, new Gson().toJson(digilockergetissuedfiles));

			Request request = new Request.Builder()
					.url(DecentroUtils.DECENTRO_API_BASE_URL + "v2/kyc/digilocker/access_token/code")
					.method("POST", body).addHeader("client_id", DecentroUtils.DECENTRO_CLIENT_ID)
					.addHeader("client_secret", DecentroUtils.DECENTRO_CLIENT_SECRET)
					.addHeader("module_secret", DecentroUtils.DECENTRO_MODULE_SECRET)
					.addHeader("Content-Type", "application/json").build();

			Response response = client.newCall(request).execute();
			responseBody = response.body().string();

			LOGGER.info("ApiResponse: {}", responseBody);

			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> mapObjcet = objectMapper.readValue(responseBody, Map.class);

			String result = (String) mapObjcet.get("status");
			String message = (String) mapObjcet.get("message");

			if (result != null && result.contains("SUCCESS")) {
				map.put("code", "0x200");
				Map<String, Object> data = (Map<String, Object>) mapObjcet.get("data");

				map.put("description", message);
				map.put("merchantData", data);

			} else if (message.equals("Your IP address is not allowed")) {
				map.put("message", message);
			} else {
				String merchantTxnId = (String) mapObjcet.get("decentroTxnId");
				map.put("code", "0x202");
				map.put("description", message);
				map.put("merchantTxnId", merchantTxnId);

			}
		} catch (IOException e) {
			LOGGER.error("IOException: {}", e);
		} catch (Exception e) {
			LOGGER.error("Exception: {}", e);
		}

		return map;
	}

	public synchronized Map<String, Object> saveDataForValidateUPI(String vpa, long merchantId,
			Double merchantFloatAmount, String businessName, String email) {

		LOGGER.info("Inside saveDataForValidateUPI");

		Map<String, Object> map = new HashMap<>();
		try {
			semaphore.acquire();
			lock.lock();

			String serviceName = "VALIDATE UPI";
			double amtInDouble = 1.50;

			if (!serviceInfoRepository.existsByServiceName(Encryption.encString(serviceName))) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Service does not exist.");
				return map;

			}

			if (!ekycService.checkServiceExistOrNot(merchantId, serviceName)) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
				return map;

			}

			if (vpa.isEmpty()) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, "vpa can not be empty.");
				return map;
			}

//			if (!vpa.matches(VPA_REGEX)) {
//				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
//				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
//				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.INVALID_VPA);
//				return map;
//			}

			String merchantTrxnRefId = RandomNumberGenrator.generateRandomStringRefId()
					+ RandomNumberGenrator.randomNumberGenerate(16);

			String trxnRefId = GenerateTrxnRefId.getTranRefID("API", "Wallet", "EKYC");
			String walletTxnRefNo = GenerateTrxnRefId.getTranRefID("API", "Wallet", "");

			ServiceInfo serviceInfo = serviceInfoRepository.findServiceByName(Encryption.encString(serviceName));
			Long serviceId = serviceInfo.getServiceId();

			MerchantService merchantsService = merchantServiceRepository.findByMerchantIdAndServiceId(merchantId,
					serviceId);
			Long merchantServiceId = merchantsService.getMerchantServiceId();
			String serviceType = merchantsService.getServiceType();
			LOGGER.info("serviceType: {}", serviceType);
			double charges = 0.0;
			double commission = 0.0;

			switch (serviceType) {

			case "Charge":
				charges = chargeService.getMerchantServiceChargesV2(merchantServiceId, amtInDouble);
				LOGGER.info("charges: " + charges);
				break;

			default:
				charges = amtInDouble;
				break;

			}

			String resWallet = walletNotification.checkWalletBalance(merchantFloatAmount, businessName, email);

			if (merchantFloatAmount < charges) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}

			LOGGER.info("serviceName {}", serviceName);
			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			EkycRequest ekycRequest = ekycRequestRepository
					.save(new EkycRequest(merchantId, merchantTrxnRefId, walletTxnRefNo, trxnDate));
			long ekycRequestId = ekycRequest.getRequestId();

//			Bank bank = new Bank();
//			ThreadWithdrawal test = new ThreadWithdrawal(bank, merchantTrxnRefId, charges, merchantFloatAmount,
//					merchantId, merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId, serviceName,
//					trxnRefId, walletTxnRefNo, serviceName);
//			LOGGER.info("Name of Thread -----------------------{}", test.getName());
//			test.start();

			WalletRequest debitRequest = new WalletRequest(merchantId, charges, "Debit", serviceName, serviceName,
					ekycRequestId, trxnRefId, merchantTrxnRefId, walletTxnRefNo);
			walletService.enqueueTransaction(debitRequest);

			map = validateUPI(vpa);

			String serviceProvider = ResponseMessage.DECENTRO;
			ServiceProviders spInfo = serviceprovidersrepository.findBySpName(Encryption.encString(serviceProvider));
			Long serviceProviderId = spInfo.getServiceProviderId();

			EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
					.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
							ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
							serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
							serviceProviderId, '0'));
			LOGGER.info("ekycTransactionDetails: {} ", ekycTransactionDetails);

//			EkycTransactionDetails elk = ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
//			LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'VALIDATE UPI' EKYC TRXN DETAILS : {}", elk);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info("Inside catch block ----------------------- ");
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		} finally {
			lock.unlock();
			semaphore.release();
		}
		return map;
	}

	public synchronized Map<String, Object> saveDataForValidateUPIV2(String vpa, long merchantId,
			Double merchantFloatAmount, String businessName, String email) {

		LOGGER.info("Inside saveDataForValidateUPIV2");

		Map<String, Object> map = new HashMap<>();
		try {
			semaphore.acquire();
			lock.lock();

			String serviceName = "VALIDATE UPI";
			double amtInDouble = 1.50;

			if (!serviceInfoRepository.existsByServiceName(Encryption.encString(serviceName))) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Service does not exist.");
				return map;

			}

			if (!ekycService.checkServiceExistOrNot(merchantId, serviceName)) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
				return map;

			}

			if (vpa.isEmpty()) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, "vpa can not be empty.");
				return map;
			}

			if (!vpa.matches(VPA_REGEX)) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.INVALID_VPA);
				return map;
			}

			String merchantTrxnRefId = RandomNumberGenrator.generateRandomStringRefId()
					+ RandomNumberGenrator.randomNumberGenerate(16);

			String trxnRefId = GenerateTrxnRefId.getTranRefID("API", "Wallet", "EKYC");
			String walletTxnRefNo = GenerateTrxnRefId.getTranRefID("API", "Wallet", "");

			ServiceInfo serviceInfo = serviceInfoRepository.findServiceByName(Encryption.encString(serviceName));
			Long serviceId = serviceInfo.getServiceId();

			MerchantService merchantsService = merchantServiceRepository.findByMerchantIdAndServiceId(merchantId,
					serviceId);
			Long merchantServiceId = merchantsService.getMerchantServiceId();
			String serviceType = merchantsService.getServiceType();
			LOGGER.info("serviceType: {}", serviceType);
			double charges = 0.0;
			double commission = 0.0;

			switch (serviceType) {

			case "Charge":
				charges = chargeService.getMerchantServiceChargesV2(merchantServiceId, amtInDouble);
				LOGGER.info("charges: " + charges);
				break;

			default:
				charges = amtInDouble;
				break;

			}

			String resWallet = walletNotification.checkWalletBalance(merchantFloatAmount, businessName, email);

			if (merchantFloatAmount < charges) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}

			LOGGER.info("serviceName {}", serviceName);
			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			EkycRequest ekycRequest = ekycRequestRepository
					.save(new EkycRequest(merchantId, merchantTrxnRefId, walletTxnRefNo, trxnDate));
			long ekycRequestId = ekycRequest.getRequestId();

//			Bank bank = new Bank();
//			ThreadWithdrawal test = new ThreadWithdrawal(bank, merchantTrxnRefId, charges, merchantFloatAmount,
//					merchantId, merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId, serviceName,
//					trxnRefId, walletTxnRefNo, serviceName);
//			LOGGER.info("Name of Thread -----------------------{}", test.getName());
//			test.start();

			WalletRequest debitRequest = new WalletRequest(merchantId, charges, "Debit", serviceName, serviceName,
					ekycRequestId, trxnRefId, merchantTrxnRefId, walletTxnRefNo);
			walletService.enqueueTransaction(debitRequest);

			map = validateUPI1(vpa, merchantTrxnRefId);

			String serviceProvider = ResponseMessage.DECENTRO;
			ServiceProviders spInfo = serviceprovidersrepository.findBySpName(Encryption.encString(serviceProvider));
			Long serviceProviderId = spInfo.getServiceProviderId();

			EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
					.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
							ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
							serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
							serviceProviderId, '0'));
			LOGGER.info("ekycTransactionDetails: {} ", ekycTransactionDetails);

//			EkycTransactionDetails elk = ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
//			LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'VALIDATE UPI' EKYC TRXN DETAILS : {}", elk);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info("Inside catch block ----------------------- ");
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		} finally {
			lock.unlock();
			semaphore.release();
		}
		return map;
	}

	private Map<String, Object> validateUPI1(String vpa, String merchantTrxnRefId) {

		String result = null;

		Map<String, Object> map = new HashMap<>();

		JSONObject responseJson = new JSONObject();

		String requestJson = "{\n" + "  \"upi_id\": \"" + vpa + "\",\n" + "  \"type\": \"basic\",\n"
				+ "  \"reference_id\": \"" + merchantTrxnRefId + "\"\n" + "}";

		LOGGER.info("requestJson------------->" + requestJson);

		try {
			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, requestJson);
			Request request = new Request.Builder().url("https://in.decentro.tech/v2/payments/vpa/validate")
					.method("POST", body).addHeader("accept", "application/json")
					.addHeader("client_id", DecentroUtils.DECENTRO_CLIENT_ID)
					.addHeader("client_secret", DecentroUtils.DECENTRO_CLIENT_SECRET)
					.addHeader("module_secret", VALIDATE_UPI_MODULE_SECRET)
					.addHeader("content-type", "application/json").build();
			Response response = client.newCall(request).execute();

			result = response.body().string();

			LOGGER.info("result------------->" + result);

			JSONObject resultJsonObject = new JSONObject(result);

			String status = resultJsonObject.getString("status");
			String responseCode = resultJsonObject.getString("responseCode");

			if (status.equalsIgnoreCase("SUCCESS") && responseCode.equalsIgnoreCase("S00000")) {

				JSONObject dataObject = resultJsonObject.getJSONObject("data");

				map = ESignServices.setResponse(ResponseMessage.SUCCESS, ResponseMessage.API_STATUS_SUCCESS,
						ResponseMessage.DATA_SUCCESS);

				responseJson.put("vpa", vpa);
				responseJson.put("name", dataObject.getString("name"));
				responseJson.put("Ifsc", "NA");
				responseJson.put("accountType", "NA");
				responseJson.put("vpaStatus", dataObject.getString("status"));
				map.put(ResponseMessage.DATA, responseJson.toMap());

				return map;

			} else if (status.equalsIgnoreCase("FAILURE") && responseCode.equalsIgnoreCase("E00029")) {

				map = ESignServices.setResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED,
						"Please connect with our team on tech.support@fidypay.com for credit renewal.");

			}

			else if (status.equalsIgnoreCase("FAILURE") && responseCode.equalsIgnoreCase("E00009")) {

				String message = resultJsonObject.getString("message");

				map = ESignServices.setResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED, message);

			}

			else {

				map = ESignServices.setResponse(ResponseMessage.SOMETHING_WENT_WRONG, ResponseMessage.API_STATUS_FAILED,
						ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);

			}

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Exception: {}", e);
			map = ESignServices.setResponse(ResponseMessage.SOMETHING_WENT_WRONG, ResponseMessage.API_STATUS_FAILED,
					ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		}

		return map;

	}

	private Map<String, Object> validateUPI(String vpa) {

		LOGGER.info("Inside validateUPI impl");

		Map<String, Object> map = new HashMap<>();

		JSONObject responseJson = new JSONObject();

		try {

			String validateUpiUrl = "https://prelive.fidypay.com/payin/validateVPA/v2/" + vpa;

			LOGGER.info("apiRequest: {}", validateUpiUrl);

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.set("accept", "*/*");

			HttpEntity<String> entity = new HttpEntity<>("", headers);
			ResponseEntity<String> response = restTemplate.exchange(validateUpiUrl, HttpMethod.POST, entity,
					String.class);
			String apiResponse = response.getBody();

			LOGGER.info("apiResponse: {}", apiResponse);

			JSONObject resultJsonObject = new JSONObject(apiResponse);

			String status = resultJsonObject.getString("status");

			if (status.equals("SUCCESS")) {

				JSONArray dataArray = resultJsonObject.getJSONArray("data");

				JSONObject dataObject = dataArray.getJSONObject(0);

				String customerName = dataObject.getString("customerName");

				map = ESignServices.setResponse(ResponseMessage.SUCCESS, ResponseMessage.API_STATUS_SUCCESS,
						ResponseMessage.DATA_SUCCESS);

				if (customerName.equals("0000")) {
					responseJson.put("vpa", vpa);
					responseJson.put("vpaStatus", ResponseMessage.INVALID_VPA);
					map.put(ResponseMessage.DATA, responseJson.toMap());
					return map;
				}

				responseJson.put("vpa", vpa);
				responseJson.put("name", customerName);
				responseJson.put("Ifsc", "NA");
				responseJson.put("accountType", "NA");
				responseJson.put("vpaStatus", "Valid");
				map.put(ResponseMessage.DATA, responseJson.toMap());
				return map;
			} else {
				map = ESignServices.setResponse(ResponseMessage.SUCCESS, ResponseMessage.API_STATUS_SUCCESS,
						ResponseMessage.DATA_SUCCESS);
				responseJson.put("vpa", vpa);
				responseJson.put("vpaStatus", ResponseMessage.INVALID_VPA);
				map.put(ResponseMessage.DATA, responseJson.toMap());
			}

		} catch (HttpClientErrorException e) {
			e.printStackTrace();
			String responeString = e.getResponseBodyAsString();

			LOGGER.info("HttpClientErrorException e: {}", responeString);

			map = ESignServices.setResponse(ResponseMessage.SUCCESS, ResponseMessage.API_STATUS_SUCCESS,
					ResponseMessage.DATA_SUCCESS);
			responseJson.put("vpa", vpa);
			responseJson.put("vpaStaus", ResponseMessage.INVALID_VPA);
			map.put(ResponseMessage.DATA, responseJson.toMap());

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Exception: {}", e);
			map = ESignServices.setResponse(ResponseMessage.SOMETHING_WENT_WRONG, ResponseMessage.API_STATUS_FAILED,
					ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		}
		return map;
	}

	public synchronized Map<String, Object> saveDataForSynchronousAdvancedCkyc(
			AdvancedCKycDownloadRequest advancedCKycDownloadRequest, long merchantId, Double merchantFloatAmount,
			String businessName, String email) {

		LOGGER.info("Inside saveDataForSynchronousAdvancedCkyc");
		Map<String, Object> map = new HashMap<>();
		try {
			semaphore.acquire();
			lock.lock();
			String serviceName = "SYNCHRONOUS ADVANCED CKYC";
			double amtInDouble = 30.0;

			if (!advancedCKycDownloadRequest.getDob().matches(DOB)) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Invalid dob.");
				return map;

			}

			if (!serviceInfoRepository.existsByServiceName(Encryption.encString(serviceName))) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Service does not exist.");
				return map;

			}

			if (!ekycService.checkServiceExistOrNot(merchantId, serviceName)) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
				return map;

			}

			String merchantTrxnRefId = RandomNumberGenrator.generateRandomStringRefId()
					+ RandomNumberGenrator.randomNumberGenerate(16);

			String trxnRefId = GenerateTrxnRefId.getTranRefID("API", "Wallet", "EKYC");
			String walletTxnRefNo = GenerateTrxnRefId.getTranRefID("API", "Wallet", "");

			ServiceInfo serviceInfo = serviceInfoRepository.findServiceByName(Encryption.encString(serviceName));
			Long serviceId = serviceInfo.getServiceId();

			MerchantService merchantsService = merchantServiceRepository.findByMerchantIdAndServiceId(merchantId,
					serviceId);
			Long merchantServiceId = merchantsService.getMerchantServiceId();
			String serviceType = merchantsService.getServiceType();
			LOGGER.info("serviceType: {}", serviceType);
			double charges = 0.0;
			double commission = 0.0;

			switch (serviceType) {

			case "Charge":
				charges = chargeService.getMerchantServiceChargesV2(merchantServiceId, amtInDouble);
				LOGGER.info("charges: " + charges);
				break;

			default:
				charges = amtInDouble;
				break;

			}

			String resWallet = walletNotification.checkWalletBalance(merchantFloatAmount, businessName, email);

			if (merchantFloatAmount < charges) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}

			LOGGER.info("serviceName {}", serviceName);
			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			EkycRequest ekycRequest = ekycRequestRepository
					.save(new EkycRequest(merchantId, merchantTrxnRefId, walletTxnRefNo, trxnDate));
			long ekycRequestId = ekycRequest.getRequestId();

//			Bank bank = new Bank();
//			ThreadWithdrawal test = new ThreadWithdrawal(bank, merchantTrxnRefId, charges, merchantFloatAmount,
//					merchantId, merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId, serviceName,
//					trxnRefId, walletTxnRefNo, serviceName);
//			LOGGER.info("Name of Thread -----------------------{}", test.getName());
//			test.start();

			WalletRequest debitRequest = new WalletRequest(merchantId, charges, "Debit", serviceName, serviceName,
					ekycRequestId, trxnRefId, merchantTrxnRefId, walletTxnRefNo);
			walletService.enqueueTransaction(debitRequest);

			map = synchronousAdvancedCkyc(advancedCKycDownloadRequest, merchantTrxnRefId);

			String serviceProvider = ResponseMessage.DECENTRO;
			ServiceProviders spInfo = serviceprovidersrepository.findBySpName(Encryption.encString(serviceProvider));
			Long serviceProviderId = spInfo.getServiceProviderId();

			EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
					.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
							ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
							serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
							serviceProviderId, '0'));
			LOGGER.info("ekycTransactionDetails: {} ", ekycTransactionDetails);

//			EkycTransactionDetails elk = ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
//			LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'SYNCHRONOUS ADVANCED CKYC' EKYC TRXN DETAILS : {}", elk);
		} catch (Exception e) {
			LOGGER.info("Inside catch block ----------------------- ");
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		} finally {
			lock.unlock();
			semaphore.release();
		}
		return map;
	}

	private Map<String, Object> synchronousAdvancedCkyc(AdvancedCKycDownloadRequest advancedCKycDownloadRequest,
			String merchantTrxnRefId) {

		Map<String, Object> map = new HashMap<>();

		try {

			String name = advancedCKycDownloadRequest.getName();
			String documentType = advancedCKycDownloadRequest.getDocumentType();
			String documentId = advancedCKycDownloadRequest.getDocumentId();
			String gender = advancedCKycDownloadRequest.getGender();
			String dob = advancedCKycDownloadRequest.getDob();

			map = searchCKyc(name, documentType, documentId, gender, dob, merchantTrxnRefId);

			if (map.containsKey("code")) {
				return map;
			}

			if (map.containsKey("message")) {

				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, map.get("message"));
				map.remove("message");
				return map;
			}

			String ckycId = (String) map.get("ckycId");
			map.remove("ckycId");
			dob = DateAndTime.formatDate1(dob);

			String apiRequest = "{\r\n    \"reference_id\": \"" + merchantTrxnRefId
					+ "\",\r\n    \"hyperstream\": \"ADVANCED_CKYC_DOWNLOAD\",\r\n    \"initial_input\": {\r\n        \"id_number\": \""
					+ ckycId + "\",\r\n        \"auth_factor_type\": 1,\r\n        \"auth_factor\": \"" + dob
					+ "\"\r\n    },\r\n    \"consent\": true,\r\n    \"consent_purpose\": \"Some Consent purpose\"\r\n}";

			LOGGER.info("apiRequest: {}", apiRequest);

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
			headers.set("client_id", CLIENT_ID);
			headers.set("client_secret", CLIENT_SECRET);
			headers.set("module_secret", KYC_AND_ONBOARDING_SECRET);

			HttpEntity<String> entity = new HttpEntity<>(apiRequest, headers);
			ResponseEntity<String> response = restTemplate.exchange(HYPERSTREAM_SYNCHRONOUS_URL, HttpMethod.POST,
					entity, String.class);
			String apiResponse = response.getBody();

			LOGGER.info("apiResponse: {}", apiResponse);

			JSONObject resultJsonObject = new JSONObject(apiResponse);

			String status = resultJsonObject.getString("status");
			String message = null;
			if (status.equals("SUCCESS")) {
				JSONObject data = resultJsonObject.getJSONObject("data");
				JSONObject advancedCKycDownload = data.getJSONObject("ADVANCED_CKYC_DOWNLOAD");
				advancedCKycDownload.remove("image_details");
				message = resultJsonObject.getString("message");
				map = ESignServices.setResponse(ResponseMessage.SUCCESS, ResponseMessage.API_STATUS_SUCCESS, message);
				map.put("merchantTxnRefId", merchantTrxnRefId);
				map.put("Data", advancedCKycDownload.toMap());
				return map;
			} else {
				message = resultJsonObject.getString("message");
				map = ESignServices.setResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED, message);
				map.put("Data", resultJsonObject.getJSONObject("data").toMap());
				map.put("merchantTxnRefId", merchantTrxnRefId);
			}

		} catch (HttpClientErrorException e) {
			String responeString = e.getResponseBodyAsString();
			LOGGER.error("Exception: {}", responeString);
			JSONObject errorJsonObject = new JSONObject(responeString);
			String message = errorJsonObject.getString("message");
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, message);

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Exception: {}", e);
			map = ESignServices.setResponse(ResponseMessage.SOMETHING_WENT_WRONG, ResponseMessage.API_STATUS_FAILED,
					ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		}
		return map;
	}

	public synchronized Map<String, Object> saveDataForAsynchronousAdvancedCkyc(
			AdvancedCKycDownloadRequest advancedCKycDownloadRequest, long merchantId, Double merchantFloatAmount,
			String businessName, String email) {

		LOGGER.info("Inside saveDataForSynchronousAdvancedCkyc");
		Map<String, Object> map = new HashMap<>();
		try {
			semaphore.acquire();
			lock.lock();
			String serviceName = "ASYNCHRONOUS ADVANCED CKYC";
			double amtInDouble = 30.0;

			if (!advancedCKycDownloadRequest.getDob().matches(DOB)) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Invalid dob.");
				return map;
			}

			if (!serviceInfoRepository.existsByServiceName(Encryption.encString(serviceName))) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Service does not exist.");
				return map;
			}

			if (!ekycService.checkServiceExistOrNot(merchantId, serviceName)) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
				return map;
			}

			String merchantTrxnRefId = RandomNumberGenrator.generateRandomStringRefId()
					+ RandomNumberGenrator.randomNumberGenerate(16);

			String trxnRefId = GenerateTrxnRefId.getTranRefID("API", "Wallet", "EKYC");
			String walletTxnRefNo = GenerateTrxnRefId.getTranRefID("API", "Wallet", "");

			ServiceInfo serviceInfo = serviceInfoRepository.findServiceByName(Encryption.encString(serviceName));
			Long serviceId = serviceInfo.getServiceId();

			MerchantService merchantsService = merchantServiceRepository.findByMerchantIdAndServiceId(merchantId,
					serviceId);
			Long merchantServiceId = merchantsService.getMerchantServiceId();
			String serviceType = merchantsService.getServiceType();
			LOGGER.info("serviceType: {}", serviceType);
			double charges = 0.0;
			double commission = 0.0;

			switch (serviceType) {

			case "Charge":
				charges = chargeService.getMerchantServiceChargesV2(merchantServiceId, amtInDouble);
				LOGGER.info("charges: " + charges);
				break;

			default:
				charges = amtInDouble;
				break;

			}

			String resWallet = walletNotification.checkWalletBalance(merchantFloatAmount, businessName, email);

			if (merchantFloatAmount < charges) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}

			LOGGER.info("serviceName {}", serviceName);
			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			EkycRequest ekycRequest = ekycRequestRepository
					.save(new EkycRequest(merchantId, merchantTrxnRefId, walletTxnRefNo, trxnDate));
			long ekycRequestId = ekycRequest.getRequestId();

//			Bank bank = new Bank();
//			ThreadWithdrawal test = new ThreadWithdrawal(bank, merchantTrxnRefId, charges, merchantFloatAmount,
//					merchantId, merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId, serviceName,
//					trxnRefId, walletTxnRefNo, serviceName);
//			LOGGER.info("Name of Thread -----------------------{}", test.getName());
//			test.start();

			WalletRequest debitRequest = new WalletRequest(merchantId, charges, "Debit", serviceName, serviceName,
					ekycRequestId, trxnRefId, merchantTrxnRefId, walletTxnRefNo);
			walletService.enqueueTransaction(debitRequest);

			map = asynchronousAdvancedCkyc(advancedCKycDownloadRequest, merchantTrxnRefId);

			String serviceProvider = ResponseMessage.DECENTRO;
			ServiceProviders spInfo = serviceprovidersrepository.findBySpName(Encryption.encString(serviceProvider));
			Long serviceProviderId = spInfo.getServiceProviderId();

			EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
					.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
							ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
							serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
							serviceProviderId, '0'));
			LOGGER.info("ekycTransactionDetails: {} ", ekycTransactionDetails);

//			EkycTransactionDetails elk = ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
//			LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'SYNCHRONOUS ADVANCED CKYC' EKYC TRXN DETAILS : {}", elk);
		} catch (Exception e) {
			LOGGER.info("Inside catch block ----------------------- ");
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		} finally {
			lock.unlock();
			semaphore.release();
		}
		return map;
	}

	private Map<String, Object> asynchronousAdvancedCkyc(AdvancedCKycDownloadRequest advancedCKycDownloadRequest,
			String merchantTrxnRefId) {

		Map<String, Object> map = new HashMap<>();

		try {

			String name = advancedCKycDownloadRequest.getName();
			String documentType = advancedCKycDownloadRequest.getDocumentType();
			String documentId = advancedCKycDownloadRequest.getDocumentId();
			String gender = advancedCKycDownloadRequest.getGender();
			String dob = advancedCKycDownloadRequest.getDob();
			String callbackUrl = advancedCKycDownloadRequest.getCallbackURL();

			map = searchCKyc(name, documentType, documentId, gender, dob, merchantTrxnRefId);

			if (map.containsKey("code")) {
				return map;
			}

			if (map.containsKey("message")) {

				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.USER_NOT_FOUND);
				map.remove("message");
				return map;
			}

			String ckycId = (String) map.get("ckycId");

			dob = DateAndTime.formatDate1(dob);

			String apiRequest = "{\r\n    \"reference_id\": \"" + merchantTrxnRefId
					+ "\",\r\n    \"hyperstream\": \"ADVANCED_CKYC_DOWNLOAD\",\r\n    \"callback_url\": \""
					+ callbackUrl + "\",\r\n    \"initial_input\": {\r\n        \"id_number\": \"" + ckycId
					+ "\",\r\n        \"auth_factor_type\": 1,\r\n        \"auth_factor\": \"" + dob
					+ "\"\r\n    },\r\n    \"progress_report\": true,\r\n    \"checkpoint_results\": true,\r\n    \"consent\": true,\r\n    \"consent_purpose\": \"Some valid consent purpose\"\r\n}";

			LOGGER.info("apiRequest: {}", apiRequest);

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
			headers.set("client_id", CLIENT_ID);
			headers.set("client_secret", CLIENT_SECRET);
			headers.set("module_secret", KYC_AND_ONBOARDING_SECRET);

			HttpEntity<String> entity = new HttpEntity<>(apiRequest, headers);
			ResponseEntity<String> response = restTemplate.exchange(HYPERSTREAM_ASYNCHRONOUS_URL, HttpMethod.POST,
					entity, String.class);
			String apiResponse = response.getBody();

			LOGGER.info("apiResponse: {}", apiResponse);

			JSONObject resultJsonObject = new JSONObject(apiResponse);

			String status = resultJsonObject.getString("status");
			String message = null;
			if (status.equals("SUCCESS")) {
//				JSONObject data = resultJsonObject.getJSONObject("data");
//				JSONObject advancedCKycDownload = data.getJSONObject("ADVANCED_CKYC_DOWNLOAD");
//				advancedCKycDownload.remove("image_details");
				message = resultJsonObject.getString("message");
				map = ESignServices.setResponse(ResponseMessage.SUCCESS, ResponseMessage.API_STATUS_SUCCESS, message);
				map.put("merchantTxnRefId", merchantTrxnRefId);
//				map.put("Data", advancedCKycDownload.toMap());
				return map;
			} else {
				message = resultJsonObject.getString("message");
				map = ESignServices.setResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED, message);
				map.put("Data", resultJsonObject.getJSONObject("data").toMap());
				map.put("merchantTxnRefId", merchantTrxnRefId);
			}

		} catch (HttpClientErrorException e) {
			String responeString = e.getResponseBodyAsString();
			JSONObject errorJsonObject = new JSONObject(responeString);
			String message = errorJsonObject.getString("message");
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, message);

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Exception: {}", e);
			map = ESignServices.setResponse(ResponseMessage.SOMETHING_WENT_WRONG, ResponseMessage.API_STATUS_FAILED,
					ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		}
		return map;
	}

	private Map<String, Object> searchCKyc(String name, String documentType, String idNumber, String gender, String dob,
			String merchantTrxnRefId) {

		Map<String, Object> map = new HashMap<>();

		map = ValidateUtils.validateCKycRequest(name, documentType, idNumber, gender, dob);

		if (!map.isEmpty()) {

			if (map.containsKey("aadhaarNumber")) {
				idNumber = (String) map.get("aadhaarNumber");
				map.remove("aadhaarNumber");
			} else {
				return map;
			}

		}

		String message = "Not Found";
		try {

			String apiRequest = "{\r\n    \"consent\": true,\r\n    \"reference_id\": \"" + merchantTrxnRefId
					+ "\",\r\n    \"document_type\": \"" + documentType + "\",\r\n    \"id_number\": \"" + idNumber
					+ "\",\r\n    \"consent_purpose\": \"For fetching CKYC Details\"\r\n}";

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
			headers.set("client_id", CLIENT_ID);
			headers.set("client_secret", CLIENT_SECRET);
			headers.set("module_secret", KYC_AND_ONBOARDING_SECRET);

			HttpEntity<String> entity = new HttpEntity<>(apiRequest, headers);
			ResponseEntity<String> response = restTemplate.exchange(SEARCH_CKYC_URL, HttpMethod.POST, entity,
					String.class);
			String apiResponse = response.getBody();

			LOGGER.info("searchCKyc apiResponse: {}", apiResponse);

			JSONObject resultJsonObject = new JSONObject(apiResponse);

			String status = resultJsonObject.getString("status");

			if (status.equals("SUCCESS")) {
				JSONObject data = resultJsonObject.getJSONObject("data");
				if (data.has("kycResult")) {
					JSONObject kycResult = data.getJSONObject("kycResult");
					message = kycResult.getString("ckycId");
					map.put("ckycId", message);
				} else {
					message = data.getString("message");
					map.put("message", message);
				}

			}
		} catch (HttpClientErrorException e) {
			String responeString = e.getResponseBodyAsString();
			JSONObject errorJsonObject = new JSONObject(responeString);
			JSONObject dataJsonObject = errorJsonObject.getJSONObject("data");
			message = dataJsonObject.getString("message");
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, "No records found for the given ID.");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	public synchronized Map<String, Object> saveDataForPhoneTOPanDetails(PanToMobileRequest panToMobileRequest,
			long merchantId, Double merchantFloatAmount, String businessName, String email) {
		LOGGER.info("Inside saveDataForPhoneTOPanDetails");

		Map<String, Object> map = new HashMap<>();
		try {
			semaphore.acquire();
			lock.lock();
			String serviceName = "MOBILE TO PAN DETAILS";
			double amtInDouble = 15.0;

			map = ValidateUtils.validateUser(panToMobileRequest);

			if (!map.isEmpty()) {
				return map;
			}

			if (!serviceInfoRepository.existsByServiceName(Encryption.encString(serviceName))) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Service does not exist.");
				return map;

			}

			if (!ekycService.checkServiceExistOrNot(merchantId, serviceName)) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
				return map;

			}

			String merchantTrxnRefId = RandomNumberGenrator.generateRandomStringRefId()
					+ RandomNumberGenrator.randomNumberGenerate(16);

			String trxnRefId = GenerateTrxnRefId.getTranRefID("API", "Wallet", "EKYC");
			String walletTxnRefNo = GenerateTrxnRefId.getTranRefID("API", "Wallet", "");

			ServiceInfo serviceInfo = serviceInfoRepository.findServiceByName(Encryption.encString(serviceName));
			Long serviceId = serviceInfo.getServiceId();

			MerchantService merchantsService = merchantServiceRepository.findByMerchantIdAndServiceId(merchantId,
					serviceId);
			Long merchantServiceId = merchantsService.getMerchantServiceId();
			String serviceType = merchantsService.getServiceType();
			LOGGER.info("serviceType: {}", serviceType);
			double charges = 0.0;
			double commission = 0.0;

			switch (serviceType) {

			case "Charge":
				charges = chargeService.getMerchantServiceChargesV2(merchantServiceId, amtInDouble);
				LOGGER.info("charges: " + charges);
				break;

			default:
				charges = amtInDouble;
				break;

			}

			String resWallet = walletNotification.checkWalletBalance(merchantFloatAmount, businessName, email);

			if (merchantFloatAmount < charges) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}
			LOGGER.info("serviceName {}", serviceName);
			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			EkycRequest ekycRequest = ekycRequestRepository
					.save(new EkycRequest(merchantId, merchantTrxnRefId, walletTxnRefNo, trxnDate));
			long ekycRequestId = ekycRequest.getRequestId();

//			Bank bank = new Bank();
//			ThreadWithdrawal test = new ThreadWithdrawal(bank, merchantTrxnRefId, charges, merchantFloatAmount,
//					merchantId, merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId, serviceName,
//					trxnRefId, walletTxnRefNo, serviceName);
//			LOGGER.info("Name of Thread -----------------------{}", test.getName());
//			test.start();

			WalletRequest debitRequest = new WalletRequest(merchantId, charges, "Debit", serviceName, serviceName,
					ekycRequestId, trxnRefId, merchantTrxnRefId, walletTxnRefNo);
			walletService.enqueueTransaction(debitRequest);

			map = phoneTOPanDetails(panToMobileRequest, merchantTrxnRefId);

			String serviceProvider = ResponseMessage.DECENTRO;
			ServiceProviders spInfo = serviceprovidersrepository.findBySpName(Encryption.encString(serviceProvider));
			Long serviceProviderId = spInfo.getServiceProviderId();

			EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
					.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
							ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
							serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
							serviceProviderId, '0'));
			LOGGER.info("ekycTransactionDetails: {} ", ekycTransactionDetails);

//			EkycTransactionDetails elk = ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
//			LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'PHONE OTP PAN DETAILS' EKYC TRXN DETAILS : {}", elk);

		} catch (Exception e) {
			LOGGER.info("Inside catch block ----------------------- ");
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		} finally {
			lock.unlock();
			semaphore.release();
		}
		return map;
	}

	private Map<String, Object> phoneTOPanDetails(PanToMobileRequest panToMobileRequest, String merchantTrxnRefId) {

		Map<String, Object> map = new HashMap<>();

		try {

			String name = panToMobileRequest.getName();
			String mobileNo = panToMobileRequest.getMobile();

			String apiRequest = "{\n" + "    \"reference_id\": \"" + merchantTrxnRefId + "\",\n"
					+ "    \"hyperstream\": \"CKYC_PREFILL\",\n" + "    \"consent\": true,\n"
					+ "    \"consent_purpose\": \"To onboard user into platform\",\n" + "    \"initial_input\": {\n"
					+ "        \"full_name\": \"" + name + "\",\n" + "        \"mobile_number\": \"" + mobileNo + "\"\n"
					+ "    }\n" + "}";

			LOGGER.info("apiRequest: {}", apiRequest);

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
			headers.set("client_id", DecentroUtils.DECENTRO_CLIENT_ID);
			headers.set("client_secret", DecentroUtils.DECENTRO_CLIENT_SECRET);
			headers.set("module_secret", DecentroUtils.DECENTRO_MODULE_SECRET);

			HttpEntity<String> entity = new HttpEntity<>(apiRequest, headers);
			ResponseEntity<String> response = restTemplate.exchange(PHONE_TO_MOBILE_URL, HttpMethod.POST, entity,
					String.class);
			String apiResponse = response.getBody();

			LOGGER.info("apiResponse: {}", apiResponse);

			JSONObject resultJsonObject = new JSONObject(apiResponse);

			String status = resultJsonObject.getString("status");
			String message = null;
			if (status.equals("SUCCESS")) {
				JSONObject data = resultJsonObject.getJSONObject("data");
				JSONObject advancedCKycDownload = data.getJSONObject("CKYC_SEARCH_AND_DOWNLOAD");
				advancedCKycDownload.remove("image_details");
				message = resultJsonObject.getString("message");
				map = ESignServices.setResponse(ResponseMessage.SUCCESS, ResponseMessage.API_STATUS_SUCCESS, message);
				map.put("merchantTxnRefId", merchantTrxnRefId);
				map.put("Data", advancedCKycDownload.toMap());
				return map;
			} else {
				message = resultJsonObject.getString("message");
				map = ESignServices.setResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED, message);
				map.put("Data", resultJsonObject.getJSONObject("data").toMap());
				map.put("merchantTxnRefId", merchantTrxnRefId);
			}

		} catch (HttpClientErrorException e) {
			String responeString = e.getResponseBodyAsString();
			JSONObject errorJsonObject = new JSONObject(responeString);
			String message = errorJsonObject.getString("message");
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, message);

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Exception: {}", e);
			map = ESignServices.setResponse(ResponseMessage.SOMETHING_WENT_WRONG, ResponseMessage.API_STATUS_FAILED,
					ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		}
		return map;
	}

	public Map<String, Object> validatePan(String panNumber, String merchantTrxnRefId) {

		Map<String, Object> map = new HashMap<>();

		try {

			String apiRequest = "{\n" + "  \"randomUUID\": \"" + merchantTrxnRefId + "\",\n"
					+ "  \"randomStr\": \"6fb9dca859\",\n" + "  \"reference_id\": \"" + merchantTrxnRefId + "\",\n"
					+ "  \"document_type\": \"PAN\",\n" + "  \"id_number\": \"" + panNumber + "\",\n"
					+ "  \"consent\": \"Y\",\n" + "  \"dob\": \"1900-01-25\",\n"
					+ "  \"consent_purpose\": \"For bank account purpose only\"\n" + "}";

			LOGGER.info("apiRequest: {}", apiRequest);

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
			headers.set("client_id", DecentroUtils.DECENTRO_CLIENT_ID);
			headers.set("client_secret", DecentroUtils.DECENTRO_CLIENT_SECRET);
			headers.set("module_secret", DecentroUtils.DECENTRO_MODULE_SECRET);

			HttpEntity<String> entity = new HttpEntity<>(apiRequest, headers);
			ResponseEntity<String> response = restTemplate.exchange(
					DecentroUtils.DECENTRO_API_BASE_URL + "kyc/public_registry/validate", HttpMethod.POST, entity,
					String.class);
			String apiResponse = response.getBody();

			LOGGER.info("apiResponse: {}", apiResponse);

			JSONObject resultJsonObject = new JSONObject(apiResponse);

			String status = resultJsonObject.getString("status");
			String kycStatus = resultJsonObject.getString("kycStatus");

			if (status.equals("SUCCESS") && kycStatus.equals("SUCCESS")) {
				JSONObject kycResult = resultJsonObject.getJSONObject("kycResult");
				map = ESignServices.setResponse(ResponseMessage.SUCCESS, ResponseMessage.API_STATUS_SUCCESS,
						ResponseMessage.DATA_SUCCESS);
				map.put("name", kycResult.get("name"));
				map.put("panStatus", kycResult.get("idStatus"));
				map.put("panNumber", panNumber);
				map.put("merchantTxnRefId", merchantTrxnRefId);
				return map;
			} else {
				map = ESignServices.setResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED,
						ResponseMessage.DATA_NOT_FOUND);
				map.put("panNumber", panNumber);
				map.put("merchantTxnRefId", merchantTrxnRefId);
			}

		} catch (HttpClientErrorException e) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Exception: {}", e);
			map = ESignServices.setResponse(ResponseMessage.SOMETHING_WENT_WRONG, ResponseMessage.API_STATUS_FAILED,
					ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		}
		return map;
	}

	public Map<String, Object> verifyVoterID(String voterId, String merchantTrxnRefId) {

		Map<String, Object> map = new HashMap<>();

		try {

			String apiRequest = "{\n" + "  \"randomUUID\": \"" + merchantTrxnRefId + "\",\n"
					+ "  \"randomStr\": \"6fb9dca859\",\n" + "  \"reference_id\": \"" + merchantTrxnRefId + "\",\n"
					+ "  \"document_type\": \"VOTERID\",\n" + "  \"id_number\": \"" + voterId + "\",\n"
					+ "  \"consent\": \"Y\",\n" + "  \"dob\": \"1900-01-25\",\n"
					+ "  \"consent_purpose\": \"For bank account purpose only\"\n" + "}";

			LOGGER.info("apiRequest: {}", apiRequest);

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
			headers.set("client_id", DecentroUtils.DECENTRO_CLIENT_ID);
			headers.set("client_secret", DecentroUtils.DECENTRO_CLIENT_SECRET);
			headers.set("module_secret", DecentroUtils.DECENTRO_MODULE_SECRET);
//			headers.set("client_id", "fidypay_staging");
//			headers.set("client_secret", "dRr5IqBh5L4wQ2GYmst5iAxYLcWCLXEN");
//			headers.set("module_secret", "OLD006mfTYAF4Zgco4rZkL0SkIPc6vtN");

			HttpEntity<String> entity = new HttpEntity<>(apiRequest, headers);
			ResponseEntity<String> response = restTemplate.exchange(
					DecentroUtils.DECENTRO_API_BASE_URL + "kyc/public_registry/validate", HttpMethod.POST, entity,
					String.class);
//			ResponseEntity<String> response = restTemplate.exchange(
//					"https://in.staging.decentro.tech/kyc/public_registry/validate", HttpMethod.POST, entity,
//					String.class);
			String apiResponse = response.getBody();

			LOGGER.info("apiResponse: {}", apiResponse);

			JSONObject resultJsonObject = new JSONObject(apiResponse);

			org.json.JSONObject responseJsonObject = new org.json.JSONObject();
			String status = resultJsonObject.getString("status");
			String kycStatus = resultJsonObject.getString("kycStatus");

			if (status.equals("SUCCESS") && kycStatus.equals("SUCCESS")) {

				JSONObject kycResult = resultJsonObject.getJSONObject("kycResult");
				JSONObject pollingBoothDetails = kycResult.getJSONObject("pollingBoothDetails");
				JSONObject address = kycResult.getJSONObject("address");

				String slNoInPart = String.valueOf(kycResult.get("cardSerialNumberInPollingList"));
				String sectionNo = String.valueOf(kycResult.get("sectionOfConstituencyPart"));
				String age = String.valueOf(kycResult.get("age"));
				String partNo = String.valueOf(pollingBoothDetails.get("number"));

				responseJsonObject.put("slNoInPart", slNoInPart);
				responseJsonObject.put("gender", kycResult.get("gender"));
				responseJsonObject.put("psLatLong", pollingBoothDetails.get("latLong"));
				responseJsonObject.put("rlnType", kycResult.get("relativeRelationType"));
				responseJsonObject.put("sectionNo", sectionNo);
				responseJsonObject.put("partName", kycResult.get("partOrLocationInConstituency"));
				responseJsonObject.put("epicNo", kycResult.get("epicNo"));
				responseJsonObject.put("acName", kycResult.get("assemblyConstituency"));
				responseJsonObject.put("pin", "");
				responseJsonObject.put("nameV3", "");
				responseJsonObject.put("nameV2", "");
				responseJsonObject.put("houseNo", kycResult.get("houseNumber"));
				responseJsonObject.put("nameV1", kycResult.get("nameInVernacular"));
				responseJsonObject.put("rlnName", kycResult.get("relativeName"));
				responseJsonObject.put("id", "");
				responseJsonObject.put("state", address.get("state"));
				responseJsonObject.put("pcName", kycResult.get("parliamentaryConstituency"));
				responseJsonObject.put("acNo", "");
				responseJsonObject.put("stCode", address.get("stateCode"));
				responseJsonObject.put("rlnNameV2", "");
				responseJsonObject.put("rlnNameV1", kycResult.get("relativeNameInVernacular"));
				responseJsonObject.put("rlnNameV3", "");
				responseJsonObject.put("psName", pollingBoothDetails.get("name"));
				responseJsonObject.put("dob", kycResult.get("dateOfBirth"));
				responseJsonObject.put("district", address.get("districtName"));
				responseJsonObject.put("lastUpdate", kycResult.get("lastUpdateDate"));
				responseJsonObject.put("name", kycResult.get("name"));
				responseJsonObject.put("age", age);
				responseJsonObject.put("partNo", partNo);

				map = ESignServices.setResponse(ResponseMessage.SUCCESS, ResponseMessage.API_STATUS_SUCCESS,
						ResponseMessage.DATA_SUCCESS);
				map.put("result", responseJsonObject.toMap());
				return map;

			} else {
				map = ESignServices.setResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED,
						ResponseMessage.DATA_NOT_FOUND);
			}

		} catch (HttpClientErrorException e) {

			map = ESignServices.setResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED,
					ResponseMessage.DATA_NOT_FOUND);

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Exception: {}", e);
			map = ESignServices.setResponse(ResponseMessage.SOMETHING_WENT_WRONG, ResponseMessage.API_STATUS_FAILED,
					ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		}
		return map;
	}

	public Map<String, Object> verifyDrivingLicense(String drivingLicenseNumber, String dob, String merchantTrxnRefId)
			throws Exception {

		Map<String, Object> map = new HashMap<>();

		dob = DateAndTime.formatDate1(dob);

		try {

			String apiRequest = "{\n" + "  \"randomUUID\": \"" + merchantTrxnRefId + "\",\n"
					+ "  \"randomStr\": \"6fb9dca859\",\n" + "  \"reference_id\": \"" + merchantTrxnRefId + "\",\n"
					+ "  \"document_type\": \"DRIVING_LICENSE\",\n" + "  \"id_number\": \"" + drivingLicenseNumber
					+ "\",\n" + "  \"consent\": \"Y\",\n" + "  \"dob\": \"" + dob + "\",\n"
					+ "  \"consent_purpose\": \"For bank account purpose only\"\n" + "}";

			LOGGER.info("apiRequest: {}", apiRequest);

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
			headers.set("client_id", DecentroUtils.DECENTRO_CLIENT_ID);
			headers.set("client_secret", DecentroUtils.DECENTRO_CLIENT_SECRET);
			headers.set("module_secret", DecentroUtils.DECENTRO_MODULE_SECRET);

			HttpEntity<String> entity = new HttpEntity<>(apiRequest, headers);

			ResponseEntity<String> response = restTemplate.exchange(
					DecentroUtils.DECENTRO_API_BASE_URL + "kyc/public_registry/validate", HttpMethod.POST, entity,
					String.class);
			String apiResponse = response.getBody();

			LOGGER.info("apiResponse: {}", apiResponse);

			JSONObject resultJsonObject = new JSONObject(apiResponse);

			String status = resultJsonObject.getString("status");
			String kycStatus = resultJsonObject.getString("kycStatus");

			if (status.equals("SUCCESS") && kycStatus.equals("SUCCESS")) {

				JSONObject kycResult = resultJsonObject.getJSONObject("kycResult");

				JSONArray address = kycResult.getJSONArray("addresses");
				JSONObject address1 = address.getJSONObject(0);

				Map<String, Object> jsonMap = new HashMap<>();

				// Create address map
				Map<String, Object> addressMap = new HashMap<>();
				addressMap.put("addressLine1", address1.get("addressLine"));
				addressMap.put("state", address1.get("state"));
				addressMap.put("district", address1.get("district"));
				addressMap.put("pin", address1.get("pin"));
				addressMap.put("completeAddress", address1.get("completeAddress"));
				addressMap.put("country", address1.get("country"));
				addressMap.put("type", address1.get("type"));

				// Create covDetails list
				List<Map<String, Object>> covDetailsList = new ArrayList<>();
				Map<String, Object> cov1 = new HashMap<>();

				cov1.put("cov", "");
				cov1.put("issueDate", "");
				Map<String, Object> cov2 = new HashMap<>();
				cov2.put("cov", "");
				cov2.put("issueDate", "");
				covDetailsList.add(cov1);
				covDetailsList.add(cov2);

				// Populate the map with sample values
				jsonMap.put("result", new HashMap<String, Object>() {
					{
						put("dlNumber", kycResult.get("drivingLicenseNumber"));
						put("name", kycResult.get("name"));
						put("address", new ArrayList<Map<String, Object>>() {
							{
								add(addressMap);
							}
						});
						put("father/husband", kycResult.get("fatherOrHusbandName"));
						put("issueDate", "");
						put("bloodGroup", "");
						put("dob", kycResult.get("dateOfBirth"));
						put("validity", new HashMap<String, Object>() {
							{
								put("nonTransport", kycResult.get("validTo"));
								put("transport", "");
							}
						});
						put("covDetails", covDetailsList);
						put("status", kycResult.get("status"));
						put("statusDetails", new HashMap<String, Object>() {
							{
								put("from", kycResult.get("validFrom"));
								put("to", kycResult.get("validTo"));
								put("remarks", "");
							}
						});
						put("endorsementAndHazardousDetails", new HashMap<String, Object>() {
							{
								put("initialIssuingOffice", "");
								put("lastEndorsementDate", kycResult.get("endorseDate"));
								put("lastEndorsedOffice", "");
								put("endorsementReason", "");
								put("hazardousValidTill", "NA");
								put("hillValidTill", "NA");
							}
						});
					}
				});

				jsonMap.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				jsonMap.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_SUCCESS);
				jsonMap.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);

				return jsonMap;

			} else {
				map = ESignServices.setResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED,
						ResponseMessage.DATA_NOT_FOUND);
			}

		} catch (HttpClientErrorException e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, "No records found for the given ID.");

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Exception: {}", e);
			map = ESignServices.setResponse(ResponseMessage.SOMETHING_WENT_WRONG, ResponseMessage.API_STATUS_FAILED,
					ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		}
		return map;
	}

	public Map<String, Object> validateGstin(String gSTIN, String merchantTrxnRefId) {

		Map<String, Object> map = new HashMap<>();

		try {

			String apiRequest = "{\r\n    \"consent\": \"Y\",\r\n    \"consent_purpose\": \"For bank account purpose only\",\r\n    \"document_type\": \"GSTIN\",\r\n    \"reference_id\": \""
					+ merchantTrxnRefId + "\",\r\n    \"dob\": \"1999-07-26\",\r\n    \"id_number\": \"" + gSTIN
					+ "\"\r\n}";

			LOGGER.info("apiRequest: {}", apiRequest);

			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, apiRequest);
			Request request = new Request.Builder().url("https://in.decentro.tech/kyc/public_registry/validate")
					.method("POST", body).addHeader("accept", "application/json")
					.addHeader("client_id", DecentroUtils.DECENTRO_CLIENT_ID)
					.addHeader("client_secret", DecentroUtils.DECENTRO_CLIENT_SECRET)
					.addHeader("content-type", "application/json")
					.addHeader("module_secret", DecentroUtils.DECENTRO_MODULE_SECRET).build();
			Response response = client.newCall(request).execute();

			String apiResponse = response.body().string();

			LOGGER.info("apiResponse: {}", apiResponse);

			JSONObject resultJsonObject = new JSONObject(apiResponse);
			org.json.JSONObject resultObject = new org.json.JSONObject();
			
			// value will be over-ride once its DECENTRO...
			String status = "SUCCESS", kycStatus = "SUCCESS";
			try {
				if (StringUtils.isNoneEmpty(resultJsonObject.getString("status"))) {
					status = ResponseMessage.DECENTRO;
				}

				if (StringUtils.isNoneEmpty(resultJsonObject.getString("kycStatus"))) {
					kycStatus = ResponseMessage.DECENTRO;
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				LOGGER.error("Exception: {}", e);
				map = ESignServices.setResponse(ResponseMessage.SOMETHING_WENT_WRONG, ResponseMessage.API_STATUS_FAILED,
						ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			}

			if (status.equals("SUCCESS") && kycStatus.equals("SUCCESS")) {
				JSONObject kycResult = resultJsonObject.getJSONObject("kycResult");
//				JSONObject primaryBusinessContact = kycResult.getJSONObject("primaryBusinessContact");
				resultObject.put("gstin", gSTIN);
				resultObject.put("email", "NA");
				resultObject.put("address", kycResult.get("principalPlaceOfBusiness"));
				resultObject.put("mobileNumber", "NA");
				resultObject.put("natureOfBusinessAtAddress", kycResult.get("natureOfBusiness"));
				resultObject.put("stateJurisdiction", kycResult.get("stateJurisdiction"));
				resultObject.put("taxpayerType", kycResult.get("taxpayerType"));
				resultObject.put("registrationDate", kycResult.get("registrationDate"));
				resultObject.put("constitutionOfBusiness", kycResult.get("constitutionOfBusiness"));
				resultObject.put("gstnStatus", kycResult.get("gstnStatus"));
				resultObject.put("legalName", kycResult.get("legalName"));
				resultObject.put("centralJurisdiction", kycResult.get("centralJurisdiction"));
				resultObject.put("pan", kycResult.get("pan"));
				resultObject.put("tradeName", kycResult.get("tradeName"));

				map = ESignServices.setResponse(ResponseMessage.SUCCESS, ResponseMessage.API_STATUS_SUCCESS,
						ResponseMessage.DATA_SUCCESS);

				map.put("merchantTxnRefId", merchantTrxnRefId);
				map.put(ResponseMessage.DATA, resultObject.toMap());
				return map;
			} else {
				map = ESignServices.setResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED,
						ResponseMessage.DATA_NOT_FOUND);
				map.put("gstin", gSTIN);
				map.put("merchantTxnRefId", merchantTrxnRefId);
			}

		} catch (HttpClientErrorException e) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Exception: {}", e);
			map = ESignServices.setResponse(ResponseMessage.SOMETHING_WENT_WRONG, ResponseMessage.API_STATUS_FAILED,
					ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		}
		return map;
	}

	/**
	 * @author pradeep
	 * @serialData 16-09-2024 name matching api
	 */

	public Map<String, Object> nameSimilarityApi(String name1, String name2, String merchantTrxnRefId) {
		Map<String, Object> map = new HashMap<>();

		try {

			RestTemplate restTemplate = new RestTemplate();

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
			headers.set("accept", "application/json");
			headers.set("client_id", DecentroUtils.DECENTRO_CLIENT_ID);
			headers.set("client_secret", DecentroUtils.DECENTRO_CLIENT_SECRET);
			headers.set("module_secret", DecentroUtils.DECENTRO_MODULE_SECRET);

			String apiRequest = "{\"reference_id\":\"" + merchantTrxnRefId + "\",\"text1\":\"" + name1
					+ "\",\"text2\":\"" + name2 + "\"}";

			LOGGER.info("apiRequest: {}", apiRequest);

			HttpEntity<String> request = new HttpEntity<>(apiRequest, headers);

			ResponseEntity<String> response = restTemplate.exchange(
					DecentroUtils.DECENTRO_API_BASE_URL + "/v2/kyc/match", HttpMethod.POST, request, String.class);

			String apiResponse = response.getBody();

			LOGGER.info("apiResponse: " + apiResponse);

			JSONObject jsonObject = new JSONObject(apiResponse);

			String status = jsonObject.getString("status");
			LOGGER.info("status {}", status);

			if (!status.equals("SUCCESS")) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.NAME_NOT_MATCH);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}

			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, "success");
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			map.put("result", jsonObject.getJSONObject("data").toMap());

		} catch (NullPointerException e) {
			LOGGER.error("NullPointerException: {}", e);
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}

		catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}

		return map;
	}

}
