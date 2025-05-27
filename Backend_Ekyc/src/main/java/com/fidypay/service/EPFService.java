package com.fidypay.service;

import java.util.Map;

import javax.validation.Valid;

public interface EPFService {
	
	Map<String,Object> epfUANValidation(@Valid String uanNumber, long merchantId, Double merchantFloatAmount, String businessName, String email);

}
