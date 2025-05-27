package com.fidypay.ServiceProvider.YesBank;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class YesBanKSSL {

	// UAT Details 
//	final static String DOMESTIC_PAYMENT_API_URL = "https://uatsky.yesbank.in:444/app/uat/api-banking/";
//	final static String DEBIT_ACCOUNT_NO = "000190600017042";
//	final static String CLIENT_ID = "653a362f-8e5e-454a-9f53-0ad76f3a31a7";
//	final static String CLIENT_SECRET = "cS7fK4rH4yE4oF3pF3qU3uT7vP3sR3lY0fK6mA6sQ2xM0kT4vL";
//	final static String AUTHORIZATION = "Basic dGVzdGNsaWVudDpPeFljb29sQDEyMw==";
//	final static String CUSTOMER_ID = "453733";
//	final static String PG_MERCHANT_ID = "YES0000000050989";
//	final static String MERCHANT_KEY = "b872f8882162eda2d1c7070667946da1";
//	final static String MCC = "1520";
//	final static String MERCHANT_VPA = "8552004565@yesb";
//	final static String APP = "com.mgs.yesapp";
//	final static String TYPE = "MOB";
//	final static String UPI_PAYMENT_URL = "https://uatsky.yesbank.in:444/app/uat/upi/";
//	final static String PAY_UPI_URL = "https://uatsky.yesbank.in:444/app/uat/upi/mePayServerReqImps";
//	final static String CHECK_VIRTUAL_ADDRESS_UPI_URL = "https://uatsky.yesbank.in:444/app/uat/upi/CheckVirtualAddress";
//	final static String INTEGRATION_TYPE = "WEBAPI";
//	final static String SUB_MERCHANT_URL = "https://uatsky.yesbank.in:444/app/uat/upi/onBoardSubMerchant";
//	final static String PASSWORD = "fidy@1234";
//    final static String FILE_PATH =	"C://Fidy/prebuild.fidypay.com.p12";
////  final static String FILE_PATH = "/usr/local/tomcat/webapps/ROOT/RSA/prebuild.fidypay.com.p12";
//    final static String CALL_BACK_URL = "https://prebuild.fidypay.com/FP_ROOT/service/YesBank/callBackUPI"; 
//    final static String TRANSACTION_STATEMENT_URL = "https://uatsky.yesbank.in:444/app/uat/WS/AdhocStatement/Inquiry";

	
	// production
	
		final static String DOMESTIC_PAYMENT_API_URL = "https://sky.yesbank.in:444/app/live/api-banking/";
		final static String CLIENT_ID = "a5576f80-7eac-4018-ba39-94b81ade6598";
		final static String CLIENT_SECRET = "B7sJ1sP3fV4mE1tR4qR1kJ2mV3iC6nY2lB6dX1eH3gP7iY3qY0";
		final static String AUTHORIZATION = "Basic bWFuYW4uamU6UEJaTzY4cmR4dA==";
		final static String CUSTOMER_ID = "2233188"; // CUSTOMER_ID = APP_ID
		final static String UPI_PAYMENT_URL = "https://sky.yesbank.in:444/app/live/upi/";
		final static String PAY_UPI_URL = "https://sky.yesbank.in:444/app/live/upi/mePayServerReqImp";
		final static String CHECK_VIRTUAL_ADDRESS_UPI_URL = "https://sky.yesbank.in:444/app/live/upi/checkVirtualAddressME";
		final static String MCC = "7399";
		final static String TYPE = "MOB";
		final static String APP = "com.mgs.yesapp";
		final static String PG_MERCHANT_ID = "YES0000002094658";
		public final static  String MERCHANT_KEY = "faf5164181f691c2b3e875b6ec4004a9";
		final static String INTEGRATION_TYPE = "WEBAPI";
		final static String SUB_MERCHANT_URL = "https://sky.yesbank.in:444/app/live/upi/onBoardSubMerchant";
		final static String PASSWORD = "fidy@12#$";
		final static String FILE_PATH = "/usr/local/tomcat/webapps/ROOT/RSA/portal.fidypay.com.p12";
		final static String CALL_BACK_URL = "https://portal.fidypay.com/service/YesBank/callBackUPI"; 
		final static String MERCHANT_VPA = "fidypaym@yesbank";
		final static String BUSINESS_NAME = "FidyPay";

	
	
	   
	public static SSLContext yesBankSSL() throws IOException, KeyManagementException, KeyStoreException,
			NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException {
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		FileInputStream clientCertificateContent = new FileInputStream(FILE_PATH);
		keyStore.load(clientCertificateContent, PASSWORD.toCharArray());

		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		keyManagerFactory.init(keyStore, PASSWORD.toCharArray());

		TrustManagerFactory trustManagerFactory = TrustManagerFactory
				.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		trustManagerFactory.init((KeyStore) null);
		TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
		if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
			throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
		}
		X509TrustManager trustManager = (X509TrustManager) trustManagers[0];

		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(keyManagerFactory.getKeyManagers(), new TrustManager[] { trustManager }, null);
		return sslContext;
	}
	

}