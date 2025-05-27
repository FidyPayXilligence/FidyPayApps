package com.fidypay.entity;

import javax.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "BANK_ACCOUNT_INFO")
@EntityListeners(AuditingEntityListener.class)
public class BankAccountInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="BANK_ID")
	private long bankId;
	
	@Column(name="BANK_NAME", length = 100)
	private String bankName;
	
	@Column(name = "BANK_ACCOUNT_NO", length = 100)
	private String bankAccountNo;
	
	@Column(name = "ACCOUNT_NAME", length = 100)
	private String accountName;
	
	@Column(name = "BANK_BRANCH", length = 100)
	private String bankBranch;
	
	@Column(name = "BANK_IFSC", length = 100)
	private String bankIfsc;

	public BankAccountInfo() {
	}

	public BankAccountInfo(long bankId) {
		this.bankId = bankId;
	}

	public BankAccountInfo(long bankId, String bankName, String bankAccountNo,
			String accountName, String bankBranch, String bankIfsc) {
		this.bankId = bankId;
		this.bankName = bankName;
		this.bankAccountNo = bankAccountNo;
		this.accountName = accountName;
		this.bankBranch = bankBranch;
		this.bankIfsc = bankIfsc;
	}

	public long getBankId() {
		return this.bankId;
	}

	public void setBankId(long bankId) {
		this.bankId = bankId;
	}

	public String getBankName() {
		return this.bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankAccountNo() {
		return this.bankAccountNo;
	}

	public void setBankAccountNo(String bankAccountNo) {
		this.bankAccountNo = bankAccountNo;
	}

	public String getAccountName() {
		return this.accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getBankBranch() {
		return this.bankBranch;
	}

	public void setBankBranch(String bankBranch) {
		this.bankBranch = bankBranch;
	}

	public String getBankIfsc() {
		return this.bankIfsc;
	}

	public void setBankIfsc(String bankIfsc) {
		this.bankIfsc = bankIfsc;
	}

}
