package com.fidypay.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public interface ESignService {

	Map<String, Object> getAuthenticateForESign(String name, String email, MultipartFile file, long merchantId,
			Double merchantFloatAmount, String businessName, String merchantEmail) throws IOException;

	Map<String, Object> getEsignDocument(String documentId, long merchantId, Double merchantFloatAmount, String businessName, String email);
	
	Map<String, Object> deleteEsignDocument(String documentId, long merchantId, Double merchantFloatAmount, String businessName, String email);

	Map<String, Object> GSTSearchBasisPAN(String panNo, long merchantId, Double merchantFloatAmount, String businessName, String email);

}
