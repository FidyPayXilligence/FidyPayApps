package com.fidypay.service;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public interface OCRService {

	 Map<String, Object> saveDataForOCRKycAWS(MultipartFile file, long merchantId, double merchantWallet,
			String docType, String businessName, String email);
	 
	 Map<String, Object> saveDataForOCRKycPan(MultipartFile file, long merchantId, double merchantWallet, String businessName, String email);
	 
	 Map<String, Object> saveDataForOCRKycAadhar(MultipartFile file, long merchantId, double merchantWallet, String businessName, String email);
	 
	 Map<String, Object> saveDataForOCRKycDrivingLicense(MultipartFile file, long merchantId, double merchantWallet, String businessName, String email);
	 
	 Map<String, Object> saveDataForOCRKycPanV2(String url, long merchantId, double merchantWallet,
				String businessName, String email);
	 
	 Map<String, Object> saveDataForOCRKycAadharV2(String url, long merchantId, double merchantWallet, String businessName, String email);

	 Map<String, Object> saveDataForOCRKycDrivingLicenseV2(String url, long merchantId, double merchantWallet, String businessName, String email);
}
