package com.fidypay.service;

import java.text.ParseException;
import java.util.*;

import com.fidypay.request.PassbookRequest;
import com.fidypay.response.PassbookPayload;

public interface PassbookService {

	Map<String, Object> passBookTransactionList(PassbookRequest passbookRequest, long merchantId)throws Exception,ParseException;

	List<PassbookPayload> passBookTransactionListExcel(long merchantId, String startDate, String endDate);
}
