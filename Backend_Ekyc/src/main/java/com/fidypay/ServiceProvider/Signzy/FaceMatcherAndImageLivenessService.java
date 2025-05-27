package com.fidypay.ServiceProvider.Signzy;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.validation.constraints.NotNull;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
import com.fidypay.utils.constants.EkycCommonLogicConfig;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.AmazonClient;
import com.fidypay.utils.ex.DateAndTime;
import com.fidypay.utils.ex.GenerateTrxnRefId;
import com.fidypay.utils.ex.RandomNumberGenrator;
import com.fidypay.wallet.WalletNotification;
import com.fidypay.wallet.WalletRequest;
import com.fidypay.wallet.WalletService;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class FaceMatcherAndImageLivenessService {

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
	private AmazonClient amazonClient;

	@Autowired
	private ServiceProvidersRepository serviceprovidersrepository;

	@Autowired
	private EKYCService ekycService;

	@Autowired
	private EkycCommonLogicConfig ekycCommonLogicConfig;

	@Autowired
	private WalletService walletService;

//	@Autowired
//	private EkycTransactionDetailsSearchRepository ekycTransactionDetailsSearchRepository;

	private static final Logger LOGGER = LoggerFactory.getLogger(FaceMatcherAndImageLivenessService.class);
	private final Semaphore semaphore = new Semaphore(1);
	private final Lock lock = new ReentrantLock();

	// UAT
//	private static final String USER_NAME = "fidypay_test";
//	private static final String PASSWORD = "6zvWtnSar8dTjPDwr8dv";
//	private static final String LOGIN_URL = "https://preproduction.signzy.tech/api/v2/patrons/login";
//	private static final String BASE_URL = "https://preproduction.signzy.tech/api/v2/patrons/";

	// PRODUCTION

	public static final String USER_NAME = "fidypay_prod";
	public static final String PASSWORD = "u4wwVbDFy2xYMrbRU8xs";
	private static final String LOGIN_URL = "https://signzy.tech/api/v2/patrons/login";
	private static final String BASE_URL = "https://signzy.tech/api/v2/patrons/";

	public static String login() {

		String responseBody = null;
		try {
			OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(60, TimeUnit.SECONDS)
					.readTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS).build();

			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType,
					"{\r\n    \"username\": \"" + USER_NAME + "\",\r\n    \"password\": \"" + PASSWORD + "\"\r\n}");

			LOGGER.info("Body ----------------------- " + body);

			Request request = new Request.Builder().url(LOGIN_URL).method("POST", body)
					.addHeader("Content-Type", "application/json").build();

			LOGGER.info("Request ----------------------- " + request);

			Response response;

			response = client.newCall(request).execute();

			responseBody = response.body().string();

			LOGGER.info("Response ----------------------- " + responseBody);
		} catch (IOException e) {
			LOGGER.error("IOException: {}", e);
		}

		return responseBody;
	}

	public synchronized Map<String, Object> saveDataForApi(long merchantId, Double merchantFloatAmount,
			String businessName, String email) throws Exception {

		semaphore.acquire();
		lock.lock();

		String serviceName = "Image Liveness";
		String serviceProvider = ResponseMessage.SIGNZY;
		double amtInDouble = 3.00;

		// Call common logic
		Map<String, Object> commonLogicResponse = ekycCommonLogicConfig.handleCommonLogic(merchantId,
				merchantFloatAmount, serviceName, amtInDouble, businessName, email);

		if (commonLogicResponse.get(ResponseMessage.CODE) != null) {
			return commonLogicResponse;
		}

		try {
			// Extract necessary data
			String merchantTrxnRefId = (String) commonLogicResponse.get("merchantTrxnRefId");
			String walletTxnRefNo = (String) commonLogicResponse.get("walletTxnRefNo");
			String trxnRefId = (String) commonLogicResponse.get("trxnRefId");
			Timestamp trxnDate = (Timestamp) commonLogicResponse.get("trxnDate");
			Long ekycRequestId = (Long) commonLogicResponse.get("ekycRequestId");
			Long serviceId = (Long) commonLogicResponse.get("serviceId");
			double charges = (double) commonLogicResponse.get("charges");

			// API-specific logic (call the faceLiveness API)
			String url = "URL_FROM_SOME_LOGIC"; // Replace with actual URL logic if needed
			Map<String, Object> faceLivenessResponse = faceLiveness(url, merchantTrxnRefId);

			// Handle transaction details
			ekycCommonLogicConfig.saveTransactionDetails(ekycRequestId, merchantId, serviceId, charges, serviceName,
					trxnDate, merchantTrxnRefId, trxnRefId, serviceProvider);

			return faceLivenessResponse;

		} catch (Exception e) {
			e.printStackTrace();
			return ekycCommonLogicConfig.buildResponse(ResponseMessage.SOMETHING_WENT_WRONG,
					ResponseMessage.STATUS_FAILED, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		}
	}

	public synchronized Map<String, Object> saveDataForImageLiveness(MultipartFile image, long merchantId,
			Double merchantFloatAmount, String businessName, String email) {

		Map<String, Object> map = new HashMap<>();
		try {

			semaphore.acquire();
			lock.lock();

			String serviceName = "Image Liveness";
			double amtInDouble = 3.00;

			if (image.isEmpty()) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SELECT_FILE);
				return map;
			}

			LOGGER.info("file size " + image.getSize());

			if (image.getSize() > 52428800) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.FILE_SIZE);
				return map;
			}

			String extension = FilenameUtils.getExtension(image.getOriginalFilename());

			LOGGER.info("extension " + extension);

			if (!extension.equalsIgnoreCase("jpeg") && !extension.equalsIgnoreCase("pdf")
					&& !extension.equalsIgnoreCase("tiff") && !extension.equalsIgnoreCase("png")) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.INVALID_IMAGE_FORMAT);
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
			LOGGER.info("serviceType: " + serviceType);
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

			LOGGER.info("serviceName " + serviceName);
			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());
			String url = amazonClient.uploadFile(image, merchantId, serviceName);
			LOGGER.info("url " + url);

			EkycRequest ekycRequest = ekycRequestRepository
					.save(new EkycRequest(merchantId, merchantTrxnRefId, walletTxnRefNo, trxnDate));
			long ekycRequestId = ekycRequest.getRequestId();

