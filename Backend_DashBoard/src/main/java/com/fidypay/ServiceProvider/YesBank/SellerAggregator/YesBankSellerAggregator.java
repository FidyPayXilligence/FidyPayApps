package com.fidypay.ServiceProvider.YesBank.SellerAggregator;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Timestamp;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fidypay.ServiceProvider.YesBank.YesBankSubMerchant;
import com.fidypay.dto.SubMerchantDTOSeller;
import com.fidypay.entity.MerchantSubMerchantInfoV2;
import com.fidypay.entity.PayinRequest;
import com.fidypay.entity.PayinResponse;
import com.fidypay.service.PayinRequestService;
import com.fidypay.service.PayinResponseService;
import com.fidypay.service.YBSellerAggregatorService;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.DateUtil;
import com.fidypay.utils.ex.GenerateTrxnRefId;
import com.google.gson.Gson;

@Service
public class YesBankSellerAggregator {

	private static final Logger LOGGER = LoggerFactory.getLogger(YesBankSellerAggregator.class);

	@Autowired
	private EncryptionAndDecryptionProduction encryptionAndDecryptionProduction;

	@Autowired
	private YBSellerAggregatorService ybSellerAggregatorService;

	@Autowired
	private PayinRequestService payinRequestService;

	@Autowired
	private PayinResponseService payinResponseService;

