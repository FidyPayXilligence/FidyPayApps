package com.fidypay.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.validation.Valid;

import org.apache.commons.collections4.map.HashedMap;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.JSONArray;
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
import com.fidypay.request.PanAadharRequest;
import com.fidypay.service.PanService;
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
public class PanServiceImpl implements PanService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PanServiceImpl.class);

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

	public final static String PAN = "[A-Z]{5}[0-9]{4}[A-Z]{1}";
	private final Semaphore semaphore = new Semaphore(1);
	private final Lock lock = new ReentrantLock();

	public String fetchPan(String number) throws Exception {
		String finalResponse = null;
		JSONObject jsonObj = new JSONObject();
		try {

			String loginResponse = new PanServiceImpl().login(GSTServiceImpl.USERNAME, GSTServiceImpl.PASSWORD);
			LOGGER.info("Login  : " + loginResponse);
			JSONObject jsonObject = new JSONObject(loginResponse);
			String id = jsonObject.getString("id");
			String userId = jsonObject.getString("userId");

			JSONObject object = new JSONObject();
			object.put("task", "fetch");

			JSONObject essentials = new JSONObject();
			essentials.put("number", number);
			object.put("essentials", essentials);

			String req = object.toString();

			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, req);
			Request request = new Request.Builder().url(GSTServiceImpl.BASE_URL + userId + "/panv2")
					.method("POST", body).addHeader("Authorization", id).addHeader("Content-Type", "application/json")
					.build();
			Response response = client.newCall(request).execute();

			String results = response.body().string();
			JSONObject responseJson = new JSONObject(results);
			JSONObject resultJson = responseJson.getJSONObject("result");
			finalResponse = resultJson.toString();
			LOGGER.info("PAN Response : " + finalResponse);
		} catch (JSONException e) {
			jsonObj.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICE_NOT_AVILABLE);
			jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			finalResponse = jsonObj.toString();
		} catch (Exception e) {
			jsonObj.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			finalResponse = jsonObj.toString();
		}
		return finalResponse;
	}

	public String fetchPanV2(String number) throws Exception {
		String finalResponse = null;
		JSONObject jsonObj = new JSONObject();
		try {

			String loginResponse = new PanServiceImpl().login(GSTServiceImpl.USERNAME, GSTServiceImpl.PASSWORD);
			JSONObject jsonObject = new JSONObject(loginResponse);
			String id = jsonObject.getString("id");
			String userId = jsonObject.getString("userId");

			JSONObject object = new JSONObject();

			JSONArray array = new JSONArray();

			array.add(0, "1");

			object.put("task", array);

			JSONObject essentials = new JSONObject();
			essentials.put("number", number);
			object.put("essentials", essentials);

			String req = object.toString();

			System.out.println("req: " + req);

			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, req);
			Request request = new Request.Builder().url(GSTServiceImpl.BASE_URL + userId + "/panv2")
					.method("POST", body).addHeader("Authorization", id).addHeader("Content-Type", "application/json")
					.build();
			Response response = client.newCall(request).execute();

			String results = response.body().string();
			JSONObject responseJson = new JSONObject(results);
			JSONObject resultJson = responseJson.getJSONObject("result");
			finalResponse = resultJson.toString();

		} catch (JSONException e) {
			jsonObj.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICE_NOT_AVILABLE);
			jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			finalResponse = jsonObj.toString();
		} catch (Exception e) {
			jsonObj.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			finalResponse = jsonObj.toString();
		}
		return finalResponse;
	}

	public String fetchPanV2New(String number) throws Exception {
		String finalResponse = null;
		JSONObject jsonObj = new JSONObject();
		try {

			String loginResponse = new PanServiceImpl().login(GSTServiceImpl.USERNAME, GSTServiceImpl.PASSWORD);
			JSONObject jsonObject = new JSONObject(loginResponse);
			String id = jsonObject.getString("id");
			String userId = jsonObject.getString("userId");

			JSONObject object = new JSONObject();

			JSONArray array = new JSONArray();

			array.add(0, "1");

			object.put("task", array);

			JSONObject essentials = new JSONObject();
			essentials.put("number", number);
			object.put("essentials", essentials);

			String req = object.toString();

			System.out.println("req: " + req);

			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, req);
			Request request = new Request.Builder().url(GSTServiceImpl.BASE_URL + userId + "/panv2")
					.method("POST", body).addHeader("Authorization", id).addHeader("Content-Type", "application/json")
					.build();
			Response response = client.newCall(request).execute();

			String results = response.body().string();
			JSONObject responseJson = new JSONObject(results);
			JSONObject resultJson = responseJson.getJSONObject("result");
			finalResponse = resultJson.toString();

		} catch (JSONException e) {
			jsonObj.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICE_NOT_AVILABLE);
			jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			finalResponse = jsonObj.toString();
		} catch (Exception e) {
			jsonObj.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			finalResponse = jsonObj.toString();
		}
		return finalResponse;
	}

	public String compliance(String number) throws Exception {
		String finalResponse = null;
		JSONObject jsonObj = new JSONObject();
		try {

			String loginResponse = new PanServiceImpl().login(GSTServiceImpl.USERNAME, GSTServiceImpl.PASSWORD);
			JSONObject jsonObject = new JSONObject(loginResponse);
			String id = jsonObject.getString("id");
			String userId = jsonObject.getString("userId");

			JSONObject object = new JSONObject();

			JSONArray array = new JSONArray();

			array.add(0, "2");

			object.put("task", array);

			JSONObject essentials = new JSONObject();
			essentials.put("number", number);
			object.put("essentials", essentials);

			String req = object.toString();

			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, req);
			Request request = new Request.Builder().url(GSTServiceImpl.BASE_URL + userId + "/panv2")
					.method("POST", body).addHeader("Authorization", id).addHeader("Content-Type", "application/json")
					.build();
			Response response = client.newCall(request).execute();

			System.out.println("json req : " + req);

			System.out.println("request : " + request);
			String results = response.body().string();
			System.out.println("results : " + results);
			JSONObject responseJson = new JSONObject(results);
			JSONObject resultJson = responseJson.getJSONObject("result");
			finalResponse = resultJson.toString();

		} catch (JSONException e) {
			jsonObj.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICE_NOT_AVILABLE);
			jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			finalResponse = jsonObj.toString();
		} catch (Exception e) {
			jsonObj.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			finalResponse = jsonObj.toString();
		}
		return finalResponse;
	}

	public String fetchCompliance(String number) throws Exception {
		String finalResponse = null;
		JSONObject jsonObj = new JSONObject();
		try {

			String loginResponse = new PanServiceImpl().login(GSTServiceImpl.USERNAME, GSTServiceImpl.PASSWORD);
			JSONObject jsonObject = new JSONObject(loginResponse);
			String id = jsonObject.getString("id");
			String userId = jsonObject.getString("userId");

			JSONObject object = new JSONObject();

			JSONArray array = new JSONArray();

			array.add(0, "1");
			array.add(1, "2");
			object.put("task", array);

			JSONObject essentials = new JSONObject();
			essentials.put("number", number);
			object.put("essentials", essentials);

			String req = object.toString();

			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, req);
			Request request = new Request.Builder().url(GSTServiceImpl.BASE_URL + userId + "/panv2")
					.method("POST", body).addHeader("Authorization", id).addHeader("Content-Type", "application/json")
					.build();
			Response response = client.newCall(request).execute();

			String results = response.body().string();

			System.out.println("results : " + results);
			JSONObject responseJson = new JSONObject(results);
			JSONObject resultJson = responseJson.getJSONObject("result");
			finalResponse = resultJson.toString();

		}

		catch (JSONException e) {
			jsonObj.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICE_NOT_AVILABLE);
			jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			finalResponse = jsonObj.toString();
		} catch (Exception e) {
			jsonObj.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			finalResponse = jsonObj.toString();
		}
		return finalResponse;
	}

	public String login(String username, String password) throws Exception {
		// For Production Credentials
		// https://signzy.tech/api/v2/patrons/login

		String requestStr = "{\r\n" + "    \"username\": \"" + username + "\",\r\n" + "    \"password\": \"" + password
				+ "\"\r\n" + "  }";
		OkHttpClient client = new OkHttpClient().newBuilder().build();
		MediaType mediaType = MediaType.parse("application/json");
		RequestBody body = RequestBody.create(mediaType, requestStr);
		Request request = new Request.Builder().url(GSTServiceImpl.BASE_URL + "login").method("POST", body)
				.addHeader("Content-Type", "application/json").build();
		Response response = client.newCall(request).execute();
		String finalResposne = response.body().string();
		return finalResposne;
	}

	// -------------------------------------------------------------------------------------------------------------

	@Override
	public synchronized Map<String, Object> saveDataForFetchPan(@Valid String panNumber, long merchantId,
			Double merchantWallet, String businessName, String email) {
		Map<String, Object> map = new HashedMap<>();
		try {
			semaphore.acquire();
			lock.lock();
			String serviceName = "PAN Card Basic Verify";
			LOGGER.info("  Inside Fetch Pan  ");

			String pcOptionName = "Wallet";
			String amount = "3.0";
			double amtInDouble = Double.parseDouble(amount);
			panNumber = panNumber.toUpperCase();

			if (panNumber.isEmpty() || panNumber.length() < 6 || panNumber.length() > 15
					|| !panNumber.matches("^[a-zA-Z\\d-]+$")) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

				if (panNumber.isEmpty()) {
					map.put(ResponseMessage.DESCRIPTION, "panNumber can't be empty.");
				} else if (panNumber.length() < 6 || panNumber.length() > 15) {
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
					.save(new EkycRequest(merchantId, panNumber, panNumber, trxnDate));
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

			// response = panService.fetchPan(panNumber);

			map = signzyService.fetchPan(panNumber, merchantTrxnRefId);

			String serviceProvider = ResponseMessage.SIGNZY;
			ServiceProviders spInfo = serviceprovidersrepository.findBySpName(Encryption.encString(serviceProvider));
			Long serviceProviderId = spInfo.getServiceProviderId();
			EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
					.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
							ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
							serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
							serviceProviderId, '0'));

//			EkycTransactionDetails elk = ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
//			LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'FETCH PAN SIGNZY' EKYC TRXN DETAILS : {}", elk);

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

	@Override
	public synchronized Map<String, Object> saveDataForFetchPanDetails(@Valid String panNumber, long merchantId,
			Double merchantWallet, String businessName, String email) {
		Map<String, Object> map = new HashedMap<>();
		try {
			semaphore.acquire();
			lock.lock();
			String serviceName = "PAN Card Details";
//			String serviceName = "OCR EKYC";

			LOGGER.info("  Inside Fetch Pan  ");

			String pcOptionName = "Wallet";
			String amount = "3.0";

			double amtInDouble = Double.parseDouble(amount);

			if (panNumber.isEmpty() || panNumber.length() < 6 || panNumber.length() > 15
					|| !panNumber.matches("^[a-zA-Z\\d-]+$")) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

				if (panNumber.isEmpty()) {
					map.put(ResponseMessage.DESCRIPTION, "panNumber can't be empty.");
				} else if (panNumber.length() < 6 || panNumber.length() > 15) {
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
					.save(new EkycRequest(merchantId, panNumber, panNumber, trxnDate));
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

			if (Validations.isValidPanCardNo(panNumber.toUpperCase())) {
				String serviceProvider = ResponseMessage.SIGNZY;
				ServiceProviders spInfo = serviceprovidersrepository
						.findBySpName(Encryption.encString(serviceProvider));
				Long serviceProviderId = spInfo.getServiceProviderId();
				EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
						.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
								ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
								serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
								serviceProviderId, '0'));
				LOGGER.info("ekycTransactionDetails: " + ekycTransactionDetails);

//				EkycTransactionDetails elk = ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
//				LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'FETCH PAN DETAILS KARZA' EKYC TRXN DETAILS : {}", elk);

				// response = panService.fetchPanV2(panNumber);

				return signzyService.fetchPanV2(panNumber.toUpperCase());

			} else {
				String serviceProvider = "Riskcovry";
				ServiceProviders spInfo = serviceprovidersrepository
						.findBySpName(Encryption.encString(serviceProvider));
				Long serviceProviderId = spInfo.getServiceProviderId();
				EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
						.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
								ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
								serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
								serviceProviderId, '0'));
				LOGGER.info("ekycTransactionDetails: " + ekycTransactionDetails);
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.PAN_CARD_INVALID);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			}

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

	@Override
	public synchronized Map<String, Object> saveDataForFetchPanV3Details(@Valid String panNumber, long merchantId,
			Double merchantWallet, String businessName, String email) {
		Map<String, Object> map = new HashedMap<>();
		try {
			semaphore.acquire();
			lock.lock();
			String serviceName = "PAN Card Details";

			LOGGER.info("Inside Fetch Pan");

			String pcOptionName = "Wallet";
			String amount = "1";

			double amtInDouble = Double.parseDouble(amount);

			panNumber = panNumber.toUpperCase();

			if (panNumber.isEmpty() || panNumber.length() < 6 || panNumber.length() > 15
					|| !panNumber.matches("^[a-zA-Z\\d-]+$")) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

				if (panNumber.isEmpty()) {
					map.put(ResponseMessage.DESCRIPTION, "panNumber can't be empty.");
				} else if (panNumber.length() < 6 || panNumber.length() > 15) {
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
					.save(new EkycRequest(merchantId, panNumber, panNumber, trxnDate));
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

//			if (Validations.isValidPanCardNo(panNumber)) {

			String serviceProvider = ResponseMessage.SIGNZY;
			ServiceProviders spInfo = serviceprovidersrepository.findBySpName(Encryption.encString(serviceProvider));
			Long serviceProviderId = spInfo.getServiceProviderId();
			EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
					.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
							ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
							serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
							serviceProviderId, '0'));

//			EkycTransactionDetails elk = ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
//			LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'FETCH PAN V3 DETAILS' EKYC TRXN DETAILS : {}", elk);
			// response = panService.fetchPanV2New(panNumber);

			map = signzyService.fetchPanV2New(panNumber);

//			} else {
//				String serviceProvider = "Signzy";
//				ServiceProviders spInfo = serviceprovidersrepository
//						.findBySpName(Encryption.encString(serviceProvider));
//				Long serviceProviderId = spInfo.getServiceProviderId();
//				EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
//						.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
//								ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
//								serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
//								serviceProviderId, '0'));
//
//				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
//				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.PAN_CARD_INVALID);
//				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
//			}

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
	public synchronized Map<String, Object> saveDataForPanCompliance(@Valid String panNumber, long merchantId,
			Double merchantWallet, String businessName, String email) {
		Map<String, Object> map = new HashedMap<>();
		try {
			semaphore.acquire();
			lock.lock();
			String serviceName = "PAN Card Compliance Details";

			LOGGER.info("Inside Pan Commpliance ");

			String pcOptionName = "Wallet";
			String amount = "1";

			double amtInDouble = Double.parseDouble(amount);

			if (panNumber.isEmpty() || panNumber.length() < 6 || panNumber.length() > 15
					|| !panNumber.matches("^[a-zA-Z\\d-]+$")) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

				if (panNumber.isEmpty()) {
					map.put(ResponseMessage.DESCRIPTION, "panNumber can't be empty.");
				} else if (panNumber.length() < 6 || panNumber.length() > 15) {
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
					.save(new EkycRequest(merchantId, panNumber, panNumber, trxnDate));
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

			if (Validations.isValidPanCardNo(panNumber)) {

				// response = panService.compliance(panNumber);

				map = signzyService.panCompliance(panNumber);

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
//				LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'VALIDATE PAN SIGNZY' EKYC TRXN DETAILS : {}", elk);
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
//				LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'INVALIDATE PAN SIGNZY' EKYC TRXN DETAILS : {}", elk);

				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.PAN_CARD_INVALID);

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
	public synchronized Map<String, Object> saveDataForBasicPanCompliance(@Valid String panNumber, long merchantId,
			Double merchantWallet, String businessName, String email) {
		Map<String, Object> map = new HashedMap<>();
		try {
			semaphore.acquire();
			lock.lock();
			String serviceName = "PAN Card Basic Compliance";

			LOGGER.info("  Inside  Pan Commpliance ");

			String pcOptionName = "Wallet";
			String amount = "1";

			double amtInDouble = Double.parseDouble(amount);

			if (panNumber.isEmpty() || panNumber.length() < 6 || panNumber.length() > 15
					|| !panNumber.matches("^[a-zA-Z\\d-]+$")) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

				if (panNumber.isEmpty()) {
					map.put(ResponseMessage.DESCRIPTION, "panNumber can't be empty.");
				} else if (panNumber.length() < 6 || panNumber.length() > 15) {
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
					.save(new EkycRequest(merchantId, panNumber, panNumber, trxnDate));
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

			if (Validations.isValidPanCardNo(panNumber)) {
				// response = panService.fetchCompliance(panNumber);

				map = signzyService.fetchCompliance(panNumber);

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
//				LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'SIGNZY VALIDATE PAN SIGNZY' EKYC TRXN DETAILS : {}",
//						elk);
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
//				LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'SIGNZY InVALIDATE PAN SIGNZY' EKYC TRXN DETAILS : {}",
//						elk);
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.PAN_CARD_INVALID);
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
	public synchronized Map<String, Object> saveDataForPanAdhar(PanAadharRequest panAadharRequest, long merchantId,
			Double merchantWallet, String businessName, String email) {
		Map<String, Object> map = new HashedMap<>();
		try {
			semaphore.acquire();
			lock.lock();
			String panNumber = panAadharRequest.getPanNumber();
			String adharNumber = panAadharRequest.getuId();
			String serviceName = "PAN Aadhar Link";

			LOGGER.info("Inside Pan Adhar");

			String pcOptionName = "Wallet";
			String amount = "1";
			double amtInDouble = Double.parseDouble(amount);

			if (panNumber.isEmpty() || panNumber.length() < 6 || panNumber.length() > 15
					|| !panNumber.matches("^[a-zA-Z\\d-]+$")) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

				if (panNumber.isEmpty()) {
					map.put(ResponseMessage.DESCRIPTION, "panNumber can't be empty.");
				} else if (panNumber.length() < 6 || panNumber.length() > 15) {
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
					.save(new EkycRequest(merchantId, walletTxnRefNo, walletTxnRefNo, trxnDate));
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

			if (Validations.isValidPanCardNo(panNumber)) {

				if (Validations.isValidAdharNumber(adharNumber)) {
					String serviceProvider = ResponseMessage.SIGNZY;
					ServiceProviders spInfo = serviceprovidersrepository
							.findBySpName(Encryption.encString(serviceProvider));
					Long serviceProviderId = spInfo.getServiceProviderId();
					EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
							.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
									ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
									serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
									serviceProviderId, '0'));

//					EkycTransactionDetails elk = ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
//					LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'SIGNZY VALIDATE PAN SIGNZY' EKYC TRXN DETAILS : {}",
//							elk);

					// response = panService.panAadharLinkStatus(adharNumber, panNumber);

					map = signzyService.panAadharLinkStatus(adharNumber, panNumber);

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

//					EkycTransactionDetails elk = ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
//					LOGGER.info(
//							"ELASTICSEARCH DATA INSERTION FOR 'SIGNZY INVALIDATE PAN SIGNZY' EKYC TRXN DETAILS : {}",
//							elk);
					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.DESCRIPTION, ResponseMessage.AADHAR_NUMBER_INVALID);
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				}

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
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.PAN_CARD_INVALID);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			}

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

	@Override
	public synchronized Map<String, Object> checkPanAadharLinkStatus(@Valid String panNumber, long merchantId,
			Double merchantFloatAmount, String businessName, String email) {
		Map<String, Object> map = new HashedMap<>();
		try {
			semaphore.acquire();
			lock.lock();
			String serviceName = "PAN Aadhar Link";
			double amtInDouble = 1.0;

			if (panNumber.isEmpty() || panNumber.length() < 6 || panNumber.length() > 15
					|| !panNumber.matches("^[a-zA-Z\\d-]+$")) {

				List<String> responseList = new ArrayList<>();
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

				if (panNumber.isEmpty()) {
					responseList.add("panNumber can't be empty.");
				} else if (panNumber.length() < 6 || panNumber.length() > 15) {
					responseList.add("panNumber must be between 6 to 15.");
				} else {
					responseList.add("Invalid PAN.");
				}
				map.put(ResponseMessage.DESCRIPTION, responseList);
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
//			LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'AADHAAR LINK STATUS' EKYC TRXN DETAILS : {}", elk);

			map = karzaService.checkPanAadharLinkStatusAPI(panNumber);

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

	@Override
	public synchronized Map<String, Object> checkPanStatus(@Valid String panNumber, long merchantId,
			Double merchantFloatAmount, String businessName, String email) {
		Map<String, Object> map = new HashedMap<>();
		try {
			semaphore.acquire();
			lock.lock();
			String serviceName = "OCR EKYC";
			double amtInDouble = 1.0;

			if (panNumber.isEmpty() || panNumber.length() < 6 || panNumber.length() > 15
					|| !panNumber.matches("^[a-zA-Z\\d-]+$")) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

				if (panNumber.isEmpty()) {
					map.put(ResponseMessage.DESCRIPTION, "panNumber can't be empty.");
				} else if (panNumber.length() < 6 || panNumber.length() > 15) {
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
//			LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'PAN STATUS' EKYC TRXN DETAILS : {}", elk);

			map = karzaService.checkPanStatus(panNumber);

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
	public synchronized Map<String, Object> panProfileDetails(@Valid String panNumber, long merchantId,
			Double merchantFloatAmount, String businessName, String email) {
		Map<String, Object> map = new HashedMap<>();
		try {
			semaphore.acquire();
			lock.lock();
			String serviceName = "PAN Card Basic Verify";
			double amtInDouble = 3.0;

			panNumber = panNumber.toUpperCase();

			if (panNumber.isEmpty() || panNumber.length() < 6 || panNumber.length() > 15
					|| !panNumber.matches("^[a-zA-Z\\d-]+$")) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

				if (panNumber.isEmpty()) {
					map.put(ResponseMessage.DESCRIPTION, "panNumber can't be empty.");
				} else if (panNumber.length() < 6 || panNumber.length() > 15) {
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
//			LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'PAN PROFILE DETAILS KARZA' EKYC TRXN DETAILS : {}", elk);

			map = karzaService.panProfileDetailsAPI(panNumber, merchantTrxnRefId);

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
	public synchronized Map<String, Object> panDetails(@Valid String panNumber, long merchantId,
			Double merchantFloatAmount, String businessName, String email) {
		Map<String, Object> map = new HashedMap<>();
		try {
			semaphore.acquire();
			lock.lock();
			String serviceName = "PAN Card Basic Verify";
			double amtInDouble = 20.0;

			panNumber = panNumber.toUpperCase();

			if (panNumber.isEmpty() || panNumber.length() < 6 || panNumber.length() > 15
					|| !panNumber.matches("^[a-zA-Z\\d-]+$")) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

				if (panNumber.isEmpty()) {
					map.put(ResponseMessage.DESCRIPTION, "panNumber can't be empty.");
				} else if (panNumber.length() < 6 || panNumber.length() > 15) {
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
//			LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'PAN PROFILE DETAILS KARZA' EKYC TRXN DETAILS : {}", elk);

			map = karzaService.panDetailsAPI(panNumber, merchantTrxnRefId);

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
	public synchronized Map<String, Object> validatePan(@Valid String panNumber, long merchantId,
			Double merchantFloatAmount, String businessName, String email) {
		Map<String, Object> map = new HashedMap<>();
		try {
			semaphore.acquire();
			lock.lock();
			String serviceName = "PAN Card Basic Verify";
			double amtInDouble = 3.0;
			panNumber = panNumber.toUpperCase();

			if (panNumber.isEmpty() || panNumber.length() < 6 || panNumber.length() > 15
					|| !panNumber.matches("^[a-zA-Z\\d-]+$")) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

				if (panNumber.isEmpty()) {
					map.put(ResponseMessage.DESCRIPTION, "panNumber can't be empty.");
				} else if (panNumber.length() < 6 || panNumber.length() > 15) {
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

			String serviceProvider = ResponseMessage.DECENTRO;
			ServiceProviders spInfo = serviceprovidersrepository.findBySpName(Encryption.encString(serviceProvider));
			Long serviceProviderId = spInfo.getServiceProviderId();

			EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
					.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
							ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
							serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
							serviceProviderId, '0'));

//			EkycTransactionDetails elk = ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
//			LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'VALIDATE PAN DECENTRO' EKYC TRXN DETAILS : {}", elk);

			map = decentroServiceImpl.validatePan(panNumber, merchantTrxnRefId);

		} catch (Exception e) {
			LOGGER.error("Exception in validatePan: {}", e.getLocalizedMessage(), e);
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		} finally {
			lock.unlock();
			semaphore.release();
		}
		return map;
	}
}