//			Bank bank = new Bank();
//			ThreadWithdrawal test = new ThreadWithdrawal(bank, merchantTrxnRefId, charges, merchantFloatAmount,
//					merchantId, merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId, serviceName,
//					trxnRefId, walletTxnRefNo, serviceName);
//
//			LOGGER.info("Name of Thread -----------------------" + test.getName());
//
//			test.start();

			WalletRequest debitRequest = new WalletRequest(merchantId, charges, "Debit", serviceName, serviceName,
					ekycRequestId, trxnRefId, merchantTrxnRefId, walletTxnRefNo);
			walletService.enqueueTransaction(debitRequest);

			map = faceLiveness(url, merchantTrxnRefId);

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
//			LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'IMAGE LIVENESS' EKYC TRXN DETAILS : {}", elk);

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

	public Map<String, Object> faceLiveness(String url, String merchantTrxnRefId) {
		String responseBody = null;

		Map<String, Object> map = new HashMap<>();
		try {

			responseBody = login();

			LOGGER.info("Response ----------------------- " + responseBody);

			JSONObject respJson = new JSONObject(responseBody);

			String authorization = respJson.getString("id");
			String userId = respJson.getString("userId");

			String faceMetcherUrl = BASE_URL + userId + "/passiveLiveness";

			OkHttpClient client1 = new OkHttpClient().newBuilder().connectTimeout(60, TimeUnit.SECONDS)
					.readTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS).build();

			MediaType mediaType1 = MediaType.parse("application/json");
			RequestBody body1 = RequestBody.create(mediaType1,
					"{\"task\":\"checkLiveness\",\"essentials\":{\"image\":\"" + url + "\"}}");
			Request request1 = new Request.Builder().url(faceMetcherUrl).post(body1)
					.addHeader("Accept-Language", "en-US,en;q=0.8").addHeader("Accept", "*/*")
					.addHeader("Authorization", authorization).build();

			Response response1 = client1.newCall(request1).execute();

			responseBody = response1.body().string();

			LOGGER.info("Response1 ----------------------- " + responseBody);

			JSONObject respJson1 = new JSONObject(responseBody);

			if (respJson1.has("result")) {

				JSONObject result = respJson1.getJSONObject("result");

				LOGGER.info("result ----------------------- " + result);

				JSONObject essentials = respJson1.getJSONObject("essentials");
				String imageUrl = essentials.getString("image");
				String liveness = String.valueOf(result.getBoolean("liveness"));
				String score = String.valueOf(result.getDouble("score"));
				String probability = String.valueOf(result.getDouble("probability"));
				String quality = String.valueOf(result.getLong("quality"));

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_SUCCESS);
				map.put("merchantTrxnRefId", merchantTrxnRefId);
				map.put("imageUrl", imageUrl);
				map.put("liveness", liveness);
				map.put("score", score);
				map.put("probability", probability);
				map.put("quality", quality);

			} else if (respJson1.has("error")) {

				JSONObject error = respJson1.getJSONObject("error");

				LOGGER.info("error ----------------------- " + error);

				String message = error.getString("message");
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, message);

			}

		} catch (Exception e) {
			LOGGER.info("Inside catch block ----------------------- ");
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.API_STATUS_FAILED);
			return map;
		}
		return map;
	}

	public synchronized Map<String, Object> faceMatcherSignzy(@NotNull MultipartFile file1,
			@NotNull MultipartFile file2, long merchantId, Double merchantFloatAmount, String businessName,
			String email) {
		Map<String, Object> map = new HashedMap<>();
		try {
			semaphore.acquire();
			lock.lock();
			String serviceName = "Face Matcher";
			double amtInDouble = 1.50;

			if (!ekycService.checkServiceExistOrNot(merchantId, serviceName)) {

				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
				return map;

			}

			if (file1.isEmpty()) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SELECT_FILE);
				return map;
			}

			if (file2.isEmpty()) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SELECT_FILE);
				return map;
			}

			if (file1.getSize() > 7340032 || file2.getSize() > 7340032) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.IMAGE_SIZE);
				return map;
			}

			String extension = FilenameUtils.getExtension(file1.getOriginalFilename());
			LOGGER.info("extension " + extension);
			if (!extension.equalsIgnoreCase("jpg") && !extension.equalsIgnoreCase("png")) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.IMAGE_FORMAT);
				return map;
			}

			String extension2 = FilenameUtils.getExtension(file2.getOriginalFilename());
			LOGGER.info("extension2 " + extension2);
			if (!extension2.equalsIgnoreCase("jpg") && !extension2.equalsIgnoreCase("png")) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.IMAGE_FORMAT);
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
			LOGGER.info("serviceType: " + serviceType);
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

			LOGGER.info("serviceName " + serviceName);
			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());
			String url1 = amazonClient.uploadFile(file1, merchantId, serviceName);
			String url2 = amazonClient.uploadFile(file2, merchantId, serviceName);

			LOGGER.info("Key1 " + url1);
			LOGGER.info("Key2 " + url2);

			EkycRequest ekycRequest = ekycRequestRepository
					.save(new EkycRequest(merchantId, merchantTrxnRefId, walletTxnRefNo, trxnDate));
			long ekycRequestId = ekycRequest.getRequestId();

