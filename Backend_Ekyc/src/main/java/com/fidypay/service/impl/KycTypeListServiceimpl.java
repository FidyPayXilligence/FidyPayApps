package com.fidypay.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fidypay.ServiceProvider.Signzy.MerchantServiceChargeService;
import com.fidypay.dto.PANApi;
import com.fidypay.dto.PANService;
import com.fidypay.encryption.Encryption;
import com.fidypay.entity.MerchantService;
import com.fidypay.entity.ServiceInfo;
import com.fidypay.repo.MerchantServiceRepository;
import com.fidypay.repo.ServiceInfoRepository;

@Service
public class KycTypeListServiceimpl {

	private static final Logger LOGGER = LoggerFactory.getLogger(KycTypeListServiceimpl.class);

	@Autowired
	private ServiceInfoRepository serviceInfoRepository;

	@Autowired
	private MerchantServiceRepository merchantServiceRepository;

	@Autowired
	private MerchantServiceChargeService chargeService;

	public String kycTypeList(long merchantId) {

		JSONObject finalObject = new JSONObject();

		Map<String, Object> map = getServiceIdAndCharges(apisNameList(), merchantId);

		LOGGER.info("Service Id and Charges map------->{}", map);

		// Create PANService instances
		PANService panService = createPANService("PAN", createPANApis(map));
		PANService aadhaarService = createPANService("AADHAAR", createAadhaarApis(map));
		PANService accountVerificationService = createPANService("ACCOUNT VERIFICATION",
				createAccountVerificationApis(map));
		PANService voterIdService = createPANService("VOTER ID", createVoterIdApis(map));
		PANService dlService = createPANService("DL", createDLApis(map));
		PANService ckycService = createPANService("CKYC", createCKYCApis(map));
		PANService gstService = createPANService("GST", createGSTApis(map));
		PANService imageLivenessService = createPANService("IMAGE LIVENESS", createImageLivenessApis(map));
		PANService faceMatchService = createPANService("FACE MATCH", createFaceMatchApis(map));
//		PANService videoKycService = createPANService("VIDEO KYC", createVideoKycApis(map));

		// Create JSONArray for Individual KYC
		JSONArray individualKYCArray = new JSONArray();
		individualKYCArray.put(panService);
		individualKYCArray.put(aadhaarService);
		individualKYCArray.put(accountVerificationService);
		individualKYCArray.put(voterIdService);
		individualKYCArray.put(dlService);
		individualKYCArray.put(ckycService);

		// Create JSONArray for Business KYC
		JSONArray businessKYCArray = new JSONArray();
		businessKYCArray.put(panService);
		businessKYCArray.put(accountVerificationService);
		businessKYCArray.put(gstService);

		// Create JSONArray for Bank KYC
		JSONArray bankKYCArray = new JSONArray();
		bankKYCArray.put(panService);
		bankKYCArray.put(aadhaarService);
		bankKYCArray.put(accountVerificationService);

		// Create JSONArray for Custom KYC
		JSONArray customKYCArray = new JSONArray();
		customKYCArray.put(panService);
		customKYCArray.put(aadhaarService);
		customKYCArray.put(accountVerificationService);
		customKYCArray.put(gstService);
		customKYCArray.put(voterIdService);
		customKYCArray.put(imageLivenessService);
		customKYCArray.put(faceMatchService);
		customKYCArray.put(dlService);
		customKYCArray.put(ckycService);
//		customKYCArray.put(videoKycService);

		// Create JSONObject for kycTypeList
		JSONObject kycTypeList = new JSONObject();
//		kycTypeList.put("Individual KYC", individualKYCArray.toList());
//		kycTypeList.put("Business KYC", businessKYCArray.toList());
//		kycTypeList.put("Bank KYC", bankKYCArray.toList());
		kycTypeList.put("Custom KYC", customKYCArray.toList());

		// Populate finalObject with data
		finalObject.put("kycTypeList", kycTypeList);
		finalObject.put("code", "0x0200");
		finalObject.put("description", "KYC Type list");
		finalObject.put("status", "SUCCESS");

		// Convert finalObject to string and print

		LOGGER.info("Response: " + finalObject.toString());

		return finalObject.toString();
	}

