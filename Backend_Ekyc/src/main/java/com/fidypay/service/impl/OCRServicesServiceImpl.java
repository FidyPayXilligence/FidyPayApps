package com.fidypay.service.impl;

import java.io.File;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fidypay.controller.OCRServicesController;
import com.fidypay.service.OCRServicesService;
import com.fidypay.utils.constants.ResponseMessage;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class OCRServicesServiceImpl implements OCRServicesService {

	private static final Logger LOGGER = LoggerFactory.getLogger(OCRServicesServiceImpl.class);
	
	@Override
	public String kycOCR(String filePath, long merchantId, Double merchantFloatAmount) {
	JSONObject object=null;
	String finalResponse=null;
	try {
	
		LOGGER.info("filePath: "+filePath);
		
		OkHttpClient client = new OkHttpClient().newBuilder()
				  .build();
				MediaType mediaType = MediaType.parse("text/plain");
				RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
				  .addFormDataPart("file",filePath,
				    RequestBody.create(MediaType.parse("application/octet-stream"),
				    new File(filePath)))
				  .addFormDataPart("maskAadhaar","")
				  .addFormDataPart("hideAadhaar","")
				  .addFormDataPart("conf","")
				  .addFormDataPart("docType","")
				  .build();
				Request request = new Request.Builder()
				  .url("https://testapi.karza.in/v3/ocr/kyc")
				  .method("POST", body)
				  .addHeader("x-karza-key", "NmXy370lZytA27VA")
				  .build();
				Response response = client.newCall(request).execute();
		
				finalResponse=response.body().string();
				
				LOGGER.info("request: "+request);
				LOGGER.info("finalResponse: "+finalResponse);
				
				LOGGER.info("request: "+request);
				LOGGER.info("finalResponse: "+finalResponse);
				
				Object obj = null;
				JSONParser parser = new JSONParser();
				obj = parser.parse(finalResponse);
				org.json.simple.JSONObject jsonObjectt = (org.json.simple.JSONObject) obj;
				
				org.json.simple.JSONArray result=(org.json.simple.JSONArray) jsonObjectt.get("result");
				LOGGER.info("result: "+result);
				
				
				
				
		
				finalResponse=result.toString();
		
		
	} catch (Exception e) {
		JSONObject jsonObject=new JSONObject();
		jsonObject.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
		jsonObject.put(ResponseMessage.DESCRIPTION, e.getMessage());
		jsonObject.put(ResponseMessage.STATUS, ResponseMessage.API_STATUS_SUCCESS);
		finalResponse=jsonObject.toString();
	}
	
		return finalResponse;
	}

	
	
}
