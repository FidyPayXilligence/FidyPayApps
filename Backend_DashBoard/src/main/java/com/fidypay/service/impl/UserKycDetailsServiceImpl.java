package com.fidypay.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fidypay.encryption.Encryption;
import com.fidypay.entity.UserKycDetails;
import com.fidypay.repo.MerchantsRepository;
import com.fidypay.repo.UserKycDetailsRepository;
import com.fidypay.request.UserKycRequest;
import com.fidypay.request.UserKycUpdateRequest;
import com.fidypay.response.UserKycDetailsPayload;
import com.fidypay.service.UserKycDetailsService;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.DateAndTime;
import com.fidypay.utils.ex.GenerateTrxnRefId;

@Service
public class UserKycDetailsServiceImpl implements UserKycDetailsService {

	private static final Logger log = LoggerFactory.getLogger(UserKycDetailsServiceImpl.class);

	@Autowired
	private UserKycDetailsRepository userKycDetailsRepository;

	@Autowired
	private MerchantsRepository merchantsRepository;

	@Override
	public Map<String, Object> addUserKycDetails(UserKycRequest userKycRequest) {
		Map<String, Object> map = new HashMap<>();
		try {
			if (!merchantsRepository.existsByMerchantPhone(Encryption.encString(userKycRequest.getUserMobile()))) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.MERCHANT_MOBILE_MESSAGE);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}
			
