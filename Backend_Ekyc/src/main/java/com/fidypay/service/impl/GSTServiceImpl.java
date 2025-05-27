package com.fidypay.service.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.collections4.map.HashedMap;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fidypay.ServiceProvider.Decentro.DecentroServiceImpl;
import com.fidypay.ServiceProvider.Karza.KarzaService;
import com.fidypay.ServiceProvider.Signzy.EKYCService;
import com.fidypay.ServiceProvider.Signzy.MerchantServiceChargeService;
import com.fidypay.ServiceProvider.Signzy.SignzyService;
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
import com.fidypay.service.GSTService;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.DateAndTime;
import com.fidypay.utils.ex.GenerateTrxnRefId;
import com.fidypay.utils.ex.RandomNumberGenrator;
import com.fidypay.utils.ex.Validations;
import com.fidypay.wallet.WalletNotification;
import com.fidypay.wallet.WalletRequest;
import com.fidypay.wallet.WalletService;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class GSTServiceImpl implements GSTService {

	private static final Logger LOGGER = LoggerFactory.getLogger(GSTServiceImpl.class);

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
	private MerchantWalletTransactionsRepository merchantWalletTransactionsRepository;

	@Autowired
	private ServiceProvidersRepository serviceprovidersrepository;

	@Autowired
	private EkycTransactionDetailsRepository ekycTransactionDetailsRepository;

	@Autowired
	private SignzyService signzyService;

	@Autowired
	private EKYCService ekycService;

	@Autowired
	private KarzaService karzaService;

	@Autowired
	private DecentroServiceImpl decentroServiceImpl;
	
	@Autowired
	private WalletService walletService;

//	@Autowired
//	private EkycTransactionDetailsSearchRepository ekycTransactionDetailsSearchRepository;

	// UAT
//	public static final String USERNAME = "fidypay_test";
//	public static final String PASSWORD = "6zvWtnSar8dTjPDwr8dv";
//	public static final String BASE_URL = "https://preproduction.signzy.tech/api/v2/patrons/";

	// LIVE
	public static final String USERNAME = "fidypay_prod";
	public static final String PASSWORD = "u4wwVbDFy2xYMrbRU8xs";
	public static final String BASE_URL = "https://signzy.tech/api/v2/patrons/";
	public final static String PAN = "[A-Z]{5}[0-9]{4}[A-Z]{1}";
	public final static String GSTIN = "[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}";
	private final Semaphore semaphore = new Semaphore(1);
	private final Lock lock = new ReentrantLock();

	@Override
	public String GSTINSearch(String GSTIN) throws Exception {
		// 36AADCF6310J1ZR

		String finalResponse = null;
		try {
			String loginResponse = new GSTServiceImpl().login(USERNAME, PASSWORD);
			JSONObject jsonObject = new JSONObject(loginResponse);
			String id = jsonObject.getString("id");
			String userId = jsonObject.getString("userId");

			String requestStr = " {\r\n" + "    \"task\" : \"gstinSearch\",\r\n" + "    \"essentials\": {\r\n"
					+ "        \"gstin\": \"" + GSTIN + "\"\r\n" + "    }\r\n" + "  }\r\n" + "";

			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, requestStr);
			Request request = new Request.Builder().url(BASE_URL + userId + "/gstns").method("POST", body)
					.addHeader("Authorization", id).addHeader("Content-Type", "application/json").build();
			Response response = client.newCall(request).execute();
			finalResponse = response.body().string();
		} catch (Exception e) {
		}
		return finalResponse;
	}

	@Override
	public String GSTINDetailsSearch(String GSTIN) throws Exception {
		// 36AADCF6310J1ZR
		String finalResponse = null;
		try {
			String loginResponse = new GSTServiceImpl().login(USERNAME, PASSWORD);
			JSONObject jsonObject = new JSONObject(loginResponse);
			String id = jsonObject.getString("id");
			String userId = jsonObject.getString("userId");

			String requestStr = " {\r\n" + "    \"task\" : \"detailedGstinSearch\",\r\n" + "    \"essentials\": {\r\n"
					+ "        \"gstin\": \"" + GSTIN + "\"\r\n" + "    }\r\n" + "  }\r\n" + "";

			LOGGER.info("requestStr: " + requestStr);
			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, requestStr);
			Request request = new Request.Builder().url(BASE_URL + userId + "/gstns").method("POST", body)
					.addHeader("Authorization", id).addHeader("Content-Type", "application/json").build();

			LOGGER.info("request: " + request);

			Response response = client.newCall(request).execute();
			finalResponse = response.body().string();

			LOGGER.info("response: " + finalResponse);
		} catch (Exception e) {

		}
		return finalResponse;
	}

	@Override
	public String GSTSearchCompanyName(String companyName) throws Exception {
		// 36AADCF6310J1ZR
		String finalResponse = null;
		try {
			String loginResponse = new GSTServiceImpl().login(USERNAME, PASSWORD);
			JSONObject jsonObject = new JSONObject(loginResponse);
			String id = jsonObject.getString("id");
			String userId = jsonObject.getString("userId");

			String requestStr = " {\r\n" + "    \"task\" : \"companyNameToGst\",\r\n" + "    \"essentials\": {\r\n"
					+ "        \"companyName\": \"" + companyName + "\"\r\n" + "    }\r\n" + "  }\r\n" + "";

			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, requestStr);
			Request request = new Request.Builder().url(BASE_URL + userId + "/gstns").method("POST", body)
					.addHeader("Authorization", id).addHeader("Content-Type", "application/json").build();
			Response response = client.newCall(request).execute();
			finalResponse = response.body().string();

			JSONObject js = new JSONObject(finalResponse);
			JSONObject jsonResponse = new JSONObject();
			if (js.has("error")) {
				JSONObject error = js.getJSONObject("error");
				String message = error.getString("message");

				jsonResponse.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				jsonResponse.put(ResponseMessage.DESCRIPTION, message);
				jsonResponse.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				finalResponse = jsonResponse.toString();

			}
		} catch (Exception e) {

		}
		return finalResponse;
	}

	@Override
	public String login(String username, String password) throws Exception {
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
		return finalResposne;
	}

	// ---------------------------------------------------

	@Override
	public synchronized Map<String, Object> saveDataForGSTINSearch(String accountNumber, long merchantId, Double merchantWallet,
			String businessName, String email) {
		Map<String, Object> map = new HashedMap<>();
		try {
			semaphore.acquire();
			lock.lock();
			String serviceName = "GST Number";
			LOGGER.info("  Inside GSTINSearch  ");

			String pcOptionName = "Wallet";
			String amount = "1.50";
			accountNumber = accountNumber.toUpperCase();
			double amtInDouble = Double.parseDouble(amount);

//			if (!accountNumber.matches(GSTIN)) {
//
//				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
//				map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
//				map.put(ResponseMessage.DESCRIPTION, "Invalid GSTIN");
//				return map;
//			}

			if (accountNumber.isEmpty() || accountNumber.length() < 8 || accountNumber.length() > 20
					|| !accountNumber.matches("^[a-zA-Z\\d-]+$")) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

				if (accountNumber.isEmpty()) {
					map.put(ResponseMessage.DESCRIPTION, "GSTIN can't be empty.");
				} else if (accountNumber.length() < 8 || accountNumber.length() > 20) {
					map.put(ResponseMessage.DESCRIPTION, "GSTIN must be between 8 to 20.");
				} else {
					map.put(ResponseMessage.DESCRIPTION, "Invalid GSTIN.");
				}

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
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
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

			String resWallet = walletNotification.checkWalletBalance(merchantWallet, businessName, email);

			if (merchantWallet < charges) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}

			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			EkycRequest ekycRequest = ekycRequestRepository
					.save(new EkycRequest(merchantId, accountNumber, accountNumber, trxnDate));
			long ekycRequestId = ekycRequest.getRequestId();

//			Bank bank = new Bank();
//			ThreadWithdrawal test = new ThreadWithdrawal(bank, merchantTrxnRefId, charges, merchantWallet, merchantId,
//					merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId, serviceName, trxnRefId,
//					walletTxnRefNo, serviceName);
//			LOGGER.info("Name of Thread ----------------------- " + test.getName());
//			test.start();
			
			WalletRequest debitRequest = new WalletRequest(merchantId, charges, "Debit", serviceName, serviceName,
					ekycRequestId, trxnRefId, merchantTrxnRefId, walletTxnRefNo);
			walletService.enqueueTransaction(debitRequest);

			if (Validations.isValidGSTNo(accountNumber.toUpperCase())) {

				map = signzyService.gstinSearch(accountNumber, merchantTrxnRefId);

				String serviceProvider = ResponseMessage.SIGNZY;
				ServiceProviders spInfo = serviceprovidersrepository
						.findBySpName(Encryption.encString(serviceProvider));
				Long serviceProviderId = spInfo.getServiceProviderId();
				EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
						.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
								ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
								serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
								serviceProviderId, '0'));