	public String sellerAddition(SubMerchantDTOSeller dtoSeller, long merchantId, String partnerKey)
			throws Exception, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException,
			InvalidKeySpecException, Exception {
		// String partnerKey = "bHhNTlRaNj";
		String turnOverType = "LARGE";
		String partnerRefrenceNumber = YesBankSubMerchant.generateRandomMerchantId();
		String action = "ADD_PARTNER_SELLER";
		Timestamp trxnDate = Timestamp.valueOf(DateUtil.getCurrentTimeInIST());
		LOGGER.info("  Date : " + trxnDate);
		String vpa = "NA";
		String sellerStatus = "NA";
		String qrString = "NA";
		JSONObject jsonObject2 = new JSONObject();
		String subMerchantId = "CB" + GenerateTrxnRefId.getNumericString(8);

		String sellerCreationReq = "{\r\n" + 
			    "    \"partnerReferenceNo\": \"" + partnerRefrenceNumber + "\",\r\n" + 
			    "    \"actionName\": \"" + action + "\",\r\n" + 
			    "    \"partnerKey\": \"" + partnerKey + "\",\r\n" + 
			    "    \"p1\": \"" + dtoSeller.getMerchantBussiessName() + "\",\r\n" + 
			    "    \"p2\": \"" + dtoSeller.getName() + "\",\r\n" + 
			    "    \"p3\": \"" + subMerchantId + "\",\r\n" + 
			    "    \"p4\": \"" + dtoSeller.getMobile() + "\",\r\n" + 
			    "    \"p5\": \"" + dtoSeller.getContactEmail() + "\",\r\n" + 
			    "    \"p6\": \"" + dtoSeller.getMCC() + "\",\r\n" + 
			    "    \"p7\": \"" + turnOverType + "\",\r\n" + 
			    "    \"p8\": \"" + dtoSeller.getMerchantGenre() + "\",\r\n" + 
			    "    \"p9\": \"" + dtoSeller.getMerchantBussinessType() + "\",\r\n" + 
			    "    \"p10\": \"" + dtoSeller.getCity() + "\",\r\n" + 
			    "    \"p11\": \"" + dtoSeller.getCity() + "\",\r\n" + 
			    "    \"p12\": \"" + dtoSeller.getState() + "\",\r\n" + 
			    "    \"p13\": \"" + dtoSeller.getPinCode() + "\",\r\n" + 
			    "    \"p14\": \"" + dtoSeller.getPanNo() + "\",\r\n" + 
			    "    \"p15\": \"" + dtoSeller.getGstn() + "\",\r\n" + 
			    "    \"p16\": \"" + dtoSeller.getSubMerchantBankAccount() + "\",\r\n" + 
			    "    \"p17\": \"" + dtoSeller.getSubMerchantIfscCode() + "\",\r\n" + 
			    "    \"p18\": \"" + dtoSeller.getLatitude() + "\",\r\n" + 
			    "    \"p19\": \"" + dtoSeller.getLongitude() + "\",\r\n" + 
			    "    \"p20\": \"" + dtoSeller.getAddress() + "\",\r\n" + 
			    "    \"p21\": \"" + dtoSeller.getAlternateAddress() + "\",\r\n" + 
			    "    \"p22\": \""+dtoSeller.getLlpOrCin()+"\",\r\n" + 
			    "    \"p23\": \""+dtoSeller.getUdhoyogAadhaar()+"\",\r\n" + 
			    "    \"p24\": \""+dtoSeller.getElectricityBill()+"\",\r\n" + 
			    "    \"p25\": \""+dtoSeller.getElectricityBoard()+"\",\r\n" + 
			    "    \"p26\": \"" + dtoSeller.getDob() + "\",\r\n" + 
			    "    \"p27\": \"" + dtoSeller.getDoi() + "\",\r\n" + 
			    "    \"p28\": \"fidypay.com\",\r\n" + 
			    "    \"p29\": \"\",\r\n" + 
			    "    \"p30\": \"\"\r\n" + 
			    "}\r\n";

		
		
		PayinRequest payinRequest = payinRequestService.save(merchantId, partnerKey, sellerCreationReq,
				dtoSeller.getMobile(), 0.0, "NA", "NA", subMerchantId, "Submerchant Creation API Request", trxnDate);

		String sellerCreationResp = encryptionAndDecryptionProduction.getEncDec(sellerCreationReq, partnerKey);
		LOGGER.info("  sellerCreationResp -- " + sellerCreationResp);

		JSONObject bankDetailJSON = new JSONObject();
		bankDetailJSON.put("subMerchantIfscCode", dtoSeller.getSubMerchantIfscCode());
		bankDetailJSON.put("subMerchantBankName", dtoSeller.getSubMerchantBankName());
		bankDetailJSON.put("subMerchantBankAccount", dtoSeller.getSubMerchantBankAccount());
		String bankDetails = bankDetailJSON.toString();

		JSONObject jsonObject = new JSONObject(sellerCreationResp);
		String status = jsonObject.getString("status");
		String responseCode = jsonObject.getString("responseCode");
		String responseMessage = jsonObject.getString("responseMessage");

		if (status.equalsIgnoreCase("SUCCESS") || responseCode.equals("00")) {
			String yphubName = jsonObject.getString("ypHubUsername");
			String sellerIdentifier = jsonObject.getString("sellerIdentifier");
			LOGGER.info("  sellerIdentifier -- " + sellerIdentifier);

			// Fetch QR

			String fetchRequest = "{\r\n" + "\"requestId\": \"1\",\r\n" + "\"actionName\": \"FETCH_QR\",\r\n"
					+ "\"partnerKey\": \"" + partnerKey + "\",\r\n" + "\"p1\": \"" + sellerIdentifier + "\"\r\n" + "}";
			LOGGER.info("  fetchRequest -- " + fetchRequest);

			String fetchResponse = encryptionAndDecryptionProduction.getEncDec(fetchRequest, partnerKey);

			LOGGER.info("  fetchResponse -- " + fetchResponse);

			jsonObject = new JSONObject(fetchResponse);

			String fetchStatus = jsonObject.getString("status");
			String fetchResponseCode = jsonObject.getString("responseCode");
			String fetchResponseMessage = jsonObject.getString("responseMessage");
			LOGGER.info("  fetchStatus -- " + fetchStatus);

			if (fetchStatus.equalsIgnoreCase("SUCCESS") || fetchResponseCode.equals("00")) {
				LOGGER.info("  fetchStatus -- " + fetchStatus);

				vpa = jsonObject.getString("vpa");
				qrString = jsonObject.getString("qrString");
				sellerStatus = jsonObject.getString("sellerStatus");

				MerchantSubMerchantInfoV2 merchantSubMerchantInfo = ybSellerAggregatorService.save(merchantId, action,
						vpa, bankDetails, sellerIdentifier, sellerCreationResp, sellerCreationReq, partnerKey, qrString,
						fetchStatus, sellerStatus, trxnDate, dtoSeller.getMerchantBussiessName(),
						new Gson().toJson(dtoSeller), dtoSeller.getName(), dtoSeller.getMobile(),
						dtoSeller.getContactEmail(), dtoSeller.getPanNo(), dtoSeller.getMCC(), dtoSeller.getGstn(),
						"Yes Bank");

				jsonObject2.put("status", fetchStatus);
				jsonObject2.put("merchantBussinessName", dtoSeller.getMerchantBussiessName());
				jsonObject2.put("action", action);
				jsonObject2.put("merchantVirtualAddress", vpa);
				jsonObject2.put("crtDate", trxnDate);
				jsonObject2.put("requestId", partnerRefrenceNumber);
				jsonObject2.put("subMerchantId", sellerIdentifier);
				jsonObject2.put("loginaccess", sellerStatus);
				jsonObject2.put("code", ResponseMessage.SUCCESS);
				jsonObject2.put("qrString", qrString);

			} else {

				MerchantSubMerchantInfoV2 merchantSubMerchantInfo = ybSellerAggregatorService.save(merchantId, action,
						vpa, bankDetails, sellerIdentifier, sellerCreationResp, sellerCreationReq, partnerKey, qrString,
						fetchStatus, sellerStatus, trxnDate, dtoSeller.getMerchantBussiessName(),
						new Gson().toJson(dtoSeller), dtoSeller.getName(), dtoSeller.getMobile(),
						dtoSeller.getContactEmail(), dtoSeller.getPanNo(), dtoSeller.getMCC(), dtoSeller.getGstn(),
						"Yes Bank");

				jsonObject2.put("status", fetchStatus);
				jsonObject2.put("merchantBussinessName", dtoSeller.getMerchantBussiessName());
				jsonObject2.put("action", action);
				jsonObject2.put("merchantVirtualAddress", vpa);
				jsonObject2.put("crtDate", trxnDate);
				jsonObject2.put("requestId", partnerRefrenceNumber);
				jsonObject2.put("subMerchantId", sellerIdentifier);
				jsonObject2.put("loginaccess", sellerStatus);
				jsonObject2.put("code", ResponseMessage.SUCCESS);
				jsonObject2.put("qrString", qrString);

			}

			PayinResponse payinResponse = payinResponseService.save(sellerCreationResp, fetchResponse,
					partnerRefrenceNumber, subMerchantId, 0.0, "NA", status, "Submerchant Creation API Response",
					merchantId, trxnDate);
		}
		else {
			PayinResponse payinResponse = payinResponseService.save(sellerCreationResp, "NA",
					partnerRefrenceNumber, subMerchantId, 0.0, "NA", status, "Submerchant Creation API Response",
					merchantId, trxnDate);
			jsonObject2.put("status", ResponseMessage.STATUS_FAIL);
			jsonObject2.put(ResponseMessage.DESCRIPTION, responseMessage);
			jsonObject2.put(ResponseMessage.CODE, ResponseMessage.FAILED);
		}

		return jsonObject2.toString();
	}

