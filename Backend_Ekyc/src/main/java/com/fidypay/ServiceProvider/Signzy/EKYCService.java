package com.fidypay.ServiceProvider.Signzy;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.validation.Valid;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fidypay.ServiceProvider.Decentro.DecentroUtils;
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
import com.fidypay.request.CibilScoreOtpRequest;
import com.fidypay.request.CibilScoreValidateRequest;
import com.fidypay.request.FetchPassportRequest;
import com.fidypay.request.GenerateOtpRequest;
import com.fidypay.request.Validate;
import com.fidypay.request.ValidateOtp;
import com.fidypay.request.VerifyPassportRequestV2;
import com.fidypay.request.VoterIdRequest;
import com.fidypay.request.VoterIdRequestV2;
import com.fidypay.service.GSTService;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.DateAndTime;
import com.fidypay.utils.ex.GenerateTrxnRefId;
import com.fidypay.utils.ex.RandomNumberGenrator;
import com.fidypay.utils.ex.Validations;
import com.fidypay.wallet.WalletNotification;
import com.fidypay.wallet.WalletRequest;
import com.fidypay.wallet.WalletService;
import com.google.gson.Gson;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class EKYCService {

	private static final Logger LOGGER = LoggerFactory.getLogger(EKYCService.class);

	@Autowired
	private WalletNotification walletNotification;

	@Autowired
	private MerchantsRepository merchantsRepository;

	@Autowired
	private MerchantWalletTransactionsRepository merchantWalletTransactionsRepository;

	@Autowired
	private EkycRequestRepository ekycRequestRepository;

	@Autowired
	private EkycTransactionDetailsRepository ekycTransactionDetailsRepository;

	@Autowired
	private GSTService gstService;

	@Autowired
	private ServiceInfoRepository serviceInfoRepository;

	@Autowired
	private MerchantServiceRepository merchantServiceRepository;

	@Autowired
	private MerchantServiceChargeService chargeService;

	@Autowired
	private Validations validations;

	@Autowired
	private ServiceProvidersRepository serviceprovidersrepository;

	@Autowired
	private WalletService walletService;

//	@Autowired
//	private EkycTransactionDetailsSearchRepository ekycTransactionDetailsSearchRepository;

	private final Semaphore semaphore = new Semaphore(1);
	private final Lock lock = new ReentrantLock();

//	private final String GENERATE_OTP_URL = "https://in.staging.decentro.tech/v2/kyc/aadhaar/otp";
//	private final String VALIDATE_OTP_URL = "https://in.staging.decentro.tech/v2/kyc/aadhaar_connect/otp/validate";
//	private final String CLIENT_ID = "fidypay_staging";
//	private final String CLIENT_SECRET = "dRr5IqBh5L4wQ2GYmst5iAxYLcWCLXEN";
//	private final String MODULE_SECRET = "OLD006mfTYAF4Zgco4rZkL0SkIPc6vtN";
//	private final String VALIDATE_URL = "https://in.staging.decentro.tech/kyc/public_registry/validate";

	private static final String USER_NAME = "fidypay_test";
	private static final String PASSWORD = "6zvWtnSar8dTjPDwr8dv";
	private static final String SIGNZY_ID = "SIGNZY2019098";
	private static final String LOGIN_URL = "https://preproduction.signzy.tech/api/v2/patrons/login";
	private static final String CIBIL_SCORE_VALIDATE_URL = "http://regtechapi.in/api/equifax";
	private static final String CIBIL_SCORE_OTP_URL = "http://regtechapi.in/api/equifaxsendOTP";

	public String saveDataForVerify(String accountNumber, long merchantId, double merchantWallet) throws Exception {
		String response = null;
		JSONObject jsonObj = new JSONObject();

		try {
			String serviceName = "Aadhar Verify";

			LOGGER.info("  Inside saveAdharData  ");

			String pcOptionName = "Wallet";
			String amount = "1";
			double amtInDouble = Double.parseDouble(amount);
			if (merchantWallet < amtInDouble) {

				jsonObj.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
				jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

				return jsonObj.toString();
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

			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			EkycRequest ekycRequest = ekycRequestRepository
					.save(new EkycRequest(merchantId, accountNumber, accountNumber, trxnDate));
			long ekycRequestId = ekycRequest.getRequestId();

//			Bank bank = new Bank();
//			ThreadWithdrawal test = new ThreadWithdrawal(bank, merchantTrxnRefId, amtInDouble, merchantWallet,
//					merchantId, merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId, serviceName,
//					trxnRefId, walletTxnRefNo, serviceName);
//			LOGGER.info("Name of Thread ----------------------- " + test.getName());
//			test.start();

			WalletRequest debitRequest = new WalletRequest(merchantId, charges, "Debit", serviceName, serviceName,
					ekycRequestId, trxnRefId, merchantTrxnRefId, walletTxnRefNo);
			walletService.enqueueTransaction(debitRequest);

			// response = aadhaarService.verifyAadhaar(accountNumber);

			String serviceProvider = "Signzy";
			ServiceProviders spInfo = serviceprovidersrepository.findBySpName(Encryption.encString(serviceProvider));
			Long serviceProviderId = spInfo.getServiceProviderId();

			EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
					.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
							ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
							serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
							serviceProviderId, '0'));

		} catch (Exception e) {
			jsonObj.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response = jsonObj.toString();
		}
		return response;
	}

	public String saveDataForBasicVerify(String accountNumber, long merchantId, double merchantWallet)
			throws Exception {
		String response = null;
		JSONObject jsonObj = new JSONObject();

		try {
			String serviceName = "Aadhar Basic Verify";

			LOGGER.info("  Inside Basic saveAdharData  ");

			String pcOptionName = "Wallet";
			String amount = "1";
			double amtInDouble = Double.parseDouble(amount);

			if (merchantWallet < amtInDouble) {

				jsonObj.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
				jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

				return jsonObj.toString();
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

			if (merchantWallet < amtInDouble) {

				jsonObj.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
				jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

				return jsonObj.toString();
			}

			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			EkycRequest ekycRequest = ekycRequestRepository
					.save(new EkycRequest(merchantId, accountNumber, accountNumber, trxnDate));
			long ekycRequestId = ekycRequest.getRequestId();

//			Bank bank = new Bank();
//			ThreadWithdrawal test = new ThreadWithdrawal(bank, merchantTrxnRefId, amtInDouble, merchantWallet,
//					merchantId, merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId, serviceName,
//					trxnRefId, walletTxnRefNo, serviceName);
//			LOGGER.info("Name of Thread ----------------------- " + test.getName());
//			test.start();

			WalletRequest debitRequest = new WalletRequest(merchantId, charges, "Debit", serviceName, serviceName,
					ekycRequestId, trxnRefId, merchantTrxnRefId, walletTxnRefNo);
			walletService.enqueueTransaction(debitRequest);

			if (validations.isValidAdharNumber(accountNumber)) {
				// response = aadhaarService.basicVerifyAadhaar(accountNumber);

				String serviceProvider = "Signzy";
				ServiceProviders spInfo = serviceprovidersrepository
						.findBySpName(Encryption.encString(serviceProvider));
				Long serviceProviderId = spInfo.getServiceProviderId();
				EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
						.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
								ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
								serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
								serviceProviderId, '0'));
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

				jsonObj.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.AADHAR_NUMBER_INVALID);
				jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				response = jsonObj.toString();
			}

		} catch (Exception e) {
			jsonObj.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response = jsonObj.toString();
		}
		return response;
	}

	public String saveDataForGSTINSearch(String accountNumber, long merchantId, double merchantWallet)
			throws Exception {
		String response = null;
		JSONObject jsonObj = new JSONObject();

		try {
			String serviceName = "GST Number";

			LOGGER.info("  Inside GSTINSearch  ");

			String pcOptionName = "Wallet";
			String amount = "0.15";
			double amtInDouble = Double.parseDouble(amount);

			if (merchantWallet < amtInDouble) {
				jsonObj.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
				return jsonObj.toString();
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

			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			EkycRequest ekycRequest = ekycRequestRepository
					.save(new EkycRequest(merchantId, accountNumber, accountNumber, trxnDate));
			long ekycRequestId = ekycRequest.getRequestId();

//			Bank bank = new Bank();
//			ThreadWithdrawal test = new ThreadWithdrawal(bank, merchantTrxnRefId, amtInDouble, merchantWallet,
//					merchantId, merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId, serviceName,
//					trxnRefId, walletTxnRefNo, serviceName);
//			LOGGER.info("Name of Thread ----------------------- " + test.getName());
//			test.start();

			WalletRequest debitRequest = new WalletRequest(merchantId, charges, "Debit", serviceName, serviceName,
					ekycRequestId, trxnRefId, merchantTrxnRefId, walletTxnRefNo);
			walletService.enqueueTransaction(debitRequest);

			if (validations.isValidGSTNo(accountNumber)) {

				response = gstService.GSTINDetailsSearch(accountNumber);
				String serviceProvider = "Signzy";
				ServiceProviders spInfo = serviceprovidersrepository
						.findBySpName(Encryption.encString(serviceProvider));
				Long serviceProviderId = spInfo.getServiceProviderId();
				EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
						.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
								ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
								serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
								serviceProviderId, '0'));

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

				jsonObj.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.GST_NUMBER_INVALID);
				jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				response = jsonObj.toString();
			}
		} catch (Exception e) {
			jsonObj.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response = jsonObj.toString();
		}
		return response;
	}

	public String saveDataForGSTDetailSearch(String accountNumber, long merchantId, double merchantWallet)
			throws Exception {
		String response = null;
		JSONObject jsonObj = new JSONObject();

		try {
			String serviceName = "GST Number V2";

			LOGGER.info("  Inside GSTINSearch  ");

			String pcOptionName = "Wallet";
			String amount = "0.15";

			double amtInDouble = Double.parseDouble(amount);

			if (merchantWallet < amtInDouble) {

				jsonObj.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
				jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

				return jsonObj.toString();
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

			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			EkycRequest ekycRequest = ekycRequestRepository
					.save(new EkycRequest(merchantId, accountNumber, accountNumber, trxnDate));
			long ekycRequestId = ekycRequest.getRequestId();

//			Bank bank = new Bank();
//			ThreadWithdrawal test = new ThreadWithdrawal(bank, merchantTrxnRefId, amtInDouble, merchantWallet,
//					merchantId, merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId, serviceName,
//					trxnRefId, walletTxnRefNo, serviceName);
//			LOGGER.info("Name of Thread ----------------------- " + test.getName());
//			test.start();

			WalletRequest debitRequest = new WalletRequest(merchantId, charges, "Debit", serviceName, serviceName,
					ekycRequestId, trxnRefId, merchantTrxnRefId, walletTxnRefNo);
			walletService.enqueueTransaction(debitRequest);

			if (validations.isValidGSTNo(accountNumber)) {

				response = gstService.GSTINDetailsSearch(accountNumber);
				String serviceProvider = "Signzy";
				ServiceProviders spInfo = serviceprovidersrepository
						.findBySpName(Encryption.encString(serviceProvider));
				Long serviceProviderId = spInfo.getServiceProviderId();
				EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
						.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
								ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
								serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
								serviceProviderId, '0'));

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

				jsonObj.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.GST_NUMBER_INVALID);
				jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				response = jsonObj.toString();
			}

		} catch (Exception e) {
			jsonObj.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response = jsonObj.toString();
		}
		return response;
	}

	public String saveDataForGSTSearchCompanyName(String accountNumber, long merchantId, double merchantWallet)
			throws Exception {
		String response = null;
		JSONObject jsonObj = new JSONObject();

		try {
			String serviceName = "GST Company Name";

			LOGGER.info("  Inside GSTINSearch  ");

			String pcOptionName = "Wallet";
			String amount = "0.15";

			double amtInDouble = Double.parseDouble(amount);

			if (merchantWallet < amtInDouble) {

				jsonObj.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
				jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

				return jsonObj.toString();
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

			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			EkycRequest ekycRequest = ekycRequestRepository
					.save(new EkycRequest(merchantId, accountNumber, accountNumber, trxnDate));
			long ekycRequestId = ekycRequest.getRequestId();

//			Bank bank = new Bank();
//			ThreadWithdrawal test = new ThreadWithdrawal(bank, merchantTrxnRefId, amtInDouble, merchantWallet,
//					merchantId, merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId, serviceName,
//					trxnRefId, walletTxnRefNo, serviceName);
//			LOGGER.info("Name of Thread ----------------------- " + test.getName());
//			test.start();

			WalletRequest debitRequest = new WalletRequest(merchantId, charges, "Debit", serviceName, serviceName,
					ekycRequestId, trxnRefId, merchantTrxnRefId, walletTxnRefNo);
			walletService.enqueueTransaction(debitRequest);

			// if (validations.isValidGSTNo(accountNumber)) {

			response = gstService.GSTSearchCompanyName(accountNumber);
			String serviceProvider = "Signzy";
			ServiceProviders spInfo = serviceprovidersrepository.findBySpName(Encryption.encString(serviceProvider));
			Long serviceProviderId = spInfo.getServiceProviderId();
			EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
					.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
							ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
							serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
							serviceProviderId, '0'));

		} catch (Exception e) {
			jsonObj.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response = jsonObj.toString();
		}
		return response;
	}

	public String saveDataForVoter(VoterIdRequest voterIdRequest, long merchantId, double merchantWallet,
			String accountNumber) throws Exception {
		String response = null;
		JSONObject jsonObj = new JSONObject();

		try {
			String serviceName = "Voter Details";
			String voterId = voterIdRequest.getEpicNumber();
			String name = voterIdRequest.getVoterName();
			String state = voterIdRequest.getState();
			LOGGER.info("Inside Voter Details");

			String pcOptionName = "Wallet";
			String amount = "1";
			double amtInDouble = Double.parseDouble(amount);
			if (merchantWallet < amtInDouble) {

				jsonObj.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
				jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

				return jsonObj.toString();
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

			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			EkycRequest ekycRequest = ekycRequestRepository
					.save(new EkycRequest(merchantId, accountNumber, walletTxnRefNo, trxnDate));
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

			if (Validations.isValidEPICNumber(voterId)) {

				// response = voterIdService.verifyVoter(voterId, name, state);
				String serviceProvider = "Signzy";
				ServiceProviders spInfo = serviceprovidersrepository
						.findBySpName(Encryption.encString(serviceProvider));
				Long serviceProviderId = spInfo.getServiceProviderId();
				EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
						.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
								ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
								serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
								serviceProviderId, '0'));
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
				jsonObj.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.VOTER_ID_INVALID);
				jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				response = jsonObj.toString();
			}

		} catch (Exception e) {
			jsonObj.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response = jsonObj.toString();
		}
		return response;
	}

	public String saveDataForGetURL(long merchantId, double merchantWallet) throws Exception {
		String response = null;
		JSONObject jsonObj = new JSONObject();

		try {
			String serviceName = "Get URL For EAdhar";

			LOGGER.info("  Inside Get URL For EAdhar  ");

			String pcOptionName = "Wallet";
			String amount = "1";
			double amtInDouble = Double.parseDouble(amount);
			if (merchantWallet < amtInDouble) {

				jsonObj.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
				jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

				return jsonObj.toString();
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

			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			EkycRequest ekycRequest = ekycRequestRepository
					.save(new EkycRequest(merchantId, "EAadharGetURL", "EAadharGetURL", trxnDate));
			long ekycRequestId = ekycRequest.getRequestId();

			// response = aadhaarService.createUrlForDigilocker();
			String serviceProvider = "Signzy";
			ServiceProviders spInfo = serviceprovidersrepository.findBySpName(Encryption.encString(serviceProvider));
			Long serviceProviderId = spInfo.getServiceProviderId();
			EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
					.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
							ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
							serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
							serviceProviderId, '0'));

		}

		catch (JSONException e) {
			e.printStackTrace();
			jsonObj.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICE_NOT_AVILABLE);
			jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response = jsonObj.toString();
		} catch (Exception e) {
			e.printStackTrace();
			jsonObj.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response = jsonObj.toString();
		}
		return response;
	}

	public String eAdhar(String requestId, long merchantId, double merchantWallet) throws Exception {
		String response = null;
		JSONObject jsonObj = new JSONObject();

		try {
			String serviceName = "EAdhar";

			LOGGER.info("  Inside Get URL For EAdhar  ");

			String amount = "1";
			double amtInDouble = Double.parseDouble(amount);

			if (merchantWallet < amtInDouble) {

				jsonObj.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
				jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

				return jsonObj.toString();
			}
			String pcOptionName = "Wallet";

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

			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			EkycRequest ekycRequest = ekycRequestRepository
					.save(new EkycRequest(merchantId, requestId, requestId, trxnDate));
			long ekycRequestId = ekycRequest.getRequestId();

//			Bank bank = new Bank();
//			ThreadWithdrawal test = new ThreadWithdrawal(bank, merchantTrxnRefId, amtInDouble, merchantWallet,
//					merchantId, merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId, serviceName,
//					trxnRefId, walletTxnRefNo, serviceName);
//			LOGGER.info("Name of Thread ----------------------- " + test.getName());
//			test.start();

			WalletRequest debitRequest = new WalletRequest(merchantId, charges, "Debit", serviceName, serviceName,
					ekycRequestId, trxnRefId, merchantTrxnRefId, walletTxnRefNo);
			walletService.enqueueTransaction(debitRequest);

			// response = aadhaarService.eAadhar(requestId);
			String serviceProvider = "Signzy";
			ServiceProviders spInfo = serviceprovidersrepository.findBySpName(Encryption.encString(serviceProvider));
			Long serviceProviderId = spInfo.getServiceProviderId();
			EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
					.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
							ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
							serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
							serviceProviderId, '0'));

		}

		catch (JSONException e) {
			jsonObj.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICE_NOT_AVILABLE);
			jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response = jsonObj.toString();
		} catch (Exception e) {
			e.printStackTrace();
			jsonObj.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response = jsonObj.toString();
		}
		return response;
	}

	public String getDetailsEAdhar(String requestId, long merchantId, double merchantWallet) throws Exception {
		String response = null;
		JSONObject jsonObj = new JSONObject();

		try {
			String serviceName = "Get Details EAdhar";

			LOGGER.info("  Inside Get URL For EAdhar  ");

			String pcOptionName = "Wallet";
			String amount = "1";
			double amtInDouble = Double.parseDouble(amount);
			if (merchantWallet < amtInDouble) {

				jsonObj.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
				jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

				return jsonObj.toString();
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

			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			EkycRequest ekycRequest = ekycRequestRepository
					.save(new EkycRequest(merchantId, requestId, requestId, trxnDate));
			long ekycRequestId = ekycRequest.getRequestId();

//			Bank bank = new Bank();
//			ThreadWithdrawal test = new ThreadWithdrawal(bank, merchantTrxnRefId, amtInDouble, merchantWallet,
//					merchantId, merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId, serviceName,
//					trxnRefId, walletTxnRefNo, serviceName);
//			LOGGER.info("Name of Thread ----------------------- " + test.getName());
//			test.start();

			WalletRequest debitRequest = new WalletRequest(merchantId, charges, "Debit", serviceName, serviceName,
					ekycRequestId, trxnRefId, merchantTrxnRefId, walletTxnRefNo);
			walletService.enqueueTransaction(debitRequest);

			// response = aadhaarService.getDetails(requestId);
			String serviceProvider = "Signzy";
			ServiceProviders spInfo = serviceprovidersrepository.findBySpName(Encryption.encString(serviceProvider));
			Long serviceProviderId = spInfo.getServiceProviderId();
			EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
					.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
							ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
							serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
							serviceProviderId, '0'));
		}

		catch (JSONException e) {
			jsonObj.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICE_NOT_AVILABLE);
			jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response = jsonObj.toString();
		} catch (Exception e) {
			jsonObj.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response = jsonObj.toString();
		}
		return response;
	}

	public boolean checkServiceExistOrNot(long merchantId, String serviceName) {
		LOGGER.info(" Inside 4 : " + serviceName);
		ServiceInfo serviceInfo = serviceInfoRepository.findServiceByName(Encryption.encString(serviceName));
		Long serviceId = 0L;

		try {
			serviceId = serviceInfo.getServiceId();
		} catch (Exception e) {
			return false;
		}

		LOGGER.info(" serviceId : " + serviceId);

		if (merchantServiceRepository.existsByServiceIdAndMerchantIdAndIsMerchantServiceActive(serviceId, merchantId,
				'Y')) {
			LOGGER.info(" merchantServiceRepository : ");
			return true;
		}

		else {
			LOGGER.info(" Inside 5 : ");
			return false;
		}

	}

	public String saveDataForGenerateOtp(String aadhaarNumber, long merchantId, double merchantWallet,
			String serviceName) throws Exception {
		String response = null;
		JSONObject jsonObj = new JSONObject();

		try {

			LOGGER.info(" Inside validateOtp ");
			String amount = "1";
			double amtInDouble = Double.parseDouble(amount);

			if (merchantWallet < amtInDouble) {
				jsonObj.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
				jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return jsonObj.toString();
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
				LOGGER.info("charges: {}", charges);
				break;

			case "Commission":
				commission = chargeService.getMerchantServiceCommissionV2(merchantServiceId, amtInDouble);
				LOGGER.info("commission: {}", commission);
				break;

			default:
				break;

			}

			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			EkycRequest ekycRequest = ekycRequestRepository
					.save(new EkycRequest(merchantId, merchantTrxnRefId, merchantTrxnRefId, trxnDate));

			long ekycRequestId = ekycRequest.getRequestId();

			LOGGER.info("ekycRequestId---> {}", ekycRequestId);

//			Bank bank = new Bank();
//			ThreadWithdrawal test = new ThreadWithdrawal(bank, merchantTrxnRefId, amtInDouble, merchantWallet,
//					merchantId, merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId, serviceName,
//					trxnRefId, walletTxnRefNo, serviceName);
//			LOGGER.info("Name of Thread -------- {}", test.getName());
//			test.start();

			WalletRequest debitRequest = new WalletRequest(merchantId, charges, "Debit", serviceName, serviceName,
					ekycRequestId, trxnRefId, merchantTrxnRefId, walletTxnRefNo);
			walletService.enqueueTransaction(debitRequest);

			response = generateOtp(aadhaarNumber, merchantId);

			LOGGER.info("serviceProvider ----------------------- {}", Encryption.decString("0i5NGA6oxVC3u7vH4GYf5A=="));

			String serviceProvider = "Decentro";
			ServiceProviders spInfo = serviceprovidersrepository.findBySpName(Encryption.encString(serviceProvider));

			Long serviceProviderId = spInfo.getServiceProviderId();

			LOGGER.info("serviceProviderId--->{}", serviceProviderId);
			EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
					.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
							ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
							serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
							serviceProviderId, '0'));
			LOGGER.info("EkycTransactionId {}", ekycTransactionDetails.getEkycTransactionId());
		} catch (Exception e) {
			jsonObj.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response = jsonObj.toString();
		}
		return response;
	}

	public String generateOtp(String aadhaarNumber, long merchantId) throws ParseException {
		String responseBody = "";
		GenerateOtpRequest generateotprequest = new GenerateOtpRequest();
		Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());
		generateotprequest.setAadhaar_number(aadhaarNumber);
		try {

			OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(60, TimeUnit.SECONDS)
					.readTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS).build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, new Gson().toJson(generateotprequest));

			Request request = new Request.Builder().url(DecentroUtils.DECENTRO_API_BASE_URL + "v2/kyc/aadhaar/otp")
					.method("POST", body).addHeader("client_id", DecentroUtils.DECENTRO_CLIENT_ID)
					.addHeader("client_secret", DecentroUtils.DECENTRO_CLIENT_SECRET)
					.addHeader("module_secret", DecentroUtils.DECENTRO_MODULE_SECRET)
					.addHeader("Content-Type", "application/json").build();

			System.out.println("Client-Id: " + DecentroUtils.DECENTRO_CLIENT_ID);
			System.out.println("Client-Secret: " + DecentroUtils.DECENTRO_CLIENT_SECRET);

			System.out.println("body: " + new Gson().toJson(generateotprequest));

			System.out.println("request: " + request);

			Response response = client.newCall(request).execute();
			responseBody = response.body().string();

			System.out.println("responseBody: " + responseBody);

			JSONObject json;
			try {
				json = new JSONObject(responseBody);
				String result = json.getString("status");
				String message = json.getString("message");
				String merchantTxnId = json.getString("decentroTxnId");
				JSONObject jsonObject = new JSONObject();
				if (result.contains("SUCCESS")) {
					EkycRequest ekycRequest = ekycRequestRepository
							.save(new EkycRequest(merchantId, merchantTxnId, merchantTxnId, trxnDate));

					jsonObject.put(ResponseMessage.STATUS, ResponseMessage.STATUS_SUCCESS);
					jsonObject.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
					jsonObject.put(ResponseMessage.DESCRIPTION, message);
					jsonObject.put("merchantTxnRefId", merchantTxnId);

				} else if (message.contains("No response received from the underlying provider")) {
//					String merchantTxnId = json.getString("decentroTxnId");
					jsonObject.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
					jsonObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					jsonObject.put(ResponseMessage.DESCRIPTION,
							"Please try again after sometime. If the problem persists, please drop a mail to tech.support@fidypay.com");
					jsonObject.put("merchantTxnRefId", merchantTxnId);
				} else {
					jsonObject.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
					jsonObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					jsonObject.put(ResponseMessage.DESCRIPTION, message);
					jsonObject.put("merchantTxnRefId", merchantTxnId);

				}
				responseBody = jsonObject.toString();

				return responseBody;
			} catch (JSONException e) {

			}

		} catch (IOException e) {

		}

		return responseBody;
	}

	public String saveDataForAadharValidate(ValidateOtp validateotp, long merchantId, double merchantWallet)
			throws Exception {
		String response = null;
		JSONObject jsonObj = new JSONObject();

		try {
			String serviceName = "Validate Aadhaar";

			LOGGER.info(" Inside validateOtp ");
			String amount = "1";
			double amtInDouble = Double.parseDouble(amount);
			if (merchantWallet < amtInDouble) {
				jsonObj.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
				jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return jsonObj.toString();
			}
			String merchantTrxnRefId = RandomNumberGenrator.generateRandomStringRefId()
					+ RandomNumberGenrator.randomNumberGenerate(16);
			validateotp.setReference_id(merchantTrxnRefId);
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
			String valOtp = validateotp.getReference_id();

			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			EkycRequest ekycRequest = ekycRequestRepository.save(new EkycRequest(merchantId, valOtp, valOtp, trxnDate));
			long ekycRequestId = ekycRequest.getRequestId();

//			Bank bank = new Bank();
//			ThreadWithdrawal test = new ThreadWithdrawal(bank, merchantTrxnRefId, amtInDouble, merchantWallet,
//					merchantId, merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId, serviceName,
//					trxnRefId, walletTxnRefNo, serviceName);
//			LOGGER.info("Name of Thread ----------------------- " + test.getName());
//			test.start();

			WalletRequest debitRequest = new WalletRequest(merchantId, charges, "Debit", serviceName, serviceName,
					ekycRequestId, trxnRefId, merchantTrxnRefId, walletTxnRefNo);
			walletService.enqueueTransaction(debitRequest);

			// response = aadhaarService.validateOtp(validateotp);
			String serviceProvider = "Decentro";
			ServiceProviders spInfo = serviceprovidersrepository.findBySpName(Encryption.encString(serviceProvider));
			Long serviceProviderId = spInfo.getServiceProviderId();
			EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
					.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
							ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
							serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
							serviceProviderId, '0'));

		} catch (Exception e) {
			jsonObj.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response = jsonObj.toString();
		}
		return response;
	}

	public String saveDataForValidate(Validate validate, long merchantId, double merchantWallet) throws Exception {
		String response = null;
		JSONObject jsonObj = new JSONObject();

		// response = ValidateUtils.validate(validate.getDocument_type(),
		// validate.getId_number());

		if (response != ResponseMessage.STATUS_SUCCESS) {

			return response;
		}

		try {
			String serviceName = "Single KYC API";

			LOGGER.info(" Inside validate ");

			String pcOptionName = "Wallet";
			String amount = "1";
			double amtInDouble = Double.parseDouble(amount);
			if (merchantWallet < amtInDouble) {

				jsonObj.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObj.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
				jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);

				return jsonObj.toString();
			}
			String merchantTrxnRefId = RandomNumberGenrator.generateRandomStringRefId()
					+ RandomNumberGenrator.randomNumberGenerate(16);
			validate.setReference_id(merchantTrxnRefId);
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
			String valOtp = validate.getReference_id();

			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			EkycRequest ekycRequest = ekycRequestRepository.save(new EkycRequest(merchantId, valOtp, valOtp, trxnDate));
			long ekycRequestId = ekycRequest.getRequestId();

//			Bank bank = new Bank();
//			ThreadWithdrawal test = new ThreadWithdrawal(bank, merchantTrxnRefId, amtInDouble, merchantWallet,
//					merchantId, merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId, serviceName,
//					trxnRefId, walletTxnRefNo, serviceName);
//			LOGGER.info("Name of Thread ----------------------- " + test.getName());
//			test.start();

			WalletRequest debitRequest = new WalletRequest(merchantId, charges, "Debit", serviceName, serviceName,
					ekycRequestId, trxnRefId, merchantTrxnRefId, walletTxnRefNo);
			walletService.enqueueTransaction(debitRequest);

			response = validate(validate);
			String serviceProvider = "Decentro";
			ServiceProviders spInfo = serviceprovidersrepository.findBySpName(Encryption.encString(serviceProvider));
			Long serviceProviderId = spInfo.getServiceProviderId();
			EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
					.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
							ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
							serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
							serviceProviderId, '0'));

		} catch (Exception e) {
			jsonObj.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			jsonObj.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
			jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			response = jsonObj.toString();
		}
		return response;
	}

	@SuppressWarnings("unused")
	public String validate(Validate validate) {
		String responseBody = "";
		try {

			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, new Gson().toJson(validate));

			LOGGER.info("Body ----------------------- " + body);

			Request request = new Request.Builder()
					.url(DecentroUtils.DECENTRO_API_BASE_URL + "kyc/public_registry/validate").method("POST", body)
					.addHeader("client_id", DecentroUtils.DECENTRO_CLIENT_ID)
					.addHeader("client_secret", DecentroUtils.DECENTRO_CLIENT_SECRET)
					.addHeader("module_secret", DecentroUtils.DECENTRO_MODULE_SECRET)
					.addHeader("Content-Type", "application/json").build();

			LOGGER.info("request ----------------------- " + request);

			Response response = client.newCall(request).execute();
			responseBody = response.body().string();

			LOGGER.info("responseBody ----------------------- " + responseBody);

			JSONObject jsonObject = new JSONObject();

			if (responseBody == null || responseBody.isEmpty()) {
				jsonObject.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
				jsonObject.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
				jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
				responseBody = jsonObject.toString();
				return responseBody;
			}
			JSONObject json = new JSONObject(responseBody);
			try {
				String kycStatus = json.getString("kycStatus");

				if (validate.getDocument_type().equals("DRIVING_LICENSE") && kycStatus.equals("UNKNOWN")) {

					JSONObject error = json.getJSONObject("error");
					String message = error.getString("message");
					String merchantTxnId = json.getString("decentroTxnId");

					jsonObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					jsonObject.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
					jsonObject.put("merchantTxnId", merchantTxnId);
					jsonObject.put(ResponseMessage.DESCRIPTION, message);
					responseBody = jsonObject.toString();
					return responseBody;

				}

				if (kycStatus.equals("SUCCESS")) {
					JSONObject kycResult = json.getJSONObject("kycResult");
					kycResult.remove("status");
					kycResult.remove("dateOfBirth");
					String merchantTxnId = json.getString("decentroTxnId");
					jsonObject.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
					jsonObject.put(ResponseMessage.STATUS, ResponseMessage.STATUS_SUCCESS);
					jsonObject.put("merchantKycResult", kycResult);
					jsonObject.put("merchantTxnId", merchantTxnId);
					jsonObject.put(ResponseMessage.DESCRIPTION,
							validate.getDocument_type() + " details retrived successfully.");
					responseBody = jsonObject.toString();
				}

				if (kycStatus.equals("UNKNOWN") || kycStatus.equals("FAILURE")) {

					String merchantTxnId = json.getString("decentroTxnId");

					jsonObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					jsonObject.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
					jsonObject.put("merchantTxnId", merchantTxnId);
					jsonObject.put(ResponseMessage.DESCRIPTION,
							"No records found for the given " + validate.getDocument_type() + " ID.");
					responseBody = jsonObject.toString();
				}

//				if (kycStatus.equals("FAILURE") || kycStatus.equals("UNKNOWN")) {
//
//					String merchantTxnId = json.getString("decentroTxnId");
//					JSONObject messageObject = json.getJSONObject("error");
//					String message = messageObject.getString("message");
//
//					if (message.contains("You have consumed your PRODUCTION credits.")) {
//						jsonObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
//						jsonObject.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
//						jsonObject.put("merchantTxnId", merchantTxnId);
//						jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE + " !");
//						responseBody = jsonObject.toString();
//					}
//				}

			} catch (JSONException e) {
				String merchantTxnId = json.getString("decentroTxnId");
				jsonObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObject.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
				jsonObject.put(ResponseMessage.DESCRIPTION, "Please enter valid documentIdNumber.");
				jsonObject.put("merchantTxnId", merchantTxnId);
				responseBody = jsonObject.toString();
				e.printStackTrace();
			}

		} catch (Exception e) {

		}

		return responseBody;
	}

	public String saveDataForPassportFetchV2(@Valid FetchPassportRequest fetchPassportRequest, long merchantId,
			Double merchantWallet, String serviceName) {
		String response = null;
		JSONObject jsonObj = new JSONObject();
		try {
			String fileNumber = fetchPassportRequest.getFileNumber();
			String dob = fetchPassportRequest.getDob();

			LOGGER.info("Inside PassPort Details");

			String pcOptionName = "Wallet";
			String amount = "1";
			double amtInDouble = Double.parseDouble(amount);
			if (merchantWallet < amtInDouble) {

				jsonObj.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
				jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

				return jsonObj.toString();
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

			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			EkycRequest ekycRequest = ekycRequestRepository
					.save(new EkycRequest(merchantId, fileNumber, walletTxnRefNo, trxnDate));
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

			Date date1 = new SimpleDateFormat("dd-MM-yyyy").parse(fetchPassportRequest.getDob());

			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			dob = formatter.format(date1);
			System.out.println("dob: " + dob);

			if (validations.validateJavaDate(dob)) {
				// response = karzaService.fetchPassportV2(fetchPassportRequest.getFileNumber(),
				// dob);
				String serviceProvider = "Karza Tech";
				ServiceProviders spInfo = serviceprovidersrepository
						.findBySpName(Encryption.encString(serviceProvider));
				Long serviceProviderId = spInfo.getServiceProviderId();
				EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
						.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
								ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
								serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
								serviceProviderId, '0'));
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

				jsonObj.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATE_FORMATE_INVALID);
				jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				response = jsonObj.toString();
			}

		} catch (Exception e) {
			jsonObj.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response = jsonObj.toString();
		}
		return response;
	}

	public String saveDataForVerifyPassportV2(VerifyPassportRequestV2 verifyPassportRequestV2, long merchantId,
			Double merchantWallet, String serviceName) {
		String response = null;
		JSONObject jsonObj = new JSONObject();

		try {
			String passportNumber = verifyPassportRequestV2.getPassportNumber();
			String dob = verifyPassportRequestV2.getDob();

			LOGGER.info("Inside PassPort verify");

			String pcOptionName = "Wallet";
			String amount = "1";
			double amtInDouble = Double.parseDouble(amount);
			if (merchantWallet < amtInDouble) {

				jsonObj.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
				jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

				return jsonObj.toString();
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

			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			EkycRequest ekycRequest = ekycRequestRepository
					.save(new EkycRequest(merchantId, passportNumber, walletTxnRefNo, trxnDate));
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

			Date date1 = new SimpleDateFormat("dd-MM-yyyy").parse(verifyPassportRequestV2.getDob());

			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			dob = formatter.format(date1);
			System.out.println("dob: " + dob);

			if (validations.validateJavaDate(dob)) {

//				response = karzaService.verifyPassportV2(verifyPassportRequestV2.getPassportNumber(), dob,
//						verifyPassportRequestV2.getName());
				String serviceProvider = "Karza Tech";
				ServiceProviders spInfo = serviceprovidersrepository
						.findBySpName(Encryption.encString(serviceProvider));
				Long serviceProviderId = spInfo.getServiceProviderId();
				EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
						.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
								ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
								serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
								serviceProviderId, '0'));
			} else {
				String serviceProvider = "Karza Tech";
				ServiceProviders spInfo = serviceprovidersrepository
						.findBySpName(Encryption.encString(serviceProvider));
				Long serviceProviderId = spInfo.getServiceProviderId();
				@SuppressWarnings("unused")
				EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
						.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
								ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
								serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
								serviceProviderId, '0'));
				jsonObj.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATE_FORMATE_INVALID);
				jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				response = jsonObj.toString();
			}

		} catch (Exception e) {
			jsonObj.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response = jsonObj.toString();
		}
		return response;

	}

	public String saveDataForVoterV2(@Valid VoterIdRequestV2 voterIdRequestV2, long merchantId, Double merchantWallet,
			String password) {
		String response = null;
		JSONObject jsonObj = new JSONObject();

		try {
			String serviceName = "Voter Details";
			String voterId = voterIdRequestV2.getVoterId();

			LOGGER.info("Inside Voter Details");

			String pcOptionName = "Wallet";
			String amount = "1";
			double amtInDouble = Double.parseDouble(amount);
			if (merchantWallet < amtInDouble) {

				jsonObj.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
				jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

				return jsonObj.toString();
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

			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			EkycRequest ekycRequest = ekycRequestRepository
					.save(new EkycRequest(merchantId, password, walletTxnRefNo, trxnDate));
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

			if (validations.isValidEPICNumber(voterId)) {

				// response = karzaService.verifyVoterV2(voterId);
				String serviceProvider = "Karza Tech";
				ServiceProviders spInfo = serviceprovidersrepository
						.findBySpName(Encryption.encString(serviceProvider));
				Long serviceProviderId = spInfo.getServiceProviderId();
				EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
						.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
								ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
								serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
								serviceProviderId, '0'));
			} else {
				String serviceProvider = "Karza Tech";
				ServiceProviders spInfo = serviceprovidersrepository
						.findBySpName(Encryption.encString(serviceProvider));
				Long serviceProviderId = spInfo.getServiceProviderId();
				EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
						.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
								ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
								serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
								serviceProviderId, '0'));
				jsonObj.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.VOTER_ID_INVALID);
				jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				response = jsonObj.toString();
			}

		} catch (Exception e) {
			jsonObj.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			jsonObj.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response = jsonObj.toString();
		}
		return response;
	}

	public String saveDataForRCValidate(String vehicleNumber, long merchantId, double merchantWallet) {
		String response = null;
		JSONObject jsonObj = new JSONObject();

		vehicleNumber = vehicleNumber.toUpperCase();

		try {
			String serviceName = "Vehicle Registration";

			LOGGER.info(" Inside validate ");

			String pcOptionName = "Wallet";
			String amount = "1";
			double amtInDouble = Double.parseDouble(amount);
			if (merchantWallet < amtInDouble) {

				jsonObj.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObj.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
				jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);

				return jsonObj.toString();
			}
			String merchantTrxnRefId = RandomNumberGenrator.generateRandomStringRefId()
					+ RandomNumberGenrator.randomNumberGenerate(16);
			String trxnRefId = GenerateTrxnRefId.getTranRefID("API", "Wallet", "EKYC");
			String walletTxnRefNo = GenerateTrxnRefId.getTranRefID("API", "Wallet", "");

			UUID randomUUID = UUID.randomUUID();
			String randomStr = randomUUID.toString().replaceAll("-", "").substring(0, 10);

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

			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			EkycRequest ekycRequest = ekycRequestRepository
					.save(new EkycRequest(merchantId, merchantTrxnRefId, merchantTrxnRefId, trxnDate));
			long ekycRequestId = ekycRequest.getRequestId();

//			Bank bank = new Bank();
//			ThreadWithdrawal test = new ThreadWithdrawal(bank, merchantTrxnRefId, amtInDouble, merchantWallet,
//					merchantId, merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId, serviceName,
//					trxnRefId, walletTxnRefNo, serviceName);
//			LOGGER.info("Name of Thread ----------------------- " + test.getName());
//			test.start();

			WalletRequest debitRequest = new WalletRequest(merchantId, charges, "Debit", serviceName, serviceName,
					ekycRequestId, trxnRefId, merchantTrxnRefId, walletTxnRefNo);
			walletService.enqueueTransaction(debitRequest);

			response = RCValidate(vehicleNumber);
			String serviceProvider = "Signzy";
			ServiceProviders spInfo = serviceprovidersrepository.findBySpName(Encryption.encString(serviceProvider));
			Long serviceProviderId = spInfo.getServiceProviderId();
			EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
					.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
							ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
							serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
							serviceProviderId, '0'));

		} catch (Exception e) {
			LOGGER.info("Inside catch block ----------------------- ");
			jsonObj.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			jsonObj.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
			jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			response = jsonObj.toString();
		}
		return response;
	}

	public String RCValidate(String vehicleNumber) {

		String responseBody = null;

		JSONObject jsonObject = new JSONObject();
		try {
			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType,
					"{\r\n    \"username\": \"" + USER_NAME + "\",\r\n    \"password\": \"" + PASSWORD + "\"\r\n}");

			LOGGER.info("Body ----------------------- " + body);
			Request request = new Request.Builder().url(LOGIN_URL).method("POST", body)
					.addHeader("Content-Type", "application/json").build();

			LOGGER.info("Request ----------------------- " + request);

			Response response = client.newCall(request).execute();

			responseBody = response.body().string();

			LOGGER.info("Response ----------------------- " + responseBody);

			JSONObject respJson = new JSONObject(responseBody);

			String Authorization = respJson.getString("id");
			String userId = respJson.getString("userId");

			String vehicleRegistrationsurl = "https://preproduction.signzy.tech/api/v2/patrons/" + userId
					+ "/vehicleregistrations";

			LOGGER.info("vehicleRegistrationsurl ----------------------- " + vehicleRegistrationsurl);

			OkHttpClient client1 = new OkHttpClient().newBuilder().build();
			MediaType mediaType1 = MediaType.parse("application/json");
			RequestBody body1 = RequestBody.create(mediaType1,
					"{\r\n    \"task\": \"detailedSearch\",\r\n    \"essentials\": {\r\n        \"vehicleNumber\": \""
							+ vehicleNumber
							+ "\",\r\n        \"blacklistCheck\": \"true\"\r\n    },\r\n    \"signzyID\": \""
							+ SIGNZY_ID + "\"\r\n}");

			LOGGER.info("vehicleRegistrationsrequest ----------------------- " + body1);
			Request request1 = new Request.Builder().url(vehicleRegistrationsurl).method("POST", body1)
					.addHeader("Authorization", Authorization).addHeader("Content-Type", "application/json").build();
			Response response1 = client1.newCall(request1).execute();

			LOGGER.info("vehicleRegistrationsrequest ----------------------- " + request1);

			responseBody = response1.body().string();

			LOGGER.info("Response1 ----------------------- " + responseBody);

			int code = response1.code();

			LOGGER.info("code ----------------------- " + code);

			JSONObject respJson1 = new JSONObject(responseBody);

			if (code == 400 || code == 404 || code == 403) {

				JSONObject error = respJson1.getJSONObject("error");

				jsonObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObject.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
				jsonObject.put(ResponseMessage.DESCRIPTION, error.get("message"));
				responseBody = jsonObject.toString();
				return responseBody;
			}

			JSONObject result = respJson1.getJSONObject("result");
			result.remove("status");
			LOGGER.info("result ----------------------- " + result);
			jsonObject.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.STATUS_SUCCESS);
			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.STATUS_SUCCESS);
			jsonObject.put("Data", result);
			responseBody = jsonObject.toString();

		} catch (IOException e) {
			LOGGER.info("Inside catch block ----------------------- ");
			jsonObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.STATUS_FAILED);
			responseBody = jsonObject.toString();
			return responseBody;
		}

		return responseBody;

	}

	public synchronized String saveDataForValidateCibilScore(CibilScoreValidateRequest cibilscorerequest,
			long merchantId, double merchantWallet, String businessName, String email) {
		String response = null;
		JSONObject jsonObj = new JSONObject();

		try {
			semaphore.acquire();
			lock.lock();

			String serviceName = "EAdhar";
			LOGGER.info(" Inside validate ");

			String pcOptionName = "Wallet";
			String amount = "33.00";
			double amtInDouble = Double.parseDouble(amount);

			String merchantTrxnRefId = RandomNumberGenrator.generateRandomStringRefId()
					+ RandomNumberGenrator.randomNumberGenerate(16);
			String trxnRefId = GenerateTrxnRefId.getTranRefID("API", "Wallet", "EKYC");
			String walletTxnRefNo = GenerateTrxnRefId.getTranRefID("API", "Wallet", "");

			UUID randomUUID = UUID.randomUUID();
			String randomStr = randomUUID.toString().replaceAll("-", "").substring(0, 10);

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

			if (merchantWallet < amtInDouble) {
				jsonObj.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObj.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
				jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
				return jsonObj.toString();
			}

			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			EkycRequest ekycRequest = ekycRequestRepository
					.save(new EkycRequest(merchantId, randomStr, randomStr, trxnDate));
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

			response = cibilScoreValidate(cibilscorerequest);
			String serviceProvider = "DocBoyz";
			ServiceProviders spInfo = serviceprovidersrepository.findBySpName(Encryption.encString(serviceProvider));
			Long serviceProviderId = spInfo.getServiceProviderId();

			EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
					.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
							ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
							serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
							serviceProviderId, '0'));

