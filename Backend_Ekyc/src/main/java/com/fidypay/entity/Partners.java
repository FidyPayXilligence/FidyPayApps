package com.fidypay.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "PARTNERS")
@EntityListeners(AuditingEntityListener.class)
public class Partners {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PARTNER_ID")
	private long partnerId;

	@Column(name = "PARTNER_FIRST_NAME", length = 100, nullable = false)
	private String partnerFirstName;

	@Column(name = "PARTNER_LAST_NAME", length = 100, nullable = false)
	private String partnerLastName;

	@Column(name = "PARTNER_BUSINESS_NAME", length = 300, nullable = false)
	private String partnerBusinessName;

	@Column(name = "PARTNER_MOBILE", length = 100, nullable = false)
	private String partnerMobile;

	@Column(name = "PARTNER_EMAIL", length = 200, nullable = false)
	private String partnerEmail;

	@Column(name = "PARTNER_PASSWORD", length = 100, nullable = false)
	private String partnerPassword;

	@Column(name = "PARTNER_ADDRESS_LINE_ONE", length = 300, nullable = false)
	private String partnerAddressLineOne;

	@Column(name = "PARTNER_ADDRESS_LINE_TWO", length = 300, nullable = false)
	private String partnerAddressLineTwo;

	@Column(name = "PARTNER_CITY", length = 200, nullable = false)
	private String partnerCity;

	@Column(name = "PARTNER_STATE", length = 200, nullable = false)
	private String partnerState;

	@Column(name = "PARTNER_PIN_CODE", length = 100, nullable = false)
	private String partnerPinCode;

	@Column(name = "PARTNER_CREATION_DATE", nullable = false)
	private Timestamp partnerCreationDate;

	@Column(name = "IS_PARTNER_ACTIVE", nullable = false)
	private Character isPartnerActive;

	@Column(name = "PARTNER_LAST_ACTIVE", nullable = false)
	private Timestamp partnerLastActive;

	@Column(name = "PARTNER_KEY", length = 200, nullable = false)
	private String partnerKey;

	@Column(name = "PARTNER_ALTERNATE_EMAIL", length = 3000, nullable = false)
	private String partnerAlternateEmail;
	
	

	public long getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(long partnerId) {
		this.partnerId = partnerId;
	}

	public String getPartnerFirstName() {
		return partnerFirstName;
	}

	public void setPartnerFirstName(String partnerFirstName) {
		this.partnerFirstName = partnerFirstName;
	}

	public String getPartnerLastName() {
		return partnerLastName;
	}

	public void setPartnerLastName(String partnerLastName) {
		this.partnerLastName = partnerLastName;
	}

	public String getPartnerBusinessName() {
		return partnerBusinessName;
	}

	public void setPartnerBusinessName(String partnerBusinessName) {
		this.partnerBusinessName = partnerBusinessName;
	}

	public String getPartnerMobile() {
		return partnerMobile;
	}

	public void setPartnerMobile(String partnerMobile) {
		this.partnerMobile = partnerMobile;
	}

	public String getPartnerEmail() {
		return partnerEmail;
	}

	public void setPartnerEmail(String partnerEmail) {
		this.partnerEmail = partnerEmail;
	}

	public String getPartnerPassword() {
		return partnerPassword;
	}

	public void setPartnerPassword(String partnerPassword) {
		this.partnerPassword = partnerPassword;
	}

	public String getPartnerAddressLineOne() {
		return partnerAddressLineOne;
	}

	public void setPartnerAddressLineOne(String partnerAddressLineOne) {
		this.partnerAddressLineOne = partnerAddressLineOne;
	}

	public String getPartnerAddressLineTwo() {
		return partnerAddressLineTwo;
	}

	public void setPartnerAddressLineTwo(String partnerAddressLineTwo) {
		this.partnerAddressLineTwo = partnerAddressLineTwo;
	}

	public String getPartnerCity() {
		return partnerCity;
	}

	public void setPartnerCity(String partnerCity) {
		this.partnerCity = partnerCity;
	}

	public String getPartnerState() {
		return partnerState;
	}

	public void setPartnerState(String partnerState) {
		this.partnerState = partnerState;
	}

	public String getPartnerPinCode() {
		return partnerPinCode;
	}

	public void setPartnerPinCode(String partnerPinCode) {
		this.partnerPinCode = partnerPinCode;
	}

	public Timestamp getPartnerCreationDate() {
		return partnerCreationDate;
	}

	public void setPartnerCreationDate(Timestamp partnerCreationDate) {
		this.partnerCreationDate = partnerCreationDate;
	}

	public Character getIsPartnerActive() {
		return isPartnerActive;
	}

	public void setIsPartnerActive(Character isPartnerActive) {
		this.isPartnerActive = isPartnerActive;
	}

	public Timestamp getPartnerLastActive() {
		return partnerLastActive;
	}

	public void setPartnerLastActive(Timestamp partnerLastActive) {
		this.partnerLastActive = partnerLastActive;
	}

	public String getPartnerKey() {
		return partnerKey;
	}

	public void setPartnerKey(String partnerKey) {
		this.partnerKey = partnerKey;
	}

	public String getPartnerAlternateEmail() {
		return partnerAlternateEmail;
	}

	public void setPartnerAlternateEmail(String partnerAlternateEmail) {
		this.partnerAlternateEmail = partnerAlternateEmail;
	}

	
//	public Set<Partners> getPartners() {
//		return partners;
//	}
//
//	public void setPartners(Set<Partners> partners) {
//		this.partners = partners;
//	}
	
	

}
