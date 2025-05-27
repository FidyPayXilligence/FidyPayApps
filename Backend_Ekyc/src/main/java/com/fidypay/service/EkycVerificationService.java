package com.fidypay.service;

import java.io.IOException;
import java.util.Map;

import com.fidypay.entity.EkycVerification;
import com.fidypay.request.EkycMerchantRequest;

public interface EkycVerificationService {

	Map<String, Object> saveEkycVerification(String phone, String email);

	boolean existsByMobile(String mobile);

	boolean existsByEmail(String email);

	Map<String, Object> checkMobileNo(String mobile)throws IOException;

	Map<String, Object> checkEmail(String email)  throws IOException;

	EkycVerification findByEKycId(Long ekyc_id);

	EkycVerification findByEmailAndPhone(String email, String phone);

	Map<String, Object> updateEkyc(Long ekycId, EkycMerchantRequest ekycMerchantRequest);

}
