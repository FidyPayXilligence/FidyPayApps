package com.fidypay.entity;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author prave
 * @Date 09-10-2023
 */
@Entity
@Table(name = "MERCHANT_USER_ACTIVITY")
@EntityListeners(AuditingEntityListener.class)
public class MerchantUserActivity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MERCHANT_USER_ACTIVE_ID")
	private Long merchantUserActiveId;

	@Column(name = "MERCHANT_USER_ID")
	private Long merchantUserId;

	@Column(name = "MERCHANT_ID")
	private Long merchantId;

	@Column(name = "DATE")
	private Timestamp date;

	@Column(name = "API_URL")
	private String apiUrl;

	@Column(name = "PRODUCT_NAME")
	private String productName;

	@Column(name = "API_NAME")
	private String apiName;

	@Column(name = "API_REQUEST")
	private String apiRequest;

	@Column(name = "TYPE")
	private String type;

	public Long getMerchantUserActiveId() {
		return merchantUserActiveId;
	}

	public void setMerchantUserActiveId(Long merchantUserActiveId) {
		this.merchantUserActiveId = merchantUserActiveId;
	}

	public Long getMerchantUserId() {
		return merchantUserId;
	}

	public void setMerchantUserId(Long merchantUserId) {
		this.merchantUserId = merchantUserId;
	}

	public Long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

	public String getApiUrl() {
		return apiUrl;
	}

	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getApiName() {
		return apiName;
	}

	public void setApiName(String apiName) {
		this.apiName = apiName;
	}

	public String getApiRequest() {
		return apiRequest;
	}

	public void setApiRequest(String apiRequest) {
		this.apiRequest = apiRequest;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "MerchantUserActivity{" + "merchantUserActiveId=" + merchantUserActiveId + ", merchantUserId="
				+ merchantUserId + ", merchantId=" + merchantId + ", date=" + date + ", apiUrl='" + apiUrl + '\''
				+ ", productName='" + productName + '\'' + ", apiName='" + apiName + '\'' + ", apiRequest='"
				+ apiRequest + '\'' + '}';
	}
}
