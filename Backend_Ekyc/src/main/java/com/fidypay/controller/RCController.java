package com.fidypay.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.fidypay.ServiceProvider.Signzy.EKYCService;
import com.fidypay.encryption.Encryption;
import com.fidypay.entity.MerchantInfo;
import com.fidypay.entity.Merchants;
import com.fidypay.repo.MerchantInfoRepository;
import com.fidypay.repo.MerchantsRepository;
import com.fidypay.service.RCService;
import com.fidypay.utils.constants.AuthenticationVerify;
import com.fidypay.utils.constants.EkycCommonLogicConfig;
import com.fidypay.utils.constants.ResponseMessage;

import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RCController {

	private static final Logger LOGGER = LoggerFactory.getLogger(RCController.class);

	@Autowired
	private MerchantsRepository merchantsRepository;

	@Autowired
	private MerchantInfoRepository merchantinforepository;

	@Autowired
	private EKYCService ekycService;

	@Autowired
	private RCService rcService;

	@Autowired
	private EkycCommonLogicConfig ekycCommonLogicConfig;

	@ApiOperation(value = "Validate RC Details Signzy")
	@PostMapping("/validateRCDetails/{vehicleNumber}")
	public Map<String, Object> rcDetails(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@PathVariable("vehicleNumber") String vehicleNumber) {

		Map<String, Object> map = new HashMap<>();

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

				Optional<Merchants> findById = merchantsRepository.findById(merchantInfo.getMerchantId());
				if (findById.isPresent()) {

					Merchants merchants = findById.get();

					String serviceName = ResponseMessage.VEHICLE_REG_SERVICE;
					if (!ekycService.checkServiceExistOrNot(Long.parseLong(Encryption.decString(clientId)),
							serviceName)) {
						map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
						map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
						map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
						return map;

					}
//					return rcService.saveDataForRCValidate(vehicleNumber, merchantInfo.getMerchantId(),
//							merchants.getMerchantFloatAmount(), merchants.getMerchantBusinessName(),
//							merchants.getMerchantEmail());
					map = ekycCommonLogicConfig.buildResponse(ResponseMessage.FAILED, ResponseMessage.API_STATUS_FAILED,
							ResponseMessage.SERVICE_CLOSED);

				}
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);

			}

		} catch (Exception e) {
			LOGGER.error("Exception: {}", e);
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}
		return map;
	}
}
