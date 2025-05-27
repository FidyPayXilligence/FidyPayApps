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
@RequestMapping("/CreateMerchantByAdmin")
public class MerchantRegisterController {

	@Autowired
	MerchantsService merchantsService;

	@ApiOperation(value = "Register Merchant By Admin")
	@GetMapping("/POST")
	public String addFloatByAdmin(@RequestParam(value = "urlParam") String urlParam,
			@RequestParam(value = "fname") String fname, @RequestParam(value = "lname") String lname,
			@RequestParam(value = "bName") String bName, @RequestParam(value = "gender") String gender,
			@RequestParam(value = "email") String email, @RequestParam(value = "phone") String phone,
			@RequestParam(value = "merchantPassword") String merchantPassword,
			@RequestParam(value = "merchantAddress1") String merchantAddress1,
			@RequestParam(value = "city") String city, @RequestParam(value = "state") String state,
			@RequestParam(value = "zipCode") String zipCode, @RequestParam(value = "country") String country,
			@RequestParam(value = "latitude") String latitude, @RequestParam(value = "longitude") String longitude,
			@RequestParam(value = "outletType") String outletType,
			@RequestParam(value = "tillPassword") String tillPassword) {

		String response = "NA";
		try {
			fname = fname.replaceAll(" ", "+");
			lname = lname.replaceAll(" ", "+");
			bName = bName.replaceAll(" ", "+");
			gender = gender.replaceAll(" ", "+");
			email = email.replaceAll(" ", "+");
			phone = phone.replaceAll(" ", "+");
			merchantPassword = merchantPassword.replaceAll(" ", "+");
			merchantAddress1 = merchantAddress1.replaceAll(" ", "+");
			city = city.replaceAll(" ", "+");
			state = state.replaceAll(" ", "+");
			zipCode = zipCode.replaceAll(" ", "+");
			country = country.replaceAll(" ", "+");
			latitude = latitude.replaceAll(" ", "+");
			longitude = longitude.replaceAll(" ", "+");
			outletType = outletType.replaceAll(" ", "+");
			tillPassword = tillPassword.replaceAll(" ", "+");

			response = merchantsService.addMerchantByAdmin(urlParam, fname, lname, bName, gender, email, phone,
					merchantPassword, merchantAddress1, city, state, zipCode, country, latitude, longitude, outletType,
					tillPassword);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;

	}

	
	
}
