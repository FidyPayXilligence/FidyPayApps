package com.fidypay.service;

import javax.validation.Valid;

public interface PanKarzaService {

	String checkPanStatus(@Valid String panNumber, long parseLong, Double merchantFloatAmount);

	String panProfileDetails(@Valid String panNumber, long parseLong, Double merchantFloatAmount);

	String checkPanAadharLinkStatus(@Valid String panNumber, long parseLong, Double merchantFloatAmount);

}
