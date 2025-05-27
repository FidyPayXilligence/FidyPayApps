package com.fidypay.service.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.validation.Valid;

import org.apache.commons.collections4.map.HashedMap;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
import com.fidypay.request.FetchPassportRequest;
import com.fidypay.request.VerifyPassportRequest;
import com.fidypay.request.VerifyPassportRequestV2;
import com.fidypay.service.PassportService;
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
public class PassportServiceImpl implements PassportService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PassportServiceImpl.class);

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
	private KarzaService karzaService;

	@Autowired
	private EKYCService ekycService;

	@Autowired
	private WalletService walletService;

//	@Autowired
//	private EkycTransactionDetailsSearchRepository ekycTransactionDetailsSearchRepository;

	private final Semaphore semaphore = new Semaphore(1);
	private final Lock lock = new ReentrantLock();

	public Map<String, Object> identityCardObjectForPassport() throws Exception {

		Map<String, Object> map = new HashMap<String, Object>();

		String responseLogin = new GSTServiceImpl().login(GSTServiceImpl.USERNAME, GSTServiceImpl.PASSWORD);
		LOGGER.info("Login Response {}", responseLogin);
		JSONObject jsonObject = new JSONObject(responseLogin);
		String id = jsonObject.getString("id");
		String userId = jsonObject.getString("userId");

		String requestStr = "{\r\n" + "              \"type\": \"passport\",\r\n"
				+ "              \"email\": \"admin@signzy.com\",\r\n"
				+ "              \"callbackUrl\": \"https://prebuild.com/system\",\r\n"
				+ "              \"images\": [\r\n"
				+ "                \"https://images.unsplash.com/photo-1453728013993-6d66e9c9123a?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8dmlld3xlbnwwfHwwfHw%3D&w=1000&q=80\"\r\n"
				+ "              ]\r\n" + "            }";

		OkHttpClient client = new OkHttpClient().newBuilder().build();
		MediaType mediaType = MediaType.parse("application/json");
		RequestBody body = RequestBody.create(mediaType, requestStr);
		Request request = new Request.Builder().url(GSTServiceImpl.BASE_URL + userId + "/identities")
				.method("POST", body).addHeader("Authorization", id).addHeader("Content-Type", "application/json")
				.build();
		Response response = client.newCall(request).execute();
		String finalResponse = response.body().string();
		LOGGER.info("Identity Response {}", finalResponse);
		JSONObject jsonObjectIdentity = new JSONObject(finalResponse);
		String idIdentity = jsonObjectIdentity.getString("id");
		String accessTokenIdentity = jsonObjectIdentity.getString("accessToken");

		LOGGER.info("Inside Identity idIdentity {}", idIdentity);
		LOGGER.info("Inside Identity authorization {}", accessTokenIdentity);

		map.put("accessToken", accessTokenIdentity);
		map.put("authorization", id);
		map.put("idIdentity", idIdentity);
		return map;
	}

//---------------------------------	

	@Override
	public synchronized Map<String, Object> saveDataForPassportFetch(@Valid FetchPassportRequest fetchPassportRequest,
			long merchantId, Double merchantWallet, String businessName, String email) {
		Map<String, Object> map = new HashedMap<>();
		try {
			semaphore.acquire();
			lock.lock();
			String fileNumber = fetchPassportRequest.getFileNumber();
			String dob = fetchPassportRequest.getDob();
			String serviceName = "Passport Details";
			LOGGER.info("Inside PassPort Details");

			String pcOptionName = "Wallet";
			String amount = "3.00";
			double amtInDouble = Double.parseDouble(amount);

			if (!fileNumber.matches("^[a-zA-Z\\d-]+$")) {

				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Invalid fileNumber.");
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

			if (DateAndTime.isValidDateFormat(fetchPassportRequest.getDob()) == false) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.INVALID_DATE_FORMATE);
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

			String resWallet = walletNotification.checkWalletBalance(merchantWallet, businessName, email);

			if (merchantWallet < charges) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}

			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			EkycRequest ekycRequest = ekycRequestRepository
					.save(new EkycRequest(merchantId, fileNumber, walletTxnRefNo, trxnDate));
			long ekycRequestId = ekycRequest.getRequestId();

//			Bank bank = new Bank();
//			ThreadWithdrawal test = new ThreadWithdrawal(bank, merchantTrxnRefId, charges, merchantWallet, merchantId,
//					merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId, serviceName, trxnRefId,
//					walletTxnRefNo, serviceName);
//			LOGGER.info("Name of Thread ---------- {}", test.getName());
//			test.start();

			WalletRequest debitRequest = new WalletRequest(merchantId, charges, "Debit", serviceName, serviceName,
					ekycRequestId, trxnRefId, merchantTrxnRefId, walletTxnRefNo);
			walletService.enqueueTransaction(debitRequest);

			Date date1 = new SimpleDateFormat("dd-MM-yyyy").parse(fetchPassportRequest.getDob());

			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			dob = formatter.format(date1);
			LOGGER.info("dob: {}", dob);

			if (Validations.validateJavaDate(dob)) {

				// response = passportService.fetchPassport(fetchPassportRequest);

				map = signzyService.fetchPassport(fetchPassportRequest);

				String serviceProvider = ResponseMessage.SIGNZY;
				ServiceProviders spInfo = serviceprovidersrepository
						.findBySpName(Encryption.encString(serviceProvider));
				Long serviceProviderId = spInfo.getServiceProviderId();

				EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
						.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
								ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
								serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
								serviceProviderId, '0'));
				LOGGER.info("dob: {}", ekycTransactionDetails.getEkycTransactionId());

