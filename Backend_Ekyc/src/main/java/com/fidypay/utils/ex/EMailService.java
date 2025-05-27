package com.fidypay.utils.ex;

import java.util.List;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fidypay.request.KycFailedRequest;
import com.fidypay.utils.constants.ResponseMessage;



@Service
public class EMailService {

	private final RestTemplate restTemplate = new RestTemplate();
	
	
	
	//KYC --------------------------------------------Rohit Kanojiya----------------------------------------
	
	public  String sendEmailForKycVerificationSuccess(String mail,String customerName,String logo,String businessName,String verificationDate) throws Exception {
		String finalResponse=null;
		JSONObject object=new JSONObject();
	try {
		HttpHeaders headers = new HttpHeaders();
		            headers.setContentType(MediaType.APPLICATION_JSON);

		            JSONObject requestBody = new JSONObject();
		            requestBody.put("customerName", customerName);
		            requestBody.put("email", mail);
		            requestBody.put("logo", logo);
		            requestBody.put("merchantBusinessName", businessName);
		            requestBody.put("verificationDate", verificationDate);

		            HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

		            ResponseEntity<String> responseEntity = restTemplate.exchange(
		                    "https://api.fidypay.com/notification/emailNotification/sendEmailForKycVerificationSuccess",
		                    HttpMethod.POST,
		                    entity,
		                    String.class);

		            finalResponse = responseEntity.getBody();
				
		
	} catch (Exception e) {
		object.put(ResponseMessage.CODE, ResponseMessage.FAILED);
		object.put(ResponseMessage.DESCRIPTION, e.getMessage());
		object.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
		finalResponse=object.toString();
	}
	
		return finalResponse;
	}

	
	
	 public String sendEmailForReKycVerification(String customerName, String email, String lastDate, String link, String logo, String merchantBusinessName) {
	        try {
	            HttpHeaders headers = new HttpHeaders();
	            headers.setContentType(MediaType.APPLICATION_JSON);

	            JSONObject requestBody = new JSONObject();
	            requestBody.put("customerName", customerName);
	            requestBody.put("email", email);
	            requestBody.put("lastDate", lastDate);
	            requestBody.put("link", link);
	            requestBody.put("logo", logo);
	            requestBody.put("merchantBusinessName", merchantBusinessName);

	            System.out.println("requestBody: "+requestBody.toString());
	            
	            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody.toString(), headers);

	            ResponseEntity<String> responseEntity = restTemplate.exchange(
	                    "https://api.fidypay.com/notification/emailNotification/sendEmailForReKyc",
	                    HttpMethod.POST,
	                    requestEntity,
	                    String.class);

	            return responseEntity.getBody();
	        } catch (Exception e) {
	            // Handle exception
	            e.printStackTrace();
	            return "Error occurred: " + e.getMessage();
	        }
	    }
	
	 
	 public String sendEmailForKycVerificationFailed(String customerName, String email, String link, String logo, String merchantBusinessName,String failedReason) {
	        try {
	            HttpHeaders headers = new HttpHeaders();
	            headers.setContentType(MediaType.APPLICATION_JSON);
	            headers.set("accept", "*/*");

	            JSONObject requestBody = new JSONObject();
	            requestBody.put("customerName", customerName);
	            requestBody.put("email", email);
	            requestBody.put("link", link);
	            requestBody.put("logo", logo);
	            requestBody.put("merchantBusinessName", merchantBusinessName);
	            requestBody.put("failedReason", failedReason);
	
	            System.out.println("requestBody: "+requestBody.toString());

	            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody.toString(), headers);

	            ResponseEntity<String> responseEntity = restTemplate.exchange(
	            	    "https://api.fidypay.com/notification/emailNotification/sendEmailForKycVerificationfailed",
	                    HttpMethod.POST,
	                    requestEntity,
	                    String.class);

	            return responseEntity.getBody();
	        } catch (Exception e) {
	            // Handle exception
	            e.printStackTrace();
	            return "Error occurred: " + e.getMessage();
	        }
	    }
	 
	 
	 public String sendEmailForKycVerificationFailed2(String customerName, String email, String link, String logo, String merchantBusinessName, List<KycFailedRequest> failedReason) {
	        RestTemplate restTemplate = new RestTemplate();
	        String responseBody=null;
           try {
        	   HttpHeaders headers = new HttpHeaders();
   	        headers.setContentType(MediaType.APPLICATION_JSON);
   	        headers.set("accept", "*/*");

   	        String url = "https://api.fidypay.com/notification/emailNotification/sendEmailForKycVerificationfailed";

               JSONObject requestBody = new JSONObject();
               requestBody.put("customerName", customerName);
               requestBody.put("email", email);
               requestBody.put("link", link);
               requestBody.put("logo", logo);
               requestBody.put("merchantBusinessName", merchantBusinessName);
               requestBody.put("failedReason", failedReason);

               System.out.println("requestBody: "+requestBody.toString());
               
   	        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody.toString(), headers);

   	        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

   	        HttpStatus statusCode = responseEntity.getStatusCode();
   	         responseBody = responseEntity.getBody();

   	        return responseBody;

		} catch (Exception e) {
			responseBody=e.getMessage();
		}
	       return responseBody;
	    }
}
