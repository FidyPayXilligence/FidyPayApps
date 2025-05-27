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

import com.fidypay.ServiceProvider.Decentro.DecentroServiceImpl;
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
import com.fidypay.request.DigilockerGetAccessToken;
import com.fidypay.request.DigilockerGetIssuedFiles;
import com.fidypay.service.DigilockerService;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.DateAndTime;
import com.fidypay.utils.ex.GenerateTrxnRefId;
import com.fidypay.utils.ex.RandomNumberGenrator;
import com.fidypay.wallet.WalletNotification;
import com.fidypay.wallet.WalletRequest;
import com.fidypay.wallet.WalletService;

@Service
public class DigilockerServiceImpl implements DigilockerService {

	private static final Logger LOGGER = LoggerFactory.getLogger(DigilockerServiceImpl.class);

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
	private ServiceInfoRepository serviceInfoRepository;

	@Autowired
	private MerchantServiceRepository merchantServiceRepository;

	@Autowired
	private MerchantServiceChargeService chargeService;

	@Autowired
	private ServiceProvidersRepository serviceprovidersrepository;

	@Autowired
	private DecentroServiceImpl decentroServiceImpl;

	@Autowired
	private WalletService walletService;

//	@Autowired
//	private EkycTransactionDetailsSearchRepository ekycTransactionDetailsSearchRepository;

	private final Semaphore semaphore = new Semaphore(1);
	private final Lock lock = new ReentrantLock();

	@Override
	public synchronized Map<String, Object> saveDataForDigilockerGetAccessToken(
			DigilockerGetAccessToken digilockergetaccesstoken, long merchantId, double merchantWallet,
			String businessName, String email) {
		Map<String, Object> map = new HashMap<>();

		try {
			semaphore.acquire();
			lock.lock();
			String serviceName = "EAdhar";
			LOGGER.info(" Inside fetch ");

			String amount = "1";
			double amtInDouble = Double.parseDouble(amount);

			String merchantTrxnRefId = RandomNumberGenrator.generateRandomStringRefId()
					+ RandomNumberGenrator.randomNumberGenerate(16);
			digilockergetaccesstoken.setReference_id(merchantTrxnRefId);
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

			String valOtp = digilockergetaccesstoken.toString();

			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			EkycRequest ekycRequest = ekycRequestRepository.save(new EkycRequest(merchantId, valOtp, valOtp, trxnDate));
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

			map = decentroServiceImpl.getAccessToken(digilockergetaccesstoken);

			String serviceProvider = "Riskcovry";
			ServiceProviders spInfo = serviceprovidersrepository.findBySpName(Encryption.encString(serviceProvider));
			Long serviceProviderId = spInfo.getServiceProviderId();

			EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
					.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
							ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
							serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
							serviceProviderId, '0'));
			LOGGER.info("ekycTransactionDetails: {}", ekycTransactionDetails);

//			EkycTransactionDetails elk = ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
//			LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'DIGI LOCKER ACCESS TOKEN' EKYC TRXN DETAILS : {}", elk);

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
	public synchronized Map<String, Object> saveDataForDigilockerGetIssuedFiles(
			DigilockerGetIssuedFiles digilockergetissuedfiles, long merchantId, double merchantWallet,
			String businessName, String email) {
		Map<String, Object> map = new HashMap<>();

		try {
			semaphore.acquire();
			lock.lock();
			String serviceName = "EAdhar";
			LOGGER.info(" Inside fetch ");

			String amount = "1";
			double amtInDouble = Double.parseDouble(amount);

			String merchantTrxnRefId = RandomNumberGenrator.generateRandomStringRefId()
					+ RandomNumberGenrator.randomNumberGenerate(16);
			digilockergetissuedfiles.setReference_id(merchantTrxnRefId);
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
			String valOtp = digilockergetissuedfiles.getReference_id();

			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			EkycRequest ekycRequest = ekycRequestRepository.save(new EkycRequest(merchantId, valOtp, valOtp, trxnDate));
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

			map = decentroServiceImpl.getIssuedFiles(digilockergetissuedfiles);
			String serviceProvider = "Riskcovry";
			ServiceProviders spInfo = serviceprovidersrepository.findBySpName(Encryption.encString(serviceProvider));
			Long serviceProviderId = spInfo.getServiceProviderId();

			EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
					.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
							ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
							serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
							serviceProviderId, '0'));
			LOGGER.info("ekycTransactionDetails: {}", ekycTransactionDetails);

//			EkycTransactionDetails elk = ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
//			LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'DIGI LOCKER ISSUED FILES' EKYC TRXN DETAILS : {}", elk);

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