//				EkycTransactionDetails elk = ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
//				LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'VALID FETCH PASSPORT' EKYC TRXN DETAILS : {}", elk);
			}

			else {
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
//				LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'INVALID  FETCH PASSPORT' EKYC TRXN DETAILS : {}", elk);

				LOGGER.info("dob: {}", ekycTransactionDetails.getEkycTransactionId());
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATE_FORMATE_INVALID);
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
	public synchronized Map<String, Object> saveDataForPassportFetchV2(@Valid FetchPassportRequest fetchPassportRequest,
			long merchantId, Double merchantWallet, String serviceName, String businessName, String email) {
		Map<String, Object> map = new HashedMap<>();
		try {
			semaphore.acquire();
			lock.lock();
			String fileNumber = fetchPassportRequest.getFileNumber();
			String dob = fetchPassportRequest.getDob();

			LOGGER.info("Inside PassPort Details");

			String pcOptionName = "Wallet";
			String amount = "3.00";
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

			String resWallet = walletNotification.checkWalletBalance(merchantWallet, businessName, email);

			if (merchantWallet < charges) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}

			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			EkycRequest ekycRequest = ekycRequestRepository
					.save(new EkycRequest(merchantId, fileNumber, walletTxnRefNo, trxnDate));
			long ekycRequestId = ekycRequest.getRequestId();

//			Bank bank = new Bank();
//			ThreadWithdrawal test = new ThreadWithdrawal(bank, merchantTrxnRefId, charges, merchantWallet, merchantId,
//					merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId, serviceName, trxnRefId,
//					walletTxnRefNo, serviceName);
//			LOGGER.info("Name of Thread -----------------------{}", test.getName());
//			test.start();

			WalletRequest debitRequest = new WalletRequest(merchantId, charges, "Debit", serviceName, serviceName,
					ekycRequestId, trxnRefId, merchantTrxnRefId, walletTxnRefNo);
			walletService.enqueueTransaction(debitRequest);

			Date date1 = new SimpleDateFormat("dd-MM-yyyy").parse(fetchPassportRequest.getDob());

			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			dob = formatter.format(date1);
			LOGGER.info("dob: {}", dob);

			if (Validations.validateJavaDate(dob)) {

				map = karzaService.fetchPassportV2(fetchPassportRequest.getFileNumber(), dob);

				String serviceProvider = ResponseMessage.KARZA;
				ServiceProviders spInfo = serviceprovidersrepository
						.findBySpName(Encryption.encString(serviceProvider));
				Long serviceProviderId = spInfo.getServiceProviderId();
				EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
						.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
								ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
								serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
								serviceProviderId, '0'));
				LOGGER.info("EkycTransactionId: {}", ekycTransactionDetails.getEkycTransactionId());

//				EkycTransactionDetails elk = ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
//				LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'IF PASSPORT V2' EKYC TRXN DETAILS : {}", elk);
			}

			else {
				String serviceProvider = "Karza Tech";
				ServiceProviders spInfo = serviceprovidersrepository
						.findBySpName(Encryption.encString(serviceProvider));
				Long serviceProviderId = spInfo.getServiceProviderId();
				EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
						.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
								ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
								serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
								serviceProviderId, '0'));
				LOGGER.info("EkycTransactionId: {}", ekycTransactionDetails.getEkycTransactionId());

//				EkycTransactionDetails elk = ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
//				LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'ELSE PASSPORT V2' EKYC TRXN DETAILS : {}", elk);
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATE_FORMATE_INVALID);
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
	public synchronized Map<String, Object> saveDataForVerifyPassport(VerifyPassportRequest verifyPassportRequest,
			long merchantId, Double merchantWallet, String serviceName, String businessName, String email) {
		Map<String, Object> map = new HashedMap<>();
		try {
			semaphore.acquire();
			lock.lock();
			String fileNumber = verifyPassportRequest.getFileNumber();
			String dob = verifyPassportRequest.getDob();

			LOGGER.info("Inside PassPort verify");

			String pcOptionName = "Wallet";
			String amount = "3.00";
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

			String resWallet = walletNotification.checkWalletBalance(merchantWallet, businessName, email);

			if (merchantWallet < charges) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}

			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			EkycRequest ekycRequest = ekycRequestRepository
					.save(new EkycRequest(merchantId, fileNumber, walletTxnRefNo, trxnDate));
			long ekycRequestId = ekycRequest.getRequestId();

//			Bank bank = new Bank();
//			ThreadWithdrawal test = new ThreadWithdrawal(bank, merchantTrxnRefId, charges, merchantWallet, merchantId,
//					merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId, serviceName, trxnRefId,
//					walletTxnRefNo, serviceName);
//			LOGGER.info("Name of Thread ------------: {}", test.getName());
//			test.start();

			WalletRequest debitRequest = new WalletRequest(merchantId, charges, "Debit", serviceName, serviceName,
					ekycRequestId, trxnRefId, merchantTrxnRefId, walletTxnRefNo);
			walletService.enqueueTransaction(debitRequest);

			Date date1 = new SimpleDateFormat("dd-MM-yyyy").parse(verifyPassportRequest.getDob());

			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			dob = formatter.format(date1);
			LOGGER.info("dob: {}", dob);

			if (Validations.validateJavaDate(dob)) {

				map = signzyService.verifyPassport(verifyPassportRequest);

				String serviceProvider = ResponseMessage.SIGNZY;
				ServiceProviders spInfo = serviceprovidersrepository
						.findBySpName(Encryption.encString(serviceProvider));
				Long serviceProviderId = spInfo.getServiceProviderId();
				EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
						.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
								ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
								serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
								serviceProviderId, '0'));

				LOGGER.info("EkycTransactionId: {}", ekycTransactionDetails.getEkycTransactionId());

