package com.fidypay.utils.constants;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;

import com.fidypay.ServiceProvider.Signzy.EKYCService;
import com.fidypay.ServiceProvider.Signzy.MerchantServiceChargeService;
import com.fidypay.encryption.Encryption;
import com.fidypay.entity.EkycRequest;
import com.fidypay.entity.EkycTransactionDetails;
import com.fidypay.entity.MerchantInfo;
import com.fidypay.entity.MerchantService;
import com.fidypay.entity.Merchants;
import com.fidypay.entity.ServiceInfo;
import com.fidypay.entity.ServiceProviders;
import com.fidypay.repo.EkycRequestRepository;
import com.fidypay.repo.EkycTransactionDetailsRepository;
import com.fidypay.repo.MerchantInfoRepository;
import com.fidypay.repo.MerchantServiceRepository;
import com.fidypay.repo.MerchantWalletTransactionsRepository;
import com.fidypay.repo.MerchantsRepository;
import com.fidypay.repo.ServiceInfoRepository;
import com.fidypay.repo.ServiceProvidersRepository;
import com.fidypay.utils.ex.DateAndTime;
import com.fidypay.utils.ex.GenerateTrxnRefId;
import com.fidypay.utils.ex.RandomNumberGenrator;
import com.fidypay.wallet.WalletNotification;
import com.fidypay.wallet.WalletRequest;
import com.fidypay.wallet.WalletService;

