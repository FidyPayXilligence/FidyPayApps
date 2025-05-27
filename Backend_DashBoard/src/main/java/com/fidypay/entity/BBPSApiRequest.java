package com.fidypay.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/*
 To Save data of API Request,we are Using ApiRequest Entity Class.
*/

@Entity
@Table(name = "BBPS_API_REQUEST")
public class BBPSApiRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "REQUEST_ID")
	private Long requestId;

	@Column(name = "CREATION_DATE", nullable = false)
	private Date creationDate;

	@Column(name = "BILLER_ID", nullable = false, length = 200)
	private String billerId;

	@Column(name = "CATEGORY", nullable = false, length = 200)
	private String category="NA";

	////del
	@Column(name = "FETCH_REF_ID", nullable = false, length = 200)
	//@Size(max = 47)
	private String fetchRefId = "NA";

	@Column(name = "MERCHANT_TRXN_REF_ID", nullable = false, length = 200)
	private String merchantTrxnRefId;

	@Column(name = "AMOUNT", nullable = false)
	private double amount;

	@Column(name = "QUICK_PAY", nullable = false, length = 200)
	private String quickPay="NA";

	@Column(name = "SPLIT_PAY", nullable = false, length = 200)
	private String splitPay="NA";

	@Column(name = "SPLIT_PAY_AMOUNT", nullable = false, length = 200)
	private String splitPayAmount="NA";

	@Column(name = "ADDITIONAL_INFO", nullable = false, length = 200)
	private String addtionalInfo="NA";

	@Column(name = "CUSTOMER_NAME", nullable = false, length = 200)
	private String customerName="NA";

	@Column(name = "CUSTOMER_EMAIL", nullable = false, length = 200)
	private String customerEmail="NA";

	@Column(name = "CUSTOMER_MOBILE_NO", nullable = false, length = 200)
	private String customerMobileNo="NA";

	@Column(name = "CUSTOMER_PARAMS", nullable = false, length = 500)
	private String customerParams="NA";

	@Column(name = "MERCHANT_ID", nullable = false)
	private Long merchantId;

	@Column(name = "PAYMENT_ID", nullable = false, length = 200)
	private String paymentId ="NA";

	@Column(name = "CIRCLE_REF_ID", nullable = false, length = 200)
	private String circleRefId ="NA";

	@Column(name = "OPERATOR_ID", nullable = false, length = 200)
	private String operatorId ="NA";

	@Column(name = "REMARK", nullable = false, length = 200)
	private String remark ="NA";

	@Column(name = "PAYMENT_DETAILS", nullable = false, length = 200)
	private String paymentDetails ="NA";

	public Long getRequestId() {
		return requestId;
	}

	public void setRequestId(Long requestId) {
		this.requestId = requestId;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getBillerId() {
		return billerId;
	}

	public void setBillerId(String billerId) {
		this.billerId = billerId;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getFetchRefId() {
		return fetchRefId;
	}

	public void setFetchRefId(String fetchRefId) {
		this.fetchRefId = fetchRefId;
	}

	public String getMerchantTrxnRefId() {
		return merchantTrxnRefId;
	}

	public void setMerchantTrxnRefId(String merchantTrxnRefId) {
		this.merchantTrxnRefId = merchantTrxnRefId;
	}

	
	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getQuickPay() {
		return quickPay;
	}

	public void setQuickPay(String quickPay) {
		this.quickPay = quickPay;
	}

	public String getSplitPay() {
		return splitPay;
	}

	public void setSplitPay(String splitPay) {
		this.splitPay = splitPay;
	}

	public String getSplitPayAmount() {
		return splitPayAmount;
	}

	public void setSplitPayAmount(String splitPayAmount) {
		this.splitPayAmount = splitPayAmount;
	}

	public String getAddtionalInfo() {
		return addtionalInfo;
	}

	public void setAddtionalInfo(String addtionalInfo) {
		this.addtionalInfo = addtionalInfo;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	public String getCustomerMobileNo() {
		return customerMobileNo;
	}

	public void setCustomerMobileNo(String customerMobileNo) {
		this.customerMobileNo = customerMobileNo;
	}

	public String getCustomerParams() {
		return customerParams;
	}

	public void setCustomerParams(String customerParams) {
		this.customerParams = customerParams;
	}

	public Long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public String getCircleRefId() {
		return circleRefId;
	}

	public void setCircleRefId(String circleRefId) {
		this.circleRefId = circleRefId;
	}



	public String getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getPaymentDetails() {
		return paymentDetails;
	}

	public void setPaymentDetails(String paymentDetails) {
		this.paymentDetails = paymentDetails;
	}

}
