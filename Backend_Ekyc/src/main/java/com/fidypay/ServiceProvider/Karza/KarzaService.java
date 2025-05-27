package com.fidypay.ServiceProvider.Karza;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.validation.Valid;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.fidypay.request.WorkflowKYCRequest;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.AmazonClient;
import com.fidypay.utils.ex.DateAndTime;
import com.fidypay.utils.ex.GenerateTrxnRefId;
import com.fidypay.utils.ex.RandomNumberGenrator;
import com.fidypay.wallet.WalletRequest;
import com.fidypay.wallet.WalletService;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class KarzaService {

	private String ADDRESS_MATCHING_URL = "https://testapi.karza.in/v2/address";
	private String NAME_MATCHING_URL = "https://testapi.karza.in/v3/name";
	private String KEY = "NmXy370lZytA27VA";

	public final static String CONTENT_TYPE = "application/json";
	public final static Boolean ADDITIONAL_DETAILS = true;
	public final static String CONSENT = "Y";

	private static final Logger LOGGER = LoggerFactory.getLogger(KarzaService.class);

	private static final String GST_SEARCH_BASIS_PAN_URL = "https://api.karza.in/gst/uat/v2/search";

	private static final String X_KARZA_KEY = "NmXy370lZytA27VA";

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
	private EKYCService ekycService;

	@Autowired
	private ServiceProvidersRepository serviceprovidersrepository;

	@Autowired
	private WalletService walletService;

	private final Semaphore semaphore = new Semaphore(1);
	private final Lock lock = new ReentrantLock();

	public Map<String, Object> kycOCR(String key, String docType) {
		JSONObject object = null;
		String finalResponse = null;
		Map<String, Object> map = new HashMap<>();
		try {

			// String filePath = "https://ocr-image-aws.s3.ap-south-1.amazonaws.com/" + key;
			String filePath = key;
			LOGGER.info("filePath: " + filePath);

			String jsonRequest = "{\"url\":\"" + filePath
					+ "\",\"maskAadhaar\":false,\"hideAadhaar\":false,\"conf\":true,\"docType\":\"" + docType + "\"}";

			LOGGER.info("request: " + jsonRequest);

			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, jsonRequest);
			Request request = new Request.Builder().url("https://testapi.karza.in/v3/ocr/kyc").method("POST", body)
					.addHeader("x-karza-key", "NmXy370lZytA27VA").addHeader("Content-Type", "application/json").build();
			Response response = client.newCall(request).execute();

			finalResponse = response.body().string();

			LOGGER.info("API JsonRequest: " + jsonRequest);

			LOGGER.info("API Request: " + request);
			LOGGER.info("API Response: " + finalResponse);

			Object obj = null;
			JSONParser parser = new JSONParser();
			obj = parser.parse(finalResponse);
			org.json.simple.JSONObject jsonObjectt = (org.json.simple.JSONObject) obj;

			Long status = 0L;
			try {
				status = (Long) jsonObjectt.get("status");
			} catch (Exception e) {
				status = (Long) jsonObjectt.get("statusCode");
			}

			if (status != null && status == 402) {

				String error = (String) jsonObjectt.get("error");
				LOGGER.info("error: " + error);

				map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			} else {

				org.json.simple.JSONArray result = (org.json.simple.JSONArray) jsonObjectt.get("result");
				LOGGER.info("result: " + result);

				if (result.isEmpty()) {
					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.DESCRIPTION, "Data not found");
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				} else {
					map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
					map.put(ResponseMessage.DESCRIPTION, "OCR Kyc details");
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
					map.put("result", result);
				}
			}
		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;
	}

	public Map<String, Object> saveDataForOCRKycAWS(MultipartFile file, long merchantId, double merchantWallet,
			String docType) throws Exception {
		Map<String, Object> map = new HashMap<>();

		try {

			String serviceName = "OCR EKYC";
			double amtInDouble = 1.0;

			if (file.isEmpty()) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SELECT_FILE);
				return map;
			}

			if (!ekycService.checkServiceExistOrNot(merchantId, serviceName)) {

				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
				return map;

			}

			if (merchantWallet < amtInDouble) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
				return map;
			}

			String extension = FilenameUtils.getExtension(file.getOriginalFilename());
			LOGGER.info("extension " + extension);
			if (!extension.equalsIgnoreCase("jpeg") && !extension.equalsIgnoreCase("pdf")
					&& !extension.equalsIgnoreCase("jpg") && !extension.equalsIgnoreCase("png")) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.INVALID_FILE_FORMAT);
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
			String url = amazonClient.uploadFile(file, merchantId, serviceName);

			LOGGER.info("Key " + url);
			EkycRequest ekycRequest = ekycRequestRepository
					.save(new EkycRequest(merchantId, merchantTrxnRefId, walletTxnRefNo, trxnDate));
			long ekycRequestId = ekycRequest.getRequestId();

