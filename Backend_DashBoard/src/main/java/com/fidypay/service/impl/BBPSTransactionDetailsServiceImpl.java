package com.fidypay.service.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
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

import org.apache.commons.math3.util.Precision;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fidypay.encryption.Encryption;
import com.fidypay.entity.BBPSApiRequest;
import com.fidypay.entity.BBPSMerchantReconciliation;
import com.fidypay.entity.BBPSTrxnDetails;
import com.fidypay.entity.MerchantService;
import com.fidypay.entity.PGTransactionDetail;
import com.fidypay.entity.ServiceCategory;
import com.fidypay.entity.ServiceInfo;
import com.fidypay.repo.BBPSApiRequestRepository;
import com.fidypay.repo.BBPSMerchantReconciliationRepository;
import com.fidypay.repo.BBPSTrxnDetailsRepository;
import com.fidypay.repo.MerchantServiceRepository;
import com.fidypay.repo.PGTransactionDetailRepository;
import com.fidypay.repo.ServiceCategoryRepository;
import com.fidypay.repo.ServiceInfoRepository;
import com.fidypay.request.BBPSCommissionRequest;
import com.fidypay.request.BBPSTransactionHistoryRequest;
import com.fidypay.request.BBPSTransactionRequest;
import com.fidypay.response.BBPSCommissionResponse;
import com.fidypay.response.BBPSTransactionHistoryResponse;
import com.fidypay.response.BBPSTransactionResponse;
import com.fidypay.response.BBPSTransactionsReportPayLoad;
import com.fidypay.response.MerchantServicesPayload;
import com.fidypay.response.ServiceWiseTransactionResponse;
import com.fidypay.service.BBPSTransactionDetailsService;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.DateAndTime;
import com.fidypay.utils.ex.DateUtil;
import com.fidypay.utils.validation.RegexValidator;

