package com.fidypay.service;

import java.util.Map;

import com.fidypay.request.AddressRequest;
import com.fidypay.request.DrivingLicenceKarzaRequest;
import com.fidypay.request.NameRequest;

public interface NameAndAddressSimilarityService {

	Map<String, Object> nameSimilarity(NameRequest nameRequest, Long merchantId, double merchantFloatAmount, String businessName, String email);

	Map<String, Object> addressSimilarity(AddressRequest addressRequest, Long merchantId, double merchantFloatAmount, String businessName, String email);

	Map<String, Object> drivingLicence(DrivingLicenceKarzaRequest drivingLicenceKarzaRequest, long merchantId,
			Double merchantFloatAmount, String businessName, String email);

}
