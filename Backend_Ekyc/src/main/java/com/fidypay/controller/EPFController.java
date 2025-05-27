package com.fidypay.controller;

import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.collections4.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fidypay.encryption.Encryption;
import com.fidypay.entity.MerchantInfo;
import com.fidypay.entity.Merchants;
import com.fidypay.repo.MerchantInfoRepository;
import com.fidypay.repo.MerchantsRepository;
import com.fidypay.service.EPFService;
import com.fidypay.utils.constants.AuthenticationVerify;
import com.fidypay.utils.constants.ResponseMessage;

import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class EPFController {

	private static final Logger LOGGER = LoggerFactory.getLogger(EPFController.class);
	
	@Autowired
	private MerchantsRepository merchantsRepository;
	
	@Autowired
	private MerchantInfoRepository merchantinforepository;
	
	@Autowired
	private EPFService epfService;
	
	
	@ApiOperation(value = "EPF UAN Verification")
	@PostMapping("/epfUANVerification")
	public Map<String,Object> epfUANVerification(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret,
			@Valid @RequestParam String uanNumber,
			@RequestHeader("Authorization") String Authorization) throws Exception {
		Map<String,Object> map=new HashedMap<String,Object>();
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

				return  epfService.epfUANValidation(uanNumber,
						merchants.getMerchantId(), merchants.getMerchantFloatAmount(),merchants.getMerchantBusinessName(), merchants.getMerchantEmail());

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
