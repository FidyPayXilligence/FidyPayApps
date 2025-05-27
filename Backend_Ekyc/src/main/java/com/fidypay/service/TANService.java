package com.fidypay.service;

import java.util.Map;

import com.fidypay.request.TANRequest;

public interface TANService {

	Map<String, Object> tanVerification(TANRequest tanRequest, long merchantId, Double merchantFloatAmount, String businessName, String email);

}
