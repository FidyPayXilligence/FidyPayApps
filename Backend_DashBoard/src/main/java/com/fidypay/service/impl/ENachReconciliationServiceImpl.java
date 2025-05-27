package com.fidypay.service.impl;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fidypay.entity.ENachMerchantSettelment;
import com.fidypay.entity.ENachReconciliation;
import com.fidypay.repo.ENachReconciliationRepository;
import com.fidypay.repo.ENachSattelementRepo;
import com.fidypay.request.ENachReconciliationRequest;
import com.fidypay.request.ENachReconciliationRequestlist;
import com.fidypay.response.ENachMerchantSattelmentResponse;
import com.fidypay.response.ENachReconciliationListResponse;
import com.fidypay.response.ENachReconciliationReportPayLoad;
import com.fidypay.response.ENachSattelmentReportPayLoad;
import com.fidypay.service.ENachReconciliationService;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.DateAndTime;
import com.fidypay.utils.ex.DateUtil;

@Service
public class ENachReconciliationServiceImpl implements ENachReconciliationService {

	@Autowired
	private ENachReconciliationRepository enachreconciliationrepository;

	@Autowired
	private ENachSattelementRepo enachsattelementrepo;

	@Override
	public List<ENachReconciliationReportPayLoad> getENachReconciliationReportExcel(
			ENachReconciliationRequest enachreconciliationrequest, String cId) {

		Map<String, Object> map = new HashMap<>();
		List<ENachReconciliationReportPayLoad> activityList = new ArrayList<ENachReconciliationReportPayLoad>();
		try {

			List<ENachReconciliation> details = null;

			Long merchantId = Long.parseLong(cId);
			String startDate = enachreconciliationrequest.getFromDate() + " " + "00:00:00.0";
			String endDate = enachreconciliationrequest.getToDate() + " " + "23:59:59.9";

			details = enachreconciliationrepository.findByFromDateAndToDateAndMerchantId(startDate, endDate,
					merchantId);
			AtomicInteger atomicInteger = new AtomicInteger(1);
			if (details.size() != 0) {
				details.stream().forEach(objects -> {
					DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");
					try {
						String ReconciliationDate = DateAndTime
								.dateFormatReports(objects.getReconciliationDate().toString());
						String fromdate = DateAndTime.dateFormatReports(objects.getFromDate());
						String todate = DateAndTime.dateFormatReports(objects.getToDate());

						ENachReconciliationReportPayLoad enachreconciliationreportpayload = new ENachReconciliationReportPayLoad();

						enachreconciliationreportpayload.setsNo(atomicInteger.getAndIncrement());
						enachreconciliationreportpayload
								.setMerchantTransactionRefId(objects.getMerchantTransactionRefId());
						enachreconciliationreportpayload.setMerchantId(objects.getMerchantId());

						enachreconciliationreportpayload
								.setCollectionAmount(amount1.format(objects.getCollectionAmount()));
						enachreconciliationreportpayload
								.setPrincipalAmount(amount1.format(objects.getPrincipalAmount()));

						enachreconciliationreportpayload
								.setReconciliationAmount(amount1.format(objects.getReconciliationAmount()));

						enachreconciliationreportpayload.setFromDate(fromdate);
						enachreconciliationreportpayload.setToDate(todate);

						enachreconciliationreportpayload.setReconciliationDate(ReconciliationDate);

						enachreconciliationreportpayload.setIsVerified(String.valueOf(objects.getIsVerified()));

						enachreconciliationreportpayload.setReconciliationDetails(objects.getReconciliationDetails());

						activityList.add(enachreconciliationreportpayload);
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
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage().toString());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return activityList;
	}

	@Override
	public List<ENachSattelmentReportPayLoad> getENachSattelmentReportExcel(
			ENachReconciliationRequest enachreconciliationrequest, String cId) {
		System.out.println("Inside getENachSattelmentReportExcel");
		Map<String, Object> map = new HashMap<>();
		List<ENachSattelmentReportPayLoad> activityList = new ArrayList<ENachSattelmentReportPayLoad>();
		try {

			List<ENachMerchantSettelment> details = null;

			Long merchantId = Long.parseLong(cId);
			String startDate = enachreconciliationrequest.getFromDate() + " " + "00.00.00.0";
			String endDate = enachreconciliationrequest.getToDate() + " " + "23.59.59.9";

			details = enachsattelementrepo.findByFromDateAndToDateAndMerchantId(startDate, endDate, merchantId);
			AtomicInteger atomicInteger = new AtomicInteger(1);
			if (details.size() != 0) {
				details.forEach(objects -> {
					DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");
					try {
						String SettlementDate = DateAndTime.dateFormatReports(objects.getSettlementDate().toString());

						String fromdate = DateAndTime.dateFormatReports(objects.getFromDate());
						String todate = DateAndTime.dateFormatReports(objects.getToDate());

						ENachSattelmentReportPayLoad enachsattelmentreportpayload = new ENachSattelmentReportPayLoad();
						enachsattelmentreportpayload.setsNo(atomicInteger.getAndIncrement());
						enachsattelmentreportpayload.setIsVerfied(String.valueOf(objects.getIsVerfied()));
						enachsattelmentreportpayload.setMerchantTransactionRefId(objects.getMerchantTransactionRefId());
						enachsattelmentreportpayload.setSettlementDetails(objects.getSettlementDetails());
						enachsattelmentreportpayload.setMerchantId(objects.getMerchantId());
						enachsattelmentreportpayload.setSettlementAmount(amount1.format(objects.getSettlementAmount()));
						enachsattelmentreportpayload.setSettlementDate(SettlementDate.toString());
						enachsattelmentreportpayload.setFromDate(fromdate);
						enachsattelmentreportpayload.setToDate(todate);
						enachsattelmentreportpayload.setMerchantServiceId(objects.getMerchantServiceId());
						enachsattelmentreportpayload.setAmount(amount1.format(objects.getAmount()));
						enachsattelmentreportpayload.setServiceName(objects.getServiceName());
						enachsattelmentreportpayload.setTotalTransaction(objects.getTotalTransaction());
                        enachsattelmentreportpayload.setUtr(objects.getUtr());
						activityList.add(enachsattelmentreportpayload);
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
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage().toString());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return activityList;
	}

	@Override
	public Map<String, Object> findENachReconciliationDetails(
			ENachReconciliationRequestlist eNachReconciliationRequestlist, long merchantId) {
		Map<String, Object> map = new HashMap<>();

		try {

			String fromDate = eNachReconciliationRequestlist.getFromDate();
			String toDate = eNachReconciliationRequestlist.getToDate();

			Pageable pageble = PageRequest.of(eNachReconciliationRequestlist.getPageNo(),
					eNachReconciliationRequestlist.getPageSize(), Sort.by("RECONCILIATION_DATE").descending());

			if (DateUtil.isValidDateFormat(fromDate) == false || DateUtil.isValidDateFormat(toDate) == false) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.INVALID_DATE_FORMATE);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}

			if (DateUtil.isValidDateFormat(fromDate, toDate)) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.INVALID_FROM_TO_DATE);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}

			List<ENachReconciliation> list = new ArrayList<ENachReconciliation>();
			Page<ENachReconciliation> listTransaction = null;
			List<ENachReconciliationListResponse> ENachReconListResponse = new ArrayList<ENachReconciliationListResponse>();

			fromDate = fromDate + " 00.00.00.0";
			toDate = toDate + " 23.59.59.9";

			if (pageble != null) {
				listTransaction = enachreconciliationrepository.findBypageSizeANDFromDateAndToDate(pageble, fromDate,
						toDate, merchantId);

			}
			list = listTransaction.getContent();

			if (list.size() != 0) {
				list.forEach(objects -> {
					DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");

					try {
						String ReconciliationDate = DateAndTime
								.dateFormatReports(objects.getReconciliationDate().toString());

						String fromdate = DateAndTime.dateFormatReports(objects.getFromDate());
						String todate = DateAndTime.dateFormatReports(objects.getToDate());

						ENachReconciliationListResponse eNachReconciliationListResponse = new ENachReconciliationListResponse();
						eNachReconciliationListResponse.setMerchantId(objects.getMerchantId());
						eNachReconciliationListResponse
								.setCollectionAmount(amount1.format(objects.getCollectionAmount()));

						eNachReconciliationListResponse
								.setPrincipalAmount(amount1.format(objects.getPrincipalAmount()));

						eNachReconciliationListResponse
								.setReconciliationAmount(amount1.format(objects.getReconciliationAmount()));

						eNachReconciliationListResponse.setFromDate(fromdate);
						eNachReconciliationListResponse.setToDate(todate);

						eNachReconciliationListResponse.setReconciliationDate(ReconciliationDate.toString());

						eNachReconciliationListResponse.setIsVerified(objects.getIsVerified());
						eNachReconciliationListResponse.setReconciliationDetails(objects.getReconciliationDetails());
						ENachReconListResponse.add(eNachReconciliationListResponse);
					} catch (ParseException e) {

						e.printStackTrace();
					}
				});

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_FOUND);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put("totalItems", listTransaction.getTotalElements());
				map.put("totalPages", listTransaction.getTotalPages());
				map.put("data", ENachReconListResponse);
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
			}
		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage().toString());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

			e.printStackTrace();
		}
		return map;

	}

	@Override
	public Map<String, Object> findENachSettlmentDetails(ENachReconciliationRequestlist eNachReconciliationRequestlist,
			long merchantId) {
		Map<String, Object> map = new HashMap<>();

		try {

			String fromDate = eNachReconciliationRequestlist.getFromDate();
			String toDate = eNachReconciliationRequestlist.getToDate();

			Pageable pageble = PageRequest.of(eNachReconciliationRequestlist.getPageNo(),
					eNachReconciliationRequestlist.getPageSize());

			if (DateUtil.isValidDateFormat(fromDate) == false || DateUtil.isValidDateFormat(toDate) == false) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.INVALID_DATE_FORMATE);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}

			if (DateUtil.isValidDateFormat(fromDate, toDate)) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.INVALID_FROM_TO_DATE);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}

