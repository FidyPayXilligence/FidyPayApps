package com.fidypay.service;

import java.util.Map;

import javax.validation.Valid;

import com.fidypay.request.VoterIdRequest;
import com.fidypay.request.VoterIdRequestV2;

public interface VoterIdService {


	//-------------------------------------------
	
	Map<String, Object> saveDataForVoter(@Valid VoterIdRequest voterIdRequest, long merchantId,
			Double merchantFloatAmount, String password, String businessName, String email);

	Map<String, Object> saveDataForVoterV2(@Valid VoterIdRequestV2 voterIdRequestV2, long merchantId,
			Double merchantFloatAmount, String password, String businessName, String email);
	
	Map<String, Object> saveDataForValidateVoterId(@Valid VoterIdRequestV2 voterIdRequestV2, long merchantId,
			Double merchantFloatAmount, String password, String businessName, String email);

}
