package com.fidypay.controller;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fidypay.encryption.Encryption;
import com.fidypay.entity.Merchants;
import com.fidypay.repo.MerchantsRepository;
import com.fidypay.service.CoreTransactionsService;
import com.fidypay.utils.constants.ResponseMessage;

import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/dashboard")
public class DashBoardController {

	private static final Logger LOGGER = LoggerFactory.getLogger(DashBoardController.class);

	@Autowired
	private CoreTransactionsService coreTransactionsService;

	@Autowired
	private MerchantsRepository merchantsRepository;

	@ApiOperation(value = "Get Months Data By Year")
	@GetMapping(value = "/getMonthsDataByYear")
	public Map<String, Object> getMonthsDataByYear(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, @RequestParam("year") String year) throws Exception {
		Map<String, Object> map = new HashMap<>();
         try {

			LOGGER.info("clientId:" + clientId + ", clientSecret: " + clientSecret);
			Merchants merchants = merchantsRepository.findByEmail(clientSecret);
			if (merchants.getIsMerchantActive() == '1' && clientSecret.equals(merchants.getMerchantEmail())
					&& Long.parseLong(Encryption.decString(clientId)) == merchants.getMerchantId()) {
				
				List<?> list = coreTransactionsService
						.getAllYearWiseData(Long.parseLong(Encryption.decString(clientId)), year);
				if (!list.isEmpty()) {
				Iterator<?> it = list.iterator();
				while (it.hasNext()) {
					Object[] obj = (Object[]) it.next();
					Integer month = (Integer) obj[0];
					String sMonth = String.valueOf(month);
					double amount = (double) obj[1];
					map.put(sMonth, amount);
				}}else {
					
				}
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);

			}

		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}
		return map;
	}

	@ApiOperation(value = "Get Week Data By Year And Month")
	@GetMapping(value = "/getWeekDataByYearAndMonth")
	public Map<String, Object> getWeekDataByYearAndMonth(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, @RequestParam("year") String year,
			@RequestParam("month") String month) {
		Map<String, Object> map = new HashMap<>();

		try {

			LOGGER.info("clientId:" + clientId + ", clientSecret: " + clientSecret);
			Merchants merchants = merchantsRepository.findByEmail(clientSecret);
			if (merchants.getIsMerchantActive() == '1' && clientSecret.equals(merchants.getMerchantEmail())
					&& Long.parseLong(Encryption.decString(clientId)) == merchants.getMerchantId()) {
				List<?> list = coreTransactionsService
						.getWeekDataByYearAndMonth(Long.parseLong(Encryption.decString(clientId)), year, month);
				if (!list.isEmpty()) {
				Iterator<?> it = list.iterator();
				while (it.hasNext()) {
					Object[] obj = (Object[]) it.next();
					Integer week = (Integer) obj[0];
					String sWeek = String.valueOf(week);
					double amount = (double) obj[1];

					map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
					map.put(sWeek, amount);
				}
				}else {
					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
					map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_AVAILABLE);										
				}
			}else {
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);				
			}

		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}
		return map;
	}

	@ApiOperation(value = "Get Day Data By Year And Month")
	@GetMapping(value = "/getDayDataByYearAndMonth")
	public Map<String, Object> getDayDataByYearAndMonth(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, @RequestParam("year") String year,
			@RequestParam("month") String month) {
		Map<String, Object> map = new HashMap<>();

		try {

			LOGGER.info("clientId:" + clientId + ", clientSecret: " + clientSecret);
			Merchants merchants = merchantsRepository.findByEmail(clientSecret);
			if (merchants.getIsMerchantActive() == '1' && clientSecret.equals(merchants.getMerchantEmail())
					&& Long.parseLong(Encryption.decString(clientId)) == merchants.getMerchantId()) {
				List<?> list = coreTransactionsService
						.getDayDataByYearAndMonth(Long.parseLong(Encryption.decString(clientId)), year, month);

				if (!list.isEmpty()) {
				Iterator<?> it = list.iterator();
				while (it.hasNext()) {
					Object[] obj = (Object[]) it.next();
					Integer day = (Integer) obj[0];
					String sDay = String.valueOf(day);
					double amount = (double) obj[1];

					map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
					map.put(sDay, amount);
				}}else {
					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
					map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_AVAILABLE);					
				}
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);

			}

		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}
		return map;
	}

	@ApiOperation(value = "show Transactions By Pi Chart")
	@GetMapping(value = "/showTransactionsByPiChart")
	public Map<String, Object> showTransactionsByPiChart(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, @RequestParam("year") String year,
			@RequestParam("month") String month) {
		Map<String, Object> map = new HashMap<>();
		try {

			LOGGER.info("clientId:" + clientId + ", clientSecret: " + clientSecret);
			Merchants merchants = merchantsRepository.findByEmail(clientSecret);
			if (merchants.getIsMerchantActive() == '1' && clientSecret.equals(merchants.getMerchantEmail())
					&& Long.parseLong(Encryption.decString(clientId)) == merchants.getMerchantId()) {
				List<?> list = coreTransactionsService
						.showTransactionsByPiChart(Long.parseLong(Encryption.decString(clientId)), year, month);

				if (!list.isEmpty()) {
					Iterator<?> it = list.iterator();
					while (it.hasNext()) {
						Object[] obj = (Object[]) it.next();
						String serviceName = Encryption.decString((String) obj[2]);
						BigInteger transactions = (BigInteger) obj[3];
						map.put(serviceName, transactions);
						map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
						map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
					}

				} else {
					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
					map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_AVAILABLE);
				}
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			}

		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}
		return map;
	}

	@ApiOperation(value = "show Amount By Pi Chart")
	@GetMapping(value = "/showAmountByPiChart")
	public Map<String, Object> showAmountByPiChart(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, @RequestParam("year") String year,
			@RequestParam("month") String month) {
		Map<String, Object> map = new HashMap<>();
		try {

			LOGGER.info("clientId:" + clientId + ", clientSecret: " + clientSecret);
			Merchants merchants = merchantsRepository.findByEmail(clientSecret);
			if (merchants.getIsMerchantActive() == '1' && clientSecret.equals(merchants.getMerchantEmail())
					&& Long.parseLong(Encryption.decString(clientId)) == merchants.getMerchantId()) {
				List<?> list = coreTransactionsService
						.showTransactionAmountByPiChart(Long.parseLong(Encryption.decString(clientId)), year, month);

				if (!list.isEmpty()) {
					Iterator<?> it = list.iterator();
					while (it.hasNext()) {
						Object[] obj = (Object[]) it.next();
						double amount = (double) obj[0];
						String serviceName = Encryption.decString((String) obj[1]);
						map.put(serviceName, amount);
						map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
						map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
					}

				} else {
					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
					map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_AVAILABLE);
				}
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);

			}

		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}
		return map;
	}

}