//			Bank bank = new Bank();
//			ThreadWithdrawal test = new ThreadWithdrawal(bank, merchantTrxnRefId, charges, merchantFloatAmount,
//					merchantId, merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId, serviceName,
//					trxnRefId, walletTxnRefNo, serviceName);
//			LOGGER.info("Name of Thread -----------------------" + test.getName());
//			test.start();

			WalletRequest debitRequest = new WalletRequest(merchantId, charges, "Debit", serviceName, serviceName,
					ekycRequestId, trxnRefId, merchantTrxnRefId, walletTxnRefNo);
			walletService.enqueueTransaction(debitRequest);

			Map<String, Object> res = faceMetcherSignzyAPI(url1, url2, merchantTrxnRefId);

			String serviceProvider = ResponseMessage.SIGNZY;
			ServiceProviders spInfo = serviceprovidersrepository.findBySpName(Encryption.encString(serviceProvider));
			Long serviceProviderId = spInfo.getServiceProviderId();

			EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
					.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
							ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
							serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
							serviceProviderId, '0'));
			LOGGER.info("ekycTransactionDetails: {}", ekycTransactionDetails);

//			EkycTransactionDetails elk = ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
//			LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'FACE MATCHER SIGNZY' EKYC TRXN DETAILS : {}", elk);

			return res;

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		} finally {
			lock.unlock();
			semaphore.release();
		}
		return map;
	}

	private Map<String, Object> faceMetcherSignzyAPI(String url1, String url2, String merchantTrxnRefId) {

		Map<String, Object> map = new HashedMap<>();
		try {

			String loginResponse = login();

			LOGGER.info("Login : " + loginResponse);

			JSONObject jsonObject = new JSONObject(loginResponse);

			String authorization = jsonObject.getString("id");
			String userId = jsonObject.getString("userId");

			String faceMetcherUrl = BASE_URL + userId + "/facematches";

			OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(120, TimeUnit.SECONDS)
					.readTimeout(120, TimeUnit.SECONDS).writeTimeout(120, TimeUnit.SECONDS).build();

			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType,
					"{\"essentials\":{\"firstImage\":\"" + url1 + "\",\"secondImage\":\"" + url2 + "\"}}");
			Request request = new Request.Builder().url(faceMetcherUrl).post(body)
					.addHeader("Accept-Language", "en-US,en;q=0.8").addHeader("Accept", "*/*")
					.addHeader("Authorization", authorization).build();

			LOGGER.info("request : " + request);

			Response response = client.newCall(request).execute();

			String responseBody = response.body().string();

			LOGGER.info("result : " + responseBody);

			JSONObject respJson = new JSONObject(responseBody);
			if (respJson.has("essentials")) {
				JSONObject essentials = respJson.getJSONObject("essentials");

				String firstImage = essentials.getString("firstImage");
				String secondImage = essentials.getString("secondImage");

				JSONObject result = respJson.getJSONObject("result");

				String reorient = String.valueOf(essentials.getLong("reorient"));
				String verified = String.valueOf(result.getBoolean("verified"));

				String message = result.getString("message");
				String matchPercentage = result.getString("matchPercentage");

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_SUCCESS);
				map.put("merchantTrxnRefId", merchantTrxnRefId);
				map.put("firstImage", firstImage);
				map.put("secondImage", secondImage);
				map.put("reorient", reorient);
				map.put("verified", verified);
				map.put("message", message);
				map.put("matchPercentage", matchPercentage);
			} else if (respJson.has("error")) {
				JSONObject error = respJson.getJSONObject("error");

				LOGGER.info("error ----------------------- " + error);

				String message = error.getString("message");
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, message);
			}

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.API_STATUS_FAILED);
			return map;
		}
		return map;
	}
