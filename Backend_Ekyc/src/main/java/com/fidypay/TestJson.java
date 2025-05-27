package com.fidypay;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fidypay.ServiceProvider.Karza.ESignServices;
import com.fidypay.utils.constants.ResponseMessage;

public class TestJson {

	public static void main(String[] args) {
		// Sample JSON string
		String jsonString = "{\r\n" + "    \"decentroTxnId\": \"556E0FD627764775829CA47A38E8BF6B\",\r\n"
				+ "    \"status\": \"FAILURE\",\r\n" + "    \"responseCode\": \"S00000\",\r\n"
				+ "    \"message\": \"The email address was verified successfully.\",\r\n" + "    \"data\": {\r\n"
				+ "        \"isDisposable\": false,\r\n" + "        \"isPublic\": true,\r\n"
				+ "        \"account\": {\r\n" + "            \"username\": \"uvyashverma\",\r\n"
				+ "            \"isGibberish\": false\r\n" + "        }\r\n" + "    },\r\n"
				+ "    \"responseKey\": \"success_public_email_found\"\r\n" + "}";

		// Parse the JSON string into a JSONObject
		JSONObject jsonObject = new JSONObject(jsonString);

		// Replace the key 'decentroTxnId' with 'merchantTrxnRefId'
		if (jsonObject.has("decentroTxnId")) {
			Object value = jsonObject.remove("decentroTxnId"); // Get the value and remove the key
			jsonObject.put("merchantTrxnRefId", value); // Add the new key with the same value
		}
		
		if(jsonObject.has("isPublic")) {
			jsonObject.remove("isPublic");
		}

		// Print the updated JSON object
		System.out.println("Updated JSON: " + jsonObject.toString(4)); // Pretty print with indentation
	}

	/*
	 * public static void main(String[] args) throws Exception {
	 * 
	 * String txnDateString = "--------------else2------1--------"; // String
	 * txnDateString = "--------------else2------1--------";
	 * 
	 * SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); Date date
	 * = sdf.parse(txnDateString);
	 * 
	 * Timestamp trxnDate = new Timestamp(date.getTime());
	 * 
	 * // System.out.println(trxnDate);
	 * 
	 * String dobRegex = "(0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[1,2])-(19|20)\\d{2}";
	 * 
	 * String dlRegex = "^[a-zA-Z\\d-]+$";
	 * 
	 * String dlNumber = "GJ1920160000969"; // 14-10-1983 String dobString =
	 * "25-07-1999";
	 * 
	 * if (!dlNumber.matches(dlRegex)) { System.out.println("Not matched"); } else {
	 * System.out.println("matched"); }
	 * 
	 * Map<String, Object> map = new HashMap<>();
	 * 
	 * String jsonString = "{\r\n" + "   \"source\": \"FIDYPAY001\",\r\n" +
	 * "   \"channel\": \"api\",\r\n" + "   \"terminalId\": \"FIDYPAY001-001\",\r\n"
	 * + "   \"extTransactionId\": \"FIDYPAY4cb29219a0a945dc927\",\r\n" +
	 * "   \"upiId\": \"uvyashverma@okicici\",\r\n" +
	 * "   \"checksum\": \"9b9143664266dc58fac54e8e44993b18ebf223bd92cb1f021530937369ea8566\",\r\n"
	 * + "   \"status\": \"SUCCESS\",\r\n" + "   \"txnType\": \"VALADD\",\r\n" +
	 * "   \"sid\": \"FIDYPAY001-001\",\r\n" + "   \"data\": [\r\n" + "      {\r\n"
	 * + "         \"customerName\": \"YASH KUMAR VERMA\",\r\n" +
	 * "         \"respCode\": \"0\",\r\n" +
	 * "         \"respMessge\": \"SUCCESS\"\r\n" + "      }\r\n" + "   ],\r\n" +
	 * "   \"transactionList\": []\r\n" + "}";
	 * 
	 * map = validMap(jsonString);
	 * 
	 * // String respoString = JSONValue.toJSONString(map); // //
	 * System.out.println(respoString);
	 * 
	 * }
	 */
	public static Map<String, Object> validMap(String jsonString) {

		Map<String, Object> map = new HashMap<>();

//		LOGGER.info("apiResponse: {}", jsonString);

		JSONObject resultJsonObject = new JSONObject(jsonString);

		JSONObject responseJson = new JSONObject();

		String status = resultJsonObject.getString("status");

		String upiId = resultJsonObject.getString("upiId");

		if (status.equals("SUCCESS")) {

			JSONArray dataArray = resultJsonObject.getJSONArray("data");

			JSONObject dataObject = dataArray.getJSONObject(0);

			String customerName = dataObject.getString("customerName");

			map = ESignServices.setResponse(ResponseMessage.SUCCESS, ResponseMessage.API_STATUS_SUCCESS,
					ResponseMessage.DATA_SUCCESS);

			if (customerName.equals("0000")) {
				responseJson.put("vpa", "");
				responseJson.put("vpaStatus", "InValid");
				map.put(ResponseMessage.DATA, responseJson.toMap());
				return map;
			}

			responseJson.put("vpa", upiId);
			responseJson.put("name", customerName);
			responseJson.put("Ifsc", "NA");
			responseJson.put("accountType", "NA");
			responseJson.put("vpaStatus", "Valid");
			map.put(ResponseMessage.DATA, responseJson.toMap());
			return map;
		} else {
			map = ESignServices.setResponse(ResponseMessage.SUCCESS, ResponseMessage.API_STATUS_SUCCESS,
					ResponseMessage.DATA_SUCCESS);
			responseJson.put("vpa", "");
			responseJson.put("vpaStatus", "InValid");
			map.put(ResponseMessage.DATA, responseJson.toMap());
		}

		return map;
	}

}
