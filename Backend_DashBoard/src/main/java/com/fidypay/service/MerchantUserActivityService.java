package com.fidypay.service;

import com.fidypay.request.MerchantUserActivityPayload;
import com.fidypay.request.MerchantUserActivityRequest;

import java.text.ParseException;
import java.util.Map;

/**
 * @author prave
 * @Date 09-10-2023
 */
public interface MerchantUserActivityService {

	Map<String, Object> saveMerchantUserActivity(MerchantUserActivityRequest merchantUserActivityRequest,
			long merchantId) throws ParseException;

	Map<String, Object> findActivityByMerchantId(MerchantUserActivityPayload merchantUserActivityPayload,
			long merchantId);

	Map<String, Object> findActivityByUserId(MerchantUserActivityPayload merchantUserActivityPayload,
			long merchantUserId);
}