	public String sellerAdditionForMerchantDashboard(SubMerchantDTOSeller dtoSeller, long merchantId, String partnerKey)
			throws Exception, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException,
			InvalidKeySpecException, Exception {
		// String partnerKey = "bHhNTlRaNj";
		String turnOverType = "LARGE";
		String partnerRefrenceNumber = YesBankSubMerchant.generateRandomMerchantId();
		String action = "ADD_PARTNER_SELLER";
		Timestamp trxnDate = Timestamp.valueOf(DateUtil.getCurrentTimeInIST());
		LOGGER.info("  Date : " + trxnDate);
		String vpa = "NA";
		String sellerStatus = "NA";
		String qrString = "NA";
		JSONObject jsonObject2 = new JSONObject();
		String subMerchantId = "FP" + GenerateTrxnRefId.getNumericString(8);

		String sellerCreationReq = "{\r\n" + 
			    "    \"partnerReferenceNo\": \"" + partnerRefrenceNumber + "\",\r\n" + 
			    "    \"actionName\": \"" + action + "\",\r\n" + 
			    "    \"partnerKey\": \"" + partnerKey + "\",\r\n" + 
			    "    \"p1\": \"" + dtoSeller.getMerchantBussiessName() + "\",\r\n" + 
			    "    \"p2\": \"" + dtoSeller.getName() + "\",\r\n" + 
			    "    \"p3\": \"" + subMerchantId + "\",\r\n" + 
			    "    \"p4\": \"" + dtoSeller.getMobile() + "\",\r\n" + 
			    "    \"p5\": \"" + dtoSeller.getContactEmail() + "\",\r\n" + 
			    "    \"p6\": \"" + dtoSeller.getMCC() + "\",\r\n" + 
			    "    \"p7\": \"" + turnOverType + "\",\r\n" + 
			    "    \"p8\": \"" + dtoSeller.getMerchantGenre() + "\",\r\n" + 
			    "    \"p9\": \"" + dtoSeller.getMerchantBussinessType() + "\",\r\n" + 
			    "    \"p10\": \"" + dtoSeller.getCity() + "\",\r\n" + 
			    "    \"p11\": \"" + dtoSeller.getCity() + "\",\r\n" + 
			    "    \"p12\": \"" + dtoSeller.getState() + "\",\r\n" + 
			    "    \"p13\": \"" + dtoSeller.getPinCode() + "\",\r\n" + 
			    "    \"p14\": \"" + dtoSeller.getPanNo() + "\",\r\n" + 
			    "    \"p15\": \"" + dtoSeller.getGstn() + "\",\r\n" + 
			    "    \"p16\": \"" + dtoSeller.getSubMerchantBankAccount() + "\",\r\n" + 
			    "    \"p17\": \"" + dtoSeller.getSubMerchantIfscCode() + "\",\r\n" + 
			    "    \"p18\": \"" + dtoSeller.getLatitude() + "\",\r\n" + 
			    "    \"p19\": \"" + dtoSeller.getLongitude() + "\",\r\n" + 
			    "    \"p20\": \"" + dtoSeller.getAddress() + "\",\r\n" + 
			    "    \"p21\": \"" + dtoSeller.getAlternateAddress() + "\",\r\n" + 
			    "    \"p22\": \""+dtoSeller.getLlpOrCin()+"\",\r\n" + 
			    "    \"p23\": \""+dtoSeller.getUdhoyogAadhaar()+"\",\r\n" + 
			    "    \"p24\": \""+dtoSeller.getElectricityBill()+"\",\r\n" + 
			    "    \"p25\": \""+dtoSeller.getElectricityBoard()+"\",\r\n" + 
			    "    \"p26\": \"" + dtoSeller.getDob() + "\",\r\n" + 
			    "    \"p27\": \"" + dtoSeller.getDoi() + "\",\r\n" + 
			    "    \"p28\": \"fidypay.com\",\r\n" + 
			    "    \"p29\": \"\",\r\n" + 
			    "    \"p30\": \"\"\r\n" + 
			    "}\r\n";

		PayinRequest payinRequest = payinRequestService.save(merchantId, partnerKey, sellerCreationReq,
				dtoSeller.getMobile(), 0.0, "NA", "NA", subMerchantId, "Submerchant Creation API Request", trxnDate);

		String sellerCreationResp = encryptionAndDecryptionProduction.getEncDec(sellerCreationReq, partnerKey);
		LOGGER.info("  sellerCreationResp -- " + sellerCreationResp);

		JSONObject bankDetailJSON = new JSONObject();
		bankDetailJSON.put("subMerchantIfscCode", dtoSeller.getSubMerchantIfscCode());
		bankDetailJSON.put("subMerchantBankName", dtoSeller.getSubMerchantBankName());
		bankDetailJSON.put("subMerchantBankAccount", dtoSeller.getSubMerchantBankAccount());
		String bankDetails = bankDetailJSON.toString();


		JSONObject jsonObject = new JSONObject(sellerCreationResp);
		String status = jsonObject.getString("status");
		String responseCode = jsonObject.getString("responseCode");
		String responseMessage = jsonObject.getString("responseMessage");

		if (status.equalsIgnoreCase("SUCCESS") || responseCode.equals("00")) {
			String yphubName = jsonObject.getString("ypHubUsername");
			String sellerIdentifier = jsonObject.getString("sellerIdentifier");
			LOGGER.info("  sellerIdentifier -- " + sellerIdentifier);

			// Fetch QR

			String fetchRequest = "{\r\n" + "\"requestId\": \"1\",\r\n" + "\"actionName\": \"FETCH_QR\",\r\n"
					+ "\"partnerKey\": \"" + partnerKey + "\",\r\n" + "\"p1\": \"" + sellerIdentifier + "\"\r\n" + "}";
			LOGGER.info("  fetchRequest -- " + fetchRequest);

			String fetchResponse = encryptionAndDecryptionProduction.getEncDec(fetchRequest, partnerKey);

			LOGGER.info("  fetchResponse -- " + fetchResponse);

			jsonObject = new JSONObject(fetchResponse);

			String fetchStatus = jsonObject.getString("status");
			String fetchResponseCode = jsonObject.getString("responseCode");
			String fetchResponseMessage = jsonObject.getString("responseMessage");
			LOGGER.info("  fetchStatus -- " + fetchStatus);

			if (fetchStatus.equalsIgnoreCase("SUCCESS") || fetchResponseCode.equals("00")) {
				LOGGER.info("  fetchStatus -- " + fetchStatus);

				vpa = jsonObject.getString("vpa");
				qrString = jsonObject.getString("qrString");
				sellerStatus = jsonObject.getString("sellerStatus");

				MerchantSubMerchantInfoV2 merchantSubMerchantInfo = ybSellerAggregatorService.save(merchantId, action,
						vpa, bankDetails, sellerIdentifier, sellerCreationResp, sellerCreationReq, partnerKey, qrString,
						fetchStatus, sellerStatus, trxnDate, dtoSeller.getMerchantBussiessName(),
						new Gson().toJson(dtoSeller), dtoSeller.getName(), dtoSeller.getMobile(),
						dtoSeller.getContactEmail(), dtoSeller.getPanNo(), dtoSeller.getMCC(), dtoSeller.getGstn(),
						"Yes Bank");

				jsonObject2.put("status", fetchStatus);
				jsonObject2.put("merchantBussinessName", dtoSeller.getMerchantBussiessName());
				jsonObject2.put("action", action);
				jsonObject2.put("merchantVirtualAddress", vpa);
				jsonObject2.put("crtDate", trxnDate);
				jsonObject2.put("requestId", partnerRefrenceNumber);
				jsonObject2.put("subMerchantId", sellerIdentifier);
				jsonObject2.put("loginaccess", sellerStatus);
				jsonObject2.put("code", ResponseMessage.SUCCESS);
				jsonObject2.put("qrString", qrString);

			} else {

				MerchantSubMerchantInfoV2 merchantSubMerchantInfo = ybSellerAggregatorService.save(merchantId, action,
						vpa, bankDetails, sellerIdentifier, sellerCreationResp, sellerCreationReq, partnerKey, qrString,
						fetchStatus, sellerStatus, trxnDate, dtoSeller.getMerchantBussiessName(),
						new Gson().toJson(dtoSeller), dtoSeller.getName(), dtoSeller.getMobile(),
						dtoSeller.getContactEmail(), dtoSeller.getPanNo(), dtoSeller.getMCC(), dtoSeller.getGstn(),
						"Yes Bank");

				jsonObject2.put("status", fetchStatus);
				jsonObject2.put("merchantBussinessName", dtoSeller.getMerchantBussiessName());
				jsonObject2.put("action", action);
				jsonObject2.put("merchantVirtualAddress", vpa);
				jsonObject2.put("crtDate", trxnDate);
				jsonObject2.put("requestId", partnerRefrenceNumber);
				jsonObject2.put("subMerchantId", sellerIdentifier);
				jsonObject2.put("loginaccess", sellerStatus);
				jsonObject2.put("code", ResponseMessage.SUCCESS);
				jsonObject2.put("qrString", qrString);

			}

			PayinResponse payinResponse = payinResponseService.save(sellerCreationResp, fetchResponse,
					partnerRefrenceNumber, subMerchantId, 0.0, "NA", status, "Submerchant Creation API Response",
					merchantId, trxnDate);
		}

		else {
			jsonObject2.put("status", ResponseMessage.STATUS_FAIL);
			jsonObject2.put(ResponseMessage.DESCRIPTION, responseMessage);
			jsonObject2.put(ResponseMessage.CODE, ResponseMessage.FAILED);
		}

		return jsonObject2.toString();
	}

