package com.fidypay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.fidypay.ServiceProvider.Signzy.MerchantServiceChargeService;
import com.fidypay.dto.PANApi;
import com.fidypay.dto.PANService;
import com.fidypay.encryption.Encryption;
import com.fidypay.entity.MerchantService;
import com.fidypay.entity.ServiceInfo;
import com.fidypay.repo.MerchantServiceRepository;
import com.fidypay.repo.ServiceInfoRepository;

public class TestServices {

	@Autowired
	private ServiceInfoRepository serviceInfoRepository;

	@Autowired
	private MerchantServiceRepository merchantServiceRepository;

	@Autowired
	private MerchantServiceChargeService chargeService;

	public static void main(String[] args) {

		TestServices test = new TestServices();

		JSONObject finalObject = new JSONObject();

		long merchantId = 307;

		Map<String, Object> map = test.getServiceIdAndCharges(apisNameList(), merchantId);

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
		PANService videoKycService = createPANService("VIDEO KYC", createVideoKycApis(map));

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
		customKYCArray.put(videoKycService);

		// Create JSONObject for kycTypeList
		JSONObject kycTypeList = new JSONObject();
		kycTypeList.put("Individual KYC", individualKYCArray.toList());
		kycTypeList.put("Business KYC", businessKYCArray.toList());
		kycTypeList.put("Bank KYC", bankKYCArray.toList());
		kycTypeList.put("Custom KYC", customKYCArray.toList());

		// Populate finalObject with data
		finalObject.put("kycTypeList", kycTypeList);
		finalObject.put("code", "0x0200");
		finalObject.put("description", "KYC Type list");
		finalObject.put("status", "SUCCESS");

		// Convert finalObject to string and print
		String response = finalObject.toString();
		System.out.println("kycTypeList-------------------->" + response);
	}

	// Helper method to create PANService instances
	private static PANService createPANService(String serviceName, List<PANApi> apis) {
		PANService service = new PANService();
		service.setServiceName(serviceName);
		service.setApis(apis);
		return service;
	}

	// Helper methods to create PANApi instances for various services
	private static List<PANApi> createPANApis(Map<String, Object> map) {
		List<PANApi> apis = new ArrayList<>();
		apis.add(new PANApi("PAN Card Basic Verify", (String) map.get("PAN Card Basic VerifyId"), "0", "Pan Number",
				(String) map.get("PAN Card Basic VerifyCharge")));
		apis.add(new PANApi("OCR PAN", (String) map.get("OCR PANId"), "0", "Pan Image", (String) map.get("OCR PANCharge")));
		return apis;
	}

	private static List<PANApi> createAadhaarApis(Map<String, Object> map) {
		List<PANApi> apis = new ArrayList<>();
		apis.add(new PANApi("Aadhar Verify", (String) map.get("Aadhar VerifyId"), "0", "Aadhar details without OTP",
				(String) map.get("Aadhar VerifyCharge")));
		apis.add(new PANApi("Validate Aadhaar", (String) map.get("Validate AadhaarId"), "0", "Aadhar details with OTP",
				(String) map.get("Validate AadhaarCharge")));
		apis.add(new PANApi("OCR AADHAR", (String) map.get("OCR AADHARId"), "0", "Aadhar Image", (String) map.get("OCR AADHARCharge")));
		return apis;
	}

	private static List<PANApi> createAccountVerificationApis(Map<String, Object> map) {
		List<PANApi> apis = new ArrayList<>();
		apis.add(new PANApi("Bank Account Verification", (String) map.get("Bank Account VerificationId"), "0",
				"Penny Drop", (String) map.get("Bank Account VerificationCharge")));
		apis.add(new PANApi("Bank Account Verification Penny Less",
				(String) map.get("Bank Account Verification Penny LessId"), "0", "Penny Less", (String) map.get("Bank Account Verification Penny LessCharge")));
		return apis;
	}

	private static List<PANApi> createVoterIdApis(Map<String, Object> map) {
		List<PANApi> apis = new ArrayList<>();
		apis.add(new PANApi("Voter Details", (String) map.get("Voter DetailsId"), "0", "Voter Id Number", (String) map.get("Voter DetailsCharge")));
		return apis;
	}

	private static List<PANApi> createVideoKycApis(Map<String, Object> map) {
		List<PANApi> apis = new ArrayList<>();
		apis.add(new PANApi("VIDEO LIVENESS", (String) map.get("VIDEO LIVENESSId"), "0", "Upload video", (String) map.get("VIDEO LIVENESSCharge")));
		return apis;
	}

