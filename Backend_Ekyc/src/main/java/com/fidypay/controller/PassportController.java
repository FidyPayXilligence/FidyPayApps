package com.fidypay.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.collections4.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fidypay.ServiceProvider.Signzy.EKYCService;
import com.fidypay.encryption.Encryption;
import com.fidypay.entity.MerchantInfo;
import com.fidypay.entity.Merchants;
import com.fidypay.repo.MerchantInfoRepository;
import com.fidypay.repo.MerchantsRepository;
import com.fidypay.request.FetchPassportRequest;
import com.fidypay.request.VerifyPassportRequest;
import com.fidypay.request.VerifyPassportRequestV2;
import com.fidypay.service.PassportService;
import com.fidypay.utils.constants.AuthenticationVerify;
import com.fidypay.utils.constants.EkycCommonLogicConfig;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.DateAndTime;

import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/passport")
public class PassportController {

	private static final Logger LOGGER = LoggerFactory.getLogger(PassportController.class);

	@Autowired
	private MerchantsRepository merchantsRepository;

	@Autowired
	private EKYCService ekycService;

	@Autowired
	private MerchantInfoRepository merchantinforepository;

	@Autowired
	private PassportService passportService;

	@Autowired
	private EkycCommonLogicConfig ekycCommonLogicConfig;

	@ApiOperation(value = "Fetch Passport")
	@PostMapping("/fetchPassport")
	public Map<String, Object> fetchPassport(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@Valid @RequestBody FetchPassportRequest fetchPassportRequest) throws Exception {
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

//				return passportService.saveDataForPassportFetch(fetchPassportRequest, merchantInfo.getMerchantId(),
//						merchants.getMerchantFloatAmount(), merchants.getMerchantBusinessName(),
//						merchants.getMerchantEmail());

				map = ekycCommonLogicConfig.buildResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED,
						ResponseMessage.SERVICE_CLOSED);

			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			}

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}

		return map;
	}

	@ApiOperation(value = "Fetch PassportV2 Karza API")
	@PostMapping("/fetchPassportV2")
	public Map<String, Object> fetchPassportV2(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@Valid @RequestBody FetchPassportRequest fetchPassportRequest) throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {
			String serviceName = "Passport Details";

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
				if (!ekycService.checkServiceExistOrNot(Long.parseLong(Encryption.decString(clientId)), serviceName)) {
					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
					return map;

				} else {

					if (DateAndTime.isValidDateFormat(fetchPassportRequest.getDob()) == false) {
						map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
						map.put(ResponseMessage.DESCRIPTION, ResponseMessage.INVALID_DATE_FORMATE);
						map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
						return map;
					} else {
						return passportService.saveDataForPassportFetchV2(fetchPassportRequest,
								merchantInfo.getMerchantId(), merchants.getMerchantFloatAmount(), serviceName,
								merchants.getMerchantBusinessName(), merchants.getMerchantEmail());
					}
				}
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			}

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}

		return map;
	}

	@ApiOperation(value = "Verify Passport")
	@PostMapping("/verifyPassport")
	public Map<String, Object> verifyPassport(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@Valid @RequestBody VerifyPassportRequest verifyPassportRequest) throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {
			String serviceName = "Passport Verify";

			String password = AuthenticationVerify.authenticationPassword(Authorization);
			String firstName = AuthenticationVerify.authenticationUsername(Authorization);
			LOGGER.info(" clientSecret {}", clientSecret);

			MerchantInfo merchantInfo = merchantinforepository.findByClientIdAndClientSecretAndUserNameAndPassword(
					Encryption.encString(clientId), Encryption.encString(clientSecret), Encryption.encString(firstName),
					Encryption.encString(password));

			LOGGER.info(" merchantInfo: " + merchantInfo.getMerchantInfoId());

			if (merchantInfo.getIsMerchantActive() == '1'
					&& Encryption.encString(password).equals(merchantInfo.getPassword())
					&& Encryption.encString(clientSecret).equals(merchantInfo.getClientSecret())
					&& Encryption.encString(firstName).equals(merchantInfo.getUsername())) {

				Merchants merchants = merchantsRepository.findById(merchantInfo.getMerchantId()).get();
				// response = passportService.verifyPassport(verifyPassportRequest);
				if (!ekycService.checkServiceExistOrNot(Long.parseLong(Encryption.decString(clientId)), serviceName)) {
					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
					return map;

				} else {
					if (DateAndTime.isValidDateFormat(verifyPassportRequest.getDob()) == false) {
						map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
						map.put(ResponseMessage.DESCRIPTION, ResponseMessage.INVALID_DATE_FORMATE);
						map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
						return map;
					} else {
//						return passportService.saveDataForVerifyPassport(verifyPassportRequest,
//								merchantInfo.getMerchantId(), merchants.getMerchantFloatAmount(), serviceName,
//								merchants.getMerchantBusinessName(), merchants.getMerchantEmail());

						map = ekycCommonLogicConfig.buildResponse(ResponseMessage.FAILED,
								ResponseMessage.API_STATUS_FAILED, ResponseMessage.SERVICE_CLOSED);

					}
				}
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

	@ApiOperation(value = "Verify PassportV2 Karza API")
	@PostMapping("/verifyPassportV2")
	public Map<String, Object> verifyPassportV2(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@RequestBody VerifyPassportRequestV2 verifyPassportRequestV2) throws Exception {
		Map<String, Object> map = new HashedMap<>();
		try {
			String serviceName = "Passport Verify";

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

				if (!ekycService.checkServiceExistOrNot(Long.parseLong(Encryption.decString(clientId)), serviceName)) {
					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
					return map;

				} else {
					if (DateAndTime.isValidDateFormat(verifyPassportRequestV2.getDob()) == false) {
						map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
						map.put(ResponseMessage.DESCRIPTION, ResponseMessage.INVALID_DATE_FORMATE);
						map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
						return map;
					} else {
						return passportService.saveDataForVerifyPassportV2(verifyPassportRequestV2,
								merchantInfo.getMerchantId(), merchants.getMerchantFloatAmount(), serviceName,
								merchants.getMerchantBusinessName(), merchants.getMerchantEmail());
					}
				}
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