//			EkycTransactionDetails elk = ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
//			LOGGER.info("ELASTICSEARCH DATA INSERTION FOR VALIDATE CIBIL SCORE EKYC TRXN DETAILS : {}", elk);

		} catch (Exception e) {
			LOGGER.info("Inside catch block ----------------------- ");
			jsonObj.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			jsonObj.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
			jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			response = jsonObj.toString();
		} finally {
			lock.unlock();
			semaphore.release();
		}
		return response;
	}

	public synchronized String saveDataForOtpCibilScore(CibilScoreOtpRequest cibilscorerequest, long merchantId,
			double merchantWallet, String businessName, String email) {
		String response = null;
		JSONObject jsonObj = new JSONObject();

		try {
			semaphore.acquire();
			lock.lock();
			String serviceName = "EAdhar";
			LOGGER.info(" Inside validate ");
			String pcOptionName = "Wallet";
			String amount = "33.00";
			double amtInDouble = Double.parseDouble(amount);

			String merchantTrxnRefId = RandomNumberGenrator.generateRandomStringRefId()
					+ RandomNumberGenrator.randomNumberGenerate(16);
			String trxnRefId = GenerateTrxnRefId.getTranRefID("API", "Wallet", "EKYC");
			String walletTxnRefNo = GenerateTrxnRefId.getTranRefID("API", "Wallet", "");

			UUID randomUUID = UUID.randomUUID();
			String randomStr = randomUUID.toString().replaceAll("-", "").substring(0, 10);

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

			if (merchantWallet < amtInDouble) {
				jsonObj.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObj.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
				jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
				return jsonObj.toString();
			}

			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			EkycRequest ekycRequest = ekycRequestRepository
					.save(new EkycRequest(merchantId, randomStr, randomStr, trxnDate));
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

			response = cibilScoreGenerateOtp(cibilscorerequest);
			String serviceProvider = "DocBoyz";
			ServiceProviders spInfo = serviceprovidersrepository.findBySpName(Encryption.encString(serviceProvider));
			Long serviceProviderId = spInfo.getServiceProviderId();

			EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
					.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
							ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
							serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
							serviceProviderId, '0'));

