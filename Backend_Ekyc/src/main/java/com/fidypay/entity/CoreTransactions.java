package com.fidypay.entity;

import java.sql.Timestamp;
import java.util.*;
import javax.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "CORE_TRANSACTIONS")
@EntityListeners(AuditingEntityListener.class)
public class CoreTransactions {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "TRANSACTION_ID")
	private long transactionId;

	@ManyToOne
	@JoinColumn(name = "TRANSACTION_STATUS_ID")
	private TransactionStatus transactionStatus;

	@ManyToOne
	@JoinColumn(name = "PC_OPTION_ID")
	private PaymentChannelOptions paymentChannelOptions;

	@ManyToOne
	@JoinColumn(name = "PAYMENT_CHANNEL_ID")
	private PaymentChannels paymentChannels;

	@ManyToOne
	@JoinColumn(name = "TRXN_SOURCE_ID")
	private TransactionSources transactionSources;

	@ManyToOne
	@JoinColumn(name = "SERVICE_ID")
	private ServiceInfo serviceInfo;

	@Column(name = "TRXN_REF_ID", length = 200)
	private String trxnRefId;

	@Column(name = "TRXN_SERVICE_IDENTIFIER", length = 200)
	private String trxnServiceIdentifier;

	@Column(name = "TRXN_SERVICE_DETAILS", length = 2000)
	private String trxnServiceDetails;

	@Column(name = "TRXN_PAYMENT_IDENTIFIER", length = 200)
	private String trxnPaymentIdentifier;

	@Column(name = "TRXN_PAYMEN_DETAILS", length = 200)
	private String trxnPaymenDetails;

	@Column(name = "TRXN_DATE")
	private Timestamp trxnDate;

	@Column(name = "TRXN_AMOUNT", length = 126)
	private double trxnAmount;

	@Column(name = "SERVICE_CHARGE", length = 126)
	private double serviceCharge;

	@OneToMany(mappedBy = "coreTransactions", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<ServiceProviderTransactions> serviceProviderTransactionses = new HashSet<ServiceProviderTransactions>(
			0);

	@OneToMany(mappedBy = "coreTransactions", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<MerchantTransactions> merchantTransactionses = new HashSet<MerchantTransactions>(0);

	@OneToMany(mappedBy = "coreTransactions", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<MerchantWalletTransactions> merchantWalletTransactionses = new HashSet<MerchantWalletTransactions>(0);

	@OneToMany(mappedBy = "coreTransactions", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<CustomerWalletTransactions> customerWalletTransactionses = new HashSet<CustomerWalletTransactions>(0);

	@OneToMany(mappedBy = "coreTransactions", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<CustomerTransactions> customerTransactionses = new HashSet<CustomerTransactions>(0);

	@OneToMany(mappedBy = "coreTransactions", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<TicketingDetail> ticketingDetails = new HashSet<TicketingDetail>(0);

	public CoreTransactions() {
	}

	public CoreTransactions(long transactionId, TransactionStatus transactionStatus,
			PaymentChannelOptions paymentChannelOptions, PaymentChannels paymentChannels, ServiceInfo serviceInfo,
			String trxnRefId, String trxnServiceIdentifier, String trxnPaymentIdentifier, Timestamp trxnDate,
			double trxnAmount) {
		this.transactionId = transactionId;
		this.transactionStatus = transactionStatus;
		this.paymentChannelOptions = paymentChannelOptions;
		this.paymentChannels = paymentChannels;
		this.serviceInfo = serviceInfo;
		this.trxnRefId = trxnRefId;
		this.trxnServiceIdentifier = trxnServiceIdentifier;
		this.trxnPaymentIdentifier = trxnPaymentIdentifier;
		this.trxnDate = trxnDate;
		this.trxnAmount = trxnAmount;
	}

	public CoreTransactions(long transactionId, TransactionStatus transactionStatus,
			PaymentChannelOptions paymentChannelOptions, PaymentChannels paymentChannels,
			TransactionSources transactionSources, ServiceInfo serviceInfo, String trxnRefId,
			String trxnServiceIdentifier, String trxnServiceDetails, String trxnPaymentIdentifier,
			String trxnPaymenDetails, Timestamp trxnDate, double trxnAmount,
			Set<ServiceProviderTransactions> serviceProviderTransactionses,
			Set<MerchantTransactions> merchantTransactionses,
			Set<MerchantWalletTransactions> merchantWalletTransactionses,
			Set<CustomerWalletTransactions> customerWalletTransactionses,
			Set<CustomerTransactions> customerTransactionses, Set<TicketingDetail> ticketingDetails) {
		this.transactionId = transactionId;
		this.transactionStatus = transactionStatus;
		this.paymentChannelOptions = paymentChannelOptions;
		this.paymentChannels = paymentChannels;
		this.transactionSources = transactionSources;
		this.serviceInfo = serviceInfo;
		this.trxnRefId = trxnRefId;
		this.trxnServiceIdentifier = trxnServiceIdentifier;
		this.trxnServiceDetails = trxnServiceDetails;
		this.trxnPaymentIdentifier = trxnPaymentIdentifier;
		this.trxnPaymenDetails = trxnPaymenDetails;
		this.trxnDate = trxnDate;
		this.trxnAmount = trxnAmount;
		this.serviceProviderTransactionses = serviceProviderTransactionses;
		this.merchantTransactionses = merchantTransactionses;
		this.merchantWalletTransactionses = merchantWalletTransactionses;
		this.customerWalletTransactionses = customerWalletTransactionses;
		this.customerTransactionses = customerTransactionses;
		this.ticketingDetails = ticketingDetails;
	}

	public CoreTransactions(long transactionId) {
		this.transactionId = transactionId;
	}

	public long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(long transactionId) {
		this.transactionId = transactionId;
	}

	public TransactionStatus getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(TransactionStatus transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public PaymentChannelOptions getPaymentChannelOptions() {
		return paymentChannelOptions;
	}

	public void setPaymentChannelOptions(PaymentChannelOptions paymentChannelOptions) {
		this.paymentChannelOptions = paymentChannelOptions;
	}

	public PaymentChannels getPaymentChannels() {
		return paymentChannels;
	}

	public void setPaymentChannels(PaymentChannels paymentChannels) {
		this.paymentChannels = paymentChannels;
	}

	public TransactionSources getTransactionSources() {
		return transactionSources;
	}

	public void setTransactionSources(TransactionSources transactionSources) {
		this.transactionSources = transactionSources;
	}

	public ServiceInfo getServiceInfo() {
		return serviceInfo;
	}

	public void setServiceInfo(ServiceInfo serviceInfo) {
		this.serviceInfo = serviceInfo;
	}

	public String getTrxnRefId() {
		return trxnRefId;
	}

	public void setTrxnRefId(String trxnRefId) {
		this.trxnRefId = trxnRefId;
	}

	public String getTrxnServiceIdentifier() {
		return trxnServiceIdentifier;
	}

	public void setTrxnServiceIdentifier(String trxnServiceIdentifier) {
		this.trxnServiceIdentifier = trxnServiceIdentifier;
	}

	public String getTrxnServiceDetails() {
		return trxnServiceDetails;
	}

	public void setTrxnServiceDetails(String trxnServiceDetails) {
		this.trxnServiceDetails = trxnServiceDetails;
	}

	public String getTrxnPaymentIdentifier() {
		return trxnPaymentIdentifier;
	}

	public void setTrxnPaymentIdentifier(String trxnPaymentIdentifier) {
		this.trxnPaymentIdentifier = trxnPaymentIdentifier;
	}

	public String getTrxnPaymenDetails() {
		return trxnPaymenDetails;
	}

	public void setTrxnPaymenDetails(String trxnPaymenDetails) {
		this.trxnPaymenDetails = trxnPaymenDetails;
	}

	public Timestamp getTrxnDate() {
		return trxnDate;
	}

	public void setTrxnDate(Timestamp trxnDate) {
		this.trxnDate = trxnDate;
	}

	public double getTrxnAmount() {
		return trxnAmount;
	}

	public void setTrxnAmount(double trxnAmount) {
		this.trxnAmount = trxnAmount;
	}

	public double getServiceCharge() {
		return serviceCharge;
	}

	public void setServiceCharge(double serviceCharge) {
		this.serviceCharge = serviceCharge;
	}

	public Set<ServiceProviderTransactions> getServiceProviderTransactionses() {
		return serviceProviderTransactionses;
	}

	public void setServiceProviderTransactionses(Set<ServiceProviderTransactions> serviceProviderTransactionses) {
		this.serviceProviderTransactionses = serviceProviderTransactionses;
	}

	public Set<MerchantTransactions> getMerchantTransactionses() {
		return merchantTransactionses;
	}

	public void setMerchantTransactionses(Set<MerchantTransactions> merchantTransactionses) {
		this.merchantTransactionses = merchantTransactionses;
	}

	public Set<MerchantWalletTransactions> getMerchantWalletTransactionses() {
		return merchantWalletTransactionses;
	}

	public void setMerchantWalletTransactionses(Set<MerchantWalletTransactions> merchantWalletTransactionses) {
		this.merchantWalletTransactionses = merchantWalletTransactionses;
	}

	public Set<CustomerWalletTransactions> getCustomerWalletTransactionses() {
		return customerWalletTransactionses;
	}

	public void setCustomerWalletTransactionses(Set<CustomerWalletTransactions> customerWalletTransactionses) {
		this.customerWalletTransactionses = customerWalletTransactionses;
	}

	public Set<CustomerTransactions> getCustomerTransactionses() {
		return customerTransactionses;
	}

	public void setCustomerTransactionses(Set<CustomerTransactions> customerTransactionses) {
		this.customerTransactionses = customerTransactionses;
	}

	public Set<TicketingDetail> getTicketingDetails() {
		return ticketingDetails;
	}

	public void setTicketingDetails(Set<TicketingDetail> ticketingDetails) {
		this.ticketingDetails = ticketingDetails;
	}

}
