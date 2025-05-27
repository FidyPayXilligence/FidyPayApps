package com.fidypay.service.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;

import com.fidypay.encryption.Encryption;
import com.fidypay.entity.MerchantSubMerchantInfoV2;
import com.fidypay.entity.Merchants;
import com.fidypay.repo.MerchantSubMerchantInfoV2Repository;
import com.fidypay.repo.MerchantsRepository;
import com.fidypay.repo.SoundBoxSubscriptionRepository;
import com.fidypay.response.SubMerchantDeatilsResponse;
import com.fidypay.response.SubMerchantDetailsResponse;
import com.fidypay.service.SubMerchantService;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.DateAndTime;
import com.fidypay.utils.ex.DateUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class SubMerchantServiceImpl extends JdbcDaoSupport implements SubMerchantService {

	@Autowired
	private SoundBoxSubscriptionRepository soundBoxSubscriptionRepository;

	@Autowired
	private MerchantSubMerchantInfoV2Repository infoV2Repository;

	@Autowired
	private MerchantsRepository merchantsRepository;

	@Autowired
	DataSource dataSource;

	@PostConstruct
	private void initialize() {
		setDataSource(dataSource);
	}

	@Override
	public List<SubMerchantDeatilsResponse> getSubMerchantDetailExcel(long merchantId) {

		List<SubMerchantDeatilsResponse> activityList = new ArrayList<SubMerchantDeatilsResponse>();

		List<MerchantSubMerchantInfoV2> details = infoV2Repository.findVpaListByMerchantId(merchantId);

		AtomicInteger atomicInteger = new AtomicInteger(1);
		if (details.size() != 0) {
			details.forEach(objects -> {

				SubMerchantDeatilsResponse response = new SubMerchantDeatilsResponse();
				try {

					int soundBoxCount = soundBoxSubscriptionRepository
							.findSoundBoxCount(objects.getSubMerchantInfoIdV2(), '0');
					char soundBoxExist = soundBoxCount != 0 ? '1' : '0';
					String soundBox = "NO";
					if (soundBoxExist == '1') {
						soundBox = "YES";
					}

					String soundBoxId = "NA";
					List<String> listSoundBoxId = soundBoxSubscriptionRepository
							.findBySoundBoxTId(objects.getSubMerchantInfoIdV2());

					if (listSoundBoxId == null || listSoundBoxId.isEmpty()) {
						soundBoxId = "NA";
					} else {
						soundBoxId = String.join(",", listSoundBoxId);
					}

					String date = DateAndTime.dateFormatReports(objects.getSubMerchantDate().toString());

					response.setsNo(atomicInteger.getAndIncrement());
					response.setSubMerchantName(objects.getSubMerchantName());
					response.setSubMerchantBussinessName(objects.getSubMerchantBussinessName());
					response.setSubMerchantEmail(objects.getSubMerchantEmailId());
					response.setSubMerchantMobile(objects.getSubMerchantMobileNumber());
					response.setRegistrationDate(date);
					response.setVpa(objects.getSubMerchantAdditionalInfo());
					response.setSoundBox(soundBox);
					response.setSoundBoxId(soundBoxId);

					JSONObject jobject = new JSONObject(objects.getSubMerchantBankDetails());
					if (jobject.has("subMerchantIfscCode")) {
						response.setIfsc(jobject.getString("subMerchantIfscCode"));
					} else {
						response.setIfsc("NA");
					}
					if (jobject.has("subMerchantBankAccount")) {
						String accountNumber = jobject.getString("subMerchantBankAccount");

						if (accountNumber.length() >= 4) {
							accountNumber = "XXXXX" + accountNumber.substring(accountNumber.length() - 4);
						} else {
							accountNumber = "NA";
						}
						response.setAccountNumber(accountNumber);

					} else {

						response.setAccountNumber("NA");
					}

				} catch (ParseException e) {

				}
				activityList.add(response);

			});

		} else {

		}

		return activityList;

	}

	@Override
	public List<SubMerchantDetailsResponse> getSubMerchantDetails(long merchantId) {
		Map<String, Object> map = new HashMap<>();
		List<SubMerchantDetailsResponse> activityList = new ArrayList<SubMerchantDetailsResponse>();
		List<MerchantSubMerchantInfoV2> details = null;
		if (merchantId == 0) {
			details = infoV2Repository.findAll();
		} else {
			details = infoV2Repository.findVpaListByMerchantId(merchantId);
		}

		AtomicInteger atomicInteger = new AtomicInteger(1);
		if (details.size() != 0) {
			details.forEach(objects -> {

				SubMerchantDetailsResponse response = new SubMerchantDetailsResponse();
				try {
					String date = DateAndTime.dateFormatReports(objects.getSubMerchantDate().toString());

					response.setsNo(atomicInteger.getAndIncrement());
					response.setSubMerchantName(objects.getSubMerchantName());
					response.setSubMerchantBussinessName(objects.getSubMerchantBussinessName());
					response.setSubMerchantEmail(objects.getSubMerchantEmailId());
					response.setSubMerchantMobile(objects.getSubMerchantMobileNumber());
					response.setQrIssueDate(date);

					JSONObject jobject = new JSONObject(objects.getSubMerchantRegisterInfo());
					if (jobject.has("p26")) {
						response.setDob(jobject.getString("p26"));
					} else {

						response.setDob("NA");
					}
					if (jobject.has("p25")) {
						response.setDoBussiness(jobject.getString("p25"));
					} else {

						response.setDoBussiness("NA");
					}
					if (jobject.has("p9")) {
						response.setOwnership(jobject.getString("p9"));
					} else {

						response.setOwnership("NA");
					}

					JSONObject jObject = new JSONObject(objects.getSubMerchantBankDetails());
					response.setAccountNumber(jObject.getString("subMerchantBankAccount"));

					JSONObject jobj = new JSONObject(objects.getSubMerchantUserRequest());

					if (jobj.has("pinCode")) {
						response.setPincode(jobj.getString("pinCode"));
					} else {
						response.setPincode("NA");
					}
					if (jobj.has("merchantBussinessType")) {

						response.setBussinessType(jobj.getString("merchantBussinessType"));
					} else {

						response.setBussinessType("NA");
					}
					if (jobj.has("address")) {
						response.setAddress(jobj.getString("address"));
					} else {
						response.setAddress("NA");
					}

					String panNumber = objects.getSubMerchantPan();
					if (panNumber != null) {
						response.setSubMerchantPan(panNumber);
					} else {
						response.setSubMerchantPan("NA");
					}

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				activityList.add(response);

			});

		} else {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Transaction not found");
		}

		return activityList;

	}

	@Override
	public List<SubMerchantDeatilsResponse> getSubMerchantDetail() {

		List<SubMerchantDeatilsResponse> activityList = new ArrayList<SubMerchantDeatilsResponse>();

		List<MerchantSubMerchantInfoV2> details = infoV2Repository.findAll();

		AtomicInteger atomicInteger = new AtomicInteger(1);
		if (details.size() != 0) {
			details.forEach(objects -> {

				SubMerchantDeatilsResponse response = new SubMerchantDeatilsResponse();
				try {

					String date = DateAndTime.dateFormatReports(objects.getSubMerchantDate().toString());
					Merchants merchant = merchantsRepository.findById(objects.getMerchantId()).get();
					response.setMerchantBussinessName(Encryption.decString(merchant.getMerchantBusinessName()));
					response.setsNo(atomicInteger.getAndIncrement());
					response.setSubMerchantName(objects.getSubMerchantName());
					response.setSubMerchantBussinessName(objects.getSubMerchantBussinessName());
					response.setSubMerchantEmail(objects.getSubMerchantEmailId());
					response.setSubMerchantMobile(objects.getSubMerchantMobileNumber());
					response.setRegistrationDate(date);
					response.setVpa(objects.getSubMerchantAdditionalInfo());

					JSONObject jobject = new JSONObject(objects.getSubMerchantBankDetails());
					if (jobject.has("subMerchantIfscCode")) {
						response.setIfsc(jobject.getString("subMerchantIfscCode"));
					} else {
						response.setIfsc("NA");
					}
					if (jobject.has("subMerchantBankAccount")) {
						String accountNumber = jobject.getString("subMerchantBankAccount");

						if (accountNumber.length() >= 4) {
							accountNumber = "XXXXX" + accountNumber.substring(accountNumber.length() - 4);
						} else {
							accountNumber = "NA";
						}
						response.setAccountNumber(accountNumber);

					} else {

						response.setAccountNumber("NA");
					}

				} catch (ParseException e) {

				}
				activityList.add(response);

			});

		} else {

		}

		return activityList;

	}

	@Override
	public Map<String, Object> createSubMerchantById(String subMerchantInfoId) {
		Map<String, Object> map = new HashMap<String, Object>();

		if (!DateUtil.isValidNumber(subMerchantInfoId)) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Invalid Sub Merchant Info Id");
			return map;
		}

		MerchantSubMerchantInfoV2 merchantSubMerchantInfoV2 = infoV2Repository
				.findBySubMerchantInfoIdV2(Long.parseLong(subMerchantInfoId));

		if (merchantSubMerchantInfoV2 != null) {
			String subMerchantBankDetails = merchantSubMerchantInfoV2.getSubMerchantBankDetails();
			JsonObject jsonBankObject = JsonParser.parseString(subMerchantBankDetails).getAsJsonObject();

			String merchantBankAccountNumber = jsonBankObject.has("subMerchantBankAccount")
					? jsonBankObject.get("subMerchantBankAccount").getAsString()
					: "NA";
			String merchantBankIfsc = jsonBankObject.has("subMerchantIfscCode")
					? jsonBankObject.get("subMerchantIfscCode").getAsString()
					: "NA";
			String merchantBankName = jsonBankObject.has("subMerchantBankName")
					? jsonBankObject.get("subMerchantBankName").getAsString()
					: "NA";
			String email = merchantSubMerchantInfoV2.getSubMerchantEmailId();
			String fullName = merchantSubMerchantInfoV2.getSubMerchantName();
			String businessName = merchantSubMerchantInfoV2.getSubMerchantBussinessName();
			String phone = merchantSubMerchantInfoV2.getSubMerchantMobileNumber();
			String subMerchantId = merchantSubMerchantInfoV2.getSubMerchantId();
			String submerchantUserRequest = merchantSubMerchantInfoV2.getSubMerchantUserRequest();
			String subMerchantInfo = merchantSubMerchantInfoV2.getSubMerchantInfo();
			String vpa = merchantSubMerchantInfoV2.getSubMerchantAdditionalInfo();
			String qrString = merchantSubMerchantInfoV2.getSubMerchantQRString();

			String subMerchantRegisterInfo = merchantSubMerchantInfoV2.getSubMerchantRegisterInfo();
			JsonObject jsonRegisterObject = JsonParser.parseString(subMerchantRegisterInfo).getAsJsonObject();

			String address1 = jsonRegisterObject.has("p10") ? jsonRegisterObject.get("p10").getAsString() : "NA";
			String city = jsonRegisterObject.has("p11") ? jsonRegisterObject.get("p11").getAsString() : "NA";
			String state = jsonRegisterObject.has("p12") ? jsonRegisterObject.get("p12").getAsString() : "NA";
			String pinCode = jsonRegisterObject.has("p13") ? jsonRegisterObject.get("p13").getAsString() : "NA";

			map = subMerchantRequestCOOP(merchantBankAccountNumber, merchantBankIfsc, address1, merchantBankIfsc, city,
					email, fullName, merchantBankName, businessName, phone, pinCode, state, subMerchantId,
					submerchantUserRequest, subMerchantInfo, vpa, qrString);
			return map;
		}
		map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
		map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
		return map;
	}

	private Map<String, Object> subMerchantRequestCOOP(String accountNumber, String ifsc, String address,
			String subMerchantBankBranch, String city, String contactEmail, String name, String subMerchantBankName,
			String merchnatBussiessName, String mobile, String pinCode, String state, String subMerchantId,
			String submerchantDTOJSON, String response, String subMerchantVPA, String qrString) {
		Map<String, Object> map = new HashMap<>();
		try {

			JSONObject jObject = new JSONObject();
			jObject.put("accountNumber", accountNumber);
			jObject.put("address", address);
			jObject.put("bankBranch", subMerchantBankBranch);
			jObject.put("city", city);
			jObject.put("contactEmail", contactEmail);
			jObject.put("name", name);
			jObject.put("ifsc", ifsc);
			jObject.put("merchantBankName", subMerchantBankName);
			jObject.put("merchnatBussiessName", merchnatBussiessName);
			jObject.put("mobile", mobile);
			jObject.put("password", "123456");
			jObject.put("pinCode", pinCode);
			jObject.put("state", state);
			jObject.put("subMerchantCreateResponse", response);
			jObject.put("subMerchantId", subMerchantId);
			jObject.put("subMerchantRequest", submerchantDTOJSON);
			jObject.put("subMerchantVPA", subMerchantVPA);
			jObject.put("isMessageActive", 'Y');
			jObject.put("qrUrl", qrString);
			jObject.put("subMerchantBussinessName", merchnatBussiessName);

			String jsonRequest = jObject.toString();

			OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(60, TimeUnit.SECONDS)
					.readTimeout(30, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS).build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, jsonRequest);
			Request request = new Request.Builder().url(ResponseMessage.MERCHANT_SUBMERCHANT_REGISTER_URL)
					.method("POST", body).addHeader("Content-Type", "application/json").build();

			Response resp = client.newCall(request).execute();
			String results = resp.body().string();

			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, "Merchant created successfully");
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			map.put("response", response);

		} catch (NullPointerException e) {
			map.put(ResponseMessage.CODE, ResponseMessage.MISSING_PARAMETER);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.MISSING_PARAMETER_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;
	}

}
