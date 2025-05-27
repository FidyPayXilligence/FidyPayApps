package com.fidypay.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fidypay.encryption.Encryption;
import com.fidypay.entity.MerchantInfo;
import com.fidypay.repo.MerchantInfoRepository;
import com.fidypay.request.MerchantSubMerchantOnboardingRequest;
import com.fidypay.request.Pagination;
import com.fidypay.request.SubMerchantOnboardingRequest;
import com.fidypay.service.SubMerchantTempService;
import com.fidypay.utils.constants.AuthenticationVerify;
import com.fidypay.utils.constants.ResponseMessage;

import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/sub-merchant-temp")
public class SubMerchantTempController {

	@Autowired
	private SubMerchantTempService subMerchantTempService;

	@Autowired
	private MerchantInfoRepository merchantInfoRepository;

	@ApiOperation(value = "Save Sub Merchant")
	@PostMapping("/save")
	public Map<String, Object> saveDetails(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret,
			@Valid @RequestBody MerchantSubMerchantOnboardingRequest subMerchantRequest) throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {
				String merchantBusinessName = Encryption.decString(merchantInfo.getMerchantBusinessName());
				String logo = merchantInfo.getImageUrl();
				return subMerchantTempService.saveDetails(Long.parseLong(Encryption.decString(clientId)),
						subMerchantRequest, merchantBusinessName, merchantInfo.getBankIdUpi(), logo);

			} else {

				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);

			}
		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}
		return map;
	}

	@ApiOperation(value = "Find All Sub Merchant List")
	@PostMapping("/findAll")
	public Map<String, Object> findAll(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, @RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate, @RequestBody Pagination pagination) throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {
				long merchantId = Long.parseLong(Encryption.decString(clientId));
				return subMerchantTempService.findAllSubMerchant(startDate, endDate, pagination, merchantId);

			} else {

				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);

			}
		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}
		return map;
	}

	@ApiOperation(value = "Find By Mobile Or Token")
	@PostMapping("/findBy/{key}/{value}")
	public Map<String, Object> findByTokenOrMobile(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, @PathVariable(value = "key") String key,
			@PathVariable(value = "value") String value) throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {
				long merchantId = Long.parseLong(Encryption.decString(clientId));
				return subMerchantTempService.findByTokenOrMobile(merchantId, value, key);
			} else {

				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);

			}
		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}
		return map;
	}

	@ApiOperation(value = "Find By Sub Merchant Temp ID")
	@PostMapping("/findBySubMerchantTempId/{subMerchantTempId}")
	public Map<String, Object> findBySubMerchantTempId(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret,
			@PathVariable(value = "subMerchantTempId") String subMerchantTempId) throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {
				long merchantId = Long.parseLong(Encryption.decString(clientId));
				return subMerchantTempService.findBySubMerchantTempId(merchantId, subMerchantTempId);
			} else {

				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);

			}
		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}
		return map;
	}

	@ApiOperation(value = "Find By Token")
	@PostMapping("/findByToken/{token}")
	public Map<String, Object> findByToken(@PathVariable(value = "token") String token) throws Exception {
		return subMerchantTempService.findByToken(token);
	}

	@ApiOperation(value = "Find By Token")
	@PostMapping("/findByTokenNew/{token}")
	public Map<String, Object> findByTokenNew(@PathVariable(value = "token") String token) throws Exception {
		return subMerchantTempService.findByTokenNew(token);
	}

	@ApiOperation(value = "sendOTPPhone Phone")
	@PostMapping("/sendOTPPhone/{mobile}")
	public Map<String, Object> sendOTPPhone(@PathVariable("mobile") String mobile) throws Exception {
		return subMerchantTempService.sendOTPPhone(mobile);
	}

	@ApiOperation(value = "otpVerification")
	@PostMapping("/{otp}/otpVerification/{otpToken}")
	public Map<String, Object> otpVerification(@PathVariable("otp") String otp,
			@PathVariable("otpToken") String otpToken) throws Exception {
		return subMerchantTempService.otpVerification(otp, otpToken);

	}

	@ApiOperation(value = "Update Sub Merchant")
	@PostMapping("/update-details/{token}")
	public Map<String, Object> updateDetails(@PathVariable("token") String token,
			@Valid @RequestBody MerchantSubMerchantOnboardingRequest onboardingFormRequest) throws Exception {
		return subMerchantTempService.updateDetails(onboardingFormRequest, token);
	}

	@ApiOperation(value = "Resend Notification")
	@PostMapping("/resendNotification/{subMerchantTempId}")
	public Map<String, Object> resendNotification(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret,
			@PathVariable(value = "subMerchantTempId") String subMerchantTempId) throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {
				long merchantId = Long.parseLong(Encryption.decString(clientId));
				String merchantBusinessName = Encryption.decString(merchantInfo.getMerchantBusinessName());
				String logo = merchantInfo.getImageUrl();
				return subMerchantTempService.resendNotification(merchantId, subMerchantTempId, merchantBusinessName,
						logo);
			} else {

				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);

			}
		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}
		return map;
	}

	@ApiOperation(value = "Save Sub Merchant Onboarding Scheduler")
	@GetMapping("/save-merchant-submerchant-Info")
	public Map<String, Object> saveMerchantSubMerchant() throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {
			return subMerchantTempService.saveMerchantSubMerchant("", "");
		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
		}
		return map;
	}

	@ApiOperation(value = "Save Sub Merchant Onboarding Scheduler Date")
	@GetMapping("/save-merchant-submerchant-Info-date/{fromDate}/to/{toDate}")
	public Map<String, Object> saveMerchantSubMerchantByDate(@PathVariable String fromDate, @PathVariable String toDate)
			throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {
			return subMerchantTempService.saveMerchantSubMerchant(fromDate, toDate);
		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
		}
		return map;
	}

	@ApiOperation(value = "Save Sub Merchant Onboarding Scheduler Date")
	@GetMapping("/save-merchant-submerchant-tempid/{subMerchantTempId}")
	public Map<String, Object> saveMerchantSubMerchantByTempId(@PathVariable String subMerchantTempId)
			throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {
			return subMerchantTempService.saveMerchantSubMerchantByTempId(subMerchantTempId);
		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
		}
		return map;
	}

	@ApiOperation(value = "Verify Or reject")
	@PostMapping("/{isOnboarding}/verifySubMerchant/{subMerchantTempId}")
	public Map<String, Object> verifySubMerchant(@PathVariable(value = "subMerchantTempId") String subMerchantTempId,
			@PathVariable(value = "isOnboarding") String isOnboarding) throws Exception {
		return subMerchantTempService.verifySubMerchant(subMerchantTempId, isOnboarding);
	}

	@ApiOperation(value = "Resend Notification Sub Merchant Temp ID")
	@PostMapping("/resend-notification/{subMerchantTempId}")
	public Map<String, Object> resendNotificationByTempId(
			@PathVariable(value = "subMerchantTempId") String subMerchantTempId) throws Exception {
		return subMerchantTempService.resendNotificationByTempId(subMerchantTempId);
	}

	// PWA APIs

	@ApiOperation(value = "Create Sub Merchant airtel")
	@PostMapping("/create-vpa/pwa/{merchantUserId}")
	public Map<String, Object> createVPA(@RequestHeader("Authorization") String Authorization,
			@RequestHeader("Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@Valid @RequestBody SubMerchantOnboardingRequest subMerchantRequest, @PathVariable String merchantUserId)
			throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {
			String password = AuthenticationVerify.authenticationPassword(Authorization);
			String firstName = AuthenticationVerify.authenticationUsername(Authorization);
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndUserNameAndPassword(
					Encryption.encString(clientId), Encryption.encString(clientSecret), Encryption.encString(firstName),
					Encryption.encString(password));
			if (merchantInfo.getIsMerchantActive() == '1'
					&& Encryption.encString(password).equals(merchantInfo.getPassword())
					&& Encryption.encString(firstName).equals(merchantInfo.getUsername())
					&& Encryption.encString(clientSecret).equals(merchantInfo.getClientSecret())) {
				String merchantBusinessName = Encryption.decString(merchantInfo.getMerchantBusinessName());
				String logo = merchantInfo.getImageUrl();
				return subMerchantTempService.createVPAPWA(Long.parseLong(Encryption.decString(clientId)),
						subMerchantRequest, merchantBusinessName, merchantInfo.getBankIdUpi(), logo, merchantUserId);

			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			}
		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;
	}

	@ApiOperation(value = "find  Active And InActive Merchant Count")
	@PostMapping("/find-active-inactive-merchant-count/pwa/{merchantUserId}")
	public Map<String, Object> findActiveInActiveMerchantCountPWA(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, @PathVariable String merchantUserId) {
		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {
				long merchantId = Long.parseLong(Encryption.decString(clientId));
				return subMerchantTempService.findActiveInActiveMerchantCount(merchantId, merchantUserId);
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;

	}

	@ApiOperation(value = "Find All Sub Merchant List")
	@PostMapping("/findAll/pwa/{merchantUserId}")
	public Map<String, Object> findAllPWA(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, @RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate, @RequestBody Pagination pagination,
			@PathVariable String merchantUserId) throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {
				long merchantId = Long.parseLong(Encryption.decString(clientId));
				return subMerchantTempService.findAllSubMerchantPWA(startDate, endDate, pagination, merchantId,
						merchantUserId);

			} else {

				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);

			}
		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}
		return map;
	}

	@ApiOperation(value = "Find By Mobile Or Token")
	@PostMapping("/findBy/{merchantUserId}/pwa/{key}/{value}")
	public Map<String, Object> findByTokenOrMobilePWA(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, @PathVariable(value = "key") String key,
			@PathVariable(value = "value") String value, @PathVariable String merchantUserId) throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {
				long merchantId = Long.parseLong(Encryption.decString(clientId));
				return subMerchantTempService.findByTokenOrMobilePWA(merchantId, value, key, merchantUserId);
			} else {

				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);

			}
		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}
		return map;
	}

}
