package com.fidypay.dto;

public class SubMerchantDTO {

	private String action;
	private String merchantBussiessName;
	private String panNo;
	private String contactEmail;
	private String gstn;
	private String perDayTxnCount;
	private String merchantBussinessType;
	private String perDayTxnLmt;
	private String perDayTxnAmt;
	private String mobile;
	private String address;
	private String state;
	private String city;
	private String pinCode;
	private String subMerchantId;
	private String MCC;
	private String subMerchantBankName;
	private String subMerchantBankAccount;
	private String subMerchantIfscCode;
	private String merchantGenre;
	private String merchantVirtualAddress;

//	private String firstName;
//	private String lastName;
	private String name;
	private String subMerchantBankBranch;
	private String alternateAddress;
	private String latitude;
	private String longitude;
	private String dob;
	private String doi;

	// As per doc Ver 20.1
	private String llpOrCin;
	private String udhoyogAadhaar;
	private String electricityBill;
	private String electricityBoard;

	public SubMerchantDTO() {
	}

	public SubMerchantDTO(String action, String merchantBussiessName, String panNo, String contactEmail, String gstn,
			String perDayTxnCount, String merchantBussinessType, String perDayTxnLmt, String perDayTxnAmt,
			String mobile, String address, String state, String city, String pinCode, String subMerchantId, String mCC,
			String subMerchantBankName, String subMerchantBankAccount, String subMerchantIfscCode, String merchantGenre,
			String merchantVirtualAddress, String name, String subMerchantBankBranch, String alternateAddress,
			String latitude, String longitude, String dob, String doi) {
		this.action = action;
		this.merchantBussiessName = merchantBussiessName;
		this.panNo = panNo;
		this.contactEmail = contactEmail;
		this.gstn = gstn;
		this.perDayTxnCount = perDayTxnCount;
		this.merchantBussinessType = merchantBussinessType;
		this.perDayTxnLmt = perDayTxnLmt;
		this.perDayTxnAmt = perDayTxnAmt;
		this.mobile = mobile;
		this.address = address;
		this.state = state;
		this.city = city;
		this.pinCode = pinCode;
		this.subMerchantId = subMerchantId;
		MCC = mCC;
		this.subMerchantBankName = subMerchantBankName;
		this.subMerchantBankAccount = subMerchantBankAccount;
		this.subMerchantIfscCode = subMerchantIfscCode;
		this.merchantGenre = merchantGenre;
		this.merchantVirtualAddress = merchantVirtualAddress;
		this.name = name;
		this.subMerchantBankBranch = subMerchantBankBranch;
		this.alternateAddress = alternateAddress;
		this.latitude = latitude;
		this.longitude = longitude;
		this.dob = dob;
		this.doi = doi;
	}

	public String getLlpOrCin() {
		return llpOrCin;
	}

	public void setLlpOrCin(String llpOrCin) {
		this.llpOrCin = llpOrCin;
	}

	public String getUdhoyogAadhaar() {
		return udhoyogAadhaar;
	}

	public void setUdhoyogAadhaar(String udhoyogAadhaar) {
		this.udhoyogAadhaar = udhoyogAadhaar;
	}

	public String getElectricityBill() {
		return electricityBill;
	}

	public void setElectricityBill(String electricityBill) {
		this.electricityBill = electricityBill;
	}

	public String getElectricityBoard() {
		return electricityBoard;
	}

