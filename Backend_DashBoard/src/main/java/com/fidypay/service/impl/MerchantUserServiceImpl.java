package com.fidypay.service.impl;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

import javax.validation.Valid;

import org.apache.commons.collections4.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fidypay.encryption.Encryption;
import com.fidypay.encryption.EncryptionDataRequest;
import com.fidypay.entity.MerchantInfo;
import com.fidypay.entity.MerchantUser;
import com.fidypay.entity.Merchants;
import com.fidypay.entity.OTPVerification;
import com.fidypay.entity.ServiceInfo;
import com.fidypay.repo.MerchantInfoRepository;
import com.fidypay.repo.MerchantServiceRepository;
import com.fidypay.repo.MerchantUserRepository;
import com.fidypay.repo.MerchantsRepository;
import com.fidypay.repo.OTPVerificationRepository;
import com.fidypay.repo.ServiceInfoRepository;
import com.fidypay.request.EncryptedRequest;
import com.fidypay.request.MerchantUserPWARequest;
import com.fidypay.request.MerchantUserRequest;
import com.fidypay.request.MerchantUserRequestForSecondary;
import com.fidypay.request.MerchantUserUpdateRequest;
import com.fidypay.request.UpdateUserPassword;
import com.fidypay.response.EncryptedResponse;
import com.fidypay.response.MerchantUserResponse;
import com.fidypay.service.MerchantUserService;
import com.fidypay.utils.constants.EmailNotification;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.BasicAuth;
import com.fidypay.utils.ex.DateAndTime;
import com.fidypay.utils.ex.DateUtil;
import com.fidypay.utils.ex.RandomNumberGenrator;
import com.fidypay.utils.ex.SMSAPIImpl;
import com.fidypay.utils.ex.URLGenerater;

@Service
public class MerchantUserServiceImpl implements MerchantUserService {
	private static final Logger LOGGER = LoggerFactory.getLogger(MerchantUserServiceImpl.class);

	private static final String IS_ACTIVE = "0|1";
	private static final String MERCHANT_USER_TYPE_PRIMARY = "primary";
	private static final String MERCHANT_USER_TYPE_SECONDARY = "secondary";

	@Autowired
	private OTPVerificationRepository otpVerificationRepository;

	@Autowired
	private MerchantUserRepository merchantUserRepository;

	@Autowired
	private MerchantsRepository merchantsRepository;

	@Autowired
	private MerchantInfoRepository merchantInfoRepository;

	@Autowired
	private LoginDetailsServiceImpl detailsService;

	@Autowired
	private ServiceInfoRepository serviceInfoRepository;

	@Autowired
	private MerchantServiceRepository merchantServiceRepository;

	@Autowired
	private URLGenerater urlGenerater;

	@Override
	public Map<String, Object> saveDetails(long merchantId, MerchantUserRequest merchantUserRequest) throws Exception {
		Map<String, Object> map = new HashedMap<>();
		try {
			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			String mobileNo = merchantUserRequest.getMerchantUserMobileNo();
			String email = merchantUserRequest.getMerchantUserEmail().toLowerCase();
			String name = merchantUserRequest.getMerchantUserName();
			String password = merchantUserRequest.getMerchantUserPassword();

			LOGGER.info("Processing request for: " + email);

			if (merchantUserRepository.existsByMerchantUserMobileNo(Encryption.encString(mobileNo))) {
				LOGGER.info("Inside mobileNo: " + mobileNo);

				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.MOBILE_NUMBER_ALREADY_REGISTERED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

				return map;
			}

			if (merchantUserRepository.existsByMerchantUserEmail(Encryption.encString(email))) {
				LOGGER.info("Inside email: " + email);

				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.EMAILID_ALREADY_REGISTERED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

				return map;
			}

			String merchantBussinessName = merchantsRepository.findByIdMerchantBussinessName(merchantId);
			String merchantUserKey = merchantId + "SEC" + mobileNo;

			int count = merchantUserRepository.totalCountByMerchantId(merchantId);

			if (count > 11) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.USETR_KYC_LIMIT_MESSAGE);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}

			MerchantUser merchantUser = new MerchantUser();
			merchantUser.setDate(trxnDate);
			merchantUser.setMerchantId(merchantId);
			merchantUser.setIsActive('1');
			merchantUser.setMerchantUserEmail(Encryption.encString(email));
			merchantUser.setMerchantUserMobileNo(Encryption.encString(mobileNo));
			merchantUser.setMerchantUserName(Encryption.encString(name));
			merchantUser.setMerchantUserPassword(Encryption.encString(password));
			merchantUser.setMerchantUserType(MERCHANT_USER_TYPE_SECONDARY);
			merchantUser.setMerchantBusinessName(merchantBussinessName);
			merchantUser.setMerchantUserKey(Encryption.encString(merchantUserKey));
			merchantUser.setLoginCount(0L);
			merchantUser.setUpdatePasswordDate(trxnDate);
			merchantUserRepository.save(merchantUser);

			String res = EmailNotification.sendEmailForMerchantOnboarding(email, name, "User", password,
					ResponseMessage.MERCHANT_DASHBOARD_URL_REACT, name, ResponseMessage.MERCHANT_ENVIRONMENT);

