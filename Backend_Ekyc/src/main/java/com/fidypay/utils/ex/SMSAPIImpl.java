package com.fidypay.utils.ex;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SMSAPIImpl {

	public String registrationOTP(String mobiles, String merchantName, String otp) throws IOException {

		String requestString = "{ \"mobile\": \"" + mobiles + "\", \"otp\": \"" + otp + "\", \"p1\": \"" + merchantName
				+ "\", \"p2\": \"0\", \"p3\": \"0\"}";

		OkHttpClient client = new OkHttpClient().newBuilder().build();
		MediaType mediaType = MediaType.parse("application/json");
		RequestBody body = RequestBody.create(mediaType, requestString);
		Request request = new Request.Builder().url("https://api.fidypay.com/notification/sendNotification")
				.method("POST", body).addHeader("accept", "*/*").addHeader("smsToken", "3708302484886346196")
				.addHeader("Content-Type", "application/json").build();
		Response response = client.newCall(request).execute();
		String results = response.body().string();

		return results;
	}

	public String workFlowLink(String mobiles, String businessName, String link) throws IOException {

		String requestString = "{\r\n" + "    \"mobile\": \"" + mobiles + "\",\r\n" + "    \"otp\": \"string\",\r\n"
				+ "    \"p1\": \"" + businessName + "\",\r\n" + "    \"p2\": \"" + link + "\",\r\n"
				+ "    \"p3\": \"string\"\r\n" + "}";

		OkHttpClient client = new OkHttpClient().newBuilder().build();
		MediaType mediaType = MediaType.parse("application/json");
		RequestBody body = RequestBody.create(mediaType, requestString);
		Request request = new Request.Builder().url("https://api.fidypay.com/notification/sendNotification")
				.method("POST", body).addHeader("accept", "*/*").addHeader("smsToken", "0122161208934346164")
				.addHeader("Content-Type", "application/json").build();
		Response response = client.newCall(request).execute();
		String results = response.body().string();

		return results;
	}

}
