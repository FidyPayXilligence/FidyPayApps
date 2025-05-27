package com.fidypay.service;

import java.util.Map;

import com.fidypay.request.OneBharatRequest;

public interface OneBharatService {

	Map<String, Object> initiateConsent(OneBharatRequest oneBharatRequest, long merchantId, double merchantWallet, String businessName, String email);

	Map<String, Object> reportsByConsentId(String consentRefId, long merchantId, double merchantWallet, String businessName, String email);

}
