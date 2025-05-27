package com.fidypay.service;

import java.util.List;

public interface CoreTransactionsService {

	List<?> getAllYearWiseData(Long merchantId,String year);

	List<?> getWeekDataByYearAndMonth(Long merchantId, String year,String month);

	List<?> getDayDataByYearAndMonth(Long merchantId, String year, String month);

	List<?> showTransactionsByPiChart(Long merchantId, String year,String month);
	
	List<?> showTransactionAmountByPiChart(Long merchantId, String year,String month);

}
