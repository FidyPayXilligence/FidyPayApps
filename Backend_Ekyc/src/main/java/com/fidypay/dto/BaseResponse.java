package com.fidypay.dto;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;


/*
 * This class is for API Response.
*/
public class BaseResponse {
	
	private HttpStatus status;
	private Object message;
	private LocalDateTime timestamp;
	private String code;

	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public Object getMessage() {
		return message;
	}

	public void setMessage(Object message) {
		this.message = message;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}


}