//			Bank bank = new Bank();
//			ThreadWithdrawal test = new ThreadWithdrawal(bank, merchantTrxnRefId, amtInDouble, merchantWallet,
//					merchantId, merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId, serviceName,
//					trxnRefId, walletTxnRefNo, serviceName);
//			LOGGER.info("Name of Thread -----------------------" + test.getName());
//			test.start();

			WalletRequest debitRequest = new WalletRequest(merchantId, charges, "Debit", serviceName, serviceName,
					ekycRequestId, trxnRefId, merchantTrxnRefId, walletTxnRefNo);
			walletService.enqueueTransaction(debitRequest);

			Map<String, Object> res = kycOCR(url, docType);
			String serviceProvider = "Karza Tech";
			ServiceProviders spInfo = serviceprovidersrepository.findBySpName(Encryption.encString(serviceProvider));
			Long serviceProviderId = spInfo.getServiceProviderId();

			EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
					.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
							ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
							serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
							serviceProviderId, '0'));
			return res;

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;
	}

	public synchronized Map<String, Object> saveDataForOCRKycAWSForWorkFlow(WorkflowKYCRequest workflowKYCRequest,
			long merchantId, Double merchantFloatAmount) {
		Map<String, Object> map = new HashMap<>();

		try {
			semaphore.acquire();
			lock.lock();
			String serviceName = "OCR EKYC";
			double amtInDouble = 1.0;

			if (!ekycService.checkServiceExistOrNot(merchantId, serviceName)) {

				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
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

			LOGGER.info("Key " + workflowKYCRequest.getUrl());
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

			Map<String, Object> res = kycOCR(workflowKYCRequest.getUrl(), workflowKYCRequest.getDocType());
			String serviceProvider = ResponseMessage.KARZA;
			ServiceProviders spInfo = serviceprovidersrepository.findBySpName(Encryption.encString(serviceProvider));
			Long serviceProviderId = spInfo.getServiceProviderId();

			EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
					.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
							ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
							serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
							serviceProviderId, '0'));
			return res;

		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		} finally {
			lock.unlock();
			semaphore.release();
		}
		return map;
	}

	// -----------------------------------------------------------------

	public Map<String, Object> gstSearch(String gSTIN, String merchantTrxnRefId) {
		Map<String, Object> map = new HashMap<>();
		try {
			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, "{\"gstin\":\"" + gSTIN + "\",\"consent\":\"Y\"}");
			Request request = new Request.Builder().url("https://api.karza.in/gst/uat/v2/gst-verification")
					.method("POST", body).addHeader("x-karza-key", "NmXy370lZytA27VA")
					.addHeader("Content-Type", "application/json").build();
			Response response = client.newCall(request).execute();

			String r = response.body().string();

			LOGGER.info("request: " + request);
			LOGGER.info("finalRes: " + r);

			org.json.JSONObject resultJsonObject = new org.json.JSONObject(r);
			org.json.JSONObject resultObject = new org.json.JSONObject();

			Long statusCode = resultJsonObject.getLong("statusCode");

			if (statusCode == 101) {

				org.json.JSONObject kycResult = resultJsonObject.getJSONObject("result");
//				org.json.JSONObject contacted = kycResult.getJSONObject("contacted");
//				org.json.JSONObject pradr = kycResult.getJSONObject("pradr");
//
//				resultObject.put("gstin", gSTIN);
//				resultObject.put("email", contacted.get("email"));
//				resultObject.put("address", pradr.get("adr"));
//				resultObject.put("mobileNumber", contacted.get("mobNum"));
//				resultObject.put("natureOfBusinessAtAddress", pradr.get("ntr"));
//				resultObject.put("stateJurisdiction", kycResult.get("stjCd"));
//				resultObject.put("taxpayerType", kycResult.get("dty"));
//				resultObject.put("registrationDate", kycResult.get("rgdt"));
//				resultObject.put("constitutionOfBusiness", kycResult.get("ctb"));
//				resultObject.put("gstnStatus", kycResult.get("sts"));
//				resultObject.put("legalName", kycResult.get("lgnm"));
//				resultObject.put("centralJurisdiction", kycResult.get("ctj"));
//				resultObject.put("pan", "NA");
//				resultObject.put("tradeName", kycResult.get("tradeNam"));

				map = ESignServices.setResponse(ResponseMessage.SUCCESS, ResponseMessage.API_STATUS_SUCCESS,
						ResponseMessage.DATA_SUCCESS);

//				map.put("merchantTxnRefId", merchantTrxnRefId);
//				map.put(ResponseMessage.DATA, resultObject.toMap());
				map.put("result", kycResult.toMap());
				return map;
			} else {
//				map.put("merchantTxnRefId", merchantTrxnRefId);
				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "Invalid GSTIN.");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			}

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;
	}

	public Map<String, Object> gstAuthentication(String gSTIN) {
		Map<String, Object> map = new HashMap<>();
		try {
			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType,
					"{\"consent\":\"Y\",\"additionalData\":false,\"gstin\":\"" + gSTIN + "\"}");
			Request request = new Request.Builder().url("https://api.karza.in/gst/uat/v2/gstdetailed")
					.method("POST", body).addHeader("x-karza-key", "NmXy370lZytA27VA")
					.addHeader("Content-Type", "application/json").build();
			Response response = client.newCall(request).execute();

			String r = response.body().string();

			org.json.JSONObject json = new org.json.JSONObject(r);

			Long statusCode = json.getLong("statusCode");

			if (statusCode == 101) {
				org.json.JSONObject result = json.getJSONObject("result");

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "GSTN details");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put("result", result.toMap());

			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "Invalid GSTIN.");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			}

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;
	}

	public Map<String, Object> checkPanStatus(@Valid String panNumber) {
		Map<String, Object> map = new HashMap<>();
		try {
			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, "{\"consent\":\"Y\",\"pan\":\"" + panNumber + "\"}");
			Request request = new Request.Builder().url(KarzaUtils.KARZA_API_BASE_URL + "v2/pan").method("POST", body)
					.addHeader("x-karza-key", KarzaUtils.KARZA_KEY).addHeader("Content-Type", "application/json")
					.build();
			Response response = client.newCall(request).execute();

			org.json.JSONObject object = new org.json.JSONObject(response.body().string());

			org.json.JSONObject result = object.getJSONObject("result");

			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, "pan status details");
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			map.put("result", result);

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;
	}

	public Map<String, Object> checkPanAadharLinkStatusAPI(@Valid String panNumber) {
		Map<String, Object> map = new HashMap<>();
		try {
			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, "{\"consent\":\"Y\",\"pan\":\"" + panNumber + "\"}");
			Request request = new Request.Builder().url(KarzaUtils.KARZA_API_BASE_URL + "v3/pan-link")
					.method("POST", body).addHeader("x-karza-key", KarzaUtils.KARZA_KEY)
					.addHeader("Content-Type", "application/json").build();
			Response response = client.newCall(request).execute();

			org.json.JSONObject object = new org.json.JSONObject(response.body().string());

			org.json.JSONObject result = object.getJSONObject("result");

			System.out.println("--------------Response----------" + result);

			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, "pan status details");
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			map.put("result", result.toMap());

		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;
	}

	public Map<String, Object> panProfileDetailsAPI(@Valid String panNumber, String merchantTrxnRefId) {
		Map<String, Object> map = new HashMap<>();
		try {

			String requestJson = "{\"pan\":\"" + panNumber + "\",\"consent\":\"Y\"}";

			LOGGER.info("requestJson: " + requestJson);

			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, requestJson);
			Request request = new Request.Builder().url(KarzaUtils.KARZA_API_BASE_URL + "v3/pan-profile")
					.method("POST", body).addHeader("x-karza-key", KarzaUtils.KARZA_KEY)
					.addHeader("Content-Type", "application/json").build();
			Response response = client.newCall(request).execute();

			String rResponse = response.body().string();

			LOGGER.info("response: " + rResponse);

			org.json.JSONObject object = new org.json.JSONObject(rResponse);

			long statusCode = object.getLong("statusCode");

			if (statusCode == 101) {
				org.json.JSONObject resultJson = object.getJSONObject("result");
				String name = resultJson.getString("name");

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_SUCCESS);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put("name", name);
				map.put("panStatus", "VALID");
				map.put("panNumber", panNumber);
				map.put("merchantTxnRefId", merchantTrxnRefId);
				return map;
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put("panNumber", panNumber);
				map.put("merchantTxnRefId", merchantTrxnRefId);
				return map;
			}
