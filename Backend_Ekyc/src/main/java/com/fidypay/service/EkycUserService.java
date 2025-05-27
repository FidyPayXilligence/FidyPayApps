package com.fidypay.service;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.web.multipart.MultipartFile;

import com.fidypay.dto.BulkEkycUserPayload;
import com.fidypay.request.EKycUpdateRequest;
import com.fidypay.request.EKycWorkFlowTempRequest;
import com.fidypay.request.EkycRequest;
import com.fidypay.request.EkycRequests;
import com.fidypay.request.EkycUserFilterRequest;
import com.fidypay.request.EkycUserRequest;

public interface EkycUserService {

	Map<String, Object> saveEkycUserInfo(EkycRequest eKycRequest, long merchantId, String businessName)
			throws Exception;

	Map<String, Object> findByEkycUserId(long ekycUserId, long merchantId) throws Exception;

	Map<String, Object> findAllUser(long merchantId, EkycUserRequest request);

	Map<String, Object> updateIsVerified(long ekycUserId, long merchantId,String isVerified);

	Map<String, Object> IsDeletByEkycUserId(long ekycUserId, long merchantId) throws Exception;

	Map<String, Object> findAllUserByRequest(long merchantId, EkycRequests ekycRequests);

	Map<String, Object> sendOTPPhone(String userPhone,String userUniqueId) throws Exception;

	Map<String, Object> otpVerification(String otp, String otpToken, long merchantId) throws Exception;

	Map<String, Object> findByEkycWorkflowId(long ekycWorkflowId, long merchantId) throws Exception;
	
	
	Map<String, Object> resendNotification(Long eKycUserId, long merchantId, String businessName);

	Map<String, Object> updateServicesJson(EKycUpdateRequest ekycUpdateRequest, long merchantId);

	Map<String, Object> ekycUserFilterDetails(@Valid EkycUserFilterRequest ekycUserFilterRequest, long merchantId);

	Map<String, Object> findByUserUniqueId(String userUniqueId);

	Map<String, Object> workflowExpiredScheduler();

	Map<String, Object> AllApprovedByEkycUserId(Long ekycUserId);

	Map<String, Object> AllRejectedByEkycUserId(Long valueOf);

	List<BulkEkycUserPayload> saveBulkEkycUsers(MultipartFile file, long merchantId, String clientId,String workflowUniqueId);

	Map<String, Object> eKycWorkflowTempList(@Valid EKycWorkFlowTempRequest eKycWorkFlowTempRequest, long merchantId);

}
