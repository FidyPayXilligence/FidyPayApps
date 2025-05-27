package com.fidypay.service.impl;

import java.math.BigDecimal;
import java.net.ConnectException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fidypay.ServiceProvider.YesBank.YesBankSubMerchant;
import com.fidypay.ServiceProvider.YesBank.SellerAggregator.EncryptionAndDecryptionProduction;
import com.fidypay.ServiceProvider.YesBank.SellerAggregator.YesBankSellerAggregator;
import com.fidypay.dto.SubMerchantDTO;
import com.fidypay.dto.SubMerchantDTOCoop;
import com.fidypay.dto.SubMerchantDTOSeller;
import com.fidypay.encryption.Encryption;
import com.fidypay.entity.MerchantSubMerchantInfoV2;
import com.fidypay.entity.Merchants;
import com.fidypay.repo.MerchantSubMerchantInfoV2Repository;
import com.fidypay.repo.MerchantsRepository;
import com.fidypay.repo.SoundBoxSubscriptionRepository;
import com.fidypay.request.SubMerchantListRequest;
import com.fidypay.request.SubmerchantDocsRequest;
import com.fidypay.request.SubmerchatRequest;
import com.fidypay.response.CheckUPILimitResponse;
import com.fidypay.response.MCCListPayload;
import com.fidypay.response.MerchantSubMerchantInfoAllPayload;
import com.fidypay.response.MerchantSubMerchantInfoPayload;
import com.fidypay.response.SubMerchantResponse;
import com.fidypay.service.MerchantSubMerchantInfoService;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.DateAndTime;
import com.google.gson.Gson;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class MerchantSubMerchantInfoServiceImpl implements MerchantSubMerchantInfoService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MerchantSubMerchantInfoServiceImpl.class);

	@Autowired
	private SoundBoxSubscriptionRepository soundBoxSubscriptionRepository;

	@Autowired
	private MerchantSubMerchantInfoV2Repository merchantSubMerchantInfoV2Repository;

	@Autowired
	private YesBankSellerAggregator yesBankSellerAggregator;

	@Autowired
	private MerchantsRepository merchantsRepository;

	@Autowired
	private EncryptionAndDecryptionProduction encryptionAndDecryptionProduction;

	public Map<String, Object> subMerchantRequest(SubMerchantDTO subMerchantDTO, Long merchantId, String partnerKey) {
		Map<String, Object> map = new HashMap<>();
		JSONParser parser = new JSONParser();
		String subMerchantreponse = null;
		String response = null;
		try {
			LOGGER.info(" object To Json MCC -----------: " + subMerchantDTO.getMCC());
			String submerchantDTOJSON = new Gson().toJson(subMerchantDTO);

			LOGGER.info(" object To Json : " + submerchantDTOJSON);

			String action = subMerchantDTO.getAction();
			String merchnatBussiessName = subMerchantDTO.getMerchantBussiessName();
			String panNo = subMerchantDTO.getPanNo();
			String contactEmail = subMerchantDTO.getContactEmail();
			String gstn = subMerchantDTO.getGstn();
			String perDayTxnCount = subMerchantDTO.getPerDayTxnAmt();
			String merchantBussinessType = subMerchantDTO.getMerchantBussinessType();
			String perDayTxnLmt = subMerchantDTO.getPerDayTxnLmt();
			String perDayTxnAmt = subMerchantDTO.getPerDayTxnAmt();
			String mobile = subMerchantDTO.getMobile();
			String address = subMerchantDTO.getAddress();
			String state = subMerchantDTO.getState();
			String city = subMerchantDTO.getCity();
			String pinCode = subMerchantDTO.getPinCode();
			String subMerchantId = subMerchantDTO.getSubMerchantId();
			String MCC = subMerchantDTO.getMCC();
			String merchantGenre = subMerchantDTO.getMerchantGenre();

			String subMerchantIdRes = null;
			String subMerchantVPA = null;

			String subMerchantBankName = subMerchantDTO.getSubMerchantBankName();
			String subMerchantBankAccount = subMerchantDTO.getSubMerchantBankAccount();
			String subMerchantIfscCode = subMerchantDTO.getSubMerchantIfscCode();

			String subMerchantBankBranch = subMerchantDTO.getSubMerchantBankBranch();
			String name = subMerchantDTO.getName();
			String merchantVirtualAddress = subMerchantDTO.getMerchantVirtualAddress();
			// String isMessageActive = subMerchantDTO.getIsMessageActive();

			String alternateAddress = subMerchantDTO.getAlternateAddress();
			String longitude = subMerchantDTO.getLongitude();
			String latitude = subMerchantDTO.getLatitude();
			String dob = subMerchantDTO.getDob();
			String doi = subMerchantDTO.getDoi();

			LOGGER.info("merchantVirtualAddress " + merchantVirtualAddress);
			JSONObject bankDetail = new JSONObject();
			bankDetail.put("subMerchantBankName", subMerchantBankName);
			bankDetail.put("subMerchantBankAccount", subMerchantBankAccount);
			bankDetail.put("subMerchantIfscCode", subMerchantIfscCode);
			bankDetail.put("subMerchantBankBranch", subMerchantBankBranch);

			String subMerchantBankDetails = bankDetail.toString();
			LOGGER.info(" subMerchantBankDetails : " + subMerchantBankDetails);

			LOGGER.info(" object To Json : " + submerchantDTOJSON);
			// String merchantTrxnRefId = (String) req.get("merchantTrxnRefId");
			if (merchnatBussiessName == "" || action == "" || merchantVirtualAddress == "" || panNo == ""
					|| contactEmail == "" || perDayTxnCount == "" || merchantBussinessType == "" || perDayTxnLmt == ""
					|| perDayTxnAmt == "" || pinCode == "" || merchantGenre == "" || MCC == null
					|| merchnatBussiessName == null || action == null || merchantVirtualAddress == null || panNo == null
					|| contactEmail == null || gstn == null || perDayTxnCount == null || merchantBussinessType == null
					|| perDayTxnLmt == null || perDayTxnAmt == null || pinCode == null || MCC == null
					|| merchantGenre == null) {
				map.put(ResponseMessage.CODE, ResponseMessage.MISSING_PARAMETER);
				map.put(ResponseMessage.DESCRIPTION, "Bad Request");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			} else {
				SubMerchantDTOSeller subMerchantDTOSeller = new SubMerchantDTOSeller();
				subMerchantDTOSeller.setAction(action);
				subMerchantDTOSeller.setMerchantBussiessName(merchnatBussiessName);
				subMerchantDTOSeller.setPanNo(panNo);
				subMerchantDTOSeller.setContactEmail(contactEmail);
				subMerchantDTOSeller.setGstn(gstn);
				subMerchantDTOSeller.setPerDayTxnCount(perDayTxnCount);
				subMerchantDTOSeller.setMerchantBussinessType(merchantBussinessType);
				subMerchantDTOSeller.setPerDayTxnLmt(perDayTxnLmt);
				subMerchantDTOSeller.setPerDayTxnAmt(perDayTxnAmt);
				subMerchantDTOSeller.setMobile(mobile);
				subMerchantDTOSeller.setState(state);
				subMerchantDTOSeller.setCity(city);
				subMerchantDTOSeller.setPinCode(pinCode);
				subMerchantDTOSeller.setSubMerchantId(subMerchantId);
				subMerchantDTOSeller.setMCC(MCC);
				subMerchantDTOSeller.setSubMerchantBankName(subMerchantBankName);
				subMerchantDTOSeller.setSubMerchantBankAccount(subMerchantBankAccount);
				subMerchantDTOSeller.setSubMerchantIfscCode(subMerchantIfscCode);
				subMerchantDTOSeller.setMerchantGenre(merchantGenre);
				subMerchantDTOSeller.setName(name);
				subMerchantDTOSeller.setSubMerchantBankBranch(subMerchantBankBranch);
				subMerchantDTOSeller.setMerchantVirtualAddress(merchantVirtualAddress);
				subMerchantDTOSeller.setAddress(address);
				subMerchantDTOSeller.setAlternateAddress(alternateAddress);
				subMerchantDTOSeller.setLongitude(longitude);
				subMerchantDTOSeller.setLatitude(latitude);
				subMerchantDTOSeller.setDob(dob);
				subMerchantDTOSeller.setDoi(doi);

				//new
				subMerchantDTOSeller.setLlpOrCin(subMerchantDTO.getLlpOrCin());
				subMerchantDTOSeller.setUdhoyogAadhaar(subMerchantDTO.getUdhoyogAadhaar());
				subMerchantDTOSeller.setElectricityBill(subMerchantDTO.getElectricityBill());
				subMerchantDTOSeller.setElectricityBoard(subMerchantDTO.getElectricityBoard());

				
				subMerchantreponse = yesBankSellerAggregator.sellerAdditionForMerchantDashboard(subMerchantDTOSeller,
						merchantId, partnerKey);

				LOGGER.info("Sub Merchant API Response : " + subMerchantreponse);
				Object collectObj = parser.parse(subMerchantreponse);
				org.json.simple.JSONObject responseCollect = (org.json.simple.JSONObject) collectObj;
				String status = (String) responseCollect.get("status");

				response = subMerchantreponse;
				if (status.equalsIgnoreCase("SUCCESS") || status == "SUCCESS") {

					String qrString = (String) responseCollect.get("qrString");
					subMerchantVPA = (String) responseCollect.get("merchantVirtualAddress");
					subMerchantIdRes = (String) responseCollect.get("subMerchantId");

					JSONObject jObject = new JSONObject();
					jObject.put("accountNumber", subMerchantBankAccount);
					jObject.put("address", address);
					jObject.put("bankBranch", subMerchantBankBranch);
					jObject.put("city", city);
					jObject.put("contactEmail", contactEmail);
					jObject.put("name", name);
					jObject.put("ifsc", subMerchantIfscCode);
					jObject.put("merchantBankName", subMerchantBankName);
					jObject.put("merchnatBussiessName", merchnatBussiessName);
					jObject.put("mobile", mobile);
					jObject.put("password", "123456");
					jObject.put("pinCode", pinCode);
					jObject.put("state", state);
					jObject.put("subMerchantCreateResponse", response.toString());
					jObject.put("subMerchantId", subMerchantIdRes);
					jObject.put("subMerchantRequest", submerchantDTOJSON);
					jObject.put("subMerchantVPA", subMerchantVPA);
					// jObject.put("isMessageActive", isMessageActive);
					jObject.put("qrUrl", qrString);
					jObject.put("subMerchantBussinessName", merchnatBussiessName);

					String jsonRequest = jObject.toString();

					LOGGER.info("Merchant Register Json Request : " + jsonRequest);

					map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
					map.put(ResponseMessage.DESCRIPTION, "subMerchant created successfully");
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
					map.put("response", response);
				}

				else {
					response = subMerchantreponse;
					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put("response", response);
					LOGGER.info(" Else Status Submerchant : " + response);
				}

			}

		} catch (NullPointerException e) {
			LOGGER.error(" NullPointerException " + e);
			map.put(ResponseMessage.CODE, ResponseMessage.MISSING_PARAMETER);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.MISSING_PARAMETER_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, subMerchantreponse);
		} catch (ConnectException e) {
			LOGGER.error(" ConnectException " + e);
			map.put(ResponseMessage.CODE, ResponseMessage.CONNECTION_TIMEOUT);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.CONNECTION_TIMEOUT_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, subMerchantreponse);

		} catch (Exception e) {
			LOGGER.error(" Exception " + e);
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, subMerchantreponse);

		}
		LOGGER.info("Sub Merchant Response " + response);
		return map;
	}

	public Map<String, Object> subMerchantRequestCOOP(SubMerchantDTOCoop subMerchantDTO, Long merchantId,
			String partnerKey) {
		Map<String, Object> map = new HashMap<>();
		JSONParser parser = new JSONParser();
		String subMerchantreponse = null;
		String response = null;
		try {
			String submerchantDTOJSON = new Gson().toJson(subMerchantDTO);

			LOGGER.info(" object To Json : " + submerchantDTOJSON);

			String action = subMerchantDTO.getAction();
			String merchnatBussiessName = subMerchantDTO.getMerchantBussiessName();
			String panNo = subMerchantDTO.getPanNo();
			String contactEmail = subMerchantDTO.getContactEmail();
			String gstn = subMerchantDTO.getGstn();
			String perDayTxnCount = subMerchantDTO.getPerDayTxnAmt();
			String merchantBussinessType = subMerchantDTO.getMerchantBussinessType();
			String perDayTxnLmt = subMerchantDTO.getPerDayTxnLmt();
			String perDayTxnAmt = subMerchantDTO.getPerDayTxnAmt();
			String mobile = subMerchantDTO.getMobile();
			String address = subMerchantDTO.getAddress();
			String state = subMerchantDTO.getState();
			String city = subMerchantDTO.getCity();
			String pinCode = subMerchantDTO.getPinCode();
			String subMerchantId = subMerchantDTO.getSubMerchantId();
			String MCC = subMerchantDTO.getMCC();
			String merchantGenre = subMerchantDTO.getMerchantGenre();

			String alternateAddress = subMerchantDTO.getAlternateAddress();
			String longitude = subMerchantDTO.getLongitude();
			String latitude = subMerchantDTO.getLatitude();
			String dob = subMerchantDTO.getDob();
			String doi = subMerchantDTO.getDoi();

			String subMerchantIdRes = null;
			String subMerchantVPA = null;

			String subMerchantBankName = subMerchantDTO.getSubMerchantBankName();
			String subMerchantBankAccount = subMerchantDTO.getSubMerchantBankAccount();
			String subMerchantIfscCode = subMerchantDTO.getSubMerchantIfscCode();

			String subMerchantBankBranch = subMerchantDTO.getSubMerchantBankBranch();
			String name = subMerchantDTO.getName();
			String merchantVirtualAddress = subMerchantDTO.getMerchantVirtualAddress();
			String isMessageActive = subMerchantDTO.getIsMessageActive();

			JSONObject bankDetail = new JSONObject();
			bankDetail.put("subMerchantBankName", subMerchantBankName);
			bankDetail.put("subMerchantBankAccount", subMerchantBankAccount);
			bankDetail.put("subMerchantIfscCode", subMerchantIfscCode);
			bankDetail.put("subMerchantBankBranch", subMerchantBankBranch);

			String subMerchantBankDetails = bankDetail.toString();
			LOGGER.info(" subMerchantBankDetails : " + subMerchantBankDetails);

			LOGGER.info(" object To Json : " + submerchantDTOJSON);
			if (merchnatBussiessName == "" || action == "" || merchantVirtualAddress == "" || panNo == ""
					|| contactEmail == "" || perDayTxnCount == "" || merchantBussinessType == "" || perDayTxnLmt == ""
					|| perDayTxnAmt == "" || pinCode == "" || merchantGenre == "" || MCC == null
					|| merchnatBussiessName == null || action == null || merchantVirtualAddress == null || panNo == null
					|| contactEmail == null || gstn == null || perDayTxnCount == null || merchantBussinessType == null
					|| perDayTxnLmt == null || perDayTxnAmt == null || pinCode == null || MCC == null
					|| merchantGenre == null) {
				map.put(ResponseMessage.CODE, ResponseMessage.MISSING_PARAMETER);
				map.put(ResponseMessage.DESCRIPTION, "Bad Request");
			} else {

				SubMerchantDTOSeller subMerchantDTOSeller = new SubMerchantDTOSeller();
				subMerchantDTOSeller.setAction(action);
				subMerchantDTOSeller.setMerchantBussiessName(merchnatBussiessName);
				subMerchantDTOSeller.setPanNo(panNo);
				subMerchantDTOSeller.setContactEmail(contactEmail);
				subMerchantDTOSeller.setGstn(gstn);
				subMerchantDTOSeller.setPerDayTxnCount(perDayTxnCount);
				subMerchantDTOSeller.setMerchantBussinessType(merchantBussinessType);
				subMerchantDTOSeller.setPerDayTxnLmt(perDayTxnLmt);
				subMerchantDTOSeller.setPerDayTxnAmt(perDayTxnAmt);
				subMerchantDTOSeller.setMobile(mobile);
				subMerchantDTOSeller.setAddress(address);
				subMerchantDTOSeller.setState(state);
				subMerchantDTOSeller.setCity(city);
				subMerchantDTOSeller.setPinCode(pinCode);
				subMerchantDTOSeller.setSubMerchantId(subMerchantId);
				subMerchantDTOSeller.setMCC(MCC);
				subMerchantDTOSeller.setSubMerchantBankName(subMerchantBankName);
				subMerchantDTOSeller.setSubMerchantBankAccount(subMerchantBankAccount);
				subMerchantDTOSeller.setSubMerchantIfscCode(subMerchantIfscCode);
				subMerchantDTOSeller.setMerchantGenre(merchantGenre);
				subMerchantDTOSeller.setName(name);
				subMerchantDTOSeller.setSubMerchantBankBranch(subMerchantBankBranch);
				subMerchantDTOSeller.setMerchantVirtualAddress(merchantVirtualAddress);
				subMerchantDTOSeller.setAddress(address);
				subMerchantDTOSeller.setAlternateAddress(alternateAddress);
				subMerchantDTOSeller.setLongitude(longitude);
				subMerchantDTOSeller.setLatitude(latitude);
				subMerchantDTOSeller.setDob(dob);
				subMerchantDTOSeller.setDoi(doi);
				//new
				subMerchantDTOSeller.setLlpOrCin(subMerchantDTO.getLlpOrCin());
				subMerchantDTOSeller.setUdhoyogAadhaar(subMerchantDTO.getUdhoyogAadhaar());
				subMerchantDTOSeller.setElectricityBill(subMerchantDTO.getElectricityBill());
				subMerchantDTOSeller.setElectricityBoard(subMerchantDTO.getElectricityBoard());
				
				subMerchantreponse = yesBankSellerAggregator.sellerAddition(subMerchantDTOSeller, merchantId,
						partnerKey);

				LOGGER.info("Sub Merchant API Response : " + subMerchantreponse);
				Object collectObj = parser.parse(subMerchantreponse);
				org.json.simple.JSONObject responseCollect = (org.json.simple.JSONObject) collectObj;
				String status = (String) responseCollect.get("status");

				if (status.equalsIgnoreCase("SUCCESS") || status == "SUCCESS") {

					response = subMerchantreponse;
					String qrString = (String) responseCollect.get("qrString");
					subMerchantVPA = (String) responseCollect.get("merchantVirtualAddress");
					subMerchantIdRes = (String) responseCollect.get("subMerchantId");

					JSONObject jObject = new JSONObject();
					jObject.put("accountNumber", subMerchantBankAccount);
					jObject.put("address", address);
					jObject.put("bankBranch", subMerchantBankBranch);
					jObject.put("city", city);
					jObject.put("contactEmail", contactEmail);
					jObject.put("name", name);
					jObject.put("ifsc", subMerchantIfscCode);
					jObject.put("merchantBankName", subMerchantBankName);
					jObject.put("merchnatBussiessName", merchnatBussiessName);
					jObject.put("mobile", mobile);
					jObject.put("password", "123456");
					jObject.put("pinCode", pinCode);
					jObject.put("state", state);
					jObject.put("subMerchantCreateResponse", response.toString());
					jObject.put("subMerchantId", subMerchantIdRes);
					jObject.put("subMerchantRequest", submerchantDTOJSON);
					jObject.put("subMerchantVPA", subMerchantVPA);
					jObject.put("isMessageActive", isMessageActive);
					jObject.put("qrUrl", qrString);
					jObject.put("subMerchantBussinessName", merchnatBussiessName);

					String jsonRequest = jObject.toString();

					LOGGER.info("Merchant Register Json Request : " + jsonRequest);
					OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(60, TimeUnit.SECONDS)
							.readTimeout(30, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS).build();
					MediaType mediaType = MediaType.parse("application/json");
					RequestBody body = RequestBody.create(mediaType, jsonRequest);
					Request request = new Request.Builder().url(ResponseMessage.MERCHANT_SUBMERCHANT_REGISTER_URL)
							.method("POST", body).addHeader("Content-Type", "application/json").build();
					LOGGER.info("Request Merchant Register Api : " + request);

					Response resp = client.newCall(request).execute();
					String results = resp.body().string();
					LOGGER.info("Response Merchant Register Api : " + results);

					map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
					map.put(ResponseMessage.DESCRIPTION, "Merchant created successfully");
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
					map.put("response", response);

				} else {
					response = subMerchantreponse;
					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put("response", response);
					LOGGER.info(" Else Status Submerchant : " + response);
				}
			}

		} catch (NullPointerException e) {
			LOGGER.error(" NullPointerException " + e);
			map.put(ResponseMessage.CODE, ResponseMessage.MISSING_PARAMETER);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.MISSING_PARAMETER_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, subMerchantreponse);
		} catch (ConnectException e) {
			LOGGER.error(" ConnectException " + e);
			map.put(ResponseMessage.CODE, ResponseMessage.CONNECTION_TIMEOUT);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.CONNECTION_TIMEOUT_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, subMerchantreponse);

		} catch (Exception e) {
			LOGGER.error(" Exception " + e);
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, subMerchantreponse);
		}
		LOGGER.info("Sub Merchant Response " + response);
		return map;
	}

	@Override
	public Map<String, Object> subMerchantList(Long merchantId, SubmerchatRequest submerchatRequest) {
		Map<String, Object> map = new HashMap<>();
		try {
			Pageable paging = PageRequest.of(submerchatRequest.getPageNo(), submerchatRequest.getPageSize(),
					Sort.by("SUB_MERCHANT_DATE").descending());

			List<MerchantSubMerchantInfoPayload> msmList = new ArrayList<MerchantSubMerchantInfoPayload>();
			Page<MerchantSubMerchantInfoV2> list = merchantSubMerchantInfoV2Repository.findByMerchantIds(merchantId,
					paging);
			List<MerchantSubMerchantInfoV2> listSubMerchant = new ArrayList<MerchantSubMerchantInfoV2>();

			AtomicInteger atomicInteger = new AtomicInteger(1);
			listSubMerchant = list.getContent();
			if (listSubMerchant.size() != 0) {

				for (MerchantSubMerchantInfoV2 merchantSubMerchantInfo : list) {
					MerchantSubMerchantInfoPayload infoPayload = new MerchantSubMerchantInfoPayload();
					Long subMerchantInfoId = merchantSubMerchantInfo.getSubMerchantInfoIdV2();

					String isActive = merchantSubMerchantInfo.getSubMerchantStatus();

					String isStatus = null;
					if (isActive.equals("SUCCESS")) {
						isStatus = "ACTIVE";
					} else {
						isStatus = isActive;
					}

					String res = merchantSubMerchantInfo.getSubMerchantRegisterInfo();
					String merchantVirtualAddress = merchantSubMerchantInfo.getSubMerchantAdditionalInfo();
					String qrString = merchantSubMerchantInfo.getSubMerchantQRString();

					org.json.JSONObject jsonObject = new org.json.JSONObject(res);

					String action = null;

					if (jsonObject.has("action")) {
						action = jsonObject.getString("action");
					} else {
						action = (String) jsonObject.get("actionName");
					}

					String merchantBussiessName = merchantSubMerchantInfo.getSubMerchantBussinessName();

					String panNo = merchantSubMerchantInfo.getSubMerchantPan();
					panNo = !"NA".equals(panNo) && panNo != null ? "XXXXXX" + panNo.substring(panNo.length() - 4)
							: "NA";
					String contactEmail = merchantSubMerchantInfo.getSubMerchantEmailId();
					String gstn = merchantSubMerchantInfo.getSubMerchantGst();
					String mobile = merchantSubMerchantInfo.getSubMerchantMobileNumber();
					String date = DateAndTime
							.convertDateTimeFormatSubMerchant(merchantSubMerchantInfo.getSubMerchantDate().toString());
					int soundBoxCount = soundBoxSubscriptionRepository.findSoundBoxCount(subMerchantInfoId, '0');
					char soundBoxExist = soundBoxCount != 0 ? '1' : '0';

					String address = null;
					String state = null;
					String city = null;
					String pinCode = null;
					String subMerchantId = null;
					String MCC = null;

					if (jsonObject.has("address")) {
						address = jsonObject.getString("address");
					} else {
						address = (String) jsonObject.get("p10");
					}

					if (jsonObject.has("state")) {
						state = jsonObject.getString("state");
					} else {
						state = (String) jsonObject.get("p12");
					}

					if (jsonObject.has("city")) {
						city = jsonObject.getString("city");
					} else {
						city = (String) jsonObject.get("p11");
					}

					if (jsonObject.has("pinCode")) {
						pinCode = jsonObject.getString("pinCode");
					} else {
						pinCode = (String) jsonObject.get("p13");
					}

					if (jsonObject.has("subMerchantId")) {
						subMerchantId = jsonObject.getString("subMerchantId");
					} else {
						subMerchantId = (String) jsonObject.get("p3");
					}

					if (jsonObject.has("MCC")) {
						address = jsonObject.getString("MCC");
					} else {
						MCC = (String) jsonObject.get("p6");
					}

					if ((gstn == null || gstn.equals(""))) {
						infoPayload.setGstn("NA");
					} else {
						infoPayload.setGstn(gstn);
					}

					infoPayload.setsNo(atomicInteger.getAndIncrement());
					infoPayload.setSoundBoxExist(soundBoxExist);
					infoPayload.setRegistrationDate(date);
					infoPayload.setIsActive(isStatus);
					infoPayload.setSubMerchantInfoId(subMerchantInfoId);
					infoPayload.setAction(action);
					infoPayload.setAddress(address);
					infoPayload.setCity(city);
					infoPayload.setContactEmail(contactEmail);
					infoPayload.setMCC(MCC);
					infoPayload.setMerchantBussiessName(merchantBussiessName);
					infoPayload.setMerchantVirtualAddress(merchantVirtualAddress);
					infoPayload.setMobile(mobile);
					infoPayload.setPanNo(panNo);
					infoPayload.setPinCode(pinCode);
					infoPayload.setState(state);
					infoPayload.setSubMerchantId(subMerchantId);
					infoPayload.setSubMerchantVpa(merchantSubMerchantInfo.getSubMerchantAdditionalInfo());
					infoPayload.setQrCodeUrl(qrString);
					msmList.add(infoPayload);
				}

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "Sub Merchant List");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put("subMerchantList", msmList);
				map.put("totalRecord", list.getTotalElements());
				map.put("currentPage", list.getNumber());
				map.put("totalPages", list.getTotalPages());
				return map;

			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}

		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}

		return map;
	}

	@Override
	public Map<String, Object> subMerchantAllList(SubmerchatRequest submerchatRequest) {
		Map<String, Object> map = new HashMap<>();
		try {

			Pageable paging = PageRequest.of(submerchatRequest.getPageNo(), submerchatRequest.getPageSize(),
					Sort.by("SUB_MERCHANT_DATE").descending());
			List<MerchantSubMerchantInfoAllPayload> msmList = new ArrayList<MerchantSubMerchantInfoAllPayload>();
			Page<MerchantSubMerchantInfoV2> list = merchantSubMerchantInfoV2Repository.findAllWithPage(paging);
			List<MerchantSubMerchantInfoV2> listSubMerchant = new ArrayList<MerchantSubMerchantInfoV2>();

			AtomicInteger atomicInteger = new AtomicInteger(1);
			listSubMerchant = list.getContent();

			if (listSubMerchant.size() != 0) {

				for (MerchantSubMerchantInfoV2 merchantSubMerchantInfo : list) {
					MerchantSubMerchantInfoAllPayload infoPayload = new MerchantSubMerchantInfoAllPayload();
					Long subMerchantInfoId = merchantSubMerchantInfo.getSubMerchantInfoIdV2();

					String isActive = merchantSubMerchantInfo.getSubMerchantStatus();

					String isStatus = null;
					if (isActive.equals("SUCCESS")) {
						isStatus = "ACTIVE";
					} else {
						isStatus = isActive;
					}

					String res = merchantSubMerchantInfo.getSubMerchantRegisterInfo();
					String merchantVirtualAddress = merchantSubMerchantInfo.getSubMerchantAdditionalInfo();
					String qrString = merchantSubMerchantInfo.getSubMerchantQRString();

					org.json.JSONObject jsonObject = new org.json.JSONObject(res);

					String action = null;

					if (jsonObject.has("action")) {
						action = jsonObject.getString("action");
					} else {
						action = (String) jsonObject.get("actionName");
					}

					String merchantBussiessName = merchantSubMerchantInfo.getSubMerchantBussinessName();

					String panNo = merchantSubMerchantInfo.getSubMerchantPan();
					String contactEmail = merchantSubMerchantInfo.getSubMerchantEmailId();
					String gstn = merchantSubMerchantInfo.getSubMerchantGst();
					String mobile = merchantSubMerchantInfo.getSubMerchantMobileNumber();

					String address = null;
					String state = null;
					String city = null;
					String pinCode = null;
					String subMerchantId = null;
					String MCC = null;

					if (jsonObject.has("address")) {
						address = jsonObject.getString("address");
					} else {
						address = (String) jsonObject.get("p10");
					}

					if (jsonObject.has("state")) {
						state = jsonObject.getString("state");
					} else {
						state = (String) jsonObject.get("p12");
					}

					if (jsonObject.has("city")) {
						city = jsonObject.getString("city");
					} else {
						city = (String) jsonObject.get("p11");
					}

					if (jsonObject.has("pinCode")) {
						pinCode = jsonObject.getString("pinCode");
					} else {
						pinCode = (String) jsonObject.get("p13");
					}

					if (jsonObject.has("subMerchantId")) {
						subMerchantId = jsonObject.getString("subMerchantId");
					} else {
						subMerchantId = (String) jsonObject.get("p3");
					}

					if (jsonObject.has("MCC")) {
						address = jsonObject.getString("MCC");
					} else {
						MCC = (String) jsonObject.get("p6");
					}

					if ((gstn == null || gstn.equals(""))) {
						infoPayload.setGstn("NA");
					} else {
						infoPayload.setGstn(gstn);
					}

					infoPayload.setsNo(atomicInteger.getAndIncrement());
					infoPayload.setIsActive(isStatus);
					infoPayload.setSubMerchantInfoId(subMerchantInfoId);
					infoPayload.setAction(action);
					infoPayload.setAddress(address);
					infoPayload.setCity(city);
					infoPayload.setContactEmail(contactEmail);
					infoPayload.setMCC(MCC);
					infoPayload.setMerchantBussiessName(merchantBussiessName);
					infoPayload.setMerchantVirtualAddress(merchantVirtualAddress);
					infoPayload.setMobile(mobile);
					infoPayload.setPanNo(panNo);
					infoPayload.setPinCode(pinCode);
					infoPayload.setState(state);
					infoPayload.setSubMerchantId(subMerchantId);
					infoPayload.setSubMerchantVpa(merchantSubMerchantInfo.getSubMerchantAdditionalInfo());
					infoPayload.setQrCodeUrl(qrString);
					msmList.add(infoPayload);
				}

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "Sub Merchant List");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put("subMerchantList", msmList);
				map.put("totalRecord", list.getTotalElements());
				map.put("currentPage", list.getNumber());
				map.put("totalPages", list.getTotalPages());
				return map;

			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Sub Merchant List");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			}

		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}

		return map;
	}

	@Override
	public Map<String, Object> getMccDetailsList() {
		Map<String, Object> map = new HashMap<>();
		try {

			String respoString = "[\r\n" + "    {\r\n" + "        \"code\": \"9405\",\r\n"
					+ "        \"description\": \"Government Services, Intra - Govemment Purchases, Government Only\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"9402\",\r\n"
					+ "        \"description\": \"Others - Postal services government only\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"9400\",\r\n"
					+ "        \"description\": \"Others - PMNRF\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"9311\",\r\n" + "        \"description\": \"Tax Payment - Tax payments\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"9223\",\r\n"
					+ "        \"description\": \"Others - Bail and bond payments\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"9222\",\r\n" + "        \"description\": \"Others - Fines\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"9211\",\r\n"
					+ "        \"description\": \"Others - Court costs, including alimony and child support\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"8931\",\r\n"
					+ "        \"description\": \"Others - Accounting, auditing and bookkeeping services\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"8911\",\r\n"
					+ "        \"description\": \"Others - Architectural, engineering and surveying services\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"8734\",\r\n"
					+ "        \"description\": \"Others - Testing laboratories (non-medical)\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"8675\",\r\n"
					+ "        \"description\": \"Others - Automobile associations\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"8661\",\r\n"
					+ "        \"description\": \"Others - Religious organizations\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"8651\",\r\n"
					+ "        \"description\": \"Others - Political organizations\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"8641\",\r\n"
					+ "        \"description\": \"Others - Civic, social and fraternal associations\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"8493\",\r\n"
					+ "        \"description\": \"Vaccine voucher - \"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"8398\",\r\n"
					+ "        \"description\": \"Others - Charitable and social service organizations\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"8351\",\r\n"
					+ "        \"description\": \"Others - Child care services\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"8249\",\r\n"
					+ "        \"description\": \"Education - Trade and vocational schools\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"8244\",\r\n"
					+ "        \"description\": \"Education - Business and secretarial schools\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"8241\",\r\n"
					+ "        \"description\": \"Education - Correspondence schools\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"8220\",\r\n"
					+ "        \"description\": \"Education - College/University\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"8211\",\r\n" + "        \"description\": \"Education - School\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"8111\",\r\n"
					+ "        \"description\": \"Others - Legal services and attorneys\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"8071\",\r\n"
					+ "        \"description\": \"Others - Medical and dental laboratories\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"8062\",\r\n"
					+ "        \"description\": \"Hospital - Hospitals\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"8050\",\r\n"
					+ "        \"description\": \"Others - Nursing and personal care facilities\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"8049\",\r\n"
					+ "        \"description\": \"Others - Podiatrists and chiropodists\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"8043\",\r\n"
					+ "        \"description\": \"Others - Opticians, optical goods and eyeglasses\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"8042\",\r\n"
					+ "        \"description\": \"Others - Optometrists and Ophthalmologists\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"8041\",\r\n"
					+ "        \"description\": \"Others - Chiropractors\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"8031\",\r\n" + "        \"description\": \"Others - Osteopaths\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"8021\",\r\n"
					+ "        \"description\": \"Others - Dentists and orthodontists\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"7998\",\r\n"
					+ "        \"description\": \"Others - Aquariums, seaquariums and dolphinariums\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"7997\",\r\n"
					+ "        \"description\": \"Others - Membership clubs (sports, recreation, athletic), country clubs and private golf courses\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"7996\",\r\n"
					+ "        \"description\": \"Entertainment - Exhibition/Events/Theme Park\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"7994\",\r\n"
					+ "        \"description\": \"Entertainment - Gaming Zone\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"7993\",\r\n"
					+ "        \"description\": \"Others - Video amusement game supplies\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"7992\",\r\n"
					+ "        \"description\": \"Others - Public golf courses\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"7991\",\r\n"
					+ "        \"description\": \"Others - Tourist attractions and exhibits\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"7941\",\r\n"
					+ "        \"description\": \"Others - Commercial sports, professional sports clubs, athletic fields and sports promoters\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"7933\",\r\n"
					+ "        \"description\": \"Others - Bowling alleys\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"7932\",\r\n"
					+ "        \"description\": \"Others - Billiard and pool establishments\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"7922\",\r\n"
					+ "        \"description\": \"Others - Theatrical producers (except motion pictures) and ticket agencies\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"7911\",\r\n"
					+ "        \"description\": \"Others - Dance halls, studios and schools\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"7841\",\r\n"
					+ "        \"description\": \"Others - Video tape rentals\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"7832\",\r\n"
					+ "        \"description\": \"Entertainment - Cinema Hall/Multiplex\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"7829\",\r\n"
					+ "        \"description\": \"Others - Motion picture and video tape production and distribution\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"7692\",\r\n"
					+ "        \"description\": \"Others - Welding services\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"7641\",\r\n"
					+ "        \"description\": \"Others - Furniture reupholstery, repair and refinishing\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"7631\",\r\n"
					+ "        \"description\": \"Others - Watch, clock and jewellery repair shops\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"7629\",\r\n"
					+ "        \"description\": \"Others - Electrical and small appliance repair shops\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"7623\",\r\n"
					+ "        \"description\": \"Others - Air conditioning and refrigeration repair shops\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"7622\",\r\n"
					+ "        \"description\": \"Others - Electronics repair shops\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"7549\",\r\n" + "        \"description\": \"Others - Towing services\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"7542\",\r\n"
					+ "        \"description\": \"Others - Car washes\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"7538\",\r\n"
					+ "        \"description\": \"Others - Automotive service shops (non-dealer)\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"7535\",\r\n"
					+ "        \"description\": \"Others - Automotive paint shops\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"7534\",\r\n"
					+ "        \"description\": \"Others - Tyre retreading and repair shops\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"7531\",\r\n"
					+ "        \"description\": \"Others - Automotive body repair shops\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"7523\",\r\n"
					+ "        \"description\": \"Others - Parking lots and garages\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"7519\",\r\n"
					+ "        \"description\": \"Others - Motor home and recreational vehicle rentals\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"7513\",\r\n"
					+ "        \"description\": \"Others - Truck and utility trailer rentals\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"7512\",\r\n"
					+ "        \"description\": \"Travel - Rental Car\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"7409\",\r\n" + "        \"description\": \"Digital Account Opening - \"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"7408\",\r\n"
					+ "        \"description\": \"Entities Facilitating P2P Lending - \"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"7407\",\r\n"
					+ "        \"description\": \"P2PM CHANGES - \"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"7395\",\r\n"
					+ "        \"description\": \"Others - Photofinishing laboratories and photo developing\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"7394\",\r\n"
					+ "        \"description\": \"Others - Equipment, tool, furniture and appliance rentals and leasing\\n\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"7393\",\r\n"
					+ "        \"description\": \"Others - Detective agencies, protective agencies and security services, including armoured cars and guard dogs\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"7392\",\r\n"
					+ "        \"description\": \"Others - Management, consulting and public relations services\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"7375\",\r\n"
					+ "        \"description\": \"Others - Information retrieval services\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"7372\",\r\n"
					+ "        \"description\": \"Others - Computer programming, data processing and integrated systems design services\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"7361\",\r\n"
					+ "        \"description\": \"Others - Employment agencies and temporary help services\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"7349\",\r\n"
					+ "        \"description\": \"Others - Cleaning, maintenance and janitorial services\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"7342\",\r\n"
					+ "        \"description\": \"Others - Exterminating and disinfecting services\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"7339\",\r\n"
					+ "        \"description\": \"Others - Stenographic and secretarial support services\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"7338\",\r\n"
					+ "        \"description\": \"Others - Quick copy, reproduction and blueprinting services\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"7333\",\r\n"
					+ "        \"description\": \"Others - Commercial photography, art and graphics\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"7332\",\r\n"
					+ "        \"description\": \"Retail Shopping - Blueprinting/Photocopy Services\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"7322\",\r\n"
					+ "        \"description\": \"Wholesale/Dealer/B2B - Wholesale/Dealer/B2B\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"7321\",\r\n"
					+ "        \"description\": \"Others - Consumer credit reporting agencies\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"7311\",\r\n"
					+ "        \"description\": \"Others - Advertising services\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"7298\",\r\n"
					+ "        \"description\": \"Beauty/Wellness - Health and Beauty Spa\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"7297\",\r\n"
					+ "        \"description\": \"Others - Massage parlours\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"7296\",\r\n"
					+ "        \"description\": \"Others - Clothing rentals costumes, uniforms and formal wear\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"7278\",\r\n"
					+ "        \"description\": \"Others - Buying and shopping services and clubs\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"7277\",\r\n"
					+ "        \"description\": \"Others - Counselling services debt, marriage and personal\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"7276\",\r\n"
					+ "        \"description\": \"Others - Tax preparation services\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"7273\",\r\n"
					+ "        \"description\": \"Others - Dating and escort services\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"7261\",\r\n"
					+ "        \"description\": \"Others - Funeral services and crematoriums\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"7251\",\r\n"
					+ "        \"description\": \"Others - Shoe repair shops, shoe shine parlours and hat cleaning shops\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"7230\",\r\n"
					+ "        \"description\": \"Others - Beauty and barber shops\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"7221\",\r\n"
					+ "        \"description\": \"Others - Photographic studios\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"7217\",\r\n"
					+ "        \"description\": \"Others - Carpet and upholstery cleaning\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"7216\",\r\n"
					+ "        \"description\": \"Others - Dry cleaners\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"7211\",\r\n"
					+ "        \"description\": \"Others - Laundry services family and commercial\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"7210\",\r\n"
					+ "        \"description\": \"Others - Laundry, cleaning and garment services\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"7033\",\r\n"
					+ "        \"description\": \"Others - Trailer parks and camp-sites\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"7032\",\r\n"
					+ "        \"description\": \"Others - Sporting and recreational camps\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"7013\",\r\n"
					+ "        \"description\": \"Small PPI wallet loading - \"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"7012\",\r\n" + "        \"description\": \"Others - Timeshares\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"7011\",\r\n"
					+ "        \"description\": \"Others - Lodging hotels, motels and resorts\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"6666\",\r\n"
					+ "        \"description\": \"International merchant services - \"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"6540\",\r\n"
					+ "        \"description\": \"Others - Debit card to wallet credit (Wallet top up)\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"6513\",\r\n"
					+ "        \"description\": \"Others - Real Estate Agents and Managers - Rentals\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"6300\",\r\n"
					+ "        \"description\": \"Insurance - Insurance sales, underwriting and premiums\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"6211\",\r\n"
					+ "        \"description\": \"Others - Securities brokers and dealers\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"6051\",\r\n"
					+ "        \"description\": \"Others - Non-financial institutions foreign currency, money orders (not wire transfer), scrip and travellers checks\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"6012\",\r\n"
					+ "        \"description\": \"Others - Financial institutions merchandise and services\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"6011\",\r\n"
					+ "        \"description\": \"Others - Financial institutions automated cash disbursements\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"6010\",\r\n"
					+ "        \"description\": \"Others - Financial institutions manual cash disbursements\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5998\",\r\n"
					+ "        \"description\": \"Others - Tent and awning shops\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"5997\",\r\n"
					+ "        \"description\": \"Others - Electric razor outlets sales and service\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5996\",\r\n"
					+ "        \"description\": \"Others - Swimming pools sales, supplies and services\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5995\",\r\n"
					+ "        \"description\": \"Others - Pet shops, pet food and supplies\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"5994\",\r\n"
					+ "        \"description\": \"Others - Newsagents and news-stands\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"5993\",\r\n"
					+ "        \"description\": \"Others - Cigar shops and stands\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"5992\",\r\n" + "        \"description\": \"Others - Florists\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5983\",\r\n"
					+ "        \"description\": \"Others - Fuel dealers fuel oil, wood, coal and liquefied petroleum\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5978\",\r\n"
					+ "        \"description\": \"Others - Typewriter outlets sales, service and rentals\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5977\",\r\n"
					+ "        \"description\": \"Others - Cosmetic Stores\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"5976\",\r\n"
					+ "        \"description\": \"Others - Orthopaedic goods and prosthetic devices\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5975\",\r\n"
					+ "        \"description\": \"Others - Hearing aids sales, service and supplies\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5973\",\r\n"
					+ "        \"description\": \"Others - Religious goods and shops\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"5972\",\r\n"
					+ "        \"description\": \"Others - Stamp and coin shops\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"5971\",\r\n"
					+ "        \"description\": \"Others - Art dealers and galleries\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"5970\",\r\n"
					+ "        \"description\": \"Others - Artist supply and craft shops\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"5968\",\r\n"
					+ "        \"description\": \"Others - Direct marketing continuity/subscription merchants\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5967\",\r\n"
					+ "        \"description\": \"Others - Direct marketing inbound telemarketing merchants\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5966\",\r\n"
					+ "        \"description\": \"Others - Direct marketing outbound telemarketing merchants\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5965\",\r\n"
					+ "        \"description\": \"Others - Direct marketing combination catalogue and retail merchants\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5964\",\r\n"
					+ "        \"description\": \"Others - Direct marketing catalogue merchants\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"5963\",\r\n"
					+ "        \"description\": \"Others - Door-to-door sales\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"5962\",\r\n"
					+ "        \"description\": \"Others - Telemarketing travel-related arrangement services\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5961\",\r\n"
					+ "        \"description\": \"Others - MailOrderHouses\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"5960\",\r\n"
					+ "        \"description\": \"Insurance - Direct marketing - insurance services\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5950\",\r\n"
					+ "        \"description\": \"Others - Glassware and crystal shops\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"5949\",\r\n"
					+ "        \"description\": \"Others - Sewing, needlework, fabric and piece goods shops\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5948\",\r\n"
					+ "        \"description\": \"Others - Luggage and leather goods shops\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"5947\",\r\n"
					+ "        \"description\": \"Others - Gift, card, novelty and souvenir shops\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"5946\",\r\n"
					+ "        \"description\": \"Others - Camera and photographic supply shops\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"5945\",\r\n"
					+ "        \"description\": \"Others - Hobby, toy and game shops\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"5944\",\r\n"
					+ "        \"description\": \"Others - Jewellery, watch, clock and silverware shops\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5943\",\r\n"
					+ "        \"description\": \"Others - Stationery, office and school supply shops\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5942\",\r\n"
					+ "        \"description\": \"Others - Bookshops\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"5941\",\r\n"
					+ "        \"description\": \"Others - Sporting goods shops\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"5940\",\r\n"
					+ "        \"description\": \"Others - Bicycle shops sales and service\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"5937\",\r\n"
					+ "        \"description\": \"Others - Antique reproduction shops\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"5935\",\r\n"
					+ "        \"description\": \"Others - Wrecking and salvage yards\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"5933\",\r\n"
					+ "        \"description\": \"Others - Pawn shops\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"5932\",\r\n"
					+ "        \"description\": \"Others - AntiqueShopsâ\\u20ac\\u201cSales,Repairs&Restoration\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5931\",\r\n"
					+ "        \"description\": \"Others - Used merchandise and second-hand shops\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"5921\",\r\n"
					+ "        \"description\": \"Others - Package shops beer, wine and liquor\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"5912\",\r\n"
					+ "        \"description\": \"Retail Shopping - Medicine Shops\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"5832\",\r\n"
					+ "        \"description\": \"Antique Shops Ã¢â\\u201a¬â\\u20acœ Sales, Repairs, and Restoration Services - \"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5818\",\r\n"
					+ "        \"description\": \"Others - Digital Goods: Large Digital Goods Merchant\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5817\",\r\n"
					+ "        \"description\": \"Others - Digital Goods: Applications (Excludes Games)\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5816\",\r\n"
					+ "        \"description\": \"Others - Digital Goods: Games\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"5815\",\r\n"
					+ "        \"description\": \"Others - Digital Goods: Media, Books, Movies, Music\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5814\",\r\n"
					+ "        \"description\": \"Food & Beverages - Juice Shop\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"5813\",\r\n" + "        \"description\": \"Food & Beverages - Bar/Pub\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5812\",\r\n"
					+ "        \"description\": \"Food & Beverages - Eating Places & Restaurants\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"5811\",\r\n"
					+ "        \"description\": \"Others - Caterers\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"5735\",\r\n" + "        \"description\": \"Others - Record shops\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5734\",\r\n"
					+ "        \"description\": \"Others - Computer software outlets\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"5733\",\r\n"
					+ "        \"description\": \"Others - Music shops musical instruments, pianos and sheet music\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5732\",\r\n"
					+ "        \"description\": \"Electronics & Durables - Electronic & Mobile Shop\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5722\",\r\n"
					+ "        \"description\": \"Electronics & Durables - Home Appliances\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"5718\",\r\n"
					+ "        \"description\": \"Others - Fireplaces, fireplace screens and accessories shops\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5715\",\r\n"
					+ "        \"description\": \"Others - Alcoholic beverage wholesalers\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"5714\",\r\n"
					+ "        \"description\": \"Others - Drapery, window covering and upholstery shops\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5713\",\r\n"
					+ "        \"description\": \"Others - Floor covering services\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"5712\",\r\n"
					+ "        \"description\": \"Retail Shopping - Furniture Shops\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"5698\",\r\n"
					+ "        \"description\": \"Clothing Store - Wig and toupee shops\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"5697\",\r\n"
					+ "        \"description\": \"Clothing Store - Tailors, seamstresses, mending and alterations\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5691\",\r\n"
					+ "        \"description\": \"Clothing Store - Mens and womens clothing shops\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"5681\",\r\n"
					+ "        \"description\": \"Clothing Store - Furriers and fur shops\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"5661\",\r\n"
					+ "        \"description\": \"Retail Shopping - Shoe Shops\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"5655\",\r\n"
					+ "        \"description\": \"Retail Shopping - Sports & Riding Apparel Shops\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"5651\",\r\n"
					+ "        \"description\": \"Retail Shopping - Family Clothing Shops\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"5641\",\r\n"
					+ "        \"description\": \"Clothing Store - ChildrenÃ¢â\\u201a¬â\\u201e¢s and infants wear shops\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5631\",\r\n"
					+ "        \"description\": \"Clothing Store - WomenÃ¢â\\u201a¬â\\u201e¢s accessory and speciality shops\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5621\",\r\n"
					+ "        \"description\": \"Clothing Store - WomenÃ¢â\\u201a¬â\\u201e¢s ready-to-wear shops\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5611\",\r\n"
					+ "        \"description\": \"Clothing Store - MenÃ¢â\\u201a¬â\\u201e¢s and boys clothing and accessory shops\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5598\",\r\n"
					+ "        \"description\": \"Retail - Snowmobile dealers\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"5592\",\r\n" + "        \"description\": \"Retail - Motor home dealers\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5571\",\r\n"
					+ "        \"description\": \"Automobile - Motorcycle\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"5561\",\r\n"
					+ "        \"description\": \"Retail - Camper, recreational and utility trailer dealers\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5551\",\r\n"
					+ "        \"description\": \"Retail - Boat dealers\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"5542\",\r\n"
					+ "        \"description\": \"Fuel - Automated fuel dispensers\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"5541\",\r\n"
					+ "        \"description\": \"Fuel - Service stations (with or without ancillary services)\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5533\",\r\n"
					+ "        \"description\": \"Automobile - Spare Parts/Accessories\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"5532\",\r\n"
					+ "        \"description\": \"Retail - Automotive tyre outlets\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"5531\",\r\n"
					+ "        \"description\": \"Retail - Auto and home supply outlets\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"5521\",\r\n"
					+ "        \"description\": \"Retail - Car and truck dealers (used only) sales, service, repairs, parts and leasing\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5511\",\r\n"
					+ "        \"description\": \"Automobile - Automobile Dealer/Repair/Service\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"5462\",\r\n"
					+ "        \"description\": \"Food & Beverages - Bakery/Namkeen/Sweets\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"5451\",\r\n"
					+ "        \"description\": \"Dairy/Fresh Products - Milk Booth/Dairy\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"5441\",\r\n"
					+ "        \"description\": \"Retail - Candy, nut and confectionery shops\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"5422\",\r\n"
					+ "        \"description\": \"Retail - Freezer and locker meat provisioners\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"5413\",\r\n"
					+ "        \"description\": \"Credit Card Bill Payments - \"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"5412\",\r\n" + "        \"description\": \"Purchase of digital gold - \"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5411\",\r\n"
					+ "        \"description\": \"Retail Shopping - Grocery/Supermarket/Vegetables\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"5311\",\r\n"
					+ "        \"description\": \"Retail - Department stores\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"5310\",\r\n" + "        \"description\": \"Retail - Discount shops\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5309\",\r\n"
					+ "        \"description\": \"Retail - Duty-free shops\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"5300\",\r\n" + "        \"description\": \"Retail - Wholesale clubs\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5271\",\r\n"
					+ "        \"description\": \"Retail - Mobile home dealers\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"5262\",\r\n" + "        \"description\": \"Online Marketplaces - \"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5261\",\r\n"
					+ "        \"description\": \"Retail - Lawn and garden supply outlets, including nurseries\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5251\",\r\n"
					+ "        \"description\": \"Retail - Hardware shops\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"5231\",\r\n"
					+ "        \"description\": \"Retail - Glass, paint and wallpaper shops\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"5211\",\r\n"
					+ "        \"description\": \"Retail - Lumber and building materials outlets\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"5200\",\r\n"
					+ "        \"description\": \"Retail - Home supply warehouse outlets\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"5198\",\r\n"
					+ "        \"description\": \"Retail - Paints, varnishes and supplies\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"5193\",\r\n"
					+ "        \"description\": \"Retail - Florists supplies, nursery stock and flowers\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5192\",\r\n"
					+ "        \"description\": \"Retail - Books, periodicals and newspapers\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"5172\",\r\n"
					+ "        \"description\": \"Fuel & Gas - Petrol/CNG Pump\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"5139\",\r\n"
					+ "        \"description\": \"Retail - Commercial footwear\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"5137\",\r\n"
					+ "        \"description\": \"Retail - Mens, womens and childrens uniforms and commercial clothing\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5131\",\r\n"
					+ "        \"description\": \"Retail - Piece goods, notions and other dry goods\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5122\",\r\n"
					+ "        \"description\": \"Retail - Drugs, drug proprietors\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"5111\",\r\n"
					+ "        \"description\": \"Retail - Stationery, office supplies and printing and writing paper\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5094\",\r\n"
					+ "        \"description\": \"Retail - Precious stones and metals, watches and jewellery\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5074\",\r\n"
					+ "        \"description\": \"Retail - Plumbing and heating equipment and supplies\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5072\",\r\n"
					+ "        \"description\": \"Retail - Hardware equipment and supplies\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"5065\",\r\n"
					+ "        \"description\": \"Retail - Electrical parts and equipment\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"5051\",\r\n"
					+ "        \"description\": \"Retail - Metal service centres and offices\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"5047\",\r\n"
					+ "        \"description\": \"Retail - Dental/laboratory/medical/ophthalmic hospital equipment and supplies\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5044\",\r\n"
					+ "        \"description\": \"Retail - Office, photographic, photocopy and microfilm equipment\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"5021\",\r\n"
					+ "        \"description\": \"Retail - Office and commercial furniture\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"5013\",\r\n"
					+ "        \"description\": \"Retail - Motor vehicle supplies and new parts\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"4900\",\r\n"
					+ "        \"description\": \"Fuel & Gas - Gas Agency\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"4899\",\r\n"
					+ "        \"description\": \"Utilities - Cable and other pay television services\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"4829\",\r\n"
					+ "        \"description\": \"Utilities - Wire transfers and money orders\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"4816\",\r\n"
					+ "        \"description\": \"Utilities - Computer network/information services\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"4814\",\r\n"
					+ "        \"description\": \"Utilities - Telecommunication services, including local and long distance calls, credit card calls, calls through use of magnetic stripe reading telephones and faxes\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"4812\",\r\n"
					+ "        \"description\": \"Utilities - Telecommunication equipment and telephone sales\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"4784\",\r\n"
					+ "        \"description\": \"Transportation - Tolls and bridge fees\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"4722\",\r\n"
					+ "        \"description\": \"Travel - Travel Agency\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"4582\",\r\n"
					+ "        \"description\": \"Transportation - Airports, flying fields and airport terminals\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"4511\",\r\n"
					+ "        \"description\": \"Transportation - Airlines and air carriers\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"4468\",\r\n"
					+ "        \"description\": \"Transportation - Marinas, marine service and supplies\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"4457\",\r\n"
					+ "        \"description\": \"Transportation - Boat rentals and leasing\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"4411\",\r\n"
					+ "        \"description\": \"Transportation - Steamships and cruise lines\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"4225\",\r\n"
					+ "        \"description\": \"Transportation - Public warehousing and storage farm products, refrigerated goods and household goods\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"4215\",\r\n"
					+ "        \"description\": \"Transportation - Courier services - air and ground and freight forwarders\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"4214\",\r\n"
					+ "        \"description\": \"Transportation - Motor Freight Carriers,TruckingÃ¢â\\u201a¬â\\u20acœLocal/Long Distance,\\nMoving and Storage Companies, Local Delivery\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"4131\",\r\n"
					+ "        \"description\": \"Transportation - Bus Lines\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"4121\",\r\n" + "        \"description\": \"Travel - Taxi Cab\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"4119\",\r\n"
					+ "        \"description\": \"Transportation - Ambulance Services\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"4112\",\r\n"
					+ "        \"description\": \"Transportation - Passenger Railways\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"4111\",\r\n"
					+ "        \"description\": \"Transportation - TransportationÃ¢â\\u201a¬â\\u20acœSuburban and Local Commuter\\nPassenger, including Ferries\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"4011\",\r\n"
					+ "        \"description\": \"Transportation - Railroads\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"2842\",\r\n"
					+ "        \"description\": \"Contractual Services - Speciality cleaning, polishing and sanitation preparations\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"2791\",\r\n"
					+ "        \"description\": \"Contractual Services - Typesetting, platemaking and related services\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"1771\",\r\n"
					+ "        \"description\": \"Contractual Services - Concrete work contractors\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"1761\",\r\n"
					+ "        \"description\": \"Contractual Services - Roofing, siding and sheet metal work contractors\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"1750\",\r\n"
					+ "        \"description\": \"Contractual Services - Carpentry contractors\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"1740\",\r\n"
					+ "        \"description\": \"Contractual Services - Masonry, stonework, tile setting, plastering and insulation contractors\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"1731\",\r\n"
					+ "        \"description\": \"Contractual Services - Electrical contractors\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"1711\",\r\n"
					+ "        \"description\": \"Contractual Services - Heating, plumbing and air-conditioning contractors\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"1520\",\r\n"
					+ "        \"description\": \"Contractual Services - General contractors - residential and commercial\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"1221\",\r\n"
					+ "        \"description\": \"NETS \"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"0825\",\r\n"
					+ "        \"description\": \"Agricultural Services - Other Agri Inputs\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"0824\",\r\n"
					+ "        \"description\": \"Agricultural Services - Agricultural Machinery\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"0823\",\r\n"
					+ "        \"description\": \"Agricultural Services - Farm Equipment\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"0822\",\r\n"
					+ "        \"description\": \"Agricultural Services - Seeds\"\r\n" + "    },\r\n" + "    {\r\n"
					+ "        \"code\": \"0821\",\r\n"
					+ "        \"description\": \"Agricultural Services - Pesticides / Insecticides\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"0820\",\r\n"
					+ "        \"description\": \"Agricultural Services - Fertilizer Dealers\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"0780\",\r\n"
					+ "        \"description\": \"Agricultural Services - Landscaping and horticultural services\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"0763\",\r\n"
					+ "        \"description\": \"Agricultural Services - Agricultural co-operatives\"\r\n"
					+ "    },\r\n" + "    {\r\n" + "        \"code\": \"0744\",\r\n"
					+ "        \"description\": \"Agricultural Services - Champagne producers\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"0743\",\r\n"
					+ "        \"description\": \"Agricultural Services - Wine producers\"\r\n" + "    },\r\n"
					+ "    {\r\n" + "        \"code\": \"0742\",\r\n"
					+ "        \"description\": \"Agricultural Services - Veterinary services\"\r\n" + "    }\r\n"
					+ "]";

			List<MCCListPayload> listPayloads = new ArrayList<MCCListPayload>();
			org.json.JSONArray jsonArray = new org.json.JSONArray(respoString);
			for (int i = 0; i < jsonArray.length(); i++) {
				MCCListPayload listPayload = new MCCListPayload();
				org.json.JSONObject jsonObject = jsonArray.getJSONObject(i);
				String code = jsonObject.getString("code");
				String description = jsonObject.getString("description");
				listPayload.setCode(code);
				listPayload.setDescription(description);

				listPayloads.add(listPayload);
			}
			map.put("mccList", listPayloads);
		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}

		return map;
	}

	@Override
	public Map<String, Object> getMccDetailsList2() {
		Map<String, Object> map = new HashMap<>();
		try {

			String mcc2 = "\"[{\\\"code\\\":1520,\\\"description\\\":\\\"GeneralContractors–ResidentialandCommercial\\\"},{\\\"code\\\":1711,\\\"description\\\":\\\"AirConditioning,HeatingandPlumbingContractors\\\"},{\\\"code\\\":1731,\\\"description\\\":\\\"ElectricalContractors\\\"},{\\\"code\\\":1740,\\\"description\\\":\\\"Insulation,Masonry,Plastering,StoneworkandTileSettingContractors\\\"},{\\\"code\\\":1750,\\\"description\\\":\\\"CarpentryContractors\\\"},{\\\"code\\\":1761,\\\"description\\\":\\\"RoofingandSiding,SheetMetalWorkContractors\\\"},{\\\"code\\\":1771,\\\"description\\\":\\\"ConcreteWorkContractors\\\"},{\\\"code\\\":1799,\\\"description\\\":\\\"Contractors,SpecialTradeContractors\\\"},{\\\"code\\\":2741,\\\"description\\\":\\\"MiscellaneousPublishingandPrinting\\\"},{\\\"code\\\":2791,\\\"description\\\":\\\"Typesetting,PlateMakingandRelatedServices\\\"},{\\\"code\\\":2842,\\\"description\\\":\\\"Sanitation,PolishingandSpecialtyCleaningPreparations\\\"},{\\\"code\\\":4011,\\\"description\\\":\\\"Railroads–Freight\\\"},{\\\"code\\\":4111,\\\"description\\\":\\\"Transportation–SuburbanandLocalCommuterPassenger,includingFerries\\\"},{\\\"code\\\":4112,\\\"description\\\":\\\"PassengerRailways\\\"},{\\\"code\\\":4119,\\\"description\\\":\\\"AmbulanceServices\\\"},{\\\"code\\\":4121,\\\"description\\\":\\\"TaxicabsandLimousines\\\"},{\\\"code\\\":4131,\\\"description\\\":\\\"BusLines\\\"},{\\\"code\\\":4214,\\\"description\\\":\\\"MotorFreightCarriers,Trucking,Moving&StorageCompanies\\\"},{\\\"code\\\":4215,\\\"description\\\":\\\"CourierServices–AirandGround,Freight\\\"},{\\\"code\\\":4225,\\\"description\\\":\\\"PublicWarehousing–FarmProducts,RefrigeratedGoods\\\"},{\\\"code\\\":4411,\\\"description\\\":\\\"CruiseLines\\\"},{\\\"code\\\":4457,\\\"description\\\":\\\"BoatLeasesandBoatRentals\\\"},{\\\"code\\\":4468,\\\"description\\\":\\\"Marinas,MarineService\\\\/Supplies\\\"},{\\\"code\\\":4511,\\\"description\\\":\\\",AirlinesAirCarriers\\\"},{\\\"code\\\":4582,\\\"description\\\":\\\"Airports,AirportTerminals,FlyingFields\\\"},{\\\"code\\\":4722,\\\"description\\\":\\\"TravelAgenciesandTourOperators\\\"},{\\\"code\\\":4761,\\\"description\\\":\\\"TelemarketingofTravelRelatedServices\\\"},{\\\"code\\\":4784,\\\"description\\\":\\\"BridgeandRoadFees,Tolls\\\"},{\\\"code\\\":4789,\\\"description\\\":\\\"TransportationServices-Notelsewhereclassified\\\"},{\\\"code\\\":4812,\\\"description\\\":\\\"TelecommunicationEquipmentIncludingTelephoneSales\\\"},{\\\"code\\\":4814,\\\"description\\\":\\\"TelecommunicationServicesincludingprepaidphoneservicesandrecurringphoneservices\\\"},{\\\"code\\\":4816,\\\"description\\\":\\\"ComputerNetwork\\\\/InformationServices\\\"},{\\\"code\\\":4821,\\\"description\\\":\\\"TelegraphServices\\\"},{\\\"code\\\":4899,\\\"description\\\":\\\"Cable,Satellite&OtherTelevision\\\\/RadioServices\\\"},{\\\"code\\\":4900,\\\"description\\\":\\\"Utilities–Electric,Gas,HeatingOil,Sanitary,Water\\\"},{\\\"code\\\":5013,\\\"description\\\":\\\"MotorVehicleSuppliesandNewParts\\\"},{\\\"code\\\":5021,\\\"description\\\":\\\"OfficeandCommercialFurniture\\\"},{\\\"code\\\":5039,\\\"description\\\":\\\"ConstructionMaterials\\\"},{\\\"code\\\":5044,\\\"description\\\":\\\"Office,Photographic,PhotocopyandMicrofilmEquipment\\\"},{\\\"code\\\":5045,\\\"description\\\":\\\"Computers,ComputerEquipment,Software\\\"},{\\\"code\\\":5046,\\\"description\\\":\\\"CommercialEquipment\\\"},{\\\"code\\\":5047,\\\"description\\\":\\\"Dental\\\\/Laboratory\\\\/Medical\\\\/OphthalmicHospitalEquipmentandSupplies\\\"},{\\\"code\\\":5051,\\\"description\\\":\\\"MetalServiceCentresandOffices\\\"},{\\\"code\\\":5065,\\\"description\\\":\\\"ElectricalPartsandEquipment\\\"},{\\\"code\\\":5072,\\\"description\\\":\\\"HardwareEquipmentandSupplies\\\"},{\\\"code\\\":5074,\\\"description\\\":\\\"PlumbingandHeatingEquipment\\\"},{\\\"code\\\":5085,\\\"description\\\":\\\"IndustrialSupplies\\\"},{\\\"code\\\":5094,\\\"description\\\":\\\"PreciousStonesandMetals,WatchesandJewellery\\\"},{\\\"code\\\":5099,\\\"description\\\":\\\"DurableGoods\\\"},{\\\"code\\\":5111,\\\"description\\\":\\\"Stationery,OfficeSupplies,PrintingandWritingPaper\\\"},{\\\"code\\\":5122,\\\"description\\\":\\\"Drugs,DrugProprietorsandDruggistsSundries\\\"},{\\\"code\\\":5131,\\\"description\\\":\\\"PieceGoods,Notions,andOtherDryGoods\\\"},{\\\"code\\\":5137,\\\"description\\\":\\\"Men’s,Women’sandChildren’sUniformsandCommercialClothing\\\"},{\\\"code\\\":5139,\\\"description\\\":\\\"CommercialFootwear\\\"},{\\\"code\\\":5169,\\\"description\\\":\\\"ChemicalsandAlliedProducts\\\"},{\\\"code\\\":5172,\\\"description\\\":\\\"PetroleumandPetroleumProducts\\\"},{\\\"code\\\":5192,\\\"description\\\":\\\"Books,PeriodicalsandNewspapers\\\"},{\\\"code\\\":5193,\\\"description\\\":\\\"FloristsSupplies,NurseryStockandFlowers\\\"},{\\\"code\\\":5198,\\\"description\\\":\\\"Paints,VarnishesandSupplies\\\"},{\\\"code\\\":5199,\\\"description\\\":\\\"Non-DurableGoods\\\"},{\\\"code\\\":5200,\\\"description\\\":\\\"HomeSupplyWarehouseStores\\\"},{\\\"code\\\":5211,\\\"description\\\":\\\"BuildingMaterials,LumberStores\\\"},{\\\"code\\\":5231,\\\"description\\\":\\\"Glass,Paint,WallpaperStores\\\"},{\\\"code\\\":5251,\\\"description\\\":\\\"HardwareStores\\\"},{\\\"code\\\":5261,\\\"description\\\":\\\"LawnandGardenSupplyStores\\\"},{\\\"code\\\":5271,\\\"description\\\":\\\"MobileHomeDealers\\\"},{\\\"code\\\":5299,\\\"description\\\":\\\"WarehouseClubGas\\\"},{\\\"code\\\":5300,\\\"description\\\":\\\"WholesaleClubs\\\"},{\\\"code\\\":5309,\\\"description\\\":\\\"DutyFreeStores\\\"},{\\\"code\\\":5310,\\\"description\\\":\\\"DiscountStores\\\"},{\\\"code\\\":5311,\\\"description\\\":\\\"DepartmentStores\\\"},{\\\"code\\\":5331,\\\"description\\\":\\\"VarietyStores\\\"},{\\\"code\\\":5399,\\\"description\\\":\\\"MiscellaneousGeneralMerchandiseStores\\\"},{\\\"code\\\":5411,\\\"description\\\":\\\"GroceryStores,Supermarkets\\\"},{\\\"code\\\":5422,\\\"description\\\":\\\"FreezerandLockerMeatProvisionals\\\"},{\\\"code\\\":5441,\\\"description\\\":\\\"Candy,NutandConfectioneryStores\\\"},{\\\"code\\\":5451,\\\"description\\\":\\\"DairyProductsStores\\\"},{\\\"code\\\":5462,\\\"description\\\":\\\"Bakeries\\\"},{\\\"code\\\":5499,\\\"description\\\":\\\"MiscellaneousFoodStores–ConvenienceStores\\\"},{\\\"code\\\":5511,\\\"description\\\":\\\"AutomobileandTruckDealers–Sales,Repairs,PartsandLeasing,Service\\\"},{\\\"code\\\":5521,\\\"description\\\":\\\"AutomobileandTruckDealers–(UsedOnly)–Sales\\\"},{\\\"code\\\":5532,\\\"description\\\":\\\"AutomotiveTireStores\\\"},{\\\"code\\\":5533,\\\"description\\\":\\\"AutomotivePartsandAccessoriesStores\\\"},{\\\"code\\\":5541,\\\"description\\\":\\\"ServiceStations(WithorWithoutAncillaryServices)\\\"},{\\\"code\\\":5542,\\\"description\\\":\\\"FuelDispenser,Automated\\\"},{\\\"code\\\":5551,\\\"description\\\":\\\"BoatDealers\\\"},{\\\"code\\\":5561,\\\"description\\\":\\\"CamperDealers,RecreationalandUtilityTrailers\\\"},{\\\"code\\\":5571,\\\"description\\\":\\\"MotorcycleShopsandDealers\\\"},{\\\"code\\\":5592,\\\"description\\\":\\\"MotorHomeDealers\\\"},{\\\"code\\\":5598,\\\"description\\\":\\\"SnowmobileDealers\\\"},{\\\"code\\\":5599,\\\"description\\\":\\\"MiscellaneousAutomotive,Aircraft,andFarmEquipmentDealers\\\"},{\\\"code\\\":5611,\\\"description\\\":\\\"Men’sandBoys’ClothingandAccessoriesStores\\\"},{\\\"code\\\":5621,\\\"description\\\":\\\"Women’sReadytoWearStores\\\"},{\\\"code\\\":5631,\\\"description\\\":\\\"Women’sAccessoryandSpecialtyStores\\\"},{\\\"code\\\":5641,\\\"description\\\":\\\"Children’sandInfants’WearStores\\\"},{\\\"code\\\":5651,\\\"description\\\":\\\"FamilyClothingStores\\\"},{\\\"code\\\":5655,\\\"description\\\":\\\"SportsApparel,andRidingApparelStores\\\"},{\\\"code\\\":5661,\\\"description\\\":\\\"ShoeStores\\\"},{\\\"code\\\":5681,\\\"description\\\":\\\"FurriersandFurShops\\\"},{\\\"code\\\":5691,\\\"description\\\":\\\"Men’sandWomen’sClothingStores\\\"},{\\\"code\\\":5697,\\\"description\\\":\\\"Alterations,Mending,Seamstresses,Tailors\\\"},{\\\"code\\\":5698,\\\"description\\\":\\\"WigandToupeeShops\\\"},{\\\"code\\\":5699,\\\"description\\\":\\\"AccessoryandApparelStores–Miscellaneous\\\"},{\\\"code\\\":5712,\\\"description\\\":\\\"Equipment,FurnitureandHomeFurnishingsStores\\\"},{\\\"code\\\":5713,\\\"description\\\":\\\"FloorCoveringStores\\\"},{\\\"code\\\":5714,\\\"description\\\":\\\"Drapery,UpholsteryandWindowCoveringsStores\\\"},{\\\"code\\\":5718,\\\"description\\\":\\\"Fireplace,FireplaceScreensandAccessoriesStores\\\"},{\\\"code\\\":5719,\\\"description\\\":\\\"MiscellaneousHouseFurnishingSpecialtyShops\\\"},{\\\"code\\\":5722,\\\"description\\\":\\\"HouseholdApplianceStores\\\"},{\\\"code\\\":5732,\\\"description\\\":\\\"ElectronicsSales\\\"},{\\\"code\\\":5733,\\\"description\\\":\\\"MusicStores–MusicalInstruments\\\"},{\\\"code\\\":5734,\\\"description\\\":\\\"ComputerSoftwareStores\\\"},{\\\"code\\\":5735,\\\"description\\\":\\\"RecordShops\\\"},{\\\"code\\\":5811,\\\"description\\\":\\\"Caterers\\\"},{\\\"code\\\":5812,\\\"description\\\":\\\"EatingPlacesandRestaurants\\\"},{\\\"code\\\":5813,\\\"description\\\":\\\"Bars,CocktailLoungesNightclubsandTaverns–DrinkingPlaces(AlcoholicBeverages)\\\"},{\\\"code\\\":5814,\\\"description\\\":\\\"FastFoodRestaurants\\\"},{\\\"code\\\":5815,\\\"description\\\":\\\"DigitalGoods:Books,Movies,Music\\\"},{\\\"code\\\":5816,\\\"description\\\":\\\"DigitalGoods:Games\\\"},{\\\"code\\\":5817,\\\"description\\\":\\\"DigitalGoods:Applications(ExcludesGames)\\\"},{\\\"code\\\":5818,\\\"description\\\":\\\"DigitalGoods:Multi-Category\\\"},{\\\"code\\\":5912,\\\"description\\\":\\\"DrugStoresandPharmacies\\\"},{\\\"code\\\":5921,\\\"description\\\":\\\"PackageStores–Beer,WineandLiquor\\\"},{\\\"code\\\":5931,\\\"description\\\":\\\"SecondHandStores,UsedMerchandiseStores\\\"},{\\\"code\\\":5932,\\\"description\\\":\\\"AntiqueShops–Sales,Repairs&Restoration\\\"},{\\\"code\\\":5933,\\\"description\\\":\\\"PawnShops\\\"},{\\\"code\\\":5935,\\\"description\\\":\\\"WreckingandSalvageYards\\\"},{\\\"code\\\":5937,\\\"description\\\":\\\"AntiqueReproductionStores\\\"},{\\\"code\\\":5940,\\\"description\\\":\\\"BicycleShops–SalesandService\\\"},{\\\"code\\\":5941,\\\"description\\\":\\\"SportingGoodsStores\\\"},{\\\"code\\\":5942,\\\"description\\\":\\\"BookStores\\\"},{\\\"code\\\":5943,\\\"description\\\":\\\"Office,SchoolSupplyandStationeryStores\\\"},{\\\"code\\\":5944,\\\"description\\\":\\\"Clock,Jewellery,WatchandSilverwareStores\\\"},{\\\"code\\\":5945,\\\"description\\\":\\\"Game,ToyandHobbyShops\\\"},{\\\"code\\\":5946,\\\"description\\\":\\\"CameraandPhotographicSupplyStores\\\"},{\\\"code\\\":5947,\\\"description\\\":\\\"Card,Gift,NoveltyandSouvenirShops\\\"},{\\\"code\\\":5948,\\\"description\\\":\\\"LeatherGoodsandLuggageStores\\\"},{\\\"code\\\":5949,\\\"description\\\":\\\"Fabric,Needlework,PieceGoodsandSewingStores\\\"},{\\\"code\\\":5950,\\\"description\\\":\\\"CrystalandGlasswareStores\\\"},{\\\"code\\\":5960,\\\"description\\\":\\\"DirectMarketing-InsuranceServices\\\"},{\\\"code\\\":5961,\\\"description\\\":\\\"MailOrderHouses\\\"},{\\\"code\\\":5962,\\\"description\\\":\\\"DirectMarketing–TravelRelatedArrangementServices\\\"},{\\\"code\\\":5963,\\\"description\\\":\\\"Door-to-DoorSales\\\"},{\\\"code\\\":5964,\\\"description\\\":\\\"DirectMarketing–CatalogueMerchants\\\"},{\\\"code\\\":5965,\\\"description\\\":\\\"CombinationCatalogueandRetailMerchant\\\"},{\\\"code\\\":5966,\\\"description\\\":\\\"OutboundTelemarketingMerchants\\\"},{\\\"code\\\":5967,\\\"description\\\":\\\"InboundTelemarketingMerchants\\\"},{\\\"code\\\":5968,\\\"description\\\":\\\"DirectMarketing–SubscriptionMerchants\\\"},{\\\"code\\\":5969,\\\"description\\\":\\\"DirectMarketing–OtherDirectMarketers\\\"},{\\\"code\\\":5970,\\\"description\\\":\\\"ArtistSupplyStores,CraftShops\\\"},{\\\"code\\\":5971,\\\"description\\\":\\\"ArtDealersandGalleries\\\"},{\\\"code\\\":5972,\\\"description\\\":\\\"Stamp&CoinStores\\\"},{\\\"code\\\":5973,\\\"description\\\":\\\"ReligiousGoodsStores\\\"},{\\\"code\\\":5974,\\\"description\\\":\\\"RubberStampStore\\\"},{\\\"code\\\":5975,\\\"description\\\":\\\"HearingAids–Sales,Service,SupplyStores\\\"},{\\\"code\\\":5976,\\\"description\\\":\\\"OrthopaedicGoods–ArtificialLimbStores\\\"},{\\\"code\\\":5977,\\\"description\\\":\\\"CosmeticStores\\\"},{\\\"code\\\":5978,\\\"description\\\":\\\"TypewriterStores–Rentals\\\"},{\\\"code\\\":5983,\\\"description\\\":\\\"FuelDealers–Coal\\\"},{\\\"code\\\":5992,\\\"description\\\":\\\"Florists\\\"},{\\\"code\\\":5993,\\\"description\\\":\\\"CigarStoresandStands\\\"},{\\\"code\\\":5994,\\\"description\\\":\\\"NewsDealersandNewsstands\\\"},{\\\"code\\\":5995,\\\"description\\\":\\\"PetShops\\\"},{\\\"code\\\":5996,\\\"description\\\":\\\"SwimmingPools–SalesandSupplies\\\"},{\\\"code\\\":5997,\\\"description\\\":\\\"ElectricRazorStores–SalesandService\\\"},{\\\"code\\\":5998,\\\"description\\\":\\\"TentandAwningShops\\\"},{\\\"code\\\":5999,\\\"description\\\":\\\"MiscellaneousandSpecialtyRetailStores\\\"},{\\\"code\\\":6012,\\\"description\\\":\\\"MutualFunds\\\"},{\\\"code\\\":6211,\\\"description\\\":\\\"Securities-Brokers\\\\/Dealers\\\"},{\\\"code\\\":6300,\\\"description\\\":\\\"InsuranceSales\\\"},{\\\"code\\\":6513,\\\"description\\\":\\\"RealEstateAgentsandManagers–Rentals\\\"},{\\\"code\\\":6760,\\\"description\\\":\\\"SavingsBonds\\\"},{\\\"code\\\":7011,\\\"description\\\":\\\"Lodging–Hotels\\\"},{\\\"code\\\":7012,\\\"description\\\":\\\"Timeshares\\\"},{\\\"code\\\":7032,\\\"description\\\":\\\"SportingandRecreationalCamps\\\"},{\\\"code\\\":7033,\\\"description\\\":\\\"CampgroundsandTrailerParks\\\"},{\\\"code\\\":7210,\\\"description\\\":\\\"Cleaning\\\"},{\\\"code\\\":7211,\\\"description\\\":\\\"LaundryServices–FamilyandCommercial\\\"},{\\\"code\\\":7216,\\\"description\\\":\\\"DryCleaners\\\"},{\\\"code\\\":7217,\\\"description\\\":\\\"CarpetandUpholsteryCleaning\\\"},{\\\"code\\\":7221,\\\"description\\\":\\\"PhotographicStudios\\\"},{\\\"code\\\":7230,\\\"description\\\":\\\"BarberandBeautyShops\\\"},{\\\"code\\\":7251,\\\"description\\\":\\\"ShoeRepairShops\\\"},{\\\"code\\\":7261,\\\"description\\\":\\\"FuneralServiceandCrematories\\\"},{\\\"code\\\":7273,\\\"description\\\":\\\"DatingServices\\\"},{\\\"code\\\":7276,\\\"description\\\":\\\"TaxPreparationService\\\"},{\\\"code\\\":7277,\\\"description\\\":\\\"Debt\\\\/\\\"},{\\\"code\\\":7278,\\\"description\\\":\\\"Buying\\\\/ShoppingClubs\\\"},{\\\"code\\\":7280,\\\"description\\\":\\\"HospitalPatient-PersonalFundsWithdrawal\\\"},{\\\"code\\\":7295,\\\"description\\\":\\\"BabysittingServices\\\"},{\\\"code\\\":7296,\\\"description\\\":\\\"ClothingRental–Costumes\\\"},{\\\"code\\\":7297,\\\"description\\\":\\\"MassageParlours\\\"},{\\\"code\\\":7298,\\\"description\\\":\\\"HealthandBeautySpas\\\"},{\\\"code\\\":7299,\\\"description\\\":\\\"OtherServices–NotElsewhereClassified\\\"},{\\\"code\\\":7311,\\\"description\\\":\\\"AdvertisingServices\\\"},{\\\"code\\\":7321,\\\"description\\\":\\\"ConsumerCreditReportingAgencies\\\"},{\\\"code\\\":7322,\\\"description\\\":\\\"B2BCollections\\\\/DebtCollection\\\\/EMICollection\\\"},{\\\"code\\\":7332,\\\"description\\\":\\\"BlueprintingandPhotocopyingServices\\\"},{\\\"code\\\":7333,\\\"description\\\":\\\"CommercialArt\\\"},{\\\"code\\\":7338,\\\"description\\\":\\\"QuickCopy\\\"},{\\\"code\\\":7339,\\\"description\\\":\\\"StenographicandSecretarialSupportServices\\\"},{\\\"code\\\":7342,\\\"description\\\":\\\"ExterminatingandDisinfectingServices\\\"},{\\\"code\\\":7349,\\\"description\\\":\\\"CleaningandMaintenance\\\"},{\\\"code\\\":7361,\\\"description\\\":\\\"EmploymentAgencies\\\"},{\\\"code\\\":7372,\\\"description\\\":\\\"ComputerProgramming\\\"},{\\\"code\\\":7375,\\\"description\\\":\\\"InformationRetrievalServices\\\"},{\\\"code\\\":7379,\\\"description\\\":\\\"ComputerMaintenance\\\"},{\\\"code\\\":7392,\\\"description\\\":\\\"Consulting\\\"},{\\\"code\\\":7393,\\\"description\\\":\\\"DetectiveAgencies\\\"},{\\\"code\\\":7394,\\\"description\\\":\\\"EquipmentRentalandLeasingServices\\\"},{\\\"code\\\":7395,\\\"description\\\":\\\"PhotoDeveloping\\\"},{\\\"code\\\":7399,\\\"description\\\":\\\"BusinessServices-NotElsewhereClassified\\\"},{\\\"code\\\":7512,\\\"description\\\":\\\"AutomobileRentalAgency\\\"},{\\\"code\\\":7513,\\\"description\\\":\\\"TruckRental\\\"},{\\\"code\\\":7519,\\\"description\\\":\\\"MotorHomeandRecreationalVehicleRental\\\"},{\\\"code\\\":7523,\\\"description\\\":\\\"AutomobileParkingLotsandGarages\\\"},{\\\"code\\\":7531,\\\"description\\\":\\\"AutomotiveBodyRepairShops\\\"},{\\\"code\\\":7534,\\\"description\\\":\\\"TireRethreadingandRepairShops\\\"},{\\\"code\\\":7535,\\\"description\\\":\\\"AutomotivePaintShops\\\"},{\\\"code\\\":7538,\\\"description\\\":\\\"AutomotiveServiceShops\\\"},{\\\"code\\\":7542,\\\"description\\\":\\\"CarWashes\\\"},{\\\"code\\\":7549,\\\"description\\\":\\\"TowingServices\\\"},{\\\"code\\\":7622,\\\"description\\\":\\\"ElectronicRepairShops\\\"},{\\\"code\\\":7623,\\\"description\\\":\\\"AirConditioningandRefrigerationRepairShops\\\"},{\\\"code\\\":7629,\\\"description\\\":\\\"ApplianceRepairShops\\\"},{\\\"code\\\":7631,\\\"description\\\":\\\"Clock\\\"},{\\\"code\\\":7641,\\\"description\\\":\\\"Furniture–Reupholster\\\"},{\\\"code\\\":7692,\\\"description\\\":\\\"WeldingRepair\\\"},{\\\"code\\\":7699,\\\"description\\\":\\\"MiscellaneousRepairShopsandRelatedServices\\\"},{\\\"code\\\":7800,\\\"description\\\":\\\"GovernmentOwnedLottery\\\"},{\\\"code\\\":7802,\\\"description\\\":\\\"Government-LicensedHorse\\\\/DogRacing\\\"},{\\\"code\\\":7829,\\\"description\\\":\\\"MotionPicture&VideoTapeProduction\\\"},{\\\"code\\\":7832,\\\"description\\\":\\\"MotionPictureTheatres\\\"},{\\\"code\\\":7841,\\\"description\\\":\\\"DVD\\\\/VideoTapeRentalStores\\\"},{\\\"code\\\":7911,\\\"description\\\":\\\"DanceHalls\\\"},{\\\"code\\\":7922,\\\"description\\\":\\\"TheatricalProducers\\\"},{\\\"code\\\":7929,\\\"description\\\":\\\"Bands\\\"},{\\\"code\\\":7932,\\\"description\\\":\\\"PoolandBilliardEstablishments\\\"},{\\\"code\\\":7933,\\\"description\\\":\\\"BowlingAlleys\\\"},{\\\"code\\\":7941,\\\"description\\\":\\\"AthleticFields\\\"},{\\\"code\\\":7991,\\\"description\\\":\\\"TouristAttractionsandExhibits\\\"},{\\\"code\\\":7992,\\\"description\\\":\\\"GolfCourses\\\"},{\\\"code\\\":7993,\\\"description\\\":\\\"VideoAmusementGameSupplies\\\"},{\\\"code\\\":7994,\\\"description\\\":\\\"VideoGameArcadesandEstablishments\\\"},{\\\"code\\\":7996,\\\"description\\\":\\\"AmusementParks\\\"},{\\\"code\\\":7997,\\\"description\\\":\\\"Clubs–CountryClubs\\\"},{\\\"code\\\":7998,\\\"description\\\":\\\"Aquariums\\\"},{\\\"code\\\":7999,\\\"description\\\":\\\"RecreationServices\\\"},{\\\"code\\\":8011,\\\"description\\\":\\\"Doctors\\\"},{\\\"code\\\":8021,\\\"description\\\":\\\"DentistsandOrthodontists\\\"},{\\\"code\\\":8031,\\\"description\\\":\\\"OsteopathicPhysicians\\\"},{\\\"code\\\":8041,\\\"description\\\":\\\"Chiropractors\\\"},{\\\"code\\\":8042,\\\"description\\\":\\\"OptometristsandOphthalmologists\\\"},{\\\"code\\\":8043,\\\"description\\\":\\\"Opticians\\\"},{\\\"code\\\":8044,\\\"description\\\":\\\"OpticalGoodsandEyeglasses\\\"},{\\\"code\\\":8049,\\\"description\\\":\\\"Chiropodists\\\"},{\\\"code\\\":8050,\\\"description\\\":\\\"NursingandPersonalCareFacilities\\\"},{\\\"code\\\":8062,\\\"description\\\":\\\"Hospitals\\\"},{\\\"code\\\":8071,\\\"description\\\":\\\"DentalandMedicalLaboratories\\\"},{\\\"code\\\":8099,\\\"description\\\":\\\"HealthPractitioners\\\"},{\\\"code\\\":8111,\\\"description\\\":\\\"Attorneys\\\"},{\\\"code\\\":820,\\\"description\\\":\\\"FertilizerDealers\\\"},{\\\"code\\\":821,\\\"description\\\":\\\"Pesticides\\\\/Insecticides\\\"},{\\\"code\\\":8211,\\\"description\\\":\\\"Schools\\\"},{\\\"code\\\":0822,\\\"description\\\":\\\"Seeds\\\"},{\\\"code\\\":8220,\\\"description\\\":\\\"Colleges\\\"},{\\\"code\\\":823,\\\"description\\\":\\\"FarmEquipment\\\"},{\\\"code\\\":824,\\\"description\\\":\\\"AgriculturalMachinery\\\"},{\\\"code\\\":8241,\\\"description\\\":\\\"Schools\\\"},{\\\"code\\\":8244,\\\"description\\\":\\\"BusinessandSecretarialSchools\\\"},{\\\"code\\\":8249,\\\"description\\\":\\\"Schools\\\"},{\\\"code\\\":825,\\\"description\\\":\\\"OtherAgriinputs\\\"},{\\\"code\\\":8351,\\\"description\\\":\\\"ChildCareServices\\\"},{\\\"code\\\":8398,\\\"description\\\":\\\"Organizations\\\"},{\\\"code\\\":8641,\\\"description\\\":\\\"Associations–Civic\\\"},{\\\"code\\\":8651,\\\"description\\\":\\\"Organizations\\\"},{\\\"code\\\":8661,\\\"description\\\":\\\"Organizations\\\"},{\\\"code\\\":8675,\\\"description\\\":\\\"AutomobileAssociations\\\"},{\\\"code\\\":8699,\\\"description\\\":\\\"Organizations–NotElsewhereClassified\\\"},{\\\"code\\\":8734,\\\"description\\\":\\\"TestingLaboratories(Non-Medical)\\\"},{\\\"code\\\":8911,\\\"description\\\":\\\"Architectural\\\"},{\\\"code\\\":8931,\\\"description\\\":\\\"Accounting\\\"},{\\\"code\\\":8999,\\\"description\\\":\\\"ProfessionalServices–NotElsewhereClassified\\\"},{\\\"code\\\":9211,\\\"description\\\":\\\"CourtCosts\\\"},{\\\"code\\\":9222,\\\"description\\\":\\\"Fines\\\"},{\\\"code\\\":9311,\\\"description\\\":\\\"TaxPayments\\\"},{\\\"code\\\":9399,\\\"description\\\":\\\"GovernmentServices\\\"},{\\\"code\\\":9405,\\\"description\\\":\\\"GovernmentServices\\\"}]\"";

			JSONParser parser = null;
			Object obj = null;

			parser = new JSONParser();
			obj = parser.parse(mcc2);
			String jsonObject = (String) obj;
			obj = parser.parse(jsonObject);
			JSONArray array = (JSONArray) obj;
			org.json.JSONArray addArray = new org.json.JSONArray();

//    System.out.println("arraySize: "+array.size());
			for (int i = 0; i < array.size(); i++) {
				JSONObject getJson = (JSONObject) array.get(i);

				Long code = (Long) getJson.get("code");
				String description = (String) getJson.get("description");

				JSONObject setJson = new JSONObject();
				setJson.put("code", code);
				setJson.put("description", description);

				addArray.put(setJson);

			}
			// System.out.println("--------------array-------------: "+addArray.toString());

			map.put("mccList", addArray.toString());

		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}

		return map;
	}

	@Override
	public Map<String, Object> getBussinessTypeList() {
		Map<String, Object> map = new HashMap<>();
		try {

			map.put("1", "Individual- HUF");
			map.put("2", "Partnership");
			map.put("3", "Companies registered under AcT");
			map.put("4", "Govt/ Govt Undertakings");
			map.put("41", "Proprietor");
			map.put("42", "Individuals / Professionals");
			map.put("44", "Regd Trusts");
			map.put("45", "LLPs");

		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;
	}

	@Override
	public Map<String, Object> subMerchantDetails(long merchantId, long subMerchantInfoId) {
		Map<String, Object> map = new HashMap<>();
		try {

			MerchantSubMerchantInfoV2 merchantSubMerchantInfo = merchantSubMerchantInfoV2Repository
					.findByMerchantIdsAnsSubMerchantInfoId(merchantId, subMerchantInfoId);
			if (merchantSubMerchantInfo != null) {
				MerchantSubMerchantInfoPayload infoPayload = new MerchantSubMerchantInfoPayload();
				String isActive = merchantSubMerchantInfo.getSubMerchantStatus();
				String isStatus = null;
				if (isActive.equals("SUCCESS")) {
					isStatus = "ACTIVE";
				} else {
					isStatus = isActive;
				}
				String res = merchantSubMerchantInfo.getSubMerchantRegisterInfo();
				String merchantVirtualAddress = merchantSubMerchantInfo.getSubMerchantAdditionalInfo();
				String qrString = merchantSubMerchantInfo.getSubMerchantQRString();
				org.json.JSONObject jsonObject = new org.json.JSONObject(res);

				String action = null;

				if (jsonObject.has("action")) {
					action = jsonObject.getString("action");
				} else {
					action = (String) jsonObject.get("actionName");
				}

				String merchantBussiessName = merchantSubMerchantInfo.getSubMerchantBussinessName();
				String panNo = merchantSubMerchantInfo.getSubMerchantPan();
				String contactEmail = merchantSubMerchantInfo.getSubMerchantEmailId();
				String gstn = merchantSubMerchantInfo.getSubMerchantGst();
				String mobile = merchantSubMerchantInfo.getSubMerchantMobileNumber();

				String address = null;
				String state = null;
				String city = null;
				String pinCode = null;
				String subMerchantId = null;
				String MCC = null;

				if (jsonObject.has("address")) {
					address = jsonObject.getString("address");
				} else {
					address = (String) jsonObject.get("p10");
				}

				if (jsonObject.has("state")) {
					state = jsonObject.getString("state");
				} else {
					state = (String) jsonObject.get("p12");
				}

				if (jsonObject.has("city")) {
					city = jsonObject.getString("city");
				} else {
					city = (String) jsonObject.get("p11");
				}

				if (jsonObject.has("pinCode")) {
					pinCode = jsonObject.getString("pinCode");
				} else {
					pinCode = (String) jsonObject.get("p13");
				}

				if (jsonObject.has("subMerchantId")) {
					subMerchantId = jsonObject.getString("subMerchantId");
				} else {
					subMerchantId = (String) jsonObject.get("p3");
				}

				if (jsonObject.has("MCC")) {
					address = jsonObject.getString("MCC");
				} else {
					MCC = (String) jsonObject.get("p6");
				}

				if ((gstn == null || gstn.equals(""))) {
					infoPayload.setGstn("NA");
				} else {
					infoPayload.setGstn(gstn);
				}

				infoPayload.setIsActive(isStatus);
				infoPayload.setSubMerchantInfoId(subMerchantInfoId);
				infoPayload.setAction(action);
				infoPayload.setAddress(address);
				infoPayload.setCity(city);
				infoPayload.setContactEmail(contactEmail);
				infoPayload.setMCC(MCC);
				infoPayload.setMerchantBussiessName(merchantBussiessName);
				infoPayload.setMerchantVirtualAddress(merchantVirtualAddress);
				infoPayload.setMobile(mobile);
				infoPayload.setPanNo(panNo);
				infoPayload.setPinCode(pinCode);
				infoPayload.setState(state);
				infoPayload.setSubMerchantId(subMerchantId);
				infoPayload.setSubMerchantVpa(merchantSubMerchantInfo.getSubMerchantAdditionalInfo());
				infoPayload.setQrCodeUrl(qrString);

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_FOUND);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put("data", infoPayload);
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			}

		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}

		return map;
	}

