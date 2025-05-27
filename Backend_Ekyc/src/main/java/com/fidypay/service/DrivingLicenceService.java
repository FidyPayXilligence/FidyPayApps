package com.fidypay.service;

import java.util.Map;

import com.fidypay.request.DrivingLicenceKarzaRequest;
import com.fidypay.request.DrivingLicenceRequest;
import com.fidypay.request.SearchByDLRequest;

public interface DrivingLicenceService {

	Map<String, Object> verifyDrivingLicence(DrivingLicenceRequest drivingLicenceRequest);

	Map<String, Object> dlNumberBasedSearchDrivingLicence(SearchByDLRequest earchByDLRequest);
	
	Map<String, Object> saveDataForDrivingLicence(DrivingLicenceKarzaRequest drivingLicenceKarzaRequest, long merchantId,
			Double merchantFloatAmount, String businessName, String email);

}
