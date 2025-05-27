package com.fidypay.service;

import java.util.List;
import java.util.Map;

import com.fidypay.request.PayoutTransactionRequest;
import com.fidypay.response.PayoutTransactionsReportPayLoad;
import com.fidypay.response.ServiceWiseTransactionResponse;

public interface PayoutTransactionDetailsService {

	Map<String, Object> payoutTransactionList(PayoutTransactionRequest payoutTransactionRequest, long merchantId);

	Map<String, Object> payoutTotalTransactionAndTotalAmount(long parseLong);

	List<PayoutTransactionsReportPayLoad> getPayoutTransactionsStatementReportExcel(
			PayoutTransactionRequest transactionsReportRequest, String clientId);

	Map<String, Object> payoutServicesList(long parseLong);

	Map<String, Object> payoutServiceTransactionList(PayoutTransactionRequest payoutTransactionRequest, long parseLong);

	List<ServiceWiseTransactionResponse> payoutSDateAndMerchantId(long merchantId, String from, String to);

	List<ServiceWiseTransactionResponse> payoutSDateAndMerchantIdAndMerchantServiceId(long merchantId, String startDate,
			String endDate, long merchantServiceId);

	List<ServiceWiseTransactionResponse> payoutSDateAndMerchantIdAndStatusId(long merchantId, String startDate,
			String endDate, long statusId);

	List<ServiceWiseTransactionResponse> payoutSDateAndMerchantIdAndMServiceIdAndStatusID(long merchantId,
			String startDate, String endDate, long merchantServiceId, long statusId);
}
