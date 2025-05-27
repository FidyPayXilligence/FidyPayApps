package com.fidypay.response;

import java.sql.Timestamp;

public class TransactionReportPayload {
	
	
	private String TRXN_REF_ID;
	private Timestamp  TRXN_DATE;
	private String  SERVICE_NAME;
	private double  TRXN_AMOUNT;
	private String  TRXN_SERVICE_IDENTIFIER;
	private String  STATUS_NAME;
	private String OPERATOR_REF_NO;
	private String  MERCHANT_TRXN_REF_ID;
	private String  SP_REFERENCE_ID;
	public String getTRXN_REF_ID() {
		return TRXN_REF_ID;
	}
	public void setTRXN_REF_ID(String tRXN_REF_ID) {
		TRXN_REF_ID = tRXN_REF_ID;
	}
	public Timestamp getTRXN_DATE() {
		return TRXN_DATE;
	}
	public void setTRXN_DATE(Timestamp tRXN_DATE) {
		TRXN_DATE = tRXN_DATE;
	}
	public String getSERVICE_NAME() {
		return SERVICE_NAME;
	}
	public void setSERVICE_NAME(String sERVICE_NAME) {
		SERVICE_NAME = sERVICE_NAME;
	}
	public double getTRXN_AMOUNT() {
		return TRXN_AMOUNT;
	}
	public void setTRXN_AMOUNT(double tRXN_AMOUNT) {
		TRXN_AMOUNT = tRXN_AMOUNT;
	}
	public String getTRXN_SERVICE_IDENTIFIER() {
		return TRXN_SERVICE_IDENTIFIER;
	}
	public void setTRXN_SERVICE_IDENTIFIER(String tRXN_SERVICE_IDENTIFIER) {
		TRXN_SERVICE_IDENTIFIER = tRXN_SERVICE_IDENTIFIER;
	}
	public String getSTATUS_NAME() {
		return STATUS_NAME;
	}
	public void setSTATUS_NAME(String sTATUS_NAME) {
		STATUS_NAME = sTATUS_NAME;
	}
	public String getOPERATOR_REF_NO() {
		return OPERATOR_REF_NO;
	}
	public void setOPERATOR_REF_NO(String oPERATOR_REF_NO) {
		OPERATOR_REF_NO = oPERATOR_REF_NO;
	}
	public String getMERCHANT_TRXN_REF_ID() {
		return MERCHANT_TRXN_REF_ID;
	}
	public void setMERCHANT_TRXN_REF_ID(String mERCHANT_TRXN_REF_ID) {
		MERCHANT_TRXN_REF_ID = mERCHANT_TRXN_REF_ID;
	}
	public String getSP_REFERENCE_ID() {
		return SP_REFERENCE_ID;
	}
	public void setSP_REFERENCE_ID(String sP_REFERENCE_ID) {
		SP_REFERENCE_ID = sP_REFERENCE_ID;
	}
	
	
	

}
