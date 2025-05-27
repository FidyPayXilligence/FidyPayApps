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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fidypay.encryption.Encryption;
import com.fidypay.entity.ENachTransactionDetails;
import com.fidypay.entity.MerchantService;
import com.fidypay.entity.ServiceCategory;
import com.fidypay.entity.ServiceInfo;
import com.fidypay.repo.ENachTransactionDetailsRepository;
import com.fidypay.repo.MerchantServiceRepository;
import com.fidypay.repo.ServiceCategoryRepository;
import com.fidypay.repo.ServiceInfoRepository;
import com.fidypay.request.ENachTxnInfoRequest;
import com.fidypay.request.PayoutTransactionRequest;
import com.fidypay.response.ENachAllResponse;
import com.fidypay.response.ENachTransactionResponse;
import com.fidypay.response.MerchantServicesPayload;
import com.fidypay.response.ServiceWiseTransactionResponse;
import com.fidypay.service.ENachTransactionDetailsService;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.DateAndTime;
import com.fidypay.utils.ex.DateUtil;

@Service
public class ENachTransactionDetailsServiceImpl implements ENachTransactionDetailsService {

	@Autowired
	private ENachTransactionDetailsRepository eNachTransactionDetailsRepository;

	@Autowired
	private MerchantServiceRepository merchantServiceRepository;

	@Autowired
	private ServiceInfoRepository serviceInfoRepository;

	@Autowired
	private ServiceCategoryRepository serviceCategoryRepository;

	@Override
	public Map<String, Object> eNachTotalTransactionAndTotalAmount(long merchantId) {
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

			totalTransaction = eNachTransactionDetailsRepository
					.findTotalTransactionsByMerchantIdAndStartAndEndDate(merchantId, startDate, endDate);

			amount = eNachTransactionDetailsRepository.findByMerchantIdAndStartAndEndDate(merchantId, startDate,
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
			map.put("totalTransactions", totalTransaction);
			map.put("totalAmount", tamount);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage().toString());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;
	}