//----------------------------------------------------------------------------------------------------------------

	public synchronized Map<String, Object> saveDataForImagePassiveLiveness(String imageUrl, long merchantId,
			Double merchantFloatAmount) {
		Map<String, Object> map = new HashMap<>();
		try {
			semaphore.acquire();
			lock.lock();
			String serviceName = "Image Liveness";
			double amtInDouble = 1.0;

			if (imageUrl.isEmpty()) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.IMAGE_URL);
				return map;
			}

			if (merchantFloatAmount < amtInDouble) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
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
			LOGGER.info("serviceType: " + serviceType);
			double charges = 0.0;
			double commission = 0.0;

			switch (serviceType) {

			case "Charge":
				charges = chargeService.getMerchantServiceChargesV2(merchantServiceId, amtInDouble);
				LOGGER.info("charges: " + charges);
				break;

			case "Commission":
				commission = chargeService.getMerchantServiceCommissionV2(merchantServiceId, amtInDouble);
				LOGGER.info("commission: " + commission);
				break;

			default:
				break;

			}
			LOGGER.info("serviceName " + serviceName);
			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());
			// String url = amazonClient.uploadFile(image, merchantId, serviceName);
			// LOGGER.info("url " + url);

			EkycRequest ekycRequest = ekycRequestRepository
					.save(new EkycRequest(merchantId, merchantTrxnRefId, walletTxnRefNo, trxnDate));
			long ekycRequestId = ekycRequest.getRequestId();

