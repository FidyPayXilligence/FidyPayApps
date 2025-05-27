package com.fidypay.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fidypay.encryption.EncryptionDataRequest;
import com.fidypay.response.EncryptedResponse;
import com.fidypay.utils.constants.BaseResponse;
import com.fidypay.utils.constants.ResponseMessage;
import com.google.gson.Gson;

/*
 * ExceptionHandlerController class will handle all the exceptions of all the controllers.
 */

@RestControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		List<String> errors = new ArrayList<String>();

		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
			errors.add(error.getField() + " -> " + error.getDefaultMessage());
		}

		for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
			errors.add(error.getObjectName() + " -> " + error.getDefaultMessage());
		}

		String path = request.getDescription(true);

		if (path.contains("/dashboard/accountDetails/login-merchant")) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Invalid request");
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			Gson gObjet = new Gson();
			String EncriptMap = gObjet.toJson(map);
			EncryptedResponse encryptedResponse = new EncryptedResponse();
			encryptedResponse.setResponse(EncryptionDataRequest.encrypt(EncriptMap.toString()));
			return ResponseEntity.ok().body(encryptedResponse);
		}

		BaseResponse baseResponse = new BaseResponse(HttpStatus.BAD_REQUEST, errors, ResponseMessage.FAILED);
		return handleExceptionInternal(ex, baseResponse, headers, baseResponse.getStatus(), request);

	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		String path = request.getDescription(true);

		if (path.contains("/dashboard/accountDetails/login-merchant")) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.INVALID_JSON_FORMATE);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			Gson gObjet = new Gson();
			String EncriptMap = gObjet.toJson(map);
			EncryptedResponse encryptedResponse = new EncryptedResponse();
			encryptedResponse.setResponse(EncryptionDataRequest.encrypt(EncriptMap.toString()));
			return ResponseEntity.ok().body(encryptedResponse);
		}

		BaseResponse baseResponse = new BaseResponse(HttpStatus.BAD_REQUEST,
				Arrays.asList(ResponseMessage.INVALID_JSON_FORMATE), ResponseMessage.FAILED);
		return handleExceptionInternal(ex, baseResponse, headers, status, request);
	}

	@Override
	protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		BaseResponse baseResponse = new BaseResponse(HttpStatus.BAD_REQUEST,
				Arrays.asList(ResponseMessage.HEADERS_MISSING), ResponseMessage.FAILED);
		return handleExceptionInternal(ex, baseResponse, headers, status, request);
	}

	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		BaseResponse baseResponse = new BaseResponse(HttpStatus.BAD_REQUEST,
				Arrays.asList(ResponseMessage.MISSING_PARAMETER_AND_VALUE), ResponseMessage.MISSING_PARAMETER);
		return handleExceptionInternal(ex, baseResponse, headers, status, request);

	}

}
