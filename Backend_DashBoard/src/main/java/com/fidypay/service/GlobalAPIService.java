package com.fidypay.service;

import java.util.Map;

public interface GlobalAPIService {

	Map<Object, Object> countryList();
	
	Map<Object, Object> statesListByCountryId(Long countryId);

	Map<Object, Object> citiesListByStateId(Long stateId);

	Map<Object, Object> citiesListByStateName(String stateName);

	

}
