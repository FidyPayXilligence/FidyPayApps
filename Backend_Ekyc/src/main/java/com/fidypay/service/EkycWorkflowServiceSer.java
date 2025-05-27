package com.fidypay.service;

import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import com.fidypay.request.EkycWorkflowServiceRequest;

public interface EkycWorkflowServiceSer {

	Map<String, Object> findByEkycUserId(long ekycUserId, long merchantId);

	Map<String, Object> documentUpload(String workflowToken, String userWorkflowToken, String fileName,
			@NotNull MultipartFile file, String optional, String optional2, MultipartFile file2);

	Map<String, Object> updateById(long ekycWorkflowServiceId, long merchantId, String isVerified,String rejectReason);

	Map<String, Object> getDocumentData(long ekycWorkflowServiceId, long merchantId);

	Map<String, Object> GSTINSearch(String gSTIN);

	Map<String, Object> saveEkycWorkflowUserService(@Valid EkycWorkflowServiceRequest ekycWorkflowServiceRequest,
			long merchantId);

}
