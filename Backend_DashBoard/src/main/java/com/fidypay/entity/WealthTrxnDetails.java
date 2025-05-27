package com.fidypay.entity;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author prave
 * @Date 16-10-2023
 */
@Entity
@Table(name = "WEALTH_TRXN_DETAILS")
@EntityListeners(AuditingEntityListener.class)
public class WealthTrxnDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "WEALTH_TRXN_ID")
	private Long wealthTrxnId;

	@Column(name = "RESPONSE_ID")
	private Long responseId;

	@Column(name = "DATE")
	private Timestamp date;

	@Column(name = "MERCHANT_ID")
	private Long merchantId;

	@Column(name = "MERCHANT_TRXN_REF_ID")
	private String merchantTrxnRefId;

	@Column(name = "INVESTMENT_AMOUNT")
	private Double investmentAmount;

	@Column(name = "TRXN_ID")
	private String trxnId;

	@Column(name = "MERCHANT_SERVICE_ID")
	private Long merchantServiceId;

	@Column(name = "CHARGES")
	private Double charges;

	@Column(name = "COMMISSION")
	private Double commission;

	@Column(name = "CUSTOMER_NAME")
	private String customerName;

	@Column(name = "CUSTOMER_MOBILE")
	private String customerMobile;

	@Column(name = "CUSTOMER_EMAIL")
	private String customerEmail;

	@Column(name = "ADDRESS_1")
	private String address1;

	@Column(name = "ADDRESS_2")
	private String address2;

	@Column(name = "PAYMENT_MODE")
	private String paymentMode;

	@Column(name = "ACCOUNT_NUMBER")
	private String accountNumber;

	@Column(name = "BANK_NAME")
	private String bankName;

	@Column(name = "IFSC")
	private String ifsc;

	@Column(name = "UID")
	private String uId;

	@Column(name = "PHOTO_URL")
	private String photoUrl;

	@Column(name = "DOB")
	private String dob;

	@Column(name = "GENDER")
	private String gender;

	@Column(name = "PROVIDER_UNIQUE_ID")
	private String providerUniqueId;

	@Column(name = "INVESTMENT_PERIOD")
	private String investmentPeriod;

	@Column(name = "INTEREST_RATE")
	private Double interestRate;

	@Column(name = "PAN_NUMBER")
	private String panNumber;

	@Column(name = "PAYMENT_TRXN_ID")
	private String paymentTxId;

	@Column(name = "FREQUENCY")
	private String payoutFrequency;

	@Column(name = "OTHERS_DETAILS_1")
	private String othersDetails1;

	@Column(name = "OTHERS_DETAILS_2")
	private String othersDetails2;

	@Column(name = "NOMINEE_DETAILS")
	private String nomineeDetails;

	public Long getWealthTrxnId() {
		return wealthTrxnId;
	}

	public void setWealthTrxnId(Long wealthTrxnId) {
		this.wealthTrxnId = wealthTrxnId;
	}

	public Long getResponseId() {
		return responseId;
	}

	public void setResponseId(Long responseId) {
		this.responseId = responseId;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

	public Long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
	}

	public String getMerchantTrxnRefId() {
		return merchantTrxnRefId;
	}

	public void setMerchantTrxnRefId(String merchantTrxnRefId) {
		this.merchantTrxnRefId = merchantTrxnRefId;
	}

	public Double getInvestmentAmount() {
		return investmentAmount;
	}

	public void setInvestmentAmount(Double investmentAmount) {
		this.investmentAmount = investmentAmount;
	}

	public String getTrxnId() {
		return trxnId;
	}

	public void setTrxnId(String trxnId) {
		this.trxnId = trxnId;
	}

	public Long getMerchantServiceId() {
		return merchantServiceId;
	}

	public void setMerchantServiceId(Long merchantServiceId) {
		this.merchantServiceId = merchantServiceId;
	}

	public Double getCharges() {
		return charges;
	}

	public void setCharges(Double charges) {
		this.charges = charges;
	}

	public Double getCommission() {
		return commission;
	}

	public void setCommission(Double commission) {
		this.commission = commission;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerMobile() {
		return customerMobile;
	}

	public void setCustomerMobile(String customerMobile) {
		this.customerMobile = customerMobile;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
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

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getIfsc() {
		return ifsc;
	}

	public void setIfsc(String ifsc) {
		this.ifsc = ifsc;
	}

	public String getuId() {
		return uId;
	}

	public void setuId(String uId) {
		this.uId = uId;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getProviderUniqueId() {
		return providerUniqueId;
	}

	public void setProviderUniqueId(String providerUniqueId) {
		this.providerUniqueId = providerUniqueId;
	}

	public String getInvestmentPeriod() {
		return investmentPeriod;
	}

	public void setInvestmentPeriod(String investmentPeriod) {
		this.investmentPeriod = investmentPeriod;
	}

	public Double getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(Double interestRate) {
		this.interestRate = interestRate;
	}

	public String getPanNumber() {
		return panNumber;
	}

	public void setPanNumber(String panNumber) {
		this.panNumber = panNumber;
	}

	public String getPaymentTxId() {
		return paymentTxId;
	}

	public void setPaymentTxId(String paymentTxId) {
		this.paymentTxId = paymentTxId;
	}

	public String getPayoutFrequency() {
		return payoutFrequency;
	}

	public void setPayoutFrequency(String payoutFrequency) {
		this.payoutFrequency = payoutFrequency;
	}

	public String getOthersDetails1() {
		return othersDetails1;
	}

	public void setOthersDetails1(String othersDetails1) {
		this.othersDetails1 = othersDetails1;
	}

	public String getOthersDetails2() {
		return othersDetails2;
	}

	public void setOthersDetails2(String othersDetails2) {
		this.othersDetails2 = othersDetails2;
	}

	public String getNomineeDetails() {
		return nomineeDetails;
	}

	public void setNomineeDetails(String nomineeDetails) {
		this.nomineeDetails = nomineeDetails;
	}

	@Override
	public String toString() {
		return "WealthTrxnDetails{" + "wealthTrxnId=" + wealthTrxnId + ", responseId=" + responseId + ", date=" + date
				+ ", merchantId=" + merchantId + ", merchantTrxnRefId='" + merchantTrxnRefId + '\''
				+ ", investmentAmount=" + investmentAmount + ", trxnId='" + trxnId + '\'' + ", merchantServiceId="
				+ merchantServiceId + ", charges=" + charges + ", commission=" + commission + ", customerName='"
				+ customerName + '\'' + ", customerMobile='" + customerMobile + '\'' + ", customerEmail='"
				+ customerEmail + '\'' + ", address1='" + address1 + '\'' + ", address2='" + address2 + '\''
				+ ", paymentMode='" + paymentMode + '\'' + ", accountNumber='" + accountNumber + '\'' + ", bankName='"
				+ bankName + '\'' + ", ifsc='" + ifsc + '\'' + ", uId='" + uId + '\'' + ", photoUrl='" + photoUrl + '\''
				+ ", dob='" + dob + '\'' + ", gender='" + gender + '\'' + ", providerUniqueId='" + providerUniqueId
				+ '\'' + ", investmentPeriod='" + investmentPeriod + '\'' + ", interestRate=" + interestRate
				+ ", panNumber='" + panNumber + '\'' + '}';
	}
}
