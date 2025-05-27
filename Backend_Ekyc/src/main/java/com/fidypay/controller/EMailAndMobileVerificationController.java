package com.fidypay.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.fidypay.encryption.Encryption;
import com.fidypay.entity.MerchantInfo;
import com.fidypay.repo.MerchantInfoRepository;
import com.fidypay.request.CustomerDetailsRequest;
import com.fidypay.service.EMailAndMobileVerificationService;
import com.fidypay.utils.constants.AuthenticationVerify;
import com.fidypay.utils.constants.ResponseMessage;

import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class EMailAndMobileVerificationController {

	private static final Logger LOGGER = LoggerFactory.getLogger(EMailAndMobileVerificationController.class);

	@Autowired
	private EMailAndMobileVerificationService eMailAndMobileVerificationService;

	@Autowired
	private MerchantInfoRepository merchantsInfoRepository;

	@ApiOperation(value = "Verify Email")
	@PostMapping("/verifyEmail")
	public Map<String, Object> sendOTPEmail(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@Valid @RequestBody CustomerDetailsRequest customerDetailsRequest) throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {
			String password = AuthenticationVerify.authenticationPassword(Authorization);
			String firstName = AuthenticationVerify.authenticationUsername(Authorization);
			LOGGER.info(" clientSecret " + clientSecret);
			MerchantInfo merchantInfo = merchantsInfoRepository.findByClientIdAndClientSecretAndUserNameAndPassword(
					Encryption.encString(clientId), Encryption.encString(clientSecret), Encryption.encString(firstName),
					Encryption.encString(password));
			if (merchantInfo.getIsMerchantActive() == '1'
					&& Encryption.encString(password).equals(merchantInfo.getPassword())
					&& Encryption.encString(firstName).equals(merchantInfo.getUsername())
					&& Encryption.encString(clientSecret).equals(merchantInfo.getClientSecret())) {

				return  eMailAndMobileVerificationService.sendOTPEmail(customerDetailsRequest,
						merchantInfo.getMerchantId());
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

	@ApiOperation(value = "Verify Mobile")
	@PostMapping("/verifyMobile")
	public  Map<String, Object> sendOTPPhone(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@Valid @RequestBody CustomerDetailsRequest customerDetailsRequest) throws Exception {

		Map<String, Object> map = new HashMap<>();
		try {
			String password = AuthenticationVerify.authenticationPassword(Authorization);
			String firstName = AuthenticationVerify.authenticationUsername(Authorization);
			LOGGER.info(" clientSecret " + clientSecret);
			MerchantInfo merchantInfo = merchantsInfoRepository.findByClientIdAndClientSecretAndUserNameAndPassword(
					Encryption.encString(clientId), Encryption.encString(clientSecret), Encryption.encString(firstName),
					Encryption.encString(password));
			if (merchantInfo.getIsMerchantActive() == '1'
					&& Encryption.encString(password).equals(merchantInfo.getPassword())
					&& Encryption.encString(firstName).equals(merchantInfo.getUsername())
					&& Encryption.encString(clientSecret).equals(merchantInfo.getClientSecret())) {

				return  eMailAndMobileVerificationService.sendOTPPhone(customerDetailsRequest,
						merchantInfo.getMerchantId());
			
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

	@ApiOperation(value = "otpVerification")
	@PostMapping("/{otp}/otpVerification/{otpToken}")
	public Map<String, Object> otpVerification(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@PathVariable("otp") String otp, @PathVariable("otpToken") String otpToken) throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {
			String password = AuthenticationVerify.authenticationPassword(Authorization);
			String firstName = AuthenticationVerify.authenticationUsername(Authorization);
			LOGGER.info(" clientSecret " + clientSecret);
			MerchantInfo merchantInfo = merchantsInfoRepository.findByClientIdAndClientSecretAndUserNameAndPassword(
					Encryption.encString(clientId), Encryption.encString(clientSecret), Encryption.encString(firstName),
					Encryption.encString(password));
			if (merchantInfo.getIsMerchantActive() == '1'
					&& Encryption.encString(password).equals(merchantInfo.getPassword())
					&& Encryption.encString(firstName).equals(merchantInfo.getUsername())
					&& Encryption.encString(clientSecret).equals(merchantInfo.getClientSecret())) {

				return  eMailAndMobileVerificationService.otpVerification(otp, otpToken, merchantInfo.getMerchantId());
//				return ResponseEntity.ok().body(map);
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
}
