package com.fidypay.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fidypay.service.FileUploadService;
import com.fidypay.utils.constants.ResponseMessage;
import com.fidypay.utils.ex.AmazonClient;

@Service
public class FileUploadServiceImpl implements FileUploadService {

	@Autowired
	private AmazonClient amazonClient;

	@Override
	public Map<String, Object> fileUpload(@NotNull MultipartFile file) {
		Map<String, Object> map = new HashMap<>();
		try {

			String res = amazonClient.uploadFileV2(file);

			JSONObject object = new JSONObject(res);

			String url = object.getString("url");
			String objectKey = object.getString("objectKey");

			map.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
			map.put(ResponseMessage.DESCRIPTION, "File Uploaded Sucessfully");
			map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_SUCCESS);
			map.put("url", url);
			map.put("key", objectKey);

		} catch (Exception e) {
			e.printStackTrace();
			map.put(ResponseMessage.CODE, ResponseMessage.SOMETHING_WENT_WRONG);
			map.put(ResponseMessage.DESCRIPTION, ResponseMessage.SOMETHING_WENT_WRONG_DESCRIPTION);
			map.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
		}

		return map;
	}

}
