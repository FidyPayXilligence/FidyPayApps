package com.fidypay.entity;


import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;



@Entity
@Table(name = "CUSTOMERS")
@EntityListeners(AuditingEntityListener.class)
public class Customers {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CUSTOMER_ID")
	private long customerId;
	
    @ManyToOne
	 @JoinColumn(name = "SEC_QUESTION_ID")
	private SecurityQuestions securityQuestions;
	
	@Column(name="CUST_EMAIL",length=300)
	private String custEmail;
	
	@Column(name="CUST_PHONE",nullable = false,length=100)
	private String custPhone;
	
	@Column(name="CUST_PASSWORD",length=300)
	private String custPassword;
	
	@Column(name="CUST_FIRSTNAME",length=100)
	private String custFirstname;
	
	@Column(name="CUST_LASTNAME",length=100)
	private String custLastname;
	
	@Column(name="CUST_ADDRESS1",length=200)
	private String custAddress1;
	
	@Column(name="CUST_ADDRESS2",length=200)
	private String custAddress2;
	
	@Column(name="CUST_CITY",length=100)
	private String custCity;
	
	@Column(name="CUST_STATE",length=100)
	private String custState;
	
	@Column(name="CUST_COUNTRY",length=100)
	private String custCountry;
	
	@Column(name="CUST_ZIPCODE",length=15)
	private String custZipcode;
	
	@Column(name="CUST_FROMDATE")
	private Timestamp custFromdate;
	
	@Column(name="CUST_LASTLOGIN")
	private Timestamp custLastlogin;
	
	@Column(name="IS_CUST_ACTIVE",length=1)
	private Character isCustActive;
	
	@Column(name="CUST_LOGIN_COUNT",length=1)
	private Character custLoginCount;
	
	@Column(name="IS_CUST_EMAIL_VERIFIED",length=1)
	private Character isCustEmailVerified;
	
	@Column(name="IS_CUST_PHONE_VERIFIED",length=1)
	private Character isCustPhoneVerified;
	
	@Column(name="GENDER",length=1)
	private Character gender;
	
	@Column(name="CUST_NATIONALITY",length=200)
	private String custNationality;
	
	@Column(name="CUST_OCCUPATION",length=200)
	private String custOccupation;
	
	@Column(name="CUST_DOB")
	private Timestamp custDob;
	
	@Column(name="SEC_ANSWER",length=200)
	private String secAnswer;
	
	@Column(name="SECOND_SEC_QUESTION_ID")
	private Long secondSecQuestionId;
	
	@Column(name="SECOND_SEC_ANSWER",length=200)
	private String secondSecAnswer;
	
