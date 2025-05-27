package com.fidypay.controller;

import com.fidypay.encryption.Encryption;
import com.fidypay.entity.MerchantInfo;
import com.fidypay.repo.MerchantInfoRepository;
import com.fidypay.request.MerchantUserActivityPayload;
import com.fidypay.request.MerchantUserActivityRequest;
import com.fidypay.service.MerchantUserActivityService;
import com.fidypay.utils.constants.ResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * @author prave
 * @Date 09-10-2023
 */
@RestController
@RequestMapping("/merchant-user-activities")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MerchantUserActivityController {

	private static final Logger log = LoggerFactory.getLogger(MerchantUserActivityController.class);
	private final MerchantUserActivityService merchantUserActivityService;
	private final MerchantInfoRepository merchantInfoRepository;

	public MerchantUserActivityController(MerchantUserActivityService merchantUserActivityService,
			MerchantInfoRepository merchantInfoRepository) {
		this.merchantUserActivityService = merchantUserActivityService;
		this.merchantInfoRepository = merchantInfoRepository;
	}

	@PostMapping("/save")
	public Map<String, Object> save(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret,
			@Valid @RequestBody MerchantUserActivityRequest merchantUserActivityRequest) throws Exception {
		log.info("REST Request to save MerchantUserActivity : {}", merchantUserActivityRequest);
		Map<String, Object> map = new HashMap<>();

		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');

			if (merchantInfo != null) {

				return merchantUserActivityService.saveMerchantUserActivity(merchantUserActivityRequest,
						merchantInfo.getMerchantId());

			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			}
		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;
	}

	@PostMapping("/list")
	public Map<String, Object> getMerchantActivityList(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret,
			@RequestBody MerchantUserActivityPayload merchantUserActivityPayload) {
		log.info("REST Request to getMerchantActivityList: {}", merchantUserActivityPayload);
		Map<String, Object> map = new HashMap<>();

		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');

			if (merchantInfo != null) {
				return merchantUserActivityService.findActivityByMerchantId(merchantUserActivityPayload,
						merchantInfo.getMerchantId());

			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			}
		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;
	}

	@PostMapping("/users-list")
	public Map<String, Object> getUserActivityList(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret,
			@RequestBody MerchantUserActivityPayload merchantUserActivityPayload) {
		log.info("REST Request to getUserActivityList: {}", merchantUserActivityPayload);
		Map<String, Object> map = new HashMap<>();

		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');

			if (merchantInfo != null) {
				Long userId = merchantUserActivityPayload.getMerchantUserId();
				return merchantUserActivityService.findActivityByUserId(merchantUserActivityPayload, userId);

			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			}
		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;

	}

}
