package com.fidypay.controller;

import javax.validation.Valid;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.fidypay.ServiceProvider.Signzy.EKYCService;
import com.fidypay.encryption.Encryption;
import com.fidypay.entity.MerchantInfo;
import com.fidypay.entity.Merchants;
import com.fidypay.repo.MerchantInfoRepository;
import com.fidypay.repo.MerchantsRepository;
import com.fidypay.request.CibilScoreOtpRequest;
import com.fidypay.request.CibilScoreValidateRequest;
import com.fidypay.utils.constants.AuthenticationVerify;
import com.fidypay.utils.constants.ResponseMessage;

import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CibilScoreController {

	private static final Logger LOGGER = LoggerFactory.getLogger(CibilScoreController.class);

	@Autowired
	private MerchantsRepository merchantsRepository;

	@Autowired
	private MerchantInfoRepository merchantinforepository;

	@Autowired
	private EKYCService ekycService;

	@SuppressWarnings("unchecked")
	@ApiOperation(value = "Generate OTP Cibil Score Details Docboyz")
	@PostMapping("/generateOtpCibilScore")
	public String generateOtpCibilScore(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@Valid @RequestBody CibilScoreOtpRequest cibilscorerequest) {

		String response = null;

		JSONObject jsonObject = new JSONObject();

		try {

			String password = AuthenticationVerify.authenticationPassword(Authorization);
			String firstName1 = AuthenticationVerify.authenticationUsername(Authorization);
			LOGGER.info(" clientSecret " + clientSecret);
			MerchantInfo merchantInfo = merchantinforepository.findByClientIdAndClientSecretAndUserNameAndPassword(
					Encryption.encString(clientId), Encryption.encString(clientSecret),
					Encryption.encString(firstName1), Encryption.encString(password));

			LOGGER.info(" merchantInfo: " + merchantInfo.getMerchantInfoId());

			if (merchantInfo.getIsMerchantActive() == '1'
					&& Encryption.encString(password).equals(merchantInfo.getPassword())
					&& Encryption.encString(clientSecret).equals(merchantInfo.getClientSecret())
					&& Encryption.encString(firstName1).equals(merchantInfo.getUsername())) {

				Merchants merchants = merchantsRepository.findById(merchantInfo.getMerchantId()).get();
				String serviceName = "DocBoyz Generate Otp";
				if (!ekycService.checkServiceExistOrNot(Long.parseLong(Encryption.decString(clientId)), serviceName)) {
					jsonObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					jsonObject.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
					jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
					return jsonObject.toString();

				}
				return ekycService.saveDataForOtpCibilScore(cibilscorerequest, merchantInfo.getMerchantId(),
						merchants.getMerchantFloatAmount(),
						merchants.getMerchantBusinessName(), merchants.getMerchantEmail());

			} else {
				jsonObject.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				jsonObject.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
				jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				jsonObject.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
				response = jsonObject.toString();

			}

		} catch (Exception e) {
			jsonObject.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			jsonObject.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			response = jsonObject.toString();
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	@ApiOperation(value = "Validate Cibil Score Details Docboyz")
	@PostMapping("/validateCibilScore")
	public String validateCibilScore(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@Valid @RequestBody CibilScoreValidateRequest cibilscorerequest) {

		String response = null;
		JSONObject jsonObject = new JSONObject();

		try {

			String password = AuthenticationVerify.authenticationPassword(Authorization);
			String firstName1 = AuthenticationVerify.authenticationUsername(Authorization);
			LOGGER.info(" clientSecret " + clientSecret);

			MerchantInfo merchantInfo = merchantinforepository.findByClientIdAndClientSecretAndUserNameAndPassword(
					Encryption.encString(clientId), Encryption.encString(clientSecret),
					Encryption.encString(firstName1), Encryption.encString(password));

			LOGGER.info(" merchantInfo: " + merchantInfo.getMerchantInfoId());

			if (merchantInfo.getIsMerchantActive() == '1'
					&& Encryption.encString(password).equals(merchantInfo.getPassword())
					&& Encryption.encString(clientSecret).equals(merchantInfo.getClientSecret())
					&& Encryption.encString(firstName1).equals(merchantInfo.getUsername())) {

				Merchants merchants = merchantsRepository.findById(merchantInfo.getMerchantId()).get();
				String serviceName = "DocBoyz Validate Otp";
				if (!ekycService.checkServiceExistOrNot(Long.parseLong(Encryption.decString(clientId)), serviceName)) {
					jsonObject.put(ResponseMessage.CODE, ResponseMessage.FAILED);
					jsonObject.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
					jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.SERVICEID_NOT_EXIST);
					return jsonObject.toString();

				}
				return ekycService.saveDataForValidateCibilScore(cibilscorerequest, merchantInfo.getMerchantId(),
						merchants.getMerchantFloatAmount(),
						merchants.getMerchantBusinessName(), merchants.getMerchantEmail());

			} else {
				jsonObject.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				jsonObject.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
				jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				jsonObject.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
				response = jsonObject.toString();

			}

		} catch (Exception e) {
			jsonObject.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			jsonObject.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
			jsonObject.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			jsonObject.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			response = jsonObject.toString();
		}
		return response;
	}
}
