package com.fidypay.service.impl;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fidypay.encryption.Encryption;
import com.fidypay.entity.WealthTrxnDetails;
import com.fidypay.repo.FdReportRepository;
import com.fidypay.request.WealthReportRequest;
import com.fidypay.response.FdWealthTxnResponse;
import com.fidypay.response.WealthTrxnResponse;
import com.fidypay.service.FdReportService;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.DateAndTime;
import com.fidypay.utils.ex.DateUtil;

@Service
public class FdReportServiceImpl implements FdReportService {

	private static final Logger log = LoggerFactory.getLogger(FdReportServiceImpl.class);
	private final FdReportRepository fdReportRepository;

	public FdReportServiceImpl(FdReportRepository fdReportRepository) {
		this.fdReportRepository = fdReportRepository;
	}

	@Override
	public Map<String, Object> fetchWealthTxnReport(WealthReportRequest wealthReportRequest) {
		log.info("Service Request to fetchWealthTxnReport: {}");
		Map<String, Object> map = new HashMap<>();

		try {
			String startDate = wealthReportRequest.getStartDate();
			String endDate = wealthReportRequest.getEndDate();

			List<WealthTrxnDetails> list = new ArrayList<>();
			List<FdWealthTxnResponse> listFdTxnResponse = new ArrayList<>();
			Page<WealthTrxnDetails> listFdWealthTxn = null;
			Pageable pageable = PageRequest.of(wealthReportRequest.getPageNo(), wealthReportRequest.getPageSize(),
					Sort.by("date").descending());

			if ((startDate.equals("0") && endDate.equals("0")) || (startDate == "" && endDate == "")) {
				listFdWealthTxn = fdReportRepository.findAllWithPage(pageable);

			} else {

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

				startDate = startDate + " 00.00.00.0";
				endDate = endDate + " 23.59.59.9";
				String startTime = wealthReportRequest.getStartTime();
				String endTime = wealthReportRequest.getEndTime();

				if (startTime.equals("") || startTime.equals("0") || startTime.equals("null") || endTime.equals("null")
						|| endTime.equals("") || endTime.equals("0")) {
					startDate = startDate + " 00.00.00.0";
					endDate = endDate + " 23.59.59.9";
				} else {
					startDate = startDate + " " + startTime + ".00.0";
					endDate = endDate + " " + endTime + ".00.0";
				}
				listFdWealthTxn = fdReportRepository.findWealthTxnDetailsFromDateToDate(startDate, endDate, pageable);

			}
			list = listFdWealthTxn.getContent();
			AtomicInteger atomicInteger = new AtomicInteger(1);

			if (!list.isEmpty()) {

				list.forEach(objects -> {

					DecimalFormat invest = new DecimalFormat("#,##,##,##,###.00");
					String amount = invest.format(objects.getInvestmentAmount());
					String accountNumber = Encryption.decString(objects.getAccountNumber());
					log.info("accountNumber " + accountNumber);
				//	String accNum = "XXXXXXX" + accountNumber.substring(accountNumber.length() - 4);

					try {
						String date = DateAndTime.dateFormatReports(objects.getDate().toString());
						FdWealthTxnResponse fdWealthTxnResponse = new FdWealthTxnResponse();
						fdWealthTxnResponse.setsNo(atomicInteger.getAndIncrement());
						fdWealthTxnResponse.setWealthTxnId(objects.getWealthTrxnId().toString());
						fdWealthTxnResponse.setDate(date);
						fdWealthTxnResponse.setMerchantTrxnRefId(Encryption.decString(objects.getMerchantTrxnRefId()));
						fdWealthTxnResponse.setCustomerName(Encryption.decString(objects.getCustomerName()));
						fdWealthTxnResponse.setCustomerMobile(Encryption.decString(objects.getCustomerMobile()));
						fdWealthTxnResponse.setCustomerEmail(Encryption.decString(objects.getCustomerEmail()));
						fdWealthTxnResponse.setPaymentMode(Encryption.decString(objects.getPaymentMode()));
						fdWealthTxnResponse.setAccountNumber(Encryption.decString(accountNumber));
						fdWealthTxnResponse.setNomineeDetails(Encryption.decString(objects.getNomineeDetails()));
						fdWealthTxnResponse.setBankName(Encryption.decString(objects.getBankName()));
						fdWealthTxnResponse.setIfsc(Encryption.decString(objects.getIfsc()));
						fdWealthTxnResponse
								.setInterestPayoutFrequency(Encryption.decString(objects.getPayoutFrequency()));
						fdWealthTxnResponse.setTrxnId(Encryption.decString(objects.getTrxnId()));
						fdWealthTxnResponse.setFdResponseId(objects.getOthersDetails1());
						String uid = Encryption.decString(objects.getuId());
						if (uid != null && uid.length() >= 4) {

							fdWealthTxnResponse.setuId("XXXXXX" + uid.substring(uid.length() - 4));
						} else {
							fdWealthTxnResponse.setuId(uid);
						}

						fdWealthTxnResponse.setDob(objects.getDob());
						fdWealthTxnResponse.setGender(Encryption.decString(objects.getGender()));
						fdWealthTxnResponse.setInvestmentAmount(amount);
						fdWealthTxnResponse.setInvestmentPeriod(objects.getInvestmentPeriod());
						fdWealthTxnResponse.setInterestRate(objects.getInterestRate());
						String panNumber = Encryption.decString(objects.getPanNumber());
						if (panNumber != null && panNumber.length() >= 4) {
							String pN = Encryption.decString(panNumber);
							fdWealthTxnResponse.setPanNumber("XXXXXX" + pN.substring(pN.length() - 4));
						} else {
							fdWealthTxnResponse.setPanNumber(panNumber);
						}

						fdWealthTxnResponse.setPaymentTxId(Encryption.decString(objects.getPaymentTxId()));

						listFdTxnResponse.add(fdWealthTxnResponse);

					} catch (ParseException ex) {
						ex.printStackTrace();
					}
				});
				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "FD Txn Reports");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put(ResponseMessage.DATA, listFdTxnResponse);
				map.put("currentPage", listFdWealthTxn.getNumber());
				map.put("totalItems", listFdWealthTxn.getTotalElements());
				map.put("totalPages", listFdWealthTxn.getTotalPages());
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, "FD Reports not found");
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

	//
	@Override
	public Map<String, Object> findByTrxnId(String merchantTrxnRefId, long merchantId) {
		log.info("Service Request to findByTrxnId: {}", merchantTrxnRefId);
		Map<String, Object> map = new HashMap<String, Object>();

		WealthTrxnDetails txnDetails = fdReportRepository.findByTrxnId(merchantTrxnRefId);

		try {
			if (txnDetails != null) {

				WealthTrxnResponse wealthTrxnResponse = new WealthTrxnResponse();
				String date = DateAndTime.dateFormatReports(txnDetails.getDate().toString());

				wealthTrxnResponse.setWealthTrxnId(txnDetails.getWealthTrxnId());
				wealthTrxnResponse.setResponseId(txnDetails.getResponseId());
				wealthTrxnResponse.setDate(date);
				wealthTrxnResponse.setMerchantId(txnDetails.getMerchantId());
				wealthTrxnResponse.setMerchantTrxnRefId(txnDetails.getMerchantTrxnRefId());
				wealthTrxnResponse.setInvestmentAmount(txnDetails.getInvestmentAmount());
				wealthTrxnResponse.setTrxnId(txnDetails.getTrxnId());
				wealthTrxnResponse.setMerchantServiceId(txnDetails.getMerchantServiceId());
				wealthTrxnResponse.setCharges(txnDetails.getCharges());
				wealthTrxnResponse.setCommission(txnDetails.getCommission());
				wealthTrxnResponse.setCustomerName(txnDetails.getCustomerName());
				wealthTrxnResponse.setCustomerMobile(txnDetails.getCustomerMobile());
				wealthTrxnResponse.setCustomerEmail(txnDetails.getCustomerEmail());
				wealthTrxnResponse.setAddress1(txnDetails.getAddress1());
				wealthTrxnResponse.setAddress2(txnDetails.getAddress2());
				wealthTrxnResponse.setPaymentMode(txnDetails.getPaymentMode());
				wealthTrxnResponse.setAccountNumber(txnDetails.getAccountNumber());
				wealthTrxnResponse.setBankName(txnDetails.getBankName());
				wealthTrxnResponse.setIfsc(txnDetails.getIfsc());
				wealthTrxnResponse.setuId(txnDetails.getuId());
				wealthTrxnResponse.setPhotoUrl(txnDetails.getPhotoUrl());
				wealthTrxnResponse.setDob(txnDetails.getDob());
				wealthTrxnResponse.setGender(txnDetails.getGender());
				wealthTrxnResponse.setProviderUniqueId(txnDetails.getProviderUniqueId());
				wealthTrxnResponse.setInvestmentPeriod(txnDetails.getInvestmentPeriod());
				wealthTrxnResponse.setInterestRate(txnDetails.getInterestRate());
				wealthTrxnResponse.setPanNumber(txnDetails.getPanNumber());
				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_FOUND);
				map.put("data", wealthTrxnResponse);
				return map;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
		map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
		return map;
	}

	@Override
	public List<FdWealthTxnResponse> generateFdTxnReportExcel(WealthReportRequest wealthReportRequest,
			String clientId) {
		Map<String, Object> map = new HashMap<>();
		List<FdWealthTxnResponse> activityList = new ArrayList<FdWealthTxnResponse>();

		try {
			String startHours = null;
			String endHours = null;

			if (wealthReportRequest.getStartTime().equals("0") || wealthReportRequest.getStartTime() == "0"
					|| wealthReportRequest.getEndTime() == "0" || wealthReportRequest.getEndTime().equals("0")) {

				startHours = "00.00.00.0";
				endHours = "23.59.59.9";
			}
			Long merchantId = Long.parseLong(clientId);
			String startDate = wealthReportRequest.getStartDate() + " " + startHours;
			String endDate = wealthReportRequest.getEndDate() + " " + endHours;
			List<WealthTrxnDetails> list = null;

			list = fdReportRepository.findByStartDateAndEndDate(merchantId, startDate, endDate);

			AtomicInteger atomicInteger = new AtomicInteger(1);
			if (!list.isEmpty()) {
				list.forEach(objects -> {

					DecimalFormat invest = new DecimalFormat("#,##,##,##,###.00");
					String amount = invest.format(objects.getInvestmentAmount());
					String date;

					String accountNumber = Encryption.decString(objects.getAccountNumber());
					String accNum = "XXXXXXX" + accountNumber.substring(accountNumber.length() - 4);

					try {
						date = DateAndTime.dateFormatReports(objects.getDate().toString());

						FdWealthTxnResponse fdWealthTxnResponse = new FdWealthTxnResponse();

						fdWealthTxnResponse.setsNo(atomicInteger.getAndIncrement());
						fdWealthTxnResponse.setWealthTxnId(objects.getWealthTrxnId().toString());
						fdWealthTxnResponse.setDate(date);
						fdWealthTxnResponse.setMerchantTrxnRefId(Encryption.decString(objects.getMerchantTrxnRefId()));
						fdWealthTxnResponse.setCustomerName(Encryption.decString(objects.getCustomerName()));
						fdWealthTxnResponse.setCustomerMobile(Encryption.decString(objects.getCustomerMobile()));
						fdWealthTxnResponse.setCustomerEmail(Encryption.decString(objects.getCustomerEmail()));
						fdWealthTxnResponse.setPaymentMode(Encryption.decString(objects.getPaymentMode()));
						fdWealthTxnResponse.setAccountNumber(Encryption.decString(accNum));
						fdWealthTxnResponse.setIfsc(Encryption.decString(objects.getIfsc()));
						String uid = Encryption.decString(objects.getuId());
						if (uid != null && uid.length() >= 4) {

							fdWealthTxnResponse.setuId("XXXXXX" + uid.substring(uid.length() - 4));
						} else {
							fdWealthTxnResponse.setuId("NA");
						}
						fdWealthTxnResponse.setDob(Encryption.decString(objects.getDob()));
						fdWealthTxnResponse.setGender(Encryption.decString(objects.getGender()));
						fdWealthTxnResponse.setInvestmentAmount(amount);
						fdWealthTxnResponse.setInvestmentPeriod(objects.getInvestmentPeriod());
						fdWealthTxnResponse.setInterestRate(objects.getInterestRate());
						String panNumber = Encryption.decString(objects.getPanNumber());
						if (panNumber != null && panNumber.length() >= 4) {
							String pN = Encryption.decString(panNumber);
							fdWealthTxnResponse.setPanNumber("XXXXXX" + pN.substring(pN.length() - 4));
						} else {
							fdWealthTxnResponse.setPanNumber("NA");
						}
						if (objects.getPaymentTxId() != null) {
							fdWealthTxnResponse.setPaymentTxId(Encryption.decString(objects.getPaymentTxId()));
						} else {
							fdWealthTxnResponse.setPaymentTxId("NA");
						}

						activityList.add(fdWealthTxnResponse);

					} catch (Exception ex) {
						ex.printStackTrace();
					}
				});
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, "FD Trxn not found");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage().toString());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return activityList;
	}
}
