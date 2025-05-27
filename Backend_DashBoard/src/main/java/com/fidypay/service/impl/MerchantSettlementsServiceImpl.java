package com.fidypay.service.impl;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fidypay.encryption.Encryption;
import com.fidypay.entity.MerchantSettlements;
import com.fidypay.repo.MerchantSettlementsRepository;
import com.fidypay.repo.MerchantSubMerchantInfoV2Repository;
import com.fidypay.repo.PayinTransactionDetailRepository;
import com.fidypay.request.MerchantSettlementRequest;
import com.fidypay.request.SettlementRequest;
import com.fidypay.response.SettlemenetReportResponse;
import com.fidypay.response.SettlementPayLoad;
import com.fidypay.service.MerchantSettlementsService;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.DateAndTime;
import com.fidypay.utils.ex.DateUtil;

@Service
public class MerchantSettlementsServiceImpl implements MerchantSettlementsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MerchantSettlementsServiceImpl.class);

	@Autowired
	private MerchantSettlementsRepository merchantSettlementsRepository;

	@Autowired
	PayinTransactionDetailRepository payinTransactionDetailRepository;

	@Autowired
	private MerchantSubMerchantInfoV2Repository merchantSubMerchantInfoRepository2;

	@Override
	public Map<String, Object> getSettlmentList(SettlementRequest settlementRequest, String clientId) {
		Map<String, Object> map = new HashMap<>();
		try {
			double settledAmount = 0.0;
			String settleMentAmount = null;
			double sAmount = 0.0;
			DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");
			Long merchantId = Long.valueOf(clientId);
			List<?> list = null;
			if (settlementRequest != null) {

				String startDate = settlementRequest.getFromDate() + " " + "00:00:00.0";
				String endDate = settlementRequest.getToDate() + " " + "23:59:59.9";

				if (settlementRequest.getFromDate() != null && !settlementRequest.getFromDate().equals("")
						&& settlementRequest.getToDate() != null && !settlementRequest.getToDate().equals("")
						&& (settlementRequest.getVpa() == null || settlementRequest.getVpa().equals(""))) {
					list = payinTransactionDetailRepository.findByTransactionDate(merchantId, startDate, endDate);
				} else {
					list = payinTransactionDetailRepository.findByTransactionDateANDVpa(merchantId,
							Encryption.encString(settlementRequest.getVpa()), startDate, endDate);
				}

				if (list.size() == 0) {
					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

				}

				else {
					List<SettlementPayLoad> activityList = new ArrayList<SettlementPayLoad>();

					Iterator<?> it = list.iterator();
					while (it.hasNext()) {
						Object[] object = (Object[]) it.next();

						amount1.setMinimumIntegerDigits(1);
						String tamount = amount1.format((Double) object[0]);
						String date = (String) object[2].toString();
						String vpa = Encryption.decString((String) object[3]);
						char isSettled = (char) object[4];
						String utr = Encryption.decString((String) object[5]);
						String merchantTrxnRefId = Encryption.decString((String) object[6]);
						String subMerchantBusinessName = (String) object[7];

						SettlementPayLoad payLoad = new SettlementPayLoad();

						payLoad.setMerchantTrxnRefId(merchantTrxnRefId);
						payLoad.setCustomerRefId(utr);

						payLoad.setAmount(tamount);
						// payLoad.setTransactionId(transactionId);
						payLoad.setDate(date);
						payLoad.setVpa(vpa);
						payLoad.setSubMerchantBussinessName(subMerchantBusinessName);

						if (isSettled == '1') {

							payLoad.setStatus("Completed");
						}
						if (isSettled == '0') {
							payLoad.setStatus("Pending");
						}

						activityList.add(payLoad);
						sAmount = (Double) object[0];

						settledAmount += sAmount;
						settleMentAmount = amount1.format(settledAmount);

					}
					int totalTransactions = list.size();
					map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_FOUND);
					map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
					map.put("transaction_statment", activityList);
					map.put("totalAmount", settleMentAmount);
					map.put("totalTransaction", totalTransactions);
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();

		}

		return map;
	}

	@Override
	public Map<String, Object> getMerchantSettlmentList(MerchantSettlementRequest merchantSettlementRequest,
			String clientId) {
		Map<String, Object> map = new HashMap<>();
		List<SettlemenetReportResponse> settlementList = new ArrayList<SettlemenetReportResponse>();
		List<MerchantSettlements> list = null;
		Page<MerchantSettlements> listTransaction = null;

		try {
			String startDate = merchantSettlementRequest.getFromDate();
			String endDate = merchantSettlementRequest.getToDate();
			String merchantVpa = merchantSettlementRequest.getVpa();
			String merchantStatus = merchantSettlementRequest.getStatus();
			Long merchantId = Long.valueOf(clientId);

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

			if (merchantSettlementRequest.getStartTime() != null && !merchantSettlementRequest.getStartTime().equals("")
					&& merchantSettlementRequest.getEndTime() != null
					&& !merchantSettlementRequest.getEndTime().equals("")) {

				startDate = merchantSettlementRequest.getFromDate() + " " + merchantSettlementRequest.getStartTime()
						+ ":00.0";
				endDate = merchantSettlementRequest.getToDate() + " " + merchantSettlementRequest.getEndTime()
						+ ":59.9";
			} else {
				startDate = merchantSettlementRequest.getFromDate() + " " + "00:00:00.0";
				endDate = merchantSettlementRequest.getToDate() + " " + "23:59:59.9";
			}

			int pageNo = merchantSettlementRequest.getPageNo();
			int pageSize = merchantSettlementRequest.getPageSize();

			if ((pageNo != 0 || pageNo == 0) && (pageSize != 0)) {
				Pageable paging = PageRequest.of(pageNo, pageSize);

				if ((merchantVpa.equals("") || merchantVpa.isEmpty() || merchantVpa.equals("NA"))
						&& (!merchantStatus.equals("") || !merchantStatus.isEmpty() || !merchantStatus.equals("NA"))) {
					listTransaction = merchantSettlementsRepository.findByMerchantIdAndSettlementDateAndStausWithPage(
							merchantId, startDate, endDate, merchantStatus, paging);
				} else if ((merchantStatus.equals("") || merchantStatus.isEmpty() || merchantStatus.equals("NA"))
						&& (!merchantVpa.equals("") || !merchantVpa.isEmpty() || !merchantVpa.equals("NA"))) {
					listTransaction = merchantSettlementsRepository.findByMerchantIdAndSettlementDateAndVpaWithPage(
							merchantId, startDate, endDate, merchantVpa, paging);
				} else if ((!merchantVpa.equals("") || !merchantVpa.isEmpty() || !merchantVpa.equals("NA"))
						&& (!merchantStatus.equals("") || !merchantStatus.isEmpty() || !merchantStatus.equals("NA"))) {
					listTransaction = merchantSettlementsRepository
							.findByMerchantIdAndSettlementDateAndVpaAndStatusWithPage(merchantId, startDate, endDate,
									merchantVpa, merchantStatus, paging);
				}
				if ((merchantVpa.equals("") || merchantVpa.isEmpty() || merchantVpa.equals("NA"))
						&& (merchantStatus.equals("") || merchantStatus.isEmpty() || merchantStatus.equals("NA"))) {
					listTransaction = merchantSettlementsRepository
							.findByMerchantIdAndSettlementDateWithPage(merchantId, startDate, endDate, paging);
				}

				list = listTransaction.getContent();
				if (!list.isEmpty()) {
					map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
					map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_FOUND);
					map.put("result", settlementList);
					map.put("totalTransactions", listTransaction.getTotalElements());
					map.put("totalPages", listTransaction.getTotalPages());
					map.put("currentPage", listTransaction.getNumber());
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				} else {
					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				}
			}

			else {

				if ((merchantVpa.equals("") || merchantVpa.isEmpty() || merchantVpa.equals("NA"))
						&& (!merchantStatus.equals("") || !merchantStatus.isEmpty() || !merchantStatus.equals("NA"))) {
					list = merchantSettlementsRepository.findByMerchantIdAndSettlementDateAndStatus(merchantId,
							startDate, endDate, merchantStatus);

					System.out.println(list.size());
				} else if ((merchantStatus.equals("") || merchantStatus.isEmpty() || merchantStatus.equals("NA"))
						&& (!merchantVpa.equals("") || !merchantVpa.isEmpty() || !merchantVpa.equals("NA"))) {
					list = merchantSettlementsRepository.findByMerchantIdAndSettlementDateAndVpa(merchantId, startDate,
							endDate, merchantVpa);
					System.out.println(list.size());
				} else if ((!merchantVpa.equals("") || !merchantVpa.isEmpty() || !merchantVpa.equals("NA"))
						&& (!merchantStatus.equals("") || !merchantStatus.isEmpty() || !merchantStatus.equals("NA"))) {
					list = merchantSettlementsRepository.findByMerchantIdAndSettlementDateAndVpaAndStatus(merchantId,
							startDate, endDate, merchantVpa, merchantStatus);
					System.out.println(list.size());
				}

				if ((merchantVpa.equals("") || merchantVpa.isEmpty() || merchantVpa.equals("NA"))
						&& (merchantStatus.equals("") || merchantStatus.isEmpty() || merchantStatus.equals("NA"))) {
					list = merchantSettlementsRepository.findByMerchantIdAndSettlementSDate(merchantId, startDate,
							endDate);
					System.out.println(list.size());
				}

				if (!list.isEmpty()) {
					map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
					map.put(ResponseMessage.DESCRIPTION, "Settlement list report");
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
					map.put("result", settlementList);
					map.put("totalItems", list.size());
					map.put("totalPages", 1);
				} else {
					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.DESCRIPTION, "Settlement data not available");
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				}
			}
			int i = 0;
			for (MerchantSettlements merchantSettlements : list) {
				i++;
				DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");

				String sattledate = DateAndTime.dateFormatReports(merchantSettlements.getSettlementDate().toString());
				String fromdate = DateAndTime.dateFormatReports(merchantSettlements.getSettlementFromDate().toString());
				String todate = DateAndTime.dateFormatReports(merchantSettlements.getSettlementToDate().toString());

				String vpa = merchantSettlements.getMerchantVPA();

				SettlemenetReportResponse response = new SettlemenetReportResponse();
				response.setsNo(i);
				response.setAmount(amount1.format(merchantSettlements.getSettlementNetAmount()));
				response.setIsSettlementVerified(merchantSettlements.getSettlementIsVerified());
				response.setMerchantBussinessName(merchantSettlements.getMerchantBussinessName());
				response.setMerchantId(merchantSettlements.getMerchants().getMerchantId());
				response.setMerchantSettlementId(merchantSettlements.getMerchantSettlementId());
				// response.setSettlementAmount(merchantSettlements.getSettlementGrossAmount());
				response.setSettlementDate(sattledate);
				response.setSettlementType(merchantSettlements.getSettlementType());

				List<?> listBank = merchantSubMerchantInfoRepository2.findByBankDetailByVPA(vpa);
				String bDetail = "NA";
				Iterator<?> it = listBank.iterator();
				while (it.hasNext()) {
					Object[] obj = (Object[]) it.next();
					bDetail = (String) obj[0];

				}

				{
					String subMerchantIfscCode = "NA";
					String subMerchantBankName = "NA";
					String subMerchantBankAccount = "NA";
					if (!bDetail.equals("NA")) {
						org.json.JSONObject bankDetails = new org.json.JSONObject(bDetail);

						subMerchantIfscCode = bankDetails.getString("subMerchantIfscCode");
						subMerchantBankName = bankDetails.getString("subMerchantBankName");
						subMerchantBankAccount = bankDetails.getString("subMerchantBankAccount");
					}
					String setDetails = merchantSettlements.getSettlementReportPassword();
					LOGGER.info("setDetails: " + setDetails);
					String trxn_id = "NA";
					if (setDetails.startsWith("{")) {

						JSONParser parser = new JSONParser();
						Object obj = parser.parse(setDetails);
						JSONObject settlementDetails = (JSONObject) obj;

						// double walletPreviousBalance = (double)
						// settlementDetails.get("walletPreviousBalance");
						if (settlementDetails.containsKey("yppReferenceNumber")) {
							trxn_id = (String) settlementDetails.get("yppReferenceNumber");

						}

						if (settlementDetails.containsKey("merchantTrxnRefId")) {
							trxn_id = (String) settlementDetails.get("merchantTrxnRefId");

						}

						String status = merchantSettlements.getSettlementStatus();
						response.setStatus(status);

						// response.setWalletBalance(walletPreviousBalance);
						response.setTrxnId(trxn_id);

					} else {
						// response.setWalletBalance(0.0);
						// response.setTrxnId(merchantSettlements.getSettlementDetails());
						response.setStatus("NA");

					}

					response.setSubMerchantBankAccount(subMerchantBankAccount);
					response.setSubMerchantBankName(subMerchantBankName);
					response.setSubMerchantBusinessName(merchantSettlements.getSubMerchantBussinessName());
					response.setSubMerchantIfscCode(subMerchantIfscCode);
					response.setVpa(vpa);
					response.setFromDate(fromdate);
					response.setToDate(todate);
					response.setTotalTransactions(merchantSettlements.getSettlementTotalTrxn());
					settlementList.add(response);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(" Exception " + e);
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}

		return map;
	}

	@Override
	public List<SettlemenetReportResponse> getMerchantSettlmentExcelReport(
			MerchantSettlementRequest merchantSettlementRequest, String clientId) {
		List<SettlemenetReportResponse> settlementList = new ArrayList<SettlemenetReportResponse>();
		List<MerchantSettlements> list = null;

		try {

			String startDate = merchantSettlementRequest.getFromDate();
			String endDate = merchantSettlementRequest.getToDate();
			String merchantVpa = merchantSettlementRequest.getVpa();
			String merchantStatus = merchantSettlementRequest.getStatus();
			Long merchantId = Long.valueOf(clientId);

			if (merchantSettlementRequest.getStartTime() != "0" && !merchantSettlementRequest.getStartTime().equals("0")
					&& merchantSettlementRequest.getEndTime() != "0"
					&& !merchantSettlementRequest.getEndTime().equals("0")) {

				startDate = merchantSettlementRequest.getFromDate() + " " + merchantSettlementRequest.getStartTime()
						+ ":00.0";
				endDate = merchantSettlementRequest.getToDate() + " " + merchantSettlementRequest.getEndTime()
						+ ":59.9";
			} else {
				startDate = merchantSettlementRequest.getFromDate() + " " + "00:00:00.0";
				endDate = merchantSettlementRequest.getToDate() + " " + "23:59:59.9";
			}

			if ((merchantVpa.equals("") || merchantVpa.isEmpty() || merchantVpa.equals("NA") || merchantVpa.equals("0"))
					&& (!merchantStatus.equals("") || !merchantStatus.isEmpty() || !merchantStatus.equals("NA")
							|| !merchantStatus.equals("0"))) {
				list = merchantSettlementsRepository.findByMerchantIdAndSettlementDateAndStatus(merchantId, startDate,
						endDate, merchantStatus);
				LOGGER.info("list " + list.size());
			} else if ((merchantStatus.equals("") || merchantStatus.isEmpty() || merchantStatus.equals("NA")
					|| merchantStatus.equals("0"))
					&& (!merchantVpa.equals("") || !merchantVpa.isEmpty() || !merchantVpa.equals("NA")
							|| !merchantVpa.equals("0"))) {
				list = merchantSettlementsRepository.findByMerchantIdAndSettlementDateAndVpa(merchantId, startDate,
						endDate, merchantVpa);
				LOGGER.info("list1 " + list.size());
			} else if ((!merchantVpa.equals("") || !merchantVpa.isEmpty() || !merchantVpa.equals("NA")
					|| !merchantVpa.equals("0"))
					&& (!merchantStatus.equals("") || !merchantStatus.isEmpty() || !merchantStatus.equals("NA")
							|| !merchantStatus.equals("0"))) {
				list = merchantSettlementsRepository.findByMerchantIdAndSettlementDateAndVpaAndStatus(merchantId,
						startDate, endDate, merchantVpa, merchantStatus);
				LOGGER.info("list2 " + list.size());
			}

			if ((merchantVpa.equals("") || merchantVpa.isEmpty() || merchantVpa.equals("NA") || merchantVpa.equals("0"))
					&& (merchantStatus.equals("") || merchantStatus.isEmpty() || merchantStatus.equals("NA")
							|| merchantStatus.equals("0"))) {
				list = merchantSettlementsRepository.findByMerchantIdAndSettlementSDate(merchantId, startDate, endDate);
				LOGGER.info("list3 " + list.size());

			}

			LOGGER.info("list " + list.size());

			String startDateVPA = startDate;
			String endDateVPA = endDate;

			LOGGER.info(" --- startDateVPA------------- " + startDateVPA);
			LOGGER.info(" --- endDateVPA------------- " + endDateVPA);
			int i = 0;

			for (MerchantSettlements merchantSettlements : list) {
				i++;
				DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");

				String sattledate = DateAndTime.dateFormatReports(merchantSettlements.getSettlementDate().toString());
				String fromdate = DateAndTime.dateFormatReports(merchantSettlements.getSettlementFromDate().toString());
				String todate = DateAndTime.dateFormatReports(merchantSettlements.getSettlementToDate().toString());

				String vpa = merchantSettlements.getMerchantVPA();

				SettlemenetReportResponse response = new SettlemenetReportResponse();
				response.setsNo(i);
				response.setAmount(amount1.format(merchantSettlements.getSettlementNetAmount()));
				response.setIsSettlementVerified(merchantSettlements.getSettlementIsVerified());
				response.setMerchantBussinessName(merchantSettlements.getMerchantBussinessName());
				response.setMerchantId(merchantSettlements.getMerchants().getMerchantId());
				response.setMerchantSettlementId(merchantSettlements.getMerchantSettlementId());
				// response.setSettlementAmount(merchantSettlements.getSettlementGrossAmount());
				response.setSettlementDate(sattledate);
				response.setSettlementType(merchantSettlements.getSettlementType());

				List<?> listBank = merchantSubMerchantInfoRepository2.findByBankDetailByVPA(vpa);
				String bDetail = "NA";
				Iterator<?> it = listBank.iterator();
				while (it.hasNext()) {
					Object[] obj = (Object[]) it.next();
					bDetail = (String) obj[0];

				}
				String subMerchantIfscCode = "NA";
				String subMerchantBankName = "NA";
				String subMerchantBankAccount = "NA";
				if (!bDetail.equals("NA")) {
					org.json.JSONObject bankDetails = new org.json.JSONObject(bDetail);

					subMerchantIfscCode = bankDetails.getString("subMerchantIfscCode");
					subMerchantBankName = bankDetails.getString("subMerchantBankName");
					subMerchantBankAccount = bankDetails.getString("subMerchantBankAccount");
				}
				String setDetails = merchantSettlements.getSettlementReportPassword();
				LOGGER.info("setDetails: " + setDetails);

				if (setDetails.startsWith("{")) {

					JSONParser parser = new JSONParser();
					Object obj = parser.parse(setDetails);
					JSONObject settlementDetails = (JSONObject) obj;

					// double walletPreviousBalance = (double)
					// settlementDetails.get("walletPreviousBalance");
					String trxn_id = (String) settlementDetails.get("yppReferenceNumber");
					LOGGER.info(" trxn_id : " + trxn_id);

					String status = merchantSettlements.getSettlementStatus();
					response.setStatus(status);
					LOGGER.info(" status : " + status);

					// response.setWalletBalance(walletPreviousBalance);
					response.setTrxnId(trxn_id);

				} else {
					// response.setWalletBalance(0.0);
					response.setTrxnId(merchantSettlements.getSettlementDetails());
					response.setStatus("NA");
				}
				response.setSubMerchantBankAccount(subMerchantBankAccount);
				response.setSubMerchantBankName(subMerchantBankName);
				response.setSubMerchantBusinessName(merchantSettlements.getSubMerchantBussinessName());
				response.setSubMerchantIfscCode(subMerchantIfscCode);
				response.setVpa(merchantSettlements.getMerchantVPA());
				response.setFromDate(fromdate);
				response.setToDate(todate);
				response.setTotalTransactions(merchantSettlements.getSettlementTotalTrxn());
				settlementList.add(response);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return settlementList;
	}

	@Override
	public Map<String, Object> getSettlementListCOOP(String startDate, String endDate, String vpa)
			throws ParseException {
		Map<String, Object> map = new HashMap<>();

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

		return null;
	}

}
