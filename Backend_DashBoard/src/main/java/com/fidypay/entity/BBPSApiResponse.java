package com.fidypay.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/*
 To Save data of API Response, we are Using ApiResponse Entity Class.
*/
@Entity
@Table(name = "BBPS_API_RESPONSE")
public class BBPSApiResponse {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "RESPONSE_ID")
	private Long responseId;

	//@OneToOne
	//@JoinColumn(name = "request_id")
	@Column(name = "REQUEST_ID", nullable = false)
	private Long apiRequest;

	@Column(name = "RESPONSE_CODE", nullable = false, length = 200)
	private Long responseCode;

	@Column(name = "RESPONSE_STATUS", nullable = false, length = 200)
	private String responseStatus="NA";

	@Column(name = "PAYMENT_DATE", nullable = false)
	private Date paymentDate;

	@Column(name = "PAID_AMOUNT", nullable = false)
	private double paidAmount;

	@Column(name = "RESPONSE_MESSAGE", nullable = false, length = 200)
	private String responseMessage="NA";
	
	@Column(name = "REF_ID", nullable = false, length = 200)
	private String refId="NA";

	@Column(name = "TRANSACTION_REF_ID", nullable = false, length = 200)
	private String transactionRefrenceId="NA";

	@Column(name = "BILLER_REFERENCE_NUMBER", nullable = false, length = 200)
	private String billerReferenceNummber="NA";

	public Long getResponseId() {
		return responseId;
	}

	public void setResponseId(Long responseId) {
		this.responseId = responseId;
	}




	public Long getApiRequest() {
		return apiRequest;
	}

	public void setApiRequest(Long apiRequest) {
		this.apiRequest = apiRequest;
	}

	public Long getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(Long responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseStatus() {
		return responseStatus;
	}

	public void setResponseStatus(String responseStatus) {
		this.responseStatus = responseStatus;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public double getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(double paidAmount) {
		this.paidAmount = paidAmount;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public String getTransactionRefrenceId() {
		return transactionRefrenceId;
	}

	public void setTransactionRefrenceId(String transactionRefrenceId) {
		this.transactionRefrenceId = transactionRefrenceId;
	}

	public String getBillerReferenceNummber() {
		return billerReferenceNummber;
	}

	public void setBillerReferenceNummber(String billerReferenceNummber) {
		this.billerReferenceNummber = billerReferenceNummber;
	}

	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}
	
	

}
