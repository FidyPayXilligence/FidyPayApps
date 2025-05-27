package com.fidypay.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

//  request entity class

@Entity
@Table(name = "EKYC_REQUEST")
public class EkycRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "REQUEST_ID", nullable = false)
	private Long requestId;

	@Column(name = "MERCHANT_ID", nullable = false)
	private long merchantId;

	@Column(name = "EKYC_ID", nullable = false)
	private String ekycId;

	@Column(name = "USER_REQUEST", nullable = false)
	private String userRequest;

	@Column(name = "CREATION_DATE", nullable = false)
	private Timestamp creationDate;

	public EkycRequest() {
	}

	public EkycRequest(long merchantId, String ekycId, String userRequest, Timestamp creationDate) {
		this.merchantId = merchantId;
		this.ekycId = ekycId;
		this.userRequest = userRequest;
		this.creationDate = creationDate;
	}

	public String getEkycId() {
		return ekycId;
	}

	public void setEkycId(String ekycId) {
		this.ekycId = ekycId;
	}

	public EkycRequest(Long requestId) {
		this.requestId = requestId;
	}

	public Long getRequestId() {
		return requestId;
	}

	public void setRequestId(Long requestId) {
		this.requestId = requestId;
	}

	public long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(long merchantId) {
		this.merchantId = merchantId;
	}

	public String getUserRequest() {
		return userRequest;
	}

	public void setUserRequest(String userRequest) {
		this.userRequest = userRequest;
	}

	public Timestamp getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
	}

}
