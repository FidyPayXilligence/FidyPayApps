package com.fidypay.service;

import com.fidypay.request.WealthReportRequest;
import com.fidypay.response.FdWealthTxnResponse;

import java.util.List;
import java.util.Map;

public interface FdReportService {

	Map<String, Object> fetchWealthTxnReport(WealthReportRequest wealthReportRequest);

	// Excel Reports
	List<FdWealthTxnResponse> generateFdTxnReportExcel(WealthReportRequest wealthReportRequest, String clientId);

	Map<String, Object> findByTrxnId(String merchantTrxnRefId, long merchantId);
}
