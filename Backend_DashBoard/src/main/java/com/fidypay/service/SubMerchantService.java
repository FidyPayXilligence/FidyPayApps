package com.fidypay.service;

import java.util.List;
import java.util.Map;

import com.fidypay.response.SubMerchantDeatilsResponse;
import com.fidypay.response.SubMerchantDetailsResponse;

public interface SubMerchantService {

	List<SubMerchantDetailsResponse> getSubMerchantDetails(long merchantId);

	List<SubMerchantDeatilsResponse> getSubMerchantDetailExcel(long merchantId);

	List<SubMerchantDeatilsResponse> getSubMerchantDetail();

	Map<String, Object> createSubMerchantById(String subMerchantInfoId);

}
