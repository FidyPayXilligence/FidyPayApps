package com.fidypay.service.impl;

import java.io.IOException;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fidypay.encryption.Encryption;
import com.fidypay.entity.PGMerchantInfo;
import com.fidypay.repo.PGMerchantInfoRepository;
import com.fidypay.request.PGMerchantInfoRequest;
import com.fidypay.response.PGMerchantInfoResponse;
import com.fidypay.service.PGMerchantInfoService;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.DateAndTime;
import com.fidypay.utils.ex.GenerateTrxnRefId;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class PGMerchantInfoServiceImpl implements PGMerchantInfoService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PGMerchantInfoServiceImpl.class);

	@Autowired
	private PGMerchantInfoRepository pgMerchantInfoRepository;

	private static final String CONTENT_TYPE = "application/json";
	private static final String VPA_VALIDATE = "https://api.phonepe.com/apis/hermes/pg/v1/vpa/validate";

	@Override
	public Map<String, Object> savePgMerchantInfo(PGMerchantInfoRequest pgMerchantInfoRequest, long merchantId)
			throws Exception {

		Map<String, Object> map = new HashMap<String, Object>();

		Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

		PGMerchantInfo info = new PGMerchantInfo();

		if (pgMerchantInfoRepository.existsByApiMerchantIdAndIsActiveAndMerchantId(
				Encryption.encString(pgMerchantInfoRequest.getApiMerchantId()), '1', merchantId)) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, "API Merchant Id already exists.");
			return map;
		}

		String apiMerchantId = pgMerchantInfoRequest.getApiMerchantId();
		String index = pgMerchantInfoRequest.getIndex();
		String saltKey = pgMerchantInfoRequest.getSaltKey();

		String bankId = "PhiCommerce";

		switch (bankId) {
		case "PhiCommerce":

			String pgMerchantKey = merchantId + "PGPC" + GenerateTrxnRefId.getAlphaNumericString(10);

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("apiKey", pgMerchantInfoRequest.getSaltKey());
			jsonObject.put("phiCommerceMerchantId", pgMerchantInfoRequest.getApiMerchantId());
			jsonObject.put("phiCommerceAggregatorId", pgMerchantInfoRequest.getEncryptionKey());
			String additionalInfo = jsonObject.toString();
			
			info.setMerchantId(merchantId);
			info.setDate(trxnDate);
			info.setEncryptionKey(pgMerchantInfoRequest.getEncryptionKey());
			info.setAdditionalInfo(additionalInfo);
			info.setPgMerchantKey(Encryption.encString(pgMerchantKey));
			info.setRemark(pgMerchantInfoRequest.getRemark());
			info.setApiMerchantId(Encryption.encString(pgMerchantInfoRequest.getApiMerchantId()));
			info.setIsActive('1');
			info.setBankId(bankId);
			pgMerchantInfoRepository.save(info);

			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.REGISTER_SUCCESSFULLY);

			break;
		case "Phone Pe":
			String apiResponse = validateVpa(apiMerchantId, saltKey, index);
			JSONObject js = new JSONObject(apiResponse);
			JSONObject json = new JSONObject();
			Boolean success = js.getBoolean("success");
			String code = js.getString("code");
			if (success.equals(true) && code.equalsIgnoreCase("SUCCESS")) {

				String PgMerchantKeyPhonePe = merchantId + "PGPP" + GenerateTrxnRefId.getAlphaNumericString(10);

				JSONObject jsonObjectPhonPe = new JSONObject();
				jsonObjectPhonPe.put("index", pgMerchantInfoRequest.getIndex());
				jsonObjectPhonPe.put("saltKey", pgMerchantInfoRequest.getSaltKey());
				jsonObjectPhonPe.put("APIMerchantId", pgMerchantInfoRequest.getApiMerchantId());
				String additionalInfoPhonPe = jsonObjectPhonPe.toString();

				info.setMerchantId(merchantId);
				info.setDate(trxnDate);
				info.setEncryptionKey(pgMerchantInfoRequest.getEncryptionKey());
				info.setAdditionalInfo(additionalInfoPhonPe);
				info.setPgMerchantKey(Encryption.encString(PgMerchantKeyPhonePe));
				info.setRemark(pgMerchantInfoRequest.getRemark());
				info.setApiMerchantId(Encryption.encString(pgMerchantInfoRequest.getApiMerchantId()));
				info.setIsActive('1');
				info.setBankId("Phone Pe");
				pgMerchantInfoRepository.save(info);

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.REGISTER_SUCCESSFULLY);
				return map;
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.INVALID_CREDENTIALS);

			}
			break;

		default:

			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Something went wrong");

			break;
		}
		return map;


	}

	private static String getSHA256Hash(String data) {
		String result = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(data.getBytes("UTF-8"));
			return bytesToHex(hash); // make it printable
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	private static String bytesToHex(byte[] hash) {
		return DatatypeConverter.printHexBinary(hash);
	}

	private String validateVpa(String apiMerchantId, String saltKey, String index) {
		String apiResponse = "";

		try {

			String request = "{" + " \"merchantId\":\"" + apiMerchantId + "\",\r\n" + " \"vpa\":\"" + "9826359752@apl"
					+ "\"\r\n" + "}";

			String encryptRequest = Base64.getEncoder().encodeToString(request.getBytes());

			String encryptFinalRequest = "{\r\n" + " \"request\": \"" + encryptRequest + "\"\r\n" + "}";

			String hash = getSHA256Hash(encryptRequest + "/pg/v1/vpa/validate" + saltKey);
			LOGGER.info("hash " + hash);
			String xVerify = hash + "###" + index;
			LOGGER.info("xVerify " + xVerify);
			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, encryptFinalRequest);
			Request requestapi = new Request.Builder().url(VPA_VALIDATE).method("POST", body)
					.addHeader("X-VERIFY", xVerify.toLowerCase()).addHeader("Content-Type", CONTENT_TYPE).build();
			Response response = client.newCall(requestapi).execute();
			apiResponse = response.body().string();
			LOGGER.info("apiResponse " + apiResponse);
		} catch (IOException e) {
			e.printStackTrace();

		}
		return apiResponse;
	}

	@Override
	public Map<String, Object> findPgMerchantInfoByMerchantId(long merchantId) {
		Map<String, Object> map = new HashMap<>();

		try {

			List<PGMerchantInfo> list = new ArrayList<PGMerchantInfo>();
			List<PGMerchantInfoResponse> listPgMerchantInfoResponse = new ArrayList<PGMerchantInfoResponse>();

			list = pgMerchantInfoRepository.findByMerchantId(merchantId);

			if (list.size() != 0) {
				list.forEach(objects -> {

					try {
						String date = DateAndTime.dateFormatReports(objects.getDate().toString());
						PGMerchantInfoResponse PGMerchantInfoResponse = new PGMerchantInfoResponse();
						PGMerchantInfoResponse.setDate(date);
						PGMerchantInfoResponse.setApiMerchantId(Encryption.decString(objects.getApiMerchantId()));
						PGMerchantInfoResponse.setPgMerchantInfoKey(Encryption.decString(objects.getPgMerchantKey()));
						PGMerchantInfoResponse.setIsActive(objects.getIsActive());
						PGMerchantInfoResponse.setBankId(objects.getBankId());
						listPgMerchantInfoResponse.add(PGMerchantInfoResponse);
					} catch (ParseException e) {

						e.printStackTrace();
					}
				});
				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "PG MerchantInfo List");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put("data", listPgMerchantInfoResponse);
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			}

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage().toString());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;
	}

	@Override
	public Map<String, Object> accountInactive(long merchantId, String pgMerchantKey) {
		Map<String, Object> map = new HashMap<>();

		PGMerchantInfo info = pgMerchantInfoRepository.findByPgMerchantKey(Encryption.encString(pgMerchantKey));
		if (info == null) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			return map;
		}
		Character isActive = info.getIsActive();
		if (isActive == '0') {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Account already Deactivated");
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			return map;
		}

		info.setIsActive('0');
		pgMerchantInfoRepository.save(info);

		map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
		map.put(ResponseMessage.DESCRIPTION, "Account Deactivated Successfully");
		map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);

		return map;
	}

}
