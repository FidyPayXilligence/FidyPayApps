package com.fidypay.service;

import com.fidypay.entity.MerchantService;

public interface MerchantsServiceService {

	MerchantService assignMerchantService(long partnerServiceId,double amc, double otc, long merchantId, long serrviceProviderId,
			long serviceId, double subscriptionAmount, String subscriptionCycle, String serviceType) throws Exception;

}
