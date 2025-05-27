package com.fidypay.service;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import com.fidypay.dto.ForgetPassword;
import com.fidypay.dto.MerchantEdit;
import com.fidypay.dto.MerchantLogin;
import com.fidypay.entity.MerchantType;
import com.fidypay.request.ChangePasswordRequest;
import com.fidypay.request.FloatRequest;
import com.fidypay.request.ForgetPasswordRequest;
import com.fidypay.request.MerchantActiveRequest;
import com.fidypay.request.MerchantRegisterRequest;
import com.fidypay.request.MerchantSubMerchantRequest;
import com.fidypay.request.TransactionHistoryRequest;
import com.fidypay.response.MerchantCommissionDeatilsResponse;

public interface MerchantsService {

	String findMerchantDetails(long merchantId);

	String checkPassword(ChangePasswordRequest changePasswordRequest, long merchantId);

	String loginDashboard(String email, String password);

	Map<String, Object> checkMerchant(String email, String password);

	public String addFloatByAdmin(String floatTo, double amount, double recAmount, String payMode, String desc,
			String txnType, char isRevert, long mId);

	String saveMerchantInfo(FloatRequest floatRequest, long merchantId);

	String saveTillInfo(FloatRequest floatRequest, long mtillId, long merchantId, double amount);

	String addMerchantByAdmin(String requestParam, String fname, String lname, String businessName, String gend,
			String email, String phone, String merchantPassword, String address, String city, String state,
			String zipCode, String country, String latitude, String longitude, String outletType, String tillPassword);

	String saveRegisterMerchantInfo(String fname, String lname, char gender, String email, String address, String city,
			String state, String zipCode, String merchantPassword, String tillPassword, String phone,
			String businessName, MerchantType mType, String outletType, String coordinates);

	Map<String, Object> merchantLoginWithOTP(String mobileNo);

	Map<String, Object> checkMobileNo(String mobileNo);

	Map<String, Object> checkEmailId(String emailid);

	Map<String, Object> merchantSubMerchantRegister(MerchantSubMerchantRequest merchantSubMerchantRequest,
			MerchantType merchantType, String partnerKey);
	/// For API Banking

	String merchantLogin(MerchantLogin merchantLogin);

	String forgetPassword(ForgetPassword forgetPassword);

	String merchantEdit(MerchantEdit merchantEdit);

	String merchantDelete(String userId);

	String sendOTP(String userId);

	String merchantRegister(MerchantRegisterRequest merchantRegisterRequest, MerchantType merchantType);

	String checkPanNumber(String panNo);

	Map<String, Object> transactionHistory(TransactionHistoryRequest transactionHistoryRequest, String clientId);

	Map<String, Object> merchantRegisterAndAssignService(long mId, MerchantRegisterRequest merchantRegister,long authUserId);

	Map<String, Object> merchantDeactive(@Valid MerchantActiveRequest merchantActiveRequest);

	Map<String, Object> findBBPSCommissionRate(long merchantId);

	Map<String, Object> verifyMerchantEmail(String email);

	Map<String, Object> forgetMerchantPassowrd(ForgetPasswordRequest forgetPasswordRequest);

	String findMerchantUserDetails(long merchantId, long merchantUserId);

	Map<String, Object> findKYcChargesRate(long merchantId);

	Map<String, Object> findENachChargesRate(long merchantId);

	Map<String, Object> findPayinChargesRate(long merchantId);

	Map<String, Object> findPayOutChargesRate(long merchantId);

	Map<String, Object> findPgChargesRate(long merchantId);

	Map<String, Object> getOTPDetails();

	List<MerchantCommissionDeatilsResponse> findBBPSCommissionRateExcel(long merchantId);

}
