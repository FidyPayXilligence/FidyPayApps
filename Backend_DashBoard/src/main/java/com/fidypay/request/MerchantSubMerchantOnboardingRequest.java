package com.fidypay.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class MerchantSubMerchantOnboardingRequest {

	private String subMerchantFullName;

	private String merchantBusinessName;

	private String businessType;

	private String merchantEmail;

	private String merchantPhone;

	private String description;

	private String businessEntity;

	// merchant onboarding address

	private String address1;

	private String address2;

	private String city;

	private String state;

	private String zipCode;

	private String natureOfBusiness;

	private String lastNameOfAuthorisedSignatory;

	private String firstNameOfAuthorisedSignatory;

	private String designationOfAuthorisedSignatory;

	private String nameOfOtherDirectors;

	private String dateOfBoardMeeting;

	private String merchantBankIfsc;

	private String merchantBankAccountNumber;

	private String merchantBankName;

	// docs

	@Pattern(regexp = "0|1|2", message = "Invalid isVerPanCardAuthorizer Please enter 0 or 1 or 2")
	@NotBlank(message = "isVerPanCardAuthorizer can not be blank")
	private String isVerPanCardAuthorizer;

	private String panCardAuthorizerKey;

	@Pattern(regexp = "0|1|2", message = "Invalid isVerAadhaarCardAuthorizer Please enter 0 or 1 or 2")
	@NotBlank(message = "isVerAadhaarCardAuthorizer can not be blank")
	private String isVerAadhaarCardAuthorizer;

	private String aadhaarCardAuthorizerKey;

	@Pattern(regexp = "0|1|2", message = "Invalid isVerGstNumber Please enter 0 or 1 or 2")
	@NotBlank(message = "isVerGstNumber can not be blank")
	private String isVerGstNumber;

	private String gstNumber;

	@Pattern(regexp = "0|1|2", message = "Invalid isVerImageLiveness Please enter 0 or 1 or 2")
	@NotBlank(message = "isVerImageLiveness can not be blank")
	private String isVerImageLiveness;

	private String imageLivenessKey;

	@Pattern(regexp = "0|1|2", message = "Invalid isVerSignatureImage Please enter 0 or 1 or 2")
	@NotBlank(message = "isVerSignatureImage can not be blank")
	private String isVerSignatureImage;

	private String signatureImageKey;

	private String otherInfo;

	private String mcc;

	@Pattern(regexp = "0|1|2", message = "Invalid isAccountVerification Please enter 0 or 1 or 2")
	@NotBlank(message = "isAccountVerification can not be blank")
	private String isAccountVerification;

	@Pattern(regexp = "0|1|2", message = "Invalid isPanAadhaarLink Please enter 0 or 1 or 2")
	@NotBlank(message = "isPanAadhaarLink can not be blank")
	private String isPanAadhaarLink;

	private String lastNameOfAadharCard;

	private String firstNameOfAadharCard;

	private String isBusinessDoc;

	private String businessDocKey;

	@Pattern(regexp = "0|1|2", message = "Invalid isVerCancelCheque Please enter 0 or 1 or 2")
	@NotBlank(message = "isVerCancelCheque can not be blank")
	private String isVerCancelCheque;

	private String cancelChequeKey;

	private String stateCode;
	private String ownerShipType;
	private String qrType;
	private String dob;
	private String doi;
	private String subMerchantId;
	private String panCardNo;

	private String additionInfo1;
	private String additionInfo2;
	private String additionInfo3;
	private String additionInfo4;
	private String additionInfo5;

	public String getIsPanAadhaarLink() {
		return isPanAadhaarLink;
	}

	public void setIsPanAadhaarLink(String isPanAadhaarLink) {
		this.isPanAadhaarLink = isPanAadhaarLink;
	}

	public String getIsVerCancelCheque() {
		return isVerCancelCheque;
	}

	public void setIsVerCancelCheque(String isVerCancelCheque) {
		this.isVerCancelCheque = isVerCancelCheque;
	}

	public String getCancelChequeKey() {
		return cancelChequeKey;
	}

	public void setCancelChequeKey(String cancelChequeKey) {
		this.cancelChequeKey = cancelChequeKey;
	}

	public String getSubMerchantFullName() {
		return subMerchantFullName;
	}

	public void setSubMerchantFullName(String subMerchantFullName) {
		this.subMerchantFullName = subMerchantFullName;
	}

	public String getMerchantBusinessName() {
		return merchantBusinessName;
	}

	public void setMerchantBusinessName(String merchantBusinessName) {
		this.merchantBusinessName = merchantBusinessName;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public String getMerchantEmail() {
		return merchantEmail;
	}

	public void setMerchantEmail(String merchantEmail) {
		this.merchantEmail = merchantEmail;
	}

	public String getMerchantPhone() {
		return merchantPhone;
	}

	public void setMerchantPhone(String merchantPhone) {
		this.merchantPhone = merchantPhone;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getBusinessEntity() {
		return businessEntity;
	}

	public void setBusinessEntity(String businessEntity) {
		this.businessEntity = businessEntity;
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

	public String getNatureOfBusiness() {
		return natureOfBusiness;
	}

	public void setNatureOfBusiness(String natureOfBusiness) {
		this.natureOfBusiness = natureOfBusiness;
	}

	public String getLastNameOfAuthorisedSignatory() {
		return lastNameOfAuthorisedSignatory;
	}

	public void setLastNameOfAuthorisedSignatory(String lastNameOfAuthorisedSignatory) {
		this.lastNameOfAuthorisedSignatory = lastNameOfAuthorisedSignatory;
	}

	public String getFirstNameOfAuthorisedSignatory() {
		return firstNameOfAuthorisedSignatory;
	}

	public void setFirstNameOfAuthorisedSignatory(String firstNameOfAuthorisedSignatory) {
		this.firstNameOfAuthorisedSignatory = firstNameOfAuthorisedSignatory;
	}

	public String getDesignationOfAuthorisedSignatory() {
		return designationOfAuthorisedSignatory;
	}

	public void setDesignationOfAuthorisedSignatory(String designationOfAuthorisedSignatory) {
		this.designationOfAuthorisedSignatory = designationOfAuthorisedSignatory;
	}

	public String getNameOfOtherDirectors() {
		return nameOfOtherDirectors;
	}

	public void setNameOfOtherDirectors(String nameOfOtherDirectors) {
		this.nameOfOtherDirectors = nameOfOtherDirectors;
	}

	public String getDateOfBoardMeeting() {
		return dateOfBoardMeeting;
	}

	public void setDateOfBoardMeeting(String dateOfBoardMeeting) {
		this.dateOfBoardMeeting = dateOfBoardMeeting;
	}

	public String getMerchantBankIfsc() {
		return merchantBankIfsc;
	}

	public void setMerchantBankIfsc(String merchantBankIfsc) {
		this.merchantBankIfsc = merchantBankIfsc;
	}

	public String getMerchantBankAccountNumber() {
		return merchantBankAccountNumber;
	}

	public void setMerchantBankAccountNumber(String merchantBankAccountNumber) {
		this.merchantBankAccountNumber = merchantBankAccountNumber;
	}

	public String getMerchantBankName() {
		return merchantBankName;
	}

	public void setMerchantBankName(String merchantBankName) {
		this.merchantBankName = merchantBankName;
	}

	public String getIsVerPanCardAuthorizer() {
		return isVerPanCardAuthorizer;
	}

	public void setIsVerPanCardAuthorizer(String isVerPanCardAuthorizer) {
		this.isVerPanCardAuthorizer = isVerPanCardAuthorizer;
	}

	public String getPanCardAuthorizerKey() {
		return panCardAuthorizerKey;
	}

	public void setPanCardAuthorizerKey(String panCardAuthorizerKey) {
		this.panCardAuthorizerKey = panCardAuthorizerKey;
	}

	public String getIsVerAadhaarCardAuthorizer() {
		return isVerAadhaarCardAuthorizer;
	}

	public void setIsVerAadhaarCardAuthorizer(String isVerAadhaarCardAuthorizer) {
		this.isVerAadhaarCardAuthorizer = isVerAadhaarCardAuthorizer;
	}

	public String getAadhaarCardAuthorizerKey() {
		return aadhaarCardAuthorizerKey;
	}

	public void setAadhaarCardAuthorizerKey(String aadhaarCardAuthorizerKey) {
		this.aadhaarCardAuthorizerKey = aadhaarCardAuthorizerKey;
	}

	public String getIsVerGstNumber() {
		return isVerGstNumber;
	}

	public void setIsVerGstNumber(String isVerGstNumber) {
		this.isVerGstNumber = isVerGstNumber;
	}

	public String getGstNumber() {
		return gstNumber;
	}

	public void setGstNumber(String gstNumber) {
		this.gstNumber = gstNumber;
	}

	public String getIsVerImageLiveness() {
		return isVerImageLiveness;
	}

	public void setIsVerImageLiveness(String isVerImageLiveness) {
		this.isVerImageLiveness = isVerImageLiveness;
	}

	public String getImageLivenessKey() {
		return imageLivenessKey;
	}

	public void setImageLivenessKey(String imageLivenessKey) {
		this.imageLivenessKey = imageLivenessKey;
	}

	public String getIsVerSignatureImage() {
		return isVerSignatureImage;
	}

	public void setIsVerSignatureImage(String isVerSignatureImage) {
		this.isVerSignatureImage = isVerSignatureImage;
	}

	public String getSignatureImageKey() {
		return signatureImageKey;
	}

	public void setSignatureImageKey(String signatureImageKey) {
		this.signatureImageKey = signatureImageKey;
	}

	public String getOtherInfo() {
		return otherInfo;
	}

	public void setOtherInfo(String otherInfo) {
		this.otherInfo = otherInfo;
	}

	public String getMcc() {
		return mcc;
	}

	public void setMcc(String mcc) {
		this.mcc = mcc;
	}

	public String getIsAccountVerification() {
		return isAccountVerification;
	}

	public void setIsAccountVerification(String isAccountVerification) {
		this.isAccountVerification = isAccountVerification;
	}

	public String getLastNameOfAadharCard() {
		return lastNameOfAadharCard;
	}

	public void setLastNameOfAadharCard(String lastNameOfAadharCard) {
		this.lastNameOfAadharCard = lastNameOfAadharCard;
	}

	public String getFirstNameOfAadharCard() {
		return firstNameOfAadharCard;
	}

	public void setFirstNameOfAadharCard(String firstNameOfAadharCard) {
		this.firstNameOfAadharCard = firstNameOfAadharCard;
	}

	public String getIsBusinessDoc() {
		return isBusinessDoc;
	}

	public void setIsBusinessDoc(String isBusinessDoc) {
		this.isBusinessDoc = isBusinessDoc;
	}

	public String getBusinessDocKey() {
		return businessDocKey;
	}

	public void setBusinessDocKey(String businessDocKey) {
		this.businessDocKey = businessDocKey;
	}

	public String getAdditionInfo1() {
		return additionInfo1;
	}

	public void setAdditionInfo1(String additionInfo1) {
		this.additionInfo1 = additionInfo1;
	}

	public String getAdditionInfo2() {
		return additionInfo2;
	}

	public void setAdditionInfo2(String additionInfo2) {
		this.additionInfo2 = additionInfo2;
	}

	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	public String getOwnerShipType() {
		return ownerShipType;
	}

	public void setOwnerShipType(String ownerShipType) {
		this.ownerShipType = ownerShipType;
	}

	public String getQrType() {
		return qrType;
	}

	public void setQrType(String qrType) {
		this.qrType = qrType;
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

	public String getSubMerchantId() {
		return subMerchantId;
	}

	public void setSubMerchantId(String subMerchantId) {
		this.subMerchantId = subMerchantId;
	}

	public String getPanCardNo() {
		return panCardNo;
	}

	public void setPanCardNo(String panCardNo) {
		this.panCardNo = panCardNo;
	}

	public String getAdditionInfo3() {
		return additionInfo3;
	}

	public void setAdditionInfo3(String additionInfo3) {
		this.additionInfo3 = additionInfo3;
	}

	public String getAdditionInfo4() {
		return additionInfo4;
	}

	public void setAdditionInfo4(String additionInfo4) {
		this.additionInfo4 = additionInfo4;
	}

	public String getAdditionInfo5() {
		return additionInfo5;
	}

	public void setAdditionInfo5(String additionInfo5) {
		this.additionInfo5 = additionInfo5;
	}

}
