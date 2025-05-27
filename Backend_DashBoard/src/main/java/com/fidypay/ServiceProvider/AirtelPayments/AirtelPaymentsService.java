package com.fidypay.ServiceProvider.AirtelPayments;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fidypay.entity.PayinRequest;
import com.fidypay.entity.PayinResponse;
import com.fidypay.service.PayinRequestService;
import com.fidypay.service.PayinResponseService;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.DateUtil;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class AirtelPaymentsService {

	private final Logger log = LoggerFactory.getLogger(AirtelPaymentsService.class);

	@Autowired
	private JwtTokenGenerator jwtTokenGenerator;

	@Autowired
	private AESCryptoV2Utils encryptionService;

	@Autowired
	private PayinRequestService payinRequestService;

	@Autowired
	private PayinResponseService payinResponseService;

//	Production Credentials
	private static final String MERCHANT_SECRET_KEY = "5989c99c-4dab-43";
	private static final String MID_PROD = "MER0000007174489";
	private static final String SUB_MERCHANT_ONBOARDING = "https://merchantupi.airtelbank.com:5055/apb/merchant-onb-service/external/vpa/create";

	// URL
	private static final String CALLBACK_URL = "https://api.fidypay.com/callback/api/airtel-callback";
	private static final String VPA_GENRE = "OFFLINE";
	private static final String VPA_ONBOARDING_TYPE = "AGGREGATOR";

	public String subMerchantOnBoarding(String mobileNumber, String vpaName, String mccCode, String address,
			String legalName, String directorName, String panNumber, String gstNumber, String setVpa, long merchantId,
			String requestTemp) throws IOException, ParseException {
		JSONObject map = new JSONObject();
		String sellerIdentifier = "FPAP" + mobileNumber;
		String vpa = setVpa + "@mairtel";

		if (gstNumber.equalsIgnoreCase("") || gstNumber == "null") {
			gstNumber = "23" + panNumber + "1Z8";
		}

		String requestData = "{\r\n" + "\"requestId\": \"" + sellerIdentifier + "\",\r\n" + "\"data\": [\r\n" + "{\r\n"
				+ "\"vpa\": \"" + vpa + "\",\r\n" + "\"vpaName\": \"" + vpaName + "\",\r\n" + "\"mccCode\": \""
				+ mccCode + "\",\r\n" + "\"address\": \"" + address + "\",\r\n" + "\"contactNumber\": \"" + mobileNumber
				+ "\",\r\n" + "\"legalName\": \"" + legalName + "\",\r\n" + "\"directorName\": \"" + directorName
				+ "\",\r\n" + "\"websiteUrl\": \"" + CALLBACK_URL + "\",\r\n" + "\"gstIN\": \"" + gstNumber + "\",\r\n"
				+ "\"panNumber\": \"" + panNumber + "\",\r\n" + "\"vpaGenre\": \"" + VPA_GENRE + "\",\r\n"
				+ "\"vpaOnboardingType\": \"" + VPA_ONBOARDING_TYPE + "\",\r\n" + "\"intentEnabled\": \"true\"\r\n"
				+ "}\r\n" + "]\r\n" + "}";

		log.info("Inside subMerchantOnBoarding: {}", requestData);
		Timestamp trxnDate = Timestamp.valueOf(DateUtil.getCurrentTimeInIST());
		PayinRequest payinRequest = payinRequestService.save(merchantId, requestTemp, requestData, mobileNumber, 0.0,
				"NA", "NA", sellerIdentifier, "Submerchant Creation API Airtel", trxnDate);

		JSONObject json = new JSONObject();

		try {

			log.info("Service Request to merchantOnBoarding: {}", requestData);
			log.info("MID: {}", MID_PROD);

			String token = jwtTokenGenerator.generateJwtToken(MID_PROD, MERCHANT_SECRET_KEY);

			String encryptedData = encryptionService.encryptString(requestData, MERCHANT_SECRET_KEY);

			json.put("data", encryptedData);

			log.info("token: {}", token);

			log.info("Request: {}", json.toString());

			OkHttpClient client = new OkHttpClient().newBuilder().build();

			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, json.toString());

			Request request = new Request.Builder().url(SUB_MERCHANT_ONBOARDING).method("POST", body)
					.addHeader("Authorization", token).addHeader("MID", MID_PROD)
					.addHeader("Content-Type", "application/json").build();

			Response response1 = client.newCall(request).execute();

			log.info("AIRTEL PAYMENT BANK SUB MERCHANT ON-BOARDING API response1 : {}", response1);

			String apiResponse = response1.body().string();
			log.info("apiResponse: {}", apiResponse);

			log.info("AIRTEL PAYMENT BANK SUB MERCHANT ON-BOARDING API response1 : {}", apiResponse);

			JSONObject jsonObject = new JSONObject(apiResponse);
			String data = jsonObject.getString("data");

			log.info(" data : {}", data);

			String rsp = encryptionService.decryptString(data, MERCHANT_SECRET_KEY);
			log.info(" rsp : {}", rsp);

			PayinResponse payinResponse = payinResponseService.save(rsp, rsp, mobileNumber, sellerIdentifier, 0.0, "NA",
					"Success", "Submerchant Creation API Airtel", merchantId, trxnDate);

			JSONObject jsonResp = new JSONObject(rsp);

			JSONObject meta = jsonResp.getJSONObject("meta");
			String status = meta.getString("status");
			String description = meta.getString("description");

			if (description.equalsIgnoreCase("SUCCESS") && status.equals("0")) {
				JSONObject dataJson = jsonResp.getJSONObject("data");
				log.info(" dataJson : {}", dataJson);
				JSONArray dataArray = dataJson.getJSONArray("data");
				JSONObject dataArrayJsonObject = dataArray.getJSONObject(0);

				JSONObject validationRuleResultjson = dataArrayJsonObject.getJSONObject("validationRuleResult");
				String statusValidationRuleResultjson = validationRuleResultjson.getString("result");
//				boolean isvalidValidationRuleResultjson = validationRuleResultjson.getBoolean("isValid");
				String message = validationRuleResultjson.getString("message");
				if (statusValidationRuleResultjson.equalsIgnoreCase("SUCCESS")) {
					String requestId = dataJson.getString("requestId");
					String getVpa = dataArrayJsonObject.getString("vpa");
					vpaName = dataArrayJsonObject.getString("vpaName");
					mccCode = dataArrayJsonObject.getString("mccCode");
					log.info("dataArrayJsonObject-----------> {}", dataArrayJsonObject.toString());
					log.info("requestIdt----------->{} ", requestId);
					String qrString = "upi://pay?pa=" + getVpa + "&pn=" + vpaName + "&mc=" + mccCode + "";
					map.put("is_vpa_registered", true);
					map.put("payeeVPA", getVpa);
					map.put("mobileNo", mobileNumber);
					map.put("subMerchantId", sellerIdentifier);
					map.put("qrString", qrString);
					map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
					map.put(ResponseMessage.DESCRIPTION, ResponseMessage.REGISTER_SUCCESSFULLY);
					return map.toString();
				}

				if (statusValidationRuleResultjson.equalsIgnoreCase("FAILURE")) {
					map.put("reason", message);
					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
					map.put(ResponseMessage.DESCRIPTION, ResponseMessage.REGISTER_SUCCESSFULLY);
					return map.toString();
				}
			}
			map.put("reason", description);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.REGISTER_NOT_SUCCESSFULLY);

		} catch (Exception ex) {
			ex.printStackTrace();
			map.put("reason", ex.getMessage());
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
		}
		return map.toString();
	}

}
