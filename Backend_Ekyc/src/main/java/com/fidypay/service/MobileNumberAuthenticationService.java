package com.fidypay.service;

import java.util.Map;

public interface MobileNumberAuthenticationService {

	Map<String, Object> mobileNumberAuthentication(String mobileNo, Long merchantId, double merchantFloatAmount, String businessName, String email);

}
