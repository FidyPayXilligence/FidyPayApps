package com.fidypay.ServiceProvider.Signzy;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fidypay.dto.IfscCodeResponse;
import com.fidypay.utils.constants.ResponseMessage;
import com.google.gson.Gson;

@Service
public class ValidateBankAccount {

	public String verifyIfscCode(String beneficiaryifsccode) throws Exception {

		RestTemplate restTemplate = new RestTemplate();
		String response = null;
		String url = "https://ifsc.razorpay.com/" + beneficiaryifsccode;
		try {
			response = restTemplate.getForObject(url, String.class);
			org.json.JSONObject jsonObject = new org.json.JSONObject(response);
			String branch = jsonObject.getString("BRANCH");
			String bank = jsonObject.getString("BANK");
			String ifsc = jsonObject.getString("IFSC");
			IfscCodeResponse ifscCodeResponse = new IfscCodeResponse();
			ifscCodeResponse.setCode(ResponseMessage.SUCCESS);
			ifscCodeResponse.setStatus(ResponseMessage.STATUS_SUCCESS);
			ifscCodeResponse.setBank(bank);
			ifscCodeResponse.setBranch(branch);
			ifscCodeResponse.setIfsc(ifsc);
			response = new Gson().toJson(IfscCodeResponse.getIfscCodeResponse(ifscCodeResponse));

		} catch (HttpClientErrorException e) {
			response = ResponseMessage.INVALID_IFSCCODE;
		}

		return response;

	}

}