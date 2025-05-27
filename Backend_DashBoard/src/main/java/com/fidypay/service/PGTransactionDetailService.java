package com.fidypay.service;

import java.util.List;
import java.util.Map;

import com.fidypay.request.PayoutTransactionRequest;
import com.fidypay.response.PgTransactionResponse;
import com.fidypay.response.ServiceWiseTransactionResponse;

public interface PGTransactionDetailService {

	Map<String, Object> pgTransactionList(PayoutTransactionRequest payoutTransactionRequest, long merchantId);

	Map<String, Object> pgTotalTransactionAndTotalAmount(long parseLong);

	List<PgTransactionResponse> getPgTransactionsStatementReportExcel(
			PayoutTransactionRequest transactionsReportRequest, String clientId);

	Map<String, Object> pgServicesList(long parseLong);

	Map<String, Object> pgServiceTransactionList(PayoutTransactionRequest payoutTransactionRequest, long merchantId);

	List<ServiceWiseTransactionResponse> pgSDateAndMerchantId(long merchantId, String from, String to);

	List<ServiceWiseTransactionResponse> pgSDateAndMerchantIdAndMServiceId(long merchantId, String from, String to,
			long merchantServiceId);

	List<ServiceWiseTransactionResponse> pgSDateAndMerchantIdAndStatusId(long merchantId, String from, String to,
			long statusId);

	List<ServiceWiseTransactionResponse> pgSDateAndMerchantIdAndMServiceIdAndStatusId(long merchantId, String from,
			String to, long merchantServiceId, long statusId);
}
