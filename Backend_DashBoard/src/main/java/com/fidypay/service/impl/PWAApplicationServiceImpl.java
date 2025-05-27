package com.fidypay.service.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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
import com.fidypay.entity.MerchantSettlements;
import com.fidypay.entity.MerchantSubMerchantInfoV2;
import com.fidypay.entity.MerchantUser;
import com.fidypay.entity.Merchants;
import com.fidypay.entity.PayinTransactionalDetail;
import com.fidypay.entity.PosDevices;
import com.fidypay.repo.MerchantSettlementsRepository;
import com.fidypay.repo.MerchantSubMerchantInfoV2Repository;
import com.fidypay.repo.MerchantsRepository;
import com.fidypay.repo.PayinTransactionDetailRepository;
import com.fidypay.repo.PosDevicesRepository;
import com.fidypay.request.PWAAppliactionLoginRequest;
import com.fidypay.request.Pagination;
import com.fidypay.request.TransactionsReportRequest;
import com.fidypay.response.SettlementReportPayload;
import com.fidypay.response.TReportPayload;
import com.fidypay.service.PWAApplicationService;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.DateAndTime;
import com.fidypay.utils.ex.DateUtil;
import com.fidypay.utils.ex.RandomNumberGenrator;
import com.fidypay.utils.ex.SMSAPIImpl;

