package com.fidypay.service;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import com.fidypay.request.MerchantSettlementRequest;
import com.fidypay.request.SettlementRequest;
import com.fidypay.response.SettlemenetReportResponse;

public interface MerchantSettlementsService {

	Map<String, Object> getSettlmentList(SettlementRequest settlementRequest, String clientId);

	Map<String, Object> getMerchantSettlmentList(MerchantSettlementRequest merchantSettlementRequest, String clientId);

//	List<SettlementReportPayload> getMerchantSettlmentExcelReport(MerchantSettlementRequest merchantSettlementRequest,
//			String clientId);

	List<SettlemenetReportResponse> getMerchantSettlmentExcelReport(MerchantSettlementRequest merchantSettlementRequest,
			String clientId);

	Map<String, Object> getSettlementListCOOP(String startDate, String endDate, String vpa)throws ParseException ;

}

