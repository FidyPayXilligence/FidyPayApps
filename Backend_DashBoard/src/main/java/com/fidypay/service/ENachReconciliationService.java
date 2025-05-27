package com.fidypay.service;

import java.util.List;
import java.util.Map;

import com.fidypay.request.ENachReconciliationRequest;
import com.fidypay.request.ENachReconciliationRequestlist;
import com.fidypay.response.ENachReconciliationReportPayLoad;
import com.fidypay.response.ENachSattelmentReportPayLoad;

public interface ENachReconciliationService {

	List<ENachReconciliationReportPayLoad> getENachReconciliationReportExcel(
			ENachReconciliationRequest enachreconciliationrequest, String cId);

	List<ENachSattelmentReportPayLoad> getENachSattelmentReportExcel(
			ENachReconciliationRequest enachreconciliationrequest, String cId);

	Map<String, Object> findENachReconciliationDetails(ENachReconciliationRequestlist eNachReconciliationRequestlist,
			long merchantId);

	Map<String, Object> findENachSettlmentDetails(ENachReconciliationRequestlist eNachReconciliationRequestlist,
			long merchantId);

}