//			Bank bank = new Bank();
//			ThreadWithdrawal test = new ThreadWithdrawal(bank, merchantTrxnRefId, amtInDouble, merchantFloatAmount,
//					merchantId, merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId, serviceName,
//					trxnRefId, walletTxnRefNo, serviceName);
//			LOGGER.info("Name of Thread -----------------------" + test.getName());
//			test.start();

			WalletRequest debitRequest = new WalletRequest(merchantId, charges, "Debit", serviceName, serviceName,
					ekycRequestId, trxnRefId, merchantTrxnRefId, walletTxnRefNo);
			walletService.enqueueTransaction(debitRequest);

			map = faceLiveness(imageUrl, merchantTrxnRefId);

			String serviceProvider = ResponseMessage.SIGNZY;
			ServiceProviders spInfo = serviceprovidersrepository.findBySpName(Encryption.encString(serviceProvider));
			Long serviceProviderId = spInfo.getServiceProviderId();

			EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
					.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
							ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
							serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
							serviceProviderId, '0'));
			LOGGER.info("ekycTransactionDetails: {}", ekycTransactionDetails);
		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		} finally {
			lock.unlock();
			semaphore.release();
		}
		return map;
	}

	public synchronized Map<String, Object> saveDataForFaceMatcher(@NotNull String imageUrl1, @NotNull String imageUrl2,
			long merchantId, Double merchantFloatAmount) {
		Map<String, Object> map = new HashedMap<>();
		try {
			semaphore.acquire();
			lock.lock();
			String serviceName = "Face Matcher";
			double amtInDouble = 1.0;

			if (!ekycService.checkServiceExistOrNot(merchantId, serviceName)) {

				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
				return map;
			}

			if (imageUrl1.isEmpty()) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.IMAGE_URL);
				return map;
			}

			if (imageUrl2.isEmpty()) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.IMAGE_URL);
				return map;
			}

			if (merchantFloatAmount < amtInDouble) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
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
			LOGGER.info("serviceType: " + serviceType);
			double charges = 0.0;
			double commission = 0.0;

			switch (serviceType) {

			case "Charge":
				charges = chargeService.getMerchantServiceChargesV2(merchantServiceId, amtInDouble);
				LOGGER.info("charges: " + charges);
				break;

			case "Commission":
				commission = chargeService.getMerchantServiceCommissionV2(merchantServiceId, amtInDouble);
				LOGGER.info("commission: " + commission);
				break;

			default:
				break;

			}
			LOGGER.info("serviceName " + serviceName);
			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());
			// String url1 = amazonClient.uploadFile(file1, merchantId, serviceName);
			// String url2 = amazonClient.uploadFile(file2, merchantId, serviceName);

			LOGGER.info("imageUrl1 " + imageUrl1);
			LOGGER.info("imageUrl2 " + imageUrl2);

			EkycRequest ekycRequest = ekycRequestRepository
					.save(new EkycRequest(merchantId, merchantTrxnRefId, walletTxnRefNo, trxnDate));
			long ekycRequestId = ekycRequest.getRequestId();

//			Bank bank = new Bank();
//			ThreadWithdrawal test = new ThreadWithdrawal(bank, merchantTrxnRefId, amtInDouble, merchantFloatAmount,
//					merchantId, merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId, serviceName,
//					trxnRefId, walletTxnRefNo, serviceName);
//			LOGGER.info("Name of Thread -----------------------" + test.getName());
//			test.start();

			WalletRequest debitRequest = new WalletRequest(merchantId, charges, "Debit", serviceName, serviceName,
					ekycRequestId, trxnRefId, merchantTrxnRefId, walletTxnRefNo);
			walletService.enqueueTransaction(debitRequest);

			Map<String, Object> res = faceMetcherSignzyAPI(imageUrl1, imageUrl2, merchantTrxnRefId);

			String serviceProvider = ResponseMessage.SIGNZY;
			ServiceProviders spInfo = serviceprovidersrepository.findBySpName(Encryption.encString(serviceProvider));
			Long serviceProviderId = spInfo.getServiceProviderId();

			EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
					.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
							ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
							serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
							serviceProviderId, '0'));
			LOGGER.info("ekycTransactionDetails: {}", ekycTransactionDetails);
			return res;

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		} finally {
			lock.unlock();
			semaphore.release();
		}
		return map;
	}
}
