package com.fidypay.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class MerchantSubMerchantRequest {

	@JsonIgnore
	private String agentCode;
	@JsonIgnore
	private String perDayTxnCount;
	@JsonIgnore
	private String subMerchantId;
	@JsonIgnore
	private String perDayTxnLmt;
	@JsonIgnore
	private String perDayTxnAmt;
	@JsonIgnore
	private String action;
	@JsonIgnore
	private String requestUrl1;

	@Size(min = 1, max = 50, message = "merchnatBussiessName can not be more than 50 words")
	@NotBlank(message = "merchnatBussiessName can not be empty")
	private String merchnatBussiessName;

	@NotBlank(message = "panNo can not be empty")
	@Pattern(regexp = "[A-Z]{5}[0-9]{4}[A-Z]{1}", message = "Please enter valid panNo")
	private String panNo;

	//@Email(regexp = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")
	@Email(regexp = "^(?=.{1,64}@)[a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*@[^-][a-zA-Z0-9]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{2,})$")
	@NotBlank(message = "contactEmail can not be empty")
	@Size(min = 1, max = 45, message = "contactEmail can not be more than 45 words")
	private String contactEmail;

//	@Pattern(regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$", message = "Please enter valid gstn")
//	@NotBlank(message = "gstn can not be empty")
	private String gstn;

	@NotBlank(message = "merchantBussinessType can not be empty")
	@Size(min = 1, max = 50, message = "merchantBussinessType can not be more than 50 words")
	private String merchantBussinessType;

	@Pattern(regexp = "^\\(?([0-9]{3})\\)?[-.\\s]?([0-9]{3})[-.\\s]?([0-9]{4})$", message = "Please enter valid phone number")
	@NotBlank(message = "mobile can not be empty")
	private String mobile;

	@NotBlank(message = "address can not be empty")
	@Size(min = 1, max = 50, message = "address can not be more than 50 words")
	private String address;

	@NotBlank(message = "state can not be empty")
	@Size(min = 1, max = 30, message = "state can not be more than 30 words")
	private String state;

	@NotBlank(message = "city can not be empty")
	@Size(min = 1, max = 30, message = "city can not be more than 30 words")
	private String city;

	@Pattern(regexp = "^[1-9]{1}[0-9]{2}\\s{0,1}[0-9]{3}$", message = "Please enter valid pinCode number")
	@NotBlank(message = "pinCode can not be empty")
	private String pinCode;

	@NotBlank(message = "MCC can not be empty")
	@Size(min = 1, max = 5, message = "MCC can not be more than 5 words")
	private String MCC;

	@NotBlank(message = "firstName can not be empty")
	@Size(min = 1, max = 45, message = "firstName can not be more than 45 words")
	private String firstName;

	@NotBlank(message = "lastName can not be empty")
	@Size(min = 1, max = 45, message = "lastName can not be more than 45 words")
	private String lastName;

	@NotBlank(message = "password can not be empty")
	@Size(min = 1, max = 30, message = "password can not be more than 30 words")
	private String password;

	@NotBlank(message = "merchantBankName can not be empty")
	@Size(min = 1, max = 30, message = "merchantBankName can not be more than 30 words")
	private String merchantBankName;

	@NotBlank(message = "accountNumber can not be empty")
	@Size(min = 1, max = 20, message = "accountNumber can not be more than 20 words")
	private String accountNumber;

	@NotBlank(message = "ifsc can not be empty")
	private String ifsc;

	@NotBlank(message = "merchantGenre can not be empty")
	private String merchantGenre;

	@NotBlank(message = "bankBranch can not be empty")
	@Size(min = 1, max = 50, message = "bankBranch can not be more than 50 words")
	private String bankBranch;

	@NotBlank(message = "alternateAddress can not be empty")
	@Size(min = 1, max = 30, message = "alternateAddress can not be more than 30 words")
	private String alternateAddress;

	@NotBlank(message = "latitude can not be empty")
	@Size(min = 1, max = 20, message = "latitude can not be more than 20 words")
	private String latitude;

	@NotBlank(message = "longitude can not be empty")
	@Size(min = 1, max = 20, message = "longitude can not be more than 20 words")
	private String longitude;

//	@Pattern(regexp = "^\\d{4}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])$", message = "Please enter valid dob")
//	@NotBlank(message = "dob can not be empty")
	private String dob;

	//@Pattern(regexp = "^\\d{4}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])$", message = "Please enter valid doi")
	private String doi;

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getMerchnatBussiessName() {
		return merchnatBussiessName;
	}

	public void setMerchnatBussiessName(String merchnatBussiessName) {
		this.merchnatBussiessName = merchnatBussiessName;
	}

//	public String getMerchantVirtualAddress() {
//		return merchantVirtualAddress;
//	}
//	public void setMerchantVirtualAddress(String merchantVirtualAddress) {
//		this.merchantVirtualAddress = merchantVirtualAddress;
//	}
	public String getRequestUrl1() {
		return requestUrl1;
	}

	public void setRequestUrl1(String requestUrl1) {
		this.requestUrl1 = requestUrl1;
	}

	public String getPanNo() {
		return panNo;
	}

	public void setPanNo(String panNo) {
		this.panNo = panNo;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public String getGstn() {
		return gstn;
	}

	public void setGstn(String gstn) {
		this.gstn = gstn;
	}

	public String getPerDayTxnCount() {
		return perDayTxnCount;
	}

	public void setPerDayTxnCount(String perDayTxnCount) {
		this.perDayTxnCount = perDayTxnCount;
	}

	public String getMerchantBussinessType() {
		return merchantBussinessType;
	}

	public void setMerchantBussinessType(String merchantBussinessType) {
		this.merchantBussinessType = merchantBussinessType;
	}

	public String getPerDayTxnLmt() {
		return perDayTxnLmt;
	}

	public void setPerDayTxnLmt(String perDayTxnLmt) {
		this.perDayTxnLmt = perDayTxnLmt;
	}

	public String getPerDayTxnAmt() {
		return perDayTxnAmt;
	}

	public void setPerDayTxnAmt(String perDayTxnAmt) {
		this.perDayTxnAmt = perDayTxnAmt;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPinCode() {
		return pinCode;
	}

	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}

	public String getSubMerchantId() {
		return subMerchantId;
	}

	public void setSubMerchantId(String subMerchantId) {
		this.subMerchantId = subMerchantId;
	}

	public String getMCC() {
		return MCC;
	}

	public void setMCC(String mCC) {
		MCC = mCC;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMerchantBankName() {
		return merchantBankName;
	}

	public void setMerchantBankName(String merchantBankName) {
		this.merchantBankName = merchantBankName;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getIfsc() {
		return ifsc;
	}

	public void setIfsc(String ifsc) {
		this.ifsc = ifsc;
	}

	public String getMerchantGenre() {
		return merchantGenre;
	}

	public void setMerchantGenre(String merchantGenre) {
		this.merchantGenre = merchantGenre;
	}

	public String getAgentCode() {
		return agentCode;
	}

	public void setAgentCode(String agentCode) {
		this.agentCode = agentCode;
	}

	public String getBankBranch() {
		return bankBranch;
	}

	public void setBankBranch(String bankBranch) {
		this.bankBranch = bankBranch;

	}

	public String getAlternateAddress() {
		return alternateAddress;
	}

	public void setAlternateAddress(String alternateAddress) {
		this.alternateAddress = alternateAddress;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getDoi() {
		return doi;
	}

	public void setDoi(String doi) {
		this.doi = doi;
	}

}
