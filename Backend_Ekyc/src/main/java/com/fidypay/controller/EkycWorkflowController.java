package com.fidypay.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fidypay.encryption.Encryption;
import com.fidypay.entity.MerchantInfo;
import com.fidypay.repo.MerchantInfoRepository;
import com.fidypay.request.EkycUpdateWorkflowRequest;
import com.fidypay.request.EkycWorkflowRequest;
import com.fidypay.request.EkycworkflowFilterRequest;
import com.fidypay.service.EkycWorkflowService;
import com.fidypay.utils.constants.ResponseMessage;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/workflow")
public class EkycWorkflowController {

	private static final Logger LOGGER = LoggerFactory.getLogger(EkycWorkflowController.class);

	@Autowired
	private MerchantInfoRepository merchantInfoRepository;

	@Autowired
	private EkycWorkflowService ekycworkflowtableservice;

	@PostMapping("/saveWorkflowDetails")
	public Map<String, Object> saveWorkflowDetails(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, @Valid @RequestBody EkycWorkflowRequest request) {

		Map<String, Object> response = new HashMap<>();

		try {
			LOGGER.info("clientSecret:- {}", clientSecret);

			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');

			if (merchantInfo != null) {
				long merchantId = merchantInfo.getMerchantId();
				String imageUrl = merchantInfo.getImageUrl();
				LOGGER.info("merchantId:- {}", merchantId);
				return ekycworkflowtableservice.saveWorkflowDetails(request, merchantId, imageUrl);

			} else {

				response.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				response.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			}

		} catch (Exception e) {
			e.printStackTrace();
			response.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
		}

		return response;
	}

	@PostMapping("/findAllWorkflowDetails")
	public Map<String, Object> findAllWorkflowDetails(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, @RequestParam("pageNo") Integer pageNo,
			@RequestParam("pageSize") Integer pageSize) {

		Map<String, Object> response = new HashMap<String, Object>();
		try {
			LOGGER.info("clientSecret:- " + clientSecret);

			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');

			if (merchantInfo != null) {
				long merchantId = merchantInfo.getMerchantId();
				LOGGER.info("merchantId:- " + merchantId);
				return ekycworkflowtableservice.getAllWorkflowDetails(pageNo, pageSize, merchantId);

			} else {

				response.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				response.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			}

		} catch (IllegalArgumentException exception) {
			response.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response.put(ResponseMessage.DESCRIPTION, "Please pass positive value on pageNo and pageSize");
		} catch (Exception e) {
            response.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
		}

		return response;
	}

	@PostMapping("/findAllWorkflowName")
	public Map<String, Object> findAllWorkflowName(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret) {

		Map<String, Object> response = new HashMap<String, Object>();
		try {
			LOGGER.info("clientSecret:- " + clientSecret);

			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');

			if (merchantInfo != null) {
				long merchantId = merchantInfo.getMerchantId();
				LOGGER.info("merchantId:- " + merchantId);
				return ekycworkflowtableservice.findAllWorkflowName(merchantId);

			} else {

				response.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				response.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			}

		} catch (IllegalArgumentException exception) {
			response.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response.put(ResponseMessage.DESCRIPTION, "Please pass positive value on pageNo and pageSize");
		} catch (Exception e) {

			response.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
		}

		return response;
	}

	@PostMapping("/findById")
	public Map<String, Object> findById(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, @RequestParam("ekycWorkflowId") String  ekycWorkflowId) {

		Map<String, Object> response = new HashMap<String, Object>();

		try {
			LOGGER.info("clientSecret:- " + clientSecret);

			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');

			if (merchantInfo != null) {
				long merchantId = merchantInfo.getMerchantId();
				LOGGER.info("merchantId:- " + merchantId);
				return ekycworkflowtableservice.getById(Long.valueOf(ekycWorkflowId), merchantId);

			} else {

				response.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				response.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			}

		}catch (NumberFormatException ex) {
			ex.printStackTrace();
			response.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
		}
		
		catch (Exception e) {
			e.printStackTrace();
			response.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
		}

		return response;
	}

	@PostMapping("/deleteById")
	public Map<String, Object> deleteById(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, @RequestParam("ekycWorkflowId") long ekycWorkflowId) {

		Map<String, Object> response = new HashMap<String, Object>();

		try {
			LOGGER.info("clientSecret:- " + clientSecret);

			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');

			if (merchantInfo != null) {
				long merchantId = merchantInfo.getMerchantId();
				LOGGER.info("merchantId:- " + merchantId);
				return ekycworkflowtableservice.deleteById(ekycWorkflowId, merchantId);

			} else {

				response.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				response.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			}

		} catch (Exception e) {
			response.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
		}

		return response;
	}

	@PostMapping("/updateById")
	public Map<String, Object> updateById(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, @RequestBody EkycUpdateWorkflowRequest ekycUpdateWorkflowRequest) {

		Map<String, Object> response = new HashMap<>();

		try {
			LOGGER.info("clientSecret:- {}" , clientSecret);

			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');

			if (merchantInfo != null) {
				long merchantId = merchantInfo.getMerchantId();
				LOGGER.info("merchantId:- {}" , merchantId);
				return ekycworkflowtableservice.updateById(ekycUpdateWorkflowRequest, merchantId);

			} else {

				response.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				response.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			}

		} catch (Exception e) {
			response.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
		}

		return response;
	}

	@GetMapping("/getKycTypeList")
	public Map<String, Object> getKycTypeList(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret) {

		Map<String, Object> response = new HashMap<>();

		try {
			LOGGER.info("clientSecret:- {}" , clientSecret);

			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');

			if (merchantInfo != null) {
				long merchantId = merchantInfo.getMerchantId();
				LOGGER.info("merchantId:- {}" , merchantId);
				return ekycworkflowtableservice.getKycTypeList(merchantId);

			} else {

				response.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				response.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			}

		} catch (Exception e) {
			e.printStackTrace();
			response.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
		}

		return response;
	}
	
	
	@PostMapping("/findByWorkflowName")
	public Map<String, Object> findByWorkflowName(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, @RequestParam("ekycWorkflowName") String ekycWorkflowName) {

		Map<String, Object> response = new HashMap<>();

		try {
			LOGGER.info("clientSecret:- {}" , clientSecret);

			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');

			if (merchantInfo != null) {
				long merchantId = merchantInfo.getMerchantId();
				LOGGER.info("merchantId:- {}" , merchantId);
				return ekycworkflowtableservice.getByWorkflowName(ekycWorkflowName, merchantId);

			} else {

				response.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				response.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			}

		} catch (Exception e) {
			e.printStackTrace();
			response.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
		
		}

		return response;
	}
	
	
	@PostMapping("/searchEkycworkflowByFilter")
	public Map<String, Object> searchEkycworkflowByFilter(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret,
			@RequestBody @Valid EkycworkflowFilterRequest ekycworkflowFilterRequest) {
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			LOGGER.info("clientSecret:- " + clientSecret);

			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');

			if (merchantInfo != null) {
				long merchantId = merchantInfo.getMerchantId();
				LOGGER.info("merchantId:- " + merchantId);
				return ekycworkflowtableservice.searchEkycworkflowByFilter(ekycworkflowFilterRequest, merchantId);

			} else {
               	response.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				response.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			}

		} catch (Exception e) {
			response.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			response.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			response.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
		}

		return response;
	}
}
