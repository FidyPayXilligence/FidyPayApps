package com.fidypay.service.impl;

import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.validation.constraints.NotNull;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
import com.fidypay.service.FaceMatchingService;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.AmazonClient;
import com.fidypay.utils.ex.DateAndTime;
import com.fidypay.utils.ex.GenerateTrxnRefId;
import com.fidypay.utils.ex.RandomNumberGenrator;
import com.fidypay.wallet.WalletNotification;
import com.fidypay.wallet.WalletRequest;
import com.fidypay.wallet.WalletService;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class FaceMatchingServiceImpl implements FaceMatchingService {

	private static final Logger LOGGER = LoggerFactory.getLogger(FaceMatchingServiceImpl.class);

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
	private AmazonClient amazonClient;

	@Autowired
	private EKYCService ekycService;

	@Autowired
	private ServiceProvidersRepository serviceprovidersrepository;

	@Autowired
	private WalletService walletService;

//	@Autowired
//	private EkycTransactionDetailsSearchRepository ekycTransactionDetailsSearchRepository;

	private static final String FACESIMILARITY = "https://testapi.karza.in/v3/facesimilarity";
	private static final String KEY = "NmXy370lZytA27VA";
	private final Semaphore semaphore = new Semaphore(1);
	private final Lock lock = new ReentrantLock();

	@Override
	public synchronized Map<String, Object> faceMatcher(@NotNull MultipartFile file1, @NotNull MultipartFile file2,
			long merchantId, Double merchantFloatAmount, String businessName, String email) {
		Map<String, Object> map = new HashedMap<>();
		try {
			semaphore.acquire();
			lock.lock();
			String serviceName = "Face Matcher";
			double amtInDouble = 1.0;

			if (file1.isEmpty()) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SELECT_FILE);
				return map;
			}

			if (file2.isEmpty()) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SELECT_FILE);
				return map;
			}

			if (!ekycService.checkServiceExistOrNot(merchantId, serviceName)) {

				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
				return map;

			}

			String extension = FilenameUtils.getExtension(file1.getOriginalFilename());
			LOGGER.info("extension " + extension);
			if (!extension.equalsIgnoreCase("jpeg") && !extension.equalsIgnoreCase("pdf")
					&& !extension.equalsIgnoreCase("jpg") && !extension.equalsIgnoreCase("png")) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.INVALID_FILE_FORMAT);
				return map;
			}

			String extension2 = FilenameUtils.getExtension(file2.getOriginalFilename());
			LOGGER.info("extension " + extension2);
			if (!extension.equalsIgnoreCase("jpeg") && !extension2.equalsIgnoreCase("pdf")
					&& !extension2.equalsIgnoreCase("jpg") && !extension2.equalsIgnoreCase("png")) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.INVALID_FILE_FORMAT);
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
			String url1 = amazonClient.uploadFile(file1, merchantId, serviceName);
			String url2 = amazonClient.uploadFile(file2, merchantId, serviceName);

			LOGGER.info("Key1 " + url1);
			LOGGER.info("Key2 " + url2);
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

			Map<String, Object> res = faceMetcher(url1, url2);

			String serviceProvider = ResponseMessage.KARZA;
			ServiceProviders spInfo = serviceprovidersrepository.findBySpName(Encryption.encString(serviceProvider));
			Long serviceProviderId = spInfo.getServiceProviderId();

			EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
					.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
							ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
							serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
							serviceProviderId, '0'));
			LOGGER.info("ekycTransactionDetails: {}", ekycTransactionDetails);

//			EkycTransactionDetails elk = ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
//			LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'FACE MATCHER' EKYC TRXN DETAILS : {}", elk);

			return res;

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

	private Map<String, Object> faceMetcher(String url1, String url2) {
		Map<String, Object> map = new HashedMap<>();
		try {
			LOGGER.info("url1 " + url1);
			LOGGER.info("url2 " + url2);

			String jsonRequest = "{\r\n" + "	\"url1\": \"" + url1 + "\",\r\n" + "	\"url2\": \"" + url2 + "\"\r\n"
					+ "}";

			LOGGER.info("jsonRequest: " + jsonRequest);

			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, jsonRequest);

			Request request = new Request.Builder().url(FACESIMILARITY).method("POST", body)
					.addHeader("x-karza-key", KEY).addHeader("Content-Type", "application/json").build();

			Response response = client.newCall(request).execute();

			LOGGER.info("request " + request);

			String res = response.body().string();

			LOGGER.info("res " + res);
			JSONObject json;

			json = new JSONObject(res);

			JSONObject result = json.getJSONObject("result");
			String match = result.getString("match");

			if (match.equals("yes")) {

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "Face match sucessfuly");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put("result", result.toMap());

			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Face does not match.");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

			}
		} catch (JSONException e) {
			LOGGER.error("JSONException: {}", e);
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}

		catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;
	}

}
