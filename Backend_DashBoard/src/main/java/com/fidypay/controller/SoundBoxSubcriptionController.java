package com.fidypay.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fidypay.encryption.Encryption;
import com.fidypay.entity.MerchantInfo;
import com.fidypay.repo.MerchantInfoRepository;
import com.fidypay.request.SoundBoxSubscriptionRequest;
import com.fidypay.request.UpadteSoundBoxDetailsRequest;
import com.fidypay.request.UpdateSoundBoxSubscriptionRequest;
import com.fidypay.service.SoundBoxSubscriptionService;
import com.fidypay.utils.constants.ResponseMessage;

import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class SoundBoxSubcriptionController {

	@Autowired
	private SoundBoxSubscriptionService soundBoxSubscriptionService;

	@Autowired
	private MerchantInfoRepository merchantInfoRepository;

	@ApiOperation(value = "Save soundbox Subscription details")
	@PostMapping("/save-soundbox-basic-detail")
	public Map<String, Object> saveSoundBoxSubscriptionDetails(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret,
			@Valid @RequestBody SoundBoxSubscriptionRequest soundBoxSubscriptionRequest) {
		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {
				return soundBoxSubscriptionService.saveSoundBoxSubscriptionDetails(soundBoxSubscriptionRequest);
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}

		return map;
	}

	@ApiOperation(value = "Update SoundBox Subscription Details")
	@PostMapping("/update-soundbox-subscription-detail")
	public Map<String, Object> UpdateSoundBoxSubscriptionDetails(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret,
			@Valid @RequestBody UpdateSoundBoxSubscriptionRequest updateSoundBoxSubscriptionRequest) throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {
				return soundBoxSubscriptionService.updateSoundBoxSubscriptionDetails(updateSoundBoxSubscriptionRequest,
						merchantInfo.getMerchantId());
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}

		return map;
	}

	@ApiOperation(value = "Get SoundBoxDetails")
	@PostMapping("/getSoundBoxDetails")
	public Map<String, Object> getSoundBoxDetails(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret,
			@RequestParam("subMerchantInfoId") long subMerchantInfoIdV2) {
		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {
				return soundBoxSubscriptionService.getSoundBoxDetails(subMerchantInfoIdV2,
						merchantInfo.getMerchantId());
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}

		return map;
	}

	@ApiOperation(value = "SoundBox Subscription Acitve DeActive")
	@PostMapping("/soundBoxSubscriptionAcitveDeActive")
	public Map<String, Object> soundBoxSubscriptionAcitveDeActive(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret,
			@RequestParam("soundBoxSubscriptionId") long soundBoxSubscriptionId,
			@RequestParam("isActive") String isActive) {
		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {
				return soundBoxSubscriptionService.soundBoxSubscriptionAcitveDeActive(soundBoxSubscriptionId, isActive);
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}

		return map;
	}

	@ApiOperation(value = "Update SoundBoxSubscription")
	@PostMapping("/update-soundbox-basic-details")
	public Map<String, Object> UpdateSoundBoxSubscription(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret,
			@Valid @RequestBody UpadteSoundBoxDetailsRequest soundBoxSubscriptionRequest) {
		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {
				return soundBoxSubscriptionService.UpdateSoundBoxSubscription(soundBoxSubscriptionRequest,
						merchantInfo.getMerchantId());
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}

		return map;
	}

	@ApiOperation(value = "SoundBox Subscription Is-Deleted")
	@PostMapping("/soundBoxSubscriptionIsDeleted")
	public Map<String, Object> soundBoxSubscriptionIsDeleted(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret,
			@RequestParam("soundBoxSubscriptionId") long soundBoxSubscriptionId,
			@RequestParam("isDeleted") String isDeleted) {
		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {
				return soundBoxSubscriptionService.soundBoxSubscriptionIsDeleted(soundBoxSubscriptionId, isDeleted);
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}

		return map;
	}

}
