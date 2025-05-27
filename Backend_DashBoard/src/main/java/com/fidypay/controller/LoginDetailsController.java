package com.fidypay.controller;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fidypay.encryption.Encryption;
import com.fidypay.entity.MerchantInfo;
import com.fidypay.repo.MerchantInfoRepository;
import com.fidypay.request.LoginRequest;
import com.fidypay.service.LogInDetailsService;
import com.fidypay.utils.constants.ResponseMessage;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class LoginDetailsController {

	@Autowired
	public LogInDetailsService logInDetailsService;

	@Autowired
	private MerchantInfoRepository merchantInfoRepository;

	@PostMapping("/logOut")
	public Map<String, Object> saveLogOutDetails(@RequestHeader("Login-Key") String loginKey) throws ParseException {
		return logInDetailsService.saveLogOutDetails(loginKey);

	}

	@PostMapping("/findByMerchantId")
	public Map<String, Object> findByMerchantId(@RequestHeader(value = "Client-Id") String clientId,
			@RequestHeader("Client-Secret") String clientSecret, @RequestBody LoginRequest LoginRequest)
			throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {

			MerchantInfo merchantInfo = merchantInfoRepository.findByClientIdAndClientSecretAndIsMerchantActive(
					Encryption.encString(clientId), Encryption.encString(clientSecret), '1');
			if (merchantInfo != null) {
				System.out.println("Inside Condition true");
				return logInDetailsService.loginlogOutMerchantId(LoginRequest, merchantInfo.getMerchantId());
			} else {
				System.out.println("Inside Condition false");
				map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Inside Condition exception");
			map.put(ResponseMessage.CODE, ResponseMessage.UNAUTHORISED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.UNAUTHORISED_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.FIELD, ResponseMessage.FIELD_I);
		}

		return map;

	}

}
