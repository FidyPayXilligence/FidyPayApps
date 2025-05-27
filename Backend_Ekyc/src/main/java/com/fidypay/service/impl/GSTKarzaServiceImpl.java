package com.fidypay.service.impl;

import java.sql.Timestamp;

import org.json.simple.JSONObject;
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
import com.fidypay.entity.ServiceInfo;
import com.fidypay.entity.ServiceProviders;
import com.fidypay.repo.EkycRequestRepository;
import com.fidypay.repo.EkycTransactionDetailsRepository;
import com.fidypay.repo.MerchantServiceRepository;
import com.fidypay.repo.MerchantWalletTransactionsRepository;
import com.fidypay.repo.MerchantsRepository;
import com.fidypay.repo.ServiceInfoRepository;
import com.fidypay.repo.ServiceProvidersRepository;
import com.fidypay.service.GSTKarzaService;
import com.fidypay.utils.constants.ResponseMessage;
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
public class GSTKarzaServiceImpl implements GSTKarzaService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MobileNumberAuthenticationServiceImpl.class);

//	@Autowired
//	private KarzaService karzaService;

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
	private WalletService walletService;

	@Override
	public String saveDataForGSTINSearchKarza(String gSTIN, long merchantId, Double merchantFloatAmount) {
		JSONObject object = new JSONObject();
		String res = null;
		try {
			String serviceName = "OCR EKYC";
			double amtInDouble = 1.0;

			if (!ekycService.checkServiceExistOrNot(merchantId, serviceName)) {

				object.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				object.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				object.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
				res = object.toString();
				return res;

			}

			if (merchantFloatAmount < amtInDouble) {
				object.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				object.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				object.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
				res = object.toString();
				return res;
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

			String serviceProvider = "Karza Tech";
			ServiceProviders spInfo = serviceprovidersrepository.findBySpName(Encryption.encString(serviceProvider));
			Long serviceProviderId = spInfo.getServiceProviderId();

			EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
					.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
							ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
							serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
							serviceProviderId, '0'));

			res = gstSearch(gSTIN);

		} catch (Exception e) {
			object.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			object.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			object.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			res = object.toString();
		}
		return res;
	}

	private String gstSearch(String gSTIN) {
		JSONObject object = new JSONObject();
		String res = null;
		try {

			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, "{\"gstin\":\"" + gSTIN + "\",\"consent\":\"Y\"}");
			Request request = new Request.Builder().url("https://api.karza.in/gst/uat/v2/gst-verification")
					.method("POST", body).addHeader("x-karza-key", "NmXy370lZytA27VA")
					.addHeader("Content-Type", "application/json").build();
			Response response = client.newCall(request).execute();

			String r = response.body().string();

			org.json.JSONObject json = new org.json.JSONObject(r);

			Long statusCode = json.getLong("statusCode");

			if (statusCode == 101) {
				org.json.JSONObject result = json.getJSONObject("result");

				object.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				object.put(ResponseMessage.DESCRIPTION, "GSTN details");
				object.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				object.put("result", result);
				res = object.toString();
			} else {
				object.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				object.put(ResponseMessage.DESCRIPTION, "Please pass valid GSTIN Number");
				object.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				res = object.toString();
			}

		} catch (Exception e) {
			object.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			object.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			object.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

			res = object.toString();
		}
		return res;
	}

	@Override
	public String GSTINAuthentication(String gSTIN, long merchantId, Double merchantFloatAmount) {
		JSONObject object = new JSONObject();
		String res = null;
		try {
			String serviceName = "OCR EKYC";
			double amtInDouble = 1.0;

			if (!ekycService.checkServiceExistOrNot(merchantId, serviceName)) {

				object.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				object.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				object.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
				res = object.toString();
				return res;

			}

			if (merchantFloatAmount < amtInDouble) {
				object.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				object.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				object.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
				res = object.toString();
				return res;
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

			String serviceProvider = "Karza Tech";
			ServiceProviders spInfo = serviceprovidersrepository.findBySpName(Encryption.encString(serviceProvider));
			Long serviceProviderId = spInfo.getServiceProviderId();
			EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
					.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
							ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
							serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
							serviceProviderId, '0'));

			res = gstAuthentication(gSTIN);

		} catch (Exception e) {
			object.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			object.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			object.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			res = object.toString();
		}
		return res;

	}

	private String gstAuthentication(String gSTIN) {
		JSONObject object = new JSONObject();
		String res = null;
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

				object.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				object.put(ResponseMessage.DESCRIPTION, "GSTN details");
				object.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);

				object.put("result", result);
				res = object.toString();
			} else {
				object.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				object.put(ResponseMessage.DESCRIPTION, "Please pass valid GSTIN Number");
				object.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);

				res = object.toString();
			}

		} catch (Exception e) {
			object.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			object.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			object.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

			res = object.toString();
		}
		return res;
	}

}
