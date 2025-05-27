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
@Table(name = "USER_KYC_DETAILS")
@EntityListeners(AuditingEntityListener.class)
public class UserKycDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "USER_KYC_ID")
	private long userKycId;

	@Column(name = "USER_DOB", nullable = false)
	private String userDob;

	@Column(name = "USER_ACCOUNT_NO", nullable = false)
	private String userAccountNo;

	@Column(name = "USER_IFSC", nullable = false)
	private String userIfsc;

	@Column(name = "PAN_CARD_NO", nullable = false)
	private String panCardNo;

	@Column(name = "PAN_CARD_IMAGE_ID", nullable = false)
	private String panCardImageId;

	@Column(name = "AADHAR_NO", nullable = false)
	private String aadharNo;

	@Column(name = "AADHAR_IMAGE_ID", nullable = false)
	private String aadharImageId;

	@Column(name = "USER_ADDRESS1", nullable = false)
	private String userAddress1;

	@Column(name = "USER_ADDRESS2", nullable = false)
	private String userAddress2;

	@Column(name = "USER_CITY", nullable = false)
	private String userCity;

	@Column(name = "USER_STATE", nullable = false)
	private String userState;

	@Column(name = "USER_PINCODE", nullable = false)
	private String userPincode;

	@Column(name = "USER_FIRST_NAME", nullable = false)
	private String userFirstName;

	@Column(name = "USER_LAST_NAME", nullable = false)
	private String userLastName;

	@Column(name = "USER_FULL_NAME", nullable = false)
	private String useFullName;

	@Column(name = "USER_EMAIL", nullable = false)
	private String userEmail;

	@Column(name = "USER_MOBILE", nullable = false)
	private String userMobile;

	@Column(name = "GENDER", nullable = false)
	private String gender;

	@Column(name = "IS_VERIFIED_PAN", nullable = false)
	private Character isVerifiedPan;

	@Column(name = "IS_VERIFIED_AADHAR", nullable = false)
	private Character isVerifiedAadhar;

	@Column(name = "IS_KYC_ID", nullable = false)
	private Character isKycId;

	@Column(name = "KYC_DATE", nullable = false)
	private Timestamp KycDate;

	@Column(name = "LAST_KYC_UPDATE_DATE", nullable = false)
	private Timestamp lastKycUpdateDate;

	@Column(name = "IS_VERIFIED_ACCOUNT", nullable = false)
	private Character isVerifiedAccount;

	@Column(name = "USER_UNIQUE_ID", nullable = false)
	private String userUniqueId;

	@Column(name = "IMAGE_ID", nullable = false)
	private String imageId;

	public long getUserKycId() {
		return userKycId;
	}

	public void setUserKycId(long userKycId) {
		this.userKycId = userKycId;
	}

	public String getUserDob() {
		return userDob;
	}

	public void setUserDob(String userDob) {
		this.userDob = userDob;
	}

	public String getUserAccountNo() {
		return userAccountNo;
	}

	public void setUserAccountNo(String userAccountNo) {
		this.userAccountNo = userAccountNo;
	}

	public String getUserIfsc() {
		return userIfsc;
	}

	public void setUserIfsc(String userIfsc) {
		this.userIfsc = userIfsc;
	}

	public String getPanCardNo() {
		return panCardNo;
	}

	public void setPanCardNo(String panCardNo) {
		this.panCardNo = panCardNo;
	}

	public String getPanCardImageId() {
		return panCardImageId;
	}

	public void setPanCardImageId(String panCardImageId) {
		this.panCardImageId = panCardImageId;
	}

	public String getAadharNo() {
		return aadharNo;
	}

	public void setAadharNo(String aadharNo) {
		this.aadharNo = aadharNo;
	}

	public String getAadharImageId() {
		return aadharImageId;
	}

	public void setAadharImageId(String aadharImageId) {
		this.aadharImageId = aadharImageId;
	}

	public String getUserAddress1() {
		return userAddress1;
	}

	public void setUserAddress1(String userAddress1) {
		this.userAddress1 = userAddress1;
	}

	public String getUserAddress2() {
		return userAddress2;
	}

	public void setUserAddress2(String userAddress2) {
		this.userAddress2 = userAddress2;
	}

	public String getUserCity() {
		return userCity;
	}

	public void setUserCity(String userCity) {
		this.userCity = userCity;
	}

	public String getUserState() {
		return userState;
	}

	public void setUserState(String userState) {
		this.userState = userState;
	}

	public String getUserPincode() {
		return userPincode;
	}

	public void setUserPincode(String userPincode) {
		this.userPincode = userPincode;
	}

	public String getUserFirstName() {
		return userFirstName;
	}

	public void setUserFirstName(String userFirstName) {
		this.userFirstName = userFirstName;
	}

	public String getUserLastName() {
		return userLastName;
	}

	public void setUserLastName(String userLastName) {
		this.userLastName = userLastName;
	}

	public String getUseFullName() {
		return useFullName;
	}

	public void setUseFullName(String useFullName) {
		this.useFullName = useFullName;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserMobile() {
		return userMobile;
	}

	public void setUserMobile(String userMobile) {
		this.userMobile = userMobile;
	}

	public Character getIsVerifiedPan() {
		return isVerifiedPan;
	}

	public void setIsVerifiedPan(Character isVerifiedPan) {
		this.isVerifiedPan = isVerifiedPan;
	}

	public Character getIsVerifiedAadhar() {
		return isVerifiedAadhar;
	}

	public void setIsVerifiedAadhar(Character isVerifiedAadhar) {
		this.isVerifiedAadhar = isVerifiedAadhar;
	}

	public Character getIsKycId() {
		return isKycId;
	}

	public void setIsKycId(Character isKycId) {
		this.isKycId = isKycId;
	}

	public Timestamp getKycDate() {
		return KycDate;
	}

	public void setKycDate(Timestamp kycDate) {
		KycDate = kycDate;
	}

	public Timestamp getLastKycUpdateDate() {
		return lastKycUpdateDate;
	}

	public void setLastKycUpdateDate(Timestamp lastKycUpdateDate) {
		this.lastKycUpdateDate = lastKycUpdateDate;
	}

	public Character getIsVerifiedAccount() {
		return isVerifiedAccount;
	}

	public void setIsVerifiedAccount(Character isVerifiedAccount) {
		this.isVerifiedAccount = isVerifiedAccount;
	}

	public String getUserUniqueId() {
		return userUniqueId;
	}

	public void setUserUniqueId(String userUniqueId) {
		this.userUniqueId = userUniqueId;
	}

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

}
