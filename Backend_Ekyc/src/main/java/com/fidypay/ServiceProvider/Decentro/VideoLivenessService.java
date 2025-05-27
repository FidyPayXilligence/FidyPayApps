package com.fidypay.ServiceProvider.Decentro;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fidypay.ServiceProvider.Karza.ESignServices;
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
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.AmazonClient;
import com.fidypay.utils.ex.DateAndTime;
import com.fidypay.utils.ex.GenerateTrxnRefId;
import com.fidypay.utils.ex.RandomNumberGenrator;
import com.fidypay.wallet.WalletNotification;
import com.fidypay.wallet.WalletRequest;
import com.fidypay.wallet.WalletService;

@Service
public class VideoLivenessService {

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
	private ServiceProvidersRepository serviceprovidersrepository;

	@Autowired
	private EKYCService ekycService;

	@Autowired
	private WalletService walletService;

//	@Autowired
//	private EkycTransactionDetailsSearchRepository ekycTransactionDetailsSearchRepository;

	private static final Logger LOGGER = LoggerFactory.getLogger(VideoLivenessService.class);

	private static final String VIDEO_LIVENESS_URL = "https://in.staging.decentro.tech/v2/kyc/forensics/video_liveness";

	private static final String CLIENT_ID = "fidypay_staging";
	private static final String CLIENT_SECRET = "dRr5IqBh5L4wQ2GYmst5iAxYLcWCLXEN";
	private static final String MODULE_SECRET = "OLD006mfTYAF4Zgco4rZkL0SkIPc6vtN";
	private final Semaphore semaphore = new Semaphore(1);
	private final Lock lock = new ReentrantLock();

	public synchronized Map<String, Object> saveDataForVideoLiveness(MultipartFile video, long merchantId,
			Double merchantFloatAmount, String businessName, String email) {

		LOGGER.info("Inside saveDataForVideoLiveness");

		Map<String, Object> map = new HashMap<>();
		try {
			semaphore.acquire();
			lock.lock();
			String serviceName = "VIDEO LIVENESS";
			double amtInDouble = 7.50;

			if (!serviceInfoRepository.existsByServiceName(Encryption.encString(serviceName))) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Service does not exist.");
				return map;

			}

			if (!ekycService.checkServiceExistOrNot(merchantId, serviceName)) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
				return map;

			}

			if (video.isEmpty()) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SELECT_FILE);
				return map;
			}

			String extension = FilenameUtils.getExtension(video.getOriginalFilename());
			LOGGER.info("extension " + extension);
			if (!extension.equalsIgnoreCase("mp4") && !extension.equalsIgnoreCase("webm")) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.INVALID_VIDEO_FORMAT);
				return map;
			}

			LOGGER.info("file size " + video.getSize());

			if (video.getSize() > 21000000) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.VIDEO_SIZE);
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
			String url = amazonClient.uploadFile(video, merchantId, serviceName);
			LOGGER.info("url " + url);

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

			map = videoLiveness(url, merchantTrxnRefId);

			String serviceProvider = ResponseMessage.DECENTRO;
			ServiceProviders spInfo = serviceprovidersrepository.findBySpName(Encryption.encString(serviceProvider));
			Long serviceProviderId = spInfo.getServiceProviderId();

			EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
					.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
							ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
							serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
							serviceProviderId, '0'));
			LOGGER.info("ekycTransactionDetails: {} ", ekycTransactionDetails);

//			EkycTransactionDetails elk = ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
//			LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'VIDEO LIVENESS' EKYC TRXN DETAILS : {}", elk);
		} catch (Exception e) {
			LOGGER.info("Inside catch block ----------------------- ");
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		} finally {
			lock.unlock();
			semaphore.release();
		}
		return map;
	}

	private Map<String, Object> videoLiveness(String url, String merchantTrxnRefId) {

		Map<String, Object> map = new HashMap<>();

		try {

			MultiValueMap<String, String> builder = new LinkedMultiValueMap<>();
			builder.add("reference_id", merchantTrxnRefId);
			builder.add("consent", "true");
			builder.add("consent_purpose", "For bank account purpose only");
			builder.add("video_url", url);

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			headers.set("client_id", CLIENT_ID);
			headers.set("client_secret", CLIENT_SECRET);
			headers.set("module_secret", MODULE_SECRET);

			HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(builder, headers);
			ResponseEntity<String> response = restTemplate.exchange(VIDEO_LIVENESS_URL, HttpMethod.POST, entity,
					String.class);
			String apiResponse = response.getBody();

			JSONObject resultJsonObject = new JSONObject(apiResponse);

			LOGGER.info("apiResponse: {}", apiResponse);

			String status = resultJsonObject.getString("status");
			String message = null;
			if (status.equals("SUCCESS")) {
				JSONObject data = resultJsonObject.getJSONObject("data");
				data.remove("status");
				message = resultJsonObject.getString("message");
				map = ESignServices.setResponse(ResponseMessage.SUCCESS, ResponseMessage.API_STATUS_SUCCESS, message);
				map.put("merchantTxnRefId", merchantTrxnRefId);
				map.put("Data", data.toMap());
				return map;
			} else {
				JSONObject data = resultJsonObject.getJSONObject("data");
				data.remove("status");
				message = resultJsonObject.getString("message");
				map = ESignServices.setResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED, message);
				map.put("Data", data.toMap());
				map.put("merchantTxnRefId", merchantTrxnRefId);
			}

		} catch (HttpClientErrorException e) {
			String responeString = e.getResponseBodyAsString();
			JSONObject errorJsonObject = new JSONObject(responeString);
			String message = errorJsonObject.getString("message");
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, message);

		} catch (Exception e) {
			LOGGER.error("Exception: {}", e);
			map = ESignServices.setResponse(ResponseMessage.SOMETHING_WENT_WRONG, ResponseMessage.API_STATUS_FAILED,
					ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		}
		return map;
	}

}