@Service
public class PWAApplicationServiceImpl implements PWAApplicationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PWAApplicationServiceImpl.class);

	@Autowired
	private MerchantsRepository merchantsRepository;

	@Autowired
	private MerchantSubMerchantInfoV2Repository merchantSubMerchantInfoV2Repository;

	@Autowired
	private PosDevicesRepository posDevicesRepository;

	@Autowired
	private PayinTransactionDetailRepository payinTransactionDetailRepository;

	@Autowired
	private MerchantSettlementsRepository merchantSettlementsRepository;

	@Override
	public Map<String, Object> merchantLogin(PWAAppliactionLoginRequest loginDTO) {
		Map<String, Object> map = new HashMap<>();
		try {

			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());
			String nDate = DateUtil.convertDateToStringWithTimeNew(trxnDate);
			LOGGER.info("nDate: " + nDate);
			String mobileNo = Encryption.encString(loginDTO.getMobileNo());
			LOGGER.info("Mobile " + mobileNo);
			Merchants merchants = merchantsRepository.findByMobileNo(mobileNo);
			if (merchants != null) {
				Long mMerchantId = merchants.getMerchantId();
				String clientSecret = merchants.getMerchantEmail();
				String mEmail = Encryption.decString(clientSecret);
				String merchantId = String.valueOf(mMerchantId);
				String mMobileNo = merchants.getMerchantPhone();
				String mBussinessName = Encryption.decString(merchants.getMerchantBusinessName());
				LOGGER.info(" mMobileNo " + mMobileNo);
				String otp = RandomNumberGenrator.generateWalletPin();
				String merchantName = Encryption.decString(merchants.getMerchantFirstname());
				if (merchantName.length() >= 20) {
					merchantName = merchantName.substring(0, 20);
				}
				MerchantSubMerchantInfoV2 merchantSubMerchantInfoV2 = merchantSubMerchantInfoV2Repository
						.findByMerchantId(mMerchantId);

				if (merchantSubMerchantInfoV2 != null) {

					SMSAPIImpl impl = new SMSAPIImpl();
					impl.registrationOTP(Encryption.decString(mMobileNo), merchantName, otp);

					String smri = merchantSubMerchantInfoV2.getSubMerchantRegisterInfo();

					JSONObject json = new JSONObject(smri);
					String gstn = json.getString("gstn");
					String MCC = json.getString("MCC");
					String panNo = json.getString("panNo");
					String vpa = merchantSubMerchantInfoV2.getSubMerchantAdditionalInfo();
					String qrString = merchantSubMerchantInfoV2.getSubMerchantQRString();

					String clientId = Encryption.encString(String.valueOf(merchantId));
					String fireBasetoken = loginDTO.getFireBaseToken();
					merchants.setEstablishType(fireBasetoken);
					merchantsRepository.save(merchants);
					LOGGER.info("Update Fire Base Token In Merchants tabel EstablishType");

					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
					map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
					map.put("description", "Login Successfully");
					map.put("address", Encryption.decString(merchants.getMerchantAddress1()));
					map.put("clientId", clientId);
					map.put("city", merchants.getMerchantCity());
					map.put("VPA", vpa);
					map.put("panNo", panNo);
					map.put("mobile", Encryption.decString(merchants.getMerchantPhone()));
					map.put("bussinessName", mBussinessName);
					map.put("otp", otp);
					map.put("MCC", MCC);
					map.put("clientSecretKey", clientSecret);
					map.put("name", Encryption.decString(merchants.getMerchantFirstname()) + " "
							+ Encryption.decString(merchants.getMerchantLastname()));
					map.put("password", Encryption.decString(merchants.getMerchantPassword()));
					map.put("gstn", gstn);
					map.put("pinCode", merchants.getMerchantZipcode());
					map.put("state", merchants.getMerchantState());
					map.put("email", mEmail);
					map.put("qrString", qrString);

					PosDevices devicesV2 = new PosDevices();
					devicesV2.setIsMobile('1');
					devicesV2.setMerchants(merchants);
					devicesV2.setIsPosAssigned('1');
					devicesV2.setPosIsActive('1');
					devicesV2.setPosAssignDate(trxnDate);
					devicesV2.setPosDetails(loginDTO.getDeviceModel());
					devicesV2.setPosMake(loginDTO.getDeviceModel());
					devicesV2.setPosSoftwareVer(loginDTO.getVersion());
					devicesV2.setPosSerialNo(loginDTO.getHardwareId());
					devicesV2 = posDevicesRepository.save(devicesV2);
					return map;
				} else {
					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.DESCRIPTION, "Mobile Number is Incorrect ");
					map.put("date", trxnDate);
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				}

			}

			else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Mobile Number is Incorrect ");
				map.put("date", trxnDate);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			}

		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Mobile Number is Incorrect ");
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;
	}

	@Override
	public Map<String, Object> getTransactionsList(TransactionsReportRequest transactionsReportRequest, String clientId)
			throws Exception {
		Map<String, Object> map = new HashMap<>();
		String startDate = transactionsReportRequest.getStartDate();
		String endDate = transactionsReportRequest.getEndDate();
		String startTime = transactionsReportRequest.getStartHours();
		String endTime = transactionsReportRequest.getEndHours();
		long merchantId = Long.parseLong(Encryption.decString(clientId));

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
		List<TReportPayload> activityList = new ArrayList<TReportPayload>();
		List<PayinTransactionalDetail> details = payinTransactionDetailRepository
				.findByStartDateAndEndDateWithoutPage(merchantId, startDate, endDate);

		double tamount = 0.0;
		if (details.size() != 0) {

			for (PayinTransactionalDetail objects : details) {

				Timestamp date = objects.getTransactionDate();
				SimpleDateFormat dateFormate = new SimpleDateFormat("dd");
				SimpleDateFormat monthFormate = new SimpleDateFormat("MMM");
				SimpleDateFormat yearFormate = new SimpleDateFormat("YYYY");
				SimpleDateFormat timeFormate = new SimpleDateFormat("HH:mm");
				String trxndate = dateFormate.format(date);

				String month = monthFormate.format(date);
				String year = yearFormate.format(date);
				String time = timeFormate.format(date);

				TReportPayload payLoad = new TReportPayload();
				payLoad.setReponseStatus(objects.getTransactionStatusId());
				payLoad.setDate(trxndate);
				payLoad.setMonth(month);
				payLoad.setSourceType(objects.getServiceName());
				payLoad.setServiceIdentifier(Encryption.decString(objects.getPayerVpa()));
				payLoad.setAmount(objects.getTransactionAmount());
				payLoad.setTransactionStatus(objects.getTransactionStatus());
				payLoad.setTransactionRefrenceId(Encryption.decString(objects.getMerchantTransactionRefId()));
				payLoad.setSourceName(objects.getServiceName());
				payLoad.setResponseMessage(objects.getRemark());
				payLoad.setTrxnTime(time);
				payLoad.setYear(year);
				payLoad.setUtr(Encryption.decString(objects.getUtr()));

				if (objects.getTransactionStatusId() == 1) {
					tamount += objects.getTransactionAmount();
				}

				activityList.add(payLoad);

			}

			map.put("transaction_statment", activityList);
			map.put("total_records", details.size());
			map.put("total_amount", tamount);
			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_FOUND);
			return map;
		} else {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_AVAILABLE);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}

		return map;
	}

	@Override
	public Map<String, Object> getAllTransactionsList(Pagination pagination, String clientId) {
		Map<String, Object> map = new HashMap<>();
		long merchantId = Long.parseLong(Encryption.decString(clientId));

		Pageable pageable = PageRequest.of(pagination.getPageNo(), pagination.getPageSize());

		List<TReportPayload> activityList = new ArrayList<TReportPayload>();
		List<PayinTransactionalDetail> details = payinTransactionDetailRepository
				.findByStartDateAndEndDateWithPage(merchantId, pageable);

		double tamount = 0.0;
		if (details.size() != 0) {

			for (PayinTransactionalDetail objects : details) {

				Timestamp date = objects.getTransactionDate();
				SimpleDateFormat dateFormate = new SimpleDateFormat("dd");
				SimpleDateFormat monthFormate = new SimpleDateFormat("MMM");
				SimpleDateFormat yearFormate = new SimpleDateFormat("YYYY");
				SimpleDateFormat timeFormate = new SimpleDateFormat("HH:mm");
				String trxndate = dateFormate.format(date);

				String month = monthFormate.format(date);
				String year = yearFormate.format(date);
				String time = timeFormate.format(date);

				TReportPayload payLoad = new TReportPayload();
				payLoad.setReponseStatus(objects.getTransactionStatusId());
				payLoad.setDate(trxndate);
				payLoad.setMonth(month);
				payLoad.setSourceType(objects.getServiceName());
				payLoad.setServiceIdentifier(Encryption.decString(objects.getPayerVpa()));
				payLoad.setAmount(objects.getTransactionAmount());
				payLoad.setTransactionStatus(objects.getTransactionStatus());
				payLoad.setTransactionRefrenceId(Encryption.decString(objects.getMerchantTransactionRefId()));
				payLoad.setSourceName(objects.getServiceName());
				payLoad.setResponseMessage(objects.getRemark());
				payLoad.setTrxnTime(time);
				payLoad.setYear(year);
				payLoad.setUtr(Encryption.decString(objects.getUtr()));

				if (objects.getTransactionStatusId() == 1) {
					tamount += objects.getTransactionAmount();
				}

				activityList.add(payLoad);

			}

			map.put("transaction_statment", activityList);
			map.put("total_records", details.size());
			map.put("total_amount", tamount);
			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_FOUND);
			return map;
		} else {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_AVAILABLE);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}

		return map;
	}

	public Map<String, Object> getSettlementList(TransactionsReportRequest transactionsReportRequest) throws Exception {
		Map<String, Object> map = new HashMap<>();

		int pageNo = transactionsReportRequest.getPageNo();
		int pageSize = transactionsReportRequest.getPageSize();

		Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by("SETTLEMENT_DATE").descending());
		Page<MerchantSettlements> page = null;

		String startDate = transactionsReportRequest.getStartDate();
		String endDate = transactionsReportRequest.getEndDate();
		String vpa = transactionsReportRequest.getVpa();
		List<SettlementReportPayload> list = new ArrayList<SettlementReportPayload>();

		if (startDate == "" && endDate == "" && vpa != null) {
			page = merchantSettlementsRepository.findByVPA(vpa, paging);
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

			String startTime = transactionsReportRequest.getStartHours();
			String endTime = transactionsReportRequest.getEndHours();

			if (startTime.equals("") || startTime.equals("0") || startTime.equals("null") || endTime.equals("null")
					|| endTime.equals("") || endTime.equals("0")) {
				startDate = startDate + " 00.00.00.0";
				endDate = endDate + " 23.59.59.9";
			} else {
				startDate = startDate + " " + startTime + ".00.0";
				endDate = endDate + " " + endTime + ".00.0";
			}

			page = merchantSettlementsRepository.findByVPAAndSettlementDate(vpa, startDate, endDate, paging);
			LOGGER.info(""+page.getSize());
		}
		AtomicInteger atomicInteger = new AtomicInteger(1);
		List<MerchantSettlements> merchantSettelementList = page.getContent();

		if (!merchantSettelementList.isEmpty()) {

			merchantSettelementList.forEach(merchantSettlements -> {
				Timestamp date = merchantSettlements.getSettlementDate();
				SimpleDateFormat dateFormate = new SimpleDateFormat("YYYY-MM-dd");
				String trxndate = dateFormate.format(date);
				Timestamp fromdate = merchantSettlements.getSettlementFromDate();
				Timestamp todate = merchantSettlements.getSettlementToDate();

				SettlementReportPayload payload = new SettlementReportPayload();
				payload.setAmount(merchantSettlements.getSettlementNetAmount());
				payload.setSettlementFromDate(dateFormate.format(fromdate));
				payload.setSettlementToDate(dateFormate.format(todate));

				payload.setSettlementDate(trxndate);

				payload.setUtr(merchantSettlements.getSettlementUTR());
				payload.setStatus(merchantSettlements.getSettlementStatus());
				payload.setSubmerchantBusinessName(merchantSettlements.getSubMerchantBussinessName());
				payload.setSubmerchantVpa(merchantSettlements.getMerchantVPA());
				payload.setTotalTransaction(merchantSettlements.getSettlementTotalTrxn());
				payload.setsNo(atomicInteger.getAndIncrement());
				list.add(payload);
			});

			map.put(ResponseMessage.DATA, list);
			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_FOUND);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			map.put("currentPage", page.getNumber());
			map.put("totalItems", page.getTotalElements());
			map.put("totalPages", page.getTotalPages());
			return map;

		} else {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_AVAILABLE);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}

		return map;

	}

}
