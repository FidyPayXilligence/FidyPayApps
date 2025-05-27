package com.fidypay.service.impl;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fidypay.ServiceProvider.NSDL.NSDLServiceImpl;
import com.fidypay.ServiceProvider.Signzy.AccountVerification;
import com.fidypay.ServiceProvider.Signzy.EKYCService;
import com.fidypay.ServiceProvider.Signzy.MerchantServiceChargeService;
import com.fidypay.ServiceProvider.Signzy.ValidateBankAccount;
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
import com.fidypay.request.AccountVerificationRequest;
import com.fidypay.request.NSDLRequest;
import com.fidypay.service.BankAccountVerificationService;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.DateAndTime;
import com.fidypay.utils.ex.GenerateTrxnRefId;
import com.fidypay.utils.ex.RandomNumberGenrator;
import com.fidypay.utils.ex.ValidateUtils;
import com.fidypay.wallet.WalletNotification;
import com.fidypay.wallet.WalletRequest;
import com.fidypay.wallet.WalletService;

@Service
public class BankAccountVerificationServiceImpl implements BankAccountVerificationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(BankAccountVerificationServiceImpl.class);

	@Autowired
	private WalletNotification walletNotification;

	@Autowired
	private ValidateBankAccount validateBankAccount;

	@Autowired
	private ServiceInfoRepository serviceInfoRepository;

	@Autowired
	private MerchantsRepository merchantsRepository;

	@Autowired
	private MerchantWalletTransactionsRepository merchantWalletTransactionsRepository;

	@Autowired
	private AccountVerification accountVerification;

	@Autowired
	private EkycRequestRepository ekycRequestRepository;

	@Autowired
	private EkycTransactionDetailsRepository ekycTransactionDetailsRepository;

	@Autowired
	private MerchantServiceRepository merchantServiceRepository;

	@Autowired
	private NSDLServiceImpl nsdlServiceImpl;

	@Autowired
	private MerchantServiceChargeService chargeService;

	@Autowired
	private ServiceProvidersRepository serviceprovidersrepository;

	@Autowired
	private EKYCService ekycService;

	@Autowired
	private WalletService walletService;

//    @Autowired
//    private EkycTransactionDetailsSearchRepository ekycTransactionDetailsSearchRepository;

	private final Semaphore semaphore = new Semaphore(1);
	private final Lock lock = new ReentrantLock();

	@Override
	public synchronized Map<String, Object> bankAccountVerificationRequest(
			AccountVerificationRequest accountVerificationRequest, long merchantId, double merchantWallet,
			String bussinessName, String email) {
		Map<String, Object> map = new HashMap<>();

		try {
			semaphore.acquire();
			lock.lock();
			String accountNumber = accountVerificationRequest.getBeneficiaryAccNo();
			String ifsc = accountVerificationRequest.getBeneficiaryIfscCode();
			String merchantTrxnRefId = accountVerificationRequest.getMerchantTrxnRefId();

			String amount = "3";
			String serviceName = "Bank Account Verification";
			double amtInDouble = Double.parseDouble(amount);

			map = ValidateUtils.accountVerification(accountVerificationRequest);

			if (!map.isEmpty()) {
				return map;
			}

			if (validateBankAccount.verifyIfscCode(ifsc).equals(ResponseMessage.INVALID_IFSCCODE)) {

				map.put("status", "BAD_REQUEST");
				map.put("description", Arrays.asList(ResponseMessage.INVALID_IFSCCODE));
				map.put("code", ResponseMessage.FAILED);
				return map;
			}

			if (ekycTransactionDetailsRepository.existsByMerchantTransactionRefId(merchantTrxnRefId)
					|| ekycTransactionDetailsRepository
							.existsByMerchantTransactionRefId(Encryption.encString(merchantTrxnRefId))) {
				map.put("status", "BAD_REQUEST");
				map.put("description", Arrays.asList(ResponseMessage.MERCHANTTRXNREFID_ALREADY_EXIST));
				map.put("code", ResponseMessage.FAILED);
				return map;
			}

			ServiceInfo serviceInfo = serviceInfoRepository.findServiceByName(Encryption.encString(serviceName));
			Long serviceId = serviceInfo.getServiceId();
			MerchantService merchantsService = merchantServiceRepository.findByMerchantIdAndServiceId(merchantId,
					serviceId);
			Long merchantServiceId = merchantsService.getMerchantServiceId();
			String serviceType = merchantsService.getServiceType();
			LOGGER.info("serviceType: " + serviceType);
			double charges = 0.0;

			switch (serviceType) {

			case "Charge":
				charges = chargeService.getMerchantServiceChargesV2(merchantServiceId, amtInDouble);
				LOGGER.info("charges: " + charges);
				break;

			default:
				charges = amtInDouble;
				break;

			}

			String resWallet = walletNotification.checkWalletBalance(merchantWallet, bussinessName, email);

			if (merchantWallet < charges) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, Arrays.asList(ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE));
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}

			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());
			EkycRequest ekycRequest = ekycRequestRepository
					.save(new EkycRequest(merchantId, accountNumber, accountNumber, trxnDate));
			long ekycRequestId = ekycRequest.getRequestId();
			LOGGER.info("Save ekycRequestId ----------------{}", ekycRequestId);

			String trxnRefId = GenerateTrxnRefId.getTranRefID("API", "Wallet", "Banking");
			String walletTxnRefNo = GenerateTrxnRefId.getTranRefID("API", "Wallet", "");

