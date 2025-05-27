package com.fidypay.ServiceProvider.Decentro;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fidypay.ServiceProvider.Karza.KarzaService;
import com.fidypay.ServiceProvider.Signzy.AccountVerification;
import com.fidypay.ServiceProvider.Signzy.SignzyService;
import com.fidypay.request.AccountVerificationRequest;
import com.fidypay.utils.constants.EkycCommonLogicConfig;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.Validations;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class DecentroServiceImplNew {

	@Autowired
	private EkycCommonLogicConfig ekycCommonLogicConfig;

	@Autowired
	private SignzyService signzyService;

	@Autowired
	private KarzaService karzaService;

	private static final Logger LOGGER = LoggerFactory.getLogger(DecentroServiceImplNew.class);

	private final Semaphore semaphore = new Semaphore(10);
	private final Lock lock = new ReentrantLock();

	public synchronized Map<String, Object> saveDataForBasicGst(Map<String, Object> map, String gstNumber)
			throws Exception {

		long merchantId = (long) map.get("merchantId");
		double merchantFloatAmount = (double) map.get("merchantFloatAmount");
		String businessName = (String) map.get("merchantBusinessName");
		String merchantEmail = (String) map.get("merchantEmail");

		double apiFeeAmount = 3.00;
		String serviceName = "GST Number";
		gstNumber = gstNumber.toUpperCase();
		String serviceProvider = ResponseMessage.DECENTRO;

		if (!Validations.isValidGSTNo(gstNumber)) {
			return ekycCommonLogicConfig.buildResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED,
					"Invalid GST");
		}

		return saveDataForAPI(merchantId, merchantFloatAmount, businessName, merchantEmail, gstNumber, serviceName,
				"GSTIN", apiFeeAmount, serviceProvider);
	}

	public synchronized Map<String, Object> saveDataForDetailedGst(Map<String, Object> map, String gstNumber)
			throws Exception {

		long merchantId = (long) map.get("merchantId");
		double merchantFloatAmount = (double) map.get("merchantFloatAmount");
		String businessName = (String) map.get("merchantBusinessName");
		String merchantEmail = (String) map.get("merchantEmail");

		double apiFeeAmount = 3.00;
		String serviceName = "GST Number V2";
		gstNumber = gstNumber.toUpperCase();
		String serviceProvider = ResponseMessage.DECENTRO;

		if (!Validations.isValidGSTNo(gstNumber)) {
			return ekycCommonLogicConfig.buildResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED,
					"Invalid GST");
		}

		return saveDataForAPI(merchantId, merchantFloatAmount, businessName, merchantEmail, gstNumber, serviceName,
				"GSTIN_DETAILED", apiFeeAmount, serviceProvider);
	}

	public synchronized Map<String, Object> saveDataForBasicPan(Map<String, Object> map, String panNumber)
			throws Exception {

		long merchantId = (long) map.get("merchantId");
		double merchantFloatAmount = (double) map.get("merchantFloatAmount");
		String businessName = (String) map.get("merchantBusinessName");
		String merchantEmail = (String) map.get("merchantEmail");

		double apiFeeAmount = 3.00;
		String serviceName = "PAN Card Details";
		panNumber = panNumber.toUpperCase();
		String serviceProvider = ResponseMessage.DECENTRO;

		if (!Validations.isValidPanCardNo(panNumber)) {
			return ekycCommonLogicConfig.buildResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED,
					"Invalid PAN");
		}

		return saveDataForAPI(merchantId, merchantFloatAmount, businessName, merchantEmail, panNumber, serviceName,
				"PAN", apiFeeAmount, serviceProvider);
	}

	public synchronized Map<String, Object> saveDataForDetailedPan(Map<String, Object> map, String panNumber)
			throws Exception {

		long merchantId = (long) map.get("merchantId");
		double merchantFloatAmount = (double) map.get("merchantFloatAmount");
		String businessName = (String) map.get("merchantBusinessName");
		String merchantEmail = (String) map.get("merchantEmail");

		double apiFeeAmount = 3.00;
		String serviceName = "PAN Card Details";
		panNumber = panNumber.toUpperCase();
		String serviceProvider = ResponseMessage.DECENTRO;

		if (!Validations.isValidPanCardNo(panNumber)) {
			return ekycCommonLogicConfig.buildResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED,
					"Invalid PAN");
		}

		return saveDataForAPI(merchantId, merchantFloatAmount, businessName, merchantEmail, panNumber, serviceName,
				"PAN_DETAILED", apiFeeAmount, serviceProvider);
	}

	public synchronized Map<String, Object> saveDataForEmailVerfication(Map<String, Object> map, String emailId)
			throws Exception {

		long merchantId = (long) map.get("merchantId");
		double merchantFloatAmount = (double) map.get("merchantFloatAmount");
		String businessName = (String) map.get("merchantBusinessName");
		String merchantEmail = (String) map.get("merchantEmail");

		double apiFeeAmount = 3.00;
		String serviceName = ResponseMessage.EMAIL_VERIFICATION_SERVICE;
		String serviceProvider = ResponseMessage.DECENTRO;

		if (!Validations.isValidEmail(emailId)) {
			return ekycCommonLogicConfig.buildResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED,
					"Invalid Email");
		}

		return saveDataForAPI(merchantId, merchantFloatAmount, businessName, merchantEmail, emailId, serviceName,
				"EMAIL_VERIFICATION", apiFeeAmount, serviceProvider);
	}

	public synchronized Map<String, Object> saveDataForPanToGst(Map<String, Object> map, String panNo)
			throws Exception {

		long merchantId = (long) map.get("merchantId");
		double merchantFloatAmount = (double) map.get("merchantFloatAmount");
		String businessName = (String) map.get("merchantBusinessName");
		String merchantEmail = (String) map.get("merchantEmail");

		double apiFeeAmount = 3.00;
		String serviceName = "GST VERIFY USING PAN";
		panNo = panNo.toUpperCase();
		String serviceProvider = ResponseMessage.DECENTRO;

		if (!Validations.isValidPanCardNo(panNo)) {
			return ekycCommonLogicConfig.buildResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED,
					"Invalid PAN");
		}

		return saveDataForAPI(merchantId, merchantFloatAmount, businessName, merchantEmail, panNo, serviceName,
				"PAN_TO_GST", apiFeeAmount, serviceProvider);
	}

	public synchronized Map<String, Object> saveDataForPanLinkStatusWithAadhaar(Map<String, Object> map, String panNo)
			throws Exception {

		long merchantId = (long) map.get("merchantId");
		double merchantFloatAmount = (double) map.get("merchantFloatAmount");
		String businessName = (String) map.get("merchantBusinessName");
		String merchantEmail = (String) map.get("merchantEmail");

		double apiFeeAmount = 3.00;
		String serviceName = "PAN Aadhar Link";
		panNo = panNo.toUpperCase();
		String serviceProvider = ResponseMessage.KARZA;

		if (!Validations.isValidPanCardNo(panNo)) {
			return ekycCommonLogicConfig.buildResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED,
					"Invalid PAN");
		}

		return saveDataForAPI(merchantId, merchantFloatAmount, businessName, merchantEmail, panNo, serviceName,
				"PAN_LINK_STATUS", apiFeeAmount, serviceProvider);
	}

	public synchronized Map<String, Object> saveDataForBankAccountVerification(Map<String, Object> map,
			AccountVerificationRequest accountVerificationRequest) throws Exception {

		long merchantId = (long) map.get("merchantId");
		double merchantFloatAmount = (double) map.get("merchantFloatAmount");
		String businessName = (String) map.get("merchantBusinessName");
		String merchantEmail = (String) map.get("merchantEmail");

		double apiFeeAmount = 3.00;
		String serviceName = "Bank Account Verification";
		String serviceProvider = ResponseMessage.KARZA;

		return saveDataForAPI(merchantId, merchantFloatAmount, businessName, merchantEmail, accountVerificationRequest,
				serviceName, "BANK_ACCOUNT_VERIFICATION", apiFeeAmount, serviceProvider);
	}

	public synchronized Map<String, Object> saveDataForImageLivenessVerification(Map<String, Object> map,
			MultipartFile image) throws Exception {

		long merchantId = (long) map.get("merchantId");
		double merchantFloatAmount = (double) map.get("merchantFloatAmount");
		String businessName = (String) map.get("merchantBusinessName");
		String merchantEmail = (String) map.get("merchantEmail");

		double apiFeeAmount = 3.00;
		String serviceName = "Image Liveness";
		String serviceProvider = ResponseMessage.DECENTRO;

		if (image.isEmpty()) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SELECT_FILE);
			return map;
		}

		LOGGER.info("file size {}", image.getSize());

		if (image.getSize() > 6291456) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Maximum file size 6 MB");
			return map;
		}

		String extension = FilenameUtils.getExtension(image.getOriginalFilename());

		LOGGER.info("extension {}", extension);

		if (!extension.equalsIgnoreCase("jpeg") && !extension.equalsIgnoreCase("jpg")
				&& !extension.equalsIgnoreCase("png")) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Invalid file format. Please pass jpg, jpeg, png format");
			return map;
		}

		return saveDataForAPI(merchantId, merchantFloatAmount, businessName, merchantEmail, image, serviceName,
				"IMAGE_LIVENESS", apiFeeAmount, serviceProvider);
	}

	public synchronized Map<String, Object> saveDataForValidateRC(Map<String, Object> map, String vehicleNumber)
			throws Exception {

		long merchantId = (long) map.get("merchantId");
		double merchantFloatAmount = (double) map.get("merchantFloatAmount");
		String businessName = (String) map.get("merchantBusinessName");
		String merchantEmail = (String) map.get("merchantEmail");

		double apiFeeAmount = 3.00;
		String serviceName = ResponseMessage.VEHICLE_REG_SERVICE;
		String serviceProvider = ResponseMessage.DECENTRO;

		return saveDataForAPI(merchantId, merchantFloatAmount, businessName, merchantEmail, vehicleNumber, serviceName,
				"RC", apiFeeAmount, serviceProvider);
	}

	public synchronized Map<String, Object> eAadhar(Map<String, Object> map, String vehicleNumber) throws Exception {

		long merchantId = (long) map.get("merchantId");
		double merchantFloatAmount = (double) map.get("merchantFloatAmount");
		String businessName = (String) map.get("merchantBusinessName");
		String merchantEmail = (String) map.get("merchantEmail");

		double apiFeeAmount = 3.00;
		String serviceName = "EAdhar";
		String serviceProvider = ResponseMessage.DECENTRO;

		return saveDataForAPI(merchantId, merchantFloatAmount, businessName, merchantEmail, vehicleNumber, serviceName,
				"EAdhar", apiFeeAmount, serviceProvider);
	}

	public synchronized Map<String, Object> getInitiateSession(Map<String, Object> map) throws Exception {

		long merchantId = (long) map.get("merchantId");
		double merchantFloatAmount = (double) map.get("merchantFloatAmount");
		String businessName = (String) map.get("merchantBusinessName");
		String merchantEmail = (String) map.get("merchantEmail");

		double apiFeeAmount = 3.00;
		String serviceName = "EAdhar";
		String serviceProvider = ResponseMessage.DECENTRO;

		return saveDataForAPI(merchantId, merchantFloatAmount, businessName, merchantEmail, "vehicleNumber",
				serviceName, "INITIATE_SESSION", apiFeeAmount, serviceProvider);
	}

	private synchronized Map<String, Object> saveDataForAPI(long merchantId, Double merchantFloatAmount,
			String businessName, String email, Object idNumber, String serviceName, String documentType,
			double apiFeeAmount, String serviceProvider) throws Exception {

		LOGGER.info("Inside saveDataForAPI:");

		semaphore.acquire();
		lock.lock();

		// Call common logic
		Map<String, Object> commonLogicResponse = ekycCommonLogicConfig.handleCommonLogic(merchantId,
				merchantFloatAmount, serviceName, apiFeeAmount, businessName, email);

		if (commonLogicResponse.get(ResponseMessage.CODE) != null) {
			return commonLogicResponse;
		}

		try {
			// Extract necessary data
			String merchantTrxnRefId = (String) commonLogicResponse.get("merchantTrxnRefId");
			// String walletTxnRefNo = (String) commonLogicResponse.get("walletTxnRefNo");
			String trxnRefId = (String) commonLogicResponse.get("trxnRefId");
			Timestamp trxnDate = (Timestamp) commonLogicResponse.get("trxnDate");
			Long ekycRequestId = (Long) commonLogicResponse.get("ekycRequestId");
			Long serviceId = (Long) commonLogicResponse.get("serviceId");
			double charges = (double) commonLogicResponse.get("charges");

			// API-specific logic (calling the Third party API)
			Map<String, Object> apiResponse = new HashMap<>();

			switch (documentType) {

			case "GSTIN":

				switch (serviceProvider) {

				case ResponseMessage.DECENTRO:
					apiResponse = parseBasicGstResponse(idNumber, documentType, merchantTrxnRefId);
					break;
				case ResponseMessage.KARZA:

					break;
				case ResponseMessage.SIGNZY:
					apiResponse = signzyService.gstinSearch(idNumber.toString(), merchantTrxnRefId);
					break;

				default:
					break;
				}

				break;

			case "GSTIN_DETAILED":

				switch (serviceProvider) {
				case ResponseMessage.DECENTRO:
					apiResponse = parseDetailGstResponse(idNumber, documentType, merchantTrxnRefId);
					break;
				case ResponseMessage.KARZA:

					break;
				case ResponseMessage.SIGNZY:

					break;

				default:
					break;
				}

				break;

			case "PAN":

				switch (serviceProvider) {

				case ResponseMessage.DECENTRO:
					apiResponse = parseBasicPanResponse(idNumber, documentType, merchantTrxnRefId);
					break;
				case ResponseMessage.KARZA:

					break;
				case ResponseMessage.SIGNZY:

					apiResponse = signzyService.fetchPanV2(idNumber.toString());

					break;

				default:
					break;
				}

				break;

			case "PAN_DETAILED":

				switch (serviceProvider) {

				case ResponseMessage.DECENTRO:
					apiResponse = parseDetailPanResponse(idNumber, documentType, merchantTrxnRefId);
					break;
				case ResponseMessage.KARZA:

					break;
				case ResponseMessage.SIGNZY:

					break;

				default:
					break;
				}

				break;

			case "EMAIL_VERIFICATION":

				switch (serviceProvider) {

				case ResponseMessage.DECENTRO:
					apiResponse = parseEmailVerificationResponse(idNumber, merchantTrxnRefId);
					break;
				case ResponseMessage.KARZA:

					break;
				case ResponseMessage.SIGNZY:

					break;

				default:
					break;
				}

				break;

			case "PAN_TO_GST":

				switch (serviceProvider) {

				case ResponseMessage.DECENTRO:
					apiResponse = parsePenToGstResponse(idNumber, merchantTrxnRefId);
					break;
				case ResponseMessage.KARZA:

					break;
				case ResponseMessage.SIGNZY:

					break;

				default:
					break;
				}

				break;

			case "PAN_LINK_STATUS":

				switch (serviceProvider) {

				case ResponseMessage.DECENTRO:

					break;
				case ResponseMessage.KARZA:
					apiResponse = karzaService.checkPanAadharLinkStatusAPI(idNumber.toString());
					break;
				case ResponseMessage.SIGNZY:

					break;

				default:
					break;
				}

				break;

			case "BANK_ACCOUNT_VERIFICATION":

				switch (serviceProvider) {

				case ResponseMessage.DECENTRO:
					apiResponse = null;
					break;
				case ResponseMessage.KARZA:
					apiResponse = parseBankAccountVerificationResponse(idNumber);
					break;
				case ResponseMessage.SIGNZY:
					apiResponse = parseBankAccountVerificationResponseSignzy(idNumber);
					break;

				default:
					break;
				}

				break;

			case "IMAGE_LIVENESS":

				switch (serviceProvider) {

				case ResponseMessage.DECENTRO:
					apiResponse = parseImageLivenessResponse(idNumber, merchantTrxnRefId);
					break;
				case ResponseMessage.KARZA:

					break;
				case ResponseMessage.SIGNZY:

					break;

				default:
					break;
				}

			case "RC":

				switch (serviceProvider) {

				case ResponseMessage.DECENTRO:
					apiResponse = parseRCVerification(idNumber, documentType, merchantTrxnRefId);
					break;
				case ResponseMessage.KARZA:

					break;
				case ResponseMessage.SIGNZY:

					break;

				default:
					break;
				}

				break;

			case "EAdhar":

				switch (serviceProvider) {

				case ResponseMessage.DECENTRO:
					apiResponse = parseEAdharVerification(idNumber, documentType, merchantTrxnRefId);
					break;
				case ResponseMessage.KARZA:

					break;
				case ResponseMessage.SIGNZY:

					break;

				default:
					break;
				}

				break;

			case "INITIATE_SESSION":

				switch (serviceProvider) {

				case ResponseMessage.DECENTRO:
					apiResponse = parseInitiateSession(merchantTrxnRefId);
					break;
				case ResponseMessage.KARZA:

					break;
				case ResponseMessage.SIGNZY:

					break;

				default:
					break;
				}

				break;

			default:
				apiResponse = ekycCommonLogicConfig.buildResponse(ResponseMessage.SOMETHING_WENT_WRONG,
						ResponseMessage.STATUS_FAILED, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
				break;
			}

			// Handle transaction details
			ekycCommonLogicConfig.saveTransactionDetails(ekycRequestId, merchantId, serviceId, charges, serviceName,
					trxnDate, merchantTrxnRefId, trxnRefId, serviceProvider);

			return apiResponse;

		} catch (Exception e) {
			e.printStackTrace();
			return ekycCommonLogicConfig.buildResponse(ResponseMessage.SOMETHING_WENT_WRONG,
					ResponseMessage.STATUS_FAILED, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		} finally {
			semaphore.release();
			lock.unlock();
		}
	}

	private Map<String, Object> parseInitiateSession(String merchantTrxnRefId) {

		LOGGER.info("Inside parseInitiateSession:");

		Map<String, Object> map = new HashMap<>();

		String responseBody = "";
		try {

			String apiRequest = "{\r\n" + "  \"consent\": true,\r\n" + "  \"redirect_to_signup\": false,\r\n"
					+ "  \"abstract_access_token\": true,\r\n" + "  \"reference_id\": \"" + merchantTrxnRefId
					+ "\",\r\n" + "  \"consent_purpose\": \"for banking purpose only\",\r\n"
					+ "  \"redirect_url\": \"https://www.fidypay.com\"\r\n" + "}";

			LOGGER.info("ApiRequest: {}", apiRequest);

			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, apiRequest);

			Request request = new Request.Builder()
					.url(DecentroUtils.DECENTRO_API_BASE_URL_UAT + "v2/kyc/digilocker/initiate_session")
					.method("POST", body).addHeader("client_id", DecentroUtils.DECENTRO_CLIENT_ID_UAT)
					.addHeader("client_secret", DecentroUtils.DECENTRO_CLIENT_SECRET_UAT)
					.addHeader("module_secret", DecentroUtils.DECENTRO_MODULE_SECRET_UAT)
					.addHeader("Content-Type", "application/json").build();

			Response response = client.newCall(request).execute();
			responseBody = response.body().string();

			LOGGER.info("ApiResponse: {}", responseBody);

			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> mapObject = objectMapper.readValue(responseBody, Map.class);

			String result = (String) mapObject.get("status");
			String message = (String) mapObject.get("message");
			String merchantTxnId = (String) mapObject.get("decentroTxnId");

			if (result != null && result.contains("SUCCESS")) {
				Map<String, Object> data = (Map<String, Object>) mapObject.get("data");
				map.put("code", "0x200");
				map.put("merchantTxnId", merchantTxnId);
				map.put("description", message);
				map.put("merchantData", data);

			} else if (message.equals("Your IP address is not allowed")) {
				map.put("message", message);
			} else {

				map.put("code", "0x202");
				map.put("description", message);
				map.put("merchantTxnId", merchantTxnId);

			}
		} catch (IOException e) {
			LOGGER.error("IOException: {}", e);
		} catch (Exception e) {
			LOGGER.error("Exception: {}", e);
		}

		return map;
	}

	private Map<String, Object> parseEAdharVerification(Object idNumber, String documentType,
			String merchantTrxnRefId) {

		LOGGER.info("Inside parseEAdharVerification:");

		Map<String, Object> responseMap = new HashMap<>();
		try {

			String docId = (String) idNumber;

			String eAdharResponse = eAadhar(docId, documentType, merchantTrxnRefId);

			LOGGER.info("eAdharResponse: {}", eAdharResponse);

			JSONObject resultJsonObject = new JSONObject(eAdharResponse);

			String status = resultJsonObject.getString("status");
			String responseCode = resultJsonObject.getString("responseCode");

			if (status.equalsIgnoreCase("SUCCESS") && responseCode.equalsIgnoreCase("S00000")) {

				JSONObject data = resultJsonObject.getJSONObject("data");
				data.remove("image");
				data.remove("pdf");

				responseMap = ekycCommonLogicConfig.buildResponse(ResponseMessage.SUCCESS,
						ResponseMessage.API_STATUS_SUCCESS, ResponseMessage.DATA_FOUND);

				responseMap.put(ResponseMessage.DATA, data.toMap());

				return responseMap;

			} else {

				responseMap = ekycCommonLogicConfig.buildResponse(ResponseMessage.FAILED,
						ResponseMessage.API_STATUS_FAILED, ResponseMessage.DATA_NOT_FOUND);

				return responseMap;

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return responseMap;
	}

	private Map<String, Object> parseRCVerification(Object idNumber, String documentType, String merchantTrxnRefId) {

		LOGGER.info("Inside parseRCVerification:");

		Map<String, Object> responseMap = new HashMap<>();
		try {

			String docId = (String) idNumber;

			String rcVerificationResponse = validateDocument(docId, documentType, merchantTrxnRefId);

			LOGGER.info("rcVerificationResponse: {}", rcVerificationResponse);

			JSONObject resultJsonObject = new JSONObject(rcVerificationResponse);

			String status = resultJsonObject.getString("status");
			String responseCode = resultJsonObject.getString("responseCode");

			if (status.equalsIgnoreCase("SUCCESS") && responseCode.equalsIgnoreCase("S00000")) {

				JSONObject kycResult = resultJsonObject.getJSONObject("kycResult");
				kycResult.put("vehicleStatus", kycResult.get("status"));
				kycResult.remove("status");

				responseMap = ekycCommonLogicConfig.buildResponse(ResponseMessage.SUCCESS,
						ResponseMessage.API_STATUS_SUCCESS, ResponseMessage.DATA_FOUND);

				responseMap.put(ResponseMessage.DATA, kycResult.toMap());

				return responseMap;

			} else {

				responseMap = ekycCommonLogicConfig.buildResponse(ResponseMessage.FAILED,
						ResponseMessage.API_STATUS_FAILED, ResponseMessage.DATA_NOT_FOUND);

				return responseMap;

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return responseMap;
	}

	private Map<String, Object> parseImageLivenessResponse(Object idNumber, String merchantTrxnRefId) {

		LOGGER.info("Inside parseImageLivenessResponse:");

		Map<String, Object> responseMap = new HashMap<>();
		try {
			MultipartFile image = (MultipartFile) idNumber;

			byte[] fileBytes = IOUtils.toByteArray(image.getInputStream());
			String base64Image = Base64.getEncoder().encodeToString(fileBytes);
			String extension = FilenameUtils.getExtension(image.getOriginalFilename());

			String imageLivenessResponse = imageLiveness(base64Image, merchantTrxnRefId, extension);

			LOGGER.info("imageLivenessResponse: {}", imageLivenessResponse);

			JSONObject resultJsonObject = new JSONObject(imageLivenessResponse);

			String status = resultJsonObject.getString("status");
			String responseCode = resultJsonObject.getString("responseCode");

			if (status.equalsIgnoreCase("SUCCESS") && responseCode.equalsIgnoreCase("S00000")) {

				JSONObject dataJson = resultJsonObject.getJSONObject("data");
				dataJson.put("imageStatus", dataJson.get("status"));
				dataJson.remove("status");

				responseMap = ekycCommonLogicConfig.buildResponse(ResponseMessage.SUCCESS,
						ResponseMessage.API_STATUS_SUCCESS, ResponseMessage.DATA_FOUND);

				responseMap.put(ResponseMessage.DATA, dataJson.toMap());

				return responseMap;

			} else {

				responseMap = ekycCommonLogicConfig.buildResponse(ResponseMessage.FAILED,
						ResponseMessage.API_STATUS_FAILED, ResponseMessage.DATA_NOT_FOUND);

				return responseMap;

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return responseMap;
	}

	private String imageLiveness(String image, String merchantTrxnRefId, String extension) {

		LOGGER.info("Inside imageLiveness:");

		String apiRequest = "{\r\n" + "  \"consent\": true,\r\n" + "  \"image\": \"" + image + "\",\r\n"
				+ "  \"reference_id\": \"" + merchantTrxnRefId + "\",\r\n"
				+ "  \"purpose\": \"For the baking purpose we testing\"\r\n" + "}";

		LOGGER.info("apiRequest: {}", apiRequest);

		String apiResponse = null;

		try {

			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("image/" + extension);

			MultipartBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
					.addFormDataPart("consent", "true")
					.addFormDataPart("image", "image." + extension, RequestBody.create(mediaType, image))
					.addFormDataPart("reference_id", merchantTrxnRefId)
					.addFormDataPart("purpose", "For the baking purpose we testing").build();

			Request request = new Request.Builder()
					.url("https://in.staging.decentro.tech/v3/kyc/farsight/liveness/passive").method("POST", body)
					.addHeader("accept", "application/json")
					.addHeader("client_id", DecentroUtils.DECENTRO_CLIENT_ID_UAT)
					.addHeader("client_secret", DecentroUtils.DECENTRO_CLIENT_SECRET_UAT)
					.addHeader("module_secret", DecentroUtils.DECENTRO_MODULE_SECRET_UAT).build();
			Response response = client.newCall(request).execute();

			apiResponse = response.body().string();

			LOGGER.info("apiResponse: {}", apiResponse);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return apiResponse;
	}

	private Map<String, Object> parseBankAccountVerificationResponseSignzy(Object idNumber) {

		LOGGER.info("Inside parseBankAccountVerificationResponseSignzy:");

		Map<String, Object> responseMap = new HashMap<>();

		AccountVerificationRequest request = (AccountVerificationRequest) idNumber;
		String beneficiaryAccNo = request.getBeneficiaryAccNo();
		String beneficiaryIfscCode = request.getBeneficiaryIfscCode().toUpperCase();
		String merchantTrxnRefId = request.getMerchantTrxnRefId();
		try {

			Map<String, Object> verificationResponse = new AccountVerification()
					.bankAccountVerification(beneficiaryAccNo, beneficiaryIfscCode, merchantTrxnRefId);

			String resposne = (String) verificationResponse.get("results");
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> mapResponse = objectMapper.readValue(resposne, Map.class);

			Map<String, Object> essentials = (Map<String, Object>) mapResponse.get("essentials");
			String beneficiaryAccountRes = (String) essentials.get("beneficiaryAccount");
			String remarks = (String) essentials.get("remarks");

			Map<String, Object> result = (Map<String, Object>) mapResponse.get("result");

			String active = (String) result.get("active");
			String reason = (String) result.get("reason");
			if (active.equals("yes")) {

				Map<String, Object> bankTransfer = (Map<String, Object>) result.get("bankTransfer");
				String beneName = (String) bankTransfer.get("beneName");
				String beneIFSC = (String) bankTransfer.get("beneIFSC");
				String bankRRN = (String) bankTransfer.get("bankRRN");

				responseMap = ekycCommonLogicConfig.buildResponse(ResponseMessage.SUCCESS,
						ResponseMessage.API_STATUS_SUCCESS, "Account verified successfully");

				responseMap.put("beneficiaryAccNo", beneficiaryAccountRes);
				responseMap.put("merchantTrxnRefId", remarks);
				responseMap.put("active", active);
				responseMap.put("beneficiaryName", beneName);
				responseMap.put("beneficiaryIfscCode", beneIFSC);
				responseMap.put("utr", bankRRN);

				return responseMap;

			} else {

				responseMap = ekycCommonLogicConfig.buildResponse(ResponseMessage.FAILED,
						ResponseMessage.API_STATUS_FAILED, "Your Account is not verified.");
				responseMap.put("beneficiaryAccNo", beneficiaryAccNo);
				responseMap.put("merchantTrxnRefId", merchantTrxnRefId);
				responseMap.put("active", "No");

				return responseMap;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return responseMap;

	}

	private Map<String, Object> parseBankAccountVerificationResponse(Object idNumber) {

		LOGGER.info("Inside parseBankAccountVerificationResponse:");

		Map<String, Object> responseMap = new HashMap<>();

		AccountVerificationRequest request = (AccountVerificationRequest) idNumber;
		String beneficiaryAccNo = request.getBeneficiaryAccNo();
		String beneficiaryIfscCode = request.getBeneficiaryIfscCode().toUpperCase();
		String merchantTrxnRefId = request.getMerchantTrxnRefId();

		try {

			String bankAccountVerificationResponse = bankAccountVerification(beneficiaryAccNo, beneficiaryIfscCode);

			LOGGER.info("bankAccountVerificationResponse: {}", bankAccountVerificationResponse);

			JSONObject resultJsonObject = new JSONObject(bankAccountVerificationResponse);

			JSONObject kycResult = new JSONObject();

			String statusCode = String.valueOf(resultJsonObject.getLong("status-code"));

			if (statusCode.equalsIgnoreCase("101")) {

				JSONObject resultJson = resultJsonObject.getJSONObject("result");

				kycResult.put("utr", "NA");
				kycResult.put("beneficiaryAccNo", resultJson.get("accountName"));
				kycResult.put("beneficiaryName", resultJson.get("accountName"));
				kycResult.put("active", resultJson.get("bankTxnStatus"));
				kycResult.put("beneficiaryIfscCode", resultJson.get("ifsc"));
				kycResult.put("bankResponse", resultJson.get("bankResponse"));
				kycResult.put("merchantTrxnRefId", merchantTrxnRefId);

				responseMap = ekycCommonLogicConfig.buildResponse(ResponseMessage.SUCCESS,
						ResponseMessage.API_STATUS_SUCCESS, "Account verified successfully");

				responseMap.put(ResponseMessage.DATA, kycResult.toMap());

				return responseMap;

			} else {

				responseMap = ekycCommonLogicConfig.buildResponse(ResponseMessage.FAILED,
						ResponseMessage.API_STATUS_FAILED, "Your Account is not verified.");
				responseMap.put("beneficiaryAccNo", beneficiaryAccNo);
				responseMap.put("merchantTrxnRefId", merchantTrxnRefId);
				responseMap.put("active", "No");

				return responseMap;
			}

		} catch (HttpClientErrorException e) {
			responseMap = ekycCommonLogicConfig.buildResponse(ResponseMessage.SOMETHING_WENT_WRONG,
					ResponseMessage.STATUS_FAILED, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Exception: {}", e);
			responseMap = ekycCommonLogicConfig.buildResponse(ResponseMessage.SOMETHING_WENT_WRONG,
					ResponseMessage.STATUS_FAILED, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		}
		return responseMap;
	}

	private String bankAccountVerification(String beneficiaryAccNo, String beneficiaryIfscCode) {

		LOGGER.info("Inside bankAccountVerification:");

		String apiRequest = "{\r\n" + "    \"consent\": \"Y\",\r\n" + "    \"ifsc\": \"" + beneficiaryIfscCode
				+ "\",\r\n" + "    \"accountNumber\": \"" + beneficiaryAccNo + "\"\r\n" + "}";

		LOGGER.info("apiRequest: {}", apiRequest);

		String apiResponse = null;

		try {

			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, apiRequest);
			Request request = new Request.Builder().url("https://testapi.karza.in/v2/bankacc").method("POST", body)
					.addHeader("x-karza-key", "NmXy370lZytA27VA").addHeader("Content-Type", "application/json").build();
			Response response = client.newCall(request).execute();

			apiResponse = response.body().string();

			LOGGER.info("apiResponse: {}", apiResponse);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return apiResponse;
	}

	private Map<String, Object> parsePenToGstResponse(Object panNo, String merchantTrxnRefId) {

		LOGGER.info("Inside parsePenToGstResponse:");

		Map<String, Object> responseMap = new HashMap<>();

		String docId = (String) panNo;

		try {

			String penToGstVerificationResponse = penToGstVerification(docId, merchantTrxnRefId);

			LOGGER.info("penToGstVerificationResponse: {}", penToGstVerificationResponse);

			JSONObject resultJsonObject = new JSONObject(penToGstVerificationResponse);

			String status = resultJsonObject.getString("status");
			String responseCode = resultJsonObject.getString("responseCode");

			if (status.equalsIgnoreCase("SUCCESS") && responseCode.equalsIgnoreCase("S00000")) {

				JSONObject dataJson = resultJsonObject.getJSONObject("data");

				responseMap = ekycCommonLogicConfig.buildResponse(ResponseMessage.SUCCESS,
						ResponseMessage.API_STATUS_SUCCESS, ResponseMessage.DATA_FOUND);

				responseMap.put(ResponseMessage.DATA, dataJson.toMap());

				return responseMap;

			} else {

				responseMap = ekycCommonLogicConfig.buildResponse(ResponseMessage.FAILED,
						ResponseMessage.API_STATUS_FAILED, ResponseMessage.DATA_NOT_FOUND);

				return responseMap;

			}

		} catch (HttpClientErrorException e) {
			responseMap = ekycCommonLogicConfig.buildResponse(ResponseMessage.SOMETHING_WENT_WRONG,
					ResponseMessage.STATUS_FAILED, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Exception: {}", e);
			responseMap = ekycCommonLogicConfig.buildResponse(ResponseMessage.SOMETHING_WENT_WRONG,
					ResponseMessage.STATUS_FAILED, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		}
		return responseMap;
	}

	private Map<String, Object> parseEmailVerificationResponse(Object email, String merchantTrxnRefId) {

		LOGGER.info("Inside parseDetailEmailVerificationResponse:");

		Map<String, Object> responseMap = new HashMap<>();

		String docId = (String) email;

		try {

			String emailVerificationResponse = emailVerification(docId, merchantTrxnRefId);

			LOGGER.info("emailVerificationResponse: {}", emailVerificationResponse);

			JSONObject resultJsonObject = new JSONObject(emailVerificationResponse);

			String responseCode = resultJsonObject.getString("responseCode");
			String status = resultJsonObject.getString("status");

			String messsage = "NA";

			if (resultJsonObject.has("message")) {

				messsage = resultJsonObject.getString("message");
			}

			if (responseCode.equalsIgnoreCase("S00000") && status.equalsIgnoreCase("SUCCESS")) {

				JSONObject data = resultJsonObject.getJSONObject("data");

				responseMap = ekycCommonLogicConfig.buildResponse(ResponseMessage.SUCCESS,
						ResponseMessage.API_STATUS_SUCCESS, messsage);

				responseMap.put(ResponseMessage.DATA, data.toMap());

				return responseMap;

			} else if (responseCode.equalsIgnoreCase("E00009") && status.equalsIgnoreCase("FAILURE")) {

				return ekycCommonLogicConfig.buildResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED,
						messsage);
			} else {

				return ekycCommonLogicConfig.buildResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED,
						"Invalid email id, Please try again later.");

			}

		} catch (HttpClientErrorException e) {
			responseMap = ekycCommonLogicConfig.buildResponse(ResponseMessage.SOMETHING_WENT_WRONG,
					ResponseMessage.STATUS_FAILED, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Exception: {}", e);
			responseMap = ekycCommonLogicConfig.buildResponse(ResponseMessage.SOMETHING_WENT_WRONG,
					ResponseMessage.STATUS_FAILED, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		}
		return responseMap;
	}

	private String penToGstVerification(String panNo, String merchantTrxnRefId) {

		LOGGER.info("Inside emailVerification:");

		String apiRequest = "{\r\n" + "  \"consent\": true,\r\n"
				+ "  \"purpose\": \"To perform KYC of the individual\",\r\n" + "  \"reference_id\": \""
				+ merchantTrxnRefId + "\",\r\n" + "  \"pan\": \"" + panNo + "\"\r\n" + "}";

		LOGGER.info("apiRequest: {}", apiRequest);

		String apiResponse = null;

		try {

			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, apiRequest);
			Request request = new Request.Builder()
					.url(DecentroUtils.DECENTRO_API_BASE_URL_UAT + "v2/kyc/converter/pan/gstin").method("POST", body)
					.addHeader("client_id", DecentroUtils.DECENTRO_CLIENT_ID_UAT)
					.addHeader("client_secret", DecentroUtils.DECENTRO_CLIENT_SECRET_UAT)
					.addHeader("Content-Type", "application/json").build();
			Response response = client.newCall(request).execute();

			apiResponse = response.body().string();

			LOGGER.info("apiResponse: {}", apiResponse);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return apiResponse;
	}

	private String emailVerification(String email, String merchantTrxnRefId) {

		LOGGER.info("Inside emailVerification:");

		String apiRequest = "{\r\n" + "  \"consent\": true,\r\n"
				+ "  \"purpose\": \"To perform KYC of the individual\",\r\n" + "  \"reference_id\": \""
				+ merchantTrxnRefId + "\",\r\n" + "  \"email\": \"" + email + "\"\r\n" + "}";

		LOGGER.info("apiRequest: {}", apiRequest);

		String apiResponse = null;

		try {

			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, apiRequest);
			Request request = new Request.Builder()
					.url("https://in.staging.decentro.tech/v2/kyc/identities/employment-verification/email")
					.method("POST", body).addHeader("accept", "application/json")
					.addHeader("client_id", DecentroUtils.DECENTRO_CLIENT_ID_UAT)
					.addHeader("client_secret", DecentroUtils.DECENTRO_CLIENT_SECRET_UAT)
					.addHeader("content-type", "application/json")
					.addHeader("module_secret", DecentroUtils.DECENTRO_MODULE_SECRET_UAT).build();
			Response response = client.newCall(request).execute();

			apiResponse = response.body().string();

			LOGGER.info("apiResponse: {}", apiResponse);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return apiResponse;
	}

	private Map<String, Object> parseDetailPanResponse(Object documentIdNumber, String documentType,
			String merchantTrxnRefId) {

		LOGGER.info("Inside parseDetailPanResponse:");

		Map<String, Object> responseMap = new HashMap<>();

		String docId = (String) documentIdNumber;

		try {

			String validateDocumentResponse = validateDocument(docId, documentType, merchantTrxnRefId);

			LOGGER.info("validateDocumentResponse: {}", validateDocumentResponse);

			JSONObject resultJsonObject = new JSONObject(validateDocumentResponse);

			String status = resultJsonObject.getString("status");
			String kycStatus = resultJsonObject.getString("kycStatus");

			if (status.equalsIgnoreCase("SUCCESS") && kycStatus.equalsIgnoreCase("SUCCESS")) {

				JSONObject kycResult = resultJsonObject.getJSONObject("kycResult");

				responseMap = ekycCommonLogicConfig.buildResponse(ResponseMessage.SUCCESS,
						ResponseMessage.API_STATUS_SUCCESS, ResponseMessage.DATA_SUCCESS);

				responseMap.put(ResponseMessage.DATA, kycResult.toMap());
				return responseMap;
			} else {

				responseMap = ekycCommonLogicConfig.buildResponse(ResponseMessage.FAILED,
						ResponseMessage.API_STATUS_FAILED, ResponseMessage.DATA_NOT_FOUND);
				responseMap.put("pan", documentIdNumber);
				responseMap.put("merchantTxnRefId", merchantTrxnRefId);
			}

		} catch (HttpClientErrorException e) {
			responseMap = ekycCommonLogicConfig.buildResponse(ResponseMessage.SOMETHING_WENT_WRONG,
					ResponseMessage.STATUS_FAILED, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Exception: {}", e);
			responseMap = ekycCommonLogicConfig.buildResponse(ResponseMessage.SOMETHING_WENT_WRONG,
					ResponseMessage.STATUS_FAILED, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		}
		return responseMap;
	}

	private Map<String, Object> parseBasicPanResponse(Object documentIdNumber, String documentType,
			String merchantTrxnRefId) {

		LOGGER.info("Inside parseBasicPanResponse:");

		Map<String, Object> responseMap = new HashMap<>();

		String docId = (String) documentIdNumber;

		try {

			String validateDocumentResponse = validateDocument(docId, documentType, merchantTrxnRefId);

			LOGGER.info("validateDocumentResponse: {}", validateDocumentResponse);

			JSONObject resultJsonObject = new JSONObject(validateDocumentResponse);

			JSONObject resultObject = new JSONObject();

			String status = resultJsonObject.getString("status");
			String kycStatus = resultJsonObject.getString("kycStatus");

			if (status.equalsIgnoreCase("SUCCESS") && kycStatus.equalsIgnoreCase("SUCCESS")) {

				JSONObject kycResult = resultJsonObject.getJSONObject("kycResult");

				resultObject.put("typeOfHolder", kycResult.get("category"));
				resultObject.put("lastName", kycResult.get("name"));
				resultObject.put("panStatusCode", "E");
				resultObject.put("isValid", "true");
				resultObject.put("isIndividual", "true");
				resultObject.put("title", "NA");
				resultObject.put("panStatus", kycResult.get("idStatus"));
				resultObject.put("number", kycResult.get("idNumber"));
				resultObject.put("firstName", kycResult.get("name"));
				resultObject.put("aadhaarSeedingStatusCode", "NA");
				resultObject.put("name", kycResult.get("name"));
				resultObject.put("lastUpdatedOn", "NA");
				resultObject.put("middleName", kycResult.get("name"));
				resultObject.put("aadhaarSeedingStatus", "NA");

				responseMap = ekycCommonLogicConfig.buildResponse(ResponseMessage.SUCCESS,
						ResponseMessage.API_STATUS_SUCCESS, ResponseMessage.DATA_SUCCESS);

				responseMap.put(ResponseMessage.DATA, resultObject.toMap());
				return responseMap;
			} else {

				responseMap = ekycCommonLogicConfig.buildResponse(ResponseMessage.FAILED,
						ResponseMessage.API_STATUS_FAILED, ResponseMessage.DATA_NOT_FOUND);
			}

		} catch (HttpClientErrorException e) {
			responseMap = ekycCommonLogicConfig.buildResponse(ResponseMessage.SOMETHING_WENT_WRONG,
					ResponseMessage.STATUS_FAILED, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Exception: {}", e);
			responseMap = ekycCommonLogicConfig.buildResponse(ResponseMessage.SOMETHING_WENT_WRONG,
					ResponseMessage.STATUS_FAILED, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		}
		return responseMap;
	}

	private Map<String, Object> parseDetailGstResponse(Object documentIdNumber, String documentType,
			String merchantTrxnRefId) {

		LOGGER.info("Inside parseGstResponse:");

		Map<String, Object> responseMap = new HashMap<>();

		String docId = (String) documentIdNumber;
		try {

			String validateDocumentResponse = validateDocument(docId, documentType, merchantTrxnRefId);

			LOGGER.info("validateDocumentResponse: {}", validateDocumentResponse);

			JSONObject resultJsonObject = new JSONObject(validateDocumentResponse);

			String status = resultJsonObject.getString("status");
//			String kycStatus = resultJsonObject.getString("kycStatus");
			String responseCode = resultJsonObject.getString("responseCode");

			if (status.equalsIgnoreCase("SUCCESS") && responseCode.equalsIgnoreCase("S00000")) {

				JSONObject kycResult = resultJsonObject.getJSONObject("kycResult");

				responseMap = ekycCommonLogicConfig.buildResponse(ResponseMessage.SUCCESS,
						ResponseMessage.API_STATUS_SUCCESS, ResponseMessage.DATA_SUCCESS);

				responseMap.put(ResponseMessage.DATA, kycResult.toMap());

				return responseMap;
			} else {

				responseMap = ekycCommonLogicConfig.buildResponse(ResponseMessage.FAILED,
						ResponseMessage.API_STATUS_FAILED, ResponseMessage.DATA_NOT_FOUND);
				responseMap.put("gstin", documentIdNumber);
				responseMap.put("merchantTxnRefId", merchantTrxnRefId);
			}

		} catch (HttpClientErrorException e) {
			responseMap = ekycCommonLogicConfig.buildResponse(ResponseMessage.SOMETHING_WENT_WRONG,
					ResponseMessage.STATUS_FAILED, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Exception: {}", e);
			responseMap = ekycCommonLogicConfig.buildResponse(ResponseMessage.SOMETHING_WENT_WRONG,
					ResponseMessage.STATUS_FAILED, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		}

		return responseMap;

	}

	private Map<String, Object> parseBasicGstResponse(Object documentIdNumber, String documentType,
			String merchantTrxnRefId) {

		LOGGER.info("Inside parseGstResponse:");

		Map<String, Object> responseMap = new HashMap<>();

		String docTd = (String) documentIdNumber;

		try {

			String validateDocumentResponse = validateDocument(docTd, documentType, merchantTrxnRefId);

			LOGGER.info("validateDocumentResponse: {}", validateDocumentResponse);

			JSONObject resultJsonObject = new JSONObject(validateDocumentResponse);

			org.json.JSONObject resultObject = new org.json.JSONObject();
			String status = resultJsonObject.getString("status");
			String kycStatus = resultJsonObject.getString("kycStatus");

			if (status.equals("SUCCESS") && kycStatus.equals("SUCCESS")) {

				JSONObject kycResult = resultJsonObject.getJSONObject("kycResult");
//				JSONObject primaryBusinessContact = kycResult.getJSONObject("primaryBusinessContact");

				resultObject.put("gstin", docTd);
				resultObject.put("email", "NA");
				resultObject.put("address", kycResult.get("principalPlaceOfBusiness"));
				resultObject.put("mobileNumber", "NA");
				resultObject.put("natureOfBusinessAtAddress", kycResult.get("natureOfBusiness"));
				resultObject.put("stateJurisdiction", kycResult.get("stateJurisdiction"));
				resultObject.put("taxpayerType", kycResult.get("taxpayerType"));
				resultObject.put("registrationDate", kycResult.get("registrationDate"));
				resultObject.put("constitutionOfBusiness", kycResult.get("constitutionOfBusiness"));
				resultObject.put("gstnStatus", kycResult.get("gstnStatus"));
				resultObject.put("legalName", kycResult.get("legalName"));
				resultObject.put("centralJurisdiction", kycResult.get("centralJurisdiction"));
				resultObject.put("pan", kycResult.get("pan"));
				resultObject.put("tradeName", kycResult.get("tradeName"));

				responseMap = ekycCommonLogicConfig.buildResponse(ResponseMessage.SUCCESS,
						ResponseMessage.API_STATUS_SUCCESS, ResponseMessage.DATA_SUCCESS);

				responseMap.put("merchantTxnRefId", merchantTrxnRefId);
				responseMap.put(ResponseMessage.DATA, resultObject.toMap());
				return responseMap;
			} else {
				responseMap = ekycCommonLogicConfig.buildResponse(ResponseMessage.FAILED,
						ResponseMessage.API_STATUS_FAILED, ResponseMessage.DATA_NOT_FOUND);
				responseMap.put("gstin", documentIdNumber);
				responseMap.put("merchantTxnRefId", merchantTrxnRefId);
			}

		} catch (HttpClientErrorException e) {
			responseMap = ekycCommonLogicConfig.buildResponse(ResponseMessage.SOMETHING_WENT_WRONG,
					ResponseMessage.STATUS_FAILED, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Exception: {}", e);
			responseMap = ekycCommonLogicConfig.buildResponse(ResponseMessage.SOMETHING_WENT_WRONG,
					ResponseMessage.STATUS_FAILED, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		}

		return responseMap;

	}

	// This api is commonly used for Basic(GST, PAN, PASSPORT, RC, VOTER_ID) and
	// Detailed(GST,
	// PAN, PASSPORT)
	private String validateDocument(String documentIdNumber, String documentType, String merchantTrxnRefId) {

		LOGGER.info("Inside validateDocument:");

		String apiRequest = "{\r\n    \"consent\": \"Y\",\r\n    \"consent_purpose\": \"For bank account purpose only\",\r\n    \"document_type\": \""
				+ documentType + "\",\r\n    \"reference_id\": \"" + merchantTrxnRefId
				+ "\",\r\n    \"dob\": \"1999-07-26\",\r\n    \"id_number\": \"" + documentIdNumber + "\"\r\n}";

		LOGGER.info("apiRequest: {}", apiRequest);

		String apiResponse = null;

		try {

			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, apiRequest);
			Request request = new Request.Builder().url(DecentroUtils.DECENTRO_API_BASE_URL+"kyc/public_registry/validate")
					.method("POST", body).addHeader("accept", "application/json")
					.addHeader("client_id", DecentroUtils.DECENTRO_CLIENT_ID)
					.addHeader("client_secret", DecentroUtils.DECENTRO_CLIENT_SECRET)
					.addHeader("content-type", "application/json")
					.addHeader("module_secret", DecentroUtils.DECENTRO_MODULE_SECRET).build();
			Response response = client.newCall(request).execute();

			apiResponse = response.body().string();

			LOGGER.info("apiResponse: {}", apiResponse);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return apiResponse;
	}

	private String eAadhar(String documentIdNumber, String documentType, String merchantTrxnRefId) {

		LOGGER.info("Inside eAadhar:");

		String apiRequest = "{\r\n" + "  \"consent\": true,\r\n" + "  \"initial_decentro_transaction_id\": \""
				+ documentIdNumber + "\",\r\n" + "  \"consent_purpose\": \"for banking purpose only\",\r\n"
				+ "  \"reference_id\": \"" + merchantTrxnRefId + "\",\r\n" + "  \"generate_xml\": false,\r\n"
				+ "  \"generate_pdf\": false\r\n" + "}";

		LOGGER.info("apiRequest: {}", apiRequest);

		String apiResponse = null;

		try {

			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, apiRequest);
			Request request = new Request.Builder().url("https://in.staging.decentro.tech/v2/kyc/digilocker/eaadhaar")
					.method("POST", body).addHeader("accept", "application/json")
					.addHeader("client_id", DecentroUtils.DECENTRO_CLIENT_ID_UAT)
					.addHeader("client_secret", DecentroUtils.DECENTRO_CLIENT_SECRET_UAT)
					.addHeader("content-type", "application/json")
					.addHeader("module_secret", DecentroUtils.DECENTRO_MODULE_SECRET_UAT).build();
			Response response = client.newCall(request).execute();

			apiResponse = response.body().string();

			LOGGER.info("apiResponse: {}", apiResponse);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return apiResponse;
	}

	private String jwtGeneration() {

		LOGGER.info("Inside jwtGeneration:");

		String apiRequest = "{\"grant_type\":\"client_credentials\",\"client_id\":\""
				+ DecentroUtils.DECENTRO_CLIENT_ID_UAT + "\",\"client_secret\":\""
				+ DecentroUtils.DECENTRO_CLIENT_SECRET_UAT + "\"}";

		LOGGER.info("apiRequest: {}", apiRequest);

		String apiResponse = null;
		String accessToken = null;

		try {

			OkHttpClient client = new OkHttpClient();

			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, apiRequest);
			Request request = new Request.Builder().url("https://staging.api.decentro.tech/v2/auth/token").get()
					.addHeader("accept", "application/json").addHeader("content-type", "application/json").build();

			Response response = client.newCall(request).execute();

			apiResponse = response.body().string();

			LOGGER.info("apiResponse: {}", apiResponse);

			JSONObject jsonObject = new JSONObject(apiResponse);

			accessToken = jsonObject.getString("access_token");

			LOGGER.info("accessToken: {}", accessToken);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return accessToken;
	}

}
