package com.fidypay.service.impl;

import java.sql.Timestamp;

import javax.validation.Valid;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fidypay.ServiceProvider.Karza.KarzaService;
import com.fidypay.ServiceProvider.Karza.KarzaUtils;
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
import com.fidypay.service.PanKarzaService;
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
public class PanKarzaServiceImpl implements PanKarzaService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PanKarzaServiceImpl.class);

	@Autowired
	private KarzaService karzaService;

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
	private EKYCService ekycService;

	@Autowired
	private ServiceProvidersRepository serviceprovidersrepository;

	@Autowired
	private WalletService walletService;

	@Override
	public String checkPanStatus(@Valid String panNumber, long merchantId, Double merchantFloatAmount) {
		JSONObject jsonObject = new JSONObject();
		String res = null;
		try {
			String serviceName = "OCR EKYC";
			double amtInDouble = 1.0;

			if (!ekycService.checkServiceExistOrNot(merchantId, serviceName)) {

				jsonObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
				res = jsonObject.toString();
				return res;

			}

			if (merchantFloatAmount < amtInDouble) {
				jsonObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
				res = jsonObject.toString();
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

			res = checkPanStatus(panNumber);

			return res;

		} catch (Exception e) {
			jsonObject.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			jsonObject.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			res = jsonObject.toString();
		}
		return res;

	}

	private String checkPanStatus(@Valid String panNumber) {
		JSONObject jsonObject = new JSONObject();
		String res = null;
		try {

			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, "{\"consent\":\"Y\",\"pan\":\"" + panNumber + "\"}");
			Request request = new Request.Builder().url(KarzaUtils.KARZA_API_BASE_URL + "v2/pan").method("POST", body)
					.addHeader("x-karza-key", KarzaUtils.KARZA_KEY).addHeader("Content-Type", "application/json")
					.build();
			Response response = client.newCall(request).execute();

			org.json.JSONObject object = new org.json.JSONObject(response.body().string());

			org.json.JSONObject result = object.getJSONObject("result");

			jsonObject.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			jsonObject.put(ResponseMessage.DESCRIPTION, "pan status details");
			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			jsonObject.put("result", result);
			res = jsonObject.toString();

			return res;

		} catch (Exception e) {
			jsonObject.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			res = jsonObject.toString();
		}
		return res;
	}

	@Override
	public String panProfileDetails(@Valid String panNumber, long merchantId, Double merchantFloatAmount) {
		JSONObject jsonObject = new JSONObject();
		String res = null;
		try {
			String serviceName = "OCR EKYC";
			double amtInDouble = 1.0;

			if (!ekycService.checkServiceExistOrNot(merchantId, serviceName)) {

				jsonObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
				res = jsonObject.toString();
				return res;

			}

			if (merchantFloatAmount < amtInDouble) {
				jsonObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
				res = jsonObject.toString();
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

			res = panProfileDetailsAPI(panNumber);

			return res;

		} catch (Exception e) {
			jsonObject.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			jsonObject.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			res = jsonObject.toString();
		}
		return res;

	}

	private String panProfileDetailsAPI(@Valid String panNumber) {
		JSONObject jsonObject = new JSONObject();
		String res = null;
		try {

			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, "{\"pan\":\"" + panNumber + "\",\"consent\":\"Y\"}");
			Request request = new Request.Builder().url(KarzaUtils.KARZA_API_BASE_URL + "v3/pan-profile")
					.method("POST", body).addHeader("x-karza-key", KarzaUtils.KARZA_KEY)
					.addHeader("Content-Type", "application/json").build();
			Response response = client.newCall(request).execute();

			String rResponse = response.body().string();

			LOGGER.info("request: " + request);

			LOGGER.info("response: " + rResponse);

			org.json.JSONObject object = new org.json.JSONObject(rResponse);

			try {
				org.json.JSONObject result = object.getJSONObject("result");

				LOGGER.info("result: " + result);

				String firstName = result.getString("firstName");

				LOGGER.info("firstName: " + firstName);

				String lastName = result.getString("lastName");
				String middleName = result.getString("middleName");

				String pan = result.getString("pan");
				String dob = result.getString("dob");
				String gender = result.getString("gender");
				boolean aadhaarLinked = result.getBoolean("aadhaarLinked");

				JSONObject object2 = new JSONObject();
				object2.put("firstName", firstName);
				object2.put("lastName", lastName);
				object2.put("middleName", middleName);
				object2.put("name", "NA");
				object2.put("number", pan);

				object2.put("panStatusCode", "NA");
				object2.put("typeOfHolder", "Individual or Person");
				object2.put("isValid", true);
				object2.put("isIndividual", true);

				object2.put("title", "NA");
				object2.put("panStatus", "VALID");
				object2.put("lastUpdatedOn", "NA");

				if (aadhaarLinked == true) {
					object2.put("aadhaarSeedingStatusCode", "Y");
					object2.put("aadhaarSeedingStatus", "Successful");
				} else {
					object2.put("aadhaarSeedingStatusCode", "N");
					object2.put("aadhaarSeedingStatus", "Failed");
				}

				object2.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				object2.put(ResponseMessage.DESCRIPTION, "pan status details");
				object2.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				// jsonObject.put("result", result);
				res = object2.toString();

				return res;

			} catch (Exception e) {
				e.printStackTrace();

				String error = object.getString("error");

				jsonObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObject.put(ResponseMessage.DESCRIPTION, error);
				jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				res = jsonObject.toString();

			}

		} catch (Exception e) {
			e.printStackTrace();
			jsonObject.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			res = jsonObject.toString();
		}
		return res;
	}

	@Override
	public String checkPanAadharLinkStatus(@Valid String panNumber, long merchantId, Double merchantFloatAmount) {
		JSONObject jsonObject = new JSONObject();
		String res = null;
		try {
			String serviceName = "OCR EKYC";
			double amtInDouble = 1.0;

			if (!ekycService.checkServiceExistOrNot(merchantId, serviceName)) {

				jsonObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
				res = jsonObject.toString();
				return res;

			}

			if (merchantFloatAmount < amtInDouble) {
				jsonObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.DEBIT_AMOUNT_NOT_AVAILABLE);
				res = jsonObject.toString();
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

			res = checkPanAadharLinkStatusAPI(panNumber);

			return res;

		} catch (Exception e) {
			e.printStackTrace();
			jsonObject.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			jsonObject.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			res = jsonObject.toString();
		}
		return res;

	}

	private String checkPanAadharLinkStatusAPI(@Valid String panNumber) {
		JSONObject jsonObject = new JSONObject();
		String res = null;
		try {

			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, "{\"consent\":\"Y\",\"pan\":\"" + panNumber + "\"}");
			Request request = new Request.Builder().url(KarzaUtils.KARZA_API_BASE_URL + "v3/pan-link")
					.method("POST", body).addHeader("x-karza-key", KarzaUtils.KARZA_KEY)
					.addHeader("Content-Type", "application/json").build();
			Response response = client.newCall(request).execute();

			org.json.JSONObject object = new org.json.JSONObject(response.body().string());

			org.json.JSONObject result = object.getJSONObject("result");

			jsonObject.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			jsonObject.put(ResponseMessage.DESCRIPTION, "pan status details");
			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			jsonObject.put("result", result);
			res = jsonObject.toString();

			return res;

		} catch (Exception e) {
			e.printStackTrace();
			jsonObject.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			res = jsonObject.toString();
		}
		return res;
	}

}