	@Override
	public Map<String, Object> eNachTransactionList(PayoutTransactionRequest payoutTransactionRequest,
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

			List<ENachTransactionDetails> list = new ArrayList<ENachTransactionDetails>();
			List<ENachTransactionResponse> listPayoutTransactionResponse = new ArrayList<ENachTransactionResponse>();
			Page<ENachTransactionDetails> listTransaction = null;

			if (startTime.equals("") || startTime.equals("0") || startTime.equals("null") || endTime.equals("null")
					|| endTime.equals("") || endTime.equals("0")) {
				startDate = startDate + " 00.00.00.0";
				endDate = endDate + " 23.59.59.9";
			} else {
				startDate = startDate + " " + startTime + ".00.0";
				endDate = endDate + " " + endTime + ".00.0";
			}

			if ((merchantServiceId == null || merchantServiceId == 0) && (status == null || status == 0)) {
				listTransaction = eNachTransactionDetailsRepository.findByStartDateAndEndDate(merchantId, startDate,
						endDate, paging);
			} else if ((merchantServiceId != null || merchantServiceId != 0) && (status == null || status == 0)) {
				listTransaction = eNachTransactionDetailsRepository.findByStartDateAndEndDateANDService(merchantId,
						startDate, endDate, merchantServiceId, paging);
			} else if ((merchantServiceId == null || merchantServiceId == 0) && (status != null || status != 0)) {
				listTransaction = eNachTransactionDetailsRepository.findByStartDateAndEndDateANDStatus(merchantId,
						startDate, endDate, status, paging);
			} else if ((merchantServiceId != null || merchantServiceId != 0) && (status != null || status != 0)) {
				listTransaction = eNachTransactionDetailsRepository.findByStartDateAndEndDateANDStatusANDService(
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

						ENachTransactionResponse eNachTransactionResponse = new ENachTransactionResponse();

						System.out.println(
								"MerchantTrxnRefId: " + Encryption.decString(objects.getMerchantTransactionRefId()));
						System.out.println("Amount: " + objects.getTransactionAmount());

						eNachTransactionResponse.setsNo(atomicInteger.getAndIncrement());
						eNachTransactionResponse.seteNachTransactionId(objects.geteNachTransactionId().toString());
						eNachTransactionResponse.setTransactionDate(date);
						eNachTransactionResponse.setMerchantTransactionRefId(
								Encryption.decString(objects.getMerchantTransactionRefId()));
						eNachTransactionResponse.setTransactionAmount(txnAmount);
						eNachTransactionResponse.setServiceName(objects.getServiceName());
						eNachTransactionResponse.setTrxnRefId(Encryption.decString(objects.getTrxnRefId()));
						eNachTransactionResponse.setMandateId(Encryption.decString(objects.getMandateId()));
						eNachTransactionResponse.setCustomerId(Encryption.decString(objects.getCustomerId()));
						eNachTransactionResponse.setCustomerName(Encryption.decString(objects.getCustomerName()));
						eNachTransactionResponse.setUmrnNo(Encryption.decString(objects.geteNachUMRN()));
						eNachTransactionResponse.setRequestSource(objects.getRequestSource());
						eNachTransactionResponse.setBankId(objects.getBankId());
						eNachTransactionResponse
								.setCustomerBankAccountNo(Encryption.decString(objects.getCustomerBankAccountNumber()));
						eNachTransactionResponse
								.setCustomerBankIFSC(Encryption.decString(objects.getCustomerBankIfsc()));
						eNachTransactionResponse.setRemark(objects.getRemark());

						if (objects.getServiceName().equals("Debit Presentation")) {

							eNachTransactionResponse
									.setDebitDate(DateAndTime.getCurrentTimeInISTForDebitTrxn(objects.getType()));
						} else {
							eNachTransactionResponse.setDebitDate("NA");
						}

						if (objects.getServiceName().equals("Mandate Registrations")) {
							if (objects.getTransactionStatus().equals("Success")
									|| objects.getTransactionStatus().equals("Registered")) {
								eNachTransactionResponse.setTransactionStatus(ResponseMessage.REGISTERED);
							}
							if (objects.getTransactionStatus().equals("Fail")
									|| objects.getTransactionStatus().equals("Failed")) {
								eNachTransactionResponse.setTransactionStatus("Failed");
							}
							if (objects.getTransactionStatus().equals("Pending")) {
								eNachTransactionResponse.setTransactionStatus("Pending");
							}
							if (objects.getTransactionStatus().equals("Initiated")) {
								eNachTransactionResponse.setTransactionStatus(ResponseMessage.INITIATED);
							}
							if (objects.getTransactionStatus().equals("Cancelled")) {
								eNachTransactionResponse.setTransactionStatus("Cancelled");
							}
							if (objects.getTransactionStatus().equals("Cancel Initiated")) {
								eNachTransactionResponse.setTransactionStatus("Cancel Initiated");
							}
						} else {

							if (objects.getTransactionStatus().equals("Success")
									|| objects.getTransactionStatus().equals("Registered")) {
								eNachTransactionResponse.setTransactionStatus(ResponseMessage.STATUS_SUCCESS);
							}
							if (objects.getTransactionStatus().equals("Fail")
									|| objects.getTransactionStatus().equals("Failed")) {
								eNachTransactionResponse.setTransactionStatus("Failed");
							}
							if (objects.getTransactionStatus().equals("Pending")) {
								eNachTransactionResponse.setTransactionStatus("Pending");
							}
							if (objects.getTransactionStatus().equals("Initiated")) {
								eNachTransactionResponse.setTransactionStatus(ResponseMessage.INITIATED);
							}
							if (objects.getTransactionStatus().equals("Cancelled")) {
								eNachTransactionResponse.setTransactionStatus("Cancelled");
							}
							if (objects.getTransactionStatus().equals("Cancel Initiated")) {
								eNachTransactionResponse.setTransactionStatus("Cancel Initiated");
							}
						}

//						String sStatus = objects.getTransactionStatus();
//
//						switch (sStatus) {
//
//						case "Mandate Completed":
//							eNachTransactionResponse.setTransactionStatus("Success");
//							break;
//
//						case "Issue from sponsor bank":
//							eNachTransactionResponse.setTransactionStatus("Failed");
//							break;
//
//						case "In Progress":
//							eNachTransactionResponse.setTransactionStatus("Pending");
//							break;
//
//						case "Pending":
//							eNachTransactionResponse.setTransactionStatus("Pending");
//							break;
//							
//						case "Failed":
//							eNachTransactionResponse.setTransactionStatus("Failed");
//							break;	
//
//						case "Mandate Cancelled":
//							eNachTransactionResponse.setTransactionStatus("Failed");
//							break;
//
//						case "Mandate Rejected":
//							eNachTransactionResponse.setTransactionStatus("Failed");
//							break;
//
//						case "Mandate sign Esign mode then request send to NSDL/Sponsor Bank waiting for response":
//
//							eNachTransactionResponse.setTransactionStatus("Pending");
//							break;
//
//						case "Ack of submitted fil":
//							eNachTransactionResponse.setTransactionStatus("Pending");
//							break;
//
//						case "Mandate sign with EMandate(Debit,NetBanking,RealTime Aadhaar) and submmited to NPCI":
//							eNachTransactionResponse.setTransactionStatus("Pending");
//							break;
//
//						case "Mandate sign with Esign and submmited to NSDL":
//							eNachTransactionResponse.setTransactionStatus("Pending");
//							break;
//
//						case "NSDL end Failuer (Esign Mode)":
//							eNachTransactionResponse.setTransactionStatus("Failed");
//							break;
//
//						case "User cancelled mandate":
//							eNachTransactionResponse.setTransactionStatus("Failed");
//							break;
//
//						case "Session_Timeout Error":
//							eNachTransactionResponse.setTransactionStatus("Failed");
//							break;
//
//						default:
//
//							eNachTransactionResponse.setTransactionStatus("Pending");
//							break;
//						}

						listPayoutTransactionResponse.add(eNachTransactionResponse);

					} catch (ParseException e) {

						e.printStackTrace();
					}
				});

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "ENach Transaction List");
				map.put("callBackList", listPayoutTransactionResponse);
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

	@Override
	public Map<String, Object> eNachServicesList(long merchantId) {

		Map<String, Object> map = new HashMap<>();
		List<MerchantServicesPayload> servicesList = new ArrayList<MerchantServicesPayload>();

		try {
			List<MerchantService> list = merchantServiceRepository.findENachServicesByMerchantId(merchantId);

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
	public List<ENachTransactionResponse> getENachTransactionsStatementReportExcel(
			PayoutTransactionRequest transactionsReportRequest, String clientId) {
		Map<String, Object> map = new HashMap<>();
		List<ENachTransactionResponse> activityList = new ArrayList<ENachTransactionResponse>();
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
			List<ENachTransactionDetails> list = null;

			if ((merchantServiceId == null || merchantServiceId == 0) && (statusId == null || statusId == 0)) {
				list = eNachTransactionDetailsRepository.findByStartDateAndEndDate(merchantId, startDate, endDate);
			} else if ((merchantServiceId != null || merchantServiceId != 0) && (statusId == null || statusId == 0)) {
				list = eNachTransactionDetailsRepository.findByStartDateAndEndDateANDService(merchantId, startDate,
						endDate, merchantServiceId);
			} else if ((merchantServiceId == null || merchantServiceId == 0) && (statusId != null || statusId != 0)) {
				list = eNachTransactionDetailsRepository.findByStartDateAndEndDateANDStatus(merchantId, startDate,
						endDate, statusId);
			} else if ((merchantServiceId != null || merchantServiceId != 0) && (statusId != null || statusId != 0)) {
				list = eNachTransactionDetailsRepository.findByStartDateAndEndDateANDStatusANDService(merchantId,
						startDate, endDate, statusId, merchantServiceId);
			}

			AtomicInteger atomicInteger = new AtomicInteger(1);
			if (list.size() != 0) {

				list.forEach(objects -> {

					DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");
					String TxnAmount = amount1.format(objects.getTransactionAmount());

					String date;
					try {
						date = DateAndTime.dateFormatReports(objects.getTransactionDate().toString());

						ENachTransactionResponse eNachTransactionResponse = new ENachTransactionResponse();
						eNachTransactionResponse.setsNo(atomicInteger.getAndIncrement());
						eNachTransactionResponse.setTransactionDate(date);
						eNachTransactionResponse.setMerchantTransactionRefId(
								Encryption.decString(objects.getMerchantTransactionRefId()));
						eNachTransactionResponse.setTransactionAmount(TxnAmount);
						eNachTransactionResponse.setServiceName(objects.getServiceName());
						eNachTransactionResponse.setTrxnRefId(Encryption.decString(objects.getTrxnRefId()));
						eNachTransactionResponse.setMandateId(Encryption.decString(objects.getMandateId()));
						eNachTransactionResponse.setCustomerId(Encryption.decString(objects.getCustomerId()));
						eNachTransactionResponse.setCustomerName(Encryption.decString(objects.getCustomerName()));
						eNachTransactionResponse.setUmrnNo(Encryption.decString(objects.geteNachUMRN()));
						eNachTransactionResponse
								.setCharges(Double.parseDouble(amount1.format(objects.getMerchantServiceCharge())));
						eNachTransactionResponse.setIsReconcile(String.valueOf(objects.getIsReconcile()));
						eNachTransactionResponse.setIsSettled(String.valueOf(objects.getIsSettled()));
						eNachTransactionResponse.setRemark(objects.getRemark());
						eNachTransactionResponse
								.setCustomerBankAccountNo(Encryption.decString(objects.getCustomerBankAccountNumber()));
						eNachTransactionResponse
								.setCustomerBankIFSC(Encryption.decString(objects.getCustomerBankIfsc()));

						if (objects.getServiceName().equals("Debit Presentation")) {

							eNachTransactionResponse
									.setDebitDate(DateAndTime.getCurrentTimeInISTForDebitTrxn(objects.getType()));
						} else {
							eNachTransactionResponse.setDebitDate("NA");
						}

						if (objects.getServiceName().equals("Mandate Registrations")) {
							if (objects.getTransactionStatus().equals("Success")
									|| objects.getTransactionStatus().equals("Registered")) {
								eNachTransactionResponse.setTransactionStatus(ResponseMessage.REGISTERED);
							}
							if (objects.getTransactionStatus().equals("Fail")
									|| objects.getTransactionStatus().equals("Failed")) {
								eNachTransactionResponse.setTransactionStatus("Failed");
							}
							if (objects.getTransactionStatus().equals("Pending")) {
								eNachTransactionResponse.setTransactionStatus("Pending");
							}
							if (objects.getTransactionStatus().equals("Initiated")) {
								eNachTransactionResponse.setTransactionStatus(ResponseMessage.INITIATED);
							}
							if (objects.getTransactionStatus().equals("Cancelled")) {
								eNachTransactionResponse.setTransactionStatus("Cancelled");
							}
							if (objects.getTransactionStatus().equals("Cancel Initiated")) {
								eNachTransactionResponse.setTransactionStatus("Cancel Initiated");
							}

						} else {
							if (objects.getTransactionStatus().equals("Success")
									|| objects.getTransactionStatus().equals("Registered")) {
								eNachTransactionResponse.setTransactionStatus(ResponseMessage.STATUS_SUCCESS);
							}
							if (objects.getTransactionStatus().equals("Fail")
									|| objects.getTransactionStatus().equals("Failed")) {
								eNachTransactionResponse.setTransactionStatus("Failed");
							}
							if (objects.getTransactionStatus().equals("Pending")) {
								eNachTransactionResponse.setTransactionStatus("Pending");
							}
							if (objects.getTransactionStatus().equals("Initiated")) {
								eNachTransactionResponse.setTransactionStatus(ResponseMessage.INITIATED);
							}
							if (objects.getTransactionStatus().equals("Cancelled")) {
								eNachTransactionResponse.setTransactionStatus("Cancelled");
							}
							if (objects.getTransactionStatus().equals("Cancel Initiated")) {
								eNachTransactionResponse.setTransactionStatus("Cancel Initiated");
							}
						}
//
//						String sStatus = objects.getTransactionStatus();
//
//						switch (sStatus) {
//
//						case "Mandate Completed":
//							eNachTransactionResponse.setTransactionStatus("Success");
//							break;
//
//						case "Issue from sponsor bank":
//							eNachTransactionResponse.setTransactionStatus("Failed");
//							break;
//
//						case "In Progress":
//							eNachTransactionResponse.setTransactionStatus("Pending");
//							break;
//
//						case "Pending":
//							eNachTransactionResponse.setTransactionStatus("Pending");
//							break;
//							
//						case "Failed":
//							eNachTransactionResponse.setTransactionStatus("Failed");
//							break;	
//
//						case "Mandate Cancelled":
//							eNachTransactionResponse.setTransactionStatus("Failed");
//							break;
//
//						case "Mandate Rejected":
//							eNachTransactionResponse.setTransactionStatus("Failed");
//							break;
//
//						case "Mandate sign Esign mode then request send to NSDL/Sponsor Bank waiting for response":
//							eNachTransactionResponse.setTransactionStatus("Pending");
//							break;
//
//						case "Ack of submitted fil":
//							eNachTransactionResponse.setTransactionStatus("Pending");
//							break;
//
//						case "Mandate sign with EMandate(Debit,NetBanking,RealTime Aadhaar) and submmited to NPCI":
//							eNachTransactionResponse.setTransactionStatus("Pending");
//							break;
//
//						case "Mandate sign with Esign and submmited to NSDL":
//							eNachTransactionResponse.setTransactionStatus("Pending");
//							break;
//
//						case "NSDL end Failuer (Esign Mode)":
//							eNachTransactionResponse.setTransactionStatus("Failed");
//							break;
//
//						case "User cancelled mandate":
//							eNachTransactionResponse.setTransactionStatus("Failed");
//							break;
//
//						case "Session_Timeout Error":
//							eNachTransactionResponse.setTransactionStatus("Failed");
//							break;
//
//						default:
//
//							eNachTransactionResponse.setTransactionStatus("Pending");
//							break;
//						}
//
						activityList.add(eNachTransactionResponse);
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
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage().toString());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return activityList;

	}

	@Override
	public Map<String, Object> eNachServiceTransactionList(PayoutTransactionRequest payoutTransactionRequest,
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

				list = eNachSDateAndMerchantId(merchantId, startDate, endDate);

			} else if ((merchantServiceId != null || merchantServiceId != 0) && (status == null || status == 0)) {

				list = eNachSDateAndMerchantIdAndMerchantServiceId(merchantId, startDate, endDate, merchantServiceId);

			} else if ((merchantServiceId == null || merchantServiceId == 0) && (status != null || status != 0)) {

				list = eNachSDateAndMerchantIdAndStatusId(merchantId, startDate, endDate, status);

			} else if ((merchantServiceId != null || merchantServiceId != 0) && (status != null || status != 0)) {

				list = eNachSDateAndMerchantIdAndMServiceIdAndStatusID(merchantId, startDate, endDate,
						merchantServiceId, status);

			}

			if (list.size() != 0) {
				map.put("list", list);
				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "ENach Service  Transaction List");
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
	public List<ServiceWiseTransactionResponse> eNachSDateAndMerchantId(long merchantId, String from, String to) {

		List<ServiceWiseTransactionResponse> list = new ArrayList<>();

		List<?> list1 = eNachTransactionDetailsRepository.findByDateANDMerchantId(merchantId, from, to);
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

			List<?> list2 = eNachTransactionDetailsRepository.findByDateANDMerchantIdAndServiceName(merchantId, from,
					to, serviceName);

			for (Iterator<?> iterator2 = list2.iterator(); iterator2.hasNext();) {

				Object[] object2 = (Object[]) iterator2.next();
				String statusName = (String) object2[0];

				BigInteger statusWiseTotalTransaction = (BigInteger) object2[1];
				Integer statusWiseTotalTransactionInteger = statusWiseTotalTransaction.intValue();
				String stringTotalAmount = amountFormate.format((Double) object2[2]);

				if (statusName.equalsIgnoreCase("Success") || statusName.equalsIgnoreCase("Registered")) {
					serviceWiseTransactionResponse.setSuccessAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setSuccessTransaction(statusWiseTotalTransactionInteger);
				}
				if (statusName.equalsIgnoreCase("Pending")) {

					serviceWiseTransactionResponse.setPendingAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setPendingTransaction(statusWiseTotalTransactionInteger);
				}
				if (statusName.equalsIgnoreCase("Fail") || statusName.equalsIgnoreCase("Failed")
						|| statusName.equalsIgnoreCase("Failure")) {

					serviceWiseTransactionResponse.setFailedAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setFailedTransaction(statusWiseTotalTransactionInteger);
				}
				if (statusName.equalsIgnoreCase("Cancelled")) {

					serviceWiseTransactionResponse.setCancelAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setCancelTransaction(statusWiseTotalTransactionInteger);
				}
				if (statusName.equalsIgnoreCase("Cancel Initiated")) {

					serviceWiseTransactionResponse.setCancelAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setCancelTransaction(statusWiseTotalTransactionInteger);
				}

				if (statusName.equalsIgnoreCase("Initiated")) {

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
	public List<ServiceWiseTransactionResponse> eNachSDateAndMerchantIdAndMerchantServiceId(long merchantId,
			String startDate, String endDate, long merchantServiceId) {

		List<ServiceWiseTransactionResponse> list = new ArrayList<>();

		List<?> list1 = eNachTransactionDetailsRepository.findByDateANDMerchantIdANdMServiceId(merchantId, startDate,
				endDate, merchantServiceId);
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

			List<?> list2 = eNachTransactionDetailsRepository.findByDateANDMerchantIdAndServiceName(merchantId,
					startDate, endDate, serviceName);

			for (Iterator<?> iterator2 = list2.iterator(); iterator2.hasNext();) {

				Object[] object2 = (Object[]) iterator2.next();
				String statusName = (String) object2[0];
				BigInteger statusWiseTotalTransaction = (BigInteger) object2[1];
				Integer statusWiseTotalTransactionInteger = statusWiseTotalTransaction.intValue();
				String stringTotalAmount = amountFormate.format((Double) object2[2]);

				if (statusName.equalsIgnoreCase("Success") || statusName.equalsIgnoreCase("Registered")) {
					serviceWiseTransactionResponse.setSuccessAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setSuccessTransaction(statusWiseTotalTransactionInteger);
				}
				if (statusName.equalsIgnoreCase("Pending")) {

					serviceWiseTransactionResponse.setPendingAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setPendingTransaction(statusWiseTotalTransactionInteger);
				}
				if (statusName.equalsIgnoreCase("Fail") || statusName.equalsIgnoreCase("Failed")
						|| statusName.equalsIgnoreCase("Failure")) {

					serviceWiseTransactionResponse.setFailedAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setFailedTransaction(statusWiseTotalTransactionInteger);
				}
				if (statusName.equalsIgnoreCase("Cancelled")) {

					serviceWiseTransactionResponse.setCancelAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setCancelTransaction(statusWiseTotalTransactionInteger);
				}
				if (statusName.equalsIgnoreCase("Cancel Initiated")) {

					serviceWiseTransactionResponse.setCancelAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setCancelTransaction(statusWiseTotalTransactionInteger);
				}

				if (statusName.equalsIgnoreCase("Initiated")) {

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
	public List<ServiceWiseTransactionResponse> eNachSDateAndMerchantIdAndStatusId(long merchantId, String startDate,
			String endDate, long statusId) {

		List<ServiceWiseTransactionResponse> list = new ArrayList<>();

		List<?> list1 = eNachTransactionDetailsRepository.findByDateANDMerchantIdANdStatusId(merchantId, startDate,
				endDate, statusId);
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

			List<?> list2 = eNachTransactionDetailsRepository.findByDateANDMerchantIdAndServiceNameAndStatusId(
					merchantId, startDate, endDate, serviceName, statusId);

			for (Iterator<?> iterator2 = list2.iterator(); iterator2.hasNext();) {

				Object[] object2 = (Object[]) iterator2.next();
				String statusName = (String) object2[0];
				BigInteger statusWiseTotalTransaction = (BigInteger) object2[1];
				Integer statusWiseTotalTransactionInteger = statusWiseTotalTransaction.intValue();
				String stringTotalAmount = amountFormate.format((Double) object2[2]);

				if (statusName.equalsIgnoreCase("Success") || statusName.equalsIgnoreCase("Registered")) {
					serviceWiseTransactionResponse.setSuccessAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setSuccessTransaction(statusWiseTotalTransactionInteger);
				}
				if (statusName.equalsIgnoreCase("Pending")) {

					serviceWiseTransactionResponse.setPendingAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setPendingTransaction(statusWiseTotalTransactionInteger);
				}
				if (statusName.equalsIgnoreCase("Fail") || statusName.equalsIgnoreCase("Failed")
						|| statusName.equalsIgnoreCase("Failure")) {

					serviceWiseTransactionResponse.setFailedAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setFailedTransaction(statusWiseTotalTransactionInteger);
				}
				if (statusName.equalsIgnoreCase("Cancelled")) {

					serviceWiseTransactionResponse.setCancelAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setCancelTransaction(statusWiseTotalTransactionInteger);
				}
				if (statusName.equalsIgnoreCase("Cancel Initiated")) {

					serviceWiseTransactionResponse.setCancelAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setCancelTransaction(statusWiseTotalTransactionInteger);
				}

				if (statusName.equalsIgnoreCase("Initiated")) {

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
	public List<ServiceWiseTransactionResponse> eNachSDateAndMerchantIdAndMServiceIdAndStatusID(long merchantId,
			String startDate, String endDate, long merchantServiceId, long statusId) {

		List<ServiceWiseTransactionResponse> list = new ArrayList<>();

		List<?> list1 = eNachTransactionDetailsRepository.findByDateANDMerchantIdANdMServiceIdStatusId(merchantId,
				startDate, endDate, merchantServiceId, statusId);
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

			List<?> list2 = eNachTransactionDetailsRepository.findByDateANDMerchantIdAndServiceNameAndStatusId(
					merchantId, startDate, endDate, serviceName, statusId);

			for (Iterator<?> iterator2 = list2.iterator(); iterator2.hasNext();) {

				Object[] object2 = (Object[]) iterator2.next();
				String statusName = (String) object2[0];
				BigInteger statusWiseTotalTransaction = (BigInteger) object2[1];
				Integer statusWiseTotalTransactionInteger = statusWiseTotalTransaction.intValue();
				String stringTotalAmount = amountFormate.format((Double) object2[2]);

				if (statusName.equalsIgnoreCase("Success") || statusName.equalsIgnoreCase("Registered")) {
					serviceWiseTransactionResponse.setSuccessAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setSuccessTransaction(statusWiseTotalTransactionInteger);
				}
				if (statusName.equalsIgnoreCase("Pending")) {

					serviceWiseTransactionResponse.setPendingAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setPendingTransaction(statusWiseTotalTransactionInteger);
				}
				if (statusName.equalsIgnoreCase("Fail") || statusName.equalsIgnoreCase("Failed")
						|| statusName.equalsIgnoreCase("Failure")) {

					serviceWiseTransactionResponse.setFailedAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setFailedTransaction(statusWiseTotalTransactionInteger);
				}
				if (statusName.equalsIgnoreCase("Cancelled")) {

					serviceWiseTransactionResponse.setCancelAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setCancelTransaction(statusWiseTotalTransactionInteger);
				}
				if (statusName.equalsIgnoreCase("Cancel Initiated")) {

					serviceWiseTransactionResponse.setCancelAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setCancelTransaction(statusWiseTotalTransactionInteger);
				}

				if (statusName.equalsIgnoreCase("Initiated")) {

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

	// bharti
	@Override
	public Map<String, Object> findByENachTransactionId(long eNachTransactionId, long merchantId) {

		Map<String, Object> map = new HashMap<String, Object>();

		ENachTransactionDetails info = eNachTransactionDetailsRepository.findByENachTransactionId(eNachTransactionId);

		try {
			if (info != null) {

				ENachAllResponse response = new ENachAllResponse();
				String date = DateAndTime.dateFormatReports(info.getTransactionDate().toString());
				String mdate = DateAndTime.dateFormatReportsForFinal(info.getMandateActivationDate().toString());
				String mdate1 = DateAndTime.dateFormatReports(info.getMandateCancellationDate().toString());

				String finalDate = DateAndTime.dateFormatReportsForFinal(info.getFinalCollectionDate().toString());

				response.setMerchantId(info.getMerchantId());
				response.setMandateCancellationDate(mdate1);
				response.setMandateActivationDate(mdate);
				response.setTransactionAmount(String.valueOf(info.getTransactionAmount()));
				response.setTransactionStatus(info.getTransactionStatus());
				response.setBankId(info.getBankId());
				response.setCustomerAccountType(info.getCustomerAccountType());
				response.setFrequency(info.getFrequency());
				response.setInstrumentType(info.getInstrumentType());
				response.setIsReconcile(info.getIsReconcile());
				response.setIsSettled(info.getIsSettled());
				response.setTransactionDate(date);
				response.setRequestSource(info.getRequestSource());
				response.setCustomerBankAccountNumber(Encryption.decString(info.getCustomerBankAccountNumber()));
				response.setCustomerBankIfsc(Encryption.decString(info.getCustomerBankIfsc()));
				response.setCustomerBankName(Encryption.decString(info.getCustomerBankName()));
				response.setCustomerId(Encryption.decString(info.getCustomerId()));
				response.setCustomerName(Encryption.decString(info.getCustomerName()));
				response.setMandateId(Encryption.decString(info.getMandateId()));
				response.setMerchantTransactionRefId(Encryption.decString(info.getMerchantTransactionRefId()));
				response.seteNachId(Encryption.decString(info.geteNachId()));
				response.setTrxnRefId(Encryption.decString(info.getTrxnRefId()));
				response.setServiceProviderName(Encryption.decString(info.getServiceProviderName()));
				response.setMandateCancellationId(Encryption.decString(info.getMandateCancellationId()));
				response.setServiceProviderUtilityCode(Encryption.decString(info.getServiceProviderUtilityCode()));
				response.seteNachUMRN(Encryption.decString(info.geteNachUMRN()));
				response.setMerchantServiceCharge(String.valueOf(info.getMerchantServiceCharge()));
				response.setMerchantServiceCommision(String.valueOf(info.getMerchantServiceCommision()));
				response.setMerchantServiceId(String.valueOf(info.getMerchantServiceId()));
				response.setRemark(info.getRemark());
				response.setServiceName(info.getServiceName());
				response.setTransactionStatusId(String.valueOf(info.getTransactionStatusId()));
				response.setApiStatus(info.getApiStatus());
				response.setResponseId(info.getResponseId().toString());
				response.seteNachTransactionId(info.geteNachTransactionId().toString());
				response.setType(info.getType());
				response.setIsInitiated(info.getIsInitiated());
				response.setIsPending(info.getIsPending());
				response.setIsRegistered(info.getIsRegistered());
				response.setFinalCollectionDate(finalDate);
				response.setCategoryCode(info.getCategoryCode());
				response.setDebitType(info.getDebitType());
				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_FOUND);
				map.put("data", response);
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
	public Map<String, Object> findENachTransactionByFilter(ENachTxnInfoRequest eNachTxnInfoRequest) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {

			Pageable paging = PageRequest.of(eNachTxnInfoRequest.getPageNo(), eNachTxnInfoRequest.getPageSize(),
					Sort.by("TRANSACTION_DATE").descending());
			String mandateId = eNachTxnInfoRequest.getMandateId();
			String mobileNumber = eNachTxnInfoRequest.getMobileNumber();
			String utilityCode = eNachTxnInfoRequest.getServiceProviderUtilityCode();
			String customerAccountNumber = eNachTxnInfoRequest.getCustomerBankAccountNumber();

			List<ENachTransactionResponse> list1 = new ArrayList<ENachTransactionResponse>();
			List<ENachTransactionDetails> list = null;
			Page<ENachTransactionDetails> listTransaction = null;
			if ((!mandateId.equals("") || !mandateId.equals("0") || !mandateId.equals("NA"))
					&& (mobileNumber.equals("") || mobileNumber.equals("0") || mobileNumber.equals("NA"))
					&& (customerAccountNumber.equals("") || customerAccountNumber.equals("0")
							|| customerAccountNumber.equals("NA"))
					&& (utilityCode.equals("") || utilityCode.equals("0") || utilityCode.equals("NA"))) {
				listTransaction = eNachTransactionDetailsRepository.findByMandateId(mandateId, paging);
			}

			else if ((!mobileNumber.equals("") || !mobileNumber.equals("0") || !mobileNumber.equals("NA"))
					&& (mandateId.equals("") || mandateId.equals("0") || mandateId.equals("NA"))
					&& (customerAccountNumber.equals("") || customerAccountNumber.equals("0")
							|| customerAccountNumber.equals("NA"))
					&& (utilityCode.equals("") || utilityCode.equals("0") || utilityCode.equals("NA"))) {
				listTransaction = eNachTransactionDetailsRepository
						.findByMobileNumber(Encryption.encString(mobileNumber), paging);
			}

			else if ((!utilityCode.equals("") || !utilityCode.equals("0") || !utilityCode.equals("NA"))
					&& (mandateId.equals("") || mandateId.equals("0") || mandateId.equals("NA"))
					&& (customerAccountNumber.equals("") || customerAccountNumber.equals("0")
							|| customerAccountNumber.equals("NA"))
					&& (mobileNumber.equals("") || mobileNumber.equals("0") || mobileNumber.equals("NA"))) {
				listTransaction = eNachTransactionDetailsRepository.findByUtilityCode(Encryption.encString(utilityCode),
						paging);
			}

			else if ((!customerAccountNumber.equals("") || !customerAccountNumber.equals("0")
					|| !customerAccountNumber.equals("NA"))
					&& (mandateId.equals("") || mandateId.equals("0") || mandateId.equals("NA"))
					&& (mobileNumber.equals("") || mobileNumber.equals("0") || mobileNumber.equals("NA"))
					&& (utilityCode.equals("") || utilityCode.equals("0") || utilityCode.equals("NA"))) {
				listTransaction = eNachTransactionDetailsRepository
						.findByCustomerAccountNumber(Encryption.encString(customerAccountNumber), paging);
			}
			list = listTransaction.getContent();
			AtomicInteger atomicInteger = new AtomicInteger(1);

			System.out.println("list: " + list.size());
			if (list.size() != 0) {
				list.forEach(objects -> {

					DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");
					String txnAmount = amount1.format(objects.getTransactionAmount());

					try {
						String date = DateAndTime.dateFormatReports(objects.getTransactionDate().toString());

						ENachTransactionResponse eNachTransactionResponse = new ENachTransactionResponse();

						System.out.println(
								"MerchantTrxnRefId: " + Encryption.decString(objects.getMerchantTransactionRefId()));
						System.out.println("Amount: " + objects.getTransactionAmount());

						eNachTransactionResponse.setsNo(atomicInteger.getAndIncrement());
						eNachTransactionResponse.seteNachTransactionId(objects.geteNachTransactionId().toString());
						eNachTransactionResponse.setTransactionDate(date);
						eNachTransactionResponse.setMerchantTransactionRefId(
								Encryption.decString(objects.getMerchantTransactionRefId()));
						eNachTransactionResponse.setTransactionAmount(txnAmount);
						eNachTransactionResponse.setServiceName(objects.getServiceName());
						eNachTransactionResponse.setTrxnRefId(Encryption.decString(objects.getTrxnRefId()));
						eNachTransactionResponse.setMandateId(Encryption.decString(objects.getMandateId()));
						eNachTransactionResponse.setCustomerId(Encryption.decString(objects.getCustomerId()));
						eNachTransactionResponse.setCustomerName(Encryption.decString(objects.getCustomerName()));
						eNachTransactionResponse.setUmrnNo(Encryption.decString(objects.geteNachUMRN()));
						eNachTransactionResponse.setRequestSource(objects.getRequestSource());
						eNachTransactionResponse.setBankId(objects.getBankId());
						if (objects.getServiceName().equals("Mandate Registrations")) {

							if (objects.getTransactionStatus().equals("Success")
									|| objects.getTransactionStatus().equals("Registered")) {
								eNachTransactionResponse.setTransactionStatus(ResponseMessage.REGISTERED);
							}
							if (objects.getTransactionStatus().equals("Fail")
									|| objects.getTransactionStatus().equals("Failed")) {
								eNachTransactionResponse.setTransactionStatus("Failed");
							}
							if (objects.getTransactionStatus().equals("Pending")) {
								eNachTransactionResponse.setTransactionStatus("Pending");
							}
							if (objects.getTransactionStatus().equals("Initiated")) {
								eNachTransactionResponse.setTransactionStatus(ResponseMessage.INITIATED);
							}
							if (objects.getTransactionStatus().equals("Cancelled")) {
								eNachTransactionResponse.setTransactionStatus("Cancelled");
							}
						} else {
							if (objects.getTransactionStatus().equals("Success")
									|| objects.getTransactionStatus().equals("Registered")) {
								eNachTransactionResponse.setTransactionStatus(ResponseMessage.STATUS_SUCCESS);
							}
							if (objects.getTransactionStatus().equals("Fail")
									|| objects.getTransactionStatus().equals("Failed")) {
								eNachTransactionResponse.setTransactionStatus("Failed");
							}
							if (objects.getTransactionStatus().equals("Pending")) {
								eNachTransactionResponse.setTransactionStatus("Pending");
							}
							if (objects.getTransactionStatus().equals("Initiated")) {
								eNachTransactionResponse.setTransactionStatus(ResponseMessage.INITIATED);
							}
							if (objects.getTransactionStatus().equals("Cancelled")) {
								eNachTransactionResponse.setTransactionStatus("Cancelled");
							}
						}
						list1.add(eNachTransactionResponse);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				});
				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_FOUND);
				map.put("data", list1);
				map.put("currentPage", listTransaction.getNumber());
				map.put("totalItems", listTransaction.getTotalElements());
				map.put("totalPages", listTransaction.getTotalPages());
				return map;
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
				return map;
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage().toString());
		}
		return map;
	}

}