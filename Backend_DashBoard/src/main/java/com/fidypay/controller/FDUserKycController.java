package com.fidypay.controller;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fidypay.request.UserKycRequest;
import com.fidypay.request.UserKycUpdateRequest;
import com.fidypay.service.UserKycDetailsService;

import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/kyc")
public class FDUserKycController {

	@Autowired
	private UserKycDetailsService userKycDetailsService;

	@ApiOperation(value = "Add User Kyc Details")
	@PostMapping("/addUserKycDetails")
	public Map<String, Object> addUserKycDetails(@Valid @RequestBody UserKycRequest userKycRequest) {
		return userKycDetailsService.addUserKycDetails(userKycRequest);
	}

	@ApiOperation(value = "Update User Kyc Details")
	@PostMapping("/updateUserKycDetails")
	public Map<String, Object> updateUserKycDetails(@Valid @RequestBody UserKycUpdateRequest userKycUpdateRequest) {
		return userKycDetailsService.updateUserKycDetails(userKycUpdateRequest);
	}
	
	
	@ApiOperation(value = "Find User Kyc Details By User Mobile")
	@GetMapping("/findUserKycDetailsByUserMobile")
	public Map<String, Object> findUserKycDetailsByUserMobile(@RequestParam("userMobile") String userMobile) {
		return userKycDetailsService.findUserKycDetailsByMobileNo(userMobile);
	}

	@ApiOperation(value = "Find User Kyc Details By User Unique Id")
	@GetMapping("/findUserKycDetailsByUserUniqueId")
	public Map<String, Object> findUserKycDetailsByUserUniqueId(@RequestParam("userUniqueId") String userUniqueId) {
		return userKycDetailsService.findUserKycDetailsByUserUniqueId(userUniqueId);
	}
	
	@ApiOperation(value = "Check User Kyc Status")
	@GetMapping("/checkUserKycStatus")
	public Map<String, Object> checkUserKycStatus(@RequestParam("userMobile") String userMobile) {
		return userKycDetailsService.checkUserKycStatus(userMobile);
	}

}