	// API's service name list
	private static List<String> apisNameList() {

		List<String> apiNameList = new ArrayList<>();
		apiNameList.add("PAN Card Basic Verify");
		apiNameList.add("OCR PAN");
		apiNameList.add("Aadhaar Verify");
//		apiNameList.add("Validate Aadhaar");
		apiNameList.add("OCR AADHAAR");
		apiNameList.add("Bank Account Verification");
		apiNameList.add("Bank Account Verification Penny Less");
		apiNameList.add("Voter Details");
		apiNameList.add("DRIVING LICENSE");
		apiNameList.add("OCR DRIVING LICENSE");
		apiNameList.add("CKycSearch");
		apiNameList.add("CKycDetails");
//		apiNameList.add("MOBILE TO PAN DETAILS");
		apiNameList.add("GST VERIFY USING PAN");
//		apiNameList.add("GST Number V2");
		apiNameList.add("GST Number");
		apiNameList.add("Image Liveness");
		apiNameList.add("Face Matcher");
//		apiNameList.add("VIDEO LIVENESS");

		return apiNameList;
	}

	// Helper method to get Charges of API
	private Map<String, Object> getServiceIdAndCharges(List<String> apiNameList, long merchantId) {
		Map<String, Object> map = new HashMap<>();
		double amtInDouble = 1.0;

		try {
			for (String serviceName : apiNameList) {
				LOGGER.info("Processing serviceName: {}", serviceName);
				ServiceInfo serviceInfo = serviceInfoRepository.findServiceByName(Encryption.encString(serviceName));
				long serviceId = 0;
				double charges = 0.0;
				if (serviceInfo != null) {
					serviceId = serviceInfo.getServiceId();
					MerchantService merchantService = merchantServiceRepository.findByMerchantIdAndServiceId(merchantId,
							serviceId);
					if (merchantService != null) {
						long merchantServiceId = merchantService.getMerchantServiceId();
						charges = chargeService.getMerchantServiceChargesV2(merchantServiceId, amtInDouble);
					} else {
						LOGGER.info("MerchantService not found for serviceName: {}", serviceName);
					}
				} else {
					LOGGER.error("ServiceInfo not found for serviceName: {}", serviceName);
				}
				LOGGER.info("{} - Charge: {}", serviceName, charges);
				map.put(serviceName + "Id", serviceId);
				map.put(serviceName + "Charge", charges);
			}
		} catch (Exception e) {
			LOGGER.error("Error occurred while processing getServiceIdAndCharges: {}", e.getMessage());
			e.printStackTrace();
		}
		return map;
	}

	// Helper method to create PANService instances
	private static PANService createPANService(String serviceName, List<PANApi> apis) {
		PANService service = new PANService();
		service.setServiceName(serviceName);
		service.setDescription("");
		service.setFlag("0");
		service.setStepId("0");
		service.setStepName("0");
		service.setApis(apis);
		return service;
	}

	// Helper methods to create PANApi instances for various services
	private static List<PANApi> createPANApis(Map<String, Object> map) {

		List<PANApi> apis = new ArrayList<>();
		apis.add(new PANApi("PAN Card Basic Verify", String.valueOf(map.get("PAN Card Basic VerifyId")), "0",
				"PAN Number", String.valueOf(map.get("PAN Card Basic VerifyCharge"))));

		apis.add(new PANApi("OCR PAN", String.valueOf(map.get("OCR PANId")), "0", "PAN Image",
				String.valueOf(map.get("OCR PANCharge"))));
		return apis;
	}

	// Helper methods to create AadhaarApis instances for various services
	private static List<PANApi> createAadhaarApis(Map<String, Object> map) {
		List<PANApi> apis = new ArrayList<>();
		apis.add(new PANApi("Aadhaar Verify", String.valueOf(map.get("Aadhaar VerifyId")), "0", "Aadhaar Number",
				String.valueOf(map.get("Aadhaar VerifyCharge"))));
//		apis.add(new PANApi("Validate Aadhaar", String.valueOf(map.get("Validate AadhaarId")), "0", "Aadhar details with OTP",
//				String.valueOf(map.get("Validate AadhaarCharge"))));
		apis.add(new PANApi("OCR AADHAAR", String.valueOf(map.get("OCR AADHAARId")), "0", "Aadhaar Image",
				String.valueOf(map.get("OCR AADHAARCharge"))));
		return apis;
	}