//			try {
//				org.json.JSONObject result = object.getJSONObject("result");
//
//				LOGGER.info("result: " + result);
//
//				String firstName = result.getString("firstName");
//
//				LOGGER.info("firstName: " + firstName);
//
//				String lastName = result.getString("lastName");
//				String middleName = result.getString("middleName");
//
//				String pan = result.getString("pan");
//				String dob = result.getString("dob");
//				String gender = result.getString("gender");
//				boolean aadhaarLinked = result.getBoolean("aadhaarLinked");
//
//				// JSONObject object2 = new JSONObject();
//				map.put("firstName", firstName);
//				map.put("lastName", lastName);
//				map.put("middleName", middleName);
//				map.put("name", "NA");
//				map.put("number", pan);
//
//				map.put("panStatusCode", "NA");
//				map.put("typeOfHolder", "Individual or Person");
//				map.put("isValid", true);
//				map.put("isIndividual", true);
//
//				map.put("title", "NA");
//				map.put("panStatus", "VALID");
//				map.put("lastUpdatedOn", "NA");
//
//				if (aadhaarLinked == true) {
//					map.put("aadhaarSeedingStatusCode", "Y");
//					map.put("aadhaarSeedingStatus", "Successful");
//				} else {
//					map.put("aadhaarSeedingStatusCode", "N");
//					map.put("aadhaarSeedingStatus", "Failed");
//				}
//
//				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
//				map.put(ResponseMessage.DESCRIPTION, "pan status details");
//				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
//				return map;
//
//			} catch (Exception e) {
//				String error = object.getString("error");
//
//				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
//				map.put(ResponseMessage.DESCRIPTION, error);
//				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
//			}

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;
	}

	// ----------------------------Passport
	// Service-------------------------------------

	public Map<String, Object> fetchPassportV2(String fileNumber, String dob) {
		Map<String, Object> map = new HashedMap<>();
		String res = null;
		JSONObject jsonObject = new JSONObject();
		try {

			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, "{\"name\":\"\",\"passportNo\":\"\",\"fileNo\":\""
					+ fileNumber + "\",\"dob\":\"" + dob + "\",\"doi\":\"\",\"consent\":\"y\"}");
			Request request = new Request.Builder().url("https://testapi.karza.in/v3/passport-verification")
					.method("POST", body).addHeader("x-karza-key", "NmXy370lZytA27VA")
					.addHeader("Content-Type", "application/json").build();
			Response response = client.newCall(request).execute();

			String s = response.body().string();

			LOGGER.info("request: " + request);
			LOGGER.info("response: " + s);

			org.json.JSONObject jsonObject2 = new org.json.JSONObject(s);

			Long statusCode = jsonObject2.getLong("statusCode");

			LOGGER.info("jsonObject2: " + jsonObject2);
			if (statusCode == 101) {

				org.json.JSONObject result = jsonObject2.getJSONObject("result");
				LOGGER.info("result: " + result);

				org.json.JSONObject name = result.getJSONObject("name");

				String nameFromPassport = name.getString("nameFromPassport");
				String surnameFromPassport = name.getString("surnameFromPassport");

				org.json.JSONObject passportNumber = result.getJSONObject("passportNumber");

				String passportNumberFromSource = passportNumber.getString("passportNumberFromSource");

				org.json.JSONObject dateOfIssue = result.getJSONObject("dateOfIssue");
				String dispatchedOnFromSource = dateOfIssue.getString("dispatchedOnFromSource");

				String typeOfApplication = result.getString("typeOfApplication");
				String applicationDate = result.getString("applicationDate");

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "Passport details");
				map.put("typeOfApplication", typeOfApplication);
				map.put("applicationReceivedOnDate", dispatchedOnFromSource);
				map.put("fileNumber", fileNumber);
				map.put("surname", surnameFromPassport);
				map.put("dob", dob);
				map.put("givenName", nameFromPassport + " " + surnameFromPassport);
				map.put("name", nameFromPassport);

			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Please pass valid file number or dob combination");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			}

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;
	}

	public Map<String, Object> verifyPassportV2(String passportNumber, String dob, String name) {
		Map<String, Object> map = new HashedMap<>();
		String res = null;
		JSONObject jsonObject = new JSONObject();
		try {

			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType,
					"{\"consent\":\"Y\",\"name\":\"" + name + "\",\"last_name\":\"\",\"dob\":\"" + dob
							+ "\",\"doe\":\"10/07/2080\",\"gender\":\"M\",\"passport_no\":\"" + passportNumber
							+ "\",\"type\":\"P\",\"country\":\"IND\"}");
			Request request = new Request.Builder().url("https://testapi.karza.in/v2/passport").method("POST", body)
					.addHeader("x-karza-key", "NmXy370lZytA27VA").addHeader("Content-Type", "application/json").build();
			Response response = client.newCall(request).execute();

			String s = response.body().string();

			LOGGER.info("request: " + request);
			LOGGER.info("response: " + s);

			org.json.JSONObject jsonObject2 = new org.json.JSONObject(s);

			Long statusCode = jsonObject2.getLong("status-code");

			LOGGER.info("jsonObject2: " + jsonObject2);
			if (statusCode == 101) {

				org.json.JSONObject result = jsonObject2.getJSONObject("result");
				LOGGER.info("result: " + result);

				String string1 = result.getString("string1");
				String string2 = result.getString("string2");

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "Passport details");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put("string1", string1);
				map.put("string2", string2);
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Please pass valid passport number");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			}

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;
	}

	// -----------------------------VoterId Service----------------------

	public Map<String, Object> verifyVoterV2(String voterId, String merchantTrxnRefId) {
		Map<String, Object> map = new HashedMap<>();
		try {

			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, "{\"consent\":\"Y\",\"epicNo\":\"" + voterId + "\"}");
			Request request = new Request.Builder().url("https://testapi.karza.in/v3/voter").method("POST", body)
					.addHeader("x-karza-key", "NmXy370lZytA27VA").addHeader("Content-Type", "application/json").build();
			Response response = client.newCall(request).execute();

			String s = response.body().string();

			LOGGER.info("body: " + body.toString());
			LOGGER.info("request: " + request);
			LOGGER.info("response: " + s);

			org.json.JSONObject jsonObject2 = new org.json.JSONObject(s);

			Long statusCode = jsonObject2.getLong("statusCode");

			LOGGER.info("jsonObject2: " + jsonObject2);
			if (statusCode == 101) {
				org.json.JSONObject resultObject = new org.json.JSONObject();
				org.json.JSONObject result = jsonObject2.getJSONObject("result");
				LOGGER.info("result: " + result);

//				resultObject.put("voterIdNo", voterId);
//				resultObject.put("name", result.get("name"));
//				resultObject.put("gender", result.get("gender"));
//				resultObject.put("age", result.get("age"));
//				resultObject.put("relativeName", result.get("rlnName"));
//				resultObject.put("relativeRelationType", result.get("rlnType"));
//				resultObject.put("lastUpdateDate", "NA");
//				resultObject.put("partNumberOrLocationNumberInConstituency", result.get("partNo"));
//				resultObject.put("partOrLocationInConstituency", result.get("partName"));
//				resultObject.put("assemblyConstituency", result.get("acName"));
//				resultObject.put("voterIdStatus", "VALID");
//				resultObject.put("districtName", result.get("district"));
//				resultObject.put("state", result.get("state"));
//				resultObject.put("pollingBoothDetails", result.get("psName") + "," + result.get("partNo"));
//				map.put("merchantTxnRefId", merchantTrxnRefId);

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_SUCCESS);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put("result", result.toMap());
				return map;

			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
//				map.put("voterIdNo", voterId);
//				map.put("merchantTxnRefId", merchantTrxnRefId);
			}

		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Please contact to FidyPay team.");
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;
	}

	// --------------------------------Karza APIs-------------------------------

	@SuppressWarnings("unchecked")
	public Map<String, Object> nameSimilarityApi(String name1, String name2) {

		Map<String, Object> map = new HashMap<>();

		try {

			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, "{\r\n    \"name1\": \"" + name1
					+ "\",\r\n    \"name2\": \"" + name2
					+ "\",\r\n    \"type\": \"individual\",\r\n    \"preset\": \"s\",\r\n    \"allowPartialMatch\": true,\r\n    \"suppressReorderPenalty\": true\r\n}");
			Request request = new Request.Builder().url(NAME_MATCHING_URL).method("POST", body)
					.addHeader("x-karza-key", KEY).addHeader("Content-Type", "application/json").build();
			Response response = client.newCall(request).execute();

			String apiResponse = response.body().string();

			LOGGER.info("finalRes: " + apiResponse);

			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> readValue = objectMapper.readValue(apiResponse, Map.class);

			Map<String, Object> result = (Map<String, Object>) readValue.get("result");

			boolean aResult = (boolean) result.get("result");
			LOGGER.info("Boolean Value: {} ", aResult);

			if (!aResult) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.NAME_NOT_MATCH);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}

			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.NAME_MATCH);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			map.put("result", result);

		} catch (NullPointerException e) {
			LOGGER.error("NullPointerException: {}", e);
		}

		catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}

		return map;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> addressSimilarityApi(String address1, String address2) {

		Map<String, Object> map = new HashMap<>();
		try {

			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType,
					"{\r\n    \"address1\": \"" + address1 + "\",\r\n    \"address2\": \"" + address2 + "\"\r\n}");
			Request request = new Request.Builder().url(ADDRESS_MATCHING_URL).method("POST", body)
					.addHeader("x-karza-key", KEY).addHeader("Content-Type", "application/json").build();
			Response response = client.newCall(request).execute();

			String apiResponse = response.body().string();

			LOGGER.info("finalRes: " + apiResponse);

			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> readValue = objectMapper.readValue(apiResponse, Map.class);

			Map<String, Object> result = (Map<String, Object>) readValue.get("result");

			String match = (String) result.get("match");

			if (match.equals("False")) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.ADDRESS_NOT_MATCH);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}

			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.ADDRESS_MATCH);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			map.put("result", result);

		} catch (NullPointerException e) {

		}

		catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}

		return map;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> drivingLicenceApi(String dlNo, String dob, String merchantTrxnRefId) {

		Map<String, Object> map = new HashMap<>();
		try {

			String jsonBody = "{\r\n \"dlNo\": \"" + dlNo + "\",\r\n\"dob\": \"" + dob
					+ "\",\r\n \"additionalDetails\": " + ADDITIONAL_DETAILS + ",\r\n \"consent\": \"" + CONSENT
					+ "\"\r\n}";

			LOGGER.info("jsonBody: " + jsonBody);

			OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(30, TimeUnit.SECONDS)
					.readTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, jsonBody);
			Request request = new Request.Builder().url("https://testapi.karza.in/v3/dl").method("POST", body)
					.addHeader("x-karza-key", KEY).addHeader("Content-Type", CONTENT_TYPE).build();

			LOGGER.info("request: " + request);
			Response response = client.newCall(request).execute();
			String r = response.body().string();
			LOGGER.info("finalRes: " + r);

			org.json.JSONObject responseJson = new org.json.JSONObject(r);

			org.json.JSONObject resultObject = new org.json.JSONObject();

			int code = responseJson.getInt("statusCode");

			if (code == 101) {

				org.json.JSONObject result = responseJson.getJSONObject("result");
//				org.json.JSONArray address = result.getJSONArray("address");
//				org.json.JSONObject address1 = address.getJSONObject(0);
//				org.json.JSONObject validity = result.getJSONObject("validity");
//
//				resultObject.put("drivingLicenseNumber", dlNo);
//				resultObject.put("name", result.get("name"));
//				resultObject.put("addressLine", address1.get("addressLine1"));
//				resultObject.put("completeAddress", address1.get("completeAddress"));
//				resultObject.put("district", address1.get("district"));
//				resultObject.put("pin", address1.get("pin"));
//				resultObject.put("state", address1.get("state"));
//				resultObject.put("issueDate", result.get("issueDate"));
//				resultObject.put("fatherOrHusbandName", result.get("father/husband"));
//				resultObject.put("validFrom", result.get("issueDate"));
//				resultObject.put("drivingLicenseNumberStatus", "VALID");
//				resultObject.put("validTo", validity.get("nonTransport"));

				map = ESignServices.setResponse(ResponseMessage.SUCCESS, ResponseMessage.API_STATUS_SUCCESS,
						ResponseMessage.DATA_SUCCESS);
//				map.put("merchantTxnRefId", merchantTrxnRefId);
				map.put("result", result.toMap());
				return map;

			} else {
//				map = ESignServices.setResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED,
//						ResponseMessage.DATA_NOT_FOUND);
				map = ESignServices.setResponse(ResponseMessage.SUCCESS, ResponseMessage.API_STATUS_SUCCESS,
						"Invalid dlNo.");
//				map.put("drivingLicenseNumber", dlNo);
//				map.put("merchantTxnRefId", merchantTrxnRefId);
			}

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> mobileNumberAuthenticationApi(String mobileNo) {

		Map<String, Object> map = new HashMap<>();
		try {

			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType,
					"{\"countryCode\":\"91\",\"mobile\":\"" + mobileNo + "\",\"consent\":\"Y\"}");
			Request request = new Request.Builder().url("https://testapi.karza.in/v3/mobile-auth").method("POST", body)
					.addHeader("x-karza-key", "NmXy370lZytA27VA").addHeader("Content-Type", "application/json").build();
			Response response = client.newCall(request).execute();

			String r = response.body().string();

			LOGGER.info("finalRes: " + r);

			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> mapObject = objectMapper.readValue(r, Map.class);

			Map<String, Object> result = (Map<String, Object>) mapObject.get("result");

			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, "Mobile number authentication details");
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			map.put("result", result);

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}

		return map;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> epfUANValidationAPI(@Valid String uanNumber) {

		Map<String, Object> map = new HashMap<>();
		Long statusCode = 0L;
		try {

			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType,
					"{\"uan\":\"" + uanNumber + "\",\"consent\":\"Y\",\"clientData\":{\"caseId\":\"123456\"}}");
			Request request = new Request.Builder().url("https://testapi.karza.in/v3/epf-auth").method("POST", body)
					.addHeader("x-karza-key", "NmXy370lZytA27VA").addHeader("Content-Type", "application/json").build();
			Response response = client.newCall(request).execute();

			String s = response.body().string();

			LOGGER.info("request: " + request);
			LOGGER.info("response: " + s);

			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> readValue = objectMapper.readValue(s, Map.class);

			statusCode = ((Number) readValue.get("statusCode")).longValue();
			LOGGER.info("statusCode: {}", statusCode);

			LOGGER.info("readValue: " + readValue);
			if (statusCode == 101) {

				Map<String, Object> result = (Map<String, Object>) readValue.get("result");
				LOGGER.info("result: " + result);

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "EPF UAN details");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put("data", result);

			} else {

				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Please pass valid UAN number");
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
	public Map<String, Object> electricityBillAuthentication(String consumerId, String serviceProviderCode) {

		Map<String, Object> map = new HashMap<>();

		try {

			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, "{\"consent\":\"Y\",\"consumer_id\":\""
					+ consumerId.toUpperCase() + "\",\"service_provider\":\"" + serviceProviderCode + "\"}");
			Request request = new Request.Builder().url("https://testapi.karza.in/v2/elec").method("POST", body)
					.addHeader("x-karza-key", "NmXy370lZytA27VA").addHeader("Content-Type", "application/json").build();
			Response response = client.newCall(request).execute();

			String finalRes = response.body().string();

			LOGGER.info("finalRes: " + finalRes);

			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> readValue = objectMapper.readValue(finalRes, Map.class);

			String statusCode = (String) readValue.get("status-code");

			if (statusCode != null && statusCode.equals("101")) {
				Map<String, Object> result = (Map<String, Object>) readValue.get("result");

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.ELECTRICITY_BILL_DETAILS);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put("data", result);
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			}

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;
	}

	public Map<String, Object> GSTSearchBasisPAN(String panNo, String merchantTrxnRefId) {

		Map<String, Object> map = new HashMap<>();

		try {
			String apiRequest = "{\r\n  \"consent\": \"Y\",\r\n  \"pan\": \"" + panNo
					+ "\",\r\n  \"clientData\": {\r\n    \"caseId\": \"123456\"\r\n  }\r\n}";

			LOGGER.info("apiRequest: {}", apiRequest);

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
			headers.set("x-karza-key", X_KARZA_KEY);

			HttpEntity<String> entity = new HttpEntity<>(apiRequest, headers);
			ResponseEntity<String> response = restTemplate.exchange(GST_SEARCH_BASIS_PAN_URL, HttpMethod.POST, entity,
					String.class);
			String apiResponse = response.getBody();

			org.json.JSONObject resultJsonObject = new org.json.JSONObject(apiResponse);

			LOGGER.info("apiResponse: {}", apiResponse);

			if ((int) resultJsonObject.getInt("statusCode") == 101) {
				JSONArray result = resultJsonObject.getJSONArray("result");
				map = ESignServices.setResponse(ResponseMessage.SUCCESS, ResponseMessage.API_STATUS_SUCCESS,
						ResponseMessage.DATA_SUCCESS);
				map.put("merchantTxnRefId", merchantTrxnRefId);
				map.put("Data", result.toList());
				return map;

			}

			map = ESignServices.setResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED,
					ResponseMessage.DATA_NOT_FOUND);
			map.put("merchantTxnRefId", merchantTrxnRefId);

		} catch (Exception e) {
			LOGGER.error("Exception: {}", e);
			map = ESignServices.setResponse(ResponseMessage.SOMETHING_WENT_WRONG, ResponseMessage.API_STATUS_FAILED,
					ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		}
		return map;
	}

	public Map<String, Object> panDetailsAPI(@Valid String panNumber, String merchantTrxnRefId) {
		Map<String, Object> map = new HashMap<>();
		try {
			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, "{\n\"pano\":\"" + panNumber
					+ "\",\n\"client_ref_num\":7856566,\n\"dob\":\"1400-02-12\",\n\"identifier_type\":\"PAN\"\n}");
			Request request = new Request.Builder().url("http://regtechapi.in/api/search").method("POST", body)
					.addHeader("AccessToken", "106c96e6f6567e6c99f8faddee91ad485b2")
					.addHeader("Content-Type", "application/json").build();
			Response response = client.newCall(request).execute();

			String rResponse = response.body().string();

			LOGGER.info("request: " + request);

			LOGGER.info("response: " + rResponse);

			org.json.JSONObject object = new org.json.JSONObject(rResponse);

			long statusCode = object.getLong("statusCode");

			if (statusCode == 200) {
				String name = object.getJSONObject("response").getJSONObject("kycDetails")
						.getJSONObject("personalIdentifiableData").getJSONObject("personalDetails")
						.getString("fullName");

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_SUCCESS);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put("name", name);
				map.put("panStatus", "VALID");
				map.put("panNumber", panNumber);
				map.put("merchantTxnRefId", merchantTrxnRefId);
				return map;
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put("panNumber", panNumber);
				map.put("merchantTxnRefId", merchantTrxnRefId);
				return map;
			}
