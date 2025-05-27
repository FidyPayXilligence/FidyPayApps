package com.fidypay.service;

public interface GSTKarzaService {

	String saveDataForGSTINSearchKarza(String gSTIN, long parseLong, Double merchantFloatAmount);

	String GSTINAuthentication(String gSTIN, long parseLong, Double merchantFloatAmount);

}
