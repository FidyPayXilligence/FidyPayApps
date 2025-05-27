package com.fidypay.service.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.collections4.map.HashedMap;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fidypay.ServiceProvider.Decentro.DecentroServiceImpl;
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
import com.fidypay.request.DrivingLicenceKarzaRequest;
import com.fidypay.request.DrivingLicenceRequest;
import com.fidypay.request.SearchByDLRequest;
import com.fidypay.service.DrivingLicenceService;
import com.fidypay.utils.constants.ResponseMessage;
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
public class DrivingLicenceServiceImpl implements DrivingLicenceService {

	private static final Logger LOGGER = LoggerFactory.getLogger(DrivingLicenceServiceImpl.class);

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
	private MerchantWalletTransactionsRepository merchantWalletTransactionsRepository;

	@Autowired
	private ServiceProvidersRepository serviceprovidersrepository;

	@Autowired
	private EkycTransactionDetailsRepository ekycTransactionDetailsRepository;

	@Autowired
	private EKYCService ekycService;

	@Autowired
	private DecentroServiceImpl decentroServiceImpl;

	@Autowired
	private WalletService walletService;

//    @Autowired
//    private EkycTransactionDetailsSearchRepository ekycTransactionDetailsSearchRepository;

	public static final String BASE_URL = "https://signzy.tech/api/v2/snoops";

	public final static String DRIVING_LICENSE = "[A-Z]{2}[0-9]{2}[A-Z]{1}[0-9]{4}[0-9]{7}";

