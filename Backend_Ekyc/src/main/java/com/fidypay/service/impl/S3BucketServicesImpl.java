package com.fidypay.service.impl;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fidypay.entity.S3Object;
import com.fidypay.repo.S3ObjectRepository;
import com.fidypay.request.S3ObjectRequest;
import com.fidypay.service.S3ObjectServices;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.DateAndTime;

@Service
public class S3BucketServicesImpl implements S3ObjectServices {

	@Autowired
	private S3ObjectRepository s3ObjectRepository;

	@Override
	public Map<String, Object> saveS3Object(@Valid S3ObjectRequest s3ObjectRequest) {
		Timestamp timestamp;
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			timestamp = Timestamp.valueOf(DateAndTime.getCurrentTimeInIST());

			S3Object object = new S3Object();
			object.setBucketName(s3ObjectRequest.getBucketName());
			object.setObjectKey(s3ObjectRequest.getObjectKey());
			object.setCreationDate(timestamp);
			object.setObjectUrl(s3ObjectRequest.getObjectUrl());
			object.setMerchantId(s3ObjectRequest.getMerchantId());
			object.setServiceName(s3ObjectRequest.getServiceName());
			s3ObjectRepository.save(object);
			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, "Bucket added successfully");
			map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
		} catch (ParseException e) {
		
		}
		return map;
	}

	@Override
	public Map<String, Object> findByBucketId(long id) {
		Map<String, Object> map = new HashMap<String, Object>();
		S3Object ss = s3ObjectRepository.findById(id).get();
		map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
		map.put(ResponseMessage.DESCRIPTION, ss);
		map.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
		return map;
	}

}
