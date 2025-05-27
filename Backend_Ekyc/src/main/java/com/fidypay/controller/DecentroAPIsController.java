package com.fidypay.controller;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fidypay.ServiceProvider.Decentro.DecentroServiceImplNew;
import com.fidypay.request.AccountVerificationRequest;
import com.fidypay.utils.constants.EkycCommonLogicConfig;

import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/allVerification")
public class DecentroAPIsController {

	@Autowired
	private EkycCommonLogicConfig ekycCommonLogicConfig;

	@Autowired
	private DecentroServiceImplNew decentroServiceImplNew;

	@ApiOperation(value = "GST Basic Details")
	@PostMapping("/gstBasic")
	public Map<String, Object> gstBasic(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@RequestParam String gstNumber) throws Exception {

		Map<String, Object> map = ekycCommonLogicConfig.authenticateUser(Authorization, clientId, clientSecret);

		if (map.containsKey("merchantId")) {

			return decentroServiceImplNew.saveDataForBasicGst(map, gstNumber);

		}

		return map;

	}

	@ApiOperation(value = "GST Detailed")
	@PostMapping("/gstDetailed")
	public Map<String, Object> gstDetailed(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@RequestParam("gstNumber") String gstNumber) throws Exception {

		Map<String, Object> map = ekycCommonLogicConfig.authenticateUser(Authorization, clientId, clientSecret);

		if (map.containsKey("merchantId")) {

			return decentroServiceImplNew.saveDataForDetailedGst(map, gstNumber);

		}

		return map;

	}

	@ApiOperation(value = "PAN Basic Details")
	@PostMapping("/panBasic")
	public Map<String, Object> panBasic(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@RequestParam("panNumber") String panNumber) throws Exception {

		Map<String, Object> map = ekycCommonLogicConfig.authenticateUser(Authorization, clientId, clientSecret);

		if (map.containsKey("merchantId")) {

			return decentroServiceImplNew.saveDataForBasicPan(map, panNumber);

		}

		return map;

	}

	@ApiOperation(value = "PAN Detailed")
	@PostMapping("/panDetailed")
	public Map<String, Object> panDetailed(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@RequestParam("panNumber") String panNumber) throws Exception {

		Map<String, Object> map = ekycCommonLogicConfig.authenticateUser(Authorization, clientId, clientSecret);

		if (map.containsKey("merchantId")) {

			return decentroServiceImplNew.saveDataForDetailedPan(map, panNumber);

		}

		return map;

	}

	@ApiOperation(value = "Email Verification")
	@PostMapping("/emailVerfication")
	public Map<String, Object> emailVerfication(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@RequestParam("emailId") String emailId) throws Exception {

		Map<String, Object> map = ekycCommonLogicConfig.authenticateUser(Authorization, clientId, clientSecret);

		if (map.containsKey("merchantId")) {

			return decentroServiceImplNew.saveDataForEmailVerfication(map, emailId);

		}

		return map;

	}

	@ApiOperation(value = "Pan To GST Details")
	@PostMapping("/panToGst")
	public Map<String, Object> panToGst(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@RequestParam("panNo") String panNo) throws Exception {

		Map<String, Object> map = ekycCommonLogicConfig.authenticateUser(Authorization, clientId, clientSecret);

		if (map.containsKey("merchantId")) {

			return decentroServiceImplNew.saveDataForPanToGst(map, panNo);

		}

		return map;

	}

	@ApiOperation(value = "Pan Link Status With Aadhaar")
	@PostMapping("/panLinkStatusWithAadhaar")
	public Map<String, Object> panLinkStatusWithAadhaar(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@RequestParam("panNo") String panNo) throws Exception {

		Map<String, Object> map = ekycCommonLogicConfig.authenticateUser(Authorization, clientId, clientSecret);

		if (map.containsKey("merchantId")) {

			return decentroServiceImplNew.saveDataForPanLinkStatusWithAadhaar(map, panNo);

		}

		return map;

	}

	@ApiOperation(value = "Bank Account Verification")
	@PostMapping("/bankAccountVerification")
	public Map<String, Object> bankAccountVerification(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@Valid @RequestBody AccountVerificationRequest accountVerificationRequest) throws Exception {

		Map<String, Object> map = ekycCommonLogicConfig.authenticateUser(Authorization, clientId, clientSecret);

		if (map.containsKey("merchantId")) {

			return decentroServiceImplNew.saveDataForBankAccountVerification(map, accountVerificationRequest);

		}

		return map;

	}

	@ApiOperation(value = "Image Liveness Verification")
	@PostMapping("/imageLivenessVerification")
	public Map<String, Object> imageLivenessVerification(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@RequestParam(name = "image", required = true) MultipartFile image) throws Exception {

		Map<String, Object> map = ekycCommonLogicConfig.authenticateUser(Authorization, clientId, clientSecret);

		if (map.containsKey("merchantId")) {

			return decentroServiceImplNew.saveDataForImageLivenessVerification(map, image);

		}

		return map;

	}

	@ApiOperation(value = "RC Verification")
	@PostMapping("/validateRC")
	public Map<String, Object> validateRC(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@RequestParam String vehicleNumber) throws Exception {

		Map<String, Object> map = ekycCommonLogicConfig.authenticateUser(Authorization, clientId, clientSecret);

		if (map.containsKey("merchantId")) {

			return decentroServiceImplNew.saveDataForValidateRC(map, vehicleNumber);

		}

		return map;

	}

	@ApiOperation(value = "E-Aadhar")
	@PostMapping("/eAadhar")
	public Map<String, Object> eAadhar(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@RequestParam String initial_transaction_id) throws Exception {

		Map<String, Object> map = ekycCommonLogicConfig.authenticateUser(Authorization, clientId, clientSecret);

		if (map.containsKey("merchantId")) {

			return decentroServiceImplNew.eAadhar(map, initial_transaction_id);

		}

		return map;

	}

	@ApiOperation(value = "Get Initiate Session For EAdhaar")
	@PostMapping("/getInitiateSession")
	public Map<String, Object> getInitiateSession(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret)
			throws Exception {

		Map<String, Object> map = ekycCommonLogicConfig.authenticateUser(Authorization, clientId, clientSecret);

		if (map.containsKey("merchantId")) {

			return decentroServiceImplNew.getInitiateSession(map);

		}

		return map;

	}

}