	private static final String DOB = "(0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[0-2])-(19|20)\\d{2}";
	private final Semaphore semaphore = new Semaphore(1);
	private final Lock lock = new ReentrantLock();

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> verifyDrivingLicence(DrivingLicenceRequest drivingLicenceRequest) {

		Map<String, Object> mapResponse = new HashMap<>();

		try {

			if (!drivingLicenceRequest.getDlNumber().matches("^[a-zA-Z\\d-]+$")) {

				mapResponse.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				mapResponse.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				mapResponse.put(ResponseMessage.DESCRIPTION, "Invalid dlNumber.");
				return mapResponse;
			}

			Map<String, Object> map = new DrivingLicenceServiceImpl().identityCardObjectForDrivingLicence();
			String accessToken = (String) map.get("accessToken");
			String authorization = (String) map.get("authorization");
			String idIdentity = (String) map.get("idIdentity");

			String requstStr = " {\r\n" + "      \"service\":\"Identity\",\r\n" + "      \"itemId\":\"" + idIdentity
					+ "\",\r\n" + "      \"task\":\"verification\",\r\n" + "      \"accessToken\":\"" + accessToken
					+ "\",\r\n" + "      \"essentials\":{\r\n" + "        \"number\":\""
					+ drivingLicenceRequest.getDlNumber() + "\",\r\n" + "        \"dob\":\""
					+ drivingLicenceRequest.getDob() + "\", \r\n" + "        \"issueDate\":\""
					+ drivingLicenceRequest.getIssueDate() + "\" \r\n" + "      }\r\n" + "    }";

			OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(120, TimeUnit.SECONDS)
					.readTimeout(120, TimeUnit.SECONDS).writeTimeout(120, TimeUnit.SECONDS).build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, requstStr);
			Request request = new Request.Builder().url(BASE_URL).method("POST", body)
					.addHeader("Authorization", authorization).addHeader("Content-Type", "application/json").build();
			Response response = client.newCall(request).execute();

			String results = response.body().string();

			LOGGER.info(" Response:  {}", results);

			ObjectMapper objMapper = new ObjectMapper();
			Map<String, Object> mapObject = objMapper.readValue(results, Map.class);

			Map<String, Object> responseJson = (Map<String, Object>) mapObject.get("response");
			Map<String, Object> resultJson = (Map<String, Object>) responseJson.get("result");

			mapResponse.put("result", resultJson);

		} catch (Exception e) {
			mapResponse.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			mapResponse.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			mapResponse.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return mapResponse;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> dlNumberBasedSearchDrivingLicence(SearchByDLRequest searchByDLRequest) {

		Map<String, Object> mapResponse = new HashMap<>();

		try {
			LOGGER.info("In Service:");

			if (!searchByDLRequest.getDlNumber().matches("^[a-zA-Z\\d-]+$")) {

				mapResponse.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				mapResponse.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				mapResponse.put(ResponseMessage.DESCRIPTION, "Invalid dlNumber.");
				return mapResponse;
			}

			Map<String, Object> map = new DrivingLicenceServiceImpl().identityCardObjectForDrivingLicence();
			String accessToken = (String) map.get("accessToken");
			String authorization = (String) map.get("authorization");
			String idIdentity = (String) map.get("idIdentity");

			String requstStr = "{\r\n" + "    \"service\":\"Identity\",\r\n" + "    \"itemId\":\"" + idIdentity
					+ "\",\r\n" + "    \"task\":\"fetch\",\r\n" + "    \"accessToken\":\"" + accessToken + "\",\r\n"
					+ "    \"essentials\":{\r\n" + "        \"number\": \"" + searchByDLRequest.getDlNumber()
					+ "\",\r\n" + "        \"dob\": \"" + searchByDLRequest.getDob() + "\"\r\n" + "    }\r\n" + "  }";

			OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(120, TimeUnit.SECONDS)
					.readTimeout(120, TimeUnit.SECONDS).writeTimeout(120, TimeUnit.SECONDS).build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, requstStr);
			Request request = new Request.Builder().url(BASE_URL).method("POST", body)
					.addHeader("Authorization", authorization).addHeader("Content-Type", "application/json").build();
			Response response = client.newCall(request).execute();

			LOGGER.info("requstStr:" + requstStr);

			LOGGER.info("request:" + request);

			String results = response.body().string();

			LOGGER.info("results: {}", results);

			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> jsonObjectt = objectMapper.readValue(results, Map.class);

			Map<String, Object> responseJson = (Map<String, Object>) jsonObjectt.get("response");

			if (responseJson != null) {
				Map<String, Object> resultJson = (Map<String, Object>) responseJson.get("result");
				map.put("result", resultJson);
			} else {
				Map<String, Object> error = (Map<String, Object>) jsonObjectt.get("error");

				Long statusCode = (Long) error.get("statusCode");
				String message = (String) error.get("message");
				mapResponse.put("statusCode", statusCode);
				mapResponse.put("message", message);
			}

		} catch (Exception e) {
			mapResponse.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			mapResponse.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			mapResponse.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return mapResponse;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> identityCardObjectForDrivingLicence() throws Exception {

		Map<String, Object> map = new HashMap<>();
		try {
			String responseLogin = new GSTServiceImpl().login(GSTServiceImpl.USERNAME, GSTServiceImpl.PASSWORD);
			LOGGER.info("Login Response " + responseLogin);
			JSONObject jsonObject = new JSONObject(responseLogin);
			String id = jsonObject.getString("id");
			String userId = jsonObject.getString("userId");

			String requestStr = "{\r\n" + "              \"type\": \"drivingLicence\",\r\n"
					+ "              \"email\": \"admin@signzy.com\",\r\n"
					+ "              \"callbackUrl\": \"https://prebuild.com/system\",\r\n"
					+ "              \"images\": [\r\n"
					+ "                \"https://images.unsplash.com/photo-1453728013993-6d66e9c9123a?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8dmlld3xlbnwwfHwwfHw%3D&w=1000&q=80\"\r\n"
					+ "              ]\r\n" + "            }";

			LOGGER.info("requestStr " + requestStr);

			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, requestStr);
			Request request = new Request.Builder().url(GSTServiceImpl.BASE_URL + userId + "/identities")
					.method("POST", body).addHeader("Authorization", id).addHeader("Content-Type", "application/json")
					.build();
			Response response = client.newCall(request).execute();
			String finalResponse = response.body().string();

			LOGGER.info("request " + request);
			LOGGER.info("Identity Response " + finalResponse);

			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> jsonObjectIdentity = objectMapper.readValue(finalResponse, Map.class);

			String idIdentity = (String) jsonObjectIdentity.get("id");
			String accessTokenIdentity = (String) jsonObjectIdentity.get("accessToken");

			LOGGER.info("Inside Identity idIdentity " + idIdentity);
			LOGGER.info("Inside Identity authorization " + accessTokenIdentity);

			map.put("accessToken", accessTokenIdentity);
			map.put("authorization", id);
			map.put("idIdentity", idIdentity);

		} catch (Exception e) {
		}

		return map;
	}

	@Override
	public synchronized Map<String, Object> saveDataForDrivingLicence(
			DrivingLicenceKarzaRequest drivingLicenceKarzaRequest, long merchantId, Double merchantFloatAmount,
			String businessName, String email) {
		Map<String, Object> map = new HashedMap<>();
		try {
			semaphore.acquire();
			lock.lock();

			String serviceName = "DRIVING LICENSE";
//			String serviceName = "Single KYC API";
			String dlNumber = drivingLicenceKarzaRequest.getDlNo().toUpperCase();
			String dob = drivingLicenceKarzaRequest.getDob();
			double amtInDouble = 3.00;

			if (!dlNumber.matches("^[a-zA-Z\\d-]+$")) {

				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Invalid dlNo.");
				return map;
			}

			if (!dob.matches(DOB)) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Invalid dob.");
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

			String resWallet = walletNotification.checkWalletBalance(merchantFloatAmount, businessName, email);

			if (merchantFloatAmount < charges) {
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

//            Bank bank = new Bank();
//            ThreadWithdrawal test = new ThreadWithdrawal(bank, merchantTrxnRefId, charges, merchantFloatAmount,
//                    merchantId, merchantsRepository, merchantWalletTransactionsRepository, ekycRequestId, serviceName,
//                    trxnRefId, walletTxnRefNo, serviceName);
//            LOGGER.info("Name of Thread -----------------------" + test.getName());
//            test.start();

			WalletRequest debitRequest = new WalletRequest(merchantId, charges, "Debit", serviceName, serviceName,
					ekycRequestId, trxnRefId, merchantTrxnRefId, walletTxnRefNo);
			walletService.enqueueTransaction(debitRequest);

			String serviceProvider = ResponseMessage.DECENTRO;
			ServiceProviders spInfo = serviceprovidersrepository.findBySpName(Encryption.encString(serviceProvider));
			Long serviceProviderId = spInfo.getServiceProviderId();

			EkycTransactionDetails ekycTransactionDetails = ekycTransactionDetailsRepository
					.save(new EkycTransactionDetails(ekycRequestId, merchantId, merchantServiceId,
							ResponseMessage.STATUS_SUCCESS, ResponseMessage.STATUS_SUCCESS, commission, charges,
							serviceName, trxnDate, merchantTrxnRefId, trxnRefId, merchantTrxnRefId, 1L,
							serviceProviderId, '0'));

//            EkycTransactionDetails elk = ekycTransactionDetailsSearchRepository.save(ekycTransactionDetails);
//            LOGGER.info("ELASTICSEARCH DATA INSERTION FOR 'DRIVING LICENCE' EKYC TRXN DETAILS : {}", elk);

			map = decentroServiceImpl.verifyDrivingLicense(dlNumber, dob, merchantTrxnRefId);

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