			if (!merchantsRepository.existsByMerchantEmail(Encryption.encString(userKycRequest.getUserEmail()))) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.MERCHANT_EMAIL_MESSAGE);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}
			
			if (userKycDetailsRepository.existsByUserMobile(Encryption.encString(userKycRequest.getUserMobile()))) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.USER_KYC_MOBILE_MESSAGE);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}
			
			if (userKycDetailsRepository.existsByUserEmail(Encryption.encString(userKycRequest.getUserEmail()))) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.USER_KYC_EMAIL_MESSAGE);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}

			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			UserKycDetails userKycDetails = new UserKycDetails();
			userKycDetails.setAadharImageId(Encryption.encString("NA"));
			userKycDetails.setAadharNo(Encryption.encString("NA"));
			userKycDetails.setIsKycId('0');
			userKycDetails.setIsVerifiedAadhar('0');
			userKycDetails.setIsVerifiedAccount('0');
			userKycDetails.setIsVerifiedPan('0');
			userKycDetails.setKycDate(trxnDate);
			userKycDetails.setLastKycUpdateDate(trxnDate);
			userKycDetails.setPanCardImageId(Encryption.encString("NA"));
			userKycDetails.setPanCardNo(Encryption.encString("NA"));
			userKycDetails.setUseFullName(Encryption.encString("NA"));
			userKycDetails.setUserAccountNo(Encryption.encString("NA"));
			userKycDetails.setUserAddress1(Encryption.encString("NA"));
			userKycDetails.setUserAddress2(Encryption.encString("NA"));
			userKycDetails.setUserCity(Encryption.encString("NA"));
			userKycDetails.setUserDob(Encryption.encString("NA"));
			userKycDetails.setUserEmail(Encryption.encString(userKycRequest.getUserEmail()));
			userKycDetails.setUserFirstName(Encryption.encString("NA"));
			userKycDetails.setUserIfsc(Encryption.encString("NA"));
			userKycDetails.setUserLastName(Encryption.encString("NA"));
			userKycDetails.setUserMobile(Encryption.encString(userKycRequest.getUserMobile()));
			userKycDetails.setUserPincode(Encryption.encString("NA"));
			userKycDetails.setUserState(Encryption.encString("NA"));
			userKycDetails.setImageId(Encryption.encString("NA"));
			userKycDetails.setGender("NA");
			
			String randomString = GenerateTrxnRefId.getAlphaNumericString(5);
			String userUniqueId = randomString + userKycRequest.getUserMobile();

			userKycDetails.setUserUniqueId(Encryption.encString(userUniqueId));
			userKycDetails = userKycDetailsRepository.save(userKycDetails);

			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, "User kyc details added successfully");
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			map.put("userKycId", userKycDetails.getUserKycId());
			map.put("userUniqueId", userUniqueId);
			map.put("userMobile", userKycRequest.getUserMobile());

		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("EXCEPTION CONDITION CASE: {}");
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}
		return map;
	}

	@Override
	public Map<String, Object> updateUserKycDetails(@Valid UserKycUpdateRequest userKycUpdateRequest) {
		Map<String, Object> map = new HashMap<>();
		try {
           char isKyc = '0';

			if (!userKycDetailsRepository
					.existsByUserMobile(Encryption.encString(userKycUpdateRequest.getUserMobile()))) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, "User mobile number does not exist");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}

			if (userKycUpdateRequest.getIsVerifiedAadhar().equals("1")  && userKycUpdateRequest.getIsVerifiedAccount().equals("1") 
					&& userKycUpdateRequest.getIsVerifiedPan().equals("1")) {
				isKyc = '1';
			}

			Timestamp trxnDate = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			UserKycDetails userKycDetails = userKycDetailsRepository
					.findByUserMobile(Encryption.encString(userKycUpdateRequest.getUserMobile()));
			userKycDetails.setAadharImageId(Encryption.encString(userKycUpdateRequest.getAadharImageId()));
			userKycDetails.setAadharNo(Encryption.encString(userKycUpdateRequest.getAadharNo()));
			userKycDetails.setIsKycId(isKyc);
			userKycDetails.setIsVerifiedAadhar(userKycUpdateRequest.getIsVerifiedAadhar().charAt(0));
			userKycDetails.setIsVerifiedAccount(userKycUpdateRequest.getIsVerifiedAccount().charAt(0));
			userKycDetails.setIsVerifiedPan(userKycUpdateRequest.getIsVerifiedPan().charAt(0));
			userKycDetails.setLastKycUpdateDate(trxnDate);
			userKycDetails.setPanCardImageId(Encryption.encString(userKycUpdateRequest.getPanCardImageId()));
			userKycDetails.setPanCardNo(Encryption.encString(userKycUpdateRequest.getPanCardNo()));
			userKycDetails.setUseFullName(Encryption.encString(userKycUpdateRequest.getUseFullName()));
			userKycDetails.setUserAccountNo(Encryption.encString(userKycUpdateRequest.getUserAccountNo()));
			userKycDetails.setUserAddress1(Encryption.encString(userKycUpdateRequest.getUserAddress1()));
			userKycDetails.setUserAddress2(Encryption.encString(userKycUpdateRequest.getUserAddress2()));
			userKycDetails.setUserCity(Encryption.encString(userKycUpdateRequest.getUserCity()));
			userKycDetails.setUserDob(Encryption.encString(userKycUpdateRequest.getUserDob()));
			userKycDetails.setUserEmail(Encryption.encString(userKycUpdateRequest.getUserEmail()));
			userKycDetails.setUserFirstName(Encryption.encString(userKycUpdateRequest.getUserFirstName()));
			userKycDetails.setUserIfsc(Encryption.encString(userKycUpdateRequest.getUserIfsc()));
			userKycDetails.setUserLastName(Encryption.encString(userKycUpdateRequest.getUserLastName()));
			userKycDetails.setUserPincode(Encryption.encString(userKycUpdateRequest.getUserPincode()));
			userKycDetails.setUserState(Encryption.encString(userKycUpdateRequest.getUserState()));
			userKycDetails.setImageId(Encryption.encString(userKycUpdateRequest.getImageId()));
			userKycDetails.setGender(userKycUpdateRequest.getGender());			
			userKycDetails = userKycDetailsRepository.save(userKycDetails);

			String userUniqueId = Encryption.decString(userKycDetails.getUserUniqueId());
			String userMobile = Encryption.decString(userKycDetails.getUserMobile());

			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, "User kyc details update successfully");
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			map.put("userUniqueId", userUniqueId);
			map.put("userMobile", userMobile);

		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("EXCEPTION CONDITION CASE: {}");
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}
		return map;
	}

	@Override
	public Map<String, Object> findUserKycDetailsByMobileNo(String mobileNo) {
		Map<String, Object> map = new HashMap<>();
		try {

			if (!userKycDetailsRepository.existsByUserMobile(Encryption.encString(mobileNo))) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, "User mobile number does not exist");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}

			List<UserKycDetails> list = null;
			List<UserKycDetailsPayload> userKycList = new ArrayList<UserKycDetailsPayload>();
			list = userKycDetailsRepository.findByUserMobileNo(Encryption.encString(mobileNo));

			if (list.size() != 0) {
				list.forEach(userKycDetails -> {

					UserKycDetailsPayload payload = new UserKycDetailsPayload();
					payload.setAadharImageId(Encryption.decString(userKycDetails.getAadharImageId()));
					payload.setAadharNo(Encryption.decString(userKycDetails.getAadharNo()));
					payload.setIsKycId(userKycDetails.getIsKycId());
					payload.setIsVerifiedAadhar(userKycDetails.getIsVerifiedAadhar());
					payload.setIsVerifiedAccount(userKycDetails.getIsVerifiedAccount());
					payload.setIsVerifiedPan(userKycDetails.getIsVerifiedPan());
					payload.setKycDate(userKycDetails.getKycDate().toString());
					payload.setLastKycUpdateDate(userKycDetails.getLastKycUpdateDate().toString());
					payload.setPanCardImageId(Encryption.decString(userKycDetails.getPanCardImageId()));
					payload.setPanCardNo(Encryption.decString(userKycDetails.getPanCardNo()));
					payload.setUseFullName(Encryption.decString(userKycDetails.getUseFullName()));
					payload.setUserAccountNo(Encryption.decString(userKycDetails.getUserAccountNo()));
					payload.setUserAddress1(Encryption.decString(userKycDetails.getUserAddress1()));
					payload.setUserAddress2(Encryption.decString(userKycDetails.getUserAddress2()));
					payload.setUserCity(Encryption.decString(userKycDetails.getUserCity()));
					payload.setUserDob(Encryption.decString(userKycDetails.getUserDob()));
					payload.setUserEmail(Encryption.decString(userKycDetails.getUserEmail()));
					payload.setUserFirstName(Encryption.decString(userKycDetails.getUserFirstName()));
					payload.setUserIfsc(Encryption.decString(userKycDetails.getUserIfsc()));
					payload.setUserLastName(Encryption.decString(userKycDetails.getUserLastName()));
					payload.setUserMobile(Encryption.decString(userKycDetails.getUserMobile()));
					payload.setUserPincode(Encryption.decString(userKycDetails.getUserPincode()));
					payload.setUserState(Encryption.decString(userKycDetails.getUserState()));
					payload.setUserUniqueId(Encryption.decString(userKycDetails.getUserUniqueId()));
					payload.setImageId(Encryption.decString(userKycDetails.getImageId()));
					payload.setGender(userKycDetails.getGender());
					userKycList.add(payload);
				});

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "User kyc details");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put("data", userKycList);
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_AVAILABLE);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("EXCEPTION CONDITION CASE: {}");
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}
		return map;
	}

	@Override
	public Map<String, Object> findUserKycDetailsByUserUniqueId(String userUniqueId) {
		Map<String, Object> map = new HashMap<>();
		try {
			if (!userKycDetailsRepository.existsByUserUniqueId(Encryption.encString(userUniqueId))) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, "User unique id does not exist");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}

			List<UserKycDetails> list = null;
			List<UserKycDetailsPayload> userKycList = new ArrayList<UserKycDetailsPayload>();
			list = userKycDetailsRepository.findByUserUniqueId(Encryption.encString(userUniqueId));

			if (list.size() != 0) {
				list.forEach(userKycDetails -> {
					UserKycDetailsPayload payload = new UserKycDetailsPayload();
					payload.setAadharImageId(Encryption.decString(userKycDetails.getAadharImageId()));
					payload.setAadharNo(Encryption.decString(userKycDetails.getAadharNo()));
					payload.setIsKycId(userKycDetails.getIsKycId());
					payload.setIsVerifiedAadhar(userKycDetails.getIsVerifiedAadhar());
					payload.setIsVerifiedAccount(userKycDetails.getIsVerifiedAccount());
					payload.setIsVerifiedPan(userKycDetails.getIsVerifiedPan());
					payload.setKycDate(userKycDetails.getKycDate().toString());
					payload.setLastKycUpdateDate(userKycDetails.getLastKycUpdateDate().toString());
					payload.setPanCardImageId(Encryption.decString(userKycDetails.getPanCardImageId()));
					payload.setPanCardNo(Encryption.decString(userKycDetails.getPanCardNo()));
					payload.setUseFullName(Encryption.decString(userKycDetails.getUseFullName()));
					payload.setUserAccountNo(Encryption.decString(userKycDetails.getUserAccountNo()));
					payload.setUserAddress1(Encryption.decString(userKycDetails.getUserAddress1()));
					payload.setUserAddress2(Encryption.decString(userKycDetails.getUserAddress2()));
					payload.setUserCity(Encryption.decString(userKycDetails.getUserCity()));
					payload.setUserDob(Encryption.decString(userKycDetails.getUserDob()));
					payload.setUserEmail(Encryption.decString(userKycDetails.getUserEmail()));
					payload.setUserFirstName(Encryption.decString(userKycDetails.getUserFirstName()));
					payload.setUserIfsc(Encryption.decString(userKycDetails.getUserIfsc()));
					payload.setUserLastName(Encryption.decString(userKycDetails.getUserLastName()));
					payload.setUserMobile(Encryption.decString(userKycDetails.getUserMobile()));
					payload.setUserPincode(Encryption.decString(userKycDetails.getUserPincode()));
					payload.setUserState(Encryption.decString(userKycDetails.getUserState()));
					payload.setUserUniqueId(Encryption.decString(userKycDetails.getUserUniqueId()));
					payload.setImageId(Encryption.decString(userKycDetails.getImageId()));
					payload.setGender(userKycDetails.getGender());
					userKycList.add(payload);
				});

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "User kyc details");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put("data", userKycList);
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_AVAILABLE);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("EXCEPTION CONDITION CASE: {}");
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}
		return map;
	}

	@Override
	public Map<String, Object> checkUserKycStatus(String userMobile) {
		Map<String, Object> map = new HashMap<>();
		try {
			char isKyc = '0';

			if (!userKycDetailsRepository.existsByUserMobile(Encryption.encString(userMobile))) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, "User mobile number does not exist");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}

			UserKycDetails userKycDetails = userKycDetailsRepository.findByUserMobile(Encryption.encString(userMobile));

			isKyc = userKycDetails.getIsKycId();

			if (isKyc == '1') {

				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, "User kyc is verified");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, "User kyc is not verified");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("EXCEPTION CONDITION CASE: {}");
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}
		return map;
	}

}
