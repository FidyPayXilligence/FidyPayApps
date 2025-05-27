package com.fidypay.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.collections4.map.HashedMap;
import org.json.simple.JSONObject;
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

import com.fidypay.dto.ForgetPassword;
import com.fidypay.dto.MerchantEdit;
import com.fidypay.dto.MerchantLogin;
import com.fidypay.encryption.Encryption;
import com.fidypay.entity.MerchantInfo;
import com.fidypay.entity.MerchantType;
import com.fidypay.repo.MerchantInfoRepository;
import com.fidypay.repo.MerchantTypeRepository;
import com.fidypay.repo.MerchantsRepository;
import com.fidypay.request.MerchantActiveRequest;
import com.fidypay.request.MerchantRegisterRequest;
import com.fidypay.service.MerchantWalletInfoService;
import com.fidypay.service.MerchantsService;
import com.fidypay.utils.constants.ResponseMessage;

import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/merchantService")
public class MerchantServiceController {

	@Autowired
	private MerchantsService merchantsService;

	@Autowired
	private MerchantsRepository merchantsRepository;

	@Autowired
	private MerchantTypeRepository merchantTypeRepository;

	@Autowired
	private MerchantInfoRepository merchantInfoRepository;

	@Autowired
	private MerchantWalletInfoService MerchantWalletInfoService;

