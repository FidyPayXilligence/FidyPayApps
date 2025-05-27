package com.fidypay.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fidypay.entity.Cities;
import com.fidypay.entity.Countries;
import com.fidypay.entity.States;
import com.fidypay.repo.CitiesRepository;
import com.fidypay.repo.CountriesRepository;
import com.fidypay.repo.StatesRepository;
import com.fidypay.response.CitiesListPayLoad;
import com.fidypay.response.CountriesListPayLoad;
import com.fidypay.response.StatesListPayLoad;
import com.fidypay.service.GlobalAPIService;
import com.fidypay.utils.constants.ResponseMessage;

@Service
public class GlobalAPIServiceImpl implements GlobalAPIService {
	
	@Autowired
	private CountriesRepository countriesRepository;
	
	@Autowired
	private StatesRepository statesRepository;
	
	@Autowired
	private CitiesRepository citiesRepository;

	@Override
	public Map<Object, Object> countryList() {
		Map<Object, Object> map = new HashMap<Object, Object>();
		List<CountriesListPayLoad> countryList = new ArrayList<>();
		try {
			List<Countries> list = countriesRepository.findAll();
			
			for(Countries countries:list) {
				
				CountriesListPayLoad payLoad=new CountriesListPayLoad();
				payLoad.setId(countries.getId());
				payLoad.setIso(countries.getIso());
				payLoad.setName(countries.getName());
				payLoad.setPhonecode(countries.getPhonecode());
				
				countryList.add(payLoad);
				
			}
			
			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, "Countries List");
			map.put("countryList", countryList);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			
		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Data Not found");
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		
		return map;
	}

	

	@Override
	public Map<Object, Object> statesListByCountryId(Long countryId) {
		Map<Object, Object> map = new HashMap<Object, Object>();
		List<StatesListPayLoad> stateList = new ArrayList<>();
		try {
			List<States> list = statesRepository.findByCountryId(countryId);
			
			for(States states:list) {
				
				StatesListPayLoad payLoad=new StatesListPayLoad();
				payLoad.setCountryCode(states.getCountryCode());
				payLoad.setCountryId(states.getCountryId());
				payLoad.setId(states.getId());
				payLoad.setIso2(states.getIso2());
				payLoad.setName(states.getName());
				
				
				stateList.add(payLoad);
				
			}
			
			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, "States List");
			map.put("stateList", stateList);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			
		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Data Not found");
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		
		return map;
	}



	@Override
	public Map<Object, Object> citiesListByStateId(Long stateId) {
		Map<Object, Object> map = new HashMap<Object, Object>();
		List<CitiesListPayLoad> cityList = new ArrayList<>();
		try {
			List<Cities> list = citiesRepository.findByStateId(stateId);
			
			for(Cities cities:list) {
				
				CitiesListPayLoad payLoad=new CitiesListPayLoad();
				payLoad.setCountryCode(cities.getCountryCode());
				payLoad.setCountryId(cities.getCountryId());
				payLoad.setId(cities.getId());
				payLoad.setStateCode(cities.getStateCode());
				payLoad.setStateId(cities.getStateId());
				payLoad.setName(cities.getName());
				
				cityList.add(payLoad);
				
			}
			
			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, "Cities List");
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			map.put("cityList", cityList);
			
		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Data Not found");
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		
		return map;
	}



	@Override
	public Map<Object, Object> citiesListByStateName(String stateName) {
		Map<Object, Object> map = new HashMap<Object, Object>();
		String city=null;
		JSONArray dataArray = new JSONArray();
		try {
			States states= statesRepository.findByStateName(stateName);
			 
			Long statesId= states.getId();
			
			System.out.println("statesId: "+states.getId());
			
			List<Cities> list = citiesRepository.findByStateId(statesId);
			
			for(Cities cities:list) {
				
				 city=cities.getName();
				
				System.out.println("city: "+city);
				
					dataArray.add(city);
				
			}
			
			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put("cities", dataArray);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
			
		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Data Not found");
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		
		return map;
	}

	
	
	
	
}