//			Bank bank = new Bank();
//			ThreadWithdrawal threadWithdrawal = new ThreadWithdrawal(bank, merchantTrxnRefId, charges, merchantWallet,
//					merchantId, merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId, serviceName,
//					trxnRefId, walletTxnRefNo, serviceName);
//			LOGGER.info("Name of Thread ----------------------- {}", threadWithdrawal.getName());
//			threadWithdrawal.start();

			WalletRequest debitRequest = new WalletRequest(merchantId, charges, "Debit", serviceName, serviceName,
					ekycRequestId, trxnRefId, merchantTrxnRefId, walletTxnRefNo);
			walletService.enqueueTransaction(debitRequest);

			map = accountVerification.bankAccountVerificationRequest(accountNumber, ifsc, merchantTrxnRefId, merchantId,
					serviceId, amtInDouble, serviceName, trxnRefId, ekycRequestId, merchantServiceId, charges);

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
	public boolean checkServiceExistOrNot(long merchantId, String serviceName) {

		ServiceInfo serviceInfo = serviceInfoRepository.findServiceByName(Encryption.encString(serviceName));
		Long serviceId = serviceInfo.getServiceId();

		if (merchantServiceRepository.existsByServiceIdAndMerchantIdAndIsMerchantServiceActive(serviceId, merchantId,
				'Y')) {
			LOGGER.info(" merchantServiceRepository : ");

			return true;
		} else {
			return false;
		}

	}

	@Override
	public synchronized Map<String, Object> accountVerificationPennyless(
			AccountVerificationRequest accountVerificationRequest, long merchantId, double merchantWallet,
			String bussinessName, String email) {
		Map<String, Object> map = new HashMap<>();

		try {
			semaphore.acquire();
			lock.lock();

			String accountNumber = accountVerificationRequest.getBeneficiaryAccNo();
			String ifsc = accountVerificationRequest.getBeneficiaryIfscCode().toUpperCase();
			String merchantTrxnRefId = accountVerificationRequest.getMerchantTrxnRefId();

			String amount = "1.50";
			String serviceName = "Bank Account Verification Penny Less";
			double amtInDouble = Double.parseDouble(amount);

			map = ValidateUtils.accountVerification(accountVerificationRequest);

			if (!map.isEmpty()) {
				return map;
			}

			if (validateBankAccount.verifyIfscCode(ifsc).equals(ResponseMessage.INVALID_IFSCCODE)) {

				map.put("status", "BAD_REQUEST");
				map.put("description", Arrays.asList(ResponseMessage.INVALID_IFSCCODE));
				map.put("code", ResponseMessage.FAILED);
				return map;
			}

			if (ekycTransactionDetailsRepository.existsByMerchantTransactionRefId(merchantTrxnRefId)
					|| ekycTransactionDetailsRepository
							.existsByMerchantTransactionRefId(Encryption.encString(merchantTrxnRefId))) {
				map.put("status", "BAD_REQUEST");
				map.put("description", Arrays.asList(ResponseMessage.MERCHANTTRXNREFID_ALREADY_EXIST));
				map.put("code", ResponseMessage.FAILED);
				return map;
			}

			ServiceInfo serviceInfo = serviceInfoRepository.findServiceByName(Encryption.encString(serviceName));
			Long serviceId = serviceInfo.getServiceId();
			MerchantService merchantsService = merchantServiceRepository.findByMerchantIdAndServiceId(merchantId,
					serviceId);
			Long merchantServiceId = merchantsService.getMerchantServiceId();
			String serviceType = merchantsService.getServiceType();
			LOGGER.info("serviceType: " + serviceType);
			double charges = 0.0;

			switch (serviceType) {

			case "Charge":
				charges = chargeService.getMerchantServiceChargesV2(merchantServiceId, amtInDouble);
				LOGGER.info("charges: " + charges);
				break;

			default:
				charges = amtInDouble;
				break;

			}

			String resWallet = walletNotification.checkWalletBalance(merchantWallet, bussinessName, email);

			if (merchantWallet < charges) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, Arrays.asList(ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE));
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}

			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());
			EkycRequest ekycRequest = ekycRequestRepository
					.save(new EkycRequest(merchantId, accountNumber, accountNumber, trxnDate));
			long ekycRequestId = ekycRequest.getRequestId();
			LOGGER.info("Save ekycRequestId ----------------{}", ekycRequestId);

			String trxnRefId = GenerateTrxnRefId.getTranRefID("API", "Wallet", "Banking");
			String walletTxnRefNo = GenerateTrxnRefId.getTranRefID("API", "Wallet", "");

