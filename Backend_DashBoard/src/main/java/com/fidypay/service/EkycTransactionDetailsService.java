package com.fidypay.service;

import java.util.List;
import java.util.Map;

import com.fidypay.request.EKYCTransactionRequest;
import com.fidypay.response.EKYCTransactionResponse;
import com.fidypay.response.ServiceWiseTransactionResponse;

public interface EkycTransactionDetailsService {

	Map<String, Object> ekycTransactionList(EKYCTransactionRequest ekycTransactionRequest, long merchantId);

	List<EKYCTransactionResponse> getEkycTransactionsStatementReportExcel(
			EKYCTransactionRequest transactionsReportRequest, String clientId);

	Map<String, Object> eKycTotalTransactionAndTotalAmount(long parseLong);

	Map<String, Object> eKycServicesList(long parseLong);

	Map<String, Object> ekycServiceTransactionList(EKYCTransactionRequest ekycTransactionRequest, long merchantId);

	List<ServiceWiseTransactionResponse> eKycSDateAndMerchantId(long merchantId, String from, String to);

	List<ServiceWiseTransactionResponse> eKycSDateAndMerchantIdAndMServiceId(long merchantId, String from, String to,
			long merchantServiceId);

	List<ServiceWiseTransactionResponse> eKycSDateAndMerchantIdAndStatusId(long merchantId, String from, String to,
			long statusId);

	List<ServiceWiseTransactionResponse> eKycSDateAndMerchantIdAndMServiceIdAndStatusId(long merchantId, String from, String to,
			long merchantServiceId,long statusId);
	
}
