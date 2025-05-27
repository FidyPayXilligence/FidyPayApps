package com.fidypay.utils.ex;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h1>GenerateTrxnRefId</h1>
 * 
 * The GenerateTrxnRefId Class implements for Generating Transaction Reference
 * Id
 * 
 * 
 * <p>
 * 
 * @author Jambopay Express
 * @version 1.0
 * @since 2015-04-03
 */
public class GenerateTrxnRefId {

	private static final Logger logger = LoggerFactory.getLogger(GenerateTrxnRefId.class);

	static String trxnRefId;

	/*
	 * public static void main(String[] args) { GenerateTrxnRefId generateTrxnRefId=
	 * new GenerateTrxnRefId(); String serviceName= "Mobile Recharge";
	 * generateTrxnRefId.getTranRefID("Mobile App", "Credit Card",serviceName); }
	 */

	static String s1 = null;
	static String trxnRefId1 = null;

	/*
	 * getTranRefID method Generate A unique Id For Every Transactions.
	 * 
	 * @param sourceName,pcOptionName,serviceName by this Parameter we are
	 * Generating a Unique Id
	 * 
	 * @return java.lang.String .
	 * 
	 * @throws Exception.
	 * 
	 * @see Exception
	 */

	public static String getAlphaString(int n) {
		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		StringBuilder sb = new StringBuilder(n);
		for (int i = 0; i < n; i++) {
			int index = (int) (AlphaNumericString.length() * Math.random());
			sb.append(AlphaNumericString.charAt(index));
		}
		return sb.toString();
	}
	
	
	public static String getAlphaNumericString(int n) {
		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789";
		StringBuilder sb = new StringBuilder(n);
		for (int i = 0; i < n; i++) {
			int index = (int) (AlphaNumericString.length() * Math.random());
			sb.append(AlphaNumericString.charAt(index));
		}
		return sb.toString();
	}

	public static String getNumericString(int n) {
		
		// chose a Character random from this String
		String AlphaNumericString = "0123456789";

		// create StringBuffer size of AlphaNumericString
		StringBuilder sb = new StringBuilder(n);

		for (int i = 0; i < n; i++) {

			// generate a random number between
			// 0 to AlphaNumericString variable length
			int index = (int) (AlphaNumericString.length() * Math.random());

			// add Character one by one in end of sb
			sb.append(AlphaNumericString.charAt(index));
		}

		return sb.toString();
	}

