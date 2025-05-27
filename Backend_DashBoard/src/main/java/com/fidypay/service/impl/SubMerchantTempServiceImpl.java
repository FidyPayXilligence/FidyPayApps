package com.fidypay.service.impl;

import java.net.ConnectException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.Valid;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fidypay.ServiceProvider.AirtelPayments.AirtelPaymentsService;
import com.fidypay.encryption.Encryption;
import com.fidypay.entity.MerchantInfo;
import com.fidypay.entity.MerchantSubMerchantInfoV2;
import com.fidypay.entity.Merchants;
import com.fidypay.entity.OTPVerification;
import com.fidypay.entity.SubMerchantTemp;
import com.fidypay.repo.MerchantInfoRepository;
import com.fidypay.repo.MerchantSubMerchantInfoV2Repository;
import com.fidypay.repo.MerchantUserRepository;
import com.fidypay.repo.MerchantsRepository;
import com.fidypay.repo.OTPVerificationRepository;
import com.fidypay.repo.SubMerchantTempRepository;
import com.fidypay.request.MerchantSubMerchantOnboardingRequest;
import com.fidypay.request.Pagination;
import com.fidypay.request.SubMerchantOnboardingRequest;
import com.fidypay.response.SubMerchantTempResponse;
import com.fidypay.service.MerchantUserService;
import com.fidypay.service.SubMerchantTempService;
import com.fidypay.utils.constants.EmailNotification;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.constants.URLGenerater;
import com.fidypay.utils.ex.DateAndTime;
import com.fidypay.utils.ex.DateUtil;
import com.fidypay.utils.ex.GenerateTrxnRefId;
import com.fidypay.utils.ex.RandomNumberGenrator;
import com.fidypay.utils.ex.SMSAPIImpl;
import com.google.gson.JsonObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class SubMerchantTempServiceImpl implements SubMerchantTempService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SubMerchantTempServiceImpl.class);

	private static final String BUCKET_URL_DOCS = "https://ocr-image-aws.s3.ap-south-1.amazonaws.com/";
	private static final String BUCKET_URL_LOGO = "https://fidylogoandqrimages.s3.ap-south-1.amazonaws.com/";

	@Autowired
	private MerchantUserService merchantUserService;

	@Autowired
	private SubMerchantTempRepository subMerchantTempRepository;

	@Autowired
	private OTPVerificationRepository otpVerificationRepository;

	@Autowired
	private MerchantSubMerchantInfoV2Repository merchantSubMerchantInfoRepository;

	@Autowired
	private MerchantInfoRepository merchantInfoRepository;

	@Autowired
	private AirtelPaymentsService airtelPaymentsService;

	@Autowired
	private MerchantsRepository merchantsRepository;

	@Autowired
	private MerchantInfoRepository infoRepository;

	@Autowired
	private MerchantUserRepository merchantUserRepository;

	@Override
	public Map<String, Object> saveDetails(long merchantId, MerchantSubMerchantOnboardingRequest subMerchantRequest,
			String merchantBusinessName, String bankId, String logo) throws Exception {

		Map<String, Object> map = new HashMap<String, Object>();

		String mobile = subMerchantRequest.getMerchantPhone();
		String email = subMerchantRequest.getMerchantEmail();
		String mccCode = subMerchantRequest.getMcc();
		String businessType = subMerchantRequest.getBusinessType();
		String subMerchantName = subMerchantRequest.getSubMerchantFullName();
		String reason = subMerchantRequest.getDescription();

		if (!isValidMobileNo(mobile) || mobile.equals("")) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Please enter your 10 digit mobile number");
			return map;
		}

		if ((!email.equalsIgnoreCase("NA")) && (!isValidEmail(email) || email.equals(""))) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Please enter Valid Email id");
			return map;
		}

		if (subMerchantTempRepository.existsByMobile(Encryption.encString(mobile))) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.PHONE_ALREADY_ACTIVATED);
			return map;
		}

		if ((!email.equalsIgnoreCase("NA")) && (subMerchantTempRepository.existsByEmail(Encryption.encString(email)))) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.EMAIL_ALREADY_ACTIVATED);
			return map;
		}

		if (businessType.equalsIgnoreCase("NA") || businessType.equals("")) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Please enter businessType");
			return map;
		}

		if (reason.equalsIgnoreCase("NA") || reason.equals("")) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Please enter MCC Code Description");
			return map;
		}

		if (subMerchantName.equalsIgnoreCase("NA") || subMerchantName.equals("")) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Please enter SubMerchantFullName");
			return map;
		}

		if (mccCode.equalsIgnoreCase("NA") || mccCode.equals("") || mccCode.length() != 4
				|| !DateUtil.isValidNumber(mccCode)) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Invalid MCC Code");
			return map;
		}

		List<SubMerchantTemp> existingInfos = subMerchantTempRepository
				.findByEmailAndMobile(Encryption.encString(email), Encryption.encString(mobile));

		if (!existingInfos.isEmpty()) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.EMAIL_AND_MOBILE_ALREADY_ACTIVATED);
		} else {
			SubMerchantTemp info = new SubMerchantTemp();
			String subMerchantKey = merchantId + "SM" + mobile + GenerateTrxnRefId.getAlphaNumericString(6);
			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());
			info.setMerchantId(merchantId);
			info.setDate(trxnDate);
			info.setSubMerchantName(Encryption.encString(subMerchantName));
			info.setMccCode(mccCode);
			info.setBusinessType(businessType);
			info.setEmail(Encryption.encString(email));
			info.setMobile(Encryption.encString(mobile));
			info.setSubMerchantKey(Encryption.encString(subMerchantKey));
			info.setJsonResponse("NA");
			info.setIsActive('0');
			info.setMerchantSubMerchantRequest("NA");
			info.setMerchantSubMerchantResponse("NA");
			info.setIsSubMerchant('0');
			info.setIsOnboarding('0');
			info.setIsMerchant('0');
			info.setBankID(bankId);
			info.setReason(reason);
			info.setVpaCallBack("NA");
			info.setMerchantUserId(0L);

			subMerchantTempRepository.save(info);
			Long subMerchantTempId = info.getSubMerchantTempId();
			LOGGER.info("subMerchantTempId " + subMerchantTempId);
			resendNotification(merchantId, String.valueOf(subMerchantTempId), merchantBusinessName, logo);

			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.NOTIFICATION_SEND);
			map.put("token", subMerchantKey);
			return map;

		}

		return map;

	}

	@Override
	public Map<String, Object> findAllSubMerchant(String startDate, String endDate, Pagination pagination,
			long merchantId) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Pageable paging = PageRequest.of(pagination.getPageNo(), pagination.getPageSize(),
					Sort.by("DATE").descending());

			List<SubMerchantTemp> list = new ArrayList<SubMerchantTemp>();
			List<SubMerchantTempResponse> responseList = new ArrayList<SubMerchantTempResponse>();
			Page<SubMerchantTemp> pageingList = null;

			LOGGER.info("startdate " + startDate + " , enddate " + endDate);

			if (!startDate.equals("0") || !endDate.equals("0")) {
				if (DateUtil.isValidDateFormat(startDate) == false || DateUtil.isValidDateFormat(endDate) == false) {
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.DESCRIPTION, ResponseMessage.INVALID_DATE_FORMATE);
					return map;
				}

				if (DateUtil.isValidDateFormat(startDate, endDate)) {
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.DESCRIPTION, ResponseMessage.INVALID_FROM_TO_DATE);
					return map;
				}

				startDate = startDate + " 00.00.00";
				endDate = endDate + " 23.59.59";
				pageingList = subMerchantTempRepository.findByStartDateAndEndDateAndMerchantId(startDate, endDate,
						merchantId, paging);
			} else {
				pageingList = subMerchantTempRepository.findByMerchantId(merchantId, paging);
			}
			list = pageingList.getContent();

			AtomicInteger atomicInteger = new AtomicInteger(1);
			if (list.size() != 0) {
				list.forEach(objects -> {

					SubMerchantTempResponse subMerchantTempResponse = new SubMerchantTempResponse();

					try {
						String date = DateAndTime.dateFormatReports(objects.getDate().toString());

						subMerchantTempResponse.setLogo("NA");
						subMerchantTempResponse.setSubMerchantTempId(objects.getSubMerchantTempId());
						subMerchantTempResponse.setsNo(atomicInteger.getAndIncrement());
						subMerchantTempResponse.setMerchantId(objects.getMerchantId());
						subMerchantTempResponse.setDate(date);
						subMerchantTempResponse.setSubMerchantName(Encryption.decString(objects.getSubMerchantName()));
						subMerchantTempResponse.setMccCode(objects.getMccCode());
						subMerchantTempResponse.setBusinessType(objects.getBusinessType());
						subMerchantTempResponse.setEmail(Encryption.decString(objects.getEmail()));
						subMerchantTempResponse.setMobile(Encryption.decString(objects.getMobile()));
						subMerchantTempResponse.setIsActive(objects.getIsActive());
						subMerchantTempResponse.setIsOnboarding(objects.getIsOnboarding());
						subMerchantTempResponse.setIsSubMerchant(objects.getIsSubMerchant());
						subMerchantTempResponse.setJsonResponse(objects.getJsonResponse());
						subMerchantTempResponse.setMerchantSubMerchantRequest(objects.getMerchantSubMerchantRequest());
						subMerchantTempResponse
								.setMerchantSubMerchantResponse(objects.getMerchantSubMerchantResponse());
						subMerchantTempResponse.setToken(Encryption.decString(objects.getSubMerchantKey()));
						subMerchantTempResponse.setReason(objects.getReason());
						responseList.add(subMerchantTempResponse);
					} catch (ParseException e) {
						e.printStackTrace();
					}

				});

				map.put("currentPage", pageingList.getNumber());
				map.put("totalItems", pageingList.getTotalElements());
				map.put("totalPages", pageingList.getTotalPages());
				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "Sub Merchant Temp List");
				map.put("data", responseList);

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

	private static boolean isValidMobileNo(String str) {
		Pattern ptrn = Pattern.compile("^\\(?([0-9]{3})\\)?[-.\\s]?([0-9]{3})[-.\\s]?([0-9]{4})$");
		Matcher match = ptrn.matcher(str);
		return (match.find() && match.group().equals(str));
	}

	private static boolean isValidEmail(String str) {
		Pattern ptrn = Pattern.compile(
				"^(?=.{1,47}@)[a-zA-Z0-9-_]+(\\.[a-zA-Z0-9-_]+)*@[^-][a-zA-Z0-9]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{2,})$");
		Matcher match = ptrn.matcher(str);
		return (match.find() && match.group().equals(str));
	}

	@Override
	public Map<String, Object> findByTokenOrMobile(long merchantId, String value, String key) throws ParseException {
		Map<String, Object> map = new HashMap<String, Object>();
		SubMerchantTemp objects = null;
		if (key.equals("token")) {
			objects = subMerchantTempRepository
					.findBySubMerchantKeyAndMerchantIdAndIsMerchant(Encryption.encString(value), merchantId, '0');

		}

		if (key.equals("mobile")) {
			objects = subMerchantTempRepository.findByMobileAndMerchantIdAndIsMerchant(Encryption.encString(value),
					merchantId, '0');

		}

		if (objects != null) {
			SubMerchantTempResponse subMerchantTempResponse = new SubMerchantTempResponse();

			String date = DateAndTime.dateFormatReports(objects.getDate().toString());

			subMerchantTempResponse.setLogo("NA");
			subMerchantTempResponse.setSubMerchantTempId(objects.getSubMerchantTempId());
			subMerchantTempResponse.setsNo(1);
			subMerchantTempResponse.setMerchantId(objects.getMerchantId());
			subMerchantTempResponse.setDate(date);
			subMerchantTempResponse.setSubMerchantName(Encryption.decString(objects.getSubMerchantName()));
			subMerchantTempResponse.setMccCode(objects.getMccCode());
			subMerchantTempResponse.setBusinessType(objects.getBusinessType());
			subMerchantTempResponse.setEmail(Encryption.decString(objects.getEmail()));
			subMerchantTempResponse.setMobile(Encryption.decString(objects.getMobile()));
			subMerchantTempResponse.setIsActive(objects.getIsActive());
			subMerchantTempResponse.setIsOnboarding(objects.getIsOnboarding());
			subMerchantTempResponse.setIsSubMerchant(objects.getIsSubMerchant());
			subMerchantTempResponse.setJsonResponse(objects.getJsonResponse());
			subMerchantTempResponse.setMerchantSubMerchantRequest(objects.getMerchantSubMerchantRequest());
			subMerchantTempResponse.setMerchantSubMerchantResponse(objects.getMerchantSubMerchantResponse());
			subMerchantTempResponse.setToken(Encryption.decString(objects.getSubMerchantKey()));
			subMerchantTempResponse.setReason(objects.getReason());

			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_FOUND);
			map.put("data", subMerchantTempResponse);
			return map;
		}

		map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
		map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
		return map;
	}

	@Override
	public Map<String, Object> findBySubMerchantTempId(long merchantId, String subMerchantTempId) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();

		if (!DateUtil.isValidNumber(subMerchantTempId)) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Invalid Sub Merchant Id");
			return map;
		}

		SubMerchantTemp objects = subMerchantTempRepository
				.findBySubMerchantTempIdAndMerchantIdAndIsMerchant(Long.parseLong(subMerchantTempId), merchantId, '0');

		if (objects != null) {
			SubMerchantTempResponse subMerchantTempResponse = new SubMerchantTempResponse();

			String date = DateAndTime.dateFormatReports(objects.getDate().toString());

			subMerchantTempResponse.setLogo("NA");
			subMerchantTempResponse.setSubMerchantTempId(objects.getSubMerchantTempId());
			subMerchantTempResponse.setsNo(1);
			subMerchantTempResponse.setMerchantId(objects.getMerchantId());
			subMerchantTempResponse.setDate(date);
			subMerchantTempResponse.setSubMerchantName(Encryption.decString(objects.getSubMerchantName()));
			subMerchantTempResponse.setMccCode(objects.getMccCode());
			subMerchantTempResponse.setBusinessType(objects.getBusinessType());
			subMerchantTempResponse.setEmail(Encryption.decString(objects.getEmail()));
			subMerchantTempResponse.setMobile(Encryption.decString(objects.getMobile()));
			subMerchantTempResponse.setIsActive(objects.getIsActive());
			subMerchantTempResponse.setIsOnboarding(objects.getIsOnboarding());
			subMerchantTempResponse.setIsSubMerchant(objects.getIsSubMerchant());
			subMerchantTempResponse.setJsonResponse(objects.getJsonResponse());
			subMerchantTempResponse.setMerchantSubMerchantRequest(objects.getMerchantSubMerchantRequest());
			subMerchantTempResponse.setMerchantSubMerchantResponse(objects.getMerchantSubMerchantResponse());
			subMerchantTempResponse.setToken(Encryption.decString(objects.getSubMerchantKey()));
			subMerchantTempResponse.setReason(objects.getReason());

			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_FOUND);
			map.put("data", subMerchantTempResponse);
			return map;
		}

		map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
		map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
		return map;
	}

	@Override
	public Map<String, Object> findByToken(String token) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		SubMerchantTemp objects = subMerchantTempRepository.findBySubMerchantKey(Encryption.encString(token));
		if (objects != null) {
			SubMerchantTempResponse subMerchantTempResponse = new SubMerchantTempResponse();

			String date = DateAndTime.dateFormatReports(objects.getDate().toString());

			String logo = merchantInfoRepository.findImageUrl(objects.getMerchantId());
			subMerchantTempResponse.setLogo(logo);
			subMerchantTempResponse.setSubMerchantTempId(objects.getSubMerchantTempId());
			subMerchantTempResponse.setsNo(1);
			subMerchantTempResponse.setMerchantId(objects.getMerchantId());
			subMerchantTempResponse.setDate(date);
			subMerchantTempResponse.setSubMerchantName(Encryption.decString(objects.getSubMerchantName()));
			subMerchantTempResponse.setMccCode(objects.getMccCode());
			subMerchantTempResponse.setBusinessType(objects.getBusinessType());
			subMerchantTempResponse.setEmail(Encryption.decString(objects.getEmail()));
			subMerchantTempResponse.setMobile(Encryption.decString(objects.getMobile()));
			subMerchantTempResponse.setIsActive(objects.getIsActive());
			subMerchantTempResponse.setIsOnboarding(objects.getIsOnboarding());
			subMerchantTempResponse.setIsSubMerchant(objects.getIsSubMerchant());
			subMerchantTempResponse.setJsonResponse(objects.getJsonResponse());
			subMerchantTempResponse.setMerchantSubMerchantRequest(objects.getMerchantSubMerchantRequest());
			subMerchantTempResponse.setMerchantSubMerchantResponse(objects.getMerchantSubMerchantResponse());
			subMerchantTempResponse.setToken(Encryption.decString(objects.getSubMerchantKey()));
			subMerchantTempResponse.setReason(objects.getReason());

			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_FOUND);
			map.put("data", subMerchantTempResponse);
			return map;
		}

		map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
		map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
		return map;
	}

	@Override
	public Map<String, Object> sendOTPPhone(String mobile) throws Exception {
		Map<String, Object> map = new HashMap<>();

		if (!isValidMobileNo(mobile) || mobile.equals("")) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Please enter your 10 digit mobile number");
			return map;
		}

		Optional<SubMerchantTemp> optional = subMerchantTempRepository.findByMobile(Encryption.encString(mobile));

		if (optional.isPresent()) {

			if (optional.get().getIsActive() == '1' || optional.get().getIsMerchant() == '1') {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Sub Merchant Is Already Active");
				return map;
			}

			String logo = merchantInfoRepository.findImageUrl(optional.get().getMerchantId());
			String otp = RandomNumberGenrator.generateWalletPin();
			String merchantName = Encryption.decString(optional.get().getSubMerchantName());
			mobile = Encryption.decString(optional.get().getMobile());
			String subMerchantKey = Encryption.decString(optional.get().getSubMerchantKey());
			LOGGER.info("merchantName " + merchantName);

			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());
			String merchantUniqueId = "OTPSUBM" + mobile + GenerateTrxnRefId.getAlphaNumericString(8) + otp;
			SMSAPIImpl impl = new SMSAPIImpl();
			impl.registrationOTP(mobile, merchantName, otp);

			OTPVerification otpVerification = otpVerificationRepository.save(new OTPVerification(1, trxnDate, otp,
					merchantUniqueId, "NA", Encryption.encString(mobile), Encryption.encString(subMerchantKey)));

			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, "OTP Generated Successfully");
			map.put("otpToken", otpVerification.getOtpRefId());
			map.put("token", subMerchantKey);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			map.put("logo", logo);
			return map;
		}

		map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
		map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);

		return map;
	}

	@Override
	public Map<String, Object> otpVerification(String otp, String otpToken) throws Exception {
		Map<String, Object> map = new HashMap<>();

		Optional<OTPVerification> otpVerification = otpVerificationRepository.findOtpANDOtpRefId(1, otp, otpToken);

		if (!otpVerification.isPresent()) {
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.OTP_BANKINFO_FAILED);
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			return map;
		}

		Timestamp currentTime = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

		long transactionTimeValidate = DateAndTime.compareTwoTimeStamps(currentTime,
				otpVerification.get().getCreationDate());

		if (transactionTimeValidate >= 180) {
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.OTP_BANKINFO_EXPIRED);
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			return map;
		}

		map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
		map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
		map.put(ResponseMessage.DESCRIPTION, ResponseMessage.VALID_OTP_MERTRXNREFID);
		map.put("otpToken", otpVerification.get().getOtpRefId());
		map.put("token", Encryption.decString(otpVerification.get().getMerchantBankAccountNumber()));

		return map;
	}

	@Override
	public Map<String, Object> updateDetails(@Valid MerchantSubMerchantOnboardingRequest onboardingFormRequest,
			String token) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			SubMerchantTemp subMerchantTemp = subMerchantTempRepository
					.findBySubMerchantKey(Encryption.encString(token));

			char isVerPanCardAuthorizer = onboardingFormRequest.getIsVerPanCardAuthorizer().charAt(0);
			char isVerAadhaarCardAuthorizer = onboardingFormRequest.getIsVerAadhaarCardAuthorizer().charAt(0);
			// char isVerGstNumber = onboardingFormRequest.getIsVerGstNumber().charAt(0);
			char isVerImageLiveness = onboardingFormRequest.getIsVerImageLiveness().charAt(0);
			// char isVerSignatureImage =
			// onboardingFormRequest.getIsVerSignatureImage().charAt(0);
			char isAccountVerification = onboardingFormRequest.getIsAccountVerification().charAt(0);
			char isCancelCheque = onboardingFormRequest.getIsVerCancelCheque().charAt(0);
			char isAadharPanLink = onboardingFormRequest.getIsPanAadhaarLink().charAt(0);
			char isConcern = onboardingFormRequest.getAdditionInfo5().charAt(0);
			String mccCode = onboardingFormRequest.getMcc();
			char isOnboarding = '0';

			if (isVerPanCardAuthorizer == '1' && isVerAadhaarCardAuthorizer == '1' && isVerImageLiveness == '1'
					&& isAccountVerification == '1' && isConcern == '1') {
				isOnboarding = '1';
			}

			String fullName = onboardingFormRequest.getFirstNameOfAuthorisedSignatory() + " "
					+ onboardingFormRequest.getLastNameOfAuthorisedSignatory();

			if ((isCancelCheque == '1' && isAccountVerification == '2')
					|| (isAadharPanLink == '2' && isVerPanCardAuthorizer == '2')) {

				isOnboarding = '0';
				String typesDocumentFailed = "";
				if (isAccountVerification == '2') {
					typesDocumentFailed = "Bank Account Verification";
				}

				if (isVerPanCardAuthorizer == '2') {
					typesDocumentFailed = typesDocumentFailed + ", Pan Card";
				}

				// reject mail
				String sendEmail = EmailNotification.sendEmailForDocumentRejectCase(ResponseMessage.ADMIN_URL,
						onboardingFormRequest.getMerchantEmail(), onboardingFormRequest.getMerchantPhone(), fullName,
						onboardingFormRequest.getMerchantBusinessName(), onboardingFormRequest.getDescription(),
						typesDocumentFailed);
				LOGGER.info("EMAIL " + sendEmail);
			}

			if (subMerchantTemp != null) {

				LOGGER.info("(subMerchantTemp != null)");
				ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
				String json = ow.writeValueAsString(onboardingFormRequest);
				subMerchantTemp.setJsonResponse(json);
				subMerchantTemp.setIsOnboarding(isOnboarding);
				subMerchantTemp.setMccCode(mccCode);
				subMerchantTempRepository.save(subMerchantTemp);
				long merchantId = subMerchantTemp.getMerchantId();

				if (isOnboarding == '0') {

					map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
					map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UPDATE_SUCCEESSFULLY);
					map.put("isOnboarding", isOnboarding);
					map.put("token", token);
					map.put("data", subMerchantTemp.getJsonResponse());
					return map;
				}

				if (isOnboarding == '1') {

					LOGGER.info("(subMerchantTemp != null)");
					String bankId = subMerchantTemp.getBankID();
					switch (bankId) {
					case "Yes Bank":
						break;
					case "Airtel Bank":
						map = airtelBankUPI(merchantId, token, fullName, subMerchantTemp.getJsonResponse(),
								isOnboarding, mccCode, bankId);
						break;
					default:
						map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
						map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
						map.put(ResponseMessage.DESCRIPTION, "Invalid Bank ID");
						break;
					}
					return map;
				}

			}
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVER_DOWN);
		}
		return map;
	}

	@Override
	public Map<String, Object> resendNotification(long merchantId, String subMerchantTempId,
			String merchantBusinessName, String logo) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();

		if (!DateUtil.isValidNumber(subMerchantTempId)) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Invalid Sub Merchant Id");
			return map;
		}

		SubMerchantTemp objects = subMerchantTempRepository
				.findBySubMerchantTempIdAndMerchantId(Long.parseLong(subMerchantTempId), merchantId);

		if (objects != null) {

			if (objects.getIsActive() == '1' || objects.getIsMerchant() == '1') {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Sub Merchant Is Already Active");
				return map;
			}

			String token = Encryption.decString(objects.getSubMerchantKey());
			String mobile = Encryption.decString(objects.getMobile());
			String email = Encryption.decString(objects.getEmail());
			String name = Encryption.decString(objects.getSubMerchantName());
			String link = ResponseMessage.LINK_UPI_KYC + token;

			if (!logo.equalsIgnoreCase("NA")) {
				logo = BUCKET_URL_LOGO + logo;
			}

			if (email != null && !email.equals("NA")) {
				String sendEmail = EmailNotification.sendEmailForUPIOnboardingKyc(email, link, merchantBusinessName,
						name, logo);
				LOGGER.info("EMAIL " + sendEmail);
			}

			String url = URLGenerater.generateShortUrl(link);
			String response = new SMSAPIImpl().upiKyc(mobile, merchantBusinessName, url);

			LOGGER.info("SMS " + response);
			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.NOTIFICATION_SEND);

			return map;
		}

		map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
		map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
		return map;
	}

	@Override
	public Map<String, Object> saveMerchantSubMerchant(String from, String to) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();

		if ((from == null || from == "") && (to == null || to == "")) {
			String pattern = "yyyy-MM-dd";
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("IST"));
			cal.add(Calendar.DATE, -5);
			Date todate1 = cal.getTime();
			from = new SimpleDateFormat(pattern).format(todate1);
			to = new SimpleDateFormat(pattern).format(new Date());
		}
		from = from + " 00.00.00";
		to = to + " 23.59.59";

		LOGGER.info("From : " + from + " , To : " + to);

		List<SubMerchantTemp> list = subMerchantTempRepository
				.findByIsActiveAndIsSubMerchantAndIsOnboardingAndDate(from, to);
		LOGGER.info("Size " + list.size());
		if (list.isEmpty()) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
			return map;
		}

		if (list.size() > 0) {
			for (SubMerchantTemp subMerchantTemp : list) {
				long subMerchantTempId = subMerchantTemp.getSubMerchantTempId();
				long merchantId = subMerchantTemp.getMerchantId();
				LOGGER.info("subMerchantTempId : " + subMerchantTempId);
				// Save Sub Merchant
				Map<String, Object> responseSubMerchant = saveMerchantSubMerchant(merchantId, subMerchantTemp);
				LOGGER.info("responseSubMerchant " + responseSubMerchant);
				// update data
				int updateData = subMerchantTempRepository.updateOnboardingIsMerchant('1', subMerchantTempId);
				LOGGER.info("updateData " + updateData);
			}
		}
		map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
		map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
		map.put(ResponseMessage.DESCRIPTION, "Sub Merchant Temp scheduler");
		return map;
	}

	@SuppressWarnings("unused")
	private Map<String, Object> saveMerchantSubMerchant(long merchantId, SubMerchantTemp subMerchantTemp)
			throws Exception {
		Map<String, Object> map = new HashMap<>();

		// JSONObject jsonObj = new JSONObject(subMerchantTemp.getJsonResponse());
		JSONObject jsonObj1 = new JSONObject(subMerchantTemp.getVpaCallBack());
		JSONObject jsonObj3 = new JSONObject(subMerchantTemp.getMerchantSubMerchantRequest());

		LOGGER.info("merchant id :" + merchantId);
		LOGGER.info("jsonObj :" + jsonObj3);

		String VPA = jsonObj1.getString("payeeVPA");
		String bankId = subMerchantTemp.getBankID();
		String businessType = subMerchantTemp.getBusinessType();

		MerchantSubMerchantInfoV2 merchantInfoV2 = merchantSubMerchantInfoRepository.findBySubmerchantVpa(VPA);

		if (merchantInfoV2 != null) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Already exist");
			return map;
		}

		String fullName = jsonObj3.getString("subMerchantName");
		String businessName = jsonObj3.getString("merchantBussinessName");
		String phone = jsonObj3.getString("mobileNo");
		String subMerchantId = jsonObj1.getString("subMerchantId");
		String email = jsonObj3.getString("email");
		String qrString = jsonObj1.getString("qrString");
		String panCarNo = jsonObj3.getString("pan");
		String mcc = jsonObj3.getString("mcc");
		String gstNumber = jsonObj3.getString("gstn");
		String merchantBankIfsc = jsonObj3.getString("ifscCode");
		String merchantBankName = jsonObj3.getString("subMerchantBankName");
		String merchantBankAccountNumber = jsonObj3.getString("accountNumber");
		Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

		MerchantSubMerchantInfoV2 m = new MerchantSubMerchantInfoV2();
		m.setSubMerchantName(fullName);
		m.setSubMerchantBussinessName(businessName);
		m.setSubMerchantType("ACTIVE");
		m.setSubMerchantMobileNumber(phone);
		m.setSubMerchantAdditionalInfo(VPA);
		m.setSubMerchantId(subMerchantId);
		m.setSoundTId("NA");
		m.setMerchantId(merchantId);
		m.setSubMerchantAction("ADD Submerchant");
		m.setSubMerchantStatus("SUCCESS");
		m.setIsDeleted('N');
		m.setSubMerchantKey("NA");
		m.setSubMerchantDate(trxnDate);
		m.setSubMerchanModifiedtDate(trxnDate);
		m.setSubMerchantEmailId(email);
		m.setSubMerchantEditAction("NA");
		m.setBankId(bankId);
		m.setSubMerchantQRString(qrString);
		m.setSubMerchantPan(panCarNo);
		m.setSubMerchantMCC(mcc);
		m.setSubMerchantGst(gstNumber);
		m.setSoundBoxLanguage("0");
		m.setSoundBoxProvider("NA");

		JSONObject jsonObject = createKycJson(subMerchantTemp.getJsonResponse());
		m.setOtherDocument(jsonObject.toString());

		String bankDetail = merchantBankDetail(merchantBankIfsc, merchantBankName, merchantBankAccountNumber);
		m.setSubMerchantBankDetails(bankDetail);

		String subMerchantInfo = submerchantInfo(VPA, subMerchantId);
		m.setSubMerchantInfo(subMerchantInfo);

		String submerchantRegisterInfo = merchantRegisterInfo("NA", mcc, gstNumber, phone, merchantBankAccountNumber,
				merchantBankIfsc, businessName, email, "10", "10", "10", jsonObj3.getString("address1"),
				jsonObj3.getString("address2"), jsonObj3.getString("state"), jsonObj3.getString("city"),
				jsonObj3.getString("pinCode"), subMerchantId, "OFFLINE", fullName, VPA, merchantBankName, businessType,
				"NA");
		m.setSubMerchantRegisterInfo(submerchantRegisterInfo);

		String submerchantUserRequest = merchantRegisterInfo("NA", mcc, gstNumber, phone, merchantBankAccountNumber,
				merchantBankIfsc, businessName, email, "10", "10", "10", jsonObj3.getString("address1"),
				jsonObj3.getString("address2"), jsonObj3.getString("state"), jsonObj3.getString("city"),
				jsonObj3.getString("pinCode"), subMerchantId, "OFFLINE", fullName, VPA, merchantBankName, businessType,
				"NA");
		m.setSubMerchantUserRequest(submerchantUserRequest);
		merchantSubMerchantInfoRepository.save(m);

		Merchants merchants = merchantsRepository.findMerchant(merchantId);
		String VERTICAL_REGIONS = merchants.getVerticalRegions();
		String merchantEmail = Encryption.decString(merchants.getMerchantEmail());
		LOGGER.info("VERTICAL_REGIONS " + VERTICAL_REGIONS);
		if (VERTICAL_REGIONS.equalsIgnoreCase("Co-operative")) {

			Map<String, Object> mapCoopBank = subMerchantRequestCOOP(merchantBankAccountNumber, merchantBankIfsc,
					jsonObj3.getString("address1"), merchantBankIfsc, jsonObj3.getString("city"), email, fullName,
					merchantBankName, businessName, phone, jsonObj3.getString("pinCode"), jsonObj3.getString("state"),
					subMerchantId, submerchantUserRequest, subMerchantInfo, VPA, qrString);
		}

		// mail for QR Code generate

		MerchantInfo merchantInfo = infoRepository.findByMerchantId(merchantId);
		String logo = merchantInfo.getImageUrl();

		if (!logo.equalsIgnoreCase("NA")) {
			logo = BUCKET_URL_LOGO + logo;
		}

		String mailResponse = EmailNotification.sendEmailForQRCodeGeneration(email,
				Encryption.decString(merchantInfo.getMerchantBusinessName()), fullName, logo, businessName, VPA,
				VERTICAL_REGIONS, merchantEmail);

		map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
		map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_SUCCESS);
		map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UPDATE_SUCCEESSFULLY);
		return map;

	}

	private String merchantRegisterInfo(String panNo, String mCC, String gst, String mobileNo, String accountNumber,
			String iFSCCode, String merchantBussinessName, String email, String perDayTxnCount, String perDayTxnAmt,
			String perDayTxnLmt, String address1, String address2, String state, String city, String pinCode,
			String subMerchantId, String merchantGenre, String merchantLegalName, String vPA, String bankName,
			String merchantBussinessType, String branchName) {
		JsonObject merchantRegisterInfo = new JsonObject();
		merchantRegisterInfo.addProperty("action", "C");
		merchantRegisterInfo.addProperty("merchantBussiessName", merchantBussinessName);
		merchantRegisterInfo.addProperty("panNo", panNo);
		merchantRegisterInfo.addProperty("contactEmail", email);
		merchantRegisterInfo.addProperty("gstn", gst);
		merchantRegisterInfo.addProperty("perDayTxnCount", perDayTxnCount);
		merchantRegisterInfo.addProperty("merchantBussinessType", merchantBussinessType);
		merchantRegisterInfo.addProperty("perDayTxnLmt", perDayTxnLmt);
		merchantRegisterInfo.addProperty("perDayTxnAmt", perDayTxnAmt);
		merchantRegisterInfo.addProperty("mobile", mobileNo);
		String address = address1 + " " + address2;
		merchantRegisterInfo.addProperty("address", address);
		merchantRegisterInfo.addProperty("state", state);
		merchantRegisterInfo.addProperty("city", city);
		merchantRegisterInfo.addProperty("pinCode", pinCode);
		merchantRegisterInfo.addProperty("subMerchantId", subMerchantId);
		merchantRegisterInfo.addProperty("MCC", mCC);
		merchantRegisterInfo.addProperty("subMerchantBankName", bankName);
		merchantRegisterInfo.addProperty("subMerchantBankAccount", accountNumber);
		merchantRegisterInfo.addProperty("subMerchantIfscCode", iFSCCode);
		merchantRegisterInfo.addProperty("merchantGenre", merchantGenre);
		merchantRegisterInfo.addProperty("merchantVirtualAddress", vPA);
		merchantRegisterInfo.addProperty("name", merchantLegalName);
		merchantRegisterInfo.addProperty("subMerchantBankBranch", branchName);

		return merchantRegisterInfo.toString();
	}

	private static String submerchantInfo(String vPA, String subMerchantId) {
		JsonObject submerchantInfJson = new JsonObject();
		submerchantInfJson.addProperty("ypHubUsername", vPA);
		submerchantInfJson.addProperty("status", "SUCCESS");
		submerchantInfJson.addProperty("responseCode", "00");
		submerchantInfJson.addProperty("sellerIdentifier", subMerchantId);
		return submerchantInfJson.toString();
	}

	private static String merchantBankDetail(String iFSCCode, String bankName, String accountNumber) {
		JsonObject sbmerchantBankDetailsJson = new JsonObject();
		sbmerchantBankDetailsJson.addProperty("subMerchantIfscCode", iFSCCode);
		sbmerchantBankDetailsJson.addProperty("subMerchantBankName", bankName);
		sbmerchantBankDetailsJson.addProperty("subMerchantBankAccount", accountNumber);

		return sbmerchantBankDetailsJson.toString();
	}

	private JSONObject createKycJson(String subMerchnatUser) {
		LOGGER.info("Inside the createKycJson");
		JSONObject jsonObj = new JSONObject(subMerchnatUser);

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("ph_number", jsonObj.getString("merchantPhone"));
		jsonObject.put("panCardAuthorizerKey", jsonObj.getString("panCardAuthorizerKey"));
		jsonObject.put("isVerPanCardAuthorizer", "1");
		jsonObject.put("isVerAadhaarCardAuthorizer", "1");
		jsonObject.put("aadhaarCardAuthorizerKey", jsonObj.getString("aadhaarCardAuthorizerKey"));
		jsonObject.put("gstNumber", jsonObj.getString("gstNumber"));
		jsonObject.put("signatureImageKey", jsonObj.getString("signatureImageKey"));
		jsonObject.put("isVerSignatureImage", "1");
		jsonObject.put("imageLivenessKey", jsonObj.get("imageLivenessKey"));
		jsonObject.put("isVerImageLiveness", "1");
		jsonObject.put("gstKey", "NA");
		jsonObject.put("isVerGST", "2");
		jsonObject.put("cancelChecqueKey", "NA");
		jsonObject.put("isVerCancelCheque", "2");
		return jsonObject.put("documents", jsonObject.toString());
	}

	@Override
	public Map<String, Object> findByTokenNew(String token) throws Exception {

		Map<String, Object> map = new HashMap<String, Object>();
		SubMerchantTemp objects = subMerchantTempRepository.findBySubMerchantKey(Encryption.encString(token));
		if (objects != null) {

			if (objects.getIsActive() == '1' || objects.getIsMerchant() == '1') {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Sub Merchant Is Already Active");
				return map;
			}

			SubMerchantTempResponse subMerchantTempResponse = new SubMerchantTempResponse();

			String date = DateAndTime.dateFormatReports(objects.getDate().toString());

			String logo = merchantInfoRepository.findImageUrl(objects.getMerchantId());
			subMerchantTempResponse.setLogo(logo);
			subMerchantTempResponse.setsNo(1);
			subMerchantTempResponse.setDate(date);
			subMerchantTempResponse.setSubMerchantName(Encryption.decString(objects.getSubMerchantName()));
			subMerchantTempResponse.setMobile(Encryption.decString(objects.getMobile()));
			subMerchantTempResponse.setToken(Encryption.decString(objects.getSubMerchantKey()));

			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_FOUND);
			map.put("data", subMerchantTempResponse);
			return map;
		}

		map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
		map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
		return map;

	}

	private Map<String, Object> airtelBankUPI(long merchantId, String token, String fullName, String jsonResponseTemp,
			char isOnboarding, String mccCode, String bankId) {
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			SubMerchantTemp objects = subMerchantTempRepository.findBySubMerchantKey(Encryption.encString(token));
			if (objects != null) {
				String jsonResponse = objects.getJsonResponse();

				LOGGER.info("jsonResponse : {}", jsonResponse);
				JSONObject jsonObj = new JSONObject(jsonResponse);

				String panImageURL = BUCKET_URL_DOCS + jsonObj.get("panCardAuthorizerKey");
				String aadhaarImageURL = BUCKET_URL_DOCS + jsonObj.get("aadhaarCardAuthorizerKey");
				String merchantPhotoImageURL = BUCKET_URL_DOCS + jsonObj.get("imageLivenessKey");
				String signatureImageURL = BUCKET_URL_DOCS + jsonObj.get("signatureImageKey");

				String gstNumber = jsonObj.getString("gstNumber");
				if (gstNumber.equalsIgnoreCase("NA") || gstNumber == "null") {
					gstNumber = "";
				}

				String dob = jsonObj.getString("dob");
				if (dob.equalsIgnoreCase("NA") || dob == "null") {
					dob = "";
				}

				String doi = jsonObj.getString("doi");
				if (doi.equalsIgnoreCase("NA") || doi == "null") {
					doi = "";
				}
				String vpa = merchantId + GenerateTrxnRefId.getNumericString(6) + GenerateTrxnRefId.getAlphaString(4);
				LOGGER.info("vpa : {}", vpa);

				String requestStr = "{ \"aadhaarImageURL\": \"" + aadhaarImageURL + "\", \"accountNumber\": \""
						+ jsonObj.get("merchantBankAccountNumber") + "\", \"address1\": \"" + jsonObj.get("address1")
						+ "\", \"address2\": \"" + jsonObj.get("address2") + "\", \"bankId\": \"" + bankId
						+ "\", \"city\": \"" + jsonObj.get("city") + "\", \"email\": \"" + jsonObj.get("merchantEmail")
						+ "\", \"gstn\": \"" + gstNumber + "\", \"ifscCode\": \"" + jsonObj.get("merchantBankIfsc")
						+ "\", \"mcc\": \"" + jsonObj.get("mcc") + "\", \"merchantBussinessName\": \""
						+ jsonObj.get("merchantBusinessName") + "\", \"merchantBussinessType\": \""
						+ jsonObj.get("ownerShipType")
						+ "\", \"merchantGenre\": \"OFFLINE\", \"merchantPhotoImageURL\": \"" + merchantPhotoImageURL
						+ "\", \"merchnatCatagoryCode\": \"" + jsonObj.get("mcc") + "\", \"mobileNo\": \""
						+ jsonObj.get("merchantPhone") + "\", \"pan\": \"" + jsonObj.get("panCardNo")
						+ "\", \"panImageURL\": \"" + panImageURL + "\", \"pinCode\": \"" + jsonObj.get("zipCode")
						+ "\", \"signatureImageURL\": \"" + signatureImageURL + "\", \"state\": \""
						+ jsonObj.get("stateCode") + "\", \"subMerchantBankName\": \"" + jsonObj.get("merchantBankName")
						+ "\", \"subMerchantDOB\": \"" + dob + "\", \"subMerchantDOI\": \"" + jsonObj.get("doi")
						+ "\", \"subMerchantId\": \"" + jsonObj.get("subMerchantId") + "\", \"subMerchantLatitude\": \""
						+ jsonObj.get("additionInfo1") + "\", \"subMerchantLongitude\": \""
						+ jsonObj.get("additionInfo2") + "\", \"subMerchantName\": \"" + fullName + "\"}";

				LOGGER.info("requestStr : " + requestStr);

				String apiResponse = airtelPaymentsService.subMerchantOnBoarding(jsonObj.getString("merchantPhone"),
						jsonObj.getString("merchantBusinessName"), jsonObj.getString("mcc"),
						jsonObj.getString("address1"), fullName, fullName, jsonObj.getString("panCardNo"), gstNumber,
						vpa, merchantId, requestStr);

				LOGGER.info("apiresponse: {}", apiResponse);
				JSONObject responseJson = new JSONObject(apiResponse);
				if ((responseJson.get("code")).equals("0x0200")) {

					JSONObject jsonObject = new JSONObject();
					jsonObject.put("subMerchantId", responseJson.get("subMerchantId"));
					jsonObject.put("is_vpa_registered", responseJson.get("is_vpa_registered"));
					jsonObject.put("payeeVPA", responseJson.get("payeeVPA"));
					jsonObject.put("qrString", responseJson.get("qrString"));
					jsonObject.put("mobileNo", responseJson.get("mobileNo"));

					objects.setMccCode(mccCode);
					objects.setMerchantSubMerchantRequest(requestStr);
					objects.setMerchantSubMerchantResponse(apiResponse);
					objects.setVpaCallBack(jsonObject.toString());
					objects.setIsSubMerchant('1');
					objects.setIsActive('1');
					subMerchantTempRepository.save(objects);

					map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
					map.put(ResponseMessage.DESCRIPTION, responseJson.get("description"));
					map.put("isOnboarding", objects.getIsOnboarding());
					map.put("token", token);
					map.put("data", objects.getJsonResponse());

					return map;
				} else {
					objects.setMerchantSubMerchantRequest(requestStr);
					objects.setMerchantSubMerchantResponse(apiResponse);
					objects.setMccCode(mccCode);
					objects.setIsSubMerchant('0');
					objects.setIsActive('2');
					subMerchantTempRepository.save(objects);
					@SuppressWarnings("unchecked")
					Map<String, Object> responseMap = new ObjectMapper().readValue(apiResponse, HashMap.class);
					return responseMap;
				}

			}
			return map;
		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage());
			map.put("isOnboarding", isOnboarding);
			map.put("token", token);
			map.put("data", jsonResponseTemp);
		}

		return map;
	}

	@Override
	public Map<String, Object> verifySubMerchant(String subMerchantTempId, String isOnboarding) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();

		if (!DateUtil.isValidNumber(subMerchantTempId)) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Invalid Sub Merchant Id");
			return map;
		}

		if (!isOnboarding.equalsIgnoreCase("1") && !isOnboarding.equalsIgnoreCase("0")
				&& !isOnboarding.equalsIgnoreCase("2")) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Invalid isOnboarding Please enter 0 or 1 or 2");
			return map;
		}

		SubMerchantTemp objects = subMerchantTempRepository.findById(Long.parseLong(subMerchantTempId)).get();

		if (objects != null) {

			if (objects.getIsActive() == '1' || objects.getIsMerchant() == '1') {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Sub Merchant Is Already Active");
				return map;
			}

			String jsonResponse = objects.getJsonResponse();
			ObjectMapper objectMapper = new ObjectMapper();
			MerchantSubMerchantOnboardingRequest onboardingFormRequest = objectMapper.readValue(jsonResponse,
					MerchantSubMerchantOnboardingRequest.class);

			char isVerPanCardAuthorizer = onboardingFormRequest.getIsVerPanCardAuthorizer().charAt(0);
			char isVerAadhaarCardAuthorizer = onboardingFormRequest.getIsVerAadhaarCardAuthorizer().charAt(0);
			char isVerImageLiveness = onboardingFormRequest.getIsVerImageLiveness().charAt(0);
			// char isVerSignatureImage =
			// onboardingFormRequest.getIsVerSignatureImage().charAt(0);
			char isAccountVerification = onboardingFormRequest.getIsAccountVerification().charAt(0);
			// char isCancelCheque = onboardingFormRequest.getIsVerCancelCheque().charAt(0);
			// char isAadharPanLink = onboardingFormRequest.getIsPanAadhaarLink().charAt(0);

			isOnboarding = "0";

			if (isVerPanCardAuthorizer == '1' && isVerAadhaarCardAuthorizer == '1' && isVerImageLiveness == '1'
					&& isAccountVerification == '1') {
				isOnboarding = "1";
			}

			objects.setIsOnboarding(isOnboarding.charAt(0));
			subMerchantTempRepository.save(objects);

			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UPDATE_SUCCEESSFULLY);

			return map;
		}

		map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
		map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
		return map;
	}

	// coop bank api

	private Map<String, Object> subMerchantRequestCOOP(String accountNumber, String ifsc, String address,
			String subMerchantBankBranch, String city, String contactEmail, String name, String subMerchantBankName,
			String merchnatBussiessName, String mobile, String pinCode, String state, String subMerchantId,
			String submerchantDTOJSON, String response, String subMerchantVPA, String qrString) {
		Map<String, Object> map = new HashMap<>();
		try {

			JSONObject jObject = new JSONObject();
			jObject.put("accountNumber", accountNumber);
			jObject.put("address", address);
			jObject.put("bankBranch", subMerchantBankBranch);
			jObject.put("city", city);
			jObject.put("contactEmail", contactEmail);
			jObject.put("name", name);
			jObject.put("ifsc", ifsc);
			jObject.put("merchantBankName", subMerchantBankName);
			jObject.put("merchnatBussiessName", merchnatBussiessName);
			jObject.put("mobile", mobile);
			jObject.put("password", "123456");
			jObject.put("pinCode", pinCode);
			jObject.put("state", state);
			jObject.put("subMerchantCreateResponse", response);
			jObject.put("subMerchantId", subMerchantId);
			jObject.put("subMerchantRequest", submerchantDTOJSON);
			jObject.put("subMerchantVPA", subMerchantVPA);
			jObject.put("isMessageActive", 'Y');
			jObject.put("qrUrl", qrString);
			jObject.put("subMerchantBussinessName", merchnatBussiessName);

			String jsonRequest = jObject.toString();

			LOGGER.info("Merchant Register Json Request : " + jsonRequest);
			OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(60, TimeUnit.SECONDS)
					.readTimeout(30, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS).build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, jsonRequest);
			Request request = new Request.Builder().url(ResponseMessage.MERCHANT_SUBMERCHANT_REGISTER_URL)
					.method("POST", body).addHeader("Content-Type", "application/json").build();
			LOGGER.info("Request Merchant Register Api : " + request);

			Response resp = client.newCall(request).execute();
			String results = resp.body().string();
			LOGGER.info("Response Merchant Register Api : " + results);

			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, "Merchant created successfully");
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			map.put("response", response);

		} catch (NullPointerException e) {
			LOGGER.error(" NullPointerException " + e);
			map.put(ResponseMessage.CODE, ResponseMessage.MISSING_PARAMETER);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.MISSING_PARAMETER_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		} catch (ConnectException e) {
			LOGGER.error(" ConnectException " + e);
			map.put(ResponseMessage.CODE, ResponseMessage.CONNECTION_TIMEOUT);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.CONNECTION_TIMEOUT_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

		} catch (Exception e) {
			LOGGER.error(" Exception " + e);
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		LOGGER.info("Sub Merchant Response " + response);
		return map;
	}

	@Override
	public Map<String, Object> saveMerchantSubMerchantByTempId(String subMerchantTempId) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();

		if (!DateUtil.isValidNumber(subMerchantTempId)) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Invalid Sub Merchant Id");
			return map;
		}

		SubMerchantTemp objects = subMerchantTempRepository.findBySubMerchantTempId(Long.parseLong(subMerchantTempId));

		if (objects != null) {

			if (objects.getIsMerchant() == '1') {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Sub Merchant Is Already Active");
				return map;
			}

			long merchantId = objects.getMerchantId();
			LOGGER.info("subMerchantTempId : " + subMerchantTempId);
			// Save Sub Merchant
			Map<String, Object> responseSubMerchant = saveMerchantSubMerchant(merchantId, objects);
			LOGGER.info("responseSubMerchant " + responseSubMerchant);
			// update data
			int updateData = subMerchantTempRepository.updateOnboardingIsMerchant('1',
					Long.parseLong(subMerchantTempId));
			LOGGER.info("updateData " + updateData);

		}

		map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
		map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
		return map;
	}

	@Override
	public Map<String, Object> resendNotificationByTempId(String subMerchantTempId) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();

		if (!DateUtil.isValidNumber(subMerchantTempId)) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Invalid Sub Merchant Id");
			return map;
		}

		SubMerchantTemp objects = subMerchantTempRepository.findById(Long.parseLong(subMerchantTempId)).get();

		if (objects != null) {

			if (objects.getIsActive() == '1' || objects.getIsMerchant() == '1') {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Sub Merchant Is Already Active");
				return map;
			}

			MerchantInfo merchantInfo = merchantInfoRepository.findByMerchantId(objects.getMerchantId());
			String token = Encryption.decString(objects.getSubMerchantKey());
			String mobile = Encryption.decString(objects.getMobile());
			String email = Encryption.decString(objects.getEmail());
			String name = Encryption.decString(objects.getSubMerchantName());
			String link = ResponseMessage.LINK_UPI_KYC + token;
			String merchantBusinessName = Encryption.decString(merchantInfo.getMerchantBusinessName());
			String logo = merchantInfo.getImageUrl();
			if (!logo.equalsIgnoreCase("NA")) {
				logo = BUCKET_URL_LOGO + logo;
			}

			if (email != null && !email.equals("NA")) {
				String sendEmail = EmailNotification.sendEmailForUPIOnboardingKyc(email, link, merchantBusinessName,
						name, logo);
				LOGGER.info("EMAIL " + sendEmail);
			}

			String url = URLGenerater.generateShortUrl(link);
			String response = new SMSAPIImpl().upiKyc(mobile, merchantBusinessName, url);

			LOGGER.info("SMS " + response);
			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.NOTIFICATION_SEND);

			return map;
		}

		map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
		map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
		return map;
	}

	@Override
	public Map<String, Object> createVPA(long merchantId, @Valid SubMerchantOnboardingRequest subMerchantRequest,
			String merchantBusinessName, String bankId, String logo) throws Exception {

		Map<String, Object> map = new HashMap<String, Object>();

		String mobile = subMerchantRequest.getPhoneNumber();
		String email = subMerchantRequest.getEmail();
		String mccCode = subMerchantRequest.getMcc();
		String businessType = subMerchantRequest.getBusinessType();
		String subMerchantName = subMerchantRequest.getFullName();
		String notification = subMerchantRequest.getNotification();

		if (subMerchantTempRepository.existsByMobile(Encryption.encString(mobile))) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.PHONE_ALREADY_ACTIVATED);
			return map;
		}

		if ((!email.equalsIgnoreCase("NA")) && (subMerchantTempRepository.existsByEmail(Encryption.encString(email)))) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.EMAIL_ALREADY_ACTIVATED);
			return map;
		}

		String serviceName = "REGISTER UPI MERCHANTS";
		if (!merchantUserService.checkServiceExistOrNot(merchantId, serviceName)) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
			return map;
		}

		List<SubMerchantTemp> existingInfos = subMerchantTempRepository
				.findByEmailAndMobile(Encryption.encString(email), Encryption.encString(mobile));

		if (!existingInfos.isEmpty()) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.EMAIL_AND_MOBILE_ALREADY_ACTIVATED);
		} else {
			SubMerchantTemp info = new SubMerchantTemp();
			String subMerchantKey = merchantId + "SM" + mobile + GenerateTrxnRefId.getAlphaNumericString(6);
			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());
			info.setMerchantId(merchantId);
			info.setDate(trxnDate);
			info.setSubMerchantName(Encryption.encString(subMerchantName));
			info.setMccCode(mccCode);
			info.setBusinessType(businessType);
			info.setEmail(Encryption.encString(email));
			info.setMobile(Encryption.encString(mobile));
			info.setSubMerchantKey(Encryption.encString(subMerchantKey));
			info.setJsonResponse("NA");
			info.setIsActive('0');
			info.setMerchantSubMerchantRequest("NA");
			info.setMerchantSubMerchantResponse("NA");
			info.setIsSubMerchant('0');
			info.setIsOnboarding('0');
			info.setIsMerchant('0');
			info.setBankID(bankId);
			info.setReason("NA");
			info.setVpaCallBack("NA");
			info.setMerchantUserId(0L);

			subMerchantTempRepository.save(info);
			Long subMerchantTempId = info.getSubMerchantTempId();
			LOGGER.info("subMerchantTempId " + subMerchantTempId);

			if (notification.equals("true")) {
				resendNotification(merchantId, String.valueOf(subMerchantTempId), merchantBusinessName, logo);
			}

			String link = ResponseMessage.LINK_UPI_KYC + subMerchantKey;
			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			map.put("url", link);
			map.put("token", subMerchantKey);

			return map;

		}

		return map;

	}

	// PWA APIs

	@Override
	public Map<String, Object> createVPAPWA(long merchantId, @Valid SubMerchantOnboardingRequest subMerchantRequest,
			String merchantBusinessName, String bankId, String logo, String merchantUserId) throws Exception {

		Map<String, Object> map = new HashMap<String, Object>();

		String mobile = subMerchantRequest.getPhoneNumber();
		String email = subMerchantRequest.getEmail();
		String mccCode = subMerchantRequest.getMcc();
		String businessType = subMerchantRequest.getBusinessType();
		String subMerchantName = subMerchantRequest.getFullName();

		if (merchantUserId.equalsIgnoreCase("NA") || merchantUserId.equals("")
				|| !DateUtil.isValidNumber(merchantUserId) || merchantUserId.equalsIgnoreCase("0")
				|| !merchantUserRepository.existsByMerchantUserIdAndIsActiveAndMerchantId(
						Long.parseLong(merchantUserId), '1', merchantId)) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Invalid merchantUserId");
			return map;
		}

		if (subMerchantTempRepository.existsByMobile(Encryption.encString(mobile))) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.PHONE_ALREADY_ACTIVATED);
			return map;
		}

		if ((!email.equalsIgnoreCase("NA")) && (subMerchantTempRepository.existsByEmail(Encryption.encString(email)))) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.EMAIL_ALREADY_ACTIVATED);
			return map;
		}

		String serviceName = "REGISTER UPI MERCHANTS";
		if (!merchantUserService.checkServiceExistOrNot(merchantId, serviceName)) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
			return map;
		}

		List<SubMerchantTemp> existingInfos = subMerchantTempRepository
				.findByEmailAndMobile(Encryption.encString(email), Encryption.encString(mobile));

		if (!existingInfos.isEmpty()) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.EMAIL_AND_MOBILE_ALREADY_ACTIVATED);
		} else {
			SubMerchantTemp info = new SubMerchantTemp();
			String subMerchantKey = merchantId + "SM" + mobile + GenerateTrxnRefId.getAlphaNumericString(6);
			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());
			info.setMerchantId(merchantId);
			info.setDate(trxnDate);
			info.setSubMerchantName(Encryption.encString(subMerchantName));
			info.setMccCode(mccCode);
			info.setBusinessType(businessType);
			info.setEmail(Encryption.encString(email));
			info.setMobile(Encryption.encString(mobile));
			info.setSubMerchantKey(Encryption.encString(subMerchantKey));
			info.setJsonResponse("NA");
			info.setIsActive('0');
			info.setMerchantSubMerchantRequest("NA");
			info.setMerchantSubMerchantResponse("NA");
			info.setIsSubMerchant('0');
			info.setIsOnboarding('0');
			info.setIsMerchant('0');
			info.setBankID(bankId);
			info.setReason("NA");
			info.setVpaCallBack("NA");
			info.setMerchantUserId(Long.parseLong(merchantUserId));

			subMerchantTempRepository.save(info);
			Long subMerchantTempId = info.getSubMerchantTempId();
			LOGGER.info("subMerchantTempId " + subMerchantTempId);

			String link = ResponseMessage.LINK_UPI_KYC + subMerchantKey;
			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			map.put("url", link);
			map.put("token", subMerchantKey);

			return map;

		}

		return map;

	}

	@Override
	public Map<String, Object> findActiveInActiveMerchantCount(long merchantId, String merchantUserId) {
		Map<String, Object> map = new HashMap<>();

		if (merchantUserId.equalsIgnoreCase("NA") || merchantUserId.equals("")
				|| !DateUtil.isValidNumber(merchantUserId) || merchantUserId.equalsIgnoreCase("0")
				|| !merchantUserRepository.existsByMerchantUserIdAndIsActiveAndMerchantId(
						Long.parseLong(merchantUserId), '1', merchantId)) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Invalid merchantUserId");
			return map;
		}

		List<SubMerchantTemp> list = subMerchantTempRepository.findByMerchantIdAndMerchantUserId(merchantId,
				Long.parseLong(merchantUserId));

		long totalActiveMerchants = list.stream().filter(x -> x.getIsActive() == '1').count();
		long totalNotActiveMerchants = list.stream().filter(x -> x.getIsActive() == '0').count();
		long totalRejectMerchants = list.stream().filter(x -> x.getIsActive() == '2').count();
		long totalMerchants = list.stream().filter(x -> x.getIsActive() != null).count();

		map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
		map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
		map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_FOUND);
		map.put("totalActiveMerchants", totalActiveMerchants);
		map.put("totalNotActiveMerchants", totalNotActiveMerchants);
		map.put("totalMerchants", totalMerchants);
		map.put("totalReject", totalRejectMerchants);
		return map;
	}

	@Override
	public Map<String, Object> findAllSubMerchantPWA(String startDate, String endDate, Pagination pagination,
			long merchantId, String merchantUserId) {
		Map<String, Object> map = new HashMap<String, Object>();

		if (merchantUserId.equalsIgnoreCase("NA") || merchantUserId.equals("")
				|| !DateUtil.isValidNumber(merchantUserId) || merchantUserId.equalsIgnoreCase("0")
				|| !merchantUserRepository.existsByMerchantUserIdAndIsActiveAndMerchantId(
						Long.parseLong(merchantUserId), '1', merchantId)) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Invalid merchantUserId");
			return map;
		}

		try {
			Pageable paging = PageRequest.of(pagination.getPageNo(), pagination.getPageSize(),
					Sort.by("DATE").descending());

			List<SubMerchantTemp> list = new ArrayList<SubMerchantTemp>();
			List<SubMerchantTempResponse> responseList = new ArrayList<SubMerchantTempResponse>();
			Page<SubMerchantTemp> pageingList = null;

			LOGGER.info("startdate " + startDate + " , enddate " + endDate);

			if (!startDate.equals("0") || !endDate.equals("0")) {
				if (DateUtil.isValidDateFormat(startDate) == false || DateUtil.isValidDateFormat(endDate) == false) {
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.DESCRIPTION, ResponseMessage.INVALID_DATE_FORMATE);
					return map;
				}

				if (DateUtil.isValidDateFormat(startDate, endDate)) {
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.DESCRIPTION, ResponseMessage.INVALID_FROM_TO_DATE);
					return map;
				}

				startDate = startDate + " 00.00.00";
				endDate = endDate + " 23.59.59";
				pageingList = subMerchantTempRepository.findByStartDateAndEndDateAndMerchantIdAndMerchantUserId(
						startDate, endDate, merchantId, Long.parseLong(merchantUserId), paging);
			} else {
				pageingList = subMerchantTempRepository.findByMerchantIdAndMerchantUserId(merchantId,
						Long.parseLong(merchantUserId), paging);
			}
			list = pageingList.getContent();

			AtomicInteger atomicInteger = new AtomicInteger(1);
			if (list.size() != 0) {
				list.forEach(objects -> {

					SubMerchantTempResponse subMerchantTempResponse = new SubMerchantTempResponse();

					try {
						String date = DateAndTime.dateFormatReports(objects.getDate().toString());

						subMerchantTempResponse.setLogo("NA");
						subMerchantTempResponse.setSubMerchantTempId(objects.getSubMerchantTempId());
						subMerchantTempResponse.setsNo(atomicInteger.getAndIncrement());
						subMerchantTempResponse.setMerchantId(objects.getMerchantId());
						subMerchantTempResponse.setDate(date);
						subMerchantTempResponse.setSubMerchantName(Encryption.decString(objects.getSubMerchantName()));
						subMerchantTempResponse.setMccCode(objects.getMccCode());
						subMerchantTempResponse.setBusinessType(objects.getBusinessType());
						subMerchantTempResponse.setEmail(Encryption.decString(objects.getEmail()));
						subMerchantTempResponse.setMobile(Encryption.decString(objects.getMobile()));
						subMerchantTempResponse.setIsActive(objects.getIsActive());
						subMerchantTempResponse.setIsOnboarding(objects.getIsOnboarding());
						subMerchantTempResponse.setIsSubMerchant(objects.getIsSubMerchant());
						subMerchantTempResponse.setJsonResponse(objects.getJsonResponse());
						subMerchantTempResponse.setMerchantSubMerchantRequest(objects.getMerchantSubMerchantRequest());
						subMerchantTempResponse
								.setMerchantSubMerchantResponse(objects.getMerchantSubMerchantResponse());
						subMerchantTempResponse.setToken(Encryption.decString(objects.getSubMerchantKey()));
						subMerchantTempResponse.setReason(objects.getReason());
						responseList.add(subMerchantTempResponse);
					} catch (ParseException e) {
						e.printStackTrace();
					}

				});

				map.put("currentPage", pageingList.getNumber());
				map.put("totalItems", pageingList.getTotalElements());
				map.put("totalPages", pageingList.getTotalPages());
				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "Sub Merchant Temp List");
				map.put("data", responseList);

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
	public Map<String, Object> findByTokenOrMobilePWA(long merchantId, String value, String key, String merchantUserId)
			throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();

		if (merchantUserId.equalsIgnoreCase("NA") || merchantUserId.equals("")
				|| !DateUtil.isValidNumber(merchantUserId) || merchantUserId.equalsIgnoreCase("0")
				|| !merchantUserRepository.existsByMerchantUserIdAndIsActiveAndMerchantId(
						Long.parseLong(merchantUserId), '1', merchantId)) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Invalid merchantUserId");
			return map;
		}

		SubMerchantTemp objects = null;
		if (key.equals("token")) {
			objects = subMerchantTempRepository.findBySubMerchantKeyAndMerchantIdAndMerchantUserId(
					Encryption.encString(value), merchantId, Long.parseLong(merchantUserId));

		}

		if (key.equals("mobile")) {
			objects = subMerchantTempRepository.findByMobileAndMerchantIdAndMerchantUserId(Encryption.encString(value),
					merchantId, Long.parseLong(merchantUserId));

		}

		if (objects != null) {
			SubMerchantTempResponse subMerchantTempResponse = new SubMerchantTempResponse();

			String date = DateAndTime.dateFormatReports(objects.getDate().toString());

			subMerchantTempResponse.setLogo("NA");
			subMerchantTempResponse.setSubMerchantTempId(objects.getSubMerchantTempId());
			subMerchantTempResponse.setsNo(1);
			subMerchantTempResponse.setMerchantId(objects.getMerchantId());
			subMerchantTempResponse.setDate(date);
			subMerchantTempResponse.setSubMerchantName(Encryption.decString(objects.getSubMerchantName()));
			subMerchantTempResponse.setMccCode(objects.getMccCode());
			subMerchantTempResponse.setBusinessType(objects.getBusinessType());
			subMerchantTempResponse.setEmail(Encryption.decString(objects.getEmail()));
			subMerchantTempResponse.setMobile(Encryption.decString(objects.getMobile()));
			subMerchantTempResponse.setIsActive(objects.getIsActive());
			subMerchantTempResponse.setIsOnboarding(objects.getIsOnboarding());
			subMerchantTempResponse.setIsSubMerchant(objects.getIsSubMerchant());
			subMerchantTempResponse.setJsonResponse(objects.getJsonResponse());
			subMerchantTempResponse.setMerchantSubMerchantRequest(objects.getMerchantSubMerchantRequest());
			subMerchantTempResponse.setMerchantSubMerchantResponse(objects.getMerchantSubMerchantResponse());
			subMerchantTempResponse.setToken(Encryption.decString(objects.getSubMerchantKey()));
			subMerchantTempResponse.setReason(objects.getReason());

			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_FOUND);
			map.put("data", subMerchantTempResponse);
			return map;
		}

		map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
		map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
		return map;
	}

}