//				EkycTransactionDetails elk = ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
//				LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'VALID GSTNO' EKYC TRXN DETAILS : {}", elk);

			} else {
				String serviceProvider = "Signzy";
				ServiceProviders spInfo = serviceprovidersrepository
						.findBySpName(Encryption.encString(serviceProvider));
				Long serviceProviderId = spInfo.getServiceProviderId();
				EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
						.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
								ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
								serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
								serviceProviderId, '0'));

//				EkycTransactionDetails elk = ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
//				LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'INVALID GSTNO' EKYC TRXN DETAILS : {}", elk);

				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.GST_NUMBER_INVALID);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			}
		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		} finally {
			lock.unlock();
			semaphore.release();
		}
		return map;
	}

	@Override
	public synchronized Map<String, Object> saveDataForGSTDetailSearch(String accountNumber, long merchantId, Double merchantWallet, String businessName, String email) {
		Map<String, Object> map = new HashedMap<>();
		try {
			semaphore.acquire();
			lock.lock();
			String serviceName = "GST Number V2";
			LOGGER.info("  Inside GSTINSearch  ");

			String pcOptionName = "Wallet";
			String amount = "6.00";

			double amtInDouble = Double.parseDouble(amount);

			accountNumber = accountNumber.toUpperCase();

			if (accountNumber.isEmpty() || accountNumber.length() < 8 || accountNumber.length() > 20
					|| !accountNumber.matches("^[a-zA-Z\\d-]+$")) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

				if (accountNumber.isEmpty()) {
					map.put(ResponseMessage.DESCRIPTION, "GSTIN can't be empty.");
				} else if (accountNumber.length() < 8 || accountNumber.length() > 20) {
					map.put(ResponseMessage.DESCRIPTION, "GSTIN must be between 8 to 20.");
				} else {
					map.put(ResponseMessage.DESCRIPTION, "Invalid GSTIN.");
				}

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

			String resWallet = walletNotification.checkWalletBalance(merchantWallet, businessName, email);

			if (merchantWallet < charges) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}

			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			EkycRequest ekycRequest = ekycRequestRepository
					.save(new EkycRequest(merchantId, accountNumber, accountNumber, trxnDate));
			long ekycRequestId = ekycRequest.getRequestId();

//			Bank bank = new Bank();
//			ThreadWithdrawal test = new ThreadWithdrawal(bank, merchantTrxnRefId, charges, merchantWallet, merchantId,
//					merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId, serviceName, trxnRefId,
//					walletTxnRefNo, serviceName);
//			LOGGER.info("Name of Thread ----------------------- " + test.getName());
//			test.start();

			WalletRequest debitRequest = new WalletRequest(merchantId, charges, "Debit", serviceName, serviceName,
					ekycRequestId, trxnRefId, merchantTrxnRefId, walletTxnRefNo);
			walletService.enqueueTransaction(debitRequest);
			
			if (Validations.isValidGSTNo(accountNumber.toUpperCase())) {

				// response = gstService.GSTINDetailsSearch(accountNumber);

				map = signzyService.gstinDetailsSearch(accountNumber.toUpperCase());

				String serviceProvider = ResponseMessage.SIGNZY;
				ServiceProviders spInfo = serviceprovidersrepository
						.findBySpName(Encryption.encString(serviceProvider));
				Long serviceProviderId = spInfo.getServiceProviderId();
				EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
						.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
								ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
								serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
								serviceProviderId, '0'));

