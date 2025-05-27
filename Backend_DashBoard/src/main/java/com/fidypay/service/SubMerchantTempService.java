package com.fidypay.service;

import java.text.ParseException;
import java.util.Map;

import javax.validation.Valid;

import com.fidypay.request.MerchantSubMerchantOnboardingRequest;
import com.fidypay.request.Pagination;
import com.fidypay.request.SubMerchantOnboardingRequest;

public interface SubMerchantTempService {

	Map<String, Object> saveDetails(long merchantId, MerchantSubMerchantOnboardingRequest subMerchantRequest,
			String merchantBusinessName, String bankId, String logo) throws Exception;

	Map<String, Object> findAllSubMerchant(String startDate, String endDate, Pagination pagination, long merchantId);

	Map<String, Object> findByTokenOrMobile(long merchantId, String value, String key) throws ParseException;

	Map<String, Object> findBySubMerchantTempId(long merchantId, String subMerchantId) throws Exception;

	Map<String, Object> findByToken(String token) throws Exception;

	Map<String, Object> sendOTPPhone(String mobile) throws Exception;

	Map<String, Object> otpVerification(String otp, String otpToken) throws Exception;

	Map<String, Object> updateDetails(@Valid MerchantSubMerchantOnboardingRequest onboardingFormRequest, String token)
			throws Exception;

	Map<String, Object> resendNotification(long merchantId, String subMerchantTempId, String merchantBusinessName,
			String logo) throws Exception;

	Map<String, Object> saveMerchantSubMerchant(String startDate, String endDate) throws Exception;

	Map<String, Object> findByTokenNew(String token) throws Exception;

	Map<String, Object> verifySubMerchant(String subMerchantTempId, String isOnboarding) throws Exception;

	Map<String, Object> saveMerchantSubMerchantByTempId(String subMerchantTempId) throws Exception;

	Map<String, Object> resendNotificationByTempId(String subMerchantTempId) throws Exception;

	Map<String, Object> createVPA(long merchantId, @Valid SubMerchantOnboardingRequest subMerchantRequest,
			String merchantBusinessName, String bankId, String logo) throws Exception;

	// PWA APIs
	Map<String, Object> findActiveInActiveMerchantCount(long merchantId, String merchantUserId);

	Map<String, Object> createVPAPWA(long merchantId, @Valid SubMerchantOnboardingRequest subMerchantRequest,
			String merchantBusinessName, String bankIdUpi, String logo, String merchantUserId) throws Exception;

	Map<String, Object> findAllSubMerchantPWA(String startDate, String endDate, Pagination pagination, long merchantId,
			String merchantUserId);

	Map<String, Object> findByTokenOrMobilePWA(long merchantId, String value, String key, String merchantUserId)
			throws Exception;
}
