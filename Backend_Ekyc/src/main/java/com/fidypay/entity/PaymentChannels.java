package com.fidypay.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Table(name = "PAYMENT_CHANNELS")
@EntityListeners(AuditingEntityListener.class)
public class PaymentChannels {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PAYMENT_CHANNEL_ID")
	private long paymentChannelId;

	@Column(name = "PC_NAME")
	private String pcName;

	@Column(name = "PC_DETAILS")
	private String pcDetails;

	@Column(name = "PC_SETTLEMENT_FREQUENCY")
	private String pcSettlementFrequency;

	@Column(name = "PC_BANK_NAME")
	private String pcBankName;

	@Column(name = "PC_BANK_BRANCH")
	private String pcBankBranch;

	@Column(name = "PC_BANK_CODE")
	private String pcBankCode;

	@Column(name = "PC_ACCOUNT_NO")
	private String pcAccountNo;

	@Column(name = "PC_EMAIL")
	private String pcEmail;

	@Column(name = "PC_PHONE")
	private String pcPhone;

	@Column(name = "IS_WALLET")
	private Character isWallet;

		
	 @OneToMany(mappedBy = "paymentChannels", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<PaymentChannelReconciliation> paymentChannelReconciliations = new HashSet<PaymentChannelReconciliation>(0);
	
	
	
	 @OneToMany(mappedBy = "paymentChannels", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<CoreTransactions> coreTransactionses = new HashSet<CoreTransactions>(0);
	
	 @OneToMany(mappedBy = "paymentChannels", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<PaymentChannelSettlements> paymentChannelSettlementses = new HashSet<PaymentChannelSettlements>(0);

	
	
	 @OneToMany(mappedBy = "paymentChannels", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<PaymentChannelOptions> paymentChannelOptionses = new HashSet<PaymentChannelOptions>(0);

	public PaymentChannels() {
	}

	public PaymentChannels(long paymentChannelId) {
		this.paymentChannelId = paymentChannelId;
	}

	public PaymentChannels(long paymentChannelId, String pcName, String pcDetails, String pcSettlementFrequency,
			String pcBankName, String pcBankBranch, String pcBankCode, String pcAccountNo, String pcEmail,
			String pcPhone, Character isWallet, Set paymentChannelReconciliations, Set inboundNotifications,
			Set coreTransactionses, Set paymentChannelSettlementses, Set paymentChannelOptionses) {
		this.paymentChannelId = paymentChannelId;
		this.pcName = pcName;
		this.pcDetails = pcDetails;
		this.pcSettlementFrequency = pcSettlementFrequency;
		this.pcBankName = pcBankName;
		this.pcBankBranch = pcBankBranch;
		this.pcBankCode = pcBankCode;
		this.pcAccountNo = pcAccountNo;
		this.pcEmail = pcEmail;
		this.pcPhone = pcPhone;
		this.isWallet = isWallet;
		this.paymentChannelReconciliations = paymentChannelReconciliations;
	
		this.coreTransactionses = coreTransactionses;
		this.paymentChannelSettlementses = paymentChannelSettlementses;
		this.paymentChannelOptionses = paymentChannelOptionses;
	}

	public long getPaymentChannelId() {
		return this.paymentChannelId;
	}

	public void setPaymentChannelId(long paymentChannelId) {
		this.paymentChannelId = paymentChannelId;
	}

	public String getPcName() {
		return this.pcName;
	}

	public void setPcName(String pcName) {
		this.pcName = pcName;
	}

	public String getPcDetails() {
		return this.pcDetails;
	}

	public void setPcDetails(String pcDetails) {
		this.pcDetails = pcDetails;
	}

	public String getPcSettlementFrequency() {
		return this.pcSettlementFrequency;
	}

	public void setPcSettlementFrequency(String pcSettlementFrequency) {
		this.pcSettlementFrequency = pcSettlementFrequency;
	}

	public String getPcBankName() {
		return this.pcBankName;
	}

	public void setPcBankName(String pcBankName) {
		this.pcBankName = pcBankName;
	}

	public String getPcBankBranch() {
		return this.pcBankBranch;
	}

	public void setPcBankBranch(String pcBankBranch) {
		this.pcBankBranch = pcBankBranch;
	}

	public String getPcBankCode() {
		return this.pcBankCode;
	}

	public void setPcBankCode(String pcBankCode) {
		this.pcBankCode = pcBankCode;
	}

	public String getPcAccountNo() {
		return this.pcAccountNo;
	}

	public void setPcAccountNo(String pcAccountNo) {
		this.pcAccountNo = pcAccountNo;
	}

	public String getPcEmail() {
		return this.pcEmail;
	}

	public void setPcEmail(String pcEmail) {
		this.pcEmail = pcEmail;
	}

	public String getPcPhone() {
		return this.pcPhone;
	}

	public void setPcPhone(String pcPhone) {
		this.pcPhone = pcPhone;
	}

	public Character getIsWallet() {
		return this.isWallet;
	}

	public void setIsWallet(Character isWallet) {
		this.isWallet = isWallet;
	}

	public Set getPaymentChannelReconciliations() {
		return this.paymentChannelReconciliations;
	}

	public void setPaymentChannelReconciliations(Set paymentChannelReconciliations) {
		this.paymentChannelReconciliations = paymentChannelReconciliations;
	}


	public Set getCoreTransactionses() {
		return this.coreTransactionses;
	}

	public void setCoreTransactionses(Set coreTransactionses) {
		this.coreTransactionses = coreTransactionses;
	}

	public Set getPaymentChannelSettlementses() {
		return this.paymentChannelSettlementses;
	}

	public void setPaymentChannelSettlementses(Set paymentChannelSettlementses) {
		this.paymentChannelSettlementses = paymentChannelSettlementses;
	}

	public Set getPaymentChannelOptionses() {
		return this.paymentChannelOptionses;
	}

	public void setPaymentChannelOptionses(Set paymentChannelOptionses) {
		this.paymentChannelOptionses = paymentChannelOptionses;
	}

}
