package com.fidypay.entity;

import javax.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Table(name = "CORE_TEMP_TRXN")
@EntityListeners(AuditingEntityListener.class)
public class CoreTempTransactions {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CORE_TEMP_TRXN_ID")
	private long coreTempTrxnId;
	
	@Column(name = "TRANSACTION_ID", nullable = false, length = 18)
	private long transactionId;
	
	@Column(name = "TRXN_INFO", nullable = false, length = 2000)
	private String trxnInfo;
	
	@Column(name = "APP_TRXN_INFO", nullable = false, length = 1000)
	private String appTrxnInfo;
	
	@Column(name = "RETRY_COUNT", nullable = false, length = 20)
	private String retryCount;

	public CoreTempTransactions() {
	}

	public CoreTempTransactions(long transactionId, String trxnInfo, String appTrxnInfo, String retryCount) {
		this.transactionId = transactionId;
		this.trxnInfo = trxnInfo;
		this.appTrxnInfo = appTrxnInfo;
		this.retryCount = retryCount;
	}

	public long getCoreTempTrxnId() {
		return coreTempTrxnId;
	}

	public void setCoreTempTrxnId(long coreTempTrxnId) {
		this.coreTempTrxnId = coreTempTrxnId;
	}

	public long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(long transactionId) {
		this.transactionId = transactionId;
	}

	public String getTrxnInfo() {
		return trxnInfo;
	}

	public void setTrxnInfo(String trxnInfo) {
		this.trxnInfo = trxnInfo;
	}

	public String getAppTrxnInfo() {
		return appTrxnInfo;
	}

	public void setAppTrxnInfo(String appTrxnInfo) {
		this.appTrxnInfo = appTrxnInfo;
	}

	public String getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(String retryCount) {
		this.retryCount = retryCount;
	}

}
