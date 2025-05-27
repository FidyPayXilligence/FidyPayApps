package com.fidypay.controller;

import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fidypay.ServiceProvider.Signzy.EKYCService;
import com.fidypay.encryption.Encryption;
import com.fidypay.entity.MerchantInfo;
import com.fidypay.entity.Merchants;
import com.fidypay.repo.MerchantInfoRepository;
import com.fidypay.repo.MerchantsRepository;
import com.fidypay.service.GSTService;
import com.fidypay.service.PanCardDetailsService;
import com.fidypay.utils.constants.AuthenticationVerify;
import com.fidypay.utils.constants.EkycCommonLogicConfig;
import com.fidypay.utils.constants.ResponseMessage;

import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class GSTController {

	private static final Logger LOGGER = LoggerFactory.getLogger(GSTController.class);

	@Autowired
	private MerchantsRepository merchantsRepository;

	@Autowired
	private EKYCService ekycService;

	@Autowired
	private MerchantInfoRepository merchantinforepository;

	@Autowired
	private GSTService gstService;

	@Autowired
	private PanCardDetailsService panCardDetailsService;

	@Autowired
	private EkycCommonLogicConfig ekycCommonLogicConfig;
	
	
	@ApiOperation(value = "Get All SoundboxChargesById1")
	@PostMapping("/getAllTestSoundboxChargesById")
	public Map<String, Object> getAllSoundboxChargesById(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, @RequestParam long soundBoxSubscriptionId) {
		
		return null;
	}

	@PostMapping("/gstSearch/{GSTIN}")
	public Map<String, Object> GSTINSearch(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@PathVariable String GSTIN) throws Exception {
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

//					return gstService.saveDataForGSTINSearch(GSTIN, merchantInfo.getMerchantId(),
//							merchants.getMerchantFloatAmount());

				return panCardDetailsService.fetchGstinDetails(GSTIN, merchantInfo.getMerchantId(),
						merchants.getMerchantFloatAmount(), merchants.getMerchantBusinessName(),
						merchants.getMerchantEmail(), ResponseMessage.BEFISC);

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

	@PostMapping("/gstDetailsSearch/{GSTIN}")
	public Map<String, Object> GSTINDetailsSearch(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@PathVariable String GSTIN) throws Exception {
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

//					response = ekycService.saveDataForGSTDetailSearch(GSTIN,
//							merchantInfo.getMerchantId(), merchants.getMerchantFloatAmount());

//				return gstService.saveDataForGSTDetailSearch(GSTIN, merchantInfo.getMerchantId(),
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

	@PostMapping("/gstSearchCompanyName/{companyName}")
	public Map<String, Object> GSTINSearchCompanyName(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@PathVariable String companyName) throws Exception {
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
				String serviceName = "GST Company Name";

				if (!ekycService.checkServiceExistOrNot(Long.parseLong(Encryption.decString(clientId)), serviceName)) {
					map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
					map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
					return map;

				} else {

					return gstService.saveDataForGSTSearchCompanyName(companyName, merchantInfo.getMerchantId(),
							merchants.getMerchantFloatAmount(), merchants.getMerchantBusinessName(),
							merchants.getMerchantEmail());
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

	@PostMapping("/gstSearchV2/{GSTIN}")
	public Map<String, Object> GSTINSearchV2(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@PathVariable String GSTIN) throws Exception {
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

				return gstService.saveDataForGSTINSearchKarza(GSTIN, merchantInfo.getMerchantId(),
						merchants.getMerchantFloatAmount(), merchants.getMerchantBusinessName(),
						merchants.getMerchantEmail());

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

	@PostMapping("/gstAuthentication/{GSTIN}")
	public Map<String, Object> GSTINAuthentication(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@PathVariable String GSTIN) throws Exception {
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

				return gstService.gstinAuthentication(GSTIN, merchantInfo.getMerchantId(),
						merchants.getMerchantFloatAmount(), merchants.getMerchantBusinessName(),
						merchants.getMerchantEmail());

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

	@PostMapping("/GSTINSearchByPan")
	public Map<String, Object> GSTINSearchByPan(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@RequestParam String pan) throws Exception {
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

//				return gstService.saveDataForGSTINSearchByPan(pan, merchantInfo.getMerchantId(),
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
}
