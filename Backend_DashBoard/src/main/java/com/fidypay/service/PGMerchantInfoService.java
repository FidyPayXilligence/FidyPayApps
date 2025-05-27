package com.fidypay.service;

import java.util.Map;

import com.fidypay.request.PGMerchantInfoRequest;

public interface PGMerchantInfoService {

	Map<String, Object> savePgMerchantInfo(PGMerchantInfoRequest pgMerchantInfoRequest, long merchantId)
			throws Exception;

	Map<String, Object> findPgMerchantInfoByMerchantId(long merchantId);

	Map<String, Object> accountInactive(long merchantId, String pgMerchantKey);

}
