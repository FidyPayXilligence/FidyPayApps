package com.fidypay.ServiceProvider.YesBank;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fidypay.encryption.Encryption;
import com.fidypay.repo.CoreTransactionsRepository;
import com.fidypay.repo.MerchantSubMerchantInfoRepository;
import com.fidypay.repo.MerchantsRepository;
import com.fidypay.utils.constants.ResponseMessage;
import com.upi.merchanttoolkit.security.UPISecurity;

@Service
public class YesBankPaymentImpl {

	private static final Logger LOGGER = LoggerFactory.getLogger(YesBankPaymentImpl.class);

	@Autowired(required = true)
	private CoreTransactionsRepository coreTransactionsRepository;

	@Autowired(required = true)
	private MerchantsRepository merchantsRepository;

	@Autowired
	private MerchantSubMerchantInfoRepository merchantSubMerchantInfoRepository;

	public String paymentDetails(String instrId) throws KeyManagementException, UnrecoverableKeyException,
			KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		LOGGER.info("Inside Payment Details : " + instrId);
		JSONObject rspJson = null;
		SSLContext sslContext = YesBanKSSL.yesBankSSL();
		HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
		String requestParams = "{\n" + "  \"Data\": {\n" + "      \"InstrId\": \"" + instrId + "\",\n"
				+ "      \"ConsentId\": \"" + YesBanKSSL.CUSTOMER_ID + "\",\n" + "      \"SecondaryIdentification\": \""
				+ YesBanKSSL.CUSTOMER_ID + "\"\n" + "    }\n" + "  }";
		int responseCode = 0;
		String responseMessage = null;
		try {
			URL url = new URL(YesBanKSSL.DOMESTIC_PAYMENT_API_URL + "payment-details");

			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("X-IBM-Client-Id", YesBanKSSL.CLIENT_ID);
			con.setRequestProperty("X-IBM-Client-Secret", YesBanKSSL.CLIENT_SECRET);
			con.setRequestProperty("Authorization", YesBanKSSL.AUTHORIZATION);
			con.setRequestProperty("Content-Type", "application/json");

			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(requestParams);
			wr.flush();
			wr.close();

			responseCode = con.getResponseCode();
			responseMessage = con.getResponseMessage();
			System.out.println("\nSending 'POST' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);

			LOGGER.info("\nSending 'POST' request to URL : " + url);

			LOGGER.info("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			System.out.println(response.toString());

			LOGGER.info(response.toString());

			// print result
			JSONParser parser = new JSONParser();
			Object rspObj = parser.parse(response.toString());
			JSONObject rsp = (JSONObject) rspObj;
			JSONObject data = (JSONObject) rsp.get("Data");

			if (data == null) {
				String respCode = (String) rsp.get("Code");
				String respMessage = (String) rsp.get("Message");
				rspJson = new JSONObject();

				rspJson.put("status", "FFFF");
				rspJson.put("responseCode", respCode);
				rspJson.put("responseMessage", respMessage);

				System.out.println(" Code : " + respCode + " respMessage  : " + respMessage);

				LOGGER.info(" Code : " + respCode + " respMessage  : " + respMessage);
			}

			else {

				String status = (String) data.get("Status");
				String transactionIdentification = null;
				String creationDateTime = null;
				String instructionIdentification = null;
				String identification = null;
				String rspSecondaryIdentification = null;
				String credtIdentification = null;
				String schemeName = null;
				String name = null;
				String addressLine = null;
				String countySubDivision = null;
				String country = null;
				String endToEndIdentification = null;
				String errorSeverity = "";
				double amount = 0.0;
				if (status == "Accepted" || status.equalsIgnoreCase("Accepted")) {

					transactionIdentification = (String) data.get("TransactionIdentification");
					creationDateTime = (String) data.get("CreationDateTime");
					String statusUpdateDateTime = (String) data.get("StatusUpdateDateTime");
					JSONObject initiation = (JSONObject) data.get("Initiation");
					instructionIdentification = (String) initiation.get("InstructionIdentification");
					endToEndIdentification = (String) initiation.get("EndToEndIdentification");
					JSONObject instructedAmount = (JSONObject) initiation.get("InstructedAmount");
					String currency = (String) instructedAmount.get("Currency");
					JSONObject debtorAccount = (JSONObject) initiation.get("DebtorAccount");
					identification = (String) debtorAccount.get("Identification");
					rspSecondaryIdentification = (String) debtorAccount.get("SecondaryIdentification");
					JSONObject creditorAccount = (JSONObject) initiation.get("CreditorAccount");
					schemeName = (String) creditorAccount.get("SchemeName");
					credtIdentification = (String) creditorAccount.get("Identification");
					name = (String) creditorAccount.get("Name");
					JSONObject remittanceInformation = (JSONObject) initiation.get("RemittanceInformation");
					JSONObject risk = (JSONObject) rsp.get("Risk");

					String amountStr = (String) instructedAmount.get("Amount");
					amount = Double.parseDouble(amountStr);
				} else {
					transactionIdentification = (String) data.get("TransactionIdentification");
					creationDateTime = (String) data.get("CreationDateTime");
					String statusUpdateDateTime = (String) data.get("StatusUpdateDateTime");
					JSONObject initiation = (JSONObject) data.get("Initiation");
					instructionIdentification = (String) initiation.get("InstructionIdentification");
					JSONObject instructedAmount = (JSONObject) initiation.get("InstructedAmount");
					String currency = (String) instructedAmount.get("Currency");
					JSONObject debtorAccount = (JSONObject) initiation.get("DebtorAccount");
					identification = (String) debtorAccount.get("Identification");
					rspSecondaryIdentification = (String) debtorAccount.get("SecondaryIdentification");
					JSONObject creditorAccount = (JSONObject) initiation.get("CreditorAccount");
					schemeName = (String) creditorAccount.get("SchemeName");
					credtIdentification = (String) creditorAccount.get("Identification");
					name = (String) creditorAccount.get("Name");
					JSONObject remittanceInformation = (JSONObject) initiation.get("RemittanceInformation");
					JSONObject risk = (JSONObject) rsp.get("Risk");
					JSONObject deliveryAddress = (JSONObject) risk.get("DeliveryAddress");

					if (status == "SettlementCompleted" || status.equalsIgnoreCase("SettlementCompleted")
							|| status == "SettlementInProcess" || status.equalsIgnoreCase("SettlementInProcess")) {
						amount = (double) instructedAmount.get("Amount");
						addressLine = (String) deliveryAddress.get("AddressLine");
						countySubDivision = (String) deliveryAddress.get("CountySubDivision");
						country = (String) deliveryAddress.get("Country");
						endToEndIdentification = (String) initiation.get("EndToEndIdentification");
					}

					if (status == "FAILED" || status.equalsIgnoreCase("FAILED")) {
						JSONObject meta = (JSONObject) rsp.get("Meta");
						errorSeverity = (String) meta.get("ErrorSeverity");
						System.out.println("errorSeverity : " + errorSeverity);
						String errorCode = (String) meta.get("ErrorCode");
						endToEndIdentification = "NA";
						if (errorCode == "npci:EM1" || errorCode.equalsIgnoreCase("npci:EM1")) {
							amount = (double) instructedAmount.get("Amount");
							addressLine = (String) deliveryAddress.get("AddressLine");
							countySubDivision = (String) deliveryAddress.get("CountySubDivision");
							country = (String) deliveryAddress.get("Country");
						}

						if (errorCode == "ns:E500" || errorCode.equalsIgnoreCase("ns:E500")) {
							String amountS = (String) instructedAmount.get("Amount");
							amount = Double.parseDouble(amountS);
							String actionDescription = (String) meta.get("ActionDescription");
							errorSeverity = actionDescription;
						}
					}
				}

				rspJson = new JSONObject();
				identification = identification.substring(11);
				identification = "XXXXXXXXXXX" + identification;
				rspJson.put("consentId", YesBanKSSL.CUSTOMER_ID);
				rspJson.put("transactionIdentification", transactionIdentification);
				rspJson.put("status", status);
				rspJson.put("creationDateTime", creationDateTime);
				rspJson.put("InstructionIdentification", instructionIdentification);
				rspJson.put("Amount", amount);
				rspJson.put("DebitAccNo", identification);
				rspJson.put("custId", rspSecondaryIdentification);
				rspJson.put("BeneficiaryAccNo", credtIdentification);
				rspJson.put("BeneficiaryIfscCode", schemeName);
				rspJson.put("BeneficiaryName", name);
				rspJson.put("Address", addressLine);
				rspJson.put("State", countySubDivision);
				rspJson.put("country", country);
				rspJson.put("utr", endToEndIdentification);
				rspJson.put("description", errorSeverity);
			}
		} catch (Exception e) {
			e.printStackTrace();
			rspJson = new JSONObject();
			rspJson.put("responseCode", responseCode);
			rspJson.put("responseMessage", responseMessage);
		}
		return rspJson.toString();
	}

	public String domesticPaymentStatusRequest(String trxn_id, long merchantId) throws Exception {
		JSONParser parser = new JSONParser();
		JSONObject jsonObject = new JSONObject();
		String trxnStatusResponse = null;
		String response = null;
		try {
			LOGGER.info("Trxn Id : " + trxn_id);
			String spRefrenceId = null;
			String trxnId = null;
			List<?> getSpRefId = coreTransactionsRepository.findByMerchantTrxnRefId(trxn_id, merchantId);
			if (getSpRefId.size() == 0 || getSpRefId.isEmpty()) {
				getSpRefId = coreTransactionsRepository.findByTrxnRefId(merchantId, Encryption.encString(trxn_id));
			}

			Iterator it = getSpRefId.iterator();
			while (it.hasNext()) {
				Object[] object = (Object[]) it.next();
				spRefrenceId = (String) object[0];
				trxnId = Encryption.decString((String) object[1]);
			}

			trxnStatusResponse = new YesBankPaymentImpl().paymentDetails(spRefrenceId);
			LOGGER.info("Domestic Payment Details API : " + trxnStatusResponse);

			Object rspObj = parser.parse(trxnStatusResponse);
			JSONObject rsp = (JSONObject) rspObj;
			String InstructionIdentification = (String) rsp.get("InstructionIdentification");

			if (InstructionIdentification == null) {
				String respMessage = (String) rsp.get("responseMessage");
				jsonObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObject.put(ResponseMessage.DESCRIPTION, respMessage);
				response = jsonObject.toString();

			} else {
				String DebitAccNo = (String) rsp.get("DebitAccNo");
				String country = (String) rsp.get("country");
				String Address = (String) rsp.get("Address");
				double Amount = (double) rsp.get("Amount");
				String BeneficiaryName = (String) rsp.get("BeneficiaryName");
				String BeneficiaryAccNo = (String) rsp.get("BeneficiaryAccNo");
				String BeneficiaryIfscCode = (String) rsp.get("BeneficiaryIfscCode");
				String transactionIdentification = (String) rsp.get("transactionIdentification");
				String creationDateTime = (String) rsp.get("creationDateTime");
				String status = (String) rsp.get("status");
				String utr = (String) rsp.get("utr");
				String description = (String) rsp.get("description");

				jsonObject.put("merchantTrxnRefId", InstructionIdentification);
				jsonObject.put("trxn_id", trxnId);
				jsonObject.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				jsonObject.put("transactionIdentification", transactionIdentification);
				jsonObject.put("status", status);
				jsonObject.put("creationDateTime", creationDateTime);
				jsonObject.put("instructionIdentification", InstructionIdentification);
				jsonObject.put("amount", String.valueOf(Amount));
				jsonObject.put("debitAccNo", DebitAccNo);
				jsonObject.put("beneficiaryAccNo", BeneficiaryAccNo);
				jsonObject.put("beneficiaryIfscCode", BeneficiaryIfscCode);
				jsonObject.put("beneficiaryName", BeneficiaryName);
				jsonObject.put("address", Address);
				jsonObject.put("country", country);
				jsonObject.put("utr", utr);
				jsonObject.put(ResponseMessage.DESCRIPTION, description);
				response = jsonObject.toString();

			}

		} catch (NullPointerException e) {
			jsonObject.put(ResponseMessage.CODE, ResponseMessage.MISSING_PARAMETER);
			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.MISSING_PARAMETER_DESCRIPTION);
			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response = jsonObject.toString();
			LOGGER.error("Error: " + e);

		} catch (ConnectException e) {
			LOGGER.error("Error: " + e);
			jsonObject.put(ResponseMessage.CODE, ResponseMessage.CONNECTION_TIMEOUT);
			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.CONNECTION_TIMEOUT_DESCRIPTION);
			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response = jsonObject.toString();

		} catch (Exception e) {
			LOGGER.error("Error: " + e);
			jsonObject.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response = jsonObject.toString();

		}
		return response;
	}

	public String trxnStatus(String orderId, long merchantId, String subMerchantVPA) throws Exception {

		SSLContext sslContext = YesBanKSSL.yesBankSSL();
		HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
		int responseCode = 0;
		String responseMessage = null;
		JSONObject rspJson = null;
		try {
			String yblTrxnId = null;
			String merorderId = null;
			String rspAmount = null;
			String txnAuthDate = null;
			String status = null;
			String statusDescription = null;
			String payeeVPA = null;
			String payorVPA = null;
			String responseCodeString = null;
			String approvalNo = null;
			String npiTrxnId = null;
			String refId = null;

			String custRefId = null;
			String errorCode = null;
			String rspPayeeAccNo = null;
			String rspPayeeIfscCode = null;
			String rspPayeeMobileNo = null;
			String rspPayerAccNo = null;
			String rspPayerIfscCode = null;
			String rspPayerName = null;
			String rspPayeeAdharNo = null;
			String rspPayeeName = null;
			String rspTrxnNote = null;
			String resp = merchantsRepository.getJsonByMerchantId(merchantId);
			JSONParser parser = new JSONParser();
			LOGGER.info(" Resp " + resp + " submecrhant Vpa : " + subMerchantVPA);
			Object obj = null;
			JSONObject pgMerchantIdJson = null;
			String pgMerchantId = null;
			if (subMerchantVPA == null || subMerchantVPA == "" || subMerchantVPA.isEmpty()
					|| subMerchantVPA.equalsIgnoreCase("NA") || subMerchantVPA == "NA") {
				obj = parser.parse(resp);
				pgMerchantIdJson = (JSONObject) obj;
				pgMerchantId = (String) pgMerchantIdJson.get("pgMerchantId");
				LOGGER.info(" pgMerchantId : IF " + pgMerchantId);

			} else {
				List<?> list = merchantSubMerchantInfoRepository.getSubMerchantIdByVPA(subMerchantVPA);
				Iterator it = list.iterator();
				LOGGER.info("Size getSubMerchantIdByVPA : " + list.size());

				while (it.hasNext()) {
					Object[] objSubMer = (Object[]) it.next();
					pgMerchantId = (String) objSubMer[0];
					LOGGER.info("pgMerchantId " + pgMerchantId);
					String regInfo = (String) objSubMer[1];
					obj = parser.parse(regInfo);
					pgMerchantIdJson = (JSONObject) obj;

				}
				LOGGER.info("pgMerchantId " + pgMerchantId);

			}

			StringBuilder reqMsg = new StringBuilder();
			reqMsg.append(pgMerchantId + "|");
			// reqMsg.append(YesBanKSSL.PG_MERCHANT_ID + "|");

			reqMsg.append(orderId + "|");
			reqMsg.append("|");
			reqMsg.append("|");
			reqMsg.append("|");
			reqMsg.append("|");
			reqMsg.append("|");
			reqMsg.append("|");
			reqMsg.append("|");
			reqMsg.append("|");
			reqMsg.append("|");
			reqMsg.append("|");
			reqMsg.append("|");
			reqMsg.append("NA|");
			reqMsg.append("NA");
			LOGGER.info("Status Request : " + reqMsg.toString());
			JSONObject jsonObject = new JSONObject();
			UPISecurity upisecurity = new UPISecurity();
			jsonObject.put("requestMsg", upisecurity.encrypt(reqMsg.toString(), YesBanKSSL.MERCHANT_KEY));
			jsonObject.put("pgMerchantId", pgMerchantId);
			jsonObject.put("pgMerchantId", YesBanKSSL.PG_MERCHANT_ID);
			String reMsg = jsonObject.toString();
			String encRqst =upisecurity.encrypt(reqMsg.toString(), YesBanKSSL.MERCHANT_KEY);
			LOGGER.info(" enc : " + encRqst);
			LOGGER.info(" final request : " + reMsg);

			URL url = new URL(YesBanKSSL.UPI_PAYMENT_URL + "meTransStatusQuery");
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

			responseCode = con.getResponseCode();
			responseMessage = con.getResponseMessage();
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
			LOGGER.info(response.toString());
			String results = response.toString();
			String decrypt =upisecurity.decrypt(results, YesBanKSSL.MERCHANT_KEY);
			LOGGER.info(" Response to String " + decrypt);
			String[] finalResponse = decrypt.split(Pattern.quote("|"));

			for (int i = 0; i < finalResponse.length; i++) {
				LOGGER.info(finalResponse[i]);

				yblTrxnId = finalResponse[0];
				merorderId = finalResponse[1];
				rspAmount = finalResponse[2];
				txnAuthDate = finalResponse[3];
				status = finalResponse[4];
				statusDescription = finalResponse[5];
				responseCodeString = finalResponse[6];
				approvalNo = finalResponse[7];
				payorVPA = finalResponse[8];
				npiTrxnId = finalResponse[9];
				refId = finalResponse[10];
				custRefId = finalResponse[11];
				rspPayerAccNo = finalResponse[12];
				rspPayerIfscCode = finalResponse[13];
				rspPayerName = finalResponse[14];
				payeeVPA = finalResponse[15];
				rspPayeeIfscCode = finalResponse[16];
				rspPayeeAccNo = finalResponse[17];
				rspPayeeAdharNo = finalResponse[18];
				rspPayeeName = finalResponse[19];
				rspTrxnNote = finalResponse[22];
			}
			rspJson = new JSONObject();
			rspJson.put("yblTrxnId", yblTrxnId);
			rspJson.put("merchantOrderId", merorderId);
			rspJson.put("amount", rspAmount);
			rspJson.put("trxnAuthDate", txnAuthDate);
			rspJson.put("status", status);
			rspJson.put("statusDescription", statusDescription);
			rspJson.put("responseCode", responseCodeString);
			rspJson.put("approvalNo", approvalNo);
			rspJson.put("payorVPA", payorVPA);
			rspJson.put("nPCITrxnId", npiTrxnId);
			rspJson.put("refId", refId);
			rspJson.put("customerRefId", custRefId);
			rspJson.put("payerAccNo", rspPayerAccNo);
			rspJson.put("payerIFSCCode", rspPayerIfscCode);
			rspJson.put("payerName", rspPayerName);
			rspJson.put("payeeVPA", payeeVPA);
			rspJson.put("payeeAccNo", rspPayeeAccNo);
			rspJson.put("payeeIfscCode", rspPayeeIfscCode);
			rspJson.put("payeeAdharNo", rspPayeeAdharNo);
			rspJson.put("payeeName", rspPayeeName);
			rspJson.put("trxnNote", rspTrxnNote);
		} catch (NullPointerException ne) {
			ne.printStackTrace();
			rspJson = new JSONObject();
			rspJson.put("responseCode", "SPM");
			rspJson.put("responseMessage", "Some Parameter Missing");

		} catch (Exception e) {
			e.printStackTrace();
			rspJson = new JSONObject();
			rspJson.put("responseCode", responseCode);
			rspJson.put("responseMessage", responseMessage);

		}
		return rspJson.toString();

	}

	public String trxnStatusRequest(String trxn_id, long merchantId) throws Exception {
		JSONParser parser = new JSONParser();
		JSONObject jsonObject = new JSONObject();
		String trxnStatusResponse = null;
		String response = null;
		String VPA = null;
		String spRefrenceId = null;
		String merchantTrxnRefId = null;
		String trxnId = null;
		try {
			// Search by merchant Trxn RefId
			List<?> getSPRefrenceId = coreTransactionsRepository.findByMerchantTrxnRefIdForUpi(trxn_id, merchantId);
			if (getSPRefrenceId.size() == 0 || getSPRefrenceId.isEmpty()) {
				// Search by UTR
				getSPRefrenceId = coreTransactionsRepository.findByOperatorRefNo(merchantId, trxn_id);
			}

			Iterator it = getSPRefrenceId.iterator();
			while (it.hasNext()) {
				Object[] object = (Object[]) it.next();
				spRefrenceId = (String) object[0];
				VPA = (String) object[1];
				trxnId = Encryption.decString((String) object[2]);
				merchantTrxnRefId = (String) object[3];

				LOGGER.info(" SpRefrenceId " + spRefrenceId + " VPA " + VPA + " trxnId " + trxnId
						+ " merchantTrxnRefId " + merchantTrxnRefId);

			}

			trxnStatusResponse = trxnStatus(spRefrenceId, merchantId, VPA);

			LOGGER.info("Transaction Status API : " + trxnStatusResponse);
			Object collectObj = parser.parse(trxnStatusResponse);
			JSONObject responseCollect = (JSONObject) collectObj;
			String status = (String) responseCollect.get("status");
			String responseCode = (String) responseCollect.get("responseCode");
			if (status.equalsIgnoreCase("SUCCESS") || status.equalsIgnoreCase("PENDING")) {
				String approvalNo = (String) responseCollect.get("approvalNo");
				String trxnNote = (String) responseCollect.get("trxnNote");
				String amount = (String) responseCollect.get("amount");
				String payerIFSCCode = (String) responseCollect.get("payerIFSCCode");
				String payeeVPA = (String) responseCollect.get("payeeVPA");
				String merchantOrderId = (String) responseCollect.get("merchantOrderId");
				String nPCITrxnId = (String) responseCollect.get("nPCITrxnId");
				String customerRefId = (String) responseCollect.get("customerRefId");
				String yblTrxnId = (String) responseCollect.get("yblTrxnId");
				String payeeName = (String) responseCollect.get("payeeName");
				String statusDescription = (String) responseCollect.get("statusDescription");
				String payerAccNo = (String) responseCollect.get("payerAccNo");
				String payeeIfscCode = (String) responseCollect.get("payeeIfscCode");
				String payeeAccNo = (String) responseCollect.get("payeeAccNo");
				String trxnAuthDate = (String) responseCollect.get("trxnAuthDate");
				String payorVPA = (String) responseCollect.get("payorVPA");
				String payerName = (String) responseCollect.get("payerName");

				jsonObject.put("merchantTrxnRefId", merchantTrxnRefId);
				jsonObject.put("trxn_id", trxnId);
				jsonObject.put("approvalNo", approvalNo);
				jsonObject.put("trxnNote", trxnNote);
				jsonObject.put("amount", amount);
				jsonObject.put("payerIFSCCode", payerIFSCCode);
				jsonObject.put("nPCITrxnId", nPCITrxnId);
				jsonObject.put("customerRefId", customerRefId);
				jsonObject.put("bankTrxnId", yblTrxnId);
				jsonObject.put("description", statusDescription);
				jsonObject.put("payerAccNo", payerAccNo);
				jsonObject.put("trxnAuthDate", trxnAuthDate);
				jsonObject.put("payorVPA", payorVPA);
				jsonObject.put("payerName", payerName);
				jsonObject.put("status", status);
				jsonObject.put("description", statusDescription);
				jsonObject.put("code", ResponseMessage.SUCCESS);
				response = jsonObject.toString();

			} else {
				String statusDescription = (String) responseCollect.get("statusDescription");
				String merchantOrderId = (String) responseCollect.get("merchantOrderId");
				String responseCodeString = ResponseMessage.getTrxnStatusDescription(responseCode);
				LOGGER.info(" responseCodeString " + responseCodeString);
				jsonObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				jsonObject.put(ResponseMessage.DESCRIPTION, responseCodeString);
				jsonObject.put("status", status);
				jsonObject.put("originalOrderId", merchantOrderId);
				response = jsonObject.toString();
			}

		} catch (NullPointerException e) {
			LOGGER.error(" NullPointerException " + e);
			jsonObject.put(ResponseMessage.CODE, ResponseMessage.MISSING_PARAMETER);
			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.MISSING_PARAMETER_DESCRIPTION);
			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response = jsonObject.toString();
		} catch (ConnectException e) {
			LOGGER.error(" ConnectException " + e);
			jsonObject.put(ResponseMessage.CODE, ResponseMessage.CONNECTION_TIMEOUT);
			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.CONNECTION_TIMEOUT_DESCRIPTION);
			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response = jsonObject.toString();
		} catch (Exception e) {
			LOGGER.error(" Exception " + e);
			jsonObject.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response = jsonObject.toString();
		}
		LOGGER.info("UPI Status " + response);
		return response;
	}
}
