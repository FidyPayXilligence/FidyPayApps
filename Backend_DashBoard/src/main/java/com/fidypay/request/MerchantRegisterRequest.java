package com.fidypay.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class MerchantRegisterRequest {

	@NotBlank(message = "firstName can not be empty")
	@Size(min = 1, max = 45, message = "firstName can not be more than 45 words")
	private String firstName;

	@NotBlank(message = "lastName can not be empty")
	@Size(min = 1, max = 45, message = "lastName can not be more than 45 words")
	private String lastName;

	@Size(min = 1, max = 45, message = "businessName can not be more than 45 words")
	@NotBlank(message = "businessName can not be empty")
	private String businessName;

	@Pattern(regexp = "Male|Female|MALE|FEMALE", message = "gender not valid please pass Male or Female")
	@NotBlank(message = "gender can not be empty")
	private String gender;

//	@Email(regexp = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")
	@Email(regexp = "^(?=.{1,64}@)[a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*@[^-][a-zA-Z0-9]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{2,})$")
	@NotBlank(message = "email can not be empty")
	@Size(min = 1, max = 45, message = "email can not be more than 45 words")
	private String email;

	@Pattern(regexp = "^\\(?([0-9]{3})\\)?[-.\\s]?([0-9]{3})[-.\\s]?([0-9]{4})$", message = "Please enter valid phone number")
	@NotBlank(message = "phone number can not be empty")
	@Size(min = 10, max = 10, message = "Please enter your 10 digit phone number")
	private String phone;

	@NotBlank(message = "merchantPassword can not be empty")
	@Size(min = 1, max = 30, message = "merchantPassword can not be more than 30 words")
	private String merchantPassword;

	@NotBlank(message = "address1 can not be empty")
	@Size(min = 1, max = 45, message = "address1 can not be more than 45 words")
	private String address1;

	@NotBlank(message = "address2 can not be empty")
	@Size(min = 1, max = 45, message = "address2 can not be more than 45 words")
	private String address2;

	@NotBlank(message = "city can not be empty")
	@Size(min = 1, max = 30, message = "city can not be more than 30 words")
	private String city;

	@NotBlank(message = "state can not be empty")
	@Size(min = 1, max = 30, message = "state can not be more than 30 words")
	private String state;

	@Pattern(regexp = "^[1-9]{1}[0-9]{2}\\s{0,1}[0-9]{3}$", message = "Please enter valid zipCode number")
	@NotBlank(message = "zipCode can not be empty")
	private String zipCode;

	@JsonIgnore
	private String latitude;

	@JsonIgnore
	private String longitude;

	@JsonIgnore
	private String outletType;

	@JsonIgnore
	private String tillPassword;

	@JsonIgnore
	private Long merchantTypeId;

	@JsonIgnore
	private String merchantBankAccountNo;

	@JsonIgnore
	private String merchantBankIfscCode;

	@JsonIgnore
	private String merchantBankName;

	@JsonIgnore
	private String merchantBankBranch;

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

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMerchantPassword() {
		return merchantPassword;
	}

	public void setMerchantPassword(String merchantPassword) {
		this.merchantPassword = merchantPassword;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
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

	public String getOutletType() {
		return outletType;
	}

	public void setOutletType(String outletType) {
		this.outletType = outletType;
	}

	public String getTillPassword() {
		return tillPassword;
	}

	public void setTillPassword(String tillPassword) {
		this.tillPassword = tillPassword;
	}

	public Long getMerchantTypeId() {
		return merchantTypeId;
	}

	public void setMerchantTypeId(Long merchantTypeId) {
		this.merchantTypeId = merchantTypeId;
	}

	public String getMerchantBankAccountNo() {
		return merchantBankAccountNo;
	}

	public void setMerchantBankAccountNo(String merchantBankAccountNo) {
		this.merchantBankAccountNo = merchantBankAccountNo;
	}

	public String getMerchantBankIfscCode() {
		return merchantBankIfscCode;
	}

	public void setMerchantBankIfscCode(String merchantBankIfscCode) {
		this.merchantBankIfscCode = merchantBankIfscCode;
	}

	public String getMerchantBankName() {
		return merchantBankName;
	}

	public void setMerchantBankName(String merchantBankName) {
		this.merchantBankName = merchantBankName;
	}

	public String getMerchantBankBranch() {
		return merchantBankBranch;
	}

	public void setMerchantBankBranch(String merchantBankBranch) {
		this.merchantBankBranch = merchantBankBranch;
	}

}
