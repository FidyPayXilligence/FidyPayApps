package com.fidypay.service;

import java.util.Map;

public interface RegionDetailService {

	Map<String, Object> getStates();

	Map<String, Object> getCitiesByStateName(String stateName);

	Map<String, Object> getCitiesByStateCode(String stateCode);
	

}
