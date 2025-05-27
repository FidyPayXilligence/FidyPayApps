package com.fidypay.service;

import java.util.Map;

import com.fidypay.request.PWAAppliactionLoginRequest;
import com.fidypay.request.Pagination;
import com.fidypay.request.TransactionsReportRequest;

public interface PWAApplicationService {

	Map<String, Object> merchantLogin(PWAAppliactionLoginRequest loginDTO);

	Map<String, Object> getTransactionsList(TransactionsReportRequest transactionsReportRequest, String clientId)
			throws Exception;

	Map<String, Object> getAllTransactionsList(Pagination pagination, String clientId);
	
	 Map<String, Object> getSettlementList(TransactionsReportRequest transactionsReportRequest) throws Exception; 


}
