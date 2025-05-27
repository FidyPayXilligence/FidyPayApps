package com.fidypay.service;

import java.util.Map;

import com.fidypay.request.DigilockerGetAccessToken;
import com.fidypay.request.DigilockerGetIssuedFiles;

public interface DigilockerService {

	Map<String, Object> saveDataForDigilockerGetAccessToken(DigilockerGetAccessToken digilockergetaccesstoken,
			long merchantId, double merchantFloatAmount, String businessName, String email);

	Map<String, Object> saveDataForDigilockerGetIssuedFiles(DigilockerGetIssuedFiles digilockergetissuedfiles,
			long merchantId, double merchantFloatAmount, String businessName, String email);

}