//				EkycTransactionDetails elk = ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
//				LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'ISVALID GSTNO' EKYC TRXN DETAILS : {}", elk);

			} else {
				String serviceProvider = "Signzy";
				ServiceProviders spInfo = serviceprovidersrepository
						.findBySpName(Encryption.encString(serviceProvider));
				Long serviceProviderId = spInfo.getServiceProviderId();
				EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
						.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
								ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
								serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
								serviceProviderId, '0'));

//				EkycTransactionDetails elk = ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
//				LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'ISINVALID GSTNO' EKYC TRXN DETAILS : {}", elk);

				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.GST_NUMBER_INVALID);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

			}

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		} finally {
			lock.unlock();
			semaphore.release();
		}
		return map;
	}

	@Override
	public synchronized Map<String, Object> saveDataForGSTSearchCompanyName(String accountNumber, long merchantId, Double merchantWallet, String businessName, String email) {
		Map<String, Object> map = new HashedMap<>();
		try {
			semaphore.acquire();
			lock.lock();
			String serviceName = "GST Company Name";
			LOGGER.info("  Inside GSTINSearch  ");

			String pcOptionName = "Wallet";
			String amount = "6.00";

			double amtInDouble = Double.parseDouble(amount);

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

			String resWallet = walletNotification.checkWalletBalance(merchantWallet, businessName, email);

			if (merchantWallet < charges) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}

			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			if (Validations.isValidGSTNo2(accountNumber)) {

				EkycRequest ekycRequest = ekycRequestRepository
						.save(new EkycRequest(merchantId, accountNumber, accountNumber, trxnDate));
				long ekycRequestId = ekycRequest.getRequestId();

//				Bank bank = new Bank();
//				ThreadWithdrawal test = new ThreadWithdrawal(bank, merchantTrxnRefId, charges, merchantWallet,
//						merchantId, merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId,
//						serviceName, trxnRefId, walletTxnRefNo, serviceName);
//				LOGGER.info("Name of Thread ----------------------- " + test.getName());
//				test.start();
				
				WalletRequest debitRequest = new WalletRequest(merchantId, charges, "Debit", serviceName, serviceName,
						ekycRequestId, trxnRefId, merchantTrxnRefId, walletTxnRefNo);
				walletService.enqueueTransaction(debitRequest);

				map = signzyService.gstSearchCompanyName(accountNumber);

				String serviceProvider = ResponseMessage.SIGNZY;
				ServiceProviders spInfo = serviceprovidersrepository
						.findBySpName(Encryption.encString(serviceProvider));
				Long serviceProviderId = spInfo.getServiceProviderId();
				EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
						.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
								ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
								serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
								serviceProviderId, '0'));