	public String sellerAdditionForMerchantDashboardForAPI(SubMerchantDTOSeller dtoSeller, long merchantId,
			String partnerKey, String a) throws Exception, NoSuchAlgorithmException, BadPaddingException,
			IllegalBlockSizeException, InvalidKeySpecException, Exception {
		JSONObject jsonObject2 = new JSONObject();
		try {
			// String partnerKey = "bHhNTlRaNj";
			String turnOverType = "LARGE";
			String partnerRefrenceNumber = YesBankSubMerchant.generateRandomMerchantId();
			String action = "ADD_PARTNER_SELLER";
			Timestamp trxnDate = Timestamp.valueOf(DateUtil.getCurrentTimeInIST());
			LOGGER.info("  Date : " + trxnDate);
			String vpa = "NA";
			String sellerStatus = "NA";
			String qrString = "NA";

			String subMerchantId = "AP" + GenerateTrxnRefId.getNumericString(8);

			String sellerCreationReq = "{\r\n" + 
				    "    \"partnerReferenceNo\": \"" + partnerRefrenceNumber + "\",\r\n" + 
				    "    \"actionName\": \"" + action + "\",\r\n" + 
				    "    \"partnerKey\": \"" + partnerKey + "\",\r\n" + 
				    "    \"p1\": \"" + dtoSeller.getMerchantBussiessName() + "\",\r\n" + 
				    "    \"p2\": \"" + dtoSeller.getName() + "\",\r\n" + 
				    "    \"p3\": \"" + subMerchantId + "\",\r\n" + 
				    "    \"p4\": \"" + dtoSeller.getMobile() + "\",\r\n" + 
				    "    \"p5\": \"" + dtoSeller.getContactEmail() + "\",\r\n" + 
				    "    \"p6\": \"" + dtoSeller.getMCC() + "\",\r\n" + 
				    "    \"p7\": \"" + turnOverType + "\",\r\n" + 
				    "    \"p8\": \"" + dtoSeller.getMerchantGenre() + "\",\r\n" + 
				    "    \"p9\": \"" + dtoSeller.getMerchantBussinessType() + "\",\r\n" + 
				    "    \"p10\": \"" + dtoSeller.getCity() + "\",\r\n" + 
				    "    \"p11\": \"" + dtoSeller.getCity() + "\",\r\n" + 
				    "    \"p12\": \"" + dtoSeller.getState() + "\",\r\n" + 
				    "    \"p13\": \"" + dtoSeller.getPinCode() + "\",\r\n" + 
				    "    \"p14\": \"" + dtoSeller.getPanNo() + "\",\r\n" + 
				    "    \"p15\": \"" + dtoSeller.getGstn() + "\",\r\n" + 
				    "    \"p16\": \"" + dtoSeller.getSubMerchantBankAccount() + "\",\r\n" + 
				    "    \"p17\": \"" + dtoSeller.getSubMerchantIfscCode() + "\",\r\n" + 
				    "    \"p18\": \"" + dtoSeller.getLatitude() + "\",\r\n" + 
				    "    \"p19\": \"" + dtoSeller.getLongitude() + "\",\r\n" + 
				    "    \"p20\": \"" + dtoSeller.getAddress() + "\",\r\n" + 
				    "    \"p21\": \"" + dtoSeller.getAlternateAddress() + "\",\r\n" + 
				    "    \"p22\": \""+dtoSeller.getLlpOrCin()+"\",\r\n" + 
				    "    \"p23\": \""+dtoSeller.getUdhoyogAadhaar()+"\",\r\n" + 
				    "    \"p24\": \""+dtoSeller.getElectricityBill()+"\",\r\n" + 
				    "    \"p25\": \""+dtoSeller.getElectricityBoard()+"\",\r\n" + 
				    "    \"p26\": \"" + dtoSeller.getDob() + "\",\r\n" + 
				    "    \"p27\": \"" + dtoSeller.getDoi() + "\",\r\n" + 
				    "    \"p28\": \"fidypay.com\",\r\n" + 
				    "    \"p29\": \"\",\r\n" + 
				    "    \"p30\": \"\"\r\n" + 
				    "}\r\n";

			String sellerCreationResp = encryptionAndDecryptionProduction.getEncDec(sellerCreationReq, partnerKey);
			LOGGER.info("  sellerCreationResp -- " + sellerCreationResp);

			PayinRequest payinRequest = payinRequestService.save(merchantId, partnerKey, sellerCreationReq,
					dtoSeller.getMobile(), 0.0, "NA", "NA", subMerchantId, "Submerchant Creation API Request",
					trxnDate);

			JSONObject bankDetailJSON = new JSONObject();
			bankDetailJSON.put("subMerchantIfscCode", dtoSeller.getSubMerchantIfscCode());
			bankDetailJSON.put("subMerchantBankName", dtoSeller.getSubMerchantBankName());
			bankDetailJSON.put("subMerchantBankAccount", dtoSeller.getSubMerchantBankAccount());
			String bankDetails = bankDetailJSON.toString();

//			String sellerCreationResp = "{\"status\":\"SUCCESS\",\"responseCode\":\"00\",\"responseMessage\":\"Seller has been added successfully\",\"partnerReferenceNumber\":\"FPR001\",\"ypHubUsername\":\"FPYS.SI00001\",\"sellerIdentifier\":\"SI00001\"}";

			JSONObject jsonObject = new JSONObject(sellerCreationResp);
			String status = jsonObject.getString("status");
			String responseCode = jsonObject.getString("responseCode");
			String responseMessage = jsonObject.getString("responseMessage");

			if (status.equalsIgnoreCase("SUCCESS") || responseCode.equals("00")) {
				String yphubName = jsonObject.getString("ypHubUsername");
				String sellerIdentifier = jsonObject.getString("sellerIdentifier");
				LOGGER.info("  sellerIdentifier -- " + sellerIdentifier);

				// Fetch QR

				String fetchRequest = "{\r\n" + "\"requestId\": \"1\",\r\n" + "\"actionName\": \"FETCH_QR\",\r\n"
						+ "\"partnerKey\": \"" + partnerKey + "\",\r\n" + "\"p1\": \"" + sellerIdentifier + "\"\r\n"
						+ "}";
				LOGGER.info("  fetchRequest -- " + fetchRequest);

				String fetchResponse = encryptionAndDecryptionProduction.getEncDec(fetchRequest, partnerKey);

				LOGGER.info("  fetchResponse -- " + fetchResponse);

				jsonObject = new JSONObject(fetchResponse);

				String fetchStatus = jsonObject.getString("status");
				String fetchResponseCode = jsonObject.getString("responseCode");
				String fetchResponseMessage = jsonObject.getString("responseMessage");
				LOGGER.info("  fetchStatus -- " + fetchStatus);

				if (fetchStatus.equalsIgnoreCase("SUCCESS") || fetchResponseCode.equals("00")) {
					LOGGER.info("  fetchStatus -- " + fetchStatus);

					vpa = jsonObject.getString("vpa");
					qrString = jsonObject.getString("qrString");
					sellerStatus = jsonObject.getString("sellerStatus");

					MerchantSubMerchantInfoV2 merchantSubMerchantInfo = ybSellerAggregatorService.save(merchantId,
							action, vpa, bankDetails, sellerIdentifier, sellerCreationResp, sellerCreationReq,
							partnerKey, qrString, fetchStatus, sellerStatus, trxnDate,
							dtoSeller.getMerchantBussiessName(), new Gson().toJson(dtoSeller), dtoSeller.getName(),
							dtoSeller.getMobile(), dtoSeller.getContactEmail(), dtoSeller.getPanNo(),
							dtoSeller.getMCC(), dtoSeller.getGstn(), "Yes Bank");

					jsonObject2.put("status", fetchStatus);
					jsonObject2.put("merchantBussinessName", dtoSeller.getMerchantBussiessName());
					jsonObject2.put("action", action);
					jsonObject2.put("merchantVirtualAddress", vpa);
					jsonObject2.put("crtDate", trxnDate);
					jsonObject2.put("requestId", partnerRefrenceNumber);
					jsonObject2.put("subMerchantId", sellerIdentifier);
					jsonObject2.put("loginaccess", sellerStatus);
					jsonObject2.put("code", ResponseMessage.SUCCESS);
					jsonObject2.put("qrString", qrString);

				} else {

					MerchantSubMerchantInfoV2 merchantSubMerchantInfo = ybSellerAggregatorService.save(merchantId,
							action, vpa, bankDetails, sellerIdentifier, sellerCreationResp, sellerCreationReq,
							partnerKey, qrString, fetchStatus, sellerStatus, trxnDate,
							dtoSeller.getMerchantBussiessName(), new Gson().toJson(dtoSeller), dtoSeller.getName(),
							dtoSeller.getMobile(), dtoSeller.getContactEmail(), dtoSeller.getPanNo(),
							dtoSeller.getMCC(), dtoSeller.getGstn(), "Yes Bank");

					jsonObject2.put("status", fetchStatus);
					jsonObject2.put("merchantBussinessName", dtoSeller.getMerchantBussiessName());
					jsonObject2.put("action", action);
					jsonObject2.put("merchantVirtualAddress", vpa);
					jsonObject2.put("crtDate", trxnDate);
					jsonObject2.put("requestId", partnerRefrenceNumber);
					jsonObject2.put("subMerchantId", sellerIdentifier);
					jsonObject2.put("loginaccess", sellerStatus);
					jsonObject2.put("code", ResponseMessage.SUCCESS);
					jsonObject2.put("qrString", qrString);
				}

				PayinResponse payinResponse = payinResponseService.save(sellerCreationResp, fetchResponse,
						partnerRefrenceNumber, subMerchantId, 0.0, partnerKey, status,
						"Submerchant Creation API Response", merchantId, trxnDate);
			}

			else {
				jsonObject2.put("status", ResponseMessage.STATUS_FAIL);
				jsonObject2.put(ResponseMessage.DESCRIPTION, responseMessage);
				jsonObject2.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			}

		} catch (Exception e) {
			jsonObject2.put("status", "ERROR");
			jsonObject2.put(ResponseMessage.DESCRIPTION, e.getMessage());
			jsonObject2.put(ResponseMessage.CODE, ResponseMessage.FAILED);
		}

		return jsonObject2.toString();
	}

}