	// Helper methods to create AccountVerificationApis instances for various
	// services
	private static List<PANApi> createAccountVerificationApis(Map<String, Object> map) {
		List<PANApi> apis = new ArrayList<>();
		apis.add(new PANApi("Bank Account Verification", String.valueOf(map.get("Bank Account VerificationId")), "0",
				"Bank Account Verification Penny Drop", String.valueOf(map.get("Bank Account VerificationCharge"))));
		apis.add(new PANApi("Bank Account Verification Penny Less",
				String.valueOf(map.get("Bank Account Verification Penny LessId")), "0",
				"Bank Account Verification Penny Less",
				String.valueOf(map.get("Bank Account Verification Penny LessCharge"))));
		return apis;
	}

	// Helper methods to create VoterIdApis instances for various services
	private static List<PANApi> createVoterIdApis(Map<String, Object> map) {
		List<PANApi> apis = new ArrayList<>();
		apis.add(new PANApi("Voter Details", String.valueOf(map.get("Voter DetailsId")), "0", "Voter Id Number",
				String.valueOf(map.get("Voter DetailsCharge"))));
		return apis;
	}

	// Helper methods to create VideoKycApis instances for various services
//	private static List<PANApi> createVideoKycApis(Map<String, Object> map) {
//		List<PANApi> apis = new ArrayList<>();
//		apis.add(new PANApi("VIDEO LIVENESS", String.valueOf(map.get("VIDEO LIVENESSId")), "0", "Upload video",
//				String.valueOf(map.get("VIDEO LIVENESSCharge"))));
//		return apis;
//	}

	// Helper methods to create FaceMatchApis instances for various services
	private static List<PANApi> createFaceMatchApis(Map<String, Object> map) {
		List<PANApi> apis = new ArrayList<>();
		apis.add(new PANApi("Face Matcher", String.valueOf(map.get("Face MatcherId")), "0", "Upload two images",
				String.valueOf(map.get("Face MatcherCharge"))));
		return apis;
	}

	// Helper methods to create ImageLivenessApis instances for various services
	private static List<PANApi> createImageLivenessApis(Map<String, Object> map) {
		List<PANApi> apis = new ArrayList<>();
		apis.add(new PANApi("Image Liveness", String.valueOf(map.get("Image LivenessId")), "0", "Upload image",
				String.valueOf(map.get("Image LivenessCharge"))));
		return apis;
	}

	// Helper methods to create GSTApis instances for various services
	private static List<PANApi> createGSTApis(Map<String, Object> map) {
		List<PANApi> apis = new ArrayList<>();
		apis.add(new PANApi("GST VERIFY USING PAN", String.valueOf(map.get("GST VERIFY USING PANId")), "0",
				"GST PAN Number", String.valueOf(map.get("GST VERIFY USING PANCharge"))));
//		apis.add(new PANApi("GST Number V2", String.valueOf(map.get("GST Number V2Id")), "0", "GST Number(Detaild Search)",
//				String.valueOf(map.get("GST Number V2Charge"))));
		apis.add(new PANApi("GST Number", String.valueOf(map.get("GST NumberId")), "0", "GST Number(Basic Search)",
				String.valueOf(map.get("GST NumberCharge"))));
		return apis;
	}

	// Helper methods to create CKYCApis instances for various services
	private static List<PANApi> createCKYCApis(Map<String, Object> map) {
		List<PANApi> apis = new ArrayList<>();
		apis.add(new PANApi("CKyc", String.valueOf(map.get("CKycSearchId")), "0", "Search CKYC using Pan number",
				String.valueOf(map.get("CKycSearchCharge"))));
//		apis.add(new PANApi("CKycDetails", String.valueOf(map.get("CKycDetailsId")), "0", "Search CKYC using ckyc Id and DOB",
//				String.valueOf(map.get("CKycDetailsCharge"))));
		// apis.add(new PANApi("MOBILE TO PAN DETAILS", String.valueOf(map.get("MOBILE
		// TO PAN DETAILSId")), "0",
		// "Mobile Number and DOB", String.valueOf(map.get("MOBILE TO PAN
		// DETAILSCharge"))));
		return apis;
	}

	// Helper methods to create DLApis instances for various services
	private static List<PANApi> createDLApis(Map<String, Object> map) {
		List<PANApi> apis = new ArrayList<>();
		apis.add(new PANApi("DRIVING LICENSE", String.valueOf(map.get("DRIVING LICENSEId")), "0",
				"Driving License Number", String.valueOf(map.get("DRIVING LICENSECharge"))));
		apis.add(new PANApi("OCR DRIVING LICENSE", String.valueOf(map.get("OCR DRIVING LICENSEId")), "0",
				"Driving License Image", String.valueOf(map.get("OCR DRIVING LICENSECharge"))));
		return apis;
	}

}
