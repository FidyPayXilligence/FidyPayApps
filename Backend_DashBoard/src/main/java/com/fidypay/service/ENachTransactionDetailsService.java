package com.fidypay.service;

import java.util.List;
import java.util.Map;

import com.fidypay.request.ENachTxnInfoRequest;
import com.fidypay.request.PayoutTransactionRequest;
import com.fidypay.response.ENachTransactionResponse;
import com.fidypay.response.ServiceWiseTransactionResponse;

public interface ENachTransactionDetailsService {

	Map<String, Object> eNachServicesList(long parseLong);

	Map<String, Object> eNachTotalTransactionAndTotalAmount(long merchantId);

	Map<String, Object> eNachTransactionList(PayoutTransactionRequest payoutTransactionRequest, long merchantId);

	List<ENachTransactionResponse> getENachTransactionsStatementReportExcel(
			PayoutTransactionRequest transactionsReportRequest, String clientId);

	Map<String, Object> eNachServiceTransactionList(PayoutTransactionRequest payoutTransactionRequest, long parseLong);

	List<ServiceWiseTransactionResponse> eNachSDateAndMerchantId(long merchantId, String from, String to);

	List<ServiceWiseTransactionResponse> eNachSDateAndMerchantIdAndMerchantServiceId(long merchantId, String startDate,
			String endDate, long merchantServiceId);

	List<ServiceWiseTransactionResponse> eNachSDateAndMerchantIdAndStatusId(long merchantId, String startDate,
			String endDate, long statusId);

	List<ServiceWiseTransactionResponse> eNachSDateAndMerchantIdAndMServiceIdAndStatusID(long merchantId,
			String startDate, String endDate, long merchantServiceId, long statusId);

	Map<String, Object> findByENachTransactionId(long eNachTransactionId, long merchantId);

	Map<String, Object> findENachTransactionByFilter(ENachTxnInfoRequest eNachTxnInfoRequest);

}