	public void setElectricityBoard(String electricityBoard) {
		this.electricityBoard = electricityBoard;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getMerchantBussiessName() {
		return merchantBussiessName;
	}

	public void setMerchantBussiessName(String merchantBussiessName) {
		this.merchantBussiessName = merchantBussiessName;
	}

	public String getPanNo() {
		return panNo;
	}

	public void setPanNo(String panNo) {
		this.panNo = panNo;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public String getGstn() {
		return gstn;
	}

	public void setGstn(String gstn) {
		this.gstn = gstn;
	}

	public String getPerDayTxnCount() {
		return perDayTxnCount;
	}

	public void setPerDayTxnCount(String perDayTxnCount) {
		this.perDayTxnCount = perDayTxnCount;
	}

	public String getMerchantBussinessType() {
		return merchantBussinessType;
	}

	public void setMerchantBussinessType(String merchantBussinessType) {
		this.merchantBussinessType = merchantBussinessType;
	}

	public String getPerDayTxnLmt() {
		return perDayTxnLmt;
	}

	public void setPerDayTxnLmt(String perDayTxnLmt) {
		this.perDayTxnLmt = perDayTxnLmt;
	}

	public String getPerDayTxnAmt() {
		return perDayTxnAmt;
	}

	public void setPerDayTxnAmt(String perDayTxnAmt) {
		this.perDayTxnAmt = perDayTxnAmt;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPinCode() {
		return pinCode;
	}

	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}

	public String getSubMerchantId() {
		return subMerchantId;
	}

	public void setSubMerchantId(String subMerchantId) {
		this.subMerchantId = subMerchantId;
	}

	public String getMCC() {
		return MCC;
	}

	public void setMCC(String mCC) {
		MCC = mCC;
	}

	public String getSubMerchantBankName() {
		return subMerchantBankName;
	}

	public void setSubMerchantBankName(String subMerchantBankName) {
		this.subMerchantBankName = subMerchantBankName;
	}

	public String getSubMerchantBankAccount() {
		return subMerchantBankAccount;
	}

	public void setSubMerchantBankAccount(String subMerchantBankAccount) {
		this.subMerchantBankAccount = subMerchantBankAccount;
	}

	public String getSubMerchantIfscCode() {
		return subMerchantIfscCode;
	}

	public void setSubMerchantIfscCode(String subMerchantIfscCode) {
		this.subMerchantIfscCode = subMerchantIfscCode;
	}

	public String getMerchantGenre() {
		return merchantGenre;
	}

	public void setMerchantGenre(String merchantGenre) {
		this.merchantGenre = merchantGenre;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSubMerchantBankBranch() {
		return subMerchantBankBranch;
	}

	public void setSubMerchantBankBranch(String subMerchantBankBranch) {
		this.subMerchantBankBranch = subMerchantBankBranch;
	}

	public String getMerchantVirtualAddress() {
		return merchantVirtualAddress;
	}

	public void setMerchantVirtualAddress(String merchantVirtualAddress) {
		this.merchantVirtualAddress = merchantVirtualAddress;
	}

	public String getAlternateAddress() {
		return alternateAddress;
	}

	public void setAlternateAddress(String alternateAddress) {
		this.alternateAddress = alternateAddress;
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

	public static SubMerchantDTO subMerchantDTO(SubMerchantDTO subMerchantDTO) {

		subMerchantDTO.setAction(subMerchantDTO.getAction());
		subMerchantDTO.setAddress(subMerchantDTO.getAddress());
		subMerchantDTO.setCity(subMerchantDTO.getCity());
		subMerchantDTO.setContactEmail(subMerchantDTO.getContactEmail());
		subMerchantDTO.setGstn(subMerchantDTO.getGstn());
		subMerchantDTO.setMCC(subMerchantDTO.getMCC());
		subMerchantDTO.setMerchantBussiessName(subMerchantDTO.getMerchantBussiessName());
		subMerchantDTO.setMerchantBussinessType(subMerchantDTO.getMerchantBussinessType());
		subMerchantDTO.setMobile(subMerchantDTO.getMobile());
		subMerchantDTO.setPanNo(subMerchantDTO.getPanNo());
		subMerchantDTO.setPerDayTxnAmt(subMerchantDTO.getPerDayTxnAmt());
		subMerchantDTO.setPerDayTxnCount(subMerchantDTO.getPerDayTxnCount());
		subMerchantDTO.setPerDayTxnLmt(subMerchantDTO.getPerDayTxnLmt());
		subMerchantDTO.setPinCode(subMerchantDTO.getPinCode());
		subMerchantDTO.setState(subMerchantDTO.getState());
		subMerchantDTO.setSubMerchantId(subMerchantDTO.getSubMerchantId());
		subMerchantDTO.setSubMerchantBankName(subMerchantDTO.getSubMerchantBankName());
		subMerchantDTO.setSubMerchantBankAccount(subMerchantDTO.getSubMerchantBankAccount());
		subMerchantDTO.setSubMerchantIfscCode(subMerchantDTO.getSubMerchantIfscCode());
		subMerchantDTO.setMerchantGenre(subMerchantDTO.getMerchantGenre());
		subMerchantDTO.setName(subMerchantDTO.getName());
		subMerchantDTO.setSubMerchantBankBranch(subMerchantDTO.getSubMerchantBankBranch());
		return subMerchantDTO;
	}
}
