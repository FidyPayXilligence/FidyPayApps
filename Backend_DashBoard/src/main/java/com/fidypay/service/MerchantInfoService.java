package com.fidypay.service;

import java.text.ParseException;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import com.fidypay.request.MerchantInfoRequest;
import com.fidypay.request.MerchantInfoUpdateRequest;

public interface MerchantInfoService {

	Map<String, Object> saveMerchantInfo(MerchantInfoRequest merchantinforequest) throws ParseException;

	Map<String, Object> findByMerchantId(long merchantId);

	Map<String, Object> addByMerchantId(long merchantId) throws ParseException;

	Map<String, Object> updateMerchantInfoByMerchantId(long merchantId, MerchantInfoUpdateRequest merchantinforequest);

	Map<String, Object> logoUpload(long merchantId, MultipartFile file, String imageName);

	Map<String, Object> removeImage(long merchantId, String imageName);

	Map<String, Object> findUPIBankId(long merchantId);

	Map<String, Object> uploadBanner(long merchantId, @NotNull MultipartFile file);

}
