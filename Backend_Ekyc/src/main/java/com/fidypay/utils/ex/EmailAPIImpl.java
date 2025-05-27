package com.fidypay.utils.ex;

import java.io.IOException;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.fidypay.utils.constants.ResponseMessage;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


@Service
public class EmailAPIImpl {

	
	
	public String sendEmail(String email, String otp) throws IOException {

		String requestString = "{\r\n" + "  \"email\": \"" + email + "\",\r\n" + "  \"otp\": \"" + otp + "\"\r\n" + "}";

		OkHttpClient client = new OkHttpClient().newBuilder().build();
		MediaType mediaType = MediaType.parse("application/json");
		RequestBody body = RequestBody.create(mediaType, requestString);
		Request request = new Request.Builder()
				.url("https://api.fidypay.com/notification/emailNotification/sendEmailNotification")
				.method("POST", body).addHeader("accept", "*/*").addHeader("Content-Type", "application/json").build();
		Response response = client.newCall(request).execute();

		String res = response.body().string();

		return "Success";
	}

	public String sendEmailEKycWorkFlow(String email, String businessName, String link, String userName)
			throws IOException {

		String requestString = "{\r\n" + "    \"businessName\": \"" + businessName + "\",\r\n" + "    \"email\": \""
				+ email + "\",\r\n" + "    \"link\": \"" + link + "\",\r\n" + "    \"optional1\": \"" + userName
				+ "\"\r\n" + "}";

		OkHttpClient client = new OkHttpClient().newBuilder().build();
		MediaType mediaType = MediaType.parse("application/json");
		RequestBody body = RequestBody.create(mediaType, requestString);
		Request request = new Request.Builder()
				.url("https://api.fidypay.com/notification/emailNotification/sendEmailNotificationForEkycWorkFlow")
				.method("POST", body).addHeader("accept", "*/*").addHeader("Content-Type", "application/json").build();
		Response response = client.newCall(request).execute();

		String res = response.body().string();

		return "Success";
	}
	
	
	
	public String sendEmailEKycWorkFlowScheduler(String email, String businessName, String link, String userName)
			throws IOException {

		String requestString = "{\n"
				+ "  \"accountManagerName\": \""+userName+"\",\n"
				+ "  \"businessName\": \""+businessName+"\",\n"
				+ "  \"email\": \""+email+"\",\n"
				+ "  \"link\": \"ABC\",\n"
				+ "  \"optional1\": \""+link+"\",\n"
				+ "  \"optional2\": \"scdcdv\",\n"
				+ "  \"optional3\": \"cdvvbbfss\",\n"
				+ "  \"otp\": \"1221\"\n"
				+ "}";

		OkHttpClient client = new OkHttpClient().newBuilder().build();
		MediaType mediaType = MediaType.parse("application/json");
		RequestBody body = RequestBody.create(mediaType, requestString);
		Request request = new Request.Builder()
				.url("https://api.fidypay.com/notification/emailNotification/sendEmailNotificationForEkycWorkFlow")
				.method("POST", body).addHeader("accept", "*/*").addHeader("Content-Type", "application/json").build();
		Response response = client.newCall(request).execute();

		String res = response.body().string();

		return "Success";
	}

	
	public static String sendEmailForBulkEkycUsers(String mail,String businessName,String fileUrl,String logo) throws Exception {
		String finalResponse=null;
		JSONObject object=new JSONObject();
		try {
		
			
//			OkHttpClient client = new OkHttpClient().newBuilder()
//					  .build();
//					MediaType mediaType = MediaType.parse("application/json");
//					RequestBody body = RequestBody.create(mediaType, "{ \"accountManagerName\": \"String\", \"businessName\": \""+businessName+"\", \"email\": \""+mail+"\", \"link\": \""+fileUrl+"\", \"optional1\": \"String\", \"optional2\": \"String\", \"optional3\": \"String\", \"otp\": \"String\"}");
//					Request request = new Request.Builder()
//					  .url("https://api.fidypay.com/notification/emailNotification/sendEmailForBulkMandateCreation")
//					  .method("POST", body)
//					  .addHeader("accept", "*/*")
//					  .addHeader("Content-Type", "application/json")
//					  .build();
//					Response response = client.newCall(request).execute();
			
			
			OkHttpClient client = new OkHttpClient().newBuilder()
					  .build();
					MediaType mediaType = MediaType.parse("application/json");
					RequestBody body = RequestBody.create(mediaType, "{ \"businessName\": \""+businessName+"\", \"email\": \""+mail+"\", \"link\": \""+fileUrl+"\", \"optional1\": \""+logo+"\"}");
					Request request = new Request.Builder()
					  .url("https://api.fidypay.com/notification/emailNotification/sendEmailForBulkKycCreation")
					  .method("POST", body)
					  .addHeader("accept", "*/*")
					  .addHeader("Content-Type", "application/json")
					  .build();
					Response response = client.newCall(request).execute();
			
			
			
		object.put(ResponseMessage.CODE, ResponseMessage.SUCCESS);
		object.put(ResponseMessage.DESCRIPTION, "Email send Successfully");
		object.put(ResponseMessage.STATUS, ResponseMessage.STATUS_SUCCESS);
		finalResponse=object.toString();


		} catch (Exception e) {
		object.put(ResponseMessage.CODE, ResponseMessage.FAILED);
		object.put(ResponseMessage.DESCRIPTION, e.getMessage());
		object.put(ResponseMessage.STATUS, ResponseMessage.STATUS_FAILED);
		finalResponse=object.toString();
		}
		return finalResponse;
		}

}
