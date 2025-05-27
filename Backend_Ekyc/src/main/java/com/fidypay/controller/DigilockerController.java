package com.fidypay.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.fidypay.ServiceProvider.Decentro.DecentroServiceImpl;
import com.fidypay.ServiceProvider.Signzy.EKYCService;
import com.fidypay.encryption.Encryption;
import com.fidypay.entity.MerchantInfo;
import com.fidypay.entity.Merchants;
import com.fidypay.repo.MerchantInfoRepository;
import com.fidypay.repo.MerchantsRepository;
import com.fidypay.request.DigilockerGetAccessToken;
import com.fidypay.request.DigilockerGetIssuedFiles;
import com.fidypay.request.DigilockerInitiateSession;
import com.fidypay.service.DigilockerService;
import com.fidypay.utils.constants.AuthenticationVerify;
import com.fidypay.utils.constants.ResponseMessage;

import io.swagger.annotations.ApiOperation;

@RestController
public class DigilockerController {

	private static final Logger LOGGER = LoggerFactory.getLogger(DigilockerController.class);

	@Autowired
	private MerchantsRepository merchantsRepository;

	@Autowired
	private EKYCService ekycService;

	@Autowired
	private DigilockerService digilockerservice;

	@Autowired
	private DecentroServiceImpl decentroServiceImpl;

	@Autowired
	private MerchantInfoRepository merchantinforepository;

	@ApiOperation("Get Initiate Session Decentro API")
	@PostMapping("/getInitiateSession")
	public Map<String, Object> initiateSession(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@Valid @RequestBody DigilockerInitiateSession digilockerinitiatesession) {

		Map<String, Object> map = new HashMap<>();
		try {

			String password = AuthenticationVerify.authenticationPassword(Authorization);
			String firstName = AuthenticationVerify.authenticationUsername(Authorization);
			LOGGER.info(" clientSecret:  {}", clientSecret);

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
					String serviceName = "EAdhar";
					if (!ekycService.checkServiceExistOrNot(Long.parseLong(Encryption.decString(clientId)),
							serviceName)) {
						map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
						map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
						map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
						return map;

					}

					return decentroServiceImpl.initiateSession(digilockerinitiatesession);
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

	@ApiOperation("Get Access Token Decentro API")
	@PostMapping("/getAccesstoken")
	public Map<String, Object> getAccessToken(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@Valid @RequestBody DigilockerGetAccessToken digilockergetaccesstoken) {

		Map<String, Object> map = new HashMap<>();

		try {

			String password = AuthenticationVerify.authenticationPassword(Authorization);
			String firstName = AuthenticationVerify.authenticationUsername(Authorization);
			LOGGER.info(" clientSecret: {} ", clientSecret);

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

					String serviceName = "EAdhar";
					if (!ekycService.checkServiceExistOrNot(Long.parseLong(Encryption.decString(clientId)),
							serviceName)) {
						map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
						map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
						map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
						return map;

					}
					return digilockerservice.saveDataForDigilockerGetAccessToken(digilockergetaccesstoken,
							merchantInfo.getMerchantId(), merchants.getMerchantFloatAmount(),
							merchants.getMerchantBusinessName(), merchants.getMerchantEmail());
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

	@ApiOperation("Get Issued Files Decentro API")
	@PostMapping("/getIssuedFiles")
	public Map<String, Object> getIssuedFiles(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@Valid @RequestBody DigilockerGetIssuedFiles digilockergetissuedfiles) {

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
				Optional<Merchants> findById = merchantsRepository.findById(merchantInfo.getMerchantId());
				if (findById.isPresent()) {
					Merchants merchants = findById.get();

					String serviceName = "EAdhar";
					if (!ekycService.checkServiceExistOrNot(Long.parseLong(Encryption.decString(clientId)),
							serviceName)) {
						map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
						map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
						map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
						return map;

					}
					return digilockerservice.saveDataForDigilockerGetIssuedFiles(digilockergetissuedfiles,
							merchantInfo.getMerchantId(), merchants.getMerchantFloatAmount(),
							merchants.getMerchantBusinessName(), merchants.getMerchantEmail());
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
