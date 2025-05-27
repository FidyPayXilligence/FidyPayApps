package com.fidypay.service.impl;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fidypay.encryption.Encryption;
import com.fidypay.entity.MerchantService;
import com.fidypay.entity.PGTransactionDetail;
import com.fidypay.entity.ServiceCategory;
import com.fidypay.entity.ServiceInfo;
import com.fidypay.repo.MerchantServiceRepository;
import com.fidypay.repo.PGTransactionDetailRepository;
import com.fidypay.repo.ServiceCategoryRepository;
import com.fidypay.repo.ServiceInfoRepository;
import com.fidypay.request.PayoutTransactionRequest;
import com.fidypay.response.MerchantServicesPayload;
import com.fidypay.response.PgTransactionResponse;
import com.fidypay.response.ServiceWiseTransactionResponse;
import com.fidypay.service.PGTransactionDetailService;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.DateAndTime;
import com.fidypay.utils.ex.DateUtil;

@Service
public class PGTransactionDetailServiceImpl implements PGTransactionDetailService {
	
	private static final Logger log = LoggerFactory.getLogger(PGTransactionDetailServiceImpl.class);

	@Autowired
	private MerchantServiceRepository merchantServiceRepository;

	@Autowired
	private ServiceInfoRepository serviceInfoRepository;

	@Autowired
	private ServiceCategoryRepository serviceCategoryRepository;

//	@Autowired
//	private PGTransactionDetailSearchRepository pgTransactionDetailSearchRepository;

	@Autowired
	private PGTransactionDetailRepository pgTransactionDetailRepository;


	@Override
	public Map<String, Object> pgTransactionList(PayoutTransactionRequest payoutTransactionRequest, long merchantId) {
		Map<String, Object> map = new HashMap<>();
		try {
			DecimalFormat amountFormate = new DecimalFormat("#,##,##,##,###.00");
			Pageable paging = PageRequest.of(payoutTransactionRequest.getPageNo(),
					payoutTransactionRequest.getPageSize(), Sort.by("TRANSACTION_DATE").descending());

			String startDate = payoutTransactionRequest.getStartDate();
			String endDate = payoutTransactionRequest.getEndDate();
			Long merchantServiceId = payoutTransactionRequest.getMerchantServiceId();
			Long status = payoutTransactionRequest.getStatusId();
			String startTime = payoutTransactionRequest.getStartTime();
			String endTime = payoutTransactionRequest.getEndTime();

			if (DateUtil.isValidDateFormat(startDate) == false || DateUtil.isValidDateFormat(endDate) == false) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.INVALID_DATE_FORMATE);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}

