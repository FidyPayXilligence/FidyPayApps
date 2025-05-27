package com.fidypay.utils.ex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fidypay.request.AccountVerificationRequest;
import com.fidypay.request.NSDLRequest;
import com.fidypay.request.PanToMobileRequest;
import com.fidypay.utils.constants.ResponseMessage;

public class ValidateUtils {

	public final static String PAN = "[A-Z]{5}[0-9]{4}[A-Z]{1}";
	public final static String VOTERID = "[A-Z]{3}[0-9]{7}";
	public final static String DRIVING_LICENSE = "^[a-zA-Z0-9]{8,20}$";
	public final static String GSTIN = "[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}";
	public final static String RC = "[A-Z]{2}[0-9]{2}[A-Z]{2}[0-9]{4}";
	public final static String FSSAI = "[0-9]{14}";
	public final static String UDYOG_AADHAAR = "[A-Z]{2}[0-9]{2}[A-Z]{1}[0-9]{7}";
	public final static String CIN = "^([LUu]{1})([0-9]{5})([A-Za-z]{2})([0-9]{4})([A-Za-z]{3})([0-9]{6})$";
	public final static String DIN = "[0-9]{8}";
	public final static String LLPIN = "[0-9]{7}";
	public final static String FLLPIN = "[A-Z]{3}[0-9]{7}";
	public final static String FCRN = "[0-9]{6}";
	public final static String MESSAGE = " format is incorrect.";
	public final static String FIRST_NAME = "^[a-zA-Z\\s]{1,30}+$";
	public final static String LAST_NAME = "^[a-zA-Z\\s]{1,30}+$";
	public final static String PHONE_NUMBER = "\\d{10}";
	public final static String AADHAAR = "^[2-9]{1}[0-9]{3}[0-9]{4}[0-9]{4}$";
	public final static String PASSPORT = "^[A-Za-z][0-9]{7}$";
	public final static String NAME = "^[A-Za-z\\s]+$";
	public final static String MOBILE = "^[6789]\\d{9}$";
	//
	public final static String BENEFICIARY_ACC_NO = "^((?=[A-Za-z0-9@])(?![_\\\\-]).)*$";

	private static final Logger LOGGER = LoggerFactory.getLogger(ValidateUtils.class);