	@ApiOperation(value = "Merchant Register And Assign Service")
	@PostMapping("/v2/merchantRegister")
	public Map<String, Object> merchantRegisterAndAssignService(@RequestHeader(value = "MId") String MID,
			@Valid @RequestBody MerchantRegisterRequest merchantRegister) throws Exception {
		Map<String, Object> map = new HashedMap<>();
		try {
			Long merchantTypeId = Long.parseLong(Encryption.decString(MID));
			MerchantType merchantType = merchantTypeRepository.findById(merchantTypeId).get();
			if (merchantType != null) {

				if (merchantsRepository.existsByMerchantEmailAndIsMerchantActive(
						Encryption.encString(merchantRegister.getEmail()), '1')) {
					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.DESCRIPTION, ResponseMessage.EMAIL_ALREADY_ACTIVATED);
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
					return map;
				}

				if (merchantsRepository.existsByMerchantPhoneAndIsMerchantActive(
						Encryption.encString(merchantRegister.getPhone()), '1')) {
					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.DESCRIPTION, ResponseMessage.PHONE_ALREADY_ACTIVATED);
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
					return map;
				}

				return merchantsService.merchantRegisterAndAssignService(merchantTypeId, merchantRegister, 1);

			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.FIELD, "MId");

			}
		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

			map.put(ResponseMessage.FIELD, "MId");
		}
		return map;
	}

	@ApiOperation(value = "merchantDeactive")
	@PostMapping("/merchantDeactive")
	public Map<String, Object> merchantDeactive(@RequestHeader(value = "MId") String MID,
			@Valid @RequestBody MerchantActiveRequest merchantActiveRequest) throws Exception {
		Map<String, Object> map = new HashedMap<>();
		try {
			Long merchantTypeId = Long.parseLong(Encryption.decString(MID));
			MerchantType merchantType = merchantTypeRepository.findById(merchantTypeId).get();
			if (merchantType != null) {

				if (merchantsRepository.existsByMerchantEmailAndIsMerchantActive(
						Encryption.encString(merchantActiveRequest.getEmail()), '0')) {
					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.DESCRIPTION, ResponseMessage.EMAIL_ALREADY_DEACTIVATED);
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
					return map;
				}

				if (merchantsRepository.existsByMerchantPhoneAndIsMerchantActive(
						Encryption.encString(merchantActiveRequest.getPhone()), '0')) {
					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.DESCRIPTION, ResponseMessage.PHONE_ALREADY_DEACTIVATED);
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
					return map;
				}

				return merchantsService.merchantDeactive(merchantActiveRequest);

			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

				map.put(ResponseMessage.FIELD, "MId");

			}
		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

			map.put(ResponseMessage.FIELD, "MId");
		}
		return map;
	}

	@ApiOperation(value = "Merchant Register")
	@PostMapping("/merchantRegister")
	public String merchantRegister(@RequestHeader(value = "MId") String MID,
			@Valid @RequestBody MerchantRegisterRequest merchantRegisterRequest) throws Exception {
		String response = null;
		JSONObject jsonObject = new JSONObject();

		try {
			Long merchantTypeId = Long.parseLong(Encryption.decString(MID));
			MerchantType merchantType = merchantTypeRepository.findById(merchantTypeId).get();

			if (merchantType != null) {
				return response = merchantsService.merchantRegister(merchantRegisterRequest, merchantType);

			} else {
				jsonObject.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

				jsonObject.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
				response = jsonObject.toString();
			}
		} catch (Exception e) {
			jsonObject.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

			jsonObject.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			response = jsonObject.toString();
		}
		return response;
	}

	@ApiOperation(value = "Merchant Login")
	@PostMapping("/merchantLogin")
	public String merchantLogin(@RequestBody MerchantLogin merchantLogin) throws Exception {
		return merchantsService.merchantLogin(merchantLogin);
	}

	@ApiOperation(value = "Forget Password")
	@PostMapping("/forgetPassword")
	public String forgetPassword(@RequestBody ForgetPassword forgetPassword) throws Exception {
		return merchantsService.forgetPassword(forgetPassword);
	}

	@ApiOperation(value = "Merchant Edit")
	@PostMapping("/merchantEdit")
	public String merchantEdit(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, @RequestBody MerchantEdit merchantEdit)
			throws Exception {

		String response = null;
		JSONObject jsonObject = new JSONObject();
		if (clientId == "" || clientSecret == "" || clientId == null || clientSecret == null) {
			jsonObject.put(ResponseMessage.CODE, ResponseMessage.MISSING_PARAMETER);
			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.HEADERS_CANT_EMPTY);
			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

			jsonObject.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			response = jsonObject.toString();
		} else {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {

				response = merchantsService.merchantEdit(merchantEdit);

			} else {
				jsonObject.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);

				jsonObject.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
				response = jsonObject.toString();
			}

		}
		return response;

	}

	@ApiOperation(value = "Merchant Delete")
	@PostMapping("/merchantDelete")
	public String merchantDelete(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, @RequestParam("userId") String userId)
			throws Exception {

		String response = null;
		JSONObject jsonObject = new JSONObject();
		if (clientId == "" || clientSecret == "" || clientId == null || clientSecret == null) {
			jsonObject.put(ResponseMessage.CODE, ResponseMessage.MISSING_PARAMETER);
			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.HEADERS_CANT_EMPTY);
			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			jsonObject.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			response = jsonObject.toString();
		} else {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {
				response = merchantsService.merchantDelete(userId);

			} else {
				jsonObject.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				jsonObject.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
				response = jsonObject.toString();
			}

		}
		return response;

	}

	@ApiOperation(value = "Send OTP")
	@PostMapping("/sendOtp/{userId}")
	public String merchantSendOTP(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, @PathVariable("userId") String userId)
			throws Exception {

		String response = null;
		JSONObject jsonObject = new JSONObject();
		if (clientId == "" || clientSecret == "" || clientId == null || clientSecret == null) {
			jsonObject.put(ResponseMessage.CODE, ResponseMessage.MISSING_PARAMETER);
			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.HEADERS_CANT_EMPTY);
			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			jsonObject.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			response = jsonObject.toString();
		} else {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {
				response = merchantsService.sendOTP(userId);

			} else {
				jsonObject.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				jsonObject.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
				response = jsonObject.toString();
			}

		}
		return response;

	}

	@PostMapping("/virtual-account-list")
	public Map<String, Object> virtualAccoutsList(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret) throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {
				return MerchantWalletInfoService.virtualAccountList(merchantInfo.getMerchantId());
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

	
	@ApiOperation(value = "get Last 5 otp details")
	@GetMapping("/getotp")
	public Map<String, Object> getOTPDetails() throws Exception {
		return merchantsService.getOTPDetails();
	}

	
	
}
