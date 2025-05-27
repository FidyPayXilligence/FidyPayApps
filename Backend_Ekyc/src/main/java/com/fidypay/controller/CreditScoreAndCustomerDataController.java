package com.fidypay.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.fidypay.encryption.Encryption;
import com.fidypay.entity.MerchantInfo;
import com.fidypay.entity.Merchants;
import com.fidypay.repo.MerchantInfoRepository;
import com.fidypay.repo.MerchantsRepository;
import com.fidypay.request.CreditBureauReportRequest;
import com.fidypay.request.CustomerDataRequest;
import com.fidypay.service.EquifaxCreditBureauAndCustomerDataService;
import com.fidypay.utils.constants.AuthenticationVerify;
import com.fidypay.utils.constants.ResponseMessage;

import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CreditScoreAndCustomerDataController {

	private static final Logger LOGGER = LoggerFactory.getLogger(CreditScoreAndCustomerDataController.class);

	@Autowired
	private MerchantsRepository merchantsRepository;

	@Autowired
	private MerchantInfoRepository merchantinforepository;

	@Autowired
	private EquifaxCreditBureauAndCustomerDataService equifaxCreditBureauAndCustomerDataService;

	@ApiOperation(value = "Credit Bureau Report")
	@PostMapping("/creditScoreDetailed")
	public Map<String, Object> creditBureauReport(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@Valid @RequestBody CreditBureauReportRequest creditBureauReportRequest) {

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

					return equifaxCreditBureauAndCustomerDataService.saveDataForCreditBureauReport(
							creditBureauReportRequest, merchantInfo.getMerchantId(),
							merchants.getMerchantFloatAmount(),merchants.getMerchantBusinessName(), merchants.getMerchantEmail());
				}
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);

			}

		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}
		return map;

	}

	@ApiOperation(value = "Fetch Customer Details ")
	@PostMapping("/creditScoreBasic")
	public Map<String, Object> fetchCustomerDetails(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@Valid @RequestBody CustomerDataRequest customerDataRequest) {
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

					return equifaxCreditBureauAndCustomerDataService.saveDataForFetchCustomerDetails(
							customerDataRequest, merchantInfo.getMerchantId(), merchants.getMerchantFloatAmount(),merchants.getMerchantBusinessName(), merchants.getMerchantEmail());
				}
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			}

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}

		return map;
	}
	
	
	@ApiOperation(value = "Fetch Credit Score")
	@PostMapping("/fetchCreditScore")
	public Map<String, Object> fetchCreditScore(@RequestHeader("Authorization") String Authorization,
			@RequestHeader(value = "Client-Id") String clientId, @RequestHeader("Client-Secret") String clientSecret,
			@Valid @RequestBody CustomerDataRequest customerDataRequest) {
		
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

					return equifaxCreditBureauAndCustomerDataService.saveDataForFetchCreditScore(
							customerDataRequest, merchantInfo.getMerchantId(), merchants.getMerchantFloatAmount(),merchants.getMerchantBusinessName(), merchants.getMerchantEmail());
				}
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			}

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}
		return map;
	}
}