//	@Override
//	public Map<String, Object> updateSubmerchanData() throws Exception {
//
//		List<SubmerchantPayload> reqDetail = new ArrayList<>();
//		try {
//
//			List<MerchantSubMerchantInfoV2> list = merchantSubMerchantInfoV2Repository.findByBankId("Yes Bank");
//
//			LOGGER.info("Total record " + list.stream().count());
//
//			for (MerchantSubMerchantInfoV2 merchantSubMerchantInfoV2 : list) {
//
//				System.out.println();
//
//				String userRequest = merchantSubMerchantInfoV2.getSubMerchantUserRequest();
//
//				String panNo = "NA";
//				String email = "NA";
//				String gst = "NA";
//				String mcc = "NA";
//				String mobileNumber = "NA";
//				String name = "NA";
//
//				LOGGER.info("getSubMerchantUserRequest {} " + userRequest);
//				if (!userRequest.equalsIgnoreCase("NA")) {
//
//					org.json.JSONObject jsonObject = new org.json.JSONObject(userRequest);
//
//					if (jsonObject.has("panNo")) {
//						panNo = jsonObject.getString("panNo");
//					}
//					if (jsonObject.has("contactEmail")) {
//						email = jsonObject.getString("contactEmail");
//					}
//					if (jsonObject.has("mobile")) {
//						mobileNumber = jsonObject.getString("mobile");
//					}
//					if (jsonObject.has("MCC")) {
//						mcc = jsonObject.getString("MCC");
//					}
//					if (jsonObject.has("gstn")) {
//						gst = jsonObject.getString("gstn");
//					}
//					if (jsonObject.has("name")) {
//						name = jsonObject.getString("name");
//					}
//
//					LOGGER.info("Db column email  ", merchantSubMerchantInfoV2.getSubMerchantEmailId());
//					LOGGER.info("Db column mobile number ", merchantSubMerchantInfoV2.getSubMerchantMobileNumber());
//					LOGGER.info("Db column namer ", merchantSubMerchantInfoV2.getSubMerchantName());
//					LOGGER.info("Db column gst ", merchantSubMerchantInfoV2.getSubMerchantGst());
//					LOGGER.info("Db column mcc ", merchantSubMerchantInfoV2.getSubMerchantMCC());
//					LOGGER.info("Db column pan ", merchantSubMerchantInfoV2.getSubMerchantPan());
//					LOGGER.info("Db column editaction ", merchantSubMerchantInfoV2.getSubMerchantEditAction());
//
//					SubmerchantPayload merchantRequest = new SubmerchantPayload();
//					merchantRequest.setSubMerchantId(merchantSubMerchantInfoV2.getSubMerchantId());
//					merchantRequest.setEmail(merchantSubMerchantInfoV2.getSubMerchantEmailId());
//					merchantRequest.setGst(merchantSubMerchantInfoV2.getSubMerchantGst());
//					merchantRequest.setMccCode(merchantSubMerchantInfoV2.getSubMerchantMCC());
//					merchantRequest.setMobile(merchantSubMerchantInfoV2.getSubMerchantMobileNumber());
//					merchantRequest.setName(merchantSubMerchantInfoV2.getSubMerchantName());
//					merchantRequest.setPan(merchantSubMerchantInfoV2.getSubMerchantPan());
//
//					reqDetail.add(merchantRequest);
//
//					if (merchantSubMerchantInfoV2.getSubMerchantEmailId() == null
//							|| merchantSubMerchantInfoV2.getSubMerchantEmailId().equalsIgnoreCase("NA")
//							|| merchantSubMerchantInfoV2.getSubMerchantEmailId().equalsIgnoreCase("null")
//							|| merchantSubMerchantInfoV2.getSubMerchantEmailId().equalsIgnoreCase("")) {
//						merchantSubMerchantInfoV2.setSubMerchantEmailId(email);
//						LOGGER.info("email in if " + email);
//
//					}
//					if (merchantSubMerchantInfoV2.getSubMerchantMobileNumber() == null
//							|| merchantSubMerchantInfoV2.getSubMerchantMobileNumber().equalsIgnoreCase("NA")
//							|| merchantSubMerchantInfoV2.getSubMerchantMobileNumber().equalsIgnoreCase("null")
//							|| merchantSubMerchantInfoV2.getSubMerchantMobileNumber().equalsIgnoreCase("")) {
//						merchantSubMerchantInfoV2.setSubMerchantMobileNumber(mobileNumber);
//						LOGGER.info("mobile in if " + mobileNumber);
//
//					}
//					if (merchantSubMerchantInfoV2.getSubMerchantName() == null
//							|| merchantSubMerchantInfoV2.getSubMerchantName().equalsIgnoreCase("NA")
//							|| merchantSubMerchantInfoV2.getSubMerchantName().equalsIgnoreCase("null")
//							|| merchantSubMerchantInfoV2.getSubMerchantName().equalsIgnoreCase("")) {
//						merchantSubMerchantInfoV2.setSubMerchantName(name);
//						LOGGER.info("name in if " + name);
//
//					}
//					if (merchantSubMerchantInfoV2.getSubMerchantGst() == null
//							|| merchantSubMerchantInfoV2.getSubMerchantGst().equalsIgnoreCase("NA")
//							|| merchantSubMerchantInfoV2.getSubMerchantGst().equalsIgnoreCase("null")
//							|| merchantSubMerchantInfoV2.getSubMerchantGst().equalsIgnoreCase("")) {
//						merchantSubMerchantInfoV2.setSubMerchantGst(gst);
//						LOGGER.info("gst in if " + gst);
//
//					}
//
//					if (merchantSubMerchantInfoV2.getSubMerchantMCC() == null
//							|| merchantSubMerchantInfoV2.getSubMerchantMCC().equalsIgnoreCase("NA")
//							|| merchantSubMerchantInfoV2.getSubMerchantMCC().equalsIgnoreCase("null")
//							|| merchantSubMerchantInfoV2.getSubMerchantMCC().equalsIgnoreCase("")) {
//						merchantSubMerchantInfoV2.setSubMerchantMCC(mcc);
//						LOGGER.info("mcc in if " + mcc);
//					}
//
//					if (merchantSubMerchantInfoV2.getSubMerchantPan() == null
//							|| merchantSubMerchantInfoV2.getSubMerchantPan().equalsIgnoreCase("NA")
//							|| merchantSubMerchantInfoV2.getSubMerchantPan().equalsIgnoreCase("null")
//							|| merchantSubMerchantInfoV2.getSubMerchantPan().equalsIgnoreCase("")) {
//						merchantSubMerchantInfoV2.setSubMerchantPan(panNo);
//						LOGGER.info("pan in if " + panNo);
//
//					}
////					if (merchantSubMerchantInfoV2.getSubMerchantEditAction().equalsIgnoreCase("NA")||
////							merchantSubMerchantInfoV2.getSubMerchantEditAction().equalsIgnoreCase("null")
////							|| merchantSubMerchantInfoV2.getSubMerchantEditAction().equalsIgnoreCase("")
////							|| merchantSubMerchantInfoV2.getSubMerchantEditAction() == null) {
////						merchantSubMerchantInfoV2.setSubMerchantEditAction("NA");
////						LOGGER.info("setSubMerchantEditAction in if ");
////					}
//					Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());
//					merchantSubMerchantInfoV2.setSubMerchanModifiedtDate(trxnDate);
//					// merchantSubMerchantInfoV2Repository.save(merchantSubMerchantInfoV2);
//
//					LOGGER.info("merchantSubMerchantInfoV2Repository "
//							+ merchantSubMerchantInfoV2.getSubMerchantInfoIdV2());
//
//				}
//
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return Map.of("list", reqDetail);
//
//	}

	@Override
	public Map<String, Object> findByMobileNumber(String mobileNumber) {

		Map<String, Object> map = new HashMap<>();

		try {
			String flag = "0";

			LOGGER.info("Inside findByMobileNumbe {}", mobileNumber);

			MerchantSubMerchantInfoV2 subMerchantDeatils = merchantSubMerchantInfoV2Repository
					.findByMobileNumber(mobileNumber);

			if (subMerchantDeatils != null) {
				LOGGER.info("if subMerchantDeatils {}");

				String subMerchantId = subMerchantDeatils.getSubMerchantId();
				String subMerchantName = subMerchantDeatils.getSubMerchantName();
				String subMerchantMobileNumber = subMerchantDeatils.getSubMerchantMobileNumber();
				String subMerchantPan = subMerchantDeatils.getSubMerchantPan();
				String subMerchantGst = subMerchantDeatils.getSubMerchantGst();
				String subMerchantEmail = subMerchantDeatils.getSubMerchantEmailId();
				String subMerchantBussinessName = subMerchantDeatils.getSubMerchantBussinessName();
				String subMerchantBankDetails = subMerchantDeatils.getSubMerchantBankDetails();
				long submerchantInfoId = subMerchantDeatils.getSubMerchantInfoIdV2();
				String otherDocument = subMerchantDeatils.getOtherDocument();

				if (!otherDocument.equals("NA")) {
					JSONObject jsonObject = new JSONObject(otherDocument);
					String isVerPanCardAuthorizer = jsonObject.getString("isVerPanCardAuthorizer");
					String isVerAadhaarCardAuthorizer = jsonObject.getString("isVerAadhaarCardAuthorizer");
					String isVerCancelCheque = jsonObject.getString("isVerCancelCheque");

					if (isVerPanCardAuthorizer.equals("1") && isVerAadhaarCardAuthorizer.equals("1")
							&& isVerCancelCheque.equals("1")) {
						flag = "1";
					}
				}

				org.json.JSONObject subMerchantBankDetailsJsonObject = new org.json.JSONObject(subMerchantBankDetails);

				String subMerchantIfscCode = subMerchantBankDetailsJsonObject.getString("subMerchantIfscCode");
				String subMerchantBankName = subMerchantBankDetailsJsonObject.getString("subMerchantBankName");
				String subMerchantBankAccount = subMerchantBankDetailsJsonObject.getString("subMerchantBankAccount");

				SubMerchantResponse merchantResponse = new SubMerchantResponse(subMerchantId, subMerchantName,
						subMerchantMobileNumber, subMerchantPan, subMerchantGst, subMerchantEmail,
						subMerchantBussinessName, subMerchantIfscCode, subMerchantBankName, subMerchantBankAccount,
						otherDocument, String.valueOf(submerchantInfoId), flag);

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_FOUND);
				map.put("Data", merchantResponse);

			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
			}
		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
		}

		return map;
	}

	@Override
	public Map<String, Object> updateBySubMerchantId(SubmerchantDocsRequest submerchantDocsRequest,
			String submerchantInfoId) {

		Map<String, Object> map = new HashMap<>();
		try {

			String isVerPanCardAuthorizer = submerchantDocsRequest.getIsVerPanCardAuthorizer();
			String isVerAadhaarCardAuthorizer = submerchantDocsRequest.getIsVerAadhaarCardAuthorizer();
			String isVerCancelCheque = submerchantDocsRequest.getIsVerCancelCheque();

			if (isVerPanCardAuthorizer.equals("1") && isVerAadhaarCardAuthorizer.equals("1")
					&& isVerCancelCheque.equals("1")) {
				submerchantDocsRequest.setFlag("1");
			}

			String otherDocs = new Gson().toJson(submerchantDocsRequest);

			long getSubmerchantInfoId = Long.valueOf(submerchantInfoId);
			if (!merchantSubMerchantInfoV2Repository.existsBySubMerchantInfoIdV2(getSubmerchantInfoId)) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, "SubMerchant Not Exist");
				return map;
			}

			MerchantSubMerchantInfoV2 subMerchantDeatils = merchantSubMerchantInfoV2Repository
					.findBySubMerchantInfoIdV2(getSubmerchantInfoId);
			subMerchantDeatils.setOtherDocument(otherDocs);
			merchantSubMerchantInfoV2Repository.save(subMerchantDeatils);

			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UPDATE_SUCCEESSFULLY);
			return map;
		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
		}

		return map;
	}

	@Override
	public Map<String, Object> subMerchantListByFilter(long merchantId, SubMerchantListRequest subMerchantList) {
		Map<String, Object> map = new HashMap<>();
		try {
			List<MerchantSubMerchantInfoAllPayload> subMList = new ArrayList<MerchantSubMerchantInfoAllPayload>();
			List<MerchantSubMerchantInfoV2> list = new ArrayList<MerchantSubMerchantInfoV2>();
			String merchantName = subMerchantList.getMerchchantName();
			String mobile = subMerchantList.getMerchantMobile();
			String vpa = subMerchantList.getMerchantVpa();
			String subMerchantBussinessName = subMerchantList.getMerchantBussinessName();

			if ((mobile.equals("") || mobile.isEmpty() || mobile.equals("NA"))
					&& (vpa.equals("") || vpa.isEmpty() || vpa.equals("NA"))
					&& (subMerchantBussinessName.equals("") || subMerchantBussinessName.isEmpty()
							|| subMerchantBussinessName.equals("NA"))
					&& (!merchantName.equals("") || !merchantName.isEmpty() || !merchantName.equals("NA"))) {

				list = merchantSubMerchantInfoV2Repository.findByMerchantName(merchantId, merchantName);
			}
			if ((merchantName.equals("") || merchantName.isEmpty() || merchantName.equals("NA"))
					&& (vpa.equals("") || vpa.isEmpty() || vpa.equals("NA"))
					&& (subMerchantBussinessName.equals("") || subMerchantBussinessName.isEmpty()
							|| subMerchantBussinessName.equals("NA"))
					&& (!mobile.equals("") || !mobile.isEmpty() || !mobile.equals("NA"))) {
				list = merchantSubMerchantInfoV2Repository.findByMobile(merchantId, mobile);
			}
			if ((merchantName.equals("") || merchantName.isEmpty() || merchantName.equals("NA"))
					&& (mobile.equals("") || mobile.isEmpty() || mobile.equals("NA"))
					&& (subMerchantBussinessName.equals("") || subMerchantBussinessName.isEmpty()
							|| subMerchantBussinessName.equals("NA"))
					&& (!vpa.equals("") || !vpa.isEmpty() || !vpa.equals("NA"))) {
				list = merchantSubMerchantInfoV2Repository.findByVpa(merchantId, vpa);
			}
			if ((merchantName.equals("") || merchantName.isEmpty() || merchantName.equals("NA"))
					&& (vpa.equals("") || vpa.isEmpty() || vpa.equals("NA"))
					&& (mobile.equals("") || mobile.isEmpty() || mobile.equals("NA"))
					&& (!subMerchantBussinessName.equals("") || !subMerchantBussinessName.isEmpty()
							|| !subMerchantBussinessName.equals("NA"))) {
				list = merchantSubMerchantInfoV2Repository.findBySubMerchantBussinessName(merchantId,
						subMerchantBussinessName);
			}

			if (!list.isEmpty()) {
				for (MerchantSubMerchantInfoV2 merchantSubMerchantInfo : list) {
					MerchantSubMerchantInfoAllPayload infoPayload = new MerchantSubMerchantInfoAllPayload();

					Long subMerchantInfoId = merchantSubMerchantInfo.getSubMerchantInfoIdV2();
					String panNo = merchantSubMerchantInfo.getSubMerchantPan();
					panNo = !"NA".equals(panNo) && panNo != null ? "XXXXXX" + panNo.substring(panNo.length() - 4)
							: "NA";
					String date = DateAndTime
							.convertDateTimeFormatSubMerchant(merchantSubMerchantInfo.getSubMerchantDate().toString());
					int soundBoxCount = soundBoxSubscriptionRepository.findSoundBoxCount(subMerchantInfoId, '0');
					char soundBoxExist = soundBoxCount != 0 ? '1' : '0';

					String isActive = merchantSubMerchantInfo.getSubMerchantStatus();
					String isStatus = "SUCCESS".equals(isActive) ? "ACTIVE" : isActive;

					String res = merchantSubMerchantInfo.getSubMerchantRegisterInfo();
					org.json.JSONObject jsonObject = new org.json.JSONObject(res);

					String action = jsonObject.optString("action", jsonObject.optString("actionName"));

					String gstn = merchantSubMerchantInfo.getSubMerchantGst();
					String address = jsonObject.optString("address", jsonObject.optString("p10"));
					String state = jsonObject.optString("state", jsonObject.optString("p12"));
					String city = jsonObject.optString("city", jsonObject.optString("p11"));
					String pinCode = jsonObject.optString("pinCode", jsonObject.optString("p13"));
					String subMerchantId = jsonObject.optString("subMerchantId", jsonObject.optString("p3"));
					String MCC = jsonObject.optString("MCC", jsonObject.optString("p6"));

					infoPayload.setMerchantId(merchantSubMerchantInfo.getMerchantId());
					infoPayload.setGstn(gstn != null && !gstn.isEmpty() ? gstn : "NA");
					infoPayload.setSoundBoxExist(soundBoxExist);
					infoPayload.setRegistrationDate(date);
					infoPayload.setIsActive(isStatus);
					infoPayload.setSubMerchantInfoId(subMerchantInfoId);
					infoPayload.setAction(action);
					infoPayload.setAddress(address);
					infoPayload.setCity(city);
					infoPayload.setContactEmail(merchantSubMerchantInfo.getSubMerchantEmailId());
					infoPayload.setMCC(MCC);
					infoPayload.setMerchantBussiessName(merchantSubMerchantInfo.getSubMerchantBussinessName());
					infoPayload.setMerchantVirtualAddress(merchantSubMerchantInfo.getSubMerchantAdditionalInfo());
					infoPayload.setMobile(merchantSubMerchantInfo.getSubMerchantMobileNumber());
					infoPayload.setPanNo(panNo);
					infoPayload.setPinCode(pinCode);
					infoPayload.setState(state);
					infoPayload.setSubMerchantId(subMerchantId);
					infoPayload.setSubMerchantVpa(merchantSubMerchantInfo.getSubMerchantAdditionalInfo());
					infoPayload.setQrCodeUrl(merchantSubMerchantInfo.getSubMerchantQRString());
					infoPayload.setRequestUrl1("NA");
					
					subMList.add(infoPayload);
				}

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "MerchantSubMerchantInfo");
				map.put("MerchantSubMerchantInfo", subMList);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
			}
		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage().toString());
		}

		return map;
	}

	@Override
	public Map<String, Object> checkLimit(String payeeVPA) throws NoSuchPaddingException, NoSuchAlgorithmException,
			BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException, Exception {
		Map<String, Object> map = new HashMap<>();

		LOGGER.info(" Inside " + payeeVPA);

		MerchantSubMerchantInfoV2 merchantSubMerchantInfoV2 = merchantSubMerchantInfoV2Repository
				.findBySubmerchantVpa(payeeVPA, "Yes Bank");

		if (merchantSubMerchantInfoV2 == null) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UPI_NOT_EXITS);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			return map;
		}

		if (merchantSubMerchantInfoV2 != null) {
			String partnerRefrenceNumber = YesBankSubMerchant.generateRandomMerchantId();
			String subMerchantId = merchantSubMerchantInfoV2.getSubMerchantId();
			String partnerKey = merchantSubMerchantInfoV2.getSubMerchantKey();

			String request = "{\r\n" + "\"requestId\": \"" + partnerRefrenceNumber + "\",\r\n"
					+ "\"actionName\":\"FETCH_SELLER_BALANCE\",\r\n" + "\"partnerKey\": \"" + partnerKey + "\",\r\n"
					+ "\"p1\": \"" + subMerchantId + "\"\r\n" + "}\r\n" + "";

			LOGGER.info(" request : " + request);

			String response = encryptionAndDecryptionProduction.getEncDec(request, partnerKey);
			LOGGER.info(" response : " + response);

			JSONObject jsonObject = new JSONObject(response);
			String responseCode = jsonObject.getString("responseCode");
			if (responseCode.equals("00")) {
				CheckUPILimitResponse checkUPILimitResponse = new CheckUPILimitResponse();

				BigDecimal bigDecimal = (BigDecimal) jsonObject.get("overallMonthlyCollectionLimit");

				checkUPILimitResponse.setCurrentBalance((String) jsonObject.get("currentBalance"));
				checkUPILimitResponse.setWithdrawableBalance((String) jsonObject.get("withdrawableBalance"));
				checkUPILimitResponse.setMonthlyLimitUtilized((String) jsonObject.get("monthlyLimitUtilized"));
				checkUPILimitResponse.setOverallMonthlyCollectionLimit(bigDecimal.toString());

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_FOUND);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put(ResponseMessage.DATA, checkUPILimitResponse);
				return map;
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, jsonObject.get("responseMessage"));
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			}
		}
		return map;
	}

	@Override
	public Map<String, Object> countOfMerchant(long merchantId) {
		Map<String, Object> map = new HashMap<>();
		int totalMerchant = merchantSubMerchantInfoV2Repository.findByMerchantID(merchantId);
		map.put("totalMerchants", totalMerchant);
		map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
		map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
		map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_FOUND);
		return map;

	}

	@Override
	public Map<String, Object> findByMobile(String mobile) {
		Map<String, Object> map = new HashMap<>();
		List<MerchantSubMerchantInfoV2> list = merchantSubMerchantInfoV2Repository.findByMobile(mobile);

		if (!list.isEmpty()) {
			for (MerchantSubMerchantInfoV2 merchantSubMerchantInfoV2 : list) {

				Merchants merchants = merchantsRepository.findById(merchantSubMerchantInfoV2.getMerchantId()).get();

				map.put("vpa", merchantSubMerchantInfoV2.getSubMerchantAdditionalInfo());
				map.put("merchantBusinessName", Encryption.decString(merchants.getMerchantBusinessName()));
				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_FOUND);
				return map;

			}
		} else {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_AVAILABLE);
		}

		return map;
	}

}
