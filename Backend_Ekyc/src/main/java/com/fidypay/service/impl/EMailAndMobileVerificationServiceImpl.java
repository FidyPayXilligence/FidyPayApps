package com.fidypay.service.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fidypay.ServiceProvider.Signzy.EKYCService;
import com.fidypay.ServiceProvider.Signzy.MerchantServiceChargeService;
import com.fidypay.encryption.Encryption;
import com.fidypay.entity.EkycRequest;
import com.fidypay.entity.EkycTransactionDetails;
import com.fidypay.entity.MerchantService;
import com.fidypay.entity.Merchants;
import com.fidypay.entity.OTPVerification;
import com.fidypay.entity.ServiceInfo;
import com.fidypay.entity.ServiceProviders;
import com.fidypay.repo.EkycRequestRepository;
import com.fidypay.repo.EkycTransactionDetailsRepository;
import com.fidypay.repo.MerchantServiceRepository;
import com.fidypay.repo.MerchantWalletTransactionsRepository;
import com.fidypay.repo.MerchantsRepository;
import com.fidypay.repo.OTPVerificationRepository;
import com.fidypay.repo.ServiceInfoRepository;
import com.fidypay.repo.ServiceProvidersRepository;
import com.fidypay.request.CustomerDetailsRequest;
import com.fidypay.service.EMailAndMobileVerificationService;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.DateAndTime;
import com.fidypay.utils.ex.EmailAPIImpl;
import com.fidypay.utils.ex.GenerateTrxnRefId;
import com.fidypay.utils.ex.RandomNumberGenrator;
import com.fidypay.utils.ex.SMSAPIImpl;
import com.fidypay.wallet.WalletNotification;
import com.fidypay.wallet.WalletRequest;
import com.fidypay.wallet.WalletService;

@Service
public class EMailAndMobileVerificationServiceImpl implements EMailAndMobileVerificationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(EMailAndMobileVerificationServiceImpl.class);

	@Autowired
	private WalletNotification walletNotification;

	@Autowired
	private ServiceInfoRepository serviceInfoRepository;

	@Autowired
	private MerchantServiceRepository merchantServiceRepository;

	@Autowired
	private OTPVerificationRepository otpVerificationRepository;

	@Autowired
	private EmailAPIImpl emailAPIImpl;

	@Autowired
	private EKYCService ekycService;

	@Autowired
	private MerchantsRepository merchantsRepository;

	@Autowired
	private EkycRequestRepository ekycRequestRepository;

	@Autowired
	private MerchantWalletTransactionsRepository merchantWalletTransactionsRepository;

	@Autowired
	private MerchantServiceChargeService chargeService;

	@Autowired
	private ServiceProvidersRepository serviceprovidersrepository;

	@Autowired
	private EkycTransactionDetailsRepository ekycTransactionDetailsRepository;

	@Autowired
	private RandomNumberGenrator randomNumberGenrator;

	@Autowired
	private WalletService walletService;

//	@Autowired
//	private EkycTransactionDetailsSearchRepository ekycTransactionDetailsSearchRepository;

	private final Semaphore semaphore = new Semaphore(1);
	private final Lock lock = new ReentrantLock();

	@Override
	public Map<String, Object> otpVerification(String otp, String otpToken, long merchantId) throws Exception {
		Map<String, Object> map = new HashMap<>();

		Optional<OTPVerification> otpVerification = otpVerificationRepository.findOtpANDOtpRefId(merchantId, otp,
				otpToken);

		if (!otpVerification.isPresent()) {

			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.OTP_BANKINFO_FAILED);
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			return map;
		}

		Timestamp currentTime = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

		long transactionTimeValidate = DateAndTime.compareTwoTimeStamps(currentTime,
				otpVerification.get().getCreationDate());

		if (transactionTimeValidate >= 180) {
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.OTP_BANKINFO_EXPIRED);
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			return map;
		}

		map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
		map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
		map.put(ResponseMessage.DESCRIPTION, ResponseMessage.VALID_OTP_MERTRXNREFID);
		map.put("token", otpToken);

		return map;
	}

	@Override
	public synchronized Map<String, Object> sendOTPPhone(CustomerDetailsRequest customerDetailsRequest, long merchantId)
			throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			semaphore.acquire();
			lock.lock();

			String mobile = customerDetailsRequest.getCustomerId();
			String name = customerDetailsRequest.getCustomerName();
			String serviceName = ResponseMessage.MOBILE_NO_VERIFICATION_SERVICE;

			Merchants merchants = merchantsRepository.findById(merchantId).get();
			double merchantWallet = merchants.getMerchantFloatAmount();
			String businessName = merchants.getMerchantBusinessName();
			String email = merchants.getMerchantEmail();

			if (!ekycService.checkServiceExistOrNot(merchantId, serviceName)) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
				return map;

			}

			if (!isValidMobileNo(mobile) || mobile.equals("")) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Please enter your 10 digit mobile number");
				return map;
			}

			String pcOptionName = "Wallet";
			String amount = "1.50";
			double amtInDouble = Double.parseDouble(amount);

			String merchantTrxnRefId = randomNumberGenrator.generateRandomStringRefId()
					+ randomNumberGenrator.randomNumberGenerate(16);

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

			EkycRequest ekycRequest = ekycRequestRepository.save(new EkycRequest(merchantId,
					customerDetailsRequest.getCustomerId(), customerDetailsRequest.getCustomerName(), trxnDate));
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

			String serviceProvider = ResponseMessage.SIGNZY;
			ServiceProviders spInfo = serviceprovidersrepository.findBySpName(Encryption.encString(serviceProvider));
			Long serviceProviderId = spInfo.getServiceProviderId();

			EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
					.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
							ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
							serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
							serviceProviderId, '0'));