//			Bank bank = new Bank();
//			ThreadWithdrawal threadWithdrawal = new ThreadWithdrawal(bank, merchantTrxnRefId, charges, merchantWallet,
//					merchantId, merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId, serviceName,
//					trxnRefId, walletTxnRefNo, serviceName);
//			LOGGER.info("Name of Thread ----------------------- {}", threadWithdrawal.getName());
//			threadWithdrawal.start();

			WalletRequest debitRequest = new WalletRequest(merchantId, charges, "Debit", serviceName, serviceName,
					ekycRequestId, trxnRefId, merchantTrxnRefId, walletTxnRefNo);
			walletService.enqueueTransaction(debitRequest);

			map = nsdlServiceImpl.bankAccountVerificationRequestForPennyLess(accountNumber, ifsc, merchantTrxnRefId,
					merchantId, serviceId, amtInDouble, serviceName, trxnRefId, ekycRequestId);

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
	public synchronized Map<String, Object> accountVerificationPennyDrop(NSDLRequest nsdlRequest, long merchantId,
			double merchantWallet, String bussinessName, String email) {

		Map<String, Object> map = new HashMap<>();
		try {
			semaphore.acquire();
			lock.lock();
			String accountNumber = nsdlRequest.getBankAccountNumber();
			String ifsc = nsdlRequest.getBankIFSCCode().toUpperCase();
			String merchantTrxnRefId = nsdlRequest.getMerchantTrxnRefId();

			String amount = "3";
			String serviceName = "Bank Account Verification";
			double amtInDouble = Double.parseDouble(amount);

			if (validateBankAccount.verifyIfscCode(ifsc).equals(ResponseMessage.INVALID_IFSCCODE)) {

				map.put("status", "BAD_REQUEST");
				map.put("description", Arrays.asList(ResponseMessage.INVALID_IFSCCODE));
				map.put("code", ResponseMessage.FAILED);
				return map;
			}

			if (ekycTransactionDetailsRepository.existsByMerchantTransactionRefId(merchantTrxnRefId)
					|| ekycTransactionDetailsRepository
							.existsByMerchantTransactionRefId(Encryption.encString(merchantTrxnRefId))) {
				map.put("status", "BAD_REQUEST");
				map.put("description", Arrays.asList(ResponseMessage.MERCHANTTRXNREFID_ALREADY_EXIST));
				map.put("code", ResponseMessage.FAILED);
				return map;
			}

			ServiceInfo serviceInfo = serviceInfoRepository.findServiceByName(Encryption.encString(serviceName));
			Long serviceId = serviceInfo.getServiceId();
			MerchantService merchantsService = merchantServiceRepository.findByMerchantIdAndServiceId(merchantId,
					serviceId);
			Long merchantServiceId = merchantsService.getMerchantServiceId();
			String serviceType = merchantsService.getServiceType();
			LOGGER.info("serviceType: " + serviceType);
			double charges = 0.0;

			switch (serviceType) {

			case "Charge":
				charges = chargeService.getMerchantServiceChargesV2(merchantServiceId, amtInDouble);
				LOGGER.info("charges: " + charges);
				break;

			default:
				charges = amtInDouble;
				break;

			}

			String resWallet = walletNotification.checkWalletBalance(merchantWallet, bussinessName, email);

			if (merchantWallet < charges) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, Arrays.asList(ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE));
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}

			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());
			EkycRequest ekycRequest = ekycRequestRepository
					.save(new EkycRequest(merchantId, accountNumber, accountNumber, trxnDate));
			long ekycRequestId = ekycRequest.getRequestId();
			LOGGER.info("Save ekycRequestId ----------------{}", ekycRequestId);

			String trxnRefId = GenerateTrxnRefId.getTranRefID("API", "Wallet", "Banking");
			String walletTxnRefNo = GenerateTrxnRefId.getTranRefID("API", "Wallet", "");

