package com.fidypay.service.impl;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fidypay.entity.LoginDetails;
import com.fidypay.entity.MerchantUser;
import com.fidypay.repo.LoginDetailsRepository;
import com.fidypay.repo.MerchantUserRepository;
import com.fidypay.request.LoginRequest;
import com.fidypay.response.LogInLogOutDetailsResponse;
import com.fidypay.service.LogInDetailsService;
import com.fidypay.utils.AuthTokenGenerator;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.DateAndTime;
import com.fidypay.utils.ex.DateUtil;

@Service
public class LoginDetailsServiceImpl implements LogInDetailsService {

	@Autowired
	public LoginDetailsRepository loginDetailsRepository;

	@Autowired
	private MerchantUserRepository merchantUserRepository;
	
	@Autowired
	private AuthTokenGenerator authTokenGenerator;

	@Override
	public String saveLogInDetails(long merchantId, String description) throws ParseException {

		Timestamp timeStamp = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());
		// Generate a 240 bit password
		String loginKey = authTokenGenerator.generateToken(240);
		LoginDetails loginDetails = new LoginDetails();
		loginDetails.setMerchantId(merchantId);
		loginDetails.setLogInTime(timeStamp.toString());
		loginDetails.setLogOutTime("NA");
		loginDetails.setDate(timeStamp);
		loginDetails.setLoginid(loginKey);
		loginDetails.setDescription(description);
		LoginDetails savedLoginDetails = loginDetailsRepository.save(loginDetails);
		return loginKey;
	}

	@Override
	public Map<String, Object> saveLogOutDetails(String logInid) throws ParseException {
		Map<String, Object> map = new HashMap<>();

		Timestamp timeStamp = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());
		LoginDetails loginDetails = loginDetailsRepository.findBylogInid(logInid);

		if (loginDetails != null) {

			if (loginDetails.getLogOutTime() == null || loginDetails.getLogOutTime().equals("NA")) {
				loginDetails.setLogOutTime(timeStamp.toString());
				loginDetailsRepository.save(loginDetails);

				System.out.println("loginDetails.getMerchantId() " + loginDetails.getMerchantId());
				MerchantUser merchantUser = merchantUserRepository.findById(loginDetails.getMerchantId()).get();
				merchantUser.setLoginCount(0L);
				merchantUser.setMerchantUserKey("NA");
				merchantUserRepository.save(merchantUser);

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "Logout Successfully");

			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Already Logged out");
			}
			return map;

		} else {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Login details not found");
		}
		return map;

	}

	@Override
	public Map<String, Object> loginlogOutMerchantId(LoginRequest loginRequest, long merchantId) throws ParseException {

		Map<String, Object> map = new HashMap<>();
		try {
			Pageable paging = PageRequest.of(loginRequest.getPageNo(), loginRequest.getPageSize(),
					Sort.by("DATE").descending());

			String startDate = loginRequest.getStartDate();
			String endDate = loginRequest.getEndDate();

//			String description = loginRequest.getDescription();

			if (DateUtil.isValidDateFormat(startDate) == false || DateUtil.isValidDateFormat(endDate) == false) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.INVALID_DATE_FORMATE);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}

			if (DateUtil.isValidDateFormat(startDate, endDate)) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.INVALID_FROM_TO_DATE);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}
			startDate = startDate + " 00.00.00.0";
			endDate = endDate + " 23.59.59.9";

			List<LoginDetails> list = new ArrayList<LoginDetails>();

			List<LogInLogOutDetailsResponse> logInLogOutDetailsResponselist = new ArrayList<LogInLogOutDetailsResponse>();

			Page<LoginDetails> logInLogOutlist = null;

			String description = "Merchant Dashboard";

			logInLogOutlist = loginDetailsRepository.findByStartDateAndEndDateAndMerchantIdAndDescription(merchantId,
					description, startDate, endDate, paging);

			list = logInLogOutlist.getContent();

			if (list.size() != 0) {
				list.forEach(objects -> {

					try {

						String date = DateAndTime.dateFormatReports(objects.getDate().toString());
						String loginDatetime = DateAndTime.dateFormatReports(objects.getLogInTime().toString());
						String logOutDatetime = objects.getLogOutTime().toString();
						if (logOutDatetime.equals("NA")) {

							LogInLogOutDetailsResponse response = new LogInLogOutDetailsResponse();
							response.setDate(date);
							response.setLogInDetailsId(objects.getLogInDetailsId());
							response.setMerchantId(objects.getMerchantId());
							response.setLogInTime(loginDatetime);
							response.setLogOutTime(logOutDatetime);
							response.setDescription(objects.getDescription());
							response.setLoginUniqueId(objects.getLogInid());
							logInLogOutDetailsResponselist.add(response);

						} else {
							String date1 = DateAndTime.dateFormatReports(objects.getDate().toString());
							String loginDatetime1 = DateAndTime.dateFormatReports(objects.getLogInTime().toString());
							String logOutDatetime1 = DateAndTime.dateFormatReports(objects.getLogOutTime().toString());

							LogInLogOutDetailsResponse response = new LogInLogOutDetailsResponse();
							response.setDate(date1);
							response.setLogInDetailsId(objects.getLogInDetailsId());
							response.setMerchantId(objects.getMerchantId());
							response.setLogInTime(loginDatetime1);
							response.setLogOutTime(logOutDatetime1);
							response.setDescription(objects.getDescription());
							response.setLoginUniqueId(objects.getLogInid());
							logInLogOutDetailsResponselist.add(response);

						}

					} catch (ParseException e) {

						e.printStackTrace();
					}

				});

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, "logIn List");
				map.put("data", logInLogOutDetailsResponselist);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);

			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, "logInList not found");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			}

		} catch (Exception e) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, e.getMessage().toString());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}

		return map;
	}

}
