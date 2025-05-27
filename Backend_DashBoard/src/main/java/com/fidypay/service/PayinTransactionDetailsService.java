package com.fidypay.service;

import java.util.List;
import java.util.Map;

import com.fidypay.request.PayinTransactionRequest;
import com.fidypay.response.PayinTransactionsReportPayLoad;
import com.fidypay.response.ServiceWiseTransactionResponse;

public interface PayinTransactionDetailsService {

	Map<String, Object> payinTransactionList(PayinTransactionRequest payinTransactionRequest, Long merchantId);

	Map<String, Object> payinTotalTransactionAndTotalAmount(long parseLong);

	List<PayinTransactionsReportPayLoad> getPayinTransactionsStatementReportExcel(
			PayinTransactionRequest transactionsReportRequest, String clientId);

	Map<String, Object> getSubMerchantVpaList(String clientId);

	Map<String, Object> payinServicesList(long parseLong);

	Map<String, Object> payinServiceTransactionList(PayinTransactionRequest payinTransactionRequest, Long merchantId);

	List<ServiceWiseTransactionResponse> payinSDateAndMerchantId(long merchantId, String from, String to);

	List<ServiceWiseTransactionResponse> payinSDateAndMerchantIdAndMServiceId(long merchantId, String from, String to,
			long merchantServiceId);

	List<ServiceWiseTransactionResponse> payinSDateAndMerchantIdAndStatusId(long merchantId, String from, String to,
			long statusId);

	List<ServiceWiseTransactionResponse> payinSDateAndMerchantIdAndMServiceIdAndStatusId(long merchantId, String from,
			String to, long merchantServiceId, long statusId);

	List<ServiceWiseTransactionResponse> payinSDateAndMerchantIdAndVpa(long merchantId, String from, String to,
			String vpa);

	List<ServiceWiseTransactionResponse> payinSDateAndMerchantIdAndMServiceIdAndVpa(long merchantId, String from,
			String to, long merchantServiceId, String vpa);

	List<ServiceWiseTransactionResponse> payinSDateAndMerchantIdAndStatusIdAndVpa(long merchantId, String from,
			String to, long statusId, String vpa);

	List<ServiceWiseTransactionResponse> payinSDateAndMerchantIdAndStatusIdAndMServiceIdAndVpa(long merchantId,
			String from, String to, long statusId, long merchantServiceId, String vpa);
}
