package com.fidypay.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.collections4.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fidypay.encryption.Encryption;
import com.fidypay.entity.MerchantInfo;
import com.fidypay.entity.Merchants;
import com.fidypay.repo.MerchantInfoRepository;
import com.fidypay.repo.MerchantsRepository;
import com.fidypay.request.PanAadharRequest;
import com.fidypay.service.PanCardDetailsService;
import com.fidypay.service.PanService;
import com.fidypay.utils.constants.AuthenticationVerify;
import com.fidypay.utils.constants.EkycCommonLogicConfig;
import com.fidypay.utils.constants.ResponseMessage;

import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/pan")
public class PanController {

	private static final Logger LOGGER = LoggerFactory.getLogger(PanController.class);

	@Autowired
	private MerchantsRepository merchantsRepository;

	@Autowired
	private MerchantInfoRepository merchantinforepository;

	@Autowired
	private PanService panService;

	@Autowired
	private PanCardDetailsService panCardDetailsService;
	
	@Autowired
	private EkycCommonLogicConfig ekycCommonLogicConfig;

	@ApiOperation(value = "Fetch Pan(PAN Card Basic Verify)")
	@PostMapping("/fetchPan/{panNumber}")
	public Map<String, Object> fetchPan(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@Valid @PathVariable String panNumber) throws Exception {
		Map<String, Object> map = new HashedMap<>();
		try {

			String password = AuthenticationVerify.authenticationPassword(Authorization);
			String firstName = AuthenticationVerify.authenticationUsername(Authorization);
			LOGGER.info(" clientSecret {}", clientSecret);

			MerchantInfo merchantInfo = merchantinforepository.findByClientIdAndClientSecretAndUserNameAndPassword(
					Encryption.encString(clientId), Encryption.encString(clientSecret), Encryption.encString(firstName),
					Encryption.encString(password));

			LOGGER.info(" merchantInfo:  {}", merchantInfo.getMerchantInfoId());

			if (merchantInfo.getIsMerchantActive() == '1'
					&& Encryption.encString(password).equals(merchantInfo.getPassword())
					&& Encryption.encString(clientSecret).equals(merchantInfo.getClientSecret())
					&& Encryption.encString(firstName).equals(merchantInfo.getUsername())) {

				Merchants merchants = merchantsRepository.findById(merchantInfo.getMerchantId()).get();

				return panCardDetailsService.fetchPanDetails(panNumber, merchantInfo.getMerchantId(),
						merchants.getMerchantFloatAmount(), merchants.getMerchantBusinessName(),
						merchants.getMerchantEmail());

//				return panService.saveDataForFetchPan(panNumber, merchantInfo.getMerchantId(),
//						merchants.getMerchantFloatAmount());

			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			}

		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}

		return map;
	}

