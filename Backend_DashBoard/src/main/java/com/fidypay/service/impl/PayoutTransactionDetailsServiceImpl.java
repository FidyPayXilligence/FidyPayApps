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
import com.fidypay.entity.PayoutTransactionDetails;
import com.fidypay.entity.ServiceCategory;
import com.fidypay.entity.ServiceInfo;
import com.fidypay.repo.MerchantServiceRepository;
import com.fidypay.repo.PayoutTransactionDetailsRepository;
import com.fidypay.repo.ServiceCategoryRepository;
import com.fidypay.repo.ServiceInfoRepository;
import com.fidypay.request.PayoutTransactionRequest;
import com.fidypay.response.MerchantServicesPayload;
import com.fidypay.response.PayoutTransactionResponse;
import com.fidypay.response.PayoutTransactionsReportPayLoad;
import com.fidypay.response.ServiceWiseTransactionResponse;
import com.fidypay.service.PayoutTransactionDetailsService;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.DateAndTime;
import com.fidypay.utils.ex.DateUtil;

@Service
public class PayoutTransactionDetailsServiceImpl implements PayoutTransactionDetailsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PayoutTransactionDetailsServiceImpl.class);

	@Autowired
	private PayoutTransactionDetailsRepository payoutTransactionDetailsRepository;

	@Autowired
	private MerchantServiceRepository merchantServiceRepository;

	@Autowired
	private ServiceInfoRepository serviceInfoRepository;

	@Autowired
	private ServiceCategoryRepository serviceCategoryRepository;

	@SuppressWarnings("null")
	@Override
	public Map<String, Object> payoutTransactionList(PayoutTransactionRequest payoutTransactionRequest,
			long merchantId) {
		Map<String, Object> map = new HashMap<>();
		try {
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

			List<PayoutTransactionDetails> list = new ArrayList<PayoutTransactionDetails>();
			List<PayoutTransactionResponse> listPayoutTransactionResponse = new ArrayList<PayoutTransactionResponse>();
			Page<PayoutTransactionDetails> listTransaction = null;

			if (startTime.equals("") || startTime.equals("0") || startTime.equals("null") || endTime.equals("null")
					|| endTime.equals("") || endTime.equals("0")) {
				startDate = startDate + " 00.00.00.0";
				endDate = endDate + " 23.59.59.9";
			} else {
				startDate = startDate + " " + startTime + ".00.0";
				endDate = endDate + " " + endTime + ".00.0";
			}

			if ((merchantServiceId == null || merchantServiceId == 0) && (status == null || status == 0)) {
				LOGGER.info("Case 1");
				listTransaction = payoutTransactionDetailsRepository.findByStartDateAndEndDate(merchantId, startDate,
						endDate, paging);
			} else if ((merchantServiceId != null || merchantServiceId != 0) && (status == null || status == 0)) {
				LOGGER.info("Case 2");
				listTransaction = payoutTransactionDetailsRepository.findByStartDateAndEndDateANDService(merchantId,
						startDate, endDate, merchantServiceId, paging);
			} else if ((merchantServiceId == null || merchantServiceId == 0) && (status != null || status != 0)) {
				LOGGER.info("Case 3");
				listTransaction = payoutTransactionDetailsRepository.findByStartDateAndEndDateANDStatus(merchantId,
						startDate, endDate, status, paging);
			} else if ((merchantServiceId != null || merchantServiceId != 0) && (status != null || status != 0)) {
				LOGGER.info("Case 4");
				listTransaction = payoutTransactionDetailsRepository.findByStartDateAndEndDateANDStatusANDService(
						merchantId, startDate, endDate, status, merchantServiceId, paging);
			}

			list = listTransaction.getContent();
			AtomicInteger atomicInteger = new AtomicInteger(1);
			if (list.size() != 0) {
				list.forEach(objects -> {
					DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");
					String txnAmount = amount1.format(objects.getTransactionAmount());
					try {
						String date = DateAndTime.dateFormatReports(objects.getTransactionDate().toString());

						PayoutTransactionResponse payoutTransactionResponse = new PayoutTransactionResponse();

						payoutTransactionResponse.setsNo(atomicInteger.getAndIncrement());
						payoutTransactionResponse.setBankAccountKey(Encryption.decString(objects.getBankAccountKey()));
						payoutTransactionResponse.setBankTransactionIdentification(
								Encryption.decString(objects.getBankTransactionIdentification()));
						payoutTransactionResponse.setTransactionDate(date);
						payoutTransactionResponse.setMerchantTransactionRefId(
								Encryption.decString(objects.getMerchantTransactionRefId()));
						payoutTransactionResponse.setTransactionAmount(txnAmount);
						payoutTransactionResponse
								.setDebitorAcountNumber(Encryption.decString(objects.getDebitorAcountNumber()));
						payoutTransactionResponse.setDebitorIfsc(Encryption.decString(objects.getDebitorIfsc()));
						payoutTransactionResponse
								.setCreditorAccountNumber(Encryption.decString(objects.getCreditorAccountNumber()));
						payoutTransactionResponse.setCreditorIfsc(Encryption.decString(objects.getCreditorIfsc()));
						payoutTransactionResponse.setCreditorName(Encryption.decString(objects.getCreditorName()));
						payoutTransactionResponse.setCreditorEmail(Encryption.decString(objects.getCreditorEmail()));
						payoutTransactionResponse.setCreditorMobile(Encryption.decString(objects.getCreditorMobile()));
						payoutTransactionResponse.setTransactionType(objects.getTransactionType());
						payoutTransactionResponse.setUtr(Encryption.decString(objects.getUtr()));
						payoutTransactionResponse.setBankSideStatus(objects.getBankSideStatus());
						payoutTransactionResponse.setIsReconcile(String.valueOf(objects.getIsReconcile()));
						payoutTransactionResponse.setIsSettled(String.valueOf(objects.getIsSettled()));
						payoutTransactionResponse.setTrxnRefId(objects.getTrxnRefId());
						payoutTransactionResponse.setServiceName(objects.getServiceName());

						if (objects.getTransactionStatus().equals("Success")) {
							payoutTransactionResponse.setTransactionStatus("Success");
						}
						if (objects.getTransactionStatus().equals("Fail")
								|| objects.getTransactionStatus().equals("Failed")) {
							payoutTransactionResponse.setTransactionStatus("Failed");
						}
						if (objects.getTransactionStatus().equals("Pending")) {
							payoutTransactionResponse.setTransactionStatus("Pending");
						}
						if (objects.getTransactionStatus().equals("Reversed")) {
							payoutTransactionResponse.setTransactionStatus("Reversed");
						}

						listPayoutTransactionResponse.add(payoutTransactionResponse);
					} catch (ParseException e) {

						e.printStackTrace();
					}
				});

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "Payout Transaction List");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put("callBackList", listPayoutTransactionResponse);
				map.put("currentPage", listTransaction.getNumber());
				map.put("totalItems", listTransaction.getTotalElements());
				map.put("totalPages", listTransaction.getTotalPages());

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
		return map;
	}

	@SuppressWarnings("null")
	@Override
	public List<PayoutTransactionsReportPayLoad> getPayoutTransactionsStatementReportExcel(
			PayoutTransactionRequest transactionsReportRequest, String clientId) {
		Map<String, Object> map = new HashMap<>();
		List<PayoutTransactionsReportPayLoad> activityList = new ArrayList<PayoutTransactionsReportPayLoad>();
		try {
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

			LOGGER.info("startDate: " + startDate);
			LOGGER.info("endDate: " + endDate);

			List<PayoutTransactionDetails> list = null;

			if ((merchantServiceId == null || merchantServiceId == 0) && (statusId == null || statusId == 0)) {
				LOGGER.info("Case 1");
				list = payoutTransactionDetailsRepository.findByStartDateAndEndDateWithoutPage(merchantId, startDate,
						endDate);
			} else if ((merchantServiceId != null || merchantServiceId != 0) && (statusId == null || statusId == 0)) {
				LOGGER.info("Case 2");
				list = payoutTransactionDetailsRepository.findByStartDateAndEndDateANDServiceWithoutPage(merchantId,
						startDate, endDate, merchantServiceId);
			} else if ((merchantServiceId == null || merchantServiceId == 0) && (statusId != null || statusId != 0)) {
				LOGGER.info("Case 3");
				list = payoutTransactionDetailsRepository.findByStartDateAndEndDateANDStatusWithoutPage(merchantId,
						startDate, endDate, statusId);
			} else if ((merchantServiceId != null || merchantServiceId != 0) && (statusId != null || statusId != 0)) {
				LOGGER.info("Case 4");
				list = payoutTransactionDetailsRepository.findByStartDateAndEndDateANDStatusANDServiceWithoutPage(
						merchantId, startDate, endDate, statusId, merchantServiceId);
			}

			AtomicInteger atomicInteger = new AtomicInteger(1);
			if (list.size() != 0) {
				LOGGER.info("Inside list: " + list.size());
				list.forEach(objects -> {
					DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");
//					String mAmount = amount1.format(objects.getTransactionAmount());
//					String charge = amount1.format(objects.getMerchantServiceCharge());

					try {
						String date = DateAndTime.dateFormatReports(objects.getTransactionDate().toString());

						PayoutTransactionsReportPayLoad payoutTransactionResponse = new PayoutTransactionsReportPayLoad();
						payoutTransactionResponse.setsNo(atomicInteger.getAndIncrement());
						payoutTransactionResponse.setBankAccountKey(Encryption.decString(objects.getBankAccountKey()));
						payoutTransactionResponse.setBankTransactionIdentification(
								Encryption.decString(objects.getBankTransactionIdentification()));
						payoutTransactionResponse.setTransactionDate(date);
						payoutTransactionResponse.setMerchantTransactionRefId(
								Encryption.decString(objects.getMerchantTransactionRefId()));

						payoutTransactionResponse.setTransactionAmount(amount1.format(objects.getTransactionAmount()));
						payoutTransactionResponse
								.setDebitorAcountNumber(Encryption.decString(objects.getDebitorAcountNumber()));
						payoutTransactionResponse.setDebitorIfsc(Encryption.decString(objects.getDebitorIfsc()));
						payoutTransactionResponse
								.setCreditorAccountNumber(Encryption.decString(objects.getCreditorAccountNumber()));
						payoutTransactionResponse.setCreditorIfsc(Encryption.decString(objects.getCreditorIfsc()));
						payoutTransactionResponse.setCreditorName(Encryption.decString(objects.getCreditorName()));
						payoutTransactionResponse.setCreditorEmail(Encryption.decString(objects.getCreditorEmail()));
						payoutTransactionResponse.setCreditorMobile(Encryption.decString(objects.getCreditorMobile()));
						payoutTransactionResponse.setTransactionType(objects.getTransactionType());
						payoutTransactionResponse.setUtr(Encryption.decString(objects.getUtr()));
						payoutTransactionResponse.setBankSideStatus(objects.getBankSideStatus());
//					payoutTransactionResponse.setIsReconcile(String.valueOf(objects.getIsReconcile()));
//					payoutTransactionResponse.setIsSettled(String.valueOf(objects.getIsSettled()));
						payoutTransactionResponse.setTrxnRefId(objects.getTrxnRefId());
						payoutTransactionResponse.setServiceName(objects.getServiceName());
						payoutTransactionResponse
								.setCharges(Double.parseDouble(amount1.format(objects.getMerchantServiceCharge())));

						if (objects.getTransactionStatus().equals("Success")) {
							payoutTransactionResponse.setTransactionStatus("Success");
						}
						if (objects.getTransactionStatus().equals("Fail")) {
							payoutTransactionResponse.setTransactionStatus("Failed");
						}
						if (objects.getTransactionStatus().equals("Pending")) {
							payoutTransactionResponse.setTransactionStatus("Pending");
						}
						if (objects.getTransactionStatus().equals("Reversed")) {
							payoutTransactionResponse.setTransactionStatus("Reversed");
						}

						activityList.add(payoutTransactionResponse);
					} catch (ParseException e) {

						e.printStackTrace();
					}
				});

			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "Transaction not found");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			}

		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage().toString());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return activityList;

	}

	@Override
	public Map<String, Object> payoutTotalTransactionAndTotalAmount(long merchantId) {
		Map<String, Object> map = new HashMap<>();
		try {
			Date date = new Date();
			SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
			format1.setTimeZone(TimeZone.getTimeZone("IST"));

			String date1 = format1.format(date);

			Double amount = 0.0;
			Integer totalTransaction = 0;
			String tamount = "0";

			String startDate = date1 + " 00:00:00.0";
			String endDate = date1 + " 23:59:59.9";

			totalTransaction = payoutTransactionDetailsRepository
					.findTotalTransactionsByMerchantIdAndStartAndEndDate(merchantId, startDate, endDate);

			amount = payoutTransactionDetailsRepository.findByMerchantIdAndStartAndEndDate(merchantId, startDate,
					endDate);

			if (totalTransaction == null) {

				totalTransaction = 0;
			}

			if (amount == null) {
				amount = 0.0;
			}

			DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");
			amount1.setMinimumIntegerDigits(1);
			tamount = amount1.format(amount);

			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, "Total transactions and total amount");
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			map.put("totalTransactions", totalTransaction);
			map.put("totalAmount", tamount);

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage().toString());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;
	}

	@Override
	public Map<String, Object> payoutServicesList(long merchantId) {
		Map<String, Object> map = new HashMap<>();
		List<MerchantServicesPayload> servicesList = new ArrayList<MerchantServicesPayload>();

		try {
			List<MerchantService> list = merchantServiceRepository.findPayoutServicesByMerchantId(merchantId);

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
	public Map<String, Object> payoutServiceTransactionList(PayoutTransactionRequest payoutTransactionRequest,
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
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}

			if (DateUtil.isValidDateFormat(startDate, endDate)) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.INVALID_FROM_TO_DATE);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
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

				list = payoutSDateAndMerchantId(merchantId, startDate, endDate);

			} else if ((merchantServiceId != null || merchantServiceId != 0) && (status == null || status == 0)) {

				list = payoutSDateAndMerchantIdAndMerchantServiceId(merchantId, startDate, endDate, merchantServiceId);

			} else if ((merchantServiceId == null || merchantServiceId == 0) && (status != null || status != 0)) {

				list = payoutSDateAndMerchantIdAndStatusId(merchantId, startDate, endDate, status);

			} else if ((merchantServiceId != null || merchantServiceId != 0) && (status != null || status != 0)) {

				list = payoutSDateAndMerchantIdAndMServiceIdAndStatusID(merchantId, startDate, endDate,
						merchantServiceId, status);

			}

			if (list.size() != 0) {
				map.put("list", list);
				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "Payout Service  Transaction List");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
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
		return map;
	}

	@Override
	public List<ServiceWiseTransactionResponse> payoutSDateAndMerchantId(long merchantId, String from, String to) {

		List<ServiceWiseTransactionResponse> list = new ArrayList<>();

		List<?> list1 = payoutTransactionDetailsRepository.findByDateANDMerchantId(merchantId, from, to);
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

			List<?> list2 = payoutTransactionDetailsRepository.findByDateANDMerchantIdAndServiceName(merchantId, from,
					to, serviceName);

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
				if (statusName.equalsIgnoreCase("Fail")||statusName.equalsIgnoreCase("Failed")) {

					serviceWiseTransactionResponse.setFailedAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setFailedTransaction(statusWiseTotalTransactionInteger);
				}
				if (statusName.equalsIgnoreCase("Reversed")) {

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
	public List<ServiceWiseTransactionResponse> payoutSDateAndMerchantIdAndMerchantServiceId(long merchantId,
			String from, String to, long merchantServiceId) {

		List<ServiceWiseTransactionResponse> list = new ArrayList<>();

		List<?> list1 = payoutTransactionDetailsRepository.findByDateANDMerchantIdANdMServiceId(merchantId, from, to,
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

			List<?> list2 = payoutTransactionDetailsRepository.findByDateANDMerchantIdAndServiceName(merchantId, from,
					to, serviceName);

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
				if (statusName.equalsIgnoreCase("Fail")||statusName.equalsIgnoreCase("Failed")) {

					serviceWiseTransactionResponse.setFailedAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setFailedTransaction(statusWiseTotalTransactionInteger);
				}
				if (statusName.equalsIgnoreCase("Reversed")) {

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
	public List<ServiceWiseTransactionResponse> payoutSDateAndMerchantIdAndStatusId(long merchantId, String from,
			String to, long statusId) {

		List<ServiceWiseTransactionResponse> list = new ArrayList<>();

		List<?> list1 = payoutTransactionDetailsRepository.findByDateANDMerchantIdANdStatusId(merchantId, from, to,
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

			List<?> list2 = payoutTransactionDetailsRepository
					.findByDateANDMerchantIdAndServiceNameAndStatusId(merchantId, from, to, serviceName, statusId);

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
				if (statusName.equalsIgnoreCase("Fail")||statusName.equalsIgnoreCase("Failed")) {

					serviceWiseTransactionResponse.setFailedAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setFailedTransaction(statusWiseTotalTransactionInteger);
				}
				if (statusName.equalsIgnoreCase("Reversed")) {

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
	public List<ServiceWiseTransactionResponse> payoutSDateAndMerchantIdAndMServiceIdAndStatusID(long merchantId,
			String from, String to, long merchantServiceId, long statusId) {

		List<ServiceWiseTransactionResponse> list = new ArrayList<>();

		List<?> list1 = payoutTransactionDetailsRepository.findByDateANDMerchantIdANdMServiceIdStatusId(merchantId,
				from, to, merchantServiceId, statusId);
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

			List<?> list2 = payoutTransactionDetailsRepository
					.findByDateANDMerchantIdAndServiceNameAndStatusId(merchantId, from, to, serviceName, statusId);

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
				if (statusName.equalsIgnoreCase("Fail")||statusName.equalsIgnoreCase("Failed")) {

					serviceWiseTransactionResponse.setFailedAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setFailedTransaction(statusWiseTotalTransactionInteger);
				}
				if (statusName.equalsIgnoreCase("Reversed")) {

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