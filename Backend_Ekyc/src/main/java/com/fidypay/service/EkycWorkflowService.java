package com.fidypay.service;

import java.util.Map;

import javax.validation.Valid;

import com.fidypay.request.EkycUpdateWorkflowRequest;
import com.fidypay.request.EkycWorkflowRequest;
import com.fidypay.request.EkycworkflowFilterRequest;

public interface EkycWorkflowService {

	Map<String, Object> saveWorkflowDetails(EkycWorkflowRequest request, long merchantId, String imageUrl);

	Map<String, Object> getAllWorkflowDetails(Integer pageNo, Integer pageSize, long merchantId) throws Exception;

	Map<String, Object> getById(long ekycWorkflowId, long merchantId) throws Exception;

	Map<String, Object> deleteById(long ekycWorkflowId, long merchantId);

	Map<String, Object> updateById(EkycUpdateWorkflowRequest ekycUpdateWorkflowRequest, long merchantId);

	Map<String, Object> findAllWorkflowName(long merchantId) throws Exception;

	Map<String, Object> getKycTypeList(Long merchantId);

	Map<String, Object> getByWorkflowName(String ekycWorkflowName, long merchantId)throws Exception;

	Map<String, Object> searchEkycworkflowByFilter(@Valid EkycworkflowFilterRequest ekycworkflowFilterRequest,
			long merchantId);

}
