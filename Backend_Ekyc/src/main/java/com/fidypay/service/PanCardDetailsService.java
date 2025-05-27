package com.fidypay.service;

import java.util.Map;

import com.fidypay.request.DrivingLicenceKarzaRequest;
import com.fidypay.request.FetchPassportRequest;
import com.fidypay.request.NSDLRequest;
import com.fidypay.request.VoterIdRequestV2;

public interface PanCardDetailsService {

	Map<String, Object> fetchPanDetails(String panNumber, long merchantId, Double merchantFloatAmount, String businessName, String email);

	Map<String, Object> fetchVoterDetails(VoterIdRequestV2 voterIdRequestV2, long merchantId, Double merchantFloatAmount, String password, String businessName, String email);

	Map<String, Object> fetchPassportDetails(FetchPassportRequest fetchPassportRequest, long merchantId, Double merchantFloatAmount, String businessName, String email);
	
	Map<String, Object> fetchDrivingLicenseDetails(DrivingLicenceKarzaRequest drivingLicenceKarzaRequest, long merchantId, Double merchantFloatAmount, String businessName, String email);
	
	//Map<String, Object> fetchGstinDetails(String gSTIN, long merchantId, Double merchantFloatAmount, String businessName, String email);
	
	Map<String, Object> bankAccountVerificationPennyDrop(NSDLRequest nsdlRequest, long merchantId, Double merchantFloatAmount, String bussinessName, String email);

	Map<String, Object> fetchGstinDetails(String gSTIN, long merchantId, Double merchantFloatAmount, String businessName, String email, String providerName);

}
