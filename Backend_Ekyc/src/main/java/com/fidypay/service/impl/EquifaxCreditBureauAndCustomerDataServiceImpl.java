package com.fidypay.service.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
import com.fidypay.request.CreditBureauReportRequest;
import com.fidypay.request.CustomerDataRequest;
import com.fidypay.service.EquifaxCreditBureauAndCustomerDataService;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.DateAndTime;
import com.fidypay.utils.ex.GenerateTrxnRefId;
import com.fidypay.utils.ex.RandomNumberGenrator;
import com.fidypay.utils.ex.ValidateUtils;
import com.fidypay.wallet.WalletNotification;
import com.fidypay.wallet.WalletRequest;
import com.fidypay.wallet.WalletService;

@Service
public class EquifaxCreditBureauAndCustomerDataServiceImpl implements EquifaxCreditBureauAndCustomerDataService {

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
	private SignzyService signzyService;

	@Autowired
	private WalletService walletService;

//	@Autowired
//	private EkycTransactionDetailsSearchRepository ekycTransactionDetailsSearchRepository;
	private final Semaphore semaphore = new Semaphore(1);
	private final Lock lock = new ReentrantLock();

	private static final Logger LOGGER = LoggerFactory.getLogger(EquifaxCreditBureauAndCustomerDataServiceImpl.class);

//	public static String generateRandomStringRefId() {
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssSSS");
//		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//		String timestampDate = sdf.format(timestamp);
//		String refId = "FP" + timestampDate;
//		LOGGER.info("refId : " + refId + " length " + refId.length());
//		return refId;
//	}
//
//	public static String randomNumberGenerate(int n) {
//		// int n = 16;
//		// chose a Character random from this String
//		String alphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "abcdefghijklmnopqrstuvxyz";
//
//		// create StringBuffer size of AlphaNumericString
//		StringBuilder sb = new StringBuilder(n);
//
//		for (int i = 0; i < n; i++) {
//
//			// generate a random number between
//			// 0 to AlphaNumericString variable length
//			int index = (int) (alphaNumericString.length() * Math.random());
//
//			// add Character one by one in end of sb
//			sb.append(alphaNumericString.charAt(index));
//		}
//
//		return sb.toString();
//
//	}

	@Override
	public Map<String, Object> saveDataForCreditBureauReport(CreditBureauReportRequest creditBureauReportRequest,
			long merchantId, Double merchantWallet, String businessName, String email) {
		LOGGER.info("Inside saveDataForCreditBureauReport :");
		Map<String, Object> map = new HashMap<>();

		try {
			semaphore.acquire();
			lock.lock();
			String serviceName = ResponseMessage.CREDIT_SCORE_SERVICE;
			double amtInDouble = 1.0;

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

			if (merchantWallet < amtInDouble) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

				return map;
			}

			LOGGER.info("serviceName: {} ", serviceName);
			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			EkycRequest ekycRequest = ekycRequestRepository
					.save(new EkycRequest(merchantId, merchantTrxnRefId, walletTxnRefNo, trxnDate));
			long ekycRequestId = ekycRequest.getRequestId();

//			Bank bank = new Bank();
//			ThreadWithdrawal test = new ThreadWithdrawal(bank, merchantTrxnRefId, charges, merchantWallet, merchantId,
//					merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId, serviceName, trxnRefId,
//					walletTxnRefNo, serviceName);
//			LOGGER.info("Name of Thread ----------------------- {}", test.getName());
//			test.start();

			WalletRequest debitRequest = new WalletRequest(merchantId, charges, "Debit", serviceName, serviceName,
					ekycRequestId, trxnRefId, merchantTrxnRefId, walletTxnRefNo);
			walletService.enqueueTransaction(debitRequest);

			map = signzyService.creditBureauReport(creditBureauReportRequest, merchantTrxnRefId);

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
//			LOGGER.info("ELASTICSEARCH DATA INSERTION FOR CREDIT BUREAU REPORT EKYC TRXN DETAILS : {}", elk);

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

//----------------------------------------------------------------------------------------------------------------------------------------------------------

	@Override
	public synchronized Map<String, Object> saveDataForFetchCustomerDetails(CustomerDataRequest customerDataRequest,
			long merchantId, Double merchantWallet, String businessName, String email) {
		LOGGER.info("Inside saveDataForFetchCustomerDetails :");
		Map<String, Object> map = new HashMap<>();

		try {
			semaphore.acquire();
			lock.lock();

			map = ValidateUtils.validateRequest(customerDataRequest.getDocumentType(),
					customerDataRequest.getId_value().toUpperCase());

			if (!map.isEmpty()) {
				return map;
			}

			String serviceName = ResponseMessage.CREDIT_SCORE_BASIC_SERVICE;
			double amtInDouble = 1.0;

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

			if (merchantWallet < amtInDouble) {
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
//			ThreadWithdrawal test = new ThreadWithdrawal(bank, merchantTrxnRefId, charges, merchantWallet, merchantId,
//					merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId, serviceName, trxnRefId,
//					walletTxnRefNo, serviceName);
//			LOGGER.info("Name of Thread -----------------------{}", test.getName());
//			test.start();

			WalletRequest debitRequest = new WalletRequest(merchantId, charges, "Debit", serviceName, serviceName,
					ekycRequestId, trxnRefId, merchantTrxnRefId, walletTxnRefNo);
			walletService.enqueueTransaction(debitRequest);

			map = signzyService.fetchCustomerDetails(customerDataRequest, merchantTrxnRefId);

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
//			LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'CUSTOMER DETAILS' EKYC TRXN DETAILS : {}", elk);

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
	public synchronized Map<String, Object> saveDataForFetchCreditScore(CustomerDataRequest customerDataRequest,
			long merchantId, Double merchantWallet, String businessName, String email) {
		LOGGER.info("Inside saveDataForFetchCustomerDetails :");
		Map<String, Object> map = new HashMap<>();

		try {
			semaphore.acquire();
			lock.lock();
			map = ValidateUtils.validateRequest(customerDataRequest.getDocumentType(),
					customerDataRequest.getId_value().toUpperCase());

			if (!map.isEmpty()) {
				return map;
			}

			String serviceName = ResponseMessage.CREDIT_SCORE_BASIC_SERVICE;
			double amtInDouble = 20.0;

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

//			if (merchantWallet < amtInDouble) {
//				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
//				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
//				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
//
//				return map;
//			}
			LOGGER.info("serviceName " + serviceName);
			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			EkycRequest ekycRequest = ekycRequestRepository
					.save(new EkycRequest(merchantId, merchantTrxnRefId, walletTxnRefNo, trxnDate));
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

			map = signzyService.fetchCreditScore(customerDataRequest, merchantTrxnRefId);

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
//			LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'CUSTOMER DETAILS' EKYC TRXN DETAILS : {}", elk);

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
