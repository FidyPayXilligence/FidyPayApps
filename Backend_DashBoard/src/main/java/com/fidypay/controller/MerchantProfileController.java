package com.fidypay.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fidypay.dto.LoginDTO;
import com.fidypay.encryption.Encryption;
import com.fidypay.entity.MerchantInfo;
import com.fidypay.repo.MerchantInfoRepository;
import com.fidypay.request.EncryptedRequest;
import com.fidypay.request.ForgetPasswordRequest;
import com.fidypay.request.UpdateUserPassword;
import com.fidypay.service.MerchantUserService;
import com.fidypay.service.MerchantsService;
import com.fidypay.service.ServiceInfoService;
import com.fidypay.utils.constants.ResponseMessage;

import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/accountDetails")
public class MerchantProfileController {

	private static final Logger LOGGER = LoggerFactory.getLogger(MerchantProfileController.class);

	@Autowired
	private MerchantsService merchantService;

	@Autowired
	private ServiceInfoService serviceInfoService;

	@Autowired
	private MerchantInfoRepository merchantInfoRepository;

	@Autowired
	private MerchantUserService merchantUserService;

	@ApiOperation(value = "Merchant Details")
	@GetMapping("/merchantDetails")
	public String showMerchantDetails(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret) throws Exception {
		String response = null;
		JSONObject jsonObject = new JSONObject();
		LOGGER.info("Client-Id : " + clientId + " Client-Secret : " + clientSecret);
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {
				response = merchantService.findMerchantDetails(merchantInfo.getMerchantId());

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
		LOGGER.info(" Merchant Details API Response : " + response);
		return response;
	}

	@ApiOperation(value = "Merchant User Details")
	@GetMapping("/merchantDetails/{merchantUserId}")
	public String showMerchantUserDetails(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, @PathVariable long merchantUserId) throws Exception {
		String response = null;
		JSONObject jsonObject = new JSONObject();
		LOGGER.info("Client-Id : " + clientId + " Client-Secret : " + clientSecret);
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {
				response = merchantService.findMerchantUserDetails(merchantInfo.getMerchantId(), merchantUserId);

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
		LOGGER.info(" Merchant Details API Response : " + response);
		return response;
	}

	@ApiOperation(value = "Login")
	@PostMapping("/login")
	public Map<String, Object> loginDashBoard(@Valid @RequestBody LoginDTO loginDTO) throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {
			String email = loginDTO.getEmail().toLowerCase();
			String password = loginDTO.getPassword();
			map = merchantUserService.loginDashboard(email, password);
		} catch (Exception e) {
			LOGGER.info("error " + e);
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;
	}

	@ApiOperation(value = "check Merchant")
	@GetMapping(value = "/checkMerchant")
	public Map<String, Object> checkMerchant(@RequestParam("email") String email,
			@RequestParam("password") String password) {
		Map<String, Object> map = new HashMap<>();
		// return serviceInfoService.getTransactionsList(transactionsReportRequest);

		if (email == "" || email == null) {
			map.put(ResponseMessage.CODE, ResponseMessage.MISSING_PARAMETER);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.EMAIL_CANT_EMPTY);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.EMAIL);

		} else {

			if (email != null || !email.equalsIgnoreCase("")) {

				return merchantService.checkMerchant(email, password);

			}
		}
		return map;

	}