	@Column(name="CUST_ALTERNATE_EMAIL",length=300)
	private String custAlternateEmail;
	
		
	 @OneToMany(mappedBy = "customers", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<MerchantAddonWalletInfo> merchantAddonWalletInfos = new HashSet<MerchantAddonWalletInfo>(0);
	
	 @OneToMany(mappedBy = "customers", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<CustomerSubscriptions> customerSubscriptionses = new HashSet<CustomerSubscriptions>(0);
	
    @OneToMany(mappedBy = "customers", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<CustomerWalletInfo> customerWalletInfos = new HashSet<CustomerWalletInfo>(0);
	
	 @OneToMany(mappedBy = "customers", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<CustomerTransactions> customerTransactionses = new HashSet<CustomerTransactions>(0);

	public Customers() {
	}

	
	
	public Customers(long customerId) {
		this.customerId = customerId;
	}



	public Customers(long customerId, String custEmail, String custPhone) {
		this.customerId = customerId;
		this.custEmail = custEmail;
		this.custPhone = custPhone;
	}

	public Customers(long customerId, SecurityQuestions securityQuestions,
			String custEmail, String custPhone, String custPassword,
			String custFirstname, String custLastname, String custAddress1,
			String custAddress2, String custCity, String custState,
			String custCountry, String custZipcode, Timestamp custFromdate,
			Timestamp custLastlogin, Character isCustActive,
			Character custLoginCount, Character isCustEmailVerified,
			Character isCustPhoneVerified, Character gender,
			String custNationality, String custOccupation,
			Timestamp custDob, String secAnswer, Long secondSecQuestionId,
			String secondSecAnswer, String custAlternateEmail,
			Set merchantAddonWalletInfos, Set customerSubscriptionses,
			Set customerWalletInfos, Set customerTransactionses) {
		this.customerId = customerId;
		this.securityQuestions = securityQuestions;
		this.custEmail = custEmail;
		this.custPhone = custPhone;
		this.custPassword = custPassword;
		this.custFirstname = custFirstname;
		this.custLastname = custLastname;
		this.custAddress1 = custAddress1;
		this.custAddress2 = custAddress2;
		this.custCity = custCity;
		this.custState = custState;
		this.custCountry = custCountry;
		this.custZipcode = custZipcode;
		this.custFromdate = custFromdate;
		this.custLastlogin = custLastlogin;
		this.isCustActive = isCustActive;
		this.custLoginCount = custLoginCount;
		this.isCustEmailVerified = isCustEmailVerified;
		this.isCustPhoneVerified = isCustPhoneVerified;
		this.gender = gender;
		this.custNationality = custNationality;
		this.custOccupation = custOccupation;
		this.custDob = custDob;
		this.secAnswer = secAnswer;
		this.secondSecQuestionId = secondSecQuestionId;
		this.secondSecAnswer = secondSecAnswer;
		this.custAlternateEmail = custAlternateEmail;
		this.merchantAddonWalletInfos = merchantAddonWalletInfos;
		this.customerSubscriptionses = customerSubscriptionses;
		this.customerWalletInfos = customerWalletInfos;
		this.customerTransactionses = customerTransactionses;
	}

	public long getCustomerId() {
		return this.customerId;
	}

	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}

	public SecurityQuestions getSecurityQuestions() {
		return this.securityQuestions;
	}

	public void setSecurityQuestions(SecurityQuestions securityQuestions) {
		this.securityQuestions = securityQuestions;
	}

	public String getCustEmail() {
		return this.custEmail;
	}

	public void setCustEmail(String custEmail) {
		this.custEmail = custEmail;
	}

	public String getCustPhone() {
		return this.custPhone;
	}

	public void setCustPhone(String custPhone) {
		this.custPhone = custPhone;
	}

	public String getCustPassword() {
		return this.custPassword;
	}

	public void setCustPassword(String custPassword) {
		this.custPassword = custPassword;
	}

	public String getCustFirstname() {
		return this.custFirstname;
	}

	public void setCustFirstname(String custFirstname) {
		this.custFirstname = custFirstname;
	}

	public String getCustLastname() {
		return this.custLastname;
	}

	public void setCustLastname(String custLastname) {
		this.custLastname = custLastname;
	}

	public String getCustAddress1() {
		return this.custAddress1;
	}

	public void setCustAddress1(String custAddress1) {
		this.custAddress1 = custAddress1;
	}

	public String getCustAddress2() {
		return this.custAddress2;
	}

	public void setCustAddress2(String custAddress2) {
		this.custAddress2 = custAddress2;
	}

	public String getCustCity() {
		return this.custCity;
	}

	public void setCustCity(String custCity) {
		this.custCity = custCity;
	}

	public String getCustState() {
		return this.custState;
	}

	public void setCustState(String custState) {
		this.custState = custState;
	}

	public String getCustCountry() {
		return this.custCountry;
	}

	public void setCustCountry(String custCountry) {
		this.custCountry = custCountry;
	}

	public String getCustZipcode() {
		return this.custZipcode;
	}

	public void setCustZipcode(String custZipcode) {
		this.custZipcode = custZipcode;
	}

	public Timestamp getCustFromdate() {
		return this.custFromdate;
	}

	public void setCustFromdate(Timestamp custFromdate) {
		this.custFromdate = custFromdate;
	}

	public Timestamp getCustLastlogin() {
		return this.custLastlogin;
	}

	public void setCustLastlogin(Timestamp custLastlogin) {
		this.custLastlogin = custLastlogin;
	}	

	public Character getIsCustActive() {
		return isCustActive;
	}

	public void setIsCustActive(Character isCustActive) {
		this.isCustActive = isCustActive;
	}

	public Character getCustLoginCount() {
		return this.custLoginCount;
	}

	public void setCustLoginCount(Character custLoginCount) {
		this.custLoginCount = custLoginCount;
	}

	public Character getIsCustEmailVerified() {
		return this.isCustEmailVerified;
	}

	public void setIsCustEmailVerified(Character isCustEmailVerified) {
		this.isCustEmailVerified = isCustEmailVerified;
	}

	public Character getIsCustPhoneVerified() {
		return this.isCustPhoneVerified;
	}

	public void setIsCustPhoneVerified(Character isCustPhoneVerified) {
		this.isCustPhoneVerified = isCustPhoneVerified;
	}

	public Character getGender() {
		return this.gender;
	}

	public void setGender(Character gender) {
		this.gender = gender;
	}

	public String getCustNationality() {
		return this.custNationality;
	}

	public void setCustNationality(String custNationality) {
		this.custNationality = custNationality;
	}

	public String getCustOccupation() {
		return this.custOccupation;
	}

	public void setCustOccupation(String custOccupation) {
		this.custOccupation = custOccupation;
	}

	public Timestamp getCustDob() {
		return this.custDob;
	}

	public void setCustDob(Timestamp custDob) {
		this.custDob = custDob;
	}

	public String getSecAnswer() {
		return this.secAnswer;
	}

	public void setSecAnswer(String secAnswer) {
		this.secAnswer = secAnswer;
	}

	public Long getSecondSecQuestionId() {
		return this.secondSecQuestionId;
	}

	public void setSecondSecQuestionId(Long secondSecQuestionId) {
		this.secondSecQuestionId = secondSecQuestionId;
	}

	public String getSecondSecAnswer() {
		return this.secondSecAnswer;
	}

	public void setSecondSecAnswer(String secondSecAnswer) {
		this.secondSecAnswer = secondSecAnswer;
	}

	public String getCustAlternateEmail() {
		return this.custAlternateEmail;
	}

	public void setCustAlternateEmail(String custAlternateEmail) {
		this.custAlternateEmail = custAlternateEmail;
	}

	public Set getMerchantAddonWalletInfos() {
		return this.merchantAddonWalletInfos;
	}

	public void setMerchantAddonWalletInfos(Set merchantAddonWalletInfos) {
		this.merchantAddonWalletInfos = merchantAddonWalletInfos;
	}

	public Set getCustomerSubscriptionses() {
		return this.customerSubscriptionses;
	}

	public void setCustomerSubscriptionses(Set customerSubscriptionses) {
		this.customerSubscriptionses = customerSubscriptionses;
	}

	public Set getCustomerWalletInfos() {
		return this.customerWalletInfos;
	}

	public void setCustomerWalletInfos(Set customerWalletInfos) {
		this.customerWalletInfos = customerWalletInfos;
	}

	public Set getCustomerTransactionses() {
		return this.customerTransactionses;
	}

	public void setCustomerTransactionses(Set customerTransactionses) {
		this.customerTransactionses = customerTransactionses;
	}

}