//				EkycTransactionDetails elk = ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
//				LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'GST SEARCH COMPANY NAME' EKYC TRXN DETAILS : {}", elk);

			} else {

				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Pleasee pass valid company name");
				return map;

			}

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		} finally {
			lock.unlock();
			semaphore.release();
		}
		return map;
	}

	@Override
	public synchronized Map<String, Object> saveDataForGSTINSearchKarza(String gSTIN, long merchantId, Double merchantFloatAmount,
			String businessName, String email) {
		Map<String, Object> map = new HashedMap<>();
		try {
			semaphore.acquire();
			lock.lock();
			String serviceName = "GST Number";
			double amtInDouble = 1.50;
			gSTIN = gSTIN.toUpperCase();

			if (gSTIN.isEmpty() || gSTIN.length() < 8 || gSTIN.length() > 20 || !gSTIN.matches("^[a-zA-Z\\d-]+$")) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

				if (gSTIN.isEmpty()) {
					map.put(ResponseMessage.DESCRIPTION, "GSTIN can't be empty.");
				} else if (gSTIN.length() < 8 || gSTIN.length() > 20) {
					map.put(ResponseMessage.DESCRIPTION, "GSTIN must be between 8 to 20.");
				} else {
					map.put(ResponseMessage.DESCRIPTION, "Invalid GSTIN.");
				}

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

			String serviceProvider = ResponseMessage.KARZA;
			ServiceProviders spInfo = serviceprovidersrepository.findBySpName(Encryption.encString(serviceProvider));
			Long serviceProviderId = spInfo.getServiceProviderId();

			EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
					.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
							ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
							serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
							serviceProviderId, '0'));

//			EkycTransactionDetails elk = ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
//			LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'GSTIN SEARCH KARZA' EKYC TRXN DETAILS : {}", elk);

			// res = gstSearch(gSTIN);

			map = karzaService.gstSearch(gSTIN, merchantTrxnRefId);

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		} finally {
			lock.unlock();
			semaphore.release();
		}
		return map;
	}

	@Override
	public synchronized Map<String, Object> gstinAuthentication(String gSTIN, long merchantId, Double merchantFloatAmount, String businessName, String email) {
		Map<String, Object> map = new HashedMap<>();
		try {
			semaphore.acquire();
			lock.lock();
			String serviceName = "OCR EKYC";
			double amtInDouble = 1.0;

			gSTIN = gSTIN.toUpperCase();

			if (gSTIN.isEmpty() || gSTIN.length() < 8 || gSTIN.length() > 20 || !gSTIN.matches("^[a-zA-Z\\d-]+$")) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

				if (gSTIN.isEmpty()) {
					map.put(ResponseMessage.DESCRIPTION, "GSTIN can't be empty.");
				} else if (gSTIN.length() < 8 || gSTIN.length() > 20) {
					map.put(ResponseMessage.DESCRIPTION, "GSTIN must be between 8 to 20.");
				} else {
					map.put(ResponseMessage.DESCRIPTION, "Invalid GSTIN.");
				}

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

			String serviceProvider = ResponseMessage.KARZA;
			ServiceProviders spInfo = serviceprovidersrepository.findBySpName(Encryption.encString(serviceProvider));
			Long serviceProviderId = spInfo.getServiceProviderId();
			EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
					.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
							ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
							serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
							serviceProviderId, '0'));

