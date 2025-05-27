package com.fidypay.service;

import java.text.ParseException;
import java.util.Map;

import com.fidypay.request.MerchantUserPermissionPayload;

public interface MerchantUserPermissionService {

	Map<String, Object> saveMerchantUserPermission(long merchantId,
			MerchantUserPermissionPayload merchantUserPermissionPayload) throws ParseException;

	Map<String, Object> findAllData(long merchantId, Integer pageNo, Integer pageSize);

	Map<String, Object> findByMerchantUserId(long merchantUserId, long merchantId);

	Map<String, Object> findByProductName(String productName) throws ParseException;

}
