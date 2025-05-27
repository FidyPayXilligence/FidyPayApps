package com.fidypay.service;

import java.util.Map;

import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

public interface FaceMatchingService {

	public Map<String, Object> faceMatcher(@NotNull MultipartFile file1, @NotNull MultipartFile file2, long merchantId,
			Double merchantFloatAmount, String businessName, String email);

}