@Service
public class EkycCommonLogicConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(EkycCommonLogicConfig.class);

	@Autowired
	private MerchantInfoRepository merchantinforepository;

	@Autowired
	private MerchantsRepository merchantsRepository;

	@Autowired
	private EKYCService ekycService;

	@Autowired
	private EkycRequestRepository ekycRequestRepository;

	@Autowired
	private MerchantWalletTransactionsRepository merchantWalletTransactionsRepository;

	@Autowired
	private EkycTransactionDetailsRepository ekycTransactionDetailsRepository;

	@Autowired
	private ServiceProvidersRepository serviceprovidersrepository;

	@Autowired
	private MerchantServiceChargeService chargeService;

	@Autowired
	private ServiceInfoRepository serviceInfoRepository;

	@Autowired
	private MerchantServiceRepository merchantServiceRepository;

	@Autowired
	private WalletNotification walletNotification;

	@Autowired
	private WalletService walletService;

	public Map<String, Object> authenticateUser(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret) {
		Map<String, Object> responseMap = new HashMap<>();

		try {

			String password = AuthenticationVerify.authenticationPassword(Authorization);
			String firstName = AuthenticationVerify.authenticationUsername(Authorization);

			LOGGER.info(" clientSecret: {} ", clientSecret);

			String encryptedClientId = Encryption.encString(clientId);
			String encryptedClientSecret = Encryption.encString(clientSecret);
			String encryptedFirstName = Encryption.encString(firstName);
			String encryptedPassword = Encryption.encString(password);

			MerchantInfo merchantInfo = merchantinforepository.findByClientIdAndClientSecretAndUserNameAndPassword(
					encryptedClientId, encryptedClientSecret, encryptedFirstName, encryptedPassword);

			if (merchantInfo == null || merchantInfo.getIsMerchantActive() != '1') {
				return invalidCredentialsResponse(ResponseMessage.UNAUTHORISED, ResponseMessage.API_STATUS_FAILED,
						ResponseMessage.UNAUTHORISED_DESCRIPTION, ResponseMessage.FIELD_I);
			}

			Optional<Merchants> merchantsOpt = merchantsRepository.findById(merchantInfo.getMerchantId());

			LOGGER.info("merchantsOpt :{}", merchantsOpt.isPresent());

			if (!merchantsOpt.isPresent()) {

				return invalidCredentialsResponse(ResponseMessage.UNAUTHORISED, ResponseMessage.API_STATUS_FAILED,
						ResponseMessage.UNAUTHORISED_DESCRIPTION, ResponseMessage.FIELD_I);

			}

			Merchants merchants = merchantsOpt.get();

			responseMap.put("merchantId", merchantInfo.getMerchantId());
			responseMap.put("merchantFloatAmount", merchants.getMerchantFloatAmount());
			responseMap.put("merchantBusinessName", merchants.getMerchantBusinessName());
			responseMap.put("merchantEmail", merchants.getMerchantEmail());

		} catch (Exception e) {
			e.printStackTrace();
			responseMap = invalidCredentialsResponse(ResponseMessage.UNAUTHORISED, ResponseMessage.API_STATUS_FAILED,
					ResponseMessage.UNAUTHORISED_DESCRIPTION, ResponseMessage.FIELD_I);

		}

		return responseMap;
	}

	public Map<String, Object> checkServiceResponse(long merchantId, String serviceName) {

		Map<String, Object> response = new HashMap<>();

		if (!ekycService.checkServiceExistOrNot(merchantId, serviceName)) {

			return buildResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED,
					ResponseMessage.SERVICEID_NOT_EXIST);

		}

		return response;
	}

	public Map<String, Object> buildResponse(String code, String status, String description) {

		Map<String, Object> response = new HashMap<>();

		response.put(ResponseMessage.CODE, code);
		response.put(ResponseMessage.STATUS, status);
		response.put(ResponseMessage.DESCRIPTION, description);
		return response;
	}

	public Map<String, Object> invalidCredentialsResponse(String code, String status, String description,
			String field) {

		Map<String, Object> response = new HashMap<>();

		response.put(ResponseMessage.CODE, code);
		response.put(ResponseMessage.STATUS, status);
		response.put(ResponseMessage.DESCRIPTION, description);
		response.put(ResponseMessage.FIELD, field);

		return response;
	}

	// Common function to handle shared logic
	public Map<String, Object> handleCommonLogic(long merchantId, Double merchantFloatAmount, String serviceName,
			double amtInDouble, String businessName, String email) throws Exception {

		LOGGER.info("Inside handleCommonLogic:");

		LOGGER.info("merchantId: {}", merchantId);
		LOGGER.info("merchantFloatAmount: {}", merchantFloatAmount);
		LOGGER.info("serviceName: {}", serviceName);
		LOGGER.info("amtInDouble: {}", amtInDouble);

		Map<String, Object> responseMap = new HashMap<>();

		try {

			if (!serviceInfoRepository.existsByServiceName(Encryption.encString(serviceName))) {

				return buildResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED,
						"Service does not exist.");

			}

			if (!ekycService.checkServiceExistOrNot(merchantId, serviceName)) {

				return buildResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED,
						ResponseMessage.SERVICEID_NOT_EXIST);

			}

			// Generate IDs and timestamp
			JSONObject randomId = randomId();
			LOGGER.info("randomId: {}", randomId);

			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			// Fetch service information
			ServiceInfo serviceInfo = serviceInfoRepository.findServiceByName(Encryption.encString(serviceName));

			Long serviceId = serviceInfo.getServiceId();
			LOGGER.info("serviceId: {}", serviceId);

			// Fetch merchant service details
			MerchantService merchantsService = merchantServiceRepository.findByMerchantIdAndServiceId(merchantId,
					serviceId);

			// Calculate charges
			double charges = calculateCharges(merchantsService, amtInDouble);
			LOGGER.info("charges: {}", charges);

			String resWallet = walletNotification.checkWalletBalance(merchantFloatAmount, businessName, email);
			LOGGER.info("resWallet: {}", resWallet);

			if (merchantFloatAmount < charges) {
				return buildResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED,
						ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
			}

			// Save EKYC request
			EkycRequest ekycRequest = saveEkycRequest(merchantId, randomId.getString("merchantTrxnRefId"),
					randomId.getString("walletTxnRefNo"), trxnDate);

			// Start withdrawal thread
			startWithdrawalThread(randomId.getString("merchantTrxnRefId"), charges, merchantFloatAmount, merchantId,
					ekycRequest.getRequestId(), serviceName, randomId.getString("trxnRefId"),
					randomId.getString("walletTxnRefNo"));

			// Prepare response data for the third-party API
			responseMap.put("merchantTrxnRefId", randomId.getString("merchantTrxnRefId"));
			responseMap.put("walletTxnRefNo", randomId.getString("walletTxnRefNo"));
			responseMap.put("trxnRefId", randomId.getString("trxnRefId"));
			responseMap.put("ekycRequestId", ekycRequest.getRequestId());
			responseMap.put("serviceId", serviceId);
			responseMap.put("charges", charges);
			responseMap.put("trxnDate", trxnDate);

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Error in handleCommonLogic: ", e);
			return buildResponse(ResponseMessage.SOMETHING_WENT_WRONG, ResponseMessage.STATUS_FAILED,
					ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		}

		return responseMap;
	}

	private JSONObject randomId() {

		LOGGER.info("Inside randomId:");

		JSONObject jsonObject = new JSONObject();

		String merchantTrxnRefId = RandomNumberGenrator.generateRandomStringRefId()
				+ RandomNumberGenrator.randomNumberGenerate(16);

		String trxnRefId = GenerateTrxnRefId.getTranRefID("API", "Wallet", "EKYC");
		String walletTxnRefNo = GenerateTrxnRefId.getTranRefID("API", "Wallet", "");

		jsonObject.put("merchantTrxnRefId", merchantTrxnRefId);
		jsonObject.put("trxnRefId", trxnRefId);
		jsonObject.put("walletTxnRefNo", walletTxnRefNo);

		return jsonObject;
	}

	private double calculateCharges(MerchantService merchantService, double baseCharge) {

		LOGGER.info("serviceType: {}", merchantService.getServiceType());
		LOGGER.info("merchantServiceId: {}", merchantService.getMerchantServiceId());

		if ("Charge".equalsIgnoreCase(merchantService.getServiceType())) {
			return chargeService.getMerchantServiceChargesV2(merchantService.getMerchantServiceId(), baseCharge);
		}
		return baseCharge;
	}

	// Save ekyc request
	private EkycRequest saveEkycRequest(long merchantId, String merchantTrxnRefId, String walletTxnRefNo,
			Timestamp trxnDate) {

		LOGGER.info("Inside saveEkycRequest:");

		return ekycRequestRepository.save(new EkycRequest(merchantId, merchantTrxnRefId, walletTxnRefNo, trxnDate));
	}

	// Start withdrawal thread
	private void startWithdrawalThread(String merchantTrxnRefId, double charges, double merchantFloatAmount,
			long merchantId, long ekycRequestId, String serviceName, String trxnRefId, String walletTxnRefNo) {

		LOGGER.info("Inside startWithdrawalThread:");

//		Bank bank = new Bank();
//
//		ThreadWithdrawal withdrawalThread = new ThreadWithdrawal(bank, merchantTrxnRefId, charges, merchantFloatAmount,
//				merchantId, merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId, serviceName,
//				trxnRefId, walletTxnRefNo, serviceName);
//
//		withdrawalThread.start();

		WalletRequest debitRequest = new WalletRequest(merchantId, charges, "Debit", serviceName, serviceName,
				ekycRequestId, trxnRefId, merchantTrxnRefId, walletTxnRefNo);
		walletService.enqueueTransaction(debitRequest);

	}

	// Save transaction details
	public void saveTransactionDetails(long ekycRequestId, long merchantId, long merchantServiceId, double charges,
			String serviceName, Timestamp trxnDate, String merchantTrxnRefId, String trxnRefId,
			String serviceProvider) {

		LOGGER.info("Inside saveTransactionDetails:");

		ServiceProviders spInfo = serviceprovidersrepository.findBySpName(Encryption.encString(serviceProvider));

		Long serviceProviderId = spInfo.getServiceProviderId();
		LOGGER.info("serviceProviderId: {}", serviceProviderId);

		ekycTransactionDetailsRepository.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
				ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, 0.0, charges, serviceName, trxnDate,
				merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L, serviceProviderId, '0'));
	}

}