//			Bank bank = new Bank();
//			ThreadWithdrawal threadWithdrawal = new ThreadWithdrawal(bank, merchantTrxnRefId, charges, merchantWallet,
//					merchantId, merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId, serviceName,
//					trxnRefId, walletTxnRefNo, serviceName);
//			LOGGER.info("Name of Thread ----------------------- {}", threadWithdrawal.getName());
//			threadWithdrawal.start();

			WalletRequest debitRequest = new WalletRequest(merchantId, charges, "Debit", serviceName, serviceName,
					ekycRequestId, trxnRefId, merchantTrxnRefId, walletTxnRefNo);
			walletService.enqueueTransaction(debitRequest);

			map = nsdlServiceImpl.bankAccountVerificationRequestForPennyDrop(accountNumber, ifsc, merchantTrxnRefId,
					merchantId, serviceId, amtInDouble, serviceName, trxnRefId, ekycRequestId, nsdlRequest);

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
	public synchronized Map<String, Object> bankAccountVerificationPennyDrop(NSDLRequest nsdlRequest, long merchantId,
			double merchantWallet, String bussinessName, String email) {

		Map<String, Object> map = new HashMap<>();

		try {
			semaphore.acquire();
			lock.lock();
			String serviceName = "Bank Account Verification";
			LOGGER.info(" Inside validate ");
			String amount = "3";
			double amtInDouble = Double.parseDouble(amount);

			map = ValidateUtils.accountVerificationPennyDrop(nsdlRequest);

			if (!map.isEmpty()) {
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

			String resWallet = walletNotification.checkWalletBalance(merchantWallet, bussinessName, email);

			if (merchantWallet < charges) {
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
//			ThreadWithdrawal test = new ThreadWithdrawal(bank, merchantTrxnRefId, charges, merchantWallet, merchantId,
//					merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId, serviceName, trxnRefId,
//					walletTxnRefNo, serviceName);
//			LOGGER.info("Name of Thread ----------------------- " + test.getName());
//			test.start();

			WalletRequest debitRequest = new WalletRequest(merchantId, charges, "Debit", serviceName, serviceName,
					ekycRequestId, trxnRefId, merchantTrxnRefId, walletTxnRefNo);
			walletService.enqueueTransaction(debitRequest);

			map = accountVerification.bankAccountVerificationPennyDrop(nsdlRequest);

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
//			LOGGER.info("ELASTICSEARCH DATA INSERTION FOR VERIFICATION PENNY DROP EKYC TRXN DETAILS : {}", elk);

		} catch (Exception e) {
			LOGGER.info("Inside catch block ----------------------- ");
			LOGGER.error("Exception: {}", e);
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			// response = jsonObj.toString();
		} finally {
			lock.unlock();
			semaphore.release();
		}
		return map;
	}

	@Override
	public synchronized Map<String, Object> bankAccountVerificationPennyDropUat(NSDLRequest nsdlRequest,
			long merchantId, double merchantWallet, String bussinessName, String email) {

		Map<String, Object> map = new HashMap<>();

		try {
			semaphore.acquire();
			lock.lock();
			String serviceName = "Bank Account Verification";
			LOGGER.info(" Inside validate ");

			String amount = "3";
			double amtInDouble = Double.parseDouble(amount);

			map = ValidateUtils.accountVerificationPennyDrop(nsdlRequest);

			if (!map.isEmpty()) {
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

			String resWallet = walletNotification.checkWalletBalance(merchantWallet, bussinessName, email);

			if (merchantWallet < charges) {
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
//			ThreadWithdrawal test = new ThreadWithdrawal(bank, merchantTrxnRefId, charges, merchantWallet, merchantId,
//					merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId, serviceName, trxnRefId,
//					walletTxnRefNo, serviceName);
//			LOGGER.info("Name of Thread ----------------------- " + test.getName());
//			test.start();

			WalletRequest debitRequest = new WalletRequest(merchantId, charges, "Debit", serviceName, serviceName,
					ekycRequestId, trxnRefId, merchantTrxnRefId, walletTxnRefNo);
			walletService.enqueueTransaction(debitRequest);

			map = accountVerification.bankAccountVerificationPennyDropUat(nsdlRequest);

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
//			LOGGER.info("ELASTICSEARCH DATA INSERTION FOR VERIFICATION PENNY DROP EKYC TRXN DETAILS : {}", elk);

			return map;
		} catch (Exception e) {
			LOGGER.info("Inside catch block ----------------------- ");
			LOGGER.error("Exception: {}", e);
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			return map;
		} finally {
			lock.unlock();
			semaphore.release();
		}
	}

	@Override
	public synchronized Map<String, Object> accountVerificationPennylessUat(
			AccountVerificationRequest accountVerificationRequest, long merchantId, double merchantWallet,
			String bussinessName, String email) {
		Map<String, Object> map = new HashMap<>();

		try {
			semaphore.acquire();
			lock.lock();

			String accountNumber = accountVerificationRequest.getBeneficiaryAccNo();
			String ifsc = accountVerificationRequest.getBeneficiaryIfscCode().toUpperCase();
			String merchantTrxnRefId = accountVerificationRequest.getMerchantTrxnRefId();

			String amount = "1.50";
			String serviceName = "Bank Account Verification Penny Less";
			double amtInDouble = Double.parseDouble(amount);

			map = ValidateUtils.accountVerification(accountVerificationRequest);

			if (!map.isEmpty()) {
				return map;
			}

			if (validateBankAccount.verifyIfscCode(ifsc).equals(ResponseMessage.INVALID_IFSCCODE)) {

				map.put("status", "BAD_REQUEST");
				map.put("description", Arrays.asList(ResponseMessage.INVALID_IFSCCODE));
				map.put("code", ResponseMessage.FAILED);
				return map;
			}

			if (ekycTransactionDetailsRepository.existsByMerchantTransactionRefId(merchantTrxnRefId)
					|| ekycTransactionDetailsRepository
							.existsByMerchantTransactionRefId(Encryption.encString(merchantTrxnRefId))) {
				map.put("status", "BAD_REQUEST");
				map.put("description", Arrays.asList(ResponseMessage.MERCHANTTRXNREFID_ALREADY_EXIST));
				map.put("code", ResponseMessage.FAILED);
				return map;
			}

			ServiceInfo serviceInfo = serviceInfoRepository.findServiceByName(Encryption.encString(serviceName));
			Long serviceId = serviceInfo.getServiceId();
			MerchantService merchantsService = merchantServiceRepository.findByMerchantIdAndServiceId(merchantId,
					serviceId);
			Long merchantServiceId = merchantsService.getMerchantServiceId();
			String serviceType = merchantsService.getServiceType();
			LOGGER.info("serviceType: " + serviceType);
			double charges = 0.0;

			switch (serviceType) {

			case "Charge":
				charges = chargeService.getMerchantServiceChargesV2(merchantServiceId, amtInDouble);
				LOGGER.info("charges: " + charges);
				break;

			default:
				charges = amtInDouble;
				break;

			}

			String resWallet = walletNotification.checkWalletBalance(merchantWallet, bussinessName, email);

			if (merchantWallet < charges) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, Arrays.asList(ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE));
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}

			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());
			EkycRequest ekycRequest = ekycRequestRepository
					.save(new EkycRequest(merchantId, accountNumber, accountNumber, trxnDate));
			long ekycRequestId = ekycRequest.getRequestId();
			LOGGER.info("Save ekycRequestId ----------------{}", ekycRequestId);

			String trxnRefId = GenerateTrxnRefId.getTranRefID("API", "Wallet", "Banking");
			String walletTxnRefNo = GenerateTrxnRefId.getTranRefID("API", "Wallet", "");

//			Bank bank = new Bank();
//			ThreadWithdrawal threadWithdrawal = new ThreadWithdrawal(bank, merchantTrxnRefId, charges, merchantWallet,
//					merchantId, merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId, serviceName,
//					trxnRefId, walletTxnRefNo, serviceName);
//			LOGGER.info("Name of Thread ----------------------- {}", threadWithdrawal.getName());
//			threadWithdrawal.start();

			WalletRequest debitRequest = new WalletRequest(merchantId, charges, "Debit", serviceName, serviceName,
					ekycRequestId, trxnRefId, merchantTrxnRefId, walletTxnRefNo);
			walletService.enqueueTransaction(debitRequest);

			map = nsdlServiceImpl.bankAccountVerificationRequestForPennyLessUat(accountNumber, ifsc, merchantTrxnRefId,
					merchantId, serviceId, amtInDouble, serviceName, trxnRefId, ekycRequestId);

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
