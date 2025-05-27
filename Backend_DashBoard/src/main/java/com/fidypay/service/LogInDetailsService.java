package com.fidypay.service;

import java.text.ParseException;
import java.util.Map;

import com.fidypay.request.LoginRequest;

public interface LogInDetailsService {

	String saveLogInDetails(long merchantId, String description) throws ParseException;

	Map<String, Object> saveLogOutDetails(String logInid) throws ParseException;

	Map<String, Object> loginlogOutMerchantId(LoginRequest loginRequest, long merchantId) throws ParseException;

}