//			EkycTransactionDetails elk = ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
//			LOGGER.info("ELASTICSEARCH DATA INSERTION FOR OTP CIBIL SCORE EKYC TRXN DETAILS : {}", elk);

		} catch (Exception e) {
			jsonObj.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			jsonObj.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
			jsonObj.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			response = jsonObj.toString();
		} finally {
			lock.unlock();
			semaphore.release();
		}
		return response;
	}

	private String cibilScoreGenerateOtp(CibilScoreOtpRequest cibilscorerequest) {

		String responseBody = null;
		JSONObject jsonObject = new JSONObject();
		try {
			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType,
					"{\r\n\"fname\":\"" + cibilscorerequest.getFirstName() + "\",\r\n\"lname\":\""
							+ cibilscorerequest.getLastName() + "\",\r\n\"phone_number\":\""
							+ cibilscorerequest.getPhoneNumber() + "\",\r\n\"pan_num\":\""
							+ cibilscorerequest.getPanNumber() + "\"\r\n}");
			Request request = new Request.Builder().url(CIBIL_SCORE_OTP_URL).method("POST", body)
					.addHeader("Content-Type", "application/json").build();
			Response response = client.newCall(request).execute();

			responseBody = response.body().string();

			JSONObject respJson = new JSONObject(responseBody);

			String success = respJson.getString("success");

			jsonObject.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.STATUS_SUCCESS);
			jsonObject.put(ResponseMessage.DESCRIPTION, success);
			responseBody = jsonObject.toString();

		} catch (IOException e) {
			jsonObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			responseBody = jsonObject.toString();
			return responseBody;
		}

		return responseBody;
	}

	private String cibilScoreValidate(CibilScoreValidateRequest cibilscorerequest) {

		String responseBody = null;
		JSONObject jsonObject = new JSONObject();
		String AccessToken = "752961d68f6e6bb7ff40538b6e7070b2f3";
		try {
			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");

			RequestBody body = RequestBody.create(mediaType,
					"{\r\n\"fname\":\"" + cibilscorerequest.getFirstName() + "\",\r\n\"lname\":\""
							+ cibilscorerequest.getLastName() + "\",\r\n\"phone_number\":\""
							+ cibilscorerequest.getPhoneNumber() + "\",\r\n\"dob\":\"" + cibilscorerequest.getDob()
							+ "\",\r\n\"otp\":\"" + cibilscorerequest.getOtp() + "\"\r\n}\r\n");
			Request request = new Request.Builder().url(CIBIL_SCORE_VALIDATE_URL).method("POST", body)
					.addHeader("AccessToken", AccessToken).addHeader("Content-Type", "application/json").build();
			Response response = client.newCall(request).execute();

			responseBody = response.body().string();
			LOGGER.info("responseBody ----------------------- " + responseBody);

			JSONObject respJson = new JSONObject(responseBody);

			int code = respJson.getInt("statusCode");
			LOGGER.info("Code ----------------------- " + code);
			if (code == 102) {

				JSONObject message = respJson.getJSONObject("message");
				JSONObject CCRResponse = message.getJSONObject("CCRResponse");
				CCRResponse.remove("Status");
				jsonObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObject.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
				jsonObject.put(ResponseMessage.DESCRIPTION, message);
				responseBody = jsonObject.toString();
				return responseBody;
			}

			if (code == 404) {

				String message = respJson.getString("message");
				jsonObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObject.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
				jsonObject.put(ResponseMessage.DESCRIPTION, message);
				responseBody = jsonObject.toString();
				return responseBody;
			}

			JSONObject Equifax_Report = respJson.getJSONObject("Equifax_Report");
			JSONObject CCRResponse = Equifax_Report.getJSONObject("CCRResponse");
			CCRResponse.remove("Status");
			jsonObject.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.STATUS_SUCCESS);
			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.STATUS_SUCCESS);
			jsonObject.put("Data", Equifax_Report);
			responseBody = jsonObject.toString();

		} catch (IOException e) {
			jsonObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			responseBody = jsonObject.toString();
			return responseBody;
		}

		return responseBody;
	}

}
