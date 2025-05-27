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
import org.springframework.stereotype.Service;

import com.fidypay.entity.EKYCReconciliation;
import com.fidypay.repo.EkycReconRepository;
import com.fidypay.request.ENachReconciliationRequestlist;
import com.fidypay.request.EkycReconciliationRequest;
import com.fidypay.response.EKYCReconciliationReportPayLoad;
import com.fidypay.response.EkycReconResponse;
import com.fidypay.service.EkycReconciliationService;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.DateAndTime;
import com.fidypay.utils.ex.DateUtil;

@Service
public class EkycReconciliationServiceImpl implements EkycReconciliationService {

	@Autowired
	private EkycReconRepository ekycReconRepo;

	@Override
	public Map<String, Object> findEkycReconciliationDetails(ENachReconciliationRequestlist eNachReconciliationrequest,
			long merchantId) throws Exception {
		Map<String, Object> mapObject = new HashMap<String, Object>();
		try {
			String fromDate = eNachReconciliationrequest.getFromDate();
			String toDate = eNachReconciliationrequest.getToDate();
			Pageable paging = PageRequest.of(eNachReconciliationrequest.getPageNo(),
					eNachReconciliationrequest.getPageSize());

			if (DateUtil.isValidDateFormat(fromDate) == false || DateUtil.isValidDateFormat(toDate) == false) {
				mapObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				mapObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.INVALID_DATE_FORMATE);
				mapObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return mapObject;
			}
			if (DateUtil.isValidDateFormat(fromDate, toDate)) {
				mapObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				mapObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.INVALID_FROM_TO_DATE);
				mapObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return mapObject;
			}
			List<EKYCReconciliation> ekycReconlist = new ArrayList<EKYCReconciliation>();
			Page<EKYCReconciliation> listTransaction = null;
			List<EkycReconResponse> response = new ArrayList<EkycReconResponse>();

			fromDate = fromDate + " 00.00.00.0";
			toDate = toDate + " 23.59.59.9";

			listTransaction = ekycReconRepo.findByFromDateAndToDateAndMerchantIdL(fromDate, toDate, merchantId, paging);

			ekycReconlist = listTransaction.getContent();

			if (ekycReconlist.size() > 0) {
				for (EKYCReconciliation p : ekycReconlist) {

					DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");
					try {
						String ReconciliationDate = DateAndTime.dateFormatReports(p.getReconciliationDate().toString());

						EkycReconResponse ekycReconResponse = new EkycReconResponse();
						ekycReconResponse.setMerchantId(p.getMerchantId());
						ekycReconResponse.setMerchantServiceId(p.getMerchantServiceId());
						ekycReconResponse.setFromDate(DateAndTime.convertDateTimeFormat(p.getFromDate()));
						ekycReconResponse.setToDate(DateAndTime.convertDateTimeFormat(p.getToDate()));
						ekycReconResponse.setReconciliationDate(ReconciliationDate.toString());
						ekycReconResponse.setReconciliationTotalAmount(
								Double.parseDouble(amount1.format(p.getReconciliationTotalAmount())));
						ekycReconResponse.setReconciliationTotalTrxn(p.getReconciliationTotalTrxn());
						ekycReconResponse.setServiceName(p.getServiceName());
						ekycReconResponse.setServiceProviderId(p.getServiceProviderId());
						ekycReconResponse.setTotalAmount(amount1.format(p.getTotalAmount()));
						ekycReconResponse.setTotalTrxn(p.getTotalTrxn());
						response.add(ekycReconResponse);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				mapObject.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				mapObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				mapObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_FOUND);
				mapObject.put("data", response);
				mapObject.put("totalItems", listTransaction.getTotalElements());
				mapObject.put("totalPages", listTransaction.getTotalPages());

			} else {
				mapObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				mapObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				mapObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
			}
		} catch (Exception e) {
			mapObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			mapObject.put(ResponseMessage.DESCRIPTION, e.getMessage().toString());
			mapObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			e.printStackTrace();
		}
		return mapObject;
	}

	@Override
	public List<EKYCReconciliationReportPayLoad> getEKYCReconciliationReportExcel(
			EkycReconciliationRequest ekycreconciliationrequest, String cId) {
		Map<String, Object> map = new HashMap<>();
		List<EKYCReconciliationReportPayLoad> activityList = new ArrayList<EKYCReconciliationReportPayLoad>();
		try {

			String startHours = null;
			String endHours = null;
			if (ekycreconciliationrequest.getStartTime().equals("0") || ekycreconciliationrequest.getStartTime() == "0"
					|| ekycreconciliationrequest.getEndTime() == "0"
					|| ekycreconciliationrequest.getEndTime().equals("0")) {

				startHours = "00.00.00.0";
				endHours = "23.59.59.9";
			}
			List<EKYCReconciliation> details = null;

			Long merchantId = Long.parseLong(cId);
			String startDate = ekycreconciliationrequest.getFromDate() + " " + startHours;
			String endDate = ekycreconciliationrequest.getToDate() + " " + endHours;

			details = ekycReconRepo.findByFromDateAndToDateAndMerchantId(startDate, endDate, merchantId);

			AtomicInteger atomicInteger = new AtomicInteger(1);
			if (details.size() != 0) {
				details.forEach(objects -> {
					DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");
					try {
						String ReconciliationDate = DateAndTime
								.dateFormatReports(objects.getReconciliationDate().toString());

						EKYCReconciliationReportPayLoad ekycreconciliationreportresponse = new EKYCReconciliationReportPayLoad();

						ekycreconciliationreportresponse.setsNo(atomicInteger.getAndIncrement());
						ekycreconciliationreportresponse
								.setFromDate(DateAndTime.convertDateTimeFormat(objects.getFromDate()));
						ekycreconciliationreportresponse
								.setToDate(DateAndTime.convertDateTimeFormat(objects.getToDate()));
						ekycreconciliationreportresponse.setReconciliationDate(ReconciliationDate.toString());
						ekycreconciliationreportresponse.setReconciliationTotalAmount(
								Double.parseDouble(amount1.format(objects.getReconciliationTotalAmount())));
						ekycreconciliationreportresponse
								.setReconciliationTotalTrxn(objects.getReconciliationTotalTrxn());
						ekycreconciliationreportresponse.setServiceName(objects.getServiceName());

						activityList.add(ekycreconciliationreportresponse);
					} catch (ParseException e) {

						e.printStackTrace();
					}
				});
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "Transaction not found");
			}

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage().toString());
		}
		return activityList;
	}

}
