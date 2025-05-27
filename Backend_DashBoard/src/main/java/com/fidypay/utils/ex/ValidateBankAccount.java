package com.fidypay.utils.ex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	
	public static boolean isValidPanCardNo(String panCardNo) {

		String regex = "[A-Z]{5}[0-9]{4}[A-Z]{1}";

		Pattern p = Pattern.compile(regex);

		if (panCardNo == null) {
			return false;
		}
		if (panCardNo.length() != 10) {
			return false;
		}

		Matcher m = p.matcher(panCardNo);

		return m.matches();
	}

	
}
