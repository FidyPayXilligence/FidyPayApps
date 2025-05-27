package com.fidypay.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "EKYC_VER_V2")
public class EkycVerification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "EKYC_ID")
	private Long ekycId;

	@Column(name = "EMAIL", length = 100)
	private String email;

	@Column(name = "MOBILE", length = 100)
	private String mobile;

	@Column(name = "PAN_COMPANY_BUSSINESS_NAME", length = 200)
	private String panCompanyBussinessName;

	@Column(name = "PAN_COMPANY_NAME", length = 200)
	private String panCompanyName;

	@Column(name = "PAN_No_COMPANY", length = 50)
	private String panNoCompany;

	@Column(name = "PAN_JSON_RESPONSE", length = 2000)
	private String panJsonResponse;

	@Column(name = "IS_COMPANY_PAN_VERIFIDE", length = 1)
	private char isVerifidePanNO;

	@Column(name = "GST_NO", length = 50)
	private String gstNo;

	@Column(name = "CONSITUTE_OF_NAME", length = 100)
	private String consituteOfName;

	@Column(name = "REGESTER_BUSSINESS_NAME", length = 100)
	private String registerBussinessName;

	@Column(name = "GST_JSON_RESPONSE", length = 4000)
	private String gstJsonResponse;

	@Column(name = "IS_GST_VERIFIDE", length = 1)
	private char isGstVerifide;

	@Column(name = "CIN_NO", length = 50)
	private String cinNo;

	@Column(name = "REGISTER_DATE_COMPANY", length = 50)
	private String registerDateCompany;

	@Column(name = "SIGNATORY_NAME_OWNER_PAN_NO", length = 50)
	private String signatoryNameOwnerPanNo;

	@Column(name = "SIGNATORY_PAN_JSON_RESPONSE", length = 2000)
	private String signatoryPanJsonResponse;

	@Column(name = "SIGNATORY_OWNER_NAME", length = 2000)
	private String signatoryPanOwnerName;

	@Column(name = "SIGNATORY_IS_VERIFIDE", length = 1)
	private char isSignatoryVerifide;

	@Column(name = "SIGNATORY_OWNER_DESIGNATION", length = 100)
	private String signatoryOwnerDesignation;

	@Column(name = "IFSC_CODE", length = 50)
	private String ifscCode;

	@Column(name = "ACCOUNT_NO", length = 50)
	private String accountNo;

	@Column(name = "BRANCH_NAME", length = 50)
	private String branchName;

	@Column(name = "BANK_NAME", length = 50)
	private String bankName;

	@Column(name = "IS_VERIFIDE_ACCOUNT", length = 1)
	private char isVerifideAccount;

	@Column(name = "ACCOUNT_JSON_RESPONSE", length = 2000)
	private String accountJsonResponse;

	@Column(name = "AADHAR_NO", length = 20)
	private String aadharNo;

	@Column(name = "AADHAR_REF_ID", length = 50)
	private String aadharRefId;

	@Column(name = "IS_AADHAR_VERIFIDE", length = 1)
	private char isAadharVerifide;

	@Column(name = "AADHAR_JSON_RESPONSE", length = 2000)
	private String aadharJsonResponse;

	@Column(name = "CREATION_DATE")
	private Timestamp creationDate;

	@Column(name = "MERCHANT_ID")
	private Long merchantId;

	@Column(name = "SUB_MERCHANT_ID")
	private Long subMerchantId;

	@Column(name = "IP_ADDRESS", length = 50)
	private String ipAddress;

	@Column(name = "LET_LONG", length = 200)
	private String letLong;
	
	@Column(name = "TYPE", length = 200)
	 private String type;

	public Long getEkycId() {
		return ekycId;
	}

	public void setEkycId(Long ekycId) {
		this.ekycId = ekycId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPanCompanyBussinessName() {
		return panCompanyBussinessName;
	}

	public void setPanCompanyBussinessName(String panCompanyBussinessName) {
		this.panCompanyBussinessName = panCompanyBussinessName;
	}

	public String getPanCompanyName() {
		return panCompanyName;
	}

	public void setPanCompanyName(String panCompanyName) {
		this.panCompanyName = panCompanyName;
	}

	public String getPanNoCompany() {
		return panNoCompany;
	}

	public void setPanNoCompany(String panNoCompany) {
		this.panNoCompany = panNoCompany;
	}

	public String getPanJsonResponse() {
		return panJsonResponse;
	}

	public void setPanJsonResponse(String panJsonResponse) {
		this.panJsonResponse = panJsonResponse;
	}

	public char getIsVerifidePanNO() {
		return isVerifidePanNO;
	}

	public void setIsVerifidePanNO(char isVerifidePanNO) {
		this.isVerifidePanNO = isVerifidePanNO;
	}

	public String getGstNo() {
		return gstNo;
	}

	public void setGstNo(String gstNo) {
		this.gstNo = gstNo;
	}

	public String getConsituteOfName() {
		return consituteOfName;
	}

	public void setConsituteOfName(String consituteOfName) {
		this.consituteOfName = consituteOfName;
	}

	public String getRegisterBussinessName() {
		return registerBussinessName;
	}

	public void setRegisterBussinessName(String registerBussinessName) {
		this.registerBussinessName = registerBussinessName;
	}

	public String getGstJsonResponse() {
		return gstJsonResponse;
	}

	public void setGstJsonResponse(String gstJsonResponse) {
		this.gstJsonResponse = gstJsonResponse;
	}

	public char getIsGstVerifide() {
		return isGstVerifide;
	}

	public void setIsGstVerifide(char isGstVerifide) {
		this.isGstVerifide = isGstVerifide;
	}

	public String getCinNo() {
		return cinNo;
	}

	public void setCinNo(String cinNo) {
		this.cinNo = cinNo;
	}

	public String getRegisterDateCompany() {
		return registerDateCompany;
	}

	public void setRegisterDateCompany(String registerDateCompany) {
		this.registerDateCompany = registerDateCompany;
	}

	public String getSignatoryNameOwnerPanNo() {
		return signatoryNameOwnerPanNo;
	}

	public void setSignatoryNameOwnerPanNo(String signatoryNameOwnerPanNo) {
		this.signatoryNameOwnerPanNo = signatoryNameOwnerPanNo;
	}

	public String getSignatoryPanJsonResponse() {
		return signatoryPanJsonResponse;
	}

	public void setSignatoryPanJsonResponse(String signatoryPanJsonResponse) {
		this.signatoryPanJsonResponse = signatoryPanJsonResponse;
	}

	public String getSignatoryPanOwnerName() {
		return signatoryPanOwnerName;
	}

	public void setSignatoryPanOwnerName(String signatoryPanOwnerName) {
		this.signatoryPanOwnerName = signatoryPanOwnerName;
	}

	public char getIsSignatoryVerifide() {
		return isSignatoryVerifide;
	}

	public void setIsSignatoryVerifide(char isSignatoryVerifide) {
		this.isSignatoryVerifide = isSignatoryVerifide;
	}

	public String getSignatoryOwnerDesignation() {
		return signatoryOwnerDesignation;
	}

	public void setSignatoryOwnerDesignation(String signatoryOwnerDesignation) {
		this.signatoryOwnerDesignation = signatoryOwnerDesignation;
	}

	public String getIfscCode() {
		return ifscCode;
	}

	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getAccountJsonResponse() {
		return accountJsonResponse;
	}

	public void setAccountJsonResponse(String accountJsonResponse) {
		this.accountJsonResponse = accountJsonResponse;
	}

	public String getAadharNo() {
		return aadharNo;
	}

	public void setAadharNo(String aadharNo) {
		this.aadharNo = aadharNo;
	}

	public String getAadharRefId() {
		return aadharRefId;
	}

	public void setAadharRefId(String aadharRefId) {
		this.aadharRefId = aadharRefId;
	}

	public char getIsVerifideAccount() {
		return isVerifideAccount;
	}

	public void setIsVerifideAccount(char isVerifideAccount) {
		this.isVerifideAccount = isVerifideAccount;
	}

	public char getIsAadharVerifide() {
		return isAadharVerifide;
	}

	public void setIsAadharVerifide(char isAadharVerifide) {
		this.isAadharVerifide = isAadharVerifide;
	}

	public String getAadharJsonResponse() {
		return aadharJsonResponse;
	}

	public void setAadharJsonResponse(String aadharJsonResponse) {
		this.aadharJsonResponse = aadharJsonResponse;
	}

	public Timestamp getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
	}

	public Long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
	}

	public Long getSubMerchantId() {
		return subMerchantId;
	}

	public void setSubMerchantId(Long subMerchantId) {
		this.subMerchantId = subMerchantId;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getLetLong() {
		return letLong;
	}

	public void setLetLong(String letLong) {
		this.letLong = letLong;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	
	
}
