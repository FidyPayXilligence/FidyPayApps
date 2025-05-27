package com.fidypay.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.fidypay.encryption.Encryption;
import com.fidypay.entity.MerchantInfo;
import com.fidypay.entity.Merchants;
import com.fidypay.repo.MerchantInfoRepository;
import com.fidypay.repo.MerchantsRepository;
import com.fidypay.request.HelpAndSupportRequest;
import com.fidypay.utils.constants.EmailNotification;
import com.fidypay.utils.constants.ResponseMessage;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class HelpAndSupportController {

	@Autowired
	private MerchantInfoRepository merchantInfoRepository;

	@Autowired
	private MerchantsRepository merchantsRepository;

	@PostMapping("/getcomplaintDetails")
	public Map<String, Object> complaintDetails(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret,
			@RequestBody HelpAndSupportRequest helpAndSupportRequest) {

		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {

				String reason = helpAndSupportRequest.getReason();
				String message = helpAndSupportRequest.getMessage();
				String emailId = helpAndSupportRequest.getEmailId();

				Merchants merchants = merchantsRepository.findById(merchantInfo.getMerchantId()).get();

				String businessName = Encryption.decString(merchants.getMerchantBusinessName());

				String merchantEmail = Encryption.decString(merchants.getMerchantEmail());
				String merchantMobile = Encryption.decString(merchants.getMerchantPhone());

				EmailNotification EmailNotification = new EmailNotification();
				EmailNotification.sendEmailNotificationHelpAndSupport(businessName, emailId, reason, message,
						merchantEmail, merchantMobile);

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "Your complaint has been successfully registered");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);

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

	@PostMapping("/productSubscribe/{productName}")
	public Map<String, Object> productSubscribe(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, @PathVariable String productName) {

		Map<String, Object> map = new HashMap<>();
		try {
			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {

				Merchants merchants = merchantsRepository.findById(merchantInfo.getMerchantId()).get();

				String businessName = Encryption.decString(merchants.getMerchantBusinessName());

				String merchantEmail = Encryption.decString(merchants.getMerchantEmail());
				String merchantMobile = Encryption.decString(merchants.getMerchantPhone());

				EmailNotification EmailNotification = new EmailNotification();
				EmailNotification.sendEmailNotificationServiceSubscribe(businessName, merchantEmail, merchantMobile,
						productName);

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "notification send successfully");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);

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
