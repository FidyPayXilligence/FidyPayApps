package com.fidypay.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fidypay.service.MerchantsService;

import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/AddFloatByAdmin")
public class MerchantFloatController {

	@Autowired
	MerchantsService merchantsService;

	@ApiOperation(value = "add Float By Admin")
	@GetMapping("/POST")
	public String addFloatByAdmin(@RequestParam(value = "urlParam") String urlParam,
			@RequestParam(value = "amount") double amount, @RequestParam(value = "receivedAmt") double receivedAmt,
			@RequestParam(value = "payMode") String payMode, @RequestParam(value = "description") String description,
			@RequestParam(value = "txnType") String txnType, @RequestParam(value = "merchantId") long merchantId,
			@RequestParam(value = "isReverted") char isReverted) {
		String response = null;
		try {

			String floatTo = urlParam.substring(8);

			response = merchantsService.addFloatByAdmin(floatTo, amount, receivedAmt, payMode, description, txnType,
					isReverted, merchantId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;

	}

}
