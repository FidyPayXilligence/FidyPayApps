package com.fidypay.service;

import java.util.List;
import java.util.Map;

import com.fidypay.request.BBPSCommissionRequest;
import com.fidypay.request.BBPSTransactionHistoryRequest;
import com.fidypay.request.BBPSTransactionRequest;
import com.fidypay.request.Pagination;
import com.fidypay.response.BBPSCommissionResponse;
import com.fidypay.response.BBPSTransactionsReportPayLoad;
import com.fidypay.response.ServiceWiseTransactionResponse;

public interface BBPSTransactionDetailsService {

	Map<String, Object> bbpsTransactionList(BBPSTransactionRequest bbpsTransactionRequest, long parseLong);

	Map<String, Object> bbpsTotalTransactionAndTotalAmount(long parseLong);

	List<BBPSTransactionsReportPayLoad> getBBPSTransactionsStatementReportExcel(
			BBPSTransactionRequest transactionsReportRequest, String clientId);

	Map<String, Object> bbpsServicesList(long parseLong);

	Map<String, Object> bbpsServiceTransactionList(BBPSTransactionRequest bbpsTransactionRequest, long merchantId);

	List<ServiceWiseTransactionResponse> bbpsSDateAndMerchantId(long merchantId, String from, String to);

	List<ServiceWiseTransactionResponse> bbpsSDateAndMerchantIdAndMServiceId(long merchantId, String from, String to,
			long merchantServiceId);

	List<ServiceWiseTransactionResponse> bbpsSDateAndMerchantIdAndStatusId(long merchantId, String from, String to,
			long statusId);

	List<ServiceWiseTransactionResponse> bbpsSDateAndMerchantIdAndMServiceIdAndStatusId(long merchantId, String from,
			String to, long merchantServiceId, long statusId);

	Map<String, Object> bbpsCommissionslist(BBPSCommissionRequest bbpsCommissionRequest, long merchantId);

	List<BBPSCommissionResponse> bbpsCommissionsistReportExcel(String startDate, String endDate, long merchantId);

	Map<String, Object> bbpsTransactionHistory(BBPSTransactionHistoryRequest bbpsTransactionHistoryRequest,
			long merchantId);

	
	Map<String, Object> checkBBPSTransactionDetails(String key,String value, long merchantId);
	
	Map<String, Object> findByMobileNo(String mobile, long merchantId);
	
	Map<String, Object> findByMerchantTrxnRefIdo(String merchantTrxnRefId, long merchantId);

	Map<String, Object> updateTrxnIdentifierOnBBPSTrxnDeyails(String fromDate, String toDate);

	Map<String, Object> fetchAmountDetails(String merchantTrxnRefId);

}