//			EkycTransactionDetails elk = ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
//			LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'GSTIN AUTHENTICATION' EKYC TRXN DETAILS : {}", elk);
			// res = gstAuthentication(gSTIN);

			map = karzaService.gstAuthentication(gSTIN);

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		} finally {
			lock.unlock();
			semaphore.release();
		}
		return map;
	}

	@Override
	public synchronized Map<String, Object> saveDataForGSTINSearchByPan(String pan, long merchantId, Double merchantFloatAmount, String businessName, String email) {
		Map<String, Object> map = new HashMap<>();

		LOGGER.info(" Inside saveDataForGSTINSearchByPan ");

		try {
			semaphore.acquire();
			lock.lock();
			String serviceName = "GST VERIFY USING PAN";
			pan = pan.toUpperCase();

			if (pan.isEmpty() || pan.length() < 6 || pan.length() > 15 || !pan.matches("^[a-zA-Z\\d-]+$")) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

				if (pan.isEmpty()) {
					map.put(ResponseMessage.DESCRIPTION, "panNumber can't be empty.");
				} else if (pan.length() < 6 || pan.length() > 15) {
					map.put(ResponseMessage.DESCRIPTION, "panNumber must be between 6 to 15.");
				} else {
					map.put(ResponseMessage.DESCRIPTION, "Invalid PAN.");
				}

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

			String amount = "6.00";
			double amtInDouble = Double.parseDouble(amount);

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
			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			EkycRequest ekycRequest = ekycRequestRepository
					.save(new EkycRequest(merchantId, merchantTrxnRefId, merchantTrxnRefId, trxnDate));
			long ekycRequestId = ekycRequest.getRequestId();

//			Bank bank = new Bank();
//			ThreadWithdrawal test = new ThreadWithdrawal(bank, merchantTrxnRefId, charges, merchantFloatAmount,
//					merchantId, merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId, serviceName,
//					trxnRefId, walletTxnRefNo, serviceName);
//			LOGGER.info("Name of Thread ----------------------- " + test.getName());
//			test.start();
			
			WalletRequest debitRequest = new WalletRequest(merchantId, charges, "Debit", serviceName, serviceName,
					ekycRequestId, trxnRefId, merchantTrxnRefId, walletTxnRefNo);
			walletService.enqueueTransaction(debitRequest);
			
			map = signzyService.GSTINSearchByPanAndState(pan);
			String serviceProvider = ResponseMessage.SIGNZY;
			ServiceProviders spInfo = serviceprovidersrepository.findBySpName(Encryption.encString(serviceProvider));
			Long serviceProviderId = spInfo.getServiceProviderId();
			EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
					.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
							ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
							serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
							serviceProviderId, '0'));