@Service
public class BBPSTransactionDetailsServiceImpl implements BBPSTransactionDetailsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(BBPSTransactionDetailsServiceImpl.class);

	@Autowired
	private BBPSTrxnDetailsRepository bbpsTrxnDetailsRepository;

	@Autowired
	private MerchantServiceRepository merchantServiceRepository;

	@Autowired
	private ServiceInfoRepository serviceInfoRepository;

	@Autowired
	private ServiceCategoryRepository serviceCategoryRepository;

	@Autowired
	private BBPSMerchantReconciliationRepository bbpsMerchantReconciliationRepository;

	@Autowired
	private BBPSApiRequestRepository bbpsApiRequestRepository;

	@Autowired
	private BBPSTransactionDetailsService bbpsTransactionDetailsService;

	@Autowired
	private PGTransactionDetailRepository pgTransactionDetailRepository;

	@SuppressWarnings("null")
	@Override
	public Map<String, Object> bbpsTransactionList(BBPSTransactionRequest bbpsTransactionRequest, long merchantId) {
		Map<String, Object> map = new HashMap<>();
		try {
			Pageable paging = PageRequest.of(bbpsTransactionRequest.getPageNo(), bbpsTransactionRequest.getPageSize(),
					Sort.by("TRANSACTION_DATE").descending());
			Long status = bbpsTransactionRequest.getStatusId();
			String startDate = bbpsTransactionRequest.getStartDate();
			String endDate = bbpsTransactionRequest.getEndDate();
			Long merchantServiceId = bbpsTransactionRequest.getMerchantServiceId();
			String startTime = bbpsTransactionRequest.getStartTime();
			String endTime = bbpsTransactionRequest.getEndTime();

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

			List<BBPSTrxnDetails> list = new ArrayList<BBPSTrxnDetails>();
			List<BBPSTransactionResponse> listBBPSTransactionResponse = new ArrayList<BBPSTransactionResponse>();
			Page<BBPSTrxnDetails> listTransaction = null;

			if (startTime.equals("") || startTime.equals("0") || startTime.equals("null") || endTime.equals("null")
					|| endTime.equals("") || endTime.equals("0")) {
				startDate = startDate + " 00.00.00.0";
				endDate = endDate + " 23.59.59.9";
			} else {
				startDate = startDate + " " + startTime + ".00.0";
				endDate = endDate + " " + endTime + ".00.0";
			}

			if ((merchantServiceId == null || merchantServiceId == 0) && (status == null || status == 0)) {
				listTransaction = bbpsTrxnDetailsRepository.findByStartDateAndEndDate(merchantId, startDate, endDate,
						paging);

			} else if ((merchantServiceId != null || merchantServiceId != 0) && (status == null || status == 0)) {
				listTransaction = bbpsTrxnDetailsRepository.findByStartDateAndEndDateANDService(merchantId, startDate,
						endDate, merchantServiceId, paging);
			} else if ((merchantServiceId == null || merchantServiceId == 0) && (status != null || status != 0)) {
				listTransaction = bbpsTrxnDetailsRepository.findByStartDateAndEndDateANDStatus(merchantId, startDate,
						endDate, status, paging);
			} else if ((merchantServiceId != null || merchantServiceId != 0) && (status != null || status != 0)) {
				listTransaction = bbpsTrxnDetailsRepository.findByStartDateAndEndDateANDStatusANDService(merchantId,
						startDate, endDate, status, merchantServiceId, paging);
			}

			list = listTransaction.getContent();

			AtomicInteger atomicInteger = new AtomicInteger(1);
			if (list.size() != 0) {
				list.forEach(objects -> {

					DecimalFormat amountFormate = new DecimalFormat("#,##,##,##,###.00");

					try {
						String date = DateAndTime.dateFormatReports(objects.getTransactionDate().toString());
						// String mobileNo = "NA";

//						try {
//							BBPSApiRequest apiRequest = bbpsApiRequestRepository
//									.findByMerchantTrxnRefId(objects.getMerchantTransactionRefId());
//							if (apiRequest != null) {
//								String customerParam = apiRequest.getCustomerParams();
//
//								org.json.JSONObject object2 = new org.json.JSONObject(customerParam);
//
//								Iterator<String> keys = object2.keys();
//								while (keys.hasNext()) {
//									String keyValue = (String) keys.next();
//									mobileNo = object2.getString(keyValue);
//
//								}
//
//							} else {
//								mobileNo = "NA";
//							}
//						} catch (Exception e) {
//							mobileNo = "NA";
//						}

						BBPSTransactionResponse bbpsTransactionResponse = new BBPSTransactionResponse();
						bbpsTransactionResponse.setsNo(atomicInteger.getAndIncrement());
						bbpsTransactionResponse.setBbpsTransactionId(objects.getBbpsTransactionId());
						bbpsTransactionResponse.setMerchantId(objects.getMerchantId());
						bbpsTransactionResponse.setTransactionDate(date);
						bbpsTransactionResponse
								.setTransactionAmount(amountFormate.format(objects.getTransactionAmount()));
						bbpsTransactionResponse.setMerchantTransactionRefId(objects.getMerchantTransactionRefId());
						bbpsTransactionResponse.setMerchantServiceCharge(
								Double.parseDouble(amountFormate.format(objects.getMerchantServiceCharge())));
						bbpsTransactionResponse.setMerchantServiceCommision(objects.getMerchantServiceCommision());
						bbpsTransactionResponse.setIsSettled(objects.getIsSettled());
						bbpsTransactionResponse.setIsReconcile(objects.getIsReconcile());
						bbpsTransactionResponse.setTrxnId(objects.getTrxnId());
						bbpsTransactionResponse.setServiceName(Encryption.decString(objects.getServiceName()));
						bbpsTransactionResponse.setResponseMessage(objects.getResponseMessage());
						bbpsTransactionResponse.setPaymentMode(objects.getPaymentMode());

						BBPSApiRequest request = bbpsApiRequestRepository.findById(objects.getRequestId()).get();
						if (request != null && !"NA".equalsIgnoreCase(request.getCustomerParams())) {
							JSONObject jsonObj = new JSONObject(request.getCustomerParams());
							if (jsonObj.has("Mobile Number"))
								bbpsTransactionResponse.setMobile(jsonObj.getString("Mobile Number"));
							else
								bbpsTransactionResponse.setMobile(objects.getTrxnIdentifier());
						} else {
							bbpsTransactionResponse.setMobile(objects.getTrxnIdentifier());
						}

						if (objects.getPaymentId().equals("")) {
							bbpsTransactionResponse.setPaymentId("NA");
						} else {
							bbpsTransactionResponse.setPaymentId(objects.getPaymentId());
						}

						bbpsTransactionResponse.setPaymentStatus(objects.getPaymentStatus());

						if (objects.getTransactionStatus().equals("SUCCESS")) {
							bbpsTransactionResponse.setTransactionStatus("Success");
						}
						if (objects.getTransactionStatus().equals("FAILURE")
								|| objects.getTransactionStatus().equals("FAIL")
								|| objects.getTransactionStatus().equals("FAILED")) {
							bbpsTransactionResponse.setTransactionStatus("Failed");
						}
						if (objects.getTransactionStatus().equals("PENDING")) {
							bbpsTransactionResponse.setTransactionStatus("Pending");
						}

						listBBPSTransactionResponse.add(bbpsTransactionResponse);
					} catch (ParseException e) {

						e.printStackTrace();
					}

				});

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "BBPS Transaction List");
				map.put("callBackList", listBBPSTransactionResponse);
				map.put("currentPage", listTransaction.getNumber());
				map.put("totalItems", listTransaction.getTotalElements());
				map.put("totalPages", listTransaction.getTotalPages());
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);

			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
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
	public Map<String, Object> bbpsTransactionHistory(BBPSTransactionHistoryRequest bbpsTransactionHistoryRequest,
			long merchantId) {
		Map<String, Object> map = new HashMap<>();
		try {
			Pageable paging = PageRequest.of(bbpsTransactionHistoryRequest.getPageNo(),
					bbpsTransactionHistoryRequest.getPageSize(), Sort.by("TRANSACTION_DATE").descending());

			String startDate = bbpsTransactionHistoryRequest.getStartDate();
			String endDate = bbpsTransactionHistoryRequest.getEndDate();
			long merchantUserId = bbpsTransactionHistoryRequest.getMerchantUserId();

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

			List<PGTransactionDetail> list = new ArrayList<>();
			List<BBPSTransactionHistoryResponse> listBBPSTransactionResponse = new ArrayList<BBPSTransactionHistoryResponse>();
			Page<PGTransactionDetail> listTransaction = null;

			startDate = startDate + " 00.00.00.0";
			endDate = endDate + " 23.59.59.9";

			listTransaction = pgTransactionDetailRepository.findByStartDateEndDateAndMerchantUserId(merchantId,
					startDate, endDate, merchantUserId, paging);

//			listTransaction = bbpsTrxnDetailsRepository.findByStartDateAndEndDateForBBPSTransactionHistory(merchantId,
//					startDate, endDate, merchantUserId, paging);

			list = listTransaction.getContent();

			AtomicInteger atomicInteger = new AtomicInteger(1);
			if (!list.isEmpty()) {
				list.forEach(pgTrxnDetail -> {
					LOGGER.info("paymemtid : {}", Encryption.decString(pgTrxnDetail.getMerchantTransactionRefId()));

					BBPSTrxnDetails objects = bbpsTrxnDetailsRepository
							.findByPaymentId(Encryption.decString(pgTrxnDetail.getMerchantTransactionRefId()));

					if (objects != null) {

						BBPSApiRequest request = bbpsApiRequestRepository.findById(objects.getRequestId()).get();

						setBBPSTransactionResponse(request.getCustomerParams(), atomicInteger.getAndIncrement(),
								objects.getBbpsTransactionId(), objects.getMerchantId(),
								pgTrxnDetail.getTransactionDate().toString(), objects.getTransactionAmount(),
								objects.getMerchantTransactionRefId(), pgTrxnDetail.getMerchantServiceCharge(),
								objects.getMerchantServiceCommision(), objects.getIsSettled(), objects.getIsReconcile(),
								objects.getTrxnId(), Encryption.decString(objects.getServiceName()),
								objects.getResponseMessage(), pgTrxnDetail.getPaymentMode(),
								objects.getTrxnIdentifier(), objects.getTransactionStatus(),
								Encryption.decString(pgTrxnDetail.getMerchantTransactionRefId()),
								pgTrxnDetail.getTransactionStatus(), listBBPSTransactionResponse,
								Encryption.decString(pgTrxnDetail.getBankReferanceNumber()),
								objects.getBbpsPlatformFee(), pgTrxnDetail.getRemark());
					} else {

						setBBPSTransactionResponse("NA", atomicInteger.getAndIncrement(), 0l, 0l,
								pgTrxnDetail.getTransactionDate().toString(), pgTrxnDetail.getTransactionAmount(), "NA",
								0, pgTrxnDetail.getMerchantServiceCharge(), '0', '0', "NA", "NA", "NA",
								pgTrxnDetail.getPaymentMode(), "NA", "FAILURE",
								Encryption.decString(pgTrxnDetail.getMerchantTransactionRefId()),
								pgTrxnDetail.getTransactionStatus(), listBBPSTransactionResponse,
								Encryption.decString(pgTrxnDetail.getBankReferanceNumber()), 0.0,
								pgTrxnDetail.getRemark());

					}
				});

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "BBPS Transaction List");
				map.put("callBackList", listBBPSTransactionResponse);
				map.put("currentPage", listTransaction.getNumber());
				map.put("totalItems", listTransaction.getTotalElements());
				map.put("totalPages", listTransaction.getTotalPages());
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);

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

	private void setBBPSTransactionResponse(String customerParams, int andIncrement, Long bbpsTransactionId,
			Long merchantId, String transactionDate, double transactionAmount, String merchantTransactionRefId,
			double merchantServiceCharge, double merchantServiceCommision, char isSettled, char isReconcile,
			String trxnId, String serviceName, String responseMessage, String paymentMode, String trxnIdentifier,
			String transactionStatus, String paymentID, String paymentStats,
			List<BBPSTransactionHistoryResponse> listBBPSTransactionResponse, String utr, double bbpsPlatformFee,
			String remark) {

		BBPSTransactionHistoryResponse bbpsTransactionResponse = new BBPSTransactionHistoryResponse();
		try {

			bbpsTransactionResponse.setCustomerParams(customerParams);
			bbpsTransactionResponse.setsNo(andIncrement);
			bbpsTransactionResponse.setBbpsTransactionId(bbpsTransactionId);
			bbpsTransactionResponse.setMerchantId(merchantId);
			try {
				bbpsTransactionResponse.setTransactionDate(DateAndTime.dateFormatReports(transactionDate));
			} catch (ParseException e) {
				bbpsTransactionResponse.setTransactionDate(transactionDate);
				e.printStackTrace();
			}
			bbpsTransactionResponse.setTransactionAmount(Precision.round(transactionAmount, 2));
			bbpsTransactionResponse.setMerchantTransactionRefId(merchantTransactionRefId);
			bbpsTransactionResponse.setMerchantServiceCharge(Precision.round(merchantServiceCharge, 2));
			bbpsTransactionResponse.setMerchantServiceCommision(Precision.round(merchantServiceCommision, 2));
			bbpsTransactionResponse.setIsSettled(isSettled);
			bbpsTransactionResponse.setIsReconcile(isReconcile);
			bbpsTransactionResponse.setTrxnId(trxnId);
			bbpsTransactionResponse.setServiceName(serviceName);
			bbpsTransactionResponse.setResponseMessage(responseMessage);
			bbpsTransactionResponse.setUtr(utr);
			bbpsTransactionResponse.setBbpsPlatformFee(bbpsPlatformFee);

			bbpsTransactionResponse.setMobile(trxnIdentifier);

			if ("DC".equalsIgnoreCase(paymentMode) || "Debit Card".equalsIgnoreCase(paymentMode)) {
				bbpsTransactionResponse.setPaymentMode("Debit Card");
			} else if ("CC".equalsIgnoreCase(paymentMode) || "Credit Card".equalsIgnoreCase(paymentMode)) {
				bbpsTransactionResponse.setPaymentMode("Credit Card");
			} else if ("NB".equalsIgnoreCase(paymentMode) || "Net Banking".equalsIgnoreCase(paymentMode)) {
				bbpsTransactionResponse.setPaymentMode("Net Banking");
			} else if ("Cash".equalsIgnoreCase(paymentMode) || "Wallet".equalsIgnoreCase(paymentMode)
					|| "PG Wallet".equalsIgnoreCase(paymentMode)) {
				bbpsTransactionResponse.setPaymentMode("Wallet");
			}

			bbpsTransactionResponse.setPaymentId(paymentID);
			bbpsTransactionResponse.setCategoryName(remark);

			bbpsTransactionResponse.setPaymentStatus(paymentStats);

			LOGGER.info("paymentStatus : {}", paymentStats);

			if (transactionStatus.equals("SUCCESS")) {
				bbpsTransactionResponse.setTransactionStatus("Success");
			}
			if (transactionStatus.equals("FAILURE")) {
				bbpsTransactionResponse.setTransactionStatus("Failed");
			}
			if (transactionStatus.equals("PENDING")) {
				bbpsTransactionResponse.setTransactionStatus("Pending");
			}
			if (paymentStats.equals("Pending")) {
				bbpsTransactionResponse.setTransactionStatus("Pending");
			}

			listBBPSTransactionResponse.add(bbpsTransactionResponse);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("null")
	@Override
	public List<BBPSTransactionsReportPayLoad> getBBPSTransactionsStatementReportExcel(
			BBPSTransactionRequest transactionsReportRequest, String clientId) {
		Map<String, Object> map = new HashMap<>();
		List<BBPSTransactionsReportPayLoad> activityList = new ArrayList<BBPSTransactionsReportPayLoad>();
		try {
			String startHours = null;
			String endHours = null;
			Long merchantServiceId = transactionsReportRequest.getMerchantServiceId();
			Long status = transactionsReportRequest.getStatusId();

			if (transactionsReportRequest.getStartTime().equals("0") || transactionsReportRequest.getStartTime() == "0"
					|| transactionsReportRequest.getEndTime() == "0"
					|| transactionsReportRequest.getEndTime().equals("0")) {

				startHours = "00.00.00.0";
				endHours = "23.59.59.9";
			}

			List<BBPSTrxnDetails> details = null;

			Long merchantId = Long.parseLong(clientId);
			String startDate = transactionsReportRequest.getStartDate() + " " + startHours;
			String endDate = transactionsReportRequest.getEndDate() + " " + endHours;

			if ((merchantServiceId == null || merchantServiceId == 0) && (status == null || status == 0)) {
				details = bbpsTrxnDetailsRepository.findByStartDateAndEndDateWithoutPage(merchantId, startDate,
						endDate);
			} else if ((merchantServiceId != null || merchantServiceId != 0) && (status == null || status == 0)) {
				details = bbpsTrxnDetailsRepository.findByStartDateAndEndDateANDServiceWithoutPage(merchantId,
						startDate, endDate, merchantServiceId);
			} else if ((merchantServiceId == null || merchantServiceId == 0) && (status != null || status != 0)) {
				details = bbpsTrxnDetailsRepository.findByStartDateAndEndDateANDStatusWithoutPage(merchantId, startDate,
						endDate, status);
			} else if ((merchantServiceId != null || merchantServiceId != 0) && (status != null || status != 0)) {
				details = bbpsTrxnDetailsRepository.findByStartDateAndEndDateANDStatusANDServiceWithoutPage(merchantId,
						startDate, endDate, status, merchantServiceId);
			}

			AtomicInteger atomicInteger = new AtomicInteger(1);
			if (details.size() != 0) {
				details.forEach(objects -> {

					DecimalFormat amountFormate = new DecimalFormat("#,##,##,##,###.00");

					try {
						String date = DateAndTime.dateFormatReports(objects.getTransactionDate().toString());

						BBPSTransactionsReportPayLoad bbpsTransactionResponse = new BBPSTransactionsReportPayLoad();

						bbpsTransactionResponse.setBbpsTransactionId(objects.getBbpsTransactionId());
						bbpsTransactionResponse.setsNo(atomicInteger.getAndIncrement());
						bbpsTransactionResponse.setMerchantId(objects.getMerchantId());
						bbpsTransactionResponse.setTransactionDate(date);

						bbpsTransactionResponse
								.setTransactionAmount(amountFormate.format(objects.getTransactionAmount()));

						bbpsTransactionResponse.setMerchantTransactionRefId(objects.getMerchantTransactionRefId());

						bbpsTransactionResponse.setMerchantServiceCharge(objects.getMerchantServiceCharge());

						bbpsTransactionResponse.setMerchantServiceCommision(
								Double.parseDouble(amountFormate.format(objects.getMerchantServiceCommision())));
						bbpsTransactionResponse.setIsSettled(String.valueOf(objects.getIsReconcile()));
						bbpsTransactionResponse.setIsReconcile(String.valueOf(objects.getIsReconcile()));
						bbpsTransactionResponse.setTrxnId(objects.getTrxnId());
						bbpsTransactionResponse.setServiceName(Encryption.decString(objects.getServiceName()));
						bbpsTransactionResponse.setPaymentStatus(objects.getPaymentStatus());
						bbpsTransactionResponse.setTrxnId(objects.getTrxnId());
						bbpsTransactionResponse.setTrxnRefId(objects.getTransactionRefId());
						bbpsTransactionResponse.setPaymentMode(objects.getPaymentMode());
						bbpsTransactionResponse.setMobile(objects.getTrxnIdentifier());

						if (objects.getTransactionStatus().equals("SUCCESS")) {
							bbpsTransactionResponse.setTransactionStatus("Success");
						}
						if (objects.getTransactionStatus().equals("FAILURE")) {
							bbpsTransactionResponse.setTransactionStatus("Failed");
						}
						if (objects.getTransactionStatus().equals("PENDING")) {
							bbpsTransactionResponse.setTransactionStatus("Pending");
						}

						activityList.add(bbpsTransactionResponse);
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
	public Map<String, Object> bbpsTotalTransactionAndTotalAmount(long merchantId) {
		Map<String, Object> map = new HashMap<>();
		try {
			Date date = new Date();
			SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
			format1.setTimeZone(TimeZone.getTimeZone("IST"));

			String date1 = format1.format(date);
			LOGGER.info(" DATE : " + date1);

			Double amount = 0.0;
			Integer totalTransaction = 0;
			String tamount = "0";

			String startDate = date1 + " 00:00:00.0";
			String endDate = date1 + " 23:59:59.9";
			LOGGER.info("startDate : " + date1);

			totalTransaction = bbpsTrxnDetailsRepository.findTotalTransactionsByMerchantIdAndStartAndEndDate(merchantId,
					startDate, endDate);

			LOGGER.info("totalTransaction: " + totalTransaction);

			amount = bbpsTrxnDetailsRepository.findByMerchantIdAndStartAndEndDate(merchantId, startDate, endDate);

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
			map.put("totalTransactions", totalTransaction);
			map.put("totalAmount", tamount);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);

		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage().toString());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;

	}

	@Override
	public Map<String, Object> bbpsServicesList(long merchantId) {
		Map<String, Object> map = new HashMap<>();
		List<MerchantServicesPayload> servicesList = new ArrayList<MerchantServicesPayload>();

		try {
			List<MerchantService> list = merchantServiceRepository.findServicesByMerchantId(merchantId);

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
			map.put("servicesList", servicesList);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage().toString());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;
	}

	@Override
	public Map<String, Object> bbpsServiceTransactionList(BBPSTransactionRequest bbpsTransactionRequest,
			long merchantId) {
		Map<String, Object> map = new HashMap<>();
		try {

			Long status = bbpsTransactionRequest.getStatusId();
			String startDate = bbpsTransactionRequest.getStartDate();
			String endDate = bbpsTransactionRequest.getEndDate();
			Long merchantServiceId = bbpsTransactionRequest.getMerchantServiceId();
			String startTime = bbpsTransactionRequest.getStartTime();
			String endTime = bbpsTransactionRequest.getEndTime();

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

				list = bbpsSDateAndMerchantId(merchantId, startDate, endDate);

			} else if ((merchantServiceId != null || merchantServiceId != 0) && (status == null || status == 0)) {

				list = bbpsSDateAndMerchantIdAndMServiceId(merchantId, startDate, endDate, merchantServiceId);

			} else if ((merchantServiceId == null || merchantServiceId == 0) && (status != null || status != 0)) {

				list = bbpsSDateAndMerchantIdAndStatusId(merchantId, startDate, endDate, status);

			} else if ((merchantServiceId != null || merchantServiceId != 0) && (status != null || status != 0)) {

				list = bbpsSDateAndMerchantIdAndMServiceIdAndStatusId(merchantId, startDate, endDate, merchantServiceId,
						status);

			}

			if (list.size() != 0) {
				map.put("list", list);
				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "BBPS Service  Transaction List");
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
	public List<ServiceWiseTransactionResponse> bbpsSDateAndMerchantId(long merchantId, String from, String to) {
		List<ServiceWiseTransactionResponse> list = new ArrayList<>();

		List<?> list1 = bbpsTrxnDetailsRepository.findByDateANDMerchantId(merchantId, from, to);
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

			List<?> list2 = bbpsTrxnDetailsRepository.findByDateANDMerchantIdAndServiceName(merchantId, from, to,
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
				if (statusName.equalsIgnoreCase("FAILURE") || statusName.equalsIgnoreCase("Failed")) {

					serviceWiseTransactionResponse.setFailedAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setFailedTransaction(statusWiseTotalTransactionInteger);
				}
				if (statusName.equalsIgnoreCase("Reversed")) {

					serviceWiseTransactionResponse.setReversedAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setReversedTransaction(statusWiseTotalTransactionInteger);
				}
			}

			serviceWiseTransactionResponse.setsNo(sNo);
			serviceWiseTransactionResponse.setServicename(Encryption.decString(serviceName));
			serviceWiseTransactionResponse.setTotalamount(stringServiceWiseAmount);
			serviceWiseTransactionResponse.setTotalTransaction(serviceWiseTotalTransactionLong);

			list.add(serviceWiseTransactionResponse);
		}

		return list;
	}

	@Override
	public List<ServiceWiseTransactionResponse> bbpsSDateAndMerchantIdAndMServiceId(long merchantId, String from,
			String to, long merchantServiceId) {
		List<ServiceWiseTransactionResponse> list = new ArrayList<>();

		List<?> list1 = bbpsTrxnDetailsRepository.findByDateANDMerchantIdAndMServiceId(merchantId, from, to,
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

			List<?> list2 = bbpsTrxnDetailsRepository.findByDateANDMerchantIdAndServiceName(merchantId, from, to,
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
				if (statusName.equalsIgnoreCase("FAILURE") || statusName.equalsIgnoreCase("Failed")) {

					serviceWiseTransactionResponse.setFailedAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setFailedTransaction(statusWiseTotalTransactionInteger);
				}
				if (statusName.equalsIgnoreCase("Reversed")) {

					serviceWiseTransactionResponse.setReversedAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setReversedTransaction(statusWiseTotalTransactionInteger);
				}
			}

			serviceWiseTransactionResponse.setsNo(sNo);
			serviceWiseTransactionResponse.setServicename(Encryption.decString(serviceName));
			serviceWiseTransactionResponse.setTotalamount(stringServiceWiseAmount);
			serviceWiseTransactionResponse.setTotalTransaction(serviceWiseTotalTransactionLong);

			list.add(serviceWiseTransactionResponse);
		}

		return list;
	}

	@Override
	public List<ServiceWiseTransactionResponse> bbpsSDateAndMerchantIdAndStatusId(long merchantId, String from,
			String to, long statusId) {
		List<ServiceWiseTransactionResponse> list = new ArrayList<>();

		List<?> list1 = bbpsTrxnDetailsRepository.findByDateANDMerchantIdAndStatusId(merchantId, from, to, statusId);
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

			List<?> list2 = bbpsTrxnDetailsRepository.findByDateANDMerchantIdAndServiceNameAndStatusId(merchantId, from,
					to, serviceName, statusId);

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
				if (statusName.equalsIgnoreCase("FAILURE") || statusName.equalsIgnoreCase("Failed")) {

					serviceWiseTransactionResponse.setFailedAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setFailedTransaction(statusWiseTotalTransactionInteger);
				}
				if (statusName.equalsIgnoreCase("Reversed")) {

					serviceWiseTransactionResponse.setReversedAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setReversedTransaction(statusWiseTotalTransactionInteger);
				}
			}

			serviceWiseTransactionResponse.setsNo(sNo);
			serviceWiseTransactionResponse.setServicename(Encryption.decString(serviceName));
			serviceWiseTransactionResponse.setTotalamount(stringServiceWiseAmount);
			serviceWiseTransactionResponse.setTotalTransaction(serviceWiseTotalTransactionLong);

			list.add(serviceWiseTransactionResponse);
		}

		return list;
	}

	@Override
	public List<ServiceWiseTransactionResponse> bbpsSDateAndMerchantIdAndMServiceIdAndStatusId(long merchantId,
			String from, String to, long merchantServiceId, long statusId) {
		List<ServiceWiseTransactionResponse> list = new ArrayList<>();

		List<?> list1 = bbpsTrxnDetailsRepository.findByDateANDMerchantIdAndMServiceIdAndStatusId(merchantId, from, to,
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

			List<?> list2 = bbpsTrxnDetailsRepository.findByDateANDMerchantIdAndServiceNameAndStatusId(merchantId, from,
					to, serviceName, statusId);

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
				if (statusName.equalsIgnoreCase("FAILURE") || statusName.equalsIgnoreCase("Failed")) {

					serviceWiseTransactionResponse.setFailedAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setFailedTransaction(statusWiseTotalTransactionInteger);
				}
				if (statusName.equalsIgnoreCase("Reversed")) {

					serviceWiseTransactionResponse.setReversedAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setReversedTransaction(statusWiseTotalTransactionInteger);
				}
			}

			serviceWiseTransactionResponse.setsNo(sNo);
			serviceWiseTransactionResponse.setServicename(Encryption.decString(serviceName));
			serviceWiseTransactionResponse.setTotalamount(stringServiceWiseAmount);
			serviceWiseTransactionResponse.setTotalTransaction(serviceWiseTotalTransactionLong);

			list.add(serviceWiseTransactionResponse);
		}

		return list;
	}

//-------------------------------------------------------------------	
	@Override
	public Map<String, Object> bbpsCommissionslist(BBPSCommissionRequest bbpsCommissionRequest, long merchantId) {
		Map<String, Object> map = new HashMap<>();
		List<BBPSCommissionResponse> commissionList = new ArrayList<BBPSCommissionResponse>();

		List<BBPSMerchantReconciliation> list = new ArrayList<BBPSMerchantReconciliation>();
		Page<BBPSMerchantReconciliation> listTransaction = null;
		try {

			String startDate = bbpsCommissionRequest.getStartDate();
			String endDate = bbpsCommissionRequest.getEndDate();

			Pageable paging = PageRequest.of(bbpsCommissionRequest.getPageNo(), bbpsCommissionRequest.getPageSize(),
					Sort.by("RECONCILIATION_DATE").descending());

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
			String fromDate = startDate + " 00:00:00.0";
			String toDate = endDate + " 23:59:59.9";

			LOGGER.info("--fromDate-- " + fromDate);
			LOGGER.info("--toDate-- " + toDate);
			LOGGER.info("--merchantId-- " + merchantId);

			if (paging != null) {
				listTransaction = bbpsMerchantReconciliationRepository.findByFromDateAndTODate(fromDate, toDate,
						merchantId, paging);
			}
			list = listTransaction.getContent();
			LOGGER.info("list: " + list.size());
			if (list.size() != 0) {

				int i = 0;
				for (BBPSMerchantReconciliation bbpsMerchantReconciliation : list) {
					i++;
					DecimalFormat gsTdFormate = new DecimalFormat("##.0000");

					String fdate = DateAndTime.dateFormatReports(bbpsMerchantReconciliation.getFromDate().toString());

					String tdate = DateAndTime.dateFormatReports(bbpsMerchantReconciliation.getToDate().toString());

					String recodate = DateAndTime
							.dateFormatReports(bbpsMerchantReconciliation.getReconciliationDate().toString());

					BBPSCommissionResponse commissionResponse = new BBPSCommissionResponse();
					commissionResponse.setsNo(i);
					commissionResponse.setFromDate(fdate);
					commissionResponse.setIsVerified(bbpsMerchantReconciliation.getIsVerified());

					String recoAmountGstString = gsTdFormate.format(bbpsMerchantReconciliation.getRecoAmountGst());
					String recoAmountTdsStr = gsTdFormate.format(bbpsMerchantReconciliation.getRecoAmountTds());
					commissionResponse.setRecoAmountGst(recoAmountGstString);
					commissionResponse.setRecoAmountTds(recoAmountTdsStr);
					commissionResponse.setReconciliationDate(recodate);
					commissionResponse.setReconciliationDetails(bbpsMerchantReconciliation.getReconciliationDetails());

//					String rAmount = amountFormate.format(bbpsMerchantReconciliation.getReconTotalAmount());
//					String sAmount = amountFormate.format(bbpsMerchantReconciliation.getReconSettlementAmount());

					BigDecimal bd = new BigDecimal(bbpsMerchantReconciliation.getReconTotalAmount()).setScale(2,
							RoundingMode.HALF_DOWN);

					BigDecimal td = new BigDecimal(bbpsMerchantReconciliation.getReconSettlementAmount()).setScale(2,
							RoundingMode.HALF_DOWN);

					double rAmount = bd.doubleValue();
					LOGGER.info("----rAmount-------:" + rAmount);

					double sAmount = td.doubleValue();
					LOGGER.info("----sAmount-------:" + sAmount);

					commissionResponse.setReconSettlementAmount(Double.valueOf(sAmount));
					commissionResponse.setReconTotalAmount(Double.valueOf(rAmount));
					commissionResponse
							.setReconTotalTransactionCount(bbpsMerchantReconciliation.getReconTotalTransactionCount());
					commissionResponse.setServiceName(bbpsMerchantReconciliation.getServiceName());
					commissionResponse.setMerchantServiceId(bbpsMerchantReconciliation.getMerchantServiceId());
					commissionResponse.setToDate(tdate);
					commissionList.add(commissionResponse);
				}

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "BBPS Commissions Details");
				map.put("commissionList", commissionList);
				map.put("totalItems", listTransaction.getTotalElements());
				map.put("totalPages", listTransaction.getTotalPages());

				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
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
	public List<BBPSCommissionResponse> bbpsCommissionsistReportExcel(String startDate, String endDate,
			long merchantId) {
		List<BBPSCommissionResponse> commissionList = new ArrayList<>();
		try {

			String fromDate = startDate + " 00:00:00.0";
			String toDate = endDate + " 23:59:59.9";
			List<BBPSMerchantReconciliation> list = null;

			list = bbpsMerchantReconciliationRepository.findByFromDateAndTODateWithoutPage(fromDate, toDate,
					merchantId);

			if (list.size() != 0) {

				int i = 0;
				for (BBPSMerchantReconciliation bbpsMerchantReconciliation : list) {
					i++;

					DecimalFormat gsTdFormate = new DecimalFormat("##.0000");

					String fdate = DateAndTime.dateFormatReports(bbpsMerchantReconciliation.getFromDate().toString());
					String tdate = DateAndTime.dateFormatReports(bbpsMerchantReconciliation.getToDate().toString());
					String reconciliationDate = DateAndTime
							.dateFormatReports(bbpsMerchantReconciliation.getReconciliationDate().toString());

					BBPSCommissionResponse commissionResponse = new BBPSCommissionResponse();
					commissionResponse.setsNo(i);
					commissionResponse.setFromDate(fdate);
					commissionResponse.setIsVerified(bbpsMerchantReconciliation.getIsVerified());

					String recoAmountGstString = gsTdFormate.format(bbpsMerchantReconciliation.getRecoAmountGst());
					String recoAmountTdsStr = gsTdFormate.format(bbpsMerchantReconciliation.getRecoAmountTds());

					commissionResponse.setRecoAmountGst(recoAmountGstString);
					commissionResponse.setRecoAmountTds(recoAmountTdsStr);

					commissionResponse.setReconciliationDate(reconciliationDate);
					commissionResponse.setReconciliationDetails(bbpsMerchantReconciliation.getReconciliationDetails());

//					String rAmount = amountFormate.format(bbpsMerchantReconciliation.getReconTotalAmount());
//					String sAmount = amountFormate.format(bbpsMerchantReconciliation.getReconSettlementAmount());

					BigDecimal bd = new BigDecimal(bbpsMerchantReconciliation.getReconTotalAmount()).setScale(2,
							RoundingMode.HALF_DOWN);

					BigDecimal td = new BigDecimal(bbpsMerchantReconciliation.getReconSettlementAmount()).setScale(2,
							RoundingMode.HALF_DOWN);

					double rAmount = bd.doubleValue();
					LOGGER.info("----rAmount-------:" + rAmount);

					double sAmount = td.doubleValue();
					LOGGER.info("----sAmount-------:" + sAmount);
					commissionResponse.setReconSettlementAmount(sAmount);
					commissionResponse.setReconTotalAmount(rAmount);
					commissionResponse
							.setReconTotalTransactionCount(bbpsMerchantReconciliation.getReconTotalTransactionCount());
					commissionResponse.setServiceName(bbpsMerchantReconciliation.getServiceName());
					commissionResponse.setMerchantServiceId(bbpsMerchantReconciliation.getMerchantServiceId());
					commissionResponse.setToDate(tdate);

					commissionList.add(commissionResponse);
				}

			} else {
				commissionList.add(null);
			}

		} catch (Exception e) {
			e.printStackTrace();
			commissionList.add(null);
		}

		return commissionList;
	}

//	@Override
//	public Map<String, Object> findByMobileNo(String mobile, long merchantId) {
//		Map<String, Object> map = new HashMap<>();
//		try {
//			String mobileNo="NA";
//            List<BBPSTransactionResponse> listBBPSTransactionResponse = new ArrayList<BBPSTransactionResponse>();
//			List<BBPSTrxnDetails> listTransaction = null;
//			
//			List<BBPSApiRequest> bList=bbpsApiRequestRepository.findAllpostpaidandprepaidservice();
//			
//			AtomicInteger atomicInteger = new AtomicInteger(1);
//			for(BBPSApiRequest bbpsApiRequest:bList) {
//
//				
//				if(!bbpsApiRequest.getCustomerParams().equals("NA")) {
//				org.json.JSONObject object2 = new org.json.JSONObject(bbpsApiRequest.getCustomerParams());
//
//				LOGGER.info("----MerchantTrxnRefId-------:" + bbpsApiRequest.getMerchantTrxnRefId());
//				LOGGER.info("----CustomerParams-------:" + bbpsApiRequest.getCustomerParams());
//				Iterator<String> keys = object2.keys();
//				while (keys.hasNext()) {
//					String keyValue = (String) keys.next();
//					try {
//					mobileNo = object2.getString(keyValue);
//					}
//					catch(Exception e) {
//						mobileNo ="NA";
//					}
//					
//                  String merchantTrxnRefId=bbpsApiRequest.getMerchantTrxnRefId();
//				
//					if(mobileNo.equals(mobile)) {
//						LOGGER.info("----Inside matched mobile-------:");
//						
//						
//						listTransaction = bbpsTrxnDetailsRepository.findBBPSDetailsByMerchantTrxnRefId(merchantTrxnRefId);	
//		
//						
//						
//						
//	//					AtomicInteger atomicInteger = new AtomicInteger(1);
//						if (listTransaction.size() != 0) {
//							listTransaction.forEach(objects -> {
//								LOGGER.info("----Inside list-------:");
//								DecimalFormat amountFormate = new DecimalFormat("#,##,##,##,###.00");
//
//								try {
//									String date = DateAndTime.dateFormatReports(objects.getTransactionDate().toString());
//
//
//									BBPSTransactionResponse bbpsTransactionResponse = new BBPSTransactionResponse();
//									bbpsTransactionResponse.setsNo(atomicInteger.getAndIncrement());
//									bbpsTransactionResponse.setBbpsTransactionId(objects.getBbpsTransactionId());
//									bbpsTransactionResponse.setMerchantId(objects.getMerchantId());
//									bbpsTransactionResponse.setTransactionDate(date);
//									bbpsTransactionResponse
//											.setTransactionAmount(amountFormate.format(objects.getTransactionAmount()));
//									bbpsTransactionResponse.setMerchantTransactionRefId(objects.getMerchantTransactionRefId());
//									bbpsTransactionResponse.setMerchantServiceCharge(
//											Double.parseDouble(amountFormate.format(objects.getMerchantServiceCharge())));
//									bbpsTransactionResponse.setMerchantServiceCommision(objects.getMerchantServiceCommision());
//									bbpsTransactionResponse.setIsSettled(objects.getIsSettled());
//									bbpsTransactionResponse.setIsReconcile(objects.getIsReconcile());
//									bbpsTransactionResponse.setTrxnId(objects.getTrxnId());
//									bbpsTransactionResponse.setMobile(mobile);
//									bbpsTransactionResponse.setServiceName(Encryption.decString(objects.getServiceName()));
//									bbpsTransactionResponse.setResponseMessage(objects.getResponseMessage());
//									bbpsTransactionResponse.setPaymentMode(objects.getPaymentMode());
//
//									if (objects.getPaymentId().equals("")) {
//										bbpsTransactionResponse.setPaymentId("NA");
//									} else {
//										bbpsTransactionResponse.setPaymentId(objects.getPaymentId());
//									}
//
//									bbpsTransactionResponse.setPaymentStatus(objects.getPaymentStatus());
//
//									if (objects.getTransactionStatus().equals("SUCCESS")) {
//										bbpsTransactionResponse.setTransactionStatus("Success");
//									}
//									if (objects.getTransactionStatus().equals("FAILURE")
//											|| objects.getTransactionStatus().equals("FAIL")
//											|| objects.getTransactionStatus().equals("FAILED")) {
//										bbpsTransactionResponse.setTransactionStatus("Failed");
//									}
//									if (objects.getTransactionStatus().equals("PENDING")) {
//										bbpsTransactionResponse.setTransactionStatus("Pending");
//									}
//
//									listBBPSTransactionResponse.add(bbpsTransactionResponse);
//								} catch (ParseException e) {
//                                    e.printStackTrace();
//								}
//
//							});
//
//						
//
//						} else {
//							map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
//							map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
//							map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
//						}
//                      }
//					}
//			}
//				
//				
//			}
//			LOGGER.info("----list size-------:" + listBBPSTransactionResponse.size());
//			
//			if(listBBPSTransactionResponse.size()>=15) {
//				
//			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
//			map.put(ResponseMessage.DESCRIPTION, "BBPS Transaction List");
//			map.put("callBackList", listBBPSTransactionResponse.stream()
//	                .skip(listBBPSTransactionResponse.size() - 15) 
//	                .collect(Collectors.toList()));
//			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
//		}if(!listBBPSTransactionResponse.isEmpty() && listBBPSTransactionResponse.size()<=15) {
//			
//		map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
//		map.put(ResponseMessage.DESCRIPTION, "BBPS Transaction List");
//		map.put("callBackList", listBBPSTransactionResponse);
//		map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
//	}
//			else {
//				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
//				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
//				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);	
//			}
//			
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
//			map.put(ResponseMessage.DESCRIPTION, e.getMessage().toString());
//			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
//		}
//
//		return map;
//
//	}

	@Override
	public Map<String, Object> findByMobileNo(String mobile, long merchantId) {
		Map<String, Object> map = new HashMap<>();
		try {
			String mobileNo = "NA";
			List<BBPSTransactionResponse> listBBPSTransactionResponse = new ArrayList<BBPSTransactionResponse>();
			List<BBPSTrxnDetails> listTransaction = null;
			AtomicInteger atomicInteger = new AtomicInteger(1);

			listTransaction = bbpsTrxnDetailsRepository.findBBPSDetailsByTrxnIdentifier(mobile);

			if (listTransaction.size() != 0) {
				listTransaction.forEach(objects -> {
					LOGGER.info("----Inside list-------:");
					DecimalFormat amountFormate = new DecimalFormat("#,##,##,##,###.00");

					try {
						String date = DateAndTime.dateFormatReports(objects.getTransactionDate().toString());

						BBPSTransactionResponse bbpsTransactionResponse = new BBPSTransactionResponse();
						bbpsTransactionResponse.setsNo(atomicInteger.getAndIncrement());
						bbpsTransactionResponse.setBbpsTransactionId(objects.getBbpsTransactionId());
						bbpsTransactionResponse.setMerchantId(objects.getMerchantId());
						bbpsTransactionResponse.setTransactionDate(date);
						bbpsTransactionResponse
								.setTransactionAmount(amountFormate.format(objects.getTransactionAmount()));
						bbpsTransactionResponse.setMerchantTransactionRefId(objects.getMerchantTransactionRefId());
						bbpsTransactionResponse.setMerchantServiceCharge(
								Double.parseDouble(amountFormate.format(objects.getMerchantServiceCharge())));
						bbpsTransactionResponse.setMerchantServiceCommision(objects.getMerchantServiceCommision());
						bbpsTransactionResponse.setIsSettled(objects.getIsSettled());
						bbpsTransactionResponse.setIsReconcile(objects.getIsReconcile());
						bbpsTransactionResponse.setTrxnId(objects.getTrxnId());
						bbpsTransactionResponse.setMobile(mobile);
						bbpsTransactionResponse.setServiceName(Encryption.decString(objects.getServiceName()));
						bbpsTransactionResponse.setResponseMessage(objects.getResponseMessage());
						bbpsTransactionResponse.setPaymentMode(objects.getPaymentMode());

						if (objects.getPaymentId().equals("")) {
							bbpsTransactionResponse.setPaymentId("NA");
						} else {
							bbpsTransactionResponse.setPaymentId(objects.getPaymentId());
						}

						bbpsTransactionResponse.setPaymentStatus(objects.getPaymentStatus());

						if (objects.getTransactionStatus().equals("SUCCESS")) {
							bbpsTransactionResponse.setTransactionStatus("Success");
						}
						if (objects.getTransactionStatus().equals("FAILURE")
								|| objects.getTransactionStatus().equals("FAIL")
								|| objects.getTransactionStatus().equals("FAILED")) {
							bbpsTransactionResponse.setTransactionStatus("Failed");
						}
						if (objects.getTransactionStatus().equals("PENDING")) {
							bbpsTransactionResponse.setTransactionStatus("Pending");
						}

						listBBPSTransactionResponse.add(bbpsTransactionResponse);
					} catch (ParseException e) {
						e.printStackTrace();
					}

				});

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "BBPS Transaction List");
				map.put("callBackList", listBBPSTransactionResponse);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);

			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			}

		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage().toString());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}

		return map;

	}

	@Override
	public Map<String, Object> checkBBPSTransactionDetails(String key, String value, long merchantId) {
		Map<String, Object> map = new HashMap<>();
		try {

			switch (key) {

			case "mobile":
				map = bbpsTransactionDetailsService.findByMobileNo(value, merchantId);
				break;

			case "merchantTrxnRefId":
				map = bbpsTransactionDetailsService.findByMerchantTrxnRefIdo(value, merchantId);
				break;

			default:
				map = bbpsTransactionDetailsService.findByMobileNo(value, merchantId);
				break;

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
	public Map<String, Object> findByMerchantTrxnRefIdo(String merchantTrxnRefId, long merchantId) {
		Map<String, Object> map = new HashMap<>();
		try {

			List<BBPSTrxnDetails> list = new ArrayList<BBPSTrxnDetails>();
			List<BBPSTransactionResponse> listBBPSTransactionResponse = new ArrayList<BBPSTransactionResponse>();
			List<BBPSTrxnDetails> listTransaction = null;

			listTransaction = bbpsTrxnDetailsRepository.findBBPSDetailsByMerchantTrxnRefId(merchantTrxnRefId);

			AtomicInteger atomicInteger = new AtomicInteger(1);
			if (listTransaction.size() != 0) {
				listTransaction.forEach(objects -> {
					LOGGER.info("----Inside list-------:");
					DecimalFormat amountFormate = new DecimalFormat("#,##,##,##,###.00");

					try {
						String date = DateAndTime.dateFormatReports(objects.getTransactionDate().toString());

						String mobileNo = "NA";

						try {
							BBPSApiRequest apiRequest = bbpsApiRequestRepository
									.findByMerchantTrxnRefId(objects.getMerchantTransactionRefId());
							if (apiRequest != null) {
								String customerParam = apiRequest.getCustomerParams();

								org.json.JSONObject object2 = new org.json.JSONObject(customerParam);

								Iterator<String> keys = object2.keys();
								while (keys.hasNext()) {
									String keyValue = (String) keys.next();
									mobileNo = object2.getString(keyValue);

								}

							} else {
								mobileNo = "NA";
							}
						} catch (Exception e) {
							mobileNo = "NA";
						}

						BBPSTransactionResponse bbpsTransactionResponse = new BBPSTransactionResponse();
						bbpsTransactionResponse.setsNo(atomicInteger.getAndIncrement());
						bbpsTransactionResponse.setBbpsTransactionId(objects.getBbpsTransactionId());
						bbpsTransactionResponse.setMerchantId(objects.getMerchantId());
						bbpsTransactionResponse.setTransactionDate(date);
						bbpsTransactionResponse
								.setTransactionAmount(amountFormate.format(objects.getTransactionAmount()));
						bbpsTransactionResponse.setMerchantTransactionRefId(objects.getMerchantTransactionRefId());
						bbpsTransactionResponse.setMerchantServiceCharge(
								Double.parseDouble(amountFormate.format(objects.getMerchantServiceCharge())));
						bbpsTransactionResponse.setMerchantServiceCommision(objects.getMerchantServiceCommision());
						bbpsTransactionResponse.setIsSettled(objects.getIsSettled());
						bbpsTransactionResponse.setIsReconcile(objects.getIsReconcile());
						bbpsTransactionResponse.setTrxnId(objects.getTrxnId());
						bbpsTransactionResponse.setMobile(mobileNo);
						bbpsTransactionResponse.setServiceName(Encryption.decString(objects.getServiceName()));
						bbpsTransactionResponse.setResponseMessage(objects.getResponseMessage());
						bbpsTransactionResponse.setPaymentMode(objects.getPaymentMode());

						if (objects.getPaymentId().equals("")) {
							bbpsTransactionResponse.setPaymentId("NA");
						} else {
							bbpsTransactionResponse.setPaymentId(objects.getPaymentId());
						}

						bbpsTransactionResponse.setPaymentStatus(objects.getPaymentStatus());

						if (objects.getTransactionStatus().equals("SUCCESS")) {
							bbpsTransactionResponse.setTransactionStatus("Success");
						}
						if (objects.getTransactionStatus().equals("FAILURE")
								|| objects.getTransactionStatus().equals("FAIL")
								|| objects.getTransactionStatus().equals("FAILED")) {
							bbpsTransactionResponse.setTransactionStatus("Failed");
						}
						if (objects.getTransactionStatus().equals("PENDING")) {
							bbpsTransactionResponse.setTransactionStatus("Pending");
						}

						listBBPSTransactionResponse.add(bbpsTransactionResponse);
					} catch (ParseException e) {
						e.printStackTrace();
					}

				});

			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			}

			LOGGER.info("----list size-------:" + listBBPSTransactionResponse.size());

			if (listBBPSTransactionResponse.size() != 0) {
				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "BBPS Transaction List");
				map.put("callBackList", listBBPSTransactionResponse);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
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
	public Map<String, Object> updateTrxnIdentifierOnBBPSTrxnDeyails(String fromDate, String toDate) {
		Map<String, Object> map = new HashMap<>();
		try {

			String mobileNo = "NA";
			String startDate = fromDate + " 00:00:00";
			String endDate = toDate + " 23:59:59";

			List<BBPSApiRequest> bList = bbpsApiRequestRepository.findPostpaidandprepaidserviceDataByDate(startDate,
					endDate);

			if (bList.size() != 0) {
				for (BBPSApiRequest bbpsApiRequest : bList) {
					String merchantTrxnRefId = bbpsApiRequest.getMerchantTrxnRefId();

					if (!bbpsApiRequest.getCustomerParams().equals("NA")) {
						org.json.JSONObject object2 = new org.json.JSONObject(bbpsApiRequest.getCustomerParams());

						LOGGER.info("----MerchantTrxnRefId-------:" + bbpsApiRequest.getMerchantTrxnRefId());
						LOGGER.info("----CustomerParams-------:" + bbpsApiRequest.getCustomerParams());
						Iterator<String> keys = object2.keys();
						while (keys.hasNext()) {
							String keyValue = (String) keys.next();
							try {
								mobileNo = object2.getString(keyValue);
							} catch (Exception e) {
								mobileNo = "NA";
							}
						}

						BBPSTrxnDetails bbpsTrxnDetails = bbpsTrxnDetailsRepository
								.findByMerchantTrxnRefIdV2(merchantTrxnRefId);

						if (bbpsTrxnDetails != null && bbpsTrxnDetails.getTrxnIdentifier().equals("NA")) {
							bbpsTrxnDetails.setTrxnIdentifier(mobileNo);
							bbpsTrxnDetails = bbpsTrxnDetailsRepository.save(bbpsTrxnDetails);
						}
					} else {
						BBPSTrxnDetails bbpsTrxnDetails = bbpsTrxnDetailsRepository
								.findByMerchantTrxnRefIdV2(merchantTrxnRefId);
						bbpsTrxnDetails.setTrxnIdentifier(mobileNo);
						bbpsTrxnDetails = bbpsTrxnDetailsRepository.save(bbpsTrxnDetails);
					}
				}
				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "Trxn Identifier updated successfully");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);

			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
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
	public Map<String, Object> fetchAmountDetails(String merchantTrxnRefId) {
		Map<String, Object> map = new HashMap<>();
		try {

			if (!RegexValidator.checkAlphanumeric(merchantTrxnRefId)) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, "please pass valid Alphanumeric merchantTrxnRefId");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}

			if (!bbpsTrxnDetailsRepository.existsByPaymentId(merchantTrxnRefId)) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Transaction details not found.");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}

			BBPSTrxnDetails bbpsTrxnDetails = bbpsTrxnDetailsRepository.findByPaymentId(merchantTrxnRefId);
			PGTransactionDetail pgTrxnDetails = pgTransactionDetailRepository
					.findByMerchantTransactionRefId(Encryption.encString(merchantTrxnRefId));

			map.put("bbpsPlatformFee", Precision.round(bbpsTrxnDetails.getBbpsPlatformFee(), 2));
			map.put("bbpsTrxnAmount", Precision.round(bbpsTrxnDetails.getTransactionAmount(), 2));
			map.put("pgMerchantServiceCharge", Precision.round(pgTrxnDetails.getMerchantServiceCharge(), 2));
			map.put("pgTrxnAmount", Precision.round(pgTrxnDetails.getTransactionAmount(), 2));
			map.put("bbpsPaymentID", bbpsTrxnDetails.getPaymentId());
			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, "Transaction details found Successfully.");

		} catch (Exception e) {
			LOGGER.error("Error :{}", e.getStackTrace().toString());
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}

		return map;
	}

}