	public static Map<String, Object> validate(String merchantDocumentType, String documentIdNumber) {

		Map<String, Object> map = new HashMap<>();

		if ((merchantDocumentType.equals("PAN") || merchantDocumentType.equals("VOTERID")
				|| merchantDocumentType.equals("DRIVING_LICENSE") || merchantDocumentType.equals("GSTIN")
				|| merchantDocumentType.equals("RC") || merchantDocumentType.equals("FSSAI")
				|| merchantDocumentType.equals("UDYOG_AADHAAR") || merchantDocumentType.equals("CIN")
				|| merchantDocumentType.equals("DIN") || merchantDocumentType.equals("LLPIN")
				|| merchantDocumentType.equals("FLLPIN") || merchantDocumentType.equals("FCRN"))) {

			if (!merchantDocumentType.equals("CIN")) {
				documentIdNumber = documentIdNumber.toUpperCase();
			}

			if (merchantDocumentType.equals("PAN") && !documentIdNumber.matches(PAN)) {

				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Invalid PAN format.");
				return map;
			}

			if (merchantDocumentType.equals("VOTERID") && !documentIdNumber.matches(VOTERID)) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Invalid VOTERID format.");
				return map;
			}

			if (merchantDocumentType.equals("DRIVING_LICENSE") && !documentIdNumber.matches(DRIVING_LICENSE)) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION,
						"Invalid DRIVING_LICENSE.");
				return map;
			}

			if (merchantDocumentType.equals("GSTIN") && !documentIdNumber.matches(GSTIN)) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Invalid GSTIN format.");
				return map;
			}

			if (merchantDocumentType.equals("RC") && !documentIdNumber.matches(RC)) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Invalid RC format.");
				return map;
			}

			if (merchantDocumentType.equals("FSSAI") && !documentIdNumber.matches(FSSAI)) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Invalid FSSAI format.");
				return map;
			}

			if (merchantDocumentType.equals("UDYOG_AADHAAR") && !documentIdNumber.matches(UDYOG_AADHAAR)) {
				// UDYAM-MP-00-1234567
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Invalid UDYOG_AADHAAR format.");
				return map;
			}

			if (merchantDocumentType.equals("CIN") && !documentIdNumber.matches(CIN)) {
				// U12345AB6784CDE123456
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Invalid CIN format.");
				return map;
			}

			if (merchantDocumentType.equals("DIN") && !documentIdNumber.matches(DIN)) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Invalid DIN format.");
				return map;
			}

			if (merchantDocumentType.equals("LLPIN") && !documentIdNumber.matches(LLPIN)) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Invalid LLPIN format.");
				return map;
			}

			if (merchantDocumentType.equals("FLLPIN") && !documentIdNumber.matches(FLLPIN)) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Invalid FLLPIN format.");
				return map;
			}

			if (merchantDocumentType.equals("FCRN") && !documentIdNumber.matches(FCRN)) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Invalid FCRN format.");
				return map;
			}

		} else {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION,
					"Invalid Identity Document.Please pass Valid document types are PAN, VOTERID, DRIVING_LICENSE, GSTIN, RC, FSSAI, UDYOG_AADHAAR, CIN, DIN, LLPIN, FLLPIN, FCRN.");
			return map;
		}

		return map;

	}

	public static Map<String, Object> validateRequest(String merchantDocumentType, String documentIdNumber) {

		Map<String, Object> map = new HashMap<>();

		if (merchantDocumentType.equals("PAN") && !documentIdNumber.matches("()|[A-Z]{5}[0-9]{4}[A-Z]{1}")) {

			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Invalid PAN format.");
			return map;
		}

		if (merchantDocumentType.equals("VOTERID") && !documentIdNumber.matches("()|[A-Z]{3}[0-9]{7}")) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Invalid VOTERID format.");
			return map;
		}

		if (merchantDocumentType.equals("DRIVING_LICENSE")
				&& !documentIdNumber.matches("()|[A-Z]{2}[0-9]{2}[A-Z]{1}-[0-9]{4}-[0-9]{7}")) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION,
					"Invalid DRIVING_LICENSE format.The format should be XXXXX-XXXX-XXXXXXX.");
			return map;
		}

		if (merchantDocumentType.equals("AADHAAR")
				&& !documentIdNumber.matches("^()|[2-9]{1}[0-9]{3}[0-9]{4}[0-9]{4}$")) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Invalid AADHAAR format.");
			return map;
		}

		if (merchantDocumentType.equals("PASSPORT") && !documentIdNumber.matches("^()|[A-Za-z][0-9]{7}$")) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Invalid PASSPORT format.");
			return map;
		}

		return map;
	}

	public static Map<String, Object> validateCKycRequest(String name, String documentType, String idNumber,
			String gender, String dob) {

		LOGGER.info("Inside validateCKycRequest");

		Map<String, Object> map = new HashMap<>();

		idNumber = idNumber.toUpperCase();

		if (documentType.equals("PAN") && !idNumber.matches("[A-Z]{5}[0-9]{4}[A-Z]{1}")) {

			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Invalid PAN.");
			return map;
		}

		if (documentType.equals("VOTERID") && !idNumber.matches("[A-Z]{3}[0-9]{7}")) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Invalid VOTERID.");
			return map;
		}

		if (documentType.equals("DRIVING_LICENSE") && !idNumber.matches("[A-Z]{2}[0-9]{2}[A-Z]{1}-[0-9]{4}-[0-9]{7}")) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Invalid DRIVING_LICENSE.");
			return map;
		}

		if (documentType.equals("AADHAAR") && !idNumber.matches("^[2-9]{1}[0-9]{3}[0-9]{4}[0-9]{4}$")) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Invalid AADHAAR");
			return map;
		}

		if (documentType.equals("PASSPORT") && !idNumber.matches("^[A-Za-z][0-9]{7}$")) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Invalid PASSPORT.");
			return map;
		}

		if (documentType.equals("AADHAAR") && idNumber.matches("^[2-9]{1}[0-9]{3}[0-9]{4}[0-9]{4}$")) {
			String aadhaarNumber = idNumber.substring(8, 12) + "|" + name + "|" + dob + "|" + gender;
			LOGGER.info("aadhaarNumber: {}", aadhaarNumber);
			map.put("aadhaarNumber", aadhaarNumber);
			return map;
		}

		return map;
	}

	public static Map<String, Object> validateUser(PanToMobileRequest panToMobileRequest) {

		Map<String, Object> map = new HashMap<>();
		List<String> descriptionList = new ArrayList<>();

		String name = panToMobileRequest.getName();
		String mobile = panToMobileRequest.getMobile();

		if (!isValidName(name) || !isValidMobile(mobile)) {
			if (!isValidName(name)) {
				descriptionList.add("name -> name should be characters.");
			}
			if (!isValidMobile(mobile)) {
				descriptionList.add("mobile -> Invalid mobile number.");
			}

			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, descriptionList);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}

		return map;
	}

	private static boolean isValidName(String name) {
		// Use the NAME pattern constant or regex here
		return name.matches(NAME);
	}

	private static boolean isValidMobile(String mobile) {
		// Use the MOBILE pattern constant or regex here
		return mobile.matches(MOBILE);
	}

	public static Map<String, Object> accountVerification(AccountVerificationRequest accountVerificationRequest) {

		Map<String, Object> map = new HashMap<>();
		List<String> descriptionList = new ArrayList<>();

		String beneficiaryAccNo = accountVerificationRequest.getBeneficiaryAccNo();
		String beneficiaryIfscCode = accountVerificationRequest.getBeneficiaryIfscCode();
		String merchantTrxnRefId = accountVerificationRequest.getMerchantTrxnRefId();

		if (!beneficiaryAccNo.matches(BENEFICIARY_ACC_NO) || !beneficiaryIfscCode.matches("^[A-Z]{4}0[A-Z0-9]{6}$")
				|| !merchantTrxnRefId.matches(BENEFICIARY_ACC_NO)) {

			if (!beneficiaryAccNo.matches(BENEFICIARY_ACC_NO)) {
				descriptionList.add("beneficiaryAccNo -> pass valid beneficiaryAccNo.");
			}
			if (!beneficiaryIfscCode.matches("^[A-Z]{4}0[A-Z0-9]{6}$")) {
				descriptionList.add("beneficiaryIfscCode -> pass valid beneficiaryIfscCode.");
			}
			if (!merchantTrxnRefId.matches(BENEFICIARY_ACC_NO)) {
				descriptionList.add("merchantTrxnRefId -> pass valid merchantTrxnRefId.");
			}

			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, descriptionList);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}

		return map;
	}
	
	public static Map<String, Object> accountVerificationPennyDrop(NSDLRequest nsdlRequest) {

		Map<String, Object> map = new HashMap<>();
		List<String> descriptionList = new ArrayList<>();

		String beneficiaryAccNo = nsdlRequest.getBankAccountNumber();
		String beneficiaryIfscCode = nsdlRequest.getBankIFSCCode();
		String merchantTrxnRefId = nsdlRequest.getMerchantTrxnRefId();

		if (!beneficiaryAccNo.matches(BENEFICIARY_ACC_NO) || !beneficiaryIfscCode.matches("^[A-Z]{4}0[A-Z0-9]{6}$")
				|| !merchantTrxnRefId.matches(BENEFICIARY_ACC_NO)) {

			if (!beneficiaryAccNo.matches(BENEFICIARY_ACC_NO)) {
				descriptionList.add("beneficiaryAccNo -> pass valid beneficiaryAccNo.");
			}
			if (!beneficiaryIfscCode.matches("^[A-Z]{4}0[A-Z0-9]{6}$")) {
				descriptionList.add("beneficiaryIfscCode -> pass valid beneficiaryIfscCode.");
			}
			if (!merchantTrxnRefId.matches(BENEFICIARY_ACC_NO)) {
				descriptionList.add("merchantTrxnRefId -> pass valid merchantTrxnRefId.");
			}

			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, descriptionList);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}

		return map;
	}

}