			LOGGER.info("Email Response: " + res);
			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.REGISTER_SUCCESSFULLY);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
		} catch (DataIntegrityViolationException e) {
			LOGGER.error("Duplicate entry detected!", e);
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Email or Mobile Number already registered.");
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		} catch (Exception e) {
			LOGGER.error("Error occurred while saving details", e);
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Something went wrong. Please try again.");
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;
	}

	@Override
	public Map<String, Object> findByMerchantId(long merchantId, Integer pageNo, Integer pageSize) {

		LOGGER.info(" merchantId : " + merchantId);
		Map<String, Object> map = new HashedMap<>();

		if (pageSize == 0) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Page size not be less than one");
			return map;
		}

		Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by("DATE").descending());
		Page<MerchantUser> page = merchantUserRepository.findByMerchantIdAndPaging(merchantId,
				MERCHANT_USER_TYPE_SECONDARY, paging);
		List<MerchantUserResponse> activityList = new ArrayList<MerchantUserResponse>();
		List<MerchantUser> merchantUserList = page.getContent();
		AtomicInteger atomicInteger = new AtomicInteger(1);

		LOGGER.info(" merchantUserList : " + merchantUserList.size());

		if (!merchantUserList.isEmpty()) {

			merchantUserList.forEach(merchantUserDetails -> {

				MerchantUserResponse merchantUserResponse = new MerchantUserResponse();
				merchantUserResponse.setsNo(atomicInteger.getAndIncrement());
				merchantUserResponse.setMerchantUserKey(Encryption.decString(merchantUserDetails.getMerchantUserKey()));
				merchantUserResponse.setIsActive(merchantUserDetails.getIsActive());
				merchantUserResponse.setMerchantId(merchantUserDetails.getMerchantId());
				merchantUserResponse
						.setMerchantUserEmail(Encryption.decString(merchantUserDetails.getMerchantUserEmail()));
				merchantUserResponse.setMerchantUserId(merchantUserDetails.getMerchantUserId());
				merchantUserResponse
						.setMerchantUserMobileNo(Encryption.decString(merchantUserDetails.getMerchantUserMobileNo()));
				merchantUserResponse
						.setMerchantUserName(Encryption.decString(merchantUserDetails.getMerchantUserName()));
				merchantUserResponse.setMerchantUserType(merchantUserDetails.getMerchantUserType());
				merchantUserResponse
						.setMerchantBusiness(Encryption.decString(merchantUserDetails.getMerchantBusinessName()));

				activityList.add(merchantUserResponse);

			});

			map.put(ResponseMessage.DATA, activityList);
			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_FOUND);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			map.put("currentPage", page.getNumber());
			map.put("totalItems", page.getTotalElements());
			map.put("totalPages", page.getTotalPages());

			return map;
		}

		map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
		map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);

		return map;

	}

	@Override
	public Map<String, Object> checkPassword(UpdateUserPassword updateUserPassword, long merchantId) throws Exception {
		Map<String, Object> map = new HashedMap<>();

		String password = updateUserPassword.getOldPassword();
		MerchantUser merchantUser = merchantUserRepository
				.findByMerchantUserIdAndMerchantId(updateUserPassword.getMerchantUserId(), merchantId);
		Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());
		if (merchantUser == null) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, "MerchantUserId not exist");
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			return map;
		}

		if (merchantUser.getIsActive() == '0') {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.MERCHANT_USER_DEACTIVATED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			return map;
		}

		String oldPassword = Encryption.decString(merchantUser.getMerchantUserPassword());
		String newPassword = Encryption.decString(updateUserPassword.getNewPassword());
		LOGGER.info("oldPassword In db " + oldPassword);
		LOGGER.info("Api old Password " + password);

		if (!oldPassword.equals(password)) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.OLD_PASSWORD_WROUNG);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			return map;
		}

		if (oldPassword.equals(newPassword)) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.OLD_AND_NEW_PASSWORD_SAME);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			return map;
		}

		LOGGER.info("password:" + newPassword);
		newPassword = Encryption.encString(newPassword);

		Merchants merchants = merchantsRepository.findMerchant(merchantId);

		if (merchantUser.getMerchantUserType().equalsIgnoreCase(MERCHANT_USER_TYPE_PRIMARY)) {
			merchantUser.setUpdatePasswordDate(trxnDate);
			merchantUser.setMerchantUserPassword(newPassword);
			merchantUser.setMerchantUserKey("NA");
			merchantUser.setLoginCount(0L);
			merchantUserRepository.save(merchantUser);

			merchants.setMerchantPassword(newPassword);
			merchantsRepository.save(merchants);
		} else {
			merchantUser.setUpdatePasswordDate(trxnDate);
			merchantUser.setMerchantUserPassword(newPassword);
			merchantUser.setLoginCount(0L);
			merchantUser.setMerchantUserKey("NA");
			merchantUserRepository.save(merchantUser);
		}

		LOGGER.info("MerchantUser Password Updated");
		map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
		map.put(ResponseMessage.DESCRIPTION, ResponseMessage.PASSWORD_CHANGED_MESSAGE);
		map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
		return map;

	}

	@Override
	public Map<String, Object> findByMobileNumber(long merchantId, String mobileNumber) {
		LOGGER.info(" findByMobileNumber ." + mobileNumber);

		Map<String, Object> map = new HashedMap<>();

		MerchantUser merchantUser = merchantUserRepository.findByMerchantUserMobileNoAndMerchantIdAndMerchantUserType(
				Encryption.encString(mobileNumber), merchantId, "secondary");

		if (!mobileNumber.matches("^\\(?([0-9]{3})\\)?[-.\\s]?([0-9]{3})[-.\\s]?([0-9]{4})$")) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, "please enter valid mobileNumber");
			return map;
		}

		if (merchantUser != null) {

			LOGGER.info("Inside merchantUser .");

			MerchantUserResponse merchantUserResponse = new MerchantUserResponse();

			merchantUserResponse.setMerchantUserKey(Encryption.decString(merchantUser.getMerchantUserKey()));
			merchantUserResponse.setIsActive(merchantUser.getIsActive());
			merchantUserResponse.setMerchantId(merchantUser.getMerchantId());
			merchantUserResponse.setMerchantUserEmail(Encryption.decString(merchantUser.getMerchantUserEmail()));
			merchantUserResponse.setMerchantUserId(merchantUser.getMerchantUserId());
			merchantUserResponse.setMerchantUserMobileNo(Encryption.decString(merchantUser.getMerchantUserMobileNo()));
			merchantUserResponse.setMerchantUserName(Encryption.decString(merchantUser.getMerchantUserName()));
			merchantUserResponse.setMerchantUserType(merchantUser.getMerchantUserType());
			merchantUserResponse.setMerchantBusiness(Encryption.decString(merchantUser.getMerchantBusinessName()));
			map.put("data", merchantUserResponse);
			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_FOUND);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			return map;

		}

		map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
		map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		map.put(ResponseMessage.DESCRIPTION, ResponseMessage.MOBILE_NUMBER_NOT_REGISTERED);

		return map;

	}

	@Override
	public Map<String, Object> loginDashboard(String email, String password) {

		Map<String, Object> map = new HashedMap<>();

		String getEmail = null;
		String getPassword = null;
		String clientSecret = null;
		String clientId = null;
		long merchantId = 0;
		String bussinessName = null;
		String firstName = null;
		long mTypeId = 0;
		String upiQRCode = null;
		String merchantCity = null;
		String merchantUserType = null;
		long merchantUserId = 0;
		try {
			// Date date = new Date();
			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());
			String nDate = DateAndTime.dateFormatReports(trxnDate.toString());

			email = Encryption.encString(email);
			Optional<MerchantUser> optMerchant = merchantUserRepository.findByMerchantUserEmail(email);
			if (optMerchant.isPresent()) {
				char active = optMerchant.get().getIsActive();
				if (active == '0') {
					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.DESCRIPTION, "User is Not Active");
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
					map.put("date", nDate);
					map.put("loggedIn", false);
					return map;
				}

				getEmail = Encryption.decString(optMerchant.get().getMerchantUserEmail());
				getPassword = Encryption.decString(optMerchant.get().getMerchantUserPassword());
				merchantId = optMerchant.get().getMerchantId();
				bussinessName = Encryption.decString(optMerchant.get().getMerchantBusinessName());
				// firstName = Encryption.decString(optMerchant.get().getMerchantUserName());

				merchantUserType = optMerchant.get().getMerchantUserType();
				merchantUserId = optMerchant.get().getMerchantUserId();
				Merchants findListByMerchantId = merchantsRepository.findMerchant(merchantId);

				mTypeId = findListByMerchantId.getMerchantType().getMerchantTypeId(); // check once
				try {
					upiQRCode = findListByMerchantId.getSecondSecAnswer();
					merchantCity = findListByMerchantId.getMerchantCity();

					if ((upiQRCode == null || upiQRCode.equals("NA"))) {
						upiQRCode = bussinessName + ", " + merchantCity;
					}

				} catch (Exception e) {
					merchantCity = findListByMerchantId.getMerchantCity();
					upiQRCode = bussinessName + ", " + merchantCity;
				}

				if (password.equals(getPassword) || password == getPassword) {
					MerchantInfo merchantInfo = merchantInfoRepository.findByMerchantId(merchantId);

					if (active == '1') {
						String loginKey = detailsService.saveLogInDetails(merchantUserId, "Merchant Dashboard");
						clientSecret = Encryption.decString(merchantInfo.getClientSecret());
						// String mEmail = Encryption.decString(clientSecret);
						clientId = Encryption.decString(String.valueOf(merchantInfo.getClientId()));
						firstName = Encryption.decString(merchantInfo.getUsername());
						password = Encryption.decString(merchantInfo.getPassword());
						String merchantUserName = Encryption.decString(optMerchant.get().getMerchantUserName());
						String basicAuth = BasicAuth.createEncodedText(firstName, password);
						String logo = merchantInfo.getImageUrl();

						if (!logo.equalsIgnoreCase("NA")) {
							logo = ResponseMessage.LOGO_URL + logo;
						}
						String qrImage = merchantInfo.getQrImage();
						if (!qrImage.equalsIgnoreCase("NA")) {
							qrImage = ResponseMessage.LOGO_URL + qrImage;
						}

						MerchantUser merchantUser = merchantUserRepository.findById(merchantUserId).get();
						merchantUser.setLoginCount(0L);
						merchantUser.setMerchantUserKey(loginKey);
						merchantUserRepository.save(merchantUser);

						map.put("logo", logo);
						map.put("clientId", clientId);
						map.put("clientSecret", clientSecret);
						map.put("email", getEmail);
						map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
						map.put(ResponseMessage.DESCRIPTION, ResponseMessage.LOGIN_SUCCESS_MESSAGE);
						map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
						map.put("date", nDate);
						map.put("merchantTypeId", mTypeId);
						map.put("bussinessName", bussinessName);
						map.put("firstName", firstName);
						map.put("loggedIn", true);
						map.put("upiQRCode", upiQRCode);
						map.put("loginUniqueId", loginKey);
						map.put("merchantUserType", merchantUserType);
						map.put("merchantUserId", merchantUserId);
						map.put("prod", basicAuth);
						map.put("merchantUserName", merchantUserName);
						map.put("qrImage", qrImage);
					} else {
						map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
						map.put(ResponseMessage.DESCRIPTION, "User is Not Active");
						map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
						map.put("date", nDate);
						map.put("loggedIn", false);
					}
				} else {

					getEmail = Encryption.decString(optMerchant.get().getMerchantUserEmail());
					String name = Encryption.decString(optMerchant.get().getMerchantUserName());
					merchantUserType = optMerchant.get().getMerchantUserType();
					long count = optMerchant.get().getLoginCount();
					merchantUserId = optMerchant.get().getMerchantUserId();
					count = count + 1L;
					MerchantUser merchantUser = merchantUserRepository.findById(merchantUserId).get();
					merchantUser.setLoginCount(count);

					if (count > 2 && merchantUserType.equalsIgnoreCase("primary")) {
						merchantUser.setIsActive('0');
						Merchants merchant = merchantsRepository.findById(merchantId).get();
						merchant.setIsMerchantActive('0');
						merchantsRepository.save(merchant);
					}

					if (count > 2 && merchantUserType.equalsIgnoreCase("secondary")) {
						merchantUser.setIsActive('0');
					}

					merchantUserRepository.save(merchantUser);

					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.DESCRIPTION,
							descriptionForPasswordAttempt((int) count, name, email, trxnDate.toString()));
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
					map.put("date", nDate);
					map.put("loggedIn", false);
				}

			} else {

				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Invalid email or Password.");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put("date", nDate);
				map.put("loggedIn", false);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info("error " + e);
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}

		return map;
	}

	@Override
	public Map<String, Object> findByMerchantUserId(long merchantUserId, long parseLong) {

		Map<String, Object> map = new HashedMap<>();

		LOGGER.info(" merchantUserId : " + merchantUserId);
		MerchantUser merchantUserData = merchantUserRepository.findByMerchantUserId(merchantUserId);

		if (merchantUserData == null) {

			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

			return map;

		} else {

			MerchantUserResponse merchantUserResponse = new MerchantUserResponse();

			merchantUserResponse.setMerchantUserKey(Encryption.decString(merchantUserData.getMerchantUserKey()));
			merchantUserResponse.setIsActive(merchantUserData.getIsActive());
			merchantUserResponse.setMerchantId(merchantUserData.getMerchantId());
			merchantUserResponse.setMerchantUserEmail(Encryption.decString(merchantUserData.getMerchantUserEmail()));
			merchantUserResponse.setMerchantUserId(merchantUserData.getMerchantUserId());
			merchantUserResponse
					.setMerchantUserMobileNo(Encryption.decString(merchantUserData.getMerchantUserMobileNo()));
			merchantUserResponse.setMerchantUserName(Encryption.decString(merchantUserData.getMerchantUserName()));
			merchantUserResponse.setMerchantUserType(merchantUserData.getMerchantUserType());
			merchantUserResponse.setMerchantBusiness(Encryption.decString(merchantUserData.getMerchantBusinessName()));

			map.put("data", merchantUserResponse);
			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_FOUND);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
		}
		return map;

	}

	@Override
	public Map<String, Object> updateByMerchantId(MerchantUserUpdateRequest merchantUserUpdateRequest,
			long merchantId) {

		Map<String, Object> map = new HashedMap<>();
		MerchantUser merchantUser = merchantUserRepository
				.findByMerchantUserIdAndMerchantId(merchantUserUpdateRequest.getMerchantUserId(), merchantId);

		Merchants merchants = merchantsRepository.findMerchant(merchantId);
		String email = merchantUserUpdateRequest.getMerchantUserEmail().toLowerCase();
		String mobilenumber = merchantUserUpdateRequest.getMerchantUserMobileNo();
		String firstName = merchantUserUpdateRequest.getMerchantUserName();
		if (merchantUser != null) {

			LOGGER.info("Inside merchantUser .");

			if (merchantUser.getIsActive() == '0') {

				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.MERCHANT_USER_DEACTIVATED);
				return map;

			}

			Optional<MerchantUser> optionalPhone = merchantUserRepository.findByMerchantUserMobileNo(
					Encryption.encString(merchantUserUpdateRequest.getMerchantUserMobileNo()));

			if (optionalPhone.isPresent()) {
				long optionalUniqueId = optionalPhone.get().getMerchantUserId();
				if (optionalUniqueId != 0 && optionalUniqueId != merchantUserUpdateRequest.getMerchantUserId()) {
					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
					map.put(ResponseMessage.DESCRIPTION, ResponseMessage.MOBILE_NUMBER_ALREADY_REGISTERED);
					return map;
				}
			}

			Optional<MerchantUser> optionalEmail = merchantUserRepository
					.findByMerchantUserEmail(Encryption.encString(email));

			if (optionalEmail.isPresent()) {
				long optionalUniqueId = optionalEmail.get().getMerchantUserId();
				if (optionalUniqueId != 0 && optionalUniqueId != merchantUserUpdateRequest.getMerchantUserId()) {
					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
					map.put(ResponseMessage.DESCRIPTION, ResponseMessage.EMAIL_ALREADY_ACTIVATED);
					return map;
				}
			}

			String merchantUserType = merchantUser.getMerchantUserType();

			switch (merchantUserType) {
			case "primary":

				if (email != null && !email.equals("")
						&& email != Encryption.decString(merchantUser.getMerchantUserEmail())) {
					LOGGER.info("Inside getMerchantUserEmail .");

					merchantUser.setMerchantUserEmail(Encryption.encString(email));

					merchants.setMerchantEmail(Encryption.encString(email));
				}

				if (mobilenumber != null && !mobilenumber.equals("")) {
					LOGGER.info("Inside getMerchantUserMobileNo .");

					merchantUser.setMerchantUserMobileNo(Encryption.encString(mobilenumber));

					merchants.setMerchantPhone(Encryption.encString(mobilenumber));

				}
				if (firstName != null && !firstName.equals("")) {
					LOGGER.info("Inside getMerchantUserName .");

					merchantUser.setMerchantUserName(Encryption.encString(firstName));
					merchants.setMerchantFirstname(Encryption.encString(firstName));
				}

				merchantUserRepository.save(merchantUser);
				merchantsRepository.save(merchants);

				break;

			case "secondary":

				if (email != null && !email.equals("")
						&& email != Encryption.encString(merchantUser.getMerchantUserEmail())) {
					LOGGER.info("Inside getMerchantUserEmail .");

					merchantUser.setMerchantUserEmail(Encryption.encString(email));
				}

				if (mobilenumber != null && !mobilenumber.equals("")) {
					LOGGER.info("Inside getMerchantUserMobileNo .");

					merchantUser.setMerchantUserMobileNo(Encryption.encString(mobilenumber));
				}
				if (firstName != null && !firstName.equals("")) {
					LOGGER.info("Inside getMerchantUserName .");

					merchantUser.setMerchantUserName(Encryption.encString(firstName));
				}

				merchantUserRepository.save(merchantUser);

				break;

			default:
				break;
			}

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

	@Override
	public Map<String, Object> deleteByMerchantUserId(long merchantUserId, long merchantId, String isActive) {

		Map<String, Object> map = new HashedMap<>();

		if (!isActive.matches(IS_ACTIVE)) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, "isActive can only accept 0(Deactivate) and 1(Activate)");
			return map;
		}

		MerchantUser merchantUser = merchantUserRepository.findByMerchantUserIdAndMerchantIdAndMerchantUserType(
				merchantUserId, merchantId, MERCHANT_USER_TYPE_SECONDARY);

		if (merchantUser != null) {

			if (merchantUser.getIsActive() == '0' && isActive.equals("0")) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.MERCHANT_USER_DEACTIVATED);
				return map;
			}

			if (merchantUser.getIsActive() == '1' && isActive.equals("1")) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.MERCHANT_USER_ACTIVE);
				return map;
			}

			if (merchantUser.getIsActive() == '1' && isActive.equals("0")) {

				merchantUser.setIsActive('0');
				merchantUser.setLoginCount(0L);
				merchantUserRepository.save(merchantUser);

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "Merchant User is deactivated successfully");
				return map;
			}

			if (merchantUser.getIsActive() == '0' && isActive.equals("1")) {

				merchantUser.setIsActive('1');
				merchantUser.setLoginCount(0L);
				merchantUserRepository.save(merchantUser);

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "Merchant User is activated successfully");
				return map;
			}

		}

		map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
		map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
		return map;

	}

	@Override
	public Map<String, Object> addRecordsByEmail(String email) {
		Map<String, Object> map = new HashedMap<>();
		try {
			String encEmail = Encryption.encString(email);

			if (!(merchantUserRepository.existsByMerchantUserEmail(encEmail))) {

				Merchants merchants = merchantsRepository.findByEmail(encEmail);
				String merchantUserKey = "PRM" + Encryption.decString(merchants.getMerchantPhone());
				MerchantUser user = new MerchantUser();
				user.setMerchantId(merchants.getMerchantId());
				Timestamp timestamp = merchants.getMerchantFromdate();
				String date = DateUtil.convertDateToStringWithTimeNew(timestamp);
				user.setLoginCount(0L);
				user.setMerchantUserKey(Encryption.encString(merchantUserKey));
				user.setDate(Timestamp.valueOf(date));
				user.setMerchantUserEmail(merchants.getMerchantEmail());
				user.setMerchantUserMobileNo(merchants.getMerchantPhone());
				user.setMerchantUserName(merchants.getMerchantFirstname());
				user.setMerchantUserPassword(merchants.getMerchantPassword());
				user.setIsActive(merchants.getIsMerchantActive());
				user.setMerchantUserType(MERCHANT_USER_TYPE_PRIMARY);
				user.setMerchantBusinessName(merchants.getMerchantBusinessName());
				user.setUpdatePasswordDate(timestamp);
				MerchantUser save = merchantUserRepository.save(user);
				map.put("records", save);
				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.REGISTER_SUCCESSFULLY);
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.EMAILID_ALREADY_REGISTERED);
			}

		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage().toString());
		}
		return map;
	}

	@Override
	public Map<String, Object> addRecordsToMerchantUser() {
		Map<String, Object> map = new HashedMap<>();
		try {
			List<MerchantUser> list = new ArrayList<>();
			List<Merchants> merchantsList = merchantsRepository.findAll();

			System.out.println("size of list is :" + merchantsList.size());

			List<MerchantUser> findAll = merchantUserRepository.findAll();
			System.out.println("MerchantUser list size : :" + findAll.size());

			for (Merchants merchants : merchantsList) {
				String email = merchants.getMerchantEmail();
				String mobile = merchants.getMerchantPhone();
				long merchantId = merchants.getMerchantId();

				if (!(merchantUserRepository.existsByMerchantUserMobileNoAndMerchantId(mobile, merchantId))) {
					if (!(merchantUserRepository.existsByMerchantUserEmailAndMerchantId(email, merchantId))) {
						if (!(merchantUserRepository.existsByMerchantUserMobileNoAndMerchantUserEmail(mobile, email))) {

							MerchantUser user = setMerchnatDetails(merchants);
							list.add(user);
						} else {
							MerchantUser user = setMerchnatDetails(merchants);
							list.add(user);
						}
					}

				}
			}
			if (list.size() != 0) {
				List<MerchantUser> saveAll = merchantUserRepository.saveAll(list);
				map.put("records", saveAll);
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.REGISTER_SUCCESSFULLY);
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage().toString());
		}
		return map;
	}

	private MerchantUser setMerchnatDetails(Merchants merchants) {

		String name = Encryption.decString(merchants.getMerchantFirstname()) + " "
				+ Encryption.decString(merchants.getMerchantLastname());

		String merchantUserKey = "PRM" + Encryption.decString(merchants.getMerchantPhone());
		MerchantUser user = new MerchantUser();
		user.setMerchantId(merchants.getMerchantId());
		Timestamp timestamp = merchants.getMerchantFromdate();
		String date = DateUtil.convertDateToStringWithTimeNew(timestamp);
		user.setDate(Timestamp.valueOf(date));
		user.setMerchantUserEmail(merchants.getMerchantEmail());
		user.setMerchantUserMobileNo(merchants.getMerchantPhone());
		user.setMerchantUserName(Encryption.encString(name));
		user.setMerchantUserPassword(merchants.getMerchantPassword());
		user.setIsActive(merchants.getIsMerchantActive());
		user.setMerchantUserType(MERCHANT_USER_TYPE_PRIMARY);
		user.setMerchantUserKey(Encryption.encString(merchantUserKey));
		user.setLoginCount(0L);
		user.setMerchantBusinessName(merchants.getMerchantBusinessName());
		user.setUpdatePasswordDate(timestamp);
		return user;

	}

	@Override
	public Map<String, Object> findByMerchantIdUser(long merchantId) {

		LOGGER.info(" merchantId : {}", merchantId);
		Map<String, Object> map = new HashedMap<>();

		List<MerchantUserRequestForSecondary> activityList = new ArrayList<MerchantUserRequestForSecondary>();
		List<MerchantUser> list = merchantUserRepository.findByMerchantId(merchantId);

		if (list.isEmpty()) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);

			return map;
		}

		for (MerchantUser merchantUser : list) {
			MerchantUserRequestForSecondary merchantUserRequestForSecondary = new MerchantUserRequestForSecondary();
			merchantUserRequestForSecondary.setMerchantUserId(merchantUser.getMerchantUserId());
			merchantUserRequestForSecondary
					.setMerchantUserName(Encryption.decString(merchantUser.getMerchantUserName()));
			activityList.add(merchantUserRequestForSecondary);
		}

		map.put(ResponseMessage.DATA, activityList);
		map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
		map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_FOUND);
		map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
		return map;

	}

	private static String descriptionForPasswordAttempt(int count, String name, String email, String date)
			throws IOException {
		email = Encryption.decString(email);
		String description = "NA";
		switch (count) {
		case 1:

			new EmailNotification().sendEmailNotificationLoginAttempt(name, email, date, "2", "3");
			description = "Invalid email or Password. You have two attempts remaining, Please enter correct email or password.";
			break;
		case 2:
			new EmailNotification().sendEmailNotificationLoginAttempt(name, email, date, "1", "3");
			description = "Invalid email or Password. You have one attempts remaining, Please enter correct email or password.";
			break;
		case 3:
			new EmailNotification().sendEmailNotificationLoginAttemptBlocked(name, email, date);
			description = "Invalid email or Password. Your account has been blocked kindly contact with FidyPay Team.";
			break;
		default:
			new EmailNotification().sendEmailNotificationLoginAttemptBlocked(name, email, date);
			description = "Invalid email or Password. Your account has been blocked kindly contact with FidyPay Team.";
			break;
		}
		return description;
	}

	@Override
	public Object loginDashboardEnc(EncryptedRequest encryptedRequest) throws JsonProcessingException {

		Map<String, Object> map = new HashMap<>();
		String request = encryptedRequest.getRequest();
		String decrypted = EncryptionDataRequest.decrypt(request);
		org.json.JSONObject object = new org.json.JSONObject(decrypted);
		String email = object.getString("email").toLowerCase();// loginDTO.getEmail().toLowerCase();

		String password = object.getString("password");// loginDTO.getPassword();
		map = loginDashboard(email, password);
		String response = new ObjectMapper().writeValueAsString(map);
		EncryptedResponse encryptedResponse = new EncryptedResponse();
		encryptedResponse.setResponse(EncryptionDataRequest.encrypt(response));
		return encryptedResponse;
	}

	@Override
	public Object loginDashBoardSandBox(@Valid EncryptedRequest encryptedRequest) throws JsonProcessingException {
		Map<String, Object> map = new HashMap<>();
		String request = encryptedRequest.getRequest();
		String decrypted = EncryptionDataRequest.decrypt(request);
		org.json.JSONObject object = new org.json.JSONObject(decrypted);
		String email = object.getString("email").toLowerCase();// loginDTO.getEmail().toLowerCase();

		String password = object.getString("password");// loginDTO.getPassword();
		map = loginDashboardSandBox(email, password);
		String response = new ObjectMapper().writeValueAsString(map);
		EncryptedResponse encryptedResponse = new EncryptedResponse();
		encryptedResponse.setResponse(EncryptionDataRequest.encrypt(response));
		return encryptedResponse;
	}

	private Map<String, Object> loginDashboardSandBox(String email, String password) {

		Map<String, Object> map = new HashedMap<>();

		String getEmail = null;
		String getPassword = null;
		String clientSecret = null;
		String clientId = null;
		long merchantId = 0;
		String bussinessName = null;
		String firstName = null;
		long mTypeId = 0;
		String upiQRCode = null;
		String merchantCity = null;
		String merchantUserType = null;
		long merchantUserId = 0;
		try {
			// Date date = new Date();
			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());
			String nDate = DateAndTime.dateFormatReports(trxnDate.toString());

			email = Encryption.encString(email);
			Optional<MerchantUser> optMerchant = merchantUserRepository.findByMerchantUserEmail(email);
			if (optMerchant.isPresent()) {

				String userType = optMerchant.get().getMerchantUserType();
				if (userType.equalsIgnoreCase(MERCHANT_USER_TYPE_SECONDARY)) {
					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.DESCRIPTION, "Invalid email or Password.");
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
					map.put("date", nDate);
					map.put("loggedIn", false);
					return map;
				}

				char active = optMerchant.get().getIsActive();
				if (active == '0') {
					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.DESCRIPTION, "User is Not Active");
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
					map.put("date", nDate);
					map.put("loggedIn", false);
					return map;
				}

				getEmail = Encryption.decString(optMerchant.get().getMerchantUserEmail());
				getPassword = Encryption.decString(optMerchant.get().getMerchantUserPassword());
				merchantId = optMerchant.get().getMerchantId();
				bussinessName = Encryption.decString(optMerchant.get().getMerchantBusinessName());
				// firstName = Encryption.decString(optMerchant.get().getMerchantUserName());

				merchantUserType = optMerchant.get().getMerchantUserType();
				merchantUserId = optMerchant.get().getMerchantUserId();
				Merchants findListByMerchantId = merchantsRepository.findMerchant(merchantId);

				mTypeId = findListByMerchantId.getMerchantType().getMerchantTypeId(); // check once
				try {
					upiQRCode = findListByMerchantId.getSecondSecAnswer();
					merchantCity = findListByMerchantId.getMerchantCity();

					if ((upiQRCode == null || upiQRCode.equals("NA"))) {
						upiQRCode = bussinessName + ", " + merchantCity;
					}

				} catch (Exception e) {
					merchantCity = findListByMerchantId.getMerchantCity();
					upiQRCode = bussinessName + ", " + merchantCity;
				}

				if (password.equals(getPassword) || password == getPassword) {
					MerchantInfo merchantInfo = merchantInfoRepository.findByMerchantId(merchantId);

					if (active == '1') {
						String uniqueId = detailsService.saveLogInDetails(merchantUserId, "Merchant Dashboard");
						clientSecret = Encryption.decString(merchantInfo.getClientSecret());
						// String mEmail = Encryption.decString(clientSecret);
						clientId = Encryption.decString(String.valueOf(merchantInfo.getClientId()));
						firstName = Encryption.decString(merchantInfo.getUsername());
						password = Encryption.decString(merchantInfo.getPassword());
						String merchantUserName = Encryption.decString(optMerchant.get().getMerchantUserName());
						String basicAuth = BasicAuth.createEncodedText(firstName, password);
						String logo = merchantInfo.getImageUrl();

						if (!logo.equalsIgnoreCase("NA")) {
							logo = ResponseMessage.LOGO_URL + logo;
						}
						String qrImage = merchantInfo.getQrImage();
						if (!qrImage.equalsIgnoreCase("NA")) {
							qrImage = ResponseMessage.LOGO_URL + qrImage;
						}

						MerchantUser merchantUser = merchantUserRepository.findById(merchantUserId).get();
						merchantUser.setLoginCount(0L);
						merchantUserRepository.save(merchantUser);
						map.put("logo", logo);
						map.put("clientId", clientId);
						map.put("clientSecret", clientSecret);
						map.put("email", getEmail);
						map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
						map.put(ResponseMessage.DESCRIPTION, ResponseMessage.LOGIN_SUCCESS_MESSAGE);
						map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
						map.put("date", nDate);
						map.put("merchantTypeId", mTypeId);
						map.put("bussinessName", bussinessName);
						map.put("firstName", firstName);
						map.put("loggedIn", true);
						map.put("upiQRCode", upiQRCode);
						map.put("loginUniqueId", uniqueId);
						map.put("merchantUserType", merchantUserType);
						map.put("merchantUserId", merchantUserId);
						map.put("prod", basicAuth);
						map.put("merchantUserName", merchantUserName);
						map.put("qrImage", qrImage);
					} else {
						map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
						map.put(ResponseMessage.DESCRIPTION, "User is Not Active");
						map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
						map.put("date", nDate);
						map.put("loggedIn", false);
					}
				} else {

					getEmail = Encryption.decString(optMerchant.get().getMerchantUserEmail());
					String name = Encryption.decString(optMerchant.get().getMerchantUserName());
					merchantUserType = optMerchant.get().getMerchantUserType();
					long count = optMerchant.get().getLoginCount();
					merchantUserId = optMerchant.get().getMerchantUserId();
					count = count + 1L;
					MerchantUser merchantUser = merchantUserRepository.findById(merchantUserId).get();
					merchantUser.setLoginCount(count);

					if (count > 2 && merchantUserType.equalsIgnoreCase("primary")) {
						merchantUser.setIsActive('0');
						Merchants merchant = merchantsRepository.findById(merchantId).get();
						merchant.setIsMerchantActive('0');
						merchantsRepository.save(merchant);
					}

					if (count > 2 && merchantUserType.equalsIgnoreCase("secondary")) {
						merchantUser.setIsActive('0');
					}

					merchantUserRepository.save(merchantUser);

					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.DESCRIPTION,
							descriptionForPasswordAttempt((int) count, name, email, trxnDate.toString()));
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
					map.put("date", nDate);
					map.put("loggedIn", false);
				}

			} else {

				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Invalid email or Password.");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put("date", nDate);
				map.put("loggedIn", false);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info("error " + e);
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}

		return map;
	}

	@Override
	public boolean checkServiceExistOrNot(long merchantId, String serviceName) {
		ServiceInfo serviceInfo = serviceInfoRepository.findServiceByName(Encryption.encString(serviceName));
		Long serviceId = serviceInfo.getServiceId();
		if (merchantServiceRepository.existsByServiceIdAndMerchantIdAndIsMerchantServiceActive(serviceId, merchantId,
				'Y')) {

			return true;
		} else {
			return false;
		}

	}

	@Override
	public Map<String, Object> loginDashboardOtp(String email, String password) {

		Map<String, Object> map = new HashedMap<>();

		String mobile = null;
		String getEmail = null;
		String getPassword = null;
		String clientSecret = null;
		String clientId = null;
		long merchantId = 0;
		String bussinessName = null;
		String firstName = null;
		long mTypeId = 0;
		String upiQRCode = null;
		String merchantCity = null;
		String merchantUserType = null;
		String updatePasswordDate = null;
		long merchantUserId = 0;
		try {
			// Date date = new Date();
			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());
			String nDate = DateAndTime.dateFormatReports(trxnDate.toString());

			email = Encryption.encString(email);
			Optional<MerchantUser> optMerchant = merchantUserRepository.findByMerchantUserEmail(email);
			if (optMerchant.isPresent()) {
				char active = optMerchant.get().getIsActive();
				if (active == '0') {
					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.DESCRIPTION, "User is Not Active");
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
					map.put("date", nDate);
					map.put("loggedIn", false);
					return map;
				}

				getEmail = Encryption.decString(optMerchant.get().getMerchantUserEmail());
				getPassword = Encryption.decString(optMerchant.get().getMerchantUserPassword());
				merchantId = optMerchant.get().getMerchantId();
				bussinessName = Encryption.decString(optMerchant.get().getMerchantBusinessName());
				mobile = Encryption.decString(optMerchant.get().getMerchantUserMobileNo());
				updatePasswordDate = optMerchant.get().getUpdatePasswordDate().toString();
				System.out.println("updatePasswordDate " + updatePasswordDate);
				System.out.println("trxnDate " + trxnDate);
				// firstName = Encryption.decString(optMerchant.get().getMerchantUserName());

				merchantUserType = optMerchant.get().getMerchantUserType();
				merchantUserId = optMerchant.get().getMerchantUserId();
				Merchants findListByMerchantId = merchantsRepository.findMerchant(merchantId);

				mTypeId = findListByMerchantId.getMerchantType().getMerchantTypeId(); // check once
				try {
					upiQRCode = findListByMerchantId.getSecondSecAnswer();
					merchantCity = findListByMerchantId.getMerchantCity();

					if ((upiQRCode == null || upiQRCode.equals("NA"))) {
						upiQRCode = bussinessName + ", " + merchantCity;
					}

				} catch (Exception e) {
					merchantCity = findListByMerchantId.getMerchantCity();
					upiQRCode = bussinessName + ", " + merchantCity;
				}

				if (password.equals(getPassword) || password == getPassword) {
					MerchantInfo merchantInfo = merchantInfoRepository.findByMerchantId(merchantId);

					if (active == '1') {
						String uniqueId = detailsService.saveLogInDetails(merchantUserId, "Merchant Dashboard");
						clientSecret = Encryption.decString(merchantInfo.getClientSecret());
						// String mEmail = Encryption.decString(clientSecret);
						clientId = Encryption.decString(String.valueOf(merchantInfo.getClientId()));
						firstName = Encryption.decString(merchantInfo.getUsername());
						password = Encryption.decString(merchantInfo.getPassword());
						String merchantUserName = Encryption.decString(optMerchant.get().getMerchantUserName());
						String basicAuth = BasicAuth.createEncodedText(firstName, password);
						String logo = merchantInfo.getImageUrl();

						if (!logo.equalsIgnoreCase("NA")) {
							logo = ResponseMessage.LOGO_URL + logo;
						}
						String qrImage = merchantInfo.getQrImage();
						if (!qrImage.equalsIgnoreCase("NA")) {
							qrImage = ResponseMessage.LOGO_URL + qrImage;
						}

						// mail send for password expire
						long daysCount = DateUtil.countDays(updatePasswordDate, trxnDate.toString());
						if (daysCount >= 83 && daysCount <= 90) {
							daysCount = 90 - daysCount;

							String token = RandomNumberGenrator.generateToken(merchantUserId);
							String url = ResponseMessage.FORGET_PASSWORD_URL + token;
							String shortUrl = urlGenerater.generateShortUrl(url);
							String response = EmailNotification.sendEmailNotificationForPasswordExpire(merchantUserName,
									shortUrl, getEmail, String.valueOf(daysCount));
							OTPVerification otpVerification = new OTPVerification();
							otpVerification.setBankId(token);
							otpVerification.setCreationDate(trxnDate);
							otpVerification.setMerchantBankAccountNumber(Encryption.encString("NA"));
							otpVerification.setMerchantBankIfsc(Encryption.encString("NA"));
							otpVerification.setMerchantId(merchantUserId);
							otpVerification.setOtp("0");
							otpVerification.setOtpRefId(token);
							otpVerification = otpVerificationRepository.save(otpVerification);

						}

						if (daysCount >= 90) {

							map.put(ResponseMessage.CODE, ResponseMessage.PASSWORD_EXPIRE);
							map.put(ResponseMessage.DESCRIPTION, ResponseMessage.PASSWORD_EXPIRED_MESSAGE);
							map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
							map.put("date", nDate);
							map.put("loggedIn", false);
							return map;
						}

						// send OTP on mobile and email
						String otp = RandomNumberGenrator.generateWalletPin();
						String responseNotification = EmailNotification
								.sendEmailNotificationForDashboardOTP(merchantUserName, otp, getEmail, mobile);
						OTPVerification otpVerification = new OTPVerification();
						otpVerification.setBankId(uniqueId);
						otpVerification.setCreationDate(trxnDate);
						otpVerification.setMerchantBankAccountNumber(Encryption.encString("NA"));
						otpVerification.setMerchantBankIfsc(Encryption.encString("NA"));
						otpVerification.setMerchantId(merchantUserId);
						otpVerification.setOtp(otp);
						otpVerification.setOtpRefId(uniqueId);

						otpVerification = otpVerificationRepository.save(otpVerification);

						MerchantUser merchantUser = merchantUserRepository.findById(merchantUserId).get();
						merchantUser.setLoginCount(0L);
						merchantUserRepository.save(merchantUser);

						map.put("logo", logo);
						map.put("clientId", clientId);
						map.put("clientSecret", clientSecret);
						map.put("email", getEmail);
						map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
						map.put(ResponseMessage.DESCRIPTION, ResponseMessage.OTP_SEND);
						map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
						map.put("date", nDate);
						map.put("merchantTypeId", mTypeId);
						map.put("bussinessName", bussinessName);
						map.put("firstName", firstName);
						map.put("loggedIn", true);
						map.put("upiQRCode", upiQRCode);
						map.put("loginUniqueId", uniqueId);
						map.put("merchantUserType", merchantUserType);
						map.put("merchantUserId", merchantUserId);
						map.put("prod", basicAuth);
						map.put("merchantUserName", merchantUserName);
						map.put("qrImage", qrImage);
					} else {
						map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
						map.put(ResponseMessage.DESCRIPTION, "User is Not Active");
						map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
						map.put("date", nDate);
						map.put("loggedIn", false);
					}
				} else {

					getEmail = Encryption.decString(optMerchant.get().getMerchantUserEmail());
					String name = Encryption.decString(optMerchant.get().getMerchantUserName());
					merchantUserType = optMerchant.get().getMerchantUserType();
					long count = optMerchant.get().getLoginCount();
					merchantUserId = optMerchant.get().getMerchantUserId();
					count = count + 1L;
					MerchantUser merchantUser = merchantUserRepository.findById(merchantUserId).get();
					merchantUser.setLoginCount(count);

					if (count > 2 && merchantUserType.equalsIgnoreCase("primary")) {
						merchantUser.setIsActive('0');
						Merchants merchant = merchantsRepository.findById(merchantId).get();
						merchant.setIsMerchantActive('0');
						merchantsRepository.save(merchant);
					}

					if (count > 2 && merchantUserType.equalsIgnoreCase("secondary")) {
						merchantUser.setIsActive('0');
					}

					merchantUserRepository.save(merchantUser);

					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.DESCRIPTION,
							descriptionForPasswordAttempt((int) count, name, email, trxnDate.toString()));
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
					map.put("date", nDate);
					map.put("loggedIn", false);
				}

			} else {

				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Invalid email or Password.");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put("date", nDate);
				map.put("loggedIn", false);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info("error " + e);
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}

		return map;
	}

	@Override
	public Object loginDashBoardEncOtp(@Valid EncryptedRequest encryptedRequest) throws Exception {

		Map<String, Object> map = new HashMap<>();
		String request = encryptedRequest.getRequest();
		String decrypted = EncryptionDataRequest.decrypt(request);
		org.json.JSONObject object = new org.json.JSONObject(decrypted);
		String email = object.getString("email").toLowerCase();// loginDTO.getEmail().toLowerCase();

		String password = object.getString("password");// loginDTO.getPassword();
		map = loginDashboardOtp(email, password);
		String response = new ObjectMapper().writeValueAsString(map);
		EncryptedResponse encryptedResponse = new EncryptedResponse();
		encryptedResponse.setResponse(EncryptionDataRequest.encrypt(response));
		return encryptedResponse;
	}

	@Override
	public Object verifyLoginOtp(String token, String otp) throws Exception {
		Map<String, Object> map = new HashMap<>();

		if (token.equals("")) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Please enter token");
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			return map;
		}

		if (otp.equals("")) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Please enter otp");
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			return map;
		}

		Optional<OTPVerification> optional = otpVerificationRepository.findOtpANDOtpRefId(otp, token);

		if (!optional.isPresent()) {
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.OTP_BANKINFO_FAILED);
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put("token", token);
			return map;
		}

		Timestamp currentTime = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

		long transactionTimeValidate = DateAndTime.compareTwoTimeStamps(currentTime, optional.get().getCreationDate());

		if (transactionTimeValidate >= 300) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.OTP_EXPIRED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_BAD_REQUEST);
			map.put("token", token);
			return map;

		}

		map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
		map.put(ResponseMessage.DESCRIPTION, ResponseMessage.VALID_OTP_MERTRXNREFID);
		map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
		map.put("token", token);
		return map;
	}

	@Override
	public Map<String, Object> saveUserDetails(long merchantId, MerchantUserPWARequest merchantUserRequest)
			throws Exception {

		Map<String, Object> map = new HashedMap<>();

		Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

		String mobileNo = merchantUserRequest.getMerchantUserMobileNo();
		String email = merchantUserRequest.getMerchantUserEmail().toLowerCase();
		String name = merchantUserRequest.getMerchantUserName();
		String password = merchantId + "Fp@9876";

		if (merchantUserRepository.existsByMerchantUserMobileNo(Encryption.encString(mobileNo))) {
			LOGGER.info(" Inside mobileNo: " + mobileNo);

			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.MOBILE_NUMBER_ALREADY_REGISTERED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

			return map;
		}

		if (merchantUserRepository.existsByMerchantUserEmail(Encryption.encString(email))) {
			LOGGER.info(" Inside email: " + email);

			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.EMAILID_ALREADY_REGISTERED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

			return map;
		}

		String merchantBussinessName = merchantsRepository.findByIdMerchantBussinessName(merchantId);
		String merchantUserKey = merchantId + "SEC" + mobileNo;

		MerchantUser merchantUser = new MerchantUser();
		merchantUser.setDate(trxnDate);
		merchantUser.setMerchantId(merchantId);
		merchantUser.setIsActive('1');
		merchantUser.setMerchantUserEmail(Encryption.encString(email));
		merchantUser.setMerchantUserMobileNo(Encryption.encString(mobileNo));
		merchantUser.setMerchantUserName(Encryption.encString(name));
		merchantUser.setMerchantUserPassword(Encryption.encString(password));
		merchantUser.setMerchantUserType(MERCHANT_USER_TYPE_SECONDARY);
		merchantUser.setMerchantBusinessName(merchantBussinessName);
		merchantUser.setMerchantUserKey(Encryption.encString(merchantUserKey));
		merchantUser.setLoginCount(0L);
		merchantUser.setUpdatePasswordDate(trxnDate);
		merchantUserRepository.save(merchantUser);

		map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
		map.put(ResponseMessage.DESCRIPTION, ResponseMessage.REGISTER_SUCCESSFULLY);
		map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
		map.put("registrationId", merchantUserKey);

		return map;

	}

	private static final String MOBILE_NUMBER_REGEX = "^(\\+\\d{1,3}[- ]?)?\\d{10}$";
	private static final String POSITIVE_INTEGER_REGEX = "^[1-9]\\d*$";

	private static boolean isValidMobileNumber(String mobileNumber) {
		Pattern pattern = Pattern.compile(MOBILE_NUMBER_REGEX);
		return pattern.matcher(mobileNumber).matches();
	}

	private static boolean isValidPositiveInteger(String input) {
		Pattern pattern = Pattern.compile(POSITIVE_INTEGER_REGEX);
		return pattern.matcher(input).matches();
	}

	private Map<String, Object> loginPWADetails(String merchantId, String mobile) throws ParseException {
		Map<String, Object> map = new HashMap<>();

		if (isValidMobileNumber(mobile) == false) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Mobile Number is Incorrect");
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			return map;
		}

		if (isValidPositiveInteger(merchantId) == false) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Invalid mid");
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			return map;
		}

		Optional<MerchantUser> optional = merchantUserRepository.findByMerchantIdAndMerchantUserMobileNoAndIsActive(
				Long.parseLong(merchantId), Encryption.encString(mobile), '1');
		if (!optional.isPresent()) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.MOBILE_NUMBER_NOT_REGISTERED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			return map;
		}

		Optional<Merchants> optionalMerchant = merchantsRepository.findById(Long.parseLong(merchantId));
		if (optionalMerchant.get().getIsMerchantActive() == 0) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, "User is Not Active");
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			return map;
		}

		MerchantInfo merchantInfo = merchantInfoRepository.findByMerchantId(Long.parseLong(merchantId));
		String getEmail = Encryption.decString(optional.get().getMerchantUserEmail());
		String bussinessName = Encryption.decString(optional.get().getMerchantBusinessName());
		String merchantUserName = Encryption.decString(optional.get().getMerchantUserName());
		long merchantUserId = optional.get().getMerchantUserId();
		String mobileUser = Encryption.decString(optional.get().getMerchantUserMobileNo());

		String clientSecret = Encryption.decString(merchantInfo.getClientSecret());
		String clientId = Encryption.decString(String.valueOf(merchantInfo.getClientId()));
		String firstName = Encryption.decString(merchantInfo.getUsername());
		String password = Encryption.decString(merchantInfo.getPassword());
		String basicAuth = BasicAuth.createEncodedText(firstName, password);
		String logo = merchantInfo.getImageUrl();
		String bannerImage = merchantInfo.getOtherInfo1();
		String loginKey = detailsService.saveLogInDetails(merchantUserId, "Merchant Dashboard");
		map.put("logo", logo);
		map.put("clientId", clientId);
		map.put("clientSecret", clientSecret);
		map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
		map.put(ResponseMessage.DESCRIPTION, ResponseMessage.LOGIN_SUCCESS_MESSAGE);
		map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
		map.put("bussinessName", bussinessName);
		map.put("loggedIn", true);
		map.put("loginUniqueId", loginKey);
		map.put("merchantUserId", merchantUserId);
		map.put("prod", basicAuth);
		map.put("merchantUserName", merchantUserName);
		map.put("merchantUserEmail", getEmail);
		map.put("mobile", mobileUser);
		map.put("banner", bannerImage);
		return map;
	}

	@Override
	public Object loginPWA(String merchantId, String mobile) throws Exception {
		Map<String, Object> map = new HashMap<>();
		map = loginPWADetails(merchantId, mobile);
		String response = new ObjectMapper().writeValueAsString(map);
		EncryptedResponse encryptedResponse = new EncryptedResponse();
		encryptedResponse.setResponse(EncryptionDataRequest.encrypt(response));
		return encryptedResponse;

	}

	@Override
	public Map<String, Object> mobileNoVerification(String mobile, String name) throws Exception {
		Map<String, Object> map = new HashMap<>();

		Optional<MerchantUser> optional = merchantUserRepository
				.findByMerchantUserMobileNo(Encryption.encString(mobile));

		if (optional.isPresent()) {
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.MOBILE_NUMBER_ALREADY_REGISTERED);
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			return map;
		}

		Timestamp timeStamp = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());
		String token = UUID.randomUUID().toString();
		String otp = RandomNumberGenrator.generateWalletPin();

		new SMSAPIImpl().registrationOTP(mobile, name, otp);

		OTPVerification otpVerification = new OTPVerification();
		otpVerification.setBankId(token);
		otpVerification.setCreationDate(timeStamp);
		otpVerification.setMerchantBankAccountNumber(Encryption.encString(mobile));
		otpVerification.setMerchantBankIfsc(Encryption.encString("NA"));
		otpVerification.setMerchantId(0);
		otpVerification.setOtp(otp);
		otpVerification.setOtpRefId(token);
		otpVerificationRepository.save(otpVerification);

		map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
		map.put(ResponseMessage.DESCRIPTION, "OTP has been sent to your mobile number");
		map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
		map.put("token", token);
		return map;
	}

	@Override
	public Map<String, Object> otpVerification(String otp, String token) throws Exception {
		Map<String, Object> map = new HashMap<>();

		Optional<OTPVerification> optional = otpVerificationRepository.findOtpANDOtpRefId(otp, token);

		if (!optional.isPresent()) {
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.OTP_BANKINFO_FAILED);
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put("token", token);
			return map;
		}

		Timestamp currentTime = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

		long transactionTimeValidate = DateAndTime.compareTwoTimeStamps(currentTime, optional.get().getCreationDate());

		if (transactionTimeValidate >= 60) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.OTP_EXPIRED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put("token", token);
			return map;

		}

		map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
		map.put(ResponseMessage.DESCRIPTION, ResponseMessage.VALID_OTP_MERTRXNREFID);
		map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
		map.put("token", token);

		return map;
	}

	@Override
	public Map<String, Object> mobileNoLogin(String mobile) throws Exception {
		Map<String, Object> map = new HashMap<>();

		Optional<MerchantUser> optional = merchantUserRepository
				.findByMerchantUserMobileNo(Encryption.encString(mobile));

		if (!optional.isPresent()) {
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.MOBILE_NUMBER_NOT_REGISTERED);
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			return map;
		}

		if (optional.get().getIsActive() == '0') {
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, "User is not active");
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			return map;
		}

		Timestamp timeStamp = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());
		String token = UUID.randomUUID().toString();
		String otp = RandomNumberGenrator.generateWalletPin();

		new SMSAPIImpl().registrationOTP(mobile, Encryption.decString(optional.get().getMerchantUserName()), otp);

		OTPVerification otpVerification = new OTPVerification();
		otpVerification.setBankId(token);
		otpVerification.setCreationDate(timeStamp);
		otpVerification.setMerchantBankAccountNumber(Encryption.encString(mobile));
		otpVerification.setMerchantBankIfsc(Encryption.encString("NA"));
		otpVerification.setMerchantId(0);
		otpVerification.setOtp(otp);
		otpVerification.setOtpRefId(token);
		otpVerificationRepository.save(otpVerification);

		map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
		map.put(ResponseMessage.DESCRIPTION, "OTP has been sent to your mobile number");
		map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
		map.put("token", token);
		return map;
	}

}
