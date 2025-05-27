package com.fidypay.service;

import java.util.Map;

import javax.validation.Valid;

import com.fidypay.request.EncryptedRequest;
import com.fidypay.request.MerchantUserPWARequest;
import com.fidypay.request.MerchantUserRequest;
import com.fidypay.request.MerchantUserUpdateRequest;
import com.fidypay.request.UpdateUserPassword;

public interface MerchantUserService {

	Map<String, Object> saveDetails(long merchantId, MerchantUserRequest merchantUserRequest) throws Exception;

	Map<String, Object> findByMerchantId(long merchantId, Integer pageNo, Integer pageSize);

	Map<String, Object> updateByMerchantId(MerchantUserUpdateRequest merchantUserUpdateRequest, long merchantId);

	Map<String, Object> deleteByMerchantUserId(long merchantUserId, long merchantId, String isActive);

	Map<String, Object> checkPassword(UpdateUserPassword updateUserPassword, long merchantId) throws Exception;

	Map<String, Object> findByMobileNumber(long merchantId, String mobileNumber);

	Map<String, Object> loginDashboard(String email, String password);

	Map<String, Object> findByMerchantUserId(long merchantUserId, long parseLong);

	Map<String, Object> addRecordsByEmail(String email);

	Map<String, Object> addRecordsToMerchantUser();

	public Map<String, Object> findByMerchantIdUser(long merchantId);

	Object loginDashboardEnc(EncryptedRequest encryptedRequest) throws Exception;

	Object loginDashBoardSandBox(@Valid EncryptedRequest encryptedRequest) throws Exception;

	boolean checkServiceExistOrNot(long merchantId, String serviceName) throws Exception;

	Object loginDashBoardEncOtp(@Valid EncryptedRequest encryptedRequest) throws Exception;

	Map<String, Object> loginDashboardOtp(String email, String password) throws Exception;

	Object verifyLoginOtp(String token, String otp) throws Exception;

	Map<String, Object> saveUserDetails(long merchantId, MerchantUserPWARequest merchantUserRequest) throws Exception;

	Object loginPWA(String merchantId, String mobile)throws Exception;

	Map<String, Object> mobileNoVerification(String mobile, String name) throws Exception;
	Map<String, Object> otpVerification(String otp,String token)throws Exception;

	Map<String, Object> mobileNoLogin(String mobile) throws Exception;
}
