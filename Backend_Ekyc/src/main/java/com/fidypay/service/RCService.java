package com.fidypay.service;

import java.util.Map;

public interface RCService {

	 Map<String, Object> saveDataForRCValidate(String vehicleNumber, long merchantId, double merchantWallet, String businessName, String email);

}
