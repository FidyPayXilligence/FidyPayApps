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
import com.fidypay.entity.EkycTransactionDetails;
import com.fidypay.entity.MerchantService;
import com.fidypay.entity.Merchants;
import com.fidypay.entity.ServiceCategory;
import com.fidypay.entity.ServiceInfo;
import com.fidypay.repo.EkycTransactionDetailsRepository;
import com.fidypay.repo.MerchantServiceRepository;
import com.fidypay.repo.MerchantsRepository;
import com.fidypay.repo.ServiceCategoryRepository;
import com.fidypay.repo.ServiceInfoRepository;
import com.fidypay.request.EKYCTransactionRequest;
import com.fidypay.response.EKYCTransactionResponse;
import com.fidypay.response.MerchantServicesPayload;
import com.fidypay.response.ServiceWiseTransactionResponse;
import com.fidypay.service.EkycTransactionDetailsService;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.DateAndTime;
import com.fidypay.utils.ex.DateUtil;

@Service
public class EkycTransactionDetailsServiceImpl implements EkycTransactionDetailsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(EkycTransactionDetailsServiceImpl.class);

	@Autowired
	private EkycTransactionDetailsRepository ekycTransactionDetailsRepository;

	@Autowired
	private MerchantsRepository merchantsRepository;

	@Autowired
	private MerchantServiceRepository merchantServiceRepository;

	@Autowired
	private ServiceInfoRepository serviceInfoRepository;

	@Autowired
	private ServiceCategoryRepository serviceCategoryRepository;

	@SuppressWarnings("null")
	@Override
	public Map<String, Object> ekycTransactionList(EKYCTransactionRequest ekycTransactionRequest, long merchantId) {
		Map<String, Object> map = new HashMap<>();
		try {
			Pageable paging = PageRequest.of(ekycTransactionRequest.getPageNo(), ekycTransactionRequest.getPageSize(),
					Sort.by("CREATION_DATE").descending());
			Long status = ekycTransactionRequest.getStatusId();
			String startDate = ekycTransactionRequest.getStartDate();
			String endDate = ekycTransactionRequest.getEndDate();
			Long merchantServiceId = ekycTransactionRequest.getMerchantServiceId();
			String startTime = ekycTransactionRequest.getStartTime();
			String endTime = ekycTransactionRequest.getEndTime();

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

			List<EkycTransactionDetails> list = new ArrayList<EkycTransactionDetails>();
			List<EKYCTransactionResponse> listEKYCTransactionResponse = new ArrayList<EKYCTransactionResponse>();
			Page<EkycTransactionDetails> listTransaction = null;

			if (startTime.equals("") || startTime.equals("0") || startTime.equals("null") || endTime.equals("null")
					|| endTime.equals("") || endTime.equals("0")) {
				startDate = startDate + " 00.00.00.0";
				endDate = endDate + " 23.59.59.9";
			} else {
				startDate = startDate + " " + startTime + ".00.0";
				endDate = endDate + " " + endTime + ".00.0";
			}

			if ((merchantServiceId == null || merchantServiceId == 0) && (status == null || status == 0)) {
				listTransaction = ekycTransactionDetailsRepository.findByStartDateAndEndDate(merchantId, startDate,
						endDate, paging);

			} else if ((merchantServiceId != null || merchantServiceId != 0) && (status == null || status == 0)) {
				listTransaction = ekycTransactionDetailsRepository.findByStartDateAndEndDateANDService(merchantId,
						startDate, endDate, merchantServiceId, paging);
			} else if ((merchantServiceId == null || merchantServiceId == 0) && (status != null || status != 0)) {
				listTransaction = ekycTransactionDetailsRepository.findByStartDateAndEndDateANDStatus(merchantId,
						startDate, endDate, status, paging);
			} else if ((merchantServiceId != null || merchantServiceId != 0) && (status != null || status != 0)) {
				listTransaction = ekycTransactionDetailsRepository.findByStartDateAndEndDateANDStatusANDService(
						merchantId, startDate, endDate, status, merchantServiceId, paging);
			}

			list = listTransaction.getContent();

			AtomicInteger atomicInteger = new AtomicInteger(1);
			if (list.size() != 0) {
				list.forEach(objects -> {

					DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");
					// String mAmount = amount1.format(objects.getMerchantServiceCharge());
					try {
						String date = DateAndTime.dateFormatReports(objects.getCreationDate().toString());

						EKYCTransactionResponse ekycTransactionResponse = new EKYCTransactionResponse();

						ekycTransactionResponse.setMerchantId(objects.getMerchantId());

						Merchants merchants = merchantsRepository.findById(objects.getMerchantId()).get();

						ekycTransactionResponse.setsNo(atomicInteger.getAndIncrement());
						ekycTransactionResponse.setMerchantName(Encryption.decString(merchants.getMerchantFirstname())
								+ " " + Encryption.decString(merchants.getMerchantLastname()));
						ekycTransactionResponse.setTransactionDate(date);
						ekycTransactionResponse.setStatus(objects.getStatus());
						ekycTransactionResponse.setServiceName(objects.getEkycServicename());
						ekycTransactionResponse.setMerchantTransactionRefId(
								Encryption.decString(objects.getMerchantTransactionRefId()));
						ekycTransactionResponse.setTrxnRefId(Encryption.decString(objects.getTrxnRefId()));
						ekycTransactionResponse.setEkycId(Encryption.decString(objects.geteKycId()));
						ekycTransactionResponse
								.setCharges(Double.parseDouble(amount1.format(objects.getMerchantServiceCharge())));
						ekycTransactionResponse.setIsReconcile(String.valueOf(objects.getIsReconcile()));

						listEKYCTransactionResponse.add(ekycTransactionResponse);
					} catch (ParseException e) {

						e.printStackTrace();
					}
				});

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "EKYC Transaction List");
				map.put("callBackList", listEKYCTransactionResponse);
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
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage().toString());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}

		return map;

	}

	@SuppressWarnings("null")
	@Override
	public List<EKYCTransactionResponse> getEkycTransactionsStatementReportExcel(
			EKYCTransactionRequest transactionsReportRequest, String clientId) {
		Map<String, Object> map = new HashMap<>();
		List<EKYCTransactionResponse> activityList = new ArrayList<EKYCTransactionResponse>();
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

			Long merchantId = Long.parseLong(clientId);
			String startDate = transactionsReportRequest.getStartDate() + " " + startHours;
			String endDate = transactionsReportRequest.getEndDate() + " " + endHours;
			List<EkycTransactionDetails> list = new ArrayList<EkycTransactionDetails>();

			if ((merchantServiceId == null || merchantServiceId == 0) && (status == null || status == 0)) {
				LOGGER.info("Case1");
				list = ekycTransactionDetailsRepository.findByStartDateAndEndDateWithoutPage(merchantId, startDate,
						endDate);
			} else if ((merchantServiceId != null || merchantServiceId != 0) && (status == null || status == 0)) {
				LOGGER.info("Case2");
				list = ekycTransactionDetailsRepository.findByStartDateAndEndDateANDServiceWithoutPage(merchantId,
						startDate, endDate, merchantServiceId);
			} else if ((merchantServiceId == null || merchantServiceId == 0) && (status != null || status != 0)) {
				LOGGER.info("Case3");
				list = ekycTransactionDetailsRepository.findByStartDateAndEndDateANDStatusWithoutPage(merchantId,
						startDate, endDate, status);
			} else if ((merchantServiceId != null || merchantServiceId != 0) && (status != null || status != 0)) {
				LOGGER.info("Case4");
				list = ekycTransactionDetailsRepository.findByStartDateAndEndDateANDStatusANDServiceWithoutPage(
						merchantId, startDate, endDate, status, merchantServiceId);
			}

			LOGGER.info("list: " + list.size());

			AtomicInteger atomicInteger = new AtomicInteger(1);
			if (list.size() != 0) {
				list.forEach(objects -> {

					DecimalFormat amount1 = new DecimalFormat("#,##,##,##,###.00");

					try {
						String date = DateAndTime.dateFormatReports(objects.getCreationDate().toString());

						EKYCTransactionResponse ekycTransactionResponse = new EKYCTransactionResponse();

						ekycTransactionResponse.setsNo(atomicInteger.getAndIncrement());
						ekycTransactionResponse.setTransactionDate(date);
						ekycTransactionResponse.setStatus(objects.getStatus());
						ekycTransactionResponse.setServiceName(objects.getEkycServicename());
						ekycTransactionResponse.setMerchantTransactionRefId(
								Encryption.decString(objects.getMerchantTransactionRefId()));
						ekycTransactionResponse.setTrxnRefId(Encryption.decString(objects.getTrxnRefId()));
						ekycTransactionResponse.setEkycId(Encryption.decString(objects.geteKycId()));
						ekycTransactionResponse
								.setCharges(Double.parseDouble(amount1.format(objects.getMerchantServiceCharge())));
						ekycTransactionResponse.setIsReconcile(String.valueOf(objects.getIsReconcile()));
						activityList.add(ekycTransactionResponse);
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
	public Map<String, Object> eKycTotalTransactionAndTotalAmount(long merchantId) {
		Map<String, Object> map = new HashMap<>();
		try {
			Date date = new Date();
			SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
			format1.setTimeZone(TimeZone.getTimeZone("IST"));

			String date1 = format1.format(date);
			Integer totalTransaction = 0;

			String startDate = date1 + " 00:00:00.0";
			String endDate = date1 + " 23:59:59.9";

			totalTransaction = ekycTransactionDetailsRepository
					.findTotalTransactionsByMerchantIdAndStartAndEndDate(merchantId, startDate, endDate);

			if (totalTransaction == null) {

				totalTransaction = 0;
			}

			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, "Total transactions and total amount");
			map.put("totalTransactions", totalTransaction);
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
	public Map<String, Object> eKycServicesList(long merchantId) {
		Map<String, Object> map = new HashMap<>();
		List<MerchantServicesPayload> servicesList = new ArrayList<MerchantServicesPayload>();

		try {
			List<MerchantService> list = merchantServiceRepository.findEKycServicesByMerchantId(merchantId);

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
	public Map<String, Object> ekycServiceTransactionList(EKYCTransactionRequest ekycTransactionRequest,
			long merchantId) {
		Map<String, Object> map = new HashMap<>();
		try {

			Long status = ekycTransactionRequest.getStatusId();
			String startDate = ekycTransactionRequest.getStartDate();
			String endDate = ekycTransactionRequest.getEndDate();
			Long merchantServiceId = ekycTransactionRequest.getMerchantServiceId();
			String startTime = ekycTransactionRequest.getStartTime();
			String endTime = ekycTransactionRequest.getEndTime();

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

				list = eKycSDateAndMerchantId(merchantId, startDate, endDate);

			} else if ((merchantServiceId != null || merchantServiceId != 0) && (status == null || status == 0)) {

				list = eKycSDateAndMerchantIdAndMServiceId(merchantId, startDate, endDate, merchantServiceId);

			} else if ((merchantServiceId == null || merchantServiceId == 0) && (status != null || status != 0)) {

				list = eKycSDateAndMerchantIdAndStatusId(merchantId, startDate, endDate, status);

			} else if ((merchantServiceId != null || merchantServiceId != 0) && (status != null || status != 0)) {

				list = eKycSDateAndMerchantIdAndMServiceIdAndStatusId(merchantId, startDate, endDate, merchantServiceId,
						status);

			}

			if (list.size() != 0) {
				map.put("list", list);
				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "EKyc Service  Transaction List");
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
	public List<ServiceWiseTransactionResponse> eKycSDateAndMerchantId(long merchantId, String from, String to) {
		List<ServiceWiseTransactionResponse> list = new ArrayList<>();

		List<?> list1 = ekycTransactionDetailsRepository.findByDateANDMerchantId(merchantId, from, to);

		Integer sNo = 0;
		for (Iterator<?> iterator1 = list1.iterator(); iterator1.hasNext();) {

			ServiceWiseTransactionResponse serviceWiseTransactionResponse = new ServiceWiseTransactionResponse();
			sNo++;
			Object[] object1 = (Object[]) iterator1.next();
			String serviceName = (String) object1[0];
			BigInteger serviceWiseTotalTransaction = (BigInteger) object1[1];
			Integer serviceWiseTotalTransactionLong = serviceWiseTotalTransaction.intValue();

			List<?> list2 = ekycTransactionDetailsRepository.findByDateANDMerchantIdAndServiceName(merchantId, from, to,
					serviceName);

			for (Iterator<?> iterator2 = list2.iterator(); iterator2.hasNext();) {

				Object[] object2 = (Object[]) iterator2.next();
				String statusName = (String) object2[0];
				BigInteger statusWiseTotalTransaction = (BigInteger) object2[1];
				Integer statusWiseTotalTransactionInteger = statusWiseTotalTransaction.intValue();

				if (statusName.equalsIgnoreCase("Success")) {

					serviceWiseTransactionResponse.setSuccessTransaction(statusWiseTotalTransactionInteger);
				}

				if (statusName.equalsIgnoreCase("Fail") || statusName.equalsIgnoreCase("Failed")) {

					serviceWiseTransactionResponse.setFailedTransaction(statusWiseTotalTransactionInteger);
				}

			}

			serviceWiseTransactionResponse.setsNo(sNo);
			serviceWiseTransactionResponse.setServicename(serviceName);
			serviceWiseTransactionResponse.setTotalTransaction(serviceWiseTotalTransactionLong);

			list.add(serviceWiseTransactionResponse);
		}

		return list;
	}

	@Override
	public List<ServiceWiseTransactionResponse> eKycSDateAndMerchantIdAndMServiceId(long merchantId, String from,
			String to, long merchantServiceId) {
		List<ServiceWiseTransactionResponse> list = new ArrayList<>();

		List<?> list1 = ekycTransactionDetailsRepository.findByDateANDMerchantIdAndMserviceId(merchantId, from, to,
				merchantServiceId);

		Integer sNo = 0;
		for (Iterator<?> iterator1 = list1.iterator(); iterator1.hasNext();) {

			ServiceWiseTransactionResponse serviceWiseTransactionResponse = new ServiceWiseTransactionResponse();
			sNo++;
			Object[] object1 = (Object[]) iterator1.next();
			String serviceName = (String) object1[0];
			BigInteger serviceWiseTotalTransaction = (BigInteger) object1[1];
			Integer serviceWiseTotalTransactionLong = serviceWiseTotalTransaction.intValue();

			List<?> list2 = ekycTransactionDetailsRepository.findByDateANDMerchantIdAndServiceName(merchantId, from, to,
					serviceName);

			for (Iterator<?> iterator2 = list2.iterator(); iterator2.hasNext();) {

				Object[] object2 = (Object[]) iterator2.next();
				String statusName = (String) object2[0];
				BigInteger statusWiseTotalTransaction = (BigInteger) object2[1];
				Integer statusWiseTotalTransactionInteger = statusWiseTotalTransaction.intValue();

				if (statusName.equalsIgnoreCase("Success")) {

					serviceWiseTransactionResponse.setSuccessTransaction(statusWiseTotalTransactionInteger);
				}

				if (statusName.equalsIgnoreCase("Fail") || statusName.equalsIgnoreCase("Failed")) {

					serviceWiseTransactionResponse.setFailedTransaction(statusWiseTotalTransactionInteger);
				}

			}

			serviceWiseTransactionResponse.setsNo(sNo);
			serviceWiseTransactionResponse.setServicename(serviceName);
			serviceWiseTransactionResponse.setTotalTransaction(serviceWiseTotalTransactionLong);

			list.add(serviceWiseTransactionResponse);
		}

		return list;
	}

	@Override
	public List<ServiceWiseTransactionResponse> eKycSDateAndMerchantIdAndStatusId(long merchantId, String from,
			String to, long statusId) {
		List<ServiceWiseTransactionResponse> list = new ArrayList<>();

		List<?> list1 = ekycTransactionDetailsRepository.findByDateANDMerchantIdAndStatusId(merchantId, from, to,
				statusId);

		Integer sNo = 0;
		for (Iterator<?> iterator1 = list1.iterator(); iterator1.hasNext();) {

			ServiceWiseTransactionResponse serviceWiseTransactionResponse = new ServiceWiseTransactionResponse();
			sNo++;
			Object[] object1 = (Object[]) iterator1.next();
			String serviceName = (String) object1[0];
			BigInteger serviceWiseTotalTransaction = (BigInteger) object1[1];
			Integer serviceWiseTotalTransactionLong = serviceWiseTotalTransaction.intValue();

			List<?> list2 = ekycTransactionDetailsRepository
					.findByDateANDMerchantIdAndServiceNameAndStatusId(merchantId, from, to, serviceName, statusId);

			for (Iterator<?> iterator2 = list2.iterator(); iterator2.hasNext();) {

				Object[] object2 = (Object[]) iterator2.next();
				String statusName = (String) object2[0];
				BigInteger statusWiseTotalTransaction = (BigInteger) object2[1];
				Integer statusWiseTotalTransactionInteger = statusWiseTotalTransaction.intValue();

				if (statusName.equalsIgnoreCase("Success")) {

					serviceWiseTransactionResponse.setSuccessTransaction(statusWiseTotalTransactionInteger);
				}

				if (statusName.equalsIgnoreCase("Fail") || statusName.equalsIgnoreCase("Failed")) {

					serviceWiseTransactionResponse.setFailedTransaction(statusWiseTotalTransactionInteger);
				}

			}

			serviceWiseTransactionResponse.setsNo(sNo);
			serviceWiseTransactionResponse.setServicename(serviceName);
			serviceWiseTransactionResponse.setTotalTransaction(serviceWiseTotalTransactionLong);

			list.add(serviceWiseTransactionResponse);
		}

		return list;
	}

	@Override
	public List<ServiceWiseTransactionResponse> eKycSDateAndMerchantIdAndMServiceIdAndStatusId(long merchantId,
			String from, String to, long merchantServiceId, long statusId) {
		List<ServiceWiseTransactionResponse> list = new ArrayList<>();

		List<?> list1 = ekycTransactionDetailsRepository.findByDateANDMerchantIdAndMServiceIdAndStatusId(merchantId,
				from, to, merchantServiceId, statusId);

		Integer sNo = 0;
		for (Iterator<?> iterator1 = list1.iterator(); iterator1.hasNext();) {

			ServiceWiseTransactionResponse serviceWiseTransactionResponse = new ServiceWiseTransactionResponse();
			sNo++;
			Object[] object1 = (Object[]) iterator1.next();
			String serviceName = (String) object1[0];
			BigInteger serviceWiseTotalTransaction = (BigInteger) object1[1];
			Integer serviceWiseTotalTransactionLong = serviceWiseTotalTransaction.intValue();

			List<?> list2 = ekycTransactionDetailsRepository
					.findByDateANDMerchantIdAndServiceNameAndStatusId(merchantId, from, to, serviceName, statusId);

			for (Iterator<?> iterator2 = list2.iterator(); iterator2.hasNext();) {

				Object[] object2 = (Object[]) iterator2.next();
				String statusName = (String) object2[0];
				BigInteger statusWiseTotalTransaction = (BigInteger) object2[1];
				Integer statusWiseTotalTransactionInteger = statusWiseTotalTransaction.intValue();

				if (statusName.equalsIgnoreCase("Success")) {

					serviceWiseTransactionResponse.setSuccessTransaction(statusWiseTotalTransactionInteger);
				}

				if (statusName.equalsIgnoreCase("Fail") || statusName.equalsIgnoreCase("Failed")) {

					serviceWiseTransactionResponse.setFailedTransaction(statusWiseTotalTransactionInteger);
				}

			}

			serviceWiseTransactionResponse.setsNo(sNo);
			serviceWiseTransactionResponse.setServicename(serviceName);
			serviceWiseTransactionResponse.setTotalTransaction(serviceWiseTotalTransactionLong);

			list.add(serviceWiseTransactionResponse);
		}

		return list;
	}

}