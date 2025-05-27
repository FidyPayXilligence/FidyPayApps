package com.fidypay.config;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Test {

	public static void main(String[] args) {

//			
		String respApi = save("0", "xfghyu76543", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "success",
				"xfghyu76543", "NA", String.valueOf(10), "9340851619", "xfghyu76543", "NA", "cash");
//			
			System.out.println("apiResponse------------->" + respApi);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	}

	public static String save(String adult_count, String bill_no, String child_count, String camera_count,
			String vcamera_count, String vcamera_cost, String adult_cost, String child_cost, String camera_cost,
			String vehicle_count, String vehicle_cost, String total_cost, String status, String transaction_id,
			String name, String amount, String mobileNumber, String rrn, String email, String pcOptionName) {

		String apiResponse = "";
		try {
			String token = "uiGrs8Cx42jg1Mif78uxhanN2yaZvB9OM3LqWpYbplvTRTwdkwse5AnozKJ7LgPQ1707390466";

			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("text/plain");
			RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
					.addFormDataPart("bill_no", bill_no).addFormDataPart("adult_count", adult_count)
					.addFormDataPart("child_count", child_count).addFormDataPart("camera_count", camera_count)
					.addFormDataPart("vcamera_count", vcamera_count).addFormDataPart("adult_cost", adult_cost)
					.addFormDataPart("child_cost", child_cost).addFormDataPart("camera_cost", camera_cost)
					.addFormDataPart("vcamera_cost", vcamera_cost).addFormDataPart("vehicle_count", vehicle_count)
					.addFormDataPart("vehicle_cost", vehicle_cost).addFormDataPart("total_cost", total_cost)
					.addFormDataPart("status", "success").addFormDataPart("transaction_id", transaction_id)
					.addFormDataPart("transaction_data",
							"{'status':1,'url':'https://localhost/tanhoda/','data':{'name_on_card':'NA','bank_ref_num':'"
									+ rrn
									+ "','udf3':'aaaa','hash':'2nWbK87MIvxZC9QAUPBSpTDRFHoW9B05GEdgfXNZtVgiINshR5eGY6CJmD64VKLs1685000993','firstname':'"
									+ name + "','net_amount_debit':'" + amount
									+ "','payment_source':'Yes bank','surl':'https://localhost/tanhoda/','error_Message':'PGS10001-Success','issuing_bank':'NA','cardCategory':'NA','phone':'"
									+ mobileNumber
									+ "','easepayid':'TO4B9RCGRH','cardnum':'NA','key':'NA','udf8':'','unmappedstatus':'NA','PG_TYPE':'NA','addedon':'2023-05-19 07:46:19','cash_back_percentage':'0.0','status':'success','udf1':'aaaa','merchant_logo':'NA','udf6':'','udf10':'','txnid':'"
									+ transaction_id
									+ "','productinfo':'NA','furl':'https://localhost/tanhoda/','card_type':'"
									+ pcOptionName + "','amount':'" + amount
									+ "','udf2':'aaaa','udf5':'aaaa','mode':'DC','udf7':'','error':'NA','udf9':'','bankcode':'NA','deduction_percentage':'0.0','email':'"
									+ email + "','udf4':'aaaa'}}")
					.build();
			Request request = new Request.Builder().url("http://tnhorticulture.tn.gov.in/cfs-2024/api/save-pos-data").method("POST", body).addHeader("Authorization", token)
					.build();

			Response response = client.newCall(request).execute();
			apiResponse = response.body().string();
			System.out.println(" apiResponse is :" + apiResponse);

		}

		catch (Exception e) {
			e.printStackTrace();
		}
		return apiResponse;

	}
}
