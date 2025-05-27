package com.fidypay.service;

import java.util.Map;

import javax.validation.Valid;

import com.fidypay.request.UserKycRequest;
import com.fidypay.request.UserKycUpdateRequest;

public interface UserKycDetailsService {

	Map<String, Object> addUserKycDetails(UserKycRequest userKycRequest);

	Map<String, Object> findUserKycDetailsByMobileNo(String mobileNo);

	Map<String, Object> findUserKycDetailsByUserUniqueId(String userUniqueId);

	Map<String, Object> updateUserKycDetails(@Valid UserKycUpdateRequest userKycUpdateRequest);

	Map<String, Object> checkUserKycStatus(String userMobile);

}