			if (DateUtil.isValidDateFormat(startDate, endDate)) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.INVALID_FROM_TO_DATE);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}

			List<PGTransactionDetail> list = new ArrayList<PGTransactionDetail>();
			List<PgTransactionResponse> listPgTransactionResponse = new ArrayList<PgTransactionResponse>();
			Page<PGTransactionDetail> listTransaction = null;

			if (startTime.equals("") || startTime.equals("0") || startTime.equals("null") || endTime.equals("null")
					|| endTime.equals("") || endTime.equals("0")) {
				startDate = startDate + " 00.00.00.0";
				endDate = endDate + " 23.59.59.9";
			} else {
				startDate = startDate + " " + startTime.replaceAll(":", ".") + ".00.0";
				endDate = endDate + " " + endTime.replaceAll(":", ".") + ".00.0";
			}

			if ((merchantServiceId == null || merchantServiceId == 0) && (status == null || status == 0)) {
				listTransaction = pgTransactionDetailRepository.findByStartDateAndEndDate(merchantId, startDate,
						endDate, paging);

			} else if ((merchantServiceId != null || merchantServiceId != 0) && (status == null || status == 0)) {
				listTransaction = pgTransactionDetailRepository.findByStartDateAndEndDateANDService(merchantId,
						startDate, endDate, merchantServiceId, paging);

			} else if ((merchantServiceId == null || merchantServiceId == 0) && (status != null || status != 0)) {
				listTransaction = pgTransactionDetailRepository.findByStartDateAndEndDateANDStatus(merchantId,
						startDate, endDate, status, paging);

			} else if ((merchantServiceId != null || merchantServiceId != 0) && (status != null || status != 0)) {
				listTransaction = pgTransactionDetailRepository.findByStartDateAndEndDateANDStatusANDService(
						merchantId, startDate, endDate, status, merchantServiceId, paging);

			}

			list = listTransaction.getContent();
			AtomicInteger atomicInteger = new AtomicInteger(1);

			if (list.size() != 0) {
				list.forEach(objects -> {
					String txnAmount = amountFormate.format(objects.getTransactionAmount());
					try {
						String date = DateAndTime.dateFormatReports(objects.getTransactionDate().toString());

						PgTransactionResponse pgTransactionResponse = new PgTransactionResponse();

						pgTransactionResponse.setsNo(atomicInteger.getAndIncrement());
						pgTransactionResponse.setTransactionDate(date);
						pgTransactionResponse.setMerchantTransactionRefId(
								Encryption.decString(objects.getMerchantTransactionRefId()));
						pgTransactionResponse.setTransactionAmount(txnAmount);
						pgTransactionResponse.setBankRefID(Encryption.decString(objects.getBankReferanceNumber()));
						pgTransactionResponse.setServiceName(objects.getServiceName());
						pgTransactionResponse.setTrxnRefId(Encryption.decString(objects.getTrxnRefId()));
						pgTransactionResponse.setPaymentMode(objects.getPaymentMode());
						pgTransactionResponse.setPaymentId(Encryption.decString(objects.getPaymentId()));

						if (objects.getTransactionStatus().equals("Success")) {
							pgTransactionResponse.setTransactionStatus("Success");
						}
						if (objects.getTransactionStatus().equals("Fail")
								|| objects.getTransactionStatus().equals("Failed")) {
							pgTransactionResponse.setTransactionStatus("Failed");
						}
						if (objects.getTransactionStatus().equals("Pending")) {
							pgTransactionResponse.setTransactionStatus("Pending");
						}
						if (objects.getTransactionStatus().equals("Refunded")
								|| objects.getTransactionStatus().equals("Refund")) {
							pgTransactionResponse.setTransactionStatus("Refunded");
						}

						listPgTransactionResponse.add(pgTransactionResponse);
					} catch (ParseException e) {

						e.printStackTrace();
					}
				});

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "PG Transaction List");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put("callBackList", listPgTransactionResponse);
				map.put("currentPage", listTransaction.getNumber());
				map.put("totalItems", listTransaction.getTotalElements());
				map.put("totalPages", listTransaction.getTotalPages());
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Transaction not found");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			}

		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage().toString());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;
	}

	@Override
	public Map<String, Object> pgTotalTransactionAndTotalAmount(long merchantId) {
		Map<String, Object> map = new HashMap<>();
		try {
			Date date = new Date();
			SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
			format1.setTimeZone(TimeZone.getTimeZone("IST"));

			String date1 = format1.format(date);

			String startDate = date1 + " 00.00.00.0";
			String endDate = date1 + " 23.59.59.9";

//			SearchRequest searchRequest = new SearchRequest("pg_transaction_details");
//			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//
//			searchSourceBuilder.query(QueryBuilders.boolQuery().must(QueryBuilders.termQuery("MERCHANT_ID", merchantId))
//					.filter(QueryBuilders.rangeQuery("TRANSACTION_DATE").gte(startDate).lte(endDate)));
//
//			searchRequest.source(searchSourceBuilder);
//
//			SearchHits hits = client.search(searchRequest, RequestOptions.DEFAULT).getHits();

//			int totalTransaction = hits.getHits().length;
//			double totalAmount = 0.0;
//
//			for (SearchHit hit : hits.getHits()) {
//				totalAmount += Double.parseDouble(hit.getSourceAsMap().get("transactionAmount").toString());
//			}

			DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");
			amount1.setMinimumIntegerDigits(1);
	//		String tamount = amount1.format(totalAmount);

			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, "Total transactions and total amount");
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
		//	map.put("totalTransactions", totalTransaction);
		///	map.put("totalAmount", tamount);

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage().toString());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;
	}

	@Override
	public List<PgTransactionResponse> getPgTransactionsStatementReportExcel(
			PayoutTransactionRequest transactionsReportRequest, String clientId) {
		Map<String, Object> map = new HashMap<>();
		List<PgTransactionResponse> activityList = new ArrayList<PgTransactionResponse>();
		try {
			DecimalFormat amountFormate = new DecimalFormat("#,##,##,##,###.00");
			String startHours = null;
			String endHours = null;
			Long statusId = transactionsReportRequest.getStatusId();
			Long merchantServiceId = transactionsReportRequest.getMerchantServiceId();

			if (transactionsReportRequest.getStartTime().equals("0") || transactionsReportRequest.getStartTime() == "0"
					|| transactionsReportRequest.getEndTime() == "0"
					|| transactionsReportRequest.getEndTime().equals("0")) {

				startHours = "00.00.00.0";
				endHours = "23.59.59.9";
			}

			Long merchantId = Long.parseLong(clientId);
			String startDate = transactionsReportRequest.getStartDate() + " " + startHours;
			String endDate = transactionsReportRequest.getEndDate() + " " + endHours;
			List<PGTransactionDetail> list = null;

			if ((merchantServiceId == null || merchantServiceId == 0) && (statusId == null || statusId == 0)) {
				list = pgTransactionDetailRepository.findByStartDateAndEndDate(merchantId, startDate, endDate);

			} else if ((merchantServiceId != null || merchantServiceId != 0) && (statusId == null || statusId == 0)) {
				list = pgTransactionDetailRepository.findByStartDateAndEndDateANDService(merchantId, startDate,
						endDate, merchantServiceId);

			} else if ((merchantServiceId == null || merchantServiceId == 0) && (statusId != null || statusId != 0)) {
				list = pgTransactionDetailRepository.findByStartDateAndEndDateANDStatus(merchantId, startDate,
						endDate, statusId);

			} else if ((merchantServiceId != null || merchantServiceId != 0) && (statusId != null || statusId != 0)) {
				list = pgTransactionDetailRepository.findByStartDateAndEndDateANDStatusANDService(merchantId,
						startDate, endDate, statusId, merchantServiceId);

			}

			AtomicInteger atomicInteger = new AtomicInteger(1);

			if (list.size() != 0) {
				list.forEach(objects -> {

					try {
						String txnAmount = amountFormate.format(objects.getTransactionAmount());
						String date = DateAndTime.dateFormatReports(objects.getTransactionDate().toString());

						PgTransactionResponse pgTransactionResponse = new PgTransactionResponse();

						pgTransactionResponse.setsNo(atomicInteger.getAndIncrement());
						pgTransactionResponse.setTransactionDate(date);
						pgTransactionResponse.setMerchantTransactionRefId(
								Encryption.decString(objects.getMerchantTransactionRefId()));
						pgTransactionResponse.setTransactionAmount(txnAmount);

						pgTransactionResponse.setBankRefID(Encryption.decString(objects.getBankReferanceNumber()));
						pgTransactionResponse.setServiceName(objects.getServiceName());
						pgTransactionResponse.setTrxnRefId(Encryption.decString(objects.getTrxnRefId()));
						pgTransactionResponse.setPaymentMode(objects.getPaymentMode());
						pgTransactionResponse.setPaymentId(Encryption.decString(objects.getPaymentId()));
						pgTransactionResponse.setCharges(
								Double.parseDouble(amountFormate.format(objects.getMerchantServiceCharge())));
						pgTransactionResponse.setIsReconcile(String.valueOf(objects.getIsReconcile()));
						pgTransactionResponse.setIsSettled(String.valueOf(objects.getIsSettled()));

						if (objects.getTransactionStatus().equals("Success")) {
							pgTransactionResponse.setTransactionStatus("Success");
						}
						if (objects.getTransactionStatus().equals("Fail")
								|| objects.getTransactionStatus().equals("Failed")) {
							pgTransactionResponse.setTransactionStatus("Failed");
						}
						if (objects.getTransactionStatus().equals("Pending")) {
							pgTransactionResponse.setTransactionStatus("Pending");
						}
						if (objects.getTransactionStatus().equals("Reversed")) {
							pgTransactionResponse.setTransactionStatus("Reversed");
						}
						if (objects.getTransactionStatus().equals("Refunded")
								|| objects.getTransactionStatus().equals("Refund")) {
							pgTransactionResponse.setTransactionStatus("Refunded");
						}
						activityList.add(pgTransactionResponse);
					} catch (ParseException e) {

						e.printStackTrace();
					}

				});
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Transaction not found");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			}
		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage().toString());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return activityList;
	}

	@Override
	public Map<String, Object> pgServicesList(long merchantId) {
		Map<String, Object> map = new HashMap<>();
		List<MerchantServicesPayload> servicesList = new ArrayList<MerchantServicesPayload>();

		try {
			List<MerchantService> list = merchantServiceRepository.findPGServicesByMerchantId(merchantId);

			for (MerchantService merchantService : list) {
				MerchantServicesPayload merchantServicesPayload = new MerchantServicesPayload();

				merchantServicesPayload.setMerchantServiceId(merchantService.getMerchantServiceId());

				merchantServicesPayload.setServiceId(merchantService.getServiceId());

				ServiceInfo info = serviceInfoRepository.findById(merchantService.getServiceId()).get();

				merchantServicesPayload.setServiceName(Encryption.decString(info.getServiceName()).toUpperCase());

				ServiceCategory category = serviceCategoryRepository
						.findById(info.getServiceCategory().getServiceCategoryId()).get();

				merchantServicesPayload.setCategoryName(Encryption.decString(category.getServiceCategoryName()));

				servicesList.add(merchantServicesPayload);
			}
			Collections.sort(servicesList);
			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, "Merchant services list");
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			map.put("servicesList", servicesList);

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage().toString());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;
	}

	@Override
	public Map<String, Object> pgServiceTransactionList(PayoutTransactionRequest payoutTransactionRequest,
			long merchantId) {
		Map<String, Object> map = new HashMap<>();
		try {

			String startDate = payoutTransactionRequest.getStartDate();
			String endDate = payoutTransactionRequest.getEndDate();
			Long merchantServiceId = payoutTransactionRequest.getMerchantServiceId();
			Long status = payoutTransactionRequest.getStatusId();
			String startTime = payoutTransactionRequest.getStartTime();
			String endTime = payoutTransactionRequest.getEndTime();

			if (DateUtil.isValidDateFormat(startDate) == false || DateUtil.isValidDateFormat(endDate) == false) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.INVALID_DATE_FORMATE);
				return map;
			}

			if (DateUtil.isValidDateFormat(startDate, endDate)) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.INVALID_FROM_TO_DATE);
				return map;
			}

			if (startTime.equals("") || startTime.equals("0") || startTime.equals("null") || endTime.equals("null")
					|| endTime.equals("") || endTime.equals("0")) {
				startDate = startDate + " 00.00.00.0";
				endDate = endDate + " 23.59.59.9";
			} else {
				startDate = startDate + " " + startTime + ".00.0";
				endDate = endDate + " " + endTime + ".00.0";
			}

			List<ServiceWiseTransactionResponse> list = new ArrayList<>();

			if ((merchantServiceId == null || merchantServiceId == 0) && (status == null || status == 0)) {

				list = pgSDateAndMerchantId(merchantId, startDate, endDate);

			} else if ((merchantServiceId != null || merchantServiceId != 0) && (status == null || status == 0)) {

				list = pgSDateAndMerchantIdAndMServiceId(merchantId, startDate, endDate, merchantServiceId);

			} else if ((merchantServiceId == null || merchantServiceId == 0) && (status != null || status != 0)) {

				list = pgSDateAndMerchantIdAndStatusId(merchantId, startDate, endDate, status);

			} else if ((merchantServiceId != null || merchantServiceId != 0) && (status != null || status != 0)) {

				list = pgSDateAndMerchantIdAndMServiceIdAndStatusId(merchantId, startDate, endDate, merchantServiceId,
						status);

			}

			if (!list.isEmpty()) {
				map.put("list", list);
				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "PG Service  Transaction List");
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Transaction not found");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			}

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;
	}


	@Override
	public List<ServiceWiseTransactionResponse> pgSDateAndMerchantId(long merchantId, String from, String to) {
		List<ServiceWiseTransactionResponse> list = new ArrayList<>();

		List<?> list1 = pgTransactionDetailRepository.findByDateANDMerchantId(merchantId, from, to);

		DecimalFormat amountFormate = new DecimalFormat("#,##,##,##,###.00");

		Integer sNo = 0;
		for (Iterator<?> iterator1 = list1.iterator(); iterator1.hasNext();) {

			ServiceWiseTransactionResponse serviceWiseTransactionResponse = new ServiceWiseTransactionResponse();
			sNo++;
			Object[] object1 = (Object[]) iterator1.next();
			String serviceName = (String) object1[0];
			BigInteger serviceWiseTotalTransaction = (BigInteger) object1[1];
			Integer serviceWiseTotalTransactionLong = serviceWiseTotalTransaction.intValue();
			double serviceWiseAmount = (Double) object1[2];
			String stringServiceWiseAmount = amountFormate.format(serviceWiseAmount);

			List<?> list2 = pgTransactionDetailRepository.findByDateANDMerchantIdAndServiceName(merchantId, from, to,
					serviceName);

			for (Iterator<?> iterator2 = list2.iterator(); iterator2.hasNext();) {

				Object[] object2 = (Object[]) iterator2.next();
				String statusName = (String) object2[0];
				BigInteger statusWiseTotalTransaction = (BigInteger) object2[1];
				Integer statusWiseTotalTransactionInteger = statusWiseTotalTransaction.intValue();
				String stringTotalAmount = amountFormate.format((Double) object2[2]);
				if (statusName.equalsIgnoreCase("Success")) {

					serviceWiseTransactionResponse.setSuccessAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setSuccessTransaction(statusWiseTotalTransactionInteger);
				}
				if (statusName.equalsIgnoreCase("Pending")) {

					serviceWiseTransactionResponse.setPendingAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setPendingTransaction(statusWiseTotalTransactionInteger);
				}
				if (statusName.equalsIgnoreCase("Fail")
						||  statusName.equalsIgnoreCase("Failed")) {

					serviceWiseTransactionResponse.setFailedAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setFailedTransaction(statusWiseTotalTransactionInteger);
				}
				if (statusName.equalsIgnoreCase("Refunded")
						|| statusName.equalsIgnoreCase("Refund")) {

					serviceWiseTransactionResponse.setReversedAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setReversedTransaction(statusWiseTotalTransactionInteger);
				}
			}

			serviceWiseTransactionResponse.setsNo(sNo);
			serviceWiseTransactionResponse.setServicename(serviceName);
			serviceWiseTransactionResponse.setTotalamount(stringServiceWiseAmount);
			serviceWiseTransactionResponse.setTotalTransaction(serviceWiseTotalTransactionLong);

			list.add(serviceWiseTransactionResponse);
		}

		return list;
	}

	@Override
	public List<ServiceWiseTransactionResponse> pgSDateAndMerchantIdAndMServiceId(long merchantId, String from,
			String to, long merchantServiceId) {
		List<ServiceWiseTransactionResponse> list = new ArrayList<>();

		List<?> list1 = pgTransactionDetailRepository.findByDateANDMerchantIdAndMServiceId(merchantId, from, to,
				merchantServiceId);

		DecimalFormat amountFormate = new DecimalFormat("#,##,##,##,###.00");

		Integer sNo = 0;
		for (Iterator<?> iterator1 = list1.iterator(); iterator1.hasNext();) {

			ServiceWiseTransactionResponse serviceWiseTransactionResponse = new ServiceWiseTransactionResponse();
			sNo++;
			Object[] object1 = (Object[]) iterator1.next();
			String serviceName = (String) object1[0];
			BigInteger serviceWiseTotalTransaction = (BigInteger) object1[1];
			Integer serviceWiseTotalTransactionLong = serviceWiseTotalTransaction.intValue();
			double serviceWiseAmount = (Double) object1[2];
			String stringServiceWiseAmount = amountFormate.format(serviceWiseAmount);

			List<?> list2 = pgTransactionDetailRepository.findByDateANDMerchantIdAndServiceName(merchantId, from, to,
					serviceName);

			for (Iterator<?> iterator2 = list2.iterator(); iterator2.hasNext();) {

				Object[] object2 = (Object[]) iterator2.next();
				String statusName = (String) object2[0];
				BigInteger statusWiseTotalTransaction = (BigInteger) object2[1];
				Integer statusWiseTotalTransactionInteger = statusWiseTotalTransaction.intValue();
				String stringTotalAmount = amountFormate.format((Double) object2[2]);

				if (statusName.equalsIgnoreCase("Success")) {

					serviceWiseTransactionResponse.setSuccessAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setSuccessTransaction(statusWiseTotalTransactionInteger);
				}
				if (statusName.equalsIgnoreCase("Pending")) {

					serviceWiseTransactionResponse.setPendingAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setPendingTransaction(statusWiseTotalTransactionInteger);
				}
				if (statusName.equalsIgnoreCase("Fail")
						||  statusName.equalsIgnoreCase("Failed")) {

					serviceWiseTransactionResponse.setFailedAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setFailedTransaction(statusWiseTotalTransactionInteger);
				}
				if (statusName.equalsIgnoreCase("Refunded")
						|| statusName.equalsIgnoreCase("Refund")) {

					serviceWiseTransactionResponse.setReversedAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setReversedTransaction(statusWiseTotalTransactionInteger);
				}
			}

			serviceWiseTransactionResponse.setsNo(sNo);
			serviceWiseTransactionResponse.setServicename(serviceName);
			serviceWiseTransactionResponse.setTotalamount(stringServiceWiseAmount);
			serviceWiseTransactionResponse.setTotalTransaction(serviceWiseTotalTransactionLong);

			list.add(serviceWiseTransactionResponse);
		}

		return list;
	}

	@Override
	public List<ServiceWiseTransactionResponse> pgSDateAndMerchantIdAndStatusId(long merchantId, String from, String to,
			long statusId) {
		List<ServiceWiseTransactionResponse> list = new ArrayList<>();

		List<?> list1 = pgTransactionDetailRepository.findByDateANDMerchantIdAndStatusId(merchantId, from, to,
				statusId);
		DecimalFormat amountFormate = new DecimalFormat("#,##,##,##,###.00");
		Integer sNo = 0;
		for (Iterator<?> iterator1 = list1.iterator(); iterator1.hasNext();) {

			ServiceWiseTransactionResponse serviceWiseTransactionResponse = new ServiceWiseTransactionResponse();
			sNo++;
			Object[] object1 = (Object[]) iterator1.next();
			String serviceName = (String) object1[0];
			BigInteger serviceWiseTotalTransaction = (BigInteger) object1[1];
			Integer serviceWiseTotalTransactionLong = serviceWiseTotalTransaction.intValue();
			double serviceWiseAmount = (Double) object1[2];
			String stringServiceWiseAmount = amountFormate.format(serviceWiseAmount);

			List<?> list2 = pgTransactionDetailRepository.findByDateANDMerchantIdAndServiceNameAndStatusId(merchantId,
					from, to, serviceName, statusId);

			for (Iterator<?> iterator2 = list2.iterator(); iterator2.hasNext();) {

				Object[] object2 = (Object[]) iterator2.next();
				String statusName = (String) object2[0];
				BigInteger statusWiseTotalTransaction = (BigInteger) object2[1];
				Integer statusWiseTotalTransactionInteger = statusWiseTotalTransaction.intValue();
				String stringTotalAmount = amountFormate.format((Double) object2[2]);

				if (statusName.equalsIgnoreCase("Success")) {

					serviceWiseTransactionResponse.setSuccessAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setSuccessTransaction(statusWiseTotalTransactionInteger);
				}
				if (statusName.equalsIgnoreCase("Pending")) {

					serviceWiseTransactionResponse.setPendingAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setPendingTransaction(statusWiseTotalTransactionInteger);
				}
				if (statusName.equalsIgnoreCase("Fail")
						||  statusName.equalsIgnoreCase("Failed")) {

					serviceWiseTransactionResponse.setFailedAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setFailedTransaction(statusWiseTotalTransactionInteger);
				}
				if (statusName.equalsIgnoreCase("Refunded")
						|| statusName.equalsIgnoreCase("Refund")) {

					serviceWiseTransactionResponse.setReversedAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setReversedTransaction(statusWiseTotalTransactionInteger);
				}
			}

			serviceWiseTransactionResponse.setsNo(sNo);
			serviceWiseTransactionResponse.setServicename(serviceName);
			serviceWiseTransactionResponse.setTotalamount(stringServiceWiseAmount);
			serviceWiseTransactionResponse.setTotalTransaction(serviceWiseTotalTransactionLong);

			list.add(serviceWiseTransactionResponse);
		}

		return list;
	}

	@Override
	public List<ServiceWiseTransactionResponse> pgSDateAndMerchantIdAndMServiceIdAndStatusId(long merchantId,
			String from, String to, long merchantServiceId, long statusId) {
		List<ServiceWiseTransactionResponse> list = new ArrayList<>();

		List<?> list1 = pgTransactionDetailRepository.findByDateANDMerchantIdAndMServiceIdStatusId(merchantId, from, to,
				merchantServiceId, statusId);
		DecimalFormat amountFormate = new DecimalFormat("#,##,##,##,###.00");
		Integer sNo = 0;
		for (Iterator<?> iterator1 = list1.iterator(); iterator1.hasNext();) {

			ServiceWiseTransactionResponse serviceWiseTransactionResponse = new ServiceWiseTransactionResponse();
			sNo++;
			Object[] object1 = (Object[]) iterator1.next();
			String serviceName = (String) object1[0];
			BigInteger serviceWiseTotalTransaction = (BigInteger) object1[1];
			Integer serviceWiseTotalTransactionLong = serviceWiseTotalTransaction.intValue();
			double serviceWiseAmount = (Double) object1[2];
			String stringServiceWiseAmount = amountFormate.format(serviceWiseAmount);

			List<?> list2 = pgTransactionDetailRepository.findByDateANDMerchantIdAndServiceNameAndStatusId(merchantId,
					from, to, serviceName, statusId);

			for (Iterator<?> iterator2 = list2.iterator(); iterator2.hasNext();) {

				Object[] object2 = (Object[]) iterator2.next();
				String statusName = (String) object2[0];
				BigInteger statusWiseTotalTransaction = (BigInteger) object2[1];
				Integer statusWiseTotalTransactionInteger = statusWiseTotalTransaction.intValue();
				String stringTotalAmount = amountFormate.format((Double) object2[2]);

				
				
				if (statusName.equalsIgnoreCase("Success")) {

					serviceWiseTransactionResponse.setSuccessAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setSuccessTransaction(statusWiseTotalTransactionInteger);
				}
				if (statusName.equalsIgnoreCase("Pending")) {

					serviceWiseTransactionResponse.setPendingAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setPendingTransaction(statusWiseTotalTransactionInteger);
				}
				if (statusName.equalsIgnoreCase("Fail")
						||  statusName.equalsIgnoreCase("Failed")) {

					serviceWiseTransactionResponse.setFailedAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setFailedTransaction(statusWiseTotalTransactionInteger);
				}
				if (statusName.equalsIgnoreCase("Refunded")
						|| statusName.equalsIgnoreCase("Refund")) {

					serviceWiseTransactionResponse.setReversedAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setReversedTransaction(statusWiseTotalTransactionInteger);
				}
			}

			serviceWiseTransactionResponse.setsNo(sNo);
			serviceWiseTransactionResponse.setServicename(serviceName);
			serviceWiseTransactionResponse.setTotalamount(stringServiceWiseAmount);
			serviceWiseTransactionResponse.setTotalTransaction(serviceWiseTotalTransactionLong);

			list.add(serviceWiseTransactionResponse);
		}

		return list;
	}

}
