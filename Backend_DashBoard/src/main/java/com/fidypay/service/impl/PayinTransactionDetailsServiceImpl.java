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
import com.fidypay.entity.MerchantSubMerchantInfoV2;
import com.fidypay.entity.PayinTransactionalDetail;
import com.fidypay.entity.ServiceCategory;
import com.fidypay.entity.ServiceInfo;
import com.fidypay.repo.MerchantServiceRepository;
import com.fidypay.repo.MerchantSubMerchantInfoV2Repository;
import com.fidypay.repo.PayinTransactionDetailRepository;
import com.fidypay.repo.ServiceCategoryRepository;
import com.fidypay.repo.ServiceInfoRepository;
import com.fidypay.request.PayinTransactionRequest;
import com.fidypay.response.MerchantServicesPayload;
import com.fidypay.response.PayinTransactionResponse;
import com.fidypay.response.PayinTransactionsReportPayLoad;
import com.fidypay.response.ServiceWiseTransactionResponse;
import com.fidypay.response.SubMerchantPayload;
import com.fidypay.service.PayinTransactionDetailsService;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.DateAndTime;
import com.fidypay.utils.ex.DateUtil;

@Service
public class PayinTransactionDetailsServiceImpl implements PayinTransactionDetailsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PayinTransactionDetailsServiceImpl.class);

	@Autowired
	private PayinTransactionDetailRepository payinTransactionDetailRepository;

	@Autowired
	private MerchantSubMerchantInfoV2Repository merchantSubMerchantInfoV2Repository;

	@Autowired
	private MerchantServiceRepository merchantServiceRepository;

	@Autowired
	private ServiceInfoRepository serviceInfoRepository;

	@Autowired
	private ServiceCategoryRepository serviceCategoryRepository;

	@SuppressWarnings("null")
	@Override
	public Map<String, Object> payinTransactionList(PayinTransactionRequest payinTransactionRequest, Long merchantId) {
		Map<String, Object> map = new HashMap<>();
		try {
			Pageable paging = PageRequest.of(payinTransactionRequest.getPageNo(), payinTransactionRequest.getPageSize(),
					Sort.by("TRANSACTION_DATE").descending());

			String startDate = payinTransactionRequest.getStartDate();
			String endDate = payinTransactionRequest.getEndDate();
			Long merchantServiceId = payinTransactionRequest.getMerchantServiceId();
			Long status = payinTransactionRequest.getStatusId();
			String startTime = payinTransactionRequest.getStartTime();
			String endTime = payinTransactionRequest.getEndTime();
			String vpa = payinTransactionRequest.getVpa();

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

			List<PayinTransactionalDetail> list = new ArrayList<PayinTransactionalDetail>();
			List<PayinTransactionResponse> listPayinTransactionResponse = new ArrayList<PayinTransactionResponse>();
			Page<PayinTransactionalDetail> listTransaction = null;

			if (startTime.equals("") || startTime.equals("0") || startTime.equals("null") || endTime.equals("null")
					|| endTime.equals("") || endTime.equals("0")) {
				startDate = startDate + " 00.00.00.0";
				endDate = endDate + " 23.59.59.9";
			} else {
				startDate = startDate + " " + startTime + ".00.0";
				endDate = endDate + " " + endTime + ".00.0";
			}

			if ((merchantServiceId == null || merchantServiceId == 0) && (status == null || status == 0)
					&& (vpa.equals("null") || vpa.equals("") || vpa.equals("0"))) {
				LOGGER.info("Case 1");
				listTransaction = payinTransactionDetailRepository.findByStartDateAndEndDate(merchantId, startDate,
						endDate, paging);
			} else if ((merchantServiceId != null || merchantServiceId != 0) && (status == null || status == 0)
					&& (vpa.equals("null") || vpa.equals("") || vpa.equals("0"))) {
				LOGGER.info("Case 2");
				listTransaction = payinTransactionDetailRepository.findByStartDateAndEndDateANDService(merchantId,
						startDate, endDate, merchantServiceId, paging);
			} else if ((merchantServiceId == null || merchantServiceId == 0) && (status != null || status != 0)
					&& (vpa.equals("null") || vpa.equals("") || vpa.equals("0"))) {
				LOGGER.info("Case 3");
				listTransaction = payinTransactionDetailRepository.findByStartDateAndEndDateANDStatus(merchantId,
						startDate, endDate, status, paging);
			} else if ((merchantServiceId != null || merchantServiceId != 0) && (status != null || status != 0)
					&& (vpa.equals("null") || vpa.equals("") || vpa.equals("0"))) {
				LOGGER.info("Case 4");
				listTransaction = payinTransactionDetailRepository.findByStartDateAndEndDateANDStatusANDService(
						merchantId, startDate, endDate, status, merchantServiceId, paging);
			} else if ((merchantServiceId == null || merchantServiceId == 0) && (status == null || status == 0)
					&& (!vpa.equals("null") || !vpa.equals("") || !vpa.equals("0"))) {
				LOGGER.info("Case 5");
				listTransaction = payinTransactionDetailRepository.findByStartDateAndEndDateAndVpa(merchantId,
						startDate, endDate, Encryption.encString(vpa), paging);
			} else if ((merchantServiceId != null || merchantServiceId != 0) && (status == null || status == 0)
					&& (!vpa.equals("null") || !vpa.equals("") || !vpa.equals("0"))) {
				LOGGER.info("Case 6");
				listTransaction = payinTransactionDetailRepository.findByStartDateAndEndDateANDServiceAndVpa(merchantId,
						startDate, endDate, merchantServiceId, Encryption.encString(vpa), paging);
			} else if ((merchantServiceId == null || merchantServiceId == 0) && (status != null || status != 0)
					&& (!vpa.equals("null") || !vpa.equals("") || !vpa.equals("0"))) {
				LOGGER.info("Case 7");
				listTransaction = payinTransactionDetailRepository.findByStartDateAndEndDateANDStatusANDVPA(merchantId,
						startDate, endDate, status, Encryption.encString(vpa), paging);
			} else if ((merchantServiceId != null || merchantServiceId != 0) && (status != null || status != 0)
					&& (!vpa.equals("null") || !vpa.equals("") || !vpa.equals("0"))) {
				LOGGER.info("Case 8");
				listTransaction = payinTransactionDetailRepository.findByStartDateAndEndDateANDStatusANDServiceANDVPA(
						merchantId, startDate, endDate, status, merchantServiceId, Encryption.encString(vpa), paging);
			}

			AtomicInteger atomicInteger = new AtomicInteger(1);

			list = listTransaction.getContent();

			if (list.size() != 0) {
				list.forEach(objects -> {
					DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");
					String txnAmount = amount1.format(objects.getTransactionAmount());
					try {
						String date = DateAndTime.dateFormatReports(objects.getTransactionDate().toString());

						PayinTransactionResponse payinTransactionResponse = new PayinTransactionResponse();

						payinTransactionResponse.setsNo(atomicInteger.getAndIncrement());
						payinTransactionResponse.setPayinTransactionId(objects.getPayinTransactionId());
						payinTransactionResponse.setMerchantId(objects.getMerchantId());
						payinTransactionResponse.setMerchantServiceId(objects.getMerchantServiceId());
						payinTransactionResponse.setTransactionDate(date);
						payinTransactionResponse.setPgMerchantId(Encryption.decString(objects.getPgMerchantId()));
						payinTransactionResponse.setPayerVpa(Encryption.decString(objects.getPayerVpa()));
						payinTransactionResponse.setPayeeVpa(Encryption.decString(objects.getPayeeVpa()));
						payinTransactionResponse.setMerchantTransactionRefId(
								Encryption.decString(objects.getMerchantTransactionRefId()));
						payinTransactionResponse.setUtr(Encryption.decString(objects.getUtr()));
						payinTransactionResponse.setBankReferenceId(Encryption.decString(objects.getBankReferenceId()));
						payinTransactionResponse.setNpciReferenceId(Encryption.decString(objects.getNpciReferenceId()));
						payinTransactionResponse.setTransactionStatus(objects.getTransactionStatus());
						payinTransactionResponse.setBankSideStatus(objects.getBankSideStatus());
						payinTransactionResponse.setMerchantServiceCharge(
								Double.parseDouble(amount1.format(objects.getMerchantServiceCharge())));
						payinTransactionResponse.setMerchantServiceCommission(objects.getMerchantServiceCommission());
						payinTransactionResponse.setIsReconcile(objects.getIsReconcile());
						payinTransactionResponse.setIsSettled(objects.getIsSettled());
						payinTransactionResponse.setRemark(objects.getRemark());
						payinTransactionResponse.setRefundId(objects.getRefundId());
						payinTransactionResponse.setTransactionAmount(txnAmount);
						payinTransactionResponse.setMerchantBussinessName(objects.getMerchantBussinessName());
						payinTransactionResponse.setSubMerchantBussinessName(objects.getSubMerchantBussinessName());
						payinTransactionResponse.setServiceName(objects.getServiceName());
						payinTransactionResponse.setTrxnRefId(Encryption.decString(objects.getTrxnRefId()));
						payinTransactionResponse.setType(objects.getType());

						if (objects.getTransactionStatus().equals("Fail")
								|| objects.getTransactionStatus().equals("Failed")) {
							payinTransactionResponse.setTransactionStatus("Failed");
						} else {
							payinTransactionResponse.setTransactionStatus(objects.getTransactionStatus());
						}

						listPayinTransactionResponse.add(payinTransactionResponse);

					} catch (ParseException e) {

						e.printStackTrace();
					}

				});

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "Payin Transaction List");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put("callBackList", listPayinTransactionResponse);
				map.put("currentPage", listTransaction.getNumber());
				map.put("totalItems", listTransaction.getTotalElements());
				map.put("totalPages", listTransaction.getTotalPages());

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

		return map;
	}

	@SuppressWarnings("null")
	@Override
	public List<PayinTransactionsReportPayLoad> getPayinTransactionsStatementReportExcel(
			PayinTransactionRequest transactionsReportRequest, String clientId) {
		Map<String, Object> map = new HashMap<>();
		List<PayinTransactionsReportPayLoad> activityList = new ArrayList<PayinTransactionsReportPayLoad>();
		try {
			String startHours = null;
			String endHours = null;
			String vpa = transactionsReportRequest.getVpa();

			Long merchantServiceId = transactionsReportRequest.getMerchantServiceId();
			Long status = transactionsReportRequest.getStatusId();

			if (transactionsReportRequest.getStartTime().equals("0") || transactionsReportRequest.getStartTime() == "0"
					|| transactionsReportRequest.getEndTime() == "0"
					|| transactionsReportRequest.getEndTime().equals("0")) {

				startHours = "00.00.00.0";
				endHours = "23.59.59.9";
			}

			List<PayinTransactionalDetail> details = null;

			Long merchantId = Long.parseLong(clientId);
			String startDate = transactionsReportRequest.getStartDate() + " " + startHours;
			String endDate = transactionsReportRequest.getEndDate() + " " + endHours;

			if ((merchantServiceId == 0 || merchantServiceId == null) && (status == null || status == 0)
					&& (vpa.equals("null") || vpa.equals("") || vpa.equals("0"))) {
				LOGGER.info("Case 1");
				details = payinTransactionDetailRepository.findByStartDateAndEndDateWithoutPage(merchantId, startDate,
						endDate);
			} else if ((merchantServiceId != null || merchantServiceId != 0) && (status == null || status == 0)
					&& (vpa.equals("null") || vpa.equals("") || vpa.equals("0"))) {
				LOGGER.info("Case 2");
				details = payinTransactionDetailRepository.findByStartDateAndEndDateANDServiceWithoutPage(merchantId,
						startDate, endDate, merchantServiceId);
			} else if ((merchantServiceId == null || merchantServiceId == 0) && (status != null || status != 0)
					&& (vpa.equals("null") || vpa.equals("") || vpa.equals("0"))) {
				LOGGER.info("Case 3");
				details = payinTransactionDetailRepository.findByStartDateAndEndDateANDStatusWithoutPage(merchantId,
						startDate, endDate, status);
			} else if ((merchantServiceId != null || merchantServiceId != 0) && (status != null || status != 0)
					&& (vpa.equals("null") || vpa.equals("") || vpa.equals("0"))) {
				LOGGER.info("Case 4");
				details = payinTransactionDetailRepository.findByStartDateAndEndDateANDStatusANDServiceWithoutPage(
						merchantId, startDate, endDate, status, merchantServiceId);
			} else if ((merchantServiceId == null || merchantServiceId == 0) && (status == null || status == 0)
					&& (!vpa.equals("null") || !vpa.equals("") || !vpa.equals("0"))) {
				LOGGER.info("Case 5");
				details = payinTransactionDetailRepository.findByStartDateAndEndDateWithoutPageAndVpa(merchantId,
						startDate, endDate, Encryption.encString(vpa));
			} else if ((merchantServiceId != null || merchantServiceId != 0) && (status == null || status == 0)
					&& (!vpa.equals("null") || !vpa.equals("") || !vpa.equals("0"))) {
				LOGGER.info("Case 6");
				details = payinTransactionDetailRepository.findByStartDateAndEndDateANDServiceWithoutPageAndVpa(
						merchantId, startDate, endDate, merchantServiceId, Encryption.encString(vpa));
			} else if ((merchantServiceId == null || merchantServiceId == 0) && (status != null || status != 0)
					&& (!vpa.equals("null") || !vpa.equals("") || !vpa.equals("0"))) {
				LOGGER.info("Case 7");
				details = payinTransactionDetailRepository.findByStartDateAndEndDateANDStatusWithoutPageANDVPA(
						merchantId, startDate, endDate, status, Encryption.encString(vpa));
			} else if ((merchantServiceId != null || merchantServiceId != 0) && (status != null || status != 0)
					&& (!vpa.equals("null") || !vpa.equals("") || !vpa.equals("0"))) {
				LOGGER.info("Case 8");
				details = payinTransactionDetailRepository
						.findByStartDateAndEndDateANDStatusANDServiceWithoutPageANDVPA(merchantId, startDate, endDate,
								status, merchantServiceId, Encryption.encString(vpa));
			}

			AtomicInteger atomicInteger = new AtomicInteger(1);

			if (details.size() != 0) {
				details.forEach(objects -> {

					DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");
					try {
						String date = DateAndTime.dateFormatReports(objects.getTransactionDate().toString());

						PayinTransactionsReportPayLoad payinTransactionResponse = new PayinTransactionsReportPayLoad();
						payinTransactionResponse.setsNo(atomicInteger.getAndIncrement());
						payinTransactionResponse.setPayinTransactionId(objects.getPayinTransactionId());
						payinTransactionResponse.setMerchantId(objects.getMerchantId());
						payinTransactionResponse.setMerchantServiceId(objects.getMerchantServiceId());
						payinTransactionResponse.setTransactionDate(date);
						payinTransactionResponse.setPgMerchantId(Encryption.decString(objects.getPgMerchantId()));
						payinTransactionResponse.setPayerVpa(Encryption.decString(objects.getPayerVpa()));
						payinTransactionResponse.setPayeeVpa(Encryption.decString(objects.getPayeeVpa()));
						payinTransactionResponse.setMerchantTransactionRefId(
								Encryption.decString(objects.getMerchantTransactionRefId()));
						payinTransactionResponse.setUtr(Encryption.decString(objects.getUtr()));
						payinTransactionResponse.setBankReferenceId(Encryption.decString(objects.getBankReferenceId()));
						payinTransactionResponse.setNpciReferenceId(Encryption.decString(objects.getNpciReferenceId()));
						payinTransactionResponse.setTransactionStatus(objects.getTransactionStatus());
						payinTransactionResponse.setBankSideStatus(objects.getBankSideStatus());
						payinTransactionResponse
								.setCharge(Double.parseDouble(amount1.format(objects.getMerchantServiceCharge())));
						payinTransactionResponse.setIsReconcile(String.valueOf(objects.getIsReconcile()));
						payinTransactionResponse.setIsSettled(String.valueOf(objects.getIsSettled()));
						payinTransactionResponse.setRemark(objects.getRemark());
						payinTransactionResponse.setRefundId(objects.getRefundId());
						payinTransactionResponse.setTransactionAmount(amount1.format(objects.getTransactionAmount()));
						payinTransactionResponse.setMerchantBussinessName(objects.getMerchantBussinessName());
						payinTransactionResponse.setSubMerchantBussinessName(objects.getSubMerchantBussinessName());
						payinTransactionResponse.setServiceName(objects.getServiceName());
						payinTransactionResponse.setTrxnRefId(Encryption.decString(objects.getTrxnRefId()));
						payinTransactionResponse.setType(objects.getType());
						payinTransactionResponse.setCharges(objects.getMerchantServiceCharge());

						if (objects.getTransactionStatus().equals("Fail")
								|| objects.getTransactionStatus().equals("Failed")) {
							payinTransactionResponse.setTransactionStatus("Failed");
						} else {
							payinTransactionResponse.setTransactionStatus(objects.getTransactionStatus());
						}

						activityList.add(payinTransactionResponse);

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
	public Map<String, Object> payinTotalTransactionAndTotalAmount(long merchantId) {
		Map<String, Object> map = new HashMap<>();
		try {
			Date date = new Date();
			SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
			format1.setTimeZone(TimeZone.getTimeZone("IST"));
			String date1 = format1.format(date);

			Double amount = 0.0;
			Integer totalTransaction = 0;
			String tamount = "0";

			String startDate = date1 + " 00:00:00";
			String endDate = date1 + " 23:59:59";
			LOGGER.info("startDate: " + startDate);
			LOGGER.info("endDate: " + endDate);

			totalTransaction = payinTransactionDetailRepository
					.findTotalTransactionsByMerchantIdAndStartAndEndDate(merchantId, startDate, endDate);
			LOGGER.info("totalTransaction: " + totalTransaction);

			amount = payinTransactionDetailRepository.findByMerchantIdAndStartAndEndDate(merchantId, startDate,
					endDate);
			LOGGER.info("totalAmount: " + amount);

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
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage().toString());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;

	}

	@Override
	public Map<String, Object> getSubMerchantVpaList(String clientId) {

		Map<String, Object> map = new HashMap<>();
		List<SubMerchantPayload> merchantPayloads = new ArrayList<SubMerchantPayload>();
		List<MerchantSubMerchantInfoV2> merchantInfoV2s = null;
		long merchantId = Long.parseLong(clientId);
		if (merchantId == 0) {
			merchantInfoV2s = merchantSubMerchantInfoV2Repository.findAll();
		} else {
			merchantInfoV2s = merchantSubMerchantInfoV2Repository.findVpaListByMerchantId(merchantId);
		}

		for (MerchantSubMerchantInfoV2 merchantSubMerchantInfoV2 : merchantInfoV2s) {
			SubMerchantPayload merchantPayload = new SubMerchantPayload();
			merchantPayload.setSubMerchantVpa(merchantSubMerchantInfoV2.getSubMerchantAdditionalInfo());
			merchantPayload.setBussinessName(merchantSubMerchantInfoV2.getSubMerchantBussinessName().toUpperCase());
			merchantPayloads.add(merchantPayload);
		}
		Collections.sort(merchantPayloads);
		if (merchantInfoV2s.size() > 0) {

			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, "SubMerchant vpa list");
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			map.put("vpaList", merchantPayloads);

		} else {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, "VPA List not found");
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;
	}

	@Override
	public Map<String, Object> payinServicesList(long merchantId) {
		Map<String, Object> map = new HashMap<>();
		List<MerchantServicesPayload> servicesList = new ArrayList<MerchantServicesPayload>();

		try {
			List<MerchantService> list = merchantServiceRepository.findPayinServicesByMerchantId(merchantId);

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
	public Map<String, Object> payinServiceTransactionList(PayinTransactionRequest payinTransactionRequest,
			Long merchantId) {
		Map<String, Object> map = new HashMap<>();
		try {

			String startDate = payinTransactionRequest.getStartDate();
			String endDate = payinTransactionRequest.getEndDate();
			Long merchantServiceId = payinTransactionRequest.getMerchantServiceId();
			Long status = payinTransactionRequest.getStatusId();
			String startTime = payinTransactionRequest.getStartTime();
			String endTime = payinTransactionRequest.getEndTime();
			String vpa = payinTransactionRequest.getVpa();

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

			if ((merchantServiceId == null || merchantServiceId == 0) && (status == null || status == 0)
					&& (vpa.equals("null") || vpa.equals("") || vpa.equals("0"))) {

				list = payinSDateAndMerchantId(merchantId, startDate, endDate);

			} else if ((merchantServiceId != null || merchantServiceId != 0) && (status == null || status == 0)
					&& (vpa.equals("null") || vpa.equals("") || vpa.equals("0"))) {

				list = payinSDateAndMerchantIdAndMServiceId(merchantId, startDate, endDate, merchantServiceId);

			} else if ((merchantServiceId == null || merchantServiceId == 0) && (status != null || status != 0)
					&& (vpa.equals("null") || vpa.equals("") || vpa.equals("0"))) {

				list = payinSDateAndMerchantIdAndStatusId(merchantId, startDate, endDate, status);

			} else if ((merchantServiceId != null || merchantServiceId != 0) && (status != null || status != 0)
					&& (vpa.equals("null") || vpa.equals("") || vpa.equals("0"))) {

				list = payinSDateAndMerchantIdAndMServiceIdAndStatusId(merchantId, startDate, endDate,
						merchantServiceId, status);

			} else if ((merchantServiceId == null || merchantServiceId == 0) && (status == null || status == 0)
					&& (!vpa.equals("null") || !vpa.equals("") || !vpa.equals("0"))) {
				vpa = Encryption.encString(vpa);
				list = payinSDateAndMerchantIdAndVpa(merchantId, startDate, endDate, vpa);

			} else if ((merchantServiceId != null || merchantServiceId != 0) && (status == null || status == 0)
					&& (!vpa.equals("null") || !vpa.equals("") || !vpa.equals("0"))) {

				vpa = Encryption.encString(vpa);
				list = payinSDateAndMerchantIdAndMServiceIdAndVpa(merchantId, startDate, endDate, merchantServiceId,
						vpa);

			} else if ((merchantServiceId == null || merchantServiceId == 0) && (status != null || status != 0)
					&& (!vpa.equals("null") || !vpa.equals("") || !vpa.equals("0"))) {

				vpa = Encryption.encString(vpa);
				list = payinSDateAndMerchantIdAndStatusIdAndVpa(merchantId, startDate, endDate, status, vpa);

			} else if ((merchantServiceId != null || merchantServiceId != 0l) && (status != null || status != 0)
					&& (!vpa.equals("null") || !vpa.equals("") || !vpa.equals("0"))) {

				vpa = Encryption.encString(vpa);
				list = payinSDateAndMerchantIdAndStatusIdAndMServiceIdAndVpa(merchantId, startDate, endDate, status,
						merchantServiceId, vpa);

			}

			if (list.size() != 0) {
				map.put("list", list);
				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "Payin Service  Transaction List");
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
	public List<ServiceWiseTransactionResponse> payinSDateAndMerchantId(long merchantId, String from, String to) {

		List<ServiceWiseTransactionResponse> list = new ArrayList<>();
		List<?> list1 = payinTransactionDetailRepository.findByDateANDMerchantId(merchantId, from, to);
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

			LOGGER.info("serviceName : " + serviceName + " , serviceWiseTotalTransactionLong "
					+ serviceWiseTotalTransactionLong + ", serviceWiseAmount " + serviceWiseAmount);

			List<?> list2 = payinTransactionDetailRepository.findByDateANDMerchantIdAndServiceName(merchantId, from, to,
					serviceName);

			for (Iterator<?> iterator2 = list2.iterator(); iterator2.hasNext();) {

				Object[] object2 = (Object[]) iterator2.next();
				String statusName = (String) object2[0];
				BigInteger statusWiseTotalTransaction = (BigInteger) object2[1];
				Integer statusWiseTotalTransactionInteger = statusWiseTotalTransaction.intValue();
				String stringTotalAmount = amountFormate.format((Double) object2[2]);

				LOGGER.info("statusName : " + statusName + " , statusWiseTotalTransactionInteger "
						+ statusWiseTotalTransactionInteger + ", stringTotalAmount " + stringTotalAmount);

				if (statusName.equalsIgnoreCase("Success")) {

					serviceWiseTransactionResponse.setSuccessAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setSuccessTransaction(statusWiseTotalTransactionInteger);
				}
				if (statusName.equalsIgnoreCase("Pending")) {

					serviceWiseTransactionResponse.setPendingAmount(stringTotalAmount);
					serviceWiseTransactionResponse.setPendingTransaction(statusWiseTotalTransactionInteger);
				}
				if (statusName.equalsIgnoreCase("Fail") || statusName.equalsIgnoreCase("Failed")) {

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
	public List<ServiceWiseTransactionResponse> payinSDateAndMerchantIdAndMServiceId(long merchantId, String from,
			String to, long merchantServiceId) {
		List<ServiceWiseTransactionResponse> list = new ArrayList<>();
		List<?> list1 = payinTransactionDetailRepository.findByDateANDMerchantIdAndMServiceId(merchantId, from, to,
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

			List<?> list2 = payinTransactionDetailRepository.findByDateANDMerchantIdAndServiceName(merchantId, from, to,
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
				if (statusName.equalsIgnoreCase("Fail") || statusName.equalsIgnoreCase("Failed")) {

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
	public List<ServiceWiseTransactionResponse> payinSDateAndMerchantIdAndStatusId(long merchantId, String from,
			String to, long statusId) {
		List<ServiceWiseTransactionResponse> list = new ArrayList<>();
		List<?> list1 = payinTransactionDetailRepository.findByDateANDMerchantIdAndStatusId(merchantId, from, to,
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

			List<?> list2 = payinTransactionDetailRepository
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
				if (statusName.equalsIgnoreCase("Fail")|| statusName.equalsIgnoreCase("Failed")) {

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
	public List<ServiceWiseTransactionResponse> payinSDateAndMerchantIdAndMServiceIdAndStatusId(long merchantId,
			String from, String to, long merchantServiceId, long statusId) {
		List<ServiceWiseTransactionResponse> list = new ArrayList<>();
		List<?> list1 = payinTransactionDetailRepository.findByDateANDMerchantIdAndMServiceIdAndStatusId(merchantId,
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

			List<?> list2 = payinTransactionDetailRepository
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
				if (statusName.equalsIgnoreCase("Fail") || statusName.equalsIgnoreCase("Failed")) {

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
	public List<ServiceWiseTransactionResponse> payinSDateAndMerchantIdAndVpa(long merchantId, String from, String to,
			String vpa) {
		List<ServiceWiseTransactionResponse> list = new ArrayList<>();
		List<?> list1 = payinTransactionDetailRepository.findByDateANDMerchantIdAndVpa(merchantId, from, to, vpa);
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

			List<?> list2 = payinTransactionDetailRepository.findByDateANDMerchantIdAndServiceNameAndVpa(merchantId,
					from, to, serviceName, vpa);

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
				if (statusName.equalsIgnoreCase("Fail") || statusName.equalsIgnoreCase("Failed")) {

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
	public List<ServiceWiseTransactionResponse> payinSDateAndMerchantIdAndMServiceIdAndVpa(long merchantId, String from,
			String to, long merchantServiceId, String vpa) {
		List<ServiceWiseTransactionResponse> list = new ArrayList<>();
		List<?> list1 = payinTransactionDetailRepository.findByDateANDMerchantIdAndVpaAndMServiceId(merchantId, from,
				to, vpa, merchantServiceId);
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

			List<?> list2 = payinTransactionDetailRepository.findByDateANDMerchantIdAndServiceNameAndVpa(merchantId,
					from, to, serviceName, vpa);

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
				if (statusName.equalsIgnoreCase("Fail") || statusName.equalsIgnoreCase("Failed")) {

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
	public List<ServiceWiseTransactionResponse> payinSDateAndMerchantIdAndStatusIdAndVpa(long merchantId, String from,
			String to, long statusId, String vpa) {
		List<ServiceWiseTransactionResponse> list = new ArrayList<>();
		List<?> list1 = payinTransactionDetailRepository.findByDateANDMerchantIdAndVpaAndStatusId(merchantId, from, to,
				vpa, statusId);
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

			List<?> list2 = payinTransactionDetailRepository.findByDateANDMerchantIdAndServiceNameAndVpaAndStatusId(
					merchantId, from, to, serviceName, vpa, statusId);

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
				if (statusName.equalsIgnoreCase("Fail") ||statusName.equalsIgnoreCase("Failed")) {

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
	public List<ServiceWiseTransactionResponse> payinSDateAndMerchantIdAndStatusIdAndMServiceIdAndVpa(long merchantId,
			String from, String to, long statusId, long merchantServiceId, String vpa) {
		List<ServiceWiseTransactionResponse> list = new ArrayList<>();
		List<?> list1 = payinTransactionDetailRepository.findByDateANDMerchantIdAndVpaAndStatusIdAndMServiceId(
				merchantId, from, to, vpa, statusId, merchantServiceId);
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

			List<?> list2 = payinTransactionDetailRepository.findByDateANDMerchantIdAndServiceNameAndVpaAndStatusId(
					merchantId, from, to, serviceName, vpa, statusId);

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
				if (statusName.equalsIgnoreCase("Fail") || statusName.equalsIgnoreCase("Failed")) {

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
