package com.fidypay.service;

import java.util.Map;

import com.fidypay.request.CustomerDetailsRequest;

public interface EMailAndMobileVerificationService {

	Map<String, Object> otpVerification(String otp, String otpToken, long merchantId) throws Exception;

	Map<String, Object> sendOTPPhone(CustomerDetailsRequest customerDetailsRequest, long merchantId) throws Exception;

	Map<String, Object> sendOTPEmail(CustomerDetailsRequest customerDetailsRequest, long merchantId) throws Exception;

}
