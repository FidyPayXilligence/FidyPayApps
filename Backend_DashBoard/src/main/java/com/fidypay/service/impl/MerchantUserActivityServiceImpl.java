package com.fidypay.service.impl;

import com.fidypay.encryption.Encryption;
import com.fidypay.entity.MerchantUser;
import com.fidypay.entity.MerchantUserActivity;
import com.fidypay.repo.MerchantUserActivityRepository;
import com.fidypay.repo.MerchantUserRepository;
import com.fidypay.request.MerchantUserActivityPayload;
import com.fidypay.request.MerchantUserActivityRequest;
import com.fidypay.response.MerchantUserActivityResponse;
import com.fidypay.service.MerchantUserActivityService;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.DateAndTime;
import com.fidypay.utils.ex.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author prave
 * @Date 09-10-2023
 */
@Service
@Transactional
public class MerchantUserActivityServiceImpl implements MerchantUserActivityService {

	private static final Logger log = LoggerFactory.getLogger(MerchantUserActivityServiceImpl.class);
	private final MerchantUserActivityRepository merchantUserActivityRepository;
	private final MerchantUserRepository merchantUserRepository;

	public MerchantUserActivityServiceImpl(MerchantUserActivityRepository merchantUserActivityRepository,
			MerchantUserRepository merchantUserRepository) {
		this.merchantUserActivityRepository = merchantUserActivityRepository;
		this.merchantUserRepository = merchantUserRepository;
	}

