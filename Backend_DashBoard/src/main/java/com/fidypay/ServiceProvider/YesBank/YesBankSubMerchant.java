package com.fidypay.ServiceProvider.YesBank;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fidypay.utils.constants.ResponseMessage;
import com.upi.merchanttoolkit.security.UPISecurity;

@Service
public class YesBankSubMerchant {

	private static final Logger LOGGER = LoggerFactory.getLogger(YesBankSubMerchant.class);

	public static String generateRandomMerchantId() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMdd");
		Date now = new Date();
		long millis = now.getTime();
		String strDate = sdfDate.format(now);
		strDate = strDate + String.valueOf(millis);
		String refId = "FIDYPYSMID" + strDate;
		System.out.println(" Length Merchant Id : " + refId.length());
		return refId;
	}

	public String subMerchantOnBoard(String action, String merchnatBussiessName, String merchantVirtualAddress,
			String requestUrl1, String panNo, String contactEmail, String gstn, String merchantBussinessType,
			String perDayTxnCount, String perDayTxnLmt, String perDayTxnAmt, String mobile, String add, String city,
			String state, String pinCode, String subMerchantId, String MCC, String merchantGenre) throws Exception {

		// action = C - Create, U- Update, D- Delete, R- Retry
		String externalTID = "11";
		String externalMID = "11";
		String smsNotify = "N";
		String emailNotify = "N";
		String requestId = YesBankSubMerchant.generateRandomMerchantId();
		JSONObject requestMsg = new JSONObject();
		requestMsg.put("pgMerchantId", YesBanKSSL.PG_MERCHANT_ID);
		requestMsg.put("action", action);
		requestUrl1 = YesBanKSSL.CALL_BACK_URL;
		if (action.equalsIgnoreCase("C")) {
			requestMsg.put("mebussname", merchnatBussiessName);
			requestMsg.put("merVirtualAdd", merchantVirtualAddress);
			requestMsg.put("awlmcc", MCC);
			requestMsg.put("requestUrl1", requestUrl1);
			requestMsg.put("integrationType", YesBanKSSL.INTEGRATION_TYPE);
			requestMsg.put("panNo", panNo);
			requestMsg.put("cntEmail", contactEmail);
			requestMsg.put("strEmailId", contactEmail);
			requestMsg.put("gstn", gstn);
			requestMsg.put("meBussntype", merchantBussinessType);
			requestMsg.put("pdayTxnCount", perDayTxnCount);
			requestMsg.put("pdayTxnLmt", perDayTxnLmt);
			requestMsg.put("pdayTxnAmt", perDayTxnAmt);
			requestMsg.put("strCntMobile", mobile);

			requestMsg.put("extMID", externalMID);

			requestMsg.put("extTID", externalTID);

			requestMsg.put("add", add);

			requestMsg.put("city", city);

			requestMsg.put("state", state);

			requestMsg.put("requestId", requestId);

			requestMsg.put("sms", smsNotify);

			requestMsg.put("email", emailNotify);
			requestMsg.put("addinfo1", pinCode);
			requestMsg.put("merchantGenre", merchantGenre);

		} else {

			if (action.equalsIgnoreCase("U")) {
				requestMsg.put("subMerchantId", subMerchantId);
				requestMsg.put("mebussname", merchnatBussiessName);
				requestMsg.put("merVirtualAdd", merchantVirtualAddress);
				requestMsg.put("awlmcc", MCC);
				requestMsg.put("requestUrl1", requestUrl1);
				requestMsg.put("integrationType", YesBanKSSL.INTEGRATION_TYPE);
				requestMsg.put("panNo", panNo);
				requestMsg.put("cntEmail", contactEmail);
				requestMsg.put("strEmailId", contactEmail);
				requestMsg.put("gstn", gstn);
				requestMsg.put("meBussntype", merchantBussinessType);
				requestMsg.put("pdayTxnCount", perDayTxnCount);
				requestMsg.put("pdayTxnLmt", perDayTxnLmt);
				requestMsg.put("pdayTxnAmt", perDayTxnAmt);
				requestMsg.put("strCntMobile", mobile);

				requestMsg.put("extMID", externalMID);

				requestMsg.put("extTID", externalTID);

				requestMsg.put("add", add);

				requestMsg.put("city", city);

				requestMsg.put("state", state);

				requestMsg.put("requestId", requestId);

				requestMsg.put("sms", smsNotify);

				requestMsg.put("email", emailNotify);
				requestMsg.put("addinfo1", pinCode);
				requestMsg.put("merchantGenre", merchantGenre);

			} else {

				requestMsg.put("subMerchantId", subMerchantId);
				requestMsg.put("mebussname", merchnatBussiessName);
				requestMsg.put("integrationType", YesBanKSSL.INTEGRATION_TYPE);
				requestMsg.put("requestId", requestId);
			}

		}

		LOGGER.info("Request : " + requestMsg.toString());
		JSONObject jsonObject = new JSONObject();
		 UPISecurity upisecurity = new UPISecurity();
		 jsonObject.put("requestMsg", upisecurity.encrypt(requestMsg.toString(),
		 YesBanKSSL.MERCHANT_KEY));
		jsonObject.put("pgMerchantId", YesBanKSSL.PG_MERCHANT_ID);
		String reMsg = jsonObject.toString();
		// String encRqst = upisecurity.encrypt(requestMsg.toString(),
		// YesBanKSSL.MERCHANT_KEY);
		LOGGER.info(" requestMsg :  " + reMsg);
		SSLContext sslContext = YesBanKSSL.yesBankSSL();
		HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
		URL url = new URL(YesBanKSSL.SUB_MERCHANT_URL);
		HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("X-IBM-Client-Id", YesBanKSSL.CLIENT_ID);
		con.setRequestProperty("X-IBM-Client-Secret", YesBanKSSL.CLIENT_SECRET);
		con.setRequestProperty("Content-Type", "application/json");

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(reMsg);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		LOGGER.info("\nSending 'POST' request to URL : " + url);
		LOGGER.info("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print result
		LOGGER.info(" Response Enc " + response.toString());
		String results = response.toString();
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(results);
		JSONObject resp = (JSONObject) obj;
		String decResp = (String) resp.get("resp");

		String decrypt = upisecurity.decrypt(decResp, YesBanKSSL.MERCHANT_KEY);
		String showDetails = null;
		JSONParser jsonParser = new JSONParser();
		Object jsonObj = jsonParser.parse(decrypt);
		JSONObject jsonResp = (JSONObject) jsonObj;
		String status = (String) jsonResp.get("status");
		JSONObject jsonObject2 = null;
		if (status.equalsIgnoreCase("SUCCESS") || status == "SUCCESS") {
			String mebussname = (String) jsonResp.get("mebussname");
			String pgMerchantId = (String) jsonResp.get("pgMerchantId");
			String resAction = (String) jsonResp.get("action");
			String statusDesc = (String) jsonResp.get("statusDesc");
			String merVirtualAdd = (String) jsonResp.get("merVirtualAdd");
			String crtDate = (String) jsonResp.get("crtDate");
			String respRequestId = (String) jsonResp.get("requestId");
			String respSubMerchantId = (String) jsonResp.get("subMerchantId");
			String loginaccess = (String) jsonResp.get("loginaccess");
			jsonObject2 = new JSONObject();
			jsonObject2.put("status", status);
			jsonObject2.put("merchantBussinessName", mebussname);
			// jsonObject2.put("pgMerchantId", pgMerchantId);
			jsonObject2.put("action", action);
			// jsonObject2.put("statusDesc", statusDesc);
			jsonObject2.put("merchantVirtualAddress", merVirtualAdd);
			jsonObject2.put("crtDate", crtDate);
			jsonObject2.put("requestId", respRequestId);
			jsonObject2.put("subMerchantId", respSubMerchantId);
			jsonObject2.put("loginaccess", loginaccess);
			jsonObject2.put("code", ResponseMessage.SUCCESS);
			showDetails = jsonObject2.toString();

		} else {
			String statusDescription = (String) jsonResp.get("statusDesc");
			jsonObject2 = new JSONObject();
			jsonObject2.put("status", status);
			jsonObject2.put("description", statusDescription);
			jsonObject2.put("code", ResponseMessage.FAILED);

			showDetails = jsonObject2.toString();
		}

		LOGGER.info("showDetails : " + showDetails);
		return showDetails;

	}

}