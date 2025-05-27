package com.fidypay;

import org.json.JSONObject;

public class Test {

	public static void main(String[] args) {
		String tob = "Geeks@!Sportal10";
		String check = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[-!*@#$%^&+=])(?=\\S+$).*$";

		String response = "{\"singleMandate\":{\"view\":\"0\",\"edit\":\"0\",\"none\":\"0\"},\"bulkMandate\":{\"view\":\"0\",\"edit\":\"0\",\"none\":\"0\"},\"singleDebit\":{\"view\":\"string\",\"edit\":\"0\",\"none\":\"string\"},\"bulkDebit\":{\"view\":\"0\",\"edit\":\"0\",\"none\":\"0\"},\"mandateStatus\":{\"view\":\"0\",\"edit\":\"0\",\"none\":\"0\"},\"debitCancel\":{\"view\":\"0\",\"edit\":\"0\",\"none\":\"0\"},\"debirPause\":{\"view\":\"0\",\"edit\":\"0\",\"none\":\"0\"},\"debitResume\":{\"view\":\"0\",\"edit\":\"0\",\"none\":\"0\"},\"bulkMandateReport\":{\"view\":\"0\",\"edit\":\"0\",\"none\":\"0\"},\"bulkDebitReport\":{\"view\":\"0\",\"edit\":\"0\",\"none\":\"0\"},\"transactionReport\":{\"view\":\"0\",\"edit\":\"0\",\"none\":\"0\"}}";

		System.out.println(response.replaceAll("\\\\", ""));
		
//		boolean result = tob.matches(check);
//		if (result) {
//			System.out.println("Given amount is valid");
//			System.out.println(tob.length());
//		} else {
//			System.out.println("Given amount is not valid");
//		}

	}

	public static JSONObject convert(String response) {

		JSONObject obj = new JSONObject(response);

		return obj;
	}

}
