package com.fidypay.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fidypay.service.GlobalAPIService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/global")
public class GlobalAPIController {

	@Autowired
	private GlobalAPIService globalAPIService;

	@GetMapping(value = "/country_list")
	public Map<Object, Object> countryList() {
		return globalAPIService.countryList();

	}

	@GetMapping(value = "/statesList")
	public Map<Object, Object> stateslist(@RequestParam("countryId") Long countryId) {
		return globalAPIService.statesListByCountryId(countryId);
	}

	@GetMapping(value = "/citiesList")
	public Map<Object, Object> citiesList(@RequestParam("stateId") Long stateId) {
		return globalAPIService.citiesListByStateId(stateId);
	}

	@GetMapping(value = "/citiesListByStateName")
	public Map<Object, Object> citiesListByStateName(@RequestParam("stateName") String stateName) {
		return globalAPIService.citiesListByStateName(stateName);
	}

}
