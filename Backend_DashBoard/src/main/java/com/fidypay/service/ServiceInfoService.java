package com.fidypay.service;

import java.util.List;
import java.util.Map;

import com.fidypay.request.PassbookRequest;
import com.fidypay.request.TransactionsReportRequest;
import com.fidypay.response.PassbookPayload;
import com.fidypay.response.TransactionsReportPayLoad;

public interface ServiceInfoService {

	Map<String, Object> getServiceByMechantId(long merchantId);

	String getTotalTrxn(long merchantId);

	List<?> getServiceTotalTransactionReport(TransactionsReportRequest transactionsReportRequest, String clientId);

	Map<String, Object> getTransactionsList(TransactionsReportRequest transactionsReportRequest, String clientId);

	Map<String, Object> getPassbook(PassbookRequest passbookRequest, String clientId);

	List<PassbookPayload> getPassbookExcel(PassbookRequest passbookRequest, String clientId);

	List<?> getServiceTotalTransactionReportNew(TransactionsReportRequest transactionsReportRequest, String clientId);

	Map<String, Object> getSubMerchantVpaList(String clientId);

	List<TransactionsReportPayLoad> getTransactionsListReportNew(TransactionsReportRequest transactionsReportRequest,
			String clientId);

	List<TransactionsReportPayLoad> getTransactionsListReport(TransactionsReportRequest transactionsReportRequest,
			String clientId);

	List<TransactionsReportPayLoad> getTransactionsListReportCoop(TransactionsReportRequest transactionsReportRequest,
			String clientId);

	Map<String, Object> getMerchantWalletBalance(long merchantId);

	String getServiceByMechantIdOld(long parseLong);

	Map<String, Object> getMerchantServiceDetails(String serviceName);
}