	@ApiOperation(value = "Fetch PanV2(PAN Card Details)")
	@PostMapping("/fetchPanV2/{panNumber}")
	public Map<String, Object> fetchPanV2(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@Valid @PathVariable String panNumber) throws Exception {

		Map<String, Object> map = new HashedMap<>();
		try {

			String password = AuthenticationVerify.authenticationPassword(Authorization);
			String firstName = AuthenticationVerify.authenticationUsername(Authorization);
			LOGGER.info(" clientSecret {}", clientSecret);

			MerchantInfo merchantInfo = merchantinforepository.findByClientIdAndClientSecretAndUserNameAndPassword(
					Encryption.encString(clientId), Encryption.encString(clientSecret), Encryption.encString(firstName),
					Encryption.encString(password));

			LOGGER.info(" merchantInfo: {}", merchantInfo.getMerchantInfoId());

			if (merchantInfo.getIsMerchantActive() == '1'
					&& Encryption.encString(password).equals(merchantInfo.getPassword())
					&& Encryption.encString(clientSecret).equals(merchantInfo.getClientSecret())
					&& Encryption.encString(firstName).equals(merchantInfo.getUsername())) {

				Merchants merchants = merchantsRepository.findById(merchantInfo.getMerchantId()).get();

//				return panCardDetailsService.fetchPanDetailsV2(panNumber, merchantInfo.getMerchantId(),
//						merchants.getMerchantFloatAmount());

//				return panService.saveDataForFetchPanDetails(panNumber, merchantInfo.getMerchantId(),
//						merchants.getMerchantFloatAmount(), merchants.getMerchantBusinessName(),
//						merchants.getMerchantEmail());
				map = ekycCommonLogicConfig.buildResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED,
						ResponseMessage.SERVICE_CLOSED);

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

	@ApiOperation(value = "Fetch PanV3(PAN Card Details)")
	@PostMapping("/fetchPanV3/{panNumber}")
	public Map<String, Object> fetchPanV3(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@Valid @PathVariable String panNumber) throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {

			String password = AuthenticationVerify.authenticationPassword(Authorization);
			String firstName = AuthenticationVerify.authenticationUsername(Authorization);
			LOGGER.info(" clientSecret {}", clientSecret);

			MerchantInfo merchantInfo = merchantinforepository.findByClientIdAndClientSecretAndUserNameAndPassword(
					Encryption.encString(clientId), Encryption.encString(clientSecret), Encryption.encString(firstName),
					Encryption.encString(password));

			LOGGER.info(" merchantInfo: {}", merchantInfo.getMerchantInfoId());

			if (merchantInfo.getIsMerchantActive() == '1'
					&& Encryption.encString(password).equals(merchantInfo.getPassword())
					&& Encryption.encString(clientSecret).equals(merchantInfo.getClientSecret())
					&& Encryption.encString(firstName).equals(merchantInfo.getUsername())) {

				Merchants merchants = merchantsRepository.findById(merchantInfo.getMerchantId()).get();

//				return panCardDetailsService.fetchPanDetailsV3(panNumber, merchantInfo.getMerchantId(),
//						merchants.getMerchantFloatAmount());

//				return panService.saveDataForFetchPanV3Details(panNumber, merchantInfo.getMerchantId(),
//						merchants.getMerchantFloatAmount(), merchants.getMerchantBusinessName(),
//						merchants.getMerchantEmail());

				map = ekycCommonLogicConfig.buildResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED,
						ResponseMessage.SERVICE_CLOSED);
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

	@ApiOperation(value = "Compliance(PAN Card Compliance Details)")
	@PostMapping("/compliance/{panNumber}")
	public Map<String, Object> compliance(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, @Valid @PathVariable String panNumber,
			@RequestHeader("Authorization") String Authorization) throws Exception {
		Map<String, Object> map = new HashedMap<>();
		try {

			String password = AuthenticationVerify.authenticationPassword(Authorization);
			String firstName = AuthenticationVerify.authenticationUsername(Authorization);
			LOGGER.info(" clientSecret {}", clientSecret);

			MerchantInfo merchantInfo = merchantinforepository.findByClientIdAndClientSecretAndUserNameAndPassword(
					Encryption.encString(clientId), Encryption.encString(clientSecret), Encryption.encString(firstName),
					Encryption.encString(password));

			LOGGER.info(" merchantInfo: {}", merchantInfo.getMerchantInfoId());

			if (merchantInfo.getIsMerchantActive() == '1'
					&& Encryption.encString(password).equals(merchantInfo.getPassword())
					&& Encryption.encString(clientSecret).equals(merchantInfo.getClientSecret())
					&& Encryption.encString(firstName).equals(merchantInfo.getUsername())) {

				Merchants merchants = merchantsRepository.findById(merchantInfo.getMerchantId()).get();

				return panService.saveDataForPanCompliance(panNumber.toUpperCase(), merchantInfo.getMerchantId(),
						merchants.getMerchantFloatAmount(), merchants.getMerchantBusinessName(),
						merchants.getMerchantEmail());

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

	@ApiOperation(value = "Fetch Compliance(PAN Card Basic Compliance)")
	@PostMapping("/fetchCompliance/{panNumber}")
	public Map<String, Object> fetchCompliance(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, @Valid @PathVariable String panNumber,
			@RequestHeader("Authorization") String Authorization) throws Exception {
		Map<String, Object> map = new HashedMap<>();
		try {

			String password = AuthenticationVerify.authenticationPassword(Authorization);
			String firstName = AuthenticationVerify.authenticationUsername(Authorization);
			LOGGER.info(" clientSecret " + clientSecret);

			MerchantInfo merchantInfo = merchantinforepository.findByClientIdAndClientSecretAndUserNameAndPassword(
					Encryption.encString(clientId), Encryption.encString(clientSecret), Encryption.encString(firstName),
					Encryption.encString(password));

			LOGGER.info(" merchantInfo: " + merchantInfo.getMerchantInfoId());

			if (merchantInfo.getIsMerchantActive() == '1'
					&& Encryption.encString(password).equals(merchantInfo.getPassword())
					&& Encryption.encString(clientSecret).equals(merchantInfo.getClientSecret())
					&& Encryption.encString(firstName).equals(merchantInfo.getUsername())) {

				Merchants merchants = merchantsRepository.findById(merchantInfo.getMerchantId()).get();

//				response = ekycService.saveDataForBasicPanCompliance(panNumber, merchantInfo.getMerchantId(),
//						merchants.getMerchantFloatAmount());

				return panService.saveDataForBasicPanCompliance(panNumber, merchantInfo.getMerchantId(),
						merchants.getMerchantFloatAmount(), merchants.getMerchantBusinessName(),
						merchants.getMerchantEmail());

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

	@ApiOperation(value = "Pan Aadhar Link Status(PAN Aadhar Link)")
	@PostMapping("/panAadharLinkStatus")
	public Map<String, Object> panAadharLinkStatus(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, @RequestBody PanAadharRequest panAadharRequest,
			@RequestHeader("Authorization") String Authorization) throws Exception {
		Map<String, Object> map = new HashedMap<>();
		try {

			String password = AuthenticationVerify.authenticationPassword(Authorization);
			String firstName = AuthenticationVerify.authenticationUsername(Authorization);
			LOGGER.info(" clientSecret {}", clientSecret);

			MerchantInfo merchantInfo = merchantinforepository.findByClientIdAndClientSecretAndUserNameAndPassword(
					Encryption.encString(clientId), Encryption.encString(clientSecret), Encryption.encString(firstName),
					Encryption.encString(password));

			LOGGER.info(" merchantInfo: {}", merchantInfo.getMerchantInfoId());

			if (merchantInfo.getIsMerchantActive() == '1'
					&& Encryption.encString(password).equals(merchantInfo.getPassword())
					&& Encryption.encString(clientSecret).equals(merchantInfo.getClientSecret())
					&& Encryption.encString(firstName).equals(merchantInfo.getUsername())) {

				Merchants merchants = merchantsRepository.findById(merchantInfo.getMerchantId()).get();

//				response = ekycService.saveDataForPanAdhar(panAadharRequest, merchantInfo.getMerchantId(),
//						merchants.getMerchantFloatAmount());

//				return panService.saveDataForPanAdhar(panAadharRequest, merchantInfo.getMerchantId(),
//						merchants.getMerchantFloatAmount(), merchants.getMerchantBusinessName(),
//						merchants.getMerchantEmail());
				
				map = ekycCommonLogicConfig.buildResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED,
						ResponseMessage.SERVICE_CLOSED);

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

	@ApiOperation(value = "Check Pan Aadhaar Link Status Karza API(PAN Card Basic Verify)")
	@PostMapping("/checkPanAadhaarLinkStatus/{panNumber}")
	public Map<String, Object> checkPanAadharLinkStatus(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@Valid @PathVariable String panNumber) throws Exception {
		Map<String, Object> map = new HashedMap<>();
		try {

			String password = AuthenticationVerify.authenticationPassword(Authorization);
			String firstName = AuthenticationVerify.authenticationUsername(Authorization);
			LOGGER.info(" clientSecret {}", clientSecret);

			MerchantInfo merchantInfo = merchantinforepository.findByClientIdAndClientSecretAndUserNameAndPassword(
					Encryption.encString(clientId), Encryption.encString(clientSecret), Encryption.encString(firstName),
					Encryption.encString(password));

			LOGGER.info(" merchantInfo: {}", merchantInfo.getMerchantInfoId());

			if (merchantInfo.getIsMerchantActive() == '1'
					&& Encryption.encString(password).equals(merchantInfo.getPassword())
					&& Encryption.encString(clientSecret).equals(merchantInfo.getClientSecret())
					&& Encryption.encString(firstName).equals(merchantInfo.getUsername())) {

				Merchants merchants = merchantsRepository.findById(merchantInfo.getMerchantId()).get();

				return panService.checkPanAadharLinkStatus(panNumber, merchantInfo.getMerchantId(),
						merchants.getMerchantFloatAmount(), merchants.getMerchantBusinessName(),
						merchants.getMerchantEmail());

			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			}

		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}

		return map;
	}

	@ApiOperation(value = "Check Pan Status Karza API(PAN Card Basic Verify)")
	@PostMapping("/checkPanStatus/{panNumber}")
	public Map<String, Object> checkPanStatus(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@Valid @PathVariable String panNumber) throws Exception {
		Map<String, Object> map = new HashedMap<>();
		try {

			String password = AuthenticationVerify.authenticationPassword(Authorization);
			String firstName = AuthenticationVerify.authenticationUsername(Authorization);
			LOGGER.info(" clientSecret {}", clientSecret);

			MerchantInfo merchantInfo = merchantinforepository.findByClientIdAndClientSecretAndUserNameAndPassword(
					Encryption.encString(clientId), Encryption.encString(clientSecret), Encryption.encString(firstName),
					Encryption.encString(password));

			LOGGER.info(" merchantInfo: {}", merchantInfo.getMerchantInfoId());

			if (merchantInfo.getIsMerchantActive() == '1'
					&& Encryption.encString(password).equals(merchantInfo.getPassword())
					&& Encryption.encString(clientSecret).equals(merchantInfo.getClientSecret())
					&& Encryption.encString(firstName).equals(merchantInfo.getUsername())) {

				Merchants merchants = merchantsRepository.findById(merchantInfo.getMerchantId()).get();

				return panService.checkPanStatus(panNumber, merchantInfo.getMerchantId(),
						merchants.getMerchantFloatAmount(), merchants.getMerchantBusinessName(),
						merchants.getMerchantEmail());

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

	@ApiOperation(value = "Pan Profile Details Karza API(PAN Card Basic Verify)")
	@PostMapping("/panProfileDetails/{panNumber}")
	public Map<String, Object> panProfileDetails(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@Valid @PathVariable String panNumber) throws Exception {
		Map<String, Object> map = new HashedMap<>();
		try {

			String password = AuthenticationVerify.authenticationPassword(Authorization);
			String firstName = AuthenticationVerify.authenticationUsername(Authorization);
			LOGGER.info(" clientSecret {}", clientSecret);

			MerchantInfo merchantInfo = merchantinforepository.findByClientIdAndClientSecretAndUserNameAndPassword(
					Encryption.encString(clientId), Encryption.encString(clientSecret), Encryption.encString(firstName),
					Encryption.encString(password));

			LOGGER.info(" merchantInfo: {}", merchantInfo.getMerchantInfoId());

			if (merchantInfo.getIsMerchantActive() == '1'
					&& Encryption.encString(password).equals(merchantInfo.getPassword())
					&& Encryption.encString(clientSecret).equals(merchantInfo.getClientSecret())
					&& Encryption.encString(firstName).equals(merchantInfo.getUsername())) {

				Merchants merchants = merchantsRepository.findById(merchantInfo.getMerchantId()).get();

				return panService.panProfileDetails(panNumber, merchantInfo.getMerchantId(),
						merchants.getMerchantFloatAmount(), merchants.getMerchantBusinessName(),
						merchants.getMerchantEmail());

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

	@ApiOperation(value = "Pan Profile Details API(PAN Card Basic Verify)")
	@PostMapping("/panDetails")
	public Map<String, Object> panDetails(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@Valid @RequestParam String panNumber) throws Exception {
		Map<String, Object> map = new HashedMap<>();
		try {

			String password = AuthenticationVerify.authenticationPassword(Authorization);
			String firstName = AuthenticationVerify.authenticationUsername(Authorization);
			LOGGER.info(" clientSecret {}", clientSecret);

			MerchantInfo merchantInfo = merchantinforepository.findByClientIdAndClientSecretAndUserNameAndPassword(
					Encryption.encString(clientId), Encryption.encString(clientSecret), Encryption.encString(firstName),
					Encryption.encString(password));

			LOGGER.info(" merchantInfo: {}", merchantInfo.getMerchantInfoId());

			if (merchantInfo.getIsMerchantActive() == '1'
					&& Encryption.encString(password).equals(merchantInfo.getPassword())
					&& Encryption.encString(clientSecret).equals(merchantInfo.getClientSecret())
					&& Encryption.encString(firstName).equals(merchantInfo.getUsername())) {

				Merchants merchants = merchantsRepository.findById(merchantInfo.getMerchantId()).get();

				return panService.panDetails(panNumber, merchantInfo.getMerchantId(),
						merchants.getMerchantFloatAmount(), merchants.getMerchantBusinessName(),
						merchants.getMerchantEmail());

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

//	Decentro ValidateKYC
//	@ApiOperation(value = "Pan Details API")
//	@PostMapping("/validatePan/{panNumber}")
//	public Map<String, Object> validatePan(@RequestHeader("Authorization") String Authorization,
//			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
//			@Valid @PathVariable String panNumber) throws Exception {
//		Map<String, Object> map = new HashedMap<>();
//		try {
//
//			String password = AuthenticationVerify.authenticationPassword(Authorization);
//			String firstName = AuthenticationVerify.authenticationUsername(Authorization);
//			LOGGER.info(" clientSecret {}", clientSecret);
//
//			MerchantInfo merchantInfo = merchantinforepository.findByClientIdAndClientSecretAndUserNameAndPassword(
//					Encryption.encString(clientId), Encryption.encString(clientSecret), Encryption.encString(firstName),
//					Encryption.encString(password));
//
//			LOGGER.info(" merchantInfo: {}", merchantInfo.getMerchantInfoId());
//
//			if (merchantInfo.getIsMerchantActive() == '1'
//					&& Encryption.encString(password).equals(merchantInfo.getPassword())
//					&& Encryption.encString(clientSecret).equals(merchantInfo.getClientSecret())
//					&& Encryption.encString(firstName).equals(merchantInfo.getUsername())) {
//
//				Merchants merchants = merchantsRepository.findById(merchantInfo.getMerchantId()).get();
//
//				return panService.validatePan(panNumber, merchantInfo.getMerchantId(),
//						merchants.getMerchantFloatAmount());
//
//			} else {
//				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
//				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
//				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
//				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
//			}
//
//		} catch (Exception e) {
//			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
//			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
//			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
//			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
//		}
//
//		return map;
//	}
}
