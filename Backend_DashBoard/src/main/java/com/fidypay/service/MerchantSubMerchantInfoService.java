package com.fidypay.service;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.fidypay.dto.SubMerchantDTO;
import com.fidypay.dto.SubMerchantDTOCoop;
import com.fidypay.request.SubMerchantListRequest;
import com.fidypay.request.SubmerchantDocsRequest;
import com.fidypay.request.SubmerchatRequest;

public interface MerchantSubMerchantInfoService {

	Map<String, Object> subMerchantRequest(SubMerchantDTO subMerchantDTO, Long merchantId, String partnerKey);

	Map<String, Object> subMerchantList(Long merchantId, SubmerchatRequest submerchatRequest);

	Map<String, Object> getMccDetailsList();

	Map<String, Object> getMccDetailsList2();

	Map<String, Object> subMerchantRequestCOOP(SubMerchantDTOCoop subMerchantDTOCoop, Long merchantId,
			String partnerKey);

	Map<String, Object> getBussinessTypeList();

	Map<String, Object> subMerchantAllList(SubmerchatRequest submerchatRequest);

	Map<String, Object> subMerchantDetails(long merchantId, long subMerchantInfoId);

	Map<String, Object> findByMobileNumber(String mobileNumber);

	Map<String, Object> updateBySubMerchantId(SubmerchantDocsRequest submerchantDocsRequest, String submerchantInfoId);

	Map<String, Object> subMerchantListByFilter(long merchantId, SubMerchantListRequest subMerchantList);

	Map<String, Object> checkLimit(String vpa) throws NoSuchPaddingException, NoSuchAlgorithmException,
			BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException, Exception;

	Map<String, Object> countOfMerchant(long merchantId);

	Map<String, Object> findByMobile(String mobile);

}