//			EkycTransactionDetails elk = ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
//			LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'SEND OTP PHONE' EKYC TRXN DETAILS : {}", elk);

			String otp = RandomNumberGenrator.generateWalletPin();

			// Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());
			String token = "OTPPH" + GenerateTrxnRefId.generateMerchantTrxnRefId() + otp;
			SMSAPIImpl impl = new SMSAPIImpl();
			impl.registrationOTP(mobile, name, otp);

			OTPVerification otpVerification = otpVerificationRepository.save(
					new OTPVerification(merchantId, trxnDate, otp, token, "NA", Encryption.encString("NA"), mobile));

			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, "OTP Generated Successfully");
			map.put("token", token);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
		} finally {
			lock.unlock();
			semaphore.release();
		}
		return map;
	}

	@Override
	public synchronized Map<String, Object> sendOTPEmail(CustomerDetailsRequest customerDetailsRequest, long merchantId)
			throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			semaphore.acquire();
			lock.lock();
			String email = customerDetailsRequest.getCustomerId();
			String name = customerDetailsRequest.getCustomerName();
			String serviceName = ResponseMessage.EMAIL_VERIFICATION_SERVICE;

			Merchants merchants = merchantsRepository.findById(merchantId).get();
			double merchantWallet = merchants.getMerchantFloatAmount();
			String businessName = merchants.getMerchantBusinessName();
			String merchantEmail = merchants.getMerchantEmail();

			if (!ekycService.checkServiceExistOrNot(merchantId, serviceName)) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
				return map;
			}

			if (!isValidEmail(email) || email.equals("")) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Please enter Valid Email id");
				return map;
			}

			String pcOptionName = "Wallet";
			String amount = "1.50";
			double amtInDouble = Double.parseDouble(amount);

			String merchantTrxnRefId = randomNumberGenrator.generateRandomStringRefId()
					+ randomNumberGenrator.randomNumberGenerate(16);

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

			String resWallet = walletNotification.checkWalletBalance(merchantWallet, businessName, merchantEmail);

			if (merchantWallet < charges) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}

			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			EkycRequest ekycRequest = ekycRequestRepository.save(new EkycRequest(merchantId,
					customerDetailsRequest.getCustomerId(), customerDetailsRequest.getCustomerName(), trxnDate));
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

			String serviceProvider = ResponseMessage.SIGNZY;
			ServiceProviders spInfo = serviceprovidersrepository.findBySpName(Encryption.encString(serviceProvider));
			Long serviceProviderId = spInfo.getServiceProviderId();

			EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
					.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
							ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
							serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
							serviceProviderId, '0'));

//			EkycTransactionDetails elk = ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
//			LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'SEND OTP EMAIL' EKYC TRXN DETAILS : {}", elk);

			String otp = RandomNumberGenrator.generateWalletPin();
			// Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());
			String token = "OTPEM" + GenerateTrxnRefId.generateMerchantTrxnRefId() + otp;

			String sendEmail = emailAPIImpl.sendEmail(email, otp);

			OTPVerification otpVerification = otpVerificationRepository.save(
					new OTPVerification(merchantId, trxnDate, otp, token, "NA", Encryption.encString("NA"), token));

			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, "OTP Generated Successfully");
			map.put("token", token);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
		} finally {
			lock.unlock();
			semaphore.release();
		}
		return map;
	}

	private static boolean isValidMobileNo(String str) {
		Pattern ptrn = Pattern.compile("^\\(?([0-9]{3})\\)?[-.\\s]?([0-9]{3})[-.\\s]?([0-9]{4})$");
		Matcher match = ptrn.matcher(str);
		return (match.find() && match.group().equals(str));
	}

	private static boolean isValidEmail(String str) {
		Pattern ptrn = Pattern.compile(
				"^(?=.{1,47}@)[a-zA-Z0-9-_]+(\\.[a-zA-Z0-9-_]+)*@[^-][a-zA-Z0-9]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{2,})$");
		Matcher match = ptrn.matcher(str);
		return (match.find() && match.group().equals(str));
	}
}