//				EkycTransactionDetails elk = ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
//				LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'IF VERIFY PASSPORT' EKYC TRXN DETAILS : {}", elk);

			} else {
				String serviceProvider = ResponseMessage.SIGNZY;
				ServiceProviders spInfo = serviceprovidersrepository
						.findBySpName(Encryption.encString(serviceProvider));
				Long serviceProviderId = spInfo.getServiceProviderId();
				EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
						.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
								ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
								serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
								serviceProviderId, '0'));
				LOGGER.info("EkycTransactionId: {}", ekycTransactionDetails.getEkycTransactionId());

//				EkycTransactionDetails elk = ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
//				LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'ELSE VERIFY PASSPORT' EKYC TRXN DETAILS : {}", elk);

				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATE_FORMATE_INVALID);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
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
	public synchronized Map<String, Object> saveDataForVerifyPassportV2(VerifyPassportRequestV2 verifyPassportRequestV2,
			long merchantId, Double merchantWallet, String serviceName, String businessName, String email) {
		Map<String, Object> map = new HashedMap<>();
		try {
			semaphore.acquire();
			lock.lock();
			String passportNumber = verifyPassportRequestV2.getPassportNumber();
			String dob = verifyPassportRequestV2.getDob();

			LOGGER.info("Inside PassPort verify");

			String pcOptionName = "Wallet";
			String amount = "3.00";
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

			String resWallet = walletNotification.checkWalletBalance(merchantWallet, businessName, email);

			if (merchantWallet < charges) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}
			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			EkycRequest ekycRequest = ekycRequestRepository
					.save(new EkycRequest(merchantId, passportNumber, walletTxnRefNo, trxnDate));
			long ekycRequestId = ekycRequest.getRequestId();

//			Bank bank = new Bank();
//			ThreadWithdrawal test = new ThreadWithdrawal(bank, merchantTrxnRefId, charges, merchantWallet, merchantId,
//					merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId, serviceName, trxnRefId,
//					walletTxnRefNo, serviceName);
//			LOGGER.info("Name of Thread ------{}", test.getName());
//			test.start();

			WalletRequest debitRequest = new WalletRequest(merchantId, charges, "Debit", serviceName, serviceName,
					ekycRequestId, trxnRefId, merchantTrxnRefId, walletTxnRefNo);
			walletService.enqueueTransaction(debitRequest);

			Date date1 = new SimpleDateFormat("dd-MM-yyyy").parse(verifyPassportRequestV2.getDob());

			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			dob = formatter.format(date1);
			LOGGER.info("dob: {}", dob);

			if (Validations.validateJavaDate(dob)) {

				map = karzaService.verifyPassportV2(verifyPassportRequestV2.getPassportNumber(), dob,
						verifyPassportRequestV2.getName());

				String serviceProvider = ResponseMessage.KARZA;
				ServiceProviders spInfo = serviceprovidersrepository
						.findBySpName(Encryption.encString(serviceProvider));
				Long serviceProviderId = spInfo.getServiceProviderId();
				EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
						.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
								ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
								serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
								serviceProviderId, '0'));

//				EkycTransactionDetails elk = ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
//				LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'IF VERIFY PASSPORT V@' EKYC TRXN DETAILS : {}", elk);

				LOGGER.info("EkycTransactionId: {}", ekycTransactionDetails.getEkycTransactionId());
			} else {
				String serviceProvider = ResponseMessage.KARZA;
				ServiceProviders spInfo = serviceprovidersrepository
						.findBySpName(Encryption.encString(serviceProvider));
				Long serviceProviderId = spInfo.getServiceProviderId();
				@SuppressWarnings("unused")
				EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
						.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
								ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
								serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
								serviceProviderId, '0'));

//				EkycTransactionDetails elk = ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
//				LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'ELSE VERIFY PASSPORT V@' EKYC TRXN DETAILS : {}", elk);

				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATE_FORMATE_INVALID);
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
}