//			EkycTransactionDetails elk = ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
//			LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'GSTIN SEARCH BY PAN' EKYC TRXN DETAILS : {}", elk);

			LOGGER.info("ekycTransactionDetails: {}", ekycTransactionDetails);

		} catch (Exception e) {
			LOGGER.info("Inside catch block ----------------------- ");
			LOGGER.error("Exception: {}", e);
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		} finally {
			lock.unlock();
			semaphore.release();
		}
		return map;

	}

	@Override
	public synchronized Map<String, Object> GSTSearchBasisPAN(String panNo, long merchantId, Double merchantFloatAmount,
			String businessName, String email) {
		Map<String, Object> map = new HashMap<>();

		try {
			semaphore.acquire();
			lock.lock();
			String serviceName = "BASIC GST VERIFY USING PAN";

			LOGGER.info(" Inside GSTSearchBasisPAN ");

			String amount = "6.00";
			double amtInDouble = Double.parseDouble(amount);

			panNo = panNo.toUpperCase();

			if (!panNo.matches(PAN)) {

				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Invalid PAN");
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

			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			EkycRequest ekycRequest = ekycRequestRepository
					.save(new EkycRequest(merchantId, merchantTrxnRefId, merchantTrxnRefId, trxnDate));
			long ekycRequestId = ekycRequest.getRequestId();

//			Bank bank = new Bank();
//			ThreadWithdrawal test = new ThreadWithdrawal(bank, merchantTrxnRefId, amtInDouble, merchantFloatAmount,
//					merchantId, merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId, serviceName,
//					trxnRefId, walletTxnRefNo, serviceName);
//			LOGGER.info("Name of Thread ----------------------- " + test.getName());
//			test.start();
			
			WalletRequest debitRequest = new WalletRequest(merchantId, charges, "Debit", serviceName, serviceName,
					ekycRequestId, trxnRefId, merchantTrxnRefId, walletTxnRefNo);
			walletService.enqueueTransaction(debitRequest);
			
			map = karzaService.GSTSearchBasisPAN(panNo, merchantTrxnRefId);
			String serviceProvider = ResponseMessage.KARZA;
			ServiceProviders spInfo = serviceprovidersrepository.findBySpName(Encryption.encString(serviceProvider));
			Long serviceProviderId = spInfo.getServiceProviderId();
			EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
					.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
							ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
							serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
							serviceProviderId, '0'));

			LOGGER.info("ekycTransactionDetails: {}", ekycTransactionDetails);

//			EkycTransactionDetails elk = ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
//			LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'GST SEARCH BASIC PAN' EKYC TRXN DETAILS : {}", elk);

		} catch (Exception e) {
			LOGGER.info("Inside catch block ----------------------- ");
			LOGGER.error("Exception: {}", e);
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		} finally {
			lock.unlock();
			semaphore.release();
		}
		return map;
	}

	@Override
	public Map<String, Object> saveDataForValidateGSTINSearch(String gSTIN, long merchantId, Double merchantFloatAmount, String businessName, String email) {
		Map<String, Object> map = new HashMap<>();

		try {
			semaphore.acquire();
			lock.lock();

			String serviceName = "GST Number";
			LOGGER.info(" Inside saveDataForValidateGSTINSearch ");

			String amount = "1.50";
			double amtInDouble = Double.parseDouble(amount);

			gSTIN = gSTIN.toUpperCase();

			if (gSTIN.isEmpty() || gSTIN.length() < 8 || gSTIN.length() > 20 || !gSTIN.matches("^[a-zA-Z\\d-]+$")) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

				if (gSTIN.isEmpty()) {
					map.put(ResponseMessage.DESCRIPTION, "GSTIN can't be empty.");
				} else if (gSTIN.length() < 8 || gSTIN.length() > 20) {
					map.put(ResponseMessage.DESCRIPTION, "GSTIN must be between 8 to 20.");
				} else {
					map.put(ResponseMessage.DESCRIPTION, "Invalid GSTIN.");
				}

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

			if (!gSTIN.matches(GSTIN)) {

				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Invalid GSTIN");
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

			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			EkycRequest ekycRequest = ekycRequestRepository
					.save(new EkycRequest(merchantId, merchantTrxnRefId, merchantTrxnRefId, trxnDate));
			long ekycRequestId = ekycRequest.getRequestId();

//			Bank bank = new Bank();
//			ThreadWithdrawal test = new ThreadWithdrawal(bank, merchantTrxnRefId, charges, merchantFloatAmount,
//					merchantId, merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId, serviceName,
//					trxnRefId, walletTxnRefNo, serviceName);
//			LOGGER.info("Name of Thread ----------------------- " + test.getName());
//			test.start();
			
			WalletRequest debitRequest = new WalletRequest(merchantId, charges, "Debit", serviceName, serviceName,
					ekycRequestId, trxnRefId, merchantTrxnRefId, walletTxnRefNo);
			walletService.enqueueTransaction(debitRequest);

			map = decentroServiceImpl.validateGstin(gSTIN, merchantTrxnRefId);

			String serviceProvider = ResponseMessage.DECENTRO;
			ServiceProviders spInfo = serviceprovidersrepository.findBySpName(Encryption.encString(serviceProvider));
			Long serviceProviderId = spInfo.getServiceProviderId();
			EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
					.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
							ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
							serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
							serviceProviderId, '0'));

			LOGGER.info("ekycTransactionDetails: {}", ekycTransactionDetails);

//			EkycTransactionDetails elk = ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
//			LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'SEARCH GSTIN' EKYC TRXN DETAILS : {}", elk);

		} catch (Exception e) {
			LOGGER.info("Inside catch block ----------------------- ");
			LOGGER.error("Exception: {}", e);
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		} finally {
			lock.unlock();
			semaphore.release();
		}
		return map;
	}
}