	@ApiOperation(value = "Change Password By User Id")
	@PostMapping("/changePassword")
	public Map<String, Object> updatePassword(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret,
			@Valid @RequestBody UpdateUserPassword updateUserPassword) {
		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			LOGGER.info(" Merchant Info : " + merchantInfo);
			if (merchantInfo != null) {
				LOGGER.info(" Merchant Info Inside : " + merchantInfo);

				return merchantUserService.checkPassword(updateUserPassword,
						Long.parseLong(Encryption.decString(clientId)));
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

	@ApiOperation(value = "Merchant Authorization")
	@GetMapping("/merchantAuthorization")
	public String showClientIdClientSecretKey(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret) throws Exception {
		String response = null;
		JSONObject jsonObject = new JSONObject();
		LOGGER.info("Client-Id : " + clientId + " Client-Secret : " + clientSecret);
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {
				jsonObject.put("ClientId", clientId);
				jsonObject.put("ClientSecret", clientSecret);

				response = jsonObject.toString();

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
		LOGGER.info(" Change Password API Response : " + response);
		return response;
	}

	@ApiOperation(value = "Get Services")
	@GetMapping("/getServices")
	public Map<String, Object> getServiceByMerchantId(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret) throws Exception {
		Map<String, Object> map = new HashMap<>();
		LOGGER.info("Client-Id : " + clientId + " Client-Secret : " + clientSecret);
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {
				return serviceInfoService.getServiceByMechantId(merchantInfo.getMerchantId());

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

	@ApiOperation(value = "Get ServicesOld")
	@GetMapping("/getServicesOld")
	public String getServiceByMerchantIdOld(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret) throws Exception {
		String response = null;
		JSONObject jsonObject = new JSONObject();
		LOGGER.info("Client-Id : " + clientId + " Client-Secret : " + clientSecret);
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {
				response = serviceInfoService.getServiceByMechantIdOld(merchantInfo.getMerchantId());

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
		LOGGER.info(" Service Reports API Response : " + response);
		return response;
	}

	@ApiOperation(value = "Get Merchant Wallet Balance")
	@GetMapping("/getMerchantWalletBalance")
	public Map<String, Object> getMerchantWalletBalance(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret) throws Exception {
		Map<String, Object> map = new HashMap<>();
		LOGGER.info("Client-Id : " + clientId + " Client-Secret : " + clientSecret);
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {

				return serviceInfoService.getMerchantWalletBalance(merchantInfo.getMerchantId());

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

	@ApiOperation(value = "Get Total Transaction Detail")
	@GetMapping("/getTotalTransactionDetail")
	public String getTotalTransactionDetail(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret) throws Exception {
		String response = null;
		JSONObject jsonObject = new JSONObject();
		LOGGER.info("Client-Id : " + clientId + " Client-Secret : " + clientSecret);
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {

				response = serviceInfoService.getTotalTrxn(merchantInfo.getMerchantId());

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
		LOGGER.info(" Total Trxn API Response : " + response);
		return response;
	}

	@ApiOperation(value = "Get Merchant Service Details")
	@GetMapping("/getMerchantServiceDetails")
	public Map<String, Object> getMerchantServiceDetails(@RequestParam(value = "serviceName") String serviceName)
			throws Exception {
		return serviceInfoService.getMerchantServiceDetails(serviceName);
	}

	@ApiOperation(value = "Verify Merchant Email")
	@PostMapping("/verifyMerchantEmail")
	public Map<String, Object> verifyMerchantEmail(@RequestParam(value = "email") String email) throws Exception {
		return merchantService.verifyMerchantEmail(email);
	}

	@ApiOperation(value = "Forget Merchant Passowrd")
	@PostMapping("/forgetMerchantPassowrd")
	public Map<String, Object> forgetMerchantPassowrd(@Valid @RequestBody ForgetPasswordRequest forgetPasswordRequest)
			throws Exception {
		return merchantService.forgetMerchantPassowrd(forgetPasswordRequest);
	}

	@ApiOperation(value = "Login Encryption")
	@PostMapping("/login-merchant")
	public ResponseEntity<?> loginDashBoardEnc(@Valid @RequestBody EncryptedRequest encryptedRequest) throws Exception {
		return ResponseEntity.ok().body(merchantUserService.loginDashboardEnc(encryptedRequest));
	}

	@ApiOperation(value = "Login Encryption Sandbox")
	@PostMapping("/login-merchant-sandbox")
	public ResponseEntity<?> loginDashBoardSandBox(@Valid @RequestBody EncryptedRequest encryptedRequest)
			throws Exception {
		return ResponseEntity.ok().body(merchantUserService.loginDashBoardSandBox(encryptedRequest));
	}

	@ApiOperation(value = "Login OTP based")
	@PostMapping("/login-merchant-otp")
	public ResponseEntity<?> loginDashBoardEncOtp(@Valid @RequestBody EncryptedRequest encryptedRequest)
			throws Exception {
		return ResponseEntity.ok().body(merchantUserService.loginDashBoardEncOtp(encryptedRequest));
	}

	@ApiOperation(value = "Login OTP verify")
	@PostMapping("/verify-login-otp/{token}/{otp}")
	public ResponseEntity<?> verifyLoginOtp(@PathVariable String token, @PathVariable String otp) throws Exception {
		return ResponseEntity.ok().body(merchantUserService.verifyLoginOtp(token, otp));
	}
	
	//PWA BBPS get Credentials for BBPS and PG
	//mid = merchant id merchant table
	@ApiOperation(value = "Login For PWA")
	@PostMapping("/login-pwa/{mid}/{mobile}")
	public ResponseEntity<?> getCredentials(@PathVariable String mid, @PathVariable String mobile) throws Exception {
		return ResponseEntity.ok().body(merchantUserService.loginPWA(mid, mobile));
	}
	
	//PWA BBPS Mobile No verification
	@ApiOperation(value = "Login For PWA")
	@PostMapping("/{name}/mobileNoVerification/{mobile}")
	public Map<String,Object> mobileNoVerification(@PathVariable String mobile,@PathVariable String name) throws Exception {
	return merchantUserService.mobileNoVerification(mobile,name);
	}
	
	@ApiOperation(value = "OTP Verification")
	@PostMapping("/{otp}/otpVerification/{token}")
	public Map<String,Object> otpVerification(@PathVariable String otp,@PathVariable String token) throws Exception {
	return merchantUserService.otpVerification(otp,token);
	}
	
	@ApiOperation(value = "Login For BBPS PWA")
	@PostMapping("/mobileNoLogin/{mobile}")
	public Map<String,Object> mobileNoLogin(@PathVariable String mobile) throws Exception {
	return merchantUserService.mobileNoLogin(mobile);
	}
}
