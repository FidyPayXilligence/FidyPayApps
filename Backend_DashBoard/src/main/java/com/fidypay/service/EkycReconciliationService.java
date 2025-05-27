package com.fidypay.service;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import com.fidypay.request.ENachReconciliationRequestlist;
import com.fidypay.request.EkycReconciliationRequest;
import com.fidypay.response.EKYCReconciliationReportPayLoad;

public interface EkycReconciliationService {

	List<EKYCReconciliationReportPayLoad> getEKYCReconciliationReportExcel(
			EkycReconciliationRequest ekycreconciliationrequest, String cId);

	Map<String, Object> findEkycReconciliationDetails(@Valid ENachReconciliationRequestlist eNachReconciliationrequest,
			long merchantId)  throws Exception;

}
