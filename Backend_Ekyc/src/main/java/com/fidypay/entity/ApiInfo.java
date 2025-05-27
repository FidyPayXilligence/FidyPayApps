package com.fidypay.entity;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "API_INFO", indexes = {
		@Index(name = "index_API_ID", columnList = "API_ID"),
		@Index(name = "index_API_NAME", columnList = "API_NAME"),
		@Index(name = "index_API_URL", columnList = "API_URL"),
		@Index(name = "index_API_USERNAME", columnList = "API_USERNAME"),
		@Index(name = "index_API_PASSWORD", columnList = "API_PASSWORD"),
		@Index(name = "index_API_IS_ACTIVE", columnList = "API_IS_ACTIVE"),
		@Index(name = "index_IS_API_DELETED", columnList = "IS_API_DELETED")
		})
public class ApiInfo{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "API_ID")
	private long apiId;

	@Column(name = "API_NAME", nullable = false, length = 200)
	private String apiName;

	@Column(name = "API_URL", nullable = false, length = 400)
	private String apiUrl;

	@Column(name = "API_USERNAME", nullable = false, length = 300)
	private String apiUsername;

	@Column(name = "API_PASSWORD", nullable = false, length = 300)
	private String apiPassword;

	@Column(name = "API_IS_ACTIVE", nullable = false, length = 1)
	private char apiIsActive;

	@Column(name = "API_PROTOCOL", nullable = false, length = 100)
	private String apiProtocol;

	@Column(name = "IS_API_DELETED", nullable = false, length = 1)
	private Character isApiDeleted;

	
	@OneToMany(mappedBy = "apiInfo", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<PaymentChannelOptions> paymentChannelOptionses;

	public ApiInfo() {
	}

	public ApiInfo(long apiId, String apiName, String apiUrl, String apiUsername, String apiPassword, char apiIsActive,
			String apiProtocol) {
		this.apiId = apiId;
		this.apiName = apiName;
		this.apiUrl = apiUrl;
		this.apiUsername = apiUsername;
		this.apiPassword = apiPassword;
		this.apiIsActive = apiIsActive;
		this.apiProtocol = apiProtocol;
	}

	public ApiInfo(long apiId, String apiName, String apiUrl, String apiUsername, String apiPassword, char apiIsActive,
			String apiProtocol, Character isApiDeleted, Set<PaymentChannelOptions> paymentChannelOptionses) {
		this.apiId = apiId;
		this.apiName = apiName;
		this.apiUrl = apiUrl;
		this.apiUsername = apiUsername;
		this.apiPassword = apiPassword;
		this.apiIsActive = apiIsActive;
		this.apiProtocol = apiProtocol;
		this.isApiDeleted = isApiDeleted;
		this.paymentChannelOptionses = paymentChannelOptionses;
	}

	public long getApiId() {
		return apiId;
	}

	public void setApiId(long apiId) {
		this.apiId = apiId;
	}

	public String getApiName() {
		return apiName;
	}

	public void setApiName(String apiName) {
		this.apiName = apiName;
	}

	public String getApiUrl() {
		return apiUrl;
	}

	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}

	public String getApiUsername() {
		return apiUsername;
	}

	public void setApiUsername(String apiUsername) {
		this.apiUsername = apiUsername;
	}

	public String getApiPassword() {
		return apiPassword;
	}

	public void setApiPassword(String apiPassword) {
		this.apiPassword = apiPassword;
	}

	public char getApiIsActive() {
		return apiIsActive;
	}

	public void setApiIsActive(char apiIsActive) {
		this.apiIsActive = apiIsActive;
	}

	public String getApiProtocol() {
		return apiProtocol;
	}

	public void setApiProtocol(String apiProtocol) {
		this.apiProtocol = apiProtocol;
	}

	public Character getIsApiDeleted() {
		return isApiDeleted;
	}

	public void setIsApiDeleted(Character isApiDeleted) {
		this.isApiDeleted = isApiDeleted;
	}

	public Set<PaymentChannelOptions> getPaymentChannelOptionses() {
		return paymentChannelOptionses;
	}

	public void setPaymentChannelOptionses(Set<PaymentChannelOptions> paymentChannelOptionses) {
		this.paymentChannelOptionses = paymentChannelOptionses;
	}

}
