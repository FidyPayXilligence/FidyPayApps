package com.fidypay.utils.constants;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import com.fidypay.entity.EkycWorkflow;
import com.fidypay.repo.EkycWorkflowRepository;
import com.fidypay.request.BulkEkycUsersRequest;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

public class ValidateBulkEKyc {
	
	  @Autowired
      private  EkycWorkflowRepository ekycWorkflowRepository;

	private static final Logger LOGGER = LoggerFactory.getLogger(ValidateBulkEKyc.class);

	public final static String MERCHANT_TRXN_REF_ID = "^[a-zA-Z0-9]{1,20}$";
	public final static String CATEGORY_CODE = "Loan Instalment Payment|Loan Amount Security|Small Value Mandate";
	public final static String FREQUENCY = "Adhoc|Monthly|Quarterly|Yearly";
	public final static String FIRST_COLLECTION_DATE = "([0-9]{2})-([0-9]{2})-([0-9]{4})";
	public final static String LAST_COLLECTION_DATE = "([0-9]{2})-([0-9]{2})-([0-9]{4})";
	public final static String DEBIT_TYPE = "FIXED_AMOUNT|MAXIMUM_AMOUNT";
	public final static String PRINCIPAL_AMOUNT = "^(?!(0))[0-9]{2,7}([.][0-9]{1,2})?$";
	public final static String COLLECTION_AMOUNT = "^(?!(0))[0-9]{2,7}([.][0-9]{1,2})?$";
	public final static String CUSTOMER_NAME = "^[a-zA-Z0-9\\\\s]+$";
	public final static String CUSTOMER_EMAIL = "^(?=.{1,50}@)[a-zA-Z0-9-_]+(\\.[a-zA-Z0-9-_]+)*@[^-][a-zA-Z0-9]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{2,})$";
	public final static String CUSTOMER_MOBILE = "^\\(?([0-9]{3})\\)?[-.\\s]?([0-9]{3})[-.\\s]?([0-9]{4})$";
//	public final static String ACCOUNT_HOLDER_NAME = "^[a-zA-Z]+( [a-zA-Z_]+)*$";
	public final static String ACCOUNT_NUMBER = "^[0-9]{9,17}$";
	public final static String ACCOUNT_TYPE = "SAVINGS|CURRENT";
	public final static String AUTH_TYPE = "NetBanking|DebitCard|EsignOtp";

	
		
//	public static  Map<String, String> validate(BulkEkycUsersRequest request) throws ParseException {
//		LOGGER.info("inside validate");
//		HashMap<String, String> map = new HashMap<String, String>();
//		try {
//
//			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
//			Date date = new Date();
//			Date firstDate = null;
//			Date lastDate = null;
//			Date currentDate = simpleDateFormat.parse(simpleDateFormat.format(date));
//
//
//
//			if (!(request.getUserEmail().matches(CUSTOMER_EMAIL))) {
//				map.put("customerEmail", "Please enter valid customerEmail.");
//			}
//
//			if (!(request.getUserMobile().matches(CUSTOMER_MOBILE))) {
//				map.put("customerMobile", "Please enter valid 10 digit mobile number.");
//			}
//
//		
//			if (!(request.getUserName().matches(CUSTOMER_NAME))) {
//				map.put("userName", "Please enter valid userName.");
//			}
//			
//
//		} catch (Exception e) {
//		
//		}
//		return map;
//	}


	
	public  JSONArray validate(BulkEkycUsersRequest request) throws ParseException {
		LOGGER.info("inside validate");
//		List<String> list=new ArrayList<>();
		//HashMap<String, String> map = new HashMap<String, String>();
		JSONArray list=null;
		try {
			
			JSONArray array=new JSONArray();
			
			LOGGER.info("request.getUserEmail(): "+request.getUserEmail());
			LOGGER.info("request.getUserMobile(): "+request.getUserMobile());
			LOGGER.info("request.getUserName(): "+request.getUserName());


			if (!(request.getUserEmail().matches(CUSTOMER_EMAIL))) {
				LOGGER.info("Inside Email: ");
				
				//map.put("customerEmail", "Please enter valid customerEmail.");
				
				array.put("Please enter valid customerEmail");
			}

			if (!(request.getUserMobile().matches(CUSTOMER_MOBILE))) {
				LOGGER.info("Inside Mobile: ");
				//map.put("customerMobile", "Please enter valid 10 digit mobile number.");
				
				array.put("Please enter valid 10 digit mobile number");
			}

		
			if (!(request.getUserName().matches(CUSTOMER_NAME))) {
				LOGGER.info("Inside Customer Name: ");
				//map.put("userName", "Please enter valid userName.");
				
				array.put("Please enter valid userName");
			}
			
			
			return array;

		} catch (Exception e) {
		  e.printStackTrace();
		}
		//return map;
		return list;
	}

	
		
	public static String formatDate(String inDate) throws ParseException {
		LOGGER.info("inside formate Date");
		if (inDate.isEmpty() || inDate == null) {
			return "NA";
		}
		SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
		SimpleDateFormat outputFormat = new SimpleDateFormat("YYYY-MM-dd");
		Date date = inputFormat.parse(inDate);
		String output = outputFormat.format(date);
		return output;
	}

	public static String convertWithIteration(Map<String, String> map) {
		StringBuilder mapAsString = new StringBuilder("{");
		for (String key : map.keySet()) {
			mapAsString.append(key + " = " + map.get(key) + ", ");
		}
		mapAsString.delete(mapAsString.length() - 1, mapAsString.length()).append("}");
		return mapAsString.toString();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<BulkEkycUsersRequest> convertToModel(MultipartFile file,
			Class<BulkEkycUsersRequest> responseType) {

		List<BulkEkycUsersRequest> models;

		try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

			CsvToBean<?> csvToBean = new CsvToBeanBuilder(reader).withType(responseType)
					.withIgnoreLeadingWhiteSpace(true).withIgnoreEmptyLine(true).build();

			models = (List<BulkEkycUsersRequest>) csvToBean.parse();

		} catch (Exception ex) {

			throw new IllegalArgumentException(ex.getCause().getMessage());
		}
		return (List<BulkEkycUsersRequest>) models;
	}
	
	
	
	public static double validateAmount(String amount) {
		if (!amount.equals("")) {
			return 0;
		}
		
		if (!amount.matches(PRINCIPAL_AMOUNT)) {
			return Double.valueOf(0);
		}
		return 0;
	}

	public static boolean isRowEmpty(BulkEkycUsersRequest request) {
		boolean isEmpty = false;

		if ((request.getSno().isEmpty() || request.getSno().equals(" "))
				&& (request.getUserEmail().isEmpty() || request.getUserEmail().equals(" "))
				&& (request.getUserMobile().isEmpty() || request.getUserMobile().equals(" "))
				&& (request.getUserName().isEmpty() || request.getUserName().equals(" "))) {

			isEmpty = true;
		}

		return isEmpty;
	}
	
	
	public static String validateIsEmpty(String value) {
		if (value.isEmpty()) {
			return "NA";
		}
		return value;
	}
}