//			try {
//				org.json.JSONObject result = object.getJSONObject("result");
//
//				LOGGER.info("result: " + result);
//
//				String firstName = result.getString("firstName");
//
//				LOGGER.info("firstName: " + firstName);
//
//				String lastName = result.getString("lastName");
//				String middleName = result.getString("middleName");
//
//				String pan = result.getString("pan");
//				String dob = result.getString("dob");
//				String gender = result.getString("gender");
//				boolean aadhaarLinked = result.getBoolean("aadhaarLinked");
//
//				// JSONObject object2 = new JSONObject();
//				map.put("firstName", firstName);
//				map.put("lastName", lastName);
//				map.put("middleName", middleName);
//				map.put("name", "NA");
//				map.put("number", pan);
//
//				map.put("panStatusCode", "NA");
//				map.put("typeOfHolder", "Individual or Person");
//				map.put("isValid", true);
//				map.put("isIndividual", true);
//
//				map.put("title", "NA");
//				map.put("panStatus", "VALID");
//				map.put("lastUpdatedOn", "NA");
//
//				if (aadhaarLinked == true) {
//					map.put("aadhaarSeedingStatusCode", "Y");
//					map.put("aadhaarSeedingStatus", "Successful");
//				} else {
//					map.put("aadhaarSeedingStatusCode", "N");
//					map.put("aadhaarSeedingStatus", "Failed");
//				}
//
//				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
//				map.put(ResponseMessage.DESCRIPTION, "pan status details");
//				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
//				return map;
//
//			} catch (Exception e) {
//				String error = object.getString("error");
//
//				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
//				map.put(ResponseMessage.DESCRIPTION, error);
//				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
//			}

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;
	}

}
