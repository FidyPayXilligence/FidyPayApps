package com.fidypay.config;

import org.json.JSONArray;
import org.json.JSONObject;

public class Test2 {

	public static void main(String[] args)  throws Exception {
//		String jsonString = "[ { \"serviceName\" : \"PAN\", \"description\" : \"Individual PAN\", \"stepName\" : \"PAN\", \"flag\" : \"1\", \"stepId\" : \"1\", \"apis\" : [ { \"apiname\" : \"PAN Card Basic Verify\", \"serviceId\" : \"356\", \"flag\" : \"1\", \"title\" : \"Pan Number\", \"charge\" : \"0.0118\" }, { \"apiname\" : \"OCR PAN\", \"serviceId\" : \"458\", \"flag\" : \"1\", \"title\" : \"Pan Image\", \"charge\" : \"5.9\" } ] }, { \"serviceName\" : \"GST\", \"description\" : \"NA\", \"stepName\" : \"GST\", \"flag\" : \"0\", \"stepId\" : \"2\", \"apis\" : [ { \"apiname\" : \"GST VERIFY USING PAN\", \"serviceId\" : \"463\", \"flag\" : \"0\", \"title\" : \"Pan Number\", \"charge\" : \"5.9\" }, { \"apiname\" : \"GST Number\", \"serviceId\" : \"350\", \"flag\" : \"0\", \"title\" : \"GST Number(Basic Search)\", \"charge\" : \"12.98\" } ] }, { \"serviceName\" : \"AADHAAR\", \"description\" : \"NA\", \"stepName\" : \"AADHAAR\", \"flag\" : \"0\", \"stepId\" : \"3\", \"apis\" : [ { \"apiname\" : \"OCR AADHAR\", \"serviceId\" : \"459\", \"flag\" : \"0\", \"title\" : \"Aadhar Image\", \"charge\" : \"5.9\" } ] }, { \"serviceName\" : \"ACCOUNT VERIFICATION\", \"description\" : \"NA\", \"stepName\" : \"ACCOUNT VERIFICATION\", \"flag\" : \"0\", \"stepId\" : \"4\", \"apis\" : [ { \"apiname\" : \"Bank Account Verification Penny Less\", \"serviceId\" : \"416\", \"flag\" : \"0\", \"title\" : \"Penny Less\", \"charge\" : \"1.0\" } ] } ]";
//
//		JSONArray jsonArray=new JSONArray(jsonString);
//		
//		System.out.println("jsonArray: "+jsonArray);
//		
//		
//		JSONArray array=new JSONArray();
//		
//		for(int i=0; i<jsonArray.length(); i++) {
//			
//			org.json.JSONObject aa = (org.json.JSONObject) jsonArray.get(i);
//			
//			
//			
//			String serviceName=aa.getString("serviceName");
//			String description=aa.getString("description");
//			String stepName=aa.getString("stepName");
//			String flag=aa.getString("flag");
//			String stepId=aa.getString("stepId");
//			
//			System.out.println("serviceName: "+serviceName);
//			System.out.println("description: "+description);
//			System.out.println("stepName: "+stepName);
//			System.out.println("flag: "+flag);
//			System.out.println("stepId: "+stepId);
//			
//			
//			JSONObject jsonObject=new JSONObject();
//			jsonObject.put("serviceName", serviceName);
//			jsonObject.put("description", description);
//			jsonObject.put("stepName", stepName);
//			jsonObject.put("flag", flag);
//			jsonObject.put("stepId", stepId);
//			
//			
//			
//			
//			System.out.println("--------------------------------------------------- ");
//			
//			JSONArray jsonArray2=aa.getJSONArray("apis");
//			
//			System.out.println("jsonArray2: "+jsonArray2);
//			
//			JSONArray apis=new JSONArray();
//			
//			for(int j=0; j<jsonArray2.length(); j++) {
//				
//				org.json.JSONObject bb = (org.json.JSONObject) jsonArray2.get(j);
//				
//				String apiname=bb.getString("apiname");
//				String serviceId=bb.getString("serviceId");
//				String flag2=bb.getString("flag");
//				String title=bb.getString("title");
//				String charge=bb.getString("charge");
//				
//				System.out.println("apiname: "+apiname);
//				System.out.println("serviceId: "+serviceId);
//				System.out.println("flag2: "+flag2);
//				System.out.println("title: "+title);
//				System.out.println("charge: "+charge);
//				
//				
//				JSONObject jsonObject2=new JSONObject();
//				jsonObject2.put("apiname", apiname);
//				jsonObject2.put("serviceId", serviceId);
//				jsonObject2.put("flag", flag2);
//				jsonObject2.put("title", title);
//				jsonObject2.put("charge", charge);
//				
//				apis.put(jsonObject2);
//				
//				jsonObject.put("apis", apis);
//				
//			}
//			
//			array.put(jsonObject);
//			System.out.println("============================================== ");
//			
//			}		
//		
//		
//		System.out.println("array: "+array);
//    }
//
//	
		
//		 String jsonWithBackslashes = "{\"type\":\"res\",\"batch_id\":\"65e675a213f3f9ed0ce94d95\",\"mandate_list\":[{\"emandate_id\":\"64a7e87b22ff0183f6da04b6\",\"umrn\":\"HDFC7010707230029637\",\"status\":\"RES_Accepted\",\"error_code\":\"NA\",\"error_description\":\"NA\"},{\"emandate_id\":\"658d3303262cc85ca90af7da\",\"umrn\":\"HDFC6000000020482270\",\"status\":\"RES_Accepted\",\"error_code\":\"NA\",\"error_description\":\"NA\"},{\"emandate_id\":\"65d057ac2e17dc1d7661df43\",\"umrn\":\"SBIN7021702245006278\",\"status\":\"ACK_Accepted\",\"error_code\":\"NA\",\"error_description\":\"NA\"}]}";
//
//	        String jsonWithoutBackslashes = jsonWithBackslashes.replaceAll("\\\\", "");
//	        System.out.println(jsonWithoutBackslashes);

		String batchId="660b5fa273645052a1504185";
		String mandateId="657001a432a4c28c93389308";
		
		String merchatTrxnRefId="FP"+batchId.substring(batchId.length() - 6)+""+mandateId.substring(mandateId.length() - 6);
		
		System.out.println("merchatTrxnRefId: "+merchatTrxnRefId);
		
	}
}