	public static String generateRandomStringRefId() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssSSS");
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String timestampDate = sdf.format(timestamp);
		String refId = "FP" + timestampDate;
		// LOGGER.info("refId : " + refId + " length " + refId.length());
		return refId;
	}

	public static String generateWalletRefId() {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssms");
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String timestampDate = sdf.format(timestamp);
		String refId = "FIDYW" + timestampDate;
		System.out.println("refId : " + refId + " length " + refId.length());
		return refId;
	}

	public static String getTranRefID(String sourceName, String pcOptionName, String serviceName) {

		logger.info("GenerateTrxnRefId.class--------------" + sourceName);
		logger.info("GenerateTrxnRefId.class--------------" + pcOptionName);
		logger.info("GenerateTrxnRefId.class--------------" + serviceName);

		switch (sourceName) {

		case "API":
			logger.info("from API");
			switch (pcOptionName) {
			case "Wallet":
				logger.info("from pcname--Wallet");
				switch (serviceName) {
				case "Banking":
					logger.info("Banking---API");
					s1 = "FPA";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Payment":
					logger.info("Payment---API");
					s1 = "FPA";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				default:
					char servicechar = serviceName.charAt(0);
					s1 = "WD" + servicechar;
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;
				}
			case "UPI":
				logger.info("from pcname--UPI");
				switch (serviceName) {
				case "Banking":
					logger.info("Banking---API");
					s1 = "FPA";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Payment":
					logger.info("Payment---API");
					s1 = "FPA";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				default:
					char servicechar = serviceName.charAt(0);
					s1 = "WD" + servicechar;
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;
				}
			default:
			}
		case "Web":
			logger.info("from web");
			switch (pcOptionName) {

			case "Credit Card":
				logger.info("from pcname--credit");
				switch (serviceName) {
				case "Mobile Recharge":
					logger.info("mobile recharge---WCM");
					s1 = "WCM";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Merchant Payment":
					logger.info("Merchant Payment---WCP");
					s1 = "WCP";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Dth Recharge":

					s1 = "WCD";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Bill Payment":

					s1 = "WCB";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "DataCard Recharge":

					s1 = "WCD";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Purchase Airtime":

					s1 = "WCA";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Dstv Recharge":

					s1 = "WCS";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Wallet":
					logger.info("Load Money---WCW");
					s1 = "WCW";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				default:

					char servicechar = serviceName.charAt(0);
					s1 = "WC" + servicechar;
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				}

				// break;
			case "Debit Card":
				switch (serviceName) {
				case "Mobile Recharge":

					s1 = "WDM";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Merchant Payment":
					logger.info("Merchant Payment---WDP");
					s1 = "WDP";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Dth Recharge":

					s1 = "WDD";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Bill Payment":

					s1 = "WDB";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "DataCard Recharge":

					s1 = "WDD";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Purchase Airtime":

					s1 = "WDA";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Dstv Recharge":

					s1 = "WDS";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Wallet":
					logger.info("Load Money---WCW");
					s1 = "WCW";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				default:

					char servicechar = serviceName.charAt(0);
					s1 = "WD" + servicechar;
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;
				}

			case "Net Banking":
				switch (serviceName) {
				case "Mobile Recharge":

					s1 = "WNM";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Merchant Payment":
					logger.info("Merchant Payment---WNP");
					s1 = "WNP";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Dth Recharge":

					s1 = "WND";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Bill Payment":

					s1 = "WNB";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "DataCard Recharge":

					s1 = "WND";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;
				default:

					char servicechar = serviceName.charAt(0);
					s1 = "WN" + servicechar;
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				}

			case "Mobile Money":
				logger.info("from pcname--Mobile Money");
				switch (serviceName) {
				case "Bill Payment":

					s1 = "WMB";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Purchase Airtime":

					s1 = "WMA";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Dstv Recharge":

					s1 = "WMS";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Wallet":
					logger.info("Load Money---WCW");
					s1 = "WCW";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				default:

					char servicechar = serviceName.charAt(0);
					s1 = "WM" + servicechar;
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				}

			case "SUPA KASH":

				logger.info("from pcname--Wallet Cash");
				switch (serviceName) {

				case "Wallet":
					logger.info("Load Money---WWW");
					s1 = "WWW";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				default:

					char servicechar = serviceName.charAt(0);
					s1 = "WW" + servicechar;
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				}

			case "Cash":

				logger.info("from pcname--Wallet Cash");
				switch (serviceName) {

				case "Merchant Payment":
					logger.info("Merchant Payment---WCP");
					s1 = "WCP";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Load Money":
					logger.info("Load Money---WWW");
					s1 = "WCL";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				default:

					char servicechar = serviceName.charAt(0);
					s1 = "WC" + servicechar;
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				}
				// pcname :- QR Code
			case "QR Code":
				logger.info("from pcname--QR Code");
				switch (serviceName) {

				case "Merchant Payment":
					logger.info("Merchant Payment---WQP");
					s1 = "WQP";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				default:

					char servicechar = serviceName.charAt(0);
					s1 = "WQR" + servicechar;
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				}

				// pcname :- Aadhar Pay
			case "Aadhaar Pay":
				logger.info("from pcname--Aadhar Pay");
				switch (serviceName) {

				case "Merchant Payment":
					logger.info("Merchant Payment---WAP");
					s1 = "WAP";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				default:

					char servicechar = serviceName.charAt(0);
					s1 = "WAR" + servicechar;
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				}

				// pcname= rokda
			case "Rokda":
				logger.info("from pcname--Rokda ");
				switch (serviceName) {

				case "Pay14D":
					logger.info("---Pay14D---");
					s1 = "WRL";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				default:

					char servicechar = serviceName.charAt(0);
					s1 = "WR" + servicechar;
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				}
			case "Cheque":

				logger.info("from pcname--Wallet Cash");
				switch (serviceName) {

				case "Load Money":
					logger.info("Load Money---WWW");
					s1 = "WKL";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				default:

					char servicechar = serviceName.charAt(0);
					s1 = "WK" + servicechar;
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				}
			case "split":

				logger.info("Load Money---WWW");
				s1 = "WKS";
				trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
				return trxnRefId1;

			}

			// end
			/////////// start of mobile
		case "Mobile":
			logger.info("from Mobile");
			switch (pcOptionName) {

			case "Credit Card":
				logger.info("from pcname--credit");
				switch (serviceName) {
				case "Mobile Recharge":
					logger.info("mobile recharge---WCM");
					s1 = "MCM";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Merchant Payment":
					logger.info("Merchant Payment---MCP");
					s1 = "MCP";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Dth Recharge":

					s1 = "MCD";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Bill Payment":

					s1 = "MCB";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "DataCard Recharge":

					s1 = "MCD";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Purchase Airtime":

					s1 = "MCA";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Dstv Recharge":

					s1 = "MCS";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Wallet":
					logger.info("Load Money---MCW");
					s1 = "MCW";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				default:

					char servicechar = serviceName.charAt(0);
					s1 = "MC" + servicechar;
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				}

				// break;
			case "Debit Card":
				switch (serviceName) {
				case "Mobile Recharge":

					s1 = "MDM";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Merchant Payment":
					logger.info("Merchant Payment---MDP");
					s1 = "MDP";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Dth Recharge":

					s1 = "MDD";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Bill Payment":

					s1 = "MDB";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "DataCard Recharge":

					s1 = "MDD";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Purchase Airtime":

					s1 = "MDA";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Dstv Recharge":

					s1 = "MDS";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Wallet":
					logger.info("Load Money---MDW");
					s1 = "MDW";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				default:

					char servicechar = serviceName.charAt(0);
					s1 = "MD" + servicechar;
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;
				}

			case "Net Banking":
				switch (serviceName) {
				case "Mobile Recharge":

					s1 = "MNM";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Merchant Payment":
					logger.info("Merchant Payment---MNP");
					s1 = "MNP";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Dth Recharge":

					s1 = "MND";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Bill Payment":

					s1 = "MNB";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "DataCard Recharge":

					s1 = "MND";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;
				default:

					char servicechar = serviceName.charAt(0);
					s1 = "MN" + servicechar;
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				}

			case "Mobile Money":
				logger.info("from pcname--Mobile Money");
				switch (serviceName) {

				case "Bill Payment":

					s1 = "MMB";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Purchase Airtime":

					s1 = "MMA";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Dstv Recharge":

					s1 = "MMS";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Wallet":
					logger.info("Load Money---MMW");
					s1 = "WCW";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;
				default:

					char servicechar = serviceName.charAt(0);
					s1 = "MM" + servicechar;
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				}

			case "SUPA KASH":

				logger.info("from pcname--Wallet Cash");
				switch (serviceName) {

				case "Merchant Payment":
					logger.info("Merchant Payment---WCP");
					s1 = "WCP";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Wallet":
					logger.info("Load Money---WWW");
					s1 = "MWW";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				default:

					char servicechar = serviceName.charAt(0);
					s1 = "MW" + servicechar;
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				}

				// pcname :- QR Code
			case "QR Code":
				logger.info("from pcname--QR Code");
				switch (serviceName) {

				case "Merchant Payment":
					logger.info("Merchant Payment---WQP");
					s1 = "MQP";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				default:

					char servicechar = serviceName.charAt(0);
					s1 = "MQ" + servicechar;
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				}

				// pcname :- Aadhar Pay
			case "Aadhaar Pay":
				logger.info("from pcname--Aadhar Pay");
				switch (serviceName) {

				case "Merchant Payment":
					logger.info("Merchant Payment---WAP");
					s1 = "MAP";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				default:

					char servicechar = serviceName.charAt(0);
					s1 = "MAR" + servicechar;
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				}
			}

			////////////////////// end of POS

			////////////////////// Start of Merchant Mobile-App

		case "Mobile App":
			switch (pcOptionName) {
			case "Credit Card":

				switch (serviceName) {
				case "Mobile Recharge":

					s1 = "ACM";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Merchant Payment":
					logger.info("Merchant Payment---ACP");
					s1 = "ACP";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Dth Recharge":

					s1 = "ACD";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Bill Payment":

					s1 = "ACB";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "DataCard Recharge":

					s1 = "ACD";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Purchase Airtime":

					s1 = "ACA";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Dstv Recharge":

					s1 = "ACS";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				default:

					char servicechar = serviceName.charAt(0);
					s1 = "AC" + servicechar;
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				}

			case "Debit Card":
				switch (serviceName) {
				case "Mobile Recharge":

					s1 = "ADM";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Merchant Payment":
					logger.info("Merchant Payment---ADP");
					s1 = "ADP";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Dth Recharge":

					s1 = "ADD";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Bill Payment":

					s1 = "ADB";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "DataCard Recharge":

					s1 = "ADD";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Purchase Airtime":

					s1 = "ADP";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Dstv Recharge":

					s1 = "ADS";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;
				default:

					char servicechar = serviceName.charAt(0);
					s1 = "AD" + servicechar;
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;
				}

				// pcname :- QR Code
			case "QR Code":
				logger.info("from pcname--QR Code");
				switch (serviceName) {

				case "Merchant Payment":
					logger.info("Merchant Payment---AQP");
					s1 = "AQP";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				default:

					char servicechar = serviceName.charAt(0);
					s1 = "AQ" + servicechar;
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				}

				// pcname :- Aadhar Pay
			case "Aadhaar Pay":
				logger.info("from pcname--Aadhar Pay");
				switch (serviceName) {

				case "Merchant Payment":
					logger.info("Merchant Payment---AAP");
					s1 = "AAP";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				default:

					char servicechar = serviceName.charAt(0);
					s1 = "AAR" + servicechar;
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				}

			case "Cash":
				switch (serviceName) {
				case "Mobile Recharge":

					s1 = "ACM";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Merchant Payment":
					logger.info("Merchant Payment---AWP");
					s1 = "AWP";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Dth Recharge":

					s1 = "ACD";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Bill Payment":

					s1 = "ACB";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "DataCard Recharge":

					s1 = "ACD";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Purchase Airtime":

					s1 = "ACP";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				default:
					char servicechar = serviceName.charAt(0);
					s1 = "AC" + servicechar;
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;
				}

			default:
				char serchar = serviceName.charAt(0);
				char pco = pcOptionName.charAt(0);
				s1 = "A" + pco + "" + serchar;
				logger.info("Inside default Case: adding " + s1);

				trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);

				trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
				return trxnRefId1;
			}

			// mobile app end
		case "Pos":
			switch (pcOptionName) {
			case "Credit Card":

				switch (serviceName) {
				case "Mobile Recharge":

					s1 = "PCM";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Merchant Payment":
					logger.info("Merchant Payment---PCP");
					s1 = "PCP";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Dth Recharge":

					s1 = "PCD";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Bill Payment":

					s1 = "PCB";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "DataCard Recharge":

					s1 = "PCD";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Purchase Airtime":

					s1 = "PCA";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Dstv Recharge":

					s1 = "PCS";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				default:

					char servicechar = serviceName.charAt(0);
					s1 = "PC" + servicechar;
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				}

			case "Debit Card":
				switch (serviceName) {
				case "Mobile Recharge":

					s1 = "PDM";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Merchant Payment":
					logger.info("Merchant Payment---PDP");
					s1 = "PDP";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Dth Recharge":

					s1 = "PDD";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Bill Payment":

					s1 = "PDB";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "DataCard Recharge":

					s1 = "PDD";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Purchase Airtime":

					s1 = "PDA";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Dstv Recharge":

					s1 = "PDS";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;
				default:

					char servicechar = serviceName.charAt(0);
					s1 = "PD" + servicechar;
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;
				}

				// pcname :- QR Code
			case "QR Code":
				logger.info("from pcname--QR Code");
				switch (serviceName) {

				case "Merchant Payment":
					logger.info("Merchant Payment---PQP");
					s1 = "PQP";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				default:

					char servicechar = serviceName.charAt(0);
					s1 = "PQ" + servicechar;
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				}

				// pcname :- Aadhar Pay
			case "Aadhaar Pay":
				logger.info("from pcname--Aadhar Pay");
				switch (serviceName) {

				case "Merchant Payment":
					logger.info("Merchant Payment---PAP");
					s1 = "PAP";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				default:

					char servicechar = serviceName.charAt(0);
					s1 = "PAR" + servicechar;
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				}

			case "Cash":
				switch (serviceName) {
				case "Mobile Recharge":

					s1 = "PCM";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Merchant Payment":
					logger.info("Merchant Payment---PWP");
					s1 = "PWP";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Dth Recharge":

					s1 = "PCD";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Bill Payment":

					s1 = "PCB";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "DataCard Recharge":

					s1 = "PCD";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Purchase Airtime":

					s1 = "PCP";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				default:
					char servicechar = serviceName.charAt(0);
					s1 = "PC" + servicechar;
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;
				}

			}

		case "Mobile Money":
			switch (pcOptionName) {
			case "Credit":

				switch (serviceName) {
				case "Mobile Recharge":

					s1 = "MCM";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Dth Recharge":

					s1 = "MCD";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Bill Payment":

					s1 = "MCB";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "DataCard Recharge":

					s1 = "MCD";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Purchase Airtime":

					s1 = "MCA";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Dstv Recharge":

					s1 = "MCS";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;
				default:

					char servicechar = serviceName.charAt(0);
					s1 = "MC" + servicechar;
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				}

			case "Debit Card":
				switch (serviceName) {
				case "Mobile Recharge":

					s1 = "MDM";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Merchant Payment":
					logger.info("Merchant Payment---MDP");
					s1 = "MDP";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Dth Recharge":

					s1 = "MDD";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Bill Payment":

					s1 = "MDB";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "DataCard Recharge":

					s1 = "MDD";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Purchase Airtime":

					s1 = "MDA";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Dstv Recharge":

					s1 = "MDS";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;
				default:

					char servicechar = serviceName.charAt(0);
					s1 = "MD" + servicechar;
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				}

			case "Net Banking":
				switch (serviceName) {
				case "Mobile Recharge":

					s1 = "MNM";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Dth Recharge":

					s1 = "MND";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Bill Payment":

					s1 = "MNB";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "DataCard Recharge":

					s1 = "MND";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				default:

					char servicechar = serviceName.charAt(0);
					s1 = "MN" + servicechar;
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;
				}

			case "Mobile Money":
				logger.info("from pcname--Mobile Money");
				switch (serviceName) {
				case "Bill Payment":

					s1 = "MMB";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Purchase Airtime":

					s1 = "MMA";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				case "Dstv Recharge":

					s1 = "MMS";
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;

				}

			}

		case "Bank":
			switch (pcOptionName) {

			case "Cash":
				switch (serviceName) {
				default:
					char servicechar = serviceName.charAt(0);
					s1 = "BC" + servicechar;
					trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
					return trxnRefId1;
				}

			}
		case "SUPA KASH":

			logger.info("from pcname--Wallet Cash");
			char servicechar = serviceName.charAt(0);
			s1 = "PS" + servicechar;
			trxnRefId1 = GenerateTrxnRefId.generateTranRefID(s1);
			return trxnRefId1;
		}

		return trxnRefId;

	}

	/*
	 * generateTranRefID method Generate A unique Id For Every Transactions.
	 * 
	 * @param s1 (sourceName,pcOptionName,serviceName) is combination of first
	 * character of all parameter by this Parameter we are Generating a Unique Id
	 * 
	 * @return java.lang.String . This Method returning a 10 Digit number with four
	 * character
	 */

	private static String generateTranRefID(String s1) {

		logger.info("___________Wait 5 second random number Generated_________");

		String trxnRefId2 = (s1.toUpperCase()) + System.currentTimeMillis();

		logger.info("length of trxnRefId2 is__________" + trxnRefId2.length());

		logger.info("*********Random Generated TranRefID is********" + trxnRefId2);

		logger.info("GenerateTrxnRefId.class--------------" + trxnRefId2);

		return trxnRefId2;
	}

}