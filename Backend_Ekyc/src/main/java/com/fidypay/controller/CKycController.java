package com.fidypay.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

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

import com.fidypay.ServiceProvider.Signzy.EKYCService;
import com.fidypay.encryption.Encryption;
import com.fidypay.entity.MerchantInfo;
import com.fidypay.entity.Merchants;
import com.fidypay.repo.MerchantInfoRepository;
import com.fidypay.repo.MerchantsRepository;
import com.fidypay.request.CKycRequest;
import com.fidypay.service.CKycService;
import com.fidypay.utils.constants.AuthenticationVerify;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.DateAndTime;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/cKyc")
public class CKycController {

	private static final Logger LOGGER = LoggerFactory.getLogger(CKycController.class);

	@Autowired
	private MerchantsRepository merchantsRepository;

	@Autowired
	private CKycService cKycService;

	@Autowired
	private EKYCService ekycService;

	@Autowired
	private MerchantInfoRepository merchantinforepository;

	@PostMapping("/cKycSearch/{idNumber}")
	public Map<String, Object> cKycSearch(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@PathVariable String idNumber) {
		Map<String, Object> map = new HashMap<>();
		try {

			String password = AuthenticationVerify.authenticationPassword(Authorization);
			String firstName = AuthenticationVerify.authenticationUsername(Authorization);
			LOGGER.info(" clientSecret: {}", clientSecret);

			MerchantInfo merchantInfo = merchantinforepository.findByClientIdAndClientSecretAndUserNameAndPassword(
					Encryption.encString(clientId), Encryption.encString(clientSecret), Encryption.encString(firstName),
					Encryption.encString(password));

			LOGGER.info(" merchantInfo: {}", merchantInfo.getMerchantInfoId());

			if (merchantInfo.getIsMerchantActive() == '1'
					&& Encryption.encString(password).equals(merchantInfo.getPassword())
					&& Encryption.encString(clientSecret).equals(merchantInfo.getClientSecret())
					&& Encryption.encString(firstName).equals(merchantInfo.getUsername())) {

				Optional<Merchants> findById = merchantsRepository.findById(merchantInfo.getMerchantId());
				if (findById.isPresent()) {
					Merchants merchants = findById.get();
					String serviceName = "CKycSearch";
					LOGGER.info("Inside1 :");

					if (!ekycService.checkServiceExistOrNot(Long.parseLong(Encryption.decString(clientId)),
							serviceName)) {
						LOGGER.info("Inside3 :");
						map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
						map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
						map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
						return map;

					}
					LOGGER.info("Inside2 :");

					if (idNumber.length() == 10) {

						return cKycService.saveDataCKYCSearch(idNumber.toUpperCase(), merchantInfo.getMerchantId(),
								merchants.getMerchantFloatAmount(), merchants.getMerchantBusinessName(),
								merchants.getMerchantEmail());
					} else {
						map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
						map.put(ResponseMessage.DESCRIPTION, ResponseMessage.VALID_PAN_NUMBER);
						map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
						return map;
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

	@PostMapping("/cKycSearchDownload")
	public Map<String, Object> cKycSearchDownload(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@Valid @RequestBody CKycRequest cKycRequest) {

		Map<String, Object> map = new HashMap<>();

		try {
			String password = AuthenticationVerify.authenticationPassword(Authorization);
			String firstName = AuthenticationVerify.authenticationUsername(Authorization);
			LOGGER.info(" clientSecret: {}", clientSecret);

			MerchantInfo merchantInfo = merchantinforepository.findByClientIdAndClientSecretAndUserNameAndPassword(
					Encryption.encString(clientId), Encryption.encString(clientSecret), Encryption.encString(firstName),
					Encryption.encString(password));

			LOGGER.info(" merchantInfo: {}", merchantInfo.getMerchantInfoId());

			if (merchantInfo.getIsMerchantActive() == '1'
					&& Encryption.encString(password).equals(merchantInfo.getPassword())
					&& Encryption.encString(clientSecret).equals(merchantInfo.getClientSecret())
					&& Encryption.encString(firstName).equals(merchantInfo.getUsername())) {

				Optional<Merchants> findById = merchantsRepository.findById(merchantInfo.getMerchantId());
				if (findById.isPresent()) {
					Merchants merchants = findById.get();

					String serviceName = "CKycDetails";

					if (!ekycService.checkServiceExistOrNot(Long.parseLong(Encryption.decString(clientId)),
							serviceName)) {
						map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
						map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
						map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
						return map;

					}

					if (!DateAndTime.isValidDateFormat(cKycRequest.getAuthFactor())) {
						map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
						map.put(ResponseMessage.DESCRIPTION, ResponseMessage.INVALID_DATE_FORMATE);
						map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
						return map;
					} else {
						return cKycService.saveDataCKYCDetails(cKycRequest, merchantInfo.getMerchantId(),
								merchants.getMerchantFloatAmount(), merchants.getMerchantBusinessName(),
								merchants.getMerchantEmail());
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

	@PostMapping("/cKycSearchV2")
	public Map<String, Object> cKycSearchV2(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@RequestParam("panNumber") String panNumber) {
		Map<String, Object> map = new HashMap<>();
		try {

			String password = AuthenticationVerify.authenticationPassword(Authorization);
			String firstName = AuthenticationVerify.authenticationUsername(Authorization);
			LOGGER.info(" clientSecret: {}", clientSecret);

			MerchantInfo merchantInfo = merchantinforepository.findByClientIdAndClientSecretAndUserNameAndPassword(
					Encryption.encString(clientId), Encryption.encString(clientSecret), Encryption.encString(firstName),
					Encryption.encString(password));

			LOGGER.info(" merchantInfo: {}", merchantInfo.getMerchantInfoId());

			if (merchantInfo.getIsMerchantActive() == '1'
					&& Encryption.encString(password).equals(merchantInfo.getPassword())
					&& Encryption.encString(clientSecret).equals(merchantInfo.getClientSecret())
					&& Encryption.encString(firstName).equals(merchantInfo.getUsername())) {

				Optional<Merchants> findById = merchantsRepository.findById(merchantInfo.getMerchantId());
				if (findById.isPresent()) {
					Merchants merchants = findById.get();
					String serviceName = "CKycSearch";
					LOGGER.info("Inside1 :");

					if (!ekycService.checkServiceExistOrNot(Long.parseLong(Encryption.decString(clientId)),
							serviceName)) {
						LOGGER.info("Inside3 :");
						map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
						map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
						map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
						return map;

					}
					LOGGER.info("Inside2 :");

//					if (panNumber.length() == 10) {

					return cKycService.saveDataCKYCSearchV2(panNumber.toUpperCase(), merchantInfo.getMerchantId(),
							merchants.getMerchantFloatAmount(), merchants.getMerchantBusinessName(),
							merchants.getMerchantEmail());
//					} else {
//						map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
//						map.put(ResponseMessage.DESCRIPTION, ResponseMessage.VALID_PAN_NUMBER);
//						map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
//						return map;
//					}
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
}