	private static List<PANApi> createFaceMatchApis(Map<String, Object> map) {
		List<PANApi> apis = new ArrayList<>();
		apis.add(new PANApi("Face Matcher", (String) map.get("Face MatcherId"), "0", "Upload two images", (String) map.get("Face MatcherCharge")));
		return apis;
	}

	private static List<PANApi> createImageLivenessApis(Map<String, Object> map) {
		List<PANApi> apis = new ArrayList<>();
		apis.add(new PANApi("Image Liveness", (String) map.get("Image LivenessId"), "0", "Upload image", (String) map.get("Image LivenessCharge")));
		return apis;
	}

	private static List<PANApi> createGSTApis(Map<String, Object> map) {
		List<PANApi> apis = new ArrayList<>();
		apis.add(new PANApi("GST VERIFY USING PAN", (String) map.get("GST VERIFY USING PANId"), "0", "Pan Number",
				(String) map.get("GST VERIFY USING PANCharge")));
		apis.add(new PANApi("GST Number V2", (String) map.get("GST Number V2Id"), "0", "GST Number(Detaild Search)",
				(String) map.get("GST Number V2Charge")));
		apis.add(new PANApi("GST Number", (String) map.get("GST NumberId"), "0", "GST Number(Basic Search)", (String) map.get("GST NumberCharge")));
		return apis;
	}

	private static List<PANApi> createCKYCApis(Map<String, Object> map) {
		List<PANApi> apis = new ArrayList<>();
		apis.add(
				new PANApi("CKycSearch", (String) map.get("CKycSearchId"), "0", "Search CKYC using Pan number", (String) map.get("CKycSearchCharge")));
		apis.add(new PANApi("CKycDetails", (String) map.get("CKycDetailsId"), "0", "Search CKYC using ckyc Id and DOB",
				(String) map.get("CKycDetailsCharge")));
		apis.add(new PANApi("MOBILE TO PAN DETAILS", (String) map.get("MOBILE TO PAN DETAILSId"), "0",
				"Mobile Number and DOB", (String) map.get("MOBILE TO PAN DETAILSCharge")));
		return apis;
	}

	private static List<PANApi> createDLApis(Map<String, Object> map) {
		List<PANApi> apis = new ArrayList<>();
		apis.add(new PANApi("DRIVING LICENSE", (String) map.get("DRIVING LICENSEId"), "0", "Driving License Number",
				(String) map.get("DRIVING LICENSECharge")));
		apis.add(new PANApi("OCR DRIVING LICENSE", (String) map.get("OCR DRIVING LICENSEId"), "0",
				"Driving License Image", (String) map.get("OCR DRIVING LICENSECharge")));
		return apis;
	}

	private static List<String> apisNameList() {

		List<String> apiNameList = new ArrayList<>();

		apiNameList.add("PAN Card Basic Verify");
		apiNameList.add("OCR PAN");
		apiNameList.add("Aadhar Verify");
		apiNameList.add("Validate Aadhaar");
		apiNameList.add("OCR AADHAR");
		apiNameList.add("Bank Account Verification");
		apiNameList.add("Bank Account Verification Penny Less");
		apiNameList.add("Voter Details");
		apiNameList.add("DRIVING LICENSE");
		apiNameList.add("OCR DRIVING LICENSE");
		apiNameList.add("CKycSearch");
		apiNameList.add("CKycDetails");
		apiNameList.add("MOBILE TO PAN DETAILS");
		apiNameList.add("GST VERIFY USING PAN");
		apiNameList.add("GST Number V2");
		apiNameList.add("GST Number");
		apiNameList.add("Image Liveness");
		apiNameList.add("Face Matcher");
		apiNameList.add("VIDEO LIVENESS");

		return apiNameList;
	}

	public Map<String, Object> getServiceIdAndCharges(List<String> apiNameList, long merchantId) {

		Map<String, Object> map = new HashMap<>();

		double amtInDouble = 1.0;

		try {

			for (String serviceName : apiNameList) {

				ServiceInfo serviceInfo = serviceInfoRepository.findServiceByName(Encryption.encString(serviceName));
				Long serviceId = serviceInfo.getServiceId();

				MerchantService merchantsService = merchantServiceRepository.findByMerchantIdAndServiceId(merchantId,
						serviceId);
				Long merchantServiceId = merchantsService.getMerchantServiceId();

				double charges = chargeService.getMerchantServiceChargesV2(merchantServiceId, amtInDouble);

				map.put(serviceName + "Id", serviceId);
				map.put(serviceName + "Charge", charges);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return map;

	}
}