			fromDate = fromDate + " 00.00.00";
			toDate = toDate + " 23.59.59";

			List<ENachMerchantSettelment> list = new ArrayList<ENachMerchantSettelment>();
			Page<ENachMerchantSettelment> listTransaction = null;
			List<ENachMerchantSattelmentResponse> ENachSattelmentListResponse = new ArrayList<ENachMerchantSattelmentResponse>();

			if (pageble != null) {
				listTransaction = enachsattelementrepo.findBypageSizeANDFromDateAndToDate(pageble, fromDate, toDate,
						merchantId);

			}
			list = listTransaction.getContent();

			if (list.size() != 0) {
				list.forEach(objects -> {

					DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");
					try {
						String SettlementDate = DateAndTime.dateFormatReports(objects.getSettlementDate().toString());

						String fromdate = DateAndTime.dateFormatReports(objects.getFromDate());
						String todate = DateAndTime.dateFormatReports(objects.getToDate());

						ENachMerchantSattelmentResponse eNachMerchantSattelmentResponse = new ENachMerchantSattelmentResponse();
						eNachMerchantSattelmentResponse.setMerchantId(objects.getMerchantId());
						eNachMerchantSattelmentResponse.setFromDate(fromdate);
						eNachMerchantSattelmentResponse.setToDate(todate);
						eNachMerchantSattelmentResponse.setTotalAmount(amount1.format(objects.getAmount()));
						eNachMerchantSattelmentResponse.setIsVerfied(objects.getIsVerfied());
						eNachMerchantSattelmentResponse.setMerchantServiceId(objects.getMerchantServiceId());
						eNachMerchantSattelmentResponse
								.setSettlementAmount(amount1.format(objects.getSettlementAmount()));
						eNachMerchantSattelmentResponse.setSettlementDate(SettlementDate.toString());
						eNachMerchantSattelmentResponse.setSettlementDetails(objects.getSettlementDetails());
						eNachMerchantSattelmentResponse.setServiceName(objects.getServiceName());
						eNachMerchantSattelmentResponse.setTotalTransaction(objects.getTotalTransaction());
						eNachMerchantSattelmentResponse.setUtr(objects.getUtr());
						ENachSattelmentListResponse.add(eNachMerchantSattelmentResponse);
					} catch (ParseException e) {

						e.printStackTrace();
					}
				});

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_FOUND);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put("data", ENachSattelmentListResponse);
				map.put("totalItems", listTransaction.getTotalElements());
				map.put("totalPages", listTransaction.getTotalPages());
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
			}
		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage().toString());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

		}
		return map;

	}

}