	@Override
	public Map<String, Object> saveMerchantUserActivity(MerchantUserActivityRequest merchantUserActivityRequest,
			long merchantId) throws ParseException {
		log.info("Service Request to save MerchantUserActivity : {}", merchantUserActivityRequest.getMerchantUserId());
		Map<String, Object> map = new HashMap<>();

		if (!merchantUserRepository.existsByMerchantUserId(merchantUserActivityRequest.getMerchantUserId())) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, "Merchant User Id not exist");
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			return map;
		}

		if (merchantUserRepository.existsByMerchantUserIdAndIsActive(merchantUserActivityRequest.getMerchantUserId(),
				'0')) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.MERCHANT_USER_DEACTIVATED);
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			return map;
		}
		
		

		MerchantUserActivity merchantUserActivity = new MerchantUserActivity();
		Timestamp date = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

		merchantUserActivity.setMerchantUserId(merchantUserActivityRequest.getMerchantUserId());
		merchantUserActivity.setMerchantId(merchantId);
		merchantUserActivity.setDate(date);
		merchantUserActivity.setApiUrl(merchantUserActivityRequest.getApiUrl());
		merchantUserActivity.setProductName(merchantUserActivityRequest.getProductName());
		merchantUserActivity.setApiName(merchantUserActivityRequest.getApiName());
		merchantUserActivity.setApiRequest(merchantUserActivityRequest.getApiRequest());
		merchantUserActivity.setType("MERCHANT");
		merchantUserActivityRepository.save(merchantUserActivity);

		map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
		map.put(ResponseMessage.DESCRIPTION, ResponseMessage.REGISTER_SUCCESSFULLY);
		map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
		return map;
	}

	@Override
	public Map<String, Object> findActivityByMerchantId(MerchantUserActivityPayload merchantUserActivityPayload,
			long merchantId) {
		log.info("Service Request to findActivityByMerchantId: {}", merchantId);
		Map<String, Object> map = new HashMap<>();

		try {
			long userId = merchantUserActivityPayload.getMerchantUserId();
			Pageable pageable = PageRequest.of(merchantUserActivityPayload.getPageNo(),
					merchantUserActivityPayload.getPageSize(), Sort.by("DATE").descending());

			String fromDate = merchantUserActivityPayload.getFromDate();
			String toDate = merchantUserActivityPayload.getToDate();

			if (!DateUtil.isValidDateFormat(fromDate) || !DateUtil.isValidDateFormat(toDate)) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.INVALID_DATE_FORMATE);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}

			if (DateUtil.isValidDateFormat(fromDate, toDate)) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.INVALID_FROM_TO_DATE);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}

			List<MerchantUserActivity> list = new ArrayList<MerchantUserActivity>();
			List<MerchantUserActivityResponse> listMerchantUserActivityResponse = new ArrayList<MerchantUserActivityResponse>();
			Page<MerchantUserActivity> listMerchantUserActivity = null;

			fromDate = fromDate + " 00.00.00.0";
			toDate = toDate + " 23.59.59.9";

			if (userId == 0) {
				listMerchantUserActivity = merchantUserActivityRepository
						.findActivityByMerchantIdAndDateRange(merchantId, fromDate, toDate, pageable);
			} else {
				listMerchantUserActivity = merchantUserActivityRepository.findActivityByUserIdAndDateRange(userId,
						fromDate, toDate, pageable);
			}
			list = listMerchantUserActivity.getContent();
			if (list.size() > 0) {
				list.forEach(objects -> {
					try {
						MerchantUserActivityResponse response = new MerchantUserActivityResponse();
						MerchantUser user = merchantUserRepository.findByMerchantUserId(objects.getMerchantUserId());

						String date = DateAndTime.dateFormatReports(objects.getDate().toString());
						response.setMerchantUserName(Encryption.decString(user.getMerchantUserName()));
						response.setMerchantUserId(objects.getMerchantUserId());
						response.setMerchantId(objects.getMerchantId());
						response.setDate(date);
						response.setApiUrl(objects.getApiUrl());
						response.setProductName(objects.getProductName());
						response.setApiName(objects.getApiName());
						response.setApiRequest(objects.getApiRequest());
						listMerchantUserActivityResponse.add(response);

					} catch (ParseException e) {
						e.printStackTrace();
					}
				});

				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_FOUND);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put("activityList", listMerchantUserActivityResponse);
				map.put("currentPage", listMerchantUserActivity.getNumber());
				map.put("totalItems", listMerchantUserActivity.getTotalElements());
				map.put("totalPages", listMerchantUserActivity.getTotalPages());
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			}
		} catch (Exception ex) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, ex.getMessage());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;
	}

	@Override
	public Map<String, Object> findActivityByUserId(MerchantUserActivityPayload merchantUserActivityPayload,
			long merchantId) {
		log.info("Service Request to merchantId: {}", merchantId);
		Map<String, Object> map = new HashMap<>();

		try {

			if (!merchantUserRepository.existsByMerchantUserId(merchantUserActivityPayload.getMerchantUserId())) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, "Merchant User Id not exist");
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}

			Pageable pageable = PageRequest.of(merchantUserActivityPayload.getPageNo(),
					merchantUserActivityPayload.getPageSize(), Sort.by("DATE").descending());

			String fromDate = merchantUserActivityPayload.getFromDate();
			String toDate = merchantUserActivityPayload.getToDate();

			if (!DateUtil.isValidDateFormat(fromDate) || !DateUtil.isValidDateFormat(toDate)) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.INVALID_DATE_FORMATE);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}

			if (DateUtil.isValidDateFormat(fromDate, toDate)) {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.INVALID_FROM_TO_DATE);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
				return map;
			}

			List<MerchantUserActivity> list = new ArrayList<MerchantUserActivity>();
			List<MerchantUserActivityResponse> listMerchantUserActivityResponse = new ArrayList<MerchantUserActivityResponse>();
			Page<MerchantUserActivity> listMerchantUserActivity = null;

			fromDate = fromDate + " 00.00.00.0";
			toDate = toDate + " 23.59.59.9";

			listMerchantUserActivity = merchantUserActivityRepository.findActivityByUserIdAndDateRange(
					merchantUserActivityPayload.getMerchantUserId(), fromDate, toDate, pageable);
			list = listMerchantUserActivity.getContent();

			if (list.size() != 0) {
				list.forEach(objects -> {
					try {
						MerchantUserActivityResponse response = new MerchantUserActivityResponse();
						MerchantUser user = merchantUserRepository
								.getById(merchantUserActivityPayload.getMerchantUserId());
						String date = DateAndTime.dateFormatReports(objects.getDate().toString());

						response.setMerchantUserName(Encryption.decString(user.getMerchantUserName()));
						response.setMerchantUserId(objects.getMerchantUserId());
						response.setMerchantId(objects.getMerchantId());
						response.setDate(date);
						response.setApiUrl(objects.getApiUrl());
						response.setProductName(objects.getProductName());
						response.setApiName(objects.getApiName());
						response.setApiRequest(objects.getApiRequest());

						listMerchantUserActivityResponse.add(response);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				});
				map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_FOUND);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
				map.put("activityList", listMerchantUserActivityResponse);
				map.put("currentPage", listMerchantUserActivity.getNumber());
				map.put("totalItems", listMerchantUserActivity.getTotalElements());
				map.put("totalPages", listMerchantUserActivity.getTotalPages());
			} else {
				map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
				map.put(ResponseMessage.DESCRIPTION, ResponseMessage.DATA_NOT_FOUND);
				map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
			}
		} catch (Exception ex) {
			map.put(ResponseMessage.CODE, ResponseMessage.FAILED);
			map.put(ResponseMessage.DESCRIPTION, ex.getMessage());
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_FAILED);
		}
		return map;
	}
}